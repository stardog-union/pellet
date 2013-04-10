// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Incremental change tracker
 * </p>
 * <p>
 * Description: Tracks the changes for incremental ABox reasoning services
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public interface IncrementalChangeTracker {

	/**
	 * Record that a new edge has been deleted from the ABox
	 * 
	 * @param e
	 *            the Edge
	 * @return boolean {@code true} if delete is not already noted for edge,
	 *         {@code false} else
	 */
	public boolean addDeletedEdge(Edge e);

	/**
	 * Record that a type was deleted from an individual
	 * 
	 * @param n
	 *            the Node
	 * @param type
	 *            the type
	 * @return boolean {@code true} if delete is not already noted for node,
	 *         type pair {@code false} else
	 */
	public boolean addDeletedType(Node n, ATermAppl type);

	/**
	 * Record that a new edge has been added to the ABox
	 * 
	 * @param e
	 *            the Edge
	 * @return boolean {@code true} if addition is not already noted for edge,
	 *         {@code false} else
	 */
	public boolean addNewEdge(Edge e);

	/**
	 * Record that a new individual has been added to the ABox
	 * 
	 * @param i
	 *            the Individual
	 * @return boolean {@code true} if addition is not already noted for
	 *         individual, {@code false} else
	 */
	public boolean addNewIndividual(Individual i);

	/**
	 * Record that a node has been "unpruned" because a merge was reverted
	 * during restore
	 * 
	 * @param n
	 *            the Node
	 * @return boolean {@code true} if unpruning is not already noted for node,
	 *         {@code false} else
	 */
	public boolean addUnprunedNode(Node n);

	/**
	 * Record that an individual has been updated
	 * 
	 * @param i
	 *            the Individual
	 * @return boolean {@code true} if addition is not already noted for
	 *         individual, {@code false} else
	 */
	public boolean addUpdatedIndividual(Individual i);

	/**
	 * Clear all recorded changes
	 */
	public void clear();

	/**
	 * Copy change tracker for use with a new ABox (presumably as part of
	 * {@code ABox.copy()})
	 * 
	 * @param target
	 *            The ABox for the copy
	 * @return a copy, with individuals in the target ABox
	 */
	public IncrementalChangeTracker copy(ABox target);

	/**
	 * Iterate over all edges deleted (see {@link #addDeletedEdge(Edge)}) since
	 * the previous {@link #clear()}
	 * 
	 * @return Iterator
	 */
	public Iterator<Edge> deletedEdges();

	/**
	 * Iterate over all nodes with deleted types (and those types) (see
	 * {@link #addDeletedType(Node, ATermAppl)}) since the previous
	 * {@link #clear()}
	 * 
	 * @return Iterator
	 */
	public Iterator<Map.Entry<Node, Set<ATermAppl>>> deletedTypes();

	/**
	 * Iterate over all edges added (see {@link #addNewEdge(Edge)}) since the
	 * previous {@link #clear()}
	 * 
	 * @return Iterator
	 */
	public Iterator<Edge> newEdges();

	/**
	 * Iterate over all individuals added (see
	 * {@link #addNewIndividual(Individual)}) since the previous
	 * {@link #clear()}
	 * 
	 * @return Iterator
	 */
	public Iterator<Individual> newIndividuals();

	/**
	 * Iterate over all nodes unpruned (see
	 * {@link #addUnprunedIndividual(Individual)}) since the previous
	 * {@link #clear()}
	 * 
	 * @return Iterator
	 */
	public Iterator<Node> unprunedNodes();

	/**
	 * Iterate over all individuals updated (see
	 * {@link #addUpdatedIndividual(Individual)}) since the previous
	 * {@link #clear()}
	 * 
	 * @return Iterator
	 */
	public Iterator<Individual> updatedIndividuals();
}
