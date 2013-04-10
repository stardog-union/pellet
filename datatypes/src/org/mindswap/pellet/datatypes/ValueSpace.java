// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.util.Comparator;

public interface ValueSpace extends Comparator {
    public static final int INFINITE = -1;

    public Object getMidValue();
    
    public Object getMinValue();
    
    public Object getMaxValue();
    
    public boolean isInfinite();
    
    public boolean isInfinite( Object value );
    
    public boolean isValid( Object value );
    
    public Object getValue( String literal );
    
    public String getLexicalForm( Object value );
    
    public int compare(Object o1, Object o2);
    
    public int count( Object start, Object end );
    
    public Object succ( Object start, int n );
}