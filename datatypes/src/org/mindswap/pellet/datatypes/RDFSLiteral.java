// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;


/**
 * @author Evren Sirin
 */
public class RDFSLiteral extends BaseUnionDatatype implements UnionDatatype {
	public static final RDFSLiteral instance = new RDFSLiteral();

	RDFSLiteral() {
		super(
			ATermUtils.makeTermAppl(Namespaces.RDFS + "Literal"), 
//			new Datatype[] { RDFSPlainLiteral.instance, RDFSTypedLiteral.instance });
			new Datatype[] { XSDSimpleType.instance, RDFXMLLiteral.instance, UnknownDatatype.instance });		
	}

}
