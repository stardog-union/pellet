package com.clarkparsia.pellet.io;

import java.io.Serializable;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;

/**
 * Immutable and serializable
 *
 * @author Edgar Rodriguez-Diaz
 */
public class SerializableNode<E extends OWLObject> extends DefaultNode<E> implements Node<E>, Serializable {

	public SerializableNode(final DefaultNode<E> theOther) {
		super(theOther.getEntities());
	}

	public SerializableNode(final Set<E> theObjects) {
		super(theObjects);
	}

	@Override
	protected E getTopEntity() {
		return null;
	}

	@Override
	protected E getBottomEntity() {
		return null;
	}
}
