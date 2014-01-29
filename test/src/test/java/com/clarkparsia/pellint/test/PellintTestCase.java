package com.clarkparsia.pellint.test;

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

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;

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
public class PellintTestCase {
	protected static final double DOUBLE_DELTA = 0.000001;
	
	protected OWLOntologyManager m_Manager;
	protected OWLOntology m_Ontology;
	protected OWLClass[] m_Cls;
	protected OWLObjectProperty[] m_Pro;
	protected OWLIndividual[] m_Ind;
	
	protected OWLClassExpression m_P0AllC0;
	protected OWLClassExpression m_P0SomeC1;

	@Before
	public void setUp() throws OWLOntologyCreationException {
		m_Manager = OWLManager.createOWLOntologyManager();
		IRI ontologyURI = IRI.create("tag:clarkparsia.com,2008:pellint:test");
		m_Ontology = m_Manager.createOntology(ontologyURI);
		
		m_Cls = new OWLClass[5];
		for (int i = 0; i < m_Cls.length; i++) {
			m_Cls[i] = OWL.Class(ontologyURI + "#C" + i); 
		}
		
		m_Pro = new OWLObjectProperty[5];
		for (int i = 0; i < m_Pro.length; i++) {
			m_Pro[i] = OWL.ObjectProperty(ontologyURI + "#R" + i); 
		}
		
		m_Ind = new OWLIndividual[5];
		for (int i = 0; i < m_Ind.length; i++) {
			m_Ind[i] = OWL.Individual(ontologyURI + "#I" + i); 
		}
		
		m_P0AllC0 = OWL.all(m_Pro[0], m_Cls[0]);
		m_P0SomeC1 = OWL.some(m_Pro[0], m_Cls[1]);
	}
	
	protected void addAxiom(OWLAxiom axiom) throws OWLException {
		OntologyUtils.addAxioms(m_Ontology, Collections.singleton(axiom));
	}
}
