// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.expressivity;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;
import java.util.Iterator;
import java.util.Set;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.tbox.impl.Unfolding;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 * @author Evren Sirin
 */
public class DLExpressivityChecker extends ProfileBasedExpressivityChecker
{
	private static Set<ATermAppl> TOP_SET = SetUtils.singleton(ATermUtils.TOP);

	private final Visitor m_Visitor;

	private Expressivity m_Expressivity;

	public DLExpressivityChecker(final KnowledgeBase kb)
	{
		super(kb);
		m_Visitor = new Visitor();
	}

	@Override
	public boolean compute(final Expressivity expressivity)
	{
		m_Expressivity = expressivity;

		processIndividuals();
		processClasses();
		processRoles();

		return true;
	}

	@Override
	public boolean updateWith(final Expressivity expressivity, final ATermAppl term)
	{
		m_Expressivity = expressivity;
		m_Visitor.visit(term);
		return true;
	}

	private void processIndividuals()
	{
		if (!m_KB.getABox().isEmpty())
			m_Expressivity.setHasIndividual(true);

		final Iterator<Individual> i = m_KB.getABox().getIndIterator();
		while (i.hasNext())
		{
			final Individual ind = i.next();
			final ATermAppl nominal = ATermUtils.makeValue(ind.getName());
			final Iterator<ATermAppl> j = ind.getTypes().iterator();
			while (j.hasNext())
			{
				final ATermAppl term = j.next();

				if (term.equals(nominal))
					continue;
				m_Visitor.visit(term);
			}
		}
	}

	private void processClasses()
	{
		final TBox tbox = m_KB.getTBox();

		for (final ATermAppl c : m_KB.getAllClasses())
		{
			final Iterator<Unfolding> unfoldC = tbox.unfold(c);
			while (unfoldC.hasNext())
			{
				final Unfolding unf = unfoldC.next();
				m_Visitor.visit(unf.getResult());
			}
		}
	}

	private void processRoles()
	{
		for (final Role r : m_KB.getRBox().getRoles())
		{
			if (r.isBuiltin())
				continue;

			if (r.isDatatypeRole())
			{
				m_Expressivity.setHasDatatype(true);
				if (r.isInverseFunctional())
					m_Expressivity.setHasKeys(true);
			}

			if (r.isAnon())
				for (final Role subRole : r.getSubRoles())
					if (!subRole.isAnon() && !subRole.isBottom())
						m_Expressivity.setHasInverse(true);

			// InverseFunctionalProperty declaration may mean that a named
			// property has an anonymous inverse property which is functional
			// The following condition checks this case
			if (r.isAnon() && r.isFunctional())
				m_Expressivity.setHasInverse(true);
			if (r.isFunctional())
				if (r.isDatatypeRole())
					m_Expressivity.setHasFunctionalityD(true);
				else
					if (r.isObjectRole())
						m_Expressivity.setHasFunctionality(true);
			if (r.isTransitive())
				m_Expressivity.setHasTransitivity(true);
			if (r.isReflexive())
				m_Expressivity.setHasReflexivity(true);
			if (r.isIrreflexive())
				m_Expressivity.setHasIrreflexivity(true);
			if (r.isAsymmetric())
				m_Expressivity.setHasAsymmetry(true);
			if (!r.getDisjointRoles().isEmpty())
				m_Expressivity.setHasDisjointRoles(true);
			if (r.hasComplexSubRole())
				m_Expressivity.setHasComplexSubRoles(true);

			// Each property has itself included in the subroles set. We need
			// at least two properties in the set to conclude there is a role
			// hierarchy defined in the ontology
			if (r.getSubRoles().size() > 1)
				m_Expressivity.setHasRoleHierarchy(true);

			final Set<ATermAppl> domains = r.getDomains();
			if (!domains.isEmpty() && !domains.equals(TOP_SET))
			{
				m_Expressivity.setHasDomain(true);
				for (final ATermAppl domain : domains)
					m_Visitor.visit(domain);
			}

			final Set<ATermAppl> ranges = r.getRanges();
			if (!ranges.isEmpty() && !ranges.equals(TOP_SET))
			{
				m_Expressivity.setHasRange(true);
				for (final ATermAppl range : ranges)
					m_Visitor.visit(range);
			}
		}
	}

	class Visitor extends ATermBaseVisitor
	{
		@Override
		public void visitTerm(final ATermAppl term)
		{
			//
		}

