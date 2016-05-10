//The MIT License
//
//Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to
//deal in the Software without restriction, including without limitation the
//rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
//sell copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.

package org.mindswap.pellet;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;

import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;

/*
 * Created on Aug 27, 2003
 *
 */

/**
 * @author Evren Sirin
 */
public class Individual extends Node implements CachedNode
{
	private EdgeList outEdges;

	@SuppressWarnings("unchecked")
	private final ArrayList<ATermAppl>[] _types = new ArrayList[TYPES]; // Known warning message
	public int[] _applyNext = new int[TYPES];

	private int _nominalLevel;

	private Individual _parent;

	private boolean _modifiedAfterMerge = false;

	private short _depth;

	private boolean _isBlocked;

	Individual(final ATermAppl name, final ABox abox, final Individual parent)
	{
		super(name, abox);

		this._parent = parent;

		if (parent == null)
		{
			_nominalLevel = NOMINAL;
			_depth = 0;
		}
		else
		{
			_nominalLevel = BLOCKABLE;
			_depth = (short) (parent._depth + 1);
		}

		for (int i = 0; i < TYPES; i++)
		{
			_types[i] = new ArrayList<>();
			_applyNext[i] = 0;
		}

		outEdges = new EdgeList();
	}

	Individual(final Individual ind, final ABox abox)
	{
		super(ind, abox);

		_nominalLevel = ind._nominalLevel;
		_parent = ind._parent;

		for (int i = 0; i < TYPES; i++)
		{
			_types[i] = new ArrayList<>(ind._types[i]);
			_applyNext[i] = ind._applyNext[i];
		}

		if (isPruned())
			outEdges = new EdgeList(ind.outEdges);
		else
			outEdges = new EdgeList(ind.outEdges.size());
	}

	public boolean isBlocked()
	{
		return _isBlocked;
	}

	public void setBlocked(final boolean isBlocked)
	{
		this._isBlocked = isBlocked;
	}

	public short getDepth()
	{
		return _depth;
	}

	@Override
	public DependencySet getNodeDepends()
	{
		return getDepends(ATermUtils.TOP);
	}

	@Override
	public boolean isLiteral()
	{
		return false;
	}

	@Override
	public boolean isIndividual()
	{
		return true;
	}

	@Override
	public boolean isNominal()
	{
		return _nominalLevel != BLOCKABLE;
	}

	@Override
	public boolean isBlockable()
	{
		return _nominalLevel == BLOCKABLE;
	}

	@Override
	public boolean isIndependent()
	{
		return true;
	}

	public void setNominalLevel(final int level)
	{
		_nominalLevel = level;

		if (_nominalLevel != BLOCKABLE)
			_parent = null;
	}

	@Override
	public int getNominalLevel()
	{
		return _nominalLevel;
	}

	@Override
	public ATermAppl getTerm()
	{
		return _name;
	}

	@Override
	public Node copyTo(final ABox abox)
	{
		return new Individual(this, abox);
	}

	public List<ATermAppl> getTypes(final int type)
	{
		return _types[type];
	}

	@Override
	public boolean isDifferent(final Node node)
	{
		if (PelletOptions.USE_UNIQUE_NAME_ASSUMPTION)
			if (isNamedIndividual() && node.isNamedIndividual())
				return !_name.equals(node._name);

		return _differents.containsKey(node);
	}

	@Override
	public Set<Node> getDifferents()
	{
		return _differents.keySet();
	}

	@Override
	public DependencySet getDifferenceDependency(final Node node)
	{
		if (PelletOptions.USE_UNIQUE_NAME_ASSUMPTION)
			if (isNamedIndividual() && node.isNamedIndividual())
				return DependencySet.INDEPENDENT;

		return _differents.get(node);
	}

	/**
	 * Collects atomic concepts such that either that concept or its negation exist in the _types list without depending on any non-deterministic _branch. First
	 * list is filled with _types and second list is filled with non-_types, i.e. this _individual can never be an instance of any element in the second list.
	 *
	 * @param _types All atomic concepts found in _types
	 * @param nonTypes All atomic concepts
	 */
	public void getObviousTypes(final List<ATermAppl> types, final List<ATermAppl> nonTypes)
	{
		for (final ATermAppl c : getTypes(Node.ATOM))
			if (getDepends(c).isIndependent())
				if (ATermUtils.isPrimitive(c))
					types.add(c);
				else
					if (ATermUtils.isNegatedPrimitive(c))
						nonTypes.add((ATermAppl) c.getArgument(0));
	}

	public boolean canApply(final int type)
	{
		return _applyNext[type] < _types[type].size();
	}

	@Override
	public void addType(final ATermAppl c, final DependencySet ds)
	{
		addType(c, ds, true);
	}

