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

package org.mindswap.pellet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.SetUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * @author Evren Sirin
 *
 */
public abstract class Node {
	public final static Logger log = Logger.getLogger( Node.class.getName() );
	
	public final static int BLOCKABLE = Integer.MAX_VALUE;
	public final static int NOMINAL   = 0;
	
	public final static int CHANGED   = 0x7F;
	public final static int UNCHANGED = 0x00;
	public final static int ATOM = 0;
	public final static int OR   = 1;
	public final static int SOME = 2;
	public final static int ALL  = 3;
	public final static int MIN  = 4;
	public final static int MAX  = 5;
	public final static int NOM  = 6;
	public final static int TYPES = 7;
	
	protected ABox abox;
	protected ATermAppl name;
	protected Map<ATermAppl, DependencySet> depends;
	private boolean isRoot;
	private boolean isConceptRoot;		
	
	/**
	 * If this node is merged to another one, points to that node otherwise
	 * points to itself. This is a linked list implementation of disjoint-union
	 * data structure.
	 */
	protected Node mergedTo = this;
    
    protected EdgeList inEdges;
	
	/**
	 * Dependency information about why merged happened (if at all)
	 */
	protected DependencySet mergeDepends = null;
	
	protected DependencySet pruned = null;
	
	/**
	 * Set of other nodes that have been merged to this node. Note that this 
	 * is only the set of nodes directly merged to this one. A recursive traversal
	 * is required to get all the merged nodes.
	 */
	protected Set<Node> merged;
	
	protected Map<Node, DependencySet> differents;
	
	protected Node(ATermAppl name, ABox abox) {
		this.name = name;
		this.abox = abox;		

		isRoot = !ATermUtils.isAnon( name );
		isConceptRoot = false;
		
		mergeDepends = DependencySet.INDEPENDENT; 
		differents = CollectionUtils.makeMap();
		depends = CollectionUtils.makeMap();

        inEdges = new EdgeList();
	}

	protected Node(Node node, ABox abox) {
		this.name = node.getName();
		this.abox = abox;

		isRoot = node.isRoot;
		isConceptRoot = node.isConceptRoot;
		
		mergeDepends = node.mergeDepends;
		mergedTo = node.mergedTo;
		merged = node.merged;
		pruned = node.pruned;

		// do not copy differents right now because we need to
		// update node references later anyway
		differents = node.differents;
		depends = CollectionUtils.makeMap(node.depends);
		        
        inEdges = node.inEdges;
	}
	
	protected void updateNodeReferences() {
        mergedTo = abox.getNode( mergedTo.getName() );

        Map<Node, DependencySet> diffs = new HashMap<Node, DependencySet>( differents.size() );
        for(Map.Entry<Node, DependencySet> entry : differents.entrySet() ) {
            Node node = entry.getKey();

            diffs.put( abox.getNode( node.getName() ), entry.getValue() );
        }
        differents = diffs;

        if( merged != null ) {
            Set<Node> sames = new HashSet<Node>( merged.size() );
            for( Node node : merged ) {
                sames.add( abox.getNode( node.getName() ) );
            }
            merged = sames;
        }        
        
        EdgeList oldEdges = inEdges;
        inEdges = new EdgeList(oldEdges.size());
        for(int i = 0; i < oldEdges.size(); i++) {
            Edge edge = oldEdges.edgeAt(i);
            
            Individual from = abox.getIndividual( edge.getFrom().getName() );
			Edge newEdge = new DefaultEdge( edge.getRole(), from, this, edge.getDepends() );
			
			inEdges.addEdge( newEdge );	      
			if( !isPruned() ) 
				from.getOutEdges().addEdge( newEdge );
        }
    }

