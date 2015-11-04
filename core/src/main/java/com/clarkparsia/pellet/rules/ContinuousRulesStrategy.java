// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.branch.RuleBranch;
import org.mindswap.pellet.tableau.completion.SROIQStrategy;
import org.mindswap.pellet.tableau.completion.rule.TableauRule;
import org.mindswap.pellet.utils.Pair;
import org.mindswap.pellet.utils.Timer;

import aterm.ATermAppl;

import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.AlphaNetwork;
import com.clarkparsia.pellet.rules.rete.Compiler;
import com.clarkparsia.pellet.rules.rete.Interpreter;

public class ContinuousRulesStrategy extends SROIQStrategy {
	private BindingGeneratorStrategy					bindingStrategy;
	private Interpreter									interpreter;
	private boolean										merging;
	private Set<PartialBinding>							unsafeRules;
	private Queue<PartialBinding>						partialBindings;
	private Map<Pair<Rule, VariableBinding>, Integer>	rulesApplied;
	private RulesToATermTranslator						atermTranslator;
	private RuleAtomAsserter							ruleAtomAsserter;
	private TrivialSatisfactionHelpers					atomTester;

	public ContinuousRulesStrategy(ABox abox) {
		super( abox );
		bindingStrategy = new BindingGeneratorStrategyImpl( abox );
		partialBindings = new LinkedList<PartialBinding>();
		unsafeRules = new HashSet<PartialBinding>();
		rulesApplied = new HashMap<Pair<Rule, VariableBinding>, Integer>();
		atermTranslator = new RulesToATermTranslator();
		ruleAtomAsserter = new RuleAtomAsserter();
		atomTester = new TrivialSatisfactionHelpers( abox );
	}

	public void addUnsafeRule(Rule rule, Set<ATermAppl> explain) {
		unsafeRules.add(new PartialBinding(rule, new VariableBinding(abox), new DependencySet(explain)));
	}

	public void addPartialBinding(PartialBinding binding) {
		if( !partialBindings.contains(binding) )
			partialBindings.add(binding);
	}

	@Override
	public Edge addEdge(Individual subj, Role pred, Node obj, DependencySet ds) {
		Edge edge = super.addEdge( subj, pred, obj, ds );

		if( edge != null && !abox.isClosed() && subj.isRootNominal() && obj.isRootNominal() ) {
			if( interpreter != null ) {
				interpreter.alphaNet.activateEdge(edge);
			}
		}

		return edge;
	}

	@Override
	public void addType(Node node, ATermAppl c, DependencySet ds) {
		super.addType( node, c, ds );

		if( !merging && !abox.isClosed() && node.isRootNominal() && interpreter != null && node.isIndividual() ) {
			Individual ind = (Individual) node;
			interpreter.alphaNet.activateType(ind, c, ds);
		}
	}

