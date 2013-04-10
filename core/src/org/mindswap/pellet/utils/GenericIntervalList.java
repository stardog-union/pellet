/****************************************************************************
 * Copyright (C) 1999-2001 by the Massachusetts Institute of Technology,
 *                       Cambridge, Massachusetts.
 *
 *                        All Rights Reserved
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for any purpose and without fee is hereby
 * granted, provided that the above copyright notice appear in all
 * copies and that both that copyright notice and this permission
 * notice appear in supporting documentation, and that MIT's name not
 * be used in advertising or publicity pertaining to distribution of
 * the software without specific, written prior permission.
 *  
 * THE MASSACHUSETTS INSTITUTE OF TECHNOLOGY DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS.  IN NO EVENT SHALL THE MASSACHUSETTS
 * INSTITUTE OF TECHNOLOGY BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
 * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 * @author: Jeffrey Sheldon (jeffshel@mit.edu)
 *          Spring 2001
 *
 * Version: $Id: IntervalList.java,v 1.1 2003/04/01 00:10:20 noto Exp $
 *
 ***************************************************************************/

/**
 * An IntervalList is a mutable abstraction of a set of ranges of continuous
 * numbers.
 */

package org.mindswap.pellet.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.mindswap.pellet.datatypes.ValueSpace;
import org.mindswap.pellet.exceptions.InternalReasonerException;

import com.clarkparsia.pellet.datatypes.DiscreteInterval;

/**
 * @deprecated Use {@link DiscreteInterval} instead
 */
public class GenericIntervalList {
    // Representation Invariant:
    // forall 0 <= i < j < intervals.size,
    // intervals[i].end < intervals[j].start

    // Abstraction Function:
    // An interval list represents the set of all numbers contained
    // within any of the intervals in intervals.

    /**
     * An Interval is an immutable representation of a single contigous range of numbers from start
     * to end.
     */
    public class Interval implements Comparable {

        // Representation Invariant:
        // start < end

        // Abstraction Function:
        // The set of all numbers between start and end

        private final Object start;

        private final boolean incStart;

        private final Object end;

        private final boolean incEnd;

        /**
         * @requires: end >= start
         * 
         * @effects: creates a new Interval representing the numbers from <code>start</code> to
         *           <code>end</code>.
         */
        public Interval( Object start, boolean incStart, Object end, boolean incEnd ) {
            int cmp = valueSpace.compare( end, start );
            if( cmp < 0 ) {
                throw new IllegalArgumentException( "Interval end is less than start " + end + " < " + start );
            }
            else if( cmp == 0 && !incStart && !incEnd ) {
                throw new IllegalArgumentException( "Cannot create empty interval (" + start + ", " + end + ")");                
            }
            this.start = start;
            this.incStart = incStart;
            this.end = end;
            this.incEnd = incEnd;
        }

        /**
         * @effects: returns the lower bound of this Interval.
         */
        public Object start() {
            return start;
        }
        
        /**
         * @effects: returns if the start value is included in this Interval.
         */
        public boolean incStart() {
            return incStart;
        }

        /**
         * @effects: returns the upper bound of this Interval.
         */
        public Object end() {
            return end;
        }
        /**
         * @effects: returns if the end value is included in this Interval.
         */
        public boolean incEnd() {
            return incEnd;
        }


        /**
         * @effects: returns the number of elements in this Interval.
         */
        public int count() {
            int count = valueSpace.count( start, end );

            if( count != ValueSpace.INFINITE ) {
                if( !incStart )
                    count--;
                if( !incEnd )
                    count--;
            }

            return count;
        }
        
        // returns true of this and i overlap
        private boolean overlaps( Interval i ) {
            if( valueSpace.compare( this.start, i.start ) <= 0 ) {
                int cmp = valueSpace.compare( i.start, this.end );
                if( cmp > 0 || (cmp == 0 && (!i.incStart || !this.incEnd)) ) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                return i.overlaps( this );
            }
        }

