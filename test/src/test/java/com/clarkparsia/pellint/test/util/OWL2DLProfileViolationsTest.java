package com.clarkparsia.pellint.test.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;

import com.clarkparsia.owlapiv3.OWL;

/**
 * 
 * Tests for OWL2 Detection. The tests below call directly the OWL2 DL profile detection from OWLAPI, since
 * this is what Pellint uses to display the violations.
 * 
 * @author Blazej Bulka <blazej@clarkparsia.com>
 *
 */
public class OWL2DLProfileViolationsTest {

	@Test
	public void testCorrectOntology() throws OWLOntologyCreationException {
		OWLOntology ontology = OWL.manager.loadOntologyFromOntologyDocument(new File("test/data/misc/agencies.owl"));
		
		try {
			OWL2DLProfile owl2Profile = new OWL2DLProfile();
			assertTrue( owl2Profile.checkOntology( ontology ).isInProfile() );			
		}
		finally {
			OWL.manager.removeOntology(ontology);
		}
	}
	
	@Test
	public void testSuperPropertyTopDataProperty() throws OWLOntologyCreationException {
		
		OWLOntology ontology = OWL.manager.createOntology();
		
		try {
			OWLDataProperty property = OWL.factory.getOWLDataProperty(IRI.create("tag:clarkparsia.com,2008:pellint:test:superProperty"));
			OWLDataProperty topProperty = OWL.factory.getOWLTopDataProperty();
			
			OWLAxiom axiom = OWL.factory.getOWLSubDataPropertyOfAxiom(topProperty, property);
			
			OWL.manager.addAxiom(ontology, axiom);
			
			OWL2DLProfile owl2Profile = new OWL2DLProfile();
			assertFalse( owl2Profile.checkOntology( ontology ).isInProfile() );
		}
		finally {
			OWL.manager.removeOntology(ontology);
		}			
	}
	
	@Test
	public void testInvalidTransitivity() throws OWLOntologyCreationException {
		OWLOntology ontology = OWL.manager.loadOntologyFromOntologyDocument(new File("test/data/misc/invalidTransitivity.owl"));
		
		try {
			OWL2DLProfile owl2Profile = new OWL2DLProfile();
			assertFalse( owl2Profile.checkOntology( ontology ).isInProfile() );			
		}
		finally {
			OWL.manager.removeOntology(ontology);
		}
	}
}
