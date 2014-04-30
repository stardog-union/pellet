package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.test.PelletTestSuite;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> <br/>
 * Created: Jun 3, 2009 9:43:57 AM <br/>
 *
 * @author Michael Grove <mike@clarkparsia.com>
 */
public class TestMiscSPARQL {

	@Test
	public void test() {
		// test case for the bug reported in #247

		boolean uas = PelletOptions.USE_ANNOTATION_SUPPORT;
		PelletOptions.USE_ANNOTATION_SUPPORT = false;

		try {
			String aOnt = "file:" + PelletTestSuite.base + "misc/ticket-247-test-case.rdf";
	
			PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();
	
			InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );
	
			aModel.read( aOnt );
	
			String aQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
							"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
							"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
							"SELECT ?x WHERE { ?y rdfs:comment ?x . }";
	
			QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );
	
			// this should not produce an NPE
			qe.execSelect();
		}
		finally {
			PelletOptions.USE_ANNOTATION_SUPPORT = uas;
		}
	}

	@Test
	public void testUndefinedVarInProjection() {
		// Test case for the bug reproted in #277
		String aOnt = "file:" + PelletTestSuite.base + "misc/ticket-277-test-case.rdf";

		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();

		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read( aOnt );

		String aQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
						"SELECT ?y ?foo WHERE { ?y rdf:type ?x . }";

		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

		ResultSet aResults = qe.execSelect();

		// you should be able to iterate over the results w/o an NPE
		while( aResults.hasNext() ) {
			Binding aBinding = aResults.nextBinding();
			Iterator<?> aVarIter = aBinding.vars();

			while( aVarIter.hasNext() ) {
				Var aVar = (Var) aVarIter.next();
				aBinding.get( aVar );
			}
		}

		aQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
						"SELECT ?foo WHERE { ?y rdf:type ?x . }";

		qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

		aResults = qe.execSelect();

		// in this case, since the query var that was undefined was the only thing in the projection,
		// you shouldn't get results with any bindings
		while( aResults.hasNext() ) {
			Binding aBinding = aResults.nextBinding();

			assertFalse( aBinding.vars().hasNext() );
		}
	}

	@Test
	public void testSizeEstimateNPE() {
		boolean savedValue = PelletOptions.USE_ANNOTATION_SUPPORT;
		
		for( boolean b : new boolean[] { false, true } ) {
			PelletOptions.USE_ANNOTATION_SUPPORT = b;
			
			String aOnt = "file:" + PelletTestSuite.base + "misc/ticket-276-test-case.rdf";

			// Test case for #276
			PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();

			InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

			aModel.read( aOnt );

			String aQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
							"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
							"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
							"SELECT ?x WHERE { ?y rdf:type owl:Thing . ?y rdfs:comment ?x . }";

			QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

			// this will throw a NPE, but should not
			ResultSet aResults = qe.execSelect();

			// there should be some results
			assertTrue( aResults.hasNext() );
		
		}
			
		PelletOptions.USE_ANNOTATION_SUPPORT = savedValue;
	}

	@Test
	public void test248() {
		// Test case for #248
		String aOnt = "file:" + PelletTestSuite.base + "misc/pizza.owl";

		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();

		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read( aOnt );

		String aQuery = "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
						"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
						"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
						"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
						"PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>\n" +
						"SELECT ?v0 WHERE\n" +
						"{\n" +
						" ?v0 rdf:type ?v1.\n" +
						" ?v1 rdf:type owl:Restriction.\n" +
						" ?v1 owl:onProperty pizza:hasTopping.\n" +
						" ?v1 owl:allValuesFrom ?v2.\n" +
						"}";

		// this should not thrown an InternalReasonerException, we want the unsupported query to be detected, and
		// the execution to fallback to using the mixed evaluator

		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

		qe.execSelect();
	}
	
	@Test
	public void test196() {
		// Test case for #196
		String aOnt = "file:" + PelletTestSuite.base + "misc/pizza.owl";

		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();

		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read( aOnt );

		String aQuery = "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
						"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
						"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
						"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
						"PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>\n" +
						"SELECT ?v0 WHERE\n" +
						"{\n" +
						" ?v0 ?v1 owl:ObjectProperty.\n" +
						"}";

		// this should not thrown an InternalReasonerException, we want the unsupported query to be detected, and
		// the execution to fallback to using the mixed evaluator
		
		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

		qe.execSelect();
	}

	@Ignore("According to latest OWL 2 spec onDatatype property can only point to named datatypes so the input " +
			"ontology for this test is not valid anymore")
	@Test
	public void test306() {
		String aOnt = "file:" + PelletTestSuite.base + "misc/longitude.ttl";
		
		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();
		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read( aOnt, "N3" );
		String aQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX mon: <http://www.semwebtech.org/mondial/10/meta#>\n"
				+ "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n" 
				+ "PREFIX : <foo://bla/>\n"
				+ "SELECT ?v0 WHERE\n" + "{ ?v0 rdf:type :EasternHemispherePlace. }\n";
		
		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

		Resource berlin = aModel.getResource( "foo://bla/Berlin" );
		ResultSet results = qe.execSelect();
		assertTrue( results.hasNext() );
		assertTrue( berlin.equals( results.next().getResource("v0") ) );
		assertFalse( results.hasNext() );
	}
	
	@Test
	public void test253() {
		String aOnt = "file:" + PelletTestSuite.base + "misc/longitude2.ttl";
		
		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();
		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read( aOnt, "N3" );
		String aQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX mon: <http://www.semwebtech.org/mondial/10/meta#>\n"
				+ "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n" 
				+ "PREFIX : <foo://bla/>\n"
				+ "SELECT ?v0 WHERE\n" + "{ ?v0 rdf:type :EasternHemispherePlace. }\n";
		
		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );

		Resource berlin = aModel.getResource( "foo://bla/Berlin" );
		ResultSet results = qe.execSelect();
		assertTrue( results.hasNext() );
		assertTrue( berlin.equals( results.next().getResource("v0") ) );
		assertFalse( results.hasNext() );
	}
	
	@Test
	public void test210() {
		Object prevValue = ARQ.getContext().get(ARQ.optFilterPlacement);
		try {
			ARQ.getContext().set(ARQ.optFilterPlacement, false);
			
			String aOnt = "file:" + PelletTestSuite.base + "misc/ticket-210-test-case.owl";
			
			PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();
			InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );
	
			aModel.read(aOnt);
			String aQuery = "PREFIX : <http://www.semanticweb.org/ontologies/2010/5/ticket-210-test-case.owl#>\n"		
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "SELECT ?x\n"
				+ "WHERE {\n"
				+ 	"?x rdfs:subClassOf [\n"
				+ 		"owl:onProperty :R ;\n"
				+ 		"owl:someValuesFrom :C\n"
				+ 	"]\n"
				+ "FILTER( ?x != owl:Nothing )\n"
				+ "}";
			
			QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );
			
			ResultSet results = qe.execSelect();
			
			while(results.hasNext()){
				results.next();
			}
			
			
			assertEquals(2, results.getRowNumber());
		}
		finally  {
			ARQ.getContext().set(ARQ.optFilterPlacement, prevValue);
		}
	}
	
	@Test
	public void test421() {
		String aOnt = "file:" + PelletTestSuite.base + "misc/ticket-421-test-case.owl";
		
		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();
		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read(aOnt);
		String aQuery = "PREFIX : <http://www.semanticweb.org/ontologies/2010/5/ticket-421-test-case.owl#>\n"		
			+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "SELECT ?x\n"
			+ "WHERE {\n"
			+ 	"?x rdf:type :D\n"
			+ "}";
		
		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );
		
		ResultSet results = qe.execSelect();
		
