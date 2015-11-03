package com.complexible.pellet.service.messages.response;

import java.util.Set;

import com.complexible.pellet.service.messages.AbstractJsonMessage;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ExplainResponse extends AbstractJsonMessage {

	public final Set<Set<OWLAxiom>> results;

	public ExplainResponse(final Set<Set<OWLAxiom>> theResults) {
		results = ImmutableSet.copyOf(theResults);
	}

	public Set<Set<OWLAxiom>> getResults() {
		return results;
	}
}
