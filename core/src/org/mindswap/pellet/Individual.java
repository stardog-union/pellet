//The MIT License
//
//Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to
//deal in the Software without restriction, including without limitation the
//rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
//sell copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.

package org.mindswap.pellet;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;

import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;

/*
 * Created on Aug 27, 2003
 *
 */

/**
 * @author Evren Sirin
 *
 */
public class Individual extends Node implements CachedNode {
	private EdgeList outEdges;

	@SuppressWarnings("unchecked")
	private ArrayList<ATermAppl>[] types = new ArrayList[TYPES]; // Known warning message
	public int[] applyNext = new int[TYPES];

	private int nominalLevel;
	
	private Individual parent;
	
	private boolean modifiedAfterMerge = false;
	
	private short depth;
	
	private boolean isBlocked;
	
	Individual(ATermAppl name, ABox abox, Individual parent) {
		super(name, abox);

		this.parent = parent;
		
		if( parent == null ) {
			nominalLevel = NOMINAL;
			depth = 0;
		}
		else {
			nominalLevel = BLOCKABLE;		
			depth = (short) (parent.depth + 1);
		}
		
		for(int i = 0; i < TYPES; i++) {
			types[i] = new ArrayList<ATermAppl>();
			applyNext[i] = 0;		
		}
		
		outEdges = new EdgeList();
	}
	
	Individual(Individual ind, ABox abox) {
		super(ind, abox);
		
		nominalLevel = ind.nominalLevel;
		parent = ind.parent;

		for(int i = 0; i < TYPES; i++) {
			types[i] = new ArrayList<ATermAppl>(ind.types[i]);		
			applyNext[i] = ind.applyNext[i];		
		}
			
		if( isPruned() )
			outEdges = new EdgeList(ind.outEdges);
		else
			outEdges = new EdgeList(ind.outEdges.size());		
	}
	
	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public short getDepth() {
		return depth;
	}
	
	public DependencySet getNodeDepends() {
		return getDepends( ATermUtils.TOP );
	}
	
	public boolean isLiteral() {
	    return false;
	}
	
	public boolean isIndividual() {
	    return true;
	}

	public boolean isNominal() {
		return nominalLevel != BLOCKABLE;
	}
	
	public boolean isBlockable() {
		return nominalLevel == BLOCKABLE;
	}
	
	public boolean isIndependent() {
		return true;
	}
	
	public void setNominalLevel(int level) {
	    nominalLevel = level;
	    
	    if( nominalLevel != BLOCKABLE )
	        parent = null;
	}

	public int getNominalLevel() {
	    return nominalLevel;
	}
	
    public ATermAppl getTerm() {
        return name;
    }

	public Node copyTo(ABox abox) {
		return new Individual(this, abox);
	}
	
	public List<ATermAppl> getTypes(int type) {
		return types[type];
	}
		
	public boolean isDifferent( Node node ) {
	    if( PelletOptions.USE_UNIQUE_NAME_ASSUMPTION ) {
	        if( isNamedIndividual() && node.isNamedIndividual() )
	            return !name.equals( node.name );
	    }
	    
		return differents.containsKey(node);
	}
		
	public Set<Node> getDifferents() {
		return differents.keySet();
	}

	public DependencySet getDifferenceDependency( Node node ) {
	    if( PelletOptions.USE_UNIQUE_NAME_ASSUMPTION ) {
	        if( isNamedIndividual() && node.isNamedIndividual() )
	            return DependencySet.INDEPENDENT;
	    }
	    
		return differents.get(node);
	}	

	/**
	 * Collects atomic concepts such that either that concept or its negation 
	 * exist in the types list without depending on any non-deterministic branch. 
	 * First list is filled with types and second list is filled with non-types,
	 * i.e. this individual can never be an instance of any element in the 
	 * second list. 
	 * 
	 * @param types All atomic concepts found in types
	 * @param nonTypes All atomic concepts
	 */
	public void getObviousTypes( List<ATermAppl> types, List<ATermAppl> nonTypes ) {
		for( ATermAppl c : getTypes( Node.ATOM ) ) {
            if( getDepends( c ).isIndependent() ) {
                if( ATermUtils.isPrimitive( c ) ) {
                    types.add( c );
                }
                else if( ATermUtils.isNegatedPrimitive( c ) )                   
                    nonTypes.add( (ATermAppl) c.getArgument( 0 ) );
            }
        }
	}
	
	public boolean canApply(int type) {
		return applyNext[type] < types[type].size();
	}
	
	public void addType(ATermAppl c, DependencySet ds) {
		addType( c, ds, true );
	}
		
