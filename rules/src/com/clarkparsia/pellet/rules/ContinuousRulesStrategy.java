// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
import com.clarkparsia.pellet.rules.rete.Compiler;
import com.clarkparsia.pellet.rules.rete.Fact;
import com.clarkparsia.pellet.rules.rete.Interpreter;

public class ContinuousRulesStrategy extends SROIQStrategy {

	private BindingGeneratorStrategy					bindingStrategy;
	private ContinuousReteTransformer					continuousTransformer;
	private Interpreter									interpreter;
	private boolean										merging;
	private Map<Fact, Integer>							partialBindings;
	private Map<Pair<Rule, VariableBinding>, Integer>	rulesApplied;
	private RulesToReteTranslator						ruleTranslator;
	private RulesToATermTranslator						atermTranslator;
	private RuleAtomAsserter							ruleAtomAsserter;
	private TrivialSatisfactionHelpers					atomTester;
	boolean												runRules;

	public ContinuousRulesStrategy(ABox abox) {
		super( abox );
		bindingStrategy = new BindingGeneratorStrategyImpl( abox );
		continuousTransformer = new ContinuousReteTransformer( abox );
		partialBindings = new HashMap<Fact, Integer>();
		rulesApplied = new HashMap<Pair<Rule, VariableBinding>, Integer>();
		ruleTranslator = new RulesToReteTranslator( abox );
		atermTranslator = new RulesToATermTranslator();
		ruleAtomAsserter = new RuleAtomAsserter();
		atomTester = new TrivialSatisfactionHelpers( abox );
		runRules = true;
	}

	@Override
	public void addEdge(Individual subj, Role pred, Node obj, DependencySet ds) {
		super.addEdge( subj, pred, obj, ds );

		if( !merging && !abox.isClosed() && subj.isRootNominal() && obj.isRootNominal() ) {
			if( interpreter != null ) {
				EdgeList edges = subj.getRNeighborEdges( pred, obj );
				for( Edge edge : edges ) {
					if( edge.getFrom().isRootNominal() && edge.getTo().isRootNominal() )
						runRules |= interpreter.rete.addFact( edge );
				}

				runRules |= interpreter.rete.addDifferents( subj );
				if( obj.isIndividual() )
					runRules |= interpreter.rete.addDifferents( (Individual) obj );
			}

		}

	}

	@Override
	public void addType(Node node, ATermAppl c, DependencySet ds) {
		super.addType( node, c, ds );

		if( !merging && !abox.isClosed() && node.isRootNominal() ) {
			if( interpreter != null && node.isIndividual() ) {
				Individual ind = (Individual) node;
				runRules |= interpreter.rete.addFact( ind, c, ind.getDepends( c ) );
				runRules |= interpreter.rete.addDifferents( (Individual) node );
			}
		}

	}

	private void applyFact(Fact fact) {

		// A fact with no elements means that the rule has an empty head and
		// hence the KB inconsistent.
		if( fact.getElements().size() == 0 ) {
			abox.setClash( Clash.unexplained( null, fact.getDependencySet() ) );
			log.log( Level.WARNING,  "Fact with no elements, create clash" );
			return;
		}

		if( fact.getElements().size() == 3 ) {
			DependencySet ds = fact.getDependencySet();
			
			if( log.isLoggable( Level.FINE ) )
				log.fine( "RULE: " + fact + " " + ds );

			ATermAppl pred = fact.getElements().get( Compiler.PRED );
			Individual subj = abox.getIndividual( fact.getElements().get( Compiler.SUBJ ) );
			if( subj.isMerged() ) {
				ds = ds.union( subj.getMergeDependency( true ), abox.doExplanation() );
				subj = subj.getSame();
			}

			ATermAppl objTerm = fact.getElements().get( Compiler.OBJ );

			if( pred.equals( Compiler.TYPE ) ) {
				// add a type assertion for the individual
				ATermAppl type = objTerm;
				addType( subj, type, ds );
			}
			else {
				Node obj = abox.getNode( objTerm );
				if( obj != null && obj.isMerged() ) {
//					ds = ds.union( ds, abox.doExplanation() );
					obj = obj.getSame();
				}

				if( pred.equals( Compiler.SAME_AS ) ) {
					Individual ind2 = (Individual) obj;
					mergeTo( ind2, subj, ds );
				}
				else if( pred.equals( Compiler.DIFF_FROM ) ) {
					Individual ind2 = (Individual) obj;
					subj.setDifferent( ind2, ds );
				}
				else {
					// add code for inferring roles, too
					Role r = abox.getRole( pred );
					if( obj == null && r.isDatatypeRole() ) {
						// Constant data values in rules may not be in the ABox.
						obj = abox.addLiteral( objTerm );
					}
					addEdge( subj, r, obj, ds );
				}
			}
		}
	}

