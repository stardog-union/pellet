// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.converter;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * <p>
 * Title: TripleAdder
 * </p>
 * <p>
 * Description: Convenience class that defines methods to add triples into a
 * graph.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class TripleAdder {
	public static void add(Graph graph, Node s, Node p, Node o) {
		graph.add( Triple.create( s, p, o ) );
	}

	public static void add(Graph graph, Node s, Resource p, Node o) {
		add( graph, s, p.asNode(), o );
	}

	public static void add(Graph graph, Node s, Resource p, RDFNode o) {
		add( graph, s, p.asNode(), o.asNode() );
	}

	public static void add(Graph graph, Resource s, Resource p, RDFNode o) {
		add( graph, s.asNode(), p.asNode(), o.asNode() );
	}
}
