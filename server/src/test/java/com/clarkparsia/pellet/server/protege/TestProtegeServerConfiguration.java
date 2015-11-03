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
		mProperties.setProperty(Configuration.HOST, "localhost");
		mProperties.setProperty(Configuration.PORT, "4875");
		mProperties.setProperty(Configuration.USERNAME, "redmond");
		mProperties.setProperty(Configuration.PASSWORD, "bicycle");
	}

	@Override
	public Properties getSettings() {
		return mProperties;
	}
}
