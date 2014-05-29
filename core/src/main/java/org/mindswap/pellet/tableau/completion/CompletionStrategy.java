// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion;

import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;

/**
 * A completion strategy specifies how the tableau rules will be applied to an ABox. Depending on the expressivity of
 * the KB, e.g. SHIN, SHON, etc., different (more efficient) strategies may be used. This class is the base for all
 * different implementations and contains strategy independent functions.
 * 
 * @author Evren Sirin
 */
public abstract class CompletionStrategy {
	public final static Logger log = Logger.getLogger(CompletionStrategy.class.getName());

	/**
	 * ABox being completed
	 */
	protected ABox abox;

	/**
	 * TBox associated with the abox
	 */
	protected TBox tbox;

	/**
	 * Blocking method specific to this completion strategy
	 */
	protected Blocking blocking;

	/**
	 * Timers of the associated KB
	 */
	protected Timers timers;

	/**
	 * Timer to be used by the complete function. KB's consistency timer depends on this one and this dependency is set
	 * in the constructor. Any concrete class that extends CompletionStrategy should check this timer to respect the
	 * timeouts defined in the KB.
	 */
	protected Timer completionTimer;

	/**
	 * Flag to indicate that a merge operation is going on
	 */
	private boolean merging = false;

	/**
	 * Flat to indicate that we are merging all nodes in the queue
	 */
	private boolean mergingAll = false;

	/**
	 * The queue of node pairs that are waiting to be merged
	 */
	protected List<NodeMerge> mergeList;

	protected TableauRule unfoldingRule = new UnfoldingRule(this);
	protected TableauRule disjunctionRule = new DisjunctionRule(this);
	protected AllValuesRule allValuesRule = new AllValuesRule(this);
	protected TableauRule someValuesRule = new SomeValuesRule(this);
	protected TableauRule chooseRule = new ChooseRule(this);
	protected TableauRule minRule = new MinRule(this);
	protected MaxRule maxRule = new MaxRule(this);
	protected TableauRule selfRule = new SelfRule(this);
	protected TableauRule nominalRule = new NominalRule(this);
	protected TableauRule guessRule = new GuessRule(this);
	protected TableauRule dataSatRule = new DataSatisfiabilityRule(this);
	protected TableauRule dataCardRule = new DataCardinalityRule(this);

	protected List<TableauRule> tableauRules;

	/**
     * 
     */
	public CompletionStrategy(ABox abox) {
		this.abox = abox;
		this.tbox = abox.getTBox();
		this.timers = abox.getKB().timers;

		completionTimer = timers.getTimer("complete");
	}

	public ABox getABox() {
		return abox;
	}

	public TBox getTBox() {
		return tbox;
	}

	public Blocking getBlocking() {
		return blocking;
	}

	public void checkTimer() {
		completionTimer.check();
	}

	/**
	 * Return individuals to which we need to apply the initialization rules
	 * 
	 * @return
	 */
	public Iterator<Individual> getInitializeIterator() {
		return new IndividualIterator(abox);
	}

	protected void configureTableauRules(Expressivity expr) {
		if (!PelletOptions.USE_COMPLETION_STRATEGY) {
			addAllRules();
			return;
		}

		boolean fullDatatypeReasoning = PelletOptions.USE_FULL_DATATYPE_REASONING
		                                && (expr.hasUserDefinedDatatype() || expr.hasCardinalityD() || expr.hasKeys());

		tableauRules = new ArrayList<TableauRule>();

		if ((!PelletOptions.USE_PSEUDO_NOMINALS && expr.hasNominal()) || implicitNominals()) {
			tableauRules.add(nominalRule);

			if (expr.hasCardinalityQ()) {
				tableauRules.add(guessRule);
			}
		}

		if (expr.hasCardinalityQ() || expr.hasCardinalityD()) {
			tableauRules.add(chooseRule);
		}

		tableauRules.add(maxRule);

		if (fullDatatypeReasoning) {
			tableauRules.add(dataCardRule);
		}

		tableauRules.add(dataSatRule);

		tableauRules.add(unfoldingRule);

		tableauRules.add(disjunctionRule);

		tableauRules.add(someValuesRule);

		tableauRules.add(minRule);

		// no need to add allValuesRule to the list since it is applied on-the-fly
		if (expr.hasComplexSubRoles()) {
			allValuesRule = new AllValuesRule(this);
		}
		else {
			allValuesRule = new SimpleAllValuesRule(this);
		}

	}

