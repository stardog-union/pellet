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

package org.mindswap.pellet.tableau.blocking;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Timer;

/**
 * <p>
 * Generic class to check if an individual in an completion graph is blocked by
 * another individual. Blocking prevents infinite models to be created and can
 * improve performance by limiting the size of the completion graph built.
 * </p>
 * <p>
 * This abstract class defines the basic functionality needed to check for
 * blocking and leaves the actual check of blocking condition between a pair of
 * individuals to its concrete subclasses that may do different things based on
 * the expressivity of the current kb.
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class Blocking {
    public final static Logger log = Logger.getLogger( Blocking.class.getName() );

    protected static final BlockingCondition block1 = new Block1();
    protected static final BlockingCondition block2 = new Block2();
    protected static final BlockingCondition block3 = new Block3();
    protected static final BlockingCondition block4 = new Block4();
    protected static final BlockingCondition block5 = new Block5();
    protected static final BlockingCondition block6 = new Block6();
    
	protected Blocking() {		
	}
	
	public boolean isDynamic() {
		return true;
	}
	
	public boolean isBlocked(Individual blocked) {
		Timer t = blocked.getABox().getKB().timers.startTimer( "blocking" );
		try {
			return !blocked.isRoot() && (isIndirectlyBlocked( blocked ) || isDirectlyBlockedInt( blocked ));
		}
		finally {
			t.stop();
		}
	}
	
	public boolean isIndirectlyBlocked(Individual blocked) {
		Individual parent = blocked.getParent();
		if( parent == null )
			return false;
		blocked.setBlocked( isBlocked( parent ) );
		return blocked.isBlocked();
	}
	
	public boolean isDirectlyBlocked(Individual blocked) {
		Timer t = blocked.getABox().getKB().timers.startTimer( "dBlocking" );	
		try {		
			return isDirectlyBlockedInt( blocked );
		}
		finally {
			t.stop();
		}
	}
	
	protected boolean isDirectlyBlockedInt(Individual blocked) {
		Individual parentBlocked = blocked.getParent();
		if( blocked.isRoot() || parentBlocked.isRoot() )
			return false;
		
		BlockingContext cxt = new BlockingContext( blocked );		
		while( cxt.moveBlockerUp() ) {			
	    	if( isDirectlyBlockedBy( cxt ) )  {
	    		blocked.setBlocked( true );
	    		if( log.isLoggable( Level.FINER ) )
	    			log.finer( blocked +  " blocked by " + cxt.blocker );	    			
				return true;
	    	}
	    }
	    
		if( PelletOptions.USE_ANYWHERE_BLOCKING ) {					
			assert cxt.blocker.isRoot();

			return isDirectlyBlockedByDescendant( cxt );			
		}
		
		return false;
	}

	protected boolean isDirectlyBlockedByDescendant(BlockingContext cxt) {
		if( cxt.blocked.getParent().equals( cxt.blocker ) )
			return false;
		
		if( !cxt.blocker.isRoot() && isDirectlyBlockedBy( cxt ) ) {
			cxt.blocked.setBlocked( true );
    		if( log.isLoggable( Level.FINER ) )
    			log.finer( cxt.blocked +  " blocked by " + cxt.blocker );
			return true;
		}
		
		Individual blocker = cxt.blocker;
		for( Edge e : blocker.getOutEdges() ) {
			Node child = e.getTo();

			if( cxt.moveBlockerDown( child ) ) {
				if( isDirectlyBlockedByDescendant( cxt ) ) {			
					return true;
				}
				cxt.moveBlockerUp();
			}
		}
		
		return false;
	}
	
	protected abstract boolean isDirectlyBlockedBy(BlockingContext cxt);
}
