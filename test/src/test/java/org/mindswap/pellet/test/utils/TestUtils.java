// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.utils;

import aterm.ATermAppl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

public class TestUtils
{

	static Random _rand = new Random(System.currentTimeMillis());

	/**
	 * @param args
	 */
	public static ATermAppl selectRandomConcept(final Individual ind)
	{

		//get all classes
		final Set<?> types = ind.getTypes();
		ATermAppl clazz = null;
		final int MAX = 20;
		int count = 0;
		do
		{
			count++;
			//get _index for concept
			final int index = _rand.nextInt(types.size());

			//get the concept
			for (final Iterator<?> it = types.iterator(); it.hasNext();)
				clazz = (ATermAppl) it.next();
		} while (((clazz == ATermUtils.TOP) || (clazz == ATermUtils.BOTTOM)) && count < MAX);

		return clazz;
	}

	/**
	 * @param args
	 */
	public static ATermAppl selectRandomConcept(final KnowledgeBase kb)
	{

		//get all classes
		final List<?> classes = new ArrayList<Object>(kb.getTBox().getAllClasses());
		ATermAppl clazz = null;

		do
		{

			//get _index for concept
			final int index = _rand.nextInt(classes.size());

			clazz = (ATermAppl) classes.get(index);
		} while ((clazz == ATermUtils.TOP) || (clazz == ATermUtils.BOTTOM));

		return clazz;
	}

	/**
	 * @param args
	 */
	public static ATermAppl selectRandomObjectProperty(final KnowledgeBase kb)
	{

		//get all classes
		final List<?> roles = new ArrayList<Object>(kb.getRBox().getRoles());
		Role role = null;
		do
		{

			//get _index for concept
			final int index = _rand.nextInt(roles.size());

			role = (Role) roles.get(index);

		} while (!role.isObjectRole());

		return role.getName();
	}

	/**
	 * @param args
	 */
	public static ATermAppl selectRandomIndividual(final KnowledgeBase kb)
	{

		//get all ind
		final List<?> inds = new ArrayList<Object>(kb.getIndividuals());

		//get _index for concept
		final int index = _rand.nextInt(inds.size());

		return (ATermAppl) inds.get(index);

	}

}
