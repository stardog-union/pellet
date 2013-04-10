// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2006 Christian Halaschek-Wiener
// Halaschek-Wiener parts of this source code are available under the terms of the MIT License.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DefaultEdge;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.NodeMerge;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.tableau.blocking.BlockingFactory;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Timer;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.IncrementalChangeTracker;
import com.clarkparsia.pellet.expressivity.Expressivity;


/**
 * The completion strategy for incremental consistency checking.
 * 
 * @author Christian Halaschek-Wiener
 */
public class SROIQIncStrategy extends SROIQStrategy {
	
	/**
	 * Constructor
	 * 
	 * @param abox
	 */
	public SROIQIncStrategy(ABox abox) {
		super(abox);
    }
 
    /**
     * Return individuals to which we need to apply the initialization rules
     *  
     * @return
     */
    public Iterator<Individual> getInitializeIterator() {	
    	return abox.getIncrementalChangeTracker().updatedIndividuals();
	}	
    
    
    /**
     * Return individuals that were newly added
     *  
     * @return
     */
    public Iterator<Individual> getNewIterator() {
    	return abox.getIncrementalChangeTracker().newIndividuals();
	}	
    
	/**
	 * Return edges that were newly added
	 * 
	 * @return
	 */
	public Iterator<Edge> getNewEdgeIterator() {
		return abox.getIncrementalChangeTracker().newEdges();
	}
	
	
	/**
	 * Return the unpruned indivdiuals
	 * 
	 * @return
	 */
	public Iterator<Node> getUnPrunedIterator() {
		return abox.getIncrementalChangeTracker().unprunedNodes();
	}
	
	
	
	/**
	 * Return edges that were newly added
	 * 
	 * @return
	 */
	public Iterator<Edge> getRemovedEdgeIterator() {
		return abox.getIncrementalChangeTracker().deletedEdges();
	}
	
	
	
