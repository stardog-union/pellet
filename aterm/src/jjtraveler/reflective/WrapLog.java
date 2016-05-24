package jjtraveler.reflective;

import jjtraveler.LogVisitor;
import jjtraveler.Logger;
import jjtraveler.Visitable;
import jjtraveler.Visitor;

/**
 * Wrap a LogVisitor around a visitor.
 * <p>
 * Test case documentation: <a href="WrapLogTest.java">WrapLogTest</a>
 */

public class WrapLog<T extends Visitable> extends VisitorVisitor<T>
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public VisitableLogVisitor visitVisitor(T v)
	{
		return new VisitableLogVisitor(v, logger);
	}

	class VisitableLogVisitor<Tx extends Visitable> extends LogVisitor<Tx> implements VisitableVisitor<Tx>
	{
		@Override
		public int getChildCount()
		{
			return 1;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public Tx getChildAt(int i)
		{
			switch (i)
			{
				case 0:
					return (Tx) visitor; // Becasue Visitor is also a Visiatable here.
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Tx setChildAt(final int i, final Visitable child)
		{
			switch (i)
			{
				case 0:
					visitor = (VisitableVisitor) child;
					return (Tx) this; // Because I am a Visitable.
				default:
					throw new IndexOutOfBoundsException();
			}
		}

		@SuppressWarnings("unchecked")
		public VisitableLogVisitor(final VisitableVisitor<T> visitor, final Logger logger)
		{
			super((Visitor<Tx>) visitor, logger);
		}

		@SuppressWarnings("unchecked")
		public VisitableLogVisitor(final T visitable, final Logger logger)
		{
			super((Visitor<Tx>) visitable, logger);
		}
	}
}
