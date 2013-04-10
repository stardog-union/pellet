package com.clarkparsia.owlwg.testcase;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

public interface TestCaseFactory<O> {

	public ConsistencyTest<O> getConsistencyTestCase(OWLOntology o, OWLNamedIndividual i);

	public InconsistencyTest<O> getInconsistencyTestCase(OWLOntology o, OWLNamedIndividual i);

	public PositiveEntailmentTest<O> getPositiveEntailmentTestCase(OWLOntology o, OWLNamedIndividual i);

	public NegativeEntailmentTest<O> getNegativeEntailmentTestCase(OWLOntology o, OWLNamedIndividual i);
}
