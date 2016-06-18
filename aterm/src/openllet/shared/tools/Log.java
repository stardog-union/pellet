package openllet.shared.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.Marker;

/**
 * Define logger and configure _log level.
 */
public class Log implements Logging
{
	public static final Logger _parent = Logger.getLogger(Log.class.getName());
	public static volatile Level _defaultLevel = Level.INFO;
	public static volatile boolean _setDefaultParent = false;

	static
	{
		final String property = System.getProperty("java.util.logging.SimpleFormatter.format");
		if (null == property)
			System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
		_parent.setLevel(_defaultLevel);
	}

	@Override
	public Logger getLogger()
	{
		return _parent;
	}

	private static Map<String, Logger> _loggers = new ConcurrentHashMap<>();

	/**
	 * Clear any reference on logger and on referring classes.
	 */
	public void reset()
	{
		_loggers.clear();
	}

	private static Logger config(final Logger logger, final Level level)
	{
		if (_setDefaultParent)
			logger.setParent(_parent);
		_loggers.put(logger.getName(), logger);
		logger.setLevel(level);
		return logger;
	}

	public static Logger getLogger(final String name)
	{
		return config(Logger.getLogger(name), _defaultLevel);
	}

	public static Logger getLogger(final Class<?> type)
	{
		return config(Logger.getLogger(type.getSimpleName()), _defaultLevel);
	}

	public static Logger getLogger(final String name, final Level specificLevel)
	{
		return config(Logger.getLogger(name), specificLevel);
	}

	public static Logger getLogger(final Class<?> type, final Level specificLevel)
	{
		return config(Logger.getLogger(type.getSimpleName()), specificLevel);
	}

	public static org.slf4j.Logger logger(final String name)
	{
		return toSlf4j(config(Logger.getLogger(name), _defaultLevel));
	}

	public static org.slf4j.Logger logger(final Class<?> type)
	{
		return toSlf4j(config(Logger.getLogger(type.getSimpleName()), _defaultLevel));
	}

	public static org.slf4j.Logger logger(final String name, final Level specificLevel)
	{
		return toSlf4j(config(Logger.getLogger(name), specificLevel));
	}

	public static org.slf4j.Logger logger(final Class<?> type, final Level specificLevel)
	{
		return toSlf4j(config(Logger.getLogger(type.getSimpleName()), specificLevel));
	}

	public static void setLevel(final Logger logger, final Level level)
	{
		logger.setLevel(level);
	}

	/**
	 * Change the level of logging only on the logger that match the giver filter (contains)
	 * 
	 * @param level of logging that will be set.
	 * @param filter that must be contains in the logger name.
	 */
	public static void setLevel(final Level level, final String filter)
	{
		_loggers.values().parallelStream().filter(l -> l.getName().contains(filter)).forEach(l -> l.setLevel(level));
	}

	public static void setLevel(final Level level)
	{
		_loggers.values().parallelStream().forEach(l -> l.setLevel(level));
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
