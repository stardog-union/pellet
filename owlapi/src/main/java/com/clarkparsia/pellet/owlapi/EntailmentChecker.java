// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class EntailmentChecker implements OWLAxiomVisitor
{
	public static Logger log = Logger.getLogger(EntailmentChecker.class.getName());

	public static final Set<AxiomType<?>> UNSUPPORTED_ENTAILMENT = Collections.unmodifiableSet(new HashSet<>(Arrays.<AxiomType<?>> asList(AxiomType.DISJOINT_UNION, AxiomType.DATATYPE_DEFINITION, AxiomType.HAS_KEY, AxiomType.SUB_PROPERTY_CHAIN_OF, AxiomType.SWRL_RULE)));

	private final PelletReasoner reasoner;
	private final KnowledgeBase kb;
	private boolean isDeferred = false;
	private boolean isEntailed = false;
	private final EntailmentQueryVisitor queryVisitor;

	public EntailmentChecker(final PelletReasoner reasoner)
	{
		this.reasoner = reasoner;
		kb = reasoner.getKB();
		queryVisitor = new EntailmentQueryVisitor(reasoner);
	}

	private void deferAxiom(final OWLIndividualAxiom axiom)
	{
		isDeferred = true;
		axiom.accept(queryVisitor);
	}

	private boolean isEntailed(final OWLAxiom axiom)
	{
		isDeferred = false;
		isEntailed = false;

		axiom.accept(this);

		return isDeferred || isEntailed;
	}

	public boolean isEntailed(final Set<? extends OWLAxiom> axioms)
	{

		if (axioms.isEmpty())
			log.warning("Empty ontologies are entailed by any premise document!");
		else
		{
			queryVisitor.reset();

			for (final OWLAxiom axiom : axioms)
				if (!isEntailed(axiom))
				{
					if (log.isLoggable(Level.FINE))
						log.fine("Axiom not entailed: (" + axiom + ")");
					return false;
				}

			return queryVisitor.isEntailed();

		}

		return true;
	}

	public Set<OWLAxiom> findNonEntailments(final Set<? extends OWLAxiom> axioms, final boolean findAll)
	{
		final Set<OWLAxiom> nonEntailments = new HashSet<>();

		if (axioms.isEmpty())
			log.warning("Empty ontologies are entailed by any premise document!");
		else
		{
			final Set<OWLAxiom> deferredAxioms = new HashSet<>();

			queryVisitor.reset();

			for (final OWLAxiom axiom : axioms)
				if (!isEntailed(axiom))
				{
					if (log.isLoggable(Level.FINE))
						log.fine("Axiom not entailed: (" + axiom + ")");

					nonEntailments.add(axiom);

					if (findAll)
						break;
				}
				else
					if (isDeferred)
						deferredAxioms.add(axiom);

			if ((findAll || nonEntailments.isEmpty()) && !queryVisitor.isEntailed())
				nonEntailments.addAll(deferredAxioms);
		}

		return nonEntailments;
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom)
	{
		isEntailed = kb.isSubClassOf(reasoner.term(axiom.getSubClass()), reasoner.term(axiom.getSuperClass()));
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

		final OWLDataFactory factory = reasoner.getManager().getOWLDataFactory();
		final OWLClassExpression hasValue = factory.getOWLObjectHasValue(axiom.getProperty(), o);
		final OWLClassExpression doesNotHaveValue = factory.getOWLObjectComplementOf(hasValue);
		isEntailed = kb.isType(reasoner.term(s), reasoner.term(doesNotHaveValue));
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isAsymmetricProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isReflexiveProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom)
	{
		isEntailed = true;

		final int n = axiom.getClassExpressions().size();
		final ATermAppl[] terms = new ATermAppl[n];
		final Iterator<OWLClassExpression> expIter = axiom.getClassExpressions().iterator();
		for (int i = 0; i < n; i++)
			terms[i] = reasoner.term(expIter.next());

		for (int i = 0; i < n - 1; i++)
			for (int j = i + 1; j < n; j++)
				if (!kb.isDisjoint(terms[i], terms[j]))
				{
					isEntailed = false;
					return;
				}
	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom)
	{
		isEntailed = kb.hasDomain(reasoner.term(axiom.getProperty()), reasoner.term(axiom.getDomain()));
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom)
	{
		isEntailed = kb.hasDomain(reasoner.term(axiom.getProperty()), reasoner.term(axiom.getDomain()));
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom)
	{
		isEntailed = true;

		final Iterator<OWLObjectPropertyExpression> i = axiom.getProperties().iterator();
		if (i.hasNext())
		{
			final OWLObjectPropertyExpression head = i.next();

			while (i.hasNext() && isEntailed)
			{
				final OWLObjectPropertyExpression next = i.next();

				isEntailed = kb.isEquivalentProperty(reasoner.term(head), reasoner.term(next));
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
		final OWLDataFactory factory = reasoner.getManager().getOWLDataFactory();
		final OWLClassExpression hasValue = factory.getOWLDataHasValue(axiom.getProperty(), axiom.getObject());
		final OWLClassExpression doesNotHaveValue = factory.getOWLObjectComplementOf(hasValue);
		isEntailed = kb.isType(reasoner.term(s), reasoner.term(doesNotHaveValue));
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom)
	{
		isEntailed = true;

		for (final OWLIndividual ind : axiom.getIndividuals())
			if (ind.isAnonymous())
			{
				deferAxiom(axiom);
				return;
			}

		final ArrayList<OWLIndividual> list = new ArrayList<>(axiom.getIndividuals());
		for (int i = 0; i < list.size() - 1; i++)
		{
			final OWLIndividual head = list.get(i);
			for (int j = i + 1; j < list.size(); j++)
			{
				final OWLIndividual next = list.get(j);

				if (!kb.isDifferentFrom(reasoner.term(head), reasoner.term(next)))
				{
					isEntailed = false;
					return;
				}
			}
		}
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom)
	{
		isEntailed = true;

		final int n = axiom.getProperties().size();
		final OWLDataProperty[] properties = axiom.getProperties().toArray(new OWLDataProperty[n]);
		for (int i = 0; i < n - 1; i++)
			for (int j = i + 1; j < n; j++)
				if (!kb.isDisjointProperty(reasoner.term(properties[i]), reasoner.term(properties[j])))
				{
					isEntailed = false;
					return;
				}
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom)
	{
		isEntailed = true;

		final int n = axiom.getProperties().size();
		final OWLObjectPropertyExpression[] properties = axiom.getProperties().toArray(new OWLObjectPropertyExpression[n]);
		for (int i = 0; i < n - 1; i++)
			for (int j = i + 1; j < n; j++)
				if (!kb.isDisjointProperty(reasoner.term(properties[i]), reasoner.term(properties[j])))
				{
					isEntailed = false;
					return;
				}
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom)
	{
		isEntailed = kb.hasRange(reasoner.term(axiom.getProperty()), reasoner.term(axiom.getRange()));
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

		isEntailed = kb.hasPropertyValue(reasoner.term(s), reasoner.term(axiom.getProperty()), reasoner.term(o));
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isFunctionalProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom)
	{
		isEntailed = kb.isSubPropertyOf(reasoner.term(axiom.getSubProperty()), reasoner.term(axiom.getSuperProperty()));
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
		isEntailed = true;
		if (log.isLoggable(Level.FINE))
			log.fine("Ignoring declaration " + axiom);
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isSymmetricProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom)
	{
		isEntailed = kb.hasRange(reasoner.term(axiom.getProperty()), reasoner.term(axiom.getRange()));
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom)
	{
		isEntailed = kb.isFunctionalProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom)
	{
		isEntailed = true;

		final Iterator<OWLDataPropertyExpression> i = axiom.getProperties().iterator();
		if (i.hasNext())
		{
			final OWLDataProperty first = (OWLDataProperty) i.next();

			while (i.hasNext() && isEntailed)
			{
				final OWLDataProperty next = (OWLDataProperty) i.next();

				isEntailed = kb.isEquivalentProperty(reasoner.term(first), reasoner.term(next));
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
		isEntailed = kb.isType(reasoner.term(ind), reasoner.term(c));

	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom)
	{
		isEntailed = true;

		final Iterator<OWLClassExpression> i = axiom.getClassExpressions().iterator();
		if (i.hasNext())
		{
			final OWLClassExpression first = i.next();

			while (i.hasNext() && isEntailed)
			{
				final OWLClassExpression next = i.next();

				isEntailed = kb.isEquivalentClass(reasoner.term(first), reasoner.term(next));
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

		isEntailed = kb.hasPropertyValue(reasoner.term(s), reasoner.term(axiom.getProperty()), reasoner.term(axiom.getObject()));
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isTransitiveProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isIrreflexiveProperty(reasoner.term(axiom.getProperty()));
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom)
	{
		isEntailed = kb.isSubPropertyOf(reasoner.term(axiom.getSubProperty()), reasoner.term(axiom.getSuperProperty()));
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom)
	{
		isEntailed = kb.isInverseFunctionalProperty(reasoner.term(axiom.getProperty()));
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

		for (final OWLIndividual ind : axiom.getIndividuals())
			if (ind.isAnonymous())
			{
				deferAxiom(axiom);
				return;
			}

		isEntailed = true;

		final Iterator<OWLIndividual> i = axiom.getIndividuals().iterator();
		if (i.hasNext())
		{
			final OWLIndividual first = i.next();

			while (i.hasNext())
			{
				final OWLIndividual next = i.next();

				if (!kb.isSameAs(reasoner.term(first), reasoner.term(next)))
				{
					isEntailed = false;
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
		isEntailed = kb.isInverse(reasoner.term(axiom.getFirstProperty()), reasoner.term(axiom.getSecondProperty()));
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
		isEntailed = true;
		if (log.isLoggable(Level.FINE))
			log.fine("Ignoring annotation assertion axiom " + axiom);
	}

	@Override
	public void visit(final OWLAnnotationPropertyDomainAxiom axiom)
	{
		isEntailed = true;
		if (log.isLoggable(Level.FINE))
			log.fine("Ignoring annotation property domain " + axiom);
	}

	@Override
	public void visit(final OWLAnnotationPropertyRangeAxiom axiom)
	{
		isEntailed = true;
		if (log.isLoggable(Level.FINE))
			log.fine("Ignoring annotation property range " + axiom);
	}

	@Override
	public void visit(final OWLSubAnnotationPropertyOfAxiom axiom)
	{
		isEntailed = true;
		if (log.isLoggable(Level.FINE))
			log.fine("Ignoring sub annotation property axiom " + axiom);
	}
}
