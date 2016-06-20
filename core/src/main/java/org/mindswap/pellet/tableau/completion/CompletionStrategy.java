// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion;

import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;

import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.aterm.ATerm;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermList;
import openllet.shared.tools.Log;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.NodeMerge;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.blocking.Blocking;
import org.mindswap.pellet.tableau.blocking.BlockingFactory;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.branch.GuessBranch;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.tableau.completion.rule.AllValuesRule;
import org.mindswap.pellet.tableau.completion.rule.ChooseRule;
import org.mindswap.pellet.tableau.completion.rule.DataCardinalityRule;
import org.mindswap.pellet.tableau.completion.rule.DataSatisfiabilityRule;
import org.mindswap.pellet.tableau.completion.rule.DisjunctionRule;
import org.mindswap.pellet.tableau.completion.rule.GuessRule;
import org.mindswap.pellet.tableau.completion.rule.MaxRule;
import org.mindswap.pellet.tableau.completion.rule.MinRule;
import org.mindswap.pellet.tableau.completion.rule.NominalRule;
import org.mindswap.pellet.tableau.completion.rule.SelfRule;
import org.mindswap.pellet.tableau.completion.rule.SimpleAllValuesRule;
import org.mindswap.pellet.tableau.completion.rule.SomeValuesRule;
import org.mindswap.pellet.tableau.completion.rule.TableauRule;
import org.mindswap.pellet.tableau.completion.rule.UnfoldingRule;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;

/**
 * A completion _strategy specifies how the tableau rules will be applied to an ABox. Depending on the expressivity of the KB, e.g. SHIN, SHON, etc., different
 * (more efficient) strategies may be used. This class is the base for all different implementations and contains _strategy independent functions.
 *
 * @author Evren Sirin
 */
public abstract class CompletionStrategy
{
	public final static Logger _logger = Log.getLogger(CompletionStrategy.class);

	/**
	 * ABox being completed
	 */
	protected ABox _abox;

	/**
	 * TBox associated with the _abox
	 */
	protected TBox _tbox;

	/**
	 * Blocking method specific to this completion _strategy
	 */
	protected Blocking blocking;

	/**
	 * Timers of the associated KB
	 */
	protected Timers _timers;

	/**
	 * Timer to be used by the complete function. KB's consistency timer depends on this one and this dependency is set in the constructor. Any concrete class
	 * that extends CompletionStrategy should check this timer to respect the timeouts defined in the KB.
	 */
	protected Timer _completionTimer;

	/**
	 * Flag to indicate that a merge operation is going on
	 */
	private boolean _merging = false;

	/**
	 * Flat to indicate that we are _merging all _nodes in the _queue
	 */
	private boolean _mergingAll = false;

	/**
	 * The _queue of _node pairs that are waiting to be merged
	 */
	protected List<NodeMerge> _mergeList;

	protected TableauRule _unfoldingRule = new UnfoldingRule(this);
	protected TableauRule _disjunctionRule = new DisjunctionRule(this);
	protected AllValuesRule _allValuesRule = new AllValuesRule(this);
	protected TableauRule _someValuesRule = new SomeValuesRule(this);
	protected TableauRule _chooseRule = new ChooseRule(this);
	protected TableauRule _minRule = new MinRule(this);
	protected MaxRule _maxRule = new MaxRule(this);
	protected TableauRule _selfRule = new SelfRule(this);
	protected TableauRule _nominalRule = new NominalRule(this);
	protected TableauRule _guessRule = new GuessRule(this);
	protected TableauRule _dataSatRule = new DataSatisfiabilityRule(this);
	protected TableauRule _dataCardRule = new DataCardinalityRule(this);

	protected List<TableauRule> _tableauRules;

	/**
	 *
	 */
	public CompletionStrategy(final ABox abox)
	{
		this._abox = abox;
		this._tbox = abox.getTBox();
		this._timers = abox.getKB().timers;

		_completionTimer = _timers.getTimer("complete");
	}

	public ABox getABox()
	{
		return _abox;
	}

	public TBox getTBox()
	{
		return _tbox;
	}

	public Blocking getBlocking()
	{
		return blocking;
	}

	public void checkTimer()
	{
		_completionTimer.check();
	}

	/**
	 * Return individuals to which we need to apply the initialization rules
	 *
	 * @return
	 */
	public Iterator<Individual> getInitializeIterator()
	{
		return new IndividualIterator(_abox);
	}

