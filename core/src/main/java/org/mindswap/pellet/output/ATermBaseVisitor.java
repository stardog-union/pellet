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

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;

/**
 * Base implementation for ATermVisitor. Implements the basic visit function that calls
 * the other functions based on the functor.
 * 
 * @author Evren Sirin
 */
public abstract class ATermBaseVisitor implements ATermVisitor {
    public static final ATermAppl OWL_THING   = ATermUtils.makeTermAppl(Namespaces.OWL + "Thing");
    public static final ATermAppl OWL_NOTHING = ATermUtils.makeTermAppl(Namespaces.OWL + "Nothing");
    
    public void visit(ATermAppl term) {
        AFun af = term.getAFun();
        
        if(term.equals(ATermUtils.TOP)) {
            visitTerm(OWL_THING);
        }
        else if(term.equals(ATermUtils.BOTTOM)) {
            visitTerm(OWL_NOTHING);
        }
        else if(af.getArity() == 0) {
		    visitTerm(term);
		}
        else if(af.equals(ATermUtils.BNODE_FUN)) {
		    visitTerm(term);
		}
		else if (af.equals(ATermUtils.ANDFUN)) {
		    visitAnd(term);
		}
		else if (af.equals(ATermUtils.ORFUN)) {
		    if(ATermUtils.isOneOf(term))
		        visitOneOf(term);
		    else
		        visitOr(term);
		}
		else if (af.equals(ATermUtils.NOTFUN)) {
		    visitNot(term);
		}
		else if (af.equals(ATermUtils.ALLFUN)) {
		    visitAll(term);
		}
		else if (af.equals(ATermUtils.SOMEFUN)) {
		    if(ATermUtils.isHasValue(term))
		        visitHasValue(term);
		    else
		        visitSome(term);
		}
		else if (af.equals(ATermUtils.MINFUN)) {
		    visitMin(term);
		}
		else if(af.equals(ATermUtils.MAXFUN)) {		
		    visitMax(term);
		}
		else if(af.equals(ATermUtils.CARDFUN)) {		
		    visitCard(term);
		}
		else if(af.equals(ATermUtils.VALUEFUN)) {		
		    visitValue(term);
		}
		else if(af.equals(ATermUtils.LITFUN)) {		
		    visitLiteral(term);
		} 
        else if(af.equals(ATermUtils.SELFFUN)) {     
            visitSelf(term);
        } 
        else if(af.equals(ATermUtils.INVFUN)) {     
            visitInverse(term);
        } 
        else if(af.equals(ATermUtils.RESTRDATATYPEFUN)) {
        	visitRestrictedDatatype(term);
        }
		else {
		    throw new InternalReasonerException("Invalid term " + term);		    
		}
	}
 
    /* (non-Javadoc)
     * @see org.mindswap.pellet.utils.ATermVisitor#visitList(aterm.ATermAppl)
     */
    public void visitList(ATermList list) {
		while (!list.isEmpty()) {
			ATermAppl term = (ATermAppl) list.getFirst();
			visit(term);
			list = list.getNext();
		}
    }
}
