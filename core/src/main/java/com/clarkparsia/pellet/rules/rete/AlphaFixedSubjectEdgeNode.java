// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
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
public class AlphaFixedSubjectEdgeNode extends AlphaFixedEdgeNode {
	public AlphaFixedSubjectEdgeNode(ABox abox, Role role, ATermAppl subjectName) {
	    super(abox, role, subjectName);
    }
	
	public boolean activate(Edge edge) {
		Individual subject = initNode();
		EdgeDirection dir = edgeMatches(edge);
		if (dir != null && (dir == EdgeDirection.FORWARD ? edge.getFrom() : edge.getTo()).isSame(subject)) {
			activate(WME.createEdge(edge, dir));
			return true;
		}
		return false;
	}

	public Iterator<WME> getMatches(int argIndex, Node arg) {
		if (argIndex != 1) {
			throw new UnsupportedOperationException();	
		}
		
		Individual subject = initNode();
		return getMatches(subject, role, arg);
	}

	public Iterator<WME> getMatches() {
		Individual subject = initNode();
		return toWMEs(subject.getOutEdges().getEdges(role), EdgeDirection.FORWARD);
	}

	@SuppressWarnings("rawtypes")
    public boolean matches(RuleAtom atom) {
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) 
						&& atom.getPredicate().equals(role.getName())
						&& ((BinaryAtom) atom).getArgument1() instanceof AtomIConstant
						&& ((AtomIConstant)((BinaryAtom) atom).getArgument1()).getValue().equals(name)
						&& ((BinaryAtom) atom).getArgument2() instanceof AtomVariable;
	}
	
	public String toString() {
		return ATermUtils.toString(role.getName()) + "(" + ATermUtils.toString(name) + ", 1)";
	}
}
