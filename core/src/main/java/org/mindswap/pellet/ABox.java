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

import static java.lang.String.format;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.BranchEffectTracker;
import com.clarkparsia.pellet.IncrementalChangeTracker;
import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.DatatypeReasonerImpl;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.impl.SimpleBranchEffectTracker;
import com.clarkparsia.pellet.impl.SimpleIncrementalChangeTracker;
import com.clarkparsia.pellet.utils.MultiMapUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.tableau.cache.CachedNodeFactory;
import org.mindswap.pellet.tableau.cache.ConceptCache;
import org.mindswap.pellet.tableau.cache.ConceptCacheLRU;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.SROIQIncStrategy;
import org.mindswap.pellet.tableau.completion.queue.BasicCompletionQueue;
import org.mindswap.pellet.tableau.completion.queue.CompletionQueue;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.OptimizedBasicCompletionQueue;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.CandidateSet;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.fsm.State;
import org.mindswap.pellet.utils.fsm.Transition;
import org.mindswap.pellet.utils.fsm.TransitionGraph;
import org.mindswap.pellet.utils.iterator.MultiListIterator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class ABox
{
	public final static Logger log = Logger.getLogger(ABox.class.getName());

	// following two variables are used to generate names
	// for newly generated individuals. so during rules are
	// applied anon1, anon2, etc. will be generated. This prefix
	// will also make sure that any _node whose name starts with
	// this prefix is not a root _node
	private int _anonCount = 0;

	public ABoxStats stats = new ABoxStats();

	/**
	 * datatype reasoner used for checking the satisfiability of datatypes
	 */
	protected final DatatypeReasoner _dtReasoner;

	/**
	 * This is a list of _nodes. Each _node has a name expressed as an ATerm which is used as the key in the Hashtable. The value is the actual _node object
	 */
	protected Map<ATermAppl, Node> _nodes;

	/**
	 * This is a list of _node names. This list stores the individuals in the _order they are created
	 */
	protected List<ATermAppl> _nodeList;

	/**
	 * Indicates if any of the completion rules has been applied to modify ABox
	 */
	private boolean _changed = false;

	private boolean _doExplanation;

	// cached satisfiability results
	// the table maps every atomic concept A (and also its negation not(A))
	// to the root _node of its completed tree. If a concept is mapped to
	// null value it means it is not satisfiable
	protected ConceptCache _cache;

	// pseudo model for this Abox. This is the ABox that results from
	// completing to the original Abox
	// private ABox pseudoModel;

	// _cache of the last completion. it may be different from the pseudo
	// model, e.g. type checking for individual adds one extra assertion
	// last completion is stored for caching the root _nodes that was
	// the result of
	private ABox _lastCompletion;
	private boolean _keepLastCompletion;
	private Clash _lastClash;

	// complete ABox means no more tableau rules are applicable
	private boolean _isComplete = false;

	// the last _clash recorded
	private Clash _clash;

	private final Set<Clash> _assertedClashes;

	// the _current _branch number
	private int _branch;
	private List<Branch> _branches;

	private List<NodeMerge> _toBeMerged;

	private Map<ATermAppl, int[]> _disjBranchStats;

	// if we are using copy on write, this is where to copy from
	private ABox _sourceABox;

	// return true if init() function is called. This indicates parsing
	// is completed and ABox is ready for completion
	private boolean _initialized = false;

	// The KB to which this ABox belongs
	private final KnowledgeBase _kb;

	public boolean _rulesNotApplied;

	public boolean _ranRete = false;
	public boolean _useRete = false;

	private BranchEffectTracker _branchEffects;
	private CompletionQueue _completionQueue;
	private IncrementalChangeTracker _incChangeTracker;

	// flag set when incrementally updating the _abox with explicit assertions
	private boolean _syntacticUpdate = false;

	public ABox(final KnowledgeBase kb)
	{
		this._kb = kb;
		_nodes = new HashMap<>();
		_nodeList = new ArrayList<>();
		_clash = null;
		_assertedClashes = new HashSet<>();
		_doExplanation = false;
		_dtReasoner = new DatatypeReasonerImpl();
		_keepLastCompletion = false;

		setBranch(DependencySet.NO_BRANCH);
		_branches = new ArrayList<>();
		setDisjBranchStats(new HashMap<ATermAppl, int[]>());

		_toBeMerged = new ArrayList<>();
		_rulesNotApplied = true;

		if (PelletOptions.TRACK_BRANCH_EFFECTS)
			_branchEffects = new SimpleBranchEffectTracker();
		else
			_branchEffects = null;

		if (PelletOptions.USE_COMPLETION_QUEUE)
		{
			if (PelletOptions.USE_OPTIMIZED_BASIC_COMPLETION_QUEUE)
				_completionQueue = new OptimizedBasicCompletionQueue(this);
			else
				_completionQueue = new BasicCompletionQueue(this);
		}
		else
			_completionQueue = null;

		if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
			_incChangeTracker = new SimpleIncrementalChangeTracker();
		else
			_incChangeTracker = null;
	}

	public ABox(final KnowledgeBase kb, final ABox abox, final ATermAppl extraIndividual, final boolean copyIndividuals)
	{
		this._kb = kb;
		final Timer timer = kb.timers.startTimer("cloneABox");

		this._rulesNotApplied = true;
		_initialized = abox._initialized;
		setChanged(abox.isChanged());
		setAnonCount(abox.getAnonCount());
		_cache = abox._cache;
		_clash = abox._clash;
		_dtReasoner = abox._dtReasoner;
		_doExplanation = abox._doExplanation;
		setDisjBranchStats(abox.getDisjBranchStats());

		final int extra = (extraIndividual == null) ? 0 : 1;
		final int nodeCount = extra + (copyIndividuals ? abox._nodes.size() : 0);

		_nodes = new HashMap<>(nodeCount);
		_nodeList = new ArrayList<>(nodeCount);

		if (PelletOptions.TRACK_BRANCH_EFFECTS)
		{
			if (copyIndividuals)
				_branchEffects = abox._branchEffects.copy();
			else
				_branchEffects = new SimpleBranchEffectTracker();
		}
		else
			_branchEffects = null;

		// copy the _queue - this must be done early so that the effects of
		// adding the extra individual do not get removed
		if (PelletOptions.USE_COMPLETION_QUEUE)
		{
			if (copyIndividuals)
			{
				_completionQueue = abox._completionQueue.copy();
				_completionQueue.setABox(this);
			}
			else
				if (PelletOptions.USE_OPTIMIZED_BASIC_COMPLETION_QUEUE)
					_completionQueue = new OptimizedBasicCompletionQueue(this);
				else
					_completionQueue = new BasicCompletionQueue(this);
		}
		else
			_completionQueue = null;

		if (extraIndividual != null)
		{
			final Individual n = new Individual(extraIndividual, this, null);
			n.setNominalLevel(Node.BLOCKABLE);
			n.setConceptRoot(true);
			n.addType(ATermUtils.TOP, DependencySet.INDEPENDENT);
			_nodes.put(extraIndividual, n);
			_nodeList.add(extraIndividual);

			if (PelletOptions.COPY_ON_WRITE)
				_sourceABox = abox;
		}

		if (copyIndividuals)
		{
			_toBeMerged = abox.getToBeMerged();
			if (_sourceABox == null)
			{
				for (int i = 0; i < nodeCount - extra; i++)
				{
					final ATermAppl x = abox._nodeList.get(i);
					final Node node = abox.getNode(x);
					final Node copy = node.copyTo(this);

					_nodes.put(x, copy);
					_nodeList.add(x);
				}

				for (final Node node : _nodes.values())
					node.updateNodeReferences();
			}
		}
		else
		{
			_toBeMerged = Collections.emptyList();
			_sourceABox = null;
			_initialized = false;
		}

		// Copy of the _incChangeTracker looks up _nodes in the new ABox, so this
		// copy must follow _node copying
		if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
		{
			if (copyIndividuals)
				_incChangeTracker = abox._incChangeTracker.copy(this);
			else
				_incChangeTracker = new SimpleIncrementalChangeTracker();
		}
		else
			_incChangeTracker = null;

		_assertedClashes = new HashSet<>();
		for (final Clash clash : abox._assertedClashes)
			_assertedClashes.add(clash.copyTo(this));

		if (extraIndividual == null || copyIndividuals)
		{
			setBranch(abox._branch);
			_branches = new ArrayList<>(abox._branches.size());
			for (int i = 0, n = abox._branches.size(); i < n; i++)
			{
				final Branch branch = abox._branches.get(i);
				Branch copy;

				if (_sourceABox == null)
				{
					copy = branch.copyTo(this);
					copy.setNodeCount(branch.getNodeCount() + extra);
				}
				else
					copy = branch;
				_branches.add(copy);
			}
		}
		else
		{
			setBranch(DependencySet.NO_BRANCH);
			_branches = new ArrayList<>();
		}

		timer.stop();

	}

	/**
	 * Create a copy of this ABox with all the _nodes and edges.
	 *
	 * @return
	 */
	public ABox copy()
	{
		return copy(_kb);
	}

	/**
	 * Create a copy of this ABox with all the _nodes and edges and the given KB.
	 */
	public ABox copy(final KnowledgeBase kb)
	{
		return new ABox(kb, this, null, true);
	}

	/**
	 * Create a copy of this ABox with one more additional individual. This is <b>NOT</b> equivalent to create a copy and then add the individual. The _order of
	 * individuals in the ABox is important to figure out which individuals exist in the original ontology and which ones are created by the tableau algorithm.
	 * This function creates a new ABox such that the individual is supposed to exist in the original ontology. This is very important when satisfiability of a
	 * concept starts with a pesudo model rather than the initial ABox.
	 *
	 * @param extraIndividual Extra individual to be added to the copy ABox
	 * @return
	 */
	public ABox copy(final ATermAppl extraIndividual, final boolean copyIndividuals)
	{
		return new ABox(_kb, this, extraIndividual, copyIndividuals);
	}

	public void copyOnWrite()
	{
		if (_sourceABox == null)
			return;

		final Timer t = _kb.timers.startTimer("copyOnWrite");

		final List<ATermAppl> currentNodeList = new ArrayList<>(_nodeList);
		final int currentSize = currentNodeList.size();
		final int nodeCount = _sourceABox._nodes.size();

		_nodeList = new ArrayList<>(nodeCount + 1);
		_nodeList.add(currentNodeList.get(0));

		for (int i = 0; i < nodeCount; i++)
		{
			final ATermAppl x = _sourceABox._nodeList.get(i);
			final Node node = _sourceABox.getNode(x);
			final Node copyNode = node.copyTo(this);
			_nodes.put(x, copyNode);
			_nodeList.add(x);
		}

		if (currentSize > 1)
			_nodeList.addAll(currentNodeList.subList(1, currentSize));

		for (final Node node : _nodes.values())
		{
			if (_sourceABox._nodes.containsKey(node.getName()))
				node.updateNodeReferences();
		}

		for (int i = 0, n = _branches.size(); i < n; i++)
		{
			final Branch branch = _branches.get(i);
			final Branch copy = branch.copyTo(this);
			_branches.set(i, copy);

			if (i >= _sourceABox.getBranches().size())
				copy.setNodeCount(copy.getNodeCount() + nodeCount);
			else
				copy.setNodeCount(copy.getNodeCount() + 1);
		}

		t.stop();

		_sourceABox = null;
	}

	/**
	 * Clear the pseudo model created for the ABox and concept satisfiability.
	 *
	 * @param clearSatCache If true clear concept satisfiability _cache, if false only clear pseudo model.
	 */
	public void clearCaches(final boolean clearSatCache)
	{
		_lastCompletion = null;

		if (clearSatCache)
			_cache = new ConceptCacheLRU(_kb);
	}

	public Bool getCachedSat(final ATermAppl c)
	{
		return _cache.getSat(c);
	}

	public ConceptCache getCache()
	{
		return _cache;
	}

	public CachedNode getCached(final ATermAppl c)
	{
		if (ATermUtils.isNominal(c))
			return getIndividual(c.getArgument(0)).getSame();
		else
			return _cache.get(c);
	}

	private void cache(final Individual rootNode, final ATermAppl c, final boolean isConsistent)
	{

		if (!isConsistent)
		{
			if (log.isLoggable(Level.FINE))
			{
				log.fine("Unsatisfiable: " + ATermUtils.toString(c));
				log.fine("Equivalent to TOP: " + ATermUtils.toString(ATermUtils.negate(c)));
			}

			_cache.putSat(c, false);
		}
		else
		{

			if (log.isLoggable(Level.FINE))
				log.fine("Cache " + rootNode.debugString());

			_cache.put(c, CachedNodeFactory.createNode(c, rootNode));

			//			System.err.println( c + " " + rootNode.debugString() );
		}
	}

	public Bool isKnownSubClassOf(final ATermAppl c1, final ATermAppl c2)
	{
		Bool isSubClassOf = Bool.UNKNOWN;
		final CachedNode cached = getCached(c1);
		if (cached != null)
			isSubClassOf = isType(cached, c2);

		return isSubClassOf;
	}

	public boolean isSubClassOf(final ATermAppl c1, final ATermAppl c2)
	{
		if (!_doExplanation)
		{
			final Bool isKnownSubClass = isKnownSubClassOf(c1, c2);
			if (isKnownSubClass.isKnown())
				return isKnownSubClass.isTrue();
		}

		if (log.isLoggable(Level.FINE))
		{
			final long count = _kb.timers.getTimer("subClassSat") == null ? 0 : _kb.timers.getTimer("subClassSat").getCount();
			log.fine(count + ") Checking subclass [" + ATermUtils.toString(c1) + " " + ATermUtils.toString(c2) + "]");
		}

		final ATermAppl notC2 = ATermUtils.negate(c2);
		final ATermAppl c = ATermUtils.makeAnd(c1, notC2);
		final Timer t = _kb.timers.startTimer("subClassSat");
		final boolean sub = !isSatisfiable(c, false);
		t.stop();

		if (log.isLoggable(Level.FINE))
			log.fine(" Result: " + sub + " (" + t.getLast() + "ms)");

		return sub;
	}

	public boolean isSatisfiable(final ATermAppl c)
	{
		final boolean cacheModel = PelletOptions.USE_CACHING && (ATermUtils.isPrimitiveOrNegated(c) || PelletOptions.USE_ADVANCED_CACHING);
		return isSatisfiable(c, cacheModel);
	}

	public boolean isSatisfiable(ATermAppl c, final boolean cacheModel)
	{
		c = ATermUtils.normalize(c);

		// if normalization revealed an obvious unsatisfiability, return
		// immediately
		if (c.equals(ATermUtils.BOTTOM))
		{
			_lastClash = Clash.unexplained(null, DependencySet.INDEPENDENT, "Obvious contradiction in class expression: " + ATermUtils.toString(c));
			return false;
		}

		if (log.isLoggable(Level.FINE))
			log.fine("Satisfiability for " + ATermUtils.toString(c));

		if (cacheModel)
		{
			final CachedNode cached = getCached(c);
			if (cached != null)
			{
				final boolean satisfiable = !cached.isBottom();
				final boolean needToCacheModel = cacheModel && !cached.isComplete();
				if (log.isLoggable(Level.FINE))
					log.fine("Cached sat for " + ATermUtils.toString(c) + " is " + satisfiable);
				// if clashExplanation is enabled we should actually build the
				// tableau again to generate the _clash. we don't _cache the
				// clashExplanation up front because generating clashExplanation is costly
				// and we only want to do it when explicitly asked note that
				// when the concepts is satisfiable there is no clashExplanation to
				// be generated so we return the result immediately
				if (!needToCacheModel && (satisfiable || !_doExplanation))
					return satisfiable;
			}
		}

		stats.satisfiabilityCount++;

		final Timer t = _kb.timers.startTimer("satisfiability");
		final boolean isSat = isConsistent(SetUtils.<ATermAppl> emptySet(), c, cacheModel);
		t.stop();

		return isSat;
	}

	public CandidateSet<ATermAppl> getObviousInstances(final ATermAppl c)
	{
		return getObviousInstances(c, _kb.getIndividuals());
	}

	public CandidateSet<ATermAppl> getObviousInstances(ATermAppl c, final Collection<ATermAppl> individuals)
	{
		c = ATermUtils.normalize(c);
		final Set<ATermAppl> subs = (_kb.isClassified() && _kb.getTaxonomy().contains(c)) ? _kb.getTaxonomy().getFlattenedSubs(c, false) : Collections.<ATermAppl> emptySet();
		subs.remove(ATermUtils.BOTTOM);

		final CandidateSet<ATermAppl> cs = new CandidateSet<>();
		for (final ATermAppl x : individuals)
		{
			final Bool isType = isKnownType(x, c, subs);
			cs.add(x, isType);
		}

		return cs;
	}

	public void getObviousTypes(final ATermAppl x, final List<ATermAppl> types, final List<ATermAppl> nonTypes)
	{
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual pNode = getIndividual(x);
		if (!pNode.getMergeDependency(true).isIndependent())
			pNode = getIndividual(x);
		else
			pNode = pNode.getSame();

		pNode.getObviousTypes(types, nonTypes);
	}

	public CandidateSet<ATermAppl> getObviousSubjects(final ATermAppl p, final ATermAppl o)
	{
		final CandidateSet<ATermAppl> candidates = new CandidateSet<>(_kb.getIndividuals());
		getObviousSubjects(p, o, candidates);

		return candidates;
	}

	public void getSubjects(final ATermAppl p, final ATermAppl o, final CandidateSet<ATermAppl> candidates)
	{
		final Iterator<ATermAppl> i = candidates.iterator();
		while (i.hasNext())
		{
			final ATermAppl s = i.next();

			final Bool hasObviousValue = hasObviousPropertyValue(s, p, o);
			candidates.update(s, hasObviousValue);
		}
	}

	public void getObviousSubjects(final ATermAppl p, final ATermAppl o, final CandidateSet<ATermAppl> candidates)
	{
		final Iterator<ATermAppl> i = candidates.iterator();
		while (i.hasNext())
		{
			final ATermAppl s = i.next();

			final Bool hasObviousValue = hasObviousPropertyValue(s, p, o);
			if (hasObviousValue.isFalse())
				i.remove();
			else
				candidates.update(s, hasObviousValue);
		}
	}

	public void getObviousObjects(ATermAppl p, final CandidateSet<ATermAppl> candidates)
	{
		p = getRole(p).getInverse().getName();
		final Iterator<ATermAppl> i = candidates.iterator();
		while (i.hasNext())
		{
			final ATermAppl s = i.next();

			final Bool hasObviousValue = hasObviousObjectPropertyValue(s, p, null);
			candidates.update(s, hasObviousValue);
		}
	}

	public Bool isKnownType(final ATermAppl x, final ATermAppl c)
	{
		return isKnownType(x, c, SetUtils.<ATermAppl> emptySet());
	}

	public Bool isKnownType(final ATermAppl x, final ATermAppl c, final Collection<ATermAppl> subs)
	{
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual pNode = getIndividual(x);

		boolean isIndependent = true;
		if (pNode.isMerged())
		{
			isIndependent = pNode.getMergeDependency(true).isIndependent();
			pNode = pNode.getSame();
		}

		final Bool isType = isKnownType(pNode, c, subs);

		if (isIndependent)
			return isType;
		else
			if (isType.isTrue())
				return Bool.UNKNOWN;
			else
				return isType;
	}

	public Bool isKnownType(final Individual pNode, final ATermAppl concept, final Collection<ATermAppl> subs)
	{
		// Timer t = _kb.timers.startTimer( "isKnownType" );
		Bool isType = isType(pNode, concept);
		if (isType.isUnknown())
		{
			final Set<ATermAppl> concepts = ATermUtils.isAnd(concept) ? ATermUtils.listToSet((ATermList) concept.getArgument(0)) : SetUtils.singleton(concept);

			isType = Bool.TRUE;
			for (final ATermAppl c : concepts)
			{
				Bool type = pNode.hasObviousType(c);

				if (type.isUnknown() && pNode.hasObviousType(subs))
					type = Bool.TRUE;

				if (type.isKnown())
					isType = isType.and(type);
				else
				{
					isType = Bool.UNKNOWN;

					//					boolean justSC = true;

					final Collection<ATermAppl> axioms = _kb.getTBox().getAxioms(c);
					LOOP: for (final ATermAppl axiom : axioms)
					{
						ATermAppl term = (ATermAppl) axiom.getArgument(1);

						//						final AFun afun = axiom.getAFun();
						//
						//						if( !afun.equals( ATermUtils.SUBFUN ) ) {
						//							justSC = false;
						//						}

						final boolean equivalent = axiom.getAFun().equals(ATermUtils.EQCLASSFUN);
						if (equivalent)
						{
							final Iterator<ATermAppl> i = ATermUtils.isAnd(term) ? new MultiListIterator((ATermList) term.getArgument(0)) : Collections.singleton(term).iterator();
							Bool knownType = Bool.TRUE;
							while (i.hasNext() && knownType.isTrue())
							{
								term = i.next();
								knownType = isKnownType(pNode, term, SetUtils.<ATermAppl> emptySet());
							}
							if (knownType.isTrue())
							{
								isType = Bool.TRUE;
								break LOOP;
							}
						}
					}

					// TODO following short-cut might be implemented correctly
					// the main problem here is that concept might be in the
					// types of the individual with a dependency. In this case,
					// Node.hasObviousType returns unknown and changing it to
					// false here is wrong.
					//					 if( justSC && ATermUtils.isPrimitive( c ) ) {
					//						return Bool.FALSE;
					//					}

					if (isType.isUnknown())
						return Bool.UNKNOWN;
				}
			}
		}
		// t.stop();

		return isType;
	}

	private Bool isType(final CachedNode pNode, final ATermAppl c)
	{
		Bool isType = Bool.UNKNOWN;

		final boolean isPrimitive = _kb.getTBox().isPrimitive(c);

		if (isPrimitive && !pNode.isTop() && !pNode.isBottom() && pNode.isComplete())
		{
			final DependencySet ds = pNode.getDepends().get(c);
			if (ds == null)
				return Bool.FALSE;
			else
				if (ds.isIndependent() && pNode.isIndependent())
					return Bool.TRUE;
		}

		final ATermAppl notC = ATermUtils.negate(c);
		final CachedNode cached = getCached(notC);
		if (cached != null && cached.isComplete())
			isType = _cache.isMergable(_kb, pNode, cached).not();

		if (PelletOptions.CHECK_NOMINAL_EDGES && isType.isUnknown())
		{
			final CachedNode cNode = getCached(c);
			if (cNode != null)
				isType = _cache.checkNominalEdges(_kb, pNode, cNode);
		}

		return isType;
	}

	public boolean isSameAs(final ATermAppl ind1, final ATermAppl ind2)
	{
		final ATermAppl c = ATermUtils.makeValue(ind2);

		return isType(ind1, c);
	}

	/**
	 * Returns true if individual x belongs to type c. This is a logical consequence of the KB if in all possible models x belongs to C. This is checked by
	 * trying to construct a model where x belongs to not(c).
	 *
	 * @param x
	 * @param c
	 * @return
	 */
	public boolean isType(final ATermAppl x, ATermAppl c)
	{
		c = ATermUtils.normalize(c);

		if (!doExplanation())
		{
			Set<ATermAppl> subs;
			if (_kb.isClassified() && _kb.getTaxonomy().contains(c))
			{
				subs = _kb.getTaxonomy().getFlattenedSubs(c, false);
				subs.remove(ATermUtils.BOTTOM);
			}
			else
				subs = SetUtils.emptySet();

			final Bool type = isKnownType(x, c, subs);
			if (type.isKnown())
				return type.isTrue();
		}
		// List list = (List) _kb.instances.get( c );
		// if( list != null )
		// return list.contains( x );

		if (log.isLoggable(Level.FINE))
			log.fine("Checking type " + ATermUtils.toString(c) + " for individual " + ATermUtils.toString(x));

		final ATermAppl notC = ATermUtils.negate(c);

		final Timer t = _kb.timers.startTimer("isType");
		final boolean isType = !isConsistent(SetUtils.singleton(x), notC, false);
		t.stop();

		if (log.isLoggable(Level.FINE))
			log.fine("Type " + isType + " " + ATermUtils.toString(c) + " for individual " + ATermUtils.toString(x));

		return isType;
	}

	/**
	 * Returns true if any of the individuals in the given list belongs to type c.
	 *
	 * @param c
	 * @param inds
	 * @return
	 */
	public boolean isType(final List<ATermAppl> inds, ATermAppl c)
	{
		c = ATermUtils.normalize(c);

		if (log.isLoggable(Level.FINE))
			log.fine("Checking type " + ATermUtils.toString(c) + " for individuals " + inds.size());

		final ATermAppl notC = ATermUtils.negate(c);

		final boolean isType = !isConsistent(inds, notC, false);

		if (log.isLoggable(Level.FINE))
			log.fine("Type " + isType + " " + ATermUtils.toString(c) + " for individuals " + inds.size());

		return isType;
	}

	public Bool hasObviousPropertyValue(final ATermAppl s, final ATermAppl p, final ATermAppl o)
	{
		final Role prop = getRole(p);

		if (prop.isDatatypeRole())
			try
			{
				final Object value = (o == null) ? null : _dtReasoner.getValue(o);
				return hasObviousDataPropertyValue(s, p, value);
			}
			catch (final UnrecognizedDatatypeException e)
			{
				log.warning(format("Returning false for property value check (%s,%s,%s) due to datatype problem with input literal: %s", s, p, o, e.getMessage()));
				return Bool.FALSE;
			}
			catch (final InvalidLiteralException e)
			{
				log.warning(format("Returning false for property value check (%s,%s,%s) due to problem with input literal: %s", s, p, o, e.getMessage()));
				return Bool.FALSE;
			}
		else
			return hasObviousObjectPropertyValue(s, p, o);
	}

	public Bool hasObviousDataPropertyValue(final ATermAppl s, final ATermAppl p, final Object value)
	{
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual subj = getIndividual(s);
		final Role prop = getRole(p);

		if (prop.isTop())
			return Bool.TRUE;
		else
			if (prop.isBottom())
				return Bool.FALSE;

		// if onlyPositive is set then the answer returned is sound but not
		// complete so we cannot return negative answers
		boolean onlyPositive = false;

		if (!subj.getMergeDependency(true).isIndependent())
		{
			onlyPositive = true;
			subj = getIndividual(s);
		}
		else
			subj = subj.getSame();

		final Bool hasValue = subj.hasDataPropertyValue(prop, value);
		if (onlyPositive && hasValue.isFalse())
			return Bool.UNKNOWN;

		return hasValue;
	}

	public Bool hasObviousObjectPropertyValue(final ATermAppl s, final ATermAppl p, final ATermAppl o)
	{
		final Role prop = getRole(p);

		if (prop.isTop())
			return Bool.TRUE;
		else
			if (prop.isBottom())
				return Bool.FALSE;

		final Set<ATermAppl> knowns = new HashSet<>();
		final Set<ATermAppl> unknowns = new HashSet<>();

		getObjectPropertyValues(s, prop, knowns, unknowns, true);

		if (o == null)
		{
			if (!knowns.isEmpty())
				return Bool.TRUE;
			else
				if (!unknowns.isEmpty())
					return Bool.UNKNOWN;
				else
					return Bool.FALSE;
		}
		else
			if (knowns.contains(o))
				return Bool.TRUE;
			else
				if (unknowns.contains(o))
					return Bool.UNKNOWN;
				else
					return Bool.FALSE;
	}

	public boolean hasPropertyValue(final ATermAppl s, final ATermAppl p, final ATermAppl o)
	{
		final Bool hasObviousValue = hasObviousPropertyValue(s, p, o);
		if (hasObviousValue.isKnown())
			if (hasObviousValue.isFalse() || !doExplanation())
				return hasObviousValue.isTrue();

		ATermAppl c = null;
		if (o == null)
		{
			if (_kb.isDatatypeProperty(p))
				c = ATermUtils.makeMin(p, 1, ATermUtils.TOP_LIT);
			else
				c = ATermUtils.makeMin(p, 1, ATermUtils.TOP);
		}
		else
			c = ATermUtils.makeHasValue(p, o);

		final boolean isType = isType(s, c);

		return isType;
	}

	public List<ATermAppl> getDataPropertyValues(final ATermAppl s, final Role role, final ATermAppl datatype)
	{
		return getDataPropertyValues(s, role, datatype, false);
	}

	public List<ATermAppl> getDataPropertyValues(final ATermAppl s, final Role role, final ATermAppl datatype, final boolean onlyObvious)
	{
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual subj = getIndividual(s);

		final List<ATermAppl> values = new ArrayList<>();

		boolean isIndependent = true;
		if (subj.isMerged())
		{
			isIndependent = subj.getMergeDependency(true).isIndependent();
			subj = subj.getSame();
		}

		final EdgeList edges = subj.getRSuccessorEdges(role);
		for (int i = 0; i < edges.size(); i++)
		{
			final Edge edge = edges.edgeAt(i);
			final DependencySet ds = edge.getDepends();
			final Literal literal = (Literal) edge.getTo();
			final ATermAppl literalValue = literal.getTerm();
			if (literalValue != null)
			{
				if (datatype != null)
					if (!literal.hasType(datatype))
						try
						{
							if (!_dtReasoner.isSatisfiable(Collections.singleton(datatype), literal.getValue()))
								continue;
						}
						catch (final DatatypeReasonerException e)
						{
							final String msg = format("Unexpected datatype reasoner exception while fetching property values (%s,%s,%s): %s", s, role, datatype, e.getMessage());
							log.severe(msg);
							throw new InternalReasonerException(msg);
						}

				if (isIndependent && ds.isIndependent())
					values.add(literalValue);
				else
					if (!onlyObvious)
					{
						final ATermAppl hasValue = ATermUtils.makeHasValue(role.getName(), literalValue);
						if (isType(s, hasValue))
							values.add(literalValue);
					}
			}
		}

		return values;
	}

	public List<ATermAppl> getObviousDataPropertyValues(final ATermAppl s, final Role prop, final ATermAppl datatype)
	{
		return getDataPropertyValues(s, prop, datatype, true);
	}

	public void getObjectPropertyValues(final ATermAppl s, final Role role, final Set<ATermAppl> knowns, final Set<ATermAppl> unknowns, final boolean getSames)
	{
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual subj = getIndividual(s);

		boolean isIndependent = true;
		if (subj.isMerged())
		{
			isIndependent = subj.getMergeDependency(true).isIndependent();
			subj = subj.getSame();
		}

		if (role.isSimple())
			getSimpleObjectPropertyValues(subj, role, knowns, unknowns, getSames);
		else
			if (!role.hasComplexSubRole())
				getTransitivePropertyValues(subj, role, knowns, unknowns, getSames, new HashMap<Individual, Set<Role>>(), true);
			else
			{
				final TransitionGraph<Role> tg = role.getFSM();
				getComplexObjectPropertyValues(subj, tg.getInitialState(), tg, knowns, unknowns, getSames, new HashMap<Individual, Set<State<Role>>>(), true);
			}

		if (!isIndependent)
		{
			unknowns.addAll(knowns);
			knowns.clear();
		}
	}

	void getSimpleObjectPropertyValues(final Individual subj, final Role role, final Set<ATermAppl> knowns, final Set<ATermAppl> unknowns, final boolean getSames)
	{
		final EdgeList edges = subj.getRNeighborEdges(role);
		for (int i = 0; i < edges.size(); i++)
		{
			final Edge edge = edges.edgeAt(i);
			final DependencySet ds = edge.getDepends();
			final Individual value = (Individual) edge.getNeighbor(subj);

			if (value.isRootNominal())
				if (ds.isIndependent())
				{
					if (getSames)
						getSames(value, knowns, unknowns);
					else
						knowns.add(value.getName());
				}
				else
					if (getSames)
						getSames(value, unknowns, unknowns);
					else
						unknowns.add(value.getName());
		}
	}

	void getTransitivePropertyValues(final Individual subj, final Role prop, final Set<ATermAppl> knowns, final Set<ATermAppl> unknowns, final boolean getSames, final Map<Individual, Set<Role>> visited, final boolean isIndependent)
	{
		if (!MultiMapUtils.addAll(visited, subj, prop.getSubRoles()))
			return;

		final EdgeList edges = subj.getRNeighborEdges(prop);
		for (int i = 0; i < edges.size(); i++)
		{
			final Edge edge = edges.edgeAt(i);
			final DependencySet ds = edge.getDepends();
			final Individual value = (Individual) edge.getNeighbor(subj);
			final Role edgeRole = edge.getFrom().equals(subj) ? edge.getRole() : edge.getRole().getInverse();
			if (value.isRootNominal())
				if (isIndependent && ds.isIndependent())
				{
					if (getSames)
						getSames(value, knowns, unknowns);
					else
						knowns.add(value.getName());
				}
				else
					if (getSames)
						getSames(value, unknowns, unknowns);
					else
						unknowns.add(value.getName());

			if (!prop.isSimple())
			{
				// all the following roles might cause this property to
				// propagate
				final Set<Role> transRoles = SetUtils.intersection(edgeRole.getSuperRoles(), prop.getTransitiveSubRoles());
				for (final Role transRole : transRoles)
				{
					getTransitivePropertyValues(value, transRole, knowns, unknowns, getSames, visited, isIndependent && ds.isIndependent());
				}
			}
		}
	}

	void getComplexObjectPropertyValues(final Individual subj, final State<Role> st, final TransitionGraph<Role> tg, final Set<ATermAppl> knowns, final Set<ATermAppl> unknowns, final boolean getSames, final HashMap<Individual, Set<State<Role>>> visited, final boolean isIndependent)
	{
		if (!MultiMapUtils.add(visited, subj, st))
			return;

		if (tg.isFinal(st) && subj.isRootNominal())
		{
			log.fine("add " + subj);
			if (isIndependent)
			{
				if (getSames)
					getSames(subj, knowns, unknowns);
				else
					knowns.add(subj.getName());
			}
			else
				if (getSames)
					getSames(subj, unknowns, unknowns);
				else
					unknowns.add(subj.getName());
		}

		log.fine(subj.toString());

		for (final Transition<Role> t : st.getTransitions())
		{
			final Role r = t.getName();
			final EdgeList edges = subj.getRNeighborEdges(r);
			for (int i = 0; i < edges.size(); i++)
			{
				final Edge edge = edges.edgeAt(i);
				final DependencySet ds = edge.getDepends();
				final Individual value = (Individual) edge.getNeighbor(subj);

				getComplexObjectPropertyValues(value, t.getTo(), tg, knowns, unknowns, getSames, visited, isIndependent && ds.isIndependent());
			}
		}
	}

	public void getSames(final Individual ind, final Set<ATermAppl> knowns, final Set<ATermAppl> unknowns)
	{
		knowns.add(ind.getName());

		final boolean thisMerged = ind.isMerged() && !ind.getMergeDependency(true).isIndependent();

		for (final Node other : ind.getMerged())
		{
			if (!other.isRootNominal())
				continue;

			final boolean otherMerged = other.isMerged() && !other.getMergeDependency(true).isIndependent();
			if (thisMerged || otherMerged)
			{
				unknowns.add(other.getName());
				getSames((Individual) other, unknowns, unknowns);
			}
			else
			{
				knowns.add(other.getName());
				getSames((Individual) other, knowns, unknowns);
			}
		}
	}

	/**
	 * Return true if this ABox is consistent. Consistent ABox means after applying all the tableau completion rules at least one _branch with no clashes was
	 * found
	 *
	 * @return
	 */
	public boolean isConsistent()
	{
		boolean isConsistent = false;

		checkAssertedClashes();

		isConsistent = isConsistent(SetUtils.<ATermAppl> emptySet(), null, false);

		if (isConsistent)
		{
			// put the BOTTOM concept into the _cache which will
			// also put TOP in there
			_cache.putSat(ATermUtils.BOTTOM, false);

			assert isComplete() : "ABox not marked complete!";
		}

		return isConsistent;
	}

	/**
	 * Checks if all the previous asserted clashes are resolved. If there is an unresolved _clash, the _clash will be set to the first such _clash found
	 * (selection is arbitrary). The _clash remains unchanged if all clashes are resolved. That is, the _clash might be non-null after this function even if all
	 * asserted clashes are This function is used when incremental deletion is disabled.
	 */
	private boolean checkAssertedClashes()
	{
		final Iterator<Clash> i = _assertedClashes.iterator();
		while (i.hasNext())
		{
			final Clash clash = i.next();
			final Node node = clash.getNode();
			final ATermAppl term = clash._args != null ? (ATermAppl) clash._args[0] : null;

			// check if _clash is resolved through deletions
			boolean resolved = true;
			switch (clash.getClashType())
			{
				case ATOMIC:
					final ATermAppl negation = ATermUtils.negate(term);
					resolved = !node.hasType(term) || !node.hasType(negation);
					break;
				case NOMINAL:
					resolved = !node.isSame(getNode(term));
					break;
				case INVALID_LITERAL:
					resolved = false;
					break;
				default:
					log.warning("Unexpected asserted _clash type: " + clash);
					break;
			}

			if (resolved)
				// discard resolved _clash
				i.remove();
			else
			{
				// this _clash is not resolved, no point in continuing
				setClash(clash);
				return false;
			}
		}

		return true;
	}

	/**
	 * Check the consistency of this ABox possibly after adding some type assertions. If <code>c</code> is null then nothing is added to ABox (pure consistency
	 * test) and the individuals should be an empty collection. If <code>c</code> is not null but <code>individuals</code> is empty, this is a satisfiability
	 * check for concept <code>c</code> so a new individual will be added with type <code>c</code>. If individuals is not empty, this means we will add type
	 * <code>c</code> to each of the individuals in the collection and check the consistency.
	 * <p>
	 * The consistency checks will be done either on a copy of the ABox or its pseudo model depending on the situation. In either case this ABox will not be
	 * modified at all. After the consistency check _lastCompletion points to the modified ABox.
	 *
	 * @param individuals
	 * @param c
	 * @return
	 */
	private boolean isConsistent(Collection<ATermAppl> individuals, ATermAppl c, final boolean cacheModel)
	{
		final Timer t = _kb.timers.startTimer("isConsistent");

		if (log.isLoggable(Level.FINE))
			if (c == null)
				log.fine("ABox consistency for " + individuals.size() + " individuals");
			else
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("[");
				final Iterator<ATermAppl> it = individuals.iterator();
				for (int i = 0; i < 100 && it.hasNext(); i++)
				{
					if (i > 0)
						sb.append(", ");
					sb.append(ATermUtils.toString(it.next()));
				}
				if (it.hasNext())
					sb.append(", ...");
				sb.append("]");
				log.fine("Consistency " + ATermUtils.toString(c) + " for " + individuals.size() + " individuals " + sb);
			}

		final Expressivity expr = _kb.getExpressivityChecker().getExpressivityWith(c);

		// if c is null we are checking the consistency of this ABox as
		// it is and we will not add anything extra
		final boolean initialConsistencyCheck = (c == null);

		final boolean emptyConsistencyCheck = initialConsistencyCheck && isEmpty();

		// if individuals is empty and we are not building the pseudo
		// model then this is concept satisfiability
		final boolean conceptSatisfiability = individuals.isEmpty() && (!initialConsistencyCheck || emptyConsistencyCheck);

		// Check if there are any nominals in the KB or nominal
		// reasoning is disabled
		final boolean hasNominal = expr.hasNominal() && !PelletOptions.USE_PSEUDO_NOMINALS;

		// Use empty model only if this is concept satisfiability for a KB
		// where there are no nominals
		final boolean canUseEmptyABox = conceptSatisfiability && !hasNominal;

		ATermAppl x = null;
		if (conceptSatisfiability)
		{
			x = ATermUtils.CONCEPT_SAT_IND;
			individuals = SetUtils.singleton(x);
		}

		if (emptyConsistencyCheck)
			c = ATermUtils.TOP;

		final ABox abox = canUseEmptyABox ? this.copy(x, false) : initialConsistencyCheck ? this : this.copy(x, true);

		for (final ATermAppl ind : individuals)
		{
			abox.setSyntacticUpdate(true);
			abox.addType(ind, c);
			abox.setSyntacticUpdate(false);
		}

		if (log.isLoggable(Level.FINE))
			log.fine("Consistency check starts");

		final CompletionStrategy strategy = _kb.chooseStrategy(abox, expr);

		if (log.isLoggable(Level.FINE))
			log.fine("Strategy: " + strategy.getClass().getName());

		final Timer completionTimer = _kb.timers.getTimer("complete");
		completionTimer.start();
		try
		{
			strategy.complete(expr);
		}
		finally
		{
			completionTimer.stop();
		}

		final boolean consistent = !abox.isClosed();

		if (x != null && c != null && cacheModel)
			cache(abox.getIndividual(x), c, consistent);

		if (log.isLoggable(Level.FINE))
			log.fine("Consistent: " + consistent + " Time: " + t.getElapsed() + " Branches " + abox._branches.size() + " Tree depth: " + abox.stats.treeDepth + " Tree size: " + abox.getNodes().size() + " Restores " + abox.stats.globalRestores + " global " + abox.stats.localRestores + " local" + " Backtracks " + abox.stats.backtracks + " avg backjump " + (abox.stats.backjumps / (double) abox.stats.backtracks));

		if (consistent)
		{
			if (initialConsistencyCheck && isEmpty())
				setComplete(true);
		}
		else
		{
			_lastClash = abox.getClash();
			if (log.isLoggable(Level.FINE))
				log.fine("Clash: " + abox.getClash().detailedString());
			if (_doExplanation && PelletOptions.USE_TRACING)
			{
				if (individuals.size() == 1)
				{
					final ATermAppl ind = individuals.iterator().next();

					final ATermAppl tempAxiom = ATermUtils.makeTypeAtom(ind, c);
					final Set<ATermAppl> explanationSet = getExplanationSet();
					final boolean removed = explanationSet.remove(tempAxiom);
					if (!removed)
						if (log.isLoggable(Level.FINE))
							log.fine("Explanation set is missing an axiom.\n\tAxiom: " + tempAxiom + "\n\tExplantionSet: " + explanationSet);
				}
				if (log.isLoggable(Level.FINE))
				{
					final StringBuilder sb = new StringBuilder();
					for (final ATermAppl axiom : getExplanationSet())
					{
						sb.append("\n\t");
						sb.append(ATermUtils.toString(axiom));
					}
					log.fine("Explanation: " + sb);
				}
			}
		}

		stats.consistencyCount++;

		if (_keepLastCompletion)
			_lastCompletion = abox;
		else
			_lastCompletion = null;

		t.stop();

		return consistent;
	}

	/**
	 * Check the consistency of this ABox using the incremental consistency checking approach
	 */
	boolean isIncConsistent()
	{
		assert isComplete() : "Initial consistency check has not been performed!";

		final Timer incT = _kb.timers.startTimer("isIncConsistent");
		final Timer t = _kb.timers.startTimer("isConsistent");

		// throw away old information to let gc do its work
		_lastCompletion = null;

		if (log.isLoggable(Level.FINE))
			log.fine("Consistency check starts");

		// currently there is only one incremental consistency _strategy
		final CompletionStrategy incStrategy = new SROIQIncStrategy(this);

		if (log.isLoggable(Level.FINE))
			log.fine("Strategy: " + incStrategy.getClass().getName());

		// set _abox to not being complete
		setComplete(false);
		final Timer completionTimer = _kb.timers.getTimer("complete");
		completionTimer.start();
		try
		{
			incStrategy.complete(_kb.getExpressivityChecker().getExpressivity());
		}
		finally
		{
			completionTimer.stop();
		}

		final boolean consistent = !isClosed();

		if (log.isLoggable(Level.FINE))
			log.fine("Consistent: " + consistent + " Tree depth: " + stats.treeDepth + " Tree size: " + getNodes().size());

		if (!consistent)
		{
			_lastClash = getClash();
			if (log.isLoggable(Level.FINE))
				log.fine(getClash().detailedString());
		}

		stats.consistencyCount++;

		_lastCompletion = this;

		t.stop();
		incT.stop();

		// do not clear the _clash information

		// ((Log4JLogger)ABox.log).getLogger().setLevel(Level.OFF);
		// ((Log4JLogger)DependencyIndex.log).getLogger().setLevel(Level.OFF);

		return consistent;
	}

	public EdgeList getInEdges(final ATerm x)
	{
		return getNode(x).getInEdges();
	}

	public EdgeList getOutEdges(final ATerm x)
	{
		final Node node = getNode(x);
		if (node instanceof Literal)
			return new EdgeList();
		return ((Individual) node).getOutEdges();
	}

	public Individual getIndividual(final ATerm x)
	{
		final Object o = _nodes.get(x);
		if (o instanceof Individual)
			return (Individual) o;
		return null;
	}

	public Literal getLiteral(final ATerm x)
	{
		final Object o = _nodes.get(x);
		if (o instanceof Literal)
			return (Literal) o;
		return null;
	}

	public Node getNode(final ATerm x)
	{
		return _nodes.get(x);
	}

	public void addType(final ATermAppl x, final ATermAppl c)
	{
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeTypeAtom(x, c)) : DependencySet.INDEPENDENT;

		addType(x, c, ds);
	}

	public void addType(final ATermAppl x, ATermAppl c, DependencySet ds)
	{
		c = ATermUtils.normalize(c);

		// when a type is being added to
		// an ABox that has already been completed, the _branch
		// of the dependency set will automatically be set to
		// the _current _branch. We need to set it to the initial
		// _branch number to make sure that this type assertion
		// will not be removed during backtracking
		final int remember = _branch;
		setBranch(DependencySet.NO_BRANCH);

		Individual node = getIndividual(x);
		node.addType(c, ds, false);

		while (node.isMerged())
		{
			ds = ds.union(node.getMergeDependency(false), _doExplanation);
			node = (Individual) node.getMergedTo();
			node.addType(c, ds, !node.isMerged());
		}

		setBranch(remember);
	}

	public Edge addEdge(final ATermAppl p, final ATermAppl s, final ATermAppl o, DependencySet ds)
	{
		final Role role = getRole(p);
		Individual subj = getIndividual(s);
		Node obj = getNode(o);

		if (subj.isMerged() && obj.isMerged())
			return null;

		if (obj.isMerged())
		{
			obj.addInEdge(new DefaultEdge(role, subj, obj, ds));
			ds = ds.union(obj.getMergeDependency(true), true);
			ds = ds.copy(ds.max() + 1);
			obj = obj.getSame();
		}

		Edge edge = new DefaultEdge(role, subj, obj, ds);
		final Edge existingEdge = subj.getOutEdges().getExactEdge(subj, role, obj);
		if (existingEdge == null)
			subj.addOutEdge(edge);
		else
			if (!existingEdge.getDepends().isIndependent())
			{
				subj.removeEdge(existingEdge);
				subj.addOutEdge(edge);
			}

		if (subj.isMerged())
		{
			ds = ds.union(subj.getMergeDependency(true), true);
			ds = ds.copy(ds.max() + 1);
			subj = subj.getSame();
			edge = new DefaultEdge(role, subj, obj, ds);

			if (subj.getOutEdges().hasEdge(edge))
				return null;

			subj.addOutEdge(edge);
			obj.addInEdge(edge);
		}
		else
			if (existingEdge == null)
				obj.addInEdge(edge);
			else
				if (!existingEdge.getDepends().isIndependent())
				{
					obj.removeInEdge(existingEdge);
					obj.addInEdge(edge);
				}

		return edge;
	}

	/**
	 * Remove the given _node from the _node map which maps names to _nodes. Does not remove the _node from the _node list or other _nodes' edge lists.
	 *
	 * @param x
	 * @return
	 */
	public boolean removeNode(final ATermAppl x)
	{
		return (_nodes.remove(x) != null);
	}

	public void removeType(final ATermAppl x, ATermAppl c)
	{
		c = ATermUtils.normalize(c);

		final Node node = getNode(x);
		node.removeType(c);
	}

	/**
	 * Add a new literal to the ABox. This function is used only when the literal value does not have a known value, e.g. applyMinRule would create such a
	 * literal.
	 *
	 * @return
	 */
	public Literal addLiteral(final DependencySet ds)
	{
		return createLiteral(ATermUtils.makeLiteral(createUniqueName(false)), ds);
	}

	/**
	 * Add a new literal to the ABox. Literal will be assigned a fresh unique name.
	 *
	 * @param dataValue A literal ATerm which should be constructed with one of ATermUtils.makeXXXLiteral functions
	 * @return Literal object that has been created
	 */
	public Literal addLiteral(final ATermAppl dataValue)
	{
		final int remember = getBranch();
		setBranch(DependencySet.NO_BRANCH);

		final Literal lit = addLiteral(dataValue, DependencySet.INDEPENDENT);

		setBranch(remember);

		return lit;
	}

	public Literal addLiteral(final ATermAppl dataValue, final DependencySet ds)
	{
		if (dataValue == null || !ATermUtils.isLiteral(dataValue))
			throw new InternalReasonerException("Invalid value to create a literal. Value: " + dataValue);

		return createLiteral(dataValue, ds);
	}

	/**
	 * Helper function to add a literal.
	 *
	 * @param value The java object that represents the value of this literal
	 * @return
	 */
	private Literal createLiteral(final ATermAppl dataValue, final DependencySet ds)
	{
		ATermAppl name;
		/*
		 * No datatype means the literal is an anonymous variable created for a
		 * min cardinality or some values from restriction.
		 */
		if (ATermUtils.NO_DATATYPE.equals(dataValue.getArgument(ATermUtils.LIT_URI_INDEX)))
			name = dataValue;
		else
			try
			{
				name = getDatatypeReasoner().getCanonicalRepresentation(dataValue);
			}
			catch (final InvalidLiteralException e)
			{
				final String msg = format("Attempt to create an invalid literal (%s): %s", dataValue, e.getMessage());
				if (PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY)
				{
					log.fine(msg);
					name = dataValue;
				}
				else
				{
					log.severe(msg);
					throw new InternalReasonerException(msg, e);
				}
			}
			catch (final UnrecognizedDatatypeException e)
			{
				final String msg = format("Attempt to create a literal with an unrecognized datatype (%s): %s", dataValue, e.getMessage());
				log.severe(msg);
				throw new InternalReasonerException(msg, e);
			}

		final Node node = getNode(name);
		if (node != null)
			if (node instanceof Literal)
			{

				if (((Literal) node).getValue() == null && PelletOptions.USE_COMPLETION_QUEUE)
				{
					// added for completion _queue
					final QueueElement newElement = new QueueElement(node);
					this._completionQueue.add(newElement, NodeSelector.LITERAL);
				}

				if (getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
					_branchEffects.add(getBranch(), node.getName());

				return (Literal) node;
			}
			else
				throw new InternalReasonerException("Same term refers to both a literal and an individual: " + name);

		final int remember = _branch;
		setBranch(DependencySet.NO_BRANCH);

		/*
		 * TODO Investigate the effects of storing asserted value
		 * The input version of the literal is not discarded, only the canonical
		 * versions are stored in the literal. This may cause problems in cases
		 * where the same value space object is presented in the data in multiple
		 * forms.
		 */
		final Literal lit = new Literal(name, dataValue, this, ds);
		lit.addType(ATermUtils.TOP_LIT, ds);

		setBranch(remember);

		_nodes.put(name, lit);
		_nodeList.add(name);

		if (lit.getValue() == null && PelletOptions.USE_COMPLETION_QUEUE)
		{
			// added for completion _queue
			final QueueElement newElement = new QueueElement(lit);
			this._completionQueue.add(newElement, NodeSelector.LITERAL);
		}

		if (getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_branchEffects.add(getBranch(), lit.getName());

		return lit;
	}

	public Individual addIndividual(final ATermAppl x, final DependencySet ds)
	{
		final Individual ind = addIndividual(x, null, ds);

		// update affected inds for this _branch
		if (getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_branchEffects.add(getBranch(), ind.getName());

		return ind;
	}

	public Individual addFreshIndividual(final Individual parent, final DependencySet ds)
	{
		final boolean isNominal = parent == null;
		final ATermAppl name = createUniqueName(isNominal);
		final Individual ind = addIndividual(name, parent, ds);

		if (isNominal)
			ind.setNominalLevel(1);

		return ind;
	}

	private Individual addIndividual(final ATermAppl x, final Individual parent, final DependencySet ds)
	{
		if (_nodes.containsKey(x))
			throw new InternalReasonerException("adding a _node twice " + x);

		setChanged(true);

		final Individual n = new Individual(x, this, parent);

		_nodes.put(x, n);
		_nodeList.add(x);

		if (n.getDepth() > stats.treeDepth)
		{
			stats.treeDepth = n.getDepth();
			if (log.isLoggable(Level.FINER))
				log.finer("Depth: " + stats.treeDepth + " Size: " + size());
		}

		//this must be performed after the _nodeList is updated as this call will update the completion queues
		n.addType(ATermUtils.TOP, ds);

		if (getBranch() > 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
			_branchEffects.add(getBranch(), n.getName());

		return n;
	}

	public void addSame(final ATermAppl x, final ATermAppl y)
	{
		final Individual ind1 = getIndividual(x);
		final Individual ind2 = getIndividual(y);

		// ind1.setSame(ind2, new DependencySet(explanationTable.getCurrent()));

		// ind1.setSame(ind2, DependencySet.INDEPENDENT);
		final ATermAppl sameAxiom = ATermUtils.makeSameAs(x, y);

		// update syntactic assertions - currently i do not add this to the
		// dependency _index
		// now, as it will be added during the actual merge when the completion
		// is performed
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_kb.getSyntacticAssertions().add(sameAxiom);

		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(sameAxiom) : DependencySet.INDEPENDENT;
		getToBeMerged().add(new NodeMerge(ind1, ind2, ds));
	}

	public void addDifferent(final ATermAppl x, final ATermAppl y)
	{
		final Individual ind1 = getIndividual(x);
		final Individual ind2 = getIndividual(y);

		final ATermAppl diffAxiom = ATermUtils.makeDifferent(x, y);

		// update syntactic assertions - currently i do not add this to the
		// dependency _index
		// now, as it will simply be used during the completion _strategy
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_kb.getSyntacticAssertions().add(diffAxiom);

		// ind1.setDifferent(ind2, new
		// DependencySet(explanationTable.getCurrent()));
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(diffAxiom) : DependencySet.INDEPENDENT;

		// Temporarily reset the _branch so that this assertion survives resets
		final int remember = _branch;
		setBranch(DependencySet.NO_BRANCH);

		ind1.setDifferent(ind2, ds);

		setBranch(remember);
	}

	public void addAllDifferent(final ATermList list)
	{
		final ATermAppl allDifferent = ATermUtils.makeAllDifferent(list);
		ATermList outer = list;
		while (!outer.isEmpty())
		{
			ATermList inner = outer.getNext();
			while (!inner.isEmpty())
			{
				final Individual ind1 = getIndividual(outer.getFirst());
				final Individual ind2 = getIndividual(inner.getFirst());

				// update syntactic assertions - currently i do not add this to
				// the dependency _index
				// now, as it will be added during the actual merge when the
				// completion is performed
				if (PelletOptions.USE_INCREMENTAL_DELETION)
					_kb.getSyntacticAssertions().add(allDifferent);

				final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(allDifferent) : DependencySet.INDEPENDENT;

				final int remember = _branch;
				setBranch(DependencySet.NO_BRANCH);

				ind1.setDifferent(ind2, ds);

				setBranch(remember);

				inner = inner.getNext();
			}
			outer = outer.getNext();
		}
	}

	public boolean isNode(final ATerm x)
	{
		return getNode(x) != null;
	}

	final public ATermAppl createUniqueName(final boolean isNominal)
	{
		_anonCount++;

		final ATermAppl name = isNominal ? ATermUtils.makeAnonNominal(_anonCount) : ATermUtils.makeAnon(_anonCount);

		return name;
	}

	final public Collection<Node> getNodes()
	{
		return _nodes.values();
	}

	final public List<ATermAppl> getNodeNames()
	{
		return _nodeList;
	}

	@Override
	public String toString()
	{
		return "[size: " + _nodes.size() + " freeMemory: " + (Runtime.getRuntime().freeMemory() / 1000000.0) + "mb]";
	}

	/**
	 * @return Returns the datatype reasoner.
	 */
	public DatatypeReasoner getDatatypeReasoner()
	{
		return _dtReasoner;
	}

	/**
	 * @return Returns the _isComplete.
	 */
	public boolean isComplete()
	{
		return _isComplete;
	}

	/**
	 * @param _isComplete The _isComplete to set.
	 */
	public void setComplete(final boolean isComplete)
	{
		this._isComplete = isComplete;
	}

	/**
	 * Returns true if Abox has a _clash.
	 *
	 * @return
	 */
	public boolean isClosed()
	{
		return !PelletOptions.SATURATE_TABLEAU && _initialized && _clash != null;
	}

	public Clash getClash()
	{
		return _clash;
	}

	public void setClash(final Clash clash)
	{
		if (clash != null)
		{
			if (log.isLoggable(Level.FINER))
			{
				log.finer("CLSH: " + clash);
				if (clash.getDepends().max() > _branch && _branch != -1)
					log.severe("Invalid _clash dependency " + clash + " > " + _branch);
			}

			if (_branch == DependencySet.NO_BRANCH && clash.getDepends().getBranch() == DependencySet.NO_BRANCH)
				_assertedClashes.add(clash);

			if (this._clash != null)
			{
				if (log.isLoggable(Level.FINER))
					log.finer("Clash was already set \nExisting: " + this._clash + "\nNew     : " + clash);

				if (this._clash.getDepends().max() < clash.getDepends().max())
					return;
			}
		}

		this._clash = clash;
		// CHW - added for incremental deletions
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_kb.getDependencyIndex().setClashDependencies(this._clash);

	}

	/**
	 * @return Returns the _kb.
	 */
	public KnowledgeBase getKB()
	{
		return _kb;
	}

	/**
	 * Convenience function to get the named role.
	 */
	public Role getRole(final ATerm r)
	{
		return _kb.getRole(r);
	}

	/**
	 * Return the RBox
	 */
	public RBox getRBox()
	{
		return _kb.getRBox();
	}

	/**
	 * Return the TBox
	 */
	public TBox getTBox()
	{
		return _kb.getTBox();
	}

	/**
	 * Return the _current _branch number. Branches are created when a non-deterministic rule, e.g. _disjunction or max rule, is being applied.
	 *
	 * @return Returns the _branch.
	 */
	public int getBranch()
	{
		return _branch;
	}

	/**
	 * Set the _branch number (should only be called when backjumping is in progress)
	 *
	 * @param _branch
	 */
	public void setBranch(final int branch)
	{
		this._branch = branch;
	}

	/**
	 * Increment the _branch number (should only be called when a non-deterministic rule, e.g. _disjunction or max rule, is being applied)
	 *
	 * @param _branch
	 */
	public void incrementBranch()
	{

		if (PelletOptions.USE_COMPLETION_QUEUE)
			_completionQueue.incrementBranch(this._branch);

		this._branch++;
	}

	/**
	 * Check if the ABox is ready to be completed.
	 *
	 * @return Returns the _initialized.
	 */
	public boolean isInitialized()
	{
		return _initialized;
	}

	public void setInitialized(final boolean initialized)
	{
		this._initialized = initialized;
	}

	/**
	 * Checks if the clashExplanation is turned on.
	 *
	 * @return Returns the _doExplanation.
	 */
	final public boolean doExplanation()
	{
		return _doExplanation;
	}

	/**
	 * Enable/disable clashExplanation generation
	 *
	 * @param _doExplanation The _doExplanation to set.
	 */
	public void setDoExplanation(final boolean doExplanation)
	{
		this._doExplanation = doExplanation;
	}

	public void setExplanation(final DependencySet ds)
	{
		_lastClash = Clash.unexplained(null, ds);
	}

	public String getExplanation()
	{
		// Clash _lastClash = (_lastCompletion != null) ?
		// _lastCompletion.getClash() : null;
		if (_lastClash == null)
			return "No inconsistency was found! There is no clashExplanation generated.";
		else
			return _lastClash.detailedString();
	}

	public Set<ATermAppl> getExplanationSet()
	{
		if (_lastClash == null)
			throw new RuntimeException("No clashExplanation was generated!");

		return _lastClash.getDepends().getExplain();
	}

	public BranchEffectTracker getBranchEffectTracker()
	{
		if (_branchEffects == null)
			throw new NullPointerException();

		return _branchEffects;
	}

	/**
	 * Returns the _branches.
	 */
	public List<Branch> getBranches()
	{
		return _branches;
	}

	public IncrementalChangeTracker getIncrementalChangeTracker()
	{
		if (_incChangeTracker == null)
			throw new NullPointerException();

		return _incChangeTracker;
	}

	/**
	 * Return individuals to which we need to apply the tableau rules
	 *
	 * @return
	 */
	public IndividualIterator getIndIterator()
	{
		return new IndividualIterator(this);
	}

	/**
	 * Validate all the edges in the ABox _nodes. Used to find bugs in the copy and detach/attach functions.
	 */
	public void validate()
	{
		if (!PelletOptions.VALIDATE_ABOX)
			return;
		System.out.print("VALIDATING...");
		final Iterator<Individual> n = getIndIterator();
		while (n.hasNext())
		{
			final Individual node = n.next();
			if (node.isPruned())
				continue;
			validate(node);
		}
	}

	void validateTypes(final Individual node, final List<ATermAppl> negatedTypes)
	{
		for (int i = 0, n = negatedTypes.size(); i < n; i++)
		{
			final ATermAppl a = negatedTypes.get(i);
			if (a.getArity() == 0)
				continue;
			final ATermAppl notA = (ATermAppl) a.getArgument(0);

			if (node.hasType(notA))
			{
				if (!node.hasType(a))
					throw new InternalReasonerException("Invalid type found: " + node + " " + " " + a + " " + node.debugString() + " " + node._depends);
				throw new InternalReasonerException("Clash found: " + node + " " + a + " " + node.debugString() + " " + node._depends);
			}
		}
	}

	void validate(final Individual node)
	{
		validateTypes(node, node.getTypes(Node.ATOM));
		validateTypes(node, node.getTypes(Node.SOME));
		validateTypes(node, node.getTypes(Node.OR));
		validateTypes(node, node.getTypes(Node.MAX));

		if (!node.isRoot())
		{
			final EdgeList preds = node.getInEdges();
			final boolean validPred = preds.size() == 1 || (preds.size() == 2 && preds.hasEdgeFrom(node));
			if (!validPred)
				throw new InternalReasonerException("Invalid blockable _node: " + node + " " + node.getInEdges());

		}
		else
			if (node.isNominal())
			{
				final ATermAppl nominal = ATermUtils.makeValue(node.getName());
				if (!ATermUtils.isAnonNominal(node.getName()) && !node.hasType(nominal))
					throw new InternalReasonerException("Invalid nominal _node: " + node + " " + node.getTypes());
			}

		for (final ATermAppl c : node.getDepends().keySet())
		{
			final DependencySet ds = node.getDepends(c);
			if (ds.max() > _branch || (!PelletOptions.USE_SMART_RESTORE && ds.getBranch() > _branch))
				throw new InternalReasonerException("Invalid ds found: " + node + " " + c + " " + ds + " " + _branch);
			// if( c.getAFun().equals( ATermUtils.VALUEFUN ) ) {
			// if( !PelletOptions.USE_PSEUDO_NOMINALS ) {
			// Individual z = getIndividual(c.getArgument(0));
			// if(z == null)
			// throw new InternalReasonerException("Nominal to non-existing
			// _node: " + _node + " " + c + " " + ds + " " + _branch);
			// }
			// }
		}
		for (final Node ind : node.getDifferents())
		{
			final DependencySet ds = node.getDifferenceDependency(ind);
			if (ds.max() > _branch || ds.getBranch() > _branch)
				throw new InternalReasonerException("Invalid ds: " + node + " != " + ind + " " + ds);
			if (ind.getDifferenceDependency(node) == null)
				throw new InternalReasonerException("Invalid difference: " + node + " != " + ind + " " + ds);
		}
		EdgeList edges = node.getOutEdges();
		for (int e = 0; e < edges.size(); e++)
		{
			final Edge edge = edges.edgeAt(e);
			final Node succ = edge.getTo();
			if (_nodes.get(succ.getName()) != succ)
				throw new InternalReasonerException("Invalid edge to a non-existing _node: " + edge + " " + _nodes.get(succ.getName()) + "(" + _nodes.get(succ.getName()).hashCode() + ")" + succ + "(" + succ.hashCode() + ")");
			if (!succ.getInEdges().hasEdge(edge))
				throw new InternalReasonerException("Invalid edge: " + edge);
			if (succ.isMerged())
				throw new InternalReasonerException("Invalid edge to a removed _node: " + edge + " " + succ.isMerged());
			final DependencySet ds = edge.getDepends();
			if (ds.max() > _branch || ds.getBranch() > _branch)
				throw new InternalReasonerException("Invalid ds: " + edge + " " + ds);
			final EdgeList allEdges = node.getEdgesTo(succ);
			if (allEdges.getRoles().size() != allEdges.size())
				throw new InternalReasonerException("Duplicate edges: " + allEdges);
		}
		edges = node.getInEdges();
		for (int e = 0; e < edges.size(); e++)
		{
			final Edge edge = edges.edgeAt(e);
			final DependencySet ds = edge.getDepends();
			if (ds.max() > _branch || ds.getBranch() > _branch)
				throw new InternalReasonerException("Invalid ds: " + edge + " " + ds);
		}
	}

	/**
	 * Print the ABox as a completion tree (child _nodes are indented).
	 */
	public void printTree()
	{
		if (!PelletOptions.PRINT_ABOX)
			return;
		System.err.println("PRINTING... " + DependencySet.INDEPENDENT);
		final Iterator<Node> n = _nodes.values().iterator();
		while (n.hasNext())
		{
			final Node node = n.next();
			if (!node.isRoot() || node instanceof Literal)
				continue;
			printNode((Individual) node, new HashSet<Individual>(), "   ");
		}
	}

	/**
	 * Print the _node in the completion tree.
	 *
	 * @param _node
	 * @param printed
	 * @param indent
	 */
	private void printNode(final Individual node, final Set<Individual> printed, String indent)
	{
		final boolean printOnlyName = (node.isNominal() && !printed.isEmpty());

		System.err.print(node);
		if (!printed.add(node))
		{
			System.err.println();
			return;
		}
		if (node.isMerged())
		{
			System.err.println(" -> " + node.getMergedTo() + " " + node.getMergeDependency(false));
			return;
		}
		else
			if (node.isPruned())
				throw new InternalReasonerException("Pruned _node: " + node);

		System.err.print(" = ");
		for (int i = 0; i < Node.TYPES; i++)
			for (final ATermAppl c : node.getTypes(i))
			{
				System.err.print(ATermUtils.toString(c));
				System.err.print(", ");
			}
		System.err.println(node.getDifferents());

		if (printOnlyName)
			return;

		indent += "  ";
		final Iterator<Edge> i = node.getOutEdges().iterator();
		while (i.hasNext())
		{
			final Edge edge = i.next();
			final Node succ = edge.getTo();
			final EdgeList edges = node.getEdgesTo(succ);

			System.err.print(indent + "[");
			for (int e = 0; e < edges.size(); e++)
			{
				if (e > 0)
					System.err.print(", ");
				System.err.print(edges.edgeAt(e).getRole());
			}
			System.err.print("] ");
			if (succ instanceof Individual)
				printNode((Individual) succ, printed, indent);
			else
				System.err.println(" (Literal) " + ATermUtils.toString(succ.getName()) + " " + ATermUtils.toString(succ.getTypes()));
		}
	}

	public Clash getLastClash()
	{
		return _lastClash;
	}

	public ABox getLastCompletion()
	{
		return _lastCompletion;
	}

	public boolean isKeepLastCompletion()
	{
		return _keepLastCompletion;
	}

	public void setKeepLastCompletion(final boolean keepLastCompletion)
	{
		this._keepLastCompletion = keepLastCompletion;
	}

	/**
	 * Return the number of _nodes in the ABox. This number includes both the individuals and the literals.
	 *
	 * @return
	 */
	public int size()
	{
		return _nodes.size();
	}

	/**
	 * Returns true if there are no individuals in the ABox.
	 *
	 * @return
	 */
	public boolean isEmpty()
	{
		return _nodes.isEmpty();
	}

	public void setLastCompletion(final ABox comp)
	{
		_lastCompletion = comp;
	}

	/**
	 * Set whether changes to the update should be treated as syntactic updates, i.e., if the changes are made on explicit source axioms. This is used for the
	 * completion _queue for incremental consistency checking purposes.
	 *
	 * @param boolean val The value
	 */
	protected void setSyntacticUpdate(final boolean val)
	{
		_syntacticUpdate = val;
	}

	/**
	 * Set whether changes to the update should be treated as syntactic updates, i.e., if the changes are made on explicit source axioms. This is used for the
	 * completion _queue for incremental consistency checking purposes.
	 *
	 * @param boolean val The value
	 */
	protected boolean isSyntacticUpdate()
	{
		return _syntacticUpdate;
	}

	public CompletionQueue getCompletionQueue()
	{
		return _completionQueue;
	}

	/**
	 * Reset the ABox to contain only asserted information. Any ABox assertion added by tableau rules will be removed.
	 */
	public void reset()
	{
		if (!isComplete())
			return;

		setComplete(false);

		final Iterator<ATermAppl> i = _nodeList.iterator();
		while (i.hasNext())
		{
			final ATermAppl nodeName = i.next();
			final Node node = _nodes.get(nodeName);
			if (!node.isRootNominal())
			{
				i.remove();
				_nodes.remove(nodeName);
			}
			else
				node.reset(false);
		}

		setComplete(false);
		setInitialized(false);
		// clear the _clash. we can safely clear the _clash because
		// either this was an asserted _clash and already stored in the
		// _assertedClashes (and will be verified before consistency change)
		// or this was a _clash that occurred during completion and will
		// reoccur (if no already resolved) since we will run the tableau
		// completion again
		setClash(null);

		setBranch(DependencySet.NO_BRANCH);
		_branches = new Vector<>();
		setDisjBranchStats(new ConcurrentHashMap<ATermAppl, int[]>());
		_rulesNotApplied = true;
	}

	public void resetQueue()
	{
		for (final Node node : _nodes.values())
			node.reset(true);
	}

	/**
	 * @param _anonCount the _anonCount to set
	 */
	public int setAnonCount(final int anonCount)
	{
		return this._anonCount = anonCount;
	}

	/**
	 * @return the _anonCount
	 */
	public int getAnonCount()
	{
		return _anonCount;
	}

	/**
	 * @param _disjBranchStats the _disjBranchStats to set
	 */
	public void setDisjBranchStats(final Map<ATermAppl, int[]> disjBranchStats)
	{
		this._disjBranchStats = disjBranchStats;
	}

	/**
	 * @return the _disjBranchStats
	 */
	public Map<ATermAppl, int[]> getDisjBranchStats()
	{
		return _disjBranchStats;
	}

	/**
	 * @param _changed the _changed to set
	 */
	public void setChanged(final boolean changed)
	{
		this._changed = changed;
	}

	/**
	 * @return the _changed
	 */
	public boolean isChanged()
	{
		return _changed;
	}

	/**
	 * @return the _toBeMerged
	 */
	public List<NodeMerge> getToBeMerged()
	{
		return _toBeMerged;
	}
}
