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

import openllet.aterm.ATermAppl;
import openllet.aterm.ATermInt;
import openllet.aterm.ATermList;

/**
 * A simple implementation to output the terms in OWL abstract syntax.
 *
 * @author Evren Sirin
 */
public class ATermAbstractSyntaxRenderer extends ATermBaseRenderer
{

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitAnd(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitAnd(final ATermAppl term)
	{
		_out.print("intersectionOf(");
		visitList((ATermList) term.getArgument(0));
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitOr(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitOr(final ATermAppl term)
	{
		_out.print("unionOf(");
		visitList((ATermList) term.getArgument(0));
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitNot(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitNot(final ATermAppl term)
	{
		_out.print("complementOf(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitSome(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitSome(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" someValuesFrom(");
		visit((ATermAppl) term.getArgument(1));
		_out.print("))");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitAll(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitAll(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" allValuesFrom(");
		visit((ATermAppl) term.getArgument(1));
		_out.print("))");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitMin(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitMin(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" minCardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitMax(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitMax(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" maxCardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
		_out.print(")");
	}

	@Override
	public void visitCard(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" cardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitLiteral(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitLiteral(final ATermAppl lit)
	{
		final String lexicalValue = ((ATermAppl) lit.getArgument(0)).getName();
		final String lang = ((ATermAppl) lit.getArgument(1)).getName();
		final String datatypeURI = ((ATermAppl) lit.getArgument(2)).getName();

		_out.print("\"" + lexicalValue + "\"");

		if (!lang.equals(""))
			_out.print("@" + lang);
		else
			if (!datatypeURI.equals(""))
			{
				_out.print("^^");
				_out.print(datatypeURI);
			}
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitOneOf(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitOneOf(final ATermAppl term)
	{
		_out.print("oneOf(");
		ATermList list = (ATermList) term.getArgument(0);
		while (!list.isEmpty())
		{
			final ATermAppl value = (ATermAppl) list.getFirst();
			visit((ATermAppl) value.getArgument(0));
			list = list.getNext();
			if (!list.isEmpty())
				_out.print(" ");
		}
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitValue(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitHasValue(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" value(");
		final ATermAppl value = (ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0);
		if (value.getArity() == 0)
			visitTerm(value);
		else
			visitLiteral(value);
		_out.print("))");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitValue(openllet.aterm.ATermAppl)
	 */
	@Override
	public void visitValue(final ATermAppl term)
	{
		_out.print("oneOf(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(")");
	}

	/* (non-Javadoc)
	 * @see org.mindswap.pellet.utils.ATermVisitor#visitList(openllet.aterm.ATermAppl)
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
				_out.print(" ");
		}
	}

	@Override
	public void visitSelf(final ATermAppl term)
	{
		_out.print("restriction(");
		visit((ATermAppl) term.getArgument(0));
		_out.print(" self)");
	}

	public void visitSubClass(final ATermAppl term)
	{
		_out.print("SubClassOf(");
		visitList(term.getArguments());
		_out.print(")");
	}

	@Override
	public void visitInverse(final ATermAppl p)
	{
		_out.print("Inv(");
		visit((ATermAppl) p.getArgument(0));
		_out.print(")");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRestrictedDatatype(final ATermAppl dt)
	{
		_out.print("datatypeRestriction(");
		visit((ATermAppl) dt.getArgument(0));
		_out.print(" ");
		ATermList list = (ATermList) dt.getArgument(1);
		while (!list.isEmpty())
		{
			final ATermAppl facet = (ATermAppl) list.getFirst();
			_out.print("(");
			visit((ATermAppl) facet.getArgument(0));
			_out.print(" ");
			visit((ATermAppl) facet.getArgument(1));
			_out.print(")");
			list = list.getNext();
			if (!list.isEmpty())
				_out.print(" ");
		}
		_out.print(" )");
	}
}