	void addType(ATermAppl c, DependencySet ds, boolean checkForPruned ) {
		if( checkForPruned ) {
		    if( isPruned() )
		        throw new InternalReasonerException( "Adding type to a pruned node " + this + " " + c );
		    else if( isMerged() )
		        return;
		}
		else if( isMerged() ) {
			modifiedAfterMerge = true;
		}
		
		if( depends.containsKey( c ) ) {
			if( !checkForPruned && ds.isIndependent() ) {
				depends.put( c, ds );
			}
				
			return;		
		}
        
//        if( ABox.log.isLoggable( Level.FINE ) ) 
//            ABox.log.fine( "TYPE: " + this + " " + c );        
		
		// if we are checking entailment using a precompleted ABox, abox.branch 
		// is set to -1. however, since applyAllValues is done automatically
		// and the edge used in applyAllValues may depend on a branch we want
		// this type to be deleted when that edge goes away, i.e. we backtrack
		// to a position before the max dependency of this type
		int b = abox.getBranch();
		int max = ds.max();
		if(b == -1 && max != 0)
		    b = max + 1;
		
		ds = ds.copy( b );
		
		depends.put(c, ds);

		abox.setChanged( true );


	    // add to effected list
	    if( abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS )
			abox.getBranchEffectTracker().add( abox.getBranch(), this.getName() );		
		
		//create new queue element
		QueueElement qElement = new QueueElement(this, c);
		
		ATermAppl notC = ATermUtils.negate( c );
		DependencySet clashDepends = depends.get( notC );
		if( clashDepends != null ) {
			ATermAppl positive = ATermUtils.isNot( notC )
				? c
				: notC;
			clashDepends = clashDepends.union( ds, abox.doExplanation() );
			clashDepends = clashDepends.copy( abox.getBranch() );
			abox.setClash( Clash.atomic( this, clashDepends, positive ) );
		}
		
		if (ATermUtils.isPrimitive(c)) {
			setChanged(ATOM);
			types[ATOM].add(c);

			if(PelletOptions.USE_COMPLETION_QUEUE){
				//update completion queue
				abox.getCompletionQueue().add(qElement, NodeSelector.ATOM );
			}
		}
		else {
			if (c.getAFun().equals(ATermUtils.ANDFUN)){
				for(ATermList cs = (ATermList) c.getArgument(0); !cs.isEmpty(); cs = cs.getNext()) {
					ATermAppl conj = (ATermAppl) cs.getFirst();
					
					addType(conj, ds, checkForPruned);
				}			
			}
			else if (c.getAFun().equals(ATermUtils.ALLFUN)) {
				setChanged(ALL);			
				types[ALL].add(c);			

				if(PelletOptions.USE_COMPLETION_QUEUE){
					//update completion queue
					abox.getCompletionQueue().add( qElement, NodeSelector.UNIVERSAL );
				}
			}
			else if (c.getAFun().equals(ATermUtils.MINFUN)) {
				if(!isRedundantMin(c)) {
					types[MIN].add(c);
					setChanged(MIN);
					
					if(PelletOptions.USE_COMPLETION_QUEUE){		
						//update completion queue
						abox.getCompletionQueue().add(qElement, NodeSelector.MIN_NUMBER );
					}
					
					// check min clash after concept is added to the type
					// list. otherwise a clash found will prevent the
					// addition to the type list and term will be only in the
					// dependency map. smart restore may not remove the cardinality
					// from dependency map leaving the node in an invalid state.
					checkMinClash(c, ds);
				}				
			}
			else if(c.getAFun().equals(ATermUtils.NOTFUN)) {
				ATermAppl x = (ATermAppl) c.getArgument(0);
				if(ATermUtils.isAnd(x)) {
					setChanged(OR);
					types[OR].add(c);
					
					if(PelletOptions.USE_COMPLETION_QUEUE){
						//update completion queue
						abox.getCompletionQueue().add( qElement, NodeSelector.DISJUNCTION );
					}
				}
				else if(ATermUtils.isAllValues(x)) {
					setChanged(SOME);
					types[SOME].add(c);
					
					if(PelletOptions.USE_COMPLETION_QUEUE){
						//update completion queue					
						abox.getCompletionQueue().add( qElement, NodeSelector.EXISTENTIAL );
					}
				}
				else if(ATermUtils.isMin(x)) {
					if(!isRedundantMax(x)) {
						types[MAX].add(c);
						setChanged(MAX);
						
						if(PelletOptions.USE_COMPLETION_QUEUE){
							//update completion queue						
							abox.getCompletionQueue().add( qElement, NodeSelector.MAX_NUMBER );
							abox.getCompletionQueue().add( qElement, NodeSelector.CHOOSE );
							abox.getCompletionQueue().add( qElement, NodeSelector.GUESS );
						}
						
						// check max clash after concept is added to the type
						// list. otherwise a clash found will prevent the
						// addition to the type list and term will be only in the
						// dependency map. smart restore may not remove the cardinality
						// from depdendency map leaving the node in an invalid state.
						checkMaxClash(c, ds);
					}
				}
				else if(ATermUtils.isNominal(x)) {
					setChanged(ATOM);
					types[ATOM].add(c);
						
					if(PelletOptions.USE_COMPLETION_QUEUE){
						//update completion queue					
						abox.getCompletionQueue().add( qElement, NodeSelector.ATOM );
					}
				}
				else if (ATermUtils.isSelf(x)) {
	            	ATermAppl p = (ATermAppl) x.getArgument( 0 );
	            	Role role = abox.getRole( p );
	            	// during loading role would be null
	            	if( role != null ) {
	            		EdgeList selfEdges = outEdges.getEdges(role).getEdgesTo( this );
	            		if( !selfEdges.isEmpty() ) {	            			
	            			abox.setClash(Clash.unexplained( this, selfEdges.getDepends( abox.doExplanation() )));
	            		}
	            	}
	            }
				else if(x.getArity() == 0) {
					setChanged(ATOM);
					types[ATOM].add(c);
					
					if(PelletOptions.USE_COMPLETION_QUEUE){
						//update completion queue					
						abox.getCompletionQueue().add( qElement, NodeSelector.ATOM );
					}
				}
				else
				    throw new InternalReasonerException( "Invalid type " +  c + " for individual " + name);
			}
			else if (c.getAFun().equals(ATermUtils.VALUEFUN)) {
				setChanged(NOM);
				types[NOM].add(c);
			
				if(PelletOptions.USE_COMPLETION_QUEUE){
					//update completion queue				
					abox.getCompletionQueue().add( qElement, NodeSelector.NOMINAL );
				}
			}		
            else if (ATermUtils.isSelf(c)) {
            	setChanged( ATOM );
                types[ATOM].add(c);
            }
			else {
				throw new InternalReasonerException("Warning: Adding invalid class constructor - " + c);
			}				
		}		
	}
	
