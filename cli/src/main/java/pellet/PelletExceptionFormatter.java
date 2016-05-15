package pellet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class PelletExceptionFormatter
{

	private boolean _verbose = false;

	public PelletExceptionFormatter()
	{
	}

	/**
	 * Format a user-friendly exception
	 *
	 * @param e
	 */
	public String formatException(final Throwable e)
	{
		Throwable cause = e;
		while (cause.getCause() != null)
			cause = cause.getCause();

		if (!_verbose)
		{
			if (cause instanceof FileNotFoundException)
				return format((FileNotFoundException) cause);
			if (cause instanceof PelletCmdException)
				return format((PelletCmdException) cause);
			return formatGeneric(cause);
		}

		final StringWriter writer = new StringWriter();
		try (PrintWriter pw = new PrintWriter(writer))
		{
			cause.printStackTrace(pw);
		}
		return writer.toString();

	}

	private String format(final FileNotFoundException e)
	{
		return "ERROR: Cannot open " + e.getMessage();
	}

	private String format(final PelletCmdException e)
	{
		return "ERROR: " + e.getMessage();
	}

	/**
	 * Return a generic exception message.
	 *
	 * @param e
	 */
	private String formatGeneric(final Throwable e)
	{
		String msg = e.getMessage();
		if (msg != null)
		{
			final int index = msg.indexOf('\n', 0);
			if (index != -1)
				msg = msg.substring(0, index);
		}

		return msg + "\nUse -v for detail.";
	}

	public void setVerbose(final boolean verbose)
	{
		this._verbose = verbose;
	}

}
