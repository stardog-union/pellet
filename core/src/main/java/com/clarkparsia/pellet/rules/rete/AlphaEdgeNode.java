// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;
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

/**
 */
public class AlphaEdgeNode extends AlphaNode
{
	protected final Role _role;

	public AlphaEdgeNode(final ABox abox, final Role role)
	{
		super(abox);
		this._role = role;
	}

	public Role getRole()
	{
		return _role;
	}

	protected EdgeDirection edgeMatches(final Edge edge)
	{
		final Role edgeRole = edge.getRole();
		final boolean isFwd = edgeRole.isSubRoleOf(_role);
		final boolean isBwd = (_role.getInverse() != null && edgeRole.isSubRoleOf(_role.getInverse()));
		return isFwd ? isBwd ? EdgeDirection.BOTH : EdgeDirection.FORWARD : isBwd ? EdgeDirection.BACKWARD : null;
	}

	protected WME createEdge(final Edge edge, final EdgeDirection dir)
	{
		if (_doExplanation)
		{
			final DependencySet ds = (dir == EdgeDirection.FORWARD) ? _role.getExplainSub(edge.getRole().getName()) : _role.getInverse().getExplainSub(edge.getRole().getName());
			if (!ds.getExplain().isEmpty())
				return WME.createEdge(new DefaultEdge(edge.getRole(), edge.getFrom(), edge.getTo(), edge.getDepends().union(ds, _doExplanation)), dir);
		}

		return WME.createEdge(edge, dir);

	}

	public boolean activate(final Edge edge)
	{
		final EdgeDirection dir = edgeMatches(edge);
		if (dir != null)
		{
			if (dir == EdgeDirection.BOTH)
				activate(createEdge(edge, EdgeDirection.FORWARD));
			//				activate(createEdge(edge, EdgeDirection.BACKWARD));
			else
				activate(createEdge(edge, dir));
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean matches(final RuleAtom atom)
	{
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) && atom.getPredicate().equals(_role.getName()) && ((BinaryAtom) atom).getArgument1() instanceof AtomVariable && ((BinaryAtom) atom).getArgument2() instanceof AtomVariable;
	}

	@Override
	public Iterator<WME> getMatches(final int argIndex, final Node arg)
	{
		return (argIndex == 0) ? getMatches((Individual) arg, _role, null) : getMatches(null, _role, arg);
	}

	protected Iterator<WME> getMatches(final Individual s, @SuppressWarnings("unused") final Role r, final Node o)
	{
		Iterator<WME> i1 = IteratorUtils.emptyIterator();
		Iterator<WME> i2 = IteratorUtils.emptyIterator();

		final Role invRole = _role.getInverse();
		if (s != null)
		{
			i1 = toWMEs(getEdges(s.getOutEdges(), _role, o), EdgeDirection.FORWARD);
			if (invRole != null)
				i2 = toWMEs(getEdges(s.getInEdges(), invRole, o), EdgeDirection.BACKWARD);
		}
		else
		{
			assert s == null;
			i1 = toWMEs(getEdges(o.getInEdges(), _role, null), EdgeDirection.FORWARD);
			if (invRole != null)
				i2 = toWMEs(getEdges(((Individual) o).getOutEdges(), invRole, null), EdgeDirection.BACKWARD);
		}

		return !i1.hasNext() ? i2 : !i2.hasNext() ? i1 : IteratorUtils.concat(i1, i2);
	}

	private EdgeList getEdges(final EdgeList edges, final Role r, final Node o)
	{
		return (o == null) ? edges.getEdges(r) : edges.getEdgesTo(r, o);
	}

	@Override
	public Iterator<WME> getMatches()
	{
		return new NestedIterator<Individual, WME>(_abox.getIndIterator())
				{
			@Override
			public Iterator<WME> getInnerIterator(final Individual ind)
			{
				return toWMEs(ind.getOutEdges().getEdges(_role), EdgeDirection.FORWARD);
			}
				};
	}

	protected Iterator<WME> toWMEs(final EdgeList edges, final EdgeDirection dir)
	{
		if (edges.isEmpty())
			return IteratorUtils.emptyIterator();
		else
			if (edges.size() == 1)
			{
				final Edge edge = edges.edgeAt(0);
				return IteratorUtils.<WME> singletonIterator(createEdge(edge, dir));
			}
			else
			{
				final List<WME> wmes = new ArrayList<>(edges.size());
				for (final Edge edge : edges)
					wmes.add(createEdge(edge, dir));
				return wmes.iterator();
			}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + _role.getName().hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		final AlphaEdgeNode other = (AlphaEdgeNode) obj;
		if (getClass() != other.getClass())
			return false;
		return _role.equals(other._role);
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(_role.getName()) + "(0, 1)";
	}
}
