package com.clarkparsia.pellet.server;

import java.util.Set;

import com.clarkparsia.pellet.server.handlers.PathHandlerSpec;
import com.clarkparsia.pellet.server.handlers.ReasonerExplainSpec;
import com.clarkparsia.pellet.server.handlers.ReasonerQuerySpec;
import com.clarkparsia.pellet.server.handlers.ReasonerUpdateSpec;
import com.clarkparsia.pellet.server.handlers.ReasonerVersionSpec;
import com.clarkparsia.pellet.server.model.ServerState;
import com.clarkparsia.pellet.server.protege.ProtegeServerStateProvider;
import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.proto.ProtoServiceDecoder;
import com.clarkparsia.pellet.service.proto.ProtoServiceEncoder;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServerModule extends AbstractModule implements Module {

	private final Configuration mSettings;

	public static TypeLiteral<Set<PathHandlerSpec>> PATH_SPECS = new TypeLiteral<Set<PathHandlerSpec>>() {};

	public PelletServerModule() {
		mSettings = null;
	}

	public PelletServerModule(final Configuration theSettings) {
		mSettings = theSettings;
	}

	@Override
	protected void configure() {
		Multibinder<PathHandlerSpec> pathsBinder = Multibinder.newSetBinder(binder(), PathHandlerSpec.class);
		pathsBinder.addBinding().to(ReasonerQuerySpec.class);
		pathsBinder.addBinding().to(ReasonerExplainSpec.class);
		pathsBinder.addBinding().to(ReasonerUpdateSpec.class);
		pathsBinder.addBinding().to(ReasonerVersionSpec.class);

		Multibinder<ServiceDecoder> decodersBinder = Multibinder.newSetBinder(binder(), ServiceDecoder.class);
		decodersBinder.addBinding().to(ProtoServiceDecoder.class);

		Multibinder<ServiceEncoder> encodersBinder = Multibinder.newSetBinder(binder(), ServiceEncoder.class);
		encodersBinder.addBinding().to(ProtoServiceEncoder.class);

		if (mSettings != null) {
			binder().bind(Configuration.class).toInstance(mSettings);
		}
		binder().bind(ServerState.class).toProvider(ProtegeServerStateProvider.class).in(Singleton.class);
	}
}
