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

	private boolean _active = false;

	private Set<E> _entities;

	public EntityNode(final E entity)
	{
		this._entities = Collections.singleton(entity);
	}

	public void addEntities(final Set<E> entity)
	{
		if (_entities.size() == 1)
			_entities = new HashSet<>(_entities);

		_entities.addAll(entity);
	}

	public void addEntity(final E entity)
	{
		if (_entities.size() == 1)
			_entities = new HashSet<>(_entities);

		_entities.add(entity);
	}

	public Set<E> getEntities()
	{
		return _entities;
	}

	@Override
	public boolean inputActivated()
	{
		return _active ? false : (_active = true);
	}

	@Override
	public boolean isActive()
	{
		return _active;
	}

	@Override
	public void reset()
	{
		_active = false;
	}

	@Override
	public String toString()
	{
		return _entities.toString();
	}

	@Override
	public boolean isEntityNode()
	{
		return true;
	}
}
