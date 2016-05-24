package com.clarkparsia.owlwg.testrun;

import static com.clarkparsia.owlwg.testcase.TestVocabulary.DatatypeProperty.IDENTIFIER;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.AnnotationProperty.DETAILS;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.CONSISTENCY_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.INCONSISTENCY_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.NEGATIVE_ENTAILMENT_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.POSITIVE_ENTAILMENT_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.SYNTAX_CONSTRAINT_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.SYNTAX_TRANSLATION_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.TEST_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.ObjectProperty.RUNNER;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.ObjectProperty.TEST;
import static com.clarkparsia.owlwg.testrun.RunTestType.CONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.INCONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.NEGATIVE_ENTAILMENT;
import static com.clarkparsia.owlwg.testrun.RunTestType.POSITIVE_ENTAILMENT;
import static java.lang.String.format;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import com.clarkparsia.owlwg.runner.ReadOnlyTestRunner;
import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.SyntaxConstraint;
import com.clarkparsia.owlwg.testcase.TestCase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * <p>
 * Title: Test Run Result Parser
 * </p>
 * <p>
 * Description: Convert from OWLAPI object model to test run result objects. See also {@link TestRunResultAdapter}.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class TestRunResultParser
{

	private static final Logger _log;

	private static final Map<IRI, TestRunner<?>> _runners;

	static
	{
		_log = Logger.getLogger(TestRunResultParser.class.getCanonicalName());
		_runners = new HashMap<>();
	}

	private static TestRunner<?> getRunner(OWLNamedIndividual i, OWLOntology o)
	{
		final IRI iri = i.getIRI();
		TestRunner<?> runner = _runners.get(iri);
		if (runner == null)
		{
			String name;
			final Collection<OWLAnnotation> s = EntitySearcher.getAnnotations(i.getIRI(), o, o.getOWLOntologyManager().getOWLDataFactory().getRDFSLabel()).collect(Collectors.toList());
			if (s == null || s.isEmpty())
			{
				name = i.getIRI().toURI().toASCIIString();
			}
			else
			{
				name = s.iterator().next().getValue().toString();
			}

			runner = new ReadOnlyTestRunner<>(iri, name);
			_runners.put(iri, runner);
		}
		return runner;
	}

	public Collection<TestRunResult> getResults(OWLOntology o, Map<String, ? extends TestCase<?>> tests)
	{

		final List<TestRunResult> results = new ArrayList<>();

		for (final OWLClassAssertionAxiom axiom : asList(o.classAssertionAxioms(TEST_RUN.getOWLClass())))
		{
			final OWLNamedIndividual i = axiom.getIndividual().asOWLNamedIndividual();
			final Map<OWLObjectPropertyExpression, Collection<OWLIndividual>> oValues = EntitySearcher.getObjectPropertyValues(i, o).asMap();

			final Collection<OWLIndividual> testObjects = oValues.get(TEST.getOWLObjectProperty());
			if (testObjects.size() != 1)
			{
				_log.warning(format("Skipping result, missing or more than one test assertion (\"%s\",%s)", i.getIRI(), testObjects));
				continue;
			}
			final Map<OWLDataPropertyExpression, Collection<OWLLiteral>> testDValues = EntitySearcher.getDataPropertyValues(testObjects.iterator().next(), o).asMap();

			final Collection<OWLLiteral> ids = testDValues.get(IDENTIFIER.getOWLDataProperty());
			TestCase<?> testCase = null;
			for (final OWLLiteral c : ids)
			{
				final String id = c.getLiteral();
				testCase = tests.get(id);
				if (testCase != null)
				{
					break;
				}
			}

			if (testCase == null)
			{
				_log.warning(format("Skipping result, no matching test case found (\"%s\",%s)", i.getIRI(), ids));
				continue;
			}

			final Collection<OWLIndividual> runnerIris = oValues.get(RUNNER.getOWLObjectProperty());
			TestRunner<?> runner = null;
			if (runnerIris.size() != 1)
			{
				_log.warning(format("Skipping result, missing or more than one test runner assertion (\"%s\",%s)", i.getIRI(), runnerIris));
				continue;
			}
			runner = getRunner(runnerIris.iterator().next().asOWLNamedIndividual(), o);

			final Collection<OWLClassExpression> types = EntitySearcher.getTypes(i, o).collect(Collectors.toList());

			RunResultType resultType = null;
			for (final RunResultType t : RunResultType.values())
			{
				if (types.contains(t.getOWLClass()))
				{
					resultType = t;
					break;
				}
			}
			if (resultType == null)
			{
				_log.warning(format("Skipping result, missing result type (\"%s\")", i.getIRI()));
				continue;
			}

			final Collection<OWLAnnotation> detailsAnnotations = EntitySearcher.getAnnotations(i, o, o.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(DETAILS.getAnnotationPropertyIRI())).collect(Collectors.toList());
			String details = null;
			final int ndetails = detailsAnnotations.size();
			if (ndetails > 0)
			{
				if (ndetails > 1)
				{
					_log.info(format("Result contains multiple details annotations, ignoring all but first (\"%s\")", i.getIRI()));
				}
				details = detailsAnnotations.iterator().next().getValue().toString();
			}

			TestRunResult result = null;
			if (types.contains(SYNTAX_TRANSLATION_RUN.getOWLClass()))
			{
				result = details == null ? new SyntaxTranslationRun(testCase, resultType, runner) : new SyntaxTranslationRun(testCase, resultType, runner, details);
			}
			else
				if (types.contains(SYNTAX_CONSTRAINT_RUN.getOWLClass()))
				{
					final Collection<OWLIndividual> constraints = oValues.get(ResultVocabulary.ObjectProperty.SYNTAX_CONSTRAINT.getOWLObjectProperty());
					SyntaxConstraint constraint = null;
					if (constraints.size() != 1)
					{
						_log.warning(format("Skipping result, missing or more than one syntax constraint assertion (\"%s\",%s)", i.getIRI(), constraints));
						continue;
					}
					final OWLNamedIndividual ind = constraints.iterator().next().asOWLNamedIndividual();
					for (final SyntaxConstraint c : SyntaxConstraint.values())
					{
						if (c.getOWLIndividual().equals(ind))
						{
							constraint = c;
							break;
						}
					}
					if (constraint == null)
					{
						_log.warning(format("Skipping result, unknown syntax constraint assertion (\"%s\",%s)", i.getIRI(), ind));
						continue;
					}
					result = details == null ? new SyntaxConstraintRun(testCase, resultType, constraint, runner) : new SyntaxConstraintRun(testCase, resultType, constraint, runner, details);
				}
				else
					if (types.contains(CONSISTENCY_RUN.getOWLClass()))
					{
						result = details == null ? new ReasoningRun(testCase, resultType, CONSISTENCY, runner) : new ReasoningRun(testCase, resultType, CONSISTENCY, runner, details);
					}
					else
						if (types.contains(INCONSISTENCY_RUN.getOWLClass()))
						{
							result = details == null ? new ReasoningRun(testCase, resultType, INCONSISTENCY, runner) : new ReasoningRun(testCase, resultType, INCONSISTENCY, runner, details);
						}
						else
							if (types.contains(NEGATIVE_ENTAILMENT_RUN.getOWLClass()))
							{
								result = details == null ? new ReasoningRun(testCase, resultType, NEGATIVE_ENTAILMENT, runner) : new ReasoningRun(testCase, resultType, NEGATIVE_ENTAILMENT, runner, details);
							}
							else
								if (types.contains(POSITIVE_ENTAILMENT_RUN.getOWLClass()))
								{
									result = details == null ? new ReasoningRun(testCase, resultType, POSITIVE_ENTAILMENT, runner) : new ReasoningRun(testCase, resultType, POSITIVE_ENTAILMENT, runner, details);
								}

			results.add(result);
		}

		return results;
	}
}
