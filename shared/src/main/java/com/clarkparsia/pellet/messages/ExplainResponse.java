package com.clarkparsia.pellet.messages;

import java.util.Objects;
import java.util.Set;

import com.clarkparsia.pellet.ProtoMessage;
import com.google.common.collect.ImmutableSet;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ExplainResponse extends ProtoMessage {

	private final Set<Set<OWLAxiom>> axiomSets;

	public ExplainResponse(final Set<Set<OWLAxiom>> theAxiomSets) {
		axiomSets = ImmutableSet.copyOf(theAxiomSets);
	}

	public Set<Set<OWLAxiom>> getAxiomSets() {
		return axiomSets;
	}

	@Override
	public boolean equals(final Object theOther) {
		if (this == theOther) {
			return true;
		}
		if (!(theOther instanceof ExplainResponse)) {
			return false;
		}

		ExplainResponse otherER = (ExplainResponse) theOther;

		return Objects.deepEquals(this.axiomSets, otherER.axiomSets);
	}

	@Override
	public int hashCode() {
		return Objects.hash(axiomSets);
	}
}