	protected void configureTableauRules(final Expressivity expr)
	{
		if (!PelletOptions.USE_COMPLETION_STRATEGY)
		{
			addAllRules();
			return;
		}

		final boolean fullDatatypeReasoning = PelletOptions.USE_FULL_DATATYPE_REASONING && (expr.hasUserDefinedDatatype() || expr.hasCardinalityD() || expr.hasKeys());

		_tableauRules = new ArrayList<>();

		if ((!PelletOptions.USE_PSEUDO_NOMINALS && expr.hasNominal()) || implicitNominals())
		{
			_tableauRules.add(_nominalRule);

			if (expr.hasCardinalityQ())
				_tableauRules.add(_guessRule);
		}

		if (expr.hasCardinalityQ() || expr.hasCardinalityD())
			_tableauRules.add(_chooseRule);

		_tableauRules.add(_maxRule);

		if (fullDatatypeReasoning)
			_tableauRules.add(_dataCardRule);

		_tableauRules.add(_dataSatRule);

		_tableauRules.add(_unfoldingRule);

		_tableauRules.add(_disjunctionRule);

		_tableauRules.add(_someValuesRule);

		_tableauRules.add(_minRule);

		// no need to add _allValuesRule to the list since it is applied on-the-fly
		if (expr.hasComplexSubRoles())
			_allValuesRule = new AllValuesRule(this);
		else
			_allValuesRule = new SimpleAllValuesRule(this);

	}

	protected void addAllRules()
	{
		_tableauRules = new ArrayList<>();

		_tableauRules.add(_nominalRule);
		_tableauRules.add(_guessRule);
		_tableauRules.add(_chooseRule);
		_tableauRules.add(_maxRule);
		_tableauRules.add(_dataCardRule);
		_tableauRules.add(_dataSatRule);
		_tableauRules.add(_unfoldingRule);
		_tableauRules.add(_disjunctionRule);
		_tableauRules.add(_someValuesRule);
		_tableauRules.add(_minRule);

		_allValuesRule = new AllValuesRule(this);
	}

	protected boolean implicitNominals()
	{
		final Collection<Rule> rules = _abox.getKB().getNormalizedRules().values();
		for (final Rule rule : rules)
		{
			if (rule == null)
				continue;

			for (final RuleAtom atom : rule.getBody())
				if (atom instanceof DifferentIndividualsAtom)
					return true;

			for (final RuleAtom atom : rule.getHead())
				if (atom instanceof SameIndividualAtom)
					return true;
		}

		return false;
	}

	public void initialize(final Expressivity expressivity)
	{
		_mergeList = new ArrayList<>();

		blocking = BlockingFactory.createBlocking(expressivity);

		configureTableauRules(expressivity);

		for (final Branch branch : _abox.getBranches())
			branch.setStrategy(this);

		if (_abox.isInitialized())
		{

			final Iterator<Individual> i = getInitializeIterator();
			while (i.hasNext())
			{
				final Individual n = i.next();

				if (n.isMerged())
					continue;

				if (n.isConceptRoot())
					applyUniversalRestrictions(n);

				_allValuesRule.apply(n);
				if (n.isMerged())
					continue;
				_nominalRule.apply(n);
				if (n.isMerged())
					continue;
				_selfRule.apply(n);

				// CHW-added for inc. _queue must see if this is bad
				final EdgeList allEdges = n.getOutEdges();
				for (int e = 0; e < allEdges.size(); e++)
				{
					final Edge edge = allEdges.edgeAt(e);
					if (edge.getTo().isPruned())
						continue;

					applyPropertyRestrictions(edge);
					if (n.isMerged())
						break;
				}

			}

			return;
		}

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Initialize started");

		_abox.setBranch(0);

		_mergeList.addAll(_abox.getToBeMerged());

		if (!_mergeList.isEmpty())
			mergeAll();

		final Role topRole = _abox.getRole(TOP_OBJECT_PROPERTY);
		final Iterator<Individual> i = getInitializeIterator();
		while (i.hasNext())
		{
			final Individual n = i.next();

			if (n.isMerged())
				continue;

			applyUniversalRestrictions(n);
			if (n.isMerged())
				continue;

			_selfRule.apply(n);
			if (n.isMerged())
				continue;

			final EdgeList allEdges = n.getOutEdges();
			for (int e = 0; e < allEdges.size(); e++)
			{
				final Edge edge = allEdges.edgeAt(e);

				if (edge.getTo().isPruned())
					continue;

				applyPropertyRestrictions(edge);

				if (n.isMerged())
					break;
			}

			if (n.isMerged())
				continue;

			// The top object role isn't in the edge list, so pretend it exists
			applyPropertyRestrictions(n, topRole, n, DependencySet.INDEPENDENT);
		}

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Merging: " + _mergeList);

		if (!_mergeList.isEmpty())
			mergeAll();

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Initialize finished");

		_abox.setBranch(_abox.getBranches().size() + 1);
		_abox.stats.treeDepth = 1;
		_abox.setChanged(true);
		_abox.setComplete(false);
		_abox.setInitialized(true);
	}

