package com.clarkparsia.pellet.datatypes;

import static org.mindswap.pellet.utils.ATermUtils.ANDFUN;
import static org.mindswap.pellet.utils.ATermUtils.ORFUN;
import static org.mindswap.pellet.utils.ATermUtils.isAnd;
import static org.mindswap.pellet.utils.ATermUtils.isOr;
import static org.mindswap.pellet.utils.ATermUtils.makeAnd;
import static org.mindswap.pellet.utils.ATermUtils.makeOr;
import static org.mindswap.pellet.utils.ATermUtils.nnf;
import static org.mindswap.pellet.utils.ATermUtils.toSet;

import java.util.ArrayList;
import java.util.List;
import openllet.aterm.AFun;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermList;
import org.mindswap.pellet.utils.iterator.MultiListIterator;

/**
 * <p>
 * Title: Disjunction Normal Form
 * </p>
 * <p>
 * Description: Static implementation to translate ATermAppl descriptions of complex _data ranges to _disjunction normal form
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class DNF
{

	/**
	 * Get disjunctive normal form for an expression
	 *
	 * @param term The expression
	 * @return <code>term</code> in DNF
	 */
	public static ATermAppl dnf(final ATermAppl term)
	{
		return dnfFromNnf(nnf(term));
	}

	/**
	 * Internal method that assumes input is NNF
	 *
	 * @param term A NNF expression
	 * @return <code>term</code> in DNF
	 */
	private static ATermAppl dnfFromNnf(final ATermAppl term)
	{
		/*
		 * TODO: Avoid processing DataOneOf when forcing into NNF
		 */

		ATermAppl dnf;

		final AFun fun = term.getAFun();

		/*
		 * If the term is a conjunction, each conjunct must be converted to dnf
		 * and then element-wise distributed.
		 */
		if (ANDFUN.equals(fun))
		{

			/*
			 * Step 1: the input conjunction may have conjunctions as arguments.
			 * After this step, <code>conjuncts</code> is the flattened list of
			 * conjuncts, each in DNF
			 */
			final ATermList rootConjuncts = (ATermList) term.getArgument(0);
			final List<ATermAppl> conjuncts = new ArrayList<>();
			final MultiListIterator i = new MultiListIterator(rootConjuncts);
			while (i.hasNext())
			{
				final ATermAppl a = i.next();
				if (isAnd(a))
					i.append((ATermList) a.getArgument(0));
				else
				{
					final ATermAppl dnfA = dnfFromNnf(a);
					conjuncts.add(dnfA);
				}
			}

			/*
			 * Step 2: element-wise distribute any _disjunction among the
			 * conjuncts.
			 */
			List<ATermAppl> disjuncts = new ArrayList<>();
			for (final ATermAppl a : conjuncts)
				if (disjuncts.isEmpty())
					addToList(a, isOr(a), disjuncts);
				else
				{
					final List<ATermAppl> thisArgs = new ArrayList<>();
					final List<ATermAppl> newDisjuncts = new ArrayList<>();
					addToList(a, isOr(a), thisArgs);

					for (final ATermAppl a1 : thisArgs)
						for (final ATermAppl b : disjuncts)
						{
							final List<ATermAppl> list = new ArrayList<>();
							addToList(a1, isAnd(a1), list);
							addToList(b, isAnd(b), list);
							newDisjuncts.add(makeAnd(toSet(list)));
						}
					disjuncts = newDisjuncts;
				}

			dnf = makeOr(toSet(disjuncts));

		}
		/*
		 * If the term is a _disjunction merge each element into DNF
		 */
		else
			if (ORFUN.equals(fun))
			{
				final ATermList disjuncts = (ATermList) term.getArgument(0);
				final MultiListIterator i = new MultiListIterator(disjuncts);
				final List<ATermAppl> args = new ArrayList<>();
				while (i.hasNext())
				{
					final ATermAppl a = i.next();
					if (isOr(a))
						i.append((ATermList) a.getArgument(0));
					else
						args.add(dnfFromNnf(a));
				}
				dnf = makeOr(toSet(args));
			}
			/*
			 * If the term is not a conjunction or _disjunction (and its in NNF), it
			 * is already in DNF
			 */
			else
				dnf = term;

		return dnf;
	}

	private static void addToList(final ATermAppl term, final boolean flatten, final List<ATermAppl> result)
	{
		if (flatten)
			for (ATermList l = (ATermList) term.getArgument(0); !l.isEmpty(); l = l.getNext())
				result.add((ATermAppl) l.getFirst());
		else
			result.add(term);
	}

}
