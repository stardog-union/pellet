// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import com.clarkparsia.pellet.rules.builtins.BuiltIn;
import java.util.Arrays;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;

/**
 */
public class BetaBuiltinNode extends BetaNode
{
	private final ABox _abox;
	private final String _name;
	private final BuiltIn _builtin;
	private final NodeProvider[] _args;

	public BetaBuiltinNode(final ABox abox, final String name, final BuiltIn builtin, final NodeProvider[] args)
	{
		this._abox = abox;
		this._name = name;
		this._builtin = builtin;
		this._args = args;
	}

	@Override
	public void activate(final WME wme)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void activate(final Token token)
	{
		final Literal[] literals = new Literal[_args.length];
		for (int i = 0; i < literals.length; i++)
			literals[i] = _args[i] == null ? null : (Literal) _args[i].getNode(null, token);
		if (_builtin.apply(_abox, literals))
			activateChildren(WME.createBuiltin(literals, DependencySet.INDEPENDENT), token);
	}

	@Override
	public void print(String indent)
	{
		indent += "  ";
		System.out.print(indent);
		System.out.println(this);
		for (final BetaNode node : getBetas())
			node.print(indent);
	}

	@Override
	public String toString()
	{
		return "Builtin " + ATermUtils.toString(ATermUtils.makeTermAppl(_name)) + Arrays.toString(_args);
	}
}
