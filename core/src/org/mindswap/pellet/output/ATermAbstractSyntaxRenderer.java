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
public class ATermAbstractSyntaxRenderer extends ATermBaseRenderer implements ATermRenderer {

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitAnd(aterm.ATermAppl)
     */
    public void visitAnd(ATermAppl term) {
        out.print("intersectionOf(");
        visitList((ATermList) term.getArgument(0));
        out.print(")");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitOr(aterm.ATermAppl)
     */
    public void visitOr(ATermAppl term) {
        out.print("unionOf(");
        visitList((ATermList) term.getArgument(0));
        out.print(")");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitNot(aterm.ATermAppl)
     */
    public void visitNot(ATermAppl term) {
        out.print("complementOf(");
        visit((ATermAppl) term.getArgument(0));
        out.print(")");    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitSome(aterm.ATermAppl)
     */
    public void visitSome(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" someValuesFrom(");
        visit((ATermAppl) term.getArgument(1));
        out.print("))");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitAll(aterm.ATermAppl)
     */
    public void visitAll(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" allValuesFrom(");
        visit((ATermAppl) term.getArgument(1));
        out.print("))");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitMin(aterm.ATermAppl)
     */
    public void visitMin(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" minCardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
        out.print(")");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitMax(aterm.ATermAppl)
     */
    public void visitMax(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" maxCardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
        out.print(")");
    }
    
    public void visitCard(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" cardinality(" + ((ATermInt) term.getArgument(1)).getInt() + ")");
        out.print(")");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitLiteral(aterm.ATermAppl)
     */
    public void visitLiteral(ATermAppl lit) {
		String lexicalValue  = ((ATermAppl)lit.getArgument(0)).getName();
		String lang = ((ATermAppl)lit.getArgument(1)).getName();
		String datatypeURI = ((ATermAppl)lit.getArgument(2)).getName();
		
		out.print("\"" + lexicalValue + "\"");

        if(!lang.equals(""))
            out.print("@" + lang);
        else if(!datatypeURI.equals("")) {
            out.print("^^");
            out.print( datatypeURI );
        }
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitOneOf(aterm.ATermAppl)
     */
    public void visitOneOf(ATermAppl term) {
        out.print("oneOf(");
        ATermList list = (ATermList) term.getArgument(0);
		while (!list.isEmpty()) {
			ATermAppl value = (ATermAppl) list.getFirst();
			visit((ATermAppl) value.getArgument(0));
			list = list.getNext();
			if(!list.isEmpty())
				out.print(" ");
		}
        out.print(")");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitValue(aterm.ATermAppl)
     */
    public void visitHasValue(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" value(");
        ATermAppl value = (ATermAppl) ((ATermAppl) term.getArgument(1)).getArgument(0);
        if(value.getArity() == 0)
            visitTerm(value);
        else
            visitLiteral(value);
        out.print("))");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitValue(aterm.ATermAppl)
     */
    public void visitValue(ATermAppl term) {
        out.print("oneOf(");
        visit((ATermAppl) term.getArgument(0));
        out.print(")");
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitList(aterm.ATermAppl)
     */
    public void visitList(ATermList list) {
		while (!list.isEmpty()) {
			ATermAppl term = (ATermAppl) list.getFirst();
			visit(term);
			list = list.getNext();
			if(!list.isEmpty())
				out.print(" ");
		}
    }

    public void visitSelf(ATermAppl term) {
        out.print("restriction(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" self)");
    }

	public void visitSubClass(ATermAppl term) {
		out.print("SubClassOf(");
		visitList(term.getArguments());
		out.print(")");
	}
	
	public void visitInverse(ATermAppl p) {
		out.print( "Inv(" );
		visit( (ATermAppl) p.getArgument( 0 ) );
		out.print( ")" );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visitRestrictedDatatype(ATermAppl dt) {
        out.print("datatypeRestriction(");
        visit((ATermAppl) dt.getArgument(0));
        out.print(" ");
        ATermList list = (ATermList) dt.getArgument( 1 );
		while (!list.isEmpty()) {
			ATermAppl facet = (ATermAppl) list.getFirst();
			out.print("(");
			visit((ATermAppl) facet.getArgument( 0 ));
			out.print(" ");
			visit((ATermAppl) facet.getArgument( 1 ));
			out.print(")");			
			list = list.getNext();
			if(!list.isEmpty())
				out.print(" ");
		}
        out.print(" )");
	}
}