	/**
	 * apply all the tableau rules to the designated ABox
	 */
	public abstract void complete(Expressivity expr);

	public Individual createFreshIndividual(final Individual parent, final DependencySet ds)
	{
		final Individual ind = _abox.addFreshIndividual(parent, ds);

		applyUniversalRestrictions(ind);

		return ind;
	}

	void applyUniversalRestrictions(final Individual node)
	{
		addType(node, ATermUtils.TOP, DependencySet.INDEPENDENT);

		final Set<Role> reflexives = _abox.getKB().getRBox().getReflexiveRoles();
		for (final Role r : reflexives)
		{
			if (_logger.isLoggable(Level.FINE) && !node.hasRNeighbor(r, node))
				_logger.fine("REF : " + node + " " + r);
			addEdge(node, r, node, r.getExplainReflexive());
			if (node.isMerged())
				return;
		}

		final Role topObjProp = _abox.getKB().getRole(ATermUtils.TOP_OBJECT_PROPERTY);
		for (final ATermAppl domain : topObjProp.getDomains())
		{
			addType(node, domain, topObjProp.getExplainDomain(domain));
			if (node.isMerged())
				continue;
		}
		for (final ATermAppl range : topObjProp.getRanges())
		{
			addType(node, range, topObjProp.getExplainRange(range));
			if (node.isMerged())
				continue;
		}

	}

	public void addType(Node node, final ATermAppl c, final DependencySet ds)
	{
		if (_abox.isClosed())
			return;

		node.addType(c, ds);
		if (node.isLiteral())
		{
			final Literal l = (Literal) node;
			final NodeMerge mtc = l.getMergeToConstant();
			if (mtc != null)
			{
				l.clearMergeToConstant();
				final Literal mergeTo = _abox.getLiteral(mtc.getTarget());
				mergeTo(l, mergeTo, mtc.getDepends());
				node = mergeTo;
			}
		}

		// update dependency _index for this _node
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_abox.getKB().getDependencyIndex().addTypeDependency(node.getName(), c, ds);

		if (_logger.isLoggable(Level.FINER))
			_logger.finer("ADD: " + node + " " + c + " - " + ds + " " + ds.getExplain());

		if (c.getAFun().equals(ATermUtils.ANDFUN))
			for (ATermList cs = (ATermList) c.getArgument(0); !cs.isEmpty(); cs = cs.getNext())
			{
				final ATermAppl conj = (ATermAppl) cs.getFirst();

				addType(node, conj, ds);

				node = node.getSame();
			}
		else
			if (c.getAFun().equals(ATermUtils.ALLFUN))
				_allValuesRule.applyAllValues((Individual) node, c, ds);
			else
				if (c.getAFun().equals(ATermUtils.SELFFUN))
				{
					final ATermAppl pred = (ATermAppl) c.getArgument(0);
					final Role role = _abox.getRole(pred);
					if (_logger.isLoggable(Level.FINE) && !((Individual) node).hasRSuccessor(role, node))
						_logger.fine("SELF: " + node + " " + role + " " + node.getDepends(c));
					addEdge((Individual) node, role, node, ds);
				}
		// else if( c.getAFun().equals( ATermUtils.VALUE ) ) {
		// applyNominalRule( (Individual) _node, c, ds);
		// }
	}

	/**
	 * This method updates the _queue in the event that there is an edge added between two _nodes. The _individual must be added back onto the MAXLIST
	 */
	protected void updateQueueAddEdge(final Individual subj, final Role pred, final Node obj)
	{
		// for each min and max card restrictions for the subject, a new
		// queueElement must be generated and added
		List<ATermAppl> types = subj.getTypes(Node.MAX);
		int size = types.size();
		for (int j = 0; j < size; j++)
		{
			final ATermAppl c = types.get(j);
			final ATermAppl max = (ATermAppl) c.getArgument(0);
			final Role r = _abox.getRole(max.getArgument(0));
			if (pred.isSubRoleOf(r))
			{
				final QueueElement newElement = new QueueElement(subj, c);
				_abox.getCompletionQueue().add(newElement, NodeSelector.MAX_NUMBER);
				_abox.getCompletionQueue().add(newElement, NodeSelector.CHOOSE);
			}
		}

		// if the predicate has an inverse or is inversefunctional and the obj
		// is an _individual, then add the object to the list.
		if (obj instanceof Individual)
		{
			types = ((Individual) obj).getTypes(Node.MAX);
			size = types.size();
			for (int j = 0; j < size; j++)
			{
				final ATermAppl c = types.get(j);
				final ATermAppl max = (ATermAppl) c.getArgument(0);
				final Role r = _abox.getRole(max.getArgument(0));

				final Role invR = pred.getInverse();

				if (invR != null)
					if (invR.isSubRoleOf(r))
					{
						final QueueElement newElement = new QueueElement(obj, c);
						_abox.getCompletionQueue().add(newElement, NodeSelector.MAX_NUMBER);
						_abox.getCompletionQueue().add(newElement, NodeSelector.CHOOSE);
					}
			}
		}
	}

