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

abstract class SingleVarQueryHandler extends TripleQueryHandler
{
	public abstract Set<ATermAppl> getResults(KnowledgeBase kb, ATermAppl term);

	@Override
	public final boolean contains(final KnowledgeBase kb, final GraphLoader loader, final Node subj, final Node pred, final Node obj)
	{
		final Node n = subj.isConcrete() ? obj : subj;
		return !getResults(kb, loader.node2term(n)).isEmpty();
	}

	@Override
	public final ExtendedIterator<Triple> find(final KnowledgeBase kb, final PelletInfGraph pellet, final Node subj, final Node pred, final Node obj)
	{
		final Node n = subj.isConcrete() ? obj : subj;
		final Set<ATermAppl> results = getResults(kb, pellet.getLoader().node2term(n));
		return subj.isConcrete() ? objectFiller(subj, pred, results) : subjectFiller(results, pred, obj);
	}
}
