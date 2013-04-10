// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tbox.impl.TBoxExpImpl;
import org.mindswap.pellet.tbox.impl.TBoxImpl;

/**
 * 
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
public class TBoxFactory {
	public static TBox createTBox( KnowledgeBase kb ) {
		return PelletOptions.USE_LEGACY_TBOX 
			? new TBoxExpImpl( kb )
			: new TBoxImpl( kb );
	}
}
