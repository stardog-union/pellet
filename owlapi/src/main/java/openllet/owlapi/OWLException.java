package openllet.owlapi;

/**
 * throw a exception for OWL related problems.
 * 
 * @since 2.5.1
 */
public class OWLException extends RuntimeException
{
	private static final long serialVersionUID = 922674511735669343L;

	public OWLException()
	{
		super();
	}

	public OWLException(final String message)
	{
		super(message);
	}

	public OWLException(final Throwable cause)
	{
		super(cause);
	}

	public OWLException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
