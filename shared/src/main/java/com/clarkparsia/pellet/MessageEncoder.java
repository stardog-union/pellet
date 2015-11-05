package com.clarkparsia.pellet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface MessageEncoder<T extends Message> {

	byte[] encode(final T theObject);
}