	public boolean checkMinClash(ATermAppl minCard, DependencySet minDepends) {
		Role minR = abox.getRole(minCard.getArgument(0));
		if( minR == null )
			return false;
		int min = ((ATermInt) minCard.getArgument(1)).getInt();
        ATermAppl minC = (ATermAppl) minCard.getArgument(2);
		
		if(minR.isFunctional() && min > 1) {
			abox.setClash(Clash.minMax(this, minDepends.union(minR.getExplainFunctional(), abox.doExplanation())));
			
			return true;
		}

		for(ATermAppl mc : types[MAX]) {
			// max(r, n) is in normalized form not(min(p, n + 1))
			ATermAppl maxCard = (ATermAppl) mc.getArgument(0);								
			Role maxR = abox.getRole(maxCard.getArgument(0));
			if( maxR == null )
				return false;
			int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;
            ATermAppl maxC = (ATermAppl) maxCard.getArgument(2);
            
			if(max < min && minC.equals( maxC ) && minR.isSubRoleOf(maxR)) {
				DependencySet maxDepends = getDepends(mc);
				DependencySet subDepends = maxR.getExplainSub(minR.getName());
				DependencySet ds = minDepends.union(maxDepends, abox.doExplanation()).union(subDepends, abox.doExplanation());
				
				abox.setClash(Clash.minMax(this, ds));

				return true;
			}
		}		
		
		return false;
	}

	public boolean checkMaxClash(ATermAppl normalizedMax, DependencySet maxDepends) {
        ATermAppl maxCard = (ATermAppl) normalizedMax.getArgument(0);
		Role maxR = abox.getRole(maxCard.getArgument(0));
		if( maxR == null )
			return false;
		int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;
        ATermAppl maxC = (ATermAppl) maxCard.getArgument(2);

		for(ATermAppl minCard : types[MIN]) {											
			Role minR = abox.getRole(minCard.getArgument(0));	
			if( minR == null )
				return false;
			int min = ((ATermInt) minCard.getArgument(1)).getInt();
            ATermAppl minC = (ATermAppl) minCard.getArgument(2);

			if(max < min && minC.equals( maxC ) && minR.isSubRoleOf(maxR)) {
				DependencySet minDepends = getDepends(minCard);
				DependencySet subDepends = maxR.getExplainSub(minR.getName());
				DependencySet ds = minDepends.union(maxDepends, abox.doExplanation()).union(subDepends, abox.doExplanation());
				
				abox.setClash(Clash.minMax(this, ds));

				return true;
			}
		}		
		
		return false;
	}

	public boolean isRedundantMin(ATermAppl minCard) {
		Role minR = abox.getRole(minCard.getArgument(0));
		
        if( minR == null )
            return false;

		int min = ((ATermInt) minCard.getArgument(1)).getInt();
        ATermAppl minQ = (ATermAppl) minCard.getArgument( 2 );
        
        for( ATermAppl prevMinCard : types[MIN] ) {
			Role prevMinR = abox.getRole(prevMinCard.getArgument(0));
			
			 if( prevMinR == null )
		         continue;
			 
			int prevMin = ((ATermInt) prevMinCard.getArgument(1)).getInt() - 1;
            ATermAppl prevMinQ = (ATermAppl) prevMinCard.getArgument( 2 );
            
			if( min <= prevMin 
                && prevMinR.isSubRoleOf( minR ) 
                && ( minQ.equals( prevMinQ ) 
                    || ATermUtils.isTop( minQ ) ) )
				return true;
		}

		return false;
	}

