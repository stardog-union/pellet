package com.clarkparsia.pellet;

import com.google.protobuf.ByteString;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface MessageDecoder<T extends Message> {

	T decode(final ByteString theBytes);
}
