package com.clarkparsia.pellet.owlapi;

import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class ProgressAdapter implements ProgressMonitor
{

	private final ReasonerProgressMonitor _monitor;
	private int _length;
	private int _progress;
	private String _title;

	public ProgressAdapter(final ReasonerProgressMonitor monitor)
	{
		this._monitor = monitor;
		_progress = 0;
		_length = -1;
		_title = "<untitled>";
	}

	@Override
	public int getProgress()
	{
		return _progress;
	}

	@Override
	public int getProgressPercent()
	{

		return _length > 0 ? (_progress * 100) / _length : 0;
	}

	@Override
	public void incrementProgress()
	{
		_progress++;
		if (_length > 0)
			_monitor.reasonerTaskProgressChanged(_progress, _length);
		else
			_monitor.reasonerTaskBusy();
	}

	@Override
	public boolean isCanceled()
	{
		return false;
	}

	@Override
	public void setProgress(final int value)
	{
		_progress = value;
		if (_length > 0)
			_monitor.reasonerTaskProgressChanged(_progress, _length);
		else
			_monitor.reasonerTaskBusy();
	}

	@Override
	public void setProgressLength(final int length)
	{
		this._length = length;
		if (length > 0)
			_monitor.reasonerTaskProgressChanged(_progress, length);
		else
			_monitor.reasonerTaskBusy();
	}

	@Override
	public void setProgressMessage(final String message)
	{
		//_monitor.setMessage( message );
	}

	@Override
	public void setProgressTitle(final String title)
	{
		this._title = title;
	}

	@Override
	public void taskFinished()
	{
		_monitor.reasonerTaskStopped();
	}

	@Override
	public void taskStarted()
	{
		_monitor.reasonerTaskStarted(_title);
	}

}
