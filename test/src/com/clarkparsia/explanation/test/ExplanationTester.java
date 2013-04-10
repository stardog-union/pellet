// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDescription;

import com.clarkparsia.explanation.ExplanationGenerator;
import com.clarkparsia.explanation.SatisfiabilityConverter;
import com.clarkparsia.explanation.io.ConciseExplanationRenderer;
import com.clarkparsia.owlapi.OntologyUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class ExplanationTester {
	private static final Logger			log			= Logger.getLogger( ExplanationTester.class.getName() );

	private SatisfiabilityConverter		converter;
	private ConciseExplanationRenderer	renderer;
	private ExplanationGenerator		expGen;

	private int							axiomCount	= 0;

	public ExplanationTester(ExplanationGenerator expGen) {
		this.expGen = expGen;

		converter = new SatisfiabilityConverter(OntologyUtils.getOWLOntologyManager().getOWLDataFactory());
		renderer = new ConciseExplanationRenderer();
	}

	public void testExplanations(OWLAxiom axiom, int max, Set<Set<OWLAxiom>> expectedExplanations)
			throws Exception {
		OWLDescription unsatClass = converter.convert( axiom );

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Axiom " + (++axiomCount) + ": " + axiom + " Expecting "
					+ expectedExplanations.size() + " explanations" );

		Set<Set<OWLAxiom>> generatedExplanations = expGen.getExplanations( unsatClass, max );
		Set<Set<OWLAxiom>> notFoundExplanations = new HashSet<Set<OWLAxiom>>( expectedExplanations );

		if( log.isLoggable( Level.FINER ) ) {
			StringWriter sw = new StringWriter();
			renderer.startRendering( sw );
			renderer.render( axiom, expectedExplanations );
			renderer.endRendering();
			log.finer( "Expected:\n" + sw );
		}

		assertNotNull( "Axiom " + axiom + " not entailed", generatedExplanations );

		Set<Set<OWLAxiom>> unexpectedExplanations = new HashSet<Set<OWLAxiom>>();
		for( Set<OWLAxiom> explanation : generatedExplanations ) {
			if( !notFoundExplanations.remove( explanation ) )
				unexpectedExplanations.add( explanation );
		}

		if( !notFoundExplanations.isEmpty() || !unexpectedExplanations.isEmpty() ) {
			StringWriter sw = new StringWriter();
			ConciseExplanationRenderer renderer = new ConciseExplanationRenderer();
			renderer.startRendering( sw );
			sw.getBuffer().append( "\nExpected:\n" );
			renderer.render( axiom, expectedExplanations );
			if( !notFoundExplanations.isEmpty() ) {
				sw.getBuffer().append( "Not Found:\n" );
				renderer.render( axiom, notFoundExplanations );
			}
			if( !unexpectedExplanations.isEmpty() ) {
				sw.getBuffer().append( "Unexpected:\n" );
				renderer.render( axiom, unexpectedExplanations );
			}
			renderer.endRendering();
			
			log.severe( "Error in explanation: " + sw );
			
			org.junit.Assert.fail( "Error in explanation, see the log file for details" );
		}
	}
}