	public boolean isRedundantMax(ATermAppl maxCard) {
		Role maxR = abox.getRole(maxCard.getArgument(0));
        if( maxR == null )
            return false;
        
		int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;

		if(max == 1 && maxR != null && maxR.isFunctional())
			return true;
        
        ATermAppl maxQ = (ATermAppl) maxCard.getArgument( 2 );
		
        for( ATermAppl mc : types[MAX] ) {
			// max(r, n) is in normalized form not(min(p, n + 1))
			ATermAppl prevMaxCard = (ATermAppl) mc.getArgument(0);								
			Role prevMaxR = abox.getRole(prevMaxCard.getArgument(0));
						
			 if( prevMaxR == null )
		         continue;
			 
			int prevMax = ((ATermInt) prevMaxCard.getArgument(1)).getInt() - 1;
            ATermAppl prevMaxQ = (ATermAppl) prevMaxCard.getArgument( 2 );
            
			if( max >= prevMax
                && maxR.isSubRoleOf( prevMaxR ) 
                && ( maxQ.equals( prevMaxQ ) 
                    || ATermUtils.isTop( prevMaxQ ) ) )
				return true;
		}		
		
		return false;
	}

	public DependencySet hasMax1( Role r ) {
		for( ATermAppl mc : types[MAX] ) {
            // max(r, n, c) is in normalized form not(min(p, n + 1))
			ATermAppl maxCard = (ATermAppl) mc.getArgument(0);
			Role maxR = abox.getRole(maxCard.getArgument(0));
			int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;
			ATermAppl maxQ = (ATermAppl) maxCard.getArgument( 2 );
            
			// FIXME returned dependency set might be wrong 
			// if there are two types max(r,1) and max(p,1) where r subproperty of p
			// then the dependency set what we return might be wrong
			if( max == 1 && r.isSubRoleOf( maxR ) && ATermUtils.isTop( maxQ ) )
				return getDepends( mc ).union( r.getExplainSub( maxR.getName() ), abox.doExplanation() );
		}		
		
		return null;
	}	
	
	public int getMaxCard( Role r ) {
	    int min = Integer.MAX_VALUE;
	    for(ATermAppl mc : types[MAX]) {
			// max(r, n) is in normalized form not(min(p, n + 1))
			ATermAppl maxCard = (ATermAppl) mc.getArgument(0);								
			Role maxR = abox.getRole( maxCard.getArgument(0) );
			int max = ((ATermInt) maxCard.getArgument(1)).getInt() - 1;

			if( r.isSubRoleOf( maxR ) && max < min )
				min = max;
		}		
		
		if( r.isFunctional() && min > 1 )
		    min = 1;
		
		return min;
	}
	
	public int getMinCard( Role r, ATermAppl c ) {
	    int maxOfMins = 0;
	    for(ATermAppl minCard : types[MIN]) {							
			Role minR = abox.getRole( minCard.getArgument(0) );			
			int min = ((ATermInt) minCard.getArgument(1)).getInt();
			ATermAppl minC = (ATermAppl) minCard.getArgument(2);
			
			if( minR.isSubRoleOf( r ) && maxOfMins < min && (minC.equals(c) || c.equals(TOP)))
				maxOfMins = min;
		}		
		
		return maxOfMins;
	}
	
	public boolean removeType(ATermAppl c) {
		boolean removed = super.removeType( c );

		// it is important to continue removal here because restore function
		// modified depends map directly 
		if (ATermUtils.isPrimitive(c) || ATermUtils.isSelf(c)) {
			types[ATOM].remove(c);
		}
		else {
			if(c.getAFun().equals(ATermUtils.ANDFUN)) {
//			    types[AND].remove(c);
			}
			else if (c.getAFun().equals(ATermUtils.ALLFUN)) {
				types[ALL].remove(c);
			}
			else if (c.getAFun().equals(ATermUtils.MINFUN)) {
				types[MIN].remove(c);
			}
			else if (c.getAFun().equals(ATermUtils.NOTFUN)) {
				ATermAppl x = (ATermAppl) c.getArgument(0);
				if(ATermUtils.isAnd(x)) {
					types[OR].remove(c);
				}
				else if(ATermUtils.isAllValues(x)) {
					types[SOME].remove(c);
				}
				else if(ATermUtils.isMin(x)) {
					types[MAX].remove(c);
				}
				else if(ATermUtils.isNominal(x)) {
					types[ATOM].remove(c);
				}
				else if(x.getArity() == 0) {
					types[ATOM].remove(c);
				}
				else if(ATermUtils.isSelf(x)) {
					// do nothing
				}
				else
				    throw new InternalReasonerException( "Invalid type " +  c + " for individual " + name);				
			}
			else if(c.getAFun().equals(ATermUtils.VALUEFUN))
				types[NOM].remove(c);
			else
				throw new RuntimeException("Invalid concept " + c);
		}
		
		return removed;
	}

