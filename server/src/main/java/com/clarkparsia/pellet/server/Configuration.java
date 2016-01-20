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
	String PROTEGE_ONTOLOGIES = "protege.ontologies";

	String PELLET_HOME =            "pellet.home";
	String PELLET_HOST =            "pellet.host";
	String PELLET_PORT =            "pellet.port";
	String PELLET_UPDATE_INTERVAL = "pellet.update.interval.sec";

	Properties getSettings();
}
