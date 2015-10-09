package com.complexible.pellet.client;

import java.io.IOException;

import com.complexible.pellet.client.cli.PelletCommand;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class Client {

	public static void main(String[] args) throws IOException {
		Injector aInjector = Guice.createInjector(new ClientModule());

		PelletCommand aShutdown = aInjector.getInstance(Key.get(PelletCommand.class,
		                                                        Names.named("shutdown")));
		aShutdown.run();
	}

}