	protected void addAllRules() {
		tableauRules = new ArrayList<TableauRule>();

		tableauRules.add(nominalRule);
		tableauRules.add(guessRule);
		tableauRules.add(chooseRule);
		tableauRules.add(maxRule);
		tableauRules.add(dataCardRule);
		tableauRules.add(dataSatRule);
		tableauRules.add(unfoldingRule);
		tableauRules.add(disjunctionRule);
		tableauRules.add(someValuesRule);
		tableauRules.add(minRule);

		allValuesRule = new AllValuesRule(this);
	}

	protected boolean implicitNominals() {
		Collection<Rule> rules = abox.getKB().getNormalizedRules().values();
		for (Rule rule : rules) {
			if (rule == null) {
				continue;
			}
			
            for (RuleAtom atom : rule.getBody()) {
                if (atom instanceof DifferentIndividualsAtom) {
                	return true;
                }
            }
            
            for (RuleAtom atom : rule.getHead()) {
                if (atom instanceof SameIndividualAtom) {
                	return true;
                }
            }
        }
		
		return false;
	}

	public void initialize(Expressivity expressivity) {
		mergeList = new ArrayList<NodeMerge>();

		blocking = BlockingFactory.createBlocking(expressivity);

		configureTableauRules(expressivity);

		for (Branch branch : abox.getBranches()) {
			branch.setStrategy(this);
		}

		if (abox.isInitialized()) {

			Iterator<Individual> i = getInitializeIterator();
			while (i.hasNext()) {
				Individual n = i.next();

				if (n.isMerged()) {
					continue;
				}

				if (n.isConceptRoot()) {
					applyUniversalRestrictions(n);
				}

				allValuesRule.apply(n);
				if (n.isMerged()) {
					continue;
				}
				nominalRule.apply(n);
				if (n.isMerged()) {
					continue;
				}
				selfRule.apply(n);

				// CHW-added for inc. queue must see if this is bad
				EdgeList allEdges = n.getOutEdges();
				for (int e = 0; e < allEdges.size(); e++) {
					Edge edge = allEdges.edgeAt(e);
					if (edge.getTo().isPruned()) {
						continue;
					}

					applyPropertyRestrictions(edge);
					if (n.isMerged()) {
						break;
					}
				}

			}

			return;
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("Initialize started");
		}

		abox.setBranch(0);

		mergeList.addAll(abox.getToBeMerged());

		if (!mergeList.isEmpty()) {
			mergeAll();
		}

		Role topRole = abox.getRole(TOP_OBJECT_PROPERTY);
		Iterator<Individual> i = getInitializeIterator();
		while (i.hasNext()) {
			Individual n = i.next();

			if (n.isMerged()) {
				continue;
			}

			applyUniversalRestrictions(n);
			if (n.isMerged()) {
				continue;
			}

			selfRule.apply(n);
			if (n.isMerged()) {
				continue;
			}

			EdgeList allEdges = n.getOutEdges();
			for (int e = 0; e < allEdges.size(); e++) {
				Edge edge = allEdges.edgeAt(e);

				if (edge.getTo().isPruned()) {
					continue;
				}

				applyPropertyRestrictions(edge);

				if (n.isMerged()) {
					break;
				}
			}

			if (n.isMerged()) {
				continue;
			}

			// The top object role isn't in the edge list, so pretend it exists
			applyPropertyRestrictions(n, topRole, n, DependencySet.INDEPENDENT);
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("Merging: " + mergeList);
		}

		if (!mergeList.isEmpty()) {
			mergeAll();
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("Initialize finished");
		}

		abox.setBranch(abox.getBranches().size() + 1);
		abox.stats.treeDepth = 1;
		abox.setChanged(true);
		abox.setComplete(false);
		abox.setInitialized(true);
	}

	/**
	 * apply all the tableau rules to the designated ABox
	 * 
	 */
	public abstract void complete(Expressivity expr);

	public Individual createFreshIndividual(Individual parent, DependencySet ds) {
		Individual ind = abox.addFreshIndividual(parent, ds);

		applyUniversalRestrictions(ind);

		return ind;
	}

	void applyUniversalRestrictions(Individual node) {
		addType(node, ATermUtils.TOP, DependencySet.INDEPENDENT);

		Set<Role> reflexives = abox.getKB().getRBox().getReflexiveRoles();
		for (Iterator<Role> i = reflexives.iterator(); i.hasNext();) {
			Role r = i.next();
			if (log.isLoggable(Level.FINE) && !node.hasRNeighbor(r, node)) {
				log.fine("REF : " + node + " " + r);
			}
			addEdge(node, r, node, r.getExplainReflexive());
			if (node.isMerged()) {
				return;
			}
		}

		Role topObjProp = abox.getKB().getRole(ATermUtils.TOP_OBJECT_PROPERTY);
		for (ATermAppl domain : topObjProp.getDomains()) {
			addType(node, domain, topObjProp.getExplainDomain(domain));
			if (node.isMerged()) {
				continue;
			}
		}
		for (ATermAppl range : topObjProp.getRanges()) {
			addType(node, range, topObjProp.getExplainRange(range));
			if (node.isMerged()) {
				continue;
			}
		}

	}