	public void applyRete() {
		Timer t;
		if( PelletOptions.ALWAYS_REBUILD_RETE ) {
			t = timers.startTimer( "rule-rebuildRete" );
			interpreter.reset();
			interpreter.rete.compileFacts( abox );
			partialBindings.clear();
			t.stop();
		}

		if( !interpreter.isDirty() )
			return;

		t = timers.startTimer( "rule-reteRun" );
		Set<Fact> inferred = interpreter.run();
		t.stop();

		t = timers.startTimer( "rule-reteFacts" );
		for( Fact fact : inferred ) {
			assert (fact.getDependencySet().getBranch() == abox.getBranch());
			assert (fact.getDependencySet().max() <= abox.getBranch());
			applyFact( fact );
			if( abox.isClosed() ) {
				t.stop();
				return;
			}
			if( fact.getElements().size() > 3
					&& fact.getElements().get( Compiler.PRED ).equals(
							ContinuousReteTransformer.VARBINDING ) ) {
				if( !partialBindings.containsKey( fact ) ) {
					partialBindings.put( fact, abox.getBranch() );
				}
			}
		}
		t.stop();
	}

	public void applyRuleBindings() {

		int total = 0;

		for( Fact ruleBinding : partialBindings.keySet() ) {
			Pair<Rule, VariableBinding> pair = continuousTransformer.translateFact( ruleBinding );
			Rule rule = pair.first;
			VariableBinding initial = pair.second;

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
//		Timer t;

		Expressivity expressivity = abox.getKB().getExpressivity();

		initialize( expressivity );

		merging = false;
//		t = timers.startTimer( "rule-buildReteRules" );
		interpreter = new Interpreter( abox );
		for( Entry<Rule,Rule> e : abox.getKB().getNormalizedRules().entrySet() ) {
			Rule rule = e.getKey();
			Rule normalizedRule = e.getValue();
			
			if( normalizedRule == null )
				continue;
			
			com.clarkparsia.pellet.rules.rete.Rule reteRule;
			Set<ATermAppl> explain = abox.doExplanation()					
				? rule.getExplanation( atermTranslator )
				: Collections.<ATermAppl>emptySet();
				
			reteRule = ruleTranslator.translateRule( normalizedRule );
			if( reteRule != null ) {
				if( log.isLoggable( Level.FINER ) ) {
					log.finer( "SWRL Rule: " + rule );
					log.finer( "Rete Rule: " + reteRule );
					log.finer( "Term Rule: " + explain );
					log.finer( "===" );
				}
				interpreter.rete.compile( reteRule, explain );
			}
				
			reteRule = continuousTransformer.transformRule( normalizedRule );
			if( reteRule != null ) 
				interpreter.rete.compile( reteRule, explain );			
		}
		partialBindings.clear();
		rulesApplied.clear();


//		t.stop();
		
		if( log.isLoggable( Level.FINER ) )
			log.finer( "AlphaStore: " + interpreter.rete );			

//		t = timers.startTimer( "rule-compileReteFacts" );
		interpreter.rete.compileFacts( abox );
//		t.stop();

		while( interpreter.isDirty() && !abox.isClosed() )
			applyRete();

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
				}

				IndividualIterator i = abox.getIndIterator();

				for( TableauRule tableauRule : tableauRules ) {
					tableauRule.apply( i );
					if( abox.isClosed() )
						break;
				}
				
				if( abox.isClosed() )
					break;

				if( !abox.isChanged() ) {
//					t = timers.startTimer( "rule-rete" );
					while( interpreter.isDirty() && !abox.isClosed() )
						applyRete();
//					t.stop();
					if( abox.isClosed() )
						break;
				}

				if( !abox.isChanged() && runRules ) {
//					t = timers.startTimer( "rule-bindings" );
					runRules = false;
					applyRuleBindings();
					// applyRULERule();
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
			if( y.isRootNominal() )
				runRules |= interpreter.removeMentions( y.getTerm() );
			if( z.isIndividual() )
				runRules |= interpreter.rete.processIndividual( (Individual) z );
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
				runRules = true;
				total++;
			}
		}

		for( Iterator<Map.Entry<Fact, Integer>> iter = partialBindings.entrySet().iterator(); iter
				.hasNext(); ) {
			Map.Entry<Fact, Integer> entry = iter.next();
			if( entry.getValue() > branch.getBranch() ) {
				iter.remove();
				runRules = true;
			}
		}

		runRules |= interpreter.restore( branch.getBranch() );
		// rebuildFacts = true;
	}
}
