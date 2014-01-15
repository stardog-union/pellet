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

package org.mindswap.pellet.tbox.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.AFun;
import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * @author Evren Sirin
 */
public class TermDefinition {
	private List<ATermAppl>	subClassAxioms;
	private List<ATermAppl>	eqClassAxioms;
	private Set<ATermAppl>	dependencies;

	public TermDefinition() {
		subClassAxioms = new ArrayList<ATermAppl>();
		eqClassAxioms = new ArrayList<ATermAppl>();
		updateDependencies();
	}
	
	public Set<ATermAppl> getDependencies() {
		if (dependencies == null) updateDependencies(); 
		return dependencies;
	}
	
	public void clearDependencies() {
		dependencies = null;
	}

	public ATermAppl getName() {
		if( !subClassAxioms.isEmpty() )
			return (ATermAppl) subClassAxioms.get( 0 ).getArgument( 0 );

		if( !eqClassAxioms.isEmpty() )
			return (ATermAppl) eqClassAxioms.get( 0 ).getArgument( 0 );

		return null;
	}

	public boolean addDef(ATermAppl appl) {
		boolean added = false;		
		
		AFun fun = appl.getAFun();
		if( fun.equals( ATermUtils.SUBFUN ) ) {
			added = subClassAxioms.contains(appl) ? false : subClassAxioms.add( appl );
		}
		else if( fun.equals( ATermUtils.EQCLASSFUN ) ) {
			added = eqClassAxioms.contains(appl) ? false : eqClassAxioms.add( appl );
		}
		else {
			throw new RuntimeException( "Cannot add non-definition!" );
		}
		
		if( added )
			updateDependencies();
		
		return added;
	}

	public boolean removeDef(ATermAppl axiom) {
		boolean removed;

		AFun fun = axiom.getAFun();
		if( fun.equals( ATermUtils.SUBFUN ) ) {
			removed = subClassAxioms.remove( axiom );
		}
		else if( fun.equals( ATermUtils.EQCLASSFUN ) ) {
			removed = eqClassAxioms.remove( axiom );
		}
		else {
			throw new RuntimeException( "Cannot remove non-definition!" );
		}

		updateDependencies();

		return removed;
	}

	public boolean isPrimitive() {
		return eqClassAxioms.isEmpty();
	}

	public boolean isUnique() {
		return eqClassAxioms.isEmpty()
			|| (subClassAxioms.isEmpty() && eqClassAxioms.size() == 1 );
	}
	
	public boolean isUnique(ATermAppl axiom) {
		return eqClassAxioms.isEmpty()
			&& (subClassAxioms.isEmpty() || axiom.getAFun().equals( ATermUtils.SUBFUN ));
	}

	public List<ATermAppl> getSubClassAxioms() {
		return subClassAxioms;
	}

	public List<ATermAppl> getEqClassAxioms() {
		return eqClassAxioms;
	}

	@Override
	public String toString() {
		return subClassAxioms + "; " + eqClassAxioms;
	}

	protected void updateDependencies() {
		dependencies = CollectionUtils.makeIdentitySet();
		for( ATermAppl sub : getSubClassAxioms() ) {
			ATermUtils.findPrimitives( (ATermAppl) sub.getArgument( 1 ), dependencies );
		}
		for( ATermAppl eq : getEqClassAxioms() ) {
			ATermUtils.findPrimitives( (ATermAppl) eq.getArgument( 1 ), dependencies );
		}
	}
}
