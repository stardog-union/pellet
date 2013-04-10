// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Alpha Store
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
public class AlphaStore {

	List<AlphaNode>					nodes;
	Map<ATermAppl, List<AlphaNode>>	sharedIndex;

	public AlphaStore() {
		nodes = new ArrayList<AlphaNode>();
		sharedIndex = new HashMap<ATermAppl, List<AlphaNode>>();
	}

	public void addNode(AlphaNode node) {
		if( !nodes.contains( node ) ) {
			nodes.add( node );
			for( ATermAppl var : node.vars )
				if( !sharedIndex.containsKey( var ) ) {
					List<AlphaNode> l = new ArrayList<AlphaNode>();
					l.add( node );
					sharedIndex.put( var, l );
				}
				else {
					List<AlphaNode> l = sharedIndex.get( var );
					l.add( node );
					sharedIndex.put( var, l );
				}

		}
	}

	public void sort() {

		List<AlphaNode> sortedNonBuiltins = new ArrayList<AlphaNode>();

		//iterate over nodes in store
		for( AlphaNode node : nodes )
			//get each node & iterate over its variables
			for( ATermAppl var : node.vars ) {
				//get a list of nodes that share the current node's current variable iteration
				List<AlphaNode> nodesThatShare = sharedIndex.get( var );

				//if there are any nodes that share it
				if( nodesThatShare.size() > 0 ) {
					//add the nodes that share the current variable to this arraylist  			   
					sortedNonBuiltins.addAll( nodesThatShare );
					//add the current node to the arraylist
					sortedNonBuiltins.add( node );
				}
			}
		//result is an array list containing all nodes, but in order of sharing variables

		//remove duplicates
		sortedNonBuiltins = Utils.removeDups( sortedNonBuiltins );

		this.nodes.addAll( 0, sortedNonBuiltins );
		this.nodes = Utils.removeDups( this.nodes );
	}

	public String toString() {
		StringBuffer tmp = new StringBuffer();
		for( Node node : nodes ) {
			tmp.append( node.toString() ).append( "\n" );
		}
		return tmp.toString();
	}
}
