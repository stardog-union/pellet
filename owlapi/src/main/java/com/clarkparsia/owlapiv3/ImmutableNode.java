package com.clarkparsia.owlapiv3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.util.OWLAPIPreconditions;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryInternalsImplNoCache;

public class ImmutableNode<E extends OWLObject> implements Node<E> {    
    public static <E extends OWLObject> ImmutableNode<E> of(E entity) {
        return new ImmutableNode<E>(ImmutableSet.of(entity));
    }

    public static <E extends OWLObject> ImmutableNode<E> of(Set<E> entities) {
        return new ImmutableNode<E>(ImmutableSet.copyOf(entities));
    }

    private final Set<E> entities;

    private ImmutableNode(ImmutableSet<E> entities) {
        this.entities = entities;
    }

    protected E getTopEntity() {
        return null;
    }

    protected E getBottomEntity() {
        return null;
    }

    public boolean isTopNode() {
        return this.entities.contains(this.getTopEntity());
    }

    public boolean isBottomNode() {
        return this.entities.contains(this.getBottomEntity());
    }

    @Nonnull
    public Set<E> getEntities() {
        return this.entities;
    }

    public int getSize() {
        return this.entities.size();
    }

    public boolean contains(E entity) {
        return this.entities.contains(entity);
    }

    @Nonnull
    public Set<E> getEntitiesMinus(E e) {
        HashSet result = new HashSet(this.entities);
        result.remove(e);
        return result;
    }

    @Nonnull
    public Set<E> getEntitiesMinusTop() {
        return this.getEntitiesMinus(this.getTopEntity());
    }

    @Nonnull
    public Set<E> getEntitiesMinusBottom() {
        return this.getEntitiesMinus(this.getBottomEntity());
    }

    public boolean isSingleton() {
        return this.entities.size() == 1;
    }

    @Nonnull
    public E getRepresentativeElement() {
        return this.entities.iterator().next();
    }

    @Nonnull
    public Iterator<E> iterator() {
        return this.entities.iterator();
    }

    @Nonnull
    public String toString() {
        return "Node" + entities;
    }

    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        } else if(obj == this) {
            return true;
        } else if(!(obj instanceof Node)) {
            return false;
        } else {
            Node other = (Node)obj;
            return this.entities.equals(other.getEntities());
        }
    }

    public int hashCode() {
        return this.entities.hashCode();
    }
}
