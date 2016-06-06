package com.clarkparsia.pellint.test;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import java.util.Collections;
import org.junit.Before;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * <p>
 * Title: Pellint test fixture abstraction
 * </p>
 * <p>
 * Description: Provides common setUp for all Pellint tests
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class PellintTestCase
{
	protected static final double DOUBLE_DELTA = 0.000001;

	protected OWLOntologyManager _manager;
	protected OWLOntology _ontology;
	protected OWLClass[] _cls;
	protected OWLObjectProperty[] _pro;
	protected OWLIndividual[] _ind;

	protected OWLClassExpression _P0AllC0;
	protected OWLClassExpression _P0SomeC1;

	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		_manager = OWLManager.createOWLOntologyManager();
		final IRI ontologyURI = IRI.create("tag:clarkparsia.com,2008:pellint:test");
		_ontology = _manager.createOntology(ontologyURI);

		_cls = new OWLClass[5];
		for (int i = 0; i < _cls.length; i++)
			_cls[i] = OWL.Class(ontologyURI + "#C" + i);

		_pro = new OWLObjectProperty[5];
		for (int i = 0; i < _pro.length; i++)
			_pro[i] = OWL.ObjectProperty(ontologyURI + "#R" + i);

		_ind = new OWLIndividual[5];
		for (int i = 0; i < _ind.length; i++)
			_ind[i] = OWL.Individual(ontologyURI + "#I" + i);

		_P0AllC0 = OWL.all(_pro[0], _cls[0]);
		_P0SomeC1 = OWL.some(_pro[0], _cls[1]);
	}

	@SuppressWarnings("unused")
	protected void addAxiom(final OWLAxiom axiom) throws OWLException
	{
		OntologyUtils.addAxioms(_ontology, Collections.singleton(axiom));
	}
}
