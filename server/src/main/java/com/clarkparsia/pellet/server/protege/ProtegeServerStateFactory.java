package com.clarkparsia.pellet.server.protege;

import java.util.Properties;

import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.ServerStateFactory;
import com.clarkparsia.pellet.server.exceptions.ProtegeConnectionException;
import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.connect.rmi.RMIClient;
import org.protege.owl.server.policy.RMILoginUtility;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerStateFactory implements ServerStateFactory {

	private static String LOCAL_SENTINEL = "local";

	private final String aHost;
	private final int aPort;
	private final String aUser;
	private final String aPassword;

	private Client mClient;


	@Inject
	public ProtegeServerStateFactory(final Configuration theConfiguration) {
		Properties aSettings = theConfiguration.getSettings();

		aHost = aSettings.getProperty("host");
		aPort = Integer.parseInt(aSettings.getProperty("port"));
		aUser = aSettings.getProperty("username");
		aPassword = aSettings.getProperty("password");
	}

	@Override
	public ServerState getInstance() throws ServerException {
		mClient = connectToProtege();

		return new ProtegeServerState(mClient);
	}

	private Client connectToProtege() throws ProtegeConnectionException {
		Preconditions.checkArgument(aUser != null);
		Preconditions.checkArgument(aPassword != null);

		try {
			if (Strings.isNullOrEmpty(aHost) || LOCAL_SENTINEL.equals(aHost)) {
				// in case we might want to do embedded server with Protege Server
				throw new IllegalArgumentException("A host is required to connect to a Protege Server");
			}
			else {
				AuthToken tim = RMILoginUtility.login("localhost", aPort, aUser, aPassword);
				RMIClient aClient = new RMIClient(tim, "localhost", aPort);
				aClient.initialise();

				return aClient;
			}
		}
		catch (Exception e) {
			throw new ProtegeConnectionException("Could not connect to Protege Server", e);
		}
	}
}
