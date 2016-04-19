// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.GlassBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;
import com.clarkparsia.owlapi.explanation.TransactionAwareSingleExpGen;
import com.clarkparsia.owlapi.explanation.io.ConciseExplanationRenderer;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * <p>
 * Title:
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
 * @author Evren Sirin
 */
@RunWith(Parameterized.class)
public class OWLAPIExplanationTest extends AbstractExplanationTest
{
	private static final Logger log = Logger.getLogger(JenaExplanationTest.class.getName());

	private PelletReasoner reasoner;
	private final boolean useGlassBox;

	private SatisfiabilityConverter converter;
	private ConciseExplanationRenderer renderer;
	private HSTExplanationGenerator expGen;

	private int axiomCount = 0;

	@Parameters
	public static Collection<Object[]> getParameters()
	{
		final Collection<Object[]> parameters = new ArrayList<>();
		parameters.add(new Object[] { true, false });
		parameters.add(new Object[] { true, true });
		parameters.add(new Object[] { false, false });
		parameters.add(new Object[] { false, true });
		return parameters;
	}

	public OWLAPIExplanationTest(final boolean useGlassBox, final boolean classify)
	{
		super(classify);

		this.useGlassBox = useGlassBox;
	}

	@BeforeClass
	public static void beforeClass()
	{
		GlassBoxExplanation.setup();
	}

	@Override
	@After
	public void after()
	{
		super.after();

		if (expGen != null)
			if (useGlassBox)
			{
				final GlassBoxExplanation gbe = (GlassBoxExplanation) expGen.getSingleExplanationGenerator();
				gbe.dispose();
				reasoner.dispose();
			}
			else
			{
				final BlackBoxExplanation bbe = (BlackBoxExplanation) expGen.getSingleExplanationGenerator();
				bbe.dispose();
				reasoner.getManager().removeOntologyChangeListener(bbe.getDefinitionTracker());
			}
	}

	@Override
	public void setupGenerators(final Collection<OWLAxiom> ontologyAxioms) throws Exception
	{
		// USE_TRACING should be turned on for glass box explanation which is done by
		// ExplanationTestSuite that calls this class. We don't set this value here to
		// avoid repeating the clean up code that sets it bakc to the old value
		assertTrue(!useGlassBox || PelletOptions.USE_TRACING);

		converter = new SatisfiabilityConverter(OWL.factory);
		renderer = new ConciseExplanationRenderer();

		final OWLOntology ontology = OWL.Ontology(ontologyAxioms);

		final PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		reasoner = reasonerFactory.createReasoner(ontology);

		final TransactionAwareSingleExpGen singleExpGen = useGlassBox ? new GlassBoxExplanation(reasoner) : new BlackBoxExplanation(ontology, reasonerFactory, reasoner)
		{
			@Override
			protected boolean isFirstExplanation()
			{
				return false;
			}
		};

		expGen = new HSTExplanationGenerator(singleExpGen);

		final KnowledgeBase kb = reasoner.getKB();

		if (classify)
		{
			kb.setDoExplanation(true);
			kb.ensureConsistency();
			kb.setDoExplanation(false);

			kb.realize();
		}
	}

	@Override
	public void testInconsistencyExplanations(final int max, final OWLAxiom[]... explanations) throws Exception
	{
		assumeTrue(useGlassBox);

		super.testInconsistencyExplanations(max, explanations);
	}

	final class Explanation implements Comparable<Explanation> // TODO : expose this class in interface.
	{
		private final Set<OWLAxiom> _base;
		private transient String _representation = null; // java.util.Optional ?

		public String getRepresentation()
		{
			if (null == _representation) // Sort the axiom to get a easy common representation of this sub ontology.
				_representation = _base.stream()//
				.map(axiom ->
				{ // Inside of the SWRLRule, the representation can vary because they are 'Set'.
					// So test can pass on some java-vm but not on some other.
					// The solution is to sort everything.
					if (axiom instanceof SWRLRule)
					{
						final SWRLRule rule = (SWRLRule) axiom;
						return "DLSafeRule( " + //
						"Body(" + rule.body().map(SWRLAtom::toString).sorted().collect(Collectors.joining(" ")) + ") " + //
						"Head(" + rule.head().map(SWRLAtom::toString).sorted().collect(Collectors.joining(" ")) + ") " + //
						" )";//
					}
					return axiom.toString();
				})//
				.sorted().collect(Collectors.joining("\n"));
			return _representation;
		}

