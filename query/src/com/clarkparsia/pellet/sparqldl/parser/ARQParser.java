// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.parser;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.PropertyType;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.BuiltinTerm;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.utils.TermFactory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title: Parser for the SPARQL-DL based on ARQ
 * </p>
 * <p>
 * Description: Meanwhile does not deal with types of variables.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class ARQParser implements QueryParser {
	public static Logger		log					= Logger.getLogger( ARQParser.class.getName() );

	private Set<Triple>			triples;

	private Map<Node, ATerm>	terms;

	private KnowledgeBase		kb;

	private QuerySolution		initialBinding;

	/*
	 * If this variable is true then queries with variable SPO statements are
	 * not handled by the SPARQL-DL engine but fall back to ARQ
	 */
	private boolean				handleVariableSPO	= true;

	public ARQParser() {
		this( true );
	}

	public ARQParser(boolean handleVariableSPO) {
		this.handleVariableSPO = handleVariableSPO;
	}

	/**
	 * {@inheritDoc}
	 */
	public Query parse(InputStream stream, KnowledgeBase kb) {
		try {
			return parse( new InputStreamReader( stream ), kb );
		} catch( IOException e ) {
			final String message = "Error creating a reader from the input stream.";
			log.severe( message );
			throw new RuntimeException( message );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Query parse(String queryStr, KnowledgeBase kb) {
		com.hp.hpl.jena.query.Query sparql = QueryFactory.create( queryStr, Syntax.syntaxSPARQL );

		return parse( sparql, kb );
	}

	private Query parse(Reader in, KnowledgeBase kb) throws IOException {
		StringBuffer queryString = new StringBuffer();
		BufferedReader r = new BufferedReader( in );

		String line = r.readLine();
		while( line != null ) {
			queryString.append( line ).append( "\n" );
			line = r.readLine();
		}

		return parse( queryString.toString(), kb );
	}

	public Query parse(com.hp.hpl.jena.query.Query sparql, KnowledgeBase kb) {
		this.kb = kb;

		if( sparql.isDescribeType() )
			throw new UnsupportedQueryException(
					"DESCRIBE queries cannot be answered with PelletQueryEngine" );

		final Element pattern = sparql.getQueryPattern();

		if( !(pattern instanceof ElementGroup) )
			throw new UnsupportedQueryException( "ElementGroup was expected, but found '"
					+ pattern.getClass() + "'." );

		final ElementGroup elementGroup = (ElementGroup) pattern;

		final List<Element> elements = elementGroup.getElements();
		final Element first = elements.get( 0 );
		if (elements.size() != 1 || (!(first instanceof ElementTriplesBlock) && !(first instanceof ElementPathBlock)))
			throw new UnsupportedQueryException("Complex query patterns are not supported yet.");

		List<Triple> triples;
		if (first instanceof ElementPathBlock) {
			triples = new ArrayList<Triple>();
			for (TriplePath path : ((ElementPathBlock) first).getPattern()) {
				if (!path.isTriple()) {
					throw new UnsupportedQueryException("Path expressions are not supported yet.");
				}
				triples.add(path.asTriple());
			}
		}
		else {
			triples = ((ElementTriplesBlock) first).getPattern().getList();
		}

		// very important to call this function so that getResultVars() will
		// work fine for SELECT * queries
		sparql.setResultVars();

		return parse( triples, sparql.getResultVars(), kb, sparql.isDistinct() );
	}
	
	private void initBuiltinTerms() {
		terms = new HashMap<Node, ATerm>();
		
		terms.put( OWL.Thing.asNode(), TOP );
		terms.put( OWL.Nothing.asNode(), BOTTOM );
		terms.put( OWL2.topObjectProperty.asNode(), TOP_OBJECT_PROPERTY );
		terms.put( OWL2.topDataProperty.asNode(), TOP_DATA_PROPERTY );
		terms.put( OWL2.bottomObjectProperty.asNode(), BOTTOM_OBJECT_PROPERTY );
		terms.put( OWL2.bottomDataProperty.asNode(), BOTTOM_DATA_PROPERTY );
	}

	public Query parse(BasicPattern basicPattern, Collection<?> resultVars, KnowledgeBase kb,
			boolean isDistinct) throws UnsupportedQueryException {
		return parse(basicPattern.getList(), resultVars, kb, isDistinct);
	}
	
	public Query parse(List<Triple> basicPattern, Collection<?> resultVars, KnowledgeBase kb,
					boolean isDistinct) throws UnsupportedQueryException {		
		this.kb = kb;

		// This set contains predicates that are distinguished variables. The
		// elements are accumulated for PropertyValueAtom and removed if used in
		// subject position of other SPARQL-DL query atoms. If the set
		// is not empty, we throw an unsupported query exception and fall back
		// to ARQ to process the query. This solves the problem of {} ?p {}
		// queries where ?p is not used as subject in other patterns
		Set<ATermAppl> variablePredicates = new HashSet<ATermAppl>();
		// This set contains subjects that are distinguished variables and is
		// used to collect variables along the way while processing triple
		// patterns. The list is used to decide whether or not the variable
		// property of a pattern {} ?p {} has to be accumulated to the
		// variablePredicates set. This avoids to add them for the case where
		// the variable in predicate position is bound to a subject of another
		// triple pattern, e.g. ?p rdf:type owl:ObjectProperty . ?s ?p ?o
		Set<ATermAppl> variableSubjects = new HashSet<ATermAppl>();

		initBuiltinTerms();
		
		// Make sure to resolve the query parameterization first, i.e.
		// substitute the variables with initial bindings, if applicable
		triples = new LinkedHashSet<Triple>( resolveParameterization( basicPattern ) );

		final Query query = new QueryImpl( kb, isDistinct );

		for( Iterator<?> i = resultVars.iterator(); i.hasNext(); ) {
			String var = (String) i.next();

			query.addResultVar( ATermUtils.makeVar( var ) );
		}

		for( final Triple t : new ArrayList<Triple>( triples ) ) {
			if( !triples.contains( t ) ) {
				continue;
			}

			Node subj = t.getSubject();
			Node pred = t.getPredicate();
			Node obj = t.getObject();
			
			if (BuiltinTerm.isSyntax(pred) || BuiltinTerm.isSyntax(obj))
				continue;

			cache( subj );
			cache( pred );
			cache( obj );
		}

		final Set<ATermAppl> possibleLiteralVars = new HashSet<ATermAppl>();

		//throw exception if triples is empty
		if(triples.isEmpty()){
			throw new UnsupportedQueryException(
			"Empty BGT" );
		}
		
		for( final Triple t : triples ) {

			Node subj = t.getSubject();
			Node pred = t.getPredicate();
			Node obj = t.getObject();

			ATermAppl s = (ATermAppl) terms.get( subj );
			ATermAppl p = (ATermAppl) terms.get( pred );
			ATermAppl o = (ATermAppl) terms.get( obj );

			
			if( pred.equals( RDF.Nodes.type ) ) {
				// Map ?c rdf:type owl:Class to SubClassOf(?c owl:Thing)
				if( obj.equals( OWL.Class.asNode() ) ) {
					query.add( QueryAtomFactory.SubClassOfAtom( s, TermFactory.TOP ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.CLASS );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}
				
				//NamedIndividual(p)
				else if( obj.equals( OWL2.NamedIndividual.asNode() ) ) {
					query.add( QueryAtomFactory.TypeAtom(s, TermFactory.TOP ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.CLASS );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}
				
				// ObjectProperty(p)
				else if( obj.equals( OWL.ObjectProperty.asNode() ) ) {
					query.add( QueryAtomFactory.ObjectPropertyAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					} 
					else {
						ensureTypedProperty( s );
					}
				}

				// DatatypeProperty(p)
				else if( obj.equals( OWL.DatatypeProperty.asNode() ) ) {
					query.add( QueryAtomFactory.DatatypePropertyAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}

				// Property(p)
				else if( obj.equals( RDF.Property.asNode() ) ) {
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}

				// Functional(p)
				else if( obj.equals( OWL.FunctionalProperty.asNode() ) ) {
					query.add( QueryAtomFactory.FunctionalAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}

				// InverseFunctional(p)
				else if( obj.equals( OWL.InverseFunctionalProperty.asNode() ) ) {
					query.add( QueryAtomFactory.InverseFunctionalAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}

				// Transitive(p)
				else if( obj.equals( OWL.TransitiveProperty.asNode() ) ) {
					query.add( QueryAtomFactory.TransitiveAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}

				// Symmetric(p)
				else if( obj.equals( OWL.SymmetricProperty.asNode() ) ) {
					query.add( QueryAtomFactory.SymmetricAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}

				// Asymmetric(p)
				else if( obj.equals( OWL2.AsymmetricProperty.asNode() ) ) {
					query.add( QueryAtomFactory.AsymmetricAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}
				
				// Reflexive(p)
				else if( obj.equals( OWL2.ReflexiveProperty.asNode() ) ) {
					query.add( QueryAtomFactory.ReflexiveAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}
				
				// Irreflexive(p)
				else if( obj.equals( OWL2.IrreflexiveProperty.asNode() ) ) {
					query.add( QueryAtomFactory.IrreflexiveAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
					else {
						ensureTypedProperty( s );
					}
				}
				
				// Annotation(s,pa,o)
				else if( hasObject( pred, RDF.type.asNode(), OWL.AnnotationProperty.asNode() ) ) {
					query.add( QueryAtomFactory.AnnotationAtom( s, p, o ) );
					if( ATermUtils.isVar( s ) || ATermUtils.isVar( p ) || ATermUtils.isVar( o ) ) {
						throw new UnsupportedQueryException(
								"Variables in annotation atom are not supported." );
					}
					else {
						ensureTypedProperty( p );
					}
				}

				// Type(i,c)
				else {
					query.add( QueryAtomFactory.TypeAtom( s, o ) );

					if( ATermUtils.isVar( o ) ) {
						ensureDistinguished( obj );
						query.addDistVar( o, VarType.CLASS );
					}
					else if( !kb.isClass( o ) ) {
						if( log.isLoggable( Level.FINE ) )
							log
									.fine( "Class " + o
											+ " used in the query is not defined in the KB." );
					}

					if( isDistinguishedVariable( subj ) ) {
						query.addDistVar( s, VarType.INDIVIDUAL );
					}
				}
			}

			// SameAs(i1,i2)
			else if( pred.equals( OWL.sameAs.asNode() ) ) {
				query.add( QueryAtomFactory.SameAsAtom( s, o ) );
				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}

				if( isDistinguishedVariable( obj ) ) {
					query.addDistVar( o, VarType.INDIVIDUAL );
				}

			}

			// DifferentFrom(i1,i2)
			else if( pred.equals( OWL.differentFrom.asNode() ) ) {
				query.add( QueryAtomFactory.DifferentFromAtom( s, o ) );
				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}

				if( isDistinguishedVariable( obj ) ) {
					query.addDistVar( o, VarType.INDIVIDUAL );
				}

			}

			// SubClassOf(c1,c2)
			else if( pred.equals( RDFS.subClassOf.asNode() ) ) {
				query.add( QueryAtomFactory.SubClassOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// strict subclass - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.strictSubClassOf.asNode() ) ) {
				query.add( QueryAtomFactory.StrictSubClassOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// direct subclass - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.directSubClassOf.asNode() ) ) {
				query.add( QueryAtomFactory.DirectSubClassOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// EquivalentClass(c1,c2)
			else if( pred.equals( OWL.equivalentClass.asNode() ) ) {
				query.add( QueryAtomFactory.EquivalentClassAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// DisjointWith(c1,c2)
			else if( pred.equals( OWL.disjointWith.asNode() ) ) {
				query.add( QueryAtomFactory.DisjointWithAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}

			}

			// ComplementOf(c1,c2)
			else if( pred.equals( OWL.complementOf.asNode() ) ) {
				query.add( QueryAtomFactory.ComplementOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}
			
			// propertyDisjointWith(p1,p2)
			else if( pred.equals( OWL2.propertyDisjointWith.asNode() ) ) {
				ensureTypedProperty( s );
				ensureTypedProperty( o );
				
				query.add( QueryAtomFactory.PropertyDisjointWithAtom( s, o ) );
				
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}

			}
			
			// SubPropertyOf(p1,p2)
			else if( pred.equals( RDFS.subPropertyOf.asNode() ) ) {
				ensureTypedProperty( s );
				ensureTypedProperty( o );

				query.add( QueryAtomFactory.SubPropertyOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// DirectSubPropertyOf(i,p) - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.directSubPropertyOf.asNode() ) ) {
				ensureTypedProperty( s );
				ensureTypedProperty( o );
				
				query.add( QueryAtomFactory.DirectSubPropertyOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// StrictSubPropertyOf(i,p) - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.strictSubPropertyOf.asNode() ) ) {
				ensureTypedProperty( s );
				ensureTypedProperty( o );
				
				query.add( QueryAtomFactory.StrictSubPropertyOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// EquivalentProperty(p1,p2)
			else if( pred.equals( OWL.equivalentProperty.asNode() ) ) {
				ensureTypedProperty( s );
				ensureTypedProperty( o );
				
				query.add( QueryAtomFactory.EquivalentPropertyAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}
			// Domain(p1, c)
			else if( pred.equals( RDFS.domain.asNode() ) ) {
				ensureTypedProperty( s );

				query.add( QueryAtomFactory.DomainAtom( s, o ) );
				if ( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( s, VarType.CLASS );
				}
			}
			// Range(p1, c)
			else if( pred.equals( RDFS.range.asNode() ) ) {
				ensureTypedProperty( s );

				query.add( QueryAtomFactory.RangeAtom( s, o ) );
				if ( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					// TODO it could also range over datatypes.
					query.addDistVar( s, VarType.CLASS );
				}
			}
			// InverseOf(p1,p2)
			else if( pred.equals( OWL.inverseOf.asNode() ) ) {
				ensureTypedProperty( s );
				ensureTypedProperty( o );
				
				query.add( QueryAtomFactory.InverseOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// DirectType(i,c) - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.directType.asNode() ) ) {
				query.add( QueryAtomFactory.DirectTypeAtom( s, o ) );
				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			else if( kb.isAnnotationProperty( p ) ) {
				if( !PelletOptions.USE_ANNOTATION_SUPPORT ) {
					throw new UnsupportedQueryException(
							"Cannot answer annotation queries when PelletOptions.USE_ANNOTATION_SUPPORT is false!" );
				}
				
				query.add( QueryAtomFactory.AnnotationAtom( s, p, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
				// throw new UnsupportedQueryException(
				// "Annotation properties are not supported in queries." );
			}

			// PropertyValue(i,p,j)
			else {
				if( s == null || p == null || o == null ) {
					throw new UnsupportedQueryException("Atom conversion incomplete for: " + t);
				}
				ensureTypedProperty( p );

				query.add( QueryAtomFactory.PropertyValueAtom( s, p, o ) );

				if( ATermUtils.isVar( p ) ) {
					ensureDistinguished( pred );
					query.addDistVar( p, VarType.PROPERTY );

					// If the predicate is a variable used in a subject position
					// we don't have to consider it as it is bound to another
					// triple pattern
					if( !variableSubjects.contains( p ) )
						variablePredicates.add( p );
				}

				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}

				if( isDistinguishedVariable( obj ) ) {
					if( ATermUtils.isVar( p ) ) {
						possibleLiteralVars.add( o );
					}
					else {
						if( kb.isObjectProperty( p ) ) {
							query.addDistVar( o, VarType.INDIVIDUAL );
						}
						else if( kb.isDatatypeProperty( p ) ) {
							query.addDistVar( o, VarType.LITERAL );
						}
					}
				}
			}
		}

		for( final ATermAppl v : possibleLiteralVars ) {
			if( !query.getDistVars().contains( v ) ) {
				query.addDistVar( v, VarType.LITERAL );
			}
			query.addDistVar( v, VarType.INDIVIDUAL );
		}

		if( !handleVariableSPO )
			return query;

		if( variablePredicates.isEmpty() )
			return query;

		throw new UnsupportedQueryException( "Queries with variable predicates are not supported "
				+ "(add the pattern {?p rdf:type owl:ObjectProperty} or"
				+ " {?p rdf:type owl:DatatypeProperty} to the query)" );

	}

	public void setInitialBinding(QuerySolution initialBinding) {
		this.initialBinding = initialBinding;
	}

	private void ensureDistinguished(Node pred) {
		ensureDistinguished( pred,
				"Non-distinguished variables in class and predicate positions are not supported : " );
	}

	private void ensureDistinguished(Node pred, String errorNonDist) {
		if( !isDistinguishedVariable( pred ) ) {
			throw new UnsupportedQueryException( errorNonDist + pred );
		}
	}
	
	private void ensureTypedProperty( ATermAppl pred ) {
		
		if ( ATermUtils.isVar( pred ) )
			return;
		
		Role r = kb.getRole( pred );
		if ( r == null ) {
			throw new UnsupportedQueryException( "Unknown role: " + pred );
		}
		
		if ( r.isUntypedRole() ) {
			throw new UnsupportedQueryException( "Untyped role: " + pred );
		}
	}

	public static boolean isDistinguishedVariable(final Node node) {
		return Var.isVar( node )
				&& (Var.isNamedVar( node ) || PelletOptions.TREAT_ALL_VARS_DISTINGUISHED);
	}

	private Node getObject(Node subj, Node pred) {
		for( final Iterator<Triple> i = triples.iterator(); i.hasNext(); ) {
			Triple t = i.next();
			if( subj.equals( t.getSubject() ) && pred.equals( t.getPredicate() ) ) {
				i.remove();
				return t.getObject();
			}
		}

		return null;
	}

	private boolean hasObject(Node subj, Node pred) {
		for( final Iterator<Triple> i = triples.iterator(); i.hasNext(); ) {
			Triple t = i.next();
			if( subj.equals( t.getSubject() ) && pred.equals( t.getPredicate() ) )
				return true;
		}

		return false;
	}

	private boolean hasObject(Node subj, Node pred, Node obj) {
		for( final Iterator<Triple> i = triples.iterator(); i.hasNext(); ) {
			Triple t = i.next();
			if( subj.equals( t.getSubject() ) && pred.equals( t.getPredicate() ) ) {
				i.remove();
				if( obj.equals( t.getObject() ) ) {
					return true;
				}
				throw new UnsupportedQueryException( "Expecting rdf:type " + obj
						+ " but found rdf:type " + t.getObject() );
			}
		}

		return false;
	}

	private ATermList createList(Node node) {
		if( node.equals( RDF.nil.asNode() ) )
			return ATermUtils.EMPTY_LIST;
		else if( terms.containsKey( node ) )
			return (ATermList) terms.get( node );

		hasObject( node, RDF.type.asNode(), RDF.List.asNode() );

		Node first = getObject( node, RDF.first.asNode() );
		Node rest = getObject( node, RDF.rest.asNode() );

		if( first == null || rest == null ) {
			throw new UnsupportedQueryException( "Invalid list structure: List " + node
					+ " does not have a " + (first == null
						? "rdf:first"
						: "rdf:rest") + " property." );
		}

		ATermList list = ATermUtils.makeList( node2term( first ), createList( rest ) );

		terms.put( node, list );

		return list;
	}

	private ATermAppl createRestriction(Node node) throws UnsupportedFeatureException {
		ATermAppl aTerm = ATermUtils.TOP;

		hasObject( node, RDF.type.asNode(), OWL.Restriction.asNode() );

		Node p = getObject( node, OWL.onProperty.asNode() );

		// TODO warning message: no owl:onProperty
		if( p == null )
			return aTerm;

		ATermAppl pt = node2term( p );
		if( !kb.isProperty( pt ) )
			throw new UnsupportedQueryException( "Property " + pt + " is not present in KB." );

		// TODO warning message: multiple owl:onProperty
		Node o = null;
		if( (o = getObject( node, OWL.hasValue.asNode() )) != null ) {
			if( PelletOptions.USE_PSEUDO_NOMINALS ) {
				if( o.isLiteral() ) {
					aTerm = ATermUtils.makeMin( pt, 1, ATermUtils.TOP_LIT );
				}
				else {
					ATermAppl ind = ATermUtils.makeTermAppl( o.getURI() );
					if( !kb.isIndividual( ind ) )
						throw new UnsupportedQueryException( "Individual " + ind
								+ " is not present in KB." );

					ATermAppl nom = ATermUtils.makeTermAppl( o.getURI() + "_nom" );

					aTerm = ATermUtils.makeSomeValues( pt, nom );
				}
			}
			else {
				ATermAppl ot = node2term( o );

				aTerm = ATermUtils.makeHasValue( pt, ot );
			}
		}
		else if( (o = getObject( node, OWL2.hasSelf.asNode() )) != null ) {
			ATermAppl ot = node2term( o );
			
			if( ATermUtils.isVar( ot ) )
				throw new UnsupportedQueryException("Variables not supported in hasSelf restriction");
			else
				aTerm = ATermUtils.makeSelf(pt);
		}
		else if( (o = getObject( node, OWL.allValuesFrom.asNode() )) != null ) {
			ATermAppl ot = node2term( o );
			
			if( ATermUtils.isVar( ot ) )
				throw new UnsupportedQueryException("Variables not supported in allValuesFrom restriction");
			else
				aTerm = ATermUtils.makeAllValues( pt, ot );
		}
		else if( (o = getObject( node, OWL.someValuesFrom.asNode() )) != null ) {
			ATermAppl ot = node2term( o );

			if( ATermUtils.isVar( ot ) )
				throw new UnsupportedQueryException("Variables not supported in someValuesFrom restriction");
			else
				aTerm = ATermUtils.makeSomeValues( pt, ot );
		}
		else if( (o = getObject( node, OWL.minCardinality.asNode() )) != null ) {
			aTerm = createCardinalityRestriction( node, OWL.minCardinality.asNode(), pt, o );		
		}
		else if( (o = getObject( node, OWL2.minQualifiedCardinality.asNode() )) != null ) {
			aTerm = createCardinalityRestriction( node, OWL2.minQualifiedCardinality.asNode(), pt, o );		
		}
		else if( (o = getObject( node, OWL.maxCardinality.asNode() )) != null ) {
			aTerm = createCardinalityRestriction( node, OWL.maxCardinality.asNode(), pt, o );
		}
		else if( (o = getObject( node, OWL2.maxQualifiedCardinality.asNode() )) != null ) {
			aTerm = createCardinalityRestriction( node, OWL2.maxQualifiedCardinality.asNode(), pt, o );
		}
		else if( (o = getObject( node, OWL.cardinality.asNode() )) != null ) {
			aTerm = createCardinalityRestriction( node, OWL.cardinality.asNode(), pt, o );
		}		
		else if( (o = getObject( node, OWL2.qualifiedCardinality.asNode() )) != null ) {
			aTerm = createCardinalityRestriction( node, OWL2.qualifiedCardinality.asNode(), pt, o );
		}		
		else {
			// TODO print warning message (invalid restriction type)
		}

		return aTerm;
	}
	
	private ATermAppl createCardinalityRestriction(Node node, Node restrictionType, ATermAppl pt, Node card)
			throws UnsupportedQueryException {

		try {
			ATermAppl c = null;
			Node qualification = null;
			if( (qualification = getObject( node, OWL2.onClass.asNode() )) != null ) {
				if( qualification.isVariable() ) {
					throw new UnsupportedQueryException( "Variables not allowed in cardinality qualification" );
				}

				if( !kb.isObjectProperty( pt ) )
					return null;
				c = node2term( qualification );
			}
			else if( (qualification = getObject( node, OWL2.onDataRange.asNode() )) != null  ) {
				if( qualification.isVariable() ) {
					throw new UnsupportedQueryException( "Variables not allowed in cardinality qualification" );
				}

				if( !kb.isDatatypeProperty( pt ) )
					return null;
				c = node2term( qualification );
			}
			else {
				PropertyType propType = kb.getPropertyType( pt );
				if( propType == PropertyType.OBJECT )
					c = ATermUtils.TOP;
				else if( propType == PropertyType.DATATYPE )
					c = ATermUtils.TOP_LIT;
				else 
					c = ATermUtils.TOP;				
			}

			int cardinality = Integer.parseInt( card.getLiteralLexicalForm() );

			if( restrictionType.equals( OWL.minCardinality.asNode() )
					|| restrictionType.equals( OWL2.minQualifiedCardinality.asNode() ) )
				return ATermUtils.makeMin( pt, cardinality, c );
			else if( restrictionType.equals( OWL.maxCardinality.asNode() )
					|| restrictionType.equals( OWL2.maxQualifiedCardinality.asNode() ) )
				return ATermUtils.makeMax( pt, cardinality, c );
			else
				return ATermUtils.makeCard( pt, cardinality, c );
		} catch( Exception ex ) {
			log.log( Level.WARNING, "Invalid cardinality", ex );
		}		
		
		return null;
	}

	private ATermAppl node2term(Node node) {
		if(!terms.containsKey(node)) {
			cache(node);
		}
		return (ATermAppl) terms.get( node );		
	}
	
	private void cache(Node node) {
		if(terms.containsKey(node) || BuiltinTerm.isBuiltin(node) ) {
			return;
		}
		
		ATerm aTerm = null;
		
		 if( node.isLiteral() )
			aTerm = JenaUtils.makeLiteral( node.getLiteral() );
		else if( hasObject( node, OWL.onProperty.asNode() ) ) {
			aTerm = createRestriction( node );
			terms.put( node, aTerm );
		}
		else if( node.isBlank() || node.isVariable() ) {
			Node o = null;
			if( (o = getObject( node, OWL.intersectionOf.asNode() )) != null ) {
				ATermList list = createList( o );
				hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

				aTerm = ATermUtils.makeAnd( list );
			}
			else if( (o = getObject( node, OWL.unionOf.asNode() )) != null ) {
				ATermList list = createList( o );
				hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

				aTerm = ATermUtils.makeOr( list );
			}
			else if( (o = getObject( node, OWL.oneOf.asNode() )) != null ) {
				ATermList list = createList( o );
				hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

				ATermList result = ATermUtils.EMPTY_LIST;
				for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
					ATermAppl c = (ATermAppl) l.getFirst();
					if( PelletOptions.USE_PSEUDO_NOMINALS ) {
						ATermAppl nominal = ATermUtils.makeTermAppl( c.getName() + "_nominal" );
						result = result.insert( nominal );
					}
					else {
						ATermAppl nominal = ATermUtils.makeValue( c );
						result = result.insert( nominal );
					}
				}

				aTerm = ATermUtils.makeOr( result );
			}
			else if( Var.isBlankNodeVar( node )
					&& (o = getObject( node, OWL.complementOf.asNode() )) != null ) {
				ATermAppl complement = node2term( o );
				hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

				aTerm = ATermUtils.makeNot( complement );
			}
			else if( node.isVariable() ) {
				aTerm = ATermUtils.makeVar( node.getName() );
			}
			else {
				if( ((o = getObject( node, OWL.complementOf.asNode() )) != null) ) {
					log.info( "Blank nodes in class variable positions are not supported" );

					// TODO
				}

				aTerm = ATermUtils.makeBnode( node.getBlankNodeId().toString() );
			}
		}
		else {
			String uri = node.getURI();

			aTerm = ATermUtils.makeTermAppl( uri );
		}

		terms.put( node, aTerm );
	}

	/*
	 * Given a parameterized query, resolve the node (SPO of a triple pattern)
	 * i.e. if it is a variable and the variable name is contained in the
	 * initial binding (as a parameter) resolve it, i.e. substitute the variable
	 * with the constant.
	 */
	private List<Triple> resolveParameterization(List<?> triples) {
		if( triples == null )
			throw new NullPointerException( "The set of triples cannot be null" );

		// Ensure that the initial binding is not a null pointer
		if( initialBinding == null )
			initialBinding = new QuerySolutionMap();

		List<Triple> ret = new ArrayList<Triple>();

		for( final Triple t : triples.toArray( new Triple[triples.size()] ) ) {
			if( !triples.contains( t ) ) {
				continue;
			}

			Node s = resolveParameterization( t.getSubject() );
			Node p = resolveParameterization( t.getPredicate() );
			Node o = resolveParameterization( t.getObject() );

			ret.add( Triple.create( s, p, o ) );
		}

		return ret;
	}

	private Node resolveParameterization(Node node) {
		if( node == null )
			throw new NullPointerException( "Node is null" );
		if( initialBinding == null )
			throw new NullPointerException( "Initial binding is null" );

		if( node.isConcrete() )
			return node;

		RDFNode binding = initialBinding.get( node.getName() );

		if( binding == null )
			return node;

		return binding.asNode();
	}
}
