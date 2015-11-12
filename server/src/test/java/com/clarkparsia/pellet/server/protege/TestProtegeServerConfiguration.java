package com.clarkparsia.pellet.server.protege;

import java.util.Properties;

import com.clarkparsia.pellet.server.Configuration;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class TestProtegeServerConfiguration implements Configuration {

	private final Properties mProperties;

	public TestProtegeServerConfiguration() {
		mProperties = new Properties();
		mProperties.setProperty(Configuration.PROTEGE_HOST, "localhost");
		mProperties.setProperty(Configuration.PROTEGE_PORT, "4875");
		mProperties.setProperty(Configuration.PROTEGE_USERNAME, "redmond");
		mProperties.setProperty(Configuration.PROTEGE_PASSWORD, "bicycle");
	}

	@Override
	public Properties getSettings() {
		return mProperties;
	}
}
