// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.branch.RuleBranch;
import org.mindswap.pellet.tableau.completion.SROIQStrategy;
import org.mindswap.pellet.tableau.completion.rule.TableauRule;

import aterm.ATermAppl;

import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.rete.Compiler;
import com.clarkparsia.pellet.rules.rete.Fact;
import com.clarkparsia.pellet.rules.rete.Interpreter;

public class RuleStrategy extends SROIQStrategy {
	
	private BindingGeneratorStrategy bindingStrategy;
	
	public RuleStrategy(ABox abox) {
		super( abox );
		bindingStrategy = new BindingGeneratorStrategyImpl( abox );
	}

	public void applyRULERule() {
		// go through the rule and create the aterms    	 
		for ( Rule rule : abox.getKB().getRules() ) {
			//create a binding

			// before enumerating the bindings for a particular rule, 
			// we can eliminate some of the individuals 
			// if a rule is A(x) ^ B(x,y) -> D(x) and for some ind. a, a:D,
			// then no need to generate any bindings including ind. a

			//find eligible individuals for this rule
//			total = 0;
//			findBinding( 0, bindings, vars, rule );

			
			int total = 0;
			for ( VariableBinding binding : bindingStrategy.createGenerator( rule ) ) {			
				if ( true ) { //!triviallySatisfiedAllBindings( binding, rule ) ) {
					total++;

					if( log.isLoggable( Level.FINE ) ) {
						log.fine( "Binding: "+binding );
						log.fine( "total:" + total );
					}
					if( !abox.isClosed() ) {
						createDisjunctionsFromBinding( binding, rule );
					}
				}				
			}
			
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "total bindings:" + total );
				log.fine( "branches:" + abox.getBranch() );
			}
		}
	}

	public void complete(Expressivity expr ) {
//		Timer t;

		Expressivity expressivity = abox.getKB().getExpressivity();

		initialize( expressivity );

		//run the RETE once when the rules are not applied 
		if( !abox.ranRete && abox.rulesNotApplied ) {
			// initialize and run the rete 
			Interpreter interp = new Interpreter( abox );

			RulesToReteTranslator translator = new RulesToReteTranslator( abox );
			RulesToATermTranslator atermTranslator = new RulesToATermTranslator();
			for( Rule rule : abox.getKB().getRules() ) {
				com.clarkparsia.pellet.rules.rete.Rule reteRule = 
					translator.translateRule( rule );
				
				if( reteRule != null ) {
					Set<ATermAppl> explain = abox.doExplanation()					
						? rule.getExplanation( atermTranslator )
						: Collections.<ATermAppl>emptySet();
					
					interp.rete.compile( reteRule, explain );
				}
			}
			
			interp.rete.compileFacts( abox );
			Set<Fact> inferred = interp.run();

			if( log.isLoggable( Level.FINE ) )
				log.fine( inferred.size() + " inferred fact(s)" );
			
			//need to add the inferred facts back to the tableau 
			DependencySet ds = DependencySet.INDEPENDENT;
			for ( Fact f : inferred ) {
				ATermAppl pred = f.getElements().get( Compiler.PRED );
				ATermAppl subj = f.getElements().get( Compiler.SUBJ );
				ATermAppl obj =  f.getElements().get( Compiler.OBJ );
				
				if( pred.equals( Compiler.TYPE ) ) {
					// add a type assertion for the individual
					Individual ind = abox.getIndividual( subj );
					ATermAppl type = obj;
					ind.addType( type, ds );
				} else if ( pred.equals( Compiler.SAME_AS ) ) {
					Individual ind1 = abox.getIndividual( subj );
					Individual ind2 = abox.getIndividual( obj );
					
					ind1.setSame( ind2, DependencySet.INDEPENDENT );
				} else if ( pred.equals( Compiler.DIFF_FROM ) ) {
					Individual ind1 = abox.getIndividual( subj );
					Individual ind2 = abox.getIndividual( obj );
					
					ind1.setDifferent( ind2, DependencySet.INDEPENDENT );
				} else {
					// add code for inferring roles, too
					Role r = abox.getRole( pred );
					Individual from = abox.getIndividual( subj );
					Node to;
					if ( r != null && r.isObjectRole()) {
						to = abox.getIndividual( obj );
					} else if ( r != null && r.isDatatypeRole()) {
						to = abox.getLiteral( obj );
						if ( to == null ) {
							to = abox.addLiteral( obj );
						}
					} else {
						log.warning("Ignoring non object or datatype role " + pred );
						continue;
					}
					
					addEdge( from, r, to, ds );
					
				}

			}
			abox.ranRete = true;
		}

		while( !abox.isComplete() ) {
			while( abox.isChanged() && !abox.isClosed() ) {
				completionTimer.check();

				abox.setChanged( false );

				if( log.isLoggable( Level.FINE ) ) {
					log.fine( "Branch: " + abox.getBranch() + ", Depth: " + abox.stats.treeDepth
							+ ", Size: " + abox.getNodes().size() + ", Mem: "
							+ (Runtime.getRuntime().freeMemory() / 1000) + "kb" );
					abox.validate();
					//					printBlocked();
					abox.printTree();
				}

				IndividualIterator i = abox.getIndIterator();

				for( TableauRule tableauRule : tableauRules ) {
					tableauRule.apply( i );
					if( abox.isClosed() )
						break;
				}

				if( !abox.isClosed() && abox.rulesNotApplied ) {
					if( log.isLoggable( Level.FINE ) ) {
						log.fine( "Applying RULE rule at branch:" + abox.getBranch() );
					}
					abox.rulesNotApplied = false;
					applyRULERule();

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
									+ " tryNext = " + unexploredBranch.getTryNext() + " tryCount = "
									+ unexploredBranch.getTryCount() );
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
	
	private boolean isDisjunct(RuleAtom atom) {
		return !(atom instanceof BuiltInAtom) && !(atom instanceof DataRangeAtom);
	}

	private void createDisjunctionsFromBinding(VariableBinding binding, Rule rule) {
		TrivialSatisfactionHelpers atomTester = new TrivialSatisfactionHelpers( abox );
		RuleAtomAsserter ruleAtomAsserter = new RuleAtomAsserter();
		
		List<RuleAtom> atoms = new ArrayList<RuleAtom>();

		for( RuleAtom atom : rule.getBody() ) {
			if( isDisjunct( atom ) )
				atoms.add( atom );
		}
		
		int bodyAtomCount = atoms.size();

		for( RuleAtom atom : rule.getHead() ) {
			if( isDisjunct( atom ) && atomTester.isAtomTrue( atom, binding ) == null )
				atoms.add( atom );
		}
		
		if( atoms.size() == bodyAtomCount )
			return;
		
		RuleBranch r = new RuleBranch( abox, this, ruleAtomAsserter, atoms, binding, bodyAtomCount, DependencySet.INDEPENDENT );
		addBranch( r );
		r.tryNext();
	}
	
	@Override
	public void restoreLocal(Individual ind, Branch br) {
		restore( br );
	}
}
