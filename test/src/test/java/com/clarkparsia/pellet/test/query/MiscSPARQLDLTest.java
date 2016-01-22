package com.clarkparsia.pellet.test.query;

import com.clarkparsia.jena.test.ResourceImportLoader;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * 
 * @author Pavel Klinov
 *
 */
public class MiscSPARQLDLTest
{

	@Test
	public void testWineNonDistinguished() throws IOException
	{
		final String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + "PREFIX food: <http://www.w3.org/2001/sw/WebOnt/guide-src/food#> \n" + "PREFIX wine: <http://www.w3.org/2001/sw/WebOnt/guide-src/wine#> \n" +

		"SELECT ?Meal ?WineColor \n" + "WHERE {\n " + "?Meal rdf:type food:MealCourse . \n" + "?Meal food:hasDrink _:Wine . \n" + "_:Wine wine:hasColor ?WineColor }";
		//String ontologyFile = "/test/data/misc/food2.owl";
		final String ontologyFile = "/test/data/sparqldl-tests/simple/wine.rdf";

		PelletOptions.TREAT_ALL_VARS_DISTINGUISHED = false;

		try (InputStream ontologyStream = this.getClass().getResourceAsStream(ontologyFile))
		{
			// First create a Jena ontology model backed by the Pellet reasoner
			// (note, the Pellet reasoner is required)
			OntDocumentManager.getInstance().setReadFailureHandler(new ResourceImportLoader());

			final OntModel m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

			// Then read the data from the file into the ontology model
			m.read(ontologyStream, "http://test.com/");

			m.prepare();

			final Query q = QueryFactory.create(query);

			// Create a SPARQL-DL query execution for the given query and
			// ontology model
			final QueryExecution qe = SparqlDLExecutionFactory.create(q, DatasetFactory.create(m), null, QueryEngineType.PELLET, false);

			// We want to execute a SELECT query, do it, and return the result set
			final ResultSet rs = qe.execSelect();

			//ResultSetFormatter.out( rs );

			assertNumberOfResults(2, rs);
		}
	}

	private void assertNumberOfResults(int expected, ResultSet rs)
	{
		int cnt = 0;

		while (rs.hasNext())
		{
			rs.next();
			cnt++;
		}

		Assert.assertEquals(expected, cnt);
	}

}
