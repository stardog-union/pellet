package com.clarkparsia.pellet.server.handlers;

import io.undertow.server.HttpHandler;

/**
 * An extended HttpHandler interface that specifies the path and the method required for this function.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface RoutingHandler extends HttpHandler {
	/**
	 * Gets the path in which the Handler will listen.
	 *
	 * @return  the path
	 */
	String getPath();

	/**
	 * Gets the HTTP method the handle supports.
	 *
	 * @return  the path
	 */
	String getMethod();
}