	final public boolean isLeaf() {
		return !isRoot() && outEdges.isEmpty();
	}
	
	final public Individual getSame() {
		return (Individual) super.getSame();
	}

	final public Set<Node> getRSuccessors(Role r, ATermAppl c) {
        Set<Node> result = new HashSet<Node>();
        
        EdgeList edges = outEdges.getEdges(r);
        for(int i = 0, n = edges.size(); i < n; i++) {
            Edge edge = edges.edgeAt(i); 
            Node other = edge.getNeighbor( this );
            if( other.hasType( c) )
                result.add( other );
        }
        
        return result;
	}

	final public EdgeList getRSuccessorEdges(Role r) {
		return outEdges.getEdges(r);
	}

	final public EdgeList getRPredecessorEdges(Role r) {
		return inEdges.getEdges(r);
	}
    
	final public Set<Node> getRNeighbors(Role r) {
		return getRNeighborEdges(r).getNeighbors(this);
	}
	
	public EdgeList getRNeighborEdges(Role r) {
		EdgeList neighbors = outEdges.getEdges( r );

		Role invR = r.getInverse();
		// inverse of datatype properties is not defined
		if( invR != null )
			neighbors.addEdgeList( inEdges.getEdges( invR ) );

		return neighbors;
	}	
	
	
	/**
	 * Get neighbor edges to a specific node
	 * 
	 * @param r
	 * @param node
	 * @return
	 */
	public EdgeList getRNeighborEdges(Role r, Node node) {
		EdgeList neighbors = outEdges.getEdgesTo( r, node );

		Role invR = r.getInverse();
		// inverse of datatype properties is not defined
		if( invR != null )
			neighbors.addEdgeList( inEdges.getEdgesFrom( (Individual) node, invR ) );

		return neighbors;
	}	
		
	public EdgeList getEdgesTo(Node x) {
		return outEdges.getEdgesTo(x);
	}

	public EdgeList getEdgesTo(Node x, Role r) {
		return outEdges.getEdgesTo(x).getEdges(r);
	}
	

	/**
	 * Checks if this individual has at least n distinct r-neighbors that has 
     * a specific type. 
	 * 
	 * @param r Role we use to find neighbors
	 * @param n Number of neighbors 
     * @param c The type that all neighbors should belong to 
	 * @return The union of dependencies for the edges leading to neighbors and 
     * the dependency of the type assertion for each neighbor 
	 */
	public DependencySet hasDistinctRNeighborsForMax( Role r, int n, ATermAppl c ) {
//	    Timer t = abox.getKB().timers.startTimer("hasDistinctRNeighbors1"); 
	    
        boolean hasNeighbors = false; 
        
	    // get all the edges to x with a role (or subrole of) r
		EdgeList edges = getRNeighborEdges( r );

		if(edges.size() >= n) {
			List<List<Node>> allDisjointSets = new ArrayList<List<Node>>();			
			
		outerloop:	
			for(int i = 0; i < edges.size(); i++ ) {
				Node y = edges.edgeAt(i).getNeighbor(this);
                
                if( !y.hasType( c ) )
                    continue;                    
				
				boolean added = false;
				for(int j = 0; j < allDisjointSets.size(); j++) {
					List<Node> disjointSet = allDisjointSets.get(j);
					int k = 0;
					for(; k < disjointSet.size(); k++) {
						Node z = disjointSet.get(k);
						if(!y.isDifferent(z)) 
							break;
					}
					if(k == disjointSet.size()) {
						added = true;
						disjointSet.add(y);
						if(disjointSet.size() >= n) {
                            hasNeighbors = true;
							break outerloop;
						}
					}
				}
				if(!added) {
					List<Node> singletonSet = new ArrayList<Node>();
					singletonSet.add(y);
					allDisjointSets.add( singletonSet );
					if( n == 1 ) {
                        hasNeighbors = true;
						break outerloop;
					}						
				}
			}			
		}
//		t.stop();

        if( !hasNeighbors )
            return null;
        
        // we are being overly cautious here by getting the union of all
        // the edges to all r-neighbors 
        DependencySet ds = DependencySet.EMPTY;
        for( Edge edge : edges ) {
        	ds = ds.union( r.getExplainSubOrInv( edge.getRole() ), abox.doExplanation() );
            ds = ds.union( edge.getDepends(), abox.doExplanation() );
            Node node = edge.getNeighbor( this );
            DependencySet typeDS = node.getDepends( c );
            if( typeDS != null )
                ds = ds.union( typeDS, abox.doExplanation() );
        }        
        
		return ds;
	}

	public boolean hasDistinctRNeighborsForMin( Role r, int n, ATermAppl c ) {
	    return hasDistinctRNeighborsForMin( r, n, c, false );
	}
	
