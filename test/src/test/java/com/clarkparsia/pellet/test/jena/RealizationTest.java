/**
 *
 */
package com.clarkparsia.pellet.test.jena;

import java.io.IOException;
import java.io.InputStream;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * A simple check to see when classification and realization are triggered by ABox changes.
 *
 * @author Pavel Klinov
 */
public class RealizationTest
{

	private static final String ONTOLOGY_PATH_ = "/test/data/misc/jena-datatypes.owl";

	private static final String PREFIX = "http://example.org/";

	@Test
	public void testDoNotReclassify() throws Exception
	{
		final OntModel ontModel = loadOntologyModel(ONTOLOGY_PATH_);

		ontModel.setStrictMode(false);
		// force classification and realization
		((PelletInfGraph) ontModel.getGraph()).realize();

		final Individual x = ontModel.getIndividual(PREFIX + "x");

		System.err.println(x.listOntClasses(false).toList());

		// add some individual assertion, this will trigger realization for the
		// entire ABox but not classification because there're no nominals in
		// the ontology
		ontModel.add(ontModel.createLiteralStatement(ontModel.createResource(PREFIX + "y"), ontModel.getProperty(PREFIX + "p"), 5));

		final Individual y = ontModel.getIndividual(PREFIX + "y");

		System.err.println(y.listOntClasses(false).toList());

		// and another one but disabling auto-realization this time. Only "z" will be realized.
		PelletOptions.AUTO_REALIZE = false;

		ontModel.add(ontModel.createLiteralStatement(ontModel.createResource(PREFIX + "z"), ontModel.getProperty(PREFIX + "p"), 15));

		final Individual z = ontModel.getIndividual(PREFIX + "z");

		System.err.println(z.listOntClasses(false).toList());
	}

	private OntModel loadOntologyModel(final String ontologyPath) throws IOException
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		// read the file
		try (InputStream ontStream = ConcurrencyTest.class.getResourceAsStream(ontologyPath))
		{
			model.read(ontStream, null, "TTL");
		}

		return model;
	}
}
