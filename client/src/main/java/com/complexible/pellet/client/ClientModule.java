package com.complexible.pellet.client;

import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.client.cli.CliModule;
import com.google.inject.AbstractModule;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ClientModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new CliModule());

		bind(PelletService.class).toProvider(PelletServiceProvider.class);
	}
}
