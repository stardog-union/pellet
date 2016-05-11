// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import aterm.ATermAppl;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;
import java.util.Iterator;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

/**
 */
public class AlphaFixedObjectEdgeNode extends AlphaFixedEdgeNode
{
	public AlphaFixedObjectEdgeNode(final ABox abox, final Role role, final ATermAppl object)
	{
		super(abox, role, object);
	}

	@Override
	public boolean activate(final Edge edge)
	{
		final EdgeDirection dir = edgeMatches(edge);
		final Node object = initNode();
		if (dir != null && (dir == EdgeDirection.FORWARD ? edge.getTo() : edge.getFrom()).isSame(object))
		{
			activate(WME.createEdge(edge, dir));
			return true;
		}
		return false;
	}

	@Override
	public Iterator<WME> getMatches(final int argIndex, final Node arg)
	{
		if (argIndex != 0)
			throw new UnsupportedOperationException();

		final Node object = initNode();
		return getMatches((Individual) arg, _role, object);
	}

	@Override
	public Iterator<WME> getMatches()
	{
		final Node object = initNode();
		return toWMEs(object.getInEdges().getEdges(_role), EdgeDirection.FORWARD);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean matches(final RuleAtom atom)
	{
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) && atom.getPredicate().equals(_role.getName()) && ((BinaryAtom) atom).getArgument1() instanceof AtomVariable && ((BinaryAtom) atom).getArgument2() instanceof AtomIConstant && ((AtomIConstant) ((BinaryAtom) atom).getArgument2()).getValue().equals(name);
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(_role.getName()) + "(0, " + ATermUtils.toString(name) + ")";
	}
}
