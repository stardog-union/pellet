// Copyright (_c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assume.assumeTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.SWRL;
import com.clarkparsia.owlapi.XSD;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

/**
 * <_p> Title: </_p> <_p> Description: </_p> <_p> Copyright: Copyright (_c) 2008 </_p> <_p> Company: Clark & Parsia, LLC. <http://www.clarkparsia.com> </_p>
 *
 * @author Evren Sirin
 */
public abstract class AbstractExplanationTest
{
	static final String BASEPATH = "file:data/";
	static final OWLOntologyManager _manager = OWL._manager;
	static final URI ontologyURI = URI.create("http://www.example.org/test#");

	protected boolean _classify;

	private OWLClass _A, _B, _C, _D, _E, _F;
	private OWLObjectProperty _p, _q, _r;
	private OWLDataProperty dp, dq, dr;
	private OWLIndividual _a, _b, _c, _d, _anon1;
	private OWLDatatype _dt;
	private SWRLVariable _x, _y;
	private SWRLVariable _dx;

	public AbstractExplanationTest(final boolean classify)
	{
		this._classify = classify;
	}

	@Before
	public void createEntities()
	{
		_A = OWL.Class(ontologyURI + "A");
		_B = OWL.Class(ontologyURI + "B");
		_C = OWL.Class(ontologyURI + "C");
		_D = OWL.Class(ontologyURI + "D");
		_E = OWL.Class(ontologyURI + "E");
		_F = OWL.Class(ontologyURI + "F");

		_p = OWL.ObjectProperty(ontologyURI + "p");
		_q = OWL.ObjectProperty(ontologyURI + "q");
		_r = OWL.ObjectProperty(ontologyURI + "r");

		dp = OWL.DataProperty(ontologyURI + "dp");
		dq = OWL.DataProperty(ontologyURI + "dq");
		dr = OWL.DataProperty(ontologyURI + "dr");

		_a = OWL.Individual(ontologyURI + "a");
		_b = OWL.Individual(ontologyURI + "b");
		_c = OWL.Individual(ontologyURI + "c");
		_d = OWL.Individual(ontologyURI + "d");

		_dt = OWL.Datatype(ontologyURI + "dt");

		_anon1 = OWL.AnonymousIndividual("anon1");

		_x = SWRL.variable(ontologyURI + "x");
		_y = SWRL.variable(ontologyURI + "y");

		_dx = SWRL.variable(ontologyURI + "dx");
	}

	public void testInconsistencyExplanations(final int max, final OWLAxiom... explanations) throws Exception
	{
		testInconsistencyExplanations(max, new OWLAxiom[][] { explanations });
	}

	public void testInconsistencyExplanations(final int max, final OWLAxiom[]... explanations) throws Exception
	{
		testExplanations(OWL.subClassOf(OWL.Thing, OWL.Nothing), max, explanations);
	}

	public void testExplanations(final OWLAxiom axiom, final int max, final OWLAxiom... explanations) throws Exception
	{
		testExplanations(axiom, max, new OWLAxiom[][] { explanations });
	}

	public void testExplanations(final OWLAxiom axiom, final int max, final OWLAxiom[]... explanations) throws Exception
	{
		final Set<Set<OWLAxiom>> explanationSet = new HashSet<>();

		for (final OWLAxiom[] explanation : explanations)
			explanationSet.add(SetUtils.create(explanation));

		testExplanations(axiom, max, explanationSet);
	}

	public abstract void testExplanations(OWLAxiom axiom, int max, Set<Set<OWLAxiom>> expectedExplanations) throws Exception;

	public abstract void setupGenerators(Stream<OWLAxiom> ontologyAxioms) throws Exception;

	@Deprecated
	public abstract void setupGenerators(Collection<OWLAxiom> ontologyAxioms) throws Exception;

	@After
	public void after()
	{
		_manager.ontologies().forEach(_manager::removeOntology);
	}

