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
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.google.common.base.Strings;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.protege.owl.server.api.OntologyDocumentRevision;
import org.protege.owl.server.api.RevisionPointer;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Evren Sirin
 */
public class ProtegeOntologyState extends OntologyStateImpl {
	public static final Logger LOGGER = Logger.getLogger(ProtegeOntologyState.class.getName());

	private final Client client;

	private final VersionedOntologyDocument versionedOntology;

	public ProtegeOntologyState(final Client client,
	                            final VersionedOntologyDocument theVersionedOnto) {
		super(theVersionedOnto.getOntology());

		this.client = client;
		this.versionedOntology = theVersionedOnto;
	}

	public ProtegeOntologyState(final Client client,
	                            final VersionedOntologyDocument theVersionedOnto,
	                            final IncrementalReasoner theReasoner) {
		super(theVersionedOnto.getOntology(), theReasoner);

		this.client = client;
		this.versionedOntology = theVersionedOnto;
	}

	@Override
	protected boolean updateOntology() {
		try {
			OntologyDocumentRevision revision = versionedOntology.getRevision();
			ClientUtilities.update(client, versionedOntology);
			return revision != versionedOntology.getRevision();
		}
		catch (OWLServerException e) {
			LOGGER.warning("Cannot retrieve changes from the server");
			return false;
		}
	}

	@Override
	public void save() {
		super.save();

		try {
			final OntologyDocumentRevision aHEAD = versionedOntology.getLocalHistory().getEndRevision();
			final Path aHeadFilePath = getOntologyDirectory().resolve("HEAD");

			removeIfExists(aHeadFilePath);

			final CharSink aSink = Files.asCharSink(aHeadFilePath.toFile(), Charset.defaultCharset());
			aSink.write(aHEAD.toString());
		}
		catch (IOException theE) {
			LOGGER.log(Level.SEVERE, "Couldn't save the OntologyState "+ getIRI().toQuotedString(), theE);
		}
	}

	private static int readHEAD(final File theOntoDir) throws IOException {
		final Path aHeadFilePath = theOntoDir.toPath().resolve("HEAD");

		CharSource aSrc = Files.asCharSource(aHeadFilePath.toFile(), Charset.defaultCharset());

		// just read the first line, the revision number should be the only thing there
		final String aLine = aSrc.readFirstLine();

		if (Strings.isNullOrEmpty(aLine)) {
			throw new RuntimeException("Revision number is invalid in HEAD file");
		}

		return Integer.parseInt(aLine);
	}

	private static IncrementalReasoner readReasoner(final File theOntoDir) {
		final Path aReasonerFilePath = theOntoDir.toPath().resolve("reasoner_state.bin");

		return IncrementalReasoner.config()
		                          .file(aReasonerFilePath.toFile())
		                          .createIncrementalReasoner();
	}

	public static OntologyState loadFromDisk(final OWLOntologyManager theManager,
	                                         final Client theClient,
	                                         final File theOntoDir,
	                                         final RemoteOntologyDocument theRemoteDoc) throws IOException,
	                                                                                           OWLOntologyCreationException,
	                                                                                           OWLServerException {
		final RevisionPointer aRev = new RevisionPointer(new OntologyDocumentRevision(readHEAD(theOntoDir)));

		// load the ontology to the specific point were we left it
		final VersionedOntologyDocument versionedOnto = ClientUtilities.loadOntology(theClient,
		                                                                             theManager,
		                                                                             theRemoteDoc, aRev);

		final IncrementalReasoner aReasoner = readReasoner(theOntoDir);

		return new ProtegeOntologyState(theClient, versionedOnto, aReasoner);
	}
}