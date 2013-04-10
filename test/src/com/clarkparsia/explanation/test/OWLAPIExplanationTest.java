// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.GlassBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;
import com.clarkparsia.owlapi.explanation.TransactionAwareSingleExpGen;
import com.clarkparsia.owlapi.explanation.io.ConciseExplanationRenderer;
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
@RunWith(Parameterized.class)
public class OWLAPIExplanationTest extends AbstractExplanationTest {
	private static final Logger	log	= Logger.getLogger( JenaExplanationTest.class.getName() );

	private PelletReasoner				reasoner;
	private boolean						useGlassBox;

	private SatisfiabilityConverter		converter;
	private ConciseExplanationRenderer	renderer;
	private HSTExplanationGenerator		expGen;

	private int							axiomCount	= 0;

	@Parameters
	public static Collection<Object[]> getParameters() {
		Collection<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add( new Object[] { true, false } );
		parameters.add( new Object[] { true, true } );
		parameters.add( new Object[] { false, false } );
		parameters.add( new Object[] { false, true } );
		return parameters;
	}

	public OWLAPIExplanationTest(boolean useGlassBox, boolean classify) {
		super( classify );
		
		this.useGlassBox = useGlassBox;
	}
	
	@After
	public void after() {
		super.after();
		
		if( expGen != null ) {
			if( useGlassBox ) {
				GlassBoxExplanation gbe = (GlassBoxExplanation) expGen.getSingleExplanationGenerator();
				gbe.dispose();
				reasoner.dispose();
			}
			else {
				BlackBoxExplanation bbe = (BlackBoxExplanation) expGen.getSingleExplanationGenerator();
				bbe.dispose();
				reasoner.getManager().removeOntologyChangeListener( bbe.getDefinitionTracker() );
			}
		}
	}

	
	public void setupGenerators(Collection<OWLAxiom> ontologyAxioms) throws Exception {	
		// USE_TRACING should be turned on for glass box explanation which is done by
		// ExplanationTestSuite that calls this class. We don't set this value here to
		// avoid repeating the clean up code that sets it bakc to the old value
		assertTrue( !useGlassBox || PelletOptions.USE_TRACING );
		
		converter = new SatisfiabilityConverter(OWL.factory);
		renderer = new ConciseExplanationRenderer();
		
		OWLOntology ontology = OWL.Ontology( ontologyAxioms );			

		PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		reasoner = reasonerFactory.createReasoner( ontology );

		TransactionAwareSingleExpGen singleExpGen = useGlassBox
			? new GlassBoxExplanation( reasoner )
			: new BlackBoxExplanation( ontology, reasonerFactory, reasoner ) {
			    protected boolean isFirstExplanation() {
			    	return false;
			    }
			};

		expGen = new HSTExplanationGenerator( singleExpGen );
		
		KnowledgeBase kb = reasoner.getKB();
		
		if( classify ) {
			kb.setDoExplanation( true );
			kb.ensureConsistency();
			kb.setDoExplanation( false );
			
			kb.realize();
		}
	}

	@Override
	public void testInconsistencyExplanations(int max, OWLAxiom[]... explanations) throws Exception {
		assumeTrue(useGlassBox);
		
		super.testInconsistencyExplanations(max, explanations);
	}
	
	@Override
	public void testExplanations(OWLAxiom axiom, int max, Set<Set<OWLAxiom>> expectedExplanations)
			throws Exception {

		OWLClassExpression unsatClass = converter.convert( axiom );

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