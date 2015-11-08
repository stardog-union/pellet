package com.clarkparsia.pellet.service.io;

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class SerializableNodeSet<E extends OWLObject> extends DefaultNodeSet<E> implements NodeSet<E>, Serializable {

	private SerializableNodeSet(final Set<Node<E>> theNodes) {
		super(theNodes);
	}

	@Override
	protected DefaultNode<E> getNode(final E entity) {
		return null;
	}

	@Override
	protected DefaultNode<E> getNode(final Set<E> entities) {
		return null;
	}

	public static <T extends OWLObject> SerializableNodeSet<T> create(final NodeSet<T> theOriginal) {
		DefaultNodeSet<T> defaultNodeSet = (DefaultNodeSet) theOriginal;
		Set<Node<T>> nodes = Sets.newLinkedHashSet();

		for (Node<T> node : defaultNodeSet) {
			DefaultNode<T> defaultNode = (DefaultNode) node;
			SerializableNode<T> serializableNode = new SerializableNode<T>(defaultNode);
			nodes.add(serializableNode);
		}

		return new SerializableNodeSet<T>(nodes);
	}

	public static <T extends OWLObject> SerializableNodeSet<T> create(final Set<Node<T>> theNodeSet) {
		return new SerializableNodeSet<T>(theNodeSet);
	}
}
