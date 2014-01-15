// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import static org.mindswap.pellet.utils.iterator.IteratorUtils.flatten;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

import aterm.ATermAppl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

public abstract class TripleQueryHandler {
	public TripleQueryHandler() {
	}
	
	public abstract boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj);
	
	public abstract ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj);
	
	protected ExtendedIterator<Triple> objectFiller(Node s, Node p, Collection<ATermAppl> objects) {
		return objectFiller( s, p, objects.iterator() );
	}

	protected ExtendedIterator<Triple> objectFiller(final Node s, final Node p, final Iterator<ATermAppl> objects) {
		Map1<ATermAppl, Triple> map = new Map1<ATermAppl, Triple>() {
		    public Triple map1( ATermAppl o ) {
		         return Triple.create( s, p, JenaUtils.makeGraphNode( o ) );
		    }
		};
		
		return WrappedIterator.create( objects ).mapWith( map );	
	}
	
	protected ExtendedIterator<Triple> objectSetFiller(Node s, Node p, Set<Set<ATermAppl>> objectSets) {
		return objectFiller( s, p, flatten( objectSets.iterator() ) );
	}
	
	protected ExtendedIterator<Triple> propertyFiller(Node s, Collection<ATermAppl> properties, Node o) {
		return propertyFiller( s, properties.iterator(), o );
	}
	
	protected ExtendedIterator<Triple> propertyFiller(final Node s, final Iterator<ATermAppl> properties, final Node o) {
		Map1<ATermAppl, Triple> map = new Map1<ATermAppl, Triple>() {
		    public Triple map1( ATermAppl p ) {
		         return Triple.create( s, JenaUtils.makeGraphNode( p ), o );
		    }
		};
		
		return WrappedIterator.create( properties ).mapWith( map );	
	}
	
	protected ExtendedIterator<Triple> subjectFiller(Collection<ATermAppl> subjects, Node p, Node o) {
		return subjectFiller( subjects.iterator(), p, o );
	}
	
	protected ExtendedIterator<Triple> subjectFiller(final Iterator<ATermAppl> subjects, final Node p, final Node o) {
		Map1<ATermAppl, Triple> map = new Map1<ATermAppl, Triple>() {
		    public Triple map1( ATermAppl s ) {
		         return Triple.create( JenaUtils.makeGraphNode( s ), p, o );
		    }
		};
		
		return WrappedIterator.create( subjects ).mapWith( map );	
	}

	protected ExtendedIterator<Triple> subjectSetFiller(Set<Set<ATermAppl>> subjectSets, Node p, Node o) {
		return subjectFiller( flatten( subjectSets.iterator() ), p, o );
	}
}
