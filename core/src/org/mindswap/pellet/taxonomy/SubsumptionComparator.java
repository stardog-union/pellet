// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.PartialOrderRelation;

import aterm.ATermAppl;

/**
 * <p>
 * Title: SubsumptionComparator
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class SubsumptionComparator implements PartialOrderComparator<ATermAppl> {

	protected KnowledgeBase	kb;

	public SubsumptionComparator(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	protected boolean isSubsumedBy(ATermAppl a, ATermAppl b) {
		return kb.isSubClassOf( a, b );
	}

	public PartialOrderRelation compare(ATermAppl a, ATermAppl b) {
		if( isSubsumedBy( a, b ) ) {
			if( isSubsumedBy( b, a ) )
				return PartialOrderRelation.EQUAL;
			else
				return PartialOrderRelation.LESS;
		}
		else if( isSubsumedBy( b, a ) )
			return PartialOrderRelation.GREATER;
		else
			return PartialOrderRelation.INCOMPARABLE;
	}

	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}

}
