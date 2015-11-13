package com.clarkparsia.pellet.server;

import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ConfigurationReader {

	private static final Logger LOGGER = Logger.getLogger(ConfigurationReader.class.getName());

	private final ProtegeSettings protegeSettings;
	private final PelletSettings pelletSettings;

	private ConfigurationReader(final Configuration theConfig) {
		protegeSettings = new ProtegeSettings(theConfig.getSettings());
		pelletSettings = new PelletSettings(theConfig.getSettings());
	}

	public ProtegeSettings protegeSettings() {
		return protegeSettings;
	}

	public PelletSettings pelletSettings() {
		return pelletSettings;
	}

	public static ConfigurationReader of(final Configuration theConfig) {
		return new ConfigurationReader(theConfig);
	}

	public static class PelletSettings {
		private Properties settings;

		private static final boolean STRICT_DEFAULT = false;

		private static final int UPDATE_INTERVAL_DEFAULT_IN_SECONDS = 15;

		PelletSettings(final Properties theSettings) {
			settings = theSettings;
		}

		public boolean isStrict() {
			boolean isStrict = STRICT_DEFAULT;
			final String strict = settings.getProperty(Configuration.PELLET_STRICT,
			                                           String.valueOf(STRICT_DEFAULT));
			try {
				isStrict = Boolean.parseBoolean(strict);
			}
			catch (Exception e) {
				LOGGER.info("Couldn't parse Pellet strict setting from configuration, default: "+ STRICT_DEFAULT);
			}

			return isStrict;
		}

		public int updateIntervalInSeconds() {
			final String val = settings.getProperty(Configuration.PELLET_UPDATE_INTERVAL);
			int intervalSecs = UPDATE_INTERVAL_DEFAULT_IN_SECONDS;

			try {
				intervalSecs = Integer.parseInt(val);
			}
			catch (Exception e) {
				LOGGER.info("Couldn't parse Pellet Server's update interval from configuration, " +
				            "using: "+ intervalSecs +" seconds");
			}

			return intervalSecs;
		}
	}

	public static class ProtegeSettings {

		private static final String HOST_DEFAULT = "localhost";
		private static final int PORT_DEFAULT = 4875;

		private Properties settings;

		ProtegeSettings(final Properties theSettings) {
			settings = theSettings;
		}

		public String host() {
			final String aHost = settings.getProperty(Configuration.PROTEGE_HOST);

			return !Strings.isNullOrEmpty(aHost) ? aHost : HOST_DEFAULT;
		}

		public int port() {
			final String aPort = settings.getProperty(Configuration.PROTEGE_PORT);
			int portNumber = PORT_DEFAULT;

			try {
				portNumber = Integer.parseInt(aPort);
			}
			catch (Exception e) {
				LOGGER.info("Couldn't parse Protege Server's Port from configuration, trying port "+ PORT_DEFAULT);
			}

			return portNumber;
		}

		public String username() {
			final String username = settings.getProperty(Configuration.PROTEGE_USERNAME);

			Preconditions.checkArgument(!Strings.isNullOrEmpty(username),
			                            "Setting 'protege.username' must not be empty");

			return username;
		}

		public String password() {
			final String username = settings.getProperty(Configuration.PROTEGE_PASSWORD);

			Preconditions.checkArgument(!Strings.isNullOrEmpty(username),
			                            "Setting 'protege.username' must not be empty");

			return username;
		}

		public Collection<String> ontologies() {
			final String ontologiesList = settings.getProperty(Configuration.PROTEGE_ONTOLOGIES, "");
			final ImmutableList.Builder<String> ontoNames = ImmutableList.builder();

			if (Strings.isNullOrEmpty(ontologiesList)) {
				return ontoNames.build();
			}

			// try parsing the ontology names list
			for (String aOntoName : ontologiesList.split(",")) {
				aOntoName = aOntoName.trim();
				// only add the ones with valid names ending in '.history'
				if (aOntoName.endsWith(".history")) {
					ontoNames.add(aOntoName);
				}
				else {
					LOGGER.info("Ignoring ontology name ["+ aOntoName +"] in configuration " +
					            "- it must be a valid name ending in '.history'");
				}
			}

			return ontoNames.build();
		}
	}
}
