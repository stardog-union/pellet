package jjtraveler.reflective;

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
		suite.addTest(new TestSuite(jjtraveler.WrapLogTest.class));
		return suite;
	}

}
