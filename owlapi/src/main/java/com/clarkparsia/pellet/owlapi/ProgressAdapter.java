package com.clarkparsia.pellet.owlapi;

import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class ProgressAdapter implements ProgressMonitor
{

	private final ReasonerProgressMonitor monitor;
	private int length;
	private int progress;
	private String title;

	public ProgressAdapter(final ReasonerProgressMonitor monitor)
	{
		this.monitor = monitor;
		progress = 0;
		length = -1;
		title = "<untitled>";
	}

	@Override
	public int getProgress()
	{
		return progress;
	}

	@Override
	public int getProgressPercent()
	{

		return length > 0 ? (progress * 100) / length : 0;
	}

	@Override
	public void incrementProgress()
	{
		progress++;
		if (length > 0)
			monitor.reasonerTaskProgressChanged(progress, length);
		else
			monitor.reasonerTaskBusy();
	}

	@Override
	public boolean isCanceled()
	{
		return false;
	}

	@Override
	public void setProgress(final int value)
	{
		progress = value;
		if (length > 0)
			monitor.reasonerTaskProgressChanged(progress, length);
		else
			monitor.reasonerTaskBusy();
	}

	@Override
	public void setProgressLength(final int length)
	{
		this.length = length;
		if (length > 0)
			monitor.reasonerTaskProgressChanged(progress, length);
		else
			monitor.reasonerTaskBusy();
	}

	@Override
	public void setProgressMessage(final String message)
	{
		//monitor.setMessage( message );
	}

	@Override
	public void setProgressTitle(final String title)
	{
		this.title = title;
	}

	@Override
	public void taskFinished()
	{
		monitor.reasonerTaskStopped();
	}

	@Override
	public void taskStarted()
	{
		monitor.reasonerTaskStarted(title);
	}

}
