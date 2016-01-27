package com.clarkparsia.pellet.server.protege;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.clarkparsia.pellet.server.ConfigurationReader;
import com.clarkparsia.pellet.server.exceptions.ProtegeConnectionException;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.exception.OWLServerException;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.policy.RMILoginUtility;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtegeServiceUtils {

	private ProtegeServiceUtils() {
		throw new IllegalStateException("Can't be instantiated!");
	}

	public static Collection<RemoteOntologyDocument> list(final Client client,
	                                                      final RemoteServerDirectory theDir) throws
	                                                                                          OWLServerException {
		List<RemoteOntologyDocument> docs = Lists.newLinkedList();
		Stack<RemoteServerDirectory> dirsToProc = new Stack<RemoteServerDirectory>();
		dirsToProc.push(theDir);

		while (!dirsToProc.empty()) {
			for (final RemoteServerDocument doc : client.list(dirsToProc.pop())) {
				if (doc != null) {
					if (doc instanceof RemoteOntologyDocument) {
						docs.add((RemoteOntologyDocument) doc);
					}
					else {
						dirsToProc.push((RemoteServerDirectory) doc);
					}
				}
			}
		}

		return docs;
	}

	public static Client connect(final ConfigurationReader config) throws ProtegeConnectionException {
		final ConfigurationReader.ProtegeSettings protege = config.protegeSettings();
		final String aHost = protege.host();

		try {
			if (Strings.isNullOrEmpty(aHost) || "local".equals(aHost)) {
				// in case we might want to do embedded server with Protege Server
				throw new IllegalArgumentException("A host is required to connect to a Protege Server");
			}
			else {
				AuthToken authToken = RMILoginUtility.login(aHost,
				                                            protege.port(),
				                                            protege.username(),
				                                            protege.password());
				RMIClient aClient = new RMIClient(authToken, aHost, protege.port());
				aClient.initialise();

				return aClient;
			}
		}
		catch (Exception e) {
			throw new ProtegeConnectionException("Could not connect to Protege Server", e);
		}
	}
}
