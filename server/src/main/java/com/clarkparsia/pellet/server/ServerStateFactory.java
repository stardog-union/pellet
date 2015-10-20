package com.clarkparsia.pellet.server;

import com.clarkparsia.pellet.server.exceptions.ServerException;
import com.clarkparsia.pellet.server.model.ServerState;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface ServerStateFactory {

	ServerState getInstance() throws ServerException;
}
