// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evren Sirin
 */
public class EntityNode<E> extends Node
{

	private boolean active = false;

	private Set<E> entities;

	public EntityNode(final E entity)
	{
		this.entities = Collections.singleton(entity);
	}

	public void addEntities(final Set<E> entity)
	{
		if (entities.size() == 1)
			entities = new HashSet<E>(entities);

		entities.addAll(entity);
	}

	public void addEntity(final E entity)
	{
		if (entities.size() == 1)
			entities = new HashSet<E>(entities);

		entities.add(entity);
	}

	public Set<E> getEntities()
	{
		return entities;
	}

	@Override
	public boolean inputActivated()
	{
		return active ? false : (active = true);
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public void reset()
	{
		active = false;
	}

	@Override
	public String toString()
	{
		return entities.toString();
	}
}