	public Edge addEdge(final Individual subj, final Role pred, final Node obj, final DependencySet ds)
	{
		final Edge edge = subj.addEdge(pred, obj, ds);

		// add to the kb dependencies
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_abox.getKB().getDependencyIndex().addEdgeDependency(edge, ds);

		if (PelletOptions.TRACK_BRANCH_EFFECTS)
		{
			_abox.getBranchEffectTracker().add(_abox.getBranch(), subj.getName());
			_abox.getBranchEffectTracker().add(_abox.getBranch(), obj.getName());
		}

		if (PelletOptions.USE_COMPLETION_QUEUE)
			// update the _queue as we are adding an edge - we must add
			// elements to the MAXLIST
			updateQueueAddEdge(subj, pred, obj);

		if (edge != null)
		{
			// note that we do not need to enforce the guess rule for
			// datatype properties because we may only have inverse
			// functional datatype properties which will be handled
			// inside applyPropertyRestrictions
			if (subj.isBlockable() && obj.isNominal() && !obj.isLiteral() && pred.isInverseFunctional())
			{
				final Individual o = (Individual) obj;
				final int max = 1;
				if (!o.hasDistinctRNeighborsForMin(pred.getInverse(), max, ATermUtils.TOP, true))
				{
					int guessMin = o.getMinCard(pred.getInverse(), ATermUtils.TOP);
					if (guessMin == 0)
						guessMin = 1;

					if (guessMin > max)
						return edge;

					final GuessBranch newBranch = new GuessBranch(_abox, this, o, pred.getInverse(), guessMin, max, ATermUtils.TOP, ds);
					addBranch(newBranch);

					// try a merge that does not trivially fail
					if (newBranch.tryNext() == false)
						return edge;

					if (_abox.isClosed())
						return edge;

					if (subj.isPruned())
						return edge;
				}
			}

			applyPropertyRestrictions(subj, pred, obj, ds);
		}

		return edge;
	}

	void applyPropertyRestrictions(final Edge edge)
	{
		applyPropertyRestrictions(edge.getFrom(), edge.getRole(), edge.getTo(), edge.getDepends());
	}

	void applyPropertyRestrictions(final Individual subj, final Role pred, final Node obj, final DependencySet ds)
	{
		applyDomainRange(subj, pred, obj, ds);
		if (subj.isPruned() || obj.isPruned())
			return;
		applyFunctionality(subj, pred, obj);
		if (subj.isPruned() || obj.isPruned())
			return;
		applyDisjointness(subj, pred, obj, ds);
		_allValuesRule.applyAllValues(subj, pred, obj, ds);
		if (subj.isPruned() || obj.isPruned())
			return;
		if (pred.isObjectRole())
		{
			final Individual o = (Individual) obj;
			_allValuesRule.applyAllValues(o, pred.getInverse(), subj, ds);
			checkReflexivitySymmetry(subj, pred, o, ds);
			checkReflexivitySymmetry(o, pred.getInverse(), subj, ds);
			applyDisjointness(o, pred.getInverse(), subj, ds);
		}
	}

	void applyDomainRange(final Individual subj, final Role pred, final Node obj, final DependencySet ds)
	{
		final Set<ATermAppl> domains = pred.getDomains();
		final Set<ATermAppl> ranges = pred.getRanges();

		for (final ATermAppl domain : domains)
		{
			if (_logger.isLoggable(Level.FINE) && !subj.hasType(domain))
				_logger.fine("DOM : " + obj + " <- " + pred + " <- " + subj + " : " + ATermUtils.toString(domain));
			addType(subj, domain, ds.union(pred.getExplainDomain(domain), _abox.doExplanation()));
			if (subj.isPruned() || obj.isPruned())
				return;
		}
		for (final ATermAppl range : ranges)
		{
			if (_logger.isLoggable(Level.FINE) && !obj.hasType(range))
				_logger.fine("RAN : " + subj + " -> " + pred + " -> " + obj + " : " + ATermUtils.toString(range));
			addType(obj, range, ds.union(pred.getExplainRange(range), _abox.doExplanation()));
			if (subj.isPruned() || obj.isPruned())
				return;
		}
	}

