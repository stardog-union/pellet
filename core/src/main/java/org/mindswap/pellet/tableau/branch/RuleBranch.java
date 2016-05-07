// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.branch;

import com.clarkparsia.pellet.rules.RuleAtomAsserter;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.BinaryAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.UnaryAtom;
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

public class RuleBranch extends Branch
{
	private final RuleAtomAsserter _ruleAtomAsserter;
	private final VariableBinding _binding;
	private final List<RuleAtom> _atoms;
	private final int _bodyAtomCount;
	private int[] _order;
	private DependencySet[] _prevDS;

	public RuleBranch(final ABox abox, final CompletionStrategy completion, final RuleAtomAsserter ruleAtomAsserter, final List<RuleAtom> atoms, final VariableBinding binding, final int bodyAtomCount, final DependencySet ds)
	{
		super(abox, completion, ds, atoms.size());

		this._ruleAtomAsserter = ruleAtomAsserter;
		this._atoms = atoms;
		this._bodyAtomCount = bodyAtomCount;
		this._binding = binding;
		this._prevDS = new DependencySet[atoms.size()];
		this._order = new int[atoms.size()];
		for (int i = 0; i < _order.length; i++)
			_order[i] = i;
	}

	@Override
	public Node getNode()
	{
		return null;
	}

	@Override
	public RuleBranch copyTo(final ABox abox)
	{
		final RuleBranch b = new RuleBranch(abox, _strategy, _ruleAtomAsserter, _atoms, _binding, _bodyAtomCount, getTermDepends());

		b.setAnonCount(getAnonCount());
		b.setNodeCount(_nodeCount);
		b.setBranch(_branch);
		b.setTryNext(_tryNext);
		b._prevDS = new DependencySet[_prevDS.length];
		System.arraycopy(_prevDS, 0, b._prevDS, 0, _tryNext);
		b._order = new int[_order.length];
		System.arraycopy(_order, 0, b._order, 0, _order.length);

		return b;
	}

	@Override
	public void setLastClash(final DependencySet ds)
	{
		super.setLastClash(ds);
		if (_tryNext >= 0)
			_prevDS[_tryNext] = ds;
	}

	@Override
	protected void tryBranch()
	{
		_abox.incrementBranch();

		// int[] stats = null;
		// if( PelletOptions.USE_DISJUNCT_SORTING ) {
		// stats = _abox.getDisjBranchStats().get(_atoms);
		// if(stats == null) {
		// stats = new int[_tryCount];
		// Arrays.fill( stats, 0 );
		// _abox.getDisjBranchStats().put(_atoms, stats);
		// }
		// if(_tryNext > 0) {
		// stats[_order[_tryNext-1]]++;
		// }
		// if(stats != null) {
		// int minIndex = _tryNext;
		// int minValue = stats[_tryNext];
		// for(int i = _tryNext + 1; i < stats.length; i++) {
		// boolean tryEarlier = ( stats[i] < minValue );
		//
		// if( tryEarlier ) {
		// minIndex = i;
		// minValue = stats[i];
		// }
		// }
		// if(minIndex != _tryNext) {
		// Collections.swap( _atoms, minIndex, _tryNext );
		//
		// _order[minIndex] = _tryNext;
		// _order[_tryNext] = minIndex;
		// }
		// }
		// }

		for (; _tryNext < _tryCount; _tryNext++)
		{
			final RuleAtom atom = _atoms.get(_tryNext);

			//			if( PelletOptions.USE_SEMANTIC_BRANCHING ) {
			//				for( int m = 0; m < _tryNext; m++ )
			//					_ruleAtomAsserter
			//							.assertAtom( _atoms.get( m ), _binding, _prevDS[m], m >= _bodyAtomCount );
			//			}

			DependencySet ds = null;
			if (_tryNext == _tryCount - 1 && !PelletOptions.SATURATE_TABLEAU)
			{
				ds = getTermDepends();

				for (int m = 0; m < _tryNext; m++)
					ds = ds.union(_prevDS[m], _abox.doExplanation());

				// CHW - added for incremental reasoning and rollback through
				// deletions
				if (PelletOptions.USE_INCREMENTAL_DELETION)
					ds.setExplain(getTermDepends().getExplain());
				else
					ds.remove(getBranch());
			}
			else
				// CHW - Changed for tracing purposes
				if (PelletOptions.USE_INCREMENTAL_DELETION)
					ds = getTermDepends().union(new DependencySet(getBranch()), _abox.doExplanation());
				else
					ds = new DependencySet(getBranch());

			if (log.isLoggable(Level.FINE))
				log.fine("RULE: Branch (" + getBranch() + ") try (" + (_tryNext + 1) + "/" + _tryCount + ") " + atom + " " + _binding + " " + _atoms + " " + ds);

			_ruleAtomAsserter.assertAtom(atom, _binding, ds, _tryNext < _bodyAtomCount, _abox, _strategy);

			// if there is a clash
			if (_abox.isClosed())
			{
				final DependencySet clashDepends = _abox.getClash().getDepends();

				if (log.isLoggable(Level.FINE))
					log.fine("CLASH: Branch " + getBranch() + " " + Clash.unexplained(null, clashDepends) + "!");

				// if( PelletOptions.USE_DISJUNCT_SORTING ) {
				// if( stats == null ) {
				// stats = new int[disj.length];
				// for( int i = 0; i < disj.length; i++ )
				// stats[i] = 0;
				// _abox.getDisjBranchStats().put( _atoms, stats );
				// }
				// stats[_order[_tryNext]]++;
				// }

				// do not restore if we do not have any more branches to try.
				// after
				// backtrack the correct _branch will restore it anyway. more
				// importantly restore clears the clash info causing exceptions
				if (_tryNext < _tryCount - 1 && clashDepends.contains(getBranch()))
				{
					final AtomIObject obj = (AtomIObject) (atom instanceof UnaryAtom ? ((UnaryAtom<?>) atom).getArgument() : ((BinaryAtom<?, ?, ?>) atom).getArgument1());
					final Individual ind = _binding.get(obj);

					_strategy.restoreLocal(ind, this);

					// global restore sets the _branch number to previous
					// value so we need to
					// increment it again
					_abox.incrementBranch();

					setLastClash(clashDepends);
				}
				else
				{

					_abox.setClash(Clash.unexplained(null, clashDepends.union(ds, _abox.doExplanation())));

					// CHW - added for inc reasoning
					if (PelletOptions.USE_INCREMENTAL_DELETION)
						_abox.getKB().getDependencyIndex().addCloseBranchDependency(this, _abox.getClash().getDepends());

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
		// _disjunction. If this exception is thrown it shows a bug in the code.
		throw new InternalReasonerException("This exception should not be thrown!");
	}

	/**
	 * Added for to re-open closed branches. This is needed for incremental reasoning through deletions Currently this method does nothing as we cannot support
	 * incremental reasoning when both rules are used in the KB
	 *
	 * @param index The shift index
	 */
	@Override
	public void shiftTryNext(final int openIndex)
	{
		//
	}

}
