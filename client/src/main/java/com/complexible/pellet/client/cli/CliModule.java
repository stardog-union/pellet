package com.complexible.pellet.client.cli;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class CliModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PelletCommand.class).annotatedWith(Names.named("shutdown")).to(ShutdownCommand.class);
	}
}
