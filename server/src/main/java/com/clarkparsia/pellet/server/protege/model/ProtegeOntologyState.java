package com.clarkparsia.pellet.server.protege.model;

import java.util.List;
import java.util.Set;

import com.clarkparsia.pellet.server.model.ClientState;
import com.clarkparsia.pellet.server.model.OntologyState;
import com.google.common.collect.ForwardingObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtegeOntologyState extends ForwardingObject implements OntologyState {

	private final OntologyState mDelegate;

	// Path to identify the ontology in Pellet Server
	private final IRI remoteOntoDocIRI;

	public ProtegeOntologyState(final IRI theRemoteOntoDocIRI, final OntologyState theOntologyState) {
		remoteOntoDocIRI = theRemoteOntoDocIRI;
		mDelegate = theOntologyState;
	}

	@Override
	protected OntologyState delegate() {
		return mDelegate;
	}

	@Override
	public ClientState createClient(final String clientID) {
		return delegate().createClient(clientID);
	}

	@Override
	public ClientState getClient(final String clientID) {
		return delegate().getClient(clientID);
	}

	@Override
	public IRI getOntologyIRI() {
		return delegate().getOntologyIRI();
	}

	@Override
	public void update(final Set<OWLAxiom> additions, final Set<OWLAxiom> removals) {
		mDelegate.update(additions, removals);
	}

	@Override
	public void reload() {
		mDelegate.reload();
	}

	@Override
	public void save() {
		mDelegate.save();
	}

	@Override
	public void close() throws Exception {
		mDelegate.close();
	}

	// TODO: maybe we need to fold this back to OntologyState or in another interface that identifies remote objects
	public IRI getRemoteOntologyIRI() {
		return remoteOntoDocIRI;
	}
}
