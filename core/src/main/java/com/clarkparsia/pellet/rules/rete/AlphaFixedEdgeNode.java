// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 */
public class AlphaFixedEdgeNode extends AlphaEdgeNode {
	protected final ATermAppl name;
	private Node node;

	public AlphaFixedEdgeNode(ABox abox, Role role, ATermAppl subjectName) {
	    super(abox, role);
	    this.name = subjectName;
    }
	
	protected <N extends Node> N initNode() {
		if (node == null) {			
			node = initNode(name);
		}
		assert node != null;
		return (N) node;
	}
	
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + super.hashCode();
	    result = prime * result + name.hashCode();
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj) {
		    return true;
	    }
	    if (obj == null) {
		    return false;
	    }
	    AlphaFixedEdgeNode other = (AlphaFixedEdgeNode) obj;
	    if (getClass() != other.getClass()) {
	    	return false;
	    }
	    return role.equals(other.role);
    }
}
