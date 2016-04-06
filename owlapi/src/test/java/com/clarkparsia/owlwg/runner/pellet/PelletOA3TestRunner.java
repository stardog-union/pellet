package com.clarkparsia.owlwg.runner.pellet;

import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import com.clarkparsia.owlwg.owlapi3.runner.impl.OwlApi3AbstractRunner;
import com.clarkparsia.owlwg.testrun.TestRunResult;

/**
 * <p>
 * Title: Pellet OWLAPIv3 Test Runner
 * </p>
 * <p>
 * Description: Pellet 2.0 based test case runner using alpha OWLAPIv3 support.
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
public class PelletOA3TestRunner extends OwlApi3AbstractRunner {

	private static final PelletReasonerFactory	reasonerFactory;

	private static final IRI					iri;

	static {
		iri = IRI.create( "http://clarkparsia.com/pellet" );
		reasonerFactory = new PelletReasonerFactory();
	}

	public String getName() {
		return "Pellet";
	}

	public IRI getIRI() {
		return iri;
	}

	@Override
	protected boolean isConsistent(OWLOntology o) {
		PelletReasoner reasoner = reasonerFactory.createReasoner( o );
		reasoner.getKB().setTimeout( timeout );
		return reasoner.isConsistent();
	}

	@Override
	protected boolean isEntailed(OWLOntology premise, OWLOntology conclusion) {
		PelletReasoner reasoner = reasonerFactory.createReasoner( premise );
		reasoner.getKB().setTimeout( timeout );
		return reasoner.isEntailed( conclusion.getLogicalAxioms() );
	}

	protected TestRunResult run(TestAsRunnable runnable) {
		runnable.run();

		try {
			return runnable.getResult();
		} catch( Throwable th ) {
			System.gc();
			return runnable.getErrorResult( th );
		}

	}
}
