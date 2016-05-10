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
public class TreeTaxonomyPrinter<T> implements TaxonomyPrinter<T>
{
	// Indentation string used when classification tree is printed
	final static String INDENT = "  ";

	protected Taxonomy<T> _taxonomy;
	protected PrintWriter _out;

	public TreeTaxonomyPrinter()
	{
	}

	@Override
	public void print(final Taxonomy<T> taxonomy)
	{
		print(taxonomy, new PrintWriter(System.out));
	}

	@Override
	public void print(final Taxonomy<T> taxonomy, final PrintWriter out)
	{
		this._taxonomy = taxonomy;
		this._out = out;

		out.println();
		printTree();
		out.println();
		out.flush();
	}

	/**
	 * Print the taxonomy as an indented tree
	 */
	protected void printTree()
	{
		final Set<T> top = new TreeSet<>(Comparators.stringComparator);
		top.add(_taxonomy.getTop().getName());
		top.addAll(_taxonomy.getTop().getEquivalents());
		printTree(top, " ");

		final Set<T> bottom = new TreeSet<>(Comparators.stringComparator);
		bottom.add(_taxonomy.getBottom().getName());
		bottom.addAll(_taxonomy.getBottom().getEquivalents());

		if (bottom.size() > 1)
			printNode(bottom, " ");
	}

	/**
	 * Print the tree rooted at the given _node
	 *
	 * @param _node
	 * @param indent
	 */
	protected void printTree(final Set<T> set, final String indent)
	{
		if (set.contains(_taxonomy.getBottom().getName()))
			return;

		printNode(set, indent);

		final T c = set.iterator().next();
		final Set<Set<T>> subs = ss(_taxonomy.getSubs(c, true));
		final Iterator<Set<T>> j = subs.iterator();

		while (j.hasNext())
		{
			final Set<T> eqs = j.next();
			if (eqs.contains(c))
				continue;

			printTree(s(eqs), indent + "   ");
		}
	}

	/**
	 * Print the _node contents with indentation and newline
	 *
	 * @param set
	 * @param indent
	 */
	protected void printNode(final Set<T> set, final String indent)
	{
		_out.print(indent);
		printNode(set);
		_out.println();
	}

	/**
	 * Print the _node contents in one line
	 *
	 * @param set
	 */
	protected void printNode(final Set<T> set)
	{
		final Iterator<T> i = set.iterator();
		final T c = i.next();
		printURI(_out, c);
		while (i.hasNext())
		{
			_out.print(" = ");
			printURI(_out, i.next());
		}
	}

	protected void printURI(final PrintWriter out, final T e)
	{
		out.print(e.toString());
	}

	private Set<Set<T>> ss(final Set<Set<T>> subs)
	{
		final Set<Set<T>> sorted = new TreeSet<>(Comparators.stringComparator);
		sorted.addAll(subs);

		return sorted;
	}

	private Set<T> s(final Set<T> set)
	{
		final Set<T> sorted = new TreeSet<>(Comparators.stringComparator);
		sorted.addAll(set);

		return sorted;
	}
}
