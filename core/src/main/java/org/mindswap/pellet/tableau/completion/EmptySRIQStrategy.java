// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.tableau.completion;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.NodeMerge;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.blocking.BlockingFactory;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.cache.CacheSafety;
import org.mindswap.pellet.tableau.cache.CacheSafetyFactory;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.Timer;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.expressivity.Expressivity;

/**
 * Completion strategy for a SRIQ KB that does not have individuals in the ABox.
 * When ABox is empty completion always starts with a single root individual
 * that represents the concept whose satisfiability is being searched. 
 * 
 * @author Evren Sirin
 */
public class EmptySRIQStrategy extends CompletionStrategy {
	/**
	 * List of individuals that needs to be expanded by applying tableau completion rules
	 */
	private LinkedList<Individual>		mayNeedExpanding;
	
	/**
	 * Cached mayNeedExpanding at a certain branch that will be restored during backtracking
	 */
	private List<List<Individual>>		mnx;

	/**
	 * Nodes in the completion graph that re already being searched
	 */
	private Map<Individual, ATermAppl>	cachedNodes;
	
	/**
	 * Cache safety checker to decide if a cached satisfiability result can be
	 * reused for a given node in the completion graph
	 */
	private CacheSafety					cacheSafety;
	
//	private static int cache = 0;
//	private static int block = 0;
	
	public EmptySRIQStrategy(ABox abox) {
		super( abox );
	}

	public void initialize(Expressivity expressivity) {
		mergeList = new ArrayList<NodeMerge>();

		cachedNodes = new HashMap<Individual, ATermAppl>();

		mnx = new ArrayList<List<Individual>>();
		// add a null entry so Branch.branch index will match with the index in this array
		mnx.add( null );

		assert abox.size() == 1 : "This strategy can only be used with originally empty ABoxes";
		
		blocking = BlockingFactory.createBlocking( expressivity );
		
		Individual root = abox.getIndIterator().next();
		applyUniversalRestrictions( root );
		selfRule.apply( root );
		
		mayNeedExpanding = new LinkedList<Individual>();
		mayNeedExpanding.add( root );

		abox.setBranch( 1 );
		abox.stats.treeDepth = 1;
		abox.setChanged( true );
		abox.setComplete( false );
		abox.setInitialized( true );
	}

	public void complete(Expressivity expr) {
		if( log.isLoggable( Level.FINE ) )
			log.fine( "************  " + EmptySRIQStrategy.class.getName() +  "  ************" );
		
		if( abox.getNodes().isEmpty() ) {
			abox.setComplete( true );
			return;
		}
		else if( abox.getNodes().size() > 1 )
			throw new RuntimeException(
					"This strategy can only be used with an ABox that has a single individual." );
			
		cacheSafety = abox.getCache().getSafety().canSupport( expr ) 
			? abox.getCache().getSafety()
			: CacheSafetyFactory.createCacheSafety( expr );
		
		initialize( expr );

		while( !abox.isComplete() && !abox.isClosed() ) {
			Individual x = getNextIndividual();

			if( x == null ) {
				abox.setComplete( true );
				break;
			}

			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Starting with node " + x );
				abox.printTree();

				abox.validate();
			}

			expand( x );

			if( abox.isClosed() ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Clash at Branch (" + abox.getBranch() + ") " + abox.getClash() );

				if( backtrack() )
					abox.setClash( null );
				else
					abox.setComplete( true );
			}
			else if( expr.hasInverse() && parentNeedsExpanding( x ) ) {
				mayNeedExpanding.removeAll( getDescendants(x.getParent()) );
				mayNeedExpanding.addFirst( x.getParent() );
				continue;
			}
		}

		if( log.isLoggable( Level.FINE ) )
			abox.printTree();