	void applyFunctionality(final Individual subj, final Role pred, final Node obj)
	{
		DependencySet maxCardDS = pred.isFunctional() ? pred.getExplainFunctional() : subj.hasMax1(pred);

		if (maxCardDS != null)
			_maxRule.applyFunctionalMaxRule(subj, pred, ATermUtils.getTop(pred), maxCardDS);

		if (pred.isDatatypeRole() && pred.isInverseFunctional())
			applyFunctionalMaxRule((Literal) obj, pred, DependencySet.INDEPENDENT);
		else
			if (pred.isObjectRole())
			{
				final Individual val = (Individual) obj;
				final Role invR = pred.getInverse();

				maxCardDS = invR.isFunctional() ? invR.getExplainFunctional() : val.hasMax1(invR);

				if (maxCardDS != null)
					_maxRule.applyFunctionalMaxRule(val, invR, ATermUtils.TOP, maxCardDS);
			}

	}

	void applyDisjointness(final Individual subj, final Role pred, final Node obj, DependencySet ds)
	{
		// TODO what about inv edges?
		// TODO improve this check
		final Set<Role> disjoints = pred.getDisjointRoles();
		if (disjoints.isEmpty())
			return;
		final EdgeList edges = subj.getEdgesTo(obj);
		for (int i = 0, n = edges.size(); i < n; i++)
		{
			final Edge otherEdge = edges.edgeAt(i);

			if (disjoints.contains(otherEdge.getRole()))
			{
				ds = ds.union(otherEdge.getDepends(), _abox.doExplanation());
				ds = ds.union(pred.getExplainDisjointRole(otherEdge.getRole()), _abox.doExplanation());
				_abox.setClash(Clash.disjointProps(subj, ds, pred.getName(), otherEdge.getRole().getName()));
				return;
			}
		}

	}

	void checkReflexivitySymmetry(final Individual subj, final Role pred, final Individual obj, DependencySet ds)
	{
		if (pred.isAsymmetric() && obj.hasRSuccessor(pred, subj))
		{
			final EdgeList edges = obj.getEdgesTo(subj, pred);
			ds = ds.union(edges.edgeAt(0).getDepends(), _abox.doExplanation());
			if (PelletOptions.USE_TRACING)
				ds = ds.union(pred.getExplainAsymmetric(), _abox.doExplanation());
			_abox.setClash(Clash.unexplained(subj, ds, "Antisymmetric property " + pred));
		}
		else
			if (subj.equals(obj))
				if (pred.isIrreflexive())
					_abox.setClash(Clash.unexplained(subj, ds.union(pred.getExplainIrreflexive(), _abox.doExplanation()), "Irreflexive property " + pred));
				else
				{
					final ATerm notSelfP = ATermUtils.makeNot(ATermUtils.makeSelf(pred.getName()));
					if (subj.hasType(notSelfP))
						_abox.setClash(Clash.unexplained(subj, ds.union(subj.getDepends(notSelfP), _abox.doExplanation()), "Local irreflexive property " + pred));
				}
	}

	protected void applyFunctionalMaxRule(final Literal x, final Role r, DependencySet ds)
	{
		final EdgeList edges = x.getInEdges().getEdges(r);

		// if there is not more than one edge then func max rule won't be triggered
		if (edges.size() <= 1)
			return;// continue;

		// find all distinct R-neighbors of x
		final Set<Node> neighbors = edges.getNeighbors(x);

		// if there is not more than one _neighbor then func max rule won't be triggered
		if (neighbors.size() <= 1)
			return;// continue;

		Individual head = null;
		DependencySet headDS = null;
		// find a nominal _node to use as the head
		for (int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++)
		{
			final Edge edge = edges.edgeAt(edgeIndex);
			final Individual ind = edge.getFrom();

			if (ind.isNominal() && (head == null || ind.getNominalLevel() < head.getNominalLevel()))
			{
				head = ind;
				headDS = edge.getDepends();
			}
		}

		// if there is no nominal in the merge list we need to create one
		if (head == null)
			head = _abox.addFreshIndividual(null, ds);
		else
			ds = ds.union(headDS, _abox.doExplanation());

		for (int i = 0; i < edges.size(); i++)
		{
			final Edge edge = edges.edgeAt(i);
			final Individual next = edge.getFrom();

			if (next.isPruned())
				continue;

			// it is possible that there are multiple edges to the same
			// _node, e.g. property p and its super property, so check if
			// we already merged this one
			if (head.isSame(next))
				continue;

			ds = ds.union(edge.getDepends(), _abox.doExplanation());

			if (next.isDifferent(head))
			{
				ds = ds.union(next.getDifferenceDependency(head), _abox.doExplanation());
				if (r.isFunctional())
					_abox.setClash(Clash.functionalCardinality(x, ds, r.getName()));
				else
					_abox.setClash(Clash.maxCardinality(x, ds, r.getName(), 1));

				break;
			}

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("FUNC: " + x + " for prop " + r + " merge " + next + " -> " + head + " " + ds);

			mergeTo(next, head, ds);

			if (_abox.isClosed())
				return;

			if (head.isPruned())
			{
				ds = ds.union(head.getMergeDependency(true), _abox.doExplanation());
				head = head.getSame();
			}
		}
	}

