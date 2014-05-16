// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;

/**
 */
public class AlphaFixedObjectEdgeNode extends AlphaFixedEdgeNode {
	public AlphaFixedObjectEdgeNode(ABox abox, Role role, ATermAppl object) {
	    super(abox, role, object);
    }
	
	public boolean activate(Edge edge) {
		EdgeDirection dir = edgeMatches(edge);
		Node object = initNode();
		if (dir != null && (dir == EdgeDirection.FORWARD ? edge.getTo() : edge.getFrom()).isSame(object)) {	
			if (!edge.getTo().equals(object)) {
				System.out.println("Edge " + edge + " matching " + this);
			}
			activate(WME.createEdge(edge, dir));
			return true;
		}
		return false;
	}

	public Iterator<WME> getMatches(int argIndex, Node arg) {
		if (argIndex != 0) {
			throw new UnsupportedOperationException();	
		}
		
		Node object = initNode();
		return getMatches((Individual) arg, role, object);
	}

	public Iterator<WME> getMatches() {
		Node object = initNode();
		return toWMEs(object.getInEdges(), EdgeDirection.FORWARD);
	}

	public boolean matches(RuleAtom atom) {
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) 
						&& atom.getPredicate().equals(role.getName())
						&& ((BinaryAtom) atom).getArgument1() instanceof AtomVariable
						&& ((BinaryAtom) atom).getArgument2() instanceof AtomIConstant
						&& ((AtomIConstant)((BinaryAtom) atom).getArgument2()).getValue().equals(name);
	}
	
	public String toString() {
		return ATermUtils.toString(role.getName()) + "(0, " + ATermUtils.toString(name) + ")";
	}
}
