// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

import aterm.ATermAppl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

abstract class ObjectVarHandler extends TripleQueryHandler {
	public abstract Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj);
	
	@Override
	public final boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		return !getObjects( kb, loader.node2term( subj ) ).isEmpty();
	}
	
	@Override
	public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		return objectFiller( subj, pred, getObjects( kb, loader.node2term( subj ) ) );
	}			
}