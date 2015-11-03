package com.complexible.pellet.service.messages.request;

import java.util.Set;

import com.complexible.pellet.service.messages.AbstractJsonMessage;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class UpdateRequest extends AbstractJsonMessage {

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
}