	private void mergeLater(final Node y, final Node z, final DependencySet ds)
	{
		_mergeList.add(new NodeMerge(y, z, ds));
	}

	/**
	 * Merge all _node pairs in the _queue.
	 */
	public void mergeAll()
	{
		if (_mergingAll)
			return;

		_mergingAll = true;
		while (!_merging && !_mergeList.isEmpty() && !_abox.isClosed())
		{
			final NodeMerge merge = _mergeList.remove(0);

			Node y = _abox.getNode(merge.getSource());
			Node z = _abox.getNode(merge.getTarget());
			DependencySet ds = merge.getDepends();

			if (y.isMerged())
			{
				ds = ds.union(y.getMergeDependency(true), _abox.doExplanation());
				y = y.getSame();
			}

			if (z.isMerged())
			{
				ds = ds.union(z.getMergeDependency(true), _abox.doExplanation());
				z = z.getSame();
			}

			if (y.isPruned() || z.isPruned())
				continue;

			mergeTo(y, z, ds);
		}
		_mergingAll = false;
	}

	/**
	 * Merge _node y into z. Node y and all its descendants will be pruned from the completion graph.
	 *
	 * @param y Node being pruned
	 * @param z Node that is being merged into
	 * @param ds Dependency of this merge operation
	 */
	public void mergeTo(final Node y, final Node z, DependencySet ds)
	{

		// add to effected list
		if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
		{
			_abox.getBranchEffectTracker().add(_abox.getBranch(), y.getName());
			_abox.getBranchEffectTracker().add(_abox.getBranch(), z.getName());
		}

		// add to merge dependency to dependency _index
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_abox.getKB().getDependencyIndex().addMergeDependency(y.getName(), z.getName(), ds);

		if (y.isDifferent(z))
		{
			_abox.setClash(Clash.nominal(y, y.getDifferenceDependency(z).union(ds, _abox.doExplanation())));
			return;
		}
		else
			if (!y.isSame(z))
			{
				_abox.setChanged(true);

				if (_merging)
				{
					mergeLater(y, z, ds);
					return;
				}

				_merging = true;

				if (_logger.isLoggable(Level.FINE))
					_logger.fine("MERG: " + y + " -> " + z + " " + ds);

				ds = ds.copy(_abox.getBranch());

				if (y instanceof Literal && z instanceof Literal)
					mergeLiterals((Literal) y, (Literal) z, ds);
				else
					if (y instanceof Individual && z instanceof Individual)
						mergeIndividuals((Individual) y, (Individual) z, ds);
					else
						throw new InternalReasonerException("Invalid merge operation!");
			}

		_merging = false;
		mergeAll();
	}

	/**
	 * Merge _individual y into x. Individual y and all its descendants will be pruned from the completion graph.
	 *
	 * @param y Individual being pruned
	 * @param x Individual that is being merged into
	 * @param ds Dependency of this merge operation
	 */
	protected boolean mergeIndividuals(final Individual y, final Individual x, final DependencySet ds)
	{
		final boolean merged = y.setSame(x, ds);
		if (!merged)
			return false;

		// if both x and y are blockable x still remains blockable (nominal level
		// is still set to BLOCKABLE), if one or both are nominals then x becomes
		// a nominal with the minimum level
		x.setNominalLevel(Math.min(x.getNominalLevel(), y.getNominalLevel()));

		// copy the types
		final Map<ATermAppl, DependencySet> types = y.getDepends();
		for (final Map.Entry<ATermAppl, DependencySet> entry : types.entrySet())
		{
			final ATermAppl yType = entry.getKey();
			final DependencySet finalDS = ds.union(entry.getValue(), _abox.doExplanation());
			addType(x, yType, finalDS);
		}

		// for all edges (z, r, y) add an edge (z, r, x)
		final EdgeList inEdges = y.getInEdges();
		for (int e = 0; e < inEdges.size(); e++)
		{
			final Edge edge = inEdges.edgeAt(e);

			final Individual z = edge.getFrom();
			final Role r = edge.getRole();
			final DependencySet finalDS = ds.union(edge.getDepends(), _abox.doExplanation());

			// if y has a self edge then x should have the same self edge
			if (y.equals(z))
				addEdge(x, r, x, finalDS);
			else
				if (x.hasSuccessor(z))
					// FIXME what if there were no inverses in this expressitivity
					addEdge(x, r.getInverse(), z, finalDS);
				else
					addEdge(z, r, x, finalDS);

			// only remove the edge from z and keep a copy in y for a
			// possible restore operation in the future
			z.removeEdge(edge);

			// add to effected list of _queue
			// if( _abox.getBranch() >= 0 && PelletOptions.USE_COMPLETION_QUEUE ) {
			// _abox.getCompletionQueue().addEffected( _abox.getBranch(), z.getName() );
			// }
			if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
				_abox.getBranchEffectTracker().add(_abox.getBranch(), z.getName());

		}

		// for all z such that y != z set x != z
		x.inheritDifferents(y, ds);

		// we want to prune y early due to an implementation issue about literals
		// if y has an outgoing edge to a literal with concrete value
		y.prune(ds);

		// for all edges (y, r, z) where z is a nominal add an edge (x, r, z)
		final EdgeList outEdges = y.getOutEdges();
		for (int e = 0; e < outEdges.size(); e++)
		{
			final Edge edge = outEdges.edgeAt(e);
			final Node z = edge.getTo();

			if (z.isNominal() && !y.equals(z))
			{
				final Role r = edge.getRole();
				final DependencySet finalDS = ds.union(edge.getDepends(), _abox.doExplanation());

				addEdge(x, r, z, finalDS);

				// add to effected list
				if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
					_abox.getBranchEffectTracker().add(_abox.getBranch(), z.getName());

				// do not remove edge here because prune will take care of that
			}
		}

		return true;
	}

