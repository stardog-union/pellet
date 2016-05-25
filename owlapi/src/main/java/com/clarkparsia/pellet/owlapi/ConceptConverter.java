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
import com.intrinsec.owlapi.facet.FacetFactoryOWL;
import java.util.Collection;
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
public class ConceptConverter extends ATermBaseVisitor implements FacetFactoryOWL
{
	private final KnowledgeBase _kb;
	private OWLObject _obj;
	private Set<OWLObject> _set;

	private final OWLDataFactory _factory;

	@Override
	public OWLDataFactory getFactory()
	{
		return _factory;
	}

	public ConceptConverter(final KnowledgeBase kb, final OWLDataFactory factory)
	{
		_kb = kb;
		_factory = factory;
	}

	public OWLIndividual convertIndividual(final ATermAppl term)
	{
		IRI iri = null;

		if (!ATermUtils.isBnode(term))
			iri = IRI.create(term.getName());

		if (_kb.isIndividual(term))
		{
			if (ATermUtils.isBnode(term))
				return _factory.getOWLAnonymousIndividual(((ATermAppl) term.getArgument(0)).getName());
			else
				return _factory.getOWLNamedIndividual(iri);
		}
		else
			throw new InternalReasonerException("Cannot convert _individual: " + term);
	}

	public OWLObject convert(final ATermAppl term)
	{
		_obj = null;

		visit(term);

		return _obj;
	}

	public OWLObject getResult()
	{
		return _obj;
	}

	@Override
	public void visitTerm(final ATermAppl term)
	{
		_obj = null;

		IRI iri = null;

		if (!ATermUtils.isBnode(term))
			iri = IRI.create(term.getName());

		if (term.equals(OWL_THING))
			_obj = _factory.getOWLThing();
		else
			if (term.equals(OWL_NOTHING))
				_obj = _factory.getOWLNothing();
			else
				if (_kb.isClass(term))
					_obj = _factory.getOWLClass(iri);
				else
					if (_kb.isObjectProperty(term))
					{
						if (ATermUtils.TOP_OBJECT_PROPERTY.equals(term))
							_obj = _factory.getOWLTopDataProperty();
						else
							if (ATermUtils.BOTTOM_DATA_PROPERTY.equals(term))
								_obj = _factory.getOWLBottomObjectProperty();
							else
								_obj = _factory.getOWLObjectProperty(iri);
					}
					else
						if (_kb.isDatatypeProperty(term))
						{
							if (ATermUtils.TOP_DATA_PROPERTY.equals(term))
								_obj = _factory.getOWLTopDataProperty();
							else
								if (ATermUtils.BOTTOM_DATA_PROPERTY.equals(term))
									_obj = _factory.getOWLBottomDataProperty();
								else
									_obj = _factory.getOWLDataProperty(iri);

						}
						else
							if (_kb.isIndividual(term))
							{
								if (ATermUtils.isBnode(term))
									_obj = _factory.getOWLAnonymousIndividual(((ATermAppl) term.getArgument(0)).getName());
								else
									_obj = _factory.getOWLNamedIndividual(iri);
							}
							else
								if (_kb.isDatatype(term))
									_obj = _factory.getOWLDatatype(iri);

		if (_obj == null)
			throw new InternalReasonerException("Ontology does not contain: " + term);
	}

	private <Type, Tmp> Set<Type> dynamicCastTheSet(final Collection<Tmp> set, final Class<Type> c)
	{
		final Set<Type> exprs = new HashSet<>();
		for (final Tmp o : set)
			if (c.isInstance(o))
				exprs.add(c.cast(o));
		return exprs;
	}

	@Override
	public void visitAnd(final ATermAppl term)
	{
		visitList((ATermList) term.getArgument(0));

		if (_obj instanceof OWLClassExpression)
			_obj = _factory.getOWLObjectIntersectionOf(dynamicCastTheSet(_set, OWLClassExpression.class));
		else
			if (_obj instanceof OWLDataRange)
				_obj = _factory.getOWLDataIntersectionOf(dynamicCastTheSet(_set, OWLDataRange.class));

	}

	@Override
	public void visitOr(final ATermAppl term)
	{
		visitList((ATermList) term.getArgument(0));

		if (_obj instanceof OWLClassExpression)
			_obj = _factory.getOWLObjectUnionOf(dynamicCastTheSet(_set, OWLClassExpression.class));
		else
			if (_obj instanceof OWLDataRange)
				_obj = _factory.getOWLDataUnionOf(dynamicCastTheSet(_set, OWLDataRange.class));
	}

