// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;

import aterm.ATermAppl;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.output.ATermManchesterSyntaxRenderer;
import org.mindswap.pellet.output.ATermRenderer;

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
public class TestATermManchesterSyntaxRenderer
{

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(TestATermManchesterSyntaxRenderer.class);
	}

	@Test
	public void testAll()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl p = term("p");

		r.visit(all(p, C));

		final String expected = "(p only C)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testAnd()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		r.visit(and(C, D, E));

		final String expected = "(C and D and E)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testHasValue()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl l = literal("foobar");
		final ATermAppl p = term("p");

		r.visit(hasValue(p, l));

		final String expected = "(p value \"foobar\")";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testInverse()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl p = term("p");

		r.visit(inv(p));

		final String expected = "inverse p";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testMax()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl p = term("p");

		r.visit(max(p, 3, C));

		final String expected = "(p max 3 C)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testMin()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl p = term("p");

		r.visit(min(p, 3, C));

		final String expected = "(p min 3 C)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testNot()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");

		r.visit(not(C));

		final String expected = "not C";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testOneOf()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		r.visit(oneOf(C, D, E));

		// oneOf inserts new list elements on top of the list
		final String expected = "{E D C}";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testOr()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		r.visit(or(C, D, E));

		final String expected = "(C or D or E)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testSelf()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl p = term("p");

		r.visit(self(p));

		final String expected = "(p Self)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testSome()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl p = term("p");

		r.visit(some(p, C));

		final String expected = "(p some C)";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void test1()
	{
		final StringWriter s = new StringWriter();
		final ATermRenderer r = new ATermManchesterSyntaxRenderer();
		r.setWriter(new PrintWriter(s));

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl p = term("p");

		r.visit(and(C, some(p, D)));

		final String expected = "(C and (p some D))";
		final String actual = s.toString();

		assertEquals(expected, actual);
	}
}
