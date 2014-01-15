// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.List;
import java.util.Set;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Rule Node
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
 */
public class RuleNode extends Node {

	public BetaNode	betaNode;
	public List<TermTuple>	rhs, lhs;
	public Set<ATermAppl> explain;
	
	public RuleNode(Rule rule, Set<ATermAppl> explain) {
		rhs = rule.getHead();
		lhs = rule.getBody();
		this.explain = explain;
	}

	public String toString() {
		return "(" + lhs + "=>" + rhs + ")";
	}
}
