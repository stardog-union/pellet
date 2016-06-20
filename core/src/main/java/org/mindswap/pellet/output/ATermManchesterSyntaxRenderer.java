package org.mindswap.pellet.output;

import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.types.floating.XSDFloat;
import com.clarkparsia.pellet.datatypes.types.real.XSDDecimal;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;
import java.util.HashMap;
import java.util.Map;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermInt;
import openllet.aterm.ATermList;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: ATermManchesterSyntaxRenderer
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Markus Stocker
 */
public class ATermManchesterSyntaxRenderer extends ATermBaseRenderer
{
	public static final Map<ATermAppl, String> FACETS;
	static
	{
		FACETS = new HashMap<>();
		FACETS.put(Facet.XSD.LENGTH.getName(), "length");
		FACETS.put(Facet.XSD.MIN_LENGTH.getName(), "minLength");
		FACETS.put(Facet.XSD.MAX_LENGTH.getName(), "maxLength");
		FACETS.put(Facet.XSD.PATTERN.getName(), "pattern");
		FACETS.put(Facet.XSD.MIN_INCLUSIVE.getName(), ">=");
		FACETS.put(Facet.XSD.MIN_EXCLUSIVE.getName(), ">");
		FACETS.put(Facet.XSD.MAX_INCLUSIVE.getName(), "<=");
		FACETS.put(Facet.XSD.MAX_EXCLUSIVE.getName(), "<");
	}

	@Override
	public void visitAll(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" only ");
		visit((ATermAppl) term.getArgument(1));
		_out.print(")");
	}

	@Override
	public void visitAnd(final ATermAppl term)
	{
		_out.print("(");
		visitList((ATermList) term.getArgument(0), "and");
		_out.print(")");
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" exactly " + ((ATermInt) term.getArgument(1)).getInt());
		_out.print(")");
	}

	@Override
	public void visitHasValue(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" value ");
		final ATermAppl value = (ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0);
		if (value.getArity() == 0)
			visitTerm(value);
		else
			visitLiteral(value);
		_out.print(")");
	}

	@Override
	public void visitInverse(final ATermAppl p)
	{
		_out.print("inverse ");
		visit((ATermAppl) p.getArgument(0));
	}

	@Override
	public void visitLiteral(final ATermAppl term)
	{
		final ATermAppl lexicalValue = (ATermAppl) term.getArgument(ATermUtils.LIT_VAL_INDEX);
		final ATermAppl lang = (ATermAppl) term.getArgument(ATermUtils.LIT_LANG_INDEX);
		final ATermAppl datatypeURI = (ATermAppl) term.getArgument(ATermUtils.LIT_URI_INDEX);

		if (datatypeURI.equals(XSDInteger.getInstance().getName()) || datatypeURI.equals(XSDDecimal.getInstance().getName()))
			_out.print(lexicalValue.getName());
		else
			if (datatypeURI.equals(XSDFloat.getInstance().getName()))
			{
				_out.print(lexicalValue.getName());
				_out.print("f");
			}
			else
				if (!datatypeURI.equals(ATermUtils.PLAIN_LITERAL_DATATYPE))
				{
					_out.print(lexicalValue.getName());
					_out.print("^^");
					_out.print(datatypeURI.getName());
				}
				else
				{
					_out.print("\"" + lexicalValue.getName() + "\"");

					if (!lang.equals(ATermUtils.EMPTY))
						_out.print("@" + lang);
				}
	}

	@Override
	public void visitMax(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" max " + ((ATermInt) term.getArgument(1)).getInt() + " ");
		visit((ATermAppl) term.getArgument(2));
		_out.print(")");
	}

	@Override
	public void visitMin(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" min " + ((ATermInt) term.getArgument(1)).getInt() + " ");
		visit((ATermAppl) term.getArgument(2));
		_out.print(")");
	}

	@Override
	public void visitNot(final ATermAppl term)
	{
		_out.print("not ");
		visit((ATermAppl) term.getArgument(0));
	}

	@Override
	public void visitOneOf(final ATermAppl term)
	{
		_out.print("{");
		ATermList list = (ATermList) term.getArgument(0);
		while (!list.isEmpty())
		{
			final ATermAppl value = (ATermAppl) list.getFirst();
			visit((ATermAppl) value.getArgument(0));
			list = list.getNext();
			if (!list.isEmpty())
				_out.print(" ");
		}
		_out.print("}");
	}

	@Override
	public void visitOr(final ATermAppl term)
	{
		_out.print("(");
		visitList((ATermList) term.getArgument(0), "or");
		_out.print(")");
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" Self)");
	}

	@Override
	public void visitSome(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" some ");
		visit((ATermAppl) term.getArgument(1));
		_out.print(")");
	}

	@Override
	public void visitValue(final ATermAppl term)
	{
		_out.print("(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(")");
	}

	public void visitList(ATermList list, final String op)
	{
		while (!list.isEmpty())
		{
			final ATermAppl term = (ATermAppl) list.getFirst();
			visit(term);
			list = list.getNext();
			if (!list.isEmpty())
				_out.print(" " + op + " ");
		}
	}

	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		_out.print("");
		visit((ATermAppl) dt.getArgument(0));
		_out.print("[");
		ATermList list = (ATermList) dt.getArgument(1);
		while (!list.isEmpty())
		{
			final ATermAppl facet = (ATermAppl) list.getFirst();
			_out.print(FACETS.get(facet.getArgument(0)));
			_out.print(" ");
			visit((ATermAppl) facet.getArgument(1));
			list = list.getNext();
			if (!list.isEmpty())
				_out.print(", ");
		}
		_out.print("]");
	}
}