	/**
	 * Indicates that node has been changed in a way that requires us to recheck
	 * the concepts of given type.
	 *  
	 * @param type type of concepts that need to be rechecked
	 */
	public void setChanged(int type) {		
		//Check if we need to updated the completion queue 
		//Currently we only updated the changed lists for checkDatatypeCount()
		QueueElement newElement = new QueueElement(this);

		//update the datatype queue
		if( (type == Node.ALL || type == Node.MIN) && PelletOptions.USE_COMPLETION_QUEUE )
			abox.getCompletionQueue().add( newElement, NodeSelector.DATATYPE );		

		// add node to effected list
		if( abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS )
			abox.getBranchEffectTracker().add( abox.getBranch(), this.getName() );
	}	

	
	/**
	 * Returns true if this is the node created for the concept satisfiability check.
	 *  
	 * @return
	 */
	public boolean isConceptRoot() {
	    return isConceptRoot;
	}
	
	public void setConceptRoot( boolean isConceptRoot ) {
	    this.isConceptRoot = isConceptRoot;
	}
	
	public boolean isBnode() {
		return ATermUtils.isBnode( name );
	}

	public boolean isNamedIndividual() {
		return isRoot && !isConceptRoot && !isBnode();
	}
	
	public boolean isRoot() {
		return isRoot || isNominal();
	}	
	
	public abstract boolean isLeaf();
		
	public boolean isRootNominal() {
		return isRoot && isNominal();
	}
	
	public abstract Node copyTo(ABox abox);
	
	protected void addInEdge(Edge edge) {
        inEdges.addEdge( edge );   
    }

    public EdgeList getInEdges() {
	    return inEdges;
    }	
    
    public boolean removeInEdge(Edge edge) {
        boolean removed = inEdges.removeEdge(edge);
        
        if( !removed ){
     		throw new InternalReasonerException("Trying to remove a non-existing edge " + edge);           
        }
        
        return true;
    }
    
    public void removeInEdges() {
        inEdges = new EdgeList();
    }

    public void reset(boolean onlyApplyTypes) {
    	assert onlyApplyTypes || isRootNominal() : "Only asserted individuals can be reset: " + this;
    	
		if( PelletOptions.USE_COMPLETION_QUEUE )
			abox.getCompletionQueue().add( new QueueElement( this ) );
		
		if( onlyApplyTypes )
			return;
		
		if( pruned != null )
			unprune( DependencySet.NO_BRANCH );
		
    	mergedTo = this;
    	mergeDepends = DependencySet.INDEPENDENT;
    	merged = null;
    	
    	Iterator<DependencySet> i = differents.values().iterator();
    	while( i.hasNext()) {
    		DependencySet d = i.next();
			if( d.getBranch() != DependencySet.NO_BRANCH ) {
				i.remove();
			}			    
		}
    	
    	resetTypes();
    	
    	inEdges.reset();
    }
    
    protected void resetTypes() {
    	Iterator<DependencySet> i = depends.values().iterator();
    	while( i.hasNext()) {
    		DependencySet d = i.next();
			if( d.getBranch() != DependencySet.NO_BRANCH ) {
				i.remove();
			}			    
		}
    }
    
