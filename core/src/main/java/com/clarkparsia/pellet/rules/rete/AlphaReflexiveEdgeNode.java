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
import org.mindswap.pellet.utils.iterator.NestedIterator;

import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;

/**
 */
public class AlphaReflexiveEdgeNode extends AlphaEdgeNode {
	public AlphaReflexiveEdgeNode(ABox abox, Role role) {
	    super(abox, role);
    }
	
	public boolean activate(Edge edge) {
		assert edgeMatches(edge) != null;
		if (edge.getFromName().equals(edge.getToName())) {
			activate(WME.createEdge(edge));
			return true;
		}
		return false;
	}

	public Iterator<WME> getMatches(int argIndex, Node arg) {
		EdgeList edges = ((Individual) arg).getRNeighborEdges(role, arg);

		return toWMEs(edges, EdgeDirection.FORWARD);
	}

	public Iterator<WME> getMatches() {
		return new NestedIterator<Individual, WME>(abox.getIndIterator()) {
			@Override
            public Iterator<WME> getInnerIterator(Individual ind) {
	            return toWMEs(ind.getEdgesTo(ind), EdgeDirection.FORWARD);
            }
		};
	}

	public boolean matches(RuleAtom atom) {
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) 
						&& atom.getPredicate().equals(role.getName())
						&& ((BinaryAtom) atom).getArgument1() instanceof AtomVariable
						&& ((BinaryAtom) atom).getArgument2().equals(((BinaryAtom) atom).getArgument1());
	}
	
	public String toString() {
		return ATermUtils.toString(role.getName()) + "(0, 0)";
	}
}
