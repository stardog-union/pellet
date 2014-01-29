// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

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
public class SparqlDawgTestVocabulary {

	private static final String		manifestVocabulary		= "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#";
	private static final String		queryVocabulary			= "http://www.w3.org/2001/sw/DataAccess/tests/test-query#";
	private static final String		dawgApprovalVocabulary	= "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#";
	private static final String		resultSetVocabulary		= "http://www.w3.org/2001/sw/DataAccess/tests/result-set#";

	// MANIFEST SCHEMA
	// general classes
	public static final Resource	Manifest				= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "Manifest" );

	// general properties
	public static final Property	include					= ResourceFactory
																	.createProperty( manifestVocabulary
																			+ "include" );

	public static final Property	entries					= ResourceFactory
																	.createProperty( manifestVocabulary
																			+ "entries" );

	// test types
	public static final Resource	PositiveSyntaxTest		= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "PositiveSyntaxTest" );

	public static final Resource	NegativeSyntaxTest		= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "NegativeSyntaxTest" );

	public static final Resource	QueryEvaluationTest		= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "QueryEvaluationTest" );

	// properties of tests
	public static final Property	name					= ResourceFactory
																	.createProperty( manifestVocabulary
																			+ "name" );

	public static final Property	action					= ResourceFactory
																	.createProperty( manifestVocabulary
																			+ "action" );
	public static final Property	result					= ResourceFactory
																	.createProperty( manifestVocabulary
																			+ "result" );

	// QUERY SCHEMA
	// general classes
	public static final Resource	QueryForm				= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "QueryForm" );

	// query forms
	public static final Resource	QuerySelect				= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "QuerySelect" );
	public static final Resource	QueryConstruct			= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "QueryConstruct" );
	public static final Resource	QueryDescribe			= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "QueryDescribe" );
	public static final Resource	QueryAsk				= ResourceFactory
																	.createResource( manifestVocabulary
																			+ "QueryAsk" );

	// general properties
	public static final Property	query					= ResourceFactory
																	.createProperty( queryVocabulary
																			+ "query" );

	public static final Property	data					= ResourceFactory
																	.createProperty( queryVocabulary
																			+ "data" );
	public static final Property	graphData				= ResourceFactory
																	.createProperty( queryVocabulary
																			+ "graphData" );

	public static final Resource	queryForm				= ResourceFactory
																	.createProperty( manifestVocabulary
																			+ "queryForm" );

	// DAWG SCHEMA
	// general classes
	public static final Resource	Status					= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "Status" );
	public static final Resource	ResultForm				= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "ResultForm" );

	// status types
	public static final Resource	NotClassified			= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "NotClassified" );

	public static final Resource	Approved				= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "Approved" );

	public static final Resource	Rejected				= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "Rejected" );
	public static final Resource	Obsoleted				= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "Obsoleted" );
	public static final Resource	Withdrawn				= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "Withdrawn" );

	// result form types
	public static final Resource	dawgResultSet			= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "ResultSet" );

	public static final Resource	ResultGraph				= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "ResultGraph" );

	public static final Resource	ResultBoolean			= ResourceFactory
																	.createResource( dawgApprovalVocabulary
																			+ "ResultBoolean" );

	// properties
	public static final Property	approval				= ResourceFactory
																	.createProperty( dawgApprovalVocabulary
																			+ "approval" );

	public static final Property	approvedBy				= ResourceFactory
																	.createProperty( dawgApprovalVocabulary
																			+ "approvedBy" );

	// RESULTS SCHEMA
	// general classes
	public static final Resource	rsResultSet				= ResourceFactory
																	.createResource( resultSetVocabulary
																			+ "ResultSet" );
	public static final Resource	ResultSolution			= ResourceFactory
																	.createResource( resultSetVocabulary
																			+ "ResultSolution" );
	public static final Resource	ResultBinding			= ResourceFactory
																	.createResource( resultSetVocabulary
																			+ "ResultBinding" );

	public static final Property	solution				= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "solution" );

	public static final Property	pBoolean				= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "boolean" );

	public static final Property	size					= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "size" );

	public static final Property	resultVariable			= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "resultVariable" );
	public static final Property	binding					= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "binding" );

	public static final Property	variable				= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "variable" );

	public static final Property	value					= ResourceFactory
																	.createProperty( resultSetVocabulary
																			+ "value" );
}
