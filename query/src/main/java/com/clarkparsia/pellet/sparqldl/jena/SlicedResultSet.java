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
	private final ResultSet results;
	private int row;
	private final long limit;

	public SlicedResultSet(final ResultSet results, final long offset, final long limit)
	{
		this.results = results;
		this.row = 0;
		this.limit = limit;

		for (int i = 0; i < offset && results.hasNext(); i++)
			results.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	 public boolean hasNext()
	{
		return row < limit && results.hasNext();
	}

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 public Binding nextBinding()
	{
		 row++;

		return results.nextBinding();
	}

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public QuerySolution nextSolution()
	{
		row++;

		return results.nextSolution();
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
		 return results.getResultVars();
	 }

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public int getRowNumber()
	{
		return row;
	}

	 /**
	  * {@inheritDoc}
	  */
	@Override
	 public void remove() throws UnsupportedOperationException
	{
		results.remove();
	}

	 @Override
	public String toString()
	{
		return results.toString();
	}

	@Override
	 public Model getResourceModel()
	{
		return null;
	}
}
