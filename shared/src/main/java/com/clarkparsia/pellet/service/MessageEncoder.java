package com.clarkparsia.pellet.service;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface MessageEncoder<T extends Message> {

	byte[] encode(final T theObject);
}
