// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxObjectRenderer;
import com.clarkparsia.owlapi.explanation.io.manchester.TextBlockWriter;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.XSD;

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
public class ExplanationRendererTest {
	private static final OWLClass			A	= OWL.Class( "A" );
	private static final OWLClass			B	= OWL.Class( "B" );
	private static final OWLObjectProperty	p	= OWL.ObjectProperty( "p" );
	private static final OWLObjectProperty	q	= OWL.ObjectProperty( "q" );
	private static final OWLIndividual		a	= OWL.Individual( "a" );

	protected void assertRendering(String expected, OWLObject obj) {
		StringWriter sw = new StringWriter();
		ManchesterSyntaxObjectRenderer renderer = new ManchesterSyntaxObjectRenderer(
				new TextBlockWriter( sw ) );
		obj.accept( renderer );
		String actual = sw.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void classAssertion() throws Exception {
		assertRendering( 
			"a type A", 
			OWL.classAssertion( a, A ) );
	}

	@Test
	public void subClassOf() throws Exception {
		assertRendering( 
			"A subClassOf B", 
			OWL.subClassOf( A, B ) );
	}
	
	@Test
	public void subPropertyOf() throws Exception {
		OWLObjectPropertyExpression[] a = { p, q };
		assertRendering( 
			"p o q subPropertyOf p", 
			OWL.subPropertyOf( a, p ) );
	}
	
	@Test
	public void qualifiedExactCardinality() throws Exception {
		assertRendering( 
			"p exactly 1 A", 
			OWL.exactly( p, 1, A ) );
	}
	
	@Test
	public void exactCardinality() throws Exception {
		assertRendering( 
			"p exactly 1", 
			OWL.exactly( p, 1 ) );
	}
	
	
	@Test
	public void someValuesFrom() throws Exception {
		assertRendering( 
			"p some A", 
			OWL.some( p, A ) );
	}
	
	
	@Test
	public void allValuesFrom() throws Exception {
		assertRendering( 
			"p only A", 
			OWL.all( p, A ) );
	}
	
	@Test
	public void maxExclusive() throws Exception {
		assertRendering( 
			"double[< \"2.0\"^^double]", 
			OWL.restrict( XSD.DOUBLE, OWL.maxExclusive( 2.0 ) ) );
	}
	
	@Test
	public void minExclusive() throws Exception {
		assertRendering( 
			"double[> \"2.0\"^^double]", 
			OWL.restrict( XSD.DOUBLE, OWL.minExclusive( 2.0 ) ) );
	}
	
	@Test
	public void maxInclusive() throws Exception {
		assertRendering( 
			"double[<= \"2.0\"^^double]", 
			OWL.restrict( XSD.DOUBLE, OWL.maxInclusive( 2.0 ) ) );
	}
	
	@Test
	public void minInclusive() throws Exception {
		assertRendering( 
			"double[>= \"2.0\"^^double]", 
			OWL.restrict( XSD.DOUBLE, OWL.minInclusive( 2.0 ) ) );
	}
}
