package com.complexible.pellet.client;

import java.util.Set;
import java.util.UUID;

import com.clarkparsia.owlapi.explanation.GlassBoxExplanation;
import com.clarkparsia.owlapiv3.ImmutableNodeSet;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.clarkparsia.pellet.service.reasoner.SchemaReasonerFactory;
import com.complexible.pellet.client.reasoner.RemoteSchemaReasoner;
import com.complexible.pellet.client.reasoner.SchemaOWLReasoner;
import com.complexible.pellet.client.reasoner.SchemaOWLReasonerFactory;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.protege.owl.server.api.ChangeMetaData;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;

import static com.clarkparsia.owlapiv3.OWL.Class;
import static com.clarkparsia.owlapiv3.OWL.DataProperty;
import static com.clarkparsia.owlapiv3.OWL.ObjectProperty;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Evren Sirin
 */
public class RemoteReasonerTest extends PelletClientTest {
	protected static final String NS = "http://example.org/test#";
	protected static final IRI ONT_IRI = IRI.create(NS);

	protected static final OWLClass A = Class(NS + "A");
	protected static final OWLClass B = Class(NS + "B");
	protected static final OWLClass C = Class(NS + "C");
	protected static final OWLClass D = Class(NS + "D");
	protected static final OWLClass E = Class(NS + "E");
	protected static final OWLObjectProperty p = ObjectProperty(NS + "p");
	protected static final OWLObjectProperty q = ObjectProperty(NS + "q");
	protected static final OWLObjectProperty r = ObjectProperty(NS + "r");
	protected static final OWLObjectProperty s = ObjectProperty(NS + "s");
	protected static final OWLDataProperty dp = DataProperty(NS + "dp");
	protected static final OWLDataProperty dq = DataProperty(NS + "dq");
	protected static final OWLDataProperty dr = DataProperty(NS + "dr");

	protected static final Set<OWLAxiom> AXIOMS = ImmutableSet.<OWLAxiom>of(
			OWL.subClassOf(B, A), OWL.subClassOf(C, B), OWL.equivalentClasses(D, C),
			OWL.subClassOf(E, A), OWL.subClassOf(E, OWL.some(p, D)), OWL.disjointClasses(E, B),
			OWL.domain(p, A), OWL.subPropertyOf(p, q), OWL.equivalentProperties(q, r), OWL.disjointProperties(p, s)
	);

	private SchemaReasonerFactory FACTORY = new SchemaReasonerFactory() {
		@Override
		public SchemaReasoner create(final OWLOntology ontology) {
			return new RemoteSchemaReasoner(serviceProvider.get(), UUID.randomUUID(), ontology);
		}
	};

	private OWLReasoner reasoner;

