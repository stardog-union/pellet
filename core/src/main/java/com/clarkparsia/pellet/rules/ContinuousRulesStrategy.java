// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import aterm.ATermAppl;
import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.AlphaNetwork;
import com.clarkparsia.pellet.rules.rete.Compiler;
import com.clarkparsia.pellet.rules.rete.Interpreter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public class ContinuousRulesStrategy extends SROIQStrategy
{
	private final BindingGeneratorStrategy bindingStrategy;
	private Interpreter interpreter;
	private boolean merging;
	private final Set<PartialBinding> unsafeRules;
	private final Set<PartialBinding> partialBindings;
	private final Map<Pair<Rule, VariableBinding>, Integer> rulesApplied;
	private final RulesToATermTranslator atermTranslator;
	private final RuleAtomAsserter ruleAtomAsserter;
	private final TrivialSatisfactionHelpers atomTester;

	public ContinuousRulesStrategy(final ABox abox)
	{
		super(abox);
		bindingStrategy = new BindingGeneratorStrategyImpl(abox);
		partialBindings = new HashSet<>();
		unsafeRules = new HashSet<>();
		rulesApplied = new HashMap<>();
		atermTranslator = new RulesToATermTranslator();
		ruleAtomAsserter = new RuleAtomAsserter();
		atomTester = new TrivialSatisfactionHelpers(abox);
	}

	public void addUnsafeRule(final Rule rule, final Set<ATermAppl> explain)
	{
		unsafeRules.add(new PartialBinding(rule, new VariableBinding(_abox), new DependencySet(explain)));
	}

	public void addPartialBinding(final PartialBinding binding)
	{
		partialBindings.add(binding);
	}

	@Override
	public Edge addEdge(final Individual subj, final Role pred, final Node obj, final DependencySet ds)
	{
		final Edge edge = super.addEdge(subj, pred, obj, ds);

		if (edge != null && !_abox.isClosed() && subj.isRootNominal() && obj.isRootNominal())
			if (interpreter != null)
				interpreter.alphaNet.activateEdge(edge);

		return edge;
	}

	@Override
	public void addType(final Node node, final ATermAppl c, final DependencySet ds)
	{
		super.addType(node, c, ds);

		if (!merging && !_abox.isClosed() && node.isRootNominal() && interpreter != null && node.isIndividual())
		{
			final Individual ind = (Individual) node;
			interpreter.alphaNet.activateType(ind, c, ds);
		}
	}

	@Override
	protected boolean mergeIndividuals(final Individual y, final Individual x, final DependencySet ds)
	{
		if (super.mergeIndividuals(y, x, ds))
		{
			if (interpreter != null)
				interpreter.alphaNet.activateDifferents(y);
			return true;
		}
		return false;
	}

	@Override
	public boolean setDifferent(final Node y, final Node z, final DependencySet ds)
	{
		if (super.setDifferent(y, z, ds))
		{
			if (interpreter != null && !merging && !_abox.isClosed() && y.isRootNominal() && y.isIndividual() && z.isRootNominal() && z.isIndividual())
				interpreter.alphaNet.activateDifferent((Individual) y, (Individual) z, ds);

			return true;
		}

		return false;
	}

	public Collection<PartialBinding> applyRete()
	{
		Timer t;
		if (PelletOptions.ALWAYS_REBUILD_RETE)
		{
			t = _timers.startTimer("rule-rebuildRete");

			partialBindings.clear();
			partialBindings.addAll(unsafeRules);
			interpreter.reset();
			t.stop();
		}

		t = _timers.startTimer("rule-reteRun");
		interpreter.run();
		t.stop();

		return interpreter.getBindings();
	}

	public void applyRuleBindings()
	{

		int total = 0;

		for (final PartialBinding ruleBinding : partialBindings)
		{
			final Rule rule = ruleBinding.getRule();
			final VariableBinding initial = ruleBinding.getBinding();

			for (final VariableBinding binding : bindingStrategy.createGenerator(rule, initial))
			{

				final Pair<Rule, VariableBinding> ruleKey = new Pair<>(rule, binding);
				if (!rulesApplied.containsKey(ruleKey))
				{
					total++;

					if (log.isLoggable(Level.FINE))
					{
						log.fine("Rule: " + rule);
						log.fine("Binding: " + binding);
						log.fine("total:" + total);
					}

					final int branch = createDisjunctionsFromBinding(binding, rule, ruleBinding.getDependencySet());

					if (branch >= 0)
						rulesApplied.put(ruleKey, branch);

					if (_abox.isClosed())
						return;
				}
			}

		}
	}

	@Override
	public void complete(final Expressivity expr)
	{
		Timer t;

		final Expressivity expressivity = _abox.getKB().getExpressivity();

		initialize(expressivity);

		merging = false;
		t = _timers.startTimer("rule-buildReteRules");
		final Compiler compiler = new Compiler(this);
		for (final Entry<Rule, Rule> e : _abox.getKB().getNormalizedRules().entrySet())
		{
			final Rule rule = e.getKey();
			final Rule normalizedRule = e.getValue();

			if (normalizedRule == null)
				continue;

			final Set<ATermAppl> explain = _abox.doExplanation() ? rule.getExplanation(atermTranslator) : Collections.<ATermAppl> emptySet();

			try
			{
				compiler.compile(normalizedRule, explain);
			}
			catch (final UnsupportedOperationException uoe)
			{
				throw new RuntimeException("Unsupported rule " + normalizedRule, uoe);
			}
		}
		t.stop();

		final AlphaNetwork alphaNet = compiler.getAlphaNet();
		if (_abox.doExplanation())
			alphaNet.setDoExplanation(true);
		interpreter = new Interpreter(alphaNet);
		partialBindings.clear();
		partialBindings.addAll(unsafeRules);
		rulesApplied.clear();

		//		t.stop();

		//		t = _timers.startTimer( "rule-compileReteFacts" );
		applyRete();
		//		t.stop();

		while (!_abox.isComplete())
		{
			while (_abox.isChanged() && !_abox.isClosed())
			{
				_completionTimer.check();

				_abox.setChanged(false);

				if (log.isLoggable(Level.FINE))
				{
					log.fine("Branch: " + _abox.getBranch() + ", Depth: " + _abox.stats.treeDepth + ", Size: " + _abox.getNodes().size() + ", Mem: " + (Runtime.getRuntime().freeMemory() / 1000) + "kb");
					_abox.validate();
					// printBlocked();
					_abox.printTree();
					interpreter.alphaNet.print();
				}

				final IndividualIterator i = _abox.getIndIterator();

				for (final TableauRule tableauRule : _tableauRules)
				{
					tableauRule.apply(i);
					if (_abox.isClosed())
						break;
				}

				if (_abox.isClosed())
					break;

				if (!_abox.isChanged() && !partialBindings.isEmpty())
				{
					//					t = _timers.startTimer( "rule-bindings" );
					applyRuleBindings();
					//					t.stop();
					if (_abox.isClosed())
						break;
				}

			}

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
				if (PelletOptions.SATURATE_TABLEAU)
				{
					Branch unexploredBranch = null;
					for (int i = _abox.getBranches().size() - 1; i >= 0; i--)
					{
						unexploredBranch = _abox.getBranches().get(i);
						unexploredBranch.setTryNext(unexploredBranch.getTryNext() + 1);
						if (unexploredBranch.getTryNext() < unexploredBranch.getTryCount())
						{
							restore(unexploredBranch);
							System.out.println("restoring _branch " + unexploredBranch.getBranch() + " _tryNext = " + unexploredBranch.getTryNext() + " _tryCount = " + unexploredBranch.getTryCount());
							unexploredBranch.tryNext();
							break;
						}
						else
						{
							System.out.println("removing _branch " + unexploredBranch.getBranch());
							_abox.getBranches().remove(i);
							unexploredBranch = null;
						}
					}
					if (unexploredBranch == null)
						_abox.setComplete(true);
				}
				else
					_abox.setComplete(true);
		}
	}

	private int createDisjunctionsFromBinding(final VariableBinding binding, final Rule rule, DependencySet ds)
	{
		final List<RuleAtom> atoms = new ArrayList<>();

		for (final RuleAtom atom : rule.getBody())
		{
			final DependencySet atomDS = atomTester.isAtomTrue(atom, binding);
			if (atomDS != null)
				ds = ds.union(atomDS, _abox.doExplanation());
			else
				atoms.add(atom);
		}

		// all the atoms in the body are true
		if (atoms.isEmpty())
		{
			if (rule.getHead().isEmpty())
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Empty head for rule " + rule);
				_abox.setClash(Clash.unexplained(null, ds));
			}
			else
				for (final RuleAtom atom : rule.getHead())
					ruleAtomAsserter.assertAtom(atom, binding, ds, false, _abox, this);
			return -1;
		}

		final int bodyAtomCount = atoms.size();

		for (final RuleAtom atom : rule.getHead())
		{
			final DependencySet atomDS = atomTester.isAtomTrue(atom, binding);
			if (atomDS == null)
				atoms.add(atom);
		}

		// all no head atoms are added to the list they are all true (unless
		// there were no head atoms to begin with) which means there is nothing
		// to be done
		if (atoms.size() == bodyAtomCount && !rule.getHead().isEmpty())
			return -1;
		else
			if (atoms.size() == 1)
			{
				ruleAtomAsserter.assertAtom(atoms.get(0), binding, ds, true, _abox, this);
				return -1;
			}
			else
			{
				final RuleBranch r = new RuleBranch(_abox, this, ruleAtomAsserter, atoms, binding, bodyAtomCount, ds);
				addBranch(r);
				r.tryNext();
				return r.getBranch();
			}
	}

	@Override
	public void mergeTo(final Node y, final Node z, final DependencySet ds)
	{
		merging = true;
		super.mergeTo(y, z, ds);
		if (!_abox.isClosed() && (interpreter != null) && (y.isRootNominal() || z.isRootNominal()))
		{
			//			if( y.isRootNominal() )
			//				runRules |= interpreter.removeMentions( y.getTerm() );
			//			if( z.isIndividual() )
			//				runRules |= interpreter.rete.processIndividual( (Individual) z );
		}
		merging = false;
	}

	@Override
	public void restore(final Branch branch)
	{
		super.restore(branch);
		restoreRules(branch);
	}

	@Override
	public void restoreLocal(final Individual ind, final Branch branch)
	{
		super.restoreLocal(ind, branch);
		restoreRules(branch);
	}

	private void restoreRules(final Branch branch)
	{
		int total = 0;
		for (final Iterator<Map.Entry<Pair<Rule, VariableBinding>, Integer>> ruleAppIter = rulesApplied.entrySet().iterator(); ruleAppIter.hasNext();)
		{
			final Map.Entry<Pair<Rule, VariableBinding>, Integer> ruleBranchEntry = ruleAppIter.next();
			if (ruleBranchEntry.getValue() > branch.getBranch())
			{
				// System.out.println( "Removing " + ruleBranchEntry.getKey() );
				ruleAppIter.remove();
				total++;
			}
		}

		for (final Iterator<PartialBinding> iter = partialBindings.iterator(); iter.hasNext();)
		{
			final PartialBinding binding = iter.next();
			if (binding.getBranch() > branch.getBranch())
				iter.remove();
		}

		interpreter.restore(branch.getBranch());
		// rebuildFacts = true;
	}
}
