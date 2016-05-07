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

package org.mindswap.pellet.tableau.completion;

import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.expressivity.Expressivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.blocking.BlockingFactory;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.cache.CacheSafety;
import org.mindswap.pellet.tableau.cache.CacheSafetyFactory;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.Timer;

/**
 * Completion _strategy for a SRIQ KB that does not have individuals in the ABox. When ABox is empty completion always starts with a single root individual that
 * represents the concept whose satisfiability is being searched.
 *
 * @author Evren Sirin
 */
public class EmptySRIQStrategy extends CompletionStrategy
{
	/**
	 * List of individuals that needs to be expanded by applying tableau completion rules
	 */
	private LinkedList<Individual> _mayNeedExpanding;

	/**
	 * Cached _mayNeedExpanding at a certain _branch that will be restored during backtracking
	 */
	private List<List<Individual>> _mnx;

	/**
	 * Nodes in the completion graph that re already being searched
	 */
	private Map<Individual, ATermAppl> _cachedNodes;

	/**
	 * Cache safety checker to decide if a cached satisfiability result can be reused for a given _node in the completion graph
	 */
	private CacheSafety _cacheSafety;

	//	private static int _cache = 0;
	//	private static int block = 0;

	public EmptySRIQStrategy(final ABox abox)
	{
		super(abox);
	}

	@Override
	public void initialize(final Expressivity expressivity)
	{
		_mergeList = new ArrayList<>();

		_cachedNodes = new HashMap<>();

		_mnx = new ArrayList<>();
		// add a null entry so Branch._branch _index will match with the _index in this array
		_mnx.add(null);

		assert _abox.size() == 1 : "This _strategy can only be used with originally empty ABoxes";

		blocking = BlockingFactory.createBlocking(expressivity);

		final Individual root = _abox.getIndIterator().next();
		applyUniversalRestrictions(root);
		_selfRule.apply(root);

		_mayNeedExpanding = new LinkedList<>();
		_mayNeedExpanding.add(root);

		_abox.setBranch(1);
		_abox.stats.treeDepth = 1;
		_abox.setChanged(true);
		_abox.setComplete(false);
		_abox.setInitialized(true);
	}

	@Override
	public void complete(final Expressivity expr)
	{
		if (log.isLoggable(Level.FINE))
			log.fine("************  " + EmptySRIQStrategy.class.getName() + "  ************");

		if (_abox.getNodes().isEmpty())
		{
			_abox.setComplete(true);
			return;
		}
		else
			if (_abox.getNodes().size() > 1)
				throw new RuntimeException("This _strategy can only be used with an ABox that has a single individual.");

		_cacheSafety = _abox.getCache().getSafety().canSupport(expr) ? _abox.getCache().getSafety() : CacheSafetyFactory.createCacheSafety(expr);

		initialize(expr);

		while (!_abox.isComplete() && !_abox.isClosed())
		{
			final Individual x = getNextIndividual();

			if (x == null)
			{
				_abox.setComplete(true);
				break;
			}

			if (log.isLoggable(Level.FINE))
			{
				log.fine("Starting with _node " + x);
				_abox.printTree();

				_abox.validate();
			}

			expand(x);

			if (_abox.isClosed())
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Clash at Branch (" + _abox.getBranch() + ") " + _abox.getClash());

				if (backtrack())
					_abox.setClash(null);
				else
					_abox.setComplete(true);
			}
			else
				if (expr.hasInverse() && parentNeedsExpanding(x))
				{
					_mayNeedExpanding.removeAll(getDescendants(x.getParent()));
					_mayNeedExpanding.addFirst(x.getParent());
					continue;
				}
		}

		if (log.isLoggable(Level.FINE))
			_abox.printTree();