	/**
	 * Return types that were deleted
	 * 
	 * @return
	 */
	public Iterator<Map.Entry<Node,Set<ATermAppl>>> getRemovedTypeIterator() {
		return abox.getIncrementalChangeTracker().deletedTypes();
	}
	

    
	/**
	 * There are additional rule that must be fired in the event of incremental
	 * additions and deletions in order to guarantee completeness. These are
	 * done here.
	 */
	public void initialize(Expressivity expr) {
		
		Timer t = abox.getKB().timers.startTimer("initialize");

		if(log.isLoggable( Level.FINE ))
			log.fine("Initialize Started");
		
		mergeList = new ArrayList<NodeMerge>();
		
        blocking = BlockingFactory.createBlocking( expr );

        configureTableauRules( expr );
        
		for (Branch branch : abox.getBranches()) {
			branch.setStrategy(this);
		}

		// if this is an incremental addition we may need to merge nodes and
		// handle newly added individuals

		// merge nodes - note branch must be temporarily set to 0 to
		// ensure that assertion
		// will not be restored during backtracking
		// int branch = abox.getBranch();
		abox.setBranch(0);
				
		mergeList.addAll(abox.getToBeMerged());
		if (!mergeList.isEmpty())
			mergeAll();

		 //Apply necessary initialization to any new individual added
		 //Currently, this is a replication of the code
		 for( Iterator<Individual> newIt = getNewIterator(); newIt.hasNext(); ) {
			Individual n = newIt.next();

			if (n.isMerged())
				continue;

			applyUniversalRestrictions(n);
			
			unfoldingRule.apply( n );
			
			selfRule.apply(n);				
		}
		 
		//handle nodes affected by the update
		for (Iterator<Individual> it = getInitializeIterator(); it.hasNext();) {
		
			Individual n = it.next();

			nominalRule.apply(n);
			
			if (n.isMerged()){
				n = n.getSame();
			}

			allValuesRule.apply( n );
		}
		
		//process new edges
		for(Iterator<Edge> it = getNewEdgeIterator(); it.hasNext();){
			Edge edge = it.next();
			
			Individual subj = edge.getFrom();
			Node obj = edge.getTo();
			if(subj.isMerged())
				subj.getSame();
			if(subj.isPruned())
				continue;

			if(obj.isMerged())
				obj.getSame();
			if(obj.isPruned())
				continue;

			Role pred = edge.getRole();				
			DependencySet ds = edge.getDepends();

			applyDomainRange(subj, pred, obj, ds);
			if (subj.isPruned() || obj.isPruned()) {
				return;
			}
			applyFunctionality(subj, pred, obj);
			if (subj.isPruned() || obj.isPruned()) {
				return;
			}

			if (pred.isObjectRole()) {
				Individual o = (Individual) obj;
				checkReflexivitySymmetry(subj, pred, o, ds);
				checkReflexivitySymmetry(o, pred.getInverse(), subj, ds);
			}
			
			//if the KB has cardinality restrictions, then we need to apply the guessing rule
	    		if(abox.getKB().getExpressivity().hasCardinality()){
	    			//update the queue so the max rule will be fired
	        		updateQueueAddEdge(subj, pred, obj);
	    		}
		}
		
		 
		//merge again if necessary
		if (!mergeList.isEmpty())
			mergeAll();
		
		//set appropriate branch
		abox.setBranch(abox.getBranches().size() + 1);

		// we will also need to add stuff to the queue in the event of a
		// deletion		
			
		//Handle removed edges
		Iterator<Edge> i = getRemovedEdgeIterator();
		while( i.hasNext() ){				
			Edge e = i.next();
			Individual subj = e.getFrom();
			Node obj = e.getTo();
			
			subj = subj.getSame();
			
			subj.applyNext[Node.SOME] = 0;
			subj.applyNext[Node.MIN] = 0;
			QueueElement qe = new QueueElement( subj );
			abox.getCompletionQueue().add( qe, NodeSelector.EXISTENTIAL );
			abox.getCompletionQueue().add( qe, NodeSelector.MIN_NUMBER );
			
			
			obj = obj.getSame();
			if(obj instanceof Individual){
				Individual objInd = (Individual)obj;
				objInd.applyNext[Node.SOME] = 0;
				objInd.applyNext[Node.MIN] = 0;
				qe = new QueueElement( objInd );
				abox.getCompletionQueue().add( qe, NodeSelector.EXISTENTIAL );
				abox.getCompletionQueue().add( qe, NodeSelector.MIN_NUMBER );
			}								
		}

		
		
		
		//Handle removed types
		Iterator<Map.Entry<Node,Set<ATermAppl>>> it = getRemovedTypeIterator();
		while( it.hasNext() ){
			Node node = it.next().getKey();
			
			if( node.isIndividual() ){
				Individual ind = (Individual)node;

				//readd the conjunctions
				readdConjunctions( ind );
				
				//it could be the case that the type can be added from unfolding, a forAll application on a self loop, or the disjunction rule
				ind.applyNext[Node.ATOM] = 0;
				ind.applyNext[Node.ALL] = 0;
				ind.applyNext[Node.OR] = 0;

				QueueElement qe = new QueueElement( ind );
				abox.getCompletionQueue().add( qe, NodeSelector.ATOM );
				abox.getCompletionQueue().add( qe, NodeSelector.DISJUNCTION );

				//fire the all rule as the is no explicit call to it
				allValuesRule.apply( ind );
				
				//get out edges and check domains, some values and min values
				for( int j = 0; j < ind.getOutEdges().size(); j++ ){
					Edge e = ind.getOutEdges().edgeAt( j );
												
					if( e.getFrom().isPruned() || e.getTo().isPruned() )
						continue;
												
			        Role pred = e.getRole();
			        Node obj = e.getTo();
			        DependencySet ds = e.getDepends();
					
			        for( ATermAppl domain : pred.getDomains() ) {			        
				        if( requiredAddType( ind, domain ) ) {
							if( !PelletOptions.USE_TRACING )
								addType( ind, domain, ds.union( DependencySet.EMPTY, abox.doExplanation() ) );
							else         
								addType( ind, domain, ds.union( pred.getExplainDomain( domain ), abox.doExplanation() ) );
				        }
			        }

			        //it could be the case that this label prevented the firing of the all values, some, or min rules of the neighbor
					if( obj instanceof Individual ){
						Individual objInd = (Individual)obj;
						objInd.applyNext[Node.ALL] = 0;
						objInd.applyNext[Node.SOME] = 0;
						objInd.applyNext[Node.MIN] = 0;
						QueueElement qeObj = new QueueElement( objInd );
						abox.getCompletionQueue().add( qeObj, NodeSelector.EXISTENTIAL );
						abox.getCompletionQueue().add( qeObj, NodeSelector.MIN_NUMBER );
						
						//apply the all values rule
						allValuesRule.apply( ind );
					}
				}
			}
				
			
			//get out edges
			for( int j = 0; j < node.getInEdges().size(); j++ ){
				Edge e = node.getInEdges().edgeAt( j );
											
				if( e.getFrom().isPruned() || e.getTo().isPruned() )
					continue;
				
				Individual subj = e.getFrom();
		        	Role pred = e.getRole();				       
		        	DependencySet ds = e.getDepends();
				
		        for( ATermAppl range : pred.getRanges() ) {
			        if( requiredAddType( node, range ) ) {
						if( !PelletOptions.USE_TRACING )
							addType( node, range, ds.union( DependencySet.EMPTY, abox.doExplanation() ) );
						else         
							addType( node, range, ds.union( pred.getExplainRange( range ), abox.doExplanation() ) );
			        }
		        }

		        //it could be the case that this label prevented the firing of the all values, some, or min rules of the neighbor
				subj.applyNext[Node.ALL] = 0;
				subj.applyNext[Node.SOME] = 0;
				subj.applyNext[Node.MIN] = 0;
				QueueElement qe = new QueueElement( subj );
				abox.getCompletionQueue().add( qe, NodeSelector.EXISTENTIAL );
				abox.getCompletionQueue().add( qe, NodeSelector.MIN_NUMBER );

				allValuesRule.apply( subj );
			}
		}
		
		
		//due to unmerging nodes, edges can actually be added
		i = getNewEdgeIterator();
		while( i.hasNext() ){				
			applyPropertyRestrictions( i.next() );				
		}
		
		
		//due to unmerging any node that was pruned could need rules applied to it. This is because these rules 
		//would have been originally applied to the node that the pruned node was merged into.  
		for(Iterator<Node> nodeIt = getUnPrunedIterator(); nodeIt.hasNext(); ) {
			Node n = nodeIt.next();
			
			if( n.isIndividual() ) {
				Individual ind = (Individual) n;
				
				//reset type pointers
				for( int j = 0; j < Node.TYPES; j++ )
					ind.applyNext[j] = 0;
				
				//add to all queues
				abox.getCompletionQueue().add( new QueueElement( ind ) );
				
				allValuesRule.apply( ind );
				
				//get out edges
				for( int j = 0; j < ind.getOutEdges().size(); j++ ){
					Edge e = ind.getOutEdges().edgeAt( j );
												
					if( !e.getFrom().isPruned() && !e.getTo().isPruned() )
						applyPropertyRestrictions( e );
					
					Node obj = e.getTo();
					if( obj instanceof Individual ){
						Individual objInd = (Individual)obj;
						objInd.applyNext[Node.ALL] = 0;
						allValuesRule.apply( objInd );
					}
				}
					
				
				//get out edges
				for( int j = 0; j < ind.getInEdges().size(); j++ ){
					Edge e = ind.getInEdges().edgeAt( j );
												
					if( !e.getFrom().isPruned() && !e.getTo().isPruned() )
						applyPropertyRestrictions( e );
					
					Individual subj= e.getFrom();
					subj.applyNext[Node.ALL] = 0;
					allValuesRule.apply( subj );
				}
			}
		}
		
        abox.setChanged( true );
        abox.setComplete( false );
        abox.setInitialized( true );

        t.stop();
        
		if(log.isLoggable( Level.FINE ))
			log.fine("Initialize Ended");
	}
		 
