// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.model.Rule;
import org.mindswap.pellet.DependencySet;

public class PartialBinding
{
	private final Rule rule;
	private final VariableBinding binding;
	private final DependencySet ds;

	public PartialBinding(final Rule rule, final VariableBinding binding, final DependencySet ds)
	{
		this.rule = rule;
		this.binding = binding;
		this.ds = ds;
	}

	Rule getRule()
	{
		return rule;
	}

	VariableBinding getBinding()
	{
		return binding;
	}

	DependencySet getDependencySet()
	{
		return ds;
	}

	int getBranch()
	{
		return ds.max();
	}
}
