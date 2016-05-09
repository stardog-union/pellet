package jjtraveler;

import java.util.Collection;
import java.util.HashSet;
import junit.framework.TestCase;

public class LibraryTest extends TestCase
{

	Node n0; // 4

	Node n1; // / \

	Node n2; // 3 2

	Node n3; // / \

	Node n4; // 0 1

	Logger logger;

	public LibraryTest(final String test)
	{
		super(test);
	}

	@Override
	protected void setUp()
	{
		Node.reset();
		final Node[] empty = {};
		logger = new Logger();
		n0 = Node.factory(empty);
		n1 = Node.factory(empty);
		n2 = Node.factory(empty);
		n3 = Node.factory(new Node[] { n0, n1 });
		n4 = Node.factory(new Node[] { n3, n2 });
	}

	public void testSequence() throws VisitFailure
	{
		final Identity id1 = new Identity();
		final Identity id2 = new Identity();

		final Logger expected = new Logger();
		expected.log(new Event(id1, n0));
		expected.log(new Event(id2, n0));

		final Sequence ls = new Sequence(logVisitor(id1), logVisitor(id2));

		final Visitable nodeReturned = ls.visit(n0);

		assertEquals(expected, logger);
		assertEquals(nodeReturned, n0);
	}

	public void testLeftChoice() throws VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n0 });

		final Choice ch = new Choice(logVisitor(id), new Identity());

		final Visitable nodeReturned = ch.visit(n0);
		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

	public void testRightChoice() throws VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n0 });

		final Choice ch = new Choice(new Fail(), logVisitor(id));

		final Visitable nodeReturned = ch.visit(n0);
		assertEquals(expected, logger);
		assertEquals(n0, nodeReturned);
	}

	public void testAll() throws jjtraveler.VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n3, n2 });

		final All all = new All(logVisitor(id));

		final Visitable nodeReturned = all.visit(n4);
		assertEquals(expected, logger);
		assertEquals(n4, nodeReturned);
	}

	public void testBottomUp() throws jjtraveler.VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n0, n1, n3, n2, n4 });

		final BottomUp visitor = new BottomUp(logVisitor(id));

		final Visitable nodeReturned = visitor.visit(n4);
		assertEquals(expected, logger);
		assertEquals(n4, nodeReturned);
	}

	public void testTopDown() throws jjtraveler.VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n4, n3, n0, n1, n2 });

		final Visitor visitor = new TopDown(logVisitor(id));

		final Visitable nodeReturned = visitor.visit(n4);
		assertEquals(expected, logger);
		assertEquals(n4, nodeReturned);
	}

	public void testDownUp() throws jjtraveler.VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n4, n3, n0, n0, n1, n1, n3, n2, n2, n4 });

		final Visitor visitor = new DownUp(logVisitor(id), logVisitor(id));

		final Visitable nodeReturned = visitor.visit(n4);
		assertEquals(expected, logger);
		assertEquals(n4, nodeReturned);
	}

	public void testNonStopDownUp() throws jjtraveler.VisitFailure
	{
		final Identity downId = new Identity();
		final Identity upId = new Identity();
		final Fail stop = new Fail();

		final Logger expected = new Logger();
		expected.log(new Event(downId, n3));
		expected.log(new Event(downId, n0));
		expected.log(new Event(upId, n0));
		expected.log(new Event(downId, n1));
		expected.log(new Event(upId, n1));
		expected.log(new Event(upId, n3));

		final Visitor visitor = new DownUp(logVisitor(downId), stop, logVisitor(upId));

		final Visitable nodeReturned = visitor.visit(n3);
		assertEquals(expected, logger);
		assertEquals(n3, nodeReturned);
	}

	public void testStopDownUp() throws jjtraveler.VisitFailure
	{
		final Identity downId = new Identity();
		final Identity upId = new Identity();
		final Identity stopId = new Identity();

		final Logger expected = new Logger();
		expected.log(new Event(downId, n4));
		expected.log(new Event(stopId, n4));
		expected.log(new Event(upId, n4));

		final Visitor visitor = new DownUp(logVisitor(downId), logVisitor(stopId), logVisitor(upId));

		final Visitable nodeReturned = visitor.visit(n4);
		assertEquals(expected, logger);
		assertEquals(n4, nodeReturned);
	}

	public void testDefUse() throws jjtraveler.VisitFailure
	{
		class Def extends Identity implements Collector
		{
			@Override
			public Collection<String> getCollection()
			{
				final HashSet<String> result = new HashSet<>();
				result.add("aap");
				result.add("noot");
				return result;
			}
		}
		class Use extends Identity implements Collector
		{
			@Override
			public Collection<String> getCollection()
			{
				final HashSet<String> result = new HashSet<>();
				result.add("aap");
				result.add("mies");
				return result;
			}
		}

		final Def def = new Def();
		final Use use = new Use();
		final DefUse du = new DefUse(use, def);
		du.visit(n0);
		assertTrue(du.getUnused().contains("noot"));
		assertTrue(du.getUndefined().contains("mies"));
		assertEquals(1, du.getUnused().size());
		assertEquals(1, du.getUndefined().size());
	}

	public void testBacktrack() throws jjtraveler.VisitFailure
	{
		class Increment implements StateVisitor
		{
			Object localState = null;

			public int state = 0;

			@Override
			public Object getState()
			{
				return new Integer(state);
			}

			@Override
			public void setState(final Object o)
			{
				state = ((Integer) o).intValue();
			}

			@Override
			public Visitable visit(final Visitable x)
			{
				state++;
				localState = getState();
				return x;
			}
		}

		final Increment i = new Increment();
		final Object initialState = i.getState();
		(new Backtrack(i)).visit(n0);
		assertNotNull(i.localState);
		assertTrue(!initialState.equals(i.localState));
		assertEquals(initialState, i.getState());
	}

	public LogVisitor logVisitor(final Visitor v)
	{
		return new LogVisitor(v, logger);
	}

	public void testBreadthFirst() throws jjtraveler.VisitFailure
	{
		final Identity id = new Identity();
		final Logger expected = new Logger(id, new Visitable[] { n4, n3, n2, n0, n1 });

		final BreadthFirst bf = new BreadthFirst(logVisitor(id));

		final Visitable resultNode = bf.visit(n4);
		assertEquals(expected, logger);
		assertEquals(resultNode, n4);
	}

	public void testNotOnFailure() throws jjtraveler.VisitFailure
	{
		final Not not = new Not(new Fail());
		final Visitable resultNode = not.visit(n0);
		assertEquals(n0, resultNode);
	}

	public void testNotOnSuccess()
	{
		final Not not = new Not(new Identity());
		Visitable resultNode = null;
		try
		{
			resultNode = not.visit(n0);
			fail("VisitFailure should have occured");
		}
		catch (final VisitFailure f)
		{
			assertNull(resultNode);
		}
	}

}
