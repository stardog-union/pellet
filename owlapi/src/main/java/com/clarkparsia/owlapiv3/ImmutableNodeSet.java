
package com.clarkparsia.owlapiv3;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Evren Sirin
 */
public class ImmutableNodeSet<E extends OWLObject> implements NodeSet<E> {
    private final Set<Node<E>> nodes;

    public static <E extends OWLObject> ImmutableNodeSet<E> of(Node<E> node) {
        return new ImmutableNodeSet<E>(ImmutableSet.of(node));
    }
    public static <E extends OWLObject> ImmutableNodeSet<E> of(Set<Node<E>> nodes) {
        return new ImmutableNodeSet<E>(ImmutableSet.copyOf(nodes));
    }

    private ImmutableNodeSet(ImmutableSet<Node<E>> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Set<Node<E>> getNodes() {
        return nodes;
    }

    @Override
    public Set<E> getFlattened() {
        Set<E> result = Sets.newHashSet();

        for (Node<E> node : nodes) {
            result.addAll(node.getEntities());
        }

        return result;
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean containsEntity(E e) {
        for (Node<E> node : nodes) {
            if (node.contains(e)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSingleton() {
        return nodes.size() == 1;
    }

    @Override
    public boolean isTopSingleton() {
        return isSingleton() && Iterables.get(nodes, 0).isTopNode();
    }

    @Override
    public boolean isBottomSingleton() {
        return isSingleton() && Iterables.get(nodes, 0).isBottomNode();
    }

    @Override
    public Iterator<Node<E>> iterator() {
        return nodes.iterator();
    }

    @Override
    public String toString() {
        return "Nodeset" + nodes;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        } else if(obj == this) {
            return true;
        } else if(!(obj instanceof NodeSet)) {
            return false;
        } else {
            NodeSet other = (NodeSet)obj;
            return nodes.equals(other.getNodes());
        }
    }

    @Override
    public int hashCode() {
        return nodes.hashCode();
    }
}