	/**
	 * Merge literal y into x. Literal y will be pruned from* the completion graph.
	 *
	 * @param y Literal being pruned
	 * @param x Literal that is being merged into
	 * @param ds Dependency of this merge operation
	 */
	protected void mergeLiterals(final Literal y, final Literal x, final DependencySet ds)
	{
		y.setSame(x, ds);

		x.addAllTypes(y.getDepends(), ds);

		// for all edges (z, r, y) add an edge (z, r, x)
		final EdgeList inEdges = y.getInEdges();
		for (int e = 0; e < inEdges.size(); e++)
		{
			final Edge edge = inEdges.edgeAt(e);

			final Individual z = edge.getFrom();
			final Role r = edge.getRole();
			final DependencySet finalDS = ds.union(edge.getDepends(), _abox.doExplanation());

			addEdge(z, r, x, finalDS);

			// only remove the edge from z and keep a copy in y for a
			// possible restore operation in the future
			z.removeEdge(edge);

			// add to effected list
			if (_abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS)
				_abox.getBranchEffectTracker().add(_abox.getBranch(), z.getName());
		}

		x.inheritDifferents(y, ds);

		y.prune(ds);

		if (x.getNodeDepends() == null || y.getNodeDepends() == null)
			throw new NullPointerException();
	}

	public boolean setDifferent(final Node y, final Node z, final DependencySet ds)
	{
		return y.setDifferent(z, ds);
	}

	public void restoreLocal(final Individual ind, final Branch br)
	{
		_abox.stats.localRestores++;
		_abox.setClash(null);
		_abox.setBranch(br.getBranch());

		final Map<Node, Boolean> visited = new HashMap<>();

		restoreLocal(ind, br.getBranch(), visited);

		for (final Map.Entry<Node, Boolean> entry : visited.entrySet())
		{
			final boolean restored = entry.getValue();
			if (restored)
				_allValuesRule.apply((Individual) entry.getKey());
		}
	}

	private void restoreLocal(final Individual ind, final int branch, final Map<Node, Boolean> visited)
	{
		final boolean restored = ind.restore(branch);
		visited.put(ind, restored);

		if (restored)
		{
			for (final Edge edge : ind.getOutEdges())
			{
				final Node succ = edge.getTo();
				if (visited.containsKey(succ))
					continue;

				if (succ.isLiteral())
				{
					visited.put(succ, Boolean.FALSE);
					succ.restore(branch);
				}
				else
					restoreLocal(((Individual) succ), branch, visited);
			}

			for (final Edge edge : ind.getInEdges())
			{
				final Individual pred = edge.getFrom();
				if (visited.containsKey(pred))
					continue;
				restoreLocal(pred, branch, visited);
			}
		}
	}

