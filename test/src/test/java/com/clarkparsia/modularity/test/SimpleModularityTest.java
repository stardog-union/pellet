// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static com.clarkparsia.owlapi.OWL.all;
import static com.clarkparsia.owlapi.OWL.and;
import static com.clarkparsia.owlapi.OWL.equivalentClasses;
import static com.clarkparsia.owlapi.OWL.or;
import static com.clarkparsia.owlapi.OWL.some;

import com.clarkparsia.modularity.ModuleExtractor;
import java.util.Arrays;
import org.junit.Test;
import org.mindswap.pellet.utils.MultiValueMap;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

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
public abstract class SimpleModularityTest extends AbstractModularityTest
{
	private MultiValueMap<OWLEntity, OWLEntity> _modules;

	public SimpleModularityTest()
	{
	}

	@Override
	public abstract ModuleExtractor createModuleExtractor();

	/**
	 * Creates an ontology from the given axioms and extracts the modules for each class in the ontology.
	 *
	 * @param axioms that will be used to construct the ontology
	 * @throws OWLException if ontology cannot be created
	 */
	private void extractModules(final OWLAxiom[] axioms)
	{
		_modExtractor.addAxioms(Arrays.stream(axioms));
		_modules = _modExtractor.extractModules();
	}

	/**
	 * Tests if the computed module of the given entity is same as the _expected module.
	 * 
	 * @param entity for which the module is being tested
	 * @param expectedModule _expected elements in the module
	 */
	private void testModule(final OWLEntity entity, final OWLEntity... expectedModule)
	{
		final OWLEntity[] computedModule = _modules.get(entity).toArray(new OWLEntity[0]);

		final String msg = "Extractor " + _modExtractor.getClass().getSimpleName() + " failed for " + entity;
		TestUtils.assertToStringEquals(msg, expectedModule, computedModule);
	}

	@Test
	public void intersectionTest()
	{
		final OWLAxiom[] axioms = { equivalentClasses(_A, and(_B, _C)) };

		extractModules(axioms);

		testModule(_A, _A, _B, _C);
		testModule(_B, _B);
		testModule(_C, _C);
	}

	@Test
	public void unionTest()
	{
		final OWLAxiom[] axioms = { equivalentClasses(_A, or(_B, _C)) };

		extractModules(axioms);

		testModule(_A, _A, _B, _C);
		testModule(_B, _A, _B, _C);
		testModule(_C, _A, _B, _C);
	}

	@Test
	public void nestedUnionTest()
	{
		final OWLAxiom[] axioms = { equivalentClasses(_A, and(_B, or(_C, _D))), equivalentClasses(_E, and(_B, _C)) };

		extractModules(axioms);

		testModule(_A, _A, _B, _C, _D, _E);
		testModule(_B, _B);
		testModule(_C, _C);
		testModule(_D, _D);
		testModule(_E, _A, _B, _C, _D, _E);
	}

	@Test
	public void someValuesTest()
	{
		final OWLAxiom[] axioms = { equivalentClasses(_A, some(_p, _B)) };

		extractModules(axioms);

		testModule(_A, _A, _p, _B);
		testModule(_B, _B);
	}

	@Test
	public void allValuesTest()
	{
		final OWLAxiom[] axioms = { equivalentClasses(_A, all(_p, _B)) };

		extractModules(axioms);

		testModule(_A, _A, _p, _B);
		testModule(_B, _A, _p, _B);
	}
}
