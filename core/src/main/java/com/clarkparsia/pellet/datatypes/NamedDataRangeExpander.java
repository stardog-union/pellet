package com.clarkparsia.pellet.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermList;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Named Data Range Expander
 * </p>
 * <p>
 * Description: Substitutes one {@link ATermAppl} for another in a _data range description, based on input _map. Used to implement OWL 2 datatype definitions.
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

	private Map<ATermAppl, ATermAppl> _map;
	private ATermAppl _ret;
	private boolean _change;

	/*
	 * TODO: Handle nesting and cycles in definitions
	 */
	public ATermAppl expand(final ATermAppl input, final Map<ATermAppl, ATermAppl> map)
	{
		if (map.isEmpty())
			return input;

		this._map = map;
		try
		{
			this.visit(input);
		}
		catch (final UnsupportedOperationException e)
		{
			throw new IllegalArgumentException(e);
		}
		return _ret;
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
			args.add(_ret);
			if (_change)
				listChange = true;
		}
		if (listChange)
		{
			_change = true;
			_ret = ATermUtils.makeAnd(ATermUtils.makeList(args));
		}
		else
		{
			_change = false;
			_ret = term;
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
		if (_change)
			_ret = ATermUtils.makeNot(_ret);
		else
			_ret = term;
	}

	@Override
	public void visitOneOf(final ATermAppl term)
	{
		_ret = term;
		_change = false;
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
			args.add(_ret);
			if (_change)
				listChange = true;
		}
		if (listChange)
		{
			_change = true;
			_ret = ATermUtils.makeOr(ATermUtils.makeList(args));
		}
		else
		{
			_change = false;
			_ret = term;
		}
	}

	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		_ret = dt;
		_change = false;
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
		final ATermAppl a = _map.get(term);
		if (a == null)
		{
			_ret = term;
			_change = false;
		}
		else
		{
			_ret = a;
			_change = true;
		}
	}

	@Override
	public void visitValue(final ATermAppl term)
	{
		_ret = term;
		_change = false;
	}
}
