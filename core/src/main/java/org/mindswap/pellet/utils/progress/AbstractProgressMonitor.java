// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.progress;

import org.mindswap.pellet.utils.Timer;

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
public abstract class AbstractProgressMonitor implements ProgressMonitor
{
	protected String _progressTitle = "";

	protected String _progressMessage = "";

	protected int _progress = 0;

	protected int _progressLength = 0;

	protected int _progressPercent = -1;

	protected Timer _timer = new Timer();

	protected boolean _cancelled = false;

	public AbstractProgressMonitor()
	{
	}

	public AbstractProgressMonitor(final int length)
	{
		setProgressLength(length);
	}

	@Override
	public int getProgress()
	{
		return _progress;
	}

	public int getProgressLength()
	{
		return _progressLength;
	}

	public String getProgressMessage()
	{
		return _progressMessage;
	}

	@Override
	public int getProgressPercent()
	{
		return _progressPercent;
	}

	public String getProgressTitle()
	{
		return _progressTitle;
	}

	@Override
	public void incrementProgress()
	{
		setProgress(_progress + 1);
	}

	@Override
	public boolean isCanceled()
	{
		return _cancelled;
	}

	protected void resetProgress()
	{
		_progress = 0;
		_progressPercent = -1;
	}

	@Override
	public void setProgress(final int progress)
	{
		this._progress = progress;

		updateProgress();
	}

	@Override
	public void setProgressLength(final int progressLength)
	{
		this._progressLength = progressLength;

		resetProgress();
	}

	@Override
	public void setProgressMessage(final String progressMessage)
	{
		this._progressMessage = progressMessage;
	}

	@Override
	public void setProgressTitle(final String progressTitle)
	{
		this._progressTitle = progressTitle;
	}

	@Override
	public void taskFinished()
	{
		_timer.stop();
	}

	@Override
	public void taskStarted()
	{
		resetProgress();

		_timer.start();
	}

	protected abstract void updateProgress();
}
