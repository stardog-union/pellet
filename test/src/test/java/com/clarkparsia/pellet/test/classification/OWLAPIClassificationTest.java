// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * 
 * @author Evren Sirin
 */
public class OWLAPIClassificationTest extends AbstractClassificationTest {
	public void testClassification(String inputOnt, String classifiedOnt) throws OWLOntologyCreationException {
		OWLOntology premise = OWL.manager.loadOntology( IRI.create( inputOnt ) );
		OWLOntology conclusion = OWL.manager.loadOntology( IRI.create( classifiedOnt ) );
		
		try {
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( premise );
			reasoner.getKB().classify();
	
			List<OWLAxiom> nonEntailments = new ArrayList<OWLAxiom>();
					
			for( OWLSubClassOfAxiom axiom : conclusion.getAxioms( AxiomType.SUBCLASS_OF ) ) {
				boolean entailed = reasoner.getSubClasses( axiom.getSuperClass(), true ).containsEntity( (OWLClass) axiom.getSubClass() );
				
				if( !entailed ) {
					if( AbstractClassificationTest.FAIL_AT_FIRST_ERROR )
						fail( "Not entailed: " + axiom );
					else
						nonEntailments.add( axiom );
				}
			}
	
			for( OWLEquivalentClassesAxiom axiom : conclusion
					.getAxioms( AxiomType.EQUIVALENT_CLASSES ) ) {
				boolean entailed = reasoner.isEntailed( axiom );
				
				if( !entailed ) {
					if( AbstractClassificationTest.FAIL_AT_FIRST_ERROR )
						fail( "Not entailed: " + axiom );
					else
						nonEntailments.add( axiom );
				}
			}
			
			for( OWLClassAssertionAxiom axiom : conclusion.getAxioms( AxiomType.CLASS_ASSERTION ) ) {
				boolean entailed = reasoner.getInstances( axiom.getClassExpression(), true ).containsEntity( (OWLNamedIndividual) axiom.getIndividual() );
				
				if( !entailed ) {
					if( AbstractClassificationTest.FAIL_AT_FIRST_ERROR )
						fail( "Not entailed: " + axiom );
					else
						nonEntailments.add( axiom );
				}
			}
			
			assertTrue( nonEntailments.size() + " " +nonEntailments.toString(), nonEntailments.isEmpty() );
		}
		finally {
			OWL.manager.removeOntology( premise );
			OWL.manager.removeOntology( conclusion );
		}
	}

}
