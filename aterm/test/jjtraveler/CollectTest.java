package jjtraveler;

import java.util.Collection;
import java.util.Vector;
import jjtraveler.util.VisitorTestCase;

public class CollectTest extends VisitorTestCase
{

	public CollectTest(final String test)
	{
		super(test);
	}

	public void testCollectTopDown()
	{
		final Collection<Node> c = new Vector<>();
		(Collect.topdown(new Identity<>(), c)).visit(n0);
		final Collection<Node> expected = new Vector<>();
		expected.add(n0);
		expected.add(n1);
		expected.add(n11);
		expected.add(n12);
		expected.add(n2);
		assertEquals(expected, c);
	}

	public void testCollectAll()
	{
		final Collection<Node> c = new Vector<>();
		(Collect.all(new Identity<>(), c)).visit(n0);
		final Collection<Node> expected = new Vector<>();
		expected.add(n1);
		expected.add(n2);
		assertEquals(expected, c);
	}

}
