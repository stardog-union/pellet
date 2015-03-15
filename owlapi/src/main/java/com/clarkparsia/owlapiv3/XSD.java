// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapiv3;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

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
	public static final OWLDatatype	STRING					= OWL.Datatype( XSDVocabulary.STRING
																	.getIRI() );

	public static final OWLDatatype	INT						= OWL.Datatype( XSDVocabulary.INT
																	.getIRI() );

	public static final OWLDatatype	INTEGER					= OWL.Datatype( XSDVocabulary.INTEGER
																	.getIRI() );
	public static final OWLDatatype	LONG					= OWL.Datatype( XSDVocabulary.LONG
																	.getIRI() );
	public static final OWLDatatype	SHORT					= OWL.Datatype( XSDVocabulary.SHORT
																	.getIRI() );
	public static final OWLDatatype	BYTE					= OWL.Datatype( XSDVocabulary.BYTE
																	.getIRI() );
	public static final OWLDatatype	POSITIVE_INTEGER		= OWL
																	.Datatype( XSDVocabulary.POSITIVE_INTEGER
																			.getIRI() );
	public static final OWLDatatype	NEGATIVE_INTEGER		= OWL
																	.Datatype( XSDVocabulary.NEGATIVE_INTEGER
																			.getIRI() );
	public static final OWLDatatype	NON_POSITIVE_INTEGER	= OWL
																	.Datatype( XSDVocabulary.NON_POSITIVE_INTEGER
																			.getIRI() );
	public static final OWLDatatype	NON_NEGATIVE_INTEGER	= OWL
																	.Datatype( XSDVocabulary.NON_NEGATIVE_INTEGER
																			.getIRI() );
	public static final OWLDatatype	ANY_URI					= OWL.Datatype( XSDVocabulary.ANY_URI
																	.getIRI() );
	public static final OWLDatatype	ANY_SIMPLE_TYPE			= OWL
																	.Datatype( XSDVocabulary.ANY_SIMPLE_TYPE
																			.getIRI() );
	public static final OWLDatatype	ANY_TYPE				= OWL.Datatype( XSDVocabulary.ANY_TYPE
																	.getIRI() );
	public static final OWLDatatype	DATE					= OWL.Datatype( XSDVocabulary.DATE
																	.getIRI() );
	public static final OWLDatatype	DATE_TIME				= OWL.Datatype( XSDVocabulary.DATE_TIME
																	.getIRI() );
	public static final OWLDatatype	G_DAY					= OWL.Datatype( XSDVocabulary.G_DAY
																	.getIRI() );
	public static final OWLDatatype	G_MONTH					= OWL.Datatype( XSDVocabulary.G_MONTH
																	.getIRI() );
	public static final OWLDatatype	G_MONTH_DAY				= OWL
																	.Datatype( XSDVocabulary.G_MONTH_DAY
																			.getIRI() );
	public static final OWLDatatype	G_YEAR					= OWL.Datatype( XSDVocabulary.G_YEAR
																	.getIRI() );
	public static final OWLDatatype	G_YEAR_MONTH			= OWL
																	.Datatype( XSDVocabulary.G_YEAR_MONTH
																			.getIRI() );
	public static final OWLDatatype	TIME					= OWL.Datatype( XSDVocabulary.TIME
																	.getIRI() );
	public static final OWLDatatype	DURATION				= OWL.Datatype( XSDVocabulary.DURATION
																	.getIRI() );
	public static final OWLDatatype	BOOLEAN					= OWL.Datatype( XSDVocabulary.BOOLEAN
																	.getIRI() );
	public static final OWLDatatype	DOUBLE					= OWL.Datatype( XSDVocabulary.DOUBLE
																	.getIRI() );
	public static final OWLDatatype	FLOAT					= OWL.Datatype( XSDVocabulary.FLOAT
																	.getIRI() );
	public static final OWLDatatype	UNSIGNED_INT			= OWL
																	.Datatype( XSDVocabulary.UNSIGNED_INT
																			.getIRI() );
	public static final OWLDatatype	UNSIGNED_LONG			= OWL
																	.Datatype( XSDVocabulary.UNSIGNED_LONG
																			.getIRI() );
	public static final OWLDatatype	BASE_64_BINARY			= OWL
																	.Datatype( XSDVocabulary.BASE_64_BINARY
																			.getIRI() );
	public static final OWLDatatype	NOTATION				= OWL.Datatype( XSDVocabulary.NOTATION
																	.getIRI() );
	public static final OWLDatatype	NORMALIZED_STRING		= OWL
																	.Datatype( XSDVocabulary.NORMALIZED_STRING
																			.getIRI() );
	public static final OWLDatatype	ID						= OWL.Datatype( XSDVocabulary.ID
																	.getIRI() );
	public static final OWLDatatype	IDREF					= OWL.Datatype( XSDVocabulary.IDREF
																	.getIRI() );
	public static final OWLDatatype	IDREFS					= OWL.Datatype( XSDVocabulary.IDREFS
																	.getIRI() );
	public static final OWLDatatype	LANGUAGE				= OWL.Datatype( XSDVocabulary.LANGUAGE
																	.getIRI() );
	public static final OWLDatatype	TOKEN					= OWL.Datatype( XSDVocabulary.TOKEN
																	.getIRI() );
	public static final OWLDatatype	ENTITIES				= OWL.Datatype( XSDVocabulary.ENTITIES
																	.getIRI() );
	public static final OWLDatatype	ENTITY					= OWL.Datatype( XSDVocabulary.ENTITY
																	.getIRI() );
	public static final OWLDatatype	HEX_BINARY				= OWL
																	.Datatype( XSDVocabulary.HEX_BINARY
																			.getIRI() );
	public static final OWLDatatype	Q_NAME					= OWL.Datatype( XSDVocabulary.Q_NAME
																	.getIRI() );
}
