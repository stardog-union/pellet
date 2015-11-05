package com.clarkparsia.pellet.messages;

import java.util.Objects;
import java.util.Set;

import com.clarkparsia.pellet.ProtoMessage;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class UpdateRequest extends ProtoMessage {

	private final Set<OWLAxiom> additions;

	private final Set<OWLAxiom> removals;


	public UpdateRequest(final Set<OWLAxiom> theAdditions,
	                     final Set<OWLAxiom> theRemovals) {
		additions = ImmutableSet.copyOf(theAdditions);
		removals = ImmutableSet.copyOf(theRemovals);
	}

	public Set<OWLAxiom> getAdditions() {
		return additions;
	}

	public Set<OWLAxiom> getRemovals() {
		return removals;
	}

	@Override
	public boolean equals(final Object theOther) {
		if (this == theOther) {
			return true;
		}
		if (!(theOther instanceof UpdateRequest)) {
			return false;
		}

		UpdateRequest otherUR = (UpdateRequest) theOther;

		return Objects.deepEquals(additions, otherUR.additions) &&
		       Objects.deepEquals(removals, otherUR.removals);
	}

	@Override
	public int hashCode() {
		return Objects.hash(additions, removals);
	}
}
