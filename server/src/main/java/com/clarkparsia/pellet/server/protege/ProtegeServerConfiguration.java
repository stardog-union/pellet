package com.clarkparsia.pellet.server.protege;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellet.server.Configuration;
import com.google.common.base.Throwables;

/**
 * Protege server configuration reader.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ProtegeServerConfiguration implements Configuration {

	private static final Logger LOGGER = Logger.getLogger(ProtegeServerConfiguration.class.getName());

	final Properties mProps;

	public ProtegeServerConfiguration(final File thePathToConfig) throws IOException {
		mProps = new Properties();
		try {
			mProps.load(new FileInputStream(thePathToConfig));
		}
		catch (FileNotFoundException fnfe) {
			LOGGER.log(Level.SEVERE, "Configuration file for the Protege Server Settings was not found", fnfe);
			Throwables.propagate(fnfe);
		}
	}

	@Override
	public Properties getSettings() {
		return mProps;
	}
}
