package com.clarkparsia.owlwg.owlapi.runner.impl;

import static com.clarkparsia.owlwg.testrun.RunResultType.FAILING;
import static com.clarkparsia.owlwg.testrun.RunResultType.INCOMPLETE;
import static com.clarkparsia.owlwg.testrun.RunResultType.PASSING;
import static com.clarkparsia.owlwg.testrun.RunTestType.CONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.INCONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.NEGATIVE_ENTAILMENT;
import static com.clarkparsia.owlwg.testrun.RunTestType.POSITIVE_ENTAILMENT;

import com.clarkparsia.owlwg.owlapi.testcase.impl.OwlApiCase;
import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.ConsistencyTest;
import com.clarkparsia.owlwg.testcase.EntailmentTest;
import com.clarkparsia.owlwg.testcase.InconsistencyTest;
import com.clarkparsia.owlwg.testcase.NegativeEntailmentTest;
import com.clarkparsia.owlwg.testcase.OntologyParseException;
import com.clarkparsia.owlwg.testcase.PositiveEntailmentTest;
import com.clarkparsia.owlwg.testcase.PremisedTest;
import com.clarkparsia.owlwg.testcase.SerializationFormat;
import com.clarkparsia.owlwg.testcase.TestCase;
import com.clarkparsia.owlwg.testcase.TestCaseVisitor;
import com.clarkparsia.owlwg.testrun.ReasoningRun;
import com.clarkparsia.owlwg.testrun.RunTestType;
import com.clarkparsia.owlwg.testrun.TestRunResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: OWLAPI Abstract Test Runner
 * </p>
 * <p>
 * Description: Base test _runner implementation intended to encapsulate non-interesting bits of the test _runner and make reuse and _runner implementation
 * easier. Handles test type-specific behavior and _timeout enforcement.
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
public abstract class OwlApiAbstractRunner implements TestRunner<OWLOntology>
{

	private static final SerializationFormat[] formatList = new SerializationFormat[] { SerializationFormat.RDFXML, SerializationFormat.FUNCTIONAL, SerializationFormat.OWLXML };

	protected abstract class AbstractTestAsRunnable<T extends TestCase<OWLOntology>> implements TestAsRunnable
	{

		protected TestRunResult _result;
		protected final T _testcase;
		protected Throwable _throwable;
		protected final RunTestType _type;

		public AbstractTestAsRunnable(final T testcase, final RunTestType type)
		{
			this._testcase = testcase;

			if (!EnumSet.of(CONSISTENCY, INCONSISTENCY, NEGATIVE_ENTAILMENT, POSITIVE_ENTAILMENT).contains(type))
				throw new IllegalArgumentException();

			this._type = type;
			_result = null;
			_throwable = null;
		}

		@Override
		public TestRunResult getErrorResult(final Throwable th)
		{
			th.printStackTrace();
			return new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, th.getMessage(), th);
		}

		@Override
		public TestRunResult getResult() throws Throwable
		{
			if (_throwable != null)
				throw _throwable;
			if (_result == null)
				throw new IllegalStateException();

			return _result;
		}

