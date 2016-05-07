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
 * Description: The output of this printer is "functional" in the sense that any taxonomy has only a single printed form. I.e., the output here is intended to
 * be unchanged by reorderings of sibling _nodes in the classification algorithm. It was developed as a way to compare the output of alternative classification
 * implementations. It is based on the format found in the DL benchmark test data.
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

	private Taxonomy<T> taxonomy;

	private PrintWriter out;

	private Set<T> bottomEquivalents;

	private Set<T> printed;

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

		this.taxonomy = taxonomy;
		this.out = out;

		/*
		 * Note the bottom class (and equivalents) b/c it should only be output
		 * as a subclass if it is the *only* subclass.
		 */
		bottomEquivalents = new TreeSet<>(Comparators.stringComparator);
		bottomEquivalents.addAll(taxonomy.getBottom().getEquivalents());

		printed = new HashSet<>();

		out.println();

		final Set<T> sortedTop = new TreeSet<>(Comparators.stringComparator);
		sortedTop.addAll(taxonomy.getTop().getEquivalents());

		final Set<Set<T>> topGroup = Collections.singleton(sortedTop);
		printGroup(topGroup);

		this.taxonomy = null;
		this.out = null;
		this.bottomEquivalents = null;
		this.printed = null;

		out.println();
		out.flush();
	}

	private void printGroup(final Collection<? extends Collection<T>> concepts)
	{

		final Set<Set<T>> nextGroup = new LinkedHashSet<>();

		for (Collection<T> eqC : concepts)
		{

			final T firstC = eqC.iterator().next();

			// Use supers to determine if this has been printed before, if so
			// skip it
			final Set<Set<T>> supEqs = taxonomy.getSupers(firstC, true);
			if ((supEqs.size() > 1) && printed.contains(firstC))
				continue;
			else
				printed.add(firstC);

			out.print("(");

			// Print equivalent class group passed in (assume sorted)
			printEqClass(eqC);

			out.print(" ");

			// Print any direct superclasses
			final Set<Set<T>> sortedSupEqs = new TreeSet<>(Comparators.stringComparator);
			for (final Set<T> set : supEqs)
			{
				final Set<T> group = new TreeSet<>(Comparators.stringComparator);
				group.addAll(set);
				sortedSupEqs.add(group);
			}
			printEqClassGroups(sortedSupEqs);

			out.print(" ");

			// Print any direct subclasses
			final Set<Set<T>> sortedSubEqs = new TreeSet<>(Comparators.stringComparator);
			final Set<Set<T>> subEqs = taxonomy.getSubs(firstC, true);
			for (final Set<T> set : subEqs)
			{
				final Set<T> group = new TreeSet<>(Comparators.stringComparator);
				group.addAll(set);
				sortedSubEqs.add(group);
			}
			printEqClassGroups(sortedSubEqs);
			nextGroup.addAll(sortedSubEqs);

			out.println(")");
		}

		switch (nextGroup.size())
		{
			case 0:
				break;
			case 1:
				printGroup(nextGroup);
				break;
			default:
				nextGroup.remove(bottomEquivalents);
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
				out.print("NIL");
				break;

			case 1:
				c = concept.iterator().next();
				printURI(c);
				break;

			default:
				out.print("(");
				boolean first = true;
				for (final Iterator<T> i = concept.iterator(); i.hasNext();)
				{
					c = i.next();
					if (first)
						first = false;
					else
						out.print(" ");
					printURI(c);
				}
				out.print(")");
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
				out.print("NIL");
				break;

			case 1:
				eqC = concepts.iterator().next();
				out.print("(");
				printEqClass(eqC);
				out.print(")");
				break;

			default:
				out.print("(");
				boolean first = true;
				for (final Iterator<? extends Collection<T>> i = concepts.iterator(); i.hasNext();)
				{
					eqC = i.next();
					if (first)
						first = false;
					else
						out.print(" ");
					printEqClass(eqC);
				}
				out.print(")");
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

		out.print(uri);
	}
}
