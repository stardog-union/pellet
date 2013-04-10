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

import org.mindswap.pellet.Individual;
import org.mindswap.pellet.utils.SetUtils;

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
public class DoubleBlocking extends Blocking {
	private static final DoubleBlocking INSTANCE = new DoubleBlocking();
	
	public static DoubleBlocking getInstance() {
		return INSTANCE;
	}
	
	private DoubleBlocking() {		
	}

	public boolean isDirectlyBlockedBy(BlockingContext cxt) {
		Individual parentBlocked = cxt.blocked.getParent();
		Individual parentBlocker = cxt.blocker.getParent();
		
		if( SetUtils.equals( cxt.blocked.getTypes(), cxt.blocker.getTypes() )
				&& SetUtils.equals( parentBlocked.getTypes(), parentBlocker.getTypes() ) ) {
			if( SetUtils.equals( cxt.getIncomingRoles(), BlockingContext.getIncomingRoles( cxt.blocker ) ) )
				return true;					
		}
		
		return false;
	}
}
