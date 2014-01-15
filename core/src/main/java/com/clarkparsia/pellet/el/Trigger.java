// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Arrays;


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
public class Trigger {
	private ConceptInfo[] m_Premises;
	private ConceptInfo m_Consequence;
	
	public Trigger(ConceptInfo[] premises, ConceptInfo consequence) {
		m_Premises = premises;
		m_Consequence = consequence;
	}
	
	public Trigger(ConceptInfo consequence) {
		this(new ConceptInfo[0], consequence);
	}

	public ConceptInfo[] getPremises() {
		return m_Premises;
	}

	public ConceptInfo getConsequence() {
		return m_Consequence;
	}
	
	public boolean isTriggered(ConceptInfo ci) {
		for (ConceptInfo premise : m_Premises) {
			if (!ci.hasSuperClass(premise)) 
				return false;
		}
		return true;
	}
	
	public String toString() {
		return Arrays.toString( m_Premises ) + " -> " + m_Consequence;
	}
}
