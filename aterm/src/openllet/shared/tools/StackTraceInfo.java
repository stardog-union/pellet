package openllet.shared.tools;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Utility class: Getting the name of the current executing method
 * http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
 * 
 * Provides:
 * 
 * getCurrentClassName()
 * getCurrentMethodName()
 * getCurrentFileName()
 * 
 * getInvokingClassName()
 * getInvokingMethodName()
 * getInvokingFileName()
 *
 * Nb. Using StackTrace's to get this info is expensive. There are more optimised ways to obtain
 * method names. See other stackoverflow posts eg. http://stackoverflow.com/questions/421280/in-java-how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection/2924426#2924426
 *
 * 29/09/2012 (lem) - added methods to return (1) fully qualified names and (2) invoking class/method names
 */
public class StackTraceInfo
{
	private static final Logger _logger = Log.getLogger(StackTraceInfo.class);

	/** (Lifted from virgo47's stackoverflow answer) */
	private static final int CLIENT_CODE_STACK_INDEX;

	/**
	 * Accesses the native method getStackTraceElement(int depth) directly.
	 * And stores the accessible Method in a static variable.
	 */
	private static Method _m;

	static
	{
		// Finds out the index of "this code" in the returned stack trace - funny but it differs in JDK 1.5 and 1.6
		int i = 0;

		for (final StackTraceElement ste : Thread.currentThread().getStackTrace())
		{
			i++;
			if (ste.getClassName().equals(StackTraceInfo.class.getName()))
				break;
		}
		CLIENT_CODE_STACK_INDEX = i;

		try
		{

			_m = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			_m.setAccessible(true);
		}
		catch (final NoSuchMethodException e)
		{
			Log.error(_logger, "getCurrentMethodName() will not be available.", e);
		}
	}

	public static String getCurrentMethodName()
	{
		// making additional overloaded method call requires +1 offset
		return (_m != null) ? getCurrentMethodName_Main(1) : getCurrentMethodName_Backup(1);
	}

	public static String getCurrentMethodName_Main(final int depth)
	{
		try
		{
			final StackTraceElement element = (StackTraceElement) _m.invoke(new Throwable(), depth + 1);
			return element.getMethodName();
		}
		catch (final Exception e) // 3 exceptions. 
		{
			Log.error(_logger, e);
			return "";
		}
	}

	private static String getCurrentMethodName_Backup(final int offset)
	{
		return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + offset].getMethodName();
	}

	public static String getCurrentClassName()
	{
		return getCurrentClassName(1);      // making additional overloaded method call requires +1 offset
	}

	private static String getCurrentClassName(final int offset)
	{
		// We should do the same trick as in getCurrentMethodeName, but getClass.getSimpleName() do the trick almost every time.
		return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + offset].getClassName();
	}

	public static String getCurrentFileName()
	{
		return getCurrentFileName(1);     // making additional overloaded method call requires +1 offset
	}

	private static String getCurrentFileName(final int offset)
	{
		final String filename = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + offset].getFileName();
		final int lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + offset].getLineNumber();

		return filename + ":" + lineNumber;
	}

	public static String getInvokingMethodName()
	{
		return getInvokingMethodName(2);
	}

	/**
	 * re-uses getCurrentMethodName() with desired index
	 * 
	 * @return same as getCurrentMethodName() but shorten as the given offset
	 */
	private static String getInvokingMethodName(final int offset)
	{
		return (_m != null) ? getCurrentMethodName_Main(offset + 1) : getCurrentMethodName_Backup(offset + 1);
	}

	public static String getInvokingClassName()
	{
		return getInvokingClassName(2);
	}

	private static String getInvokingClassName(final int offset)
	{
		return getCurrentClassName(offset + 1);     // re-uses getCurrentClassName() with desired index
	}

	public static String getInvokingFileName()
	{
		return getInvokingFileName(2);
	}

	private static String getInvokingFileName(final int offset)
	{
		return getCurrentFileName(offset + 1);     // re-uses getCurrentFileName() with desired index
	}

	public static String getCurrentMethodNameFqn()
	{
		return getCurrentMethodNameFqn(1);
	}

	private static String getCurrentMethodNameFqn(final int offset)
	{
		final String currentClassName = getCurrentClassName(offset + 1);
		final String currentMethodName = (_m != null) ? getCurrentMethodName_Main(offset + 1) : getCurrentMethodName_Backup(offset + 1);

		return currentClassName + "." + currentMethodName;
	}

	public static String getCurrentFileNameFqn()
	{
		final String CurrentMethodNameFqn = getCurrentMethodNameFqn(1);
		final String currentFileName = getCurrentFileName(1);

		return CurrentMethodNameFqn + "(" + currentFileName + ")";
	}

	public static String getInvokingMethodNameFqn()
	{
		return getInvokingMethodNameFqn(2);
	}

	private static String getInvokingMethodNameFqn(final int offset)
	{
		final String invokingClassName = getInvokingClassName(offset + 1);
		final String invokingMethodName = getInvokingMethodName(offset + 1);

		return invokingClassName + "." + invokingMethodName;
	}

	public static String getInvokingFileNameFqn()
	{
		final String invokingMethodNameFqn = getInvokingMethodNameFqn(2);
		final String invokingFileName = getInvokingFileName(2);

		return invokingMethodNameFqn + "(" + invokingFileName + ")";
	}
}
