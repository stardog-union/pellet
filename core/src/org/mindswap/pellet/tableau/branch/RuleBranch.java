// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.branch;

import java.util.List;
import java.util.logging.Level;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;

import com.clarkparsia.pellet.rules.RuleAtomAsserter;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.UnaryAtom;

public class RuleBranch extends Branch {
	private RuleAtomAsserter	ruleAtomAsserter;
	private VariableBinding		binding;
	private List<RuleAtom>		atoms;
	private int					bodyAtomCount;
	private int[]				order;
	private DependencySet[]		prevDS;

	public RuleBranch(ABox abox, CompletionStrategy completion, RuleAtomAsserter ruleAtomAsserter,
			List<RuleAtom> atoms, VariableBinding binding, int bodyAtomCount, DependencySet ds) {
		super( abox, completion, ds, atoms.size() );

		this.ruleAtomAsserter = ruleAtomAsserter;
		this.atoms = atoms;
		this.bodyAtomCount = bodyAtomCount;
		this.binding = binding;
		this.prevDS = new DependencySet[atoms.size()];
		this.order = new int[atoms.size()];
		for( int i = 0; i < order.length; i++ )
			order[i] = i;
	}

	public Node getNode() {
		return null;
	}

	public RuleBranch copyTo(ABox abox) {
		RuleBranch b = new RuleBranch( abox, strategy, ruleAtomAsserter, atoms, binding,
				bodyAtomCount, getTermDepends() );

		b.setAnonCount( getAnonCount() );
		b.setNodeCount( nodeCount );
		b.setBranch( branch );
		b.setTryNext( tryNext );
		b.prevDS = new DependencySet[prevDS.length];
		System.arraycopy( prevDS, 0, b.prevDS, 0, tryNext );
		b.order = new int[order.length];
		System.arraycopy( order, 0, b.order, 0, order.length );

		return b;
	}

	public void setLastClash(DependencySet ds) {
		super.setLastClash( ds );
		if( tryNext >= 0 )
			prevDS[tryNext] = ds;
	}

	protected void tryBranch() {
		abox.incrementBranch();

		// int[] stats = null;
		// if( PelletOptions.USE_DISJUNCT_SORTING ) {
		// stats = abox.getDisjBranchStats().get(atoms);
		// if(stats == null) {
		// stats = new int[tryCount];
		// Arrays.fill( stats, 0 );
		// abox.getDisjBranchStats().put(atoms, stats);
		// }
		// if(tryNext > 0) {
		// stats[order[tryNext-1]]++;
		// }
		// if(stats != null) {
		// int minIndex = tryNext;
		// int minValue = stats[tryNext];
		// for(int i = tryNext + 1; i < stats.length; i++) {
		// boolean tryEarlier = ( stats[i] < minValue );
		//		            
		// if( tryEarlier ) {
		// minIndex = i;
		// minValue = stats[i];
		// }
		// }
		// if(minIndex != tryNext) {
		// Collections.swap( atoms, minIndex, tryNext );
		//		            	
		// order[minIndex] = tryNext;
		// order[tryNext] = minIndex;
		// }
		// }
		// }

		for( ; tryNext < tryCount; tryNext++ ) {
			RuleAtom atom = atoms.get( tryNext );

//			if( PelletOptions.USE_SEMANTIC_BRANCHING ) {
//				for( int m = 0; m < tryNext; m++ )
//					ruleAtomAsserter
//							.assertAtom( atoms.get( m ), binding, prevDS[m], m >= bodyAtomCount );
//			}

			DependencySet ds = null;
			if( tryNext == tryCount - 1 && !PelletOptions.SATURATE_TABLEAU ) {
				ds = getTermDepends();

				for( int m = 0; m < tryNext; m++ )
					ds = ds.union( prevDS[m], abox.doExplanation() );

				// CHW - added for incremental reasoning and rollback through
				// deletions
				if( PelletOptions.USE_INCREMENTAL_DELETION )
					ds.setExplain( getTermDepends().getExplain() );
				else
					ds.remove( getBranch() );
			}
			else {
				// CHW - Changed for tracing purposes
				if( PelletOptions.USE_INCREMENTAL_DELETION )
					ds = getTermDepends().union( new DependencySet( getBranch() ),
							abox.doExplanation() );
				else
					ds = new DependencySet( getBranch() );
			}

			if( log.isLoggable( Level.FINE ) )
				log.fine( "RULE: Branch (" + getBranch() + ") try (" + (tryNext + 1) + "/"
						+ tryCount + ") " + atom + " " + binding + " " + atoms + " " + ds );

			ruleAtomAsserter.assertAtom( atom, binding, ds, tryNext < bodyAtomCount, abox, strategy );

			// if there is a clash
			if( abox.isClosed() ) {
				DependencySet clashDepends = abox.getClash().getDepends();

				if( log.isLoggable( Level.FINE ) )
					log.fine( "CLASH: Branch " + getBranch() + " "
							+ Clash.unexplained( null, clashDepends ) + "!" );

				// if( PelletOptions.USE_DISJUNCT_SORTING ) {
				// if( stats == null ) {
				// stats = new int[disj.length];
				// for( int i = 0; i < disj.length; i++ )
				// stats[i] = 0;
				// abox.getDisjBranchStats().put( atoms, stats );
				// }
				// stats[order[tryNext]]++;
				// }

				// do not restore if we do not have any more branches to try.
				// after
				// backtrack the correct branch will restore it anyway. more
				// importantly restore clears the clash info causing exceptions
				if( tryNext < tryCount - 1 && clashDepends.contains( getBranch() ) ) {
					AtomIObject obj = (AtomIObject) (atom instanceof UnaryAtom
						? ((UnaryAtom) atom).getArgument()
						: ((BinaryAtom) atom).getArgument1());
					Individual ind = binding.get( obj );

					strategy.restoreLocal( ind, this );

					// global restore sets the branch number to previous
					// value so we need to
					// increment it again
					abox.incrementBranch();

					setLastClash( clashDepends );
				}
				else {

					abox.setClash( Clash.unexplained( null, clashDepends.union( ds, abox
							.doExplanation() ) ) );

					// CHW - added for inc reasoning
					if( PelletOptions.USE_INCREMENTAL_DELETION )
						abox.getKB().getDependencyIndex().addCloseBranchDependency( this,
								abox.getClash().getDepends() );

					return;
				}
			}
			else
				return;
		}

		// this code is not unreachable. if there are no branches left restore
		// does not call this
		// function, and the loop immediately returns when there are no branches
		// left in this
		// disjunction. If this exception is thrown it shows a bug in the code.
		throw new InternalReasonerException( "This exception should not be thrown!" );
	}

	/**
	 * Added for to re-open closed branches. This is needed for incremental
	 * reasoning through deletions Currently this method does nothing as we
	 * cannot support incremental reasoning when both rules are used in the KB
	 * 
	 * @param index
	 *            The shift index
	 */
	public void shiftTryNext(int openIndex) {

	}

}
