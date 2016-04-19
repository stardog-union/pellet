package org.mindswap.pellet.jena;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Polyadic;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NullIterator;

/**
 * A simple union graph implementation whose find function may contain duplicate
 * triples. The contains function is overridden so as not to call the find method.
 * 
 * @author Evren Sirin
 */
public class SimpleUnion extends Polyadic {
	/**
	 * @param graphs
	 */
	public SimpleUnion(Iterable<Graph> graphs) {
		super( graphs.iterator() );
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected ExtendedIterator<Triple> graphBaseFind(TripleMatch m) {
//		ExtendedIterator<Triple> result = NullIterator.instance();
//		for (final Graph g : m_subGraphs) {
//			result = result.andThen(g.find(m));
//		}
//
//		return result;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean graphBaseContains(Triple t) {
		for (final Graph g : m_subGraphs) {
			if( g.contains( t ) )
				return true;
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		for (final Graph g : m_subGraphs) {
			if( !g.isEmpty() )
				return false;
		}
		
		return true;
	}

	@Override
	protected ExtendedIterator<Triple> graphBaseFind(Triple t) {
		ExtendedIterator<Triple> result = NullIterator.instance();
		
		for (final Graph g : m_subGraphs) {
			result = result.andThen(g.find(t));
		}

		return result;
	}
	
}
