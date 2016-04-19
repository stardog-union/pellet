// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphListener;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Dyadic;
import org.apache.jena.graph.compose.Polyadic;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.vocabulary.RDF;

/**
 * A graph listener that listens to graph change events and if possible processes the change event. The listener is
 * passed a possibly union graph but the listener is attached only to leaf (non-union) graphs. The listener keeps track
 * which graph is changed and also checks if subgraphs are added or removed from the root graph.
 *
 * @author Evren Sirin
 */
public class PelletGraphListener implements GraphListener {
  // KB object - used for incremental ABox changes
  private final KnowledgeBase kb;

  private final Graph rootGraph;

  private Set<Graph> leafGraphs;

  private final Set<Graph> changedGraphs;

  private boolean statementDeleted;

  private boolean enabled;

  public PelletGraphListener(final Graph rootGraph, final KnowledgeBase kb, final boolean enabled) {
    this.rootGraph = rootGraph;
    this.kb = kb;
    this.enabled = enabled;

    leafGraphs = CollectionUtils.makeSet();
    changedGraphs = CollectionUtils.makeSet();

    statementDeleted = false;

    if (enabled) {
      collectLeafGraphs(rootGraph, Collections.<Graph> emptySet());
    }
  }

  private void addABoxTriple(final Triple t) {
    // Convert the Jena nodes to ATermAppl
    final ATermAppl s = JenaUtils.makeATerm(t.getSubject());
    final ATermAppl o = JenaUtils.makeATerm(t.getObject());

    // check if this is a type assertion
    if (t.getPredicate().equals(RDF.type.asNode())) {
      // check if this is a new individual
      if (!kb.getIndividuals().contains(s))
        kb.addIndividual(s);

      // add the type
      kb.addType(s, o);
    } else {
      // check if the subject is a new individual
      if (!kb.getIndividuals().contains(s))
        kb.addIndividual(s);

      // check if the object is a new individual
      if (!t.getObject().isLiteral() && !kb.getIndividuals().contains(o))
        kb.addIndividual(o);

      final ATermAppl p = JenaUtils.makeATerm(t.getPredicate());
      // add the property value
      kb.addPropertyValue(p, s, o);
    }
  }

  /**
   * Checks if the graph can be u[dated incrementally
   *
   * @return
   */
  private boolean canUpdateIncrementally(final Graph g) {
    return PelletOptions.PROCESS_JENA_UPDATES_INCREMENTALLY && !statementDeleted && !changedGraphs.contains(g);
  }

  private void collectLeafGraphs(final Graph graph, final Set<Graph> prevLeaves) {
    if (graph instanceof Polyadic) {
      final Polyadic union = ((Polyadic) graph);
      if (union.getBaseGraph() != null)
        collectLeafGraphs(union.getBaseGraph(), prevLeaves);

      for (final Graph graph2 : union.getSubGraphs())
        collectLeafGraphs(graph2, prevLeaves);
    } else if (graph instanceof Dyadic) {
      final Dyadic dyadic = ((Dyadic) graph);
      if (dyadic.getL() instanceof Graph)
        collectLeafGraphs((Graph) dyadic.getL(), prevLeaves);

      if (dyadic.getR() instanceof Graph)
        collectLeafGraphs((Graph) dyadic.getR(), prevLeaves);
    } else if (graph instanceof InfGraph) {
      collectLeafGraphs(((InfGraph) graph).getRawGraph(), prevLeaves);
    } else if (leafGraphs.add(graph) && !prevLeaves.contains(graph)) {
      changedGraphs.add(graph);

      graph.getEventManager().register(this);
    }
  }

  private void deleteABoxTriple(final Triple t) {
    final ATermAppl s = JenaUtils.makeATerm(t.getSubject());
    final ATermAppl o = JenaUtils.makeATerm(t.getObject());

    // check if this is a type assertion
    if (t.getPredicate().equals(RDF.type.asNode())) {
      if (kb.isIndividual(s))
        kb.removeType(s, o);
    } else {
      // check if the subject is a new individual
      if (kb.isIndividual(s) && (kb.isIndividual(o) || ATermUtils.isLiteral(o))) {
        final ATermAppl p = JenaUtils.makeATerm(t.getPredicate());
        // add the property value
        kb.removePropertyValue(p, s, o);
      }
    }
  }

  public void dispose() {
    for (final Graph graph : leafGraphs) {
      graph.getEventManager().unregister(this);
    }

    leafGraphs.clear();
    changedGraphs.clear();

    statementDeleted = false;
  }

