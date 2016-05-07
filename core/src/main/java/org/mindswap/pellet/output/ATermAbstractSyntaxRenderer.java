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

package org.mindswap.pellet.output;

import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;

/**
 * A simple implementation to output the terms in OWL abstract syntax.
 *
 * @author Evren Sirin
 */
public class ATermAbstractSyntaxRenderer extends ATermBaseRenderer
{

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitAnd(aterm.ATermAppl)
	 */
	@Override
	public void visitAnd(final ATermAppl term)
	{
		out.print("intersectionOf(");
		visitList((ATermList) term.getArgument(0));
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitOr(aterm.ATermAppl)
	 */
	@Override
	public void visitOr(final ATermAppl term)
	{
		out.print("unionOf(");
		visitList((ATermList) term.getArgument(0));
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitNot(aterm.ATermAppl)
	 */
	@Override
	public void visitNot(final ATermAppl term)
	{
		out.print("complementOf(");
		visit((ATermAppl) term.getArgument(0));
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitSome(aterm.ATermAppl)
	 */
	@Override
	public void visitSome(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" someValuesFrom(");
		visit((ATermAppl) term.getArgument(1));
		out.print("))");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitAll(aterm.ATermAppl)
	 */
	@Override
	public void visitAll(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" allValuesFrom(");
		visit((ATermAppl) term.getArgument(1));
		out.print("))");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitMin(aterm.ATermAppl)
	 */
	@Override
	public void visitMin(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" minCardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitMax(aterm.ATermAppl)
	 */
	@Override
	public void visitMax(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" maxCardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
		out.print(")");
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" cardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitLiteral(aterm.ATermAppl)
	 */
	@Override
	public void visitLiteral(final ATermAppl lit)
	{
		final String lexicalValue = ((ATermAppl) lit.getArgument(0)).getName();
		final String lang = ((ATermAppl) lit.getArgument(1)).getName();
		final String datatypeURI = ((ATermAppl) lit.getArgument(2)).getName();

		out.print("\"" + lexicalValue + "\"");

		if (!lang.equals(""))
			out.print("@" + lang);
		else
			if (!datatypeURI.equals(""))
			{
				out.print("^^");
				out.print(datatypeURI);
			}
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitOneOf(aterm.ATermAppl)
	 */
	@Override
	public void visitOneOf(final ATermAppl term)
	{
		out.print("oneOf(");
		ATermList list = (ATermList) term.getArgument(0);
		while (!list.isEmpty())
		{
			final ATermAppl value = (ATermAppl) list.getFirst();
			visit((ATermAppl) value.getArgument(0));
			list = list.getNext();
			if (!list.isEmpty())
				out.print(" ");
		}
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitValue(aterm.ATermAppl)
	 */
	@Override
	public void visitHasValue(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" value(");
		final ATermAppl value = (ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0);
		if (value.getArity() == 0)
			visitTerm(value);
		else
			visitLiteral(value);
		out.print("))");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitValue(aterm.ATermAppl)
	 */
	@Override
	public void visitValue(final ATermAppl term)
	{
		out.print("oneOf(");
		visit((ATermAppl) term.getArgument(0));
		out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitList(aterm.ATermAppl)
	 */
	@Override
	public void visitList(ATermList list)
	{
		while (!list.isEmpty())
		{
			final ATermAppl term = (ATermAppl) list.getFirst();
			visit(term);
			list = list.getNext();
			if (!list.isEmpty())
				out.print(" ");
		}
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		out.print(" self)");
	}

	public void visitSubClass(final ATermAppl term)
	{
		out.print("SubClassOf(");
		visitList(term.getArguments());
		out.print(")");
	}

	@Override
	public void visitInverse(final ATermAppl p)
	{
		out.print("Inv(");
		visit((ATermAppl) p.getArgument(0));
		out.print(")");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		out.print("datatypeRestriction(");
		visit((ATermAppl) dt.getArgument(0));
		out.print(" ");
		ATermList list = (ATermList) dt.getArgument(1);
		while (!list.isEmpty())
		{
			final ATermAppl facet = (ATermAppl) list.getFirst();
			out.print("(");
			visit((ATermAppl) facet.getArgument(0));
			out.print(" ");
			visit((ATermAppl) facet.getArgument(1));
			out.print(")");
			list = list.getNext();
			if (!list.isEmpty())
				out.print(" ");
		}
		out.print(" )");
	}
}
