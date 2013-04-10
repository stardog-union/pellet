// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy.printer;

/**
 * <p>
 * Title: RDFALLXMLTaxonomyPrinter
 * </p>
 * <p>
 * Description: Prints all subclass relations
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
public class RDFALLXMLTaxonomyPrinter extends RDFXMLTaxonomyPrinter {

	public RDFALLXMLTaxonomyPrinter() {
		onlyDirectSubclass = false;
	}
}
