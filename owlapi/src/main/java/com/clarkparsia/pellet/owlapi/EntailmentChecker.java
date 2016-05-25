// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapi;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import aterm.ATermAppl;
import com.intrinsec.owlapi.facet.FacetReasonerOWL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.KnowledgeBase;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class EntailmentChecker implements OWLAxiomVisitor, FacetReasonerOWL
{
	public static Logger _logger = Log.getLogger(EntailmentChecker.class);

	public static final Set<AxiomType<?>> UNSUPPORTED_ENTAILMENT = Collections.unmodifiableSet(new HashSet<>(Arrays.<AxiomType<?>> asList(AxiomType.DISJOINT_UNION, AxiomType.DATATYPE_DEFINITION, AxiomType.HAS_KEY, AxiomType.SUB_PROPERTY_CHAIN_OF, AxiomType.SWRL_RULE)));

	private final KnowledgeBase _kb;
	private boolean _isDeferred = false;
	private boolean _isEntailed = false;
	private final EntailmentQueryVisitor _queryVisitor;

	private final PelletReasoner _reasoner;

	@Override
	public PelletReasoner getReasoner()
	{
		return _reasoner;
	}

	public EntailmentChecker(final PelletReasoner reasoner)
	{
		this._reasoner = reasoner;
		_kb = reasoner.getKB();
		_queryVisitor = new EntailmentQueryVisitor(reasoner);
	}

	private void deferAxiom(final OWLIndividualAxiom axiom)
	{
		_isDeferred = true;
		axiom.accept(_queryVisitor);
	}

	private boolean isEntailed(final OWLAxiom axiom)
	{
		_isDeferred = false;
		_isEntailed = false;

		axiom.accept(this);

		return _isDeferred || _isEntailed;
	}

	public boolean isEntailed(final Set<? extends OWLAxiom> axioms)
	{

		if (axioms.isEmpty())
			_logger.warning("Empty ontologies are entailed by any premise document!");
		else
		{
			_queryVisitor.reset();

			for (final OWLAxiom axiom : axioms)
				if (!isEntailed(axiom))
				{
					if (_logger.isLoggable(Level.FINE))
						_logger.fine("Axiom not entailed: (" + axiom + ")");
					return false;
				}

			return _queryVisitor.isEntailed();

		}

		return true;
	}

	public Set<OWLAxiom> findNonEntailments(final Set<? extends OWLAxiom> axioms, final boolean findAll)
	{
		final Set<OWLAxiom> nonEntailments = new HashSet<>();

		if (axioms.isEmpty())
			_logger.warning("Empty ontologies are entailed by any premise document!");
		else
		{
			final Set<OWLAxiom> deferredAxioms = new HashSet<>();

			_queryVisitor.reset();

			for (final OWLAxiom axiom : axioms)
				if (!isEntailed(axiom))
				{
					if (_logger.isLoggable(Level.FINE))
						_logger.fine("Axiom not entailed: (" + axiom + ")");

					nonEntailments.add(axiom);

					if (findAll)
						break;
				}
				else
					if (_isDeferred)
						deferredAxioms.add(axiom);

			if ((findAll || nonEntailments.isEmpty()) && !_queryVisitor.isEntailed())
				nonEntailments.addAll(deferredAxioms);
		}

		return nonEntailments;
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom)
	{
		_isEntailed = _kb.isSubClassOf(_reasoner.term(axiom.getSubClass()), _reasoner.term(axiom.getSuperClass()));
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom)
	{
		final OWLIndividual s = axiom.getSubject();
		final OWLIndividual o = axiom.getObject();
		if (s.isAnonymous() || o.isAnonymous())
		{
			deferAxiom(axiom);
			return;
		}

		final OWLDataFactory factory = _reasoner.getManager().getOWLDataFactory();
		final OWLClassExpression hasValue = factory.getOWLObjectHasValue(axiom.getProperty(), o);
		final OWLClassExpression doesNotHaveValue = factory.getOWLObjectComplementOf(hasValue);
		_isEntailed = _kb.isType(_reasoner.term(s), _reasoner.term(doesNotHaveValue));
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isAsymmetricProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isReflexiveProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom)
	{
		_isEntailed = true;

		final ATermAppl[] terms = axiom.classExpressions().map(_reasoner::term).toArray(ATermAppl[]::new);
		final int n = terms.length;
		for (int i = 0; i < n - 1; i++)
			for (int j = i + 1; j < n; j++)
				if (!_kb.isDisjoint(terms[i], terms[j]))
				{
					_isEntailed = false;
					return;
				}
	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom)
	{
		_isEntailed = _kb.hasDomain(_reasoner.term(axiom.getProperty()), _reasoner.term(axiom.getDomain()));
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom)
	{
		_isEntailed = _kb.hasDomain(_reasoner.term(axiom.getProperty()), _reasoner.term(axiom.getDomain()));
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom)
	{
		_isEntailed = true;

		final Iterator<OWLObjectPropertyExpression> i = axiom.properties().iterator();
		if (i.hasNext())
		{
			final OWLObjectPropertyExpression head = i.next();

			while (i.hasNext() && _isEntailed)
			{
				final OWLObjectPropertyExpression next = i.next();

				_isEntailed = _kb.isEquivalentProperty(_reasoner.term(head), _reasoner.term(next));
			}
		}
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom)
	{
		final OWLIndividual s = axiom.getSubject();
		if (s.isAnonymous())
		{
			deferAxiom(axiom);
			return;
		}
		final OWLDataFactory factory = _reasoner.getManager().getOWLDataFactory();
		final OWLClassExpression hasValue = factory.getOWLDataHasValue(axiom.getProperty(), axiom.getObject());
		final OWLClassExpression doesNotHaveValue = factory.getOWLObjectComplementOf(hasValue);
		_isEntailed = _kb.isType(_reasoner.term(s), _reasoner.term(doesNotHaveValue));
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom)
	{
		_isEntailed = true;

		for (final OWLIndividual ind : asList(axiom.individuals()))
			if (ind.isAnonymous())
			{
				deferAxiom(axiom);
				return;
			}

		final OWLIndividual[] list = axiom.individuals().toArray(OWLIndividual[]::new);

		for (int i = 0; i < list.length - 1; i++)
		{
			final OWLIndividual head = list[i];
			for (int j = i + 1; j < list.length; j++)
			{
				final OWLIndividual next = list[j];

				if (!_kb.isDifferentFrom(_reasoner.term(head), _reasoner.term(next)))
				{
					_isEntailed = false;
					return;
				}
			}
		}
	}

	private void visitProperties(OWLObject[] properties)
	{
		_isEntailed = true;

		final int n = properties.length;

		for (int i = 0; i < n - 1; i++)
			for (int j = i + 1; j < n; j++)
				if (!_kb.isDisjointProperty(_reasoner.term(properties[i]), _reasoner.term(properties[j])))
				{
					_isEntailed = false;
					return;
				}
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom)
	{
		visitProperties(axiom.properties().toArray(OWLDataProperty[]::new));
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom)
	{
		visitProperties(axiom.properties().toArray(OWLObjectPropertyExpression[]::new));
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom)
	{
		_isEntailed = _kb.hasRange(_reasoner.term(axiom.getProperty()), _reasoner.term(axiom.getRange()));
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom)
	{
		final OWLIndividual s = axiom.getSubject();
		final OWLIndividual o = axiom.getObject();

		if (s.isAnonymous() || o.isAnonymous())
		{
			deferAxiom(axiom);
			return;
		}

		_isEntailed = _kb.hasPropertyValue(_reasoner.term(s), _reasoner.term(axiom.getProperty()), _reasoner.term(o));
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isFunctionalProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom)
	{
		_isEntailed = _kb.isSubPropertyOf(_reasoner.term(axiom.getSubProperty()), _reasoner.term(axiom.getSuperProperty()));
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom axiom)
	{
		// Make sure UNSUPPORTED_ENTAILMENT is updated if this function is implemented
		assert UNSUPPORTED_ENTAILMENT.contains(axiom.getAxiomType());

		throw new UnsupportedOperationException("Unsupported entailment query: " + axiom);
	}

	@Override
	public void visit(final OWLDatatypeDefinitionAxiom axiom)
	{
		// Make sure UNSUPPORTED_ENTAILMENT is updated if this function is implemented
		assert UNSUPPORTED_ENTAILMENT.contains(axiom.getAxiomType());

		throw new UnsupportedOperationException("Unsupported entailment query: " + axiom);
	}

	@Override
	public void visit(final OWLDeclarationAxiom axiom)
	{
		_isEntailed = true;
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Ignoring declaration " + axiom);
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isSymmetricProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom)
	{
		_isEntailed = _kb.hasRange(_reasoner.term(axiom.getProperty()), _reasoner.term(axiom.getRange()));
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom)
	{
		_isEntailed = _kb.isFunctionalProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom)
	{
		_isEntailed = true;

		final Iterator<OWLDataPropertyExpression> i = axiom.properties().iterator();
		if (i.hasNext())
		{
			final OWLDataProperty first = (OWLDataProperty) i.next();

			while (i.hasNext() && _isEntailed)
			{
				final OWLDataProperty next = (OWLDataProperty) i.next();

				_isEntailed = _kb.isEquivalentProperty(_reasoner.term(first), _reasoner.term(next));
			}
		}
	}

	@Override
	public void visit(final OWLClassAssertionAxiom axiom)
	{
		final OWLIndividual ind = axiom.getIndividual();
		final OWLClassExpression c = axiom.getClassExpression();

		if (ind.isAnonymous())
		{
			deferAxiom(axiom);
			return;
		}
		_isEntailed = _kb.isType(_reasoner.term(ind), _reasoner.term(c));

	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom)
	{
		_isEntailed = true;

		final Iterator<OWLClassExpression> i = axiom.classExpressions().iterator();
		if (i.hasNext())
		{
			final OWLClassExpression first = i.next();

			while (i.hasNext() && _isEntailed)
			{
				final OWLClassExpression next = i.next();

				_isEntailed = _kb.isEquivalentClass(_reasoner.term(first), _reasoner.term(next));
			}
		}
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom)
	{
		final OWLIndividual s = axiom.getSubject();
		if (s.isAnonymous())
		{
			deferAxiom(axiom);
			return;
		}

		_isEntailed = _kb.hasPropertyValue(_reasoner.term(s), _reasoner.term(axiom.getProperty()), _reasoner.term(axiom.getObject()));
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isTransitiveProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isIrreflexiveProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom)
	{
		_isEntailed = _kb.isSubPropertyOf(_reasoner.term(axiom.getSubProperty()), _reasoner.term(axiom.getSuperProperty()));
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom)
	{
		_isEntailed = _kb.isInverseFunctionalProperty(_reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLHasKeyAxiom axiom)
	{
		// Make sure UNSUPPORTED_ENTAILMENT is updated if this function is implemented
		assert UNSUPPORTED_ENTAILMENT.contains(axiom.getAxiomType());

		throw new UnsupportedOperationException("Unsupported entailment query: " + axiom);
	}

	@Override
	public void visit(final OWLSameIndividualAxiom axiom)
	{

		for (final OWLIndividual ind : asList(axiom.individuals()))
			if (ind.isAnonymous())
			{
				deferAxiom(axiom);
				return;
			}

		_isEntailed = true;

		final Iterator<OWLIndividual> i = axiom.individuals().iterator();
		if (i.hasNext())
		{
			final OWLIndividual first = i.next();

			while (i.hasNext())
			{
				final OWLIndividual next = i.next();

				if (!_kb.isSameAs(_reasoner.term(first), _reasoner.term(next)))
				{
					_isEntailed = false;
					return;
				}
			}
		}
	}

	@Override
	public void visit(final OWLSubPropertyChainOfAxiom axiom)
	{
		// Make sure UNSUPPORTED_ENTAILMENT is updated if this function is implemented
		assert UNSUPPORTED_ENTAILMENT.contains(axiom.getAxiomType());

		throw new UnsupportedOperationException("Unsupported entailment query: " + axiom);
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom)
	{
		_isEntailed = _kb.isInverse(_reasoner.term(axiom.getFirstProperty()), _reasoner.term(axiom.getSecondProperty()));
	}

	@Override
	public void visit(final SWRLRule rule)
	{
		// Make sure UNSUPPORTED_ENTAILMENT is updated if this function is implemented
		assert UNSUPPORTED_ENTAILMENT.contains(rule.getAxiomType());

		throw new UnsupportedOperationException("Unsupported entailment query: " + rule);
	}

	@Override
	public void visit(final OWLAnnotationAssertionAxiom axiom)
	{
		_isEntailed = true;
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Ignoring annotation assertion axiom " + axiom);
	}

	@Override
	public void visit(final OWLAnnotationPropertyDomainAxiom axiom)
	{
		_isEntailed = true;
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Ignoring annotation property domain " + axiom);
	}

	@Override
	public void visit(final OWLAnnotationPropertyRangeAxiom axiom)
	{
		_isEntailed = true;
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Ignoring annotation property range " + axiom);
	}

	@Override
	public void visit(final OWLSubAnnotationPropertyOfAxiom axiom)
	{
		_isEntailed = true;
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Ignoring sub annotation property axiom " + axiom);
	}

}
