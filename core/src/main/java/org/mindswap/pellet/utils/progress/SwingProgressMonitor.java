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
 * Description: Very simple implementation of the Pellet progress monitor using Swing widgets.
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

	private javax.swing.ProgressMonitor monitor = null;

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
		return monitor.isCanceled();
	}

	@Override
	protected void resetProgress()
	{
		super.resetProgress();
		monitor = new javax.swing.ProgressMonitor(null, progressTitle, progressMessage, 0, progressLength);
		monitor.setProgress(progress);
	}

	@Override
	public void setProgressMessage(final String progressMessage)
	{
		super.setProgressMessage(progressMessage);
		monitor.setNote(progressMessage);
	}

	@Override
	public void taskFinished()
	{
		super.taskFinished();
		monitor.close();
	}

	@Override
	protected void updateProgress()
	{
		SwingUtilities.invokeLater(() -> monitor.setProgress(progress));
	}
}
