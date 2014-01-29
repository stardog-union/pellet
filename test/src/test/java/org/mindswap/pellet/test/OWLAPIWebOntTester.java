// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyIRIMapperImpl;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class OWLAPIWebOntTester implements WebOntTester {
	OWLOntologyManager			manager;
	PelletReasoner				reasoner;
	OWLOntologyIRIMapperImpl	mapper;

	public OWLAPIWebOntTester() {
		manager = OWL.manager;
		mapper = new OWLOntologyIRIMapperImpl();
	}

	public void classify() {
		reasoner.getKB().realize();
	}

	public boolean isConsistent() {
		return reasoner.isConsistent();
	}

	public void testEntailment(String entailmentFileURI, boolean positiveEntailment) {
		try {
			OWLOntology ont = manager.loadOntology( IRI.create( entailmentFileURI ) );
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
		OntologyUtils.clearOWLOntologyManager();
		OWLOntology ont = null;
		try {
			manager.addIRIMapper( mapper );
			ont = manager.loadOntology( IRI.create(inputFileURI) );
			reasoner = PelletReasonerFactory.getInstance().createReasoner(ont);
		} catch( OWLException e ) {
			throw new RuntimeException( e );
		}
		finally {
			if (ont != null) {
				manager.removeOntology(ont);
			}
		}
	}

	public void setTimeout(long timeout) {
		reasoner.getKB().setTimeout( timeout );
	}

	public void registerURIMapping(String fromURI, String toURI) {
		mapper.addMapping( IRI.create( fromURI ), IRI.create( toURI ) );
	}

}