	/**
	 * Test that entities appearing in annotations only can still be used in clashExplanation requests.
	 */
	@Ignore("This test is not valid anymore since annotation subjects do not carry type information")
	@Test
	public void annotationOnlyEntity() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.equivalentClasses(_A, OWL.Thing), OWL.comment(_B, "Annotation only class") };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(_B, _A), 0, new OWLAxiom[] { axioms[0] });
	}

	/**
	 * Test that entities appearing in annotations only can still be used in clashExplanation requests.
	 */
	@Test
	public void annotationOnlyDuringMUPSEntity() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, OWL.Thing), OWL.subClassOf(_B, _A), OWL.comment(_B, "Annotation only class") };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(_B, _A), 0, new OWLAxiom[] { axioms[1] });
	}

	/**
	 * Test that anonymous individuals as the object of property assertions are translated correctly
	 */
	@Test
	public void anonymousIndividualPropertyAssertion() throws Exception
	{
		assumeTrue(!(this instanceof JenaExplanationTest));

		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, _p, _anon1), OWL.classAssertion(_anon1, _A), OWL.subClassOf(OWL.some(_p, _A), _B) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.classAssertion(_a, _B), 0, axioms);
	}

	/**
	 * Test for built-in datatype
	 */
	/**
	 * Test that entities appearing in declarations only can still be used in clashExplanation requests.
	 */
	@Test
	public void declarationOnlyEntity() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.equivalentClasses(_A, OWL.Thing), OWL.declaration(_B) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(_B, _A), 0, new OWLAxiom[] { axioms[0] });
	}

	/**
	 * Test that entities appearing in declarations only can still be used in clashExplanation requests (in uninteresting ways).
	 */
	@Test
	public void declarationOnlyIrrelevantEntity() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_B, _A), OWL.declaration(_p) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(OWL.and(_B, OWL.some(_p, OWL.Thing)), _A), 0, new OWLAxiom[] { axioms[0] });
	}

	@Test
	public void disjointRange() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, OWL.some(_p, _B)), OWL.range(_p, _C), OWL.disjointClasses(_B, _C) };

		final OWLAxiom axiom = OWL.equivalentClasses(_A, OWL.Nothing);
		final OWLAxiom[] explanation = new OWLAxiom[] { OWL.subClassOf(_A, OWL.some(_p, _B)), OWL.range(_p, _C), OWL.disjointClasses(_B, _C) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(axiom, 0, explanation);
	}

	@Test
	public void disjointRangeSuper() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, OWL.some(_p, _B)), OWL.range(_p, _C), OWL.subClassOf(_B, _D), OWL.disjointClasses(_D, _C), OWL.subClassOf(_A, _E), OWL.subClassOf(_B, _F) };

		setupGenerators(Arrays.asList(axioms));

		// explain disjointness of _B and _C first so reasoner will _cache this
		// result
		testExplanations(OWL.disjointClasses(_B, _C), 0, new OWLAxiom[] { axioms[2], axioms[3] });

		// explain the unsatisfiability of _A and make sure cached results do not
		// interfere with clashExplanation
		testExplanations(OWL.equivalentClasses(_A, OWL.Nothing), 0, new OWLAxiom[] { axioms[0], axioms[1], axioms[2], axioms[3] });
	}

	@Test
	public void disjointSupers() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, _B), OWL.subClassOf(_A, _C), OWL.disjointClasses(_B, _C) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.equivalentClasses(_A, OWL.Nothing), 0, axioms);
	}

	@Test
	public void koalaHardWorkingDomain() throws Exception
	{
		final String ns = "http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#";
		final OWLOntology ontology = _manager.loadOntologyFromOntologyDocument(ClassLoader.getSystemResourceAsStream("test/data/modularity/koala.owl"));
		final OWLClass animal = OWL.Class(ns + "Animal");
		final OWLClass person = OWL.Class(ns + "Person");
		final OWLDataProperty hardWorking = OWL.DataProperty(ns + "isHardWorking");
		setupGenerators(ontology.axioms());
		testExplanations(OWL.domain(hardWorking, animal), 0, new OWLAxiom[] { OWL.subClassOf(person, animal), OWL.domain(hardWorking, person) });
	}

	@Test
	public void multipleEquivalentClasses() throws Exception
	{
		// test cached explanations
		final OWLAxiom[] axioms = { OWL.equivalentClasses(_A, _C), OWL.subClassOf(_A, _D), OWL.subClassOf(_D, _E), OWL.subClassOf(_B, _E), OWL.equivalentClasses(_A, _B),
				// the following axiom is to ensure that we are not in
				// EL expressivity because we want to test CD optimized
				// classifier which cached explanations
				OWL.subClassOf(_A, OWL.all(_p, _A)) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(_C, _D), 0, new OWLAxiom[] { axioms[0], axioms[1] });
		testExplanations(OWL.subClassOf(_A, _D), 0, new OWLAxiom[] { axioms[1] });
		testExplanations(OWL.subClassOf(_B, _D), 0, new OWLAxiom[] { axioms[1], axioms[4] });
	}

	@Test
	public void ruleTest1() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.classAssertion(_b, _B), OWL.propertyAssertion(_a, _p, _b), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_A, _x)), SWRL.consequent(SWRL.classAtom(_B, _x))), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_B, _x), SWRL.propertyAtom(_p, _x, _y), SWRL.classAtom(_B, _y)), SWRL.consequent(SWRL.classAtom(_C, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 0, new OWLAxiom[] { axioms[0] });
		testExplanations(OWL.classAssertion(_a, _B), 0, new OWLAxiom[] { axioms[0], axioms[3] });
		testExplanations(OWL.classAssertion(_a, _C), 1, axioms);
	}

	@Test
	public void ruleTest1b() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.classAssertion(_b, _B), OWL.propertyAssertion(_a, _p, _b), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_A, _x)), SWRL.consequent(SWRL.classAtom(_B, _x))), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_B, _x), SWRL.propertyAtom(_p, _x, _y), SWRL.classAtom(_B, _y)), SWRL.consequent(SWRL.classAtom(_C, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 0, new OWLAxiom[] { axioms[0] });
		testExplanations(OWL.classAssertion(_a, _B), 0, new OWLAxiom[] { axioms[0], axioms[3] });
		testExplanations(OWL.classAssertion(_a, _C), 1, axioms);
	}

	@Test
	public void ruleTest2() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subPropertyOf(_q, _p), OWL.propertyAssertion(_a, _q, _b), OWL.classAssertion(_b, _B), SWRL.rule(SWRL.antecedent(SWRL.propertyAtom(_p, _x, _y), SWRL.classAtom(_B, _y)), SWRL.consequent(SWRL.classAtom(_A, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 1, axioms);
	}

	@Test
	public void ruleTest3() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.inverseProperties(_q, _p), OWL.propertyAssertion(_b, _q, _a), OWL.classAssertion(_b, _B), SWRL.rule(SWRL.antecedent(SWRL.propertyAtom(_p, _x, _y), SWRL.classAtom(_B, _y)), SWRL.consequent(SWRL.classAtom(_A, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 1, axioms);
	}

	@Test
	public void ruleTest4() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_A, _x)), SWRL.consequent(SWRL.classAtom(_B, _x), SWRL.classAtom(_C, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 1, axioms);
		testExplanations(OWL.classAssertion(_a, _C), 1, axioms);
	}

	@Test
	public void ruleTest5() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, _p, _b), SWRL.rule(SWRL.antecedent(SWRL.propertyAtom(_p, _x, _y)), SWRL.consequent(SWRL.classAtom(_A, _x), SWRL.classAtom(_B, _y))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 1, axioms);
		testExplanations(OWL.classAssertion(_b, _B), 1, axioms);
	}

	@Test
	public void ruleTest6() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, _p, _b), OWL.classAssertion(_b, _B), SWRL.rule(SWRL.antecedent(SWRL.classAtom(OWL.some(_p, _B), _x)), SWRL.consequent(SWRL.classAtom(_C, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _C), 1, axioms);
	}

	@Test
	public void ruleBuiltinTest1() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.propertyAssertion(_a, dp, OWL.constant(9)), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_A, _x), SWRL.propertyAtom(dp, _x, _dx), SWRL.lessThan(_dx, SWRL.constant(10))), SWRL.consequent(SWRL.classAtom(_B, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 0, axioms);
	}

	@Test
	public void ruleBuiltinTest2() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.propertyAssertion(_a, dp, OWL.constant(9)), SWRL.rule(SWRL.antecedent(SWRL.classAtom(_A, _x), SWRL.propertyAtom(dp, _x, _dx), SWRL.greaterThan(_dx, SWRL.constant(5)), SWRL.lessThan(_dx, SWRL.constant(10))), SWRL.consequent(SWRL.classAtom(_B, _x))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 0, axioms);
	}

	@Test
	public void ruleBuiltinTest3() throws Exception
	{

		final SWRLVariable y = SWRL.variable(ontologyURI + "year");
		final SWRLVariable m = SWRL.variable(ontologyURI + "month");
		final SWRLVariable d = SWRL.variable(ontologyURI + "day");

		final OWLDataProperty year = OWL.DataProperty(ontologyURI + "year");
		final OWLDataProperty month = OWL.DataProperty(ontologyURI + "month");
		final OWLDataProperty day = OWL.DataProperty(ontologyURI + "day");

		final OWLAxiom[] axioms = {// 
				OWL.propertyAssertion(_a, dp, // 
						OWL.constant("2009-01-02", XSD.DATE)), // 
						SWRL.rule(//
								SWRL.antecedent(SWRL.propertyAtom(dp, _x, _dx),// 
										SWRL.builtIn(SWRLBuiltInsVocabulary.DATE, _dx, y, m, d)),// 
										SWRL.consequent(SWRL.propertyAtom(year, _x, y), //
												SWRL.propertyAtom(month, _x, m), //
												SWRL.propertyAtom(day, _x, d)//
												)//
								) //
		};

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, year, OWL.constant(2009)), 1, new OWLAxiom[] { axioms[0], axioms[1] });
	}

	@Test
	public void userDefinedDatatype1() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.propertyAssertion(_a, dp, OWL.constant(9)), OWL.equivalentClasses(_B, OWL.and(_A, OWL.some(dp, OWL.restrict(XSD.INTEGER, OWL.maxExclusive(10))))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 0, axioms);
	}

	@Test
	public void userDefinedDatatype2() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.subClassOf(_A, OWL.and(OWL.max(dp, 1), OWL.some(dp, OWL.restrict(XSD.INTEGER, OWL.minExclusive(10))))), OWL.equivalentClasses(_B, OWL.and(OWL.min(dp, 1), OWL.all(dp, OWL.restrict(XSD.INTEGER, OWL.minExclusive(5))))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 0, axioms);
	}

	@Test
	public void userDefinedDatatype3() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.propertyAssertion(_a, dp, OWL.constant(9)), OWL.equivalentClasses(_B, OWL.and(_A, OWL.some(dp, _dt))), OWL.datatypeDefinition(_dt, OWL.restrict(XSD.INTEGER, OWL.maxExclusive(10))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 0, axioms);
	}

	@Test
	public void datatypeEnumeration() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, dp, OWL.constant(1)), OWL.propertyAssertion(_a, dp, OWL.constant(2)), OWL.equivalentClasses(_A, OWL.some(dp, _dt)), OWL.datatypeDefinition(_dt, OWL.oneOf(OWL.constant(1), OWL.constant(2), OWL.constant(3))) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 0, new OWLAxiom[] { axioms[0], axioms[2], axioms[3] }, new OWLAxiom[] { axioms[1], axioms[2], axioms[3] });
	}

	@Test
	public void transitiveProperty() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.transitive(_p), OWL.propertyAssertion(_a, _p, _b), OWL.propertyAssertion(_b, _p, _c), OWL.equivalentClasses(_A, OWL.value(_p, _c)) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_b, _A), 0, new OWLAxiom[] { axioms[2], axioms[3] });
		testExplanations(OWL.classAssertion(_a, _A), 0, axioms);
	}

	@Test
	public void propertyChain1() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subPropertyOf(new OWLObjectProperty[] { _p, _q }, _r), OWL.propertyAssertion(_a, _p, _b), OWL.propertyAssertion(_b, _q, _c) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, _p, _b), 0, new OWLAxiom[] { axioms[1] });
		testExplanations(OWL.propertyAssertion(_a, _r, _c), 0, axioms);
	}

	@Test
	public void propertyChain2() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subPropertyOf(new OWLObjectProperty[] { _p, _q }, _r), OWL.propertyAssertion(_a, _p, _b), OWL.propertyAssertion(_b, _q, _c), OWL.equivalentClasses(_A, OWL.some(_r, OWL.Thing)) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _A), 0, axioms);
	}

	@Test
	public void inferredSubProperty() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subPropertyOf(_p, _r), OWL.subPropertyOf(_r, _q), OWL.propertyAssertion(_a, _p, _b) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, _r, _b), 0, new OWLAxiom[] { axioms[0], axioms[2] });
		testExplanations(OWL.propertyAssertion(_a, _q, _b), 0, axioms);
	}

	@Test
	public void multipleDatatypeRange() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.range(dp, XSD.BYTE), OWL.range(dp, XSD.NON_POSITIVE_INTEGER), OWL.range(dp, XSD.NON_NEGATIVE_INTEGER), OWL.subClassOf(_A, OWL.min(dp, 1)), OWL.classAssertion(_a, _A) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, dp, OWL.constant(0)), 0, new OWLAxiom[] { axioms[1], axioms[2], axioms[3], axioms[4] });
	}

	@Test
	public void subPropertiesOfFunctionalDataProperty() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.functional(dp), OWL.subPropertyOf(dq, dp), OWL.subPropertyOf(dr, dp), OWL.propertyAssertion(_a, dq, OWL.constant(1)), OWL.classAssertion(_a, OWL.some(dr, XSD.INTEGER)) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, dp, OWL.constant(1)), 0, new OWLAxiom[] { axioms[1], axioms[3] });
		testExplanations(OWL.propertyAssertion(_a, dr, OWL.constant(1)), 0, axioms);
	}

	@Test
	public void simpleSubClass() throws Exception
	{
		// this test case is to check the effect of classification and caching
		// on explanations. the last axiom is to ebsure the EL classifier will
		// not be used
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, _B), OWL.subClassOf(_B, _C), OWL.subClassOf(_A, OWL.all(_p, _B)) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.subClassOf(_A, _C), 0, axioms[0], axioms[1]);
	}

	@Test
	public void simpleType() throws Exception
	{
		// this test case is to check the effect of realization and caching
		// on explanations. the last axiom is to ebsure the EL classifier will
		// not be used
		final OWLAxiom[] axioms = { OWL.classAssertion(_a, _A), OWL.subClassOf(_A, _B), OWL.subClassOf(_A, OWL.all(_p, _B)) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.classAssertion(_a, _B), 0, axioms[0], axioms[1]);
	}

	@Test
	public void simplePropertyAssertion() throws Exception
	{
		// this test case is to check the effect of hasObviousPropertyValue
		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, _p, _b) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, _p, _b), 0, axioms);
	}

	@Test
	public void subPropertyAssertion() throws Exception
	{
		// this test case is to check the effect of hasObviousPropertyValue
		final OWLAxiom[] axioms = { OWL.propertyAssertion(_a, _p, _b), OWL.subPropertyOf(_p, _q) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.propertyAssertion(_a, _q, _b), 0, axioms);
	}

	@Test
	public void functionalPropertyInMaxCardinality() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.functional(_p), OWL.subClassOf(_C, OWL.min(_p, 2)) };

		setupGenerators(Arrays.asList(axioms));

		testExplanations(OWL.subClassOf(_C, OWL.Nothing), 0, axioms);
	}

	@Test
	public void expressionInDomain() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, OWL.some(_p, _B)), OWL.domain(_p, OWL.or(_C, _D)), OWL.disjointClasses(_A, _C) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(_A, _D), 0, axioms);
	}

	@Test
	public void expressionInRange() throws Exception
	{
		final OWLAxiom[] axioms = { OWL.subClassOf(_A, OWL.some(_p, _B)), OWL.range(_p, OWL.or(_C, _D)), OWL.disjointClasses(_B, _C), OWL.disjointClasses(_B, _D) };

		setupGenerators(Arrays.asList(axioms));
		testExplanations(OWL.subClassOf(_A, OWL.Nothing), 0, axioms);
	}

	@Test
	public void differentFromAndFunctionality() throws Exception
	{
		assumeTrue(!_classify);

		final OWLAxiom[] axioms = { OWL.functional(_p), OWL.propertyAssertion(_a, _p, _b), OWL.propertyAssertion(_a, _p, _c), OWL.propertyAssertion(_a, _p, _d), OWL.differentFrom(_b, _c), OWL.differentFrom(_c, _d) };

		setupGenerators(Arrays.asList(axioms));

		testInconsistencyExplanations(0, new OWLAxiom[] { axioms[0], axioms[1], axioms[2], axioms[4] }, new OWLAxiom[] { axioms[0], axioms[2], axioms[3], axioms[5] });
	}
}
