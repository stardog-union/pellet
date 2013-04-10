// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;


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
public class PelletIncremantalReasonerFactory implements OWLReasonerFactory {
	private static final PelletIncremantalReasonerFactory INSTANCE = new PelletIncremantalReasonerFactory();
	
	/**
	 * Returns a static factory instance that can be used to create reasoners.
	 * 
	 * @return a static factory instance 
	 */
	public static PelletIncremantalReasonerFactory getInstance() {
		return INSTANCE;
	}

	
	/**
	 * {@inheritDoc}
	 */
	public String getReasonerName() {
		return "Pellet (Incremental)";
	}

	public String toString() {
		return getReasonerName();
	}
	
	public IncrementalClassifier createReasoner(InputStream is) throws IOException {
		return IncrementalClassifierPersistence.load( is );
	}
	
	public IncrementalClassifier createReasoner(InputStream is, OWLOntology ontology) throws IOException {
		return IncrementalClassifierPersistence.load( is, ontology );
	}
	
	public IncrementalClassifier createReasoner(PelletReasoner reasoner) {
		return new IncrementalClassifier( reasoner );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IncrementalClassifier createReasoner(OWLOntology ontology) {
		return new IncrementalClassifier( ontology );
	}
	
	public IncrementalClassifier createReasoner(OWLOntology ontology, ModuleExtractor moduleExtractor) {
		return new IncrementalClassifier( ontology, moduleExtractor );
	}

	/**
	 * {@inheritDoc}
	 */
	public IncrementalClassifier createReasoner(OWLOntology ontology, OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		return new IncrementalClassifier( ontology, config );
	}

	/**
	 * {@inheritDoc}
	 */
	public IncrementalClassifier createNonBufferingReasoner(OWLOntology ontology) {
		return new IncrementalClassifier( ontology );
	}

	/**
	 * {@inheritDoc}
	 */
	public IncrementalClassifier createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config) throws IllegalConfigurationException {
		return new IncrementalClassifier( ontology, config );
	}
}
