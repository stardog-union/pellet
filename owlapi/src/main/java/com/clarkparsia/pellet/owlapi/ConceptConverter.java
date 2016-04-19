// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
import aterm.ATermList;
import java.util.HashSet;
import java.util.Set;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Converts concepts expressed as ATerms to OWL-API structures.
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
	private final KnowledgeBase kb;
	private final OWLDataFactory factory;
	private OWLObject obj;
	private Set set;

	public ConceptConverter(final KnowledgeBase kb, final OWLDataFactory factory)
	{
		this.kb = kb;
		this.factory = factory;
	}

	public OWLIndividual convertIndividual(final ATermAppl term)
	{
		IRI iri = null;

		if (!ATermUtils.isBnode(term))
			iri = IRI.create(term.getName());

		if (kb.isIndividual(term))
		{
			if (ATermUtils.isBnode(term))
				return factory.getOWLAnonymousIndividual(((ATermAppl) term.getArgument(0)).getName());
			else
				return factory.getOWLNamedIndividual(iri);
		}
		else
			throw new InternalReasonerException("Cannot convert individual: " + term);
	}

	public OWLObject convert(final ATermAppl term)
	{
		obj = null;

		visit(term);

		return obj;
	}

	public OWLObject getResult()
	{
		return obj;
	}

	@Override
	public void visitTerm(final ATermAppl term)
	{
		obj = null;

		IRI iri = null;

		if (!ATermUtils.isBnode(term))
			iri = IRI.create(term.getName());

		if (term.equals(OWL_THING))
			obj = factory.getOWLThing();
		else
			if (term.equals(OWL_NOTHING))
				obj = factory.getOWLNothing();
			else
				if (kb.isClass(term))
					obj = factory.getOWLClass(iri);
				else
					if (kb.isObjectProperty(term))
					{
						if (ATermUtils.TOP_OBJECT_PROPERTY.equals(term))
							obj = factory.getOWLTopDataProperty();
						else
							if (ATermUtils.BOTTOM_DATA_PROPERTY.equals(term))
								obj = factory.getOWLBottomObjectProperty();
							else
								obj = factory.getOWLObjectProperty(iri);
					}
					else
						if (kb.isDatatypeProperty(term))
						{
							if (ATermUtils.TOP_DATA_PROPERTY.equals(term))
								obj = factory.getOWLTopDataProperty();
							else
								if (ATermUtils.BOTTOM_DATA_PROPERTY.equals(term))
									obj = factory.getOWLBottomDataProperty();
								else
									obj = factory.getOWLDataProperty(iri);

						}
						else
							if (kb.isIndividual(term))
							{
								if (ATermUtils.isBnode(term))
									obj = factory.getOWLAnonymousIndividual(((ATermAppl) term.getArgument(0)).getName());
								else
									obj = factory.getOWLNamedIndividual(iri);
							}
							else
								if (kb.isDatatype(term))
									obj = factory.getOWLDatatype(iri);

		if (obj == null)
			throw new InternalReasonerException("Ontology does not contain: " + term);
	}

	@Override
	public void visitAnd(final ATermAppl term)
	{
		visitList((ATermList) term.getArgument(0));

		if (obj instanceof OWLClassExpression)
			obj = factory.getOWLObjectIntersectionOf(set);
		else
			if (obj instanceof OWLDataRange)
				obj = factory.getOWLDataIntersectionOf(set);

	}

	@Override
	public void visitOr(final ATermAppl term)
	{
		visitList((ATermList) term.getArgument(0));

		if (obj instanceof OWLClassExpression)
			obj = factory.getOWLObjectUnionOf(set);
		else
			if (obj instanceof OWLDataRange)
				obj = factory.getOWLDataUnionOf(set);
	}

	@Override
	public void visitNot(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));

		if (obj instanceof OWLClassExpression)
			obj = factory.getOWLObjectComplementOf((OWLClassExpression) obj);
		else
			if (obj instanceof OWLDataRange)
				obj = factory.getOWLDataComplementOf((OWLDataRange) obj);
	}

	// In the following method(s) we intentionally do not use OWLPropertyExpression<?,?>
	// because of a bug in some Sun's implementation of javac
	// http://bugs.sun.com/view_bug.do?bug_id=6548436
	// Since lack of generic type generates a warning, we suppress it
	@Override
	public void visitSome(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) obj;

		visit((ATermAppl) term.getArgument(1));

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression desc = (OWLClassExpression) obj;

			obj = factory.getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) prop, desc);
		}
		else
		{
			final OWLDataRange datatype = (OWLDataRange) obj;

			obj = factory.getOWLDataSomeValuesFrom((OWLDataProperty) prop, datatype);
		}
	}

	@Override
	public void visitAll(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) obj;

		visit((ATermAppl) term.getArgument(1));

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression desc = (OWLClassExpression) obj;

			obj = factory.getOWLObjectAllValuesFrom((OWLObjectPropertyExpression) prop, desc);
		}
		else
		{
			final OWLDataRange datatype = (OWLDataRange) obj;

			obj = factory.getOWLDataAllValuesFrom((OWLDataProperty) prop, datatype);
		}

	}

	@Override
	public void visitMin(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) obj;

		final int cardinality = Integer.parseInt(term.getArgument(1).toString());

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression c = (OWLClassExpression) convert((ATermAppl) term.getArgument(2));
			obj = factory.getOWLObjectMinCardinality(cardinality, (OWLObjectPropertyExpression) prop, c);
		}
		else
		{
			final OWLDataRange d = (OWLDataRange) convert((ATermAppl) term.getArgument(2));
			obj = factory.getOWLDataMinCardinality(cardinality, (OWLDataProperty) prop, d);
		}
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) obj;

		final int cardinality = Integer.parseInt(term.getArgument(1).toString());

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression c = (OWLClassExpression) convert((ATermAppl) term.getArgument(2));
			obj = factory.getOWLObjectExactCardinality(cardinality, (OWLObjectPropertyExpression) prop, c);
		}
		else
		{
			final OWLDataRange d = (OWLDataRange) convert((ATermAppl) term.getArgument(2));
			obj = factory.getOWLDataExactCardinality(cardinality, (OWLDataProperty) prop, d);
		}
	}

	@Override
	public void visitMax(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) obj;

		final int cardinality = Integer.parseInt(term.getArgument(1).toString());

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression c = (OWLClassExpression) convert((ATermAppl) term.getArgument(2));
			obj = factory.getOWLObjectMaxCardinality(cardinality, (OWLObjectPropertyExpression) prop, c);
		}
		else
		{
			final OWLDataRange d = (OWLDataRange) convert((ATermAppl) term.getArgument(2));
			obj = factory.getOWLDataMaxCardinality(cardinality, (OWLDataProperty) prop, d);
		}
	}

	@Override
	public void visitHasValue(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) obj;

		if (prop instanceof OWLObjectProperty)
		{

			final OWLIndividual ind = convertIndividual((ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0));

			obj = factory.getOWLObjectHasValue((OWLObjectPropertyExpression) prop, ind);
		}
		else
		{

			visit((ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0));

			final OWLLiteral dataVal = (OWLLiteral) obj;

			obj = factory.getOWLDataHasValue((OWLDataProperty) prop, dataVal);
		}
	}

	@Override
	public void visitValue(final ATermAppl term)
	{
		final ATermAppl nominal = (ATermAppl) term.getArgument(0);
		if (ATermUtils.isLiteral(nominal))
		{
			visitLiteral(nominal);
			obj = factory.getOWLDataOneOf((OWLLiteral) obj);
		}
		else
			obj = factory.getOWLObjectOneOf(convertIndividual(nominal));
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;

		obj = factory.getOWLObjectHasSelf(prop);

	}

	@Override
	public void visitOneOf(final ATermAppl term)
	{
		ATermList list = (ATermList) term.getArgument(0);

		if (ATermUtils.isLiteral((ATermAppl) ((ATermAppl) list.getFirst()).getArgument(0)))
		{
			final Set<OWLLiteral> set = new HashSet<>();

			for (; !list.isEmpty(); list = list.getNext())
			{
				final ATermAppl first = (ATermAppl) list.getFirst();
				if (!ATermUtils.isLiteral((ATermAppl) first.getArgument(0)))
					throw new InternalReasonerException("Conversion error, expecting literal but found: " + first);
				visitLiteral((ATermAppl) first.getArgument(0));
				set.add((OWLLiteral) obj);
			}

			obj = factory.getOWLDataOneOf(set);
		}
		else
		{
			final Set<OWLIndividual> set = new HashSet<>();

			for (; !list.isEmpty(); list = list.getNext())
			{
				final ATermAppl first = (ATermAppl) list.getFirst();
				final OWLIndividual ind = convertIndividual((ATermAppl) first.getArgument(0));
				set.add(ind);
			}

			obj = factory.getOWLObjectOneOf(set);
		}
	}

	@Override
	public void visitLiteral(final ATermAppl term)
	{
		// literal(lexicalValue, language, datatypeURI)

		final String lexValue = ((ATermAppl) term.getArgument(0)).toString();
		final ATermAppl lang = (ATermAppl) term.getArgument(1);
		final ATermAppl dtype = (ATermAppl) term.getArgument(2);

		if (dtype.equals(ATermUtils.PLAIN_LITERAL_DATATYPE))
		{
			if (lang.equals(ATermUtils.EMPTY))
				obj = factory.getOWLLiteral(lexValue);
			else
				obj = factory.getOWLLiteral(lexValue, lang.toString());
		}
		else
		{
			final IRI dtypeIRI = IRI.create(dtype.toString());
			final OWLDatatype datatype = factory.getOWLDatatype(dtypeIRI);
			obj = factory.getOWLLiteral(lexValue, datatype);
		}
	}

	@Override
	public void visitList(ATermList list)
	{
		set = null;
		final Set elements = new HashSet();
		while (!list.isEmpty())
		{
			final ATermAppl term = (ATermAppl) list.getFirst();
			visit(term);
			if (obj == null)
				return;
			elements.add(obj);
			list = list.getNext();
		}
		set = elements;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitInverse(final ATermAppl p)
	{
		final OWLObjectProperty prop = (OWLObjectProperty) convert((ATermAppl) p.getArgument(0));
		obj = factory.getOWLObjectInverseOf(prop);
	}

	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		final OWLDatatype baseDatatype = factory.getOWLDatatype(IRI.create(((ATermAppl) dt.getArgument(0)).getName()));

		final Set<OWLFacetRestriction> restrictions = new HashSet<>();
		for (ATermList list = (ATermList) dt.getArgument(1); !list.isEmpty(); list = list.getNext())
		{
			final ATermAppl facet = (ATermAppl) list.getFirst();
			final String facetName = ((ATermAppl) facet.getArgument(0)).getName();
			final ATermAppl facetValue = (ATermAppl) facet.getArgument(1);
			visitLiteral(facetValue);
			restrictions.add(factory.getOWLFacetRestriction(OWLFacet.getFacet(IRI.create(facetName)), (OWLLiteral) obj));
		}
		obj = factory.getOWLDatatypeRestriction(baseDatatype, restrictions);
	}
}
