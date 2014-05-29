// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.utils;

import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mindswap.pellet.output.TableData;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class Timers  {
	private Map<String, Timer> timers = new LinkedHashMap<String, Timer>();
	
	final public Timer mainTimer;
	
	public Timers() {
	    mainTimer = createTimer("main");
	    mainTimer.start();
	}
	
	public void addAll( Timers other ) {
	    for( Entry<String, Timer> entry : other.timers.entrySet() ) {
            String name = entry.getKey();
            Timer otherTimer = entry.getValue();
            Timer thisTimer = getTimer( name );
            if( thisTimer == null )
                timers.put( name, otherTimer );
            else 
                thisTimer.add( otherTimer );            
        }	    
	}

	public Timer createTimer(String name) {
		Timer t = new Timer(name, mainTimer);
		timers.put(name, t);
		return t;
	}

	public Timer startTimer(String name) {
		Timer t = getTimer(name);
		if(t == null) t = createTimer(name);
		t.start();
		return t;
	}

	public void checkTimer(String name) {
		Timer t = getTimer(name);
		if (t == null)
			throw new UnsupportedOperationException("Timer " + name + " does not exist!");
		
		t.check();
	}
	
	public void resetTimer(String name) {
		Timer t = getTimer(name);
		if (t == null)
		    throw new UnsupportedOperationException("Timer " + name + " does not exist!");
		
		t.reset();		
	}
	
	public void interrupt() {
		mainTimer.interrupt();
	}
	
	public void setTimeout(String name, long timeout) {
		Timer t = getTimer(name);
		if (t == null)
			t = createTimer(name);
		
		t.setTimeout(timeout);
	}
		
	public void stopTimer(String name) {
		Timer t = getTimer(name);
		if (t == null)
		    throw new UnsupportedOperationException("Timer " + name + " does not exist!");
		
		t.stop();
	}

	public void resetAll() {
	    for( Timer timer : timers.values() ) {
	        timer.reset();
	    }
        mainTimer.start();
	}

    public long getTimerTotal(String name) {
        Timer timer = getTimer(name);
        return (timer == null) ? 0 : timer.getTotal();
    }

    public double getTimerAverage(String name) {
        Timer timer = getTimer(name);
        return (timer == null) ? 0 : timer.getAverage();
    }

	public Timer getTimer(String name) {
		return timers.get(name);
	}
	
	public Collection<Timer> getTimers() {
		return timers.values();
	}

	public void print() {
	    print( false );
	}

	public void print( final Writer pw ) {
		print( pw, false, "Total" );
	}
	
    public void print( final boolean shortForm ) {
        print( shortForm, "Total" );
    }
    
	public void print( final boolean shortForm, final String sortBy ) {
		print( new PrintWriter( System.out ), shortForm, sortBy );
	}
	
	public void print( final Writer pw, final boolean shortForm, final String sortBy ) {
		
		String[] colNames = shortForm 
			? new String[] {"Name", "Total (ms)" }
		    : new String[] {"Name", "Count", "Avg", "Total (ms)" };
            
        boolean[] alignment = shortForm
            ? new boolean[] { false, true }
            : new boolean[] { false, true, true, true };
			
		List<Timer> list = new ArrayList<Timer>( timers.values() );
        if( sortBy != null ) {
    		Collections.sort(list, new Comparator<Timer>() {

				public int compare(Timer o1, Timer o2) {
                    if( sortBy.equalsIgnoreCase( "Total" ) ) {
                    	long t1 = o1.getTotal();
                    	long t2 = o2.getTotal();
                    	if( t1 == 0 ) 
                    		t1 = o1.getElapsed();
                    	if( t2 == 0 ) 
                    		t2 = o2.getElapsed();
                        return (int)(t2 -t1);
                    }
                    else if( sortBy.equalsIgnoreCase( "Avg" ) )
                        return (int)(o2.getAverage() - o1.getAverage());
                    else if( sortBy.equalsIgnoreCase( "Count" ) )
                        return (int)(o2.getCount() - o1.getCount());
                    else 
                        return AlphaNumericComparator.CASE_INSENSITIVE.compare( o1, o2 );
    			}			
    		});		
        }
		
        NumberFormat nf = new DecimalFormat("0.00");
      
		TableData table = new TableData( Arrays.asList( colNames ) );
        table.setAlignment( alignment );
		for( Timer timer : list ) {
//			if(timer.getCount() == 0)
//			    continue;
			List<Object> row = new ArrayList<Object>();
			row.add(timer.getName());
			if(!shortForm) {
				row.add(String.valueOf(timer.getCount()));
				row.add(nf.format(timer.getAverage()));
			}
			if( timer.isStarted() )
				row.add(String.valueOf(timer.getElapsed()));
			else
				row.add(String.valueOf(timer.getTotal()));
			table.add(row);
		}
		
		table.print( pw );
	}
	
	public String toString() {
		return timers.values().toString();
	}
}
