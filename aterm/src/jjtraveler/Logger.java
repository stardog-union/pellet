package jjtraveler;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Logs events and allows their trace to be inspected.
 */

public class Logger
{

	Vector trace = new Vector();

	public Logger()
	{
	}

	/**
	 * Create a new Logger which has as initialt trace a single visit of a
	 * particular node.
	 */
	public Logger(final Visitor v, final Visitable n)
	{
		log(new Event(v, n));
	}

	/**
	 * Create a new Logger, which has as initial trace a sequence of visiting
	 * events where the visitor <code>v</code> visits each of the <code>nodes</code>
	 */
	public Logger(final Visitor v, final Visitable[] nodes)
	{
		for (int i = 0; i < nodes.length; i++)
			log(new Event(v, nodes[i]));
	}

	/**
	 * Log a single event.
	 */
	public void log(final Event e)
	{
		trace.add(e);
	}

	/**
	 * Produces a string representation of the trace of events that have been
	 * logged so far.
	 */
	@Override
	public String toString()
	{
		String result = "";
		for (final Enumeration e = trace.elements(); e.hasMoreElements();)
			result += e.nextElement().toString() + "\n";
		return result;
	}

	/**
	 * Loggers are equal if and only of their traces are.
	 */
	@Override
	public boolean equals(final Object o)
	{
		if (o instanceof Logger)
			return ((Logger) o).toString().equals(toString());
		return false;
	}

	/**
	 * Hashcode must be redefined if equality is redefined.
	 */
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * Compute the elapsed time (in milliseconds) between the first and last
	 * event on the logger's trace.
	 */
	public long getElapsedTime()
	{
		final long startTime = ((Event) trace.firstElement()).getTimeStamp();
		final long endTime = ((Event) trace.lastElement()).getTimeStamp();
		return endTime - startTime;
	}

}
