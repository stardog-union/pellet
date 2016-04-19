// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.converter;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * <p>
 * Title: TripleAdder
 * </p>
 * <p>
 * Description: Convenience class that defines methods to add triples into a graph.
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
public class TripleAdder
{
	public static void add(final Graph graph, final Node s, final Node p, final Node o)
	{
		graph.add(Triple.create(s, p, o));
	}

	public static void add(final Graph graph, final Node s, final Resource p, final Node o)
	{
		add(graph, s, p.asNode(), o);
	}

	public static void add(final Graph graph, final Node s, final Resource p, final RDFNode o)
	{
		add(graph, s, p.asNode(), o.asNode());
	}

	public static void add(final Graph graph, final Resource s, final Resource p, final RDFNode o)
	{
		add(graph, s.asNode(), p.asNode(), o.asNode());
	}
}
