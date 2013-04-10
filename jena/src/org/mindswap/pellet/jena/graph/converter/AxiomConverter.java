package org.mindswap.pellet.jena.graph.converter;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.jena.vocabulary.SWRL;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermList;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Converts axioms from ATerms to Jena triples.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class AxiomConverter {
	private KnowledgeBase kb;
	private ConceptConverter	converter;
	private Graph				graph;

	public AxiomConverter(KnowledgeBase kb, Graph g) {
		this.kb = kb;
		graph = g;
		converter = new ConceptConverter( graph );
	}

	public void convert(ATermAppl axiom) {
		if( axiom.getAFun().equals( ATermUtils.EQCLASSFUN ) ) {
			convertBinary( axiom, OWL.equivalentClass );
		}
		else if( axiom.getAFun().equals( ATermUtils.SUBFUN ) ) {
			convertBinary( axiom, RDFS.subClassOf );
		}
		else if( axiom.getAFun().equals( ATermUtils.DISJOINTFUN ) ) {
			convertBinary( axiom, OWL.disjointWith );
		}
		else if( axiom.getAFun().equals( ATermUtils.DISJOINTSFUN ) ) {
			convertNary( axiom, OWL2.AllDisjointClasses, OWL2.members );
		}
		else if( axiom.getAFun().equals( ATermUtils.EQPROPFUN ) ) {
			convertBinary( axiom, OWL.equivalentProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.SUBPROPFUN ) ) {
			if( axiom.getArgument( 0 ) instanceof ATermList ) {
				Node s = converter.convert( axiom.getArgument( 1 ) );
				Node o = converter.convert( axiom.getArgument( 0 ) );

				TripleAdder.add( graph, s, OWL2.propertyChainAxiom, o );	
			}
			else {
				convertBinary( axiom, RDFS.subPropertyOf );
			}
		}
		else if( axiom.getAFun().equals( ATermUtils.DISJOINTPROPFUN ) ) {
			convertBinary( axiom, OWL2.propertyDisjointWith );
		}
		else if( axiom.getAFun().equals( ATermUtils.DISJOINTPROPSFUN ) ) {
			convertNary( axiom, OWL2.AllDisjointProperties, OWL2.members );
		}
		else if( axiom.getAFun().equals( ATermUtils.DOMAINFUN ) ) {
			convertBinary( axiom, RDFS.domain );
		}
		else if( axiom.getAFun().equals( ATermUtils.RANGEFUN ) ) {
			convertBinary( axiom, RDFS.range );
		}
		else if( axiom.getAFun().equals( ATermUtils.INVPROPFUN ) ) {
			convertBinary( axiom, OWL.inverseOf );
		}
		else if( axiom.getAFun().equals( ATermUtils.TRANSITIVEFUN ) ) {
			convertUnary( axiom, OWL.TransitiveProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.FUNCTIONALFUN ) ) {
			convertUnary( axiom, OWL.FunctionalProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.INVFUNCTIONALFUN ) ) {
			convertUnary( axiom, OWL.InverseFunctionalProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.SYMMETRICFUN ) ) {
			convertUnary( axiom, OWL.SymmetricProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.ASYMMETRICFUN ) ) {
			convertUnary( axiom, OWL2.AsymmetricProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.REFLEXIVEFUN ) ) {
			convertUnary( axiom, OWL2.ReflexiveProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.IRREFLEXIVEFUN ) ) {
			convertUnary( axiom, OWL2.IrreflexiveProperty );
		}
		else if( axiom.getAFun().equals( ATermUtils.TYPEFUN ) ) {
			convertBinary( axiom, RDF.type );
		}
		else if( axiom.getAFun().equals( ATermUtils.SAMEASFUN ) ) {
			convertBinary( axiom, OWL.sameAs );
		}
		else if( axiom.getAFun().equals( ATermUtils.DIFFERENTFUN ) ) {
			convertBinary( axiom, OWL.differentFrom );
		}
		else if( axiom.getAFun().equals( ATermUtils.ALLDIFFERENTFUN ) ) {
			convertNary( axiom, OWL.AllDifferent, OWL2.members );
		}
		else if( axiom.getAFun().equals( ATermUtils.NOTFUN ) ) {
			axiom = (ATermAppl) axiom.getArgument( 0 );

			Node p = converter.convert( axiom.getArgument( 0 ) );
			Node s = converter.convert( axiom.getArgument( 1 ) );
			Node o = converter.convert( axiom.getArgument( 2 ) );

			Node n = Node.createAnon();
			TripleAdder.add( graph, n, RDF.type, OWL2.NegativePropertyAssertion );
			TripleAdder.add( graph, n, RDF.subject, s );
			TripleAdder.add( graph, n, RDF.predicate, p );
			TripleAdder.add( graph, n, RDF.object, o );
		}
		else if( axiom.getAFun().equals( ATermUtils.PROPFUN ) ) {
			Node p = converter.convert( axiom.getArgument( 0 ) );
			Node s = converter.convert( axiom.getArgument( 1 ) );
			Node o = converter.convert( axiom.getArgument( 2 ) );

			TripleAdder.add( graph, s, p, o );
		}
		else if( axiom.getAFun().equals( ATermUtils.RULEFUN ) ) {
			Node node = null;
			
			ATermAppl name = (ATermAppl) axiom.getArgument( 0 );
			if( name == ATermUtils.EMPTY ) {
				node = Node.createAnon();
			}
			else if( ATermUtils.isBnode( name ) ) {
				node = Node.createAnon( new AnonId( ((ATermAppl) name.getArgument( 0 )).getName() ) );
			}
			else {
				node = Node.createURI( name.getName() );
			}
						
			TripleAdder.add( graph, node, RDF.type, SWRL.Imp );
			
			ATermList head = (ATermList) axiom.getArgument( 1 );
			if( head.isEmpty() ) {
				TripleAdder.add( graph, node, SWRL.head, RDF.nil );
			}
			else {
				Node list = null;
				for( ; !head.isEmpty(); head = head.getNext() ) {
					Node atomNode = convertAtom( (ATermAppl) head.getFirst() );
					Node newList = Node.createAnon();
					TripleAdder.add( graph, newList, RDF.type, SWRL.AtomList );
					TripleAdder.add( graph, newList, RDF.first, atomNode );
					if( list != null )
						TripleAdder.add( graph, list, RDF.rest, newList );
					else
						TripleAdder.add( graph, node, SWRL.head, newList );
					list = newList;
				}
				TripleAdder.add( graph, list, RDF.rest, RDF.nil );
			}
			
			ATermList body = (ATermList) axiom.getArgument( 2 );
			if( body.isEmpty() ) {
				TripleAdder.add( graph, node, SWRL.body, RDF.nil );
			}
			else {
				Node list = null;
				for( ; !body.isEmpty(); body = body.getNext() ) {
					Node atomNode = convertAtom( (ATermAppl) body.getFirst() );
					Node newList = Node.createAnon();
					TripleAdder.add( graph, newList, RDF.type, SWRL.AtomList );
					TripleAdder.add( graph, newList, RDF.first, atomNode );
					if( list != null )
						TripleAdder.add( graph, list, RDF.rest, newList );
					else
						TripleAdder.add( graph, node, SWRL.body, newList );
					list = newList;
				}
				TripleAdder.add( graph, list, RDF.rest, RDF.nil );
			}
		}			
	}	

	private Node convertAtom(ATermAppl term) {
		Node atom = Node.createAnon();

		if( term.getAFun().equals( ATermUtils.TYPEFUN ) ) {
			ATermAppl ind = (ATermAppl) term.getArgument( 0 );
			ATermAppl cls = (ATermAppl) term.getArgument( 1 );

			Node indNode = convertAtomObject( ind );
			Node clsNode = converter.convert( cls );
			
			TripleAdder.add( graph, atom, RDF.type, SWRL.ClassAtom );
			TripleAdder.add( graph, atom, SWRL.classPredicate, clsNode );
			TripleAdder.add( graph, atom, SWRL.argument1, indNode );
		}
		else if( term.getAFun().equals( ATermUtils.PROPFUN ) ) {
			ATermAppl prop = (ATermAppl) term.getArgument( 0 );
			ATermAppl arg1 = (ATermAppl) term.getArgument( 1 );
			ATermAppl arg2 = (ATermAppl) term.getArgument( 2 );
			
			Node propNode = JenaUtils.makeGraphNode( prop );
			Node node1 = convertAtomObject( arg1 );
			Node node2 = convertAtomObject( arg2 );

			if( kb.isObjectProperty( prop ) ) {
				TripleAdder.add( graph, atom, RDF.type, SWRL.IndividualPropertyAtom );
			}
			else if( kb.isDatatypeProperty( prop ) ) {
				TripleAdder.add( graph, atom, RDF.type, SWRL.DatavaluedPropertyAtom );
			}
			else {
				throw new UnsupportedOperationException( "Unknown property: " + prop );
			}
			
			TripleAdder.add( graph, atom, SWRL.propertyPredicate, propNode );
			TripleAdder.add( graph, atom, SWRL.argument1, node1 );
			TripleAdder.add( graph, atom, SWRL.argument2, node2 );

		}
		else if( term.getAFun().equals( ATermUtils.SAMEASFUN ) ) {
			ATermAppl arg1 = (ATermAppl) term.getArgument( 1 );
			ATermAppl arg2 = (ATermAppl) term.getArgument( 2 );
			
			Node node1 = convertAtomObject( arg1 );
			Node node2 = convertAtomObject( arg2 );
			
			TripleAdder.add( graph, atom, RDF.type, SWRL.SameIndividualAtom );
			TripleAdder.add( graph, atom, SWRL.argument1, node1 );
			TripleAdder.add( graph, atom, SWRL.argument2, node2 );		
		}
		else if( term.getAFun().equals( ATermUtils.DIFFERENTFUN ) ) {
			ATermAppl arg1 = (ATermAppl) term.getArgument( 1 );
			ATermAppl arg2 = (ATermAppl) term.getArgument( 2 );
			
			Node node1 = convertAtomObject( arg1 );
			Node node2 = convertAtomObject( arg2 );
			
			TripleAdder.add( graph, atom, RDF.type, SWRL.DifferentIndividualsAtom );
			TripleAdder.add( graph, atom, SWRL.argument1, node1 );
			TripleAdder.add( graph, atom, SWRL.argument2, node2 );					
		}
		else if( term.getAFun().equals( ATermUtils.BUILTINFUN ) ) {
			ATermList args = (ATermList) term.getArgument( 0 );
			ATermAppl builtin = (ATermAppl) args.getFirst();
			args = args.getNext();
			
			TripleAdder.add( graph, atom, RDF.type, SWRL.BuiltinAtom );
			TripleAdder.add( graph, atom, SWRL.builtin, Node.createURI( builtin.toString() ) );			
			
			if( args.isEmpty() ) {
				TripleAdder.add( graph, atom, SWRL.arguments, RDF.nil );
			}
			else {
				Node list = null;
				for( ; !args.isEmpty(); args = args.getNext() ) {
					Node atomNode = convertAtomObject( (ATermAppl) args.getFirst() );
					Node newList = Node.createAnon();
					TripleAdder.add( graph, newList, RDF.first, atomNode );
					if( list != null )
						TripleAdder.add( graph, list, RDF.rest, newList );
					else
						TripleAdder.add( graph, atom, SWRL.arguments, newList );
					list = newList;
				}
				TripleAdder.add( graph, list, RDF.rest, RDF.nil );
			}		
		}
		else {
			throw new UnsupportedOperationException( "Unsupported atom: " + atom );
		}

		return atom;
	}

	private Node convertAtomObject(ATermAppl t) {
		Node node;
		if( ATermUtils.isVar( t ) ) {
			node = JenaUtils.makeGraphNode( (ATermAppl) t.getArgument( 0 ) );
			TripleAdder.add( graph, node, RDF.type, SWRL.Variable );
		}
		else {
			node = JenaUtils.makeGraphNode( t );
		}
		
		return node;
	}

	private void convertNary(ATermAppl axiom, Resource type, Property p) {
		Node n = Node.createAnon();
		TripleAdder.add( graph, n, RDF.type, type );

		ATermList concepts = (ATermList) axiom.getArgument( 0 );
		converter.visitList( concepts );

		TripleAdder.add( graph, n, p, converter.getResult() );
	}

	private void convertBinary(ATermAppl axiom, Property p) {
		Node s = converter.convert( axiom.getArgument( 0 ) );
		Node o = converter.convert( axiom.getArgument( 1 ) );

		TripleAdder.add( graph, s, p, o );
	}

	private void convertUnary(ATermAppl axiom, Resource o) {
		Node s = converter.convert( axiom.getArgument( 0 ) );

		TripleAdder.add( graph, s, RDF.type.asNode(), o.asNode() );
	}

}
