package com.clarkparsia.owlwg.runner;

import java.util.Collection;

import org.semanticweb.owlapi.model.IRI;

import com.clarkparsia.owlwg.testcase.TestCase;
import com.clarkparsia.owlwg.testrun.TestRunResult;

/**
 * <p>
 * Title: Read Only Test Runner
 * </p>
 * <p>
 * Description: Test runner implementation that isn't capable of running tests,
 * but can be used when a read only object is needed (e.g., parsing results for
 * reporting).
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
public class ReadOnlyTestRunner<T> implements TestRunner<T> {

	public static ReadOnlyTestRunner testRunner(IRI iri, String name) {
		return new ReadOnlyTestRunner( iri, name );
	}

	final private IRI		iri;
	final private String	name;

	public ReadOnlyTestRunner(IRI iri, String name) {
		this.iri = iri;
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;

		if( obj instanceof ReadOnlyTestRunner ) {
			final ReadOnlyTestRunner other = (ReadOnlyTestRunner) obj;
			return this.iri.equals( other.iri );
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public IRI getIRI() {
		return iri;
	}

	@Override
	public int hashCode() {
		return iri.hashCode();
	}

	public Collection<TestRunResult> run(TestCase<T> testcase, long timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return iri.toString();
	}

	public void dispose() {
	}
}