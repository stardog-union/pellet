package jjtraveler.graph;

import jjtraveler.util.VisitorTestCase;

import jjtraveler.Identity;
import jjtraveler.Logger;
import jjtraveler.VisitFailure;
import jjtraveler.Visitable;
import jjtraveler.Visitor;

public class WhileNotVisitedTest extends VisitorTestCase
{

	public WhileNotVisitedTest(String test)
	{
		super(test);
	}

	public void testCircle() throws VisitFailure
	{
		Visitor id = new Identity();

		new WhileNotVisited(logVisitor(id)).visit(rootOfCircle);

		Visitable bottomOfCircle = rootOfCircle.getChildAt(0);

		Logger expected = new Logger(id, new Visitable[] { rootOfCircle, bottomOfCircle });

		assertEquals(expected, logger);
	}

}
