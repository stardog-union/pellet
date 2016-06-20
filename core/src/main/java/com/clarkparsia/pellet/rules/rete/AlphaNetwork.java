// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import com.clarkparsia.pellet.rules.model.AtomConstant;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;

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
public class AlphaNetwork implements Iterable<AlphaNode>
{
	//	private final Map<ATermAppl, List<AlphaTypeNode>> typeNodes = new HashMap<Object, List<AlphaNode>>();

	private final Map<Object, List<AlphaNode>> _map = new ConcurrentHashMap<>();
	private final List<AlphaNode> _alphaNodes = new Vector<>();
	private final AlphaNodeCreator _creator = new AlphaNodeCreator();
	private final ABox _abox;

	public AlphaNetwork(final ABox abox)
	{
		this._abox = abox;
	}

	@Override
	public Iterator<AlphaNode> iterator()
	{
		return _alphaNodes.iterator();
	}

	public AlphaNode addNode(final RuleAtom atom)
	{
		final Object pred = atom.getPredicate();
		List<AlphaNode> nodes = _map.get(pred);
		if (nodes == null)
		{
			nodes = new ArrayList<>();
			_map.put(pred, nodes);
		}
		else
			for (final AlphaNode node : nodes)
				if (node.matches(atom))
					return node;

		final AlphaNode node = _creator.create(atom);
		if (node != null)
			if (node instanceof AlphaEdgeNode)
			{
				final Role role = ((AlphaEdgeNode) node).getRole();
				addAlphaNodeForSubs(role, node);
				if (role.isObjectRole())
					addAlphaNodeForSubs(role.getInverse(), node);
			}
			else
				nodes.add(node);

		_alphaNodes.add(node);

		return node;
	}

	private void addAlphaNodeForSubs(final Role r, final AlphaNode node)
	{
		for (final Role sub : r.getSubRoles())
			if (!sub.isBottom())
				addAlphaNode(sub, node);
	}

	private void addAlphaNode(final Role r, final AlphaNode node)
	{
		if (!r.isAnon())
		{
			List<AlphaNode> subNodes = _map.get(r.getName());
			if (subNodes == null)
			{
				subNodes = new ArrayList<>();
				_map.put(r.getName(), subNodes);
			}
			subNodes.add(node);
		}
	}

	public void activateAll()
	{
		//		interpreter.addFact( EMPTY_FACT );
		for (final Iterator<Individual> i = _abox.getIndIterator(); i.hasNext();)
		{
			final Individual ind = i.next();
			activateIndividual(ind);
		}

		return;
	}

	public boolean activateIndividual(final Individual ind)
	{
		// only named non-pruned individuals
		final boolean changed = false;
		if (!ind.isRootNominal() || ind.isPruned())
			return false;

		final List<ATermAppl> types = ind.getTypes(Node.ATOM);
		for (int i = 0; i < types.size(); i++)
		{
			final ATermAppl type = types.get(i);
			activateType(ind, type, ind.getDepends(type));
		}

		activateDifferents(ind);

		for (final Edge edge : ind.getOutEdges())
			if (edge.getTo().isRootNominal())
				activateEdge(edge);

		return changed;
	}

	public void activateType(final Individual ind, final ATermAppl type, final DependencySet ds)
	{
		final List<AlphaNode> alphas = _map.get(type);
		if (alphas != null)
			for (final AlphaNode alpha : alphas)
				((AlphaTypeNode) alpha).activate(ind, type, ds);
	}

	public void activateEdge(final Edge edge)
	{
		Role r = edge.getRole();
		if (r.isAnon())
			r = r.getInverse();
		final List<AlphaNode> alphas = _map.get(r.getName());
		if (alphas != null)
			for (final AlphaNode alpha : alphas)
				((AlphaEdgeNode) alpha).activate(edge);
	}

