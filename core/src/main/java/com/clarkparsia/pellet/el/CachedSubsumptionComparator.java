// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import org.mindswap.pellet.taxonomy.SubsumptionComparator;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

import aterm.ATermAppl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class CachedSubsumptionComparator extends SubsumptionComparator {
	private MultiValueMap<ATermAppl, ATermAppl> subsumers;
	
	public CachedSubsumptionComparator(MultiValueMap<ATermAppl, ATermAppl> subsumers) {
		super( null );
		this.subsumers = subsumers;
	}
	
	@Override
	public boolean isSubsumedBy(ATermAppl a, ATermAppl b) {
		return a == ATermUtils.BOTTOM || b == ATermUtils.TOP || a.equals( b )
				|| subsumers.contains( a, b );
	}

}