	/**
	 * Returns true if this individual has at least n distinct r-neighbors. If
	 * only nominal neighbors are wanted then blockable ones will simply be 
	 * ignored (note that this should only happen if r is an object property)
	 * 
	 * @param r
	 * @param n
	 * @param onlyNominals
	 * @return
	 */
	public boolean hasDistinctRNeighborsForMin( Role r, int n, ATermAppl c, boolean onlyNominals ) {
	    // get all the edges to x with a role (or subrole of) r
		EdgeList edges = getRNeighborEdges(r);
	    
		if( n == 1 && !onlyNominals && c.equals( ATermUtils.TOP ) ) 
		    return !edges.isEmpty();		
		
	    if( edges.size() < n ) 
		    return false;

		List<List<Node>> allDisjointSets = new ArrayList<List<Node>>();					
		for(int i = 0; i < edges.size(); i++ ) {
			Node y = edges.edgeAt(i).getNeighbor(this);
            
            if( !y.hasType( c ) )
                continue;
            
			if( onlyNominals ) {
			    if( y.isBlockable() )
			        continue;
			    else if( n == 1 )
			        return true;
			}
			    
			
			boolean added = false;
			for(int j = 0; j < allDisjointSets.size(); j++) {
			    boolean addToThis = true;
				List<Node> disjointSet = allDisjointSets.get(j);
				for(int k = 0; k < disjointSet.size(); k++) {
					Node z = disjointSet.get(k);
					if(!y.isDifferent(z)) {
					    addToThis = false;
					    break;
					}					
				}
				if(addToThis) {
				    added = true;
					disjointSet.add(y);
					if(disjointSet.size() >= n)
						return true;
				}
			}
			if(!added) {
				List<Node> singletonSet = new ArrayList<Node>();
				singletonSet.add(y);
				allDisjointSets.add(singletonSet);					
			}
			
			if(n==1 && allDisjointSets.size()>=1)
				return true;
		}			
		
		return false;
	}

	final public boolean hasRNeighbor(Role r) {
		if( outEdges.hasEdge( r ) )
		    return true;
		
		Role invR = r.getInverse();
		if(invR != null && inEdges.hasEdge(invR) ) 
		    return true;
		
		return false;
	}
	
	public boolean hasRSuccessor( Role r ) {	
		return outEdges.hasEdge( r );
	}

	public boolean hasSuccessor( Node x ) {
		return outEdges.hasEdgeTo( x );
	}
	
	public final boolean hasRSuccessor( Role r, Node x ) {
		return outEdges.hasEdge( this, r, x );
	}	
	
	/**
	 * Check the property assertions to see if it is possible for this individual to
	 * have the value for the given datatype property. This function is meaningful
	 * only called for individuals in a completed ABox (a pseudo model for the KB).
	 * In a completed ABox, individual will have some literal successors that may
	 * or may not have a known value. The individual has the data property value
	 * only if it has a literal successor that has the exact given value and the
	 * edge between the individual and the literal does not depend on any non-
	 * deterministic branch. If the literal value is there but the edge depends
	 * on a branch then we cannot exactly say if the literal value is there or
	 * not. If there is no literal successor with the given value then we can
	 * for sure say that individual does not have the data property value
	 * (because it does not have the value in at least one model)  
	 * 
	 * 
	 * @param r
	 * @param value
	 * @return Bool.TRUE if the individual definetely has the property value,
	 * Bool.FALSE if the individual definetely does NOT have the property value
	 * and Bool.UNKNOWN if it cannot be determined for sure, i.e. consistency check is 
	 * required 
	 */
	public Bool hasDataPropertyValue( Role r, Object value ) {
	    Bool hasValue = Bool.FALSE;
	    
		EdgeList edges = outEdges.getEdges( r );
		for(int i = 0; i < edges.size(); i++) {
		    Edge edge = edges.edgeAt( i );
		    DependencySet ds = edge.getDepends();
		    Literal literal = (Literal) edge.getTo();
		    Object literalValue = literal.getValue();
		    if( value != null && literalValue == null ) {
				try {
					if( abox.dtReasoner.isSatisfiable( literal.getTypes(), value ) )
						hasValue = Bool.UNKNOWN;
					else
						hasValue = Bool.FALSE;
				} catch( DatatypeReasonerException e ) {
					final String msg = "Unexpected datatype reasoner exception while checking property value: "
							+ e.getMessage();
					log.severe( msg );
					throw new InternalReasonerException( msg );
				}
			}
		    else if( value == null || value.equals( literalValue ) ) {
		        if( ds.isIndependent() )
		            return Bool.TRUE;
		        else
		            hasValue = Bool.UNKNOWN;
		    }
		}
		
		return hasValue;
	}
	
	public boolean hasRNeighbor(Role r, Node x) {
		if(hasRSuccessor(r, x))
			return true;
		
		if(x instanceof Individual)
			return ((Individual) x).hasRSuccessor(r.getInverse(), this);
			
		return false;
	}

