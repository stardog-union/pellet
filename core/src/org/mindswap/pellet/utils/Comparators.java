// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import aterm.ATerm;

public class Comparators {
    public static final Comparator<Comparable<Object>> comparator = new Comparator<Comparable<Object>>() {
        public int compare( Comparable<Object> o1, Comparable<Object> o2 ) {
            return o1.compareTo( o2 );
        }
    };

    public static final Comparator<ATerm> termComparator = new Comparator<ATerm>() {
        public int compare( ATerm o1, ATerm o2 ) {
        	int h1 = o1.hashCode();
        	int h2 = o2.hashCode();

            if( h1 < h2 )
                return -1;
            else if( h1 > h2 )
                return 1;
            else if( o1 == o2 ) {
            	// aterm equality is identity equality due to maximal structure
            	// sharing
            	return 0;
            }
        	else {
        		// ATerm.toString is very inefficient but hashcodes of ATerms
        		// clash very infrequently. The need to compare two different
        		// terms with same hascode is nto very common either. String
        		// comparison gives us a stable ordering over different runs
        		return o1.toString().compareTo( o2.toString() );
        	}
        }
    };

    public static final Comparator<Number> numberComparator = new Comparator<Number>() {
        public int compare( Number n1, Number n2 ) {
            return NumberUtils.compare( n1, n2 );
        }
    };

    public static final Comparator<Object> stringComparator = new Comparator<Object>() {
        public int compare( Object o1, Object o2 ) {
            return o1.toString().compareTo( o2.toString() );
        }
    };

    public static final Comparator<Calendar> calendarComparator = new Comparator<Calendar>() {
        public int compare( Calendar c1, Calendar c2 ) {
            long t1 = c1.getTimeInMillis();
            long t2 = c2.getTimeInMillis();

            if( t1 < t2 )
                return -1;
            else if( t1 == t2 )
                return 0;
            else
                return 1;
        }
    };
    
    public static <T> Comparator<T> reverse( final Comparator<T> cmp ) {
        return Collections.reverseOrder( cmp );
    }
}
