package com.complexible.pellet.client;

import com.complexible.pellet.client.api.PelletService;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceTest extends PelletClientTest {

	PelletServiceProvider serviceProvider = new PelletServiceProvider(PelletService.DEFAULT_LOCAL_ENDPOINT,
	                                                                  0, 0, 0); // disable all timeouts for tests

	// TODO: write low level pellet service tests here
}
