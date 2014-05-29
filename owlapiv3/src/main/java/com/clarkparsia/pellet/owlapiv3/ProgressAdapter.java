package com.clarkparsia.pellet.owlapiv3;

import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class ProgressAdapter implements ProgressMonitor  {

	private ReasonerProgressMonitor	monitor;
	private int length;
	private int progress;
	private String title;

	public ProgressAdapter( ReasonerProgressMonitor monitor ) {
		this.monitor = monitor;
		progress = 0;
		length = -1;
		title = "<untitled>";
	}

	public int getProgress() {
		return progress;
	}

	public int getProgressPercent() {
		
		return length > 0 ? (progress*100)/length : 0;
	}

	public void incrementProgress() {
		progress++;
		if ( length > 0 ) {
			monitor.reasonerTaskProgressChanged( progress, length );
		} else {
			monitor.reasonerTaskBusy();
		}
	}

	public boolean isCanceled() {
		return false;
	}

	public void setProgress(int value) {
		progress = value;
		if ( length > 0 ) {
			monitor.reasonerTaskProgressChanged( progress, length );
		} else {
			monitor.reasonerTaskBusy();
		}
	}

	public void setProgressLength(int length) {
		this.length = length;
		if ( length > 0 ) {
			monitor.reasonerTaskProgressChanged( progress, length );
		} else {
			monitor.reasonerTaskBusy();
		}
	}

	public void setProgressMessage(String message) {
		//monitor.setMessage( message );
	}

	public void setProgressTitle(String title) {
		this.title = title;
	}

	public void taskFinished() {
		monitor.reasonerTaskStopped();
	}

	public void taskStarted() {
		monitor.reasonerTaskStarted( title );
	}
	
	
}
