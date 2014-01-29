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

import java.io.PrintWriter;
import java.io.Writer;

import org.mindswap.pellet.utils.URIUtils;

import aterm.ATermAppl;

/**
 * Base implementation of renderer interface to ease the implementation for different output
 * formats.
 * 
 * @author Evren Sirin
 */
public abstract class ATermBaseRenderer extends ATermBaseVisitor implements ATermRenderer {
    PrintWriter out;

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermRenderer#setWriter(org.mindswap.pellet.utils.OutputFormatter)
     */
    public void setWriter(PrintWriter out) {
        this.out = out;
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermRenderer#getWriter()
     */
    public PrintWriter getWriter() {
        return out;
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermRenderer#setWriter(java.io.Writer)
     */
    public void setWriter(Writer out) {
        this.out = new PrintWriter(out);
    }

    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitTerm(aterm.ATermAppl)
     */
    public void visitTerm(ATermAppl term) {
		out.print( URIUtils.getLocalName( term.getName() ));
    }
}
