// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation;


import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Pair;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import aterm.ATermAppl;

import com.clarkparsia.owlapi.explanation.util.DefinitionTracker;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.AxiomConverter;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * <p>
 * Title: GlassBoxExplanation
 * </p>
 * <p>
 * Description: Implementation of SingleExplanationGenerator interface using the
 * axiom tracing facilities of Pellet.
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
public class GlassBoxExplanation extends SingleExplanationGeneratorImpl {	
	static {
		setup();
	}	
	
	/**
	 * Very important initialization step that needs to be called once before a reasoner
	 * is created. this function will be called automatically when GlassBoxExplanation
	 * is loaded by the class loader.
	 */
	public static void setup() {
		// initialize PelletOptions to required values for explanations
		// to work before any Pellet reasoner instance is created
		PelletOptions.USE_TRACING = true;
	}
	
	public static final Logger	log			= Logger
													.getLogger( GlassBoxExplanation.class.getName() );

	/**
	 * Alternative reasoner. We use a second reasoner because we do not want to lose the 
	 * state in the original reasoner.
	 */
	private PelletReasoner altReasoner = null;
	
	private boolean altReasonerEnabled = false;
	
	private AxiomConverter axiomConverter;

	public GlassBoxExplanation(OWLOntology ontology, PelletReasonerFactory factory) {
		this( factory, factory.createReasoner( ontology ) );
	}
	
	public GlassBoxExplanation(PelletReasoner reasoner) {
		this( new PelletReasonerFactory(), reasoner );
	}
		
	public GlassBoxExplanation(PelletReasonerFactory factory, PelletReasoner reasoner) {
		super( reasoner.getRootOntology(), factory, reasoner );
		
		axiomConverter = new AxiomConverter( reasoner );
	}

	private void setAltReasonerEnabled(boolean enabled) {
		if( enabled ) {
			if( altReasoner == null ) {
				log.fine( "Create alt reasoner" );
				altReasoner = getReasonerFactory().createNonBufferingReasoner( getOntology() );
			}
		}

		altReasonerEnabled = enabled;
	}
	
	private OWLClass getNegation(OWLClassExpression desc) {
		if( !(desc instanceof OWLObjectComplementOf) )
			return null;
		
		OWLClassExpression not = ((OWLObjectComplementOf) desc).getOperand();
		if( not.isAnonymous() )
			return null;
		
		return (OWLClass) not;
	}
	
	private Pair<OWLClass,OWLClass> getSubClassAxiom(OWLClassExpression desc) {
		if( !(desc instanceof OWLObjectIntersectionOf) )
			return null;
		
		OWLObjectIntersectionOf conj = (OWLObjectIntersectionOf) desc; 
			
		if( conj.getOperands().size() != 2 )
			return null;
		
		Iterator<OWLClassExpression> conjuncts = conj.getOperands().iterator();		
		OWLClassExpression c1 = conjuncts.next();
		OWLClassExpression c2 = conjuncts.next();

		OWLClass sub = null;
		OWLClass sup = null;
		
		if( !c1.isAnonymous() ) {
			sub = (OWLClass) c1;
			sup = getNegation( c2 );
		}
		else if( !c2.isAnonymous() ) {
			sub = (OWLClass) c2;
			sup = getNegation( c2 );
		}
		
		if( sup == null )
			return null;
		
		return new Pair<OWLClass, OWLClass>( sub, sup );
	}
	
	private Set<OWLAxiom> getCachedExplanation(OWLClassExpression unsatClass) {
		PelletReasoner pellet = getReasoner();

		if( !pellet.getKB().isClassified() )
			return null;

		Pair<OWLClass,OWLClass> pair = getSubClassAxiom( unsatClass );

		if( pair != null ) {
			Set<Set<ATermAppl>> exps = TaxonomyUtils.getSuperExplanations( 
					pellet.getKB().getTaxonomy(), 
					pellet.term( pair.first ), 
					pellet.term( pair.second ) );

			if( exps != null ) {
				Set<OWLAxiom> result = convertExplanation( exps.iterator().next() ); 
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Cached explanation: " + result );
				return result;
			}
		}

		return null;
	}

	public Set<OWLAxiom> getExplanation(OWLClassExpression unsatClass) {
		Set<OWLAxiom> result = null;

		boolean firstExplanation = isFirstExplanation();

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Explain: " + unsatClass + " " + "First: " + firstExplanation );

		if( firstExplanation ) {
			altReasoner = null;
			
			result = getCachedExplanation( unsatClass );

			if( result == null )
				result = getPelletExplanation( unsatClass );
		}
		else {
			setAltReasonerEnabled( true );

			try {
				result = getPelletExplanation( unsatClass );
			} catch( RuntimeException e ) {
				log.log( Level.SEVERE,  "Unexpected error while trying to get explanation set from Pellet", e );
				throw new OWLRuntimeException(e);
			} finally {
				setAltReasonerEnabled( false );
			}
		}

		return result;
	}

	private Set<OWLAxiom> getPelletExplanation(OWLClassExpression unsatClass) {
		PelletReasoner pellet = getReasoner();
		
		pellet.getKB().prepare();
		
		// satisfiable if there is an undefined entity
		boolean sat = !getDefinitionTracker().isDefined( unsatClass );
				
		if( !sat ) {
			sat = isSatisfiable( pellet, unsatClass, true );
		}
		else if( log.isLoggable( Level.FINE ) )
			log.fine( "Undefined entity in " + unsatClass );
			

		if( sat ) {
			return Collections.emptySet();
		}
		else {
			Set<OWLAxiom> explanation = convertExplanation( pellet.getKB().getExplanationSet() );

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Explanation " + explanation );

			Set<OWLAxiom> prunedExplanation = pruneExplanation( unsatClass, explanation, true );
			
			int prunedAxiomCount = explanation.size() - prunedExplanation.size();
			if( log.isLoggable( Level.FINE ) && prunedAxiomCount > 0 ) {
				log.fine( "Pruned " + prunedAxiomCount + " axioms from the explanation: "
						+ SetUtils.difference( explanation, prunedExplanation ) );
				log.fine( "New explanation " + prunedExplanation );
			}

			return prunedExplanation;
		}
	}
	
