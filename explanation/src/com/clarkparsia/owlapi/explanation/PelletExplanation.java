// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * @author Evren Sirin
 */
public class PelletExplanation {
	static {
		setup();
	}

	/**
	 * Very important initialization step that needs to be called once before a
	 * reasoner is created. This function will be called automatically when
	 * GlassBoxExplanation is loaded by the class loader. This function simply
	 * calls the {@link GlassBoxExplanation#setup()} function.
	 */
	public static void setup() {
		GlassBoxExplanation.setup();
	}
	
	private OWLDataFactory factory;
	
	private HSTExplanationGenerator expGen;
	
	private SatisfiabilityConverter converter;
	
	public PelletExplanation(OWLOntology ontology) {
		this( ontology, true );
	}
	
	public PelletExplanation(OWLOntology ontology, boolean useGlassBox) {
		this( new PelletReasonerFactory().createReasoner( ontology ), useGlassBox );
	}

	public PelletExplanation(PelletReasoner reasoner) {
		this( reasoner, true );
	}
	
	private PelletExplanation(PelletReasoner reasoner, boolean useGlassBox) {
		// Get the factory object
		factory = reasoner.getManager().getOWLDataFactory();
		
		// Create a single explanation generator
		TransactionAwareSingleExpGen singleExp = useGlassBox 
			? new GlassBoxExplanation( reasoner )
			: new BlackBoxExplanation( reasoner.getRootOntology(), new PelletReasonerFactory(), reasoner );
		
		// Create multiple explanation generator
		expGen = new HSTExplanationGenerator( singleExp );		
		
		// Create the converter that will translate axioms into class expressions
		converter = new SatisfiabilityConverter( factory );
	}
	
	
	 public Set<OWLAxiom> getEntailmentExplanation(OWLAxiom axiom) {
    	OWLClassExpression unsatClass = converter.convert( axiom );
    	return getUnsatisfiableExplanation( unsatClass );
    }
    
    public Set<Set<OWLAxiom>> getEntailmentExplanations(OWLAxiom axiom) {
    	OWLClassExpression unsatClass = converter.convert( axiom );
    	return getUnsatisfiableExplanations( unsatClass );
    }


    public Set<Set<OWLAxiom>> getEntailmentExplanations(OWLAxiom axiom, int maxExplanations) {
    	OWLClassExpression unsatClass = converter.convert( axiom );
    	return getUnsatisfiableExplanations( unsatClass, maxExplanations );
    }
    
    
    public Set<OWLAxiom> getInconsistencyExplanation() {
    	return getUnsatisfiableExplanation( factory.getOWLThing() );
    }
    
    public Set<Set<OWLAxiom>> getInconsistencyExplanations() {
    	return getUnsatisfiableExplanations( factory.getOWLThing() );
    }
    
    public Set<Set<OWLAxiom>> getInconsistencyExplanations(int maxExplanations) {
    	return getUnsatisfiableExplanations( factory.getOWLThing(), maxExplanations );
    }
    
    public Set<OWLAxiom> getInstanceExplanation(OWLIndividual ind, OWLClassExpression cls) {
    	OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom( cls, ind );
    	return getEntailmentExplanation( classAssertion );
    }
    
    public Set<Set<OWLAxiom>> getInstanceExplanations(OWLIndividual ind, OWLClassExpression cls) {
    	OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom( cls, ind );
    	return getEntailmentExplanations( classAssertion );
    }
    
    public Set<Set<OWLAxiom>> getInstanceExplanations(OWLIndividual ind, OWLClassExpression cls, int maxExplanations) {
    	OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom( cls, ind );
    	return getEntailmentExplanations( classAssertion, maxExplanations );
    }
    
    public Set<OWLAxiom> getSubClassExplanation(OWLClassExpression subClass, OWLClassExpression superClass) {
    	OWLSubClassOfAxiom subClassAxiom = factory.getOWLSubClassOfAxiom( subClass, superClass );
    	return getEntailmentExplanation( subClassAxiom );
    }
    
    public Set<Set<OWLAxiom>> getSubClassExplanations(OWLClassExpression subClass, OWLClassExpression superClass) {
    	OWLSubClassOfAxiom subClassAxiom = factory.getOWLSubClassOfAxiom( subClass, superClass );
    	return getEntailmentExplanations( subClassAxiom );    	
    }
    
    public Set<Set<OWLAxiom>> getSubClassExplanations(OWLClassExpression subClass, OWLClassExpression superClass, int maxExplanations) {
    	OWLSubClassOfAxiom subClassAxiom = factory.getOWLSubClassOfAxiom( subClass, superClass );
    	return getEntailmentExplanations( subClassAxiom, maxExplanations );
    }
    
    /**
     * Returns a single explanation for an arbitrary class expression, or empty set
     * if the given expression is satisfiable.
     * @param unsatClass an unsatisfiabile class expression which is  will be
     *                   explained
     * @return set of axioms explaining the unsatisfiability of given class
     *         expression, or empty set if the given expression is satisfiable.
     */
    public Set<OWLAxiom> getUnsatisfiableExplanation(OWLClassExpression unsatClass) {
    	return expGen.getExplanation( unsatClass );
    }
    
    /**
     * Returns all the explanations for the given unsatisfiable class.
     * @param unsatClass The class that is unsatisfiable for which an explanation
     * will be generated.
     * @return All explanations for the given unsatisfiable class, or an empty
     *         set if the concept is satisfiable
     */
    public Set<Set<OWLAxiom>> getUnsatisfiableExplanations(OWLClassExpression unsatClass) {
    	return expGen.getExplanations( unsatClass );
    }
    
    /**
     * Return a specified number of explanations for the given unsatisfiable
     * class. A smaller number of explanations can be returned if there are not
     * as many explanations for the given concept. The returned set will be
     * empty if the given class is satisfiable,
     * 
     * @param unsatClass The class that is unsatisfiable for which an explanation
     * will be generated. 
     * @param maxExplanations Maximum number of explanations requested, or 0 to get all the
     *                        explanations
     * @return A specified number of explanations for the given unsatisfiable
     *         class, or an empty set if the concept is satisfiable
     */
    public Set<Set<OWLAxiom>> getUnsatisfiableExplanations(OWLClassExpression unsatClass, int maxExplanations) {
    	return expGen.getExplanations( unsatClass, maxExplanations );
    }    
}
