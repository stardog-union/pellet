package jjtraveler.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UtilTest extends VisitorTestCase
{

	public UtilTest(String test)
	{
		super(test);
	}

	public void testToStringVisitor()
	{
		assertEquals(n0.toString(), ToStringVisitor.doToString(n0));
	}

	public void testToATermString()
	{
		assertEquals("Node(Node(Node,Node),Node)", ToATermString.doToString(n0));
	}

	public void testToXMLString()
	{
		assertEquals("<Node><Node><Node/> <Node/></Node> <Node/></Node>", ToXMLString.doToString(n0));
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(jjtraveler.util.UtilTest.class);
		return suite;
	}

}