	void addType(final ATermAppl c, DependencySet ds, final boolean checkForPruned)
	{
		if (checkForPruned)
		{
			if (isPruned())
				throw new InternalReasonerException("Adding type to a pruned _node " + this + " " + c);
			else
				if (isMerged())
					return;
		}
		else
			if (isMerged())
				_modifiedAfterMerge = true;

		if (_depends.containsKey(c))
		{
			if (!checkForPruned && ds.isIndependent())
				_depends.put(c, ds);

			return;
		}

		//        if( ABox.log.isLoggable( Level.FINE ) ) 
		//            ABox.log.fine( "TYPE: " + this + " " + c );        

		// if we are checking entailment using a precompleted ABox, _abox.branch
		// is set to -1. however, since applyAllValues is done automatically
		// and the edge used in applyAllValues may depend on a _branch we want
		// this type to be deleted when that edge goes away, i.e. we backtrack
		// to a position before the max dependency of this type
		int b = _abox.getBranch();
		final int max = ds.max();
		if (b == -1 && max != 0)
			b = max + 1;

		ds = ds.copy(b);

		_depends.put(c, ds);

		_abox.setChanged(true);

		// add to effected list
		if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), this.getName());

		//create new _queue element
		final QueueElement qElement = new QueueElement(this, c);

		final ATermAppl notC = ATermUtils.negate(c);
		DependencySet clashDepends = _depends.get(notC);
		if (clashDepends != null)
		{
			final ATermAppl positive = ATermUtils.isNot(notC) ? c : notC;
			clashDepends = clashDepends.union(ds, _abox.doExplanation());
			clashDepends = clashDepends.copy(_abox.getBranch());
			_abox.setClash(Clash.atomic(this, clashDepends, positive));
		}

		if (ATermUtils.isPrimitive(c))
		{
			setChanged(ATOM);
			_types[ATOM].add(c);

			if (PelletOptions.USE_COMPLETION_QUEUE)
				//update completion _queue
				_abox.getCompletionQueue().add(qElement, NodeSelector.ATOM);
		}
		else
			if (c.getAFun().equals(ATermUtils.ANDFUN))
				for (ATermList cs = (ATermList) c.getArgument(0); !cs.isEmpty(); cs = cs.getNext())
				{
					final ATermAppl conj = (ATermAppl) cs.getFirst();

					addType(conj, ds, checkForPruned);
				}
			else
				if (c.getAFun().equals(ATermUtils.ALLFUN))
				{
					setChanged(ALL);
					_types[ALL].add(c);

					if (PelletOptions.USE_COMPLETION_QUEUE)
						//update completion _queue
						_abox.getCompletionQueue().add(qElement, NodeSelector.UNIVERSAL);
				}
				else
					if (c.getAFun().equals(ATermUtils.MINFUN))
					{
						if (!isRedundantMin(c))
						{
							_types[MIN].add(c);
							setChanged(MIN);

							if (PelletOptions.USE_COMPLETION_QUEUE)
								//update completion _queue
								_abox.getCompletionQueue().add(qElement, NodeSelector.MIN_NUMBER);

							// check min clash after concept is added to the type
							// list. otherwise a clash found will prevent the
							// addition to the type list and term will be only in the
							// dependency map. smart restore may not remove the cardinality
							// from dependency map leaving the _node in an invalid state.
							checkMinClash(c, ds);
						}
					}
					else
						if (c.getAFun().equals(ATermUtils.NOTFUN))
						{
							final ATermAppl x = (ATermAppl) c.getArgument(0);
							if (ATermUtils.isAnd(x))
							{
								setChanged(OR);
								_types[OR].add(c);

								if (PelletOptions.USE_COMPLETION_QUEUE)
									//update completion _queue
									_abox.getCompletionQueue().add(qElement, NodeSelector.DISJUNCTION);
							}
							else
								if (ATermUtils.isAllValues(x))
								{
									setChanged(SOME);
									_types[SOME].add(c);

									if (PelletOptions.USE_COMPLETION_QUEUE)
										//update completion _queue					
										_abox.getCompletionQueue().add(qElement, NodeSelector.EXISTENTIAL);
								}
								else
									if (ATermUtils.isMin(x))
									{
										if (!isRedundantMax(x))
										{
											_types[MAX].add(c);
											setChanged(MAX);

											if (PelletOptions.USE_COMPLETION_QUEUE)
											{
												//update completion _queue						
												_abox.getCompletionQueue().add(qElement, NodeSelector.MAX_NUMBER);
												_abox.getCompletionQueue().add(qElement, NodeSelector.CHOOSE);
												_abox.getCompletionQueue().add(qElement, NodeSelector.GUESS);
											}

											// check max clash after concept is added to the type
											// list. otherwise a clash found will prevent the
											// addition to the type list and term will be only in the
											// dependency map. smart restore may not remove the cardinality
											// from depdendency map leaving the _node in an invalid state.
											checkMaxClash(c, ds);
										}
									}
									else
										if (ATermUtils.isNominal(x))
										{
											setChanged(ATOM);
											_types[ATOM].add(c);

											if (PelletOptions.USE_COMPLETION_QUEUE)
												//update completion _queue					
												_abox.getCompletionQueue().add(qElement, NodeSelector.ATOM);
										}
										else
											if (ATermUtils.isSelf(x))
											{
												final ATermAppl p = (ATermAppl) x.getArgument(0);
												final Role role = _abox.getRole(p);
												// during loading role would be null
												if (role != null)
												{
													final EdgeList selfEdges = outEdges.getEdges(role).getEdgesTo(this);
													if (!selfEdges.isEmpty())
														_abox.setClash(Clash.unexplained(this, selfEdges.getDepends(_abox.doExplanation())));
												}
											}
											else
												if (x.getArity() == 0)
												{
													setChanged(ATOM);
													_types[ATOM].add(c);

													if (PelletOptions.USE_COMPLETION_QUEUE)
														//update completion _queue					
														_abox.getCompletionQueue().add(qElement, NodeSelector.ATOM);
												}
												else
													throw new InternalReasonerException("Invalid type " + c + " for _individual " + _name);
						}
						else
							if (c.getAFun().equals(ATermUtils.VALUEFUN))
							{
								setChanged(NOM);
								_types[NOM].add(c);

								if (PelletOptions.USE_COMPLETION_QUEUE)
									//update completion _queue				
									_abox.getCompletionQueue().add(qElement, NodeSelector.NOMINAL);
							}
							else
								if (ATermUtils.isSelf(c))
								{
									setChanged(ATOM);
									_types[ATOM].add(c);
								}
								else
									throw new InternalReasonerException("Warning: Adding invalid class constructor - " + c);
	}

	public boolean checkMinClash(final ATermAppl minCard, final DependencySet minDepends)
	{
		final Role minR = _abox.getRole(minCard.getArgument(0));
		if (minR == null)
			return false;
		final int min = ((ATermInt) minCard.getArgument(1)).getInt();
		final ATermAppl minC = (ATermAppl) minCard.getArgument(2);

		if (minR.isFunctional() && min > 1)
		{
			_abox.setClash(Clash.minMax(this, minDepends.union(minR.getExplainFunctional(), _abox.doExplanation())));

			return true;
		}

		for (final ATermAppl mc : _types[MAX])
		{
			// max(r, n) is in normalized form not(min(p, n + 1))
			final ATermAppl maxCard = (ATermAppl) mc.getArgument(0);
			final Role maxR = _abox.getRole(maxCard.getArgument(0));
			if (maxR == null)
				return false;
			final int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;
			final ATermAppl maxC = (ATermAppl) maxCard.getArgument(2);

			if (max < min && minC.equals(maxC) && minR.isSubRoleOf(maxR))
			{
				final DependencySet maxDepends = getDepends(mc);
				final DependencySet subDepends = maxR.getExplainSub(minR.getName());
				final DependencySet ds = minDepends.union(maxDepends, _abox.doExplanation()).union(subDepends, _abox.doExplanation());

				_abox.setClash(Clash.minMax(this, ds));

				return true;
			}
		}

		return false;
	}

	public boolean checkMaxClash(final ATermAppl normalizedMax, final DependencySet maxDepends)
	{
		final ATermAppl maxCard = (ATermAppl) normalizedMax.getArgument(0);
		final Role maxR = _abox.getRole(maxCard.getArgument(0));
		if (maxR == null)
			return false;
		final int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;
		final ATermAppl maxC = (ATermAppl) maxCard.getArgument(2);

		for (final ATermAppl minCard : _types[MIN])
		{
			final Role minR = _abox.getRole(minCard.getArgument(0));
			if (minR == null)
				return false;
			final int min = ((ATermInt) minCard.getArgument(1)).getInt();
			final ATermAppl minC = (ATermAppl) minCard.getArgument(2);

			if (max < min && minC.equals(maxC) && minR.isSubRoleOf(maxR))
			{
				final DependencySet minDepends = getDepends(minCard);
				final DependencySet subDepends = maxR.getExplainSub(minR.getName());
				final DependencySet ds = minDepends.union(maxDepends, _abox.doExplanation()).union(subDepends, _abox.doExplanation());

				_abox.setClash(Clash.minMax(this, ds));

				return true;
			}
		}

		return false;
	}

	public boolean isRedundantMin(final ATermAppl minCard)
	{
		final Role minR = _abox.getRole(minCard.getArgument(0));

		if (minR == null)
			return false;

		final int min = ((ATermInt) minCard.getArgument(1)).getInt();
		final ATermAppl minQ = (ATermAppl) minCard.getArgument(2);

		for (final ATermAppl prevMinCard : _types[MIN])
		{
			final Role prevMinR = _abox.getRole(prevMinCard.getArgument(0));

			if (prevMinR == null)
				continue;

			final int prevMin = ((ATermInt) prevMinCard.getArgument(1)).getInt() - 1;
			final ATermAppl prevMinQ = (ATermAppl) prevMinCard.getArgument(2);

			if (min <= prevMin && prevMinR.isSubRoleOf(minR) && (minQ.equals(prevMinQ) || ATermUtils.isTop(minQ)))
				return true;
		}

		return false;
	}

	public boolean isRedundantMax(final ATermAppl maxCard)
	{
		final Role maxR = _abox.getRole(maxCard.getArgument(0));
		if (maxR == null)
			return false;

		final int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;

		if (max == 1 && maxR.isFunctional())
			return true;

		final ATermAppl maxQ = (ATermAppl) maxCard.getArgument(2);

		for (final ATermAppl mc : _types[MAX])
		{
			// max(r, n) is in normalized form not(min(p, n + 1))
			final ATermAppl prevMaxCard = (ATermAppl) mc.getArgument(0);
			final Role prevMaxR = _abox.getRole(prevMaxCard.getArgument(0));

			if (prevMaxR == null)
				continue;

			final int prevMax = ((ATermInt) prevMaxCard.getArgument(1)).getInt() - 1;
			final ATermAppl prevMaxQ = (ATermAppl) prevMaxCard.getArgument(2);

			if (max >= prevMax && maxR.isSubRoleOf(prevMaxR) && (maxQ.equals(prevMaxQ) || ATermUtils.isTop(prevMaxQ)))
				return true;
		}

		return false;
	}

	public DependencySet hasMax1(final Role r)
	{
		for (final ATermAppl mc : _types[MAX])
		{
			// max(r, n, c) is in normalized form not(min(p, n + 1))
			final ATermAppl maxCard = (ATermAppl) mc.getArgument(0);
			final Role maxR = _abox.getRole(maxCard.getArgument(0));
			final int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;
			final ATermAppl maxQ = (ATermAppl) maxCard.getArgument(2);

			// FIXME returned dependency set might be wrong
			// if there are two _types max(r,1) and max(p,1) where r subproperty of p
			// then the dependency set what we return might be wrong
			if (max == 1 && r.isSubRoleOf(maxR) && ATermUtils.isTop(maxQ))
				return getDepends(mc).union(r.getExplainSub(maxR.getName()), _abox.doExplanation());
		}

		return null;
	}

	public int getMaxCard(final Role r)
	{
		int min = Integer.MAX_VALUE;
		for (final ATermAppl mc : _types[MAX])
		{
			// max(r, n) is in normalized form not(min(p, n + 1))
			final ATermAppl maxCard = (ATermAppl) mc.getArgument(0);
			final Role maxR = _abox.getRole(maxCard.getArgument(0));
			final int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;

			if (r.isSubRoleOf(maxR) && max < min)
				min = max;
		}

		if (r.isFunctional() && min > 1)
			min = 1;

		return min;
	}

	public int getMinCard(final Role r, final ATermAppl c)
	{
		int maxOfMins = 0;
		for (final ATermAppl minCard : _types[MIN])
		{
			final Role minR = _abox.getRole(minCard.getArgument(0));
			final int min = ((ATermInt) minCard.getArgument(1)).getInt();
			final ATermAppl minC = (ATermAppl) minCard.getArgument(2);

			if (minR.isSubRoleOf(r) && maxOfMins < min && (minC.equals(c) || c.equals(TOP)))
				maxOfMins = min;
		}

		return maxOfMins;
	}

	@Override
	public boolean removeType(final ATermAppl c)
	{
		final boolean removed = super.removeType(c);

		// it is important to continue removal here because restore function
		// modified _depends map directly
		if (ATermUtils.isPrimitive(c) || ATermUtils.isSelf(c))
			_types[ATOM].remove(c);
		else
			if (c.getAFun().equals(ATermUtils.ANDFUN))
			{
				//			    _types[AND].remove(c);
			}
			else
				if (c.getAFun().equals(ATermUtils.ALLFUN))
					_types[ALL].remove(c);
				else
					if (c.getAFun().equals(ATermUtils.MINFUN))
						_types[MIN].remove(c);
					else
						if (c.getAFun().equals(ATermUtils.NOTFUN))
						{
							final ATermAppl x = (ATermAppl) c.getArgument(0);
							if (ATermUtils.isAnd(x))
								_types[OR].remove(c);
							else
								if (ATermUtils.isAllValues(x))
									_types[SOME].remove(c);
								else
									if (ATermUtils.isMin(x))
										_types[MAX].remove(c);
									else
										if (ATermUtils.isNominal(x))
											_types[ATOM].remove(c);
										else
											if (x.getArity() == 0)
												_types[ATOM].remove(c);
											else
												if (ATermUtils.isSelf(x))
												{
													// do nothing
												}
												else
													throw new InternalReasonerException("Invalid type " + c + " for _individual " + _name);
						}
						else
							if (c.getAFun().equals(ATermUtils.VALUEFUN))
								_types[NOM].remove(c);
							else
								throw new RuntimeException("Invalid concept " + c);

		return removed;
	}

	@Override
	final public boolean isLeaf()
	{
		return !isRoot() && outEdges.isEmpty();
	}

	@Override
	final public Individual getSame()
	{
		return (Individual) super.getSame();
	}

	final public Set<Node> getRSuccessors(final Role r, final ATermAppl c)
	{
		final Set<Node> result = new HashSet<>();

		final EdgeList edges = outEdges.getEdges(r);
		for (int i = 0, n = edges.size(); i < n; i++)
		{
			final Edge edge = edges.edgeAt(i);
			final Node other = edge.getNeighbor(this);
			if (other.hasType(c))
				result.add(other);
		}

		return result;
	}

	final public EdgeList getRSuccessorEdges(final Role r)
	{
		return outEdges.getEdges(r);
	}

	final public EdgeList getRPredecessorEdges(final Role r)
	{
		return _inEdges.getEdges(r);
	}

	final public Set<Node> getRNeighbors(final Role r)
	{
		return getRNeighborEdges(r).getNeighbors(this);
	}

	public EdgeList getRNeighborEdges(final Role r)
	{
		final EdgeList neighbors = outEdges.getEdges(r);

		final Role invR = r.getInverse();
		// inverse of datatype properties is not defined
		if (invR != null)
			neighbors.addEdgeList(_inEdges.getEdges(invR));

		return neighbors;
	}

	/**
	 * Get _neighbor edges to a specific _node
	 *
	 * @param r
	 * @param _node
	 * @return
	 */
	public EdgeList getRNeighborEdges(final Role r, final Node node)
	{
		final EdgeList neighbors = outEdges.getEdgesTo(r, node);

		final Role invR = r.getInverse();
		// inverse of datatype properties is not defined
		if (invR != null)
			neighbors.addEdgeList(_inEdges.getEdgesFrom((Individual) node, invR));

		return neighbors;
	}

	public EdgeList getEdgesTo(final Node x)
	{
		return outEdges.getEdgesTo(x);
	}

	public EdgeList getEdgesTo(final Node x, final Role r)
	{
		return outEdges.getEdgesTo(x).getEdges(r);
	}

	/**
	 * Checks if this _individual has at least n distinct r-neighbors that has a specific type.
	 *
	 * @param r Role we use to find neighbors
	 * @param n Number of neighbors
	 * @param c The type that all neighbors should belong to
	 * @return The union of dependencies for the edges leading to neighbors and the dependency of the type assertion for each _neighbor
	 */
	public DependencySet hasDistinctRNeighborsForMax(final Role r, final int n, final ATermAppl c)
	{
		//	    Timer t = _abox.getKB().timers.startTimer("hasDistinctRNeighbors1"); 

		boolean hasNeighbors = false;

		// get all the edges to x with a role (or subrole of) r
		final EdgeList edges = getRNeighborEdges(r);

		if (edges.size() >= n)
		{
			final List<List<Node>> allDisjointSets = new ArrayList<>();

			outerloop: for (int i = 0; i < edges.size(); i++)
			{
				final Node y = edges.edgeAt(i).getNeighbor(this);

				if (!y.hasType(c))
					continue;

				boolean added = false;
				for (int j = 0; j < allDisjointSets.size(); j++)
				{
					final List<Node> disjointSet = allDisjointSets.get(j);
					int k = 0;
					for (; k < disjointSet.size(); k++)
					{
						final Node z = disjointSet.get(k);
						if (!y.isDifferent(z))
							break;
					}
					if (k == disjointSet.size())
					{
						added = true;
						disjointSet.add(y);
						if (disjointSet.size() >= n)
						{
							hasNeighbors = true;
							break outerloop;
						}
					}
				}
				if (!added)
				{
					final List<Node> singletonSet = new ArrayList<>();
					singletonSet.add(y);
					allDisjointSets.add(singletonSet);
					if (n == 1)
					{
						hasNeighbors = true;
						break outerloop;
					}
				}
			}
		}
		//		t.stop();

		if (!hasNeighbors)
			return null;

		// we are being overly cautious here by getting the union of all
		// the edges to all r-neighbors 
		DependencySet ds = DependencySet.EMPTY;
		for (final Edge edge : edges)
		{
			ds = ds.union(r.getExplainSubOrInv(edge.getRole()), _abox.doExplanation());
			ds = ds.union(edge.getDepends(), _abox.doExplanation());
			final Node node = edge.getNeighbor(this);
			final DependencySet typeDS = node.getDepends(c);
			if (typeDS != null)
				ds = ds.union(typeDS, _abox.doExplanation());
		}

		return ds;
	}

	public boolean hasDistinctRNeighborsForMin(final Role r, final int n, final ATermAppl c)
	{
		return hasDistinctRNeighborsForMin(r, n, c, false);
	}

	/**
	 * Returns true if this _individual has at least n distinct r-neighbors. If only nominal neighbors are wanted then blockable ones will simply be ignored
	 * (note that this should only happen if r is an object property)
	 *
	 * @param r
	 * @param n
	 * @param onlyNominals
	 * @return
	 */
	public boolean hasDistinctRNeighborsForMin(final Role r, final int n, final ATermAppl c, final boolean onlyNominals)
	{
		// get all the edges to x with a role (or subrole of) r
		final EdgeList edges = getRNeighborEdges(r);

		if (n == 1 && !onlyNominals && c.equals(ATermUtils.TOP))
			return !edges.isEmpty();

		if (edges.size() < n)
			return false;

		final List<List<Node>> allDisjointSets = new ArrayList<>();
		for (int i = 0; i < edges.size(); i++)
		{
			final Node y = edges.edgeAt(i).getNeighbor(this);

			if (!y.hasType(c))
				continue;

			if (onlyNominals)
				if (y.isBlockable())
					continue;
				else
					if (n == 1)
						return true;

			boolean added = false;
			for (int j = 0; j < allDisjointSets.size(); j++)
			{
				boolean addToThis = true;
				final List<Node> disjointSet = allDisjointSets.get(j);
				for (int k = 0; k < disjointSet.size(); k++)
				{
					final Node z = disjointSet.get(k);
					if (!y.isDifferent(z))
					{
						addToThis = false;
						break;
					}
				}
				if (addToThis)
				{
					added = true;
					disjointSet.add(y);
					if (disjointSet.size() >= n)
						return true;
				}
			}
			if (!added)
			{
				final List<Node> singletonSet = new ArrayList<>();
				singletonSet.add(y);
				allDisjointSets.add(singletonSet);
			}

			if (n == 1 && allDisjointSets.size() >= 1)
				return true;
		}

		return false;
	}

	@Override
	final public boolean hasRNeighbor(final Role r)
	{
		if (outEdges.hasEdge(r))
			return true;

		final Role invR = r.getInverse();
		if (invR != null && _inEdges.hasEdge(invR))
			return true;

		return false;
	}

	public boolean hasRSuccessor(final Role r)
	{
		return outEdges.hasEdge(r);
	}

	@Override
	public boolean hasSuccessor(final Node x)
	{
		return outEdges.hasEdgeTo(x);
	}

	public final boolean hasRSuccessor(final Role r, final Node x)
	{
		return outEdges.hasEdge(this, r, x);
	}

	/**
	 * Check the property assertions to see if it is possible for this _individual to have the value for the given datatype property. This function is meaningful
	 * only called for individuals in a completed ABox (a pseudo model for the KB). In a completed ABox, _individual will have some literal successors that may
	 * or may not have a known value. The _individual has the _data property value only if it has a literal successor that has the exact given value and the edge
	 * between the _individual and the literal does not depend on any non- deterministic _branch. If the literal value is there but the edge _depends on a
	 * _branch then we cannot exactly say if the literal value is there or not. If there is no literal successor with the given value then we can for sure say
	 * that _individual does not have the _data property value (because it does not have the value in at least one model)
	 *
	 * @param r
	 * @param value
	 * @return Bool.TRUE if the _individual definetely has the property value, Bool.FALSE if the _individual definetely does NOT have the property value and
	 *         Bool.UNKNOWN if it cannot be determined for sure, i.e. consistency check is required
	 */
	public Bool hasDataPropertyValue(final Role r, final Object value)
	{
		Bool hasValue = Bool.FALSE;

		final EdgeList edges = outEdges.getEdges(r);
		for (int i = 0; i < edges.size(); i++)
		{
			final Edge edge = edges.edgeAt(i);
			final DependencySet ds = edge.getDepends();
			final Literal literal = (Literal) edge.getTo();
			final Object literalValue = literal.getValue();
			if (value != null && literalValue == null)
				try
			{
					if (_abox._dtReasoner.isSatisfiable(literal.getTypes(), value))
						hasValue = Bool.UNKNOWN;
					else
						hasValue = Bool.FALSE;
			}
			catch (final DatatypeReasonerException e)
			{
				final String msg = "Unexpected datatype reasoner exception while checking property value: " + e.getMessage();
				log.severe(msg);
				throw new InternalReasonerException(msg);
			}
			else
				if (value == null || value.equals(literalValue))
					if (ds.isIndependent())
						return Bool.TRUE;
					else
						hasValue = Bool.UNKNOWN;
		}

		return hasValue;
	}

	public boolean hasRNeighbor(final Role r, final Node x)
	{
		if (hasRSuccessor(r, x))
			return true;

		if (x instanceof Individual)
			return ((Individual) x).hasRSuccessor(r.getInverse(), this);

		return false;
	}

	@Override
	protected void addInEdge(final Edge edge)
	{
		setChanged(ALL);
		setChanged(MAX);
		_applyNext[MAX] = 0;

		_inEdges.addEdge(edge);
	}

	protected void addOutEdge(final Edge edge)
	{
		setChanged(ALL);
		setChanged(MAX);
		_applyNext[MAX] = 0;

		if (edge.getRole().isBottom())
			_abox.setClash(Clash.bottomProperty(edge.getFrom(), edge.getDepends(), edge.getRole().getName()));
		else
			outEdges.addEdge(edge);
	}

	public Edge addEdge(final Role r, final Node x, DependencySet ds)
	{

		// add these _nodes to the effected list
		if (_abox.getBranch() > 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
		{
			_abox.getBranchEffectTracker().add(_abox.getBranch(), this.getName());
			_abox.getBranchEffectTracker().add(_abox.getBranch(), x.getName());
		}

		if (r.isBottom())
		{
			_abox.setClash(Clash.bottomProperty(this, ds, r.getName()));
			return null;
		}

		if (hasRSuccessor(r, x) || r.isTop())
		{
			// TODO we might miss some of explanation axioms
			if (log.isLoggable(Level.FINE))
				log.fine("EDGE: " + this + " -> " + r + " -> " + x + ": " + ds + " " + getRNeighborEdges(r).getEdgesTo(x));
			return null;
		}

		if (isPruned())
			throw new InternalReasonerException("Adding edge to a pruned _node " + this + " " + r + " " + x);
		else
			if (isMerged())
				return null;

		_abox.setChanged(true);
		setChanged(ALL);
		setChanged(MAX);
		_applyNext[MAX] = 0;

		ds = ds.copy(_abox.getBranch());

		final Edge edge = new DefaultEdge(r, this, x, ds);

		outEdges.addEdge(edge);
		x.addInEdge(edge);

		return edge;
	}

	@Override
	final public EdgeList getOutEdges()
	{
		return outEdges;
	}

	public Individual getParent()
	{
		return _parent;
	}

	/**
	 * Resets this _node (_types, edges, sames, _differents) to contain only asserted information. This function can be seen a specialized case of restore but a
	 * special function is needed both for correctness (e.g. SMART_RESTORE option should not change behavior) and performance
	 */
	@Override
	public void reset(final boolean onlyApplyTypes)
	{
		super.reset(onlyApplyTypes);

		for (int i = 0; i < TYPES; i++)
			_applyNext[i] = 0;

		if (onlyApplyTypes)
			return;

		outEdges.reset();
	}

	@Override
	protected void resetTypes()
	{
		for (int type = 0; type < TYPES; type++)
		{
			final ArrayList<ATermAppl> list = _types[type];
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				final ATermAppl c = list.get(i);

				if (_depends.get(c).getBranch() != DependencySet.NO_BRANCH)
				{
					// rather deleting the element from an ArrayList move
					// it to the _end so we can purge everything from the 
					// tail of the list (note: if we change the list impl
					// used here to a LinkedList we can modify this bit)
					Collections.swap(list, i--, --size);

					_depends.remove(c);
				}
			}

			// remove everything from the _end of list 
			if (size < list.size())
				list.subList(size, list.size()).clear();
		}

		final Iterator<Entry<ATermAppl, DependencySet>> i = _depends.entrySet().iterator();
		while (i.hasNext())
		{
			final Entry<ATermAppl, DependencySet> e = i.next();
			if (e.getValue().getBranch() != DependencySet.NO_BRANCH)
				i.remove();
		}
	}

	@Override
	public boolean restore(final int branch)
	{
		final Boolean restorePruned = restorePruned(branch);
		if (Boolean.FALSE.equals(restorePruned))
			return restorePruned;

		boolean restored = Boolean.TRUE.equals(restorePruned);

		restored |= super.restore(branch);

		for (int i = 0; i < TYPES; i++)
			_applyNext[i] = 0;

		boolean removed = false;

		for (final Iterator<Edge> i = outEdges.iterator(); i.hasNext();)
		{
			final Edge e = i.next();
			final DependencySet d = e.getDepends();

			if (d.getBranch() > branch)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("RESTORE: " + _name + " remove edge " + e + " " + d.max() + " " + branch);
				i.remove();

				restored = true;
				removed = true;
				if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
					_abox.getIncrementalChangeTracker().addDeletedEdge(e);
			}
		}

		//if we removed an edge the update the _queue
		if (removed && PelletOptions.USE_COMPLETION_QUEUE)
		{
			_abox.getCompletionQueue().add(new QueueElement(this), NodeSelector.EXISTENTIAL);
			_abox.getCompletionQueue().add(new QueueElement(this), NodeSelector.MIN_NUMBER);
		}

		if (_modifiedAfterMerge && restored)
		{
			for (final Entry<ATermAppl, DependencySet> entry : _depends.entrySet())
			{
				final ATermAppl c = entry.getKey();
				final ATermAppl notC = ATermUtils.negate(c);

				final DependencySet ds = _depends.get(notC);
				if (ds != null)
				{
					DependencySet clashDepends = entry.getValue();
					final ATermAppl positive = ATermUtils.isNot(notC) ? c : notC;
					clashDepends = clashDepends.union(ds, _abox.doExplanation());
					_abox.setClash(Clash.atomic(this, clashDepends, positive));
				}
			}
			_modifiedAfterMerge = false;
		}

		return restored;
	}

	final public boolean removeEdge(final Edge edge)
	{
		final boolean removed = outEdges.removeEdge(edge);

		if (!removed)
			throw new InternalReasonerException("Trying to remove a non-existing edge " + edge);

		return true;
	}

	/**
	 * Prune the given _node by removing all links going to nominal _nodes and recurse through all successors. No need to remove incoming edges because either
	 * the _node is the first one being pruned so the merge function already handled it or this is a successor _node and its successor is also being pruned
	 *
	 * @param succ
	 * @param ds
	 */
	@Override
	public void prune(final DependencySet ds)
	{

		// add to effected list
		if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), this.getName());

		pruned = ds;

		for (int i = 0; i < outEdges.size(); i++)
		{
			final Edge edge = outEdges.edgeAt(i);
			final Node succ = edge.getTo();

			if (succ.isPruned())
				continue;
			else
				if (succ.isNominal())
					succ.removeInEdge(edge);
				else
					succ.prune(ds);
		}
	}

	@Override
	public void unprune(final int branch)
	{
		super.unprune(branch);

		boolean added = false;

		for (int i = 0; i < outEdges.size(); i++)
		{
			final Edge edge = outEdges.edgeAt(i);
			final DependencySet d = edge.getDepends();

			if (d.getBranch() <= branch)
			{
				final Node succ = edge.getTo();
				final Role role = edge.getRole();

				if (!succ._inEdges.hasExactEdge(this, role, succ))
				{
					succ.addInEdge(edge);

					// update affected
					if (PelletOptions.TRACK_BRANCH_EFFECTS)
					{
						_abox.getBranchEffectTracker().add(d.getBranch(), succ._name);
						_abox.getBranchEffectTracker().add(d.getBranch(), _name);
					}

					if (PelletOptions.USE_COMPLETION_QUEUE)
					{
						added = true;

						if (succ instanceof Individual)
						{
							final Individual succInd = (Individual) succ;
							succInd._applyNext[Node.MAX] = 0;
							final QueueElement qe = new QueueElement(succInd);
							_abox.getCompletionQueue().add(qe, NodeSelector.MAX_NUMBER);
							_abox.getCompletionQueue().add(qe, NodeSelector.GUESS);
							_abox.getCompletionQueue().add(qe, NodeSelector.CHOOSE);
						}
					}
				}
			}
		}

		if (added)
		{
			_applyNext[Node.MAX] = 0;
			final QueueElement qe = new QueueElement(this);
			_abox.getCompletionQueue().add(qe, NodeSelector.MAX_NUMBER);
			_abox.getCompletionQueue().add(qe, NodeSelector.GUESS);
			_abox.getCompletionQueue().add(qe, NodeSelector.CHOOSE);
		}
	}

	public String debugString()
	{
		return _name.getName() + " = " + _types[ATOM] + _types[ALL] + _types[SOME] + _types[OR] + _types[MIN] + _types[MAX] + _types[NOM] + "; **" + outEdges + "**" + "; **" + _inEdges + "**" + " --> " + _depends + "";
	}

	@Override
	protected void updateNodeReferences()
	{
		super.updateNodeReferences();

		if (_parent != null)
			_parent = _abox.getIndividual(_parent.getName());

		if (isPruned())
		{
			final EdgeList oldEdges = outEdges;
			outEdges = new EdgeList(oldEdges.size());
			for (int i = 0; i < oldEdges.size(); i++)
			{
				final Edge edge = oldEdges.edgeAt(i);
				final Node to = _abox.getNode(edge.getTo().getName());
				final Edge newEdge = new DefaultEdge(edge.getRole(), this, to, edge.getDepends());
				outEdges.addEdge(newEdge);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBottom()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTop()
	{
		return false;
	}
}
