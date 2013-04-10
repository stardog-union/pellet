// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils.intset;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class IntSetFactory {
	public static IntSet create() {
		return new ArrayIntSet();
//		return new BitIntSet();
//		return new ShiftedBitIntSet();
//		return new HashIntSet();
//		return new TreeIntSet();
//		return new PrimitiveIntSet( bak.pcj.set.IntOpenHashSet.class );
//		return new PrimitiveIntSet( bak.pcj.set.IntChainedHashSet.class );
//		return new PrimitiveIntSet( bak.pcj.set.IntRangeSet.class );
//		return new PrimitiveIntSet( bak.pcj.set.IntBitSet.class );
	}
}
