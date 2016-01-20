package com.clarkparsia.pellet.server.protege;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.owlapiv3.OntologyUtils;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
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

import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class ProtegeServerTest extends TestUtilities {
	private static Server mServer;
	private static LocalTransport mLocalTransport;

	protected final static int RMI_PORT = 4875;

	protected final static Path TEST_HOME = Paths.get(".test-home");

	protected final static String OWL2_HISTORY = "owl2.history";
	protected final static String AGENCIES_HISTORY = "agencies.history";

	public ProtegeServerTest() {
		super();
	}

	@Before
	public void before() throws Exception {
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
	public void after() throws Exception {
		mServer.shutdown();
		OntologyUtils.clearOWLOntologyManager();
		cleanHome();
	}

	public static void cleanHome() {
		final TreeTraverser<File> aTraverser = com.google.common.io.Files.fileTreeTraverser();

		for (File aFile : aTraverser.postOrderTraversal(TEST_HOME.toFile())) {
			aFile.delete();
		}
	}

	protected LocalTransport local() {
		return mLocalTransport;
	}

	protected static IRI root(final Client client) {
		return IRI.create(client.getScheme() +"://"+ client.getAuthority() +":"+ RMI_PORT);
	}

	protected static void removeAll() {
		delete(ROOT_DIRECTORY);
	}

	protected static void checkClientOk(Client client) throws OWLServerException {
		IRI root = IRI.create(client.getScheme() + "://" + client.getAuthority());
		RemoteServerDocument doc = client.getServerDocument(root);
		assertTrue(doc instanceof RemoteServerDirectory);
		assertTrue(client.list((RemoteServerDirectory) doc).isEmpty());
	}

	protected static IRI createOwl2Ontology(final Client client) throws OWLServerException {
		ClientUtilities.createServerOntology(client,
		                                     IRI.create(root(client).toString() +"/", OWL2_HISTORY),
		                                     new ChangeMetaData("Initial entry"),
		                                     OntologyUtils.loadOntology(Resources.getResource("test/data/owl2.owl")
		                                                                         .toString()));
		return IRI.create("http://www.example.org/test");
	}

	protected static IRI createAgenciesOntology(final Client client) throws OWLServerException {
		ClientUtilities.createServerOntology(client,
		                                     IRI.create(root(client).toString() + "/", AGENCIES_HISTORY),
		                                     new ChangeMetaData("Initial entry"),
		                                     OntologyUtils.loadOntology(Resources.getResource("test/data/agencies.owl")
		                                                                         .toString()));
		return  IRI.create("http://www.owl-ontologies.com/unnamed.owl");
	}

}
