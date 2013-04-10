// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapiv3;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

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
public class PelletReasonerFactory implements OWLReasonerFactory {
	private static final PelletReasonerFactory INSTANCE = new PelletReasonerFactory();
	
	/**
	 * Returns a static factory instance that can be used to create reasoners.
	 * 
	 * @return a static factory instance 
	 */
	public static PelletReasonerFactory getInstance() {
		return INSTANCE;
	}

	
	/**
	 * {@inheritDoc}
	 */
	public String getReasonerName() {
		return "Pellet";
	}

	public String toString() {
		return getReasonerName();
	}

	/**
	 * {@inheritDoc}
	 */
	public PelletReasoner createReasoner(OWLOntology ontology) {
		return new PelletReasoner( ontology, BufferingMode.BUFFERING );
	}

	/**
	 * {@inheritDoc}
	 */
	public PelletReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		return new PelletReasoner( ontology, config, BufferingMode.BUFFERING );
	}

	/**
	 * {@inheritDoc}
	 */
	public PelletReasoner createNonBufferingReasoner(OWLOntology ontology) {
		return new PelletReasoner( ontology, BufferingMode.NON_BUFFERING );
	}

	/**
	 * {@inheritDoc}
	 */
	public PelletReasoner createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config) throws IllegalConfigurationException {
		return new PelletReasoner( ontology, config, BufferingMode.NON_BUFFERING );
	}
}
