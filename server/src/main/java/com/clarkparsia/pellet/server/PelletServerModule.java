package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.protege.ProtegeServerStateProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerModule extends AbstractModule implements Module {

	private Configuration mSettings;

	public PelletServerModule(final Configuration theSettings) {
		mSettings = theSettings;
	}

	@Override
	protected void configure() {
		binder().bind(Configuration.class).toInstance(mSettings);
		binder().bind(ServerState.class).toProvider(ProtegeServerStateProvider.class).in(Singleton.class);
	}
}
