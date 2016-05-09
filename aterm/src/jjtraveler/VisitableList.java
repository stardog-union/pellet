package jjtraveler;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * An auxiliary class to represent collections that implement the Visitable
 * interface. The element of the collection are assumed to be Visitables as
 * well.
 */

public class VisitableList extends AbstractList<Object> implements Visitable
{

	/**
	 * Create a new list of visitables, backed by the given list. All elements
	 * of this list should be visitables.
	 */
	public VisitableList(final List<Object> visitables)
	{
		this.visitables = visitables;
	}

	/**
	 * Create a new list of visitables, backed by a Vector initialized with the
	 * given collection. All elements of this collection should be visitables.
	 */
	public VisitableList(final Collection<?> visitables)
	{
		this(new Vector<Object>(visitables));
	}

	/**
	 * Create a new list of visitables, backed by a Vector that is initially
	 * empty.
	 */
	public VisitableList()
	{
		this(new Vector<>());
	}

	private final List<Object> visitables;

	@Override
	public Object get(final int i)
	{
		return visitables.get(i);
	}

	@Override
	public int size()
	{
		return visitables.size();
	}

	@Override
	public Object set(final int index, final Object element)
	{
		return visitables.set(index, element);
	}

	@Override
	public void add(final int index, final Object element)
	{
		visitables.add(index, element);
	}

	@Override
	public boolean add(final Object element)
	{
		visitables.add(element);
		return true;
	}

	@Override
	public Object remove(final int index)
	{
		return visitables.remove(index);
	}

	/**
	 * Return the number of visitables in the list.
	 */
	@Override
	public int getChildCount()
	{
		return visitables.size();
	}

	/**
	 * Return the visitable at the given position in the list.
	 */
	@Override
	public Visitable getChildAt(final int i)
	{
		return (Visitable) get(i);
	}

	/**
	 * Update the list at the given position with the given visitable, and
	 * return the resulting list.
	 */
	@Override
	public Visitable setChildAt(final int i, final Visitable visitable)
	{
		set(i, visitable);
		return this;
	}
}
