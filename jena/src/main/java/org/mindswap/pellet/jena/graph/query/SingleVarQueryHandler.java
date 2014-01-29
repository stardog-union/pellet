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

abstract class SingleVarQueryHandler extends TripleQueryHandler {
	public abstract Set<ATermAppl> getResults(KnowledgeBase kb, ATermAppl term);
	
	@Override
	public final boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		Node n = subj.isConcrete() ? obj : subj;
		return !getResults( kb, loader.node2term( n ) ).isEmpty();
	}
	
	@Override
	public final ExtendedIterator<Triple> find(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		Node n = subj.isConcrete() ? obj : subj;
		Set<ATermAppl> results = getResults( kb, loader.node2term( n ) );
		return subj.isConcrete()
			? objectFiller( subj, pred, results )
			: subjectFiller( results, pred, obj );
	}			
}