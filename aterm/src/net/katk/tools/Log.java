package net.katk.tools;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.Marker;

/**
 * Define logger and configure _log level.
 */
public class Log
{
	public static volatile Level _defaultLevel = Level.INFO;

	private static List<Logger> _loggers = new Vector<>();

	public static Logger getLogger(final String name)
	{
		final Logger logger = Logger.getLogger(name);
		_loggers.add(logger);
		logger.setLevel(_defaultLevel);
		return logger;
	}

	public static Logger getLogger(final Class<?> type)
	{
		final Logger logger = Logger.getLogger(type.getSimpleName());
		_loggers.add(logger);
		logger.setLevel(_defaultLevel);
		return logger;
	}

	public static Logger getLogger(final String name, final Level specificLevel)
	{
		final Logger logger = Logger.getLogger(name);
		_loggers.add(logger);
		logger.setLevel(specificLevel);
		return logger;
	}

	public static Logger getLogger(final Class<?> type, final Level specificLevel)
	{
		final Logger logger = Logger.getLogger(type.getSimpleName());
		_loggers.add(logger);
		logger.setLevel(specificLevel);
		return logger;
	}

	public static void setLevel(final Logger logger, final Level level)
	{
		logger.setLevel(level);
	}

	public static void setLevel(final Level level, final String filter)
	{
		for (final Logger logger : _loggers)
			if (logger.getName().contains(filter))
				logger.setLevel(level);
	}

	public static void setLevel(final Level level)
	{
		for (final Logger logger : _loggers)
			logger.setLevel(level);
	}

	public static void setLevel(final Level level, final Class<?> type)
	{
		setLevel(level, type.getSimpleName());
	}

	public static void error(final Logger logger, final Throwable e)
	{
		logger.log(Level.SEVERE, "", e);
	}

	public static void error(final Logger logger, final String msg, final Throwable e)
	{
		logger.log(Level.SEVERE, msg, e);
	}

