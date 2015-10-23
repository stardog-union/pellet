// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import org.semanticweb.owlapi.model.OWLOntology;
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
public class IncremantalReasonerFactory implements OWLReasonerFactory {
	private static final IncremantalReasonerFactory INSTANCE = new IncremantalReasonerFactory();
	
	/**
	 * Returns a static factory instance that can be used to create reasoners.
	 * 
	 * @return a static factory instance 
	 */
	public static IncremantalReasonerFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getReasonerName() {
		return "Pellet (Incremental)";
	}

	@Override
	public String toString() {
		return getReasonerName();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalReasoner createReasoner(OWLOntology ontology) {
		return new IncrementalReasoner( ontology, new IncrementalReasonerConfiguration() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration config) throws IllegalConfigurationException {
		return new IncrementalReasoner( ontology, config(config) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalReasoner createNonBufferingReasoner(OWLOntology ontology) {
		return new IncrementalReasoner( ontology, new IncrementalReasonerConfiguration() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalReasoner createNonBufferingReasoner(OWLOntology ontology, OWLReasonerConfiguration config) throws IllegalConfigurationException {
		return new IncrementalReasoner( ontology, config(config) );
	}

	private IncrementalReasonerConfiguration config(OWLReasonerConfiguration config) {
		return (config instanceof IncrementalReasonerConfiguration) ? (IncrementalReasonerConfiguration) config : new IncrementalReasonerConfiguration(config);
	}
}
