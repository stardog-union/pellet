package jjtraveler;

/**
 * Counter which keeps track of how
 * often its argument visitor succeeds
 * and how often it fails.
 * Can be used for various metrics
 * combinators.
 *
 * @author Arie van Deursen; Jun 30, 2003
 * @version $Id$
 */

public class SuccessCounter implements Visitor
{

	int success = 0;
	int failure = 0;
	Visitor action;

	public SuccessCounter(final Visitor v)
	{
		action = v;
	}

	public int getSuccesses()
	{
		return success;
	}

	public int getFailures()
	{
		return failure;
	}

	@Override
	public Visitable visit(final Visitable x)
	{
		try
		{
			action.visit(x);
			success++;
		}
		catch (final VisitFailure vf)
		{
			failure++;
		}
		return x;
	}
}
