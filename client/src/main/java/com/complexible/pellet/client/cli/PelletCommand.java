package com.complexible.pellet.client.cli;

import com.complexible.pellet.client.api.PelletService;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

/**
 * @author Edgar Rodriguez-Diaz
 */
public abstract class PelletCommand implements Runnable {

	@Option(type = OptionType.GLOBAL, name = "-v", description = "Verbose mode")
	protected boolean verbose;

	final PelletService mPelletService;

	public PelletCommand(final PelletService thePelletService) {
		mPelletService = thePelletService;
	}

	boolean isVerbose() {
		return verbose;
	}

	PelletService service() {
		return mPelletService;
	}
}
