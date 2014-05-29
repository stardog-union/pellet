// Copyright = term(NS + c) 2006 - 2008; public static final ATermAppl Clark &
// Parsia; public static final ATermAppl LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms; public static final ATermAppl
// including the availability of proprietary exceptions.
// Questions; public static final ATermAppl comments; public static final
// ATermAppl or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.datatypes;

import static com.clarkparsia.pellet.utils.TermFactory.term;

import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright = term(NS + c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia; public static final ATermAppl LLC.
 * <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class Datatypes {
	public static final ATermAppl	LITERAL					= term( Namespaces.RDFS + "Literal" );
	
	public static final ATermAppl	PLAIN_LITERAL			= term( Namespaces.RDF + "PlainLiteral" );
	public static final ATermAppl	XML_LITERAL				= term( Namespaces.RDF + "XMLLiteral" );
	
	public static final ATermAppl	REAL					= term( Namespaces.OWL + "Real" );
	public static final ATermAppl	RATIONAL				= term( Namespaces.OWL + "Rational" );
	
	public static final ATermAppl	ANY_TYPE				= term( Namespaces.XSD + "anyType" );
	public static final ATermAppl	ANY_SIMPLE_TYPE			= term( Namespaces.XSD + "anySimpleType" );
	public static final ATermAppl	STRING					= term( Namespaces.XSD + "string" );
	public static final ATermAppl	INTEGER					= term( Namespaces.XSD + "integer" );
	public static final ATermAppl	LONG					= term( Namespaces.XSD + "long" );
	public static final ATermAppl	INT						= term( Namespaces.XSD + "int" );
	public static final ATermAppl	SHORT					= term( Namespaces.XSD + "short" );
	public static final ATermAppl	BYTE					= term( Namespaces.XSD + "byte" );
	public static final ATermAppl	DECIMAL					= term( Namespaces.XSD + "decimal" );
	public static final ATermAppl	FLOAT					= term( Namespaces.XSD + "float" );
	public static final ATermAppl	BOOLEAN					= term( Namespaces.XSD + "boolean" );
	public static final ATermAppl	DOUBLE					= term( Namespaces.XSD + "double" );
	public static final ATermAppl	NON_POSITIVE_INTEGER	= term( Namespaces.XSD + "nonPositiveInteger" );
	public static final ATermAppl	NEGATIVE_INTEGER		= term( Namespaces.XSD + "negativeInteger" );
	public static final ATermAppl	NON_NEGATIVE_INTEGER	= term( Namespaces.XSD + "nonNegativeInteger" );
	public static final ATermAppl	UNSIGNED_LONG			= term( Namespaces.XSD + "unsignedLong" );
	public static final ATermAppl	UNSIGNED_INT			= term( Namespaces.XSD + "unsignedInt" );
	public static final ATermAppl	POSITIVE_INTEGER		= term( Namespaces.XSD + "positiveInteger" );
	public static final ATermAppl	BASE_64_BINARY			= term( Namespaces.XSD + "base64Binary" );
	public static final ATermAppl	HEX_BINARY				= term( Namespaces.XSD + "hexBinary" );
	public static final ATermAppl	ANY_URI					= term( Namespaces.XSD + "anyURI" );
	public static final ATermAppl	Q_NAME					= term( Namespaces.XSD + "QName" );
	public static final ATermAppl	NOTATION				= term( Namespaces.XSD + "NOTATION" );
	public static final ATermAppl	NORMALIZED_STRING		= term( Namespaces.XSD + "normalizedString" );
	public static final ATermAppl	TOKEN					= term( Namespaces.XSD + "token" );
	public static final ATermAppl	LANGUAGE				= term( Namespaces.XSD + "language" );
	public static final ATermAppl	NAME					= term( Namespaces.XSD + "Name" );
	public static final ATermAppl	NCNAME					= term( Namespaces.XSD + "NCName" );
	public static final ATermAppl	NMTOKEN					= term( Namespaces.XSD + "NMToken" );
	public static final ATermAppl	ID						= term( Namespaces.XSD + "ID" );
	public static final ATermAppl	IDREF					= term( Namespaces.XSD + "IDREF" );
	public static final ATermAppl	IDREFS					= term( Namespaces.XSD + "IDREFS" );
	public static final ATermAppl	ENTITY					= term( Namespaces.XSD + "ENTITY" );
	public static final ATermAppl	ENTITIES				= term( Namespaces.XSD + "ENTITIES" );
	public static final ATermAppl	DURATION				= term( Namespaces.XSD + "duration" );
	public static final ATermAppl	DATE_TIME				= term( Namespaces.XSD + "dateTime" );
	public static final ATermAppl	DATE_TIME_STAMP			= term( Namespaces.XSD + "dateTimeStamp" );
	public static final ATermAppl	TIME					= term( Namespaces.XSD + "time" );
	public static final ATermAppl	DATE					= term( Namespaces.XSD + "date" );
	public static final ATermAppl	G_YEAR_MONTH			= term( Namespaces.XSD + "gYearMonth" );
	public static final ATermAppl	G_YEAR					= term( Namespaces.XSD + "gYear" );
	public static final ATermAppl	G_MONTH_DAY				= term( Namespaces.XSD + "gMonthYear" );
	public static final ATermAppl	G_DAY					= term( Namespaces.XSD + "gDay" );
	public static final ATermAppl	G_MONTH					= term( Namespaces.XSD + "gMonth" );
	public static final ATermAppl	UNSIGNED_SHORT			= term( Namespaces.XSD + "unsignedShort" );
	public static final ATermAppl	UNSIGNED_BYTE			= term( Namespaces.XSD + "unsignedByte" );
}
