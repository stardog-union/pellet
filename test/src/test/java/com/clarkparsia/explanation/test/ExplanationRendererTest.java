// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertEquals;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.XSD;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxObjectRenderer;
import com.clarkparsia.owlapi.explanation.io.manchester.TextBlockWriter;
import java.io.StringWriter;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class ExplanationRendererTest
{
	private static final OWLClass _A = OWL.Class("A");
	private static final OWLClass _B = OWL.Class("B");
	private static final OWLObjectProperty _p = OWL.ObjectProperty("p");
	private static final OWLObjectProperty _q = OWL.ObjectProperty("q");
	private static final OWLIndividual _a = OWL.Individual("a");

	protected void assertRendering(final String expected, final OWLObject obj)
	{
		final StringWriter sw = new StringWriter();
		final ManchesterSyntaxObjectRenderer renderer = new ManchesterSyntaxObjectRenderer(new TextBlockWriter(sw));
		obj.accept(renderer);
		final String actual = sw.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void classAssertion()
	{
		assertRendering("a type A", OWL.classAssertion(_a, _A));
	}

	@Test
	public void subClassOf()
	{
		assertRendering("A subClassOf B", OWL.subClassOf(_A, _B));
	}

	@Test
	public void subPropertyOf()
	{
		final OWLObjectPropertyExpression[] a = { _p, _q };
		assertRendering("p o q subPropertyOf p", OWL.subPropertyOf(a, _p));
	}

	@Test
	public void qualifiedExactCardinality()
	{
		assertRendering("p exactly 1 A", OWL.exactly(_p, 1, _A));
	}

	@Test
	public void exactCardinality()
	{
		assertRendering("p exactly 1", OWL.exactly(_p, 1));
	}

	@Test
	public void someValuesFrom()
	{
		assertRendering("p some A", OWL.some(_p, _A));
	}

	@Test
	public void allValuesFrom()
	{
		assertRendering("p only A", OWL.all(_p, _A));
	}

	@Test
	public void maxExclusive()
	{
		assertRendering("double[< \"2.0\"^^double]", OWL.restrict(XSD.DOUBLE, OWL.maxExclusive(2.0)));
	}

	@Test
	public void minExclusive()
	{
		assertRendering("double[> \"2.0\"^^double]", OWL.restrict(XSD.DOUBLE, OWL.minExclusive(2.0)));
	}

	@Test
	public void maxInclusive()
	{
		assertRendering("double[<= \"2.0\"^^double]", OWL.restrict(XSD.DOUBLE, OWL.maxInclusive(2.0)));
	}

	@Test
	public void minInclusive()
	{
		assertRendering("double[>= \"2.0\"^^double]", OWL.restrict(XSD.DOUBLE, OWL.minInclusive(2.0)));
	}
}
