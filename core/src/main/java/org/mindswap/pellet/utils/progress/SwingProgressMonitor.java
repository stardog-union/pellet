// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.progress;

import javax.swing.SwingUtilities;

/**
 * <p>
 * Title: SwingProgressMonitor
 * </p>
 * <p>
 * Description: Very simple implementation of the Pellet progress _monitor using Swing widgets.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class SwingProgressMonitor extends AbstractProgressMonitor
{
	private volatile int _echo = 0;

	@Override
	public int getLastEcho()
	{
		return _echo;
	}

	private javax.swing.ProgressMonitor _monitor = null;

	public SwingProgressMonitor()
	{
		super();
	}

	public SwingProgressMonitor(final int length)
	{
		super(length);
	}

	@Override
	public boolean isCanceled()
	{
		return _monitor.isCanceled();
	}

	@Override
	protected void resetProgress()
	{
		super.resetProgress();
		_monitor = new javax.swing.ProgressMonitor(null, _progressTitle, _progressMessage, 0, _progressLength);
		_monitor.setProgress(_progress);
	}

	@Override
	public void setProgressMessage(final String progressMessage)
	{
		super.setProgressMessage(progressMessage);
		_monitor.setNote(progressMessage);
	}

	@Override
	public void taskFinished()
	{
		super.taskFinished();
		_monitor.close();
	}

	@Override
	protected void updateProgress()
	{
		SwingUtilities.invokeLater(() ->
		{
			_echo = _progress;
			_monitor.setProgress(_progress);
		});
	}
}
