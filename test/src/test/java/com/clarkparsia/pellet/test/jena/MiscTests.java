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
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL2;

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
    model.read(this.getClass().getClassLoader().getResourceAsStream("test/data/misc/decimal-int.owl"), null);
    final Individual entity = model.getIndividual("http://www.inmindcomputing.com/example/dataAssertion.owl#ENTITY");
    final DatatypeProperty value =
        model.getDatatypeProperty("http://www.inmindcomputing.com/example/dataAssertion.owl#dataAssertionValue");
    Assert.assertTrue(value.isFunctionalProperty());
    Assert.assertEquals(1, entity.listPropertyValues(value).toSet().size());
  }

  @Test
  public void incrementalDeletionTest() throws FileNotFoundException {
    final Individual entity =
        model.createIndividual("http://www.inmindcomputing.com/example/dataAssertion.owl#ENTITY", null);
    final DatatypeProperty property =
        model.createDatatypeProperty("http://www.inmindcomputing.com/example/dataAssertion.owl#ENTITY", true);

    final Statement firstValue = model.createLiteralStatement(entity, property, "1");
    final Statement secondValue = model.createLiteralStatement(entity, property, "2");

    // TODO: prepare registers PelletGraphListener. This implies a different behaviour of the whole graph before and
    // after the first read operation. Please, delete comment if this is as designed.
    model.prepare();
    model.add(firstValue);
    model.remove(firstValue);
    model.add(secondValue);

    try {
      model.listObjectsOfProperty(property).toSet();
    } catch (final InconsistentOntologyException e) {
      Assert.fail("Both values are contained in the knowledge base.");
    }

  }

  @Before
  public void setUp() throws Exception {
    model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
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
    model.read(this.getClass().getClassLoader().getResourceAsStream("test/data/misc/universal-property.owl"), null);
    final ObjectProperty universal =
        model.getObjectProperty("http://www.inmindcomputing.com/example/universal.owl#universalProperty");
    final ObjectProperty abstracT =
        model.getObjectProperty("http://www.inmindcomputing.com/example/universal.owl#abstractProperty");
    final ObjectProperty concrete =
        model.getObjectProperty("http://www.inmindcomputing.com/example/universal.owl#concreteProperty");
    Assert.assertTrue(universal.getEquivalentProperty().equals(OWL2.topObjectProperty));
    Assert.assertTrue(universal.listSubProperties().toSet().contains(abstracT));
    Assert.assertTrue(universal.listSubProperties().toSet().contains(concrete));
  }

}
