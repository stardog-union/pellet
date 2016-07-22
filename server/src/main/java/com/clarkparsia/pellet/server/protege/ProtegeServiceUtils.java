package com.clarkparsia.pellet.server.protege;

import com.clarkparsia.pellet.server.ConfigurationReader;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.protege.editor.owl.client.LocalHttpClient;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtegeServiceUtils {

	private ProtegeServiceUtils() {
		throw new IllegalStateException("Can't be instantiated!");
	}

	public static LocalHttpClient connect(final ConfigurationReader config) {
		final ConfigurationReader.ProtegeSettings protege = config.protegeSettings();
		final String aHost = protege.host();

		if (Strings.isNullOrEmpty(aHost) || "local".equals(aHost)) {
			// in case we might want to do embedded server with Protege Server
			throw new IllegalArgumentException("A host is required to connect to a Protege Server");
		}
		else {
			try {
				LocalHttpClient aClient = new LocalHttpClient(protege.username(), protege.password(), "http://" + protege.host() + ":" + protege.port());

				return aClient;
			}
			catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}
	}
}
