package com.complexible.pellet.client;

import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.client.cli.CliModule;
import com.complexible.pellet.client.reasoner.RemoteSchemaReasoner;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.complexible.pellet.service.reasoner.SchemaReasonerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ClientModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new CliModule());

		install(new FactoryModuleBuilder()
			        .implement(SchemaReasoner.class, RemoteSchemaReasoner.class)
			        .build(SchemaReasonerFactory.class));

		bind(PelletService.class).toProvider(PelletServiceProvider.class);
	}
}
