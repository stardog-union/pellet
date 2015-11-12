// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model.impl;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.reasoner.LocalSchemaReasoner;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Throwables;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author Evren Sirin
 */
public class ClientStateImpl implements ClientState {

	private final SchemaReasoner reasoner;
	private final OWLOntology ontology;

	public ClientStateImpl(final IncrementalReasoner incremental, final int version) {
		// create the reasoner with a copy of the incremental reasoner so it won't be affected if the original reasoner is updated
		reasoner = new LocalSchemaReasoner(incremental.copy(), version);
		ontology = incremental.getRootOntology();
	}

	@Override
	public SchemaReasoner getReasoner() {
		return reasoner;
	}

	@Override
	public void close() {
		try {
			reasoner.close();

			OWLOntologyManager manager = ontology.getOWLOntologyManager();
			if (manager != null) {
				manager.removeOntology(ontology);
			}
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
	}
}