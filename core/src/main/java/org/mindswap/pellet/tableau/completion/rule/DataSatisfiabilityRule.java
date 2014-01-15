// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;

import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class DataSatisfiabilityRule extends AbstractTableauRule {

	public DataSatisfiabilityRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.DATATYPE, BlockingType.NONE );
	}

	public void apply(Individual ind) {
		Set<Literal> nodes = new HashSet<Literal>();
		LinkedList<Literal> pending = new LinkedList<Literal>();
		Map<Literal, Set<Literal>> ne = new HashMap<Literal, Set<Literal>>();
		DependencySet ds = DependencySet.EMPTY;
		boolean nePresent = false;
		for( Iterator<Edge> it = ind.getOutEdges().iterator(); it.hasNext(); ) {
			final Edge e = it.next();
			final Role r = e.getRole();
			if( !r.isDatatypeRole() )
				continue;

			ds = ds.union( e.getDepends(), strategy.getABox().doExplanation() );

			final Literal l = (Literal) e.getTo();
			pending.add( l );

			Set<Literal> disj = ne.get( l );

			for( Role s : r.getDisjointRoles() ) {
				for( Edge f : ind.getOutEdges().getEdges( s ) ) {
					final Literal k = (Literal) f.getTo();
					if( disj == null ) {
						disj = new HashSet<Literal>();
						ne.put( l, disj );
						nePresent = true;
					}
					disj.add( k );
				}
			}
		}
		
		while (!pending.isEmpty()) {
			final Literal l = pending.removeFirst();
			if( !nodes.add( l ) )
				continue;

			Set<Literal> disj = ne.get( l );

			for( Node n : l.getDifferents() ) {
				if( n.isLiteral() ) {
					final Literal k = (Literal) n;
					pending.add( k );
					if( disj == null ) {
						disj = new HashSet<Literal>();
						ne.put( l, disj );
						nePresent = true;
					}
					disj.add( k );
					ds = ds.union( l.getDifferenceDependency( n ), strategy.getABox().doExplanation() );
				}
				else {
					throw new IllegalStateException();
				}
			}
		}

		/*
		 * This satisfiability check is only needed if an inequality is present
		 * because if no inequalities are present, the check is a repetition of
		 * the satisfiability check performed during Literal.addType
		 * (checkClash)
		 */
		if( nePresent ) {
			try {
				if( !strategy.getABox().getDatatypeReasoner().isSatisfiable( nodes, ne ) ) {
					for( Node n : nodes ) {
						for( DependencySet typeDep : n.getDepends().values() )
							ds = ds.union( typeDep, strategy.getABox().doExplanation() );
					}
					/*
					 * TODO: More descriptive clash
					 */
					strategy.getABox().setClash( Clash.unexplained( ind, ds ) );
				}
			} catch( InvalidLiteralException e ) {
				final String msg = "Invalid literal encountered during satisfiability check: "
						+ e.getMessage();
				if( PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY ) {
					log.fine( msg );
					for( Node n : nodes ) {
						for( DependencySet typeDep : n.getDepends().values() )
							ds = ds.union( typeDep, strategy.getABox().doExplanation() );
					}
					strategy.getABox().setClash( Clash.invalidLiteral( ind, ds ) );
				}
				else {
					log.severe( msg );
					throw new InternalReasonerException( msg, e );
				}
			} catch( DatatypeReasonerException e ) {
				final String msg = "Unexpected datatype reasoner exception: " + e.getMessage();
				log.severe( msg );
				throw new InternalReasonerException( msg, e );
			}
		}
	}
}