		if( PelletOptions.USE_ADVANCED_CACHING ) {
			// if completion tree is clash free cache all sat concepts
			if( !abox.isClosed() ) {
				for( Iterator<Individual> i = new IndividualIterator( abox ); i.hasNext(); ) {
					Individual ind = i.next();
					ATermAppl c = cachedNodes.get( ind );
					if( c == null )
						continue;

					addCacheSat( c );
				}
			}
		}
	}
	
	private List<Individual> getDescendants(Individual ind) {
		List<Individual> descendants = new ArrayList<Individual>();
		getDescendants( ind, descendants );
		return descendants;
	}

	private void getDescendants(Individual ind, List<Individual> descendants) {
		descendants.add( ind );
		
		for( Edge edge : ind.getOutEdges() ) {
			if( edge.getTo().isIndividual() && !edge.getTo().equals( ind ) ) {
				getDescendants( (Individual) edge.getTo(), descendants );
			}
		}
	}

	private void addCacheSat(ATermAppl c) {
		if( !abox.getCache().putSat( c, true ) )
			return;

		if( log.isLoggable( Level.FINEST ) )
			log.finest( "+++ Cache sat concept " + c );

		if( ATermUtils.isAnd( c ) ) {
			ATermList list = (ATermList) c.getArgument( 0 );
			for( ; !list.isEmpty(); list = list.getNext() ) {
				addCacheSat( (ATermAppl) list.getFirst() );
			}
		}
	}

	private Individual getNextIndividual() {
		if( mayNeedExpanding.isEmpty() ) {
			return null;
		}

		return mayNeedExpanding.get( 0 );
	}
	

    private boolean parentNeedsExpanding(Individual x) {
        if(x.isRoot()) return false;

        Individual parent = x.getParent();

        return parent.canApply(Node.ATOM) || parent.canApply(Node.OR)
            || parent.canApply(Node.SOME) || parent.canApply(Node.MIN)
            || parent.canApply(Node.MAX);
    }

	private void expand(Individual x) {
		checkTimer();

		if( !abox.doExplanation() && PelletOptions.USE_ADVANCED_CACHING ) {
			Timer t = abox.getKB().timers.startTimer( "cache" );
			Bool cachedSat = isCachedSat( x );
			t.stop();
			if( cachedSat.isKnown() ) {
				if( cachedSat.isTrue() ) {
					if( log.isLoggable( Level.FINE ) ) 
						log.fine( "Stop cached " + x );
					mayNeedExpanding.remove( 0 );
				}
				else {
					// set the clash information to be the union of all types
					DependencySet ds = DependencySet.EMPTY;
					for( Iterator<ATermAppl> i = x.getTypes().iterator(); i.hasNext(); ) {
						ATermAppl c = i.next();
						ds = ds.union( x.getDepends( c ), abox.doExplanation() );
					}
					abox.setClash( Clash.atomic( x, ds ) );
				}
				return;
			}
		}

		do {
			if( blocking.isDirectlyBlocked( x ) ) {
				if( log.isLoggable( Level.FINE ) ) 
					log.fine( "Stop blocked " + x );
				mayNeedExpanding.remove( 0 );
				return;
			}
//			else if ( SubsetBlocking.getInstance().isDirectlyBlocked( x ) ) {
//				System.err.println( "BLOCK " + ++block ); 
//			}
			
			unfoldingRule.apply( x );
			if( abox.isClosed() )
				return;

			disjunctionRule.apply( x );
			if( abox.isClosed() )
				return;

			if( x.canApply( Node.ATOM ) || x.canApply( Node.OR ) )
				continue;
			 
			if( blocking.isDynamic() && blocking.isDirectlyBlocked( x ) ) {
				if( log.isLoggable( Level.FINE ) ) 
					log.fine( "Stop blocked " + x );
				mayNeedExpanding.remove( 0 );
				return;
			}

			someValuesRule.apply( x );
			if( abox.isClosed() )
				return;

			minRule.apply( x );
			if( abox.isClosed() )
				return;

			// we don't have any inverse properties but we could have
			// domain restrictions which means we might have to re-apply
			// unfolding and disjunction rules
			if( x.canApply( Node.ATOM ) || x.canApply( Node.OR ) )
				continue;

			chooseRule.apply( x );
			if( abox.isClosed() )
				return;

			maxRule.apply( x );
			if( abox.isClosed() )
				return;

		} while( x.canApply( Node.ATOM ) || x.canApply( Node.OR ) || x.canApply( Node.SOME )
				|| x.canApply( Node.MIN ) );

		mayNeedExpanding.remove( 0 );

		EdgeList sortedSuccessors = x.getOutEdges().sort();
		if( PelletOptions.SEARCH_TYPE == PelletOptions.DEPTH_FIRST ) {
			for( Edge edge : sortedSuccessors ) {
				Node succ = edge.getTo();
				if( !succ.isLiteral() && !succ.equals( x ) ) {
					mayNeedExpanding.add( (Individual) succ );
				}
			}
		}
		else {
			for( int i = sortedSuccessors.size() - 1; i >= 0; i-- ) {
				Edge edge = sortedSuccessors.edgeAt( i );
				Node succ = edge.getTo();
				if( !succ.isLiteral() && !succ.equals( x ) ) {
					mayNeedExpanding.add( (Individual) succ );
				}
			}
		}
	}

	private ATermAppl createConcept(Individual x) {
		int count = 0;
		ATermAppl[] terms = new ATermAppl[x.getTypes().size()];
		for( int t = 0; t < Node.TYPES; t++ ) {
			if( t == Node.NOM )
				continue;
			for( ATermAppl c : x.getTypes( t ) ) {
				if( c.equals( ATermUtils.TOP ) )
					continue;
				terms[count++] = c;
			}
		}

		switch( count ) {
		case 0:
			return ATermUtils.TOP;
		case 1:
			return terms[0];
		default:			
			return ATermUtils.makeAnd( ATermUtils.toSet( terms, count ) );
		}
	}

	private Bool isCachedSat(Individual x) {
		if( x.isRoot() )
			return Bool.UNKNOWN;

		ATermAppl c = createConcept( x );
		
		Bool sat = isCachedSat( c );

		if( sat.isUnknown() ) {
			if( log.isLoggable( Level.FINEST ) )
				log.finest( "??? Cache miss for " + c );
			cachedNodes.put( x, c );
		}
		else if( !cacheSafety.isSafe( c, x ) ) {
			if( log.isLoggable( Level.FINER ) )
				log.finer( "*** Cache unsafe for " + c );
			
//			cacheSafety.isSafe( c, x.getInEdges().edgeAt( 0 ).getRole(), x.getParent() );
//			System.err.println( "CACHE " + ++cache );
			
			sat = Bool.UNKNOWN;
		}
		else if( log.isLoggable( Level.FINER ) )
			log.finer( "*** Cache hit for " + c + " sat = " + sat );
		
		return sat;
	}
	
	private Bool isCachedSat(ATermAppl c) {
		Bool sat = abox.getCachedSat(c);

		// return if we have the cached result or the class is not an intersection
		if (sat.isKnown() || !ATermUtils.isAnd(c))
			return sat;

		sat = null;

		// try to find the satisfiability of an intersection by inspecting the elements inside the conjunction
		ATermList list = (ATermList) c.getArgument(0);
		CachedNode cached1 = null;
		CachedNode cached2 = null;
		for (; !list.isEmpty(); list = list.getNext()) {
			ATermAppl d = (ATermAppl) list.getFirst();
			CachedNode node = abox.getCached(d);

			if (node == null || !node.isComplete()) {
				// we don't have complete sat info about an element so give up
				sat = Bool.UNKNOWN;
				break;
			}
			else if (node.isBottom()) {
				// an element is unsat so the intersection is unsat
				sat = Bool.FALSE;
				break;
			}
			else if (node.isTop()) {
				// do nothing, intersection with TOP is redundant
			}
			else {
				if (cached1 == null)
					cached1 = node;
				else if (cached2 == null)
					cached2 = node;
				else {
					// if there are more than two nodes we cannot do mergability check so give up
					sat = Bool.UNKNOWN;
					break;
				}
			}
		}

		// we can do mergability check
		if (sat == null) {
			if (cached2 == null) {
				// only one element in the intersection that is not TOP so the intersection is
				// satisfiable
				sat = Bool.TRUE;
			}
			else {
				// there are two classes in the intersection, so check if the cahed models can
				// be merged without a clash
				sat = abox.getCache().isMergable(abox.getKB(), cached1, cached2);
			}
		}

		if (sat.isKnown()) {
			abox.getCache().putSat(c, sat.isTrue());
		}

		return sat;	
	}

	@Override
	public void restoreLocal(Individual ind, Branch br) {
		restore( br );
	}

	public void restore(Branch br) {
		 Timer timer = timers.startTimer("restore");

		abox.stats.globalRestores++;
		
		Node clashNode = abox.getClash().getNode();
		List<ATermAppl> clashPath = clashNode.getPath();
		clashPath.add( clashNode.getName() );

		abox.setBranch( br.getBranch() );
		abox.setClash( null );
        // Setting the anonCount to the value at the time of branch creation is incorrect
        // when SMART_RESTORE option is turned on. If we create an anon node after branch
        // creation but node depends on an earlier branch restore operation will not remove
        // the node. But setting anonCount to a smaller number may mean the anonCount will
        // be incremented to that value and creating a fresh anon node will actually reuse
        // the not-removed node. The only advantage of setting anonCount to a smaller value
        // is to keep the name of anon nodes smaller to make debugging easier. For this reason,
        // the above line is not removed and under special circumstances may be uncommented
        // to help debugging only with the intent that it will be commented again after
        // debugging is complete  
		// abox.setAnonCount( br.getAnonCount() );

		mergeList.clear();

		List<ATermAppl> nodeList = abox.getNodeNames();

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "RESTORE: Branch " + br.getBranch() );
			if( br.getNodeCount() < nodeList.size() )
				log.fine( "Remove nodes " + nodeList.subList( br.getNodeCount(), nodeList.size() ) );
		}
		for( int i = 0; i < nodeList.size(); i++ ) {
			ATermAppl x = nodeList.get( i );

			Node node = abox.getNode( x );
			if( i >= br.getNodeCount() ) {
				abox.removeNode( x );
				ATermAppl c = cachedNodes.remove( node );
				if( c != null && PelletOptions.USE_ADVANCED_CACHING ) {
					if( clashPath.contains( x ) ) {
						if( log.isLoggable( Level.FINEST ) )
							log.finest( "+++ Cache unsat concept " + c );
						abox.getCache().putSat( c, false );
					}
					else if( log.isLoggable( Level.FINEST ) )
						log.finest( "--- Do not cache concept " + c + " " + x + " "
								+ clashNode + " " + clashPath );
				}
			}
			else {
				node.restore( br.getBranch() );

				// FIXME should we look at the clash path or clash node
				if( node.equals( clashNode ) ) {
					cachedNodes.remove( node );
				}
			}
		}
		nodeList.subList( br.getNodeCount(), nodeList.size() ).clear();

		for( Iterator<Individual> i = abox.getIndIterator(); i.hasNext(); ) {
			Individual ind = i.next();
			allValuesRule.apply( ind );
		}

		if( log.isLoggable( Level.FINE ) )
			abox.printTree();

		 timer.stop();
	}

	protected boolean backtrack() {
		boolean branchFound = false;

		abox.stats.backtracks++;
		
		while( !branchFound ) {
			completionTimer.check();
			
			int lastBranch = abox.getClash().getDepends().max();

			if( lastBranch <= 0 )
				return false;

			List<Branch> branches = abox.getBranches();
			abox.stats.backjumps += (branches.size() - lastBranch);
			Branch newBranch = null;
			if( lastBranch <= branches.size() ) {
				branches.subList( lastBranch, branches.size() ).clear();
				newBranch = branches.get( lastBranch - 1 );

				if( log.isLoggable( Level.FINE ) )
					log.fine( "JUMP: " + lastBranch );
				if( newBranch == null || lastBranch != newBranch.getBranch() )
					throw new RuntimeException(
							"Internal error in reasoner: Trying to backtrack branch " + lastBranch
									+ " but got " + newBranch );

				if( newBranch.getTryNext() < newBranch.getTryCount() )
					newBranch.setLastClash( abox.getClash().getDepends() );

				newBranch.setTryNext( newBranch.getTryNext() + 1 );

				if( newBranch.getTryNext() < newBranch.getTryCount() ) {
					restore( newBranch );

					branchFound = newBranch.tryNext();
				}
			}

			if( !branchFound ) {
				abox.getClash().getDepends().remove( lastBranch );
				if( log.isLoggable( Level.FINE ) )
					log.fine( "FAIL: " + lastBranch );
			}
			else {
				// create another copy of the mnx list here because we may backtrack to the same
				// branch multiple times and we want the same copy to be available every time
				mayNeedExpanding = new LinkedList<Individual>( mnx.get( newBranch.getBranch() ) );
				mnx.subList( newBranch.getBranch() + 1, mnx.size() ).clear();
				if( log.isLoggable( Level.FINE ) )
					log.fine( "MNX : " + mayNeedExpanding );
			}

		}

		abox.validate();

		return branchFound;
	}

	public void addBranch(Branch newBranch) {
		super.addBranch( newBranch );

		assert mnx.size() == newBranch.getBranch() : mnx.size() + " != " + newBranch.getBranch();
		
		// create a copy of the mnx list so that changes in the current branch will not affect it
		mnx.add( new ArrayList<Individual>( mayNeedExpanding ) );
	}
}
