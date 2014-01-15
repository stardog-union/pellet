package com.clarkparsia.owlwg.owlapi3.testcase.impl;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlwg.testcase.TestCaseFactory;

/**
 * <p>
 * Title: OWLAPIv3 Test Case Factory
 * </p>
 * <p>
 * Description:
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
public class OwlApi3TestCaseFactory implements TestCaseFactory<OWLOntology> {

	public OwlApi3ConTstImpl getConsistencyTestCase(OWLOntology o,
			OWLNamedIndividual i) {
		return new OwlApi3ConTstImpl( o, i );
	}

	public OwlApi3IncTstImpl getInconsistencyTestCase(OWLOntology o,
			OWLNamedIndividual i) {
		return new OwlApi3IncTstImpl( o, i );
	}

	public OwlApi3NegTstImpl getNegativeEntailmentTestCase(OWLOntology o,
			OWLNamedIndividual i) {
		return new OwlApi3NegTstImpl( o, i );
	}

	public OwlApi3PosTstImpl getPositiveEntailmentTestCase(OWLOntology o,
			OWLNamedIndividual i) {
		return new OwlApi3PosTstImpl( o, i );
	}

}
