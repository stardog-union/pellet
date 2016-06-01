// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.util.List;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.binding.Binding;

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
public class SlicedResultSet implements ResultSet
{
	private final ResultSet _results;
	private int _row;
	private final long _limit;

	public SlicedResultSet(final ResultSet results, final long offset, final long limit)
	{
		this._results = results;
		this._row = 0;
		this._limit = limit;

		for (int i = 0; i < offset && results.hasNext(); i++)
			results.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	 public boolean hasNext()
	{
		return _row < _limit && _results.hasNext();
	}

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 public Binding nextBinding()
	{
		 _row++;

		return _results.nextBinding();
	}

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public QuerySolution nextSolution()
	{
		_row++;

		return _results.nextSolution();
	}

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public QuerySolution next()
	{
		return nextSolution();
	}

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 public List<String> getResultVars()
	{
		 return _results.getResultVars();
	 }

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public int getRowNumber()
	{
		return _row;
	}

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public void remove() throws UnsupportedOperationException
	{
		_results.remove();
	}

	 @Override
	public String toString()
	{
		return _results.toString();
	}

	@Override
	 public Model getResourceModel()
	{
		return null;
	}
}