//		ResultSetFormatter.out(results);

		while(results.hasNext()){
			results.next();
		}
		
		assertTrue( results.getRowNumber() == 1 );
	}
	
	@Test
	public void test444() {
		String aOnt = "file:" + PelletTestSuite.base + "misc/ticket-444-test-case.owl";
		
		PelletReasoner aReasoner = (PelletReasoner) PelletReasonerFactory.theInstance().create();
		InfModel aModel = ModelFactory.createInfModel( aReasoner, ModelFactory.createDefaultModel() );

		aModel.read(aOnt);
		String aQuery = "PREFIX : <http://www.semanticweb.org/ontologies/2010/5/ticket-444-test-case.owl#>\n"		
			+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
			+ "SELECT *\n"
			+ "WHERE {\n"
			+ 	":c ?p ?o . OPTIONAL { ?o :hasName ?n }\n"
			+ "}";
				
		QueryExecution qe = QueryExecutionFactory.create( QueryFactory.create(aQuery), aModel );
		
//		QueryExecution qe = SparqlDLExecutionFactory.create( QueryFactory.create(aQuery), aModel );
		
		ResultSet results = qe.execSelect();		
//		ResultSetFormatter.out(results);

		while(results.hasNext()){
			results.next();
		}
		
		assertTrue( results.getRowNumber() == 4 );
	}
}
