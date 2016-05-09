package jjtraveler;

/**
 * <code>DownUp(down,up) = Sequence(down,Sequence(All(DownUp(down,up)),up))</code>
 * <p>
 * <code>DownUp(down,stop,up) = Sequence(down,Sequence(Choice(stop,All(DownUp(down,up))),up))</code>
 * <p>
 * Observe that if the stop condition succeeds, the current node still is visited by both the down and the up visitor.
 */

public class DownUp extends Sequence
{

	public DownUp(final Visitor down, final Visitor up)
	{
		super(down, null);
		then = new Sequence(new All(this), up);
	}

	public DownUp(final Visitor down, final Visitor stop, final Visitor up)
	{
		super(down, null);
		then = new Sequence(new Choice(stop, new All(this)), up);
	}

}