	public static org.slf4j.Logger toSlf4j(final Logger logger)
	{
		return new org.slf4j.Logger()
		{

			@Override
			public String getName()
			{
				return logger.getName();
			}

			@Override
			public boolean isTraceEnabled()
			{
				return logger.isLoggable(Level.FINEST);
			}

			@Override
			public void trace(final String msg)
			{
				logger.finest(msg);
			}

			@Override
			public void trace(final String format, final Object arg)
			{
				logger.log(Level.FINEST, format, arg);
			}

			@Override
			public void trace(final String format, final Object arg1, final Object arg2)
			{
				final Object[] t = { arg1, arg2 };
				logger.log(Level.FINEST, format, t);
			}

			@Override
			public void trace(final String format, final Object... arguments)
			{
				logger.log(Level.FINEST, format, arguments);
			}

			@Override
			public void trace(final String msg, final Throwable t)
			{
				logger.log(Level.FINEST, msg, t);
			}

			@Override
			public boolean isTraceEnabled(final Marker marker)
			{
				return isTraceEnabled();
			}

			@Override
			public void trace(final Marker marker, final String msg)
			{
				trace(msg);
			}

			@Override
			public void trace(final Marker marker, final String format, final Object arg)
			{
				trace(format, arg);
			}

			@Override
			public void trace(final Marker marker, final String format, final Object arg1, final Object arg2)
			{
				trace(format, arg1, arg2);
			}

			@Override
			public void trace(final Marker marker, final String format, final Object... argArray)
			{
				trace(format, argArray);
			}

			@Override
			public void trace(final Marker marker, final String msg, final Throwable t)
			{
				trace(msg, t);
			}

			@Override
			public boolean isDebugEnabled()
			{
				return logger.isLoggable(Level.FINE);
			}

			@Override
			public void debug(final String msg)
			{
				logger.finest(msg);
			}

			@Override
			public void debug(final String format, final Object arg)
			{
				logger.log(Level.FINE, format, arg);
			}

			@Override
			public void debug(final String format, final Object arg1, final Object arg2)
			{
				final Object[] t = { arg1, arg2 };
				logger.log(Level.FINE, format, t);
			}

			@Override
			public void debug(final String format, final Object... arguments)
			{
				logger.log(Level.FINE, format, arguments);
			}

			@Override
			public void debug(final String msg, final Throwable t)
			{
				logger.log(Level.FINE, msg, t);
			}

			@Override
			public boolean isDebugEnabled(final Marker marker)
			{
				return isDebugEnabled();
			}

			@Override
			public void debug(final Marker marker, final String msg)
			{
				debug(msg);
			}

			@Override
			public void debug(final Marker marker, final String format, final Object arg)
			{
				debug(format, arg);
			}

			@Override
			public void debug(final Marker marker, final String format, final Object arg1, final Object arg2)
			{
				debug(format, arg1, arg2);
			}

			@Override
			public void debug(final Marker marker, final String format, final Object... argArray)
			{
				debug(format, argArray);
			}

			@Override
			public void debug(final Marker marker, final String msg, final Throwable t)
			{
				debug(msg, t);
			}

			@Override
			public boolean isInfoEnabled()
			{
				return logger.isLoggable(Level.INFO);
			}

			@Override
			public void info(final String msg)
			{
				logger.finest(msg);
			}

			@Override
			public void info(final String format, final Object arg)
			{
				logger.log(Level.INFO, format, arg);
			}

			@Override
			public void info(final String format, final Object arg1, final Object arg2)
			{
				final Object[] t = { arg1, arg2 };
				logger.log(Level.INFO, format, t);
			}

			@Override
			public void info(final String format, final Object... arguments)
			{
				logger.log(Level.INFO, format, arguments);
			}

			@Override
			public void info(final String msg, final Throwable t)
			{
				logger.log(Level.INFO, msg, t);
			}

			@Override
			public boolean isInfoEnabled(final Marker marker)
			{
				return isInfoEnabled();
			}

			@Override
			public void info(final Marker marker, final String msg)
			{
				info(msg);
			}

			@Override
			public void info(final Marker marker, final String format, final Object arg)
			{
				info(format, arg);
			}

			@Override
			public void info(final Marker marker, final String format, final Object arg1, final Object arg2)
			{
				info(format, arg1, arg2);
			}

			@Override
			public void info(final Marker marker, final String format, final Object... argArray)
			{
				info(format, argArray);
			}

			@Override
			public void info(final Marker marker, final String msg, final Throwable t)
			{
				info(msg, t);
			}

			@Override
			public boolean isWarnEnabled()
			{
				return logger.isLoggable(Level.WARNING);
			}

			@Override
			public void warn(final String msg)
			{
				logger.finest(msg);
			}

			@Override
			public void warn(final String format, final Object arg)
			{
				logger.log(Level.WARNING, format, arg);
			}

			@Override
			public void warn(final String format, final Object arg1, final Object arg2)
			{
				final Object[] t = { arg1, arg2 };
				logger.log(Level.WARNING, format, t);
			}

			@Override
			public void warn(final String format, final Object... arguments)
			{
				logger.log(Level.WARNING, format, arguments);
			}

			@Override
			public void warn(final String msg, final Throwable t)
			{
				logger.log(Level.WARNING, msg, t);
			}

			@Override
			public boolean isWarnEnabled(final Marker marker)
			{
				return isWarnEnabled();
			}

			@Override
			public void warn(final Marker marker, final String msg)
			{
				warn(msg);
			}

			@Override
			public void warn(final Marker marker, final String format, final Object arg)
			{
				warn(format, arg);
			}

			@Override
			public void warn(final Marker marker, final String format, final Object arg1, final Object arg2)
			{
				warn(format, arg1, arg2);
			}

			@Override
			public void warn(final Marker marker, final String format, final Object... argArray)
			{
				warn(format, argArray);
			}

			@Override
			public void warn(final Marker marker, final String msg, final Throwable t)
			{
				warn(msg, t);
			}

			@Override
			public boolean isErrorEnabled()
			{
				return logger.isLoggable(Level.SEVERE);
			}

			@Override
			public void error(final String msg)
			{
				logger.finest(msg);
			}

			@Override
			public void error(final String format, final Object arg)
			{
				logger.log(Level.SEVERE, format, arg);
			}

			@Override
			public void error(final String format, final Object arg1, final Object arg2)
			{
				final Object[] t = { arg1, arg2 };
				logger.log(Level.SEVERE, format, t);
			}

			@Override
			public void error(final String format, final Object... arguments)
			{
				logger.log(Level.FINEST, format, arguments);
			}

			@Override
			public void error(final String msg, final Throwable t)
			{
				logger.log(Level.SEVERE, msg, t);
			}

			@Override
			public boolean isErrorEnabled(final Marker marker)
			{
				return isErrorEnabled();
			}

			@Override
			public void error(final Marker marker, final String msg)
			{
				error(msg);
			}

			@Override
			public void error(final Marker marker, final String format, final Object arg)
			{
				error(format, arg);
			}

			@Override
			public void error(final Marker marker, final String format, final Object arg1, final Object arg2)
			{
				error(format, arg1, arg2);
			}

			@Override
			public void error(final Marker marker, final String format, final Object... argArray)
			{
				error(format, argArray);
			}

			@Override
			public void error(final Marker marker, final String msg, final Throwable t)
			{
				error(msg, t);
			}
		};
	}
}
