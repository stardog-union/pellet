package com.clarkparsia.pellet.server.handlers;

import io.undertow.server.HttpHandler;

/**
 * Defines a Path-HttpHandler specification for setting up Pellet Server functionality.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PathHandlerSpec {

	/**
	 * The type of path to use for matching calls to the service. Specs defining paths as
	 * template must implement the way they'll read the path parameters.
	 */
	enum PathType {
		EXACT,  PREFIX, TEMPLATE
	}

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
	 * Gets the type of path defined for this handler.
	 *
	 * @return  the value of the {@link PathType}
	 */
	PathType getPathType();
}
