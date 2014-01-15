// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.fsm.Transition;
import org.mindswap.pellet.utils.fsm.TransitionGraph;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.expressivity.Expressivity;

/**
 * A cache safety implementation that checks the cached result and the context
 * in which we try to reuse it to decide if it is safe to reuse the result.
 * 
 * @author Evren Sirin
 */
public class CacheSafetyDynamic implements CacheSafety {
	private final Expressivity	expressivity;

	CacheSafetyDynamic(Expressivity e) {
		this.expressivity = new Expressivity( e );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canSupport(Expressivity expressivity) {
		return !expressivity.hasNominal()
				&& this.expressivity.getAnonInverses().equals( expressivity.getAnonInverses() );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSafe(ATermAppl c, Individual ind) {
		Edge parentEdge = getParentEdge( ind );
		Role r = parentEdge.getRole();
		Individual parent = parentEdge.getFrom();
		
		ABox abox = parent.getABox();

		if( !isParentSafe( abox.getKB(), r, parent ) )
			return false;

		Iterator<CachedNode> nodes = getCachedNodes( abox, c );

		if( !nodes.hasNext() )
			return false;

		if( interactsWithInverses( abox.getKB(), r ) ) {
			while( nodes.hasNext() ) {
				CachedNode node = nodes.next();

				if( node.isBottom() )
					return true;
				else if( node.isTop() || !node.isComplete() )
					return false;

				if( !isSafe( abox.getKB(), parent, r.getInverse(), node ) )
					return false;
			}
		}

		return true;
	}

	protected Edge getParentEdge(Individual ind) {
		Edge result = null;
		Role role = null;
		Individual parent = ind.getParent();
		for( Edge e : ind.getInEdges() ) {
			if( e.getFrom().equals( parent ) ) {
				if( role == null ) {
					role = e.getRole();
					result = e;
				}
				else if( e.getRole().isSubRoleOf( role ) ) {
					role = e.getRole();
					result = e;
				}
			}
		}
		
		assert result != null;
		
		return result;
	}
	
	protected Iterator<CachedNode> getCachedNodes(ABox abox, ATermAppl c) {
		CachedNode node = abox.getCached( c );
		if( node != null ) {
			return IteratorUtils.singleton( node );
		}

		if( ATermUtils.isAnd( c ) ) {
			ATermList list = (ATermList) c.getArgument( 0 );
			CachedNode[] nodes = new CachedNode[list.getLength()];
			for( int i = 0; !list.isEmpty(); list = list.getNext() ) {
				ATermAppl d = (ATermAppl) list.getFirst();
				node = abox.getCached( d );
				if( node == null )
					return IteratorUtils.emptyIterator();
				else if( node.isBottom() )
					return IteratorUtils.singleton( node );

				nodes[i++] = node;
			}
			return IteratorUtils.iterator( nodes );
		}

		return IteratorUtils.emptyIterator();
	}

	private boolean isParentSafe(KnowledgeBase kb, Role role, Individual parent) {
		return isParentFunctionalSafe( role, parent ) && isParentMaxSafe( kb, role, parent );
	}

	private boolean isParentFunctionalSafe(Role role, Individual parent) {
		return !role.isFunctional() || parent.getRNeighbors( role ).size() <= 1;
	}

	private boolean isParentMaxSafe(KnowledgeBase kb, Role role, Individual parent) {
		for( ATermAppl negatedMax : parent.getTypes( Node.MAX ) ) {
			ATermAppl max = (ATermAppl) negatedMax.getArgument( 0 );
			if( !isParentMaxSafe( kb, role, max ) )
				return false;
		}

		return true;
	}

	private boolean isParentMaxSafe(KnowledgeBase kb, Role role, ATermAppl max) {
		Role maxR = kb.getRole( max.getArgument( 0 ) );

		return !role.isSubRoleOf( maxR );
	}

	private boolean isSafe(KnowledgeBase kb, Individual parent, Role role, CachedNode node) {
		if( !isFunctionalSafe( role, node ) )
			return false;

		for( ATermAppl c : node.getDepends().keySet() ) {
			if( ATermUtils.isAllValues( c ) ) {
				if( !isAllValuesSafe( kb, parent, role, c ) )
					return false;
			}
			else if( ATermUtils.isNot( c ) ) {
				ATermAppl arg = (ATermAppl) c.getArgument( 0 );
				if( ATermUtils.isMin( arg ) ) {
					if( !isMaxSafe( kb, role, arg ) )
						return false;
				}
			}			
		}

		return true;
	}

	private boolean isAllValuesSafe(KnowledgeBase kb, Individual parent, Role role, ATermAppl term) {
		Role s = kb.getRole( term.getArgument( 0 ) );
		if( !s.hasComplexSubRole() ) {
			ATermAppl c = (ATermAppl) term.getArgument( 1 );

			if( role.isSubRoleOf( s ) && !parent.hasType( c ) )
				return false;
		}
		else {
			TransitionGraph<Role> tg = s.getFSM();

			for( Transition<Role> t : tg.getInitialState().getTransitions() ) {
				if( role.isSubRoleOf( t.getName() ) )
					return false;
			}
		}

		return true;
	}

	private boolean isFunctionalSafe(Role role, CachedNode node) {
		return !role.isFunctional() || getRNeighbors( node, role ).isEmpty();
	}

	private boolean isMaxSafe(KnowledgeBase kb, Role role, ATermAppl term) {
		Role maxR = kb.getRole( term.getArgument( 0 ) );

		return !role.isSubRoleOf( maxR );
	}

	private Set<ATermAppl> getRNeighbors(CachedNode node, Role role) {
		Set<ATermAppl> neighbors = new HashSet<ATermAppl>();

		for( Edge edge : node.getOutEdges() ) {
			Role r = edge.getRole();
			if( r.isSubRoleOf( role ) ) {
				neighbors.add( edge.getToName() );
			}
		}

		if( role.isObjectRole() ) {
			role = role.getInverse();
			for( Edge edge : node.getInEdges() ) {
				Role r = edge.getRole();
				if( r.isSubRoleOf( role ) ) {
					neighbors.add( edge.getFromName() );
				}
			}
		}

		return neighbors;
	}

	protected boolean interactsWithInverses(KnowledgeBase kb, Role role) {
		if( interactsWithInversesSimple( role ) )
			return true;

		return expressivity.hasComplexSubRoles() && interactsWithInversesComplex( kb, role );
	}

	protected boolean interactsWithInversesSimple(Role role) {
		for( Role superRole : role.getSuperRoles() ) {
			if( hasAnonInverse( superRole ) ) {
				return true;
			}
		}

		return false;
	}

	protected boolean interactsWithInversesComplex(KnowledgeBase kb, Role role) {
		for( ATermAppl p : expressivity.getAnonInverses() ) {
			Role anonRole = kb.getRole( p );
			if( anonRole.hasComplexSubRole() && anonRole.getFSM().getAlpahabet().contains( role ) )
				return true;

		}

		return false;
	}

	protected boolean hasAnonInverse(Role role) {
		return !role.isBuiltin()
				&& (role.isAnon() || expressivity.getAnonInverses().contains( role.getName() ));
	}
}
