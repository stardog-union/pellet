// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.SetUtils;

/**
 * @author Evren Sirin
 */
public abstract class Node
{
	public final static Logger log = Log.getLogger(Node.class);

	public final static int BLOCKABLE = Integer.MAX_VALUE;
	public final static int NOMINAL = 0;

	public final static int CHANGED = 0x7F;
	public final static int UNCHANGED = 0x00;
	public final static int ATOM = 0;
	public final static int OR = 1;
	public final static int SOME = 2;
	public final static int ALL = 3;
	public final static int MIN = 4;
	public final static int MAX = 5;
	public final static int NOM = 6;
	public final static int TYPES = 7;

	protected ABox _abox;
	protected ATermAppl _name;
	protected Map<ATermAppl, DependencySet> _depends;
	private final boolean _isRoot;
	private boolean _isConceptRoot;

	/**
	 * If this _node is merged to another one, points to that _node otherwise points to itself. This is a linked list implementation of disjoint-union _data
	 * structure.
	 */
	protected Node mergedTo = this;

	protected EdgeList _inEdges;

	/**
	 * Dependency information about why merged happened (if at all)
	 */
	protected DependencySet _mergeDepends = null;

	protected DependencySet pruned = null;

	/**
	 * Set of other _nodes that have been merged to this _node. Note that this is only the set of _nodes directly merged to this one. A recursive traversal is
	 * required to get all the merged _nodes.
	 */
	protected Set<Node> merged;

	protected Map<Node, DependencySet> _differents;

	protected Node(final ATermAppl name, final ABox abox)
	{
		this._name = name;
		this._abox = abox;

		_isRoot = !ATermUtils.isAnon(name);
		_isConceptRoot = false;

		_mergeDepends = DependencySet.INDEPENDENT;
		_differents = CollectionUtils.makeMap();
		_depends = CollectionUtils.makeMap();

		_inEdges = new EdgeList();
	}

