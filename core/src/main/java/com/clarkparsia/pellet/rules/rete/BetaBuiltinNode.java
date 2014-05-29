// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;

import com.clarkparsia.pellet.rules.builtins.BuiltIn;

/**
 */
public class BetaBuiltinNode extends BetaNode {
	private final ABox abox;
	private final String name;
	private final BuiltIn builtin;
	private final NodeProvider[] args;
	
	public BetaBuiltinNode(ABox abox, String name, BuiltIn builtin, NodeProvider[] args) {
	    this.abox = abox;
	    this.name = name;
	    this.builtin = builtin;
	    this.args = args;
    }
	
	@Override
	public void activate(WME wme) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void activate(Token token) {
		Literal[] literals = new Literal[args.length];
		for (int i = 0; i < literals.length; i++) {
	        literals[i] = args[i] == null ? null : (Literal) args[i].getNode(null, token);
        }
		if (builtin.apply(abox, literals)) {
			activateChildren(WME.createBuiltin(literals, DependencySet.INDEPENDENT), token);
		}
	}
	
	@Override
	public void print(String indent) {
		indent += "  ";
		System.out.print(indent);
		System.out.println(this);
		for (BetaNode node : getBetas()) {
	        node.print(indent);
        }
	}
	
	public String toString() {
		return "Builtin " + ATermUtils.toString(ATermUtils.makeTermAppl(name)) + Arrays.toString(args);
	}
}
