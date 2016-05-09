package jjtraveler;

/**
 * A class to represent a visting event: the fact that a visitable
 * node is visited by a particular visitor.
 */

public class Event
{

	Visitor visitor;
	Visitable node;
	long timeStamp;

	public Event(final Visitor v, final Visitable n)
	{
		visitor = v;
		node = n;
		timeStamp = System.currentTimeMillis();
	}

	@Override
	public String toString()
	{
		return visitor + ".visit(" + node + ")";
	}

	@Override
	public boolean equals(final Object o)
	{
		boolean result = false;
		if (o instanceof Event)
		{
			final Event e = (Event) o;
			result = toString() == e.toString();
		}
		return result;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * Return the time (in milliseconds) at which the event was generated.
	 */
	public long getTimeStamp()
	{
		return timeStamp;
	}
}
