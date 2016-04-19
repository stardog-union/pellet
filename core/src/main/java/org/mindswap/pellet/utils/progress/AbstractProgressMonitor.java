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
	protected String progressTitle = "";

	protected String progressMessage = "";

	protected int progress = 0;

	protected int progressLength = 0;

	protected int progressPercent = -1;

	protected Timer timer = new Timer();

	protected boolean cancelled = false;

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
		return progress;
	}

	public int getProgressLength()
	{
		return progressLength;
	}

	public String getProgressMessage()
	{
		return progressMessage;
	}

	@Override
	public int getProgressPercent()
	{
		return progressPercent;
	}

	public String getProgressTitle()
	{
		return progressTitle;
	}

	@Override
	public void incrementProgress()
	{
		setProgress(progress + 1);
	}

	@Override
	public boolean isCanceled()
	{
		return cancelled;
	}

	protected void resetProgress()
	{
		progress = 0;
		progressPercent = -1;
	}

	@Override
	public void setProgress(final int progress)
	{
		this.progress = progress;

		updateProgress();
	}

	@Override
	public void setProgressLength(final int progressLength)
	{
		this.progressLength = progressLength;

		resetProgress();
	}

	@Override
	public void setProgressMessage(final String progressMessage)
	{
		this.progressMessage = progressMessage;
	}

	@Override
	public void setProgressTitle(final String progressTitle)
	{
		this.progressTitle = progressTitle;
	}

	@Override
	public void taskFinished()
	{
		timer.stop();
	}

	@Override
	public void taskStarted()
	{
		resetProgress();

		timer.start();
	}

	protected abstract void updateProgress();
}