	private OWLOntology createOntology() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = manager.createOntology(ONT_IRI);
		manager.addAxioms(ont, AXIOMS);
		return ont;
	}

	@Before
	public void before() throws Exception {
		super.before();

		GlassBoxExplanation.setup();

		final OWLOntology ont = createOntology();

		ClientUtilities.createServerOntology(mClient,
		                                     IRI.create(root(mClient) + "/test.history"),
		                                     new ChangeMetaData("Initial entry"),
		                                     ont);

		startPelletServer("test.history");

		reasoner = new SchemaOWLReasonerFactory(FACTORY).createReasoner(ont);
	}

	@Test
	public void queryClasses() {
		assertEquals(setOf(B, E), reasoner.getSubClasses(A, true).getFlattened());
		assertEquals(setOf(B, C, D, E, OWL.Nothing), reasoner.getSubClasses(A, false).getFlattened());
		assertEquals(nodeSetOf(nodeOf(D, C)), reasoner.getSubClasses(B, true));

		assertEquals(setOf(A), reasoner.getSuperClasses(B, true).getFlattened());
		assertEquals(setOf(A, OWL.Thing), reasoner.getSuperClasses(B, false).getFlattened());
		assertEquals(setOf(OWL.Thing), reasoner.getSuperClasses(A, false).getFlattened());

		assertEquals(setOf(A), reasoner.getEquivalentClasses(A).getEntities());
		assertEquals(setOf(C, D), reasoner.getEquivalentClasses(D).getEntities());

		assertEquals(setOf(OWL.Nothing), reasoner.getDisjointClasses(A).getFlattened());
		assertEquals(setOf(B, C, D, OWL.Nothing), reasoner.getDisjointClasses(E).getFlattened());
	}

	@Test
	public void queryProperties() {
		assertEquals(setOf(p), reasoner.getSubObjectProperties(q, true).getFlattened());
		assertEquals(setOf(p, OWL.bottomObjectProperty), reasoner.getSubObjectProperties(q, false).getFlattened());

		assertTrue(reasoner.getSuperObjectProperties(p, true).isSingleton());
		assertEquals(setOf(q, r), reasoner.getSuperObjectProperties(p, true).getFlattened());
		assertEquals(setOf(q, r, OWL.topObjectProperty), reasoner.getSuperObjectProperties(p, false).getFlattened());
		assertEquals(setOf(OWL.topObjectProperty), reasoner.getSuperObjectProperties(r, false).getFlattened());

		assertEquals(setOf(p), reasoner.getEquivalentObjectProperties(p).getEntities());
		assertEquals(setOf(q, r), reasoner.getEquivalentObjectProperties(q).getEntities());

		assertEquals(setOf(s, OWL.bottomObjectProperty), reasoner.getDisjointObjectProperties(p).getFlattened());
		assertEquals(setOf(OWL.bottomObjectProperty), reasoner.getDisjointObjectProperties(q).getFlattened());
	}

	@Test
	public void multipleClients() throws Exception {
		OWLOntology ont = createOntology();
		OWLReasoner reasoner2 = new SchemaOWLReasonerFactory(FACTORY).createReasoner(ont);

		assertEquals(nodeSetOf(nodeOf(D, C)), reasoner.getSubClasses(B, true));
		assertEquals(nodeSetOf(nodeOf(D, C)), reasoner2.getSubClasses(B, true));

		OntologyUtils.updateOntology(ont, setOf(OWL.subClassOf(D, B)), setOf(OWL.equivalentClasses(D, C)));

		assertEquals(nodeSetOf(nodeOf(D, C)), reasoner.getSubClasses(B, true));
		assertEquals(nodeSetOf(nodeOf(D, C)), reasoner2.getSubClasses(B, true));

		reasoner2.flush();

		assertEquals(nodeSetOf(nodeOf(D, C)), reasoner.getSubClasses(B, true));
		assertEquals(nodeSetOf(nodeOf(D), nodeOf(C)), reasoner2.getSubClasses(B, true));
	}

	@Test
	public void explainInferences() throws Exception {
		assertEquals(setOf(setOf(OWL.subClassOf(B, A))), explain(OWL.subClassOf(B, A)));
		assertEquals(setOf(setOf(OWL.equivalentClasses(D, C))), explain(OWL.subClassOf(D, C)));
		assertEquals(setOf(setOf(OWL.subClassOf(B, A), OWL.subClassOf(C, B), OWL.equivalentClasses(D, C))), explain(OWL.subClassOf(D, A)));
		assertEquals(setOf(setOf(OWL.subClassOf(E, A)), setOf(OWL.subClassOf(E, OWL.some(p, D)), OWL.domain(p, A))), explain(OWL.subClassOf(E, A)));
	}

	private Set<Set<OWLAxiom>> explain(OWLAxiom axiom) {
		return ((SchemaOWLReasoner) reasoner).explain(axiom, 3);
	}

	private <T> Set<T> setOf(T... elements) {
		return ImmutableSet.copyOf(elements);
	}

	private Node<OWLClass> nodeOf(OWLClass... classes) {
		return new OWLClassNode(setOf(classes));
	}

	private <T extends OWLObject> NodeSet<T> nodeSetOf(Node<T>... nodes) {
		return ImmutableNodeSet.of(setOf(nodes));
	}
}
