// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import com.clarkparsia.pellet.rules.builtins.BuiltIn;
import java.util.Arrays;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;

public class BuiltInCondition implements FilterCondition
{
	private final ABox _abox;
	private final String _name;
	private final BuiltIn _builtin;
	private final NodeProvider[] _args;

	public BuiltInCondition(final ABox abox, final String name, final BuiltIn builtin, final NodeProvider[] args)
	{
		this._abox = abox;
		this._name = name;
		this._builtin = builtin;
		this._args = args;
		for (final NodeProvider arg : args)
			if (arg == null)
				throw new NullPointerException();
	}

	@Override
	public boolean test(final WME wme, final Token token)
	{
		final Literal[] literals = new Literal[_args.length];
		for (int i = 0; i < literals.length; i++)
			literals[i] = (Literal) _args[i].getNode(wme, token);
		return _builtin.apply(_abox, literals);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(_args);
		result = prime * result + _builtin.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof BuiltInCondition))
			return false;
		final BuiltInCondition other = (BuiltInCondition) obj;
		return _builtin.equals(other._builtin) && Arrays.equals(_args, other._args);
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(ATermUtils.makeTermAppl(_name)) + Arrays.toString(_args);
	}
}
