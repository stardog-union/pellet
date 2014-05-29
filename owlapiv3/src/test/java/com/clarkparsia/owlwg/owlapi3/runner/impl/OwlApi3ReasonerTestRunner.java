package com.clarkparsia.owlwg.owlapi3.runner.impl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * <p>
 * Title: OWLAPI v3 Reasoner Test Runner
 * </p>
 * <p>
 * Description: Wrapper to use any reasoner implementing the OWLAPI OWLReasoner
 * interface to run reasoning test cases.
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
public class OwlApi3ReasonerTestRunner extends OwlApi3AbstractRunner {

	private final OWLReasonerFactory	reasonerFactory;
	private final IRI					iri;

	public OwlApi3ReasonerTestRunner(OWLReasonerFactory reasonerFactory, IRI runnerUri) {
		this.reasonerFactory = reasonerFactory;
		this.iri = runnerUri;
	}

	public String getName() {
		return reasonerFactory.getReasonerName();
	}

	public IRI getIRI() {
		return iri;
	}

	protected boolean isConsistent(OWLOntology o) {
		OWLReasoner reasoner = reasonerFactory.createReasoner( o );
		return reasoner.isConsistent();
	}

	protected boolean isEntailed(OWLOntology premise, OWLOntology conclusion) {
		OWLReasoner reasoner = reasonerFactory.createReasoner( premise );

		return reasoner.isEntailed( conclusion.getLogicalAxioms() );
	}

}
