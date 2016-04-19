// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import java.io.IOException;
import java.io.InputStream;
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
public class PelletIncremantalReasonerFactory implements OWLReasonerFactory
{
	private static final PelletIncremantalReasonerFactory INSTANCE = new PelletIncremantalReasonerFactory();

	/**
	 * Returns a static factory instance that can be used to create reasoners.
	 *
	 * @return a static factory instance
	 */
	public static PelletIncremantalReasonerFactory getInstance()
	{
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getReasonerName()
	{
		return "Pellet (Incremental)";
	}

	@Override
	public String toString()
	{
		return getReasonerName();
	}

	public IncrementalClassifier createReasoner(final InputStream is) throws IOException
	{
		return IncrementalClassifierPersistence.load(is);
	}

	public IncrementalClassifier createReasoner(final InputStream is, final OWLOntology ontology) throws IOException
	{
		return IncrementalClassifierPersistence.load(is, ontology);
	}

	public IncrementalClassifier createReasoner(final PelletReasoner reasoner)
	{
		return new IncrementalClassifier(reasoner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalClassifier createReasoner(final OWLOntology ontology)
	{
		return new IncrementalClassifier(ontology);
	}

	public IncrementalClassifier createReasoner(final OWLOntology ontology, final ModuleExtractor moduleExtractor)
	{
		return new IncrementalClassifier(ontology, moduleExtractor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalClassifier createReasoner(final OWLOntology ontology, final OWLReasonerConfiguration config) throws IllegalConfigurationException
	{
		return new IncrementalClassifier(ontology, config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalClassifier createNonBufferingReasoner(final OWLOntology ontology)
	{
		return new IncrementalClassifier(ontology);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncrementalClassifier createNonBufferingReasoner(final OWLOntology ontology, final OWLReasonerConfiguration config) throws IllegalConfigurationException
	{
		return new IncrementalClassifier(ontology, config);
	}
}
