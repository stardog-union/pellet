package com.clarkparsia.pellet.server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.owlapiv3.OntologyUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.antlr.runtime.RecognitionException;

import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.ChangeMetaData;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.api.server.Server;
import org.protege.owl.server.api.server.ServerTransport;
import org.protege.owl.server.conflict.ConflictManager;
import org.protege.owl.server.connect.local.LocalTransport;

import org.protege.owl.server.connect.local.LocalTransportImpl;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.connect.rmi.RMITransport;
import org.protege.owl.server.core.ServerImpl;
import org.protege.owl.server.policy.Authenticator;

import org.protege.owl.server.policy.RMILoginUtility;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.IRI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerTest extends TestUtilities {
	private Server mServer;
	private LocalTransport mLocalTransport;

	private final static int RMI_PORT = 4875;

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

	@Test
	public void traverseFileSystem() throws Exception {
		Client client = createClient(RMI_PORT, REDMOND);

		// we know that root is a directory
		IRI root = IRI.create(client.getScheme() + "://" + client.getAuthority());
		System.out.println("Root IRI: " + root);

		// Needs branch with owlapi 4.1.0 - see: https://github.com/edgarRd/protege-ontology-server/tree/owlapi-4.1.0
		ClientUtilities.createServerOntology(client,
		                                     IRI.create(root.toString(), "/owl2.history"),
		                                     new ChangeMetaData("Initial entry"),
		                                     OntologyUtils.loadOntology(Resources.getResource("test/data/owl2.owl")
		                                                                         .toString()));

		ClientUtilities.createServerOntology(client,
		                                     IRI.create(root.toString(), "/agencies.history"),
		                                     new ChangeMetaData("Initial entry"),
		                                     OntologyUtils.loadOntology(Resources.getResource("test/data/agencies.owl")
		                                                                         .toString()));

		RemoteServerDocument aRoot = client.getServerDocument(root);
		List<RemoteOntologyDocument> docs = Lists.newLinkedList();

		list(client, (RemoteServerDirectory) aRoot, docs);

		System.out.println(Joiner.on(",").join(docs));
	}

	private void list(final Client client,
	                  final RemoteServerDirectory theDir,
	                  final List<RemoteOntologyDocument> theCollector) throws OWLServerException {
		for (RemoteServerDocument doc : client.list(theDir)) {
			if (doc instanceof RemoteOntologyDocument) {
				theCollector.add((RemoteOntologyDocument) doc);
			}
			else {
				// recursive!! -- don't try with a lot of docs.
				list(client, (RemoteServerDirectory) doc, theCollector);
			}
		}
	}

	private void checkClientOk(Client client) throws OWLServerException {
		IRI root = IRI.create(client.getScheme() + "://" + client.getAuthority());
		RemoteServerDocument doc = client.getServerDocument(root);
		assertTrue(doc instanceof RemoteServerDirectory);
		assertTrue(client.list((RemoteServerDirectory) doc).isEmpty());
	}

}
