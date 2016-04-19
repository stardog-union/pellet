// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.SortCondition;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingComparator;

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
public class SortedResultSet implements ResultSet
{
	private final List<Binding> sortedRows;

	private final Iterator<Binding> iterator;

	private final int row;

	private final List<String> resultVars;

	public SortedResultSet(final ResultSet results, final List<SortCondition> sortConditions)
	{
		resultVars = results.getResultVars();

		sortedRows = new ArrayList<>();
		while (results.hasNext())
			sortedRows.add(results.nextBinding());

		final BindingComparator cmp = new BindingComparator(sortConditions);
		Collections.sort(sortedRows, cmp);

		iterator = sortedRows.iterator();
		row = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getResultVars()
	{
		return resultVars;
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
	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOrdered()
	{
		return true;
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
	public Binding nextBinding()
	{
		return iterator.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuerySolution nextSolution()
	{
		return new ResultBinding(null, nextBinding());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return sortedRows.toString();
	}

	@Override
	public Model getResourceModel()
	{
		return null;
	}
}
