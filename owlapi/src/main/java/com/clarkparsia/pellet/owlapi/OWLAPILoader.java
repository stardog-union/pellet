// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapi;

import java.util.Set;
import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportEvent;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.MissingImportListener;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;

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
public class OWLAPILoader extends KBLoader
{
	private final OWLOntologyManager manager;

	private PelletReasoner pellet;

	private final LimitedMapIRIMapper iriMapper;

	private OWLOntology baseOntology;

	private boolean ignoreImports;

	/**
	 * A workaround for OWLAPI bug that does not let us import a loaded ontology so that we can minimize the warnings printed when
	 * OWLOntologyManager.makeLoadImportRequest is called
	 */
	private boolean loadSingleFile;

	public OWLAPILoader()
	{
		iriMapper = new LimitedMapIRIMapper();
		manager = OWLManager.createOWLOntologyManager();

		manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
		manager.addMissingImportListener(new MissingImportListener()
		{
			/**
			 * TODO
			 *
			 * @since
			 */
			private static final long serialVersionUID = -1580704502184270618L;

			@Override
			public void importMissing(final MissingImportEvent event)
			{
				if (!ignoreImports)
				{
					final IRI importURI = event.getImportedOntologyURI();
					System.err.println("WARNING: Cannot import " + importURI);
					event.getCreationException().printStackTrace();
				}
			}
		});

		clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KnowledgeBase getKB()
	{
		return pellet.getKB();
	}

	public OWLOntologyManager getManager()
	{
		return manager;
	}

	public OWLOntology getOntology()
	{
		return baseOntology;
	}

	public Set<OWLOntology> getAllOntologies()
	{
		return manager.getOntologies();
	}

	/**
	 * Returns the reasoner created by this loader. A <code>null</code> value is returned until {@link #load()} function is called (explicitly or implicitly).
	 *
	 * @return the reasoner created by this loader
	 */
	public PelletReasoner getReasoner()
	{
		return pellet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load()
	{
		pellet = new PelletReasonerFactory().createReasoner(baseOntology);
		pellet.getKB().setTaxonomyBuilderProgressMonitor(PelletOptions.USE_CLASSIFICATION_MONITOR.create());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(final String... fileNames)
	{
		// note if we will load a single file
		loadSingleFile = fileNames.length == 1;

		super.parse(fileNames);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseFile(final String file)
	{
		try
		{
			final IRI fileIRI = IRI.create(file);
			iriMapper.addAllowedIRI(fileIRI);

			if (loadSingleFile)
				// we are loading a single file so we can load it directly
				baseOntology = manager.loadOntologyFromOntologyDocument(fileIRI);
			else
			{
				// loading multiple files so each input file should be added as
				// an import to the base ontology we created
				final OWLOntology importOnt = manager.loadOntologyFromOntologyDocument(fileIRI);
				final OWLImportsDeclaration declaration = manager.getOWLDataFactory().getOWLImportsDeclaration(importOnt.getOntologyID().getOntologyIRI().get());
				manager.applyChange(new AddImport(baseOntology, declaration));
			}
		}
		catch (final IllegalArgumentException e)
		{
			throw new RuntimeException(file, e);
		}
		catch (final OWLOntologyCreationException e)
		{
			throw new RuntimeException(file, e);
		}
		catch (final OWLOntologyChangeException e)
		{
			throw new RuntimeException(file, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIgnoreImports(final boolean ignoreImports)
	{
		this.ignoreImports = ignoreImports;
		if (ignoreImports)
		{
			manager.clearIRIMappers();
			manager.addIRIMapper(iriMapper);
		}
		else
		{
			manager.clearIRIMappers();
			manager.addIRIMapper(new NonMappingOntologyIRIMapper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear()
	{

		iriMapper.clear();
		for (final OWLOntology ont : manager.getOntologies())
			manager.removeOntology(ont);

		try
		{
			baseOntology = manager.createOntology();
		}
		catch (final OWLOntologyCreationException e)
		{
			throw new RuntimeException(e);
		}
	}

}