	public Boolean restorePruned(int branch) {		

		if( PelletOptions.TRACK_BRANCH_EFFECTS )
			abox.getBranchEffectTracker().add( abox.getBranch(), name );

		if( pruned != null ) {
			if( pruned.getBranch() > branch ) {			
				if( log.isLoggable( Level.FINE ) ) 
				    log.fine("RESTORE: " + this + " merged node " + mergedTo + " " + mergeDepends);
				
				if( mergeDepends.getBranch() > branch )
				    undoSetSame();
				
				unprune( branch );

				if( PelletOptions.USE_INCREMENTAL_CONSISTENCY )
					abox.getIncrementalChangeTracker().addUnprunedNode( this );

				// we may need to remerge this node
				if( this instanceof Individual ) {
					final Individual ind = (Individual) this;

					if( PelletOptions.USE_COMPLETION_QUEUE ) {
						ind.applyNext[Node.NOM] = 0;
						abox.getCompletionQueue().add( new QueueElement( this ),
								NodeSelector.NOMINAL );
					}

				}

				return Boolean.TRUE;
			}
			else {
				if( log.isLoggable( Level.FINE ) ) 
					log.fine("DO NOT RESTORE: pruned node " + this + " = " + mergedTo + " " + mergeDepends);	

				return Boolean.FALSE;
			}
	    }
	    
	    return null;
	}
	
	
	public boolean restore(int branch) {		

		if( PelletOptions.TRACK_BRANCH_EFFECTS )
			abox.getBranchEffectTracker().add( abox.getBranch(), name );

		boolean restored = false;
		
		List<ATermAppl> conjunctions = new ArrayList<ATermAppl>();

		
		boolean removed = false;
		
		for( Iterator<ATermAppl> i = getTypes().iterator(); i.hasNext(); ) {									
			ATermAppl c = i.next();	
			DependencySet d = getDepends(c);
			
			boolean removeType = PelletOptions.USE_SMART_RESTORE
//                ? ( !d.contains( branch ) )
                ? ( d.max() >= branch )
				: ( d.getBranch() > branch );  

			if( removeType ) {
				removed = true;
				
				if( log.isLoggable( Level.FINE ) ) 
                    log.fine("RESTORE: " + this + " remove type " + c + " " + d + " " + branch);
				
				//track that this node is affected
				if( PelletOptions.USE_INCREMENTAL_CONSISTENCY && this instanceof Individual ) {
					abox.getIncrementalChangeTracker().addDeletedType( this, c );
				}
								
				i.remove();
				removeType(c);
				restored = true;
			}
			else if( PelletOptions.USE_SMART_RESTORE && ATermUtils.isAnd( c ) ) {
			    conjunctions.add( c );
			}			    
		}			
		
		//update the queue with things that could readd this type
		if( removed && PelletOptions.USE_COMPLETION_QUEUE && this instanceof Individual ) {
			Individual ind = (Individual)this;
			ind.applyNext[Node.ATOM] = 0;
			ind.applyNext[Node.OR] = 0;
			
			QueueElement qe = new QueueElement( this );
			abox.getCompletionQueue().add( qe, NodeSelector.DISJUNCTION );
			abox.getCompletionQueue().add( qe, NodeSelector.ATOM );
		}

		
		// with smart restore there is a possibility that we remove a conjunct 
		// but not the conjunction. this is the case if conjunct was added before 
		// the conjunction but depended on an earlier branch. so we need to make
		// sure all conjunctions are actually applied
		if( PelletOptions.USE_SMART_RESTORE ) {
			for( Iterator<ATermAppl> i = conjunctions.iterator(); i.hasNext(); ) {
				ATermAppl c = i.next();
				DependencySet d = getDepends(c);
				for(ATermList cs = (ATermList) c.getArgument(0); !cs.isEmpty(); cs = cs.getNext()) {
					ATermAppl conj = (ATermAppl) cs.getFirst();
					
					addType(conj, d);
				}            
	        }
		}        
        
		for( Iterator<Entry<Node,DependencySet>> i = differents.entrySet().iterator(); i.hasNext(); ) {
			Entry<Node,DependencySet> entry = i.next();
			Node node = entry.getKey();
			DependencySet d = entry.getValue();

			if( d.getBranch() > branch ) {			
				if( log.isLoggable( Level.FINE ) ) 
					log.fine("RESTORE: " + name + " delete difference " + node);
				i.remove();
				restored = true;
			}			
		}
		
		removed = false;
		for( Iterator<Edge> i = inEdges.iterator(); i.hasNext(); ) {
			Edge e = i.next();
			DependencySet d = e.getDepends();
            
			if( d.getBranch() > branch ) {           
				if( log.isLoggable( Level.FINE ) ) 
					log.fine("RESTORE: " + name + " delete reverse edge " + e);
                
				if( PelletOptions.USE_INCREMENTAL_CONSISTENCY )
					abox.getIncrementalChangeTracker().addDeletedEdge( e );

				i.remove();
				restored = true;
				removed = true;
			}           
		}
		
		if( removed && PelletOptions.USE_COMPLETION_QUEUE ) {
			QueueElement qe = new QueueElement( this );
			abox.getCompletionQueue().add( qe, NodeSelector.EXISTENTIAL );
			abox.getCompletionQueue().add( qe, NodeSelector.MIN_NUMBER );
		}

		return restored;
	}
	
