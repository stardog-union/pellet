// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

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
public class RoleChainCache
{
	private static final String ANON_ROLE = "anonRole";

	private int m_AnonRoleCount;
	private final Map<ATermAppl, MultiValueMap<ATermAppl, ATermAppl>> m_BinaryRoleInclusions;

	public RoleChainCache(final KnowledgeBase kb)
	{
		m_AnonRoleCount = 0;
		m_BinaryRoleInclusions = CollectionUtils.makeMap();

		for (final Role supRole : kb.getRBox().getRoles())
		{
			if (supRole.isAnon())
				continue;

			for (ATermList chain : supRole.getSubRoleChains())
			{
				final int chainLength = chain.getLength();
				if (chainLength <= 1)
					continue;

				ATermAppl r1 = (ATermAppl) chain.getFirst();
				chain = chain.getNext();
				ATermAppl r2 = (ATermAppl) chain.getFirst();
				ATermAppl superRole = createSuperRoleFor(r1, r2);
				for (int i = 1; i < chainLength - 1; i++)
				{
					add(kb, r1, r2, superRole);

					r1 = superRole;
					chain = chain.getNext();
					r2 = (ATermAppl) chain.getFirst();
					superRole = createSuperRoleFor(r1, r2);
				}

				add(kb, r1, r2, supRole.getName());
			}
		}
	}

	public boolean isAnon(final ATermAppl r)
	{
		return r.getName().startsWith(ANON_ROLE);
	}

	public Set<ATermAppl> getAllSuperRoles(final ATermAppl r1, final ATermAppl r2)
	{
		final MultiValueMap<ATermAppl, ATermAppl> innerMap = m_BinaryRoleInclusions.get(r1);
		if (innerMap == null)
			return Collections.emptySet();

		final Set<ATermAppl> superRoles = innerMap.get(r2);
		if (superRoles == null)
			return Collections.emptySet();

		return superRoles;
	}

	private ATermAppl createSuperRoleFor(final ATermAppl r1, final ATermAppl r2)
	{
		final Set<ATermAppl> superRoles = getAllSuperRoles(r1, r2);
		if (superRoles.isEmpty())
			return ATermUtils.makeTermAppl(ANON_ROLE + m_AnonRoleCount++);
		else
			return superRoles.iterator().next();
	}

	private void add(final KnowledgeBase kb, final ATermAppl r1, final ATermAppl r2, final ATermAppl superRole)
	{
		final Role role1 = kb.getRole(r1);
		final Role role2 = kb.getRole(r2);

		if (role1 == null)
		{
			if (role2 == null)
				add(r1, r2, superRole);
			else
				for (final Role sub2 : role2.getSubRoles())
					add(r1, sub2.getName(), superRole);
		}
		else
			if (role2 == null)
				for (final Role sub1 : role1.getSubRoles())
					add(sub1.getName(), r2, superRole);
			else
				for (final Role sub1 : role1.getSubRoles())
					for (final Role sub2 : role2.getSubRoles())
						add(sub1.getName(), sub2.getName(), superRole);
	}

	private boolean add(final ATermAppl r1, final ATermAppl r2, final ATermAppl superRole)
	{
		MultiValueMap<ATermAppl, ATermAppl> innerMap = m_BinaryRoleInclusions.get(r1);
		if (innerMap == null)
		{
			innerMap = new MultiValueMap<>();
			m_BinaryRoleInclusions.put(r1, innerMap);
		}

		return innerMap.add(r2, superRole);
	}

	public void print()
	{
		System.out.println("Role Chains:");
		System.out.println(m_BinaryRoleInclusions);
	}
}
