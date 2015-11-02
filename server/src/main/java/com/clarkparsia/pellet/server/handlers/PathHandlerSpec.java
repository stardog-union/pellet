package com.clarkparsia.pellet.server.handlers;

import io.undertow.server.HttpHandler;

/**
 * Defines a PathHandler specification for Pellet Server functionality
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PathHandlerSpec {

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
