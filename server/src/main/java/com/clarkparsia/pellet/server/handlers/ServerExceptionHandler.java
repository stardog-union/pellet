package com.clarkparsia.pellet.server.handlers;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.json.JsonMessage;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.util.Headers;

/**
 * Handles server defined Exceptions turning them into the proper HTTP responses with the adequate
 * HTTP status code.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class ServerExceptionHandler implements HttpHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		ServerException anException = (ServerException) exchange.getAttachment(ExceptionHandler.THROWABLE);

		JsonMessage aJsonMessage = new GenericJsonMessage(anException.getMessage());

		exchange.setStatusCode(anException.getErrorCode());
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, JsonMessage.MIME_TYPE);
		exchange.getResponseSender().send(aJsonMessage.toJsonString());
		exchange.endExchange();
	}
}