	public void addType(Node node, ATermAppl c, DependencySet ds) {
		if (abox.isClosed()) {
			return;
		}

		node.addType(c, ds);
		if (node.isLiteral()) {
			final Literal l = (Literal) node;
			final NodeMerge mtc = l.getMergeToConstant();
			if (mtc != null) {
				l.clearMergeToConstant();
				Literal mergeTo = abox.getLiteral(mtc.getTarget());
				mergeTo(l, mergeTo, mtc.getDepends());
				node = mergeTo;
			}
		}

		// update dependency index for this node
		if (PelletOptions.USE_INCREMENTAL_DELETION) {
			abox.getKB().getDependencyIndex().addTypeDependency(node.getName(), c, ds);
		}

		if (log.isLoggable(Level.FINER)) {
			log.finer("ADD: " + node + " " + c + " - " + ds + " " + ds.getExplain());
		}

		if (c.getAFun().equals(ATermUtils.ANDFUN)) {
			for (ATermList cs = (ATermList) c.getArgument(0); !cs.isEmpty(); cs = cs.getNext()) {
				ATermAppl conj = (ATermAppl) cs.getFirst();

				addType(node, conj, ds);

				node = node.getSame();
			}
		}
		else if (c.getAFun().equals(ATermUtils.ALLFUN)) {
			allValuesRule.applyAllValues((Individual) node, c, ds);
		}
		else if (c.getAFun().equals(ATermUtils.SELFFUN)) {
			ATermAppl pred = (ATermAppl) c.getArgument(0);
			Role role = abox.getRole(pred);
			if (log.isLoggable(Level.FINE) && !((Individual) node).hasRSuccessor(role, node)) {
				log.fine("SELF: " + node + " " + role + " " + node.getDepends(c));
			}
			addEdge((Individual) node, role, node, ds);
		}
		// else if( c.getAFun().equals( ATermUtils.VALUE ) ) {
		// applyNominalRule( (Individual) node, c, ds);
		// }
	}

	/**
	 * This method updates the queue in the event that there is an edge added between two nodes. The individual must be
	 * added back onto the MAXLIST
	 */
	protected void updateQueueAddEdge(Individual subj, Role pred, Node obj) {
		// for each min and max card restrictions for the subject, a new
		// queueElement must be generated and added
		List<ATermAppl> types = subj.getTypes(Node.MAX);
		int size = types.size();
		for (int j = 0; j < size; j++) {
			ATermAppl c = types.get(j);
			ATermAppl max = (ATermAppl) c.getArgument(0);
			Role r = abox.getRole(max.getArgument(0));
			if (pred.isSubRoleOf(r)) {
				QueueElement newElement = new QueueElement(subj, c);
				abox.getCompletionQueue().add(newElement, NodeSelector.MAX_NUMBER);
				abox.getCompletionQueue().add(newElement, NodeSelector.CHOOSE);
			}
		}

		// if the predicate has an inverse or is inversefunctional and the obj
		// is an individual, then add the object to the list.
		if (obj instanceof Individual) {
			types = ((Individual) obj).getTypes(Node.MAX);
			size = types.size();
			for (int j = 0; j < size; j++) {
				ATermAppl c = types.get(j);
				ATermAppl max = (ATermAppl) c.getArgument(0);
				Role r = abox.getRole(max.getArgument(0));

				Role invR = pred.getInverse();

				if (invR != null) {
					if (invR.isSubRoleOf(r)) {
						QueueElement newElement = new QueueElement(obj, c);
						abox.getCompletionQueue().add(newElement, NodeSelector.MAX_NUMBER);
						abox.getCompletionQueue().add(newElement, NodeSelector.CHOOSE);
					}
				}
			}
		}
	}

