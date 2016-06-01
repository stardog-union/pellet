// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.query;

import static org.mindswap.pellet.utils.iterator.IteratorUtils.flatten;

import aterm.ATermAppl;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.WrappedIterator;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

public abstract class TripleQueryHandler
{
	public TripleQueryHandler()
	{
		//
	}

	public abstract boolean contains(KnowledgeBase kb, GraphLoader loader, Node subj, Node pred, Node obj);

	public abstract ExtendedIterator<Triple> find(KnowledgeBase kb, PelletInfGraph pellet, Node subj, Node pred, Node obj);

	protected ExtendedIterator<Triple> objectFiller(final Node s, final Node p, final Collection<ATermAppl> objects)
	{
		return objectFiller(s, p, objects.iterator());
	}

	protected ExtendedIterator<Triple> objectFiller(final Node s, final Node p, final Iterator<ATermAppl> objects)
	{
		return WrappedIterator.create(objects).mapWith(aTermAppl -> Triple.create(s, p, JenaUtils.makeGraphNode(aTermAppl)));
	}

	protected ExtendedIterator<Triple> objectSetFiller(final Node s, final Node p, final Set<Set<ATermAppl>> objectSets)
	{
		return objectFiller(s, p, flatten(objectSets.iterator()));
	}

	protected ExtendedIterator<Triple> propertyFiller(final Node s, final Collection<ATermAppl> properties, final Node o)
	{
		return propertyFiller(s, properties.iterator(), o);
	}

	protected ExtendedIterator<Triple> propertyFiller(final Node s, final Iterator<ATermAppl> properties, final Node o)
	{
		return WrappedIterator.create(properties).mapWith(aTermAppl -> Triple.create(s, JenaUtils.makeGraphNode(aTermAppl), o));
	}

	protected ExtendedIterator<Triple> subjectFiller(final Collection<ATermAppl> subjects, final Node p, final Node o)
	{
		return subjectFiller(subjects.iterator(), p, o);
	}

	protected ExtendedIterator<Triple> subjectFiller(final Iterator<ATermAppl> subjects, final Node p, final Node o)
	{
		return WrappedIterator.create(subjects).mapWith(aTermAppl -> Triple.create(JenaUtils.makeGraphNode(aTermAppl), p, o));
	}

	protected ExtendedIterator<Triple> subjectSetFiller(final Set<Set<ATermAppl>> subjectSets, final Node p, final Node o)
	{
		return subjectFiller(flatten(subjectSets.iterator()), p, o);
	}
}
