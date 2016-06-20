// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import openllet.aterm.ATermAppl;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphListener;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Dyadic;
import org.apache.jena.graph.compose.Polyadic;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.vocabulary.RDF;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

/**
 * A graph listener that listens to graph change events and if possible processes the change event. The listener is passed a possibly union graph but the
 * listener is attached only to leaf (non-union) graphs. The listener keeps track which graph is changed and also checks if subgraphs are added or removed from
 * the root graph.
 *
 * @author Evren Sirin
 */
public class PelletGraphListener implements GraphListener
{
	// KB object - used for incremental ABox changes
	private final KnowledgeBase _kb;

	private final Graph _rootGraph;

	private Set<Graph> _leafGraphs;

	private final Set<Graph> _changedGraphs;

	private boolean _statementDeleted;

	private boolean _enabled;

	public PelletGraphListener(final Graph rootGraph, final KnowledgeBase kb, final boolean enabled)
	{
		this._rootGraph = rootGraph;
		this._kb = kb;
		this._enabled = enabled;

		_leafGraphs = CollectionUtils.makeSet();
		_changedGraphs = CollectionUtils.makeSet();

		_statementDeleted = false;

		if (enabled)
			collectLeafGraphs(rootGraph, Collections.<Graph> emptySet());
	}

	private void addABoxTriple(final Triple t)
	{
		// Convert the Jena _nodes to ATermAppl
		final ATermAppl s = JenaUtils.makeATerm(t.getSubject());
		final ATermAppl o = JenaUtils.makeATerm(t.getObject());

		// check if this is a type assertion
		if (t.getPredicate().equals(RDF.type.asNode()))
		{
			// check if this is a new _individual
			if (!_kb.getIndividuals().contains(s))
				_kb.addIndividual(s);

			// add the type
			_kb.addType(s, o);
		}
		else
		{
			// check if the subject is a new _individual
			if (!_kb.getIndividuals().contains(s))
				_kb.addIndividual(s);

			// check if the object is a new _individual
			if (!t.getObject().isLiteral() && !_kb.getIndividuals().contains(o))
				_kb.addIndividual(o);

			final ATermAppl p = JenaUtils.makeATerm(t.getPredicate());
			// add the property value
			_kb.addPropertyValue(p, s, o);
		}
	}

	/**
	 * Checks if the graph can be u[dated incrementally
	 *
	 * @return
	 */
	private boolean canUpdateIncrementally(final Graph g)
	{
		return PelletOptions.PROCESS_JENA_UPDATES_INCREMENTALLY && !_statementDeleted && !_changedGraphs.contains(g);
	}

	private void collectLeafGraphs(final Graph graph, final Set<Graph> prevLeaves)
	{
		if (graph instanceof Polyadic)
		{
			final Polyadic union = ((Polyadic) graph);
			if (union.getBaseGraph() != null)
				collectLeafGraphs(union.getBaseGraph(), prevLeaves);

			for (final Graph graph2 : union.getSubGraphs())
				collectLeafGraphs(graph2, prevLeaves);
		}
		else
			if (graph instanceof Dyadic)
			{
				final Dyadic dyadic = ((Dyadic) graph);
				if (dyadic.getL() instanceof Graph)
					collectLeafGraphs((Graph) dyadic.getL(), prevLeaves);

				if (dyadic.getR() instanceof Graph)
					collectLeafGraphs((Graph) dyadic.getR(), prevLeaves);
			}
			else
				if (graph instanceof InfGraph)
					collectLeafGraphs(((InfGraph) graph).getRawGraph(), prevLeaves);
				else
					if (_leafGraphs.add(graph) && !prevLeaves.contains(graph))
					{
						_changedGraphs.add(graph);

						graph.getEventManager().register(this);
					}
	}

	private void deleteABoxTriple(final Triple t)
	{
		final ATermAppl s = JenaUtils.makeATerm(t.getSubject());
		final ATermAppl o = JenaUtils.makeATerm(t.getObject());

		// check if this is a type assertion
		if (t.getPredicate().equals(RDF.type.asNode()))
		{
			if (_kb.isIndividual(s))
				_kb.removeType(s, o);
		}
		else
			// check if the subject is a new _individual
			if (_kb.isIndividual(s) && (_kb.isIndividual(o) || ATermUtils.isLiteral(o)))
			{
				final ATermAppl p = JenaUtils.makeATerm(t.getPredicate());
				// add the property value
				_kb.removePropertyValue(p, s, o);
			}
	}

	public void dispose()
	{
		for (final Graph graph : _leafGraphs)
			graph.getEventManager().unregister(this);

		_leafGraphs.clear();
		_changedGraphs.clear();

		_statementDeleted = false;
	}