	public void restore(final Branch br)
	{
		// Timers _timers = _abox.getKB().timers;
		// Timer timer = _timers.startTimer("restore");
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
		_abox._rulesNotApplied = true;
		_mergeList.clear();

		final List<ATermAppl> nodeList = _abox.getNodeNames();

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("RESTORE: Branch " + br.getBranch());

		if (PelletOptions.USE_COMPLETION_QUEUE)
		{
			// clear the all values list as they must have already fired and blocking never prevents the all values rule
			// from firing
			_abox.getCompletionQueue().clearQueue(NodeSelector.UNIVERSAL);

			// reset the queues
			_abox.getCompletionQueue().restore(br.getBranch());
		}

		// the restore may cause changes which require using the _allValuesRule -
		// incremental change tracker will track those
		if (PelletOptions.USE_INCREMENTAL_CONSISTENCY)
			_abox.getIncrementalChangeTracker().clear();

		// for each _node we either need to restore the _node to the status it
		// had at the time _branch was created or remove the _node completely if
		// it was created after the _branch. To optimize removing elements from
		// the ArrayList we compute the block to be deleted and then remove all
		// at once to utilize the underlying System.arraycopy operation.

		// number of _nodes in the _nodeList
		int nodeCount = nodeList.size();
		// number of _nodes
		int deleteBlock = 0;
		for (int i = 0; i < nodeCount; i++)
		{
			// get the _node name
			final ATermAppl a = nodeList.get(i);
			// and the corresponding _node
			final Node node = _abox.getNode(a);

			// _node dependency tells us if the _node was created after the _branch
			// and if that is the case we remove it completely
			// NOTE: for literals, _node.getNodeDepends() may be null when a literal value _branch is
			// restored, in that case we can remove the literal since there is no other reference
			// left for that literal
			if (node.getNodeDepends() == null || node.getNodeDepends().getBranch() > br.getBranch())
			{
				// remove the _node from the _node map
				_abox.removeNode(a);
				// if the _node is merged to another one we should remove it from
				// the other _node's merged list
				if (node.isMerged())
					node.undoSetSame();
				// increment the size of block that will be deleted
				deleteBlock++;
			}
			else
			{
				// this _node will be restored to previous state not removed

				// first if there are any _nodes collected earlier delete them
				if (deleteBlock > 0)
				{
					// create the sub list for _nodes to be removed
					final List<ATermAppl> subList = nodeList.subList(i - deleteBlock, i);
					if (_logger.isLoggable(Level.FINE))
						_logger.fine("Remove _nodes " + subList);
					// clear the sublist causing all elements to removed from _nodeList
					subList.clear();
					// update counters
					nodeCount -= deleteBlock;
					i -= deleteBlock;
					deleteBlock = 0;
				}

				// restore only if not tracking _branch effects
				if (!PelletOptions.TRACK_BRANCH_EFFECTS)
					node.restore(br.getBranch());
			}
		}

		// if there were _nodes to be removed at the _end of the list do it now
		if (deleteBlock > 0)
			nodeList.subList(nodeCount - deleteBlock, nodeCount).clear();

		if (PelletOptions.TRACK_BRANCH_EFFECTS)
		{
			// when tracking _branch effects only restore _nodes explicitly stored in the effected list
			final Set<ATermAppl> effected = _abox.getBranchEffectTracker().removeAll(br.getBranch() + 1);
			for (final ATermAppl a : effected)
			{
				final Node n = _abox.getNode(a);
				if (n != null)
					n.restore(br.getBranch());
			}
		}

		restoreAllValues();

		if (_logger.isLoggable(Level.FINE))
			_abox.printTree();

		if (!_abox.isClosed())
			_abox.validate();

		// timer.stop();
	}

	public void addBranch(final Branch newBranch)
	{
		_abox.getBranches().add(newBranch);

		if (newBranch.getBranch() != _abox.getBranches().size())
			throw new RuntimeException("Invalid _branch created: " + newBranch.getBranch() + " != " + _abox.getBranches().size());

		_completionTimer.check();

		// CHW - added for incremental deletion support
		if (PelletOptions.USE_INCREMENTAL_DELETION)
			_abox.getKB().getDependencyIndex().addBranchAddDependency(newBranch);
	}

	void printBlocked()
	{
		int blockedCount = 0;
		final StringBuffer blockedNodes = new StringBuffer();
		final Iterator<Individual> n = _abox.getIndIterator();
		while (n.hasNext())
		{
			final Individual node = n.next();
			final ATermAppl x = node.getName();

			if (blocking.isBlocked(node))
			{
				blockedCount++;
				blockedNodes.append(x).append(" ");
			}
		}

		_logger.fine("Blocked _nodes " + blockedCount + " [" + blockedNodes + "]");
	}

	@Override
	public String toString()
	{
		final String name = getClass().getName();
		final int lastIndex = name.lastIndexOf('.');
		return name.substring(lastIndex + 1);
	}

	protected void restoreAllValues()
	{
		for (final Iterator<Individual> i = new IndividualIterator(_abox); i.hasNext();)
		{
			final Individual ind = i.next();
			_allValuesRule.apply(ind);
		}
	}
}
