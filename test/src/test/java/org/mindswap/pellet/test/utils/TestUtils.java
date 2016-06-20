// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

public class TestUtils
{
	static Random _rand = new Random(System.currentTimeMillis());

	public static ATermAppl selectRandomConcept(final Individual ind)
	{
		final ATermAppl[] types = ind.types()//
				.filter(clazz -> !((clazz == ATermUtils.TOP) || (clazz == ATermUtils.BOTTOM)))//
				.toArray(ATermAppl[]::new);
		ATermAppl clazz = null;
		final int MAX = 20;
		int count = 0;
		if (types.length > 0)
			do
			{
				count++;
				clazz = types[_rand.nextInt(types.length)];
			} while (count < MAX);

		return clazz;
	}

	public static ATermAppl selectRandomConcept(final KnowledgeBase kb)
	{
		final ATermAppl[] classes = kb.getTBox().allClasses().toArray(ATermAppl[]::new);
		ATermAppl clazz = null;
		do
		{
			clazz = classes[_rand.nextInt(classes.length)];
		} while ((clazz == ATermUtils.TOP) || (clazz == ATermUtils.BOTTOM));

		return clazz;
	}

	public static ATermAppl selectRandomObjectProperty(final KnowledgeBase kb)
	{

		//get all classes
		final List<?> roles = new ArrayList<Object>(kb.getRBox().getRoles());
		Role role = null;
		do
		{
			role = (Role) roles.get(_rand.nextInt(roles.size()));
		} while (!role.isObjectRole());

		return role.getName();
	}

	public static ATermAppl selectRandomIndividual(final KnowledgeBase kb)
	{
		final ATermAppl[] inds = kb.individuals().toArray(ATermAppl[]::new);
		return inds[_rand.nextInt(inds.length)];
	}
}
