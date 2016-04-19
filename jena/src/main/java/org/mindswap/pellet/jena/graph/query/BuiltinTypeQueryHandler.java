// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import java.util.Collection;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

import aterm.ATermAppl;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

abstract class BuiltinTypeQueryHandler extends TripleQueryHandler {
	public abstract Collection<ATermAppl> getResults(KnowledgeBase kb);
	
	@Override
	public final boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		return !getResults( kb ).isEmpty();
	}
	
	@Override
	public final ExtendedIterator<Triple> find(KnowledgeBase kb, PelletInfGraph pellet, Node subj, Node pred, Node obj) {
		return subjectFiller( getResults( kb ), pred, obj );
	}			
}