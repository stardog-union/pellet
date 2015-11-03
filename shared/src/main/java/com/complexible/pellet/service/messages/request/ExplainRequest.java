package com.complexible.pellet.service.messages.request;

import com.complexible.pellet.service.messages.AbstractJsonMessage;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ExplainRequest extends AbstractJsonMessage {

	private final OWLAxiom owlAxiom;

	private final int limit;

	public ExplainRequest(final OWLAxiom theOwlAxiom, final int theLimit) {
		owlAxiom = theOwlAxiom;
		limit = theLimit;
	}

	public OWLAxiom getAxiom() {
		return owlAxiom;
	}

	public int getLimit() {
		return limit;
	}
}