  public Set<Graph> getChangedGraphs() {
    final Set<Graph> prevLeaves = leafGraphs;

    leafGraphs = CollectionUtils.makeSet();

    collectLeafGraphs(rootGraph, prevLeaves);

    for (final Graph prevLeaf : prevLeaves) {
      if (!leafGraphs.contains(prevLeaf)) {
        statementDeleted = true;

        prevLeaf.getEventManager().unregister(this);
      }
    }

    if (statementDeleted) {
      return null;
    }

    return changedGraphs;
  }

  /**
   * @return
   */
  public Set<Graph> getLeafGraphs() {
    return leafGraphs;
  }

  /**
   * Checks if the given triple is an ABox assertion. Currently, only type assertions with atomic concepts are detected
   * and property assertions
   *
   * @param t
   * @return
   */
  private boolean isABoxChange(final Triple t) {
    final Node o = t.getObject();
    final Node p = t.getPredicate();

    // detect if this is a supported ABox type assertion
    if (p.equals(RDF.type.asNode())) {
      // check if the object is a bnode to detect complex concepts
      if (o.isBlank()) {
        return false;
      }

      // check that the object is an atomic concept that exists in the KB
      final ATermAppl object = JenaUtils.makeATerm(o);
      if (!kb.isClass(object)) {
        return false;
      }

      // Note: we do not check if the subject already exists,
      // as it could be a newly added individual

    } else {
      // detect ABox property assertions
      final ATermAppl prop = JenaUtils.makeATerm(p);

      // check if the role is this is a defined role
      if (!kb.isProperty(prop)) {
        return false;
      }

      // Note: we do not check if the subject and object already exists,
      // as they
      // could be a newly added individuals
    }

    return true;
  }

  public boolean isChanged() {
    if (statementDeleted || !changedGraphs.isEmpty()) {
      return true;
    }

    getChangedGraphs();

    return statementDeleted || !changedGraphs.isEmpty();
  }

  @Override
  public void notifyAddArray(final Graph g, final Triple[] triples) {
    notifyAddIterator(g, IteratorUtils.iterator(triples));
  }

  @Override
  public void notifyAddGraph(final Graph g, final Graph added) {
    notifyAddIterator(g, added.find(Triple.ANY));
  }

  @Override
  public void notifyAddIterator(final Graph g, final Iterator<Triple> it) {
    boolean canUpdateIncrementally = canUpdateIncrementally(g);

    if (canUpdateIncrementally) {
      while (it.hasNext()) {
        final Triple t = it.next();
        if (!isABoxChange(t)) {
          canUpdateIncrementally = false;
          break;
        }
        addABoxTriple(t);
      }
    }

    if (!canUpdateIncrementally) {
      changedGraphs.add(g);
    }
  }

  @Override
  public void notifyAddList(final Graph g, final List<Triple> triples) {
    notifyAddIterator(g, triples.iterator());
  }

  @Override
  public void notifyAddTriple(final Graph g, final Triple t) {
    if (canUpdateIncrementally(g) && isABoxChange(t)) {
      addABoxTriple(t);
    } else {
      changedGraphs.add(g);
    }
  }

  @Override
  public void notifyDeleteArray(final Graph g, final Triple[] triples) {
    notifyDeleteIterator(g, IteratorUtils.iterator(triples));
  }

  @Override
  public void notifyDeleteGraph(final Graph g, final Graph removed) {
    notifyDeleteIterator(g, removed.find(Triple.ANY));
  }

  @Override
  public void notifyDeleteIterator(final Graph g, final Iterator<Triple> it) {
    boolean canUpdateIncrementally = canUpdateIncrementally(g);

    if (canUpdateIncrementally) {
      while (it.hasNext()) {
        final Triple t = it.next();
        if (!isABoxChange(t)) {
          canUpdateIncrementally = false;
          break;
        }
        deleteABoxTriple(t);
      }
    }

    if (!canUpdateIncrementally) {
      statementDeleted = true;
      changedGraphs.add(g);
    }
  }

  @Override
  public void notifyDeleteList(final Graph g, final List<Triple> list) {
    notifyDeleteIterator(g, list.iterator());
  }

  @Override
  public void notifyDeleteTriple(final Graph g, final Triple t) {
    if (canUpdateIncrementally(g) && isABoxChange(t)) {
      deleteABoxTriple(t);
    } else {
      statementDeleted = true;
      changedGraphs.add(g);
    }
  }

  @Override
  public void notifyEvent(final Graph source, final Object value) {
    statementDeleted = true;
  }

  public void reset() {
    changedGraphs.clear();
    // leafGraphs.clear();
    statementDeleted = false;
  }

  public void setEnabled(final boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }

    this.enabled = enabled;

    leafGraphs.clear();
    changedGraphs.clear();

    statementDeleted = false;

    if (enabled) {
      collectLeafGraphs(rootGraph, Collections.<Graph> emptySet());
    } else {
      for (final Graph graph : leafGraphs) {
        graph.getEventManager().unregister(this);
      }
    }
  }
}
