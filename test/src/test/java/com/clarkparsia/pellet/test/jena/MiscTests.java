/**
 * 
 */
package com.clarkparsia.pellet.test.jena;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL2;

/**
 * @author Pavel Klinov
 *
 */
public class MiscTests {

	public static void configurePelletOptions() {
		PelletOptions.PROCESS_JENA_UPDATES_INCREMENTALLY = false;
		PelletOptions.ALWAYS_REBUILD_RETE = false;
		PelletOptions.USE_UNIQUE_NAME_ASSUMPTION = true;
		PelletOptions.USE_COMPLETION_QUEUE = false;
		PelletOptions.AUTO_REALIZE = false;
	}

	private OntModel model;

	@Test
	public void dataAssertionTest() throws FileNotFoundException {
		model.read(
				this.getClass().getClassLoader()
						.getResourceAsStream("test/data/misc/decimal-int.owl"), null);
		final Individual entity = model
				.getIndividual("http://www.inmindcomputing.com/example/dataAssertion.owl#ENTITY");
		final DatatypeProperty value = model
				.getDatatypeProperty("http://www.inmindcomputing.com/example/dataAssertion.owl#dataAssertionValue");
		Assert.assertTrue(value.isFunctionalProperty());
		Assert.assertEquals(1, entity.listPropertyValues(value).toSet().size());
	}

	@Before
	public void setUp() throws Exception {
		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		configurePelletOptions();
	}

	/**
	 * @throws java.lang.Exception
	 *
	 */
	@After
	public void tearDown() throws Exception {
		model.close();
	}

	@Test
	public void universalTest() throws FileNotFoundException {
		model.read(
				this.getClass().getClassLoader()
						.getResourceAsStream("test/data/misc/universal-property.owl"), null);
		final ObjectProperty universal = model
				.getObjectProperty("http://www.inmindcomputing.com/example/universal.owl#universalProperty");
		final ObjectProperty abstracT = model
				.getObjectProperty("http://www.inmindcomputing.com/example/universal.owl#abstractProperty");
		final ObjectProperty concrete = model
				.getObjectProperty("http://www.inmindcomputing.com/example/universal.owl#concreteProperty");
		Assert.assertTrue(universal.getEquivalentProperty().equals(
				OWL2.topObjectProperty));
		Assert.assertTrue(universal.listSubProperties().toSet()
				.contains(abstracT));
		Assert.assertTrue(universal.listSubProperties().toSet()
				.contains(concrete));
	}

}
