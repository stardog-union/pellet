// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.reasoner.InfGraph;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A graph listener that listens to graph change events and if possible
 * processes the change event. The listener is passed a possibly union
 * graph but the listener is attached only to leaf (non-union) graphs.
 * The listener keeps track which graph is changed and also checks
 * if subgraphs are added or removed from the root graph. 
 * 
 * @author Evren Sirin
 */
public class PelletGraphListener implements GraphListener {
	// KB object - used for incremental ABox changes
	private KnowledgeBase	kb;
	
	private Graph 			rootGraph;

	private Set<Graph>		leafGraphs;
	
	private Set<Graph>		changedGraphs;
	
	private boolean			statementDeleted;

	public PelletGraphListener(Graph rootGraph, KnowledgeBase kb) {
		this.rootGraph = rootGraph;
		this.kb = kb;
		
		leafGraphs = CollectionUtils.makeSet();
		changedGraphs = CollectionUtils.makeSet();
		
		statementDeleted = false;
		
		collectLeafGraphs( rootGraph, Collections.<Graph>emptySet() );
	}
	
	private void addABoxTriple(Triple t) {
		// Convert the Jena nodes to ATermAppl
		ATermAppl s = JenaUtils.makeATerm( t.getSubject() );
		ATermAppl o = JenaUtils.makeATerm( t.getObject() );

		// check if this is a type assertion
		if( t.getPredicate().equals( RDF.type.asNode() ) ) {
			// check if this is a new individual
			if( !kb.getIndividuals().contains( s ) )
				kb.addIndividual( s );

			// add the type
			kb.addType( s, o );
		}
		else {
			// check if the subject is a new individual
			if( !kb.getIndividuals().contains( s ) )
				kb.addIndividual( s );

			// check if the object is a new individual
			if( !t.getObject().isLiteral() && !kb.getIndividuals().contains( o ) )
				kb.addIndividual( o );

			ATermAppl p = JenaUtils.makeATerm( t.getPredicate() );
			// add the property value
			kb.addPropertyValue( p, s, o );
		}
	}

	private void deleteABoxTriple(Triple t) {
		ATermAppl s = JenaUtils.makeATerm( t.getSubject() );
		ATermAppl o = JenaUtils.makeATerm( t.getObject() );

		// check if this is a type assertion
		if( t.getPredicate().equals( RDF.type.asNode() ) ) {
			if( kb.isIndividual( s ) )
				kb.removeType( s, o );
		}
		else {
			// check if the subject is a new individual
			if( kb.isIndividual( s ) && kb.isIndividual( o ) ) {
				ATermAppl p = JenaUtils.makeATerm( t.getPredicate() );
				// add the property value
				kb.removePropertyValue( p, s, o );
			}
		}
	}
	
	private void collectLeafGraphs(Graph graph, Set<Graph> prevLeaves) {
		if( graph instanceof MultiUnion ) {
			MultiUnion union = ((MultiUnion) graph);
			if( union.getBaseGraph() != null )
				collectLeafGraphs( union.getBaseGraph(), prevLeaves );

			for( Iterator<Graph> i = union.getSubGraphs().iterator(); i.hasNext(); )
				collectLeafGraphs( i.next(), prevLeaves );
		}
		else if( graph instanceof InfGraph ) {
			collectLeafGraphs( ((InfGraph) graph).getRawGraph(), prevLeaves );
		}
		else if( leafGraphs.add( graph ) && !prevLeaves.contains( graph ) ) {
			changedGraphs.add( graph );
			
			graph.getEventManager().register( this );
		}
	}
	
	/**
	 * Checks if the graph can be u[dated incrementally
	 * 
	 * @return
	 */
	private boolean canUpdateIncrementally(Graph g) {
		return PelletOptions.PROCESS_JENA_UPDATES_INCREMENTALLY && !statementDeleted && !changedGraphs.contains( g );
	}

	public void dispose() {
		for( Graph graph : leafGraphs ) {
			graph.getEventManager().unregister( this );
		}
		
		leafGraphs.clear();
		changedGraphs.clear();

		statementDeleted = false;
	}
	
	public boolean isChanged() {
		if( statementDeleted || !changedGraphs.isEmpty() ) {
			return true;
		}
		
		getChangedGraphs();
		
		return statementDeleted || !changedGraphs.isEmpty();
	}

