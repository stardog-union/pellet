package com.clarkparsia.pellet.server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.RecognitionException;

import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.api.server.ServerTransport;
import org.protege.owl.server.conflict.ConflictManager;
import org.protege.owl.server.connect.local.LocalTransport;

import org.protege.owl.server.connect.local.LocalTransportImpl;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.connect.rmi.RMITransport;
import org.protege.owl.server.core.ServerImpl;
import org.protege.owl.server.policy.Authenticator;

import org.protege.owl.server.policy.RMILoginUtility;
import org.semanticweb.owlapi.model.IRI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerTest extends TestUtilities {
	private org.protege.owl.server.api.server.Server mServer;
	private LocalTransport mLocalTransport;

	private final static int RMI_PORT = 4875;

	public ProtegeServerTest() {
		super();
	}

	@Before
	public void startServer() throws IOException, RecognitionException, OWLServerException {
		initializeServerRoot();

		org.protege.owl.server.api.server.Server core = new ServerImpl(ROOT_DIRECTORY,
		                                                               CONFIGURATION_DIRECTORY);
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

	@Test
	public void accessLocally() throws OWLServerException {
		AuthToken token = Authenticator.localLogin(mLocalTransport, REDMOND.getUserName(), PASSWORD_MAP.get(REDMOND));
		Client client = mLocalTransport.getClient(token);
		checkClientOk(client);
	}

	@Test
	public void accessRemotely() throws OWLServerException, RemoteException, NotBoundException {
		AuthToken tim = RMILoginUtility.login("localhost", RMI_PORT, REDMOND.getUserName(), PASSWORD_MAP.get(REDMOND));
		RMIClient client = new RMIClient(tim, "localhost", RMI_PORT);
		client.initialise();
		checkClientOk(client);
	}


	private void checkClientOk(Client client) throws OWLServerException {
		IRI root = IRI.create(client.getScheme() + "://" + client.getAuthority());
		RemoteServerDocument doc = client.getServerDocument(root);
		assertTrue(doc instanceof RemoteServerDirectory);
		assertTrue(client.list((RemoteServerDirectory) doc).isEmpty());
	}

}
