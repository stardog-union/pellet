// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.output.ATermManchesterSyntaxRenderer;
import org.mindswap.pellet.output.ATermRenderer;

import aterm.ATermAppl;

import static org.junit.Assert.assertEquals;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static com.clarkparsia.pellet.utils.TermFactory.some;

/**
 * <p>
 * Title: TestNodeFormatter
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class TestATermManchesterSyntaxRenderer {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TestATermManchesterSyntaxRenderer.class );
	}
	
	@Test
	public void testAll() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );

		r.visit( all( p, C ) );

		String expected = "(p only C)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testAnd() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		r.visit( and( C, D, E ) );

		String expected = "(C and D and E)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testHasValue() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl l = literal( "foobar" );
		ATermAppl p = term( "p" );

		r.visit( hasValue( p, l ) );

		String expected = "(p value \"foobar\")";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testInverse() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl p = term( "p" );

		r.visit( inv( p ) );

		String expected = "inverse p";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testMax() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );

		r.visit( max( p, 3, C ) );

		String expected = "(p max 3 C)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testMin() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );

		r.visit( min( p, 3, C ) );

		String expected = "(p min 3 C)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testNot() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );

		r.visit( not( C ) );

		String expected = "not C";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testOneOf() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		r.visit( oneOf( C, D, E ) );

		// oneOf inserts new list elements on top of the list
		String expected = "{E D C}";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testOr() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		r.visit( or( C, D, E ) );

		String expected = "(C or D or E)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testSelf() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl p = term( "p" );

		r.visit( self( p ) );

		String expected = "(p Self)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void testSome() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl p = term( "p" );

		r.visit( some( p, C ) );

		String expected = "(p some C)";
		String actual = s.toString();

		assertEquals( expected, actual );
	}

	@Test
	public void test1() {
		StringWriter s = new StringWriter();
		ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter( new PrintWriter( s ) );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl p = term( "p" );

		r.visit( and( C, some( p, D ) ));

		String expected = "(C and (p some D))";
		String actual = s.toString();

		assertEquals( expected, actual );
	}
}