	@Override
	public void visitNot(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));

		if (_obj instanceof OWLClassExpression)
			_obj = _factory.getOWLObjectComplementOf((OWLClassExpression) _obj);
		else
			if (_obj instanceof OWLDataRange)
				_obj = _factory.getOWLDataComplementOf((OWLDataRange) _obj);
	}

	// In the following method(s) we intentionally do not use OWLPropertyExpression<?,?>
	// because of a bug in some Sun's implementation of javac
	// http://bugs.sun.com/view_bug.do?bug_id=6548436
	// Since lack of generic type generates a warning, we suppress it
	@Override
	public void visitSome(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) _obj;

		visit((ATermAppl) term.getArgument(1));

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression desc = (OWLClassExpression) _obj;

			_obj = _factory.getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) prop, desc);
		}
		else
		{
			final OWLDataRange datatype = (OWLDataRange) _obj;

			_obj = _factory.getOWLDataSomeValuesFrom((OWLDataProperty) prop, datatype);
		}
	}

	@Override
	public void visitAll(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) _obj;

		visit((ATermAppl) term.getArgument(1));

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression desc = (OWLClassExpression) _obj;

			_obj = _factory.getOWLObjectAllValuesFrom((OWLObjectPropertyExpression) prop, desc);
		}
		else
		{
			final OWLDataRange datatype = (OWLDataRange) _obj;

			_obj = _factory.getOWLDataAllValuesFrom((OWLDataProperty) prop, datatype);
		}

	}

	@Override
	public void visitMin(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) _obj;

		final int cardinality = Integer.parseInt(term.getArgument(1).toString());

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression c = (OWLClassExpression) convert((ATermAppl) term.getArgument(2));
			_obj = _factory.getOWLObjectMinCardinality(cardinality, (OWLObjectPropertyExpression) prop, c);
		}
		else
		{
			final OWLDataRange d = (OWLDataRange) convert((ATermAppl) term.getArgument(2));
			_obj = _factory.getOWLDataMinCardinality(cardinality, (OWLDataProperty) prop, d);
		}
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) _obj;

		final int cardinality = Integer.parseInt(term.getArgument(1).toString());

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression c = (OWLClassExpression) convert((ATermAppl) term.getArgument(2));
			_obj = _factory.getOWLObjectExactCardinality(cardinality, (OWLObjectPropertyExpression) prop, c);
		}
		else
		{
			final OWLDataRange d = (OWLDataRange) convert((ATermAppl) term.getArgument(2));
			_obj = _factory.getOWLDataExactCardinality(cardinality, (OWLDataProperty) prop, d);
		}
	}

	@Override
	public void visitMax(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) _obj;

		final int cardinality = Integer.parseInt(term.getArgument(1).toString());

		if (prop instanceof OWLObjectPropertyExpression)
		{
			final OWLClassExpression c = (OWLClassExpression) convert((ATermAppl) term.getArgument(2));
			_obj = _factory.getOWLObjectMaxCardinality(cardinality, (OWLObjectPropertyExpression) prop, c);
		}
		else
		{
			final OWLDataRange d = (OWLDataRange) convert((ATermAppl) term.getArgument(2));
			_obj = _factory.getOWLDataMaxCardinality(cardinality, (OWLDataProperty) prop, d);
		}
	}

	@Override
	public void visitHasValue(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLPropertyExpression prop = (OWLPropertyExpression) _obj;

		if (prop instanceof OWLObjectProperty)
		{

			final OWLIndividual ind = convertIndividual((ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0));

			_obj = _factory.getOWLObjectHasValue((OWLObjectPropertyExpression) prop, ind);
		}
		else
		{

			visit((ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0));

			final OWLLiteral dataVal = (OWLLiteral) _obj;

			_obj = _factory.getOWLDataHasValue((OWLDataProperty) prop, dataVal);
		}
	}

	@Override
	public void visitValue(final ATermAppl term)
	{
		final ATermAppl nominal = (ATermAppl) term.getArgument(0);
		if (ATermUtils.isLiteral(nominal))
		{
			visitLiteral(nominal);
			_obj = _factory.getOWLDataOneOf((OWLLiteral) _obj);
		}
		else
			_obj = _factory.getOWLObjectOneOf(convertIndividual(nominal));
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		visit((ATermAppl) term.getArgument(0));
		final OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) _obj;

		_obj = _factory.getOWLObjectHasSelf(prop);

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
				set.add((OWLLiteral) _obj);
			}

			_obj = _factory.getOWLDataOneOf(set);
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

			_obj = _factory.getOWLObjectOneOf(set);
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
				_obj = _factory.getOWLLiteral(lexValue);
			else
				_obj = _factory.getOWLLiteral(lexValue, lang.toString());
		}
		else
		{
			final IRI dtypeIRI = IRI.create(dtype.toString());
			final OWLDatatype datatype = _factory.getOWLDatatype(dtypeIRI);
			_obj = _factory.getOWLLiteral(lexValue, datatype);
		}
	}

	@Override
	public void visitList(final ATermList listParam)
	{
		ATermList list = listParam;
		_set = null;
		final Set<OWLObject> elements = new HashSet<>();
		while (!list.isEmpty())
		{
			final ATermAppl term = (ATermAppl) list.getFirst();
			visit(term);
			if (_obj == null)
				return;
			elements.add(_obj);
			list = list.getNext();
		}
		_set = elements;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitInverse(final ATermAppl p)
	{
		final OWLObjectProperty prop = (OWLObjectProperty) convert((ATermAppl) p.getArgument(0));
		_obj = _factory.getOWLObjectInverseOf(prop);
	}

	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		final OWLDatatype baseDatatype = _factory.getOWLDatatype(IRI.create(((ATermAppl) dt.getArgument(0)).getName()));

		final Set<OWLFacetRestriction> restrictions = new HashSet<>();
		for (ATermList list = (ATermList) dt.getArgument(1); !list.isEmpty(); list = list.getNext())
		{
			final ATermAppl facet = (ATermAppl) list.getFirst();
			final String facetName = ((ATermAppl) facet.getArgument(0)).getName();
			final ATermAppl facetValue = (ATermAppl) facet.getArgument(1);
			visitLiteral(facetValue);
			restrictions.add(_factory.getOWLFacetRestriction(OWLFacet.getFacet(IRI.create(facetName)), (OWLLiteral) _obj));
		}
		_obj = _factory.getOWLDatatypeRestriction(baseDatatype, restrictions);
	}
}
