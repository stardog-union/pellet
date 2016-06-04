// Copyright (c) 2006 - 2009, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.owlapi.OWL.classAssertion;
import static com.clarkparsia.owlapi.OWL.constant;
import static com.clarkparsia.owlapi.OWL.declaration;
import static com.clarkparsia.owlapi.OWL.domain;
import static com.clarkparsia.owlapi.OWL.or;
import static com.clarkparsia.owlapi.OWL.propertyAssertion;
import static com.clarkparsia.owlapi.OWL.range;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.XSD;
import com.clarkparsia.pellet.test.owlapi.AbstractOWLAPITests;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import java.util.Properties;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Tests primarily focused on the behavior of {@link PelletLoader}
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 * @author Evren Sirin
 */
public class RBoxUpdateTests extends AbstractOWLAPITests
{

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(RBoxUpdateTests.class);
	}

	private static Properties oldOptions;

	@BeforeClass
	public static void enableTracing()
	{
		final Properties newOptions = PropertiesBuilder.singleton("USE_TRACING", "true");
		oldOptions = PelletOptions.setOptions(newOptions);
	}

	@AfterClass
	public static void resetTracing()
	{
		PelletOptions.setOptions(oldOptions);
	}

	/**
	 * A _data property domain axiom should be removable without causing a full KB reload
	 */
	@Test
	public void removeDataPropertyDomainAxiom()
	{
		createReasoner(declaration(_dp), declaration(_C), domain(_dp, _C), propertyAssertion(_a, _dp, _lit));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_a, _C)));

		final boolean changeApplied = processRemove(domain(_dp, _C));
		assertTrue("Unable to remove _data property domain axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _C)));
	}

	/**
	 * A _data property domain axiom should be removable without causing a full KB reload even if it is a class expression
	 */
	@Test
	public void removeDataPropertyDomainAxiomExpression()
	{
		createReasoner(declaration(_dp), declaration(_C), declaration(_D), domain(_dp, or(_C, _D)), propertyAssertion(_a, _dp, _lit));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_a, or(_C, _D))));

		final boolean changeApplied = processRemove(domain(_dp, or(_C, _D)));
		assertTrue("Unable to remove _data property domain axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
		assertFalse(_reasoner.isEntailed(classAssertion(_a, or(_C, _D))));
	}

	/**
	 * A _data property range axiom should be removable without causing a full KB reload
	 */
	@Test
	public void removeDataPropertyRangeAxiom()
	{

		createReasoner(declaration(_dp), declaration(_C), range(_dp, XSD.INTEGER), propertyAssertion(_a, _dp, constant("foo")));

		assertFalse(_reasoner.isConsistent());

		final boolean changeApplied = processRemove(range(_dp, XSD.INTEGER));
		assertTrue("Unable to remove _data property range axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
	}

	/**
	 * An object property domain axiom should be removable without causing a full KB reload
	 */
	@Test
	public void removeObjectPropertyDomainAxiom()
	{
		createReasoner(declaration(_p), declaration(_C), domain(_p, _C), propertyAssertion(_a, _p, _b));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_a, _C)));

		final boolean changeApplied = processRemove(domain(_p, _C));
		assertTrue("Unable to remove object property domain axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
		assertFalse(_reasoner.isEntailed(classAssertion(_a, _C)));
	}

	/**
	 * An object property domain axiom should be removable without causing a full KB reload even if it is a class expression
	 */
	@Test
	public void removeObjectPropertyDomainAxiomExpression()
	{
		createReasoner(declaration(_p), declaration(_C), declaration(_D), domain(_p, or(_C, _D)), propertyAssertion(_a, _p, _b));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_a, or(_C, _D))));

		final boolean changeApplied = processRemove(domain(_p, or(_C, _D)));
		assertTrue("Unable to remove object property domain axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
		assertFalse(_reasoner.isEntailed(classAssertion(_a, or(_C, _D))));
	}

	/**
	 * An object property range axiom should be removable without causing a full KB reload
	 */
	@Test
	public void removeObjectPropertyRangeAxiom()
	{
		createReasoner(declaration(_p), declaration(_C), range(_p, _C), propertyAssertion(_a, _p, _b));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_b, _C)));

		final boolean changeApplied = processRemove(range(_p, _C));
		assertTrue("Unable to remove object property range axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
		assertFalse(_reasoner.isEntailed(classAssertion(_b, _C)));
	}

	/**
	 * An object property range axiom should be removable without causing a full KB reload even if it is a class expression
	 */
	@Test
	public void removeObjectPropertyRangeAxiomExpression()
	{
		createReasoner(declaration(_p), declaration(_C), declaration(_D), range(_p, or(_C, _D)), propertyAssertion(_a, _p, _b));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_b, or(_C, _D))));

		final boolean changeApplied = processRemove(range(_p, or(_C, _D)));
		assertTrue("Unable to remove object property range axiom", changeApplied);

		assertTrue(_reasoner.isConsistent());
		assertFalse(_reasoner.isEntailed(classAssertion(_b, or(_C, _D))));
	}

	@Test
	public void removeAndAddObjectPropertyDomainAxiom()
	{
		createReasoner(declaration(_p), declaration(_C), domain(_p, _C), propertyAssertion(_a, _p, _b));

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_a, _C)));

		final boolean removeApplied = processRemove(domain(_p, _C));
		assertTrue("Unable to remove object property domain axiom", removeApplied);

		final boolean addApplied = processAdd(domain(_p, _C));
		assertTrue("Unable to add object property domain axiom", addApplied);

		assertTrue(_reasoner.isConsistent());
		assertTrue(_reasoner.isEntailed(classAssertion(_a, _C)));
	}
}