    /**
	 * Readd conjunction labels
	 * 
	 * @param ind
	 */
	protected void readdConjunctions( Individual ind ){
		for( ATermAppl conj : ind.getTypes() ){
			if( ATermUtils.isAnd( conj ) && ind.hasType( conj ) )
				addType(ind, conj, ind.getDepends( conj ) );
		}
	}
	
	/**
	 * Test if a type should be readded to a node
	 * 
	 * @param node
	 * @param type
	 * @return
	 */
	protected boolean requiredAddType( Node node, ATermAppl type ){
		if( type == null || node.hasType( type ) && !ATermUtils.isAnd( type ) )
			return false;
		 
		return true;		
	}
	
	@Override
	protected void restoreAllValues() {
		final IncrementalChangeTracker tracker = abox.getIncrementalChangeTracker();

		for( Iterator<Map.Entry<Node, Set<ATermAppl>>> it = tracker.deletedTypes(); it
				.hasNext(); ) {

			Map.Entry<Node, Set<ATermAppl>> entry = it.next();
			Node node = entry.getKey();
			Set<ATermAppl> types = entry.getValue();
			
			// find the edges which the all values rule needs to be
			// re-applied to
			EdgeList av = findAllValues( node, types );
			
			//apply the all values rules
			for(int i = 0; i < av.size(); i++){
				Edge e = av.edgeAt( i );						
				allValuesRule.applyAllValues( e.getFrom(), e.getRole(), e.getTo(), e.getDepends() );
			}
		}
		
		for( Iterator<Node> i = tracker.unprunedNodes(); i.hasNext(); ) {
			Node node = i.next();
			
			if( node instanceof Individual ){
				Individual ind = (Individual)node;
				//reset type pointers
				for( int j = 0; j < Node.TYPES; j++ )
					ind.applyNext[j] = 0;
				
				//add to all queues
				abox.getCompletionQueue().add( new QueueElement( ind ) );
				
				allValuesRule.apply( ind );
				
				//get out edges
				for( int j = 0; j < ind.getOutEdges().size(); j++ ){
					Edge e = ind.getOutEdges().edgeAt( j );
					
					Node obj = e.getTo();
					if( obj instanceof Individual ){
						Individual objInd = (Individual)obj;
						objInd.applyNext[Node.ALL] = 0;
						allValuesRule.apply( objInd );
					}
				}
			}
						
			//get out edges
			for( int j = 0; j < node.getInEdges().size(); j++ ){
				Edge e = node.getInEdges().edgeAt( j );
				Individual subj= e.getFrom();
				subj.applyNext[Node.ALL] = 0;
				allValuesRule.apply( subj );
			}
		}    
	}
	
