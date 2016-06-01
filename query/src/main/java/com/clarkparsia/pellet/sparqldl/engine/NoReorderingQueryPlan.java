// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;

/**
 * <p>
 * Title: Query Plan that returns the atoms in the _order as they appear in the query.
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
public class NoReorderingQueryPlan extends QueryPlan
{

	private int _index;

	private final int _size;

	public NoReorderingQueryPlan(final Query query)
	{
		super(query);

		_index = 0;

		_size = query.getAtoms().size();
	}

	@Override
	public QueryAtom next(final ResultBinding binding)
	{
		return _query.getAtoms().get(_index++).apply(binding);
	}

	@Override
	public boolean hasNext()
	{
		return _index < _size;
	}

	@Override
	public void back()
	{
		_index--;
	}

	@Override
	public void reset()
	{
		_index = 0;
	}
}
