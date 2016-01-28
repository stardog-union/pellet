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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import org.protege.owl.server.api.ChangeHistory;
import org.protege.owl.server.api.OntologyDocumentRevision;
import org.protege.owl.server.api.RevisionPointer;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.SetOntologyID;

/**
 * @author Evren Sirin
 */
public class ProtegeOntologyState extends OntologyStateImpl {
	public static final Logger LOGGER = Logger.getLogger(ProtegeOntologyState.class.getName());

	private final Client client;
	
	private final RemoteOntologyDocument remoteOnt;

	private OntologyDocumentRevision revision;

	public ProtegeOntologyState(final Client client,
	                            final RemoteOntologyDocument remoteOnt,
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

	private static OntologyDocumentRevision readRevision(final Path path) throws IOException {
		final File aHeadFile = revisionFile(path);

		if (aHeadFile.exists()) {
			return new OntologyDocumentRevision(Integer.parseInt(Files.toString(aHeadFile, Charsets.UTF_8)));
		}

		return OntologyDocumentRevision.START_REVISION;
	}

	private void writeRevision() throws IOException {
		final File aHeadFile = revisionFile(getPath());
		Files.write(String.valueOf(getVersion()), aHeadFile, Charsets.UTF_8);
	}

	@Override
	protected boolean updateOntology(OWLOntology ontology) {
		try {
			OntologyDocumentRevision headRevision = client.evaluateRevisionPointer(remoteOnt, RevisionPointer.HEAD_REVISION);
			int cmp = revision.compareTo(headRevision);
			boolean update = cmp != 0;
			if (update) {
				if (cmp > 0) {
					throw new IllegalStateException("Current revision is higher than the HEAD revision");
				}

				LOGGER.info("Updating " + this + " from " + revision + " to " + headRevision);

				ChangeHistory history = client.getChanges(remoteOnt, revision.asPointer(), headRevision.asPointer());

				List<OWLOntologyChange> changes = filterChanges(history.getChanges(ontology));
				ontology.getOWLOntologyManager().applyChanges(changes);

				revision = headRevision;
			}
			return update;
		}
		catch (OWLServerException e) {
			LOGGER.warning("Cannot retrieve changes from the server");
			return false;
		}
	}

	protected int getVersion() {
		return revision.getRevisionDifferenceFrom(OntologyDocumentRevision.START_REVISION);
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

	private static List<OWLOntologyChange> filterChanges(List<OWLOntologyChange> changes) {
		final List<OWLOntologyChange> filteredChanges = Lists.newArrayList();
		for (OWLOntologyChange change : changes) {
			if (change instanceof SetOntologyID || change.isAxiomChange() && (change.getAxiom().isLogicalAxiom() || change.getAxiom() instanceof OWLDeclarationAxiom)) {
				filteredChanges.add(change);
			}
		}
		return filteredChanges;
	}

	@Override
	public String toString() {
		IRI iri = getIRI();
		return "State(" + (iri == null ? "Anonymous ontology" : iri) + ")";
	}
}