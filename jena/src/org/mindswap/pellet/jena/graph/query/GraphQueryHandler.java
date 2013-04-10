// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.ModelExtractor;
import org.mindswap.pellet.jena.ModelExtractor.StatementType;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.utils.iterator.FlattenningIterator;
import org.mindswap.pellet.utils.iterator.IteratorUtils;
import org.mindswap.pellet.utils.iterator.NestedIterator;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.NullIterator;
import com.hp.hpl.jena.util.iterator.SingletonIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class GraphQueryHandler {
	public final static Logger	log		= Logger.getLogger( GraphQueryHandler.class.getName() );
	
	protected static final Node VAR 	= Node.ANY;
	protected static final Node CONST 	= Node.createURI( "CONST" );			
	
	private static final Node[]	BUILTIN_PREDICATES			= new Node[] {
		RDF.type.asNode(),
		OWL.sameAs.asNode(), 
		OWL.differentFrom.asNode(), 

		RDFS.subClassOf.asNode(), 
		OWL.equivalentClass.asNode(), 
		OWL.complementOf.asNode(),
		OWL.disjointWith.asNode(),								

		RDFS.subPropertyOf.asNode(), 
		OWL.equivalentProperty.asNode(),
		OWL.inverseOf.asNode(), 
		OWL2.propertyDisjointWith.asNode(),
		
		RDFS.domain.asNode(),
		RDFS.range.asNode()
	};
	
	private static final Node[]	BUILTIN_QUERY_PREDICATES	= new Node[] {
		ReasonerVocabulary.directRDFType.asNode(), 
		ReasonerVocabulary.directSubClassOf.asNode(), 
		ReasonerVocabulary.directSubPropertyOf.asNode()
	};

	private static final Node[]	BUILTIN_TYPES				= new Node[] {
		OWL.Class.asNode(),		
		
		OWL.AnnotationProperty.asNode(),
		OWL.ObjectProperty.asNode(), 
		OWL.DatatypeProperty.asNode(),
		OWL.FunctionalProperty.asNode(), 
		OWL.InverseFunctionalProperty.asNode(),
		OWL.TransitiveProperty.asNode(),
		OWL.SymmetricProperty.asNode(), 
		OWL2.AsymmetricProperty.asNode(),
		OWL2.ReflexiveProperty.asNode(),
		OWL2.IrreflexiveProperty.asNode()
	};
	
	private static final Node[]	BUILTIN_QUERY_TYPES			= new Node[] {
		RDFS.Class.asNode(),
		RDF.Property.asNode()
	};
	
	private static final Set<Node> BUILTIN_KEYWORDS = new HashSet<Node>();
	
	static {
		for( Node node : BUILTIN_PREDICATES ) {
			BUILTIN_KEYWORDS.add( node );
		}
		
		for( Node node : BUILTIN_QUERY_PREDICATES ) {
			BUILTIN_KEYWORDS.add( node );
		}
		
		for( Node node : BUILTIN_TYPES ) {
			BUILTIN_KEYWORDS.add( node );
		}		
				
		for( Node node : BUILTIN_QUERY_TYPES ) {
			BUILTIN_KEYWORDS.add( node );
		}
	}	
	
	private static final Map<Triple,TripleQueryHandler> QUERY_HANDLERS = CollectionUtils.makeMap();
	
	static {
		registerQueryHandlers();
	}
	
	private static void registerHandler(Node s, Resource p, Node o, TripleQueryHandler handler) {
		registerHandler( s, p.asNode(), o, handler );
	}
	
	private static void registerHandler(Node s, Resource p, Resource o, TripleQueryHandler handler) {
		registerHandler( s, p.asNode(), o.asNode(), handler );
	}
		
	private static void registerHandler( Node s, Node p, Node o, TripleQueryHandler handler ) {
		Triple pattern = Triple.create( s, p, o );
		
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Registering handler for pattern: " + pattern );
		
		Object prev = QUERY_HANDLERS.put( pattern, handler );
		if( prev != null ) {
			if( log.isLoggable( Level.SEVERE ) )
				log.severe( "Existing handler found for pattern: " + pattern );			
		}
	}

	private static void registerQueryHandlers() {
		registerHandler( VAR, VAR, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return true;
				}
				
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ModelExtractor me = new ModelExtractor( kb );
					me.setSelector( StatementType.ALL_STATEMENTS );
					Graph graph = me.extractModel().getGraph();
					return graph.find( Triple.ANY );
				}
			}
		);				
		
		// TODO built-in predicates can be categorized as ABox, TBox and RBox and we can issue only queries that are not obviously false
		registerHandler( VAR, VAR, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl term = loader.node2term( o );
					return kb.isClass( term ) || kb.isProperty( term ) || kb.isIndividual( term );
				}
				
				@Override
				public ExtendedIterator<Triple> find(final KnowledgeBase kb, final GraphLoader loader, final Node s, final Node p, final Node o) {
					ExtendedIterator<Triple> builtinPredicates = NullIterator.instance();
					if( !o.isLiteral() ) {
						for( Node pred : BUILTIN_PREDICATES ) {
							builtinPredicates = builtinPredicates.andThen( findTriple( kb, loader, s, pred, o ) );
						}
					}
					
					ExtendedIterator<Triple> propertyAssertions =  WrappedIterator.create(
						new NestedIterator<ATermAppl,Triple>( kb.getProperties() ) {
							@Override
							public Iterator<Triple> getInnerIterator(ATermAppl prop) {
								Node p = JenaUtils.makeGraphNode( prop );
								return findTriple( kb, loader, s, p, o );
							}
						}
					);
					
					return builtinPredicates.andThen( propertyAssertions );
				}
			}
		);
		
		registerHandler( CONST, VAR, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl term = loader.node2term( s );
					return kb.isClass( term ) || kb.isProperty( term ) || kb.isIndividual( term );
				}
				
				@Override
				public ExtendedIterator<Triple> find(final KnowledgeBase kb, final GraphLoader loader, final Node s, final Node p, final Node o) {
					ExtendedIterator<Triple> builtinPredicates = NullIterator.instance();
					for( Node pred : BUILTIN_PREDICATES ) {
						builtinPredicates = builtinPredicates.andThen( findTriple( kb, loader, s, pred, o ) );
					}
					
					ExtendedIterator<Triple> propertyAssertions =  WrappedIterator.create(
						new NestedIterator<ATermAppl,Triple>( kb.getProperties() ) {
							@Override
							public Iterator<Triple> getInnerIterator(ATermAppl prop) {
								Node p = JenaUtils.makeGraphNode( prop );
								return findTriple( kb, loader, s, p, o );
							}
						}
					);
					
					return builtinPredicates.andThen( propertyAssertions );
				}
			}
		);
		
		registerHandler( CONST, VAR, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl t = loader.node2term( s );
					return kb.isClass( t ) || kb.isProperty( t ) || kb.isIndividual( t );
				}
				
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, final Node s, final Node p, Node o) {
					ExtendedIterator<Triple> result = NullIterator.instance();
					ATermAppl subj = loader.node2term( s );
					ATermAppl obj = loader.node2term( o );
					if( kb.isIndividual( subj ) || kb.isIndividual( obj ) ) {
						
						if( kb.isIndividual( subj )) {
							List<ATermAppl> properties = kb.getProperties( loader.node2term( s ), loader.node2term( o ) );
							result = propertyFiller( s, properties, o );
							
							if(kb.isIndividual( obj ) ) {							
								if( kb.isSameAs( subj, obj ) )
									result = result.andThen( new SingletonIterator<Triple>( Triple.create( s, OWL.sameAs.asNode(), o ) ) );
								if( kb.isDifferentFrom( subj, obj ) )
									result = result.andThen( new SingletonIterator<Triple>( Triple.create( s, OWL.differentFrom.asNode(), o ) ) );
							}
						}
					}
					else {
						for( Node pred : BUILTIN_PREDICATES ) {
							result = result.andThen( findTriple( kb, loader, s, pred, o ) );
						}
					}
					return result;
				}
			}		
		);
		
		registerHandler( VAR, CONST, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl prop = loader.node2term( p );
					if( !kb.isProperty( prop ) )
						return false;
										
					for( ATermAppl ind : kb.getIndividuals() ) {
						if( kb.hasKnownPropertyValue( ind, prop, null ).isTrue() ) {
							return true;
						}
					}
					
					return false;
				}
				
				@Override
				public ExtendedIterator<Triple> find(final KnowledgeBase kb, GraphLoader loader, Node s, final Node p, Node o) {
					final ATermAppl prop = loader.node2term( p );
					return WrappedIterator.create( 
						new NestedIterator<ATermAppl,Triple>( kb.getIndividuals() ) {
							@Override
							public Iterator<Triple> getInnerIterator(ATermAppl subj) {
								Node s = JenaUtils.makeGraphNode( subj );
								return objectFiller( s, p, kb.getPropertyValues( prop, subj ) );
							}
						}
					);
				}
			}
		);
		
		registerHandler( VAR, CONST, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl prop = loader.node2term( p );
					ATermAppl val = loader.node2term( o );
										
					return !kb.getIndividualsWithProperty( prop, val ).isEmpty();
				}
				
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl prop = loader.node2term( p );
					ATermAppl val = loader.node2term( o );
					return subjectFiller( kb.getIndividualsWithProperty( prop, val ), p, o );
				}
			}
		);
		
		registerHandler( CONST, CONST, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl ind = loader.node2term( s );
					ATermAppl prop = loader.node2term( p );
					return kb.isIndividual( ind ) && kb.hasPropertyValue( ind, prop, null );
				}
				
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl ind = loader.node2term( s );
					ATermAppl prop = loader.node2term( p );
					return objectFiller( s, p, kb.getPropertyValues( prop, ind ) );
				}
			}
		);
		
		registerHandler( CONST, CONST, CONST, 
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl subj = loader.node2term( s );
					ATermAppl prop = loader.node2term( p );
					ATermAppl obj = loader.node2term( o );
					return kb.hasPropertyValue( subj, prop, obj );
				}
			}
		);
		
		registerHandler( VAR, RDF.type, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return true;
				}

				@Override
				public ExtendedIterator<Triple> find(final KnowledgeBase kb, final GraphLoader loader, final Node subj, final Node pred, Node obj) {
					ExtendedIterator<Triple> builtinTypes = WrappedIterator.create(
						new NestedIterator<Node,Triple>( Arrays.asList( BUILTIN_TYPES ) ) {
							@Override
							public Iterator<Triple> getInnerIterator(Node builtinType) {
								return findTriple( kb, loader, subj, pred, builtinType );
							}
						}
					);
					
					ExtendedIterator<Triple> typeAssertions = WrappedIterator.create(
						new NestedIterator<ATermAppl,Triple>( kb.getAllClasses() ) {
							@Override
							public Iterator<Triple> getInnerIterator(ATermAppl cls) {
								return subjectFiller( kb.getInstances( cls ), pred, JenaUtils.makeGraphResource( cls ) );
							}
						}
					);
					
					return builtinTypes.andThen( typeAssertions );
				}			
			}				
		);
		
		registerHandler( VAR, RDF.type, CONST,
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.hasInstance( loader.node2term( o ) );
				}
				
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectFiller( kb.getInstances( loader.node2term( o ) ), p, o );
				}
			}
		);		

		registerHandler( CONST, RDF.type, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl t = loader.node2term( s );
					return kb.isClass( t ) || kb.isProperty( t ) || kb.isIndividual( t );
				}
				
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, final Node s, final Node p, Node o) {
					ATermAppl term = loader.node2term( s );	
					
					if( kb.isIndividual( term ) ) {
						return objectSetFiller( s, p, kb.getTypes( term ) );
					}
					
					List<Node> types = new ArrayList<Node>();

					if( kb.isClass( term ) ) {
						types.add( OWL.Class.asNode() );
					}
					else if( kb.isDatatype( term ) ) {
						types.add( RDFS.Datatype.asNode() );
					}
					else if( kb.isObjectProperty( term ) ) {
						Role role = kb.getRole( term );
						types.add( OWL.ObjectProperty.asNode() );
						if( role.isFunctional() )
							types.add( OWL.FunctionalProperty.asNode() );
						if( role.isInverseFunctional() )
							types.add( OWL.InverseFunctionalProperty.asNode() );
						if( role.isTransitive() )
							types.add( OWL.TransitiveProperty.asNode() );
						if( role.isSymmetric() )
							types.add( OWL.SymmetricProperty.asNode() );
						if( role.isAsymmetric() )
							types.add( OWL2.AsymmetricProperty.asNode() );
						if( role.isReflexive() )
							types.add( OWL2.ReflexiveProperty.asNode() );
						if( role.isIrreflexive() )
							types.add( OWL2.IrreflexiveProperty.asNode() );
					}
					else if( kb.isDatatypeProperty( term ) ) {					
						Role role = kb.getRole( term );
						types.add( OWL.DatatypeProperty.asNode() );
						if( role.isFunctional() )
							types.add( OWL.FunctionalProperty.asNode() );
						if( role.isInverseFunctional() )
							types.add( OWL.InverseFunctionalProperty.asNode() );
					}
					else if( kb.isAnnotationProperty( term ) ) {
						types.add( OWL.AnnotationProperty.asNode() );						
					}
					
					Map1<Node, Triple> map = new Map1<Node, Triple>() {
					    public Triple map1( Node o ) {
					         return Triple.create( s, p, o );
					    }
					};
					
					return WrappedIterator.create( types.iterator() ).mapWith( map );
				}
			}
		);
		
		registerHandler( CONST, RDF.type, CONST, 
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isType( loader.node2term( s ), loader.node2term( o ) );
				}
			}
		);
		
		registerHandler( VAR, ReasonerVocabulary.directRDFType, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return IteratorUtils.flatten( kb.getTypes( subj, true ).iterator() );
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getIndividuals();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getIndividuals().isEmpty();
				}			
			}				
		);
			
		registerHandler( VAR, ReasonerVocabulary.directRDFType, CONST,
			new SubjectVarHandler() {
				@Override
				public Set<ATermAppl> getSubjects(KnowledgeBase kb, ATermAppl obj) {
					return kb.getInstances( obj, true );
				}

			}
		);		

		registerHandler( CONST, ReasonerVocabulary.directRDFType, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return kb.isIndividual( loader.node2term( subj ) );
				}

				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return objectSetFiller( subj, pred, kb.getTypes( loader.node2term( subj ), true ) );
				}			
			}
		);
			
		registerHandler( CONST, ReasonerVocabulary.directRDFType, CONST, 
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.getInstances( loader.node2term( o ), true ).contains( loader.node2term( s ) );
				}
			}
		);		
		
		registerHandler( VAR, RDF.type, OWL.Class,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getAllClasses();
				}
			}
		);
		
		registerHandler( VAR, RDF.type, RDFS.Class,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getAllClasses();
				}
			}
		);
		
		registerHandler( CONST, RDF.type, OWL.Class,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( s ) );
				}
			}
		);
		
		registerHandler( CONST, RDF.type, RDFS.Class,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( s ) );
				}
			}
		);
		
		registerHandler( CONST, RDF.type, RDF.Property, 
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( s ) );
				}
			}
		);
		
		registerHandler( VAR, RDF.type, RDF.Property, 
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getProperties();
				}
			}
		);
		
		registerHandler( CONST, RDF.type, OWL.ObjectProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isObjectProperty( loader.node2term( s ) );
				}	
			}
		);		
		
		registerHandler( VAR, RDF.type, OWL.ObjectProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getObjectProperties();
				}
			}
		);		
		
		registerHandler( CONST, RDF.type, OWL.DatatypeProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isDatatypeProperty( loader.node2term( s ) );
				}	
			}
		);
				
		registerHandler( VAR, RDF.type, OWL.DatatypeProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getDataProperties();
				}
			}
		);
		
		registerHandler( CONST, RDF.type, OWL.AnnotationProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isAnnotationProperty( loader.node2term( s ) );
				}	
			}
		);
				
		registerHandler( VAR, RDF.type, OWL.AnnotationProperty, 
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getAnnotationProperties();
				}
			}	
		);
		
		registerHandler( CONST, RDF.type, OWL.TransitiveProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isTransitiveProperty( loader.node2term( s ) );
				}			
			}
		);
				
		registerHandler( VAR, RDF.type, OWL.TransitiveProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getTransitiveProperties();
				}			
			}
		);
		
		registerHandler( CONST, RDF.type, OWL.SymmetricProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isSymmetricProperty( loader.node2term( s ) );
				}
			}
		);
				
		registerHandler( VAR, RDF.type, OWL.SymmetricProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getSymmetricProperties();
				}			
			}
		);
		
		registerHandler( CONST, RDF.type, OWL.FunctionalProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isFunctionalProperty( loader.node2term( s ) );
				}
			}
		);
				
		registerHandler( VAR, RDF.type, OWL.FunctionalProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getFunctionalProperties();
				}			
			}
		);
		
		registerHandler( CONST, RDF.type, OWL.InverseFunctionalProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isInverseFunctionalProperty( loader.node2term( s ) );
				}
			}
		);
				
		registerHandler( VAR, RDF.type, OWL.InverseFunctionalProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getInverseFunctionalProperties();
				}			
			}
		);		
		
		registerHandler( CONST, RDF.type, OWL2.ReflexiveProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isReflexiveProperty( loader.node2term( s ) );
				}
			}
		);
				
		registerHandler( VAR, RDF.type, OWL2.ReflexiveProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getReflexiveProperties();
				}			
			}
		);
		
		registerHandler( CONST, RDF.type, OWL2.IrreflexiveProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isIrreflexiveProperty( loader.node2term( s ) );
				}
			}
		);
				
		registerHandler( VAR, RDF.type, OWL2.IrreflexiveProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getIrreflexiveProperties();
				}			
			}
		);
		
		registerHandler( CONST, RDF.type, OWL2.AsymmetricProperty,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isAsymmetricProperty( loader.node2term( s ) );
				}
			}
		);
				
		registerHandler( VAR, RDF.type, OWL2.AsymmetricProperty,
			new BuiltinTypeQueryHandler() {
				@Override
				public Set<ATermAppl> getResults(KnowledgeBase kb) {
					return kb.getAsymmetricProperties();
				}			
			}
		);

		registerHandler( VAR, RDFS.subClassOf, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return new FlattenningIterator<ATermAppl>( kb.getSuperClasses( subj, false ) );
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getAllClasses();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return true;
				}			
			}
		);	
		
		registerHandler( VAR, RDFS.subClassOf, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectSetFiller( kb.getSubClasses( loader.node2term( o ) ), p, o );
				}	
			}
		);	
				
		registerHandler( CONST, RDFS.subClassOf, VAR,
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( s ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectSetFiller( s, p, kb.getSuperClasses( loader.node2term( s ) ) );
				}	
			}		
		);
		
		registerHandler( CONST, RDFS.subClassOf, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isSubClassOf( loader.node2term( s ), loader.node2term( o ) );
				}
			}
		);
		
		registerHandler( VAR, ReasonerVocabulary.directSubClassOf, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return new FlattenningIterator<ATermAppl>( kb.getSuperClasses( subj, true ) );
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getAllClasses();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return true;
				}			
			}
		);	
		
		registerHandler( VAR, ReasonerVocabulary.directSubClassOf, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( o ) ) && !o.equals( OWL.Nothing.asNode() );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectSetFiller( kb.getSubClasses( loader.node2term( o ), true ), p, o );
				}	
			}
		);	
				
		registerHandler( CONST, ReasonerVocabulary.directSubClassOf, VAR,
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( s ) ) && !o.equals( OWL.Thing.asNode() );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectSetFiller( s, p, kb.getSuperClasses( loader.node2term( s ), true ) );
				}	
			}		
		);
		
		registerHandler( CONST, ReasonerVocabulary.directSubClassOf, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.getSuperClasses( loader.node2term( s ), true ).contains( kb.getAllEquivalentClasses( loader.node2term( o ) ) );
				}
			}
		);
			
		registerHandler( VAR, OWL.equivalentClass, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getAllEquivalentClasses( subj ).iterator();
				}
	
				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getAllClasses();
				}
	
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return true;
				}
			}		
		);
		
		registerHandler( VAR, OWL.equivalentClass, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectFiller( kb.getAllEquivalentClasses( loader.node2term( o ) ), p, o );
				}		
			}		
		);
		
		registerHandler( CONST, OWL.equivalentClass, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( s ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectFiller( s, p, kb.getAllEquivalentClasses( loader.node2term( s ) ) );
				}		
			}
		);
		
		registerHandler( CONST, OWL.equivalentClass, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isEquivalentClass( loader.node2term( s ), loader.node2term( o ) );
				}	
			}
		);
		
		registerHandler( VAR, OWL.disjointWith, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return new FlattenningIterator<ATermAppl>( kb.getDisjointClasses( subj ) );
				}
	
				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getAllClasses();
				}
	
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return true;
				}
			}		
		);
		
		registerHandler( VAR, OWL.disjointWith, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectSetFiller( kb.getDisjointClasses( loader.node2term( o ) ), p, o );
				}				
			}
		);				
		
		registerHandler( CONST, OWL.disjointWith, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isClass( loader.node2term( s ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectSetFiller( s, p, kb.getDisjointClasses( loader.node2term( s ) ) );
				}		
			}
		);				
		
		registerHandler( CONST, OWL.disjointWith, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isDisjointClass( loader.node2term( s ), loader.node2term( o ) );
				}
			}
		);
		
		registerHandler( VAR, OWL.complementOf, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getComplements( subj ).iterator();
				}
	
				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getAllClasses();
				}
	
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return true;
				}
			}
		);
		
		registerHandler( VAR, OWL.complementOf, CONST,
			new SubjectVarHandler() {
				@Override
				public Set<ATermAppl> getSubjects(KnowledgeBase kb, ATermAppl c) {
					return kb.getComplements( c );
				}			
			}
		);
		
		registerHandler( CONST, OWL.complementOf, VAR,
			new ObjectVarHandler() {
				@Override
				public Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl c) {
					return kb.getComplements( c );
				}			
			}
		);
		
		registerHandler( CONST, OWL.complementOf, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isComplement( loader.node2term( s ), loader.node2term( o ) );
				}			
			}
		);
		
		registerHandler( VAR, RDFS.subPropertyOf, VAR,
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return new FlattenningIterator<ATermAppl>( kb.getAllSuperProperties( subj ) );
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getProperties().isEmpty();
				}
			}
		);
		
		registerHandler( VAR, RDFS.subPropertyOf, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectSetFiller( kb.getAllSubProperties( loader.node2term( o ) ), p, o );
				}		
			}	
		);
		
		registerHandler( CONST, RDFS.subPropertyOf, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( s ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectSetFiller( s, p, kb.getAllSuperProperties( loader.node2term( s ) ) );
				}		
			}		
		);
		
		registerHandler( CONST, RDFS.subPropertyOf, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isSubPropertyOf( loader.node2term( s ), loader.node2term( o ) );
				}
			}
		);
		
		
		registerHandler( VAR, ReasonerVocabulary.directSubPropertyOf, VAR,
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return new FlattenningIterator<ATermAppl>( kb.getSuperProperties( subj, true ) );
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getProperties().isEmpty();
				}
			}
		);
		
		registerHandler( VAR, ReasonerVocabulary.directSubPropertyOf, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectSetFiller( kb.getSubProperties( loader.node2term( o ), true ), p, o );
				}		
			}	
		);
		
		registerHandler( CONST, ReasonerVocabulary.directSubPropertyOf, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( s ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectSetFiller( s, p, kb.getSuperProperties( loader.node2term( s ), true ) );
				}		
			}		
		);
		
		registerHandler( CONST, ReasonerVocabulary.directSubPropertyOf, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.getSuperProperties( loader.node2term( s ), true ).contains( 
							kb.getAllEquivalentProperties( loader.node2term( o ) ) );
				}
			}
		);
		
		registerHandler( VAR, RDFS.domain, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getDomains( subj ).iterator();
				}
	
				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}
	
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getProperties().isEmpty();
				}
			}		
		);
		
		registerHandler( VAR, RDFS.domain, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					List<ATermAppl> props = new ArrayList<ATermAppl>();
					ATermAppl domain = loader.node2term( o );
					for( ATermAppl prop : kb.getProperties() ) {
						if( kb.getDomains( prop ).contains( domain ) ) {
							props.add( prop );
						}
					}
					return subjectFiller( props, p, o );
				}		
			}				
		);
		
		registerHandler( CONST, RDFS.domain, VAR,
			new ObjectVarHandler() {
				@Override
				public Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl p) {
					return kb.getDomains( p );
				}
			}
		);		
		
		registerHandler( CONST, RDFS.domain, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.hasDomain( loader.node2term( s ), loader.node2term( o ) );
				}			
			}
		);
		
		registerHandler( VAR, RDFS.range, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getRanges( subj ).iterator();
				}
	
				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}
	
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getProperties().isEmpty();
				}
			}	
		);
		
		registerHandler( VAR, RDFS.range, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( o ) );
				}
				
				@Override
				public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					List<ATermAppl> props = new ArrayList<ATermAppl>();
					ATermAppl range = loader.node2term( o );
					for( ATermAppl prop : kb.getProperties() ) {
						if( kb.getRanges( prop ).contains( range ) ) {
							props.add( prop );
						}
					}
					return subjectFiller( props, p, o );
				}		
			}	
		);
		
		registerHandler( CONST, RDFS.range, VAR,
			new ObjectVarHandler() {
				@Override
				public Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl p) {
					return kb.getRanges( p );
				}
			}
		);		
		
		registerHandler( CONST, RDFS.range, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.hasRange( loader.node2term( s ), loader.node2term( o ) );
				}			
			}
		);
		
		registerHandler( VAR, OWL.equivalentProperty, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getAllEquivalentProperties( subj ).iterator();
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getProperties().isEmpty();
				}
			}
		);
		
		registerHandler( VAR, OWL.equivalentProperty, CONST, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( o ) );
				}		
								
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectFiller( kb.getAllEquivalentProperties( loader.node2term( o ) ), p, o );
				}
			}
		);		
		
		registerHandler( CONST, OWL.equivalentProperty, VAR, 
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isProperty( loader.node2term( s ) );
				}		
								
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectFiller( s, p, kb.getAllEquivalentProperties( loader.node2term( s ) ) );
				}
			}
		);	
		
		registerHandler( CONST, OWL.equivalentProperty, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isEquivalentProperty( loader.node2term( s ), loader.node2term( o ) );
				}			
			}
		);
		
		registerHandler( VAR, OWL.inverseOf, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getInverses( subj ).iterator();
				}
	
				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}
	
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return !kb.getProperties().isEmpty();
				}
			}	
		);
		
		registerHandler( VAR, OWL.inverseOf, CONST,
			new SubjectVarHandler() {
				@Override
				public Set<ATermAppl> getSubjects(KnowledgeBase kb, ATermAppl p) {
					return kb.getInverses( p );
				}	
			}
		);
		
		registerHandler( CONST, OWL.inverseOf, VAR,
			new ObjectVarHandler() {
				@Override
				public Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl p) {
					return kb.getInverses( p );
				}	
			}
		);
		
		registerHandler( CONST, OWL.inverseOf, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isInverse( loader.node2term( s ), loader.node2term( o ) );
				}			
			}
		);
		
		
		registerHandler( VAR, OWL2.propertyDisjointWith, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return new FlattenningIterator<ATermAppl>( kb.getDisjointProperties( subj ) );
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getProperties();
				}

				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
					return kb.getExpressivity().hasDisjointRoles();
				}
			}
		);
		
		registerHandler( VAR, OWL2.propertyDisjointWith, CONST,
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return !kb.getDisjointProperties( loader.node2term( o ) ).isEmpty();
				}		
								
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return subjectSetFiller( kb.getDisjointProperties( loader.node2term( o ) ), p, o );
				}
			}
		);
		
		registerHandler( CONST, OWL2.propertyDisjointWith, VAR,
			new TripleQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return !kb.getDisjointProperties( loader.node2term( s ) ).isEmpty();
				}		
								
				@Override
				public ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return objectSetFiller( s, p, kb.getDisjointProperties( loader.node2term( s ) ) );
				}
			}
		);
		
		registerHandler( CONST, OWL2.propertyDisjointWith, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					ATermAppl prop1 = loader.node2term( s );
					ATermAppl prop2 = loader.node2term( o );
					return ((kb.isObjectProperty( prop1 ) && kb.isObjectProperty( prop2 )) 
							|| (kb.isDatatypeProperty( prop1 ) && kb.isDatatypeProperty( prop2 )))
						 && kb.isDisjointProperty( prop1, prop2 );
				}
			}
		);
		
		registerHandler( VAR, OWL.sameAs, VAR,
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getAllSames( subj ).iterator();
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getIndividuals();
				}			
			
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return !kb.getIndividuals().isEmpty();
				}
			}
		);
		
		registerHandler( VAR, OWL.sameAs, CONST, 
			new SubjectVarHandler() {
				@Override
				public Set<ATermAppl> getSubjects(KnowledgeBase kb, ATermAppl ind) {
					return kb.getAllSames( ind );
				}	
			}				
		);
		
		registerHandler( CONST, OWL.sameAs, VAR,
			new ObjectVarHandler() {
				@Override
				public Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl ind) {
					return kb.getAllSames( ind );
				}	
			}				
		);
		
		registerHandler( CONST, OWL.sameAs, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isSameAs( loader.node2term( s ), loader.node2term( o ) );
				}
			}
		);
		
		registerHandler( VAR, OWL.differentFrom, VAR, 
			new SubjectObjectVarHandler() {
				@Override
				public Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj) {
					return kb.getDifferents( subj ).iterator();
				}

				@Override
				public Collection<ATermAppl> getSubjects(KnowledgeBase kb) {
					return kb.getIndividuals();
				}			
			
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return !kb.getIndividuals().isEmpty();
				}
			}
		);
		
		registerHandler( VAR, OWL.differentFrom, CONST,
			new SubjectVarHandler() {
				@Override
				public Set<ATermAppl> getSubjects(KnowledgeBase kb, ATermAppl ind) {
					return kb.getDifferents( ind );
				}	
			}
		);		
		registerHandler( CONST, OWL.differentFrom, VAR, 
			new ObjectVarHandler() {
				@Override
				public Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl ind) {
					return kb.getDifferents( ind );
				}	
			}
		);				
		
		registerHandler( CONST, OWL.differentFrom, CONST,
			new BooleanQueryHandler() {
				@Override
				public boolean contains(KnowledgeBase kb, GraphLoader loader, Node s, Node p, Node o) {
					return kb.isDifferentFrom( loader.node2term( s ), loader.node2term( o ) );
				}			
			}
		);
	}
	
	public static boolean isBuiltin(Node node) {
		return BUILTIN_KEYWORDS.contains( node );
	}
	
	public static Node normalize(Node node) {
		return node == VAR || node.isVariable() ? VAR : isBuiltin( node ) ? node : CONST;
	}
	
	public static ExtendedIterator<Triple> findTriple(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		Node s = normalize( subj );
		Node p = normalize( pred );
		Node o = normalize( obj );
		
		if( p == VAR && o != VAR && o != CONST ) {
			pred = p = RDF.type.asNode();
		}
		
		TripleQueryHandler qh = QUERY_HANDLERS.get( Triple.create( s, p, o ) );
		
		if( qh == null ) {
			if( log.isLoggable( Level.WARNING ) )
				log.warning(  "No query handler found for " + subj + " " + pred + " " + obj );
		}
		
		return qh == null
			? NullIterator.<Triple>instance()
			: qh.find( kb, loader, subj, pred, obj );
	}
	
	
	public static boolean containsTriple(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		Node s = normalize( subj );
		Node p = normalize( pred );
		Node o = normalize( obj );
		
		if( p == VAR && o != VAR && o != CONST ) {
			pred = p = RDF.type.asNode();
		}
		
		TripleQueryHandler qh = QUERY_HANDLERS.get( Triple.create( s, p, o ) );
		
		if( qh == null ) {
			if( log.isLoggable( Level.WARNING ) )
				log.warning(  "No query handler found for " + subj + " " + pred + " " + obj );
		}

		return (qh != null) && qh.contains( kb, loader, subj, pred, obj );
	}

}