	protected Node(final Node node, final ABox abox)
	{
		this._name = node.getName();
		this._abox = abox;

		_isRoot = node._isRoot;
		_isConceptRoot = node._isConceptRoot;

		_mergeDepends = node._mergeDepends;
		mergedTo = node.mergedTo;
		merged = node.merged;
		pruned = node.pruned;

		// do not copy _differents right now because we need to
		// update _node references later anyway
		_differents = node._differents;
		_depends = CollectionUtils.makeMap(node._depends);

		_inEdges = node._inEdges;
	}

	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return (obj == this) || ((obj.getClass() == getClass()) && ((Node) obj)._name.equals(_name));
	}

	protected void updateNodeReferences()
	{
		mergedTo = _abox.getNode(mergedTo.getName());

		final Map<Node, DependencySet> diffs = new HashMap<>(_differents.size());
		for (final Map.Entry<Node, DependencySet> entry : _differents.entrySet())
		{
			final Node node = entry.getKey();

			diffs.put(_abox.getNode(node.getName()), entry.getValue());
		}
		_differents = diffs;

		if (merged != null)
		{
			final Set<Node> sames = new HashSet<>(merged.size());
			for (final Node node : merged)
				sames.add(_abox.getNode(node.getName()));
			merged = sames;
		}

		final EdgeList oldEdges = _inEdges;
		_inEdges = new EdgeList(oldEdges.size());
		for (int i = 0; i < oldEdges.size(); i++)
		{
			final Edge edge = oldEdges.edgeAt(i);

			final Individual from = _abox.getIndividual(edge.getFrom().getName());
			final Edge newEdge = new DefaultEdge(edge.getRole(), from, this, edge.getDepends());

			_inEdges.addEdge(newEdge);
			if (!isPruned())
				from.getOutEdges().addEdge(newEdge);
		}
	}

	/**
	 * Indicates that _node has been changed in a way that requires us to recheck the concepts of given type.
	 * 
	 * @param type type of concepts that need to be rechecked
	 */
	public void setChanged(final int type)
	{
		//Check if we need to updated the completion _queue
		//Currently we only updated the changed lists for checkDatatypeCount()
		final QueueElement newElement = new QueueElement(this);

		//update the datatype _queue
		if ((type == Node.ALL || type == Node.MIN) && PelletOptions.USE_COMPLETION_QUEUE)
			_abox.getCompletionQueue().add(newElement, NodeSelector.DATATYPE);

		// add _node to effected list
		if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), this.getName());
	}

	/**
	 * Returns true if this is the _node created for the concept satisfiability check.
	 * 
	 * @return
	 */
	public boolean isConceptRoot()
	{
		return _isConceptRoot;
	}

	public void setConceptRoot(final boolean isConceptRoot)
	{
		this._isConceptRoot = isConceptRoot;
	}

	public boolean isBnode()
	{
		return ATermUtils.isBnode(_name);
	}

	public boolean isNamedIndividual()
	{
		return _isRoot && !_isConceptRoot && !isBnode();
	}

	public boolean isRoot()
	{
		return _isRoot || isNominal();
	}

	public abstract boolean isLeaf();

	public boolean isRootNominal()
	{
		return _isRoot && isNominal();
	}

	public abstract Node copyTo(ABox abox);

	protected void addInEdge(final Edge edge)
	{
		_inEdges.addEdge(edge);
	}

	public EdgeList getInEdges()
	{
		return _inEdges;
	}

	public boolean removeInEdge(final Edge edge)
	{
		final boolean removed = _inEdges.removeEdge(edge);

		if (!removed)
			throw new InternalReasonerException("Trying to remove a non-existing edge " + edge);

		return true;
	}

	public void removeInEdges()
	{
		_inEdges = new EdgeList();
	}

	public void reset(final boolean onlyApplyTypes)
	{
		assert onlyApplyTypes || isRootNominal() : "Only asserted individuals can be reset: " + this;

		if (PelletOptions.USE_COMPLETION_QUEUE)
			_abox.getCompletionQueue().add(new QueueElement(this));

		if (onlyApplyTypes)
			return;

		if (pruned != null)
			unprune(DependencySet.NO_BRANCH);

		mergedTo = this;
		_mergeDepends = DependencySet.INDEPENDENT;
		merged = null;

		final Iterator<DependencySet> i = _differents.values().iterator();
		while (i.hasNext())
		{
			final DependencySet d = i.next();
			if (d.getBranch() != DependencySet.NO_BRANCH)
				i.remove();
		}

		resetTypes();

		_inEdges.reset();
	}

	protected void resetTypes()
	{
		final Iterator<DependencySet> i = _depends.values().iterator();
		while (i.hasNext())
		{
			final DependencySet d = i.next();
			if (d.getBranch() != DependencySet.NO_BRANCH)
				i.remove();
		}
	}

	public Boolean restorePruned(final int branch)
	{

		if (PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), _name);

		if (pruned != null)
			if (pruned.getBranch() > branch)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("RESTORE: " + this + " merged _node " + mergedTo + " " + _mergeDepends);

				if (_mergeDepends.getBranch() > branch)
					undoSetSame();

				unprune(branch);

				if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
					_abox.getIncrementalChangeTracker().addUnprunedNode(this);

				// we may need to remerge this _node
				if (this instanceof Individual)
				{
					final Individual ind = (Individual) this;

					if (PelletOptions.USE_COMPLETION_QUEUE)
					{
						ind._applyNext[Node.NOM] = 0;
						_abox.getCompletionQueue().add(new QueueElement(this), NodeSelector.NOMINAL);
					}

				}

				return Boolean.TRUE;
			}
			else
			{
				if (log.isLoggable(Level.FINE))
					log.fine("DO NOT RESTORE: pruned _node " + this + " = " + mergedTo + " " + _mergeDepends);

				return Boolean.FALSE;
			}

		return null;
	}

	public boolean restore(final int branch)
	{

		if (PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), _name);

		boolean restored = false;

		final List<ATermAppl> conjunctions = new ArrayList<>();

		boolean removed = false;

		for (final Iterator<ATermAppl> i = getTypes().iterator(); i.hasNext();)
		{
			final ATermAppl c = i.next();
			final DependencySet d = getDepends(c);

			final boolean removeType = PelletOptions.USE_SMART_RESTORE
			//                ? ( !d.contains( _branch ) )
			? (d.max() >= branch)
					: (d.getBranch() > branch);

			if (removeType)
			{
				removed = true;

				if (log.isLoggable(Level.FINE))
					log.fine("RESTORE: " + this + " remove type " + c + " " + d + " " + branch);

				//track that this _node is affected
				if (PelletOptions.USE_INCREMENTAL_CONSISTENCY && this instanceof Individual)
					_abox.getIncrementalChangeTracker().addDeletedType(this, c);

				i.remove();
				removeType(c);
				restored = true;
			}
			else
				if (PelletOptions.USE_SMART_RESTORE && ATermUtils.isAnd(c))
					conjunctions.add(c);
		}

		//update the _queue with things that could readd this type
		if (removed && PelletOptions.USE_COMPLETION_QUEUE && this instanceof Individual)
		{
			final Individual ind = (Individual) this;
			ind._applyNext[Node.ATOM] = 0;
			ind._applyNext[Node.OR] = 0;

			final QueueElement qe = new QueueElement(this);
			_abox.getCompletionQueue().add(qe, NodeSelector.DISJUNCTION);
			_abox.getCompletionQueue().add(qe, NodeSelector.ATOM);
		}

		// with smart restore there is a possibility that we remove a conjunct
		// but not the conjunction. this is the case if conjunct was added before
		// the conjunction but depended on an earlier _branch. so we need to make
		// sure all conjunctions are actually applied
		if (PelletOptions.USE_SMART_RESTORE)
			for (final ATermAppl c : conjunctions)
			{
				final DependencySet d = getDepends(c);
				for (ATermList cs = (ATermList) c.getArgument(0); !cs.isEmpty(); cs = cs.getNext())
				{
					final ATermAppl conj = (ATermAppl) cs.getFirst();

					addType(conj, d);
				}
			}

		for (final Iterator<Entry<Node, DependencySet>> i = _differents.entrySet().iterator(); i.hasNext();)
		{
			final Entry<Node, DependencySet> entry = i.next();
			final Node node = entry.getKey();
			final DependencySet d = entry.getValue();

			if (d.getBranch() > branch)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("RESTORE: " + _name + " delete difference " + node);
				i.remove();
				restored = true;
			}
		}

		removed = false;
		for (final Iterator<Edge> i = _inEdges.iterator(); i.hasNext();)
		{
			final Edge e = i.next();
			final DependencySet d = e.getDepends();

			if (d.getBranch() > branch)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("RESTORE: " + _name + " delete reverse edge " + e);

				if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
					_abox.getIncrementalChangeTracker().addDeletedEdge(e);

				i.remove();
				restored = true;
				removed = true;
			}
		}

		if (removed && PelletOptions.USE_COMPLETION_QUEUE)
		{
			final QueueElement qe = new QueueElement(this);
			_abox.getCompletionQueue().add(qe, NodeSelector.EXISTENTIAL);
			_abox.getCompletionQueue().add(qe, NodeSelector.MIN_NUMBER);
		}

		return restored;
	}

	public void addType(final ATermAppl c, DependencySet ds)
	{
		if (isPruned())
			throw new InternalReasonerException("Adding type to a pruned _node " + this + " " + c);
		else
			if (isMerged())
				return;

		// add to effected list
		if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), this.getName());

		int b = _abox.getBranch();

		final int max = ds.max();
		if (b == -1 && max != 0)
			b = max + 1;
		ds = ds.copy(b);
		_depends.put(c, ds);

		_abox.setChanged(true);
	}

	public boolean removeType(final ATermAppl c)
	{
		return _depends.remove(c) != null;
	}

	public boolean hasType(final ATerm c)
	{
		return _depends.containsKey(c);
	}

	public Bool hasObviousType(final ATermAppl c)
	{
		DependencySet ds = getDepends(c);

		if (ds != null)
		{
			if (ds.isIndependent())
				return Bool.TRUE;
		}
		else
			if ((ds = getDepends(ATermUtils.negate(c))) != null)
			{
				if (ds.isIndependent())
					return Bool.FALSE;
			}
			else
				if (isIndividual() && ATermUtils.isNominal(c))
					// TODO probably redundant if : Bool.FALSE
					if (!c.getArgument(0).equals(this.getName()))
						return Bool.FALSE;
					else
						return Bool.TRUE;

		if (isIndividual())
		{
			ATermAppl r = null;
			ATermAppl d = null;

			if (ATermUtils.isNot(c))
			{
				final ATermAppl notC = (ATermAppl) c.getArgument(0);
				if (ATermUtils.isAllValues(notC))
				{
					r = (ATermAppl) notC.getArgument(0);
					d = ATermUtils.negate((ATermAppl) notC.getArgument(1));
				}
			}
			else
				if (ATermUtils.isSomeValues(c))
				{
					r = (ATermAppl) c.getArgument(0);
					d = (ATermAppl) c.getArgument(1);
				}

			if (r != null)
			{
				final Individual ind = (Individual) this;

				final Role role = _abox.getRole(r);

				if (!role.isObjectRole() || !role.isSimple())
					return Bool.UNKNOWN;

				final EdgeList edges = ind.getRNeighborEdges(role);

				Bool ot = Bool.FALSE;

				for (int e = 0; e < edges.size(); e++)
				{
					final Edge edge = edges.edgeAt(e);

					if (!edge.getDepends().isIndependent())
					{
						ot = Bool.UNKNOWN;
						continue;
					}

					final Individual y = (Individual) edge.getNeighbor(ind);

					// TODO all this stuff in one method - this is only for
					// handling AND
					// clauses - they are implemented in _abox.isKnownType
					ot = ot.or(_abox.isKnownType(y, d, SetUtils.<ATermAppl> emptySet()));// y.hasObviousType(d));

					if (ot.isTrue())
						return ot;
				}
				return ot;
			}
		}

		return Bool.UNKNOWN;
	}

	public boolean hasObviousType(final Collection<ATermAppl> coll)
	{
		for (final ATermAppl c : coll)
		{
			final DependencySet ds = getDepends(c);

			if (ds != null && ds.isIndependent())
				return true;
		}

		return false;
	}

	boolean hasPredecessor(final Individual x)
	{
		return x.hasSuccessor(this);
	}

	public abstract boolean hasSuccessor(Node x);

	public abstract DependencySet getNodeDepends();

	public DependencySet getDepends(final ATerm c)
	{
		return _depends.get(c);
	}

	public Map<ATermAppl, DependencySet> getDepends()
	{
		return _depends;
	}

	public Set<ATermAppl> getTypes()
	{
		return _depends.keySet();
	}

	public void removeTypes()
	{
		_depends.clear();
	}

	public int prunedAt()
	{
		return pruned.getBranch();
	}

	public boolean isPruned()
	{
		return pruned != null;
	}

	public DependencySet getPruned()
	{
		return pruned;
	}

	public abstract void prune(DependencySet ds);

	public void unprune(final int branch)
	{
		pruned = null;

		boolean added = false;

		for (int i = 0; i < _inEdges.size(); i++)
		{
			final Edge edge = _inEdges.edgeAt(i);
			final DependencySet d = edge.getDepends();

			if (d.getBranch() <= branch)
			{
				final Individual pred = edge.getFrom();
				final Role role = edge.getRole();

				// if both pred and *this* were merged to other _nodes (in that _order)
				// there is a chance we might duplicate the edge so first check for
				// the existence of the edge
				if (!pred.getOutEdges().hasExactEdge(pred, role, this))
				{
					pred.addOutEdge(edge);

					// update affected
					if (PelletOptions.TRACK_BRANCH_EFFECTS)
					{
						_abox.getBranchEffectTracker().add(d.getBranch(), pred._name);
						_abox.getBranchEffectTracker().add(d.getBranch(), _name);
					}

					if (PelletOptions.USE_COMPLETION_QUEUE)
					{
						added = true;
						pred._applyNext[Node.MAX] = 0;

						final QueueElement qe = new QueueElement(pred);
						_abox.getCompletionQueue().add(qe, NodeSelector.MAX_NUMBER);
						_abox.getCompletionQueue().add(qe, NodeSelector.GUESS);
						_abox.getCompletionQueue().add(qe, NodeSelector.CHOOSE);
						_abox.getCompletionQueue().add(qe, NodeSelector.UNIVERSAL);
					}

					if (log.isLoggable(Level.FINE))
						log.fine("RESTORE: " + _name + " ADD reverse edge " + edge);
				}
			}
		}

		if (added)
			if (this instanceof Individual)
			{
				final Individual ind = (Individual) this;
				ind._applyNext[Node.MAX] = 0;
				final QueueElement qe = new QueueElement(ind);
				_abox.getCompletionQueue().add(qe, NodeSelector.MAX_NUMBER);
				_abox.getCompletionQueue().add(qe, NodeSelector.GUESS);
				_abox.getCompletionQueue().add(qe, NodeSelector.CHOOSE);
				_abox.getCompletionQueue().add(qe, NodeSelector.UNIVERSAL);
			}
	}

	public abstract int getNominalLevel();

	public abstract boolean isNominal();

	public abstract boolean isBlockable();

	public abstract boolean isLiteral();

	public abstract boolean isIndividual();

	public int mergedAt()
	{
		return _mergeDepends.getBranch();
	}

	public boolean isMerged()
	{
		return mergedTo != this;
	}

	public Node getMergedTo()
	{
		return mergedTo;
	}

	//	public DependencySet getMergeDependency() {
	//		return _mergeDepends;
	//	}

	/**
	 * Get the dependency if this _node is merged to another _node. This _node may be merged to another _node which is later merged to another _node and so on.
	 * This function may return the dependency for the first step or the union of all steps.
	 */
	public DependencySet getMergeDependency(final boolean all)
	{
		if (!isMerged() || !all)
			return _mergeDepends;

		DependencySet ds = _mergeDepends;
		Node node = mergedTo;
		while (node.isMerged())
		{
			ds = ds.union(node._mergeDepends, _abox.doExplanation());
			node = node.mergedTo;
		}

		return ds;
	}

	public Node getSame()
	{
		if (mergedTo == this)
			return this;

		return mergedTo.getSame();
	}

	public void undoSetSame()
	{
		mergedTo.removeMerged(this);
		_mergeDepends = DependencySet.INDEPENDENT;
		mergedTo = this;
	}

	private void addMerged(final Node node)
	{
		if (merged == null)
			merged = new HashSet<>(3);
		merged.add(node);
	}

	public Set<Node> getMerged()
	{
		if (merged == null)
			return SetUtils.emptySet();
		return merged;
	}

	public Map<Node, DependencySet> getAllMerged()
	{
		final Map<Node, DependencySet> result = new HashMap<>();
		getAllMerged(DependencySet.INDEPENDENT, result);
		return result;
	}

	private void getAllMerged(final DependencySet ds, final Map<Node, DependencySet> result)
	{
		if (merged == null)
			return;

		for (final Node mergedNode : merged)
		{
			final DependencySet mergeDS = ds.union(mergedNode.getMergeDependency(false), false);
			result.put(mergedNode, mergeDS);
			mergedNode.getAllMerged(mergeDS, result);
		}
	}

	private void removeMerged(final Node node)
	{
		merged.remove(node);
		if (merged.isEmpty())
			merged = null; // free space
	}

	public boolean setSame(final Node node, final DependencySet ds)
	{
		if (isSame(node))
			return false;
		if (isDifferent(node))
		{
			//CHW - added for incremental reasoning support - this is needed as we will need to backjump if possible
			if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
				_abox.setClash(Clash.nominal(this, ds.union(this._mergeDepends, _abox.doExplanation()).union(node._mergeDepends, _abox.doExplanation()), node.getName()));
			else
				_abox.setClash(Clash.nominal(this, ds, node.getName()));

			return false;
		}

		mergedTo = node;
		_mergeDepends = ds.copy(_abox.getBranch());
		node.addMerged(this);
		return true;
	}

	public boolean isSame(final Node node)
	{
		return getSame().equals(node.getSame());
	}

	public boolean isDifferent(final Node node)
	{
		return _differents.containsKey(node);
	}

	public Set<Node> getDifferents()
	{
		return _differents.keySet();
	}

	public DependencySet getDifferenceDependency(final Node node)
	{
		return _differents.get(node);
	}

	public boolean setDifferent(final Node node, DependencySet ds)
	{

		// add to effected list
		if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_abox.getBranchEffectTracker().add(_abox.getBranch(), node.getName());

		if (isDifferent(node))
			return false;
		if (isSame(node))
		{
			ds = ds.union(this.getMergeDependency(true), _abox.doExplanation());
			ds = ds.union(node.getMergeDependency(true), _abox.doExplanation());
			_abox.setClash(Clash.nominal(this, ds, node.getName()));

			if (!ds.isIndependent())
				return false;
		}

		ds = ds.copy(_abox.getBranch());
		_differents.put(node, ds);
		node.setDifferent(this, ds);
		_abox.setChanged(true);
		return true;
	}

	public void inheritDifferents(final Node y, final DependencySet ds)
	{
		for (final Map.Entry<Node, DependencySet> entry : y._differents.entrySet())
		{
			final Node yDiff = entry.getKey();
			final DependencySet finalDS = ds.union(entry.getValue(), _abox.doExplanation());

			setDifferent(yDiff, finalDS);
		}
	}

	public ATermAppl getName()
	{
		return _name;
	}

	public abstract ATermAppl getTerm();

	public String getNameStr()
	{
		return _name.getName();
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(_name);
	}

	/**
	 * A string that identifies this _node either using its _name or the path of individuals that comes to this _node. For example, a _node that has been
	 * generated by the completion rules needs to be identified with respect to a named _individual. Ultimately, we need the shortest path or something like
	 * that but right now we just use the first inEdge
	 *
	 * @return
	 */
	public List<ATermAppl> getPath()
	{
		final LinkedList<ATermAppl> path = new LinkedList<>();

		if (isNamedIndividual())
			path.add(_name);
		else
		{
			final Set<Node> cycle = new HashSet<>();
			Node node = this;
			while (!node.getInEdges().isEmpty())
			{
				final Edge inEdge = node.getInEdges().edgeAt(0);
				node = inEdge.getFrom();
				if (cycle.contains(node))
					break;
				else
					cycle.add(node);
				path.addFirst(inEdge.getRole().getName());
				if (node.isNamedIndividual())
				{
					path.addFirst(node.getName());
					break;
				}
			}
		}

		return path;
	}

	/**
	 * getABox
	 *
	 * @return
	 */
	public ABox getABox()
	{
		return _abox;
	}
}
