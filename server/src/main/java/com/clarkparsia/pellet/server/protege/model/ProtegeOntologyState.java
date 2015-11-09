// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.protege.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.model.impl.OntologyStateImpl;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import org.protege.owl.server.api.OntologyDocumentRevision;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Evren Sirin
 */
public class ProtegeOntologyState extends OntologyStateImpl {
	public static final Logger LOGGER = Logger.getLogger(ProtegeOntologyState.class.getName());

	private final Client client;

	private final VersionedOntologyDocument ontology;

	public ProtegeOntologyState(final Client client, final VersionedOntologyDocument ontology) {
		super(ontology.getOntology());

		this.client = client;
		this.ontology = ontology;
	}

	@Override
	protected boolean updateOntology() {
		try {
			OntologyDocumentRevision revision = ontology.getRevision();
			ClientUtilities.update(client, ontology);
			return revision != ontology.getRevision();
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
			final OntologyDocumentRevision aHEAD = ontology.getLocalHistory().getEndRevision();
			final Path aHeadFilePath = getOntologyDirectory().resolve("HEAD");

			removeIfExists(aHeadFilePath);

			final CharSink aSink = Files.asCharSink(aHeadFilePath.toFile(), Charset.defaultCharset());
			aSink.write(aHEAD.toString());
		}
		catch (IOException theE) {
			LOGGER.log(Level.SEVERE, "Couldn't save the OntologyState "+ getIRI().toQuotedString(), theE);
		}
	}
}