	/**
	 * Find applicable all values for a removed type during a restore
	 * 
	 * @param node
	 * @param removedTypes
	 * @return
	 */
	protected EdgeList findAllValues( Node node, Set<ATermAppl> removedTypes){
		EdgeList edges = new EdgeList();
		
		//handle in edges
		for(int i = 0; i < node.getInEdges().size(); i++){
			Edge e = node.getInEdges().edgeAt( i );
			
			edges.addEdgeList( findAllValues( node, e.getFrom(), removedTypes, e ) );
		}
		
		
		if( node instanceof Individual ){
			Individual ind = (Individual)node;			
			
			//handle in edges
			for(int i = 0; i < ind.getOutEdges().size(); i++){
				Edge e = ind.getOutEdges().edgeAt( i );
				Node to = e.getTo();
				
				Role inv = e.getRole().getInverse();
				
				if(inv != null && to instanceof Individual)
					edges.addEdgeList( findAllValues( ind, (Individual)to, removedTypes, new DefaultEdge( inv, (Individual)to, ind, e.getDepends() ) ) ) ;
			}
		}
		
		
		return edges;
	}

	
	/**
	 * Method to find the edges which an all values could be applied to
	 *  
	 * @param node
	 * @param neighbor
	 * @param removedTypes
	 * @param edge
	 * @return
	 */
	protected EdgeList findAllValues( Node node, Individual neighbor, Set<ATermAppl> removedTypes, Edge edge){
		EdgeList edges = new EdgeList();
		
		boolean applicable = false;		
		List<ATermAppl> avTypes = neighbor.getTypes( Node.ALL );
		List<ATermAppl> applicableRoles = new ArrayList<ATermAppl>();
		
		//inspect all values for a recently deleted type
		for( int i = 0; i < avTypes.size(); i++ ){
			ATermAppl avType = avTypes.get( i );
			ATermAppl role = (ATermAppl) avType.getArgument( 0 );
			ATermAppl type = (ATermAppl) avType.getArgument( 1 );
			
			//if we cannot use this edge then continue
			if( edge != null && !edge.getRole().isSubRoleOf( abox.getRole( role ) ) )
				continue;
			
			if( containsType( type, removedTypes ) ){
				applicable = true;				
				applicableRoles.add( type );
			}
		}
		
		//only proceed if necessary
		if( !applicable )
			return edges;
		
		//two cases depending on input
		if(edge == null){
			//get all edges to this node
			for( int i = 0; i < applicableRoles.size(); i++ ){
				ATerm p = applicableRoles.get( i );
				Role role = abox.getRole( p );

				edges.addEdgeList( neighbor.getRNeighborEdges( role, node ) );
			}			
		}else{
			edges.addEdge( edge );
		}
		
		return edges;
	}
    
	
	
	/**
	 * Check if a node contains a particular type that has been removed
	 * @param type
	 * @param removedTypes
	 * @return
	 */
	private boolean containsType( ATermAppl type, Set<ATermAppl> removedTypes ){		
		boolean contains = false;
		
		
		if(ATermUtils.isAnd( type )){
			for(ATermList cs = (ATermList) type.getArgument(0); !cs.isEmpty(); cs = cs.getNext()) {
				ATermAppl conj = (ATermAppl) cs.getFirst();
				
				if( containsType( conj, removedTypes ) ){
					contains = true;
					break;
				}
			}		
		}else if( removedTypes.contains( type )){
			contains = true;
		}

		return contains;
	}

}
