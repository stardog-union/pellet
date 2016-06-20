// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import static com.clarkparsia.pellet.el.ELSyntaxUtils.isEL;
import static com.clarkparsia.pellet.el.ELSyntaxUtils.simplify;

import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.expressivity.ProfileBasedExpressivityChecker;
import java.util.Collection;
import java.util.Iterator;
import openllet.aterm.AFun;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

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
 */
public class ELExpressivityChecker extends ProfileBasedExpressivityChecker
{

	private Expressivity _expressivity;

	public ELExpressivityChecker(final KnowledgeBase kb)
	{
		super(kb);
	}

	@Override
	public boolean compute(final Expressivity expressivity)
	{
		_expressivity = expressivity;

		if (!processIndividuals())
			return false;
		if (!processClasses())
			return false;
		if (!processRoles())
			return false;
		return true;
	}

	private boolean processIndividuals()
	{
		final IndividualIterator i = _KB.getABox().getIndIterator();
		while (i.hasNext())
		{
			final Individual ind = i.next();
			final ATermAppl nominal = ATermUtils.makeValue(ind.getName());
			for (final ATermAppl term : ind.getTypes())
			{
				if (term.equals(nominal))
					continue;

				if (!isEL(term))
					return false;
			}
		}

		return true;
	}

	private boolean processClasses()
	{
		for (final ATermAppl axiom : _KB.getTBox().getAssertedAxioms())
		{
			final AFun fun = axiom.getAFun();

			if (fun.equals(ATermUtils.DISJOINTSFUN))
			{
				_expressivity.setHasDisjointClasses(true);

				ATermList args = (ATermList) axiom.getArgument(0);
				for (; !args.isEmpty(); args = args.getNext())
					if (!isEL((ATermAppl) args.getFirst()))
						return false;
			}
			else
			{
				final ATermAppl sub = (ATermAppl) axiom.getArgument(0);
				final ATermAppl sup = (ATermAppl) axiom.getArgument(1);

				if (!isEL(sub) || !isEL(sup))
					return false;

				if (fun.equals(ATermUtils.SUBFUN))
				{
					if (ATermUtils.isBottom(simplify(sup)))
						_expressivity.setHasDisjointClasses(true);
				}
				else
					if (fun.equals(ATermUtils.EQCLASSFUN))
					{
						if (ATermUtils.isBottom(simplify(sub)) || ATermUtils.isBottom(simplify(sup)))
							_expressivity.setHasDisjointClasses(true);
					}
					else
						if (fun.equals(ATermUtils.DISJOINTFUN))
							_expressivity.setHasDisjointClasses(true);
						else
							return false;
			}
		}

		return true;
	}

	private boolean processRoles()
	{
		final Collection<Role> roles = _KB.getRBox().getRoles();

		for (final Role r : roles)
		{
			if (r.isBuiltin())
				continue;

			if (r.isDatatypeRole())
				return false;

			if (r.isAnon())
				for (final Role subRole : r.getSubRoles())
					if (!subRole.isAnon() && !subRole.isBottom())
						return false;

			// InverseFunctionalProperty declaration may mean that a named
			// property has an anonymous inverse property which is functional
			// The following _condition checks this case
			if (r.isAnon() && r.isFunctional())
				return false;
			if (r.isFunctional())
				return false;
			if (r.isTransitive())
				_expressivity.setHasTransitivity(true);
			if (r.isReflexive())
				_expressivity.setHasReflexivity(true);
			if (r.isIrreflexive())
				return false;
			if (r.isAsymmetric())
				return false;
			if (!r.getDisjointRoles().isEmpty())
				return false;
			if (r.hasComplexSubRole())
			{
				_expressivity.setHasComplexSubRoles(true);

				// if a property is named, all the properties in its subproperty chains should be named. since we have
				// anonymous inverses automatically created, we can have chains with inverses. in this case all the
				// properties in the chain should b einverse as well as the super property.
				final boolean isInv = r.isAnon();
				for (ATermList chain : r.getSubRoleChains())
					for (; !chain.isEmpty(); chain = chain.getNext())
						if (ATermUtils.isInv((ATermAppl) chain.getFirst()) != isInv)
							return false;
			}

			// Each property has itself included in the subroles set. We need
			// at least two properties in the set to conclude there is a role
			// hierarchy defined in the ontology
			if (r.getSubRoles().size() > 1)
				_expressivity.setHasRoleHierarchy(true);
		}

		for (final Role r : roles)
		{
			final Iterator<ATermAppl> assertedDomains = _KB.getRBox().getAssertedDomains(r);
			while (assertedDomains.hasNext())
			{
				final ATermAppl domain = assertedDomains.next();
				if (!isEL(domain))
					return false;

				_expressivity.setHasDomain(true);
			}

			final Iterator<ATermAppl> assertedRanges = _KB.getRBox().getAssertedRanges(r);
			while (assertedRanges.hasNext())
			{
				final ATermAppl range = assertedRanges.next();
				if (!isEL(range))
					return false;

				_expressivity.setHasDomain(true);
			}
		}

		return true;
	}

	@Override
	public boolean updateWith(final Expressivity expressivity, final ATermAppl term)
	{
		return false;
	}
}
