// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tbox.impl.Unfolding;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.fsm.Transition;
import org.mindswap.pellet.utils.fsm.TransitionGraph;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;

/**
 * <p>
 * Title:
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
 * @author Evren Sirin
 */
public abstract class AbstractConceptCache implements ConceptCache {
	public final static Logger	log	= Logger.getLogger( AbstractConceptCache.class.getName() );
	
	private int	maxSize;

	/**
	 * Creates an empty cache with at most <code>maxSize</code> elements which
	 * are neither named or negations of names.
	 * 
	 * @param maxSize
	 */
	public AbstractConceptCache(int maxSize) {
		this.maxSize = maxSize;
	}
	
	protected boolean isFull() {
		return size() == maxSize;
	}

	public Bool getSat(ATermAppl c) {
		CachedNode cached = get( c );
		return cached == null
			? Bool.UNKNOWN
			: Bool.create( !cached.isBottom() );
	}

	public boolean putSat(ATermAppl c, boolean isSatisfiable) {
		CachedNode cached = get( c );
		if( cached != null ) {
			if( isSatisfiable != !cached.isBottom() )
				throw new InternalReasonerException( "Caching inconsistent results for " + c );
			return false;
		}
		else if( isSatisfiable ) {
			put( c, CachedNodeFactory.createSatisfiableNode() );
		}
		else {
			ATermAppl notC = ATermUtils.negate( c );

			put( c, CachedNodeFactory.createBottomNode() );
			put( notC, CachedNodeFactory.createTopNode() );

		}

		return true;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	

	private Bool checkTrivialClash(CachedNode node1, CachedNode node2) {
		Bool result = null;

		if( node1.isBottom() || node2.isBottom() ) {
			result = Bool.TRUE;
		}
		else if( node1.isTop() || node2.isTop() ) {
			result = Bool.FALSE;
		}
		else if( !node1.isComplete() || !node2.isComplete() ) {
			result = Bool.UNKNOWN;
		}

		return result;
	}

	public Bool isMergable(KnowledgeBase kb, CachedNode root1, CachedNode root2) {
		Bool result = checkTrivialClash( root1, root2 );
		if( result != null )
			return result.not();

		CachedNode roots[] = new CachedNode[] { root1, root2 };
		boolean isIndependent = root1.isIndependent() && root2.isIndependent();
		
		int root = roots[0].getDepends().size() < roots[1].getDepends().size()
			? 0
			: 1;
		int otherRoot = 1 - root;
		for( Entry<ATermAppl, DependencySet> entry : roots[root].getDepends().entrySet() ) {
			ATermAppl c = entry.getKey();
			ATermAppl notC = ATermUtils.negate( c );

			DependencySet ds2 = roots[otherRoot].getDepends().get( notC );
			if( ds2 != null ) {
				DependencySet ds1 = entry.getValue();
				boolean allIndependent = isIndependent && ds1.isIndependent() && ds2.isIndependent();
				if( allIndependent ) {
					if( log.isLoggable( Level.FINE ) )
						log.fine( roots[root] + " has " + c + " " + roots[otherRoot]
								+ " has negation " + ds1.max() + " " + ds2.max() );
					return Bool.FALSE;
				}
				else {
					if( log.isLoggable( Level.FINE ) )
						log.fine( roots[root] + " has " + c + " " + roots[otherRoot]
								+ " has negation " + ds1.max() + " " + ds2.max() );
					result = Bool.UNKNOWN;
				}
			}
		}

		// if there is a suspicion there is no way to fix it later so return now
		if( result != null )
			return result;

		for( root = 0; root < 2; root++ ) {
			otherRoot = 1 - root;

			for( ATermAppl c : roots[root].getDepends().keySet() ) {
				if( ATermUtils.isPrimitive( c ) ) {
					result = checkBinaryClash( kb, c, roots[root], roots[otherRoot] );
				}
				else if( ATermUtils.isAllValues( c ) ) {
					result = checkAllValuesClash( kb, c, roots[root], roots[otherRoot] );
				}
				else if( ATermUtils.isNot( c ) ) {
					ATermAppl arg = (ATermAppl) c.getArgument( 0 );
					if( ATermUtils.isMin( arg ) ) {				
						result = checkMaxClash( kb, c, roots[root], roots[otherRoot] );
					}
					else if( ATermUtils.isSelf( arg ) ) {
						result = checkSelfClash( kb, arg, roots[root], roots[otherRoot] );
					}
				}

				if( result != null )
					return result;
			}
		}
		
		boolean bothNamedIndividuals = (root1 instanceof Individual && root2 instanceof Individual);

		if( kb.getExpressivity().hasFunctionality() || kb.getExpressivity().hasFunctionalityD() ) {
			root = (roots[0].getOutEdges().size() + roots[0].getInEdges().size()) < (roots[1]
					.getOutEdges().size() + roots[1].getInEdges().size())
				? 0
				: 1;
			otherRoot = 1 - root;

			if( bothNamedIndividuals )
				result = checkFunctionalityClashWithDifferents( (Individual) roots[root],
						(Individual) roots[otherRoot] );
			else
				result = checkFunctionalityClash( roots[root], roots[otherRoot] );
			if( result != null )
				return result;
		}

		if( bothNamedIndividuals ) {
			Individual ind1 = (Individual) root1;
			Individual ind2 = (Individual) root2;
			DependencySet ds = ind1.getDifferenceDependency( ind2 );
			if( ds != null ) {
				return ds.isIndependent()
					? Bool.FALSE
					: Bool.UNKNOWN;
			}
			
			for (Edge edge : ind1.getOutEdges()) {
				if (edge.getRole().isIrreflexive() && edge.getTo().equals(ind2)) {
					return edge.getDepends().isIndependent()
						? Bool.FALSE
						: Bool.UNKNOWN;
				}
			}
			
			for (Edge edge : ind1.getInEdges()) {
				if (edge.getRole().isIrreflexive() && edge.getFrom().equals(ind2)) {
					return edge.getDepends().isIndependent()
						? Bool.FALSE
						: Bool.UNKNOWN;
				}
			}
		}
		
		if( kb.getExpressivity().hasDisjointRoles() ) {
			Bool clash = checkDisjointPropertyClash( root1, root2 );
			if (clash != null ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Cannot determine if two named individuals can be merged or not: " + roots[0] + "  + roots[1]" );
				return Bool.UNKNOWN;		
			}
		}

		// if there is no obvious clash then c1 & not(c2) is satisfiable
		// therefore c1 is NOT a subclass of c2.
		return Bool.TRUE;
	}
	
	private Bool checkBinaryClash(KnowledgeBase kb, ATermAppl c, CachedNode root, CachedNode otherRoot) {
        Iterator<Unfolding> unfoldingList = kb.getTBox().unfold( c );

        while( unfoldingList.hasNext() ) {
			Unfolding unfolding = unfoldingList.next();
        	ATermAppl unfoldingCondition = unfolding.getCondition();
        	
        	if( !unfoldingCondition.equals( TOP )
        		&& otherRoot.getDepends().containsKey( unfoldingCondition ) ) {
        		return Bool.UNKNOWN;
        	}
        }
        
        return null;
	}

	private Bool checkAllValuesClash(KnowledgeBase kb, ATermAppl av, CachedNode root,
			CachedNode otherRoot) {
		ATerm r = av.getArgument( 0 );
		if( r.getType() == ATerm.LIST )
			r = ((ATermList) r).getFirst();
		Role role = kb.getRole( r );

		if( !role.hasComplexSubRole() ) {
			if( otherRoot.hasRNeighbor( role ) ) {
				if( log.isLoggable( Level.FINE ) )
					log
							.fine( root + " has " + av + " " + otherRoot + " has " + role
									+ " neighbor" );

				return Bool.UNKNOWN;
			}
		}
		else {
			TransitionGraph<Role> tg = role.getFSM();
			for( Transition<Role> t : tg.getInitialState().getTransitions() ) {
				if( otherRoot.hasRNeighbor( t.getName() ) ) {
					if( log.isLoggable( Level.FINE ) )
						log.fine( root + " has " + av + " " + otherRoot + " has " + t.getName()
								+ " neighbor" );

					return Bool.UNKNOWN;
				}
			}
		}

		return null;
	}

	private Bool checkMaxClash(KnowledgeBase kb, ATermAppl mc, CachedNode root, CachedNode otherRoot) {
		ATermAppl maxCard = (ATermAppl) mc.getArgument( 0 );

		Role maxR = kb.getRole( maxCard.getArgument( 0 ) );
		int max = ((ATermInt) maxCard.getArgument( 1 )).getInt() - 1;

		int n1 = getRNeighbors( root, maxR ).size();
		int n2 = getRNeighbors( otherRoot, maxR ).size();

		if( n1 + n2 > max ) {
			if( log.isLoggable( Level.FINE ) )
				log.fine( root + " has " + mc + " " + otherRoot + " has R-neighbor" );
			return Bool.UNKNOWN;
		}

		return null;
	}

	private Bool checkSelfClash(KnowledgeBase kb, ATermAppl self, CachedNode root, CachedNode otherRoot) {
		Role r = kb.getRole( self.getArgument( 0 ) );
		
		for( Edge e : otherRoot.getOutEdges() ) {
			if( e.getRole().isSubRoleOf( r ) && e.getToName().equals( otherRoot.getName() ) ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( root + " has not(" + self + ") " + otherRoot + " has self edge" );
				boolean allIndependent = root.isIndependent() 
					&& otherRoot.isIndependent() 
					&& e.getDepends().isIndependent();
				return allIndependent ? Bool.FALSE : Bool.UNKNOWN;
			}
		}

		return null;
	}
	
	private Bool checkFunctionalityClash(CachedNode root, CachedNode otherRoot) {
		Set<Role> checked = new HashSet<Role>();
		for( Edge edge : root.getOutEdges() ) {
			Role role = edge.getRole();

			if( !role.isFunctional() )
				continue;

			Set<Role> functionalSupers = role.getFunctionalSupers();
			for( Iterator<Role> j = functionalSupers.iterator(); j.hasNext(); ) {
				Role supRole = j.next();

				if( checked.contains( supRole ) )
					continue;

				checked.add( supRole );

				if( otherRoot.hasRNeighbor( supRole ) ) {
					if( log.isLoggable( Level.FINE ) )
						log.fine( root + " and " + otherRoot + " has " + supRole );
					return Bool.UNKNOWN;
				}
			}
		}

		for( Edge edge : root.getInEdges() ) {
			Role role = edge.getRole().getInverse();

			if( role == null || !role.isFunctional() )
				continue;

			Set<Role> functionalSupers = role.getFunctionalSupers();
			for( Iterator<Role> j = functionalSupers.iterator(); j.hasNext(); ) {
				Role supRole = j.next();

				if( checked.contains( supRole ) )
					continue;

				checked.add( supRole );

				if( otherRoot.hasRNeighbor( supRole ) ) {
					if( log.isLoggable( Level.FINE ) )
						log.fine( root + " and " + otherRoot + " has " + supRole );
					return Bool.UNKNOWN;
				}
			}
		}

		return null;
	}
	
	private Bool checkFunctionalityClashWithDifferents(Individual root, Individual otherRoot) {
		Bool result = null;
		for( Edge edge : root.getOutEdges() ) {
			Role role = edge.getRole();

			if( !role.isFunctional() )
				continue;

			Set<Role> functionalSupers = role.getFunctionalSupers();
			for( Iterator<Role> j = functionalSupers.iterator(); j.hasNext(); ) {
				Role supRole = j.next();

				EdgeList otherEdges = otherRoot.getRNeighborEdges( supRole );
				for( Edge otherEdge : otherEdges ) {
					DependencySet ds = edge.getTo().getDifferenceDependency( otherEdge.getNeighbor( otherRoot ) );
					if( log.isLoggable( Level.FINE ) )
						log.fine( root + " and " + otherRoot + " has " + supRole + " " + edge + " " + otherEdge );
					if( ds != null && ds.isIndependent() )
						return Bool.FALSE;
					result = Bool.UNKNOWN;
				}
			}
		}

		for( Edge edge : root.getInEdges() ) {
			Role role = edge.getRole().getInverse();

			if( role == null || !role.isFunctional() )
				continue;

			Set<Role> functionalSupers = role.getFunctionalSupers();
			for( Iterator<Role> j = functionalSupers.iterator(); j.hasNext(); ) {
				Role supRole = j.next();

				EdgeList otherEdges = otherRoot.getRNeighborEdges( supRole );
				for( Edge otherEdge : otherEdges ) {
					DependencySet ds = edge.getTo().getDifferenceDependency( otherEdge.getNeighbor( otherRoot ) );
					if( log.isLoggable( Level.FINE ) )
						log.fine( root + " and " + otherRoot + " has " + supRole + " " + edge + " " + otherEdge );
					if( ds != null && ds.isIndependent() )
						return Bool.FALSE;
					result = Bool.UNKNOWN;
				}
			}
		}

		return result;
	}
	
	private MultiValueMap<ATermAppl, Role> collectNeighbors(CachedNode ind) {
		MultiValueMap<ATermAppl, Role> neighbors = new MultiValueMap<ATermAppl, Role>();
		for( Edge edge : ind.getInEdges() ) {
			Role role = edge.getRole();
			ATermAppl neighbor = edge.getFromName();

			if( !ATermUtils.isBnode( neighbor ) )
				neighbors.putSingle( neighbor, role );
		}
		
		for( Edge edge : ind.getOutEdges() ) {
			Role role = edge.getRole();
			ATermAppl neighbor = edge.getToName();
			if( role.isObjectRole() && !ATermUtils.isBnode( neighbor ) )
				neighbors.putSingle( neighbor, role.getInverse() );
		}
		return neighbors;
	}
		
	
	private boolean checkDisjointProperties(Set<Role> roles1, Set<Role> roles2) {
		Set<Role> allDisjoints = new HashSet<Role>();
		for( Role role : roles1 ) {
			allDisjoints.addAll( role.getDisjointRoles() );
		}
		return SetUtils.intersects( allDisjoints, roles2 );
	}
	
	private Bool checkDisjointPropertyClash(CachedNode root1, CachedNode root2) {
		MultiValueMap<ATermAppl, Role> neighbors1 = collectNeighbors( root1 );
		if( neighbors1.isEmpty() )
			return null;

		MultiValueMap<ATermAppl, Role> neighbors2 = collectNeighbors( root2 );
		if( neighbors2.isEmpty() )
			return null;

		for( Entry<ATermAppl, Set<Role>> e : neighbors1.entrySet() ) {
			ATermAppl commonNeighbor = e.getKey();
			Set<Role> roles1 = e.getValue();
			Set<Role> roles2 = neighbors2.get( commonNeighbor );

			if( roles2 == null )
				continue;

			if( checkDisjointProperties( roles1, roles2 ) )
				return Bool.UNKNOWN;
		}

		return null;
	}

	public Bool checkNominalEdges(KnowledgeBase kb, CachedNode pNode, CachedNode cNode) {
		Bool result = Bool.UNKNOWN;

		if( pNode.isComplete() && cNode.isComplete() && cNode.isIndependent() ) {
			result = checkNominalEdges( kb, pNode, cNode, false );
			if( result.isUnknown() )
				result = checkNominalEdges( kb, pNode, cNode, true );
		}

		return result;
	}

	private Bool checkNominalEdges(KnowledgeBase kb, CachedNode pNode, CachedNode cNode,
			boolean checkInverses) {
		EdgeList edges = checkInverses
			? cNode.getInEdges()
			: cNode.getOutEdges();
		for( Edge edge : edges ) {
			Role role = checkInverses
				? edge.getRole().getInverse()
				: edge.getRole();
			DependencySet ds = edge.getDepends();

			if( !ds.isIndependent() )
				continue;

			boolean found = false;
			ATermAppl val = checkInverses
				? edge.getFromName()
				: edge.getToName();

			if( !role.isObjectRole() ) {
				found = pNode.hasRNeighbor( role );
			}
			else if( !isRootNominal( kb, val ) ) {
				if( !role.hasComplexSubRole() )
					found = pNode.hasRNeighbor( role );
				else {
					TransitionGraph<Role> tg = role.getFSM();
					Iterator<Transition<Role>> it = tg.getInitialState().getTransitions().iterator();
					while( !found && it.hasNext() ) {
						Transition<Role> tr = it.next();
						found = pNode.hasRNeighbor( tr.getName() );
					}
				}
			}
			else {
				Set<ATermAppl> neighbors = null;

				if( role.isSimple() || !(pNode instanceof Individual) )
					neighbors = getRNeighbors( pNode, role );
				else {
					neighbors = new HashSet<ATermAppl>();
					kb.getABox().getObjectPropertyValues( pNode.getName(), role, neighbors,
							neighbors, false );
				}
				
				Individual ind = kb.getABox().getIndividual( val ).getSame();
				Set<ATermAppl> samesAndMaybes = new HashSet<ATermAppl>();
				kb.getABox().getSames( ind, samesAndMaybes, samesAndMaybes );
				
				found = SetUtils.intersects( samesAndMaybes, neighbors );
			}

			if( !found ) {
				return Bool.FALSE;
			}

		}

		return Bool.UNKNOWN;
	}

	/**
	 * @param val
	 * @return
	 */
	private boolean isRootNominal(KnowledgeBase kb, ATermAppl val) {
		Individual ind = kb.getABox().getIndividual( val );
		
		return ind != null && ind.isRootNominal();
	}
	


	/**
	 * {@inheritDoc}
	 */
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
}