	//	private void activateEdge(Edge edge, boolean inverse) {
	//		Role _role = edge.getRole();
	//		if (inverse) {
	//			_role = _role.getInverse();
	//			if (_role == null) {
	//				return;
	//			}
	//		}
	//		for (Role r : _role.getSuperRoles()) {
	//			if (r.isAnon()) {
	//				continue;
	//			}
	//			List<AlphaNode> alphas = _map.get(r.getName());
	//			if (alphas != null) {
	//				if (inverse) {
	//					edge = new DefaultEdge(_role,(Individual)edge.getTo(),  edge.getFrom(), edge.getDepends());
	//				}
	//				for (AlphaNode alpha : alphas) {
	//					((AlphaEdgeNode) alpha).activate(edge);
	//				}	
	//			}
	//        }
	//    }

	public void activateDifferents(final Individual ind)
	{
		final List<AlphaNode> alphas = _map.get("DIFFERENT");
		if (alphas != null)
			for (final Node n : ind.getDifferents())
			{
				final Individual diff = (Individual) n;
				for (final AlphaNode alpha : alphas)
					((AlphaDiffFromNode) alpha).activate(ind, diff, ind.getDifferenceDependency(diff));
			}
	}

	public void activateDifferent(final Individual ind, final Individual diff, final DependencySet ds)
	{
		final List<AlphaNode> alphas = _map.get("DIFFERENT");
		if (alphas != null)
			for (final AlphaNode alpha : alphas)
				((AlphaDiffFromNode) alpha).activate(ind, diff, ds);
	}

	public void setDoExplanation(final boolean doExplanation)
	{
		for (final AlphaNode alphaNode : _alphaNodes)
			alphaNode.setDoExplanation(doExplanation);
	}

	public void print()
	{
		for (final AlphaNode node : _alphaNodes)
			node.unmark();
		for (final AlphaNode node : _alphaNodes)
			node.print("");
	}

	@Override
	public String toString()
	{
		final StringBuilder tmp = new StringBuilder();
		for (final AlphaNode node : this)
			tmp.append(node.toString()).append("\n");
		return tmp.toString();
	}

	private class AlphaNodeCreator implements RuleAtomVisitor
	{
		private AlphaNode result;

		private AlphaNode create(final RuleAtom atom)
		{
			result = null;
			atom.accept(this);
			if (result == null)
				throw new UnsupportedOperationException("Not supported " + atom);
			return result;
		}

		private void addPropertyAtom(final BinaryAtom<ATermAppl, ? extends AtomObject, ? extends AtomObject> atom)
		{
			final AtomObject s = atom.getArgument1();
			final AtomObject o = atom.getArgument2();

			final Role role = _abox.getRole(atom.getPredicate());
			if (s instanceof AtomVariable && o instanceof AtomVariable)
			{
				if (s.equals(o))
					result = new AlphaReflexiveEdgeNode(_abox, role);
				else
					result = new AlphaEdgeNode(_abox, role);
			}
			else
				if (s instanceof AtomConstant)
				{
					if (o instanceof AtomConstant)
						result = new AlphaNoVarEdgeNode(_abox, role, ((AtomConstant) s).getValue(), ((AtomConstant) o).getValue());
					else
						result = new AlphaFixedSubjectEdgeNode(_abox, role, ((AtomConstant) s).getValue());
				}
				else
					result = new AlphaFixedObjectEdgeNode(_abox, role, ((AtomConstant) o).getValue());
		}

		@Override
		public void visit(final SameIndividualAtom atom)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(final IndividualPropertyAtom atom)
		{
			addPropertyAtom(atom);
		}

		@Override
		public void visit(final DifferentIndividualsAtom atom)
		{
			result = new AlphaDiffFromNode(_abox);
		}

		@Override
		public void visit(final DatavaluedPropertyAtom atom)
		{
			addPropertyAtom(atom);
		}

		@Override
		public void visit(final DataRangeAtom atom)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(final ClassAtom atom)
		{
			final AtomObject arg = atom.getArgument();
			final ATermAppl name = (arg instanceof AtomConstant) ? ((AtomConstant) arg).getValue() : null;
			result = new AlphaTypeNode(_abox, atom.getPredicate(), name);
		}

		@Override
		public void visit(final BuiltInAtom atom)
		{
			// TODO Auto-generated method stub

		}
	}
}
