package com.clarkparsia.pellet.server.handlers;

import io.undertow.server.HttpHandler;

/**
 * Defines a PathHandler specification for Pellet Server functionality
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PathHandlerSpec {

	/**
	 * Gets the path in which the Handler will listen.
	 *
	 * @return  the path
	 */
	String getPath();

	/**
	 * Gets the {@link HttpHandler} implementing the functionality.
	 *
	 * @return  the http handler
	 */
	HttpHandler getHandler();

	/**
	 * Gets whether this spec contains an exact path or not.
	 *
	 * @return  true if the path is exact, false otherwise
	 */
	boolean isExactPath();
}
