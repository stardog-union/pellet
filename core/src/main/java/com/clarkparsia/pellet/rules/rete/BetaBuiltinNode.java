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
	private final ABox abox;
	private final String name;
	private final BuiltIn builtin;
	private final NodeProvider[] args;

	public BetaBuiltinNode(final ABox abox, final String name, final BuiltIn builtin, final NodeProvider[] args)
	{
		this.abox = abox;
		this.name = name;
		this.builtin = builtin;
		this.args = args;
	}

	@Override
	public void activate(final WME wme)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void activate(final Token token)
	{
		final Literal[] literals = new Literal[args.length];
		for (int i = 0; i < literals.length; i++)
			literals[i] = args[i] == null ? null : (Literal) args[i].getNode(null, token);
		if (builtin.apply(abox, literals))
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
		return "Builtin " + ATermUtils.toString(ATermUtils.makeTermAppl(name)) + Arrays.toString(args);
	}
}