        // requires that this an i overlap, returns a single interval
        // representing the union of the numbers contained within.
        private Interval merge( Interval i ) {
            if( overlaps( i ) ) {
                int cmpStart = valueSpace.compare( this.start, i.start );
                int cmpEnd = valueSpace.compare( i.end, this.end );

                Object newStart, newEnd;
                boolean newIncStart, newIncEnd;

                if( cmpStart < 0 ) {
                    newStart = this.start;
                    newIncStart = this.incStart;
                }
                else if( cmpStart == 0 ) {
                    newStart = this.start;
                    newIncStart = this.incStart || i.incStart;
                }
                else {
                    newStart = i.start;
                    newIncStart = i.incStart;
                }

                if( cmpEnd < 0 ) {
                    newEnd = this.end;
                    newIncEnd = this.incEnd;
                }
                else if( cmpEnd == 0 ) {
                    newEnd = this.end;
                    newIncEnd = this.incEnd || i.incEnd;
                }
                else {
                    newEnd = i.end;
                    newIncEnd = i.incEnd;
                }

                return new Interval( newStart, newIncStart, newEnd, newIncEnd );
            }
            else {
                throw new IllegalArgumentException();
            }
        }

        // requires this overlaps with i, returns a new Interval which
        // contains the intersection of numbers from this and i.
        private Interval restrictTo( Interval i ) {
            if( !overlaps( i ) ) {
                throw new IllegalArgumentException();
            }

            int cmpStart = valueSpace.compare( this.start, i.start );
            int cmpEnd = valueSpace.compare( i.end, this.end );

            Object newStart, newEnd;
            boolean newIncStart, newIncEnd;

            if( cmpStart > 0 ) {
                newStart = this.start;
                newIncStart = this.incStart;
            }
            else if( cmpStart == 0 ) {
                newStart = this.start;
                newIncStart = this.incStart && i.incStart;
            }
            else {
                newStart = i.start;
                newIncStart = i.incStart;
            }

            if( cmpEnd > 0 ) {
                newEnd = this.end;
                newIncEnd = this.incEnd;
            }
            else if( cmpEnd == 0 ) {
                newEnd = this.end;
                newIncEnd = this.incEnd && i.incEnd;
            }
            else {
                newEnd = i.end;
                newIncEnd = i.incEnd;
            }

            return new Interval( newStart, newIncStart, newEnd, newIncEnd );
        }

        // compares based only on the ordering of the start value
        public int compareTo( Object o ) {
            Interval i = (Interval) o;
            if( valueSpace.compare( this.start, i.start ) < 0 ) {
                return -1;
            }
            else if( valueSpace.compare( this.start, i.start ) > 0 ) {
                return 1;
            }
            else {
                return 0;
            }
        }

