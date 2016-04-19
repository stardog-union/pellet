// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;

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
 */
public class TestSingleSPARQLDLQueries
{
	private final String NS = "http://example.org#";

	@Test
	public void testVariableSPO1()
	{
		// Handle variable SPO pattern. This query is executed by ARQ (fall back
		// from SPARQL-DL)

		final String q = "SELECT * WHERE { ?s ?p ?o }";

		final Resource s = ResourceFactory.createResource(NS + "i");
		final Property p = RDF.type;
		final Resource o = ResourceFactory.createResource(NS + "C");

		final Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.add(s, p, o);

		final Query query = QueryFactory.create(q);
		final QueryExecution qe = SparqlDLExecutionFactory.create(query, model);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(model.listStatements().toList().size(), rs.getRowNumber());
	}

	@Test
	public void testVariableSPO2()
	{
		// Handle variable SPO pattern. This query is executed by ARQ (fall back
		// from SPARQL-DL)

		final String q = "SELECT * WHERE { ?s ?p ?o }";

		final Resource s = ResourceFactory.createResource(NS + "i");
		final Property p = ResourceFactory.createProperty(NS + "p");
		final Resource o = ResourceFactory.createResource(NS + "C");

		final Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.add(s, p, o);

		final Query query = QueryFactory.create(q);
		final QueryExecution qe = SparqlDLExecutionFactory.create(query, model);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(model.listStatements().toList().size(), rs.getRowNumber());
	}

	@Test
	public void testVariableSPO3()
	{
		// Handle variable SPO pattern. No fall back here

		final String q = "PREFIX owl: <http://www.w3.org/2002/07/owl#> " + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "PREFIX ex: <" + NS + "> " + "SELECT * WHERE { ?s ?p ?o . ?p rdf:type owl:ObjectProperty }";

		final Resource s = ResourceFactory.createResource(NS + "i");
		final Property p = ResourceFactory.createProperty(NS + "p");
		final Resource o = ResourceFactory.createResource(NS + "j");

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.add(s, p, o);
		model.add(p, RDF.type, OWL.ObjectProperty);

		final Query query = QueryFactory.create(q);
		final QueryExecution qe = SparqlDLExecutionFactory.create(query, model);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(1, rs.getRowNumber());
	}

	@Test
	public void testVariableSPO4()
	{
		// Don't handle variable SPO pattern in the query. The result set size
		// is incomplete.

		final String q = "SELECT * WHERE { ?s ?p ?o }";

		final Resource s = ResourceFactory.createResource(NS + "i");
		final Property p = ResourceFactory.createProperty(NS + "p");
		final Resource o = ResourceFactory.createResource(NS + "C");

		final Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.add(s, p, o);

		final Query query = QueryFactory.create(q);
		final QueryExecution qe = SparqlDLExecutionFactory.create(query, DatasetFactory.create(model), null, QueryEngineType.PELLET, false);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(1, rs.getRowNumber());
	}

	@Test
	public void testDAWG1()
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("file:test/data/sparql-dawg-tests/data-r2/basic/data-1.ttl", "N3");

		final Query query = QueryFactory.read("file:test/data/sparql-dawg-tests/data-r2/basic/base-prefix-1.rq");

		final QueryExecution qe = SparqlDLExecutionFactory.create(query, model);

		final ResultSet rs = qe.execSelect();
		//ResultSetFormatter.out( rs );
		ResultSetFormatter.consume(rs);

