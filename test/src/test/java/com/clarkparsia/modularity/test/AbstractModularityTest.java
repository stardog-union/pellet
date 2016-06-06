// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.owlapi.OWL.Class;
import static com.clarkparsia.owlapi.OWL.Individual;
import static com.clarkparsia.owlapi.OWL.ObjectProperty;

import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import org.junit.After;
import org.junit.Before;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: Tests modularity results for simple hand-made ontologies.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public abstract class AbstractModularityTest
{
	//	protected static final OWLOntologyManager	manager		= OWL.manager;

	protected OWLOntology _ontology;
	protected ModuleExtractor _modExtractor;

	protected OWLClass _A = Class("A");
	protected OWLClass _B = Class("B");
	protected OWLClass _C = Class("C");
	protected OWLClass _D = Class("D");
	protected OWLClass _E = Class("E");
	protected OWLClass _F = Class("F");
	protected OWLClass _G = Class("G");
	protected OWLClass _H = Class("H");

	protected OWLNamedIndividual _a = Individual("a");
	protected OWLNamedIndividual _b = Individual("b");
	protected OWLNamedIndividual _c = Individual("c");
	protected OWLNamedIndividual _d = Individual("d");
	protected OWLNamedIndividual _e = Individual("e");
	protected OWLNamedIndividual _f = Individual("f");
	protected OWLNamedIndividual _g = Individual("g");
	protected OWLNamedIndividual _h = Individual("h");

	protected OWLObjectProperty _p = ObjectProperty("p");
	protected OWLObjectProperty _q = ObjectProperty("q");

	public AbstractModularityTest()
	{
	}

	public abstract ModuleExtractor createModuleExtractor();

	protected void createOntology(final OWLAxiom... axioms)
	{
		_ontology = OWL.Ontology(axioms);
	}

	@Before
	public void before()
	{
		// create an empty module extractor
		_modExtractor = createModuleExtractor();
	}

	@After
	public void after()
	{
		_modExtractor = null;
		OntologyUtils.clearOWLOntologyManager();
	}
}
