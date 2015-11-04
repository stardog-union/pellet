package com.clarkparsia.pellet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface MessageEncoder<T> {

	byte[] encode(final T theObject);
}
