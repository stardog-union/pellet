package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.Semantics;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Semantics Filter Condition
 * </p>
 * <p>
 * Description: Filter _condition to match tests for which a particular semantics
 * is applicable.
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
public class SemanticsFilter implements FilterCondition {
	
	public static final SemanticsFilter	DIRECT, RDF;

	static {
		DIRECT = new SemanticsFilter( Semantics.DIRECT );
		RDF = new SemanticsFilter( Semantics.RDF );
	}

	final private Semantics	semantics;

	/**
	 * @throws NullPointerException
	 *             if <code>semantics == null</code>
	 */
	public SemanticsFilter(Semantics semantics) {
		if( semantics == null )
			throw new NullPointerException();

		this.semantics = semantics;
	}

	public boolean accepts(TestCase testcase) {
		return testcase.getApplicableSemantics().contains( semantics );
	}

	@Override
	public String toString() {
		return semantics.toString();
	}
}
