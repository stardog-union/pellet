package org.mindswap.pellet.jena.graph.converter;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;

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
public class ConceptConverter extends ATermBaseVisitor
{
	private final Graph _graph;
	private Node _subj;
	private Node _obj;

	public ConceptConverter(final Graph g)
	{
		_graph = g;
	}

	public Node convert(final ATerm term)
	{
		return convert(term, null);
	}

	public Node convert(final ATerm term, final Node s)
	{
		final Node prevSubj = _subj;
		_subj = s;
		_obj = null;

		if (term instanceof ATermAppl)
			visit((ATermAppl) term);
		else
			if (term instanceof ATermInt)
				_obj = NodeFactory.createLiteral(term.toString(), null, XSDDatatype.XSDnonNegativeInteger);
			else
				if (term instanceof ATermList)
					visitList((ATermList) term);
				else
					throw new IllegalArgumentException(term.toString());

		_subj = prevSubj;

		return _obj;
	}

	public Node getResult()
	{
		return _obj;
	}

	@Override
	public void visitTerm(final ATermAppl term)
	{
		_obj = JenaUtils.makeGraphNode(term);
	}

	private void createClassExpression(final Property p)
	{
		createExpression(p);
	}

	private void createDataExpression(final Property p)
	{
		createExpression(p);
	}

	private void createExpression(final Property p)
	{
		if (_subj != null)
			TripleAdder.add(_graph, _subj, p, _obj);
		else
		{
			final Node c = NodeFactory.createBlankNode();
			TripleAdder.add(_graph, c, p, _obj);
			_obj = c;
		}
	}

	@Override
	public void visitAnd(final ATermAppl term)
	{
		visitList((ATermList) term.getArgument(0));

		createClassExpression(OWL.intersectionOf);
	}

	@Override
	public void visitOr(final ATermAppl term)
	{
		visitList((ATermList) term.getArgument(0));

		createClassExpression(OWL.unionOf);
	}

	@Override
	public void visitNot(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));

		createClassExpression(OWL.complementOf);
	}

	private Node createQualifiedRestriction(final ATermAppl term, final Property restrType)
	{
		final Node restr = createRestriction(term, restrType);

		final Node qual = convert(term.getArgument(2));
		if (!ATermUtils.isTop((ATermAppl) term.getArgument(2)))
			TripleAdder.add(_graph, restr, OWL2.onClass, qual);

		_obj = restr;

		return restr;
	}

	private Node createRestriction(final ATermAppl term, final Property restrType)
	{
		final Node restr = NodeFactory.createBlankNode();

		final Node prop = convert(term.getArgument(0));
		final Node val = convert(term.getArgument(1));

		TripleAdder.add(_graph, restr, RDF.type, OWL.Restriction);
		TripleAdder.add(_graph, restr, OWL.onProperty, prop);
		TripleAdder.add(_graph, restr, restrType, val);

		_obj = restr;

		return restr;
	}

	@Override
	public void visitSome(final ATermAppl term)
	{
		createRestriction(term, OWL.someValuesFrom);
	}

	@Override
	public void visitAll(final ATermAppl term)
	{
		createRestriction(term, OWL.allValuesFrom);
	}

	@Override
	public void visitMin(final ATermAppl term)
	{
		createQualifiedRestriction(term, OWL.minCardinality);
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		createQualifiedRestriction(term, OWL.cardinality);
	}

	@Override
	public void visitMax(final ATermAppl term)
	{
		createQualifiedRestriction(term, OWL.maxCardinality);
	}

	@Override
	public void visitHasValue(final ATermAppl term)
	{
		createRestriction(term, OWL.hasValue);
	}

	@Override
	public void visitValue(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		final Node restr = NodeFactory.createBlankNode();

		final Node prop = convert(term.getArgument(0));

		TripleAdder.add(_graph, restr, RDF.type, OWL.Restriction);
		TripleAdder.add(_graph, restr, OWL.onProperty, prop);
		TripleAdder.add(_graph, restr, OWL2.hasSelf, JenaUtils.XSD_BOOLEAN_TRUE);

		_obj = restr;
	}

	@Override
	public void visitOneOf(final ATermAppl term)
	{
		final ATermList list = (ATermList) term.getArgument(0);
		visitList(list);

		if (list.isEmpty() || !ATermUtils.isLiteral((ATermAppl) ((ATermAppl) list.getFirst()).getArgument(0)))
			createClassExpression(OWL.oneOf);
		else
			createDataExpression(OWL.oneOf);
	}

	@Override
	public void visitLiteral(final ATermAppl term)
	{
		_obj = JenaUtils.makeGraphNode(term);
	}

	@Override
	public void visitList(final ATermList list)
	{
		if (list.isEmpty())
			_obj = RDF.nil.asNode();
		else
		{
			final Node rdfList = NodeFactory.createBlankNode();

			final Node first = convert(list.getFirst());
			TripleAdder.add(_graph, rdfList, RDF.first, first);

			visitList(list.getNext());
			TripleAdder.add(_graph, rdfList, RDF.rest, _obj);

			_obj = rdfList;
		}
	}

	@Override
	public void visitInverse(final ATermAppl term)
	{
		final Node node = NodeFactory.createBlankNode();

		final Node prop = convert(term.getArgument(0));

		TripleAdder.add(_graph, node, OWL.inverseOf, prop);

		_obj = node;
	}

	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		final Node def = NodeFactory.createBlankNode();

		TripleAdder.add(_graph, def, RDF.type, RDFS.Datatype);
		TripleAdder.add(_graph, def, OWL2.onDatatype, JenaUtils.makeGraphNode((ATermAppl) dt.getArgument(0)));

		Node list = null;
		ATermList restrictions = (ATermList) dt.getArgument(1);
		for (; !restrictions.isEmpty(); restrictions = restrictions.getNext())
		{
			final ATermAppl facet = (ATermAppl) restrictions.getFirst();

			final Node facetNode = NodeFactory.createBlankNode();
			TripleAdder.add(_graph, facetNode, JenaUtils.makeGraphNode((ATermAppl) facet.getArgument(0)), JenaUtils.makeGraphNode((ATermAppl) facet.getArgument(1)));

			final Node newList = NodeFactory.createBlankNode();
			TripleAdder.add(_graph, newList, RDF.first, facetNode);
			if (list != null)
				TripleAdder.add(_graph, list, RDF.rest, newList);
			else
				TripleAdder.add(_graph, def, OWL2.withRestrictions, newList);
			list = newList;
		}
		TripleAdder.add(_graph, list, RDF.rest, RDF.nil);

		_obj = def;
	}
}
