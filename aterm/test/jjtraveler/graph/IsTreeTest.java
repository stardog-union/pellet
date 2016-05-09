package jjtraveler.graph;

import jjtraveler.util.VisitorTestCase;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import junit.framework.Test;
import junit.framework.TestSuite;

public class IsTreeTest extends VisitorTestCase
{

	public void testTree() throws VisitFailure
	{
		Visitable expectedNode = (new IsTree()).visit(n0);
		assertEquals(expectedNode, n0);
	}

	public void testDiamond()
	{
		Visitable expectedNode = null;
		try
		{
			expectedNode = (new IsTree()).visit(rootOfDiamond);
			fail("VisitFailure should have occured");
		}
		catch (VisitFailure vf)
		{
			assertNull(expectedNode);
		}
	}

	public void testCircle()
	{
		Visitable expectedNode = null;
		try
		{
			expectedNode = (new IsTree()).visit(rootOfCircle);
			fail("VisitFailure should have occured");
		}
		catch (VisitFailure vf)
		{
			assertNull(expectedNode);
		}
	}

	public IsTreeTest(String test)
	{
		super(test);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(jjtraveler.util.UtilTest.class);
		return suite;
	}

}
