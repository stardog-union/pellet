// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.owlapi;

import java.net.URI;
import java.util.Set;

import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLOntologyCreationIOException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.NonMappingOntologyURIMapper;

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

	private Reasoner			pellet;
	
	private LimitedMapURIMapper	uriMapper;

	public OWLAPILoader() {
		manager = OWLManager.createOWLOntologyManager();

		pellet = new Reasoner( manager );
		
		uriMapper = new LimitedMapURIMapper();
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

	public Set<OWLOntology> getOntologies() {
		return manager.getOntologies();
	}

	public Reasoner getReasoner() {
		return pellet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		pellet.loadOntologies( manager.getOntologies() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseFile(String file) {
		try {
			URI fileURI = URI.create( file );
			uriMapper.addAllowedURI( fileURI );
			manager.loadOntology( URI.create( file ) );
		} catch( IllegalArgumentException e ) {
			throw new RuntimeException( "Cannot parse file: " + file );
		} catch( OWLOntologyCreationIOException e ) {
			throw new RuntimeException( e.getCause() );
		} catch( OWLOntologyCreationException e ) {
			throw new RuntimeException( e );
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIgnoreImports(boolean ignoreImports) {
		if ( ignoreImports ) {
			manager.clearURIMappers();
			manager.addURIMapper( uriMapper );
			manager.setSilentMissingImportsHandling( true );
		} else {
			manager.clearURIMappers();
			manager.addURIMapper( new NonMappingOntologyURIMapper() );
			manager.setSilentMissingImportsHandling( false );
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		uriMapper.clear();
		pellet.clearOntologies();
		for( OWLOntology ont : manager.getOntologies() )
			manager.removeOntology( ont.getURI() );
	}

}
