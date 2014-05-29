// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.mindswap.pellet.KnowledgeBase;

import aterm.ATerm;
import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * 
 * @author Evren Sirin
 */
public class JGraphBasedDefinitionOrder extends AbstractDefinitionOrder {
	private Map<ATermAppl,Set<ATermAppl>> equivalents;
	
	private DirectedGraph<ATermAppl,DefaultEdge> graph;	

	public JGraphBasedDefinitionOrder(KnowledgeBase kb, Comparator<ATerm> comparator) {		
		super( kb, comparator );
	}
	
	private Set<ATermAppl> createSet() {
		return comparator != null
	    	? new TreeSet<ATermAppl>( comparator )
	    	: CollectionUtils.<ATermAppl>makeIdentitySet();
	}
	
	private Queue<ATermAppl> createQueue() {
		return comparator != null
	    	? new PriorityQueue<ATermAppl>( 10, comparator )
	    	: new LinkedList<ATermAppl>();
	}
	
	private boolean addEquivalent(ATermAppl key, ATermAppl value) {
	    Set<ATermAppl> values = equivalents.get( key );
	    if( values == null ) {
	        values = createSet();
	        equivalents.put( key, values );
	    }
	    
	    return values.add( value );
	}
	
	private Set<ATermAppl> getAllEquivalents(ATermAppl key) {
	    Set<ATermAppl> values = equivalents.get( key );
	    
	    if( values != null ) {
	    	values.add( key ); 	
	    }
	    else {
	    	values = Collections.singleton( key );
	    }
	    
	    return values;
	}
	
	private Set<ATermAppl> getEquivalents(ATermAppl key) {
	    Set<ATermAppl> values = equivalents.get( key );
	    return values != null 
	    	? values
	    	: Collections.<ATermAppl>emptySet();
	}
	
	protected void initialize() {
		equivalents = CollectionUtils.makeIdentityMap();
		
		graph = new DefaultDirectedGraph<ATermAppl, DefaultEdge>( DefaultEdge.class );
		
		graph.addVertex( TOP );		
		for( ATermAppl c : kb.getClasses() ) {
			graph.addVertex( c );
		}
	}

	@Override
	protected void addUses(ATermAppl c, ATermAppl usedByC) {
		if( c.equals( TOP ) ) {
			addEquivalent( TOP, usedByC );
		}
		else if( !c.equals( usedByC ) ) {
			graph.addEdge( c, usedByC );
		}
	}

	protected Set<ATermAppl> computeCycles() {
		Set<ATermAppl> cyclicConcepts = CollectionUtils.makeIdentitySet();
		
		cyclicConcepts.addAll( getEquivalents( TOP ) );
				
		StrongConnectivityInspector<ATermAppl, DefaultEdge> scInspector = 
			new StrongConnectivityInspector<ATermAppl, DefaultEdge>( graph );
		List<Set<ATermAppl>> sccList = scInspector.stronglyConnectedSets();
		for( Set<ATermAppl> scc : sccList ) {
			if( scc.size() == 1 )
				continue;
			
			cyclicConcepts.addAll( scc );
			
			collapseCycle( scc );
		}
		
		return cyclicConcepts;
	}

	private void collapseCycle(Set<ATermAppl> scc) {
		Iterator<ATermAppl> i = scc.iterator();
		ATermAppl rep = i.next();
		
		while( i.hasNext() ) {				
			ATermAppl node = i.next();
				
			addEquivalent( rep, node );
			
			for( DefaultEdge edge : graph.incomingEdgesOf( node ) ) {
				ATermAppl incoming = graph.getEdgeSource( edge );
				if( !incoming.equals( rep  ) )
					graph.addEdge( incoming, rep );
			}
			
			for( DefaultEdge edge : graph.outgoingEdgesOf( node ) ) {
				ATermAppl outgoing = graph.getEdgeTarget( edge );
				if( !outgoing.equals( rep  ) )
					graph.addEdge( rep, outgoing );
			}
			
			graph.removeVertex( node );
		}
	}
	
	protected List<ATermAppl> computeDefinitionOrder() {		
		List<ATermAppl> definitionOrder = CollectionUtils.makeList();
		
		definitionOrder.add( TOP );
		definitionOrder.addAll( getEquivalents( TOP ) );
		
		graph.removeVertex( TOP );
		
		destructiveTopologocialSort( definitionOrder );
		
		definitionOrder.add( BOTTOM );
		
		return definitionOrder;
	}
	
	public void destructiveTopologocialSort(List<ATermAppl> nodesSorted) {
		Queue<ATermAppl> nodesPending = createQueue();		

		for( ATermAppl node : graph.vertexSet() ) {
			if( graph.outDegreeOf( node ) == 0 )
				nodesPending.add( node );
		}

		while( !nodesPending.isEmpty() ) {
			ATermAppl node = nodesPending.remove();

			assert graph.outDegreeOf( node ) == 0;
			
			nodesSorted.addAll( getAllEquivalents( node ) );				

			for( DefaultEdge edge : graph.incomingEdgesOf( node ) ) {
				ATermAppl source = graph.getEdgeSource( edge );
				if( graph.outDegreeOf( source ) == 1 )
					nodesPending.add( source );
			}
			
			graph.removeVertex( node );
		}

		assert graph.vertexSet().isEmpty() : "Failed to sort elements: " + graph.vertexSet();
	}
}
