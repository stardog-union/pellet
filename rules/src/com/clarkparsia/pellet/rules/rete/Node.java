// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Node
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
public class Node {

	private List<BetaNode>		betaNodes	= new ArrayList<BetaNode>();
	public Index<ATermAppl, Fact>	index;
	public List<ATermAppl>			svars	= new ArrayList<ATermAppl>();
	public List<ATermAppl>			vars;

	public Node() {
		index = new Index<ATermAppl, Fact>();
	}

	/**
	 * Add a directly dependent beta node.
	 */
	public void add( BetaNode beta ) {
		betaNodes.add( beta );
	}

	/**
	 * Return any directly dependent beta nodes.
	 */
	public Collection<BetaNode> getBetas() {
		return betaNodes;
	}
	
	/**
	 * Return the key for indexing.
	 */
	protected List<ATermAppl> getKey() {
		List<ATermAppl> key;
		key = Utils.concat( svars, vars );
		key = Utils.removeDups( key );
		
		return key;
	}

	protected int getKeyPosition(ATermAppl var) {
		return getKey().indexOf( var );
	}
	
	public void reset() {
		index.clear();
	}

}
