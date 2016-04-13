package com.complexible.pellet.client;

import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.clarkparsia.pellet.service.reasoner.SchemaReasonerFactory;
import com.complexible.pellet.client.reasoner.RemoteSchemaReasoner;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ClientModule extends AbstractModule {

	private final String mEndpoint;

	private final long mConnTimeoutMin;

	private final long mReadTimeoutMin;

	private final long mWriteTimeoutMin;

	public ClientModule(final String theEndpoint,
	                    final long theConnTimeoutMin, final long theReadTimeoutMin, final long theWriteTimeoutMin) {
		mEndpoint = theEndpoint;
		mConnTimeoutMin = theConnTimeoutMin;
		mReadTimeoutMin = theReadTimeoutMin;
		mWriteTimeoutMin = theWriteTimeoutMin;
	}

	public ClientModule(final String theEndpoint) {
		this(theEndpoint,
		     3, 3, 3); // timeouts in Minutes
	}

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
			        .implement(SchemaReasoner.class, RemoteSchemaReasoner.class)
			        .build(SchemaReasonerFactory.class));

		bind(String.class).annotatedWith(Names.named("endpoint"))
		                  .toInstance(mEndpoint);
		bind(Long.class).annotatedWith(Names.named("conn_timeout"))
		                   .toInstance(mConnTimeoutMin);
		bind(Long.class).annotatedWith(Names.named("read_timeout"))
		                   .toInstance(mReadTimeoutMin);
		bind(Long.class).annotatedWith(Names.named("write_timeout"))
		                   .toInstance(mWriteTimeoutMin);

		bind(PelletService.class).toProvider(PelletServiceProvider.class).in(Singleton.class);
	}
}
