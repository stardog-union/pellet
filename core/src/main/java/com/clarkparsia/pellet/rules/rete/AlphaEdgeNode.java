// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DefaultEdge;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;
import org.mindswap.pellet.utils.iterator.NestedIterator;

import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;

/**
 */
public class AlphaEdgeNode extends AlphaNode {
	protected final Role role;

	public AlphaEdgeNode(ABox abox, Role role) {
		super(abox);
	    this.role = role;
    }
	
	public Role getRole() {
		return role;
	}
	
	protected EdgeDirection edgeMatches(Edge edge) {
		Role edgeRole = edge.getRole();
		boolean isFwd = edgeRole.isSubRoleOf(role);
		boolean isBwd = (role.getInverse() != null && edgeRole.isSubRoleOf(role.getInverse()));
		return isFwd ? isBwd ? EdgeDirection.BOTH : EdgeDirection.FORWARD : isBwd ? EdgeDirection.BACKWARD : null;
	}
	
	protected WME createEdge(Edge edge, EdgeDirection dir) {
		if (doExplanation) {
			DependencySet ds = (dir == EdgeDirection.FORWARD) ? role.getExplainSub(edge.getRole().getName()) : role.getInverse().getExplainSub(edge.getRole().getName());
			if (!ds.getExplain().isEmpty()) {
				return WME.createEdge(new DefaultEdge(edge.getRole(), edge.getFrom(), edge.getTo(), edge.getDepends().union(ds, doExplanation)), dir);
			}
		}
		
		return WME.createEdge(edge, dir);
		
	}
	
	public boolean activate(Edge edge) {	
		EdgeDirection dir = edgeMatches(edge);
		if (dir != null) {
			if (dir == EdgeDirection.BOTH) {
				activate(createEdge(edge, EdgeDirection.FORWARD));	
//				activate(createEdge(edge, EdgeDirection.BACKWARD));	
			}
			else {
				activate(createEdge(edge, dir));
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
    public boolean matches(RuleAtom atom) {
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) 
						&& atom.getPredicate().equals(role.getName())
						&& ((BinaryAtom) atom).getArgument1() instanceof AtomVariable
						&& ((BinaryAtom) atom).getArgument2() instanceof AtomVariable;
	}

	public Iterator<WME> getMatches(int argIndex, Node arg) {
		return (argIndex == 0) ? getMatches((Individual) arg, role, null) : getMatches(null, role, arg);
	}

	protected Iterator<WME> getMatches(Individual s, Role r, Node o) {
		Iterator<WME> i1 = IteratorUtils.emptyIterator();
		Iterator<WME> i2 = IteratorUtils.emptyIterator();
		
		Role invRole = role.getInverse();
		if (s != null) {
			i1 = toWMEs(getEdges(s.getOutEdges(), role, o), EdgeDirection.FORWARD);
			if (invRole != null) {
				i2 = toWMEs(getEdges(s.getInEdges(), invRole, o), EdgeDirection.BACKWARD);
			}
		}
		else {
			assert s == null;
			i1 = toWMEs(getEdges(o.getInEdges(), role, null), EdgeDirection.FORWARD);
			if (invRole != null) {
				i2 = toWMEs(getEdges(((Individual) o).getOutEdges(), invRole, null), EdgeDirection.BACKWARD);
			}			
		}	
		
		return !i1.hasNext() ? i2 : !i2.hasNext() ? i1 : IteratorUtils.concat(i1,  i2);
	}
	
	private EdgeList getEdges(EdgeList edges, Role r, Node o) {
		return (o == null) ? edges.getEdges(r) : edges.getEdgesTo(r, o); 
	}

	public Iterator<WME> getMatches() {
		return new NestedIterator<Individual, WME>(abox.getIndIterator()) {
			@Override
            public Iterator<WME> getInnerIterator(Individual ind) {
	            return toWMEs(ind.getOutEdges().getEdges(role), EdgeDirection.FORWARD);
            }
		};
	}
	
	protected Iterator<WME> toWMEs(EdgeList edges, EdgeDirection dir) {
		if (edges.isEmpty()) {
			return IteratorUtils.emptyIterator();
		}
		else if (edges.size() == 1) {
			Edge edge = edges.edgeAt(0);
			return IteratorUtils.<WME>singletonIterator(createEdge(edge, dir));
		}
		else {
			List<WME> wmes = new ArrayList<WME>(edges.size());
			for (Edge edge : edges) {
	            wmes.add(createEdge(edge, dir));
            }
			return wmes.iterator();
		}
	}
	
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + role.getName().hashCode();
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
	    AlphaEdgeNode other = (AlphaEdgeNode) obj;
	    if (getClass() != other.getClass()) {
	    	return false;
	    }
	    return role.equals(other.role);
    }
	
	public String toString() {
		return ATermUtils.toString(role.getName()) + "(0, 1)";
	}
}