	public void addType(ATermAppl c, DependencySet ds) {
	    if( isPruned() )
	        throw new InternalReasonerException( "Adding type to a pruned node " + this + " " + c );
	    else if( isMerged() )
	        return;
	    
	    // add to effected list
	    if( abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS ) {
			abox.getBranchEffectTracker().add( abox.getBranch(), this.getName() );
		}
		
		int b = abox.getBranch();
		
		int max = ds.max();
		if(b == -1 && max != 0)
		    b = max + 1;
		ds = ds.copy( b );
		depends.put(c, ds);
		
		abox.setChanged( true );
	}

	public boolean removeType(ATermAppl c) {
		return depends.remove(c) != null;
	}

	public boolean hasType(ATerm c) {
		return depends.containsKey(c);
	}
	
	public Bool hasObviousType(ATermAppl c) {
		DependencySet ds = getDepends( c );

		if( ds != null ) {
			if( ds.isIndependent() ) {
				return Bool.TRUE;
			}
		}
		else if( (ds = getDepends(ATermUtils.negate(c))) != null ) { 
			if( ds.isIndependent() ) {
				return Bool.FALSE;
			}
		}
		else if( isIndividual() && ATermUtils.isNominal( c ) ) {
			// TODO probably redundant if : Bool.FALSE
			if( !c.getArgument( 0 ).equals( this.getName() ) ) {
				return Bool.FALSE;
			}
			else {
				return Bool.TRUE;
			}
		}

		if( isIndividual() ) {
			ATermAppl r = null;
			ATermAppl d = null;

			if( ATermUtils.isNot( c ) ) {
				final ATermAppl notC = (ATermAppl) c.getArgument( 0 );
				if( ATermUtils.isAllValues( notC ) ) {
					r = (ATermAppl) notC.getArgument( 0 );
					d = ATermUtils.negate( (ATermAppl) notC.getArgument( 1 ) );
				}
			}
			else if( ATermUtils.isSomeValues( c ) ) {
				r = (ATermAppl) c.getArgument( 0 );
				d = (ATermAppl) c.getArgument( 1 );
			}

			if( r != null ) {
				Individual ind = (Individual) this;

				Role role = abox.getRole( r );

				if( !role.isObjectRole() || !role.isSimple() ) {
					return Bool.UNKNOWN;
				}

				EdgeList edges = ind.getRNeighborEdges( role );

				Bool ot = Bool.FALSE;

				for( int e = 0; e < edges.size(); e++ ) {
					Edge edge = edges.edgeAt( e );

					if( !edge.getDepends().isIndependent() ) {
						ot = Bool.UNKNOWN;
						continue;
					}

					Individual y = (Individual) edge.getNeighbor( ind );

					// TODO all this stuff in one method - this is only for
					// handling AND
					// clauses - they are implemented in abox.isKnownType
					ot = ot.or( abox.isKnownType( y, d, SetUtils.<ATermAppl>emptySet() ) );// y.hasObviousType(d));

					if( ot.isTrue() ) {
						return ot;
					}
				}
				return ot;
			}
		}

		return Bool.UNKNOWN;
	}

	public boolean hasObviousType( Collection<ATermAppl> coll ) {
		for(Iterator<ATermAppl> i = coll.iterator(); i.hasNext();) {
            ATermAppl c = i.next();
            
    		DependencySet ds = getDepends( c );
    		
    		if( ds != null && ds.isIndependent() )
    			return true;
        }
		
		return false;
	}			

	boolean hasPredecessor( Individual x ) {
		return x.hasSuccessor( this );
	}
	
	public abstract boolean hasSuccessor( Node x );
	
	public abstract DependencySet getNodeDepends();
	
	public DependencySet getDepends(ATerm c) {
		return depends.get(c);
	}
	
	public Map<ATermAppl,DependencySet> getDepends() {
		return depends;
	}
	
	public Set<ATermAppl> getTypes() {
		return depends.keySet();
	}	

	public void removeTypes() {
		depends.clear();
	}

	public int prunedAt() {	    
		return pruned.getBranch();
	}
	
	public boolean isPruned() {
		return pruned != null;
	}
	
	public DependencySet getPruned() {
		return pruned;
	}
		
	public abstract void prune(DependencySet ds);

