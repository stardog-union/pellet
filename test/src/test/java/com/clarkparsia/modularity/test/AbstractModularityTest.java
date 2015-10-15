// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import java.util.List;

import static com.clarkparsia.owlapiv3.OWL.Class;
import static com.clarkparsia.owlapiv3.OWL.Individual;
import static com.clarkparsia.owlapiv3.OWL.ObjectProperty;

import com.clarkparsia.modularity.AxiomBasedModuleExtractor;
import com.clarkparsia.modularity.GraphBasedModuleExtractor;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mindswap.pellet.test.PelletTestSuite;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;

/**
 * <p>
 * Title: Tests modularity results for simple hand-made ontologies.
 * </p>
 * <p>
 * Description:
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
@RunWith(Parameterized.class)
public class AbstractModularityTest {
	public static final String	base	= PelletTestSuite.base + "modularity/";

//	protected static final OWLOntologyManager	manager		= OWL.manager;
	
	protected OWLOntology						ontology;
	protected ModuleExtractor					modExtractor;
	
	protected OWLClass							A	= Class( "A" );
	protected OWLClass							B	= Class( "B" );
	protected OWLClass							C	= Class( "C" );
	protected OWLClass							D	= Class( "D" );
	protected OWLClass							E	= Class( "E" );
	protected OWLClass							F	= Class( "F" );
	protected OWLClass							G	= Class( "G" );
	protected OWLClass							H	= Class("H");
	
	protected OWLNamedIndividual				a  = Individual( "a" );
	protected OWLNamedIndividual				b  = Individual( "b" );
	protected OWLNamedIndividual				c  = Individual( "c" );
	protected OWLNamedIndividual				d  = Individual( "d" );
	protected OWLNamedIndividual				e  = Individual( "e" );
	protected OWLNamedIndividual				f  = Individual( "f" );
	protected OWLNamedIndividual				g  = Individual( "g" );
	protected OWLNamedIndividual				h  = Individual("h");
	
	protected OWLObjectProperty					p	= ObjectProperty( "p" );
	protected OWLObjectProperty					q	= ObjectProperty( "q" );

	private final Supplier<ModuleExtractor> modExtractorSupplier;

	public AbstractModularityTest(final Supplier<ModuleExtractor> theModExtractorSupplier) {
		modExtractorSupplier = theModExtractorSupplier;
	}

	public final ModuleExtractor createModuleExtractor() {
		return modExtractorSupplier.get();
	}
	
	protected void createOntology(OWLAxiom... axioms) {
		ontology = OWL.Ontology( axioms );
	}

	@Before
	public void before() {
		// create an empty module extractor
		modExtractor = createModuleExtractor();
	}

	@After
	public void after() {
		modExtractor = null;
		OntologyUtils.clearOWLOntologyManager();
	}

	@Parameterized.Parameters
	public static List<Object[]> getParameters() {
		Supplier<ModuleExtractor> axiomBasedSupplier = new Supplier<ModuleExtractor>() {
			@Override
			public ModuleExtractor get() {
				return new AxiomBasedModuleExtractor();
			}
		};
		Supplier<ModuleExtractor> graphBasedSupplier = new Supplier<ModuleExtractor>() {
			@Override
			public ModuleExtractor get() {
				return new GraphBasedModuleExtractor();
			}
		};
		return Lists.newArrayList(new Object[] { axiomBasedSupplier }, new Object[] { graphBasedSupplier });
	}
}