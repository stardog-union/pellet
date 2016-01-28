package com.clarkparsia.pellet.server;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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

		private static final int UPDATE_INTERVAL_DEFAULT_IN_SECONDS = 300;

		PelletSettings(final Properties theSettings) {
			settings = theSettings;
		}

		public String home() {
			return getProperty(settings, Configuration.PELLET_HOME, System.getProperty("user.dir"));
		}

		public String host() {
			return getProperty(settings, Configuration.PELLET_HOST, PelletServer.DEFAULT_HOST);
		}

		public int port() {
			return getPropertyAsInteger(settings, Configuration.PELLET_PORT, PelletServer.DEFAULT_PORT);
		}

		public int updateIntervalInSeconds() {
			return getPropertyAsInteger(settings, Configuration.PELLET_UPDATE_INTERVAL, UPDATE_INTERVAL_DEFAULT_IN_SECONDS);
		}
	}

	public static class ProtegeSettings {

		private static final String DEFAULT_PROTEGE_HOST = "localhost";
		private static final int DEFAULT_PROTEGE_PORT = 5100;

		private Properties settings;

		ProtegeSettings(final Properties theSettings) {
			settings = theSettings;
		}

		public String host() {
			return getProperty(settings, Configuration.PROTEGE_HOST, DEFAULT_PROTEGE_HOST);
		}

		public int port() {
			return getPropertyAsInteger(settings, Configuration.PROTEGE_PORT, DEFAULT_PROTEGE_PORT);
		}

		public String username() {
			return getProperty(settings, Configuration.PROTEGE_USERNAME, null);
		}

		public String password() {
			return getProperty(settings, Configuration.PROTEGE_PASSWORD, null);
		}

		public Set<String> ontologies() {
			final String ontologiesList = getProperty(settings, Configuration.PROTEGE_ONTOLOGIES, "");

			// try parsing the ontology names list
			return ImmutableSet.copyOf(Splitter.on(',').omitEmptyStrings().trimResults().split(ontologiesList));
		}
	}

	public static String getProperty(Properties properties, String key, String defaultValue) {
		String val = properties.getProperty(key, defaultValue);
		if (val == null) {
			throw new IllegalArgumentException("Value of configuration property " + key + " is missing");
		}
		return val;
	}

	public static int getPropertyAsInteger(Properties properties, String key, int defaultValue) {
		String val = properties.getProperty(key);
		if (val == null) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(val);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Value of configuration property " + key + " is not a valid integer: " + defaultValue);
		}
	}

	public static boolean getPropertyAsBoolean(Properties properties, String key, boolean defaultValue) {
		String val = properties.getProperty(key);
		if (val == null) {
			return defaultValue;
		}
		else if (val.equalsIgnoreCase("true")) {
			return true;
		}
		else if (val.equalsIgnoreCase("false")) {
			return true;
		}
		else {
			throw new IllegalArgumentException("Value of configuration property " + key + " is not a valid boolean: " + defaultValue);
		}
	}
}