	protected void addInEdge(Edge edge) {        
		setChanged(ALL);
		setChanged(MAX);
		applyNext[MAX] = 0;
        
		inEdges.addEdge( edge );
	}	

	protected void addOutEdge(Edge edge) {
		setChanged(ALL);
		setChanged(MAX);
		applyNext[MAX] = 0;

		if ( edge.getRole().isBottom() ) {
			abox.setClash( Clash.bottomProperty( edge.getFrom(), edge.getDepends(), edge.getRole().getName() ) );
		} else {
			outEdges.addEdge(edge);
		}
	}
	
	public Edge addEdge( Role r, Node x, DependencySet ds ) {		
	    
		// add these nodes to the effected list
		if( abox.getBranch() > 0 && PelletOptions.TRACK_BRANCH_EFFECTS ) {
			abox.getBranchEffectTracker().add( abox.getBranch(), this.getName() );
			abox.getBranchEffectTracker().add( abox.getBranch(), x.getName() );
		}
		
		if ( r.isBottom() ) {
			abox.setClash( Clash.bottomProperty( this, ds, r.getName() ) );
			return null;
		}

		if( hasRSuccessor( r, x ) || r.isTop() ) {
		    // TODO we might miss some of explanation axioms
	    	if( log.isLoggable( Level.FINE ) )
	    		log.fine( "EDGE: " + this + " -> " + r + " -> " + x + ": " + ds + " " + getRNeighborEdges( r ).getEdgesTo( x ) );
		    return null;
		}		
	    		
	    if( isPruned() )
	        throw new InternalReasonerException( "Adding edge to a pruned node " + this + " " + r + " " + x );
	    else if( isMerged() )
	        return null;

		abox.setChanged( true );
		setChanged(ALL);
		setChanged(MAX);
		applyNext[MAX] = 0;
		
		ds = ds.copy( abox.getBranch() );
		
	    Edge edge = new DefaultEdge(r, this, x, ds);
	    
		outEdges.addEdge(edge);
		x.addInEdge(edge);
		
		return edge;
	}
	

	final public EdgeList getOutEdges() {
		return outEdges;
	}	
	
    public Individual getParent() {
        return parent;
    }
	
    /**
     * Resets this node (types, edges, sames, differents) to contain only asserted
     * information. This function can be seen a specialized case of restore but
     * a special function is needed both for correctness (e.g. SMART_RESTORE option
     * should not change behavior) and performance
     */
    public void reset(boolean onlyApplyTypes) {
    	super.reset( onlyApplyTypes );
    	
		for(int i = 0; i < TYPES; i++)
			applyNext[i] = 0;

    	if( onlyApplyTypes )
			return;
		
    	outEdges.reset();
    }
    
    protected void resetTypes() {
    	for(int type = 0; type < TYPES; type++) {
    		ArrayList<ATermAppl> list = types[type];
    		int size = list.size();
    		for(int i = 0; i < size; i++) {
    			ATermAppl c = list.get(i);
                
    			if( depends.get(c).getBranch() != DependencySet.NO_BRANCH ) {
    				// rather deleting the element from an ArrayList move
    				// it to the end so we can purge everything from the 
    				// tail of the list (note: if we change the list impl
    				// used here to a LinkedList we can modify this bit)
    				Collections.swap( list, i--, --size );
    				
    				depends.remove( c );
    			}
    		}
    		
    		// remove everything from the end of list 
    		if( size < list.size() )
    			list.subList( size, list.size() ).clear();
    	}
    	
    	Iterator<Entry<ATermAppl,DependencySet>> i = depends.entrySet().iterator();
    	while( i.hasNext() ) {
    		Entry<ATermAppl,DependencySet> e = i.next();
    		if( e.getValue().getBranch() != DependencySet.NO_BRANCH ) {
    			i.remove();
    		}
		}
    }
	
	public boolean restore( int branch ) {
		Boolean restorePruned = restorePruned( branch );
		if( Boolean.FALSE.equals( restorePruned ) ) {
			return restorePruned;
		}

		boolean restored = Boolean.TRUE.equals( restorePruned );
		
		restored |= super.restore( branch );
				
		for(int i = 0; i < TYPES; i++)
			applyNext[i] = 0;		
        
		boolean removed = false;

		for( Iterator<Edge> i = outEdges.iterator(); i.hasNext(); ) {
			Edge e = i.next();
			DependencySet d = e.getDepends();
				
			if( d.getBranch() > branch ) {				
				if( log.isLoggable( Level.FINE ) ) 
                    log.fine("RESTORE: " + name + " remove edge " + e + " " + d.max() + " " + branch);
				i.remove();		
				
				restored = true;
				removed = true;
				if( PelletOptions.USE_INCREMENTAL_CONSISTENCY )
					abox.getIncrementalChangeTracker().addDeletedEdge( e );
			}			
		}		
				
		//if we removed an edge the update the queue
		if( removed && PelletOptions.USE_COMPLETION_QUEUE ) {
			abox.getCompletionQueue().add( new QueueElement( this ), NodeSelector.EXISTENTIAL );
			abox.getCompletionQueue().add( new QueueElement( this ), NodeSelector.MIN_NUMBER );
		}
		
		if( modifiedAfterMerge && restored ) {
			for( Entry<ATermAppl, DependencySet> entry : depends.entrySet() ) {
				ATermAppl c = entry.getKey();
				ATermAppl notC = ATermUtils.negate( c );

				DependencySet ds = depends.get( notC );
				if( ds != null ) {
					DependencySet clashDepends = entry.getValue();
					ATermAppl positive = ATermUtils.isNot( notC )
						? c
						: notC;
					clashDepends = clashDepends.union( ds, abox.doExplanation() );
					abox.setClash( Clash.atomic( this, clashDepends, positive ) );
				}
			}
			modifiedAfterMerge = false;
		}
		
		return restored;
	}
	