		void visitRole(final ATermAppl p)
		{
			if (!ATermUtils.isPrimitive(p))
			{
				m_Expressivity.setHasInverse(true);
				m_Expressivity.addAnonInverse((ATermAppl) p.getArgument(0));
			}
		}

		@Override
		public void visitAnd(final ATermAppl term)
		{
			visitList((ATermList) term.getArgument(0));
		}

		@Override
		public void visitOr(final ATermAppl term)
		{
			m_Expressivity.setHasNegation(true);
			visitList((ATermList) term.getArgument(0));
		}

		@Override
		public void visitNot(final ATermAppl term)
		{
			m_Expressivity.setHasNegation(true);
			visit((ATermAppl) term.getArgument(0));
		}

		@Override
		public void visitSome(final ATermAppl term)
		{
			visitRole((ATermAppl) term.getArgument(0));
			visit((ATermAppl) term.getArgument(1));
		}

		@Override
		public void visitAll(final ATermAppl term)
		{
			m_Expressivity.setHasAllValues(true);
			final ATerm p = term.getArgument(0);
			// it is possible that due to property chains, a list of properties might have been added
			// to the restriction. this would only happen if we already performed consistency so these
			// properties should already exist in the KB and no need to process them again
			if (p instanceof ATermAppl)
				visitRole((ATermAppl) p);
			visit((ATermAppl) term.getArgument(1));
		}

		@Override
		public void visitCard(final ATermAppl term)
		{
			visitMin(term);
			visitMax(term);
		}

		@Override
		public void visitMin(final ATermAppl term)
		{
			visitRole((ATermAppl) term.getArgument(0));
			final Role role = m_KB.getRole(term.getArgument(0));
			final ATermAppl c = (ATermAppl) term.getArgument(2);
			if (!ATermUtils.isTop(c))
			{
				if (role.isDatatypeRole())
					m_Expressivity.setHasCardinalityD(true);
				else
					m_Expressivity.setHasCardinalityQ(true);
			}
			else
				if (role.isDatatypeRole())
					m_Expressivity.setHasCardinalityD(true);
				else
					m_Expressivity.setHasCardinality(true);
		}

		@Override
		public void visitMax(final ATermAppl term)
		{
			visitRole((ATermAppl) term.getArgument(0));
			final Role role = m_KB.getRole(term.getArgument(0));
			final int cardinality = ((ATermInt) term.getArgument(1)).getInt();
			final ATermAppl c = (ATermAppl) term.getArgument(2);
			if (!ATermUtils.isTop(c))
			{
				if (role.isDatatypeRole())
					m_Expressivity.setHasCardinalityD(true);
				else
					m_Expressivity.setHasCardinalityQ(true);
			}
			else
				if (cardinality > 1)
					if (role.isDatatypeRole())
						m_Expressivity.setHasCardinalityD(true);
					else
						m_Expressivity.setHasCardinality(true);
		}

		@Override
		public void visitHasValue(final ATermAppl term)
		{
			visitRole((ATermAppl) term.getArgument(0));
			visitValue((ATermAppl) term.getArgument(1));
		}

		@Override
		public void visitValue(final ATermAppl term)
		{
			final ATermAppl nom = (ATermAppl) term.getArgument(0);
			if (!ATermUtils.isLiteral(nom))
				m_Expressivity.addNominal(nom);
			else
				m_Expressivity.setHasUserDefinedDatatype(true);
		}

		@Override
		public void visitOneOf(final ATermAppl term)
		{
			m_Expressivity.setHasNegation(true);
			visitList((ATermList) term.getArgument(0));
		}

		@Override
		public void visitLiteral(final ATermAppl term)
		{
			// nothing to do here
		}

		@Override
		public void visitSelf(final ATermAppl term)
		{
			m_Expressivity.setHasReflexivity(true);
			m_Expressivity.setHasIrreflexivity(true);
		}

		public void visitSubClass(final ATermAppl term)
		{
			throw new InternalReasonerException("This function should never be called: " + term);
		}

		@Override
		public void visitInverse(final ATermAppl p)
		{
			m_Expressivity.setHasInverse(true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visitRestrictedDatatype(final ATermAppl dt)
		{
			m_Expressivity.setHasDatatype(true);
			m_Expressivity.setHasUserDefinedDatatype(true);
		}
	}
}
