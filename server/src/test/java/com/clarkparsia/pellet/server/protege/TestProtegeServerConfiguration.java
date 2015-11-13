package com.clarkparsia.pellet.server.protege;

import java.util.Properties;

import com.clarkparsia.pellet.server.Configuration;
import com.google.common.base.Joiner;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class TestProtegeServerConfiguration implements Configuration {

	private final Properties mProperties;

	public TestProtegeServerConfiguration() {
		this(new String[0]);
	}

	public TestProtegeServerConfiguration(String... ontologies) {
		mProperties = new Properties();
		mProperties.setProperty(Configuration.PROTEGE_HOST, "localhost");
		mProperties.setProperty(Configuration.PROTEGE_PORT, "4875");
		mProperties.setProperty(Configuration.PROTEGE_USERNAME, "redmond");
		mProperties.setProperty(Configuration.PROTEGE_PASSWORD, "bicycle");
		mProperties.setProperty(Configuration.PROTEGE_ONTOLOGIES, Joiner.on(",").join(ontologies));
	}

	@Override
	public Properties getSettings() {
		return mProperties;
	}
}
