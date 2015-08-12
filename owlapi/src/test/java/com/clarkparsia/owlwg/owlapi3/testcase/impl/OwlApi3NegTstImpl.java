package com.clarkparsia.owlwg.owlapi3.testcase.impl;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlwg.testcase.TestCaseVisitor;

/**
 * <p>
 * Title: OWLAPIv3 Negative Entailment Test Case
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
public class OwlApi3NegTstImpl extends OwlApi3ETImpl implements
		com.clarkparsia.owlwg.testcase.NegativeEntailmentTest<OWLOntology> {

	public OwlApi3NegTstImpl( OWLOntology ontology, OWLNamedIndividual i) {
		super( ontology, i, false );
	}

	public void accept(TestCaseVisitor<OWLOntology> visitor) {
		visitor.visit( this );
	}

	@Override
	public String toString() {
		return String.format( "NegativeEntailmentTest(%s)", getIdentifier() );
	}
}
