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

package org.mindswap.pellet.taxonomy.printer;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.Comparators;

/**
 * @author Evren Sirin
 */
public class TreeTaxonomyPrinter<T> implements TaxonomyPrinter<T> {
	// Indentation string used when classification tree is printed
	final static String		INDENT	= "  ";

	protected Taxonomy<T>	taxonomy;
	protected PrintWriter	out;

	public TreeTaxonomyPrinter() {
	}

	public void print(Taxonomy<T> taxonomy) {
		print( taxonomy, new PrintWriter(System.out) );
	}

	public void print(Taxonomy<T> taxonomy, PrintWriter out) {
		this.taxonomy = taxonomy;
		this.out = out;

		out.println();
		printTree();
		out.println();
		out.flush();
	}

	/**
	 * Print the taxonomy as an indented tree
	 */
	protected void printTree() {
		Set<T> top = new TreeSet<T>( Comparators.stringComparator );
		top.add( taxonomy.getTop().getName() );
		top.addAll( taxonomy.getTop().getEquivalents() );
		printTree( top, " " );

		Set<T> bottom = new TreeSet<T>( Comparators.stringComparator );
		bottom.add( taxonomy.getBottom().getName() );
		bottom.addAll( taxonomy.getBottom().getEquivalents() );

		if( bottom.size() > 1 )
			printNode( bottom, " " );
	}

	/**
	 * Print the tree rooted at the given node
	 * 
	 * @param node
	 * @param indent
	 */
	protected void printTree(Set<T> set, String indent) {
		if( set.contains( taxonomy.getBottom().getName() ) )
			return;

		printNode( set, indent );

		T c = set.iterator().next();
		Set<Set<T>> subs = ss( taxonomy.getSubs( c, true ) );
		Iterator<Set<T>> j = subs.iterator();

		while( j.hasNext() ) {
			Set<T> eqs = j.next();
			if( eqs.contains( c ) )
				continue;

			printTree( s( eqs ), indent + "   " );
		}
	}

	/**
	 * Print the node contents with indentation and newline
	 * 
	 * @param set
	 * @param indent
	 */
	protected void printNode(Set<T> set, String indent) {
		out.print( indent );
		printNode( set );
		out.println();
	}

	/**
	 * Print the node contents in one line
	 * 
	 * @param set
	 */
	protected void printNode(Set<T> set) {
		Iterator<T> i = set.iterator();
		T c = i.next();
		printURI( out, c );
		while( i.hasNext() ) {
			out.print( " = " );
			printURI( out, i.next() );
		}
	}

	protected void printURI(PrintWriter out, T e) {
		out.print( e.toString() );
	}

	private Set<Set<T>> ss(Set<Set<T>> subs) {
		Set<Set<T>> sorted = new TreeSet<Set<T>>( Comparators.stringComparator );
		sorted.addAll( subs );

		return sorted;
	}

	private Set<T> s(Set<T> set) {
		Set<T> sorted = new TreeSet<T>( Comparators.stringComparator );
		sorted.addAll( set );

		return sorted;
	}
}