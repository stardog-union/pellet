// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.expressivity;

import org.mindswap.pellet.KnowledgeBase;

import aterm.ATermAppl;

/**
 * <p>
 * Title: 
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
 * @author Harris Lin
 */
public abstract class ProfileBasedExpressivityChecker {
	protected KnowledgeBase m_KB;
	
	public ProfileBasedExpressivityChecker(KnowledgeBase kb) {
		m_KB = kb;
	}
	
	/**
	 * Compute the expressivity from the {@link org.mindswap.pellet.KnowledgeBase}
	 * and update it to the given {@link com.clarkparsia.pellet.expressivity.Expressivity}.
	 * 
	 * @return <code>true</code> if the expressivity is within the profile defined by the implementation,
	 * <code>false</code> otherwise.
	 */
	public abstract boolean compute(Expressivity expressivity);
	
	/**
	 * Update the given {@link com.clarkparsia.pellet.expressivity.Expressivity} by adding
	 * the new @{link aterm.ATermAppl}. 
	 * 
	 * @return <code>true</code> if the new expressivity is within the profile defined by the implementation,
	 * <code>false</code> otherwise.
	 */
	public abstract boolean updateWith(Expressivity expressivity, ATermAppl term);
}
