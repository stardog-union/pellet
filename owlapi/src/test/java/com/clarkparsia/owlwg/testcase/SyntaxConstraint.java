package com.clarkparsia.owlwg.testcase;

import org.semanticweb.owlapi.model.OWLIndividual;

import com.clarkparsia.owlwg.testcase.TestVocabulary.Individual;

/**
 * <p>
 * Title: Syntax Constraint
 * </p>
 * <p>
 * Description: Enumeration used for profile and species identification. See <a
 * href="http://www.w3.org/TR/owl2-test/#Species">OWL 2 Conformance: Species</a>
 * and <a href="http://www.w3.org/TR/owl2-test/#Profiles">Profiles</a>.
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

public enum SyntaxConstraint {

	DL(Individual.DL), EL(Individual.EL), QL(Individual.QL), RL(Individual.RL);

	public static SyntaxConstraint get(OWLIndividual i) {
		for( SyntaxConstraint c : values() ) {
			if( c.getOWLIndividual().equals( i ) )
				return c;
		}
		return null;
	}

	private final TestVocabulary.Individual	_i;

	private SyntaxConstraint(TestVocabulary.Individual i) {
		this._i = i;
	}

	public OWLIndividual getOWLIndividual() {
		return _i.getOWLIndividual();
	}
}