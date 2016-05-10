package com.clarkparsia.pellet.datatypes;

import aterm.ATermAppl;
import aterm.ATermList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Named Data Range Expander
 * </p>
 * <p>
 * Description: Substitutes one {@link ATermAppl} for another in a _data range description, based on input map. Used to implement OWL 2 datatype definitions.
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
public class NamedDataRangeExpander extends ATermBaseVisitor
{

	private Map<ATermAppl, ATermAppl> map;
	private ATermAppl ret;
	private boolean change;

	/*
	 * TODO: Handle nesting and cycles in definitions
	 */
	public ATermAppl expand(final ATermAppl input, final Map<ATermAppl, ATermAppl> map)
	{
		if (map.isEmpty())
			return input;

		this.map = map;
		try
		{
			this.visit(input);
		}
		catch (final UnsupportedOperationException e)
		{
			throw new IllegalArgumentException(e);
		}
		return ret;
	}

	@Override
	public void visitAll(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitAnd(final ATermAppl term)
	{
		boolean listChange = false;
		final List<ATermAppl> args = new ArrayList<>();
		for (ATermList l = (ATermList) term.getArgument(0); !l.isEmpty(); l = l.getNext())
		{
			final ATermAppl a = (ATermAppl) l.getFirst();
			this.visit(a);
			args.add(ret);
			if (change)
				listChange = true;
		}
		if (listChange)
		{
			change = true;
			ret = ATermUtils.makeAnd(ATermUtils.makeList(args));
		}
		else
		{
			change = false;
			ret = term;
		}
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitHasValue(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitInverse(final ATermAppl p)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitLiteral(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitMax(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitMin(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitNot(final ATermAppl term)
	{
		final ATermAppl a = (ATermAppl) term.getArgument(0);
		this.visit(a);
		if (change)
			ret = ATermUtils.makeNot(ret);
		else
			ret = term;
	}

	@Override
	public void visitOneOf(final ATermAppl term)
	{
		ret = term;
		change = false;
	}

	@Override
	public void visitOr(final ATermAppl term)
	{
		boolean listChange = false;
		final List<ATermAppl> args = new ArrayList<>();
		for (ATermList l = (ATermList) term.getArgument(0); !l.isEmpty(); l = l.getNext())
		{
			final ATermAppl a = (ATermAppl) l.getFirst();
			this.visit(a);
			args.add(ret);
			if (change)
				listChange = true;
		}
		if (listChange)
		{
			change = true;
			ret = ATermUtils.makeOr(ATermUtils.makeList(args));
		}
		else
		{
			change = false;
			ret = term;
		}
	}

	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		ret = dt;
		change = false;
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitSome(final ATermAppl term)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitTerm(final ATermAppl term)
	{
		final ATermAppl a = map.get(term);
		if (a == null)
		{
			ret = term;
			change = false;
		}
		else
		{
			ret = a;
			change = true;
		}
	}

	@Override
	public void visitValue(final ATermAppl term)
	{
		ret = term;
		change = false;
	}
}