	public Set<Graph> getChangedGraphs() {
		Set<Graph> prevLeaves = leafGraphs;
		
		leafGraphs = CollectionUtils.makeSet();
		
		collectLeafGraphs( rootGraph, prevLeaves );
		
		for( Graph prevLeaf : prevLeaves ) {
			if( !leafGraphs.contains( prevLeaf ) ) {
				statementDeleted = true;
				
				prevLeaf.getEventManager().unregister( this );
			}
		}
		
		if( statementDeleted ) {
			return null;
		}
		
		return changedGraphs;
	}

	/**
	 * @return
	 */
	public Set<Graph> getLeafGraphs() {
		return leafGraphs;
	}

	/**
	 * Checks if the given triple is an ABox assertion. Currently, only type
	 * assertions with atomic concepts are detected and property assertions
	 * 
	 * @param t
	 * @return
	 */
	private boolean isABoxChange(Triple t) {
		Node o = t.getObject();
		Node p = t.getPredicate();

		// detect if this is a supported ABox type assertion
		if( p.equals( RDF.type.asNode() ) ) {
			// check if the object is a bnode to detect complex concepts
			if( o.isBlank() ) {
				return false;
			}

			// check that the object is an atomic concept that exists in the KB
			ATermAppl object = JenaUtils.makeATerm( o );
			if( !kb.isClass( object ) ) {
				return false;
			}

			// Note: we do not check if the subject already exists,
			// as it could be a newly added individual

		}
		else {
			// detect ABox property assertions
			ATermAppl prop = JenaUtils.makeATerm( p );

			// check if the role is this is a defined role
			if( !kb.isProperty( prop ) ) {
				return false;
			}

			// Note: we do not check if the subject and object already exists,
			// as they
			// could be a newly added individuals
		}

		return true;
	}

	public void notifyAddArray(Graph g, Triple[] triples) {
		notifyAddIterator( g, IteratorUtils.iterator( triples ) );
	}

	public void notifyAddGraph(Graph g, Graph added) {
		notifyAddIterator( g, added.find( Triple.ANY ) );
	}

	public void notifyAddIterator(Graph g, Iterator<Triple> it) {
		boolean canUpdateIncrementally = canUpdateIncrementally( g );

		if( canUpdateIncrementally ) {
			while( it.hasNext() ) {
				Triple t = it.next();
				if( !isABoxChange( t ) ) {
					canUpdateIncrementally = false;
					break;
				}
				addABoxTriple( t );
			}
		}
		
		if( !canUpdateIncrementally ) {
			changedGraphs.add( g );
		}
	}

	public void notifyAddList(Graph g, List<Triple> triples) {
		notifyAddIterator( g, triples.iterator() );
	}

	public void notifyAddTriple(Graph g, Triple t) {
		if( canUpdateIncrementally( g ) && isABoxChange( t ) ) {
			addABoxTriple( t );
		}
		else {
			changedGraphs.add( g );
		}
	}

	public void notifyDeleteArray(Graph g, Triple[] triples) {
		notifyDeleteIterator( g, IteratorUtils.iterator( triples ) );
	}

	public void notifyDeleteGraph(Graph g, Graph removed) {
		notifyDeleteIterator( g, removed.find( Triple.ANY ) );
	}

	public void notifyDeleteIterator(Graph g, Iterator<Triple> it) {
		boolean canUpdateIncrementally = canUpdateIncrementally( g );

		if( canUpdateIncrementally ) {
			while( it.hasNext() ) {
				Triple t = it.next();
				if( !isABoxChange( t ) ) {
					canUpdateIncrementally = false;
					break;
				}
				deleteABoxTriple( t );
			}
		}
		
		if( !canUpdateIncrementally ) {
			statementDeleted = true;
			changedGraphs.add( g );
		}
	}

	public void notifyDeleteList(Graph g, List<Triple> list) {
		notifyDeleteIterator( g, list.iterator() );
	}

	public void notifyDeleteTriple(Graph g, Triple t) {
		if( canUpdateIncrementally( g ) && isABoxChange( t ) ) {
			deleteABoxTriple( t );
		}
		else {
			statementDeleted = true;
			changedGraphs.add( g );
		}
	}

	public void notifyEvent(Graph source, Object value) {
		statementDeleted = true;
	}

	public void reset() {
		changedGraphs.clear();
		//leafGraphs.clear();
		statementDeleted = false;
	}
}
