// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.expressivity;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

import com.clarkparsia.pellet.el.ELExpressivityChecker;

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
public class ExpressivityChecker {
	private KnowledgeBase m_KB;
	private ELExpressivityChecker m_ELChecker;
	private DLExpressivityChecker m_DLChecker;
	private Expressivity m_Expressivity;
	
	public ExpressivityChecker(KnowledgeBase kb) {
		this( kb, new Expressivity() );
	}
	
	public ExpressivityChecker(KnowledgeBase kb, Expressivity expr) {
		m_KB = kb;
		m_ELChecker = new ELExpressivityChecker(m_KB);
		m_DLChecker = new DLExpressivityChecker(m_KB);
		m_Expressivity = expr;
	}
	
	public void prepare() {
		m_Expressivity = new Expressivity();
		if (m_ELChecker.compute(m_Expressivity)) return;
		
		m_Expressivity = new Expressivity();
		// force expressivity to be non-EL
		m_Expressivity.setHasAllValues( true );
		m_DLChecker.compute(m_Expressivity);
	}
	
	public Expressivity getExpressivity() {
		return m_Expressivity; 
	}
	
	public Expressivity getExpressivityWith(ATermAppl c) {
		if (c == null) return m_Expressivity;

		Expressivity newExp = new Expressivity(m_Expressivity);
		m_DLChecker.updateWith(newExp, c);

		return newExp;
	}
	
	/**
	 * Added for incremental reasoning. Given an aterm corresponding to an
	 * individual and concept, the expressivity is updated accordingly.
	 */
	public void updateWithIndividual(ATermAppl i, ATermAppl concept) {
		ATermAppl nominal = ATermUtils.makeValue(i);

		if( concept.equals(nominal) )
			return;
		
		m_DLChecker.updateWith(m_Expressivity, concept);
	}
}
