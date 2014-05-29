// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.progress;

import java.io.PrintStream;

import org.mindswap.pellet.utils.DurationFormat;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class ConsoleProgressMonitor extends AbstractProgressMonitor implements ProgressMonitor {
    private PrintStream out;
        
    public ConsoleProgressMonitor() {    
    	this( System.err, 0 );
    }
    
    public ConsoleProgressMonitor(PrintStream out) {
    	this( out, 0 );
    }
    
    public ConsoleProgressMonitor(int length) {
    	this( System.err, length );
    }    
    
    public ConsoleProgressMonitor(PrintStream out, int length) {
    	this.out = out;
    	
        setProgressLength( length );
        setProgressTitle( "" );
    }
    
    protected void resetProgress() {
        super.resetProgress();   
    }

	public void taskStarted() {
        super.taskStarted();
        
        out.println( progressTitle + " " + progressLength + " elements" );
	}

    protected void updateProgress() {
        int pc = (int) ((100.0*progress) / progressLength);
        
        if( pc == progressPercent )
            return;
        
        progressPercent = pc;
        
        // delete the previous line
        out.print( '\r' );        
	        
        // print the new message
        out.print( progressTitle );
		out.print( ": " );
		out.print( progressMessage );
		out.print( " " );
		out.print( progressPercent );
		out.print( "% complete in " );
		out.print( DurationFormat.SHORT.format( timer.getElapsed() )  );
    }
    
    @Deprecated
    public String calcElapsedTime() {
        return DurationFormat.SHORT.format( timer.getElapsed() );
    }
    
    public void taskFinished() {
    	super.taskFinished();
    	
        setProgress( progressLength );
        
        out.println();        
        out.print( progressTitle );
        out.print( " finished in " );
        out.println( DurationFormat.SHORT.format( timer.getLast() ) );
    }
}
