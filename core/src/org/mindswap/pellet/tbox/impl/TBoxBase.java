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

import java.util.Map;

import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;

import aterm.ATerm;
import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

public class TBoxBase {
	public static Logger log = Logger.getLogger( TBoxBase.class.getName() );
	
	protected KnowledgeBase kb;
	protected TBoxExpImpl tbox;
	
	protected Map<ATermAppl,TermDefinition> termhash = CollectionUtils.makeIdentityMap();
	
	public TBoxBase(TBoxExpImpl tbox) {
		this.tbox = tbox;
		this.kb = tbox.getKB();
	}
	
	public boolean addDef(ATermAppl def) {
		ATermAppl name = (ATermAppl) def.getArgument(0);
		if (termhash.containsKey(name)) {
			getTD(name).addDef(def);
		} else {
			TermDefinition td = new TermDefinition();
			td.addDef(def);
			termhash.put(name, td);
		}
		
		return true;
	}
	
	public boolean removeDef(ATermAppl axiom) {
		boolean removed = false;
		
		ATermAppl name = (ATermAppl) axiom.getArgument( 0 );
		TermDefinition td = getTD( name );
		if( td != null ) 
			removed = td.removeDef( axiom );		
		
		return removed;
	}
	
	public boolean contains(ATerm name) {
		return termhash.containsKey(name);
	}
	
	public TermDefinition getTD(ATerm name) {
		return termhash.get(name);

	}	
	
	public boolean isEmpty() {
		return (termhash.size() == 0);
	}
	
	/**
	 * Returns the number of term definitions stored in this TBox.
	 * 
	 * @return
	 */
	public int size() {
		return termhash.size();
	}	
	
}