		@Override
		public TestRunResult getTimeoutResult()
		{
			return new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, String.format("Timeout: %s ms", _timeout));
		}
	}

	private class Runner implements TestCaseVisitor<OWLOntology>
	{

		private TestRunResult[] results;

		public List<TestRunResult> getResults(final OwlApiCase testcase)
		{
			results = null;
			testcase.accept(this);
			return Arrays.asList(results);
		}

		@Override
		public void visit(final ConsistencyTest<OWLOntology> testcase)
		{
			results = new TestRunResult[1];
			results[0] = runConsistencyTest(testcase);
		}

		@Override
		public void visit(final InconsistencyTest<OWLOntology> testcase)
		{
			results = new TestRunResult[1];
			results[0] = runInconsistencyTest(testcase);
		}

		@Override
		public void visit(final NegativeEntailmentTest<OWLOntology> testcase)
		{
			results = new TestRunResult[2];
			results[0] = runConsistencyTest(testcase);
			results[1] = runEntailmentTest(testcase);
		}

		@Override
		public void visit(final PositiveEntailmentTest<OWLOntology> testcase)
		{
			results = new TestRunResult[2];
			results[0] = runConsistencyTest(testcase);
			results[1] = runEntailmentTest(testcase);
		}
	}

	protected interface TestAsRunnable extends Runnable
	{
		public TestRunResult getErrorResult(Throwable th);

		public TestRunResult getResult() throws Throwable;

		public TestRunResult getTimeoutResult();
	}

	protected class XConsistencyTest extends AbstractTestAsRunnable<PremisedTest<OWLOntology>>
	{

		public XConsistencyTest(final PremisedTest<OWLOntology> testcase, final RunTestType type)
		{
			super(testcase, type);

			if (!EnumSet.of(CONSISTENCY, INCONSISTENCY).contains(type))
				throw new IllegalArgumentException();
		}

		@Override
		public void run()
		{
			SerializationFormat fmt = null;
			for (final SerializationFormat f : formatList)
				if (_testcase.getPremiseFormats().contains(f))
				{
					fmt = f;
					break;
				}
			if (fmt == null)
			{
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "No acceptable serialization formats found for premise ontology.");
				return;
			}

			OWLOntology o;
			try
			{
				final long parseStart = System.currentTimeMillis();
				o = _testcase.parsePremiseOntology(fmt);
				final long parseEnd = System.currentTimeMillis();
				System.err.println(_testcase.getIdentifier() + " parse time " + ((parseEnd - parseStart) / 1000));
			}
			catch (final OntologyParseException e)
			{
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "Exception parsing premise ontology: " + e.getLocalizedMessage(), e);
				return;
			}

			try
			{
				final boolean consistent = isConsistent(o);
				if (consistent)
					_result = new ReasoningRun(_testcase, CONSISTENCY.equals(_type) ? PASSING : FAILING, _type, OwlApiAbstractRunner.this);
				else
					_result = new ReasoningRun(_testcase, INCONSISTENCY.equals(_type) ? PASSING : FAILING, _type, OwlApiAbstractRunner.this);
			}
			catch (final Throwable th)
			{
				th.printStackTrace();
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "Caught throwable: " + th.getLocalizedMessage(), th);
			}
		}

	}

	protected class XEntailmentTest extends AbstractTestAsRunnable<EntailmentTest<OWLOntology>>
	{

		public XEntailmentTest(final EntailmentTest<OWLOntology> testcase, final RunTestType type)
		{
			super(testcase, type);

			if (!EnumSet.of(POSITIVE_ENTAILMENT, NEGATIVE_ENTAILMENT).contains(type))
				throw new IllegalArgumentException();
		}

		@Override
		public void run()
		{
			SerializationFormat pFmt = null, cFmt = null;
			for (final SerializationFormat f : formatList)
				if (_testcase.getPremiseFormats().contains(f))
				{
					pFmt = f;
					break;
				}
			if (pFmt == null)
			{
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "No acceptable serialization formats found for premise ontology.");
				return;
			}
			for (final SerializationFormat f : formatList)
				if (_testcase.getConclusionFormats().contains(f))
				{
					cFmt = f;
					break;
				}
			if (cFmt == null)
			{
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "No acceptable serialization formats found for conclusion ontology.");
				return;
			}

			OWLOntology premise, conclusion;
			try
			{
				final long parseStart = System.currentTimeMillis();
				premise = _testcase.parsePremiseOntology(pFmt);
				conclusion = _testcase.parseConclusionOntology(cFmt);
				final long parseEnd = System.currentTimeMillis();
				System.err.println(_testcase.getIdentifier() + " parse time " + ((parseEnd - parseStart) / 1000));
			}
			catch (final OntologyParseException e)
			{
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "Exception parsing input ontology: " + e.getLocalizedMessage(), e);
				return;
			}

			try
			{
				final boolean entailed = isEntailed(premise, conclusion);
				if (entailed)
					_result = new ReasoningRun(_testcase, POSITIVE_ENTAILMENT.equals(_type) ? PASSING : FAILING, _type, OwlApiAbstractRunner.this);
				else
					_result = new ReasoningRun(_testcase, NEGATIVE_ENTAILMENT.equals(_type) ? PASSING : FAILING, _type, OwlApiAbstractRunner.this);
			}
			catch (final Throwable th)
			{
				System.gc();
				th.printStackTrace();
				_result = new ReasoningRun(_testcase, INCOMPLETE, _type, OwlApiAbstractRunner.this, "Caught throwable: " + th.getLocalizedMessage(), th);
			}
		}
	}

	protected static final Logger _logger = Log.getLogger(OwlApiAbstractRunner.class);

	private final Runner _runner;
	protected long _timeout;

	public OwlApiAbstractRunner()
	{
		_runner = new Runner();
	}

	protected abstract boolean isConsistent(OWLOntology o);

	protected abstract boolean isEntailed(OWLOntology premise, OWLOntology conclusion);

	@SuppressWarnings("deprecation")
	protected TestRunResult run(final TestAsRunnable runnable)
	{
		final Thread t = new Thread(runnable);
		t.start();
		try
		{
			t.join(_timeout);
		}
		catch (final InterruptedException e)
		{
			return runnable.getErrorResult(e);
		}
		if (t.isAlive())
			try
			{
				t.stop();
				return runnable.getTimeoutResult();
			}
			catch (final OutOfMemoryError oome)
			{
				_logger.log(Level.WARNING, "Out of memory allocating _timeout response. Retrying.", oome);
				System.gc();
				return runnable.getTimeoutResult();
			}
		else
			try
			{
				return runnable.getResult();
			}
			catch (final Throwable th)
			{
				return runnable.getErrorResult(th);
			}
	}

	@Override
	public Collection<TestRunResult> run(final TestCase<OWLOntology> testcase, final long timeout)
	{
		_timeout = timeout;
		if (testcase instanceof OwlApiCase)
			return _runner.getResults((OwlApiCase) testcase);
		else
			throw new IllegalArgumentException();
	}

	protected TestRunResult runConsistencyTest(final PremisedTest<OWLOntology> testcase)
	{
		return run(new XConsistencyTest(testcase, CONSISTENCY));
	}

	protected TestRunResult runEntailmentTest(final NegativeEntailmentTest<OWLOntology> testcase)
	{
		return run(new XEntailmentTest(testcase, NEGATIVE_ENTAILMENT));
	}

	protected TestRunResult runEntailmentTest(final PositiveEntailmentTest<OWLOntology> testcase)
	{
		return run(new XEntailmentTest(testcase, POSITIVE_ENTAILMENT));
	}

	protected TestRunResult runInconsistencyTest(final InconsistencyTest<OWLOntology> testcase)
	{
		return run(new XConsistencyTest(testcase, INCONSISTENCY));
	}
}
