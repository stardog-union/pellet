package com.clarkparsia.pellet.service;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface MessageDecoder<T extends Message> {

	T decode(final byte[] theBytes);
}
