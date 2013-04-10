// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.queue;

/**
 * <p>
 * Title: NodeSelector
 * </p>
 * <p>
 * Description: Enumerated type used to select completion graph nodes
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public enum NodeSelector {

	ATOM, CHOOSE, DATATYPE, DISJUNCTION, EXISTENTIAL, GUESS, LITERAL, MAX_NUMBER, MIN_NUMBER,
	NOMINAL, UNIVERSAL;

	public static int numSelectors() {
		return values().length;
	}
}
