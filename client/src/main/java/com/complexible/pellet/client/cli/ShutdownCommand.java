package com.complexible.pellet.client.cli;

import java.io.IOException;

import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.service.messages.GenericJsonMessage;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import io.airlift.airline.Command;

/**
 * @author Edgar Rodriguez-Diaz
 */
@Command(name = "shutdown", description = "Shutdown Pellet Server")
public class ShutdownCommand extends PelletCommand {

	@Inject
	public ShutdownCommand(final PelletService thePelletService) {
		super(thePelletService);
	}

	@Override
	public void run() {
		try {
			GenericJsonMessage aMessage = service().shutdown()
			                                            .execute()
			                                            .body();
			System.out.println(aMessage.message);
		}
		catch (IOException e) {
			//TODO: create our own set of Exceptions
			Throwables.propagate(e);
		}
	}
}
