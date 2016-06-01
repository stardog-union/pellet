// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

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
 * @author Petr Kremen
 */
public class MultiFilterWrapper implements Filter
{

	private enum FilterType
	{
		AND, OR;
	}

	private final FilterType _type;
	private final Filter[] _filters;

	private MultiFilterWrapper(final FilterType m, final Filter... filters)
	{
		this._type = m;
		this._filters = filters;
	}

	@Override
	public boolean accept(final ResultBinding binding)
	{
		switch (_type)
		{
			case AND:
				for (final Filter f : _filters)
					if (!f.accept(binding))
						return false;
				return true;
			case OR:
				for (final Filter f : _filters)
					if (f.accept(binding))
						return true;
				return false;
			default:
				throw new RuntimeException("Filter _type not supported : " + _type);
		}
	}

	public static Filter and(final Filter... filters)
	{
		return new MultiFilterWrapper(FilterType.AND, filters);
	}

	public static Filter or(final Filter... filters)
	{
		return new MultiFilterWrapper(FilterType.OR, filters);
	}
}