	public void addEdge(Individual subj, Role pred, Node obj, DependencySet ds) {
		Edge edge = subj.addEdge(pred, obj, ds);

		// add to the kb dependencies
		if (PelletOptions.USE_INCREMENTAL_DELETION) {
			abox.getKB().getDependencyIndex().addEdgeDependency(edge, ds);
		}

		if (PelletOptions.TRACK_BRANCH_EFFECTS) {
			abox.getBranchEffectTracker().add(abox.getBranch(), subj.getName());
			abox.getBranchEffectTracker().add(abox.getBranch(), obj.getName());
		}

		if (PelletOptions.USE_COMPLETION_QUEUE) {
			// update the queue as we are adding an edge - we must add
			// elements to the MAXLIST
			updateQueueAddEdge(subj, pred, obj);
		}

		if (edge != null) {
			// note that we do not need to enforce the guess rule for
			// datatype properties because we may only have inverse
			// functional datatype properties which will be handled
			// inside applyPropertyRestrictions
			if (subj.isBlockable() && obj.isNominal() && !obj.isLiteral() && pred.isInverseFunctional()) {
				Individual o = (Individual) obj;
				int max = 1;
				if (!o.hasDistinctRNeighborsForMin(pred.getInverse(), max, ATermUtils.TOP, true)) {
					int guessMin = o.getMinCard(pred.getInverse(), ATermUtils.TOP);
					if (guessMin == 0) {
						guessMin = 1;
					}

					if (guessMin > max) {
						return;
					}

					GuessBranch newBranch = new GuessBranch(abox, this, o, pred.getInverse(), guessMin, max,
					                ATermUtils.TOP, ds);
					addBranch(newBranch);

					// try a merge that does not trivially fail
					if (newBranch.tryNext() == false) {
						return;
					}

					if (abox.isClosed()) {
						return;
					}

					if (subj.isPruned()) {
						return;
					}
				}
			}

			applyPropertyRestrictions(subj, pred, obj, ds);
		}
	}

	void applyPropertyRestrictions(Edge edge) {
		applyPropertyRestrictions(edge.getFrom(), edge.getRole(), edge.getTo(), edge.getDepends());
	}

	void applyPropertyRestrictions(Individual subj, Role pred, Node obj, DependencySet ds) {
		applyDomainRange(subj, pred, obj, ds);
		if (subj.isPruned() || obj.isPruned()) {
			return;
		}
		applyFunctionality(subj, pred, obj);
		if (subj.isPruned() || obj.isPruned()) {
			return;
		}
		applyDisjointness(subj, pred, obj, ds);
		allValuesRule.applyAllValues(subj, pred, obj, ds);
		if (subj.isPruned() || obj.isPruned()) {
			return;
		}
		if (pred.isObjectRole()) {
			Individual o = (Individual) obj;
			allValuesRule.applyAllValues(o, pred.getInverse(), subj, ds);
			checkReflexivitySymmetry(subj, pred, o, ds);
			checkReflexivitySymmetry(o, pred.getInverse(), subj, ds);
			applyDisjointness(o, pred.getInverse(), subj, ds);
		}
	}

	void applyDomainRange(Individual subj, Role pred, Node obj, DependencySet ds) {
		Set<ATermAppl> domains = pred.getDomains();
		Set<ATermAppl> ranges = pred.getRanges();

		for (ATermAppl domain : domains) {
			if (log.isLoggable(Level.FINE) && !subj.hasType(domain)) {
				log.fine("DOM : " + obj + " <- " + pred + " <- " + subj + " : " + ATermUtils.toString(domain));
			}
			addType(subj, domain, ds.union(pred.getExplainDomain(domain), abox.doExplanation()));
			if (subj.isPruned() || obj.isPruned()) {
				return;
			}
		}
		for (ATermAppl range : ranges) {
			if (log.isLoggable(Level.FINE) && !obj.hasType(range)) {
				log.fine("RAN : " + subj + " -> " + pred + " -> " + obj + " : " + ATermUtils.toString(range));
			}
			addType(obj, range, ds.union(pred.getExplainRange(range), abox.doExplanation()));
			if (subj.isPruned() || obj.isPruned()) {
				return;
			}
		}
	}

	void applyFunctionality(Individual subj, Role pred, Node obj) {
		DependencySet maxCardDS = pred.isFunctional() ? pred.getExplainFunctional() : subj.hasMax1(pred);

		if (maxCardDS != null) {
			maxRule.applyFunctionalMaxRule(subj, pred, ATermUtils.getTop(pred), maxCardDS);
		}

		if (pred.isDatatypeRole() && pred.isInverseFunctional()) {
			applyFunctionalMaxRule((Literal) obj, pred, DependencySet.INDEPENDENT);
		}
		else if (pred.isObjectRole()) {
			Individual val = (Individual) obj;
			Role invR = pred.getInverse();

			maxCardDS = invR.isFunctional() ? invR.getExplainFunctional() : val.hasMax1(invR);

			if (maxCardDS != null) {
				maxRule.applyFunctionalMaxRule(val, invR, ATermUtils.TOP, maxCardDS);
			}
		}

	}