	@Override
	protected boolean mergeIndividuals(Individual y, Individual x, DependencySet ds) {
		if (super.mergeIndividuals(y, x, ds)) {
			if (interpreter != null) {
				interpreter.alphaNet.activateDifferents(y);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean setDifferent(Node y, Node z, DependencySet ds) {
		if (super.setDifferent(y, z, ds)) {
			if( interpreter != null && !merging && !abox.isClosed() && y.isRootNominal() && y.isIndividual() && z.isRootNominal() && z.isIndividual()) {
				interpreter.alphaNet.activateDifferent((Individual) y, (Individual) z, ds);
			}

			return true;
		}

		return false;
	}

	public Collection<PartialBinding> applyRete() {
		Timer t;
		if( PelletOptions.ALWAYS_REBUILD_RETE ) {
			t = timers.startTimer( "rule-rebuildRete" );

			partialBindings.clear();
			partialBindings.addAll(unsafeRules);
			interpreter.reset();
			t.stop();
		}

		t = timers.startTimer( "rule-reteRun" );
		interpreter.run();
		t.stop();

		return interpreter.getBindings();
	}

	public void applyRuleBindings() {

		int total = 0;

		for( PartialBinding ruleBinding = partialBindings.poll(); ruleBinding != null; ruleBinding = partialBindings.poll() ) {
			Rule rule = ruleBinding.getRule();
			VariableBinding initial = ruleBinding.getBinding();

			for( VariableBinding binding : bindingStrategy.createGenerator( rule, initial ) ) {

				Pair<Rule, VariableBinding> ruleKey = new Pair<Rule, VariableBinding>( rule,
						binding );
				if( !rulesApplied.containsKey( ruleKey ) ) {
					total++;

					if( log.isLoggable( Level.FINE ) ) {
						log.fine( "Rule: " + rule );
						log.fine( "Binding: " + binding );
						log.fine( "total:" + total );
					}

					int branch = createDisjunctionsFromBinding( binding, rule, ruleBinding
							.getDependencySet() );

					if( branch >= 0 ) {
						rulesApplied.put( ruleKey, branch );
					}

					if( abox.isClosed() ) {
						return;
					}
				}
			}

		}
	}

	public void complete(Expressivity expr) {
		Timer t;

		Expressivity expressivity = abox.getKB().getExpressivity();

		initialize( expressivity );

		merging = false;
		t = timers.startTimer( "rule-buildReteRules" );
		Compiler compiler = new Compiler(this);
		for (Entry<Rule, Rule> e : abox.getKB().getNormalizedRules().entrySet()) {
			Rule rule = e.getKey();
			Rule normalizedRule = e.getValue();

			if (normalizedRule == null)
				continue;

			Set<ATermAppl> explain = abox.doExplanation() ? rule.getExplanation(atermTranslator) : Collections
							.<ATermAppl> emptySet();

			try {
				compiler.compile(normalizedRule, explain);
			}
			catch(UnsupportedOperationException uoe) {
				throw new RuntimeException("Unsupported rule " + normalizedRule, uoe);
			}
		}
		t.stop();

		AlphaNetwork alphaNet = compiler.getAlphaNet();
		if (abox.doExplanation()) {
			alphaNet.setDoExplanation(true);
		}
		interpreter = new Interpreter(alphaNet);
		partialBindings.clear();
		partialBindings.addAll(unsafeRules);
		rulesApplied.clear();


//		t.stop();		

//		t = timers.startTimer( "rule-compileReteFacts" );
		applyRete();
//		t.stop();

		while( !abox.isComplete() ) {
			while( abox.isChanged() && !abox.isClosed() ) {
				completionTimer.check();

				abox.setChanged( false );

				if( log.isLoggable( Level.FINE ) ) {
					log.fine( "Branch: " + abox.getBranch() + ", Depth: " + abox.stats.treeDepth
							+ ", Size: " + abox.getNodes().size() + ", Mem: "
							+ (Runtime.getRuntime().freeMemory() / 1000) + "kb" );
					abox.validate();
					// printBlocked();
					abox.printTree();
					interpreter.alphaNet.print();
				}

				IndividualIterator i = abox.getIndIterator();

				for( TableauRule tableauRule : tableauRules ) {
					tableauRule.apply( i );
					if( abox.isClosed() )
						break;
				}

				if( abox.isClosed() )
					break;

				if (!abox.isChanged() && !partialBindings.isEmpty()) {
//					t = timers.startTimer( "rule-bindings" );
					applyRuleBindings();
//					t.stop();
					if( abox.isClosed() )
						break;
				}

			}

			if( abox.isClosed() ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Clash at Branch (" + abox.getBranch() + ") " + abox.getClash() );

				if( backtrack() )
					abox.setClash( null );
				else
					abox.setComplete( true );
			}
			else {
				if( PelletOptions.SATURATE_TABLEAU ) {
					Branch unexploredBranch = null;
					for( int i = abox.getBranches().size() - 1; i >= 0; i-- ) {
						unexploredBranch = abox.getBranches().get( i );
						unexploredBranch.setTryNext( unexploredBranch.getTryNext() + 1 );
						if( unexploredBranch.getTryNext() < unexploredBranch.getTryCount() ) {
							restore( unexploredBranch );
							System.out.println( "restoring branch " + unexploredBranch.getBranch()
									+ " tryNext = " + unexploredBranch.getTryNext()
									+ " tryCount = " + unexploredBranch.getTryCount() );
							unexploredBranch.tryNext();
							break;
						}
						else {
							System.out.println( "removing branch " + unexploredBranch.getBranch() );
							abox.getBranches().remove( i );
							unexploredBranch = null;
						}
					}
					if( unexploredBranch == null ) {
						abox.setComplete( true );
					}
				}
				else
					abox.setComplete( true );
			}
		}
	}

	private int createDisjunctionsFromBinding(VariableBinding binding, Rule rule, DependencySet ds) {
		List<RuleAtom> atoms = new ArrayList<RuleAtom>();

		for( RuleAtom atom : rule.getBody() ) {
			DependencySet atomDS = atomTester.isAtomTrue( atom, binding );
			if( atomDS != null ) {
				ds = ds.union( atomDS, abox.doExplanation() );
			}
			else {
				atoms.add( atom );
			}
		}

		// all the atoms in the body are true
		if( atoms.isEmpty() ) {
			if( rule.getHead().isEmpty() ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Empty head for rule " + rule );
				abox.setClash( Clash.unexplained( null, ds ) );
			}
			else {
				for( RuleAtom atom : rule.getHead() ) {
					ruleAtomAsserter.assertAtom( atom, binding, ds, false, abox, this );
				}
			}
			return -1;
		}

		int bodyAtomCount = atoms.size();

		for( RuleAtom atom : rule.getHead() ) {
			DependencySet atomDS = atomTester.isAtomTrue( atom, binding );
			if( atomDS == null ) {
				atoms.add( atom );
			}
		}

		// all no head atoms are added to the list they are all true (unless
		// there were no head atoms to begin with) which means there is nothing
		// to be done
		if( atoms.size() == bodyAtomCount && !rule.getHead().isEmpty() ) {
			return -1;
		}
		// if there is only one atom in the list that should be a body atom
		// (otherwise it would mean that all body atoms are true which would
		// have been caught with the if condition at the beginning) and we
		// can directly assert it without creating a disjunction
		else if( atoms.size() == 1 ) {
			ruleAtomAsserter.assertAtom( atoms.get( 0 ), binding, ds, true, abox, this );
			return -1;
		}
		else {
			RuleBranch r = new RuleBranch( abox, this, ruleAtomAsserter, atoms, binding, bodyAtomCount, ds );
			addBranch( r );
			r.tryNext();
			return r.getBranch();
		}
	}

	@Override
	public void mergeTo(Node y, Node z, DependencySet ds) {
		merging = true;
		super.mergeTo( y, z, ds );
		if( !abox.isClosed() && (interpreter != null) && (y.isRootNominal() || z.isRootNominal()) ) {
//			if( y.isRootNominal() )
//				runRules |= interpreter.removeMentions( y.getTerm() );
//			if( z.isIndividual() )
//				runRules |= interpreter.rete.processIndividual( (Individual) z );
		}
		merging = false;
	}

	@Override
	public void restore(Branch branch) {
		super.restore( branch );
		restoreRules( branch );
	}

	@Override
	public void restoreLocal(Individual ind, Branch branch) {
		super.restoreLocal( ind, branch );
		restoreRules( branch );
	}

	private void restoreRules(Branch branch) {
		int total = 0;
		for( Iterator<Map.Entry<Pair<Rule, VariableBinding>, Integer>> ruleAppIter = rulesApplied
				.entrySet().iterator(); ruleAppIter.hasNext(); ) {
			Map.Entry<Pair<Rule, VariableBinding>, Integer> ruleBranchEntry = ruleAppIter.next();
			if( ruleBranchEntry.getValue() > branch.getBranch() ) {
				// System.out.println( "Removing " + ruleBranchEntry.getKey() );
				ruleAppIter.remove();
				total++;
			}
		}

		for( Iterator<PartialBinding> iter = partialBindings.iterator(); iter
				.hasNext(); ) {
			PartialBinding binding = iter.next();
			if( binding.getBranch() > branch.getBranch() ) {
				iter.remove();
			}
		}

		interpreter.restore( branch.getBranch() );
		// rebuildFacts = true;
	}
}
