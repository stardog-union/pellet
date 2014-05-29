// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.util;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

/**
 * <p>
 * Title: Optimized Directed Multigraph
 * </p>
 * <p>
 * Description: A directed multigraph where the edge's multiplicity is implemented
 * as the weight of the edge as opposed to many instances of edges - has better
 * performances under graph traversal and connectivity algorithms.  
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class OptimizedDirectedMultigraph<V> extends SimpleDirectedWeightedGraph<V, DefaultWeightedEdge> {
	private static final long serialVersionUID = 1L;
	private static final String NON_POSITIVE_MULTIPLICITY = "Non-positive multiplicity is not allowed";

	public OptimizedDirectedMultigraph() {
		super(DefaultWeightedEdge.class);
	}
	
	public DefaultWeightedEdge addEdge(V sourceVertex, V targetVertex) {
		return addEdge(sourceVertex, targetVertex, 1);
	}
	
	public DefaultWeightedEdge addEdge(V sourceVertex, V targetVertex, int multiplicity) {
		if (multiplicity <= 0) {
			throw new IllegalArgumentException(NON_POSITIVE_MULTIPLICITY);
		}
		
		DefaultWeightedEdge edge = getEdge(sourceVertex, targetVertex);
		if (edge == null) {
			edge = super.addEdge(sourceVertex, targetVertex);
			setEdgeWeight(edge, multiplicity);
		} else {
			double oldWeight = getEdgeWeight(edge);
			setEdgeWeight(edge, oldWeight + multiplicity);
		}
		return edge;
	}
	
	public int getEdgeMultiplicity(DefaultWeightedEdge edge) {
		return (int) getEdgeWeight(edge);
	}
}

