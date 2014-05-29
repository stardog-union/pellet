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
import aterm.ATermList;

/**
 * A visitor interface specifically designed for structures in Pellet. Since the class descriptions
 * in Pellet are all represented as ATermAppl's with different functors any output (or conversion)
 * function will need to check functors in order to create a result. This interface defines the
 * functions for each different construct to make this process easier. A better implementation 
 * would actually integrate this functionality with the jjtraveler.Visitable interface.  
 * 
 * @author Evren Sirin
 */
public interface ATermVisitor {
    /**
     * Visit a generic term which may be a class expression, individual or a literal. 
     * 
     * @param term
     */
    public void visit(ATermAppl term);
    
    /**
     * Visit a primitive term (with no arguments) that stands for a URI. This URI may
     * belong to a class, a property, an individual or a datatype.
     * 
     * @param term
     */
    public void visitTerm(ATermAppl term);
    
    /**
     * Visit the 'and' (intersectionOf) term. 
     * 
     * @param term
     */
    public void visitAnd(ATermAppl term);

    /**
     * Visit the 'or' (unionOf) term. 
     * 
     * @param term
     */
    public void visitOr(ATermAppl term);
    
    /**
     * Visit the 'not' (complementOf) term. 
     * 
     * @param term
     */
    public void visitNot(ATermAppl term);
    
    /**
     * Visit the 'some' (someValuesFrom restriction) term. 
     * 
     * @param term
     */
    public void visitSome(ATermAppl term);
    
    /**
     * Visit the 'all' (allValuesFrom restriction) term. 
     * 
     * @param term
     */
    public void visitAll(ATermAppl term);
    
    /**
     * Visit the 'min' (minCardinality restriction) term. 
     * 
     * @param term
     */
    public void visitMin(ATermAppl term);
    
    /**
     * Visit the 'card' (minCardinality restriction) term. This is not a standard term that
     * ise used inside the reasoner but sometimes used for display purposes. Normally, cardinality
     * restrictions would be stored as a conjunction of min and max restrictions. 
     * 
     * @param term
     */
    public void visitCard(ATermAppl term);
    
    /**
     * Visit the 'max' (maxCardinality restriction) term. 
     * 
     * @param term
     */
    public void visitMax(ATermAppl term);
    
    /**
     * Visit the hasValue restriction term. This term is in the form
     * some(property,value(individual)) or 
     * some(property,value(literal))
     * 
     * @param term
     */
    public void visitHasValue(ATermAppl term);
    
    /**
     * Visit the nominal term. This term is in the form
     * some(property,value(individual)) 
     * 
     * @param term
     */
    public void visitValue(ATermAppl term);
    
    /**
     * Visit the 'oneOf' term. This term is in the form
     * or([value(i1),value(i2),...,value(i3)] where i's
     * are individuals or literal constants 
     * 
     * @param term
     */    
    public void visitOneOf(ATermAppl term);
    
    /**
     * Visit the literal term. The literals are in the form
     * literal(lexicalValue, language, datatypeURI) 
     * 
     * @param term
     */
    public void visitLiteral(ATermAppl term);
    
    /**
     * Visit the list structure. Lists are found in 'and' and 'or'
     * terms.
     * 
     * @param term
     */
    public void visitList(ATermList term);
    
    /**
     * Visit the self restriction term. This is in the form self(p).
     * 
     * @param term
     */
    public void visitSelf(ATermAppl term);
    
    public void visitInverse(ATermAppl p);
    
    public void visitRestrictedDatatype(ATermAppl dt);
}
