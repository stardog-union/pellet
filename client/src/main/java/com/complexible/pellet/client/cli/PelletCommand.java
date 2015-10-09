package com.complexible.pellet.client.cli;

import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletCommand implements Runnable {

	@Option(type = OptionType.GLOBAL, name = "-v", description = "Verbose mode")
	protected boolean verbose;

	boolean isVerbose() {
		return verbose;
	}
}
