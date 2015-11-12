package com.clarkparsia.pellet.server;

import java.util.Properties;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface Configuration {

	String FILENAME = "server.properties";

	String PROTEGE_HOST =       "protege.host";
	String PROTEGE_PORT =       "protege.port";
	String PROTEGE_USERNAME =   "protege.username";
	String PROTEGE_PASSWORD =   "protege.password";

	Properties getSettings();
}
