// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.net.URI;

import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLOntologyInputSource;
import org.semanticweb.owl.io.PhysicalURIInputSource;
import org.semanticweb.owl.io.StringInputSource;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.OWLOntologyURIMapperImpl;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OWLAPIWebOntTester implements WebOntTester {
	OWLOntologyManager			manager;
	Reasoner					reasoner;
	OWLOntologyURIMapperImpl	mapper;

	public OWLAPIWebOntTester() {
		manager = OWLManager.createOWLOntologyManager();
		mapper = new OWLOntologyURIMapperImpl();
	}

	public void classify() {
		reasoner.getKB().realize();
	}

	public boolean isConsistent() {
		return reasoner.isConsistent();
	}

	public void testEntailment(String entailmentFileURI, boolean positiveEntailment) {
		try {
			OWLOntology ont = manager.loadOntology( getInputSource( entailmentFileURI ) );
			for( OWLAxiom axiom : ont.getLogicalAxioms() ) {
				if( !reasoner.isEntailed( axiom ) ) {
					assertFalse( "Entailment failed for " + axiom, positiveEntailment );
					return;
				}
			}
			
			assertTrue( "All axioms entailed in negative entailment test", positiveEntailment );
		} catch( OWLException e ) {
			throw new RuntimeException( e );
		}
	}

	public void setInputOntology(String inputFileURI) {
		try {
			manager.addURIMapper( mapper );
			OWLOntology ont = manager.loadOntology( getInputSource( inputFileURI ) );
			reasoner = new Reasoner( manager );
			reasoner.setOntology( ont );
		} catch( OWLException e ) {
			throw new RuntimeException( e );
		}
	}

	public void setTimeout(long timeout) {
		reasoner.getKB().setTimeout( timeout );
	}

	public void registerURIMapping(String fromURI, String toURI) {
		mapper.addMapping( URI.create( fromURI ), URI.create( toURI ) );
	}

	private OWLOntologyInputSource getInputSource(String fileURI) {
		if( fileURI.endsWith( ".n3" ) )
			return convertN3( fileURI );
		return new PhysicalURIInputSource( URI.create( fileURI ) );
	}

	private OWLOntologyInputSource convertN3(String fileURI) {
		StringWriter ontologySrc = new StringWriter();
		Model model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		model.read( fileURI, fileURI, "Turtle" );
		model.write( ontologySrc, "RDF/XML" );
		return new StringInputSource( ontologySrc.toString() );
	}

}
