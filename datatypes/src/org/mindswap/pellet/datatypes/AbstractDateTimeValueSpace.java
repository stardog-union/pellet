// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;

public abstract class AbstractDateTimeValueSpace extends AbstractValueSpace implements ValueSpace {
	private XSDatatype dt;
    
    public AbstractDateTimeValueSpace( IDateTimeValueType minInf, IDateTimeValueType maxInf, XSDatatype dt ) {
        super( minInf, null, maxInf, false );
        
        this.dt = dt;
    }

    public int compare( Object a, Object b ) {
        return ((IDateTimeValueType) a).compare((IDateTimeValueType) b);
    }

    public boolean isValid( Object value ) {
        return (value instanceof IDateTimeValueType);
    }

    public Object getValue( String value ) {
        return (IDateTimeValueType) dt.createValue( value, null );
    }        

    public String getLexicalForm( Object value ) {
        return dt.convertToLexicalValue( value, null );
    }

}