	public void unprune( int branch ) {
        pruned = null;

        boolean added = false;
        
        for(int i = 0; i < inEdges.size(); i++) {
            Edge edge = inEdges.edgeAt( i );
            DependencySet d = edge.getDepends();

            if( d.getBranch() <= branch ) {
                Individual pred = edge.getFrom();
                Role role = edge.getRole();

                // if both pred and *this* were merged to other nodes (in that order)
                // there is a chance we might duplicate the edge so first check for
                // the existence of the edge
                if( !pred.getOutEdges().hasExactEdge( pred, role, this ) ) {
                    pred.addOutEdge( edge );

                    // update affected
					if( PelletOptions.TRACK_BRANCH_EFFECTS ) {
						abox.getBranchEffectTracker().add( d.getBranch(), pred.name );
						abox.getBranchEffectTracker().add( d.getBranch(), name );
					}
                    
                    if( PelletOptions.USE_COMPLETION_QUEUE ){
                    		added = true;
                    		pred.applyNext[Node.MAX] = 0;
                    		
                    		QueueElement qe = new QueueElement( pred );
                			abox.getCompletionQueue().add( qe, NodeSelector.MAX_NUMBER );
                			abox.getCompletionQueue().add( qe, NodeSelector.GUESS );
                			abox.getCompletionQueue().add( qe, NodeSelector.CHOOSE );
                			abox.getCompletionQueue().add( qe, NodeSelector.UNIVERSAL );       
                    }
                    
                    if( log.isLoggable( Level.FINE ) ) 
                        log.fine( "RESTORE: " + name + " ADD reverse edge " + edge );
                }
            }
        }
        
        if( added ){
	        if( this instanceof Individual ){
	    			Individual ind = (Individual)this;
	    			ind.applyNext[Node.MAX] = 0;
	    			QueueElement qe = new QueueElement( ind );
	    			abox.getCompletionQueue().add( qe, NodeSelector.MAX_NUMBER );
	    			abox.getCompletionQueue().add( qe, NodeSelector.GUESS );
	    			abox.getCompletionQueue().add( qe, NodeSelector.CHOOSE );
	    			abox.getCompletionQueue().add( qe, NodeSelector.UNIVERSAL );       
	        }
        }
    }

	public abstract int getNominalLevel();
	
	public abstract boolean isNominal();
	
	public abstract boolean isBlockable();
	
	public abstract boolean isLiteral();
	
	public abstract boolean isIndividual();
	
	public int mergedAt() {	    
		return mergeDepends.getBranch();
	}
	
	public boolean isMerged() {
		return mergedTo != this;
	}

	public Node getMergedTo() {
		return mergedTo;
	}
	
//	public DependencySet getMergeDependency() {
//		return mergeDepends;
//	}
	
    /**
     * Get the dependency if this node is merged to another node. This
     * node may be merged to another node which is later merged to another 
     * node and so on. This function may return the dependency for the 
     * first step or the union of all steps.
     *
     */
    public DependencySet getMergeDependency( boolean all ) {
        if( !isMerged() || !all )
            return mergeDepends;

        DependencySet ds = mergeDepends;
        Node node = mergedTo;
        while( node.isMerged() ) {
            ds = ds.union( node.mergeDepends, abox.doExplanation() );
            node = node.mergedTo;            
        }
        
        return ds;
    }
    
	public Node getSame() {
		if(mergedTo == this)
			return this;
		
		return mergedTo.getSame();
	}
	
	public void undoSetSame() {
		mergedTo.removeMerged( this );
		mergeDepends = DependencySet.INDEPENDENT;
		mergedTo = this;	    
	}
	
	private void addMerged( Node node ) {
	    if( merged == null )
	        merged = new HashSet<Node>( 3 );
	    merged.add( node );
	}
		
	public Set<Node> getMerged() {
		if ( merged == null )
			return SetUtils.emptySet();
	    return merged;
	}
	
	public Map<Node,DependencySet> getAllMerged() {
		Map<Node,DependencySet> result = new HashMap<Node,DependencySet>();
		getAllMerged( DependencySet.INDEPENDENT, result );
		return result;
	}
	
