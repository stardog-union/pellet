package com.clarkparsia.pellet.server;

import java.util.Set;

import com.clarkparsia.pellet.server.handlers.PathHandlerSpec;
import com.clarkparsia.pellet.server.handlers.ReasonerQuerySpec;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.protege.ProtegeServerStateProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerModule extends AbstractModule implements Module {

	private Configuration mSettings;

	public static TypeLiteral<Set<PathHandlerSpec>> PATH_SPECS = new TypeLiteral<Set<PathHandlerSpec>>() {};

	public PelletServerModule(final Configuration theSettings) {
		mSettings = theSettings;
	}

	@Override
	protected void configure() {
		Multibinder<PathHandlerSpec> pathsBinder = Multibinder.newSetBinder(binder(), PathHandlerSpec.class);
		pathsBinder.addBinding().to(ReasonerQuerySpec.class);

		binder().bind(Configuration.class).toInstance(mSettings);
		binder().bind(ServerState.class).toProvider(ProtegeServerStateProvider.class).in(Singleton.class);
	}
}