		if (PelletOptions.USE_ADVANCED_CACHING)
			// if completion tree is clash free _cache all sat concepts
			if (!_abox.isClosed())
				for (final Iterator<Individual> i = new IndividualIterator(_abox); i.hasNext();)
				{
					final Individual ind = i.next();
					final ATermAppl c = _cachedNodes.get(ind);
					if (c == null)
						continue;

					addCacheSat(c);
				}
	}

	private List<Individual> getDescendants(final Individual ind)
	{
		final List<Individual> descendants = new ArrayList<>();
		getDescendants(ind, descendants);
		return descendants;
	}

	private void getDescendants(final Individual ind, final List<Individual> descendants)
	{
		descendants.add(ind);

		for (final Edge edge : ind.getOutEdges())
			if (edge.getTo().isIndividual() && !edge.getTo().equals(ind))
				getDescendants((Individual) edge.getTo(), descendants);
	}

	private void addCacheSat(final ATermAppl c)
	{
		if (!_abox.getCache().putSat(c, true))
			return;

		if (log.isLoggable(Level.FINEST))
			log.finest("+++ Cache sat concept " + c);

		if (ATermUtils.isAnd(c))
		{
			ATermList list = (ATermList) c.getArgument(0);
			for (; !list.isEmpty(); list = list.getNext())
				addCacheSat((ATermAppl) list.getFirst());
		}
	}

	private Individual getNextIndividual()
	{
		if (_mayNeedExpanding.isEmpty())
			return null;

		return _mayNeedExpanding.get(0);
	}

	private boolean parentNeedsExpanding(final Individual x)
	{
		if (x.isRoot())
			return false;

		final Individual parent = x.getParent();

		return parent.canApply(Node.ATOM) || parent.canApply(Node.OR) || parent.canApply(Node.SOME) || parent.canApply(Node.MIN) || parent.canApply(Node.MAX);
	}

	private void expand(final Individual x)
	{
		checkTimer();

		if (!_abox.doExplanation() && PelletOptions.USE_ADVANCED_CACHING)
		{
			final Timer t = _abox.getKB().timers.startTimer("_cache");
			final Bool cachedSat = isCachedSat(x);
			t.stop();
			if (cachedSat.isKnown())
			{
				if (cachedSat.isTrue())
				{
					if (log.isLoggable(Level.FINE))
						log.fine("Stop cached " + x);
					_mayNeedExpanding.remove(0);
				}
				else
				{
					// set the clash information to be the union of all types
					DependencySet ds = DependencySet.EMPTY;
					for (final ATermAppl c : x.getTypes())
					{
						ds = ds.union(x.getDepends(c), _abox.doExplanation());
					}
					_abox.setClash(Clash.atomic(x, ds));
				}
				return;
			}
		}

		do
		{
			if (blocking.isDirectlyBlocked(x))
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Stop _blocked " + x);
				_mayNeedExpanding.remove(0);
				return;
			}
			//			else if ( SubsetBlocking.getInstance().isDirectlyBlocked( x ) ) {
			//				System.err.println( "BLOCK " + ++block );
			//			}

			_unfoldingRule.apply(x);
			if (_abox.isClosed())
				return;

			_disjunctionRule.apply(x);
			if (_abox.isClosed())
				return;

			if (x.canApply(Node.ATOM) || x.canApply(Node.OR))
				continue;

			if (blocking.isDynamic() && blocking.isDirectlyBlocked(x))
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Stop _blocked " + x);
				_mayNeedExpanding.remove(0);
				return;
			}

			_someValuesRule.apply(x);
			if (_abox.isClosed())
				return;

			_minRule.apply(x);
			if (_abox.isClosed())
				return;

			// we don't have any inverse properties but we could have
			// domain restrictions which means we might have to re-apply
			// unfolding and _disjunction rules
			if (x.canApply(Node.ATOM) || x.canApply(Node.OR))
				continue;

			_chooseRule.apply(x);
			if (_abox.isClosed())
				return;

			_maxRule.apply(x);
			if (_abox.isClosed())
				return;

		} while (x.canApply(Node.ATOM) || x.canApply(Node.OR) || x.canApply(Node.SOME) || x.canApply(Node.MIN));

		_mayNeedExpanding.remove(0);

		final EdgeList sortedSuccessors = x.getOutEdges().sort();
		if (PelletOptions.SEARCH_TYPE == PelletOptions.DEPTH_FIRST)
			for (final Edge edge : sortedSuccessors)
			{
				final Node succ = edge.getTo();
				if (!succ.isLiteral() && !succ.equals(x))
					_mayNeedExpanding.add((Individual) succ);
			}
		else
			for (int i = sortedSuccessors.size() - 1; i >= 0; i--)
			{
				final Edge edge = sortedSuccessors.edgeAt(i);
				final Node succ = edge.getTo();
				if (!succ.isLiteral() && !succ.equals(x))
					_mayNeedExpanding.add((Individual) succ);
			}
	}

	private ATermAppl createConcept(final Individual x)
	{
		int count = 0;
		final ATermAppl[] terms = new ATermAppl[x.getTypes().size()];
		for (int t = 0; t < Node.TYPES; t++)
		{
			if (t == Node.NOM)
				continue;
			for (final ATermAppl c : x.getTypes(t))
			{
				if (c.equals(ATermUtils.TOP))
					continue;
				terms[count++] = c;
			}
		}

		switch (count)
		{
			case 0:
				return ATermUtils.TOP;
			case 1:
				return terms[0];
			default:
				return ATermUtils.makeAnd(ATermUtils.toSet(terms, count));
		}
	}

	private Bool isCachedSat(final Individual x)
	{
		if (x.isRoot())
			return Bool.UNKNOWN;

		final ATermAppl c = createConcept(x);

		Bool sat = isCachedSat(c);

		if (sat.isUnknown())
		{
			if (log.isLoggable(Level.FINEST))
				log.finest("??? Cache miss for " + c);
			_cachedNodes.put(x, c);
		}
		else
			if (!_cacheSafety.isSafe(c, x))
			{
				if (log.isLoggable(Level.FINER))
					log.finer("*** Cache unsafe for " + c);

				//			_cacheSafety.isSafe( c, x.getInEdges().edgeAt( 0 ).getRole(), x.getParent() );
				//			System.err.println( "CACHE " + ++_cache );

				sat = Bool.UNKNOWN;
			}
			else
				if (log.isLoggable(Level.FINER))
					log.finer("*** Cache hit for " + c + " sat = " + sat);

		return sat;
	}

	private Bool isCachedSat(final ATermAppl c)
	{
		Bool sat = _abox.getCachedSat(c);

		// return if we have the cached result or the class is not an intersection
		if (sat.isKnown() || !ATermUtils.isAnd(c))
			return sat;

		sat = null;

		// try to find the satisfiability of an intersection by inspecting the elements inside the conjunction
		ATermList list = (ATermList) c.getArgument(0);
		CachedNode cached1 = null;
		CachedNode cached2 = null;
		for (; !list.isEmpty(); list = list.getNext())
		{
			final ATermAppl d = (ATermAppl) list.getFirst();
			final CachedNode node = _abox.getCached(d);

			if (node == null || !node.isComplete())
			{
				// we don't have complete sat info about an element so give up
				sat = Bool.UNKNOWN;
				break;
			}
			else
				if (node.isBottom())
				{
					// an element is unsat so the intersection is unsat
					sat = Bool.FALSE;
					break;
				}
				else
					if (node.isTop())
					{
						// do nothing, intersection with TOP is redundant
					}
					else
						if (cached1 == null)
							cached1 = node;
						else
							if (cached2 == null)
								cached2 = node;
							else
							{
								// if there are more than two _nodes we cannot do mergability check so give up
								sat = Bool.UNKNOWN;
								break;
							}
		}

		// we can do mergability check
		if (sat == null)
			if (cached2 == null)
				// only one element in the intersection that is not TOP so the intersection is
				// satisfiable
				sat = Bool.TRUE;
			else
				// there are two classes in the intersection, so check if the cahed models can
				// be merged without a clash
				sat = _abox.getCache().isMergable(_abox.getKB(), cached1, cached2);

		if (sat.isKnown())
			_abox.getCache().putSat(c, sat.isTrue());

		return sat;
	}

	@Override
	public void restoreLocal(final Individual ind, final Branch br)
	{
		restore(br);
	}

	@Override
	public void restore(final Branch br)
	{
		final Timer timer = _timers.startTimer("restore");

		_abox.stats.globalRestores++;

		final Node clashNode = _abox.getClash().getNode();
		final List<ATermAppl> clashPath = clashNode.getPath();
		clashPath.add(clashNode.getName());

		_abox.setBranch(br.getBranch());
		_abox.setClash(null);
		// Setting the _anonCount to the value at the time of _branch creation is incorrect
		// when SMART_RESTORE option is turned on. If we create an anon _node after _branch
		// creation but _node depends on an earlier _branch restore operation will not remove
		// the _node. But setting _anonCount to a smaller number may mean the _anonCount will
		// be incremented to that value and creating a fresh anon _node will actually reuse
		// the not-removed _node. The only advantage of setting _anonCount to a smaller value
		// is to keep the name of anon _nodes smaller to make debugging easier. For this reason,
		// the above line is not removed and under special circumstances may be uncommented
		// to help debugging only with the intent that it will be commented again after
		// debugging is complete
		// _abox.setAnonCount( br.getAnonCount() );

		_mergeList.clear();

		final List<ATermAppl> nodeList = _abox.getNodeNames();

		if (log.isLoggable(Level.FINE))
		{
			log.fine("RESTORE: Branch " + br.getBranch());
			if (br.getNodeCount() < nodeList.size())
				log.fine("Remove _nodes " + nodeList.subList(br.getNodeCount(), nodeList.size()));
		}
		for (int i = 0; i < nodeList.size(); i++)
		{
			final ATermAppl x = nodeList.get(i);

			final Node node = _abox.getNode(x);
			if (i >= br.getNodeCount())
			{
				_abox.removeNode(x);
				final ATermAppl c = _cachedNodes.remove(node);
				if (c != null && PelletOptions.USE_ADVANCED_CACHING)
					if (clashPath.contains(x))
					{
						if (log.isLoggable(Level.FINEST))
							log.finest("+++ Cache unsat concept " + c);
						_abox.getCache().putSat(c, false);
					}
					else
						if (log.isLoggable(Level.FINEST))
							log.finest("--- Do not _cache concept " + c + " " + x + " " + clashNode + " " + clashPath);
			}
			else
			{
				node.restore(br.getBranch());

				// FIXME should we look at the clash path or clash _node
				if (node.equals(clashNode))
					_cachedNodes.remove(node);
			}
		}
		nodeList.subList(br.getNodeCount(), nodeList.size()).clear();

		for (final Iterator<Individual> i = _abox.getIndIterator(); i.hasNext();)
		{
			final Individual ind = i.next();
			_allValuesRule.apply(ind);
		}

		if (log.isLoggable(Level.FINE))
			_abox.printTree();

		timer.stop();
	}

	protected boolean backtrack()
	{
		boolean branchFound = false;

		_abox.stats.backtracks++;

		while (!branchFound)
		{
			_completionTimer.check();

			final int lastBranch = _abox.getClash().getDepends().max();

			if (lastBranch <= 0)
				return false;

			final List<Branch> branches = _abox.getBranches();
			_abox.stats.backjumps += (branches.size() - lastBranch);
			Branch newBranch = null;
			if (lastBranch <= branches.size())
			{
				branches.subList(lastBranch, branches.size()).clear();
				newBranch = branches.get(lastBranch - 1);

				if (log.isLoggable(Level.FINE))
					log.fine("JUMP: " + lastBranch);
				if (newBranch == null || lastBranch != newBranch.getBranch())
					throw new RuntimeException("Internal error in reasoner: Trying to backtrack _branch " + lastBranch + " but got " + newBranch);

				if (newBranch.getTryNext() < newBranch.getTryCount())
					newBranch.setLastClash(_abox.getClash().getDepends());

				newBranch.setTryNext(newBranch.getTryNext() + 1);

				if (newBranch.getTryNext() < newBranch.getTryCount())
				{
					restore(newBranch);

					branchFound = newBranch.tryNext();
				}
			}

			if (!branchFound || newBranch == null)
			{
				_abox.getClash().getDepends().remove(lastBranch);
				if (log.isLoggable(Level.FINE))
					log.fine("FAIL: " + lastBranch);
			}
			else
			{
				// create another copy of the _mnx list here because we may backtrack to the same
				// _branch multiple times and we want the same copy to be available every time
				_mayNeedExpanding = new LinkedList<>(_mnx.get(newBranch.getBranch()));
				_mnx.subList(newBranch.getBranch() + 1, _mnx.size()).clear();
				if (log.isLoggable(Level.FINE))
					log.fine("MNX : " + _mayNeedExpanding);
			}

		}

		_abox.validate();

		return branchFound;
	}

	@Override
	public void addBranch(final Branch newBranch)
	{
		super.addBranch(newBranch);

		assert _mnx.size() == newBranch.getBranch() : _mnx.size() + " != " + newBranch.getBranch();

		// create a copy of the _mnx list so that changes in the _current _branch will not affect it
		_mnx.add(new ArrayList<>(_mayNeedExpanding));
	}
}
