// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

/**
 * @author Evren Sirin
 *
 */
public class Bool {
    public final static Bool FALSE   = new Bool();
    public final static Bool TRUE    = new Bool();
    public final static Bool UNKNOWN = new Bool();
    
    private Bool() {
    }
    
    public static Bool create( boolean value ) {
        return value ? TRUE : FALSE;
    }
    
    public Bool not() {
        if( this == TRUE )
            return FALSE;
        
        if( this == FALSE )
            return TRUE;
        
        return UNKNOWN;       
    }

    public Bool or( Bool other ) {
        if( this == TRUE || other == TRUE )
            return TRUE;
        
        if( this == FALSE && other == FALSE )
            return FALSE;
        
        return UNKNOWN;      
    }
    
    public Bool and( Bool other ) {
        if( this == TRUE && other == TRUE )
            return TRUE;
        
        if( this == FALSE || other == FALSE )
            return FALSE;
        
        return UNKNOWN;       
    }
    
    public boolean isTrue() {
        return this == TRUE;
    }
    
    public boolean isFalse() {
        return this == FALSE;        
    }

    public boolean isUnknown() {
        return this == UNKNOWN;        
    }

    public boolean isKnown() {
        return this != UNKNOWN;        
    }
    
    public String toString() {
        return isTrue() ? "true" : isFalse() ? "false" : "unknown";
    }
}
