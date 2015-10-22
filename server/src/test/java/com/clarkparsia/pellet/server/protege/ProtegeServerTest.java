package com.clarkparsia.pellet.server.protege;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.owlapiv3.OntologyUtils;
import com.google.common.io.Resources;
import org.antlr.runtime.RecognitionException;

import org.protege.owl.server.api.ChangeMetaData;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.api.server.Server;
import org.protege.owl.server.api.server.ServerTransport;
import org.protege.owl.server.conflict.ConflictManager;
import org.protege.owl.server.connect.local.LocalTransport;

import org.protege.owl.server.connect.local.LocalTransportImpl;
import org.protege.owl.server.connect.rmi.RMITransport;
import org.protege.owl.server.core.ServerImpl;
import org.protege.owl.server.policy.Authenticator;

import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.IRI;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ProtegeServerTest extends TestUtilities {
	private Server mServer;
	private LocalTransport mLocalTransport;

	protected final static int RMI_PORT = 4875;

	protected final static String OWL2_HISTORY = "owl2.history";
	protected final static String AGENCIES_HISTORY = "agencies.history";

	public ProtegeServerTest() {
		super();
	}

	@Before
	public void startServer() throws IOException, RecognitionException, OWLServerException {
		initializeServerRoot();

		Server core = new ServerImpl(ROOT_DIRECTORY, CONFIGURATION_DIRECTORY);
		mServer = new Authenticator(new ConflictManager(core), USERDB);

		List<ServerTransport> transports = new ArrayList<ServerTransport>();
		ServerTransport rmiTransport = new RMITransport(RMI_PORT, RMI_PORT);
		rmiTransport.start(mServer);
		transports.add(rmiTransport);
		mLocalTransport = new LocalTransportImpl();
		mLocalTransport.start(mServer);
		transports.add(mLocalTransport);

		mServer.setTransports(transports);
	}

	@After
	public void stopServer() {
		mServer.shutdown();
	}

	protected LocalTransport local() {
		return mLocalTransport;
	}

	protected static IRI root(final Client client) {
		return IRI.create(client.getScheme() +"://"+ client.getAuthority() +":"+ RMI_PORT);
	}

	protected static void checkClientOk(Client client) throws OWLServerException {
		IRI root = IRI.create(client.getScheme() + "://" + client.getAuthority());
		RemoteServerDocument doc = client.getServerDocument(root);
		assertTrue(doc instanceof RemoteServerDirectory);
		assertTrue(client.list((RemoteServerDirectory) doc).isEmpty());
	}

	protected static void createOwl2Ontology(final Client client) throws OWLServerException {
		// Needs branch with owlapi 4.1.0 - see: https://github.com/edgarRd/protege-ontology-server/tree/owlapi-4.1.0
		ClientUtilities.createServerOntology(client,
		                                     IRI.create(root(client).toString() +"/", OWL2_HISTORY),
		                                     new ChangeMetaData("Initial entry"),
		                                     OntologyUtils.loadOntology(Resources.getResource("test/data/owl2.owl")
		                                                                         .toString()));
	}

	protected static void createAgenciesOntology(final Client client) throws OWLServerException {
		ClientUtilities.createServerOntology(client,
		                                     IRI.create(root(client).toString() + "/", AGENCIES_HISTORY),
		                                     new ChangeMetaData("Initial entry"),
		                                     OntologyUtils.loadOntology(Resources.getResource("test/data/agencies.owl")
		                                                                         .toString()));
	}

}
