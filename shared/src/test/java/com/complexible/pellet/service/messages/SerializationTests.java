package com.complexible.pellet.service.messages;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.clarkparsia.pellet.MessageDecoders;
import com.clarkparsia.pellet.MessageEncoders;
import com.clarkparsia.pellet.messages.ExplainRequest;
import com.clarkparsia.pellet.messages.QueryRequest;
import com.clarkparsia.pellet.messages.UpdateRequest;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class SerializationTests {

	@Test
	public void testQueryRequestRoundtrip() {
		OWLLogicalEntity entity = new OWLClassImpl(IRI.create("urn:test:iri"));

		QueryRequest originalQR = new QueryRequest(entity);

		ByteString encodedQR = ByteString.copyFrom(MessageEncoders.encode(originalQR));
		assertTrue(encodedQR.size() > 0);

		QueryRequest decodedQR = MessageDecoders.queryRequest(encodedQR);

		assertEquals(originalQR, decodedQR);
	}

	private OWLAxiom generateAxiom(int i) {
		return new OWLAnnotationAssertionAxiomImpl(IRI.create("urn:test:s"+ i),
		                                           new OWLAnnotationPropertyImpl(IRI.create("urn:test:p"+ i)),
		                                           IRI.create("urn:test:o"+ i),
		                                           Collections.<OWLAnnotation>emptyList());
	}

	private Collection<OWLAxiom> generateAxioms(int n) {
		List<OWLAxiom> axioms = Lists.newLinkedList();

		for (int i = 0; i < n; i++) {
			axioms.add(generateAxiom(i));
		}

		return axioms;
	}

	@Test
	public void testExplainRequestRoundtrip() {
		OWLAxiom axiom = generateAxiom(0);

		ExplainRequest originalER = new ExplainRequest(axiom);

		ByteString encodedER = ByteString.copyFrom(MessageEncoders.encode(originalER));
		assertTrue(encodedER.size() > 0);

		ExplainRequest decodedER = MessageDecoders.explainRequest(encodedER);

		assertEquals(originalER, decodedER);
	}

	@Test
	public void testUpdateRequestRoundtrip() {
		Set<OWLAxiom> additions = Sets.newHashSet(generateAxioms(100));
		Set<OWLAxiom> removals = Sets.newHashSet(generateAxioms(50));

		UpdateRequest originalUR = new UpdateRequest(additions, removals);

		ByteString encodedUR = ByteString.copyFrom(MessageEncoders.encode(originalUR));
		assertTrue(encodedUR.size() > 0);

		UpdateRequest decodedUR = MessageDecoders.updateRequest(encodedUR);

		assertEquals(originalUR, decodedUR);
		assertEquals(100, decodedUR.getAdditions().size());
		assertEquals(50, decodedUR.getRemovals().size());
	}
}
