// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.modularity.IncrementalReasonerConfiguration;
import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Implementation of ontology state without a backing store.
 *
 * @author Evren Sirin
 */
public class OntologyStateImpl implements OntologyState {
	public static final Logger LOGGER = Logger.getLogger(OntologyStateImpl.class.getName());

	protected static final OWLOntologyManager MANAGER = OWLManager.createOWLOntologyManager();

	private final OWLOntology ontology;

	private final IncrementalReasoner reasoner;

	private final LoadingCache<UUID, ClientState> clients;

	private final Path path;

	public OntologyStateImpl(OWLOntology ontology) {
		this.ontology = ontology;
		this.path = null;

		reasoner = IncrementalReasoner.config().createIncrementalReasoner(ontology);
		reasoner.getReasoner().getKB().setTaxonomyBuilderProgressMonitor(new ConsoleProgressMonitor());
		reasoner.classify();

		clients = initClientCache();
	}

	public OntologyStateImpl(Path path) {
		this.path = path;

		IncrementalReasonerConfiguration config = IncrementalReasoner.config().manager(MANAGER);
		OWLOntology ont = null;
		if (Files.exists(path)) {
			config.file(path.toFile());
		}
		else {
			try {
				if (!Files.exists(path)) {
					Files.createDirectories(path.getParent());
				}
				ont = MANAGER.createOntology();
			}
			catch (Exception e) {
				throw new RuntimeException("Cannot initialize ontology state", e);
			}
		}
		reasoner = config.createIncrementalReasoner(ont);
		reasoner.getReasoner().getKB().setTaxonomyBuilderProgressMonitor(new ConsoleProgressMonitor());
		reasoner.classify();

		ontology = reasoner.getRootOntology();

		clients = initClientCache();
	}

	private LoadingCache<UUID, ClientState> initClientCache() {
		return CacheBuilder.newBuilder()
		                   .expireAfterAccess(30, TimeUnit.MINUTES)
		                   .removalListener(new RemovalListener<UUID, ClientState>() {
			                   @Override
			                   public void onRemoval(final RemovalNotification<UUID, ClientState> theRemovalNotification) {
				                   UUID user = theRemovalNotification.getKey();
				                   ClientState state = theRemovalNotification.getValue();
				                   LOGGER.info("Closing client for "+ user);
				                   state.close();
			                   }
		                   })
		                   .build(new CacheLoader<UUID, ClientState>() {
			                   @Override
			                   public ClientState load(final UUID user) throws Exception {
				                   return newClientState(user);
			                   }
		                   });
	}

	public Path getPath() {
		return path;
	}

	protected int getVersion() {
		return SchemaReasoner.NO_VERSION;
	}

	private synchronized ClientState newClientState(final UUID user) {
		int version = getVersion();
		LOGGER.info("Creating new client for "+ user +" with revision "+ version);
		return new ClientStateImpl(reasoner, version);
	}

	@Override
	public ClientState getClient(final UUID clientID) {

		try {
			return clients.get(clientID);
		}
		catch (ExecutionException e) {
			LOGGER.log(Level.SEVERE, "Cannot create state for client " + clientID, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public IRI getIRI() {
		return ontology.getOntologyID().getOntologyIRI().orNull();
	}

	@Override
	public synchronized final boolean update() {
		boolean updated = updateOntology(ontology);

		if (updated) {
			LOGGER.info("Classifying updated ontology "+ ontology);

			reasoner.classify();

			save();
		}

		return updated;
	}

	/**
	 * Update ontology content with latest changes.
	 *
	 * @return {@code true} if there were changes or {@code false} if ontology was not updated
	 */
	protected boolean updateOntology(OWLOntology ontology) {
		// no changes for this implementation
		return false;
	}

	@Override
	public void save() {
		try {
			if (path != null) {
				reasoner.save(path.toFile());
			}
		}
		catch (IOException theE) {
			LOGGER.log(Level.SEVERE, "Couldn't save the OntologyState "+ getIRI().toQuotedString(), theE);
		}
	}

	@Override
	public void close() {
		clients.invalidateAll();

		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		if (manager != null) {
			manager.removeOntology(ontology);
		}

		reasoner.dispose();
	}

	@Override
	public boolean equals(final Object theOther) {
		if (this == theOther) {
			return true;
		}
		if (!(theOther instanceof OntologyState)) {
			return false;
		}

		OntologyState otherOntoState = (OntologyState) theOther;

		// Just considering for now the ontology IRI to determine equality given
		// that there shouldn't more than one state per ontology.
		return Objects.equals(this.getIRI(), otherOntoState.getIRI());
	}

	@Override
	public int hashCode() {
		return this.getIRI().hashCode();
	}
}