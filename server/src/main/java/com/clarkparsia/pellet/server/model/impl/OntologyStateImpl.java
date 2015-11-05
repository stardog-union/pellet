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
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.protege.owl.server.api.ChangeHistory;
import org.protege.owl.server.api.OntologyDocumentRevision;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Implementation of ontology state without a backing store.
 *
 * @author Evren Sirin
 */
public class OntologyStateImpl implements OntologyState {
	public static final Logger LOGGER = Logger.getLogger(OntologyStateImpl.class.getName());

	private final OWLOntology ontology;

	private final IncrementalReasoner reasoner;

	private final LoadingCache<String, ClientState> clients;

	public OntologyStateImpl(OWLOntology ontology) {
		this.ontology = ontology;

		reasoner = IncrementalReasoner.config().createIncrementalReasoner(ontology);
		reasoner.classify();

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
				                      return newClientState();
			                      }

		                      });
	}

	private synchronized ClientState newClientState() {
		return new ClientStateImpl(reasoner);
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
	public IRI getIRI() {
		return ontology.getOntologyID().getOntologyIRI().get();
	}

	@Override
	public synchronized final void update() {
		boolean updated = updateOntology();

		if (updated) {
			reasoner.classify();

			save();
		}
	}

	/**
	 * Update ontology content with latest changes.
	 *
	 * @return {@code true} if there were changes or {@code false} if ontology was not updated
	 */
	protected boolean updateOntology() {
		// no changes for this implementation
		return false;
	}

	@Override
	public void save() {
		// FIXME use IncrementalReasoner.save
	}

	@Override
	public void close() throws Exception {
		clients.invalidateAll();
	}
}