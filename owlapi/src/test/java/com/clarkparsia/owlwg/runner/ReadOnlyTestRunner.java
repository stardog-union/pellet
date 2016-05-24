package com.clarkparsia.owlwg.runner;

import com.clarkparsia.owlwg.testcase.TestCase;
import com.clarkparsia.owlwg.testrun.TestRunResult;
import java.util.Collection;
import org.semanticweb.owlapi.model.IRI;

/**
 * <p>
 * Title: Read Only Test Runner
 * </p>
 * <p>
 * Description: Test runner implementation that isn't capable of running tests, but can be used when a read only object is needed (e.g., parsing results for
 * reporting).
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
public class ReadOnlyTestRunner<T> implements TestRunner<T>
{

	public static ReadOnlyTestRunner<?> testRunner(IRI iri, String name)
	{
		return new ReadOnlyTestRunner<>(iri, name);
	}

	final private IRI _iri;
	final private String _name;

	public ReadOnlyTestRunner(IRI iri, String name)
	{
		this._iri = iri;
		this._name = name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj instanceof ReadOnlyTestRunner)
		{
			final ReadOnlyTestRunner<?> other = (ReadOnlyTestRunner<?>) obj;
			return this._iri.equals(other._iri);
		}

		return false;
	}

	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public IRI getIRI()
	{
		return _iri;
	}

	@Override
	public int hashCode()
	{
		return _iri.hashCode();
	}

	@Override
	public Collection<TestRunResult> run(TestCase<T> testcase, long timeout)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString()
	{
		return _iri.toString();
	}
}
