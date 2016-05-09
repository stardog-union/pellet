package jjtraveler;

import jjtraveler.util.VisitorTestCase;

import jjtraveler.reflective.VisitableIdentity;
import jjtraveler.reflective.VisitableSequence;
import jjtraveler.reflective.VisitableVisitor;
import jjtraveler.reflective.WrapLog;
import jjtraveler.BottomUp;
import jjtraveler.Event;
import jjtraveler.Logger;
import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.Visitor;
import junit.framework.Test;
import junit.framework.TestSuite;

public class WrapLogTest extends VisitorTestCase
{

	public void testWrapIdentity() throws VisitFailure
	{
		VisitableVisitor i = new VisitableIdentity();
		WrapLog w = new WrapLog(logger);
		Visitor v = w.visitVisitor(i);
		Visitable result = v.visit(n0);
		Logger expectedLogger = new Logger(i, new Visitable[] { n0 });
		assertEquals(expectedLogger, logger);
		assertEquals(n0, result);
	}

	public void testWeaveSequence() throws VisitFailure
	{
		VisitableVisitor i = new VisitableIdentity();
		VisitableVisitor s = new VisitableSequence(i, i);
		WrapLog w = new WrapLog(logger);
		Visitor v = (VisitableVisitor) (new BottomUp(w)).visit(s);
		Visitable result = v.visit(n0);
		Logger expectedLogger = new Logger();
		expectedLogger.log(new Event(s, n0));
		expectedLogger.log(new Event(i, n0));
		expectedLogger.log(new Event(i, n0));
		assertEquals(expectedLogger, logger);
		assertEquals(n0, result);
	}

	public WrapLogTest(String test)
	{
		super(test);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(jjtraveler.util.UtilTest.class);
		return suite;
	}

}
