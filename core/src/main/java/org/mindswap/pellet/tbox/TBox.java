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

package org.mindswap.pellet.tbox;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.tbox.impl.Unfolding;

import aterm.ATermAppl;

public interface TBox {
	/**
	 * Add a named class declaration
	 * 
	 * @return <code>true</code> if TBox changed as a result of this call
	 */
	public boolean addClass( ATermAppl term );

	/**
	 * Return all the named classes 
	 */
	public Set<ATermAppl> getClasses();
	
	/**
	 * Return all the named classes plus TOP and BOTTOM
	 */
	public Set<ATermAppl> getAllClasses();

	/**
	 * Return all the axioms defined in this TBox (may include new axioms introduced during absorption)
	 */
	public Collection<ATermAppl> getAxioms();

	/**
	 * Return all the asserted axioms in this TBox
	 */
	public Collection<ATermAppl> getAssertedAxioms();

	/**
	 * Return all the sub and equivalent class axioms that have 
	 * the given concept on the left hand side
	 */
	public Collection<ATermAppl> getAxioms( ATermAppl concept );

	/**
	 * Lazy unfold the given concept
	 *  
	 * @param c
	 * @return
	 */
	public Iterator<Unfolding> unfold( ATermAppl c );

	/**
	 * Returns if a concept has only primitive definitions in this TBox. Only
	 * primitive definitions mean the concept did not have any equivalents
	 * defined or all equivalence axioms has been absorbed into primitive
	 * definitions. This function returns <code>false</code> for complex class
	 * expressions.
	 * 
	 * @param c
	 *            a concept (named concept or a concept expression)
	 * @return <code>true</code> if the concept is not complex and has only
	 *         primitive definitions
	 */
	public boolean isPrimitive( ATermAppl c );	

	/**
	 * Add a TBox axiom.
	 * 
	 * @param axiom
	 * @return
	 */
	public boolean addAxiom(ATermAppl axiom);
	
	/**
	 * Remove {@code axiom} from TBox and all other axioms that depend on it. An
	 * axiom depends on another axiom if it is a syntactic transformation (as in
	 * disjoint axiom is transformed into subclass) or it is obtained via
	 * absorption (as equivalent class axioms are absorbed into subclass
	 * axioms). This method is syntactic sugar for
	 * {@link #removeAxiom(ATermAppl, ATermAppl)} where both parameters are
	 * {@code axiom}.
	 * 
	 * @param axiom
	 * @return
	 */
	public boolean removeAxiom(ATermAppl axiom);

	/**
	 * Remove all explanations for {@code dependantAxiom} that contain
	 * {@code explanationAxiom}. If no explanations remain,
	 * {@code dependantAxiom} is removed and all axioms which depend on it are
	 * updated (and will be removed if they have no additional explanations).
	 * 
	 * @param dependantAxiom
	 * @param explanationAxiom
	 * @return
	 */
	public boolean removeAxiom(ATermAppl dependantAxiom, ATermAppl explanationAxiom);
	
	/**
	 * Return a single explanation for the given TBox axiom.
	 * 
	 * @param axiom
	 * @return
	 */
	public Set<ATermAppl> getAxiomExplanation(ATermAppl axiom);
	
	/**
	 * Return multiple explanations for the given TBox axiom.
	 * 
	 * @param axiom
	 * @return
	 */
	public Set<Set<ATermAppl>> getAxiomExplanations(ATermAppl axiom);	

	/**
	 * Make any preparation necessary before reasoning.
	 */
	public void prepare();
}
