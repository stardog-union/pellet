package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aterm.ATermAppl;
import com.clarkparsia.pellet.owlapi.OWLAPILoader;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: TestAnnotationsKnowledgeBase
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class TestAnnotations
{

	protected static String checkPath(String path)
	{
		if (!new File(path).exists())
		{
			final String localPath = "src/test/resources/" + path;

			if (!new File(localPath).exists())//
				throw new RuntimeException("Path to data files is not correct: " + path);

			return localPath;
		}
		else
			return path;
	}

	private static final String DATA1_RDF = "file:" + checkPath("test/data/annotations/data1.rdf");
	private static final String DATA1_TTL = "file:" + checkPath("test/data/annotations/data1.ttl");
	private static final String QUERY1_RQ = "file:" + checkPath("test/data/annotations/query1.rq");

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(TestAnnotations.class);
	}

	private Properties savedOptions;

	@Before
	public void setUp()
	{
		final Properties newOptions = PropertiesBuilder.singleton("USE_ANNOTATION_SUPPORT", "true");
		savedOptions = PelletOptions.setOptions(newOptions);
	}

	@After
	public void tearDown()
	{
		PelletOptions.setOptions(savedOptions);
	}

	@Test
	public void addAnnotation1()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("i");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makePlainLiteral("o");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));
		assertTrue(kb.isIndividual(s));
		assertTrue(kb.isAnnotationProperty(p));
		assertFalse(kb.isIndividual(o));
	}

	@Test
	public void addAnnotation2()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("i");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeTermAppl("j");

		kb.addIndividual(s);
		kb.addIndividual(o);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));
		assertTrue(kb.isIndividual(s));
		assertTrue(kb.isAnnotationProperty(p));
		assertTrue(kb.isIndividual(o));
	}

	@Test
	public void addAnnotation3()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("i");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeBnode("b");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));
		assertTrue(kb.isIndividual(s));
		assertTrue(kb.isAnnotationProperty(p));
		assertFalse(kb.isIndividual(o));
	}

	@Test
	public void addAnnotations()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("i");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o1 = ATermUtils.makePlainLiteral("o1");
		final ATermAppl o2 = ATermUtils.makePlainLiteral("o2");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o1));
		assertTrue(kb.addAnnotation(s, p, o2));
		assertTrue(kb.isIndividual(s));
		assertTrue(kb.isAnnotationProperty(p));
		assertFalse(kb.isIndividual(o1));
		assertFalse(kb.isIndividual(o2));
	}

	@Test
	public void getAnnotations1()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("i");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeTermAppl("j");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));

		Set<ATermAppl> actual = kb.getAnnotations(s, p);
		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(o);
		assertEquals(expected, actual);

		actual = kb.getAnnotations(null, p);
		assertTrue(actual.isEmpty());

		actual = kb.getAnnotations(s, null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void getAnnotations2()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("i");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o1 = ATermUtils.makeTermAppl("j1");
		final ATermAppl o2 = ATermUtils.makeTermAppl("j2");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o1));
		assertTrue(kb.addAnnotation(s, p, o2));

		Set<ATermAppl> actual = kb.getAnnotations(s, p);
		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(o1);
		expected.add(o2);
		assertEquals(expected, actual);

		actual = kb.getAnnotations(null, p);
		assertTrue(actual.isEmpty());

		actual = kb.getAnnotations(s, null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void getAnnotations3()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s1 = ATermUtils.makeTermAppl("s1");
		final ATermAppl p1 = ATermUtils.makeTermAppl("p1");
		final ATermAppl o1 = ATermUtils.makeTermAppl("o1");

		final ATermAppl s2 = ATermUtils.makeTermAppl("s2");
		final ATermAppl p2 = ATermUtils.makeTermAppl("p2");
		final ATermAppl o2 = ATermUtils.makeTermAppl("o2");

		final ATermAppl o3 = ATermUtils.makeTermAppl("o3");

		kb.addIndividual(s1);
		kb.addIndividual(s2);
		kb.addAnnotationProperty(p1);
		kb.addAnnotationProperty(p2);

		assertTrue(kb.addAnnotation(s1, p1, o1));
		assertTrue(kb.addAnnotation(s1, p2, o2));
		assertTrue(kb.addAnnotation(s2, p2, o3));

		Set<ATermAppl> actual = kb.getAnnotations(s1, p1);
		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(o1);
		assertEquals(expected, actual);

		actual = kb.getAnnotations(null, p2);
		assertTrue(actual.isEmpty());

		actual = kb.getAnnotations(s1, null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void getAnnotations4()
	{
		// Test kb.getAnnotationProperties()		
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("s");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeTermAppl("o");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));

		final Set<ATermAppl> actual = kb.getAnnotationProperties();
		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(p);

		assertEquals(expected, actual);
	}

	@Test
	public void getAnnotations5()
	{
		// Test kb.getProperties()		
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("s");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeTermAppl("o");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));

		final Set<ATermAppl> actual = kb.getProperties();
		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(p);
		expected.add(ATermUtils.TOP_OBJECT_PROPERTY);
		expected.add(ATermUtils.BOTTOM_OBJECT_PROPERTY);
		expected.add(ATermUtils.TOP_DATA_PROPERTY);
		expected.add(ATermUtils.BOTTOM_DATA_PROPERTY);

		assertEquals(expected, actual);
	}

	@Test
	public void getAnnotations6()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("s");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeTermAppl("o");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));

		final Set<ATermAppl> actual = kb.getIndividualsWithAnnotation(p, o);
		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(s);

		assertEquals(expected, actual);
	}

	@Test
	public void testJenaLoader1()
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		final Resource s = ResourceFactory.createResource("i");
		final Property p = RDFS.label;
		final Literal o = ResourceFactory.createPlainLiteral("o");

		model.add(s, p, o);
		model.prepare();

		final ATermAppl st = ATermUtils.makeTermAppl("i");
		final ATermAppl pt = ATermUtils.makeTermAppl(RDFS.label.getURI());
		final ATermAppl ot = ATermUtils.makePlainLiteral("o");

		final KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		final Set<ATermAppl> actual = kb.getAnnotations(st, pt);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(ot);

		assertEquals(expected, actual);
	}

	@Test
	public void testJenaLoader2()
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		final Resource s1 = ResourceFactory.createResource("i");
		final Property p1 = RDFS.label;
		final Literal o1 = ResourceFactory.createPlainLiteral("o1");

		final Property p2 = RDFS.comment;
		final Literal o2 = ResourceFactory.createPlainLiteral("o2");

		model.add(s1, p1, o1);
		model.add(s1, p2, o2);
		model.prepare();

		final ATermAppl st = ATermUtils.makeTermAppl("i");

		final KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		final Set<ATermAppl> actual = kb.getAnnotations(st, null);

		assertTrue(actual.isEmpty());
	}

	@Test
	public void testJenaLoader3()
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(DATA1_TTL, "N3");
		model.prepare();

		final ATermAppl i = ATermUtils.makeTermAppl("http://example.org#i");
		final ATermAppl label = ATermUtils.makeTermAppl(RDFS.label.getURI());
		final ATermAppl o1 = ATermUtils.makePlainLiteral("o1");

		final KnowledgeBase kb = ((PelletInfGraph) model.getGraph()).getKB();
		final Set<ATermAppl> actual = kb.getAnnotations(i, label);

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(o1);

		assertEquals(expected, actual);
	}

	@Test
	public void testOWLAPILoader()
	{
		final KnowledgeBase kb = new OWLAPILoader().createKB(DATA1_RDF);
		final ATermAppl i = ATermUtils.makeTermAppl("http://example.org#i");
		final ATermAppl label = ATermUtils.makeTermAppl(RDFS.label.getURI());
		final ATermAppl o1 = ATermUtils.makeStringLiteral("o1");

		final Set<ATermAppl> actual = kb.getAnnotations(i, label);

		kb.getAnnotationSubjects();

		final Set<ATermAppl> expected = new HashSet<>();
		expected.add(o1);

		assertEquals(expected, actual);
	}

	@Test
	public void testCombinedQueryEngine()
	{
		// This tests annotations using the SPARQL-DL combined query engine
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(DATA1_RDF);

		final Query query = QueryFactory.read(QUERY1_RQ);
		try (final QueryExecution qe = SparqlDLExecutionFactory.create(query, model))
		{
			final ResultSet rs = qe.execSelect();

			while (rs.hasNext())
			{
				final QuerySolution qs = rs.nextSolution();
				final Resource s = qs.getResource("s");
				final Literal o = qs.getLiteral("o");

				assertEquals("http://example.org#i", s.getURI());
				assertEquals("o2", o.getLexicalForm());
			}
		}
	}

	@Test
	public void test412()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		final ATermAppl q = term("q");
		final ATermAppl r = term("r");
		final ATermAppl s = term("s");

		kb.addAnnotationProperty(p);
		kb.addAnnotationProperty(q);
		kb.addAnnotationProperty(r);
		kb.addAnnotationProperty(s);

		kb.addSubProperty(p, q);
		kb.addSubProperty(q, r);
		kb.addSubProperty(r, s);

		// The set of sub/super roles at this point are correct for each role
		assertEquals(singletonSets(p, r, q), kb.getSubProperties(s));
	}

	@Test
	public void getAnnotationsCopy()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl s = ATermUtils.makeTermAppl("s");
		final ATermAppl p = ATermUtils.makeTermAppl("p");
		final ATermAppl o = ATermUtils.makeTermAppl("o");

		kb.addIndividual(s);
		kb.addAnnotationProperty(p);

		assertTrue(kb.addAnnotation(s, p, o));

		assertEquals(Collections.singleton(o), kb.getAnnotations(s, p));

		assertEquals(Collections.singleton(o), kb.copy().getAnnotations(s, p));
	}

	@SafeVarargs
	public static <T> Set<Set<T>> singletonSets(T... es)
	{
		final Set<Set<T>> set = new HashSet<>();
		for (final T e : es)
		{
			set.add(Collections.singleton(e));
		}
		return set;
	}
}
