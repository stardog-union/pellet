// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 * @author Evren Sirin
 */
@RunWith(Parameterized.class)
public class TestParameterizedQuery {
	private static final String	NS		= "http://example.org#";
	private static final String	PREFIX	= "PREFIX : <" + NS + ">";

	private static Dataset		dataset;

	private static QuerySolutionMap binding(String var, RDFNode value) {
		QuerySolutionMap b = new QuerySolutionMap();
		b.add( var, value );

		return b;
	}

	private static QuerySolutionMap binding(String var1, RDFNode value1, String var2, RDFNode value2) {
		QuerySolutionMap b = new QuerySolutionMap();
		b.add( var1, value1 );
		b.add( var2, value2 );

		return b;
	}

	@Parameters
	public static List<Object[]> getParameters() {
		Property pred = ResourceFactory.createProperty( NS + "p" );
		Resource x1 = ResourceFactory.createResource( NS + "x1" );
		Resource y1 = ResourceFactory.createResource( NS + "y1" );
		Resource x2 = ResourceFactory.createResource( NS + "x2" );
		Resource y2 = ResourceFactory.createResource( NS + "y2" );

		OntModel m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		m.add( x1, pred, y1 );
		m.add( x2, pred, y2 );

		dataset = DatasetFactory.create( m );

		Query q1 = QueryFactory.create( PREFIX + "SELECT ?x WHERE { ?x :p ?y }" );
		Query q2 = QueryFactory.create( PREFIX + "SELECT ?x ?y WHERE { ?x :p ?y }" );
		Query q3 = QueryFactory.create( PREFIX + "SELECT * WHERE { ?x :p ?y }" );
		Query q4 = QueryFactory.create( PREFIX + "SELECT * WHERE { :x1 :p ?y }" );

		QuerySolutionMap b1 = binding( "y", y1 );
		QuerySolutionMap b2 = binding( "x", x1 );
		QuerySolutionMap b3 = binding( "y", y1, "x", x1 );
		QuerySolutionMap b4 = null;

		List<Object[]> params = new ArrayList<Object[]>();

		for( QueryEngineType qe : QueryEngineType.values() ) {
			params.add( new Object[] { qe, q1, b1, b2 } );
			params.add( new Object[] { qe, q2, b2, b3 } );
			params.add( new Object[] { qe, q3, b1, b3 } );
			params.add( new Object[] { qe, q4, b4, b1 } );
		}

		return params;
	}

	private final QueryEngineType	queryEngine;
	private final Query			query;
	private final QuerySolution	initialBinding;
	private final QuerySolution	expected;

	public TestParameterizedQuery(QueryEngineType queryEngine, Query query,
			QuerySolutionMap initialBinding, QuerySolutionMap expectedResults) {
		this.queryEngine = queryEngine;
		this.query = query;
		this.initialBinding = initialBinding;
		this.expected = expectedResults;
	}

	@Test
	public void test() {
		QueryExecution qe = SparqlDLExecutionFactory.create( query, dataset, initialBinding,
				queryEngine );
		ResultSet rs = qe.execSelect();

		if( !rs.hasNext() )
			assertTrue( "No results found", rs.hasNext() );

		QuerySolution computed = rs.nextSolution();

		for( Iterator<?> i = rs.getResultVars().iterator(); i.hasNext(); ) {
			String var = i.next().toString();
			assertEquals( "Different result for " + var, expected.get( var ), computed.get( var ) );
		}

		assertFalse( "Extra results found", rs.hasNext() );
	}
}
