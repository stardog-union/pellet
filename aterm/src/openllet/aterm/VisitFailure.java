package openllet.aterm;

/**
 * The VisitFailure exception is used to model success and failure of visitor
 * combinators. On failure, the exception is raised. At choice points, the try
 * and catch constructs are used to recover from failed visits.
 */

public class VisitFailure extends RuntimeException
{
	private static final long serialVersionUID = -1190261403667634678L;

	private String _message;

	public VisitFailure()
	{
		super();
		_message = "";
	}

	public VisitFailure(final String msg)
	{
		super();
		_message = msg;
	}

	public void setMessage(final String message)
	{
		this._message = message;
	}

	@Override
	public String toString()
	{
		return _message;
	}

}
