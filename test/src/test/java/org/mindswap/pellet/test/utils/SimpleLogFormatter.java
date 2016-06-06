package org.mindswap.pellet.test.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleLogFormatter extends Formatter
{
	private final static DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss.SSS");

	@Override
	public String format(final LogRecord record)
	{
		final String stack = getStackTrace(record.getThrown());

		return "[" + record.getLevel() + " "
				//				+ className(record.getSourceClassName()) + "."
				//				+ record.getSourceMethodName() + " - "
				+ TIME_FORMAT.format(record.getMillis()) + "] " + record.getMessage() + "\n" + stack;
	}

	private String getStackTrace(final Throwable t)
	{
		if (t == null)
			return "";

		final StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	@SuppressWarnings("unused")
	private String className(final String s)
	{
		return s.substring(s.lastIndexOf(".") + 1);
	}
}
