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

package org.mindswap.pellet;

import java.util.HashSet;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.tableau.completion.incremental.DependencyIndex;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.intset.IntSet;
import org.mindswap.pellet.utils.intset.IntSetFactory;

import aterm.ATermAppl;

/**
 * DependencySet for concepts and edges in the ABox for backjumping
 * 
 * @author Evren Sirin
 */
public class DependencySet {
    public static final Logger log;

	public static final int NO_BRANCH;

	/**
	 * An empty dependency set
	 */
	public static final DependencySet EMPTY;

	/**
	 * Used for assertions that are true by nature, i.e. an individual always
	 * has type owl:Thing
	 */
	public static final DependencySet	INDEPENDENT;

	public static final IntSet			ZERO;
	static {
		log			= Logger.getLogger( DependencySet.class.getName() );
		NO_BRANCH	= -1;
		ZERO = IntSetFactory.create();
		ZERO.add( 0 );
		EMPTY = new DependencySet();
		INDEPENDENT	= new DependencySet( 0 );
	}

	/**
	 * A dummy dependency set that is used just to indicate there is a
	 * dependency
	 */
	public static final DependencySet	DUMMY		= new DependencySet( 1 );

	/**
	 * index of branches this assertion depends on
	 */
	private IntSet						depends;

	/**
	 * branch number when this assertion was added to ABox
	 */
	private int							branch		= NO_BRANCH;

	private Set<ATermAppl>				explain;

	/**
	 * Create an empty set
	 */
	private DependencySet() {
		depends = IntSetFactory.create();
		setExplain( SetUtils.<ATermAppl>emptySet() );
	}

	/**
	 * Create a dependency set that depends on a single branch
	 * 
	 * @param branch
	 *            Branch number
	 */
	public DependencySet(int branch) {
		this.depends = IntSetFactory.create();

		depends.add( branch );
		setExplain( SetUtils.<ATermAppl>emptySet() );
	}

	/**
	 * Creates a dependency set with the given IntSet (no separate copy of
	 * IntSet is created so if IntSet is modified this DependencySet will be
	 * affected).
	 */
	private DependencySet(int branch, IntSet depends, Set<ATermAppl> explain) {
		this.branch = branch;
		this.depends = depends;
		this.setExplain( explain );
	}

	/**
	 * Creates a dependency set with no dependency and single explanation atom
	 */
	public DependencySet(ATermAppl explainAtom) {
		this.depends = DependencySet.ZERO;
		this.setExplain( SetUtils.singleton( explainAtom ) );

	}

	/**
	 * Creates a dependency set with no dependency and a set of explanation
	 * atoms
	 */
	public DependencySet(Set<ATermAppl> explain) {
		this.depends = DependencySet.ZERO;
		this.setExplain( explain );
	}

	/**
	 * Creates a new DependencySet object with a new branch number where the IntSet 
     * is shared (changing one will change the other).
	 * 
	 * @return
	 */
	public DependencySet copy(int newBranch) {
		return new DependencySet( newBranch, depends, explain );
	}

	/**
	 * Return true if <code>b</code> is in this set.
	 * 
	 * @param b
	 * @return
	 */
	public boolean contains(int b) {
		return depends.contains( b );
	}

	/**
	 * Add the integer value <code>b</code> to this DependencySet.
	 * 
	 * @param b
	 */
	public void add(int b) {
		depends.add( b );
	}

	/**
	 * Remove the integer value <code>b</code> from this DependencySet.
	 * 
	 * @param b
	 */
	public void remove(int b) {
		depends.remove( b );
	}

	/**
	 * Return true if there is no dependency on a non-deterministic branch
	 * 
	 * @return
	 */
	public boolean isIndependent() {
		return max() <= 0;
	}

	/**
	 * Get the branch number when the dependency set was created
	 */
	public int getBranch() {
		return branch;
	}
	
	/**
	 * Return the number of elements in this set.
	 * 
	 * @return
	 */
	public int size() {
		return depends.size();
	}

	/**
	 * Return the maximum value in this set.
	 * 
	 * @return
	 */
	public int max() {
		return depends.isEmpty() ? -1 : depends.max();
	}

	
	/**
	 * Create a new DependencySet and all the elements of <code>this</code>
	 * and <code>set</code> .
	 * 
	 * @param ds
	 * @return
	 */
	public DependencySet union(IntSet set) {
		return new DependencySet( branch, depends.union( set ), explain );
	}
	
	/**
	 * Create a new DependencySet and all the elements of <code>this</code>
	 * and <code>ds</code>.
	 * 
	 * @param ds
	 * @param doExplanation
	 * @return
	 */
	public DependencySet union(DependencySet ds, boolean doExplanation) {
		IntSet newDepends = depends.union( ds.depends );
		Set<ATermAppl> newExplain;
		
		if( doExplanation ) {
			newExplain = SetUtils.union( explain, ds.explain );
		}
		else {
			newExplain = SetUtils.emptySet();
		}

		return new DependencySet( branch, newDepends, newExplain );
	}

	/**
	 * 
	 * @param explain
	 * @param doExplanation
	 * @return
	 */
	public DependencySet union(Set<ATermAppl> explain, boolean doExplanation) {
		if( !doExplanation || explain.isEmpty() )
			return this;

		return new DependencySet( branch, depends.copy(), SetUtils.union( this.explain, explain ) );
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		sb.append( branch );
		sb.append( "-" );
		sb.append( depends );
		if( log.isLoggable( Level.FINE ) ) {
			sb.append( " " );
			sb.append( explain );
		}
		sb.append( "]" );
		return sb.toString();
	}

	/**
	 * Remove explanation sets which contain references to a syntactic assertion
	 * 
	 * @param assertion
	 */
	public void removeExplain(ATermAppl assertion) {
		if( getExplain().contains( assertion ) ) {
			setExplain( new HashSet<ATermAppl>() );
			if( DependencyIndex.log.isLoggable( Level.FINE ) )
				DependencyIndex.log.fine( "             Explain: removed " );
		}

	}

	public void setDepends(IntSet depends) {
		this.depends = depends;
	}

	public IntSet getDepends() {
		return depends;
	}

	/**
	 * @param explain the explain to set
	 */
	public void setExplain(Set<ATermAppl> explain) {
		this.explain = explain;
	}

	/**
	 * Return the set of explanations associated with this DependencySet.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getExplain() {
		return explain;
	}
	
	/**
	 * Return a dummy representation of this DependencySet such that
	 * 
	 * <code>this.isIndependent() == this.copyForCache().isIndependent()</code>
	 * 
	 * The returned copy will not be accurate w.r.t. any other function call,
	 * e.g. <code>contains(int)</code> for the copy will return different
	 * results for the copy. This function does not create a new DependencySet
	 * object so will not require additional memory. Caching this copy is
	 * more appropriate so we don't waste space for storing the actual
	 * dependency set or the explanation which are not used in caches anyway.
	 * 
	 * @return
	 */
	public DependencySet cache() {
    	if( isIndependent() ) {
    		return DependencySet.INDEPENDENT;
    	}
    	else {
    		return DependencySet.DUMMY;
    	}
	}
}
