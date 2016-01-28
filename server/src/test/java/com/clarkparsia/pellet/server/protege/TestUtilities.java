package com.clarkparsia.pellet.server.protege;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.protege.osgi.framework.Launcher;
import org.protege.owl.server.Activator;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.ChangeMetaData;
import org.protege.owl.server.api.OntologyDocumentRevision;
import org.protege.owl.server.api.SingletonChangeHistory;
import org.protege.owl.server.api.UserId;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.connect.local.LocalTransport;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.policy.Authenticator;
import org.protege.owl.server.policy.RMILoginUtility;
import org.protege.owl.server.policy.UserDatabase;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.xml.sax.SAXException;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class TestUtilities {
	private static Logger logger = Logger.getLogger(TestUtilities.class.getCanonicalName());

	public static final String BASE = "file:test/data/";

	public static final File SERVER_ROOT = new File("target/server-distribution/server");
	public static final File ROOT_DIRECTORY = new File(SERVER_ROOT, "root");
	public static final File CONFIGURATION_DIRECTORY = new File(SERVER_ROOT, "configuration");
	public static final File PELLET_DIRECTORY = new File(SERVER_ROOT, "pellet");
	public static final String PREFIX;
	static {
		StringBuffer sb = new StringBuffer();
		sb.append("src");
		sb.append(File.separator);
		sb.append("test");
		sb.append(File.separator);
		sb.append("resources");
		sb.append(File.separator);
		PREFIX = sb.toString();
	}

	public static final UserId FERGERSON = new UserId("fergerson");
	public static final UserId GUEST     = new UserId("guest");
	public static final UserId REDMOND   = new UserId("redmond");
	public static final UserId VENDETTI  = new UserId("vendetti");
	public static final Map<UserId, String> PASSWORD_MAP = new TreeMap<UserId, String>();
	public static final UserDatabase USERDB;
	static {
		PASSWORD_MAP.put(FERGERSON, "ncbo");
		PASSWORD_MAP.put(GUEST,     "guest");
		PASSWORD_MAP.put(REDMOND,   "bicycle");
		PASSWORD_MAP.put(VENDETTI,  "protege");

		// Create UserDatabase
		USERDB = new UserDatabase();
		for (Map.Entry<UserId, String> aEntry : PASSWORD_MAP.entrySet()) {
			USERDB.addUser(aEntry.getKey().getUserName(), aEntry.getValue());
		}
	}

	TestUtilities() {
	}

	public static File initializeServerRoot() {
		delete(ROOT_DIRECTORY);
		delete(CONFIGURATION_DIRECTORY);
		delete(PELLET_DIRECTORY);
		ROOT_DIRECTORY.mkdirs();
		CONFIGURATION_DIRECTORY.mkdirs();
		PELLET_DIRECTORY.mkdirs();

		return ROOT_DIRECTORY;
	}

	public static Framework startServer(String osgiConfiguration, String serverConfiguration) throws IOException,
	                                                                                                 ParserConfigurationException,
	                                                                                                 SAXException,
	                                                                                                 InstantiationException,
	                                                                                                 IllegalAccessException,
	                                                                                                 ClassNotFoundException,
	                                                                                                 BundleException,
	                                                                                                 InterruptedException {
		System.setProperty(Activator.SERVER_CONFIGURATION_PROPERTY, PREFIX + serverConfiguration);
		Launcher launcher = new Launcher(new File(PREFIX, osgiConfiguration));
		launcher.start(false);
		return launcher.getFramework();
	}

	public static void stopServer(Framework framework) throws OWLServerException {
		try {
			framework.stop();
			framework.waitForStop(60 * 60 * 1000);
		}
		catch (InterruptedException ie) {
			throw new OWLServerException(ie);
		}
		catch (BundleException be) {
			throw new OWLServerException(be);
		}
	}

	public static Client createClient(int rmiPort, UserId u) throws RemoteException, NotBoundException {
		AuthToken auth = RMILoginUtility.login("localhost", rmiPort, u.getUserName(), PASSWORD_MAP.get(u));
		RMIClient client = new RMIClient(auth, "localhost", rmiPort);
		client.initialise();
		return client;
	}

	public static Client createClient(LocalTransport transport, UserId u) {
		AuthToken auth = Authenticator.localLogin(transport, u.getUserName(), PASSWORD_MAP.get(u));
		return transport.getClient(auth);
	}

	public static void commit(Client client, VersionedOntologyDocument vont, OWLOntologyChange... changes) throws OWLServerException {
		OWLOntology ontology = vont.getOntology();
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		List<OWLOntologyChange> changeList = new ArrayList<OWLOntologyChange>();
		for (OWLOntologyChange change : changes) {
			changeList.add(change);
		}
		manager.applyChanges(changeList);
		ClientUtilities.commit(client, new ChangeMetaData(), vont);
	}


	public static void rawCommit(Client client, RemoteOntologyDocument doc, OntologyDocumentRevision revision, OWLOntologyChange... changes) throws
	                                                                                                                                         OWLServerException {
		List<OWLOntologyChange> changeList = new ArrayList<OWLOntologyChange>();
		for (OWLOntologyChange change : changes) {
			changeList.add(change);
		}
		SingletonChangeHistory changeHistory = client.getDocumentFactory().createChangeDocument(changeList, new ChangeMetaData(), revision);
		client.commit(doc, changeHistory);
	}

	protected static void delete(File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				delete(child);
			}
		}
		f.delete();
	}

	/**
	 * This routine creates a temporary directory and then creates a file inside that directory.
	 * <p/>
	 * This routine is sometimes needed when running a test that needs a temporary file but then also needs to
	 * be able to write to the containing directory.  When we created the temporary file with File.createTempFile(),
	 * it was noticed that when certain tests (e.g. the tests that serialize server side ontologies) were run by different
	 * users the second run would sometimes fail because the second user would fail to have write access to all the contents of
	 * the containing directory (e.g. the .owlserver directory).
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static File createFileInTempDirectory(String name) throws IOException {
		File tmpDirectory = File.createTempFile("Save", "test");
		tmpDirectory.delete();
		if (!tmpDirectory.mkdir()) {
			throw new IOException("Coud not create temporary directory " + tmpDirectory);
		}
		logger.info("Created temporary directory " + tmpDirectory + " for the file " + name);
		return new File(tmpDirectory, name);
	}
}
