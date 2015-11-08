package com.clarkparsia.pellet.service;

import com.clarkparsia.pellet.service.io.EncodingException;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface MessageEncoder<T extends Message> {

	byte[] encode(final T theObject) throws EncodingException;
}
