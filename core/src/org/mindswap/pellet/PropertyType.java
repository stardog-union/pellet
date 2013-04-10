// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

public enum PropertyType {
	UNTYPED, OBJECT, DATATYPE, ANNOTATION;

	private String str;
	
    private PropertyType() {
	    str = name().substring(0,1) + name().substring(1).toLowerCase();
    }

	public String toString() {
		return str;
	}
}
