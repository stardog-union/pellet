package com.clarkparsia.pellint.test.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import java.io.File;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;

/**
 * Tests for OWL2 Detection. The tests below call directly the OWL2 DL profile detection from OWLAPI, since this is what Pellint uses to display the violations.
 *
 * @author Blazej Bulka <blazej@clarkparsia.com>
 */
public class OWL2DLProfileViolationsTest
{

	@Test
	public void testCorrectOntology() throws OWLOntologyCreationException
	{
		final OWLOntology ontology = OWL._manager.loadOntologyFromOntologyDocument(new File("test/data/misc/agencies.owl"));

		try
		{
			final OWL2DLProfile owl2Profile = new OWL2DLProfile();
			assertTrue(owl2Profile.checkOntology(ontology).isInProfile());
		}
		finally
		{
			OWL._manager.removeOntology(ontology);
		}
	}

	@Test
	public void testSuperPropertyTopDataProperty() throws OWLOntologyCreationException
	{

		final OWLOntology ontology = OWL._manager.createOntology();

		try
		{
			final OWLDataProperty property = OWL._factory.getOWLDataProperty(IRI.create("tag:clarkparsia.com,2008:pellint:test:superProperty"));
			final OWLDataProperty topProperty = OWL._factory.getOWLTopDataProperty();

			final OWLAxiom axiom = OWL._factory.getOWLSubDataPropertyOfAxiom(topProperty, property);

			OWL._manager.addAxiom(ontology, axiom);

			final OWL2DLProfile owl2Profile = new OWL2DLProfile();
			assertFalse(owl2Profile.checkOntology(ontology).isInProfile());
		}
		finally
		{
			OWL._manager.removeOntology(ontology);
		}
	}

	@Test
	public void testInvalidTransitivity() throws OWLOntologyCreationException
	{
		final OWLOntology ontology = OWL._manager.loadOntologyFromOntologyDocument(new File("test/data/misc/invalidTransitivity.owl"));

		try
		{
			final OWL2DLProfile owl2Profile = new OWL2DLProfile();
			assertFalse(owl2Profile.checkOntology(ontology).isInProfile());
		}
		finally
		{
			OWL._manager.removeOntology(ontology);
		}
	}
}
