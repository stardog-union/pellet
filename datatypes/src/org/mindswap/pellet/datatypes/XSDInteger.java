// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.GenericIntervalList;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 */
public class XSDInteger extends XSDDecimal implements AtomicDatatype, XSDAtomicType {
    public static XSDInteger instance = new XSDInteger( ATermUtils.makeTermAppl( Namespaces.XSD + "integer" ) );

    protected XSDInteger( ATermAppl name ) {
        super( name, false );
    }

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDInteger type = new XSDInteger( null );
        type.values = intervals;

        return type;
    }
}
