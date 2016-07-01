// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.protege.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.util.ClientUtils;
import org.protege.editor.owl.server.versioning.api.ChangeHistory;
import org.protege.editor.owl.server.versioning.api.DocumentRevision;
import org.protege.editor.owl.server.versioning.api.ServerDocument;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Evren Sirin
 */
public class ProtegeOntologyState extends OntologyStateImpl {
	public static final Logger LOGGER = Logger.getLogger(ProtegeOntologyState.class.getName());

	private final LocalHttpClient client;
	
	private final ServerDocument remoteOnt;

	private DocumentRevision revision;

	public ProtegeOntologyState(final LocalHttpClient client,
	                            final ServerDocument remoteOnt,
	                            final Path path) throws IOException {
		super(path);

		this.client = client;
		this.remoteOnt = remoteOnt;
		this.revision = readRevision(path);
		writeRevision();
	}

	private static File revisionFile(final Path path) throws IOException {
		return path.resolveSibling("HEAD").toFile();
	}

	private static DocumentRevision readRevision(final Path path) throws IOException {
		final File aHeadFile = revisionFile(path);

		if (aHeadFile.exists()) {
			return DocumentRevision.create(Integer.parseInt(Files.toString(aHeadFile, Charsets.UTF_8)));
		}

		return DocumentRevision.START_REVISION;
	}

	private void writeRevision() throws IOException {
		final File aHeadFile = revisionFile(getPath());
		Files.write(String.valueOf(getVersion()), aHeadFile, Charsets.UTF_8);
	}

	@Override
	protected boolean updateOntology(OWLOntology ontology) {
		try {
			ChangeHistory history = client.getLatestChanges(remoteOnt, revision);
			boolean update = !history.isEmpty();
			if (update) {
				DocumentRevision headRevision = history.getHeadRevision();

				LOGGER.info("Updating " + this + " from " + revision + " to " + headRevision);

				ClientUtils.updateOntology(ontology, history, ontology.getOWLOntologyManager());

				revision = headRevision;
			}
			return update;
		}
		catch (Exception e) {
			LOGGER.warning("Cannot retrieve changes from the server");
			return false;
		}
	}

	protected int getVersion() {
		return revision.getRevisionNumber();
	}

	@Override
	public void save() {
		super.save();

		try {
			writeRevision();
		}
		catch (IOException theE) {
			LOGGER.log(Level.SEVERE, "Couldn't save the ontology state " + getIRI().toQuotedString(), theE);
		}
	}

	@Override
	public String toString() {
		IRI iri = getIRI();
		return "State(" + (iri == null ? "Anonymous ontology" : iri) + ")";
	}
}