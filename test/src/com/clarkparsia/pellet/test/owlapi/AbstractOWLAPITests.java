// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.owlapi;

import static com.clarkparsia.owlapiv3.OWL.AnonymousIndividual;
import static com.clarkparsia.owlapiv3.OWL.Class;
import static com.clarkparsia.owlapiv3.OWL.DataProperty;
import static com.clarkparsia.owlapiv3.OWL.Individual;
import static com.clarkparsia.owlapiv3.OWL.ObjectProperty;
import static com.clarkparsia.owlapiv3.OWL.constant;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.mindswap.pellet.test.PelletTestSuite;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class AbstractOWLAPITests {
	public static String	base	= "file:" + PelletTestSuite.base + "misc/";

	protected static final OWLClass					A		= Class( "A" );
	protected static final OWLClass					B		= Class( "B" );
	protected static final OWLClass					C		= Class( "C" );
	protected static final OWLClass					D		= Class( "D" );
	protected static final OWLClass					E		= Class( "E" );
	protected static final OWLClass					F		= Class( "F" );
	protected static final OWLObjectProperty		p		= ObjectProperty( "p" );
	protected static final OWLObjectProperty		q		= ObjectProperty( "q" );
	protected static final OWLObjectProperty		r		= ObjectProperty( "r" );
	protected static final OWLDataProperty			dp		= DataProperty( "dp" );
	protected static final OWLDataProperty			dq		= DataProperty( "dq" );
	protected static final OWLDataProperty			dr		= DataProperty( "dr" );
	protected static final OWLNamedIndividual		a		= Individual( "a" );
	protected static final OWLNamedIndividual		b		= Individual( "b" );
	protected static final OWLNamedIndividual		c		= Individual( "c" );
	protected static final OWLAnonymousIndividual	anon	= AnonymousIndividual();
	protected static final OWLLiteral				lit		= constant( "lit" );

	
	protected OWLOntology ontology;
	protected PelletReasoner reasoner;
	
	public void createReasoner(OWLAxiom... axioms) {		
		ontology = OWL.Ontology( axioms );
		reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
	}

	@Before
	@After
	public void resetOntologyManager() {
		ontology = null;
		if( reasoner != null )
			reasoner.dispose();
		
		for( OWLOntology o : OWL.manager.getOntologies() ) {
			OWL.manager.removeOntology( o );
		}
	}
	
	protected boolean processAdd(OWLAxiom axiom) {
		return processChange( new AddAxiom( ontology, axiom ) );
	}
	
	protected boolean processRemove(OWLAxiom axiom) {
		return processChange( new RemoveAxiom( ontology, axiom ) );	
	}
	
	protected boolean processChange(OWLOntologyChange change) {
		return reasoner.processChanges( Collections.singletonList( change ) );
	}


}
