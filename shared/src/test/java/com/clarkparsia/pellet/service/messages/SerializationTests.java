package com.clarkparsia.pellet.service.messages;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.proto.ProtoServiceDecoder;
import com.clarkparsia.pellet.service.proto.ProtoServiceEncoder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class SerializationTests {

	private ServiceEncoder anEncoder;
	private ServiceDecoder aDecoder;

	public SerializationTests() {
		anEncoder = new ProtoServiceEncoder();
		aDecoder = new ProtoServiceDecoder();
	}

	@Test
	public void testQueryRequestRoundTrip() {
		OWLLogicalEntity entity = new OWLClassImpl(IRI.create("urn:test:iri"));

		QueryRequest originalQR = new QueryRequest(entity);

		ByteString encodedQR = ByteString.copyFrom(anEncoder.encode(originalQR));
		assertTrue(encodedQR.size() > 0);

		QueryRequest decodedQR = aDecoder.queryRequest(encodedQR.toByteArray());

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
	public void testExplainRequestRoundTrip() {
		OWLAxiom axiom = generateAxiom(0);

		ExplainRequest originalER = new ExplainRequest(axiom);

		byte[] encodedER = anEncoder.encode(originalER);
		assertTrue(encodedER.length > 0);

		ExplainRequest decodedER = aDecoder.explainRequest(encodedER);

		assertEquals(originalER, decodedER);
	}

	@Test
	public void testUpdateRequestRoundTrip() {
		Set<OWLAxiom> additions = Sets.newHashSet(generateAxioms(100));
		Set<OWLAxiom> removals = Sets.newHashSet(generateAxioms(50));

		UpdateRequest originalUR = new UpdateRequest(additions, removals);

		byte[] encodedUR = anEncoder.encode(originalUR);
		assertTrue(encodedUR.length > 0);

		UpdateRequest decodedUR = aDecoder.updateRequest(encodedUR);

		assertEquals(originalUR, decodedUR);
		assertEquals(100, decodedUR.getAdditions().size());
		assertEquals(50, decodedUR.getRemovals().size());
	}

	@Test
	public void testQueryResponseRoundTrip() {
		NodeSet<OWLClass> classes = new OWLClassNodeSet(new OWLClassImpl(IRI.create("http://xmlns.com/foaf/0.1/Agent")));

		QueryResponse originalQR = new QueryResponse(classes);

		byte[] encodedUR = anEncoder.encode(originalQR);
		assertTrue(encodedUR.length > 0);

		QueryResponse decodedQR = aDecoder.queryResponse(encodedUR);

		assertEquals(originalQR, decodedQR);
	}

	@Test
	public void testExplainResponseRoundTrip() {
		int n = 4;
		int cardinality = 50;
		Set<Set<OWLAxiom>> axiomSets = Sets.newHashSetWithExpectedSize(n);

		for (int i = 0; i < n; i++) {
			axiomSets.add(Sets.newHashSet(generateAxioms(cardinality)));
		}

		ExplainResponse originalER = new ExplainResponse(axiomSets);
		byte[] encodedER = anEncoder.encode(originalER);
		assertTrue(encodedER.length > 0);

		ExplainResponse decodedUR = aDecoder.explainResponse(encodedER);

		assertEquals(originalER, decodedUR);

		for (Set<OWLAxiom> aAxioms : decodedUR.getAxiomSets()) {
			assertEquals(cardinality, aAxioms.size());
		}
	}
}
