/**
 * 
 */
package com.clarkparsia.pellet.test.jena;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * A simple check to see when classification and realization are triggered by
 * ABox changes.
 * 
 * @author Pavel Klinov
 *
 */
public class RealizationTest {

	private static final String ONTOLOGY_PATH_ = "/test/data/misc/jena-datatypes.owl";
	
	private static final String PREFIX = "http://example.org/";
	
	@Test
	public void testDoNotReclassify() throws Exception {
		OntModel ontModel = loadOntologyModel(ONTOLOGY_PATH_);
		
		ontModel.setStrictMode(false);
		// force classification and realization
		((PelletInfGraph) ontModel.getGraph()).realize();
		
		Individual x = ontModel.getIndividual(PREFIX + "x");
		
		System.err.println(x.listOntClasses(false).toList());
		
		// add some individual assertion, this will trigger realization for the
		// entire ABox but not classification because there're no nominals in
		// the ontology
		ontModel.add(ontModel.createLiteralStatement(ontModel.createResource(PREFIX + "y"), ontModel.getProperty(PREFIX + "p"), 5));
		
		Individual y = ontModel.getIndividual(PREFIX + "y");
		
		System.err.println(y.listOntClasses(false).toList());
		
		// and another one but disabling auto-realization this time. Only "z" will be realized.
		PelletOptions.AUTO_REALIZE = false;
		
		ontModel.add(ontModel.createLiteralStatement(ontModel.createResource(PREFIX + "z"), ontModel.getProperty(PREFIX + "p"), 15));
		
		Individual z = ontModel.getIndividual(PREFIX + "z");
		
		System.err.println(z.listOntClasses(false).toList());
	}
	
	private OntModel loadOntologyModel(String ontologyPath) throws IOException {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
        InputStream ontStream = null;
		// read the file
        try {
			ontStream = ConcurrencyTest.class.getResourceAsStream(ontologyPath);
			
			model.read( ontStream, null, "TTL" );
		} finally {
			if (ontStream != null) {
				ontStream.close();
			}
		}
        
		return model;
	}
}
