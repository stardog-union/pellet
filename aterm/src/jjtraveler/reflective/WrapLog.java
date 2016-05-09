package jjtraveler.reflective;

import jjtraveler.Visitable;
import jjtraveler.LogVisitor;
import jjtraveler.Logger;

/**
 * Wrap a LogVisitor around a visitor.
 * <p>
 * Test case documentation: <a href="WrapLogTest.java">WrapLogTest</a>
 */

public class WrapLog extends VisitorVisitor
{

	Logger logger;

	/**
	 * Wrap a LogVisitor which uses the given logger, around a
	 * visitable visitor.
	 */
	public WrapLog(Logger logger)
	{
		this.logger = logger;
	}

	public VisitableVisitor visitVisitor(VisitableVisitor v)
	{
		return new VisitableLogVisitor(v, logger);
	}

	class VisitableLogVisitor extends LogVisitor implements VisitableVisitor
	{
		public int getChildCount()
		{
			return 1;
		}

		public Visitable getChildAt(int i)
		{
			switch (i)
			{
				case 0:
					return (VisitableVisitor) visitor;
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		public Visitable setChildAt(int i, Visitable child)
		{
			switch (i)
			{
				case 0:
					visitor = (VisitableVisitor) child;
					return this;
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		public VisitableLogVisitor(VisitableVisitor visitor, Logger logger)
		{
			super(visitor, logger);
		}
	}
}