		// Type, sameAs, one entry for each property assertion
		assertEquals(1 + 1 + 2, rs.getRowNumber());
	}

	@Test
	public void testDAWG2()
	{
		// Query PREFIX : <http://example/> SELECT * { ?s ?p ?o }
		// The same as testDAWG3 but here we handle the variable SPO pattern
		// with the SPARQL-DL engine, i.e. we fall back to ARQ as the SPARQL-DL
		// engine cannot handle it. The result set size is 20 because the
		// PelletInfGraph contains all inferred triples.

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("file:test/data/sparql-dawg-tests/data-r2/graph/data-g1.ttl", "N3");

		final Query query = QueryFactory.read("file:test/data/sparql-dawg-tests/data-r2/graph/graph-01.rq");

		final QueryExecution qe = SparqlDLExecutionFactory.create(query, model);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(model.listStatements().toList().size(), rs.getRowNumber());
	}

	@Test
	public void testDAWG3()
	{
		// Query PREFIX : <http://example/> SELECT * { ?s ?p ?o }
		// The same as testDAWG2 but this time we don't handle the variable SPO
		// pattern, i.e. we handle the pattern as a property value

		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("file:test/data/sparql-dawg-tests/data-r2/graph/data-g1.ttl", "N3");

		final Query query = QueryFactory.read("file:test/data/sparql-dawg-tests/data-r2/graph/graph-01.rq");

		final QueryExecution qe = SparqlDLExecutionFactory.create(query, DatasetFactory.create(model), null, QueryEngineType.MIXED, false);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(2, rs.getRowNumber());
	}

	@Test
	public void testDAWG4()
	{
		final Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("file:test/data/sparql-dawg-tests/data-r2/optional-filter/data-1.ttl", "N3");

		final Query query = QueryFactory.read("file:test/data/sparql-dawg-tests/data-r2/optional-filter/expr-5.rq");

		final QueryExecution qe = SparqlDLExecutionFactory.create(query, model);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(3, rs.getRowNumber());
	}

	@Test
	public void testUnsupportedBuiltin()
	{
		final OntModel ontmodel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		final String nc = "urn:test:";

		final OntClass class1 = ontmodel.createClass(nc + "C1");

		final Individual[] inds = new Individual[6];
		for (int j = 0; j < 6; j++)
			inds[j] = ontmodel.createIndividual(nc + "Ind" + j, OWL.Thing);

		ontmodel.add(class1, RDFS.subClassOf, ontmodel.createEnumeratedClass(null, ontmodel.createList(inds)));

		final Query query = QueryFactory.create("PREFIX rdf:<" + RDF.getURI() + ">\n" + "SELECT * WHERE {\n" + "  ?x rdf:first ?y .\n" + "}");
		final QueryExecution qe = SparqlDLExecutionFactory.create(query, ontmodel);

		final ResultSet rs = qe.execSelect();
		ResultSetFormatter.consume(rs);

		assertEquals(6, rs.getRowNumber());

	}

	@Test
	public void testAnnotationQueryWithClassesAndProperties()
	{
		PelletOptions.USE_ANNOTATION_SUPPORT = true;

		final Resource class1 = ResourceFactory.createResource(NS + "class1");
		final Property property1 = ResourceFactory.createProperty(NS + "property1");
		final Literal literal1 = ResourceFactory.createPlainLiteral("Annotation 1");

		final Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.add(class1, RDF.type, OWL.Class);
		model.add(class1, RDFS.label, literal1);
		model.add(property1, RDF.type, RDF.Property);
		model.add(property1, RDFS.label, literal1);

		String q;
		Query query;
		QueryExecution qe;
		ResultSet rs;

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "SELECT * WHERE { ?x rdfs:label ?y }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(2, ResultSetFormatter.consume(rs));

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "PREFIX ex:<" + NS + ">\n" + "SELECT * WHERE { ex:property1 rdfs:label ?y }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(1, ResultSetFormatter.consume(rs));

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "PREFIX ex:<" + NS + ">\n" + "SELECT * WHERE { ?x rdfs:label \"Annotation 1\" }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(2, ResultSetFormatter.consume(rs));
	}

	@Test
	public void testAnnotationQueryWithGroundAtoms()
	{
		PelletOptions.USE_ANNOTATION_SUPPORT = true;

		final Resource class1 = ResourceFactory.createResource(NS + "class1");
		final Literal literal1 = ResourceFactory.createPlainLiteral("Annotation 1");

		final Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.add(class1, RDF.type, OWL.Class);
		model.add(class1, RDFS.label, literal1);

		String q;
		Query query;
		QueryExecution qe;
		ResultSet rs;

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "SELECT * WHERE { ?x rdfs:label ?y }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(1, ResultSetFormatter.consume(rs));

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "SELECT * WHERE { <" + NS + "class1> rdfs:label \"Annotation 1\" }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(1, ResultSetFormatter.consume(rs));

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "SELECT * WHERE { <" + NS + "class1> rdfs:label \"Random Annotation\" }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(0, ResultSetFormatter.consume(rs));

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "SELECT * WHERE { ?x rdfs:label \"Annotation 1\" }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(1, ResultSetFormatter.consume(rs));

		q = "PREFIX rdfs:<" + RDFS.getURI() + ">\n" + "SELECT * WHERE { ?x rdfs:label \"Random Annotation \" }";
		query = QueryFactory.create(q);
		qe = SparqlDLExecutionFactory.createPelletExecution(query, model);
		rs = qe.execSelect();
		assertEquals(0, ResultSetFormatter.consume(rs));

	}

}
