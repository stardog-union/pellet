// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import aterm.ATermAppl;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.PartialOrderRelation;

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
public class SubsumptionComparator implements PartialOrderComparator<ATermAppl>
{

	protected KnowledgeBase kb;

	public SubsumptionComparator(final KnowledgeBase kb)
	{
		this.kb = kb;
	}

	protected boolean isSubsumedBy(final ATermAppl a, final ATermAppl b)
	{
		return kb.isSubClassOf(a, b);
	}

	@Override
	public PartialOrderRelation compare(final ATermAppl a, final ATermAppl b)
	{
		if (isSubsumedBy(a, b))
		{
			if (isSubsumedBy(b, a))
				return PartialOrderRelation.EQUAL;
			else
				return PartialOrderRelation.LESS;
		}
		else
			if (isSubsumedBy(b, a))
				return PartialOrderRelation.GREATER;
			else
				return PartialOrderRelation.INCOMPARABLE;
	}

	public void setKB(final KnowledgeBase kb)
	{
		this.kb = kb;
	}

}
