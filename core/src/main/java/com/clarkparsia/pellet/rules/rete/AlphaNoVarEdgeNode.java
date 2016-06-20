// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import com.clarkparsia.pellet.rules.model.AtomConstant;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.WME.EdgeDirection;
import java.util.Iterator;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

/**
 */
public class AlphaNoVarEdgeNode extends AlphaFixedEdgeNode
{
	private final ATermAppl _objectName;
	private Node _objectNode;

	public AlphaNoVarEdgeNode(final ABox abox, final Role role, final ATermAppl subjectName, final ATermAppl objectName)
	{
		super(abox, role, subjectName);

		this._objectName = objectName;
	}

	protected Node initObjectNode()
	{
		if (_objectNode == null)
			_objectNode = initNode(_objectName);
		assert _objectNode != null;
		return _objectNode;
	}

	@Override
	public boolean activate(final Edge edge)
	{
		final Individual subject = initNode();
		final Node object = initObjectNode();
		final EdgeDirection dir = edgeMatches(edge);
		if (dir != null && (dir == EdgeDirection.FORWARD ? edge.getFrom() : edge.getTo()).isSame(subject) && (dir == EdgeDirection.BACKWARD ? edge.getFrom() : edge.getTo()).isSame(object))
		{
			activate(WME.createEdge(edge, dir));
			return true;
		}
		return false;
	}

	@Override
	public Iterator<WME> getMatches(final int argIndex, final Node arg)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<WME> getMatches()
	{
		final Individual subject = initNode();
		final Node object = initObjectNode();
		final EdgeList edges = subject.getEdgesTo(object, _role);
		return toWMEs(edges, EdgeDirection.FORWARD);
	}

	@Override
	public boolean matches(final RuleAtom atom)
	{
		return ((atom instanceof IndividualPropertyAtom) || (atom instanceof DatavaluedPropertyAtom)) && atom.getPredicate().equals(_role.getName()) && ((BinaryAtom<?, ?, ?>) atom).getArgument1() instanceof AtomIConstant && ((AtomIConstant) ((BinaryAtom<?, ?, ?>) atom).getArgument1()).getValue().equals(name) && ((BinaryAtom<?, ?, ?>) atom).getArgument2() instanceof AtomConstant && ((AtomConstant) ((BinaryAtom<?, ?, ?>) atom).getArgument2()).getValue().equals(_objectName);
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(_role.getName()) + "(" + ATermUtils.toString(name) + ", " + ATermUtils.toString(_objectName) + ")";
	}
}
