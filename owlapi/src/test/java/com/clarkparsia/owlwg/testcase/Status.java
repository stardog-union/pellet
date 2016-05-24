package com.clarkparsia.owlwg.testcase;

import org.semanticweb.owlapi.model.OWLIndividual;

import com.clarkparsia.owlwg.testcase.TestVocabulary.Individual;

/**
 * <p>
 * Title: Status
 * </p>
 * <p>
 * Description: See <a href="http://www.w3.org/TR/owl2-test/#Status">OWL 2
 * Conformance: Status</a>.
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
public enum Status {

	APPROVED(Individual.APPROVED), EXTRACREDIT(Individual.EXTRACREDIT),
	PROPOSED(Individual.PROPOSED), REJECTED(Individual.REJECTED);

	public static Status get(OWLIndividual i) {
		for( Status s : Status.values() ) {
			if( s.getOWLIndividual().equals( i ) )
				return s;
		}

		return null;
	}

	private final TestVocabulary.Individual	_i;

	private Status(TestVocabulary.Individual i) {
		this._i = i;
	}

	public OWLIndividual getOWLIndividual() {
		return _i.getOWLIndividual();
	}
}