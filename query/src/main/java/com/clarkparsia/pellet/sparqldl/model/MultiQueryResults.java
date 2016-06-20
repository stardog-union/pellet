// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import openllet.aterm.ATermAppl;

/**
 * <p>
 * Title: Result combining more disjoint query results to prevent generating cross-products.
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
 * @author Petr Kremen
 */
public class MultiQueryResults implements QueryResult
{

	private final List<ATermAppl> _resultVars;

	private final List<QueryResult> _queryResults;

	private int _size;

	public MultiQueryResults(final List<ATermAppl> resultVars, final List<QueryResult> queryResults)
	{
		this._resultVars = resultVars;
		this._queryResults = queryResults;

		_size = 1;
		for (final QueryResult result : queryResults)
			_size *= result.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final ResultBinding binding)
	{
		throw new UnsupportedOperationException("MultiQueryResults do not support addition!");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ATermAppl> getResultVars()
	{
		return _resultVars;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDistinct()
	{
		for (final QueryResult result : _queryResults)
			if (!result.isDistinct())
				return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty()
	{
		return _size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<ResultBinding> iterator()
	{
		return new Iterator<ResultBinding>()
		{
			private final List<Iterator<ResultBinding>> iterators = new ArrayList<>();

			private final List<ResultBinding> bindings = new ArrayList<>();

			private boolean hasNext = init();

			private boolean init()
			{
				for (final QueryResult result : _queryResults)
				{
					final Iterator<ResultBinding> iterator = result.iterator();

					if (!iterator.hasNext())
						return false;

					iterators.add(iterator);
					bindings.add(iterator.next());
				}

				return true;
			}

			private void findNext()
			{
				final ListIterator<Iterator<ResultBinding>> i = iterators.listIterator();

				for (int index = 0; index < iterators.size(); index++)
				{
					Iterator<ResultBinding> iterator = i.next();
					if (iterator.hasNext())
					{
						bindings.set(index, iterator.next());
						return;
					}
					else
						if (index == iterators.size() - 1)
						{
							hasNext = false;
							return;
						}
						else
						{
							iterator = _queryResults.get(index).iterator();
							i.set(iterator);
							bindings.set(index, iterator.next());
						}
				}
			}

			@Override
			public boolean hasNext()
			{
				return hasNext;
			}

			@Override
			public ResultBinding next()
			{
				if (!hasNext())
					throw new NoSuchElementException();

				final ResultBinding result = new ResultBindingImpl();
				for (final ResultBinding binding : bindings)
					result.setValues(binding);

				findNext();

				return result;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size()
	{
		return _size;
	}
}
