// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapi;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.AnnotationClasses;
import org.mindswap.pellet.utils.Comparators;
import org.mindswap.pellet.utils.MultiValueMap;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class PelletVisitor implements OWLObjectVisitor
{
	public static Logger _log = Log.getLogger(PelletVisitor.class);

	private final KnowledgeBase _kb;

	private ATermAppl _term;

	private AtomDObject _swrlDObject;

	private AtomIObject _swrlIObject;

	private RuleAtom _swrlAtom;

	private boolean _addAxioms;

	private boolean _reloadRequired;

	private Set<OWLAxiom> _unsupportedAxioms;

	/*
	 * Only simple properties can be used in cardinality restrictions,
	 * disjointness axioms, irreflexivity and antisymmetry axioms. The following
	 * constants will be used to identify why a certain property should be
	 * treated as simple
	 */
	private MultiValueMap<OWLObjectProperty, OWLObjectPropertyAxiom> compositePropertyAxioms;
	private Set<OWLObjectProperty> simpleProperties;

	public PelletVisitor(final KnowledgeBase kb)
	{
		this._kb = kb;

		clear();
	}

	/**
	 * Clear the visitor _cache about simple properties. Should be called before a reload.
	 */
	public void clear()
	{
		_unsupportedAxioms = new HashSet<>();
		compositePropertyAxioms = new MultiValueMap<>();
		simpleProperties = new HashSet<>();
	}

	private void addUnsupportedAxiom(final OWLAxiom axiom)
	{
		if (!PelletOptions.IGNORE_UNSUPPORTED_AXIOMS)
			throw new UnsupportedFeatureException("Axiom: " + axiom);

		if (_unsupportedAxioms.add(axiom))
			_log.warning("Ignoring unsupported axiom: " + axiom);
	}

	public Set<OWLAxiom> getUnsupportedAxioms()
	{
		return new HashSet<>(_unsupportedAxioms);
	}

	private OWLObjectProperty getNamedProperty(final OWLObjectPropertyExpression ope)
	{
		if (ope.isAnonymous())
			return getNamedProperty(((OWLObjectInverseOf) ope).getInverse());
		else
			return ope.asOWLObjectProperty();
	}

	private void addSimpleProperty(final OWLObjectPropertyExpression ope)
	{
		if (!_addAxioms)
			// no need to mark simple properties during removal
			return;

		final OWLObjectProperty prop = getNamedProperty(ope);
		simpleProperties.add(prop);

		prop.accept(this);
		final Role role = _kb.getRBox().getRole(_term);
		role.setForceSimple(true);
	}

	void verify()
	{
		for (final Map.Entry<OWLObjectProperty, Set<OWLObjectPropertyAxiom>> entry : compositePropertyAxioms.entrySet())
		{
			final OWLObjectProperty nonSimpleProperty = entry.getKey();

			if (!simpleProperties.contains(nonSimpleProperty))
				continue;

			final Set<OWLObjectPropertyAxiom> axioms = entry.getValue();
			for (final OWLObjectPropertyAxiom axiom : axioms)
				addUnsupportedAxiom(axiom);

			final ATermAppl name = ATermUtils.makeTermAppl(nonSimpleProperty.getIRI().toString());
			final Role role = _kb.getRBox().getRole(name);
			role.removeSubRoleChains();
		}
	}

	public void setAddAxiom(final boolean addAxioms)
	{
		this._addAxioms = addAxioms;
	}

	public boolean isReloadRequired()
	{
		return _reloadRequired;
	}

	public ATermAppl result()
	{
		return _term;
	}

	/**
	 * Reset the visitor state about created terms. Should be called before every visit so terms created earlier will not affect the future results.
	 */
	public void reset()
	{
		_term = null;
		_reloadRequired = false;
	}

	@Override
	public void visit(final OWLClass c)
	{
		if (c.isOWLThing())
			_term = ATermUtils.TOP;
		else
			if (c.isOWLNothing())
				_term = ATermUtils.BOTTOM;
			else
				_term = ATermUtils.makeTermAppl(c.getIRI().toString());

		if (_addAxioms)
			_kb.addClass(_term);
	}

	@Override
	public void visit(final OWLAnnotationProperty prop)
	{
		_term = ATermUtils.makeTermAppl(prop.getIRI().toString());

		if (_addAxioms)
			_kb.addAnnotationProperty(_term);
	}

	@Override
	public void visit(final OWLAnonymousIndividual ind)
	{
		_term = ATermUtils.makeBnode(ind.toStringID());

		if (_addAxioms)
			_kb.addIndividual(_term);
	}

	@Override
	public void visit(final OWLNamedIndividual ind)
	{
		_term = ATermUtils.makeTermAppl(ind.getIRI().toString());

		if (_addAxioms)
			_kb.addIndividual(_term);
	}

	@Override
	public void visit(final OWLObjectProperty prop)
	{
		if (prop.isOWLTopObjectProperty())
			_term = ATermUtils.TOP_OBJECT_PROPERTY;
		else
			if (prop.isOWLBottomObjectProperty())
				_term = ATermUtils.BOTTOM_OBJECT_PROPERTY;
			else
			{
				_term = ATermUtils.makeTermAppl(prop.getIRI().toString());

				if (_addAxioms)
					_kb.addObjectProperty(_term);
			}
	}

	@Override
	public void visit(final OWLObjectInverseOf propInv)
	{
		propInv.getInverse().accept(this);
		final ATermAppl p = _term;

		_term = ATermUtils.makeInv(p);
	}

	@Override
	public void visit(final OWLDataProperty prop)
	{

		if (prop.isOWLTopDataProperty())
			_term = ATermUtils.TOP_DATA_PROPERTY;
		else
			if (prop.isOWLBottomDataProperty())
				_term = ATermUtils.BOTTOM_DATA_PROPERTY;
			else
			{
				_term = ATermUtils.makeTermAppl(prop.getIRI().toString());

				if (_addAxioms)
					_kb.addDatatypeProperty(_term);
			}
	}

	@Override
	public void visit(final OWLLiteral constant)
	{
		if (constant.isRDFPlainLiteral())
		{
			final String lexicalValue = constant.getLiteral();
			final String lang = constant.getLang();

			if (lang != null)
				_term = ATermUtils.makePlainLiteral(lexicalValue, lang);
			else
				_term = ATermUtils.makePlainLiteral(lexicalValue);
		}
		else
		{
			final String lexicalValue = constant.getLiteral();
			constant.getDatatype().accept(this);

			final String lang = constant.getLang();
			final ATerm datatype = _term;
			if (lang.isEmpty())
				_term = ATermUtils.makeTypedLiteral(lexicalValue, datatype.toString());
			else
				_term = ATermUtils.makeTypedPlainLangLiteral(lexicalValue, lang);
		}
	}

	@Override
	public void visit(final OWLDatatype ocdt)
	{
		_term = ATermUtils.makeTermAppl(ocdt.getIRI().toString());

		_kb.addDatatype(_term);
	}

	@Override
	public void visit(final OWLObjectIntersectionOf and)
	{
		final Set<OWLClassExpression> operands = asSet(and.operands());
		final ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for (final OWLClassExpression desc : operands)
		{
			desc.accept(this);
			terms[size++] = _term;
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		final ATermList setOfTerms = size > 0 ? ATermUtils.toSet(terms, size) : ATermUtils.EMPTY_LIST;
		_term = ATermUtils.makeAnd(setOfTerms);
	}

	@Override
	public void visit(final OWLObjectUnionOf or)
	{
		final Set<OWLClassExpression> operands = asSet(or.operands());
		final ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for (final OWLClassExpression desc : operands)
		{
			desc.accept(this);
			terms[size++] = _term;
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		final ATermList setOfTerms = size > 0 ? ATermUtils.toSet(terms, size) : ATermUtils.EMPTY_LIST;
		_term = ATermUtils.makeOr(setOfTerms);
	}

	@Override
	public void visit(final OWLObjectComplementOf not)
	{
		final OWLClassExpression desc = not.getOperand();
		desc.accept(this);

		_term = ATermUtils.makeNot(_term);
	}

	@Override
	public void visit(final OWLObjectOneOf enumeration)
	{
		final Set<OWLIndividual> operands = asSet(enumeration.individuals());
		final ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for (final OWLIndividual ind : operands)
		{
			ind.accept(this);
			terms[size++] = ATermUtils.makeValue(_term);
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		final ATermList setOfTerms = size > 0 ? ATermUtils.toSet(terms, size) : ATermUtils.EMPTY_LIST;
		_term = ATermUtils.makeOr(setOfTerms);
	}

	@Override
	public void visit(final OWLObjectSomeValuesFrom restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		restriction.getFiller().accept(this);
		final ATerm c = _term;

		_term = ATermUtils.makeSomeValues(p, c);
	}

	@Override
	public void visit(final OWLObjectAllValuesFrom restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		restriction.getFiller().accept(this);
		final ATerm c = _term;

		_term = ATermUtils.makeAllValues(p, c);
	}

	@Override
	public void visit(final OWLObjectHasValue restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		restriction.getFiller().accept(this);
		final ATermAppl ind = _term;

		_term = ATermUtils.makeHasValue(p, ind);
	}

	@Override
	public void visit(final OWLObjectExactCardinality restriction)
	{
		addSimpleProperty(restriction.getProperty());

		restriction.getProperty().accept(this);
		final ATerm p = _term;
		final int n = restriction.getCardinality();
		restriction.getFiller().accept(this);
		final ATermAppl desc = _term;

		_term = ATermUtils.makeCard(p, n, desc);
	}

	@Override
	public void visit(final OWLObjectMaxCardinality restriction)
	{
		addSimpleProperty(restriction.getProperty());

		restriction.getProperty().accept(this);
		final ATerm p = _term;
		final int n = restriction.getCardinality();
		restriction.getFiller().accept(this);
		final ATermAppl desc = _term;

		_term = ATermUtils.makeMax(p, n, desc);

	}

	@Override
	public void visit(final OWLObjectMinCardinality restriction)
	{
		addSimpleProperty(restriction.getProperty());

		restriction.getProperty().accept(this);
		final ATerm p = _term;
		final int n = restriction.getCardinality();
		restriction.getFiller().accept(this);
		final ATermAppl desc = _term;

		_term = ATermUtils.makeMin(p, n, desc);
	}

	@Override
	public void visit(final OWLDataExactCardinality restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		final int n = restriction.getCardinality();
		restriction.getFiller().accept(this);
		final ATermAppl desc = _term;

		_term = ATermUtils.makeCard(p, n, desc);
	}

	@Override
	public void visit(final OWLDataMaxCardinality restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		final int n = restriction.getCardinality();
		restriction.getFiller().accept(this);
		final ATermAppl desc = _term;

		_term = ATermUtils.makeMax(p, n, desc);
	}

	@Override
	public void visit(final OWLDataMinCardinality restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		final int n = restriction.getCardinality();
		restriction.getFiller().accept(this);
		final ATermAppl desc = _term;

		_term = ATermUtils.makeMin(p, n, desc);
	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom)
	{
		final Set<OWLClassExpression> descriptions = asSet(axiom.classExpressions());
		final int size = descriptions.size();
		if (size > 1)
		{
			final ATermAppl[] terms = new ATermAppl[size];
			int index = 0;
			for (final OWLClassExpression desc : descriptions)
			{
				desc.accept(this);
				terms[index++] = _term;
			}
			Arrays.sort(terms, 0, size, Comparators.termComparator);

			final ATermAppl c1 = terms[0];

			for (int i = 1; i < terms.length; i++)
			{
				final ATermAppl c2 = terms[i];

				if (_addAxioms)
					_kb.addEquivalentClass(c1, c2);
				else
				{
					// create the equivalence axiom
					final ATermAppl sameAxiom = ATermUtils.makeEqClasses(c1, c2);

					// if removal fails we need to reload
					_reloadRequired = !_kb.removeAxiom(sameAxiom);
					// if removal is required there is no point to continue
					if (_reloadRequired)
						return;
				}
			}
		}
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom)
	{
		final Set<OWLClassExpression> descriptions = asSet(axiom.classExpressions());
		final int size = descriptions.size();
		if (size > 1)
		{
			final ATermAppl[] terms = new ATermAppl[size];
			int index = 0;
			for (final OWLClassExpression desc : descriptions)
			{
				desc.accept(this);
				terms[index++] = _term;
			}

			final ATermList list = ATermUtils.toSet(terms, size);
			if (_addAxioms)
				_kb.addDisjointClasses(list);
			else
				_reloadRequired = !_kb.removeAxiom(ATermUtils.makeDisjoints(list));
		}
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom)
	{
		axiom.getSubClass().accept(this);
		final ATermAppl c1 = _term;
		axiom.getSuperClass().accept(this);
		final ATermAppl c2 = _term;

		if (_addAxioms)
			_kb.addSubClass(c1, c2);
		else
		{
			// create the TBox axiom to remove
			final ATermAppl subAxiom = ATermUtils.makeSub(c1, c2);
			// reload is required if remove fails
			_reloadRequired = !_kb.removeAxiom(subAxiom);
		}
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		final OWLObjectPropertyExpression[] props = axiom.properties().toArray(OWLObjectPropertyExpression[]::new);

		for (int i = 0; i < props.length; i++)
			for (int j = i + 1; j < props.length; j++)
			{
				props[i].accept(this);
				final ATermAppl p1 = _term;
				props[j].accept(this);
				final ATermAppl p2 = _term;

				_kb.addEquivalentProperty(p1, p2);
			}
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		final OWLDataPropertyExpression[] props = axiom.properties().toArray(OWLDataPropertyExpression[]::new);

		for (int i = 0; i < props.length; i++)
			for (int j = i + 1; j < props.length; j++)
			{
				props[i].accept(this);
				final ATermAppl p1 = _term;
				props[j].accept(this);
				final ATermAppl p2 = _term;

				_kb.addEquivalentProperty(p1, p2);
			}
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}
		final List<OWLIndividual> inds = axiom.getIndividualsAsList();

		if (inds.size() == 2)
		{
			final Iterator<OWLIndividual> iter = inds.iterator();
			iter.next().accept(this);
			final ATermAppl i1 = _term;
			iter.next().accept(this);
			final ATermAppl i2 = _term;
			_kb.addDifferent(i1, i2);
		}
		else
		{
			final ATermAppl[] terms = new ATermAppl[inds.size()];
			int i = 0;
			for (final OWLIndividual ind : inds)
			{
				ind.accept(this);
				terms[i++] = _term;
			}
			_kb.addAllDifferent(ATermUtils.makeList(terms));
		}
	}

	@Override
	public void visit(final OWLSameIndividualAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		final Iterator<OWLIndividual> eqs = axiom.getIndividualsAsList().iterator();
		if (eqs.hasNext())
		{
			eqs.next().accept(this);
			final ATermAppl i1 = _term;

			while (eqs.hasNext())
			{
				eqs.next().accept(this);
				final ATermAppl i2 = _term;

				_kb.addSame(i1, i2);
			}
		}
	}

	@Override
	public void visit(final OWLHasKeyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getClassExpression().accept(this);
		final ATermAppl c = _term;

		final Set<ATermAppl> properties = new HashSet<>();
		axiom.propertyExpressions().forEach(pe ->
		{
			pe.accept(PelletVisitor.this);
			properties.add(_term);
		});

		_kb.addKey(c, properties);
	}

	@Override
	public void visit(final OWLDataOneOf enumeration)
	{
		ATermList ops = ATermUtils.EMPTY_LIST;
		for (final OWLLiteral value : asList(enumeration.values()))
		{
			value.accept(this);
			ops = ops.insert(ATermUtils.makeValue(result()));
		}
		_term = ATermUtils.makeOr(ops);
	}

	@Override
	public void visit(final OWLDataAllValuesFrom restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		restriction.getFiller().accept(this);
		final ATerm c = _term;

		_term = ATermUtils.makeAllValues(p, c);
	}

	@Override
	public void visit(final OWLDataSomeValuesFrom restriction)
	{
		restriction.getProperty().accept(this);
		final ATerm p = _term;
		restriction.getFiller().accept(this);
		final ATerm c = _term;

		_term = ATermUtils.makeSomeValues(p, c);
	}

	@Override
	public void visit(final OWLDataHasValue restriction)
	{
		restriction.getProperty().accept(this);
		final ATermAppl p = _term;
		restriction.getFiller().accept(this);
		final ATermAppl dv = _term;

		_term = ATermUtils.makeHasValue(p, dv);
	}

	@Override
	public void visit(final OWLOntology ont)
	{
		ont.signature().forEach(entity -> entity.accept(PelletVisitor.this));

		ont.axioms().forEach(axiom ->
		{
			_log.fine(() -> "Load " + axiom);
			axiom.accept(PelletVisitor.this);
		});
	}

	@Override
	public void visit(final OWLObjectHasSelf restriction)
	{
		addSimpleProperty(restriction.getProperty());

		restriction.getProperty().accept(this);
		final ATermAppl p = _term;

		_term = ATermUtils.makeSelf(p);
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		final OWLObjectPropertyExpression[] disjs = axiom.properties().toArray(OWLObjectPropertyExpression[]::new);
		for (int i = 0; i < disjs.length - 1; i++)
		{
			final OWLObjectPropertyExpression prop1 = disjs[i];
			addSimpleProperty(prop1);
			for (int j = i + 1; j < disjs.length; j++)
			{
				final OWLObjectPropertyExpression prop2 = disjs[j];
				addSimpleProperty(prop2);
				prop1.accept(this);
				final ATermAppl p1 = _term;
				prop2.accept(this);
				final ATermAppl p2 = _term;

				_kb.addDisjointProperty(p1, p2);
			}
		}
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		final OWLDataPropertyExpression[] disjs = axiom.properties().toArray(OWLDataPropertyExpression[]::new);
		for (int i = 0; i < disjs.length; i++)
		{
			final OWLDataProperty desc1 = (OWLDataProperty) disjs[i];
			desc1.accept(this);
			final ATermAppl p1 = _term;
			for (int j = i + 1; j < disjs.length; j++)
			{
				final OWLDataProperty desc2 = (OWLDataProperty) disjs[j];
				desc2.accept(this);
				final ATermAppl p2 = _term;

				_kb.addDisjointProperty(p1, p2);
			}
		}
	}

	@Override
	public void visit(final OWLSubPropertyChainOfAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		compositePropertyAxioms.add(getNamedProperty(axiom.getSuperProperty()), axiom);

		axiom.getSuperProperty().accept(this);
		final ATermAppl prop = result();

		final List<OWLObjectPropertyExpression> propChain = axiom.getPropertyChain();
		ATermList chain = ATermUtils.EMPTY_LIST;
		for (int i = propChain.size() - 1; i >= 0; i--)
		{
			propChain.get(i).accept(this);
			chain = chain.insert(result());
		}

		_kb.addSubProperty(chain, prop);
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getOWLClass().accept(this);
		final ATermAppl c = _term;

		ATermList classes = ATermUtils.EMPTY_LIST;
		for (final OWLClassExpression desc : asList(axiom.classExpressions()))
		{
			desc.accept(this);
			classes = classes.insert(result());
		}

		_kb.addDisjointClasses(classes);
		_kb.addEquivalentClass(c, ATermUtils.makeOr(classes));
	}

	@Override
	public void visit(final OWLDataComplementOf node)
	{
		node.getDataRange().accept(this);
		_term = ATermUtils.makeNot(_term);
	}

	@Override
	public void visit(final OWLDataIntersectionOf and)
	{
		final Set<OWLDataRange> operands = asSet(and.operands());
		final ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for (final OWLDataRange desc : operands)
		{
			desc.accept(this);
			terms[size++] = _term;
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		final ATermList setOfTerms = size > 0 ? ATermUtils.toSet(terms, size) : ATermUtils.EMPTY_LIST;
		_term = ATermUtils.makeAnd(setOfTerms);
	}

	@Override
	public void visit(final OWLDatatypeRestriction node)
	{
		node.getDatatype().accept(this);
		final ATermAppl baseDatatype = _term;

		final List<ATermAppl> restrictions = new ArrayList<>();
		for (final OWLFacetRestriction restr : asList(node.facetRestrictions()))
		{
			restr.accept(this);

			if (_term != null)
				restrictions.add(_term);
			else
			{
				_log.warning("Unrecognized facet " + restr.getFacet());

				return;
			}
		}

		if (restrictions.isEmpty())
			_log.warning("A _data range is defined without facet restrictions " + node);
		else
			_term = ATermUtils.makeRestrictedDatatype(baseDatatype, restrictions.toArray(new ATermAppl[restrictions.size()]));
	}

	@Override
	public void visit(final OWLDataUnionOf or)
	{
		final Set<OWLDataRange> operands = asSet(or.operands());
		final ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for (final OWLDataRange desc : operands)
		{
			desc.accept(this);
			terms[size++] = _term;
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		final ATermList setOfTerms = size > 0 ? ATermUtils.toSet(terms, size) : ATermUtils.EMPTY_LIST;
		_term = ATermUtils.makeOr(setOfTerms);
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		addSimpleProperty(axiom.getProperty());

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addAsymmetricProperty(p);
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addReflexiveProperty(p);
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		addSimpleProperty(axiom.getProperty());

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addFunctionalProperty(p);
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getSubject().accept(this);
		final ATermAppl s = _term;
		axiom.getProperty().accept(this);
		final ATermAppl p = _term;
		axiom.getObject().accept(this);
		final ATermAppl o = _term;

		_kb.addNegatedPropertyValue(p, s, o);
	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom)
	{
		axiom.getProperty().accept(this);
		final ATermAppl p = _term;
		axiom.getDomain().accept(this);
		final ATermAppl c = _term;

		if (_addAxioms)
			_kb.addDomain(p, c);
		else
			_reloadRequired = !_kb.removeDomain(p, c);
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom)
	{
		axiom.getProperty().accept(this);
		final ATermAppl p = _term;
		axiom.getDomain().accept(this);
		final ATermAppl c = _term;

		if (_addAxioms)
			_kb.addDomain(p, c);
		else
			_reloadRequired = !_kb.removeDomain(p, c);
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getSubject().accept(this);
		final ATermAppl s = _term;
		axiom.getProperty().accept(this);
		final ATermAppl p = _term;
		axiom.getObject().accept(this);
		final ATermAppl o = _term;

		_kb.addNegatedPropertyValue(p, s, o);
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom)
	{
		axiom.getProperty().accept(this);
		final ATermAppl p = _term;
		axiom.getRange().accept(this);
		final ATermAppl c = _term;

		if (_addAxioms)
			_kb.addRange(p, c);
		else
			_reloadRequired = !_kb.removeRange(p, c);
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom)
	{
		axiom.getSubject().accept(this);
		final ATermAppl subj = _term;
		axiom.getProperty().accept(this);
		final ATermAppl pred = _term;
		axiom.getObject().accept(this);
		final ATermAppl obj = _term;

		if (_addAxioms)
			_kb.addPropertyValue(pred, subj, obj);
		else
			_kb.removePropertyValue(pred, subj, obj);
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getSubProperty().accept(this);
		final ATermAppl sub = _term;
		axiom.getSuperProperty().accept(this);
		final ATermAppl sup = _term;

		_kb.addSubProperty(sub, sup);
	}

	@Override
	public void visit(final OWLDatatypeDefinitionAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getDatatype().accept(this);
		final ATermAppl datatype = _term;
		axiom.getDataRange().accept(this);
		final ATermAppl datarange = _term;

		_kb.addDatatypeDefinition(datatype, datarange);
	}

	@Override
	public void visit(final OWLDeclarationAxiom axiom)
	{
		axiom.getEntity().accept(this);
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addSymmetricProperty(p);
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom)
	{
		axiom.getProperty().accept(this);
		final ATermAppl p = _term;
		axiom.getRange().accept(this);
		final ATermAppl c = _term;

		if (_addAxioms)
			_kb.addRange(p, c);
		else
			_reloadRequired = !_kb.removeRange(p, c);
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addFunctionalProperty(p);
	}

	@Override
	public void visit(final OWLClassAssertionAxiom axiom)
	{
		axiom.getClassExpression().accept(this);
		final ATermAppl c = _term;

		if (AnnotationClasses.contains(c))
			return;

		axiom.getIndividual().accept(this);
		final ATermAppl ind = _term;

		if (_addAxioms)
			_kb.addType(ind, c);
		else
			_kb.removeType(ind, c);
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom)
	{
		axiom.getSubject().accept(this);
		final ATermAppl subj = _term;
		axiom.getProperty().accept(this);
		final ATermAppl pred = _term;
		axiom.getObject().accept(this);
		final ATermAppl obj = _term;

		if (_addAxioms)
			_kb.addPropertyValue(pred, subj, obj);
		else
			_kb.removePropertyValue(pred, subj, obj);
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		compositePropertyAxioms.add(getNamedProperty(axiom.getProperty()), axiom);

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addTransitiveProperty(p);
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		addSimpleProperty(axiom.getProperty());

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addIrreflexiveProperty(p);
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getSubProperty().accept(this);
		final ATermAppl p1 = _term;
		axiom.getSuperProperty().accept(this);
		final ATermAppl p2 = _term;

		_kb.addSubProperty(p1, p2);
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		addSimpleProperty(axiom.getProperty());

		axiom.getProperty().accept(this);
		final ATermAppl p = _term;

		_kb.addInverseFunctionalProperty(p);
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getFirstProperty().accept(this);
		final ATermAppl p1 = _term;
		axiom.getSecondProperty().accept(this);
		final ATermAppl p2 = _term;

		_kb.addInverseProperty(p1, p2);
	}

	@Override
	public void visit(final OWLFacetRestriction node)
	{
		final Facet facet = Facet.Registry.get(ATermUtils.makeTermAppl(node.getFacet().getIRI().toString()));

		if (facet != null)
		{
			final OWLLiteral facetValue = node.getFacetValue();
			facetValue.accept(this);

			_term = ATermUtils.makeFacetRestriction(facet.getName(), _term);
		}
	}

	@Override
	public void visit(final SWRLRule rule)
	{
		if (!PelletOptions.DL_SAFE_RULES)
			return;

		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		final List<RuleAtom> head = parseAtomList(asList(rule.head()));
		final List<RuleAtom> body = parseAtomList(asList(rule.body()));

		if (head == null || body == null)
		{
			addUnsupportedAxiom(rule);

			return;
		}

		final Rule pelletRule = new Rule(head, body);
		_kb.addRule(pelletRule);
	}

	@Override
	public void visit(final OWLAnnotation a)
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(final IRI annotationValue)
	{
		_term = ATermUtils.makeTermAppl(annotationValue.toString());
	}

	@Override
	public void visit(final OWLAnnotationAssertionAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = PelletOptions.USE_ANNOTATION_SUPPORT;
			return;
		}

		if (PelletOptions.USE_ANNOTATION_SUPPORT)
		{
			axiom.getSubject().accept(this);
			final ATermAppl s = _term;
			axiom.getProperty().accept(this);
			final ATermAppl p = _term;
			axiom.getValue().accept(this);
			final ATermAppl o = _term;

			_kb.addAnnotation(s, p, o);
		}
	}

	@Override
	public void visit(final OWLAnnotationPropertyDomainAxiom axiom)
	{
		// TODO: add functionality for reasoning with this kind of axiom
		//addUnsupportedAxiom( axiom );
	}

	@Override
	public void visit(final OWLAnnotationPropertyRangeAxiom axiom)
	{
		// TODO: add functionality for simple reasoning with this kind of axiom
		//addUnsupportedAxiom( axiom );
	}

	@Override
	public void visit(final OWLSubAnnotationPropertyOfAxiom axiom)
	{
		if (!_addAxioms)
		{
			_reloadRequired = true;
			return;
		}

		axiom.getSubProperty().accept(this);
		final ATermAppl sub = _term;
		axiom.getSuperProperty().accept(this);
		final ATermAppl sup = _term;

		_kb.addSubProperty(sub, sup);
	}

	private List<RuleAtom> parseAtomList(final Collection<SWRLAtom> atomList)
	{
		final List<RuleAtom> atoms = new ArrayList<>();

		for (final SWRLAtom atom : atomList)
		{
			atom.accept(this);

			if (_swrlAtom == null)
				return null;

			atoms.add(_swrlAtom);
		}

		return atoms;
	}

	@Override
	public void visit(final SWRLClassAtom atom)
	{
		final OWLClassExpression c = atom.getPredicate();

		final SWRLIArgument v = atom.getArgument();
		v.accept(this);

		final AtomIObject subj = _swrlIObject;

		c.accept(this);
		_swrlAtom = new ClassAtom(_term, subj);
	}

	@Override
	public void visit(final SWRLDataRangeAtom atom)
	{
		// set the _term field
		atom.getPredicate().accept(this);

		atom.getArgument().accept(this);
		_swrlAtom = new DataRangeAtom(_term, _swrlDObject);
	}

	@Override
	public void visit(final SWRLObjectPropertyAtom atom)
	{
		if (atom.getPredicate().isAnonymous())
		{
			_swrlAtom = null;
			return;
		}

		atom.getFirstArgument().accept(this);
		final AtomIObject subj = _swrlIObject;

		atom.getSecondArgument().accept(this);
		final AtomIObject obj = _swrlIObject;

		atom.getPredicate().accept(this);
		_swrlAtom = new IndividualPropertyAtom(_term, subj, obj);

	}

	@Override
	public void visit(final SWRLDataPropertyAtom atom)
	{
		if (atom.getPredicate().isAnonymous())
		{
			_swrlAtom = null;
			return;
		}

		atom.getFirstArgument().accept(this);
		final AtomIObject subj = _swrlIObject;

		atom.getSecondArgument().accept(this);
		final AtomDObject obj = _swrlDObject;

		atom.getPredicate().accept(this);
		_swrlAtom = new DatavaluedPropertyAtom(_term, subj, obj);
	}

	@Override
	public void visit(final SWRLSameIndividualAtom atom)
	{
		atom.getFirstArgument().accept(this);
		final AtomIObject subj = _swrlIObject;

		atom.getSecondArgument().accept(this);
		final AtomIObject obj = _swrlIObject;

		_swrlAtom = new SameIndividualAtom(subj, obj);
	}

	@Override
	public void visit(final SWRLDifferentIndividualsAtom atom)
	{
		atom.getFirstArgument().accept(this);
		final AtomIObject subj = _swrlIObject;

		atom.getSecondArgument().accept(this);
		final AtomIObject obj = _swrlIObject;

		_swrlAtom = new DifferentIndividualsAtom(subj, obj);
	}

	@Override
	public void visit(final SWRLBuiltInAtom atom)
	{
		final List<AtomDObject> arguments = new ArrayList<>((int) atom.allArguments().count());
		for (final SWRLDArgument swrlArg : atom.getArguments())
		{
			swrlArg.accept(this);
			arguments.add(_swrlDObject);
		}
		_swrlAtom = new BuiltInAtom(atom.getPredicate().toString(), arguments);
	}

	@Override
	public void visit(final SWRLVariable var)
	{
		_swrlDObject = new AtomDVariable(var.getIRI().toString());
		_swrlIObject = new AtomIVariable(var.getIRI().toString());
	}

	@Override
	public void visit(final SWRLIndividualArgument iobj)
	{
		iobj.getIndividual().accept(this);
		_swrlIObject = new AtomIConstant(_term);
	}

	@Override
	public void visit(final SWRLLiteralArgument cons)
	{
		cons.getLiteral().accept(this);
		_swrlDObject = new AtomDConstant(_term);
	}

}
