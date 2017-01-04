// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import java.util.Collection;
import java.util.Iterator;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.utils.iterator.NestedIterator;

import aterm.ATermAppl;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.WrappedIterator;

abstract class SubjectObjectVarHandler extends TripleQueryHandler {
	public abstract Collection<ATermAppl> getSubjects(KnowledgeBase kb);
	
	public abstract Iterator<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj);

	@Override
	public final ExtendedIterator<Triple> find(final KnowledgeBase kb, PelletInfGraph pellet, final Node s, final Node p, final Node o) {
		return WrappedIterator.create( 
			new NestedIterator<ATermAppl,Triple>( getSubjects( kb ) ) {
				@Override
				public Iterator<Triple> getInnerIterator(ATermAppl subj) {
					Node s = JenaUtils.makeGraphNode( subj );
					return objectFiller( s, p, getObjects( kb, subj ) );
				}
			}
		);
	}			
}