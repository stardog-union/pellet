// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

import aterm.ATermAppl;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

abstract class SubjectVarHandler extends TripleQueryHandler {
	public abstract Set<ATermAppl> getSubjects(KnowledgeBase kb, ATermAppl obj);
	
	@Override
	public final boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj) {
		return !getSubjects( kb, loader.node2term( obj ) ).isEmpty();
	}
	
	@Override
	public final ExtendedIterator<Triple> find(KnowledgeBase kb, PelletInfGraph pellet, Node subj, Node pred, Node obj) {
		return subjectFiller( getSubjects( kb, pellet.getLoader().node2term( obj ) ), pred, obj );
	}			
}