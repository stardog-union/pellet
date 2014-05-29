// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;

/**
 * <p>
 * Title: Query Plan the Uses Full Query Reordering.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class CostBasedQueryPlanNew extends QueryPlan {
	private static final Logger	log	= Logger.getLogger( CostBasedQueryPlanNew.class.getName() );

	private List<QueryAtom>		sortedAtoms;

	private int					index;

	private int					size;

	private QueryCost			cost;

	public CostBasedQueryPlanNew(Query query) {
		super( query );

		QuerySizeEstimator.computeSizeEstimate( query );

		index = 0;
		size = query.getAtoms().size();
		cost = new QueryCost( query.getKB() );
		sortedAtoms = null;

		if( size == 0 ) {
			return;
		}
		else if( size == 1 ) {
			sortedAtoms = query.getAtoms();
		}
		else {
			double minCost = chooseOrdering( new ArrayList<QueryAtom>( query.getAtoms() ),
					new ArrayList<QueryAtom>( size ), new HashSet<ATermAppl>(), false,
					Double.POSITIVE_INFINITY );

			if( sortedAtoms == null ) {
				throw new UnsupportedQueryException( "No safe ordering for query: " + query );
			}

			if( log.isLoggable( Level.FINE ) ) {
				log.log( Level.FINE, "WINNER : Cost=" + minCost + " ,atoms=" + sortedAtoms );
			}
		}
	}

	/**
	 * Recursive function that will inspect all possible orderings for a list of
	 * query atoms and returns the cost for the best ordering (min cost) found.
	 * Best ordering is saved in the sortedAtoms field. The ordering of atoms is
	 * created recursively where each step adds one more atom to the current
	 * ordering. Current ordering is discarded if it is found to be non-optimal
	 * and we have already found an ordering which is not non-optimal.
	 * Non-optimal heuristic currently is defined as follows: For each atom at
	 * position i > 1 in the ordered list, there should be at least one atom at
	 * position j < i s.t. two atoms share at least one variable. This
	 * heuristics is defined to avoid even considering cartesian products, e.g.
	 * ClassAtom(?x, A), ClassAtom(?y,B), PropertyValueAtom(?x, p, ?y). For some
	 * queries, all orderings may be non-optimal, e.g. ClassAtom(?x,A),
	 * ClassAtom(?y, B).
	 * 
	 * @param atoms
	 *            Atoms that have not yet been added to the ordered list
	 * @param orderedAtoms
	 *            Atoms that have been ordered so far
	 * @param boundVars
	 *            Variables that have referenced by the atoms in the ordered
	 *            list
	 * @param notOptimal
	 *            Current ordered list is found to be non-optimal
	 * @param minCost
	 *            Minimum cost found so far
	 * @return Minimum cost found from an ordering that has the given ordered
	 *         list as the prefix
	 */
	private double chooseOrdering(List<QueryAtom> atoms, List<QueryAtom> orderedAtoms,
			Set<ATermAppl> boundVars, boolean notOptimal, double minCost) {
		if( atoms.isEmpty() ) {
			if( notOptimal ) {
				if( sortedAtoms == null ) {
					sortedAtoms = new ArrayList<QueryAtom>( orderedAtoms );
				}
			}
			else {
				double queryCost = cost.estimate( orderedAtoms );
				log.fine( "Cost " + queryCost + " for " + orderedAtoms );
				if( queryCost < minCost ) {
					sortedAtoms = new ArrayList<QueryAtom>( orderedAtoms );
					minCost = queryCost;
				}
			}

			return minCost;
		}

		LOOP: for( int i = 0; i < atoms.size(); i++ ) {
			QueryAtom atom = atoms.get( i );

			boolean newNonOptimal = notOptimal;
			Set<ATermAppl> newBoundVars = new HashSet<ATermAppl>( boundVars );
			// TODO reorder UV atoms after all class and property variables are
			// bound.

			if( !atom.isGround() ) {
				int boundCount = 0;
				int unboundCount = 0;

				for( ATermAppl a : atom.getArguments() ) {
					if( ATermUtils.isVar( a ) ) {
						if( newBoundVars.add( a ) ) {
							unboundCount++;

							/*
							 * It is not valid to have an ordering like
							 * NotKnown(ClassAtom(?x, A)), ClassAtom(?x, B).
							 * This is because variables in negation atom will
							 * not be bound by the query evaluation. However, if
							 * an atom in the query later binds the variable to
							 * a value the result will be incorrect because
							 * earlier evaluation of negation was not evaluated
							 * with that binding.
							 */
							if( atom.getPredicate().equals( QueryPredicate.NotKnown ) ) {
								for( int j = 0; j < atoms.size(); j++ ) {
									QueryAtom nextAtom = atoms.get( j );
									
									if( i == j
										|| nextAtom.getPredicate().equals(
													QueryPredicate.NotKnown ) ) {
										continue;
									}

									if( nextAtom.getArguments().contains( a ) ) {
										if( log.isLoggable( Level.FINE ) )
											log.fine( "Unbound vars for not" );
										continue LOOP;
									}
								}
							}
						}
						else {
							boundCount++;
						}
					}
				}

				if( boundCount == 0 && newBoundVars.size() > unboundCount ) {
					if( sortedAtoms != null ) {
						if( log.isLoggable( Level.FINE ) )
							log.fine( "Stop at not optimal ordering" );
						continue;
					}
					else {
						if( log.isLoggable( Level.FINE ) )

							log.fine( "Continue not optimal ordering, no solution yet." );
						newNonOptimal = true;
					}
				}
			}

			atoms.remove( atom );
			orderedAtoms.add( atom );

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Atom[" + i + "/" + atoms.size() + "] " + atom + " from " + atoms
						+ " to " + orderedAtoms );

			minCost = chooseOrdering( atoms, orderedAtoms, newBoundVars, newNonOptimal, minCost );

			atoms.add( i, atom );
			orderedAtoms.remove( orderedAtoms.size() - 1 );
		}

		return minCost;
	}

	@Override
	public QueryAtom next(final ResultBinding binding) {
		return sortedAtoms.get( index++ ).apply( binding );
	}

	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public void back() {
		index--;
	}

	@Override
	public void reset() {
		index = 0;
	}
}