	void applyDisjointness(Individual subj, Role pred, Node obj, DependencySet ds) {
		// TODO what about inv edges?
		// TODO improve this check
		Set<Role> disjoints = pred.getDisjointRoles();
		if (disjoints.isEmpty()) {
			return;
		}
		EdgeList edges = subj.getEdgesTo(obj);
		for (int i = 0, n = edges.size(); i < n; i++) {
			Edge otherEdge = edges.edgeAt(i);

			if (disjoints.contains(otherEdge.getRole())) {
				ds = ds.union(otherEdge.getDepends(), abox.doExplanation());
				ds = ds.union(pred.getExplainDisjointRole(otherEdge.getRole()), abox.doExplanation());
				abox.setClash(Clash.disjointProps(subj, ds, pred.getName(), otherEdge.getRole().getName()));
				return;
			}
		}

	}

	void checkReflexivitySymmetry(Individual subj, Role pred, Individual obj, DependencySet ds) {
		if (pred.isAsymmetric() && obj.hasRSuccessor(pred, subj)) {
			EdgeList edges = obj.getEdgesTo(subj, pred);
			ds = ds.union(edges.edgeAt(0).getDepends(), abox.doExplanation());
			if (PelletOptions.USE_TRACING) {
				ds = ds.union(pred.getExplainAsymmetric(), abox.doExplanation());
			}
			abox.setClash(Clash.unexplained(subj, ds, "Antisymmetric property " + pred));
		}
		else if (subj.equals(obj)) {
			if (pred.isIrreflexive()) {
				abox.setClash(Clash.unexplained(subj, ds.union(pred.getExplainIrreflexive(), abox.doExplanation()),
				                "Irreflexive property " + pred));
			}
			else {
				ATerm notSelfP = ATermUtils.makeNot(ATermUtils.makeSelf(pred.getName()));
				if (subj.hasType(notSelfP)) {
					abox.setClash(Clash.unexplained(subj, ds.union(subj.getDepends(notSelfP), abox.doExplanation()),
					                "Local irreflexive property " + pred));
				}
			}
		}
	}

