package jjtraveler.graph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAll extends TestCase
{

	public TestAll(String test)
	{
		super(test);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTest(new TestSuite(jjtraveler.graph.VisitedTest.class));
		suite.addTest(new TestSuite(jjtraveler.graph.WhileNotVisitedTest.class));
		suite.addTest(new TestSuite(jjtraveler.graph.IsTreeTest.class));
		suite.addTest(new TestSuite(jjtraveler.graph.IsDagTest.class));
		suite.addTest(new TestSuite(jjtraveler.graph.ToGraphTest.class));
		suite.addTest(new TestSuite(jjtraveler.graph.EdgesGraphTest.class));
		return suite;
	}

}
