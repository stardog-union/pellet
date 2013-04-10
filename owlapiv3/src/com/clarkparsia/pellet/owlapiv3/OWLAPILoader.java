// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapiv3;

import java.net.URI;
import java.util.Set;

import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportEvent;
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
public class OWLAPILoader extends KBLoader {
	private OWLOntologyManager	manager;

	private PelletReasoner		pellet;

	private LimitedMapIRIMapper	iriMapper;

	private OWLOntology			baseOntology;
	
	private boolean				ignoreImports;
	
	/**
	 * A workaround for OWLAPI bug that does not let us import a loaded ontology so that we can
	 * minimize the warnings printed when OWLOntologyManager.makeLoadImportRequest is called
	 */
	private boolean				loadSingleFile;

	public OWLAPILoader() {
		iriMapper = new LimitedMapIRIMapper();
		manager = OWLManager.createOWLOntologyManager();

		manager.setSilentMissingImportsHandling(true);
		manager.addMissingImportListener(new MissingImportListener() {
			public void importMissing(MissingImportEvent event) {
				if (!ignoreImports) {
					URI importURI = event.getImportedOntologyURI();
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
	public KnowledgeBase getKB() {
		return pellet.getKB();
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public OWLOntology getOntology() {
		return baseOntology;
	}

	public Set<OWLOntology> getAllOntologies() {
		return manager.getOntologies();
	}

	/**
	 * Returns the reasoner created by this loader. A <code>null</code> value is returned until
	 * {@link #load()} function is called (explicitly or implicitly).
	 * 
	 * @return the reasoner created by this loader
	 */
	public PelletReasoner getReasoner() {
		return pellet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		pellet = new PelletReasonerFactory().createReasoner( baseOntology );
		pellet.getKB().setTaxonomyBuilderProgressMonitor(
				PelletOptions.USE_CLASSIFICATION_MONITOR.create() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(String... fileNames) {
		// note if we will load a single file
		loadSingleFile = fileNames.length == 1;
		
		super.parse( fileNames );		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseFile(String file) {
		try {
			IRI fileIRI = IRI.create( file );
			iriMapper.addAllowedIRI( fileIRI );
			
			if( loadSingleFile ) {
				// we are loading a single file so we can load it directly
				baseOntology = manager.loadOntologyFromOntologyDocument( fileIRI );				
			}
			else {
				// loading multiple files so each input file should be added as
				// an import to the base ontology we created
				OWLOntology importOnt = manager.loadOntologyFromOntologyDocument( fileIRI );	
				OWLImportsDeclaration declaration = manager.getOWLDataFactory()
						.getOWLImportsDeclaration( importOnt.getOntologyID().getOntologyIRI() );
				manager.applyChange( new AddImport( baseOntology, declaration ) );
			}
		} catch( IllegalArgumentException e ) {
			throw new RuntimeException( e );
		} catch( OWLOntologyCreationException e ) {
			throw new RuntimeException( e );
		} catch( OWLOntologyChangeException e ) {
			throw new RuntimeException( e );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIgnoreImports(boolean ignoreImports) {
		this.ignoreImports = ignoreImports;
		if( ignoreImports ) {
			manager.clearIRIMappers();
			manager.addIRIMapper( iriMapper );
		}
		else {
			manager.clearIRIMappers();
			manager.addIRIMapper( new NonMappingOntologyIRIMapper() );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {

		iriMapper.clear();
		for( OWLOntology ont : manager.getOntologies() )
			manager.removeOntology( ont );

		try {
			baseOntology = manager.createOntology();
		} catch( OWLOntologyCreationException e ) {
			throw new RuntimeException( e );
		}
	}

}
