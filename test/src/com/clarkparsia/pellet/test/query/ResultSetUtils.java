// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat;

/**
 * @author Evren Sirin
 */
public class ResultSetUtils {
	private static final Logger log = Logger.getLogger(ResultSetUtils.class.getName());
	
	private static final RDFNode DUMMY_FOR_BNODE = ResourceFactory.createPlainLiteral("dummy node for bnode");

	public static boolean assertEquals(ResultSet expectedResults, ResultSet computedResults) {
		ResultSetRewindable expected = ResultSetFactory.makeRewindable( expectedResults );
		ResultSetRewindable computed = ResultSetFactory.makeRewindable( computedResults );
		                                            			
		System.out.println( "Computed: " + computed.size() + " Expected: " + expected.size() );
		if( expected.size() != computed.size() ) {
			logResults( "Expected", expected );
			logResults( "Real", computed );			
			Assert.fail( "Expected " + expected.size() + " but got " + computed.size() );
		}
	
		List<String> vars = expected.getResultVars();
		Collection<Map<String, RDFNode>> results = results( computed );
		for( expected.reset(); expected.hasNext(); ) {
			QuerySolution qs = expected.nextSolution();
			Map<String, RDFNode> map = solutionMap(qs, vars);
			
			if( !results.contains( map ) ) {
				logResults( "Expected", expected );
				logResults( "Real", computed );
				Assert.fail( "Solution not found: " + map );				
	
				Assert.fail( "Expected " + expected.size() + " but got " + computed.size() );				
			}
		}
	
		return true;
	}

	public static Map<String, RDFNode> solutionMap(QuerySolution qs, List<String> vars) {
		Map<String, RDFNode> result = new HashMap<String, RDFNode>();
		for (String var : vars) {
			RDFNode val = qs.get(var);			
			result.put(var, val.isAnon() ? DUMMY_FOR_BNODE : val);
		}
		return result;
	}

	public static Collection<Map<String, RDFNode>> results(ResultSetRewindable rs) {
		rs.reset();
		List<String> vars = rs.getResultVars();
		Set<Map<String, RDFNode>> results = new HashSet<Map<String, RDFNode>>();
		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			Map<String, RDFNode> result = new HashMap<String, RDFNode>();
			for (String var : vars) {
				result.put(var, qs.get(var));
			}
			results.add(solutionMap(qs, vars));
		}
		return results;
	}

	public static void logResults(String name, ResultSetRewindable results) {
		if (log.isLoggable(Level.WARNING)) {
			log.warning(name + " (" + results.size() + ")");
			results.reset();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ResultSetFormatter.output(out, results, ResultSetFormat.syntaxText);
			log.warning("\n" + out.toString());
		}
	}
}
