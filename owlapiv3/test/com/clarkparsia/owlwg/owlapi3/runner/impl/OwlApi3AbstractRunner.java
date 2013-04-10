package com.clarkparsia.owlwg.owlapi3.runner.impl;

import static com.clarkparsia.owlwg.testrun.RunResultType.FAILING;
import static com.clarkparsia.owlwg.testrun.RunResultType.INCOMPLETE;
import static com.clarkparsia.owlwg.testrun.RunResultType.PASSING;
import static com.clarkparsia.owlwg.testrun.RunTestType.CONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.INCONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.NEGATIVE_ENTAILMENT;
import static com.clarkparsia.owlwg.testrun.RunTestType.POSITIVE_ENTAILMENT;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.owlwg.owlapi3.testcase.impl.OwlApi3Case;
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

/**
 * <p>
 * Title: OWLAPIv3 Abstract Test Runner
 * </p>
 * <p>
 * Description: Base test runner implementation intended to encapsulate
 * non-interesting bits of the test runner and make reuse and runner
 * implementation easier. Handles test type-specific behavior and timeout
 * enforcement.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public abstract class OwlApi3AbstractRunner implements TestRunner<OWLOntology> {

	private static final SerializationFormat[]	formatList;

	static {
		formatList = new SerializationFormat[] {
			SerializationFormat.RDFXML, SerializationFormat.FUNCTIONAL, SerializationFormat.OWLXML };
	}

	protected abstract class AbstractTestAsRunnable<T extends TestCase<OWLOntology>> implements
			TestAsRunnable {

		protected TestRunResult				result;
		protected final T					testcase;
		protected Throwable					throwable;
		protected final RunTestType			type;

		public AbstractTestAsRunnable( T testcase, RunTestType type) {
			this.testcase = testcase;

			if( !EnumSet.of( CONSISTENCY, INCONSISTENCY, NEGATIVE_ENTAILMENT, POSITIVE_ENTAILMENT )
					.contains( type ) )
				throw new IllegalArgumentException();

			this.type = type;
			result = null;
			throwable = null;
		}

		public TestRunResult getErrorResult(Throwable th) {
			th.printStackTrace();
			return new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this, th
					.getMessage(), th );
		}

		public TestRunResult getResult() throws Throwable {
			if( throwable != null )
				throw throwable;
			if( result == null )
				throw new IllegalStateException();

			return result;
		}

		public TestRunResult getTimeoutResult() {
			return new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this, String
					.format( "Timeout: %s ms", timeout ) );
		}
	}

	private class Runner implements TestCaseVisitor<OWLOntology> {

		private TestRunResult[]		results;

		public List<TestRunResult> getResults(OwlApi3Case testcase) {
			results = null;
			testcase.accept( this );
			return Arrays.asList( results );
		}

		public void visit(ConsistencyTest<OWLOntology> testcase) {
			results = new TestRunResult[1];
			results[0] = runConsistencyTest( testcase );
		}

		public void visit(InconsistencyTest<OWLOntology> testcase) {
			results = new TestRunResult[1];
			results[0] = runInconsistencyTest( testcase );
		}

		public void visit(NegativeEntailmentTest<OWLOntology> testcase) {
			results = new TestRunResult[2];
			results[0] = runConsistencyTest( testcase );
			results[1] = runEntailmentTest(testcase );
		}

		public void visit(PositiveEntailmentTest<OWLOntology> testcase) {
			results = new TestRunResult[2];
			results[0] = runConsistencyTest( testcase );
			results[1] = runEntailmentTest( testcase );
		}
	}

	protected interface TestAsRunnable extends Runnable {
		public TestRunResult getErrorResult(Throwable th);

		public TestRunResult getResult() throws Throwable;

		public TestRunResult getTimeoutResult();
	}

	protected class XConsistencyTest extends AbstractTestAsRunnable<PremisedTest<OWLOntology>> {

		public XConsistencyTest( PremisedTest<OWLOntology> testcase,
				RunTestType type) {
			super( testcase, type );

			if( !EnumSet.of( CONSISTENCY, INCONSISTENCY ).contains( type ) )
				throw new IllegalArgumentException();
		}

		public void run() {
			SerializationFormat fmt = null;
			for( SerializationFormat f : formatList ) {
				if( testcase.getPremiseFormats().contains( f ) ) {
					fmt = f;
					break;
				}
			}
			if( fmt == null ) {
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"No acceptable serialization formats found for premise ontology." );
				return;
			}

			OWLOntology o;
			try {
				long parseStart = System.currentTimeMillis();
				o = testcase.parsePremiseOntology( fmt );
				long parseEnd = System.currentTimeMillis();
				System.err.println(testcase.getIdentifier() + " parse time " + ((parseEnd - parseStart)/1000));
			} catch( OntologyParseException e ) {
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"Exception parsing premise ontology: " + e.getLocalizedMessage(), e );
				return;
			}

			try {
				final boolean consistent = isConsistent( o );
				if( consistent )
					result = new ReasoningRun( testcase, CONSISTENCY.equals( type )
						? PASSING
						: FAILING, type, OwlApi3AbstractRunner.this );
				else
					result = new ReasoningRun( testcase, INCONSISTENCY.equals( type )
						? PASSING
						: FAILING, type, OwlApi3AbstractRunner.this );
			} catch( Throwable th ) {
				th.printStackTrace();
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"Caught throwable: " + th.getLocalizedMessage(), th );
			}
		}

	}

	protected class XEntailmentTest extends AbstractTestAsRunnable<EntailmentTest<OWLOntology>> {

		public XEntailmentTest(EntailmentTest<OWLOntology> testcase,
				RunTestType type) {
			super( testcase, type );

			if( !EnumSet.of( POSITIVE_ENTAILMENT, NEGATIVE_ENTAILMENT ).contains( type ) )
				throw new IllegalArgumentException();
		}

		public void run() {
			SerializationFormat pFmt = null, cFmt = null;
			for( SerializationFormat f : formatList ) {
				if( testcase.getPremiseFormats().contains( f ) ) {
					pFmt = f;
					break;
				}
			}
			if( pFmt == null ) {
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"No acceptable serialization formats found for premise ontology." );
				return;
			}
			for( SerializationFormat f : formatList ) {
				if( testcase.getConclusionFormats().contains( f ) ) {
					cFmt = f;
					break;
				}
			}
			if( cFmt == null ) {
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"No acceptable serialization formats found for conclusion ontology." );
				return;
			}

			OWLOntology premise, conclusion;
			try {
				long parseStart = System.currentTimeMillis();
				premise = testcase.parsePremiseOntology( pFmt );
				conclusion = testcase.parseConclusionOntology( cFmt );
				long parseEnd = System.currentTimeMillis();
				System.err.println(testcase.getIdentifier() + " parse time " + ((parseEnd - parseStart)/1000));
			} catch( OntologyParseException e ) {
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"Exception parsing input ontology: " + e.getLocalizedMessage(), e );
				return;
			}

			try {
				boolean entailed = isEntailed(premise, conclusion );
				if( entailed )
					result = new ReasoningRun( testcase, POSITIVE_ENTAILMENT.equals( type )
						? PASSING
						: FAILING, type, OwlApi3AbstractRunner.this );
				else
					result = new ReasoningRun( testcase, NEGATIVE_ENTAILMENT.equals( type )
						? PASSING
						: FAILING, type, OwlApi3AbstractRunner.this );
			} catch( Throwable th ) {
				System.gc();
				th.printStackTrace();
				result = new ReasoningRun( testcase, INCOMPLETE, type, OwlApi3AbstractRunner.this,
						"Caught throwable: " + th.getLocalizedMessage(), th );
			}
		}
	}

	protected static final Logger	log;

	static {
		log = Logger.getLogger( OwlApi3AbstractRunner.class.getCanonicalName() );
	}

	private final Runner		runner;
	protected long				timeout;

	public OwlApi3AbstractRunner() {
		runner = new Runner();
	}

	public void dispose() {
	}

	protected abstract boolean isConsistent(OWLOntology o);

	protected abstract boolean isEntailed(OWLOntology premise,
			OWLOntology conclusion);

	protected TestRunResult run(TestAsRunnable runnable) {
		Thread t = new Thread( runnable );
		t.start();
		try {
			t.join( timeout );
		} catch( InterruptedException e ) {
			return runnable.getErrorResult( e );
		}
		if( t.isAlive() ) {
			try {
				t.stop();
				return runnable.getTimeoutResult();
			} catch( OutOfMemoryError oome ) {
				log.warning( "Out of memory allocating timeout response. Retrying." );
				System.gc();
				return runnable.getTimeoutResult();
			}
		}
		else {
			try {
				return runnable.getResult();
			} catch( Throwable th ) {
				return runnable.getErrorResult( th );
			}
		}
	}

	public Collection<TestRunResult> run(TestCase<OWLOntology> testcase, long timeout) {
		this.timeout = timeout;
		if( testcase instanceof OwlApi3Case )
			return runner.getResults( (OwlApi3Case) testcase );
		else
			throw new IllegalArgumentException();
	}

	protected TestRunResult runConsistencyTest(PremisedTest<OWLOntology> testcase) {
		return run( new XConsistencyTest( testcase, CONSISTENCY ) );
	}

	protected TestRunResult runEntailmentTest(NegativeEntailmentTest<OWLOntology> testcase) {
		return run( new XEntailmentTest(testcase, NEGATIVE_ENTAILMENT ) );
	}

	protected TestRunResult runEntailmentTest(PositiveEntailmentTest<OWLOntology> testcase) {
		return run( new XEntailmentTest( testcase, POSITIVE_ENTAILMENT ) );
	}

	protected TestRunResult runInconsistencyTest(InconsistencyTest<OWLOntology> testcase) {
		return run( new XConsistencyTest( testcase, INCONSISTENCY ) );
	}
}
