// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import openllet.aterm.ATermAppl;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: QueryParameter
 * </p>
 * <p>
 * Description: Class for query parameterization
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Markus Stocker
 */
public class QueryParameters
{

	private final Map<ATermAppl, ATermAppl> _parameters;

	public QueryParameters()
	{
		_parameters = new HashMap<>();
	}

	public QueryParameters(QuerySolution initialBindingParam)
	{
		this();
		QuerySolution initialBinding = initialBindingParam;

		if (initialBinding == null)
			initialBinding = new QuerySolutionMap();

		for (final Iterator<String> iter = initialBinding.varNames(); iter.hasNext();)
		{
			final String varName = iter.next();
			final ATermAppl key = ATermUtils.makeVar(varName);
			final ATermAppl value = JenaUtils.makeATerm(initialBinding.get(varName));
			_parameters.put(key, value);
		}
	}

	public void add(final ATermAppl key, final ATermAppl value)
	{
		_parameters.put(key, value);
	}

	public Set<Map.Entry<ATermAppl, ATermAppl>> entrySet()
	{
		return _parameters.entrySet();
	}

	public boolean cointains(final ATermAppl key)
	{
		return _parameters.containsKey(key);
	}

	public ATermAppl get(final ATermAppl key)
	{
		return _parameters.get(key);
	}

	@Override
	public String toString()
	{
		return _parameters.toString();
	}
}
