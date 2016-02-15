// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NullIterator;
import org.apache.jena.util.iterator.SingletonIterator;

abstract class BooleanQueryHandler extends TripleQueryHandler {
	@Override
	public ExtendedIterator<Triple> find(KnowledgeBase kb, PelletInfGraph pellet, Node subj, Node pred, Node obj) {
		return contains( kb, pellet.getLoader(), subj, pred, obj )
			? new SingletonIterator<Triple>( Triple.create( subj, pred, obj ) )
			: NullIterator.<Triple>instance();
	}			
}