		public Set<OWLAxiom> getExplanationEx()
		{
			return _base;
		}

		Explanation(final Set<OWLAxiom> base)
		{
			_base = base;
		}

		@Override
		public int compareTo(final Explanation that)
		{
			return this.getRepresentation().compareTo(that.getRepresentation()); // Have fun coding the tree exploration instead of this.
		}
	}

	private static void compareExplanations(//
			final List<Explanation> unexpectedExplanations,//
			final List<Explanation> notFoundExplanations,//
			final List<Explanation> generatedExplanations,//
			final List<Explanation> expectedExplanations)
	{
		final Map<String, Explanation> gen = new HashMap<>();
		generatedExplanations.forEach(e -> gen.put(e.getRepresentation(), e));

		final Map<String, Explanation> exp = new HashMap<>();
		expectedExplanations.forEach(e -> exp.put(e.getRepresentation(), e));

		generatedExplanations.stream().filter(e -> !exp.containsKey(e.getRepresentation())).forEach(unexpectedExplanations::add);
		expectedExplanations.stream().filter(e -> !gen.containsKey(e.getRepresentation())).forEach(notFoundExplanations::add);

		Collections.sort(generatedExplanations);
		Collections.sort(expectedExplanations);
		Collections.sort(unexpectedExplanations);
		Collections.sort(notFoundExplanations);
	}

	@Override
	public void testExplanations(final OWLAxiom axiom, final int max, final Set<Set<OWLAxiom>> expectedExplanationsUnordered) throws Exception
	{
		final OWLClassExpression unsatClass = converter.convert(axiom);

		if (log.isLoggable(Level.FINE))
			log.fine("Axiom " + (++axiomCount) + ": " + axiom + " Expecting " + expectedExplanationsUnordered.size() + " explanations");

		final Set<Set<OWLAxiom>> generatedExplanationsUnordered = expGen.getExplanations(unsatClass, max);

		if (log.isLoggable(Level.FINER))
		{
			final StringWriter sw = new StringWriter();
			renderer.startRendering(sw);
			renderer.render(axiom, expectedExplanationsUnordered);
			renderer.endRendering();
			log.finer("Expected:\n" + sw);
		}

		assertNotNull("Axiom " + axiom + " not entailed", generatedExplanationsUnordered);

		final List<Explanation> expectedExplanations = expectedExplanationsUnordered.stream().map(Explanation::new).collect(Collectors.toList());
		final List<Explanation> generatedExplanations = generatedExplanationsUnordered.stream().map(Explanation::new).collect(Collectors.toList());
		final List<Explanation> notFoundExplanations = new ArrayList<>();
		final List<Explanation> unexpectedExplanations = new ArrayList<>();

		compareExplanations(unexpectedExplanations, notFoundExplanations, generatedExplanations, expectedExplanations);

		if (!notFoundExplanations.isEmpty() || !unexpectedExplanations.isEmpty())
		{
			final StringBuilder buff = new StringBuilder();

			buff.append("\n\nExpected:\n");
			expectedExplanations.forEach(e -> buff.append(e.getRepresentation()));

			if (!notFoundExplanations.isEmpty())
			{
				buff.append("\n\nNot Found:\n");
				notFoundExplanations.forEach(e -> buff.append(e.getRepresentation()));
			}
			if (!unexpectedExplanations.isEmpty())
			{
				buff.append("\n\nUnexpected:\n");
				unexpectedExplanations.forEach(e -> buff.append(e.getRepresentation()));
			}

			System.out.println("Error in explanation: " + buff);

			org.junit.Assert.fail("Error in explanation, see the log file for details");
		}

	}
}