	final public boolean removeEdge(Edge edge) {
		boolean removed = outEdges.removeEdge(edge);
		
		if( !removed )
            throw new InternalReasonerException(
                "Trying to remove a non-existing edge " + edge);
		
		return true;
	}
	
	/**
	 * Prune the given node by removing all links going to nominal nodes and recurse
	 * through all successors. No need to remove incoming edges because either the node
	 * is the first one being pruned so the merge function already handled it or
	 * this is a successor node and its successor is also being pruned
	 * 
	 * @param succ
	 * @param ds
	 */
	public void prune( DependencySet ds ) {    
		
		// add to effected list
		if( abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS )
			abox.getBranchEffectTracker().add( abox.getBranch(), this.getName() );

		pruned = ds;

		for(int i = 0; i < outEdges.size(); i++) {
            Edge edge = outEdges.edgeAt( i );
            Node succ = edge.getTo();
            
            if( succ.isPruned() )
                continue;
            else if( succ.isNominal() )
                succ.removeInEdge( edge );
            else
                succ.prune( ds );                
        }
	}

	public void unprune( int branch ) {
	    super.unprune( branch );

	    boolean added = false;
	    
        for(int i = 0; i < outEdges.size(); i++) {
            Edge edge = outEdges.edgeAt( i );
            DependencySet d = edge.getDepends();

            if( d.getBranch() <= branch ) {
                Node succ = edge.getTo();
                Role role = edge.getRole();

                if( !succ.inEdges.hasExactEdge( this, role, succ ) ) {
                    	succ.addInEdge( edge );
                    
                    	// update affected
                    	if (PelletOptions.TRACK_BRANCH_EFFECTS) {
                    		abox.getBranchEffectTracker().add( d.getBranch(), succ.name );
                    		abox.getBranchEffectTracker().add( d.getBranch(), name );                    		
                    	}
                    	
                		if( PelletOptions.USE_COMPLETION_QUEUE ){
                    		added = true;

                    		if( succ instanceof Individual ){
	                			Individual succInd = (Individual)succ;
	                			succInd.applyNext[Node.MAX] = 0;
	                			QueueElement qe =new QueueElement( succInd );
	        					abox.getCompletionQueue().add( qe, NodeSelector.MAX_NUMBER );
	        					abox.getCompletionQueue().add( qe, NodeSelector.GUESS );
	        					abox.getCompletionQueue().add( qe, NodeSelector.CHOOSE );
	                		}
                		}
                	}
            	}
        }
        
        
        if( added ) {
			applyNext[Node.MAX] = 0;
			QueueElement qe = new QueueElement( this );
			abox.getCompletionQueue().add( qe, NodeSelector.MAX_NUMBER );
			abox.getCompletionQueue().add( qe, NodeSelector.GUESS );
			abox.getCompletionQueue().add( qe, NodeSelector.CHOOSE );
        }
    }

	public String debugString() {
		return name.getName() +
        " = " + 
		types[ATOM] + 
		types[ALL] +
		types[SOME] +
		types[OR] +
		types[MIN] +
		types[MAX] +
		types[NOM] +
		"; **" + outEdges + "**" +
		 "; **" + inEdges + "**" + 
		 " --> " + depends + 
		 "";
	}

	@Override
	protected void updateNodeReferences() {
		super.updateNodeReferences();		
        
		if( parent != null )
			parent = abox.getIndividual( parent.getName() );
		
        if( isPruned() ) {
        	EdgeList oldEdges = outEdges;
            outEdges = new EdgeList(oldEdges.size());
            for(int i = 0; i < oldEdges.size(); i++) {
                Edge edge = oldEdges.edgeAt(i);
                Node to = abox.getNode(edge.getTo().getName());
                Edge newEdge = new DefaultEdge(edge.getRole(), this, to, edge.getDepends());
                outEdges.addEdge(newEdge);
            }
        }
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
	public boolean isTop() {
		return false;
	}
}
