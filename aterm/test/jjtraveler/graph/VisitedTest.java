package jjtraveler.graph;

import jjtraveler.util.VisitorTestCase;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.Visitor;
import junit.framework.Test;
import junit.framework.TestSuite;

public class VisitedTest extends VisitorTestCase
{

	public void testSingleVisit()
	{
		Visitor v = new Visited();
		Visitable result = null;
		try
		{
			result = v.visit(n0);
			fail("Visit failure should have occured");
		}
		catch (VisitFailure vf)
		{
			assertNull(result);
		}
	}

	public void testDoubleVisit() throws VisitFailure
	{
		Visitor v = new Visited();
		Visitable result = null;
		try
		{
			result = v.visit(n0);
			fail("Visit failure should have occured");
		}
		catch (VisitFailure vf)
		{
			result = v.visit(n0);
			assertEquals(result, n0);
		}
	}

	public VisitedTest(String test)
	{
		super(test);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(jjtraveler.util.UtilTest.class);
		return suite;
	}

}
