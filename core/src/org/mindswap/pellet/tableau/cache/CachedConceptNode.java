// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: A node cached as the result of satisfiability checking for a
 * concept.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class CachedConceptNode implements CachedNode {
	private ATermAppl						name;
	private EdgeList						inEdges;
	private EdgeList						outEdges;
	private Map<ATermAppl, DependencySet>	types;
	private boolean							isIndependent;

	/**
	 * @param depends
	 * @param node
	 */
	public CachedConceptNode(ATermAppl name, Individual node) {
		this.name = name;
		
		// if the node is merged, get the representative node and check
		// also if the merge depends on a branch		
		isIndependent = node.getMergeDependency( true ).isIndependent();
		node = node.getSame();
		
		outEdges = copyEdgeList( node, true );
		inEdges = copyEdgeList( node, false );
		
		// collect all transitive property values
		if( node.getABox().getKB().getExpressivity().hasNominal() ) {
			collectComplexPropertyValues( node );
		}
		
		types = CollectionUtils.makeIdentityMap( node.getDepends() );
        for( Map.Entry<ATermAppl, DependencySet> e : types.entrySet() ) {        	
			e.setValue( e.getValue().cache() );
		}
	}

	private void collectComplexPropertyValues(Individual subj) {
		Set<Role> collected = new HashSet<Role>();
		for( Edge edge : subj.getOutEdges() ) {
			Role role = edge.getRole();
			
			// only collect non-simple, i.e. complex, roles
			// TODO we might not need to collect all non-simple roles
			// collecting only the base ones, i.e. minimal w.r.t. role
			// ordering, would be enough
			if( role.isSimple() || !collected.add( role ) )
				continue;
			
			collected.add( role );
			
			collectComplexPropertyValues( subj, role );
		}
		
		for( Edge edge : subj.getInEdges() ) {
			Role role = edge.getRole().getInverse();
			
			if( role.isSimple() || !collected.add( role ) )
				continue;

			collectComplexPropertyValues( subj, role );
		}
	}

	private void collectComplexPropertyValues(Individual subj, Role role) {
		Set<ATermAppl> knowns = new HashSet<ATermAppl>();
		Set<ATermAppl> unknowns = new HashSet<ATermAppl>();

		subj.getABox().getObjectPropertyValues( subj.getName(), role, knowns, unknowns, false );

		for( ATermAppl val : knowns ) {
			outEdges.addEdge( new CachedOutEdge( role, val, DependencySet.INDEPENDENT ) );
		}
		for( ATermAppl val : unknowns ) {
			outEdges.addEdge( new CachedOutEdge( role, val, DependencySet.DUMMY ) );
		}
	}
	
	/**
	 * Create an immutable copy of the given edge list and trimmed to the size. 
	 * 
	 * @param edgeList
	 * @return
	 */
	private EdgeList copyEdgeList( Individual node, boolean out) {
		EdgeList edgeList = out 
			? node.getOutEdges()
			: node.getInEdges();
		EdgeList cachedEdges = new EdgeList( edgeList.size() );
		for( Edge edge : edgeList ) {
			Edge cachedEdge = out 
				? new CachedOutEdge( edge ) 
				: new CachedInEdge( edge );
			cachedEdges.addEdge( cachedEdge );
			
			if( PelletOptions.CHECK_NOMINAL_EDGES ) {
				Node neighbor = edge.getNeighbor( node );
				Map<Node,DependencySet> mergedNodes = neighbor.getAllMerged();
					DependencySet edgeDepends = edge.getDepends();
					for( Entry<Node,DependencySet> entry : mergedNodes.entrySet() ) {
						Node mergedNode = entry.getKey();
						if( mergedNode.isRootNominal() && !mergedNode.equals( neighbor ) ) {
							Role r = edge.getRole();
							ATermAppl n = mergedNode.getName();
							DependencySet ds = edgeDepends.union( entry.getValue(), false ).cache();
							Edge e = out 
								? new CachedOutEdge( r, n, ds ) 
								: new CachedInEdge( r, n, ds );
							cachedEdges.addEdge( e );
						}
					}
			} 
		}
		
		return cachedEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isIndependent() {
		return isIndependent;
	}

	/**
	 * {@inheritDoc}
	 */
	public EdgeList getInEdges() {
		return inEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	public EdgeList getOutEdges() {
		return outEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<ATermAppl, DependencySet> getDepends() {
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasRNeighbor(Role role) {
		return outEdges.hasEdge( role )
				|| (role.isObjectRole() && inEdges.hasEdge( role.getInverse() ));

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isBottom() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isComplete() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNamedIndividual() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTop() {
		return false;
	}

	public ATermAppl getName() {
		return name;
	}

	@Override
	public String toString() {
		return ATermUtils.toString( name );
	}
}
