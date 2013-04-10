// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi;

import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.vocab.XSDVocabulary;

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
 * @author Evren Sirin
 */
public class XSD {
	public static final OWLDataType	STRING					= OWL.dataType( XSDVocabulary.STRING
																	.getURI() );

	public static final OWLDataType	INT						= OWL.dataType( XSDVocabulary.INT
																	.getURI() );

	public static final OWLDataType	INTEGER					= OWL.dataType( XSDVocabulary.INTEGER
																	.getURI() );
	public static final OWLDataType	LONG					= OWL.dataType( XSDVocabulary.LONG
																	.getURI() );
	public static final OWLDataType	SHORT					= OWL.dataType( XSDVocabulary.SHORT
																	.getURI() );
	public static final OWLDataType	BYTE					= OWL.dataType( XSDVocabulary.BYTE
																	.getURI() );
	public static final OWLDataType	POSITIVE_INTEGER		= OWL
																	.dataType( XSDVocabulary.POSITIVE_INTEGER
																			.getURI() );
	public static final OWLDataType	NEGATIVE_INTEGER		= OWL
																	.dataType( XSDVocabulary.NEGATIVE_INTEGER
																			.getURI() );
	public static final OWLDataType	NON_POSITIVE_INTEGER	= OWL
																	.dataType( XSDVocabulary.NON_POSITIVE_INTEGER
																			.getURI() );
	public static final OWLDataType	NON_NEGATIVE_INTEGER	= OWL
																	.dataType( XSDVocabulary.NON_NEGATIVE_INTEGER
																			.getURI() );
	public static final OWLDataType	ANY_URI					= OWL.dataType( XSDVocabulary.ANY_URI
																	.getURI() );
	public static final OWLDataType	ANY_SIMPLE_TYPE			= OWL
																	.dataType( XSDVocabulary.ANY_SIMPLE_TYPE
																			.getURI() );
	public static final OWLDataType	ANY_TYPE				= OWL.dataType( XSDVocabulary.ANY_TYPE
																	.getURI() );
	public static final OWLDataType	DATE					= OWL.dataType( XSDVocabulary.DATE
																	.getURI() );
	public static final OWLDataType	DATE_TIME				= OWL.dataType( XSDVocabulary.DATE_TIME
																	.getURI() );
	public static final OWLDataType	G_DAY					= OWL.dataType( XSDVocabulary.G_DAY
																	.getURI() );
	public static final OWLDataType	G_MONTH					= OWL.dataType( XSDVocabulary.G_MONTH
																	.getURI() );
	public static final OWLDataType	G_MONTH_DAY				= OWL
																	.dataType( XSDVocabulary.G_MONTH_DAY
																			.getURI() );
	public static final OWLDataType	G_YEAR					= OWL.dataType( XSDVocabulary.G_YEAR
																	.getURI() );
	public static final OWLDataType	G_YEAR_MONTH			= OWL
																	.dataType( XSDVocabulary.G_YEAR_MONTH
																			.getURI() );
	public static final OWLDataType	TIME					= OWL.dataType( XSDVocabulary.TIME
																	.getURI() );
	public static final OWLDataType	DURATION				= OWL.dataType( XSDVocabulary.DURATION
																	.getURI() );
	public static final OWLDataType	BOOLEAN					= OWL.dataType( XSDVocabulary.BOOLEAN
																	.getURI() );
	public static final OWLDataType	DOUBLE					= OWL.dataType( XSDVocabulary.DOUBLE
																	.getURI() );
	public static final OWLDataType	FLOAT					= OWL.dataType( XSDVocabulary.FLOAT
																	.getURI() );
	public static final OWLDataType	UNSIGNED_INT			= OWL
																	.dataType( XSDVocabulary.UNSIGNED_INT
																			.getURI() );
	public static final OWLDataType	UNSIGNED_LONG			= OWL
																	.dataType( XSDVocabulary.UNSIGNED_LONG
																			.getURI() );
	public static final OWLDataType	BASE_64_BINARY			= OWL
																	.dataType( XSDVocabulary.BASE_64_BINARY
																			.getURI() );
	public static final OWLDataType	NOTATION				= OWL.dataType( XSDVocabulary.NOTATION
																	.getURI() );
	public static final OWLDataType	NORMALIZED_STRING		= OWL
																	.dataType( XSDVocabulary.NORMALIZED_STRING
																			.getURI() );
	public static final OWLDataType	ID						= OWL.dataType( XSDVocabulary.ID
																	.getURI() );
	public static final OWLDataType	IDREF					= OWL.dataType( XSDVocabulary.IDREF
																	.getURI() );
	public static final OWLDataType	IDREFS					= OWL.dataType( XSDVocabulary.IDREFS
																	.getURI() );
	public static final OWLDataType	LANGUAGE				= OWL.dataType( XSDVocabulary.LANGUAGE
																	.getURI() );
	public static final OWLDataType	TOKEN					= OWL.dataType( XSDVocabulary.TOKEN
																	.getURI() );
	public static final OWLDataType	ENTITIES				= OWL.dataType( XSDVocabulary.ENTITIES
																	.getURI() );
	public static final OWLDataType	ENTITY					= OWL.dataType( XSDVocabulary.ENTITY
																	.getURI() );
	public static final OWLDataType	HEX_BINARY				= OWL
																	.dataType( XSDVocabulary.HEX_BINARY
																			.getURI() );
	public static final OWLDataType	Q_NAME					= OWL.dataType( XSDVocabulary.Q_NAME
																	.getURI() );
}
