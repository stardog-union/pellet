package jjtraveler.graph;

import jjtraveler.util.VisitorTestCase;

import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import junit.framework.Test;
import junit.framework.TestSuite;

public class IsDagTest extends VisitorTestCase
{

	public void testTree() throws VisitFailure
	{
		Visitable node = (new IsDag()).visit(n0);
		assertEquals(n0, node);
	}

	public void testDiamond() throws VisitFailure
	{
		Visitable node = null;
		node = (new IsDag()).visit(rootOfDiamond);
		assertEquals(rootOfDiamond, node);
	}

	public void testCircle()
	{
		Visitable node = null;
		try
		{
			node = (new IsDag()).visit(rootOfCircle);
			fail("VisitFailure should have occured");
		}
		catch (VisitFailure vf)
		{
			assertNull(node);
		}
	}

	public IsDagTest(String test)
	{
		super(test);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(jjtraveler.util.UtilTest.class);
		return suite;
	}

}
