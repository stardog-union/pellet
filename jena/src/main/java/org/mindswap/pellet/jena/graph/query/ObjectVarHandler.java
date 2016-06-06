// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import aterm.ATermAppl;
import java.util.Set;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

abstract class ObjectVarHandler extends TripleQueryHandler
{
	public abstract Set<ATermAppl> getObjects(KnowledgeBase kb, ATermAppl subj);

	@Override
	public final boolean contains(final KnowledgeBase kb, final GraphLoader loader, final Node subj, final Node pred, final Node obj)
	{
		return !getObjects(kb, loader.node2term(subj)).isEmpty();
	}

	@Override
	public final ExtendedIterator<Triple> find(final KnowledgeBase kb, final PelletInfGraph openllet, final Node subj, final Node pred, final Node obj)
	{
		return objectFiller(subj, pred, getObjects(kb, openllet.getLoader().node2term(subj)));
	}
}
