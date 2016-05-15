package jjtraveler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import jjtraveler.AllSpinesBottomUpTest;
import jjtraveler.IfThenElseTest;
import jjtraveler.OnceTopDownTest;
import jjtraveler.SomeTest;
import jjtraveler.LoggerTest;

public class TestAll extends TestCase
{

	public TestAll(final String test)
	{
		super(test);
	}

	public static Test suite()
	{
		final TestSuite suite = new TestSuite();
		suite.addTest(new TestSuite(AllTest.class));
		suite.addTest(new TestSuite(AllSpinesBottomUpTest.class));
		suite.addTest(new TestSuite(FailTest.class));
		suite.addTest(new TestSuite(IdentityTest.class));
		suite.addTest(new TestSuite(IfThenElseTest.class));
		suite.addTest(new TestSuite(LibraryTest.class));
		suite.addTest(new TestSuite(NestingDepthTest.class));
		suite.addTest(new TestSuite(OnceTopDownTest.class));
		suite.addTest(new TestSuite(SpineTopDownTest.class));
		suite.addTest(new TestSuite(SpineBottomUpTest.class));
		suite.addTest(new TestSuite(TopDownUntilTest.class));
		suite.addTest(new TestSuite(DoWhileSuccessTest.class));
		suite.addTest(new TestSuite(OneTest.class));
		suite.addTest(new TestSuite(SomeTest.class));
		suite.addTest(new TestSuite(CollectTest.class));
		suite.addTest(new TestSuite(SuccessCounterTest.class));
		suite.addTest(new TestSuite(jjtraveler.FailAtNodesTest.class));
		suite.addTest(new TestSuite(jjtraveler.LoggerTest.class));
		suite.addTest(new TestSuite(jjtraveler.TimeLogVisitorTest.class));
		return suite;
	}

	public static void main(final String argv[])
	{
		junit.textui.TestRunner.run(suite());
	}
}
