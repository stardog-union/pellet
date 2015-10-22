package com.clarkparsia.pellet.server.protege;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;

import com.google.common.base.Joiner;
import org.junit.Test;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.policy.Authenticator;
import org.protege.owl.server.policy.RMILoginUtility;
import org.semanticweb.owlapi.model.IRI;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class MiscTest extends ProtegeServerTest {

	@Test
	public void accessLocally() throws OWLServerException {
		AuthToken token = Authenticator.localLogin(local(), REDMOND.getUserName(), PASSWORD_MAP.get(REDMOND));
		Client client = local().getClient(token);
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

		createOwl2Ontology(client);
		createAgenciesOntology(client);

		IRI root = root(client);
		RemoteServerDocument aRoot = client.getServerDocument(root);
		Collection<RemoteOntologyDocument> docs = ProtegeService.list(client, (RemoteServerDirectory) aRoot);

		System.out.println(Joiner.on(",").join(docs));
	}
}