	protected void applyFunctionalMaxRule(Literal x, Role r, DependencySet ds) {
		EdgeList edges = x.getInEdges().getEdges(r);

		// if there is not more than one edge then func max rule won't be triggered
		if (edges.size() <= 1) {
			return;// continue;
		}

		// find all distinct R-neighbors of x
		Set<Node> neighbors = edges.getNeighbors(x);

		// if there is not more than one neighbor then func max rule won't be triggered
		if (neighbors.size() <= 1) {
			return;// continue;
		}

		Individual head = null;
		DependencySet headDS = null;
		// find a nominal node to use as the head
		for (int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
			Edge edge = edges.edgeAt(edgeIndex);
			Individual ind = edge.getFrom();

			if (ind.isNominal() && (head == null || ind.getNominalLevel() < head.getNominalLevel())) {
				head = ind;
				headDS = edge.getDepends();
			}
		}

		// if there is no nominal in the merge list we need to create one
		if (head == null) {
			head = abox.addFreshIndividual(null, ds);
		}
		else {
			ds = ds.union(headDS, abox.doExplanation());
		}

		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.edgeAt(i);
			Individual next = edge.getFrom();

			if (next.isPruned()) {
				continue;
			}

			// it is possible that there are multiple edges to the same
			// node, e.g. property p and its super property, so check if
			// we already merged this one
			if (head.isSame(next)) {
				continue;
			}

			ds = ds.union(edge.getDepends(), abox.doExplanation());

			if (next.isDifferent(head)) {
				ds = ds.union(next.getDifferenceDependency(head), abox.doExplanation());
				if (r.isFunctional()) {
					abox.setClash(Clash.functionalCardinality(x, ds, r.getName()));
				}
				else {
					abox.setClash(Clash.maxCardinality(x, ds, r.getName(), 1));
				}

				break;
			}

			if (log.isLoggable(Level.FINE)) {
				log.fine("FUNC: " + x + " for prop " + r + " merge " + next + " -> " + head + " " + ds);
			}

			mergeTo(next, head, ds);

			if (abox.isClosed()) {
				return;
			}

			if (head.isPruned()) {
				ds = ds.union(head.getMergeDependency(true), abox.doExplanation());
				head = head.getSame();
			}
		}
	}

	private void mergeLater(Node y, Node z, DependencySet ds) {
		mergeList.add(new NodeMerge(y, z, ds));
	}

	/**
	 * Merge all node pairs in the queue.
	 */
	public void mergeAll() {
		if (mergingAll) {
			return;
		}

		mergingAll = true;
		while (!merging && !mergeList.isEmpty() && !abox.isClosed()) {
			NodeMerge merge = mergeList.remove(0);

			Node y = abox.getNode(merge.getSource());
			Node z = abox.getNode(merge.getTarget());
			DependencySet ds = merge.getDepends();

			if (y.isMerged()) {
				ds = ds.union(y.getMergeDependency(true), abox.doExplanation());
				y = y.getSame();
			}

			if (z.isMerged()) {
				ds = ds.union(z.getMergeDependency(true), abox.doExplanation());
				z = z.getSame();
			}

			if (y.isPruned() || z.isPruned()) {
				continue;
			}

			mergeTo(y, z, ds);
		}
		mergingAll = false;
	}

	/**
	 * Merge node y into z. Node y and all its descendants will be pruned from the completion graph.
	 * 
	 * @param y
	 *            Node being pruned
	 * @param z
	 *            Node that is being merged into
	 * @param ds
	 *            Dependency of this merge operation
	 */
	public void mergeTo(Node y, Node z, DependencySet ds) {

		// add to effected list
		if (abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS) {
			abox.getBranchEffectTracker().add(abox.getBranch(), y.getName());
			abox.getBranchEffectTracker().add(abox.getBranch(), z.getName());
		}

		// add to merge dependency to dependency index
		if (PelletOptions.USE_INCREMENTAL_DELETION) {
			abox.getKB().getDependencyIndex().addMergeDependency(y.getName(), z.getName(), ds);
		}

		if (y.isDifferent(z)) {
			abox.setClash(Clash.nominal(y, y.getDifferenceDependency(z).union(ds, abox.doExplanation())));
			return;
		}
		else if (!y.isSame(z)) {
			abox.setChanged(true);

			if (merging) {
				mergeLater(y, z, ds);
				return;
			}

			merging = true;

			if (log.isLoggable(Level.FINE)) {
				log.fine("MERG: " + y + " -> " + z + " " + ds);
			}

			ds = ds.copy(abox.getBranch());

			if (y instanceof Literal && z instanceof Literal) {
				mergeLiterals((Literal) y, (Literal) z, ds);
			}
			else if (y instanceof Individual && z instanceof Individual) {
				mergeIndividuals((Individual) y, (Individual) z, ds);
			}
			else {
				throw new InternalReasonerException("Invalid merge operation!");
			}
		}

		merging = false;
		mergeAll();
	}

	/**
	 * Merge individual y into x. Individual y and all its descendants will be pruned from the completion graph.
	 * 
	 * @param y
	 *            Individual being pruned
	 * @param x
	 *            Individual that is being merged into
	 * @param ds
	 *            Dependency of this merge operation
	 */
	protected void mergeIndividuals(Individual y, Individual x, DependencySet ds) {
		y.setSame(x, ds);

		// if both x and y are blockable x still remains blockable (nominal level
		// is still set to BLOCKABLE), if one or both are nominals then x becomes
		// a nominal with the minimum level
		x.setNominalLevel(Math.min(x.getNominalLevel(), y.getNominalLevel()));

		// copy the types
		Map<ATermAppl, DependencySet> types = y.getDepends();
		for (Map.Entry<ATermAppl, DependencySet> entry : types.entrySet()) {
			ATermAppl yType = entry.getKey();
			DependencySet finalDS = ds.union(entry.getValue(), abox.doExplanation());
			addType(x, yType, finalDS);
		}

		// for all edges (z, r, y) add an edge (z, r, x)
		EdgeList inEdges = y.getInEdges();
		for (int e = 0; e < inEdges.size(); e++) {
			Edge edge = inEdges.edgeAt(e);

			Individual z = edge.getFrom();
			Role r = edge.getRole();
			DependencySet finalDS = ds.union(edge.getDepends(), abox.doExplanation());

			// if y has a self edge then x should have the same self edge
			if (y.equals(z)) {
				addEdge(x, r, x, finalDS);
			}
			// if z is already a successor of x add the reverse edge
			else if (x.hasSuccessor(z)) {
				// FIXME what if there were no inverses in this expressitivity
				addEdge(x, r.getInverse(), z, finalDS);
			}
			else {
				addEdge(z, r, x, finalDS);
			}

			// only remove the edge from z and keep a copy in y for a
			// possible restore operation in the future
			z.removeEdge(edge);

			// add to effected list of queue
			// if( abox.getBranch() >= 0 && PelletOptions.USE_COMPLETION_QUEUE ) {
			// abox.getCompletionQueue().addEffected( abox.getBranch(), z.getName() );
			// }
			if (abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS) {
				abox.getBranchEffectTracker().add(abox.getBranch(), z.getName());
			}

		}

		// for all z such that y != z set x != z
		x.inheritDifferents(y, ds);

		// we want to prune y early due to an implementation issue about literals
		// if y has an outgoing edge to a literal with concrete value
		y.prune(ds);

		// for all edges (y, r, z) where z is a nominal add an edge (x, r, z)
		EdgeList outEdges = y.getOutEdges();
		for (int e = 0; e < outEdges.size(); e++) {
			Edge edge = outEdges.edgeAt(e);
			Node z = edge.getTo();

			if (z.isNominal() && !y.equals(z)) {
				Role r = edge.getRole();
				DependencySet finalDS = ds.union(edge.getDepends(), abox.doExplanation());

				addEdge(x, r, z, finalDS);

				// add to effected list
				if (abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS) {
					abox.getBranchEffectTracker().add(abox.getBranch(), z.getName());
				}

				// do not remove edge here because prune will take care of that
			}
		}
	}

	/**
	 * Merge literal y into x. Literal y will be pruned from* the completion graph.
	 * 
	 * @param y
	 *            Literal being pruned
	 * @param x
	 *            Literal that is being merged into
	 * @param ds
	 *            Dependency of this merge operation
	 */
	protected void mergeLiterals(Literal y, Literal x, DependencySet ds) {
		y.setSame(x, ds);

		x.addAllTypes(y.getDepends(), ds);

		// for all edges (z, r, y) add an edge (z, r, x)
		EdgeList inEdges = y.getInEdges();
		for (int e = 0; e < inEdges.size(); e++) {
			Edge edge = inEdges.edgeAt(e);

			Individual z = edge.getFrom();
			Role r = edge.getRole();
			DependencySet finalDS = ds.union(edge.getDepends(), abox.doExplanation());

			addEdge(z, r, x, finalDS);

			// only remove the edge from z and keep a copy in y for a
			// possible restore operation in the future
			z.removeEdge(edge);

			// add to effected list
			if (abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS) {
				abox.getBranchEffectTracker().add(abox.getBranch(), z.getName());
			}
		}

		x.inheritDifferents(y, ds);

		y.prune(ds);

		if (x.getNodeDepends() == null || y.getNodeDepends() == null) {
			throw new NullPointerException();
		}
	}

	public void restoreLocal(Individual ind, Branch br) {
		abox.stats.localRestores++;
		abox.setClash(null);
		abox.setBranch(br.getBranch());

		Map<Node, Boolean> visited = new HashMap<Node, Boolean>();

		restoreLocal(ind, br.getBranch(), visited);

		for (Map.Entry<Node, Boolean> entry : visited.entrySet()) {
			boolean restored = entry.getValue();
			if (restored) {
				allValuesRule.apply((Individual) entry.getKey());
			}
		}
	}

	private void restoreLocal(Individual ind, int branch, Map<Node, Boolean> visited) {
		boolean restored = ind.restore(branch);
		visited.put(ind, restored);

		if (restored) {
			for (Edge edge : ind.getOutEdges()) {
				Node succ = edge.getTo();
				if (visited.containsKey(succ)) {
					continue;
				}

				if (succ.isLiteral()) {
					visited.put(succ, Boolean.FALSE);
					succ.restore(branch);
				}
				else {
					restoreLocal(((Individual) succ), branch, visited);
				}
			}

			for (Edge edge : ind.getInEdges()) {
				Individual pred = edge.getFrom();
				if (visited.containsKey(pred)) {
					continue;
				}
				restoreLocal(pred, branch, visited);
			}
		}
	}

	public void restore(Branch br) {
		// Timers timers = abox.getKB().timers;
		// Timer timer = timers.startTimer("restore");
		abox.setBranch(br.getBranch());
		abox.setClash(null);
		// Setting the anonCount to the value at the time of branch creation is incorrect
		// when SMART_RESTORE option is turned on. If we create an anon node after branch
		// creation but node depends on an earlier branch restore operation will not remove
		// the node. But setting anonCount to a smaller number may mean the anonCount will
		// be incremented to that value and creating a fresh anon node will actually reuse
		// the not-removed node. The only advantage of setting anonCount to a smaller value
		// is to keep the name of anon nodes smaller to make debugging easier. For this reason,
		// the above line is not removed and under special circumstances may be uncommented
		// to help debugging only with the intent that it will be commented again after
		// debugging is complete
		// abox.setAnonCount( br.getAnonCount() );
		abox.rulesNotApplied = true;
		mergeList.clear();

		List<ATermAppl> nodeList = abox.getNodeNames();

		if (log.isLoggable(Level.FINE)) {
			log.fine("RESTORE: Branch " + br.getBranch());
		}

		if (PelletOptions.USE_COMPLETION_QUEUE) {
			// clear the all values list as they must have already fired and blocking never prevents the all values rule
			// from firing
			abox.getCompletionQueue().clearQueue(NodeSelector.UNIVERSAL);

			// reset the queues
			abox.getCompletionQueue().restore(br.getBranch());
		}

		// the restore may cause changes which require using the allValuesRule -
		// incremental change tracker will track those
		if (PelletOptions.USE_INCREMENTAL_CONSISTENCY) {
			abox.getIncrementalChangeTracker().clear();
		}

		// for each node we either need to restore the node to the status it
		// had at the time branch was created or remove the node completely if
		// it was created after the branch. To optimize removing elements from
		// the ArrayList we compute the block to be deleted and then remove all
		// at once to utilize the underlying System.arraycopy operation.

		// number of nodes in the nodeList
		int nodeCount = nodeList.size();
		// number of nodes
		int deleteBlock = 0;
		for (int i = 0; i < nodeCount; i++) {
			// get the node name
			ATermAppl a = nodeList.get(i);
			// and the corresponding node
			Node node = abox.getNode(a);

			// node dependency tells us if the node was created after the branch
			// and if that is the case we remove it completely
			// NOTE: for literals, node.getNodeDepends() may be null when a literal value branch is
			// restored, in that case we can remove the literal since there is no other reference
			// left for that literal
			if (node.getNodeDepends() == null || node.getNodeDepends().getBranch() > br.getBranch()) {
				// remove the node from the node map
				abox.removeNode(a);
				// if the node is merged to another one we should remove it from
				// the other node's merged list
				if (node.isMerged()) {
					node.undoSetSame();
				}
				// increment the size of block that will be deleted
				deleteBlock++;
			}
			else {
				// this node will be restored to previous state not removed

				// first if there are any nodes collected earlier delete them
				if (deleteBlock > 0) {
					// create the sub list for nodes to be removed
					List<ATermAppl> subList = nodeList.subList(i - deleteBlock, i);
					if (log.isLoggable(Level.FINE)) {
						log.fine("Remove nodes " + subList);
					}
					// clear the sublist causing all elements to removed from nodeList
					subList.clear();
					// update counters
					nodeCount -= deleteBlock;
					i -= deleteBlock;
					deleteBlock = 0;
				}

				// restore only if not tracking branch effects
				if (!PelletOptions.TRACK_BRANCH_EFFECTS) {
					node.restore(br.getBranch());
				}
			}
		}

		// if there were nodes to be removed at the end of the list do it now
		if (deleteBlock > 0) {
			nodeList.subList(nodeCount - deleteBlock, nodeCount).clear();
		}

		if (PelletOptions.TRACK_BRANCH_EFFECTS) {
			// when tracking branch effects only restore nodes explicitly stored in the effected list
			Set<ATermAppl> effected = abox.getBranchEffectTracker().removeAll(br.getBranch() + 1);
			for (ATermAppl a : effected) {
				Node n = abox.getNode(a);
				if (n != null) {
					n.restore(br.getBranch());
				}
			}
		}

		restoreAllValues();

		if (log.isLoggable(Level.FINE)) {
			abox.printTree();
		}

		if (!abox.isClosed()) {
			abox.validate();
		}

		// timer.stop();
	}

	public void addBranch(Branch newBranch) {
		abox.getBranches().add(newBranch);

		if (newBranch.getBranch() != abox.getBranches().size()) {
			throw new RuntimeException("Invalid branch created: "
			                           + newBranch.getBranch()
			                           + " != "
			                           + abox.getBranches().size());
		}

		completionTimer.check();

		// CHW - added for incremental deletion support
		if (PelletOptions.USE_INCREMENTAL_DELETION) {
			abox.getKB().getDependencyIndex().addBranchAddDependency(newBranch);
		}
	}

	void printBlocked() {
		int blockedCount = 0;
		StringBuffer blockedNodes = new StringBuffer();
		Iterator<Individual> n = abox.getIndIterator();
		while (n.hasNext()) {
			Individual node = n.next();
			ATermAppl x = node.getName();

			if (blocking.isBlocked(node)) {
				blockedCount++;
				blockedNodes.append(x).append(" ");
			}
		}

		log.fine("Blocked nodes " + blockedCount + " [" + blockedNodes + "]");
	}

	@Override
	public String toString() {
		String name = getClass().getName();
		int lastIndex = name.lastIndexOf('.');
		return name.substring(lastIndex + 1);
	}

	protected void restoreAllValues() {
		for (Iterator<Individual> i = new IndividualIterator(abox); i.hasNext();) {
			Individual ind = i.next();
			allValuesRule.apply(ind);
		}
	}
}
