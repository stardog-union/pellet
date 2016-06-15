// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.progress;

import java.io.PrintStream;
import org.mindswap.pellet.utils.DurationFormat;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class ConsoleProgressMonitor extends AbstractProgressMonitor
{
	private final PrintStream _out;

	private volatile int _echo = 0;

	@Override
	public int getLastEcho()
	{
		return _echo;
	}

	public ConsoleProgressMonitor()
	{
		this(System.err, 0);
	}

	public ConsoleProgressMonitor(final PrintStream out)
	{
		this(out, 0);
	}

	public ConsoleProgressMonitor(final int length)
	{
		this(System.err, length);
	}

	public ConsoleProgressMonitor(final PrintStream out, final int length)
	{
		_out = out;

		setProgressLength(length);
		setProgressTitle("");
	}

	@Override
	protected void resetProgress()
	{
		super.resetProgress();
	}

	@Override
	public void taskStarted()
	{
		super.taskStarted();

		_out.println(_progressTitle + " " + _progressLength + " elements");
	}

	@Override
	protected void updateProgress()
	{
		final int pc = (int) ((100.0 * _progress) / _progressLength);

		if (pc == _progressPercent)
			return;

		_progressPercent = pc;

		if (pc < _echo)
			return;

		_echo += getEchoInterval();

		// delete the previous line
		_out.print('\r');

		// print the new message
		_out.print(_progressTitle);
		_out.print(": ");
		_out.print(_progressMessage);
		_out.print(" ");
		_out.print(_progressPercent);
		_out.print("% complete in ");
		_out.print(DurationFormat.SHORT.format(_timer.getElapsed()));
	}

	@Deprecated
	public String calcElapsedTime()
	{
		return DurationFormat.SHORT.format(_timer.getElapsed());
	}

	@Override
	public void taskFinished()
	{
		super.taskFinished();

		setProgress(_progressLength);

		_out.println();
		_out.print(_progressTitle);
		_out.print(" finished in ");
		_out.println(DurationFormat.SHORT.format(_timer.getLast()));
	}

}
