package org.mindswap.pellet.jena;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.compose.Polyadic;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ExtendedIterator<Triple> graphBaseFind(TripleMatch m) {
		ExtendedIterator<Triple> result = NullIterator.instance();
		for (final Graph g : m_subGraphs) {
			result = result.andThen(g.find(m));
		}

		return result;
	}

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
	
}
