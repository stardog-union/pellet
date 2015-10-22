package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.protege.TestProtegeServerConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class TestModule extends AbstractModule implements Module {

	@Override
	protected void configure() {
		binder().bind(Configuration.class).to(TestProtegeServerConfiguration.class).asEagerSingleton();
	}
}
