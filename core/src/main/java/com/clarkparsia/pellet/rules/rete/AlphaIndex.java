// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Alpha Index
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
public class AlphaIndex {

	private Index<ATermAppl, AlphaNode> index;

	public AlphaIndex() {
		index = new Index<ATermAppl, AlphaNode>();
	}

	public AlphaNode add(TermTuple pattern) {
		List<ATermAppl> key = getKey(pattern);
		AlphaNode result = null;
		// For future use.
		// for ( AlphaNode alpha : index.match( key ) ) {
		// if ( alpha.pattern.equals( pattern ) ) {
		// result = alpha;
		// break;
		// }
		// }
		if (result == null) {
			result = new AlphaNode(pattern);
			index.add(key, result);
		}
		return result;
	}

	private List<ATermAppl> getKey(TermTuple pattern) {
		List<ATermAppl> key = new ArrayList<ATermAppl>();
		for (ATermAppl p : pattern.getElements()) {
			if (ATermUtils.isVar(p))
				key.add(null);
			else
				key.add(p);
		}
		return key;
	}

	/**
	 * Return a list of matching alpha nodes for a given pattern
	 */
	public Collection<AlphaNode> match(Fact fact) {
		return index.match(fact.getElements());
	}

	/**
	 * Reset all alpha nodes (clear their match facts)
	 */
	public void reset() {
		for (AlphaNode alpha : index)
			alpha.reset();
	}

	public int size() {
		return index.size();
	}
	
	public String toString() {
		return index.toString();
	}

}
