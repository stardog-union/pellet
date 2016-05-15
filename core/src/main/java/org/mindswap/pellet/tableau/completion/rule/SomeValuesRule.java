// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class SomeValuesRule extends AbstractTableauRule
{
	public SomeValuesRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.EXISTENTIAL, BlockingType.COMPLETE);
	}

	@Override
	public void apply(final Individual x)
	{
		if (!x.canApply(Node.SOME))
			return;

		final List<ATermAppl> types = x.getTypes(Node.SOME);
		final int size = types.size();
		for (int j = x._applyNext[Node.SOME]; j < size; j++)
		{
			final ATermAppl sv = types.get(j);

			applySomeValuesRule(x, sv);

			if (_strategy.getABox().isClosed() || x.isPruned())
				return;
		}
		x._applyNext[Node.SOME] = size;
	}

	protected void applySomeValuesRule(final Individual x, final ATermAppl sv)
	{
		// someValuesFrom is now in the form not(all(p. not(c)))
		final ATermAppl a = (ATermAppl) sv.getArgument(0);
		final ATermAppl s = (ATermAppl) a.getArgument(0);
		ATermAppl c = (ATermAppl) a.getArgument(1);

		DependencySet ds = x.getDepends(sv);

		if (!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
			return;

		c = ATermUtils.negate(c);

		// Special rule to optimize topObjectProperty
		if (s.equals(ATermUtils.TOP_OBJECT_PROPERTY))
		{
			if (ATermUtils.isNominal(c))
				return;

			for (final Node node : _strategy.getABox().getNodes())
				if (node.isIndividual() && !node.isPruned() && node.hasType(c))
					return;

			final Individual y = _strategy.createFreshIndividual(x, ds);
			_strategy.addType(y, c, ds);
			return;
		}

		final Role role = _strategy.getABox().getRole(s);

		// Is there a r-_neighbor that satisfies the someValuesFrom restriction
		boolean neighborFound = false;
		// Safety _condition as defined in the SHOIQ algorithm.
		// An R-_neighbor y of a _node x is safe if
		// (i) x is blockable or if
		// (ii) x is a nominal _node and y is not _blocked.
		final boolean neighborSafe = x.isBlockable();
		// y is going to be the _node we create, and edge its connection to the
		// _current _node
		Node y = null;
		Edge edge = null;

		// edges contains all the edges going into of coming out from the _node
		// And labeled with the role R
		EdgeList edges = x.getRNeighborEdges(role);
		// We examine all those edges one by one and check if the _neighbor has
		// type C, in which case we set neighborFound to true
		for (final Iterator<Edge> i = edges.iterator(); i.hasNext();)
		{
			edge = i.next();

			y = edge.getNeighbor(x);

			if (PelletOptions.USE_COMPLETION_QUEUE && y.isPruned())
			{
				y = null;
				continue;
			}

			if (y != null && y.hasType(c))
			{
				neighborFound = neighborSafe || y.isLiteral() || !_strategy.getBlocking().isBlocked((Individual) y);
				if (neighborFound)
					break;
			}
		}

		// If we have found a R-_neighbor with type C, continue, do nothing
		if (neighborFound)
			return;

		// If not, we have to create it
		// If the role is a datatype property...
		if (role.isDatatypeRole())
		{
			Literal literal = (Literal) y;
			if (ATermUtils.isNominal(c) && !PelletOptions.USE_PSEUDO_NOMINALS)
			{
				_strategy.getABox().copyOnWrite();

				final ATermAppl input = (ATermAppl) c.getArgument(0);
				ATermAppl canonical;
				if (input.getArgument(ATermUtils.LIT_URI_INDEX).equals(ATermUtils.NO_DATATYPE))
					canonical = input;
				else
					try
				{
						canonical = _strategy.getABox().getDatatypeReasoner().getCanonicalRepresentation(input);
				}
				catch (final InvalidLiteralException e)
				{
					final String msg = "Invalid literal encountered in nominal when attempting to apply some values rule: " + e.getMessage();
					throw new InternalReasonerException(msg, e);
				}
				catch (final UnrecognizedDatatypeException e)
				{
					final String msg = "Unrecognized datatype for literal encountered in nominal when attempting to apply some values rule: " + e.getMessage();
					throw new InternalReasonerException(msg, e);
				}
				literal = _strategy.getABox().addLiteral(canonical);
			}
			else
			{
				if (!role.isFunctional() || literal == null)
					literal = _strategy.getABox().addLiteral(ds);
				else
				{
					ds = ds.union(role.getExplainFunctional(), _strategy.getABox().doExplanation());
					if (edge != null)
						ds = ds.union(edge.getDepends(), _strategy.getABox().doExplanation());
				}
				_strategy.addType(literal, c, ds);
			}

			if (log.isLoggable(Level.FINE))
				log.fine("SOME: " + x + " -> " + s + " -> " + literal + " : " + ATermUtils.toString(c) + " - " + ds);

			_strategy.addEdge(x, role, literal, ds);
		}
		// If it is an object property
		else
			if (ATermUtils.isNominal(c) && !PelletOptions.USE_PSEUDO_NOMINALS)
			{
				_strategy.getABox().copyOnWrite();

				final ATermAppl value = (ATermAppl) c.getArgument(0);
				y = _strategy.getABox().getIndividual(value);

				if (log.isLoggable(Level.FINE))
					log.fine("VAL : " + x + " -> " + ATermUtils.toString(s) + " -> " + y + " - " + ds);

				if (y == null)
					if (ATermUtils.isAnonNominal(value))
						y = _strategy.getABox().addIndividual(value, ds);
					else
						if (ATermUtils.isLiteral(value))
							throw new InternalReasonerException("Object Property " + role + " is used with a hasValue restriction " + "where the value is a literal: " + ATermUtils.toString(value));
						else
							throw new InternalReasonerException("Nominal " + c + " is not found in the KB!");

				if (y.isMerged())
				{
					ds = ds.union(y.getMergeDependency(true), _strategy.getABox().doExplanation());

					y = y.getSame();
				}

				_strategy.addEdge(x, role, y, ds);
			}
			else
			{
				boolean useExistingNode = false;
				boolean useExistingRole = false;
				final DependencySet maxCardDS = role.isFunctional() ? role.getExplainFunctional() : x.hasMax1(role);
				if (maxCardDS != null)
				{
					ds = ds.union(maxCardDS, _strategy.getABox().doExplanation());

					// if there is an r-_neighbor and we can have at most one r then
					// we should reuse that _node and edge. there is no way that _neighbor
					// is not safe (a _node is unsafe only if it is blockable and has
					// a nominal successor which is not possible if there is a cardinality
					// restriction on the property)
					if (edge != null)
						useExistingRole = useExistingNode = true;
					else
					{
						// this is the tricky part. we need some merges to happen
						// under following conditions:
						// 1) if r is functional and there is a p-_neighbor where
						// p is superproperty of r then we need to reuse that
						// p _neighbor for the some values restriction (no
						// need to check subproperties because functionality of r
						// precents having two or more successors for subproperties)
						// 2) if r is not functional, i.e. max(r, 1) is in the types,
						// then having a p _neighbor (where p is subproperty of r)
						// means we need to reuse that p-_neighbor
						// In either case if there are more than one such value we also
						// need to merge them together
						final Set<Role> fs = role.isFunctional() ? role.getFunctionalSupers() : role.getSubRoles();

						for (Role f : fs)
						{
							edges = x.getRNeighborEdges(f);
							if (!edges.isEmpty())
								if (useExistingNode)
								{
									DependencySet fds = DependencySet.INDEPENDENT;
									if (PelletOptions.USE_TRACING)
										if (role.isFunctional())
											fds = role.getExplainSuper(f.getName());
										else
											fds = role.getExplainSub(f.getName());
									final Edge otherEdge = edges.edgeAt(0);
									final Node otherNode = otherEdge.getNeighbor(x);
									if (edge != null)
									{
										final DependencySet d = ds.union(edge.getDepends(), _strategy.getABox().doExplanation()).union(otherEdge.getDepends(), _strategy.getABox().doExplanation()).union(fds, _strategy.getABox().doExplanation());
										_strategy.mergeTo(y, otherNode, d);
									}
								}
								else
								{
									useExistingNode = true;
									edge = edges.edgeAt(0);
									y = edge.getNeighbor(x);
								}
						}
						if (y != null)
							y = y.getSame();
					}
				}

				if (useExistingNode)
				{
					if (edge != null)
						ds = ds.union(edge.getDepends(), _strategy.getABox().doExplanation());
				}
				else
					y = _strategy.createFreshIndividual(x, ds);

				if (log.isLoggable(Level.FINE))
					log.fine("SOME: " + x + " -> " + role + " -> " + y + " : " + ATermUtils.toString(c) + (useExistingNode ? "" : " (*)") + " - " + ds);

				_strategy.addType(y, c, ds);

				if (!useExistingRole)
					if (x.isBlockable() && (y != null && y.isConceptRoot()))
						_strategy.addEdge((Individual) y, role.getInverse(), x, ds);
					else
						_strategy.addEdge(x, role, y, ds);
			}
	}
}