        public boolean equals( Object o ) {
            if( o instanceof Interval ) {
                Interval i = (Interval) o;
                if( i == this ) {
                    return true;
                }
                if( this.start.equals( i.start ) && this.end.equals( i.end ) ) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        public boolean contains( Object value ) {
            int cmpStart = valueSpace.compare( start, value );
            int cmpEnd = valueSpace.compare( value, end );

            return (cmpStart < 0 || (cmpStart == 0 && incStart))
                && (cmpEnd < 0 || (cmpEnd == 0 && incEnd));
        }

        public int hashCode() {
            int hashCode = 11;
            
            hashCode = 37 * hashCode + start.hashCode();
            hashCode = 37 * hashCode + (incStart ? 1231 : 1237);
            hashCode = 37 * hashCode + end.hashCode();
            hashCode = 37 * hashCode + (incEnd ? 1231 : 1237);
            
            return hashCode;
        }

        public String toString() {
            return (incStart?"[":"(") + start + " - " + end + (incEnd?"]":")");
        }
    }

    private final List intervals;

    private ValueSpace valueSpace;

    /**
     * @requires: end >= start
     * 
     * @effects: creates a new IntervalList containing the range from <code>start</code> to
     *           <code>end</code>.
     */
    public GenericIntervalList( Object start, Object end, ValueSpace comp ) {
        valueSpace = comp;
        intervals = new LinkedList();
        intervals.add( new Interval( start, true, end, true ) );
    }

    /**
     * @effects: creates a new empty IntervalList (contains no numbers)
     */
    public GenericIntervalList( ValueSpace comp ) {
        valueSpace = comp;
        intervals = new LinkedList();
    }

    /**
     * @effects: creates a new IntervalList which contains a copy of the ranges represented by
     *           <code>il</code>.
     */
    public GenericIntervalList( GenericIntervalList il ) {
        valueSpace = il.valueSpace;
        intervals = new LinkedList( il.intervals );
    }

    /**
     * @effects: returns true iff <code>this</code> represents the empty set of intervals.
     */
    public boolean isEmpty() {
        return intervals.isEmpty();
    }

    /**
     * @effects: returns the lower bound of the lowest range of this, or null if this represents an
     *           empty set of intervals.
     */
    public Object min() {
        if( intervals.isEmpty() ) {
            return null;
        }
        return ((Interval) intervals.get( 0 )).start();
    }

    /**
     * @effects: returns the upper bound of the highest range of this, or null if this represents an
     *           empty set of intervals.
     */
    public Object max() {
        if( intervals.isEmpty() ) {
            return null;
        }
        return ((Interval) intervals.get( intervals.size() - 1 )).end();
    }

    /**
     * @effects: returns the number of integers in this Interval.
     */
    public int count() {
        int count = 0;
        ListIterator iter = intervals.listIterator();
        while( iter.hasNext() ) {
            Interval i = (Interval) iter.next();
            int intervalCount = i.count();
            if( intervalCount == ValueSpace.INFINITE )
                return ValueSpace.INFINITE;
            count += i.count();
            // overflow
            if( count < 0 )
                return ValueSpace.INFINITE;
        }

        return count;
    }

    /**
     * @effects: canonicalizes <code>intervals</code> to contain sorted non-overlapping ranges.
     */
    private void canonicalize() {
        Collections.sort( intervals );
        Interval lastElement;
        Interval currentElement;

        ListIterator iter = intervals.listIterator();
        if( !iter.hasNext() ) {
            return;
        }
        currentElement = (Interval) iter.next();
        while( iter.hasNext() ) {
            lastElement = currentElement;
            currentElement = (Interval) iter.next();
            if( lastElement.overlaps( currentElement ) ) {
                Interval newElement = lastElement.merge( currentElement );
                iter.remove();
                iter.previous();
                iter.remove();
                iter.add( newElement );
                currentElement = newElement;
            }
        }
    }

    /**
     * @requires: end >= start
     * 
     * @effects: adds the range from <code>start</code> to <code>end</code> to this.
     */
    public void addInterval( Object start, Object end ) {
        addInterval( start, true, end, true );
    }

    /**
     * @requires: end >= start
     * 
     * @effects: adds the range from <code>start</code> to <code>end</code> to this.
     */
    public void addInterval( Object start, boolean incStart, Object end, boolean incEnd ) {
        addIntervalInternal( start, incStart, end, incEnd );
        canonicalize();
    }

    // adds the range from start to end to this, but does not canonicalize
    private void addIntervalInternal( Object start, boolean incStart, Object end, boolean incEnd ) {
        intervals.add( new Interval( start, incStart, end, incEnd ) );
    }

    
    /**
     * @requires: end >= start
     * 
     * @effects: removes the range of numbers from <code>start</code> to <code>end</code> from
     *           this.
     */
    public void removeInterval( Object start, boolean incStart, Object end, boolean incEnd) {
        removeIntervalInternal( start, incStart, end, incEnd );
        canonicalize();
    }

    // removes the range of numbers from start to end from this but does
    // not canonicalize
    private void removeIntervalInternal( Object start, boolean incStart, Object end, boolean incEnd ) {
        ListIterator iter = intervals.listIterator();
        Interval toRemove = new Interval( start, incStart, end, incEnd );
        while( iter.hasNext() ) {
            Interval curr = (Interval) iter.next();
            if( curr.overlaps( toRemove ) ) {
                iter.remove();
                if( valueSpace.compare( start, curr.start() ) > 0 ) {
                    iter.add( new Interval( curr.start(), curr.incStart(), start, !incStart ) );
                    if( valueSpace.compare( end, curr.end() ) < 0 ) {
                        iter.add( new Interval( end, !incEnd, curr.end(), curr.incEnd() ) );
                    }
                }
                else {
                    if( valueSpace.compare( end, curr.end() ) < 0 ) {
                        iter.add( new Interval( end, !incEnd, curr.end(), curr.incEnd() ) );
                    }
                }
            }
        }
    }

    /**
     * @effects: adds to this all of numbers reprsented by <code>il</code>.
     */
    public void addIntervalList( GenericIntervalList il ) {
        Iterator iter = il.intervals.iterator();
        while( iter.hasNext() ) {
            Interval i = (Interval) iter.next();
            addIntervalInternal( i.start(), i.incStart(), i.end(), i.incEnd() );
        }
        canonicalize();
    }

    /**
     * @effects: removes from this all of the numbers represented by <code>il</code>.
     */
    public void removeIntervalList( GenericIntervalList il ) {
        Iterator iter = il.intervals.iterator();
        while( iter.hasNext() ) {
            Interval i = (Interval) iter.next();
            removeIntervalInternal( i.start(), i.incStart(), i.end(), i.incEnd() );
        }
        canonicalize();
    }

    /**
     * @requires: end >= start
     * 
     * @effects: removes from this all of the numbers which are not between <code>start</code> and
     *           <code>end</code>.
     */
    public void restrictToInterval( Object start, boolean incStart, Object end, boolean incEnd ) {
        restrictToInterval( new Interval( start, incStart, end, incEnd ) );
    }

    /**
     * @effects: removes from this all of the numbers which are not contained in the range
     *           represented by <code>i</code>.
     */
    public void restrictToInterval( Interval i ) {
        ListIterator iter = intervals.listIterator();
        while( iter.hasNext() ) {
            Interval curr = (Interval) iter.next();
            if( curr.overlaps( i ) ) {
                iter.set( curr.restrictTo( i ) );
            }
            else {
                iter.remove();
            }
        }
    }

    /**
     * @effects: returns an Iterator which will return, in increasing order, Intervals representing
     *           the numbers contained in this.
     */
    public Iterator iterator() {
        return (Collections.unmodifiableList( intervals )).iterator();
    }

    public boolean equals( Object o ) {
        if( o instanceof GenericIntervalList ) {
            GenericIntervalList il = (GenericIntervalList) o;
            return intervals.equals( il.intervals );
        }
        return false;
    }

    public int hashCode() {
        return intervals.hashCode();
    }

    public String toString() {
        return intervals.toString();
    }

    public boolean contains( Object value ) {
        ListIterator iter = intervals.listIterator();
        while( iter.hasNext() ) {
            Interval curr = (Interval) iter.next();
            // TODO add early termination 
            if( curr.contains( value ) )
                return true;
        }

        return false;
    }
    
    /**
     * Return the <code>n</code>th object form this interval list. If the interval list is finite
     * the numbers will be returned in increasing order. If the interval is unbounded in one direction
     * (either the first or the last interval is infinite) then first all the values will in finite
     * intervals will be returned and then the values in the infinite interval will be returned.
     * 
     */
    public Object get( int n ) {
        int size = intervals.size();
        
        if( size > 0 ) {        
            // check if the 
            Interval first = (Interval) intervals.get( 0 );
            boolean firstInfinite = (first.count() == ValueSpace.INFINITE);
            Interval last = (Interval) intervals.get( size - 1 );
            boolean lastInfinite = (size > 1) && (last.count() == ValueSpace.INFINITE);
            
            // leave the infinite interval to last
            ListIterator iter = intervals.listIterator();
            if( firstInfinite ) 
                iter.next();            
            
            // try finite intervals
            while( iter.hasNext() ) {
              Interval i = (Interval) iter.next();
              int iCount = i.count();
              if( iCount == ValueSpace.INFINITE ) // did we hit another infinite interval?
                  break;
              else if( iCount <= n ) // this interval is used up
                  n -= i.count();
              else {
            	  int index = i.incStart ? n : n + 1;
                  Object result = valueSpace.succ( i.start(), index );
//                  System.out.println( "i.start " + i.start()  + " return " + result);
                  return result;
              }
           }
           
           // try infinite intervals
           if( firstInfinite ) {
               if( lastInfinite ) {
                   int choose = n % 2;
                   n = (int) Math.floor( n / 2.0 );
                   if( choose == 0 )
                       return valueSpace.succ( first.end(), -n );
                   else
                       return valueSpace.succ( last.start(), n );
               }
               else if( !valueSpace.isInfinite( first.start() ) )
                   return valueSpace.succ( first.start(), n );
               else if( !valueSpace.isInfinite( first.end() ) )
                   return valueSpace.succ( first.end(), -n );   
               else {
                   if( n == 0 )
                       return valueSpace.getMidValue();
                   
                   int choose = n % 2;
                   n = (int) Math.floor( n / 2.0 );
                   if( choose == 0 )
                       return valueSpace.succ( valueSpace.getMidValue(), -n );
                   else
                       return valueSpace.succ( valueSpace.getMidValue(), n );                   
               }                   
           }
           else
               valueSpace.succ( last.start(), n );            
        }

        throw new InternalReasonerException( "This interval does not have " + n + " values"  );
      }
}
