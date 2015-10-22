package com.clarkparsia.pellet.server;

import java.util.Properties;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.protege.ProtegeServerStateProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerModule extends AbstractModule implements Module {

	@Override
	protected void configure() {
		binder().bind(Configuration.class).to(LocalProtegeConfiguration.class).asEagerSingleton();
		binder().bind(ServerState.class).toProvider(ProtegeServerStateProvider.class).in(Singleton.class);
	}

	static class LocalProtegeConfiguration implements Configuration {

		private final Properties mProperties;

		LocalProtegeConfiguration() {
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
}
