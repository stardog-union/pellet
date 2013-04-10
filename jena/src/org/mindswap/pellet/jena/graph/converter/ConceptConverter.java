package org.mindswap.pellet.jena.graph.converter;

import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.output.ATermVisitor;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Converts concepts expressed as ATerms to Jena triples.
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
public class ConceptConverter extends ATermBaseVisitor implements ATermVisitor {
	private Graph	graph;
	private Node	subj;
	private Node	obj;

	public ConceptConverter(Graph g) {
		graph = g;
	}

	public Node convert(ATerm term) {
		return convert( term, null );
	}

	public Node convert(ATerm term, Node s) {
		Node prevSubj = subj;
		subj = s;
		obj = null;

		if( term instanceof ATermAppl ) {
			visit( (ATermAppl) term );
		}
		else if( term instanceof ATermInt ) {
			obj = Node.createLiteral( term.toString(), null, XSDDatatype.XSDnonNegativeInteger );
		}
		else if( term instanceof ATermList ) {
			visitList( (ATermList) term );
		}
		else {
			throw new IllegalArgumentException( term.toString() );
		}

		subj = prevSubj;

		return obj;
	}

	public Node getResult() {
		return obj;
	}

	public void visitTerm(ATermAppl term) {
		obj = JenaUtils.makeGraphNode( term );
	}

	private void createClassExpression(Property p) {		
		createExpression( p );
	}

	private void createDataExpression(Property p) {		
		createExpression( p );
	}
	
	private void createExpression(Property p) {	
		if( subj != null ) {
			TripleAdder.add( graph, subj, p, obj );
		}
		else {
			Node c = Node.createAnon();
			TripleAdder.add( graph, c, p, obj );
			obj = c;
		}
	}

	public void visitAnd(ATermAppl term) {
		visitList( (ATermList) term.getArgument( 0 ) );

		createClassExpression( OWL.intersectionOf );
	}

	public void visitOr(ATermAppl term) {
		visitList( (ATermList) term.getArgument( 0 ) );

		createClassExpression( OWL.unionOf );
	}

	public void visitNot(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );

		createClassExpression( OWL.complementOf );
	}

	private Node createQualifiedRestriction(ATermAppl term, Property restrType) {
		Node restr = createRestriction( term, restrType );

		Node qual = convert( term.getArgument( 2 ) );
		if( !ATermUtils.isTop( (ATermAppl) term.getArgument( 2 ) ) ) {
			TripleAdder.add( graph, restr, OWL2.onClass, qual );
		}

		obj = restr;

		return restr;
	}

	private Node createRestriction(ATermAppl term, Property restrType) {
		Node restr = Node.createAnon();

		Node prop = convert( term.getArgument( 0 ) );
		Node val = convert( term.getArgument( 1 ) );

		TripleAdder.add( graph, restr, RDF.type, OWL.Restriction );
		TripleAdder.add( graph, restr, OWL.onProperty, prop );
		TripleAdder.add( graph, restr, restrType, val );

		obj = restr;

		return restr;
	}

	public void visitSome(ATermAppl term) {
		createRestriction( term, OWL.someValuesFrom );
	}

	public void visitAll(ATermAppl term) {
		createRestriction( term, OWL.allValuesFrom );
	}

	public void visitMin(ATermAppl term) {
		createQualifiedRestriction( term, OWL.minCardinality );
	}

	public void visitCard(ATermAppl term) {
		createQualifiedRestriction( term, OWL.cardinality );
	}

	public void visitMax(ATermAppl term) {
		createQualifiedRestriction( term, OWL.maxCardinality );
	}

	public void visitHasValue(ATermAppl term) {
		createRestriction( term, OWL.hasValue );
	}

	public void visitValue(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
	}

	public void visitSelf(ATermAppl term) {
		Node restr = Node.createAnon();

		Node prop = convert( term.getArgument( 0 ) );

		TripleAdder.add( graph, restr, RDF.type, OWL.Restriction );
		TripleAdder.add( graph, restr, OWL.onProperty, prop );
		TripleAdder.add( graph, restr, OWL2.hasSelf, JenaUtils.XSD_BOOLEAN_TRUE );

		obj = restr;
	}

	public void visitOneOf(ATermAppl term) {
		ATermList list = (ATermList) term.getArgument( 0 );
		visitList( list );

		if( list.isEmpty() || !ATermUtils.isLiteral( (ATermAppl) ((ATermAppl) list.getFirst()).getArgument( 0 ) ) )
			createClassExpression( OWL.oneOf );
		else
			createDataExpression( OWL.oneOf );
	}

	public void visitLiteral(ATermAppl term) {
		obj = JenaUtils.makeGraphNode( term );
	}

	public void visitList(ATermList list) {
		if( list.isEmpty() ) {
			obj = RDF.nil.asNode();
		}
		else {
			Node rdfList = Node.createAnon();

			Node first = convert( list.getFirst() );
			TripleAdder.add( graph, rdfList, RDF.first, first );

			visitList( list.getNext() );
			TripleAdder.add( graph, rdfList, RDF.rest, obj );

			obj = rdfList;
		}
	}

	public void visitInverse(ATermAppl term) {
		Node node = Node.createAnon();

		Node prop = convert( term.getArgument( 0 ) );

		TripleAdder.add( graph, node, OWL.inverseOf, prop );

		obj = node;
	}
	
	public void visitRestrictedDatatype(ATermAppl dt) {
		Node def = Node.createAnon();

		TripleAdder.add( graph, def, RDF.type, RDFS.Datatype );
		TripleAdder.add( graph, def, OWL2.onDatatype, JenaUtils.makeGraphNode( (ATermAppl) dt.getArgument( 0 ) ) );	
			
		Node list = null;
		ATermList restrictions = (ATermList) dt.getArgument( 1 );
		for( ; !restrictions.isEmpty(); restrictions = restrictions.getNext() ) {
			ATermAppl facet = (ATermAppl) restrictions.getFirst();
			
			Node facetNode = Node.createAnon();
			TripleAdder.add( graph, facetNode, JenaUtils.makeGraphNode( (ATermAppl) facet.getArgument( 0 ) ), JenaUtils
					.makeGraphNode( (ATermAppl) facet.getArgument( 1 ) ) );
			
			Node newList = Node.createAnon();
			TripleAdder.add( graph, newList, RDF.first, facetNode );
			if( list != null )
				TripleAdder.add( graph, list, RDF.rest, newList );
			else
				TripleAdder.add( graph, def, OWL2.withRestrictions, newList );
			list = newList;
		}
		TripleAdder.add( graph, list, RDF.rest, RDF.nil );
		
		obj = def;
	}
}