	public Set<Graph> getChangedGraphs()
	{
		final Set<Graph> prevLeaves = _leafGraphs;

		_leafGraphs = CollectionUtils.makeSet();

		collectLeafGraphs(_rootGraph, prevLeaves);

		for (final Graph prevLeaf : prevLeaves)
			if (!_leafGraphs.contains(prevLeaf))
			{
				_statementDeleted = true;

				prevLeaf.getEventManager().unregister(this);
			}

		if (_statementDeleted)
			return null;

		return _changedGraphs;
	}

	/**
	 * @return
	 */
	public Set<Graph> getLeafGraphs()
	{
		return _leafGraphs;
	}

	/**
	 * Checks if the given triple is an ABox assertion. Currently, only type assertions with atomic concepts are detected and property assertions
	 *
	 * @param t
	 * @return
	 */
	private boolean isABoxChange(final Triple t)
	{
		final Node o = t.getObject();
		final Node p = t.getPredicate();

		// detect if this is a supported ABox type assertion
		if (p.equals(RDF.type.asNode()))
		{
			// check if the object is a bnode to detect complex concepts
			if (o.isBlank())
				return false;

			// check that the object is an atomic concept that exists in the KB
			final ATermAppl object = JenaUtils.makeATerm(o);
			if (!_kb.isClass(object))
				return false;

			// Note: we do not check if the subject already exists,
			// as it could be a newly added _individual

		}
		else
		{
			// detect ABox property assertions
			final ATermAppl prop = JenaUtils.makeATerm(p);

			// check if the role is this is a defined role
			if (!_kb.isProperty(prop))
				return false;

			// Note: we do not check if the subject and object already exists,
			// as they
			// could be a newly added individuals
		}

		return true;
	}

	public boolean isChanged()
	{
		if (_statementDeleted || !_changedGraphs.isEmpty())
			return true;

		getChangedGraphs();

		return _statementDeleted || !_changedGraphs.isEmpty();
	}

	@Override
	public void notifyAddArray(final Graph g, final Triple[] triples)
	{
		notifyAddIterator(g, IteratorUtils.iterator(triples));
	}

	@Override
	public void notifyAddGraph(final Graph g, final Graph added)
	{
		notifyAddIterator(g, added.find(Triple.ANY));
	}

	@Override
	public void notifyAddIterator(final Graph g, final Iterator<Triple> it)
	{
		boolean canUpdateIncrementally = canUpdateIncrementally(g);

		if (canUpdateIncrementally)
			while (it.hasNext())
			{
				final Triple t = it.next();
				if (!isABoxChange(t))
				{
					canUpdateIncrementally = false;
					break;
				}
				addABoxTriple(t);
			}

		if (!canUpdateIncrementally)
			_changedGraphs.add(g);
	}

	@Override
	public void notifyAddList(final Graph g, final List<Triple> triples)
	{
		notifyAddIterator(g, triples.iterator());
	}

	@Override
	public void notifyAddTriple(final Graph g, final Triple t)
	{
		if (canUpdateIncrementally(g) && isABoxChange(t))
			addABoxTriple(t);
		else
			_changedGraphs.add(g);
	}

	@Override
	public void notifyDeleteArray(final Graph g, final Triple[] triples)
	{
		notifyDeleteIterator(g, IteratorUtils.iterator(triples));
	}

	@Override
	public void notifyDeleteGraph(final Graph g, final Graph removed)
	{
		notifyDeleteIterator(g, removed.find(Triple.ANY));
	}

	@Override
	public void notifyDeleteIterator(final Graph g, final Iterator<Triple> it)
	{
		boolean canUpdateIncrementally = canUpdateIncrementally(g);

		if (canUpdateIncrementally)
			while (it.hasNext())
			{
				final Triple t = it.next();
				if (!isABoxChange(t))
				{
					canUpdateIncrementally = false;
					break;
				}
				deleteABoxTriple(t);
			}

		if (!canUpdateIncrementally)
		{
			_statementDeleted = true;
			_changedGraphs.add(g);
		}
	}

	@Override
	public void notifyDeleteList(final Graph g, final List<Triple> list)
	{
		notifyDeleteIterator(g, list.iterator());
	}

	@Override
	public void notifyDeleteTriple(final Graph g, final Triple t)
	{
		if (canUpdateIncrementally(g) && isABoxChange(t))
			deleteABoxTriple(t);
		else
		{
			_statementDeleted = true;
			_changedGraphs.add(g);
		}
	}

	@Override
	public void notifyEvent(final Graph source, final Object value)
	{
		_statementDeleted = true;
	}

	public void reset()
	{
		_changedGraphs.clear();
		// _leafGraphs.clear();
		_statementDeleted = false;
	}

	public void setEnabled(final boolean enabled)
	{
		if (this._enabled == enabled)
			return;

		this._enabled = enabled;

		_leafGraphs.clear();
		_changedGraphs.clear();

		_statementDeleted = false;

		if (enabled)
			collectLeafGraphs(_rootGraph, Collections.<Graph> emptySet());
		else
			for (final Graph graph : _leafGraphs)
				graph.getEventManager().unregister(this);
	}
}