	private void getAllMerged(DependencySet ds, Map<Node,DependencySet> result) {
		if ( merged == null )
			return;
		
		for( Node mergedNode : merged ) {
			DependencySet mergeDS = ds.union( mergedNode.getMergeDependency( false ), false );
			result.put( mergedNode, mergeDS );
			mergedNode.getAllMerged( mergeDS, result );
		}		
	}
	
	private void removeMerged( Node node ) {
	    merged.remove( node );
	    if( merged.isEmpty() )
	        merged = null; // free space
	}
	
	public void setSame(Node node, DependencySet ds) {
		if( isSame( node ) ) 
		    return;
        if( isDifferent( node ) ) {
        		//CHW - added for incremental reasoning support - this is needed as we will need to backjump if possible
        		if(PelletOptions.USE_INCREMENTAL_CONSISTENCY)
        			abox.setClash( Clash.nominal( this, ds.union(this.mergeDepends, abox.doExplanation()).union(node.mergeDepends, abox.doExplanation()), node.getName() ));
        		else
        			abox.setClash( Clash.nominal( this, ds, node.getName() ) );
        		
		    return;
		}
		
		mergedTo = node;
		mergeDepends = ds.copy( abox.getBranch() );
		node.addMerged( this );
	}
	
	public boolean isSame(Node node) {
		return getSame().equals( node.getSame() );
	}
		
	public boolean isDifferent( Node node ) {
		return differents.containsKey(node);
	}
		
	public Set<Node> getDifferents() {
		return differents.keySet();
	}

	public DependencySet getDifferenceDependency(Node node) {
		return differents.get(node);
	}	

	public void setDifferent(Node node, DependencySet ds) {

		// add to effected list
		if( abox.getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS )
			abox.getBranchEffectTracker().add( abox.getBranch(), node.getName() );

		if( isDifferent( node ) )
			return;
		if( isSame( node ) ) {
			ds = ds.union( this.getMergeDependency( true ), abox.doExplanation() );
			ds = ds.union( node.getMergeDependency( true ), abox.doExplanation() );
			abox.setClash( Clash.nominal( this, ds, node.getName() ));

			if (!ds.isIndependent()) {
				return;
			}
		}
		
		ds = ds.copy( abox.getBranch() );
		differents.put(node, ds);
		node.setDifferent(this, ds);
		abox.setChanged( true );
	}
	
	public void inheritDifferents( Node y, DependencySet ds ) {
		for( Map.Entry<Node,DependencySet> entry : y.differents.entrySet() ) {
			Node yDiff = entry.getKey();
			DependencySet finalDS = ds.union( entry.getValue(), abox.doExplanation() );
			
			setDifferent( yDiff, finalDS );
		}
	}

	public ATermAppl getName() {
		return name;
	}
	
	public abstract ATermAppl getTerm();
	
	public String getNameStr() {
		return name.getName();
	}
	
	public String toString() {
		return ATermUtils.toString( name );
	}
	
	/**
	 * A string that identifies this node either using its name or the path
	 * of individuals that comes to this node. For example, a node that has
	 * been generated by the completion rules needs to be identified with
	 * respect to a named individual. Ultimately, we need the shortest path
	 * or something like that but right now we just use the first inEdge 
	 * 
	 * @return
	 */
	public List<ATermAppl> getPath() {	
	    LinkedList<ATermAppl> path = new LinkedList<ATermAppl>();

        if(isNamedIndividual()) 
            path.add(name);
	    else {
            Set<Node> cycle = new HashSet<Node>();
		    Node node = this;
		    while(!node.getInEdges().isEmpty()) {
		        Edge inEdge = node.getInEdges().edgeAt(0);
		        node = inEdge.getFrom();
                if( cycle.contains( node ) )
                    break;
                else
                    cycle.add( node );
	            path.addFirst( inEdge.getRole().getName() );
                if( node.isNamedIndividual() ) {
                    path.addFirst( node.getName() );
                    break;
                }
		    }
	    }
	    
		
		return path;
	}
	

	/**
	 * getABox
	 * 
	 * @return
	 */
	public ABox getABox() {
		return abox;
	}
}

