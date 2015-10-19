// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * @author Evren Sirin
 */
public class OntologyStateImpl implements OntologyState {
	public static final Logger LOGGER = Logger.getLogger(OntologyStateImpl.class.getName());

	private final OWLOntology ontology;

	private final IRI ontologyIRI;

	private final LoadingCache<String, ClientState> clients;

	public OntologyStateImpl(final OWLOntology ont) {
		ontology = ont;
		ontologyIRI = ontology.getOntologyID().getOntologyIRI().get();
		clients = CacheBuilder.newBuilder()
		                      .expireAfterAccess(30, TimeUnit.MINUTES)
		                      .removalListener(new RemovalListener<String, ClientState>() {
			                      @Override
			                      public void onRemoval(final RemovalNotification<String, ClientState> theRemovalNotification) {
				                      theRemovalNotification.getValue().close();
			                      }
		                      })
		                      .build(new CacheLoader<String, ClientState>() {
			                      @Override
			                      public ClientState load(final String user) throws Exception {
				                      synchronized (ontology) {
					                      return new ClientStateImpl(OWL.Ontology(ontology.getAxioms(), ontologyIRI));
				                      }
			                      }

		                      });
	}

	@Override
	public ClientState createClient(final String clientID) {
		try {
			return clients.get(clientID);
		}
		catch (ExecutionException e) {
			LOGGER.log(Level.SEVERE, "Cannot create state for client " + clientID, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public ClientState getClient(final String clientID) {
		return Objects.requireNonNull(clients.getIfPresent(clientID), "No state found for " + clientID);
	}

	@Override
	public IRI getOntologyIRI() {
		return ontologyIRI;
	}

	@Override
	public void applyChanges(final List<OWLOntologyChange> changes) {
		synchronized (ontology) {
			ontology.getOWLOntologyManager().applyChanges(changes);
		}
	}

	@Override
	public void reload() {
		throw new UnsupportedOperationException("NYI");
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException("NYI");
	}

	@Override
	public void close() throws Exception {
		clients.invalidateAll();
	}
}