	private boolean isSatisfiable(PelletReasoner pellet, OWLClassExpression unsatClass, boolean doExplanation) {
		pellet.getKB().setDoExplanation( doExplanation );
		boolean sat = unsatClass.isOWLThing()
			? pellet.isConsistent()
			: pellet.isSatisfiable( unsatClass );
		pellet.getKB().setDoExplanation( false );
		
		return sat;
	}
	
	private Set<OWLAxiom> convertExplanation(Set<ATermAppl> explanation) {
				if( explanation == null || explanation.isEmpty() )
			throw new OWLRuntimeException( "No explanation computed" );
		
		Set<OWLAxiom> result = new HashSet<OWLAxiom>();

		for( ATermAppl term : explanation ) {
			OWLAxiom axiom = axiomConverter.convert( term );
			if( axiom == null )
				throw new OWLRuntimeException( "Cannot convert: " + term );
			result.add( axiom );
		}

		return result;
	}

	/**
	 * <p>Prunes the given explanation using slow pruning technique of BlackBox
	 * explanation. The explanation returned from Pellet axiom tracing is not
	 * guaranteed to be minimal so pruning is necessary to ensure minimality.
	 * The idea is to create an ontology with only the axioms in the
	 * explanation, remove an axiom, test satisfiability, and restore the axiom
	 * if the class turns to be satisfiable after the removal. 
	 * 
	 * <p>There are two
	 * different pruning techniques. Incremental pruning attaches the reasoner
	 * as a listener and updates the reasoner with axiom removals/restores.
	 * Non-incremental pruning clears the reasoner at each iteration and reloads
	 * the axioms from scratch each time. Incremental pruning is faster but may
	 * return incorrect answers since axiom updates are less robust.
	 */
	private Set<OWLAxiom> pruneExplanation(OWLClassExpression unsatClass, Set<OWLAxiom> explanation, boolean incremental) {
		try {
			// initialize pruned explanation to be same as the given explanation
			Set<OWLAxiom> prunedExplanation = new HashSet<OWLAxiom>( explanation );

			// we can only prune if there is more than one axiom in the
			// explanation
			if( prunedExplanation.size() <= 1 )
				return prunedExplanation;

			// create an ontology from the explanation axioms
			OWLOntology debuggingOntology = OWL.Ontology( explanation );
			
			DefinitionTracker defTracker = new DefinitionTracker( debuggingOntology );

			// since explanation size is generally small we can create and use a
			// completely new reasoner rather than destroying the state on already
			// existing reasoner
			PelletReasoner reasoner = getReasonerFactory().createNonBufferingReasoner( debuggingOntology );
						
			if( !defTracker.isDefined( unsatClass ) ) {
				log.warning( "Some of the entities in " + unsatClass
						+ " are not defined in the explanation " + explanation );
			}
			
			if( isSatisfiable( reasoner, unsatClass, true ) ) {
				log.warning( "Explanation incomplete: Concept " + unsatClass
						+ " is satisfiable in the explanation " + explanation );
			}

			// simply remove axioms one at a time. If the unsatClass turns
			// satisfiable then we know that axiom cannot be a part of minimal
			// explanation
			for( OWLAxiom axiom : explanation ) {
				if( log.isLoggable( Level.FINER ) )
					log.finer( "Try pruning " + axiom );
				
				if( !incremental) {
					reasoner.dispose();
				}
				
				OntologyUtils.removeAxioms( debuggingOntology, axiom );
				
				if( !incremental) {
					reasoner = getReasonerFactory().createNonBufferingReasoner( debuggingOntology );
				}
				
				reasoner.getKB().prepare();

				if( defTracker.isDefined( unsatClass )
						&& !isSatisfiable( reasoner, unsatClass, false ) ) {
					// does not affect satisfiability so remove from the results
					prunedExplanation.remove( axiom );
					
					if( log.isLoggable( Level.FINER ) )
						log.finer( "Pruned " + axiom );					
				}
				else {
					// affects satisfiability so add back to the ontology
					OntologyUtils.addAxioms( debuggingOntology, axiom );				
				}
			}
			
			if( incremental ) {
				// remove the listener and the ontology to avoid memory leaks
				reasoner.dispose();
			}
			
			OWL.manager.removeOntology( debuggingOntology );
			OWL.manager.removeOntologyChangeListener( defTracker );			

			return prunedExplanation;
		} catch( OWLOntologyChangeException e ) {
			throw new OWLRuntimeException( e );
		}
	}
	
	@Override
	public PelletReasoner getReasoner() {
		return altReasonerEnabled ? altReasoner : (PelletReasoner) super.getReasoner();
	}
	
	@Override
	public PelletReasonerFactory getReasonerFactory() {
		return (PelletReasonerFactory) super.getReasonerFactory();
	}
	
	public void dispose() {
		getOntologyManager().removeOntologyChangeListener( getDefinitionTracker() );
		if( altReasoner != null )
			altReasoner.dispose();
	}
	
	public String toString() {
		return "GlassBox";
	}
}