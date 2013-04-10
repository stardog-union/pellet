// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.URIUtils;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * <p>
 * Title: Engine for processing DAWG test manifests
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class PelletSparqlDawgTester extends ARQSparqlDawgTester {
	public static boolean			CLASSIFY_KB_IN_ADVANCE			= false;

	private List<String>			avoidList						= Arrays.asList( new String[] {
																	// FIXME
																	// with some
																	// effort
																	// some of
																	// the
																	// following
																	// queries
																	// can be
																	// handled

			// The following test assumes simple entailment but Pellet does
			// D-entailment
			"open-eq-01",

			// The following test requires distinguishing different undefined
			// datatypes
			"open-eq-02",

			// The following test requires properly handling undefined datatypes
			"open-eq-05",

			// The following tests require [object/data]property punning in the
			// data
			"open-eq-07", "open-eq-08", "open-eq-09", "open-eq-10", "open-eq-11", "open-eq-12",

			// Pellet returns extra type owl:thing in the results
			"term-3",

			// ?s ?p ?o atom mapped to PropertyValue causing incompleteness
			"construct-1", "construct-3", "construct-4", "construct-5",

			// variables not mentioned in the SELECT but used in ORDER BY not
			// handled
			"dawg-sort-builtin", "dawg-sort-function",

			// rdf:List vocabulary not supported
			"list-1", "list-2", "list-3", "list-4",

			// fails due to the same reasons in MiscTests.testCanonicalLiteral
			"distinct-2", "no-distinct-2",

			// requires OWL-Full compatibility (support rdf:Property)
			"join-combo-1", "join-combo-2",

			// not an approved test (and in clear conflict with
			// "dawg-optional-filter-005-simplified",
			"dawg-optional-filter-005-not-simplified",

			// fails due to bugs in ARQ filter handling
			"date-2", "date-3",

			// ?x p "+3"^^xsd:int does not match "3"^^xsd:int
			"unplus-1",
			
			// ?x p "01"^^xsd:int does not match "1"^^xsd:int
			"open-eq-03",


			// The following tests will not pass because they require
			// alternative literal forms in results. Mostly, this is
			// xsd:integer when Pellet returns xsd:decimal

			"var-1", "var-2",
			"open-eq-04",
			"nested-opt-1", "nested-opt-2", "opt-filter-1", "opt-filter-2", "filter-scope-1",
			"dawg-optional-complex-2",
			"dawg-optional-filter-001", "dawg-optional-filter-002", "dawg-optional-filter-003", "dawg-optional-filter-005-simplified",
			"dawg-graph-01", "dawg-graph-03", "dawg-graph-05", "dawg-graph-06", "dawg-graph-07", "dawg-graph-08", "dawg-graph-11",
			"dawg-dataset-01", "dawg-dataset-03", "dawg-dataset-05", "dawg-dataset-06", "dawg-dataset-07", "dawg-dataset-08", "dawg-dataset-11", "dawg-dataset-12", "dawg-dataset-12b",
			"no-distinct-1", "distinct-1",
			"dawg-sort-4", "dawg-sort-5", "dawg-sort-7", "dawg-sort-9", "dawg-sort-10",
			"limit-1", "limit-2", "limit-4", "offset-1", "offset-2", "offset-4", "slice-1", "slice-2", "slice-4", "slice-5"
																	} );

	protected QueryEngineType 		queryEngineType;

	protected boolean				handleVariableSPO				= true;

	public PelletSparqlDawgTester(QueryEngineType queryEngineType, boolean handleVariableSPO) {
		this.queryEngineType = queryEngineType;
		this.handleVariableSPO = handleVariableSPO;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Dataset createDataset() {
		boolean useQueryGraphs = !query.getGraphURIs().isEmpty()
				|| !query.getNamedGraphURIs().isEmpty();

		Collection<String> graphURIs = useQueryGraphs
			? query.getGraphURIs()
			: this.graphURIs;

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		for( String dataURI : graphURIs ) {
			FileManager.get().readModel( model, dataURI );
		}

		model.prepare();

		if( PelletSparqlDawgTester.CLASSIFY_KB_IN_ADVANCE ) {
			((PelletInfGraph) (model.getGraph())).getKB().classify();
		}

		DataSource dataset = DatasetFactory.create( model );

		Collection<String> namedGraphURIs = useQueryGraphs
			? query.getNamedGraphURIs()
			: this.namedGraphURIs;

		for( String graphURI : namedGraphURIs ) {
			model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
			FileManager.get().readModel( model, graphURI );
			dataset.addNamedModel( graphURI, model );
		}

		return dataset;
	}

	@Override
	protected QueryExecution createQueryExecution() {		
		return SparqlDLExecutionFactory.create( query, createDataset(), null, queryEngineType, handleVariableSPO );		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isApplicable(String uri) {
		return !avoidList.contains( URIUtils.getLocalName( uri ) );
	}

	public String getName() {		
		return queryEngineType.toString();
	}
}
