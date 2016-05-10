// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy.printer;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Comparators;

/**
 * <p>
 * Title: Functional Taxonomy Printer
 * </p>
 * <p>
 * Description: The output of this printer is "functional" in the sense that any _taxonomy has only a single _printed form. I.e., the output here is intended to
 * be unchanged by reorderings of sibling _nodes in the classification algorithm. It was developed as a way to compare the output of alternative classification
 * implementations. It is based on the format found in the DL benchmark test _data.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class FunctionalTaxonomyPrinter<T> implements TaxonomyPrinter<T>
{

	private Taxonomy<T> _taxonomy;

	private PrintWriter _out;

	private Set<T> _bottomEquivalents;

	private Set<T> _printed;

	public FunctionalTaxonomyPrinter()
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

		/*
		 * Note the bottom class (and equivalents) b/c it should only be output
		 * as a subclass if it is the *only* subclass.
		 */
		_bottomEquivalents = new TreeSet<>(Comparators.stringComparator);
		_bottomEquivalents.addAll(taxonomy.getBottom().getEquivalents());

		_printed = new HashSet<>();

		out.println();

		final Set<T> sortedTop = new TreeSet<>(Comparators.stringComparator);
		sortedTop.addAll(taxonomy.getTop().getEquivalents());

		final Set<Set<T>> topGroup = Collections.singleton(sortedTop);
		printGroup(topGroup);

		this._taxonomy = null;
		this._out = null;
		this._bottomEquivalents = null;
		this._printed = null;

		out.println();
		out.flush();
	}

	private void printGroup(final Collection<? extends Collection<T>> concepts)
	{

		final Set<Set<T>> nextGroup = new LinkedHashSet<>();

		for (Collection<T> eqC : concepts)
		{

			final T firstC = eqC.iterator().next();

			// Use supers to determine if this has been _printed before, if so
			// skip it
			final Set<Set<T>> supEqs = _taxonomy.getSupers(firstC, true);
			if ((supEqs.size() > 1) && _printed.contains(firstC))
				continue;
			else
				_printed.add(firstC);

			_out.print("(");

			// Print equivalent class group passed in (assume sorted)
			printEqClass(eqC);

			_out.print(" ");

			// Print any direct superclasses
			final Set<Set<T>> sortedSupEqs = new TreeSet<>(Comparators.stringComparator);
			for (final Set<T> set : supEqs)
			{
				final Set<T> group = new TreeSet<>(Comparators.stringComparator);
				group.addAll(set);
				sortedSupEqs.add(group);
			}
			printEqClassGroups(sortedSupEqs);

			_out.print(" ");

			// Print any direct subclasses
			final Set<Set<T>> sortedSubEqs = new TreeSet<>(Comparators.stringComparator);
			final Set<Set<T>> subEqs = _taxonomy.getSubs(firstC, true);
			for (final Set<T> set : subEqs)
			{
				final Set<T> group = new TreeSet<>(Comparators.stringComparator);
				group.addAll(set);
				sortedSubEqs.add(group);
			}
			printEqClassGroups(sortedSubEqs);
			nextGroup.addAll(sortedSubEqs);

			_out.println(")");
		}

		switch (nextGroup.size())
		{
			case 0:
				break;
			case 1:
				printGroup(nextGroup);
				break;
			default:
				nextGroup.remove(_bottomEquivalents);
				printGroup(nextGroup);
				break;
		}
	}

	private void printEqClass(final Collection<T> concept)
	{
		final int size = concept.size();
		T c = null;

		switch (size)
		{
			case 0:
				_out.print("NIL");
				break;

			case 1:
				c = concept.iterator().next();
				printURI(c);
				break;

			default:
				_out.print("(");
				boolean first = true;
				for (final Iterator<T> i = concept.iterator(); i.hasNext();)
				{
					c = i.next();
					if (first)
						first = false;
					else
						_out.print(" ");
					printURI(c);
				}
				_out.print(")");
				break;
		}
	}

	private void printEqClassGroups(final Collection<? extends Collection<T>> concepts)
	{
		final int size = concepts.size();
		Collection<T> eqC = null;

		switch (size)
		{
			case 0:
				_out.print("NIL");
				break;

			case 1:
				eqC = concepts.iterator().next();
				_out.print("(");
				printEqClass(eqC);
				_out.print(")");
				break;

			default:
				_out.print("(");
				boolean first = true;
				for (final Iterator<? extends Collection<T>> i = concepts.iterator(); i.hasNext();)
				{
					eqC = i.next();
					if (first)
						first = false;
					else
						_out.print(" ");
					printEqClass(eqC);
				}
				_out.print(")");
				break;
		}
	}

	private void printURI(final T c)
	{
		String uri = c.toString();
		if (c.equals(ATermUtils.TOP))
			uri = "http://www.w3.org/2002/07/owl#Thing";
		else
			if (c.equals(ATermUtils.BOTTOM))
				uri = "http://www.w3.org/2002/07/owl#Nothing";

		_out.print(uri);
	}
}
