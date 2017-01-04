// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.XSD;

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
 * @author Markus Stocker
 */
public class OWL2 extends OWL {
	final public static Resource	AllDisjointClasses				= ResourceFactory
																			.createResource( NS
																					+ "AllDisjointClasses" );

	final public static Resource	AllDisjointProperties			= ResourceFactory
																			.createResource( NS
																					+ "AllDisjointProperties" );

	final public static Resource	ReflexiveProperty				= ResourceFactory
																			.createResource( NS
																					+ "ReflexiveProperty" );

	final public static Resource	IrreflexiveProperty				= ResourceFactory
																			.createResource( NS
																					+ "IrreflexiveProperty" );

	/** @deprecated */
	final public static Resource	AntisymmetricProperty			= ResourceFactory
																			.createResource( NS
																					+ "AntisymmetricProperty" );

	final public static Resource	AsymmetricProperty				= ResourceFactory
																			.createResource( NS
																					+ "AsymmetricProperty" );

	/** @deprecated Not in OWL 2 spec, was in OWL 1.1. Use {@link #hasSelf} */
	final public static Resource	SelfRestriction					= ResourceFactory
																			.createResource( NS
																					+ "SelfRestriction" );

	final public static Resource	NamedIndividual					= ResourceFactory
																			.createResource( NS
																					+ "NamedIndividual" );

	final public static Resource	NegativePropertyAssertion		= ResourceFactory
																			.createResource( NS
																					+ "NegativePropertyAssertion" );

	/** @deprecated */
	final public static Resource	NegativeObjectPropertyAssertion	= ResourceFactory
																			.createResource( NS
																					+ "NegativeObjectPropertyAssertion" );

	/** @deprecated */
	final public static Resource	NegativeDataPropertyAssertion	= ResourceFactory
																			.createResource( NS
																					+ "NegativeDataPropertyAssertion" );
	final public static Resource	Axiom	= ResourceFactory
																			.createResource( NS
																					+ "Axiom" );
	final public static Resource	Annotation	= ResourceFactory
																			.createResource( NS
																						+ "Annotation" );
	
	final public static Property	topDataProperty 				= ResourceFactory
																			.createProperty( NS
																					+ "topDataProperty" );
	
	final public static Property	bottomDataProperty				= ResourceFactory
																			.createProperty( NS
																					+ "bottomDataProperty" );

	final public static Property	topObjectProperty				= ResourceFactory
																			.createProperty( NS
																					+ "topObjectProperty" );

	final public static Property	bottomObjectProperty			= ResourceFactory
																			.createProperty( NS
																					+ "bottomObjectProperty" );

	final public static Property	disjointUnionOf					= ResourceFactory
																			.createProperty( NS
																					+ "disjointUnionOf" );

	final public static Property	propertyDisjointWith			= ResourceFactory
																			.createProperty( NS
																					+ "propertyDisjointWith" );

	final public static Property	qualifiedCardinality			= ResourceFactory
																			.createProperty( NS
																					+ "qualifiedCardinality" );

	/** @deprecated */
	final public static Property	disjointObjectProperties		= ResourceFactory
																			.createProperty( NS
																					+ "disjointObjectProperties" );

	/** @deprecated */
	final public static Property	disjointDataProperties			= ResourceFactory
																			.createProperty( NS
																					+ "disjointDataProperties" );

	final public static Property	onClass							= ResourceFactory
																			.createProperty( NS
																					+ "onClass" );

	final public static Property	onDataRange						= ResourceFactory
																			.createProperty( NS
																					+ "onDataRange" );

	final public static Property	datatypeComplementOf			= ResourceFactory
																			.createProperty( NS
																					+ "datatypeComplementOf" );
	
	final public static Property	onDatatype						= ResourceFactory
																			.createProperty( NS
																					+ "onDatatype" );

	final public static Property	withRestrictions				= ResourceFactory
																			.createProperty( NS
																					+ "withRestrictions" );

	final public static Property	length							= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "length" );

	final public static Property	maxLength						= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "maxLength" );

	final public static Property	maxQualifiedCardinality			= ResourceFactory
																			.createProperty( NS
																					+ "maxQualifiedCardinality" );

	final public static Property	minLength						= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "minLength" );

	final public static Property	minQualifiedCardinality			= ResourceFactory
																			.createProperty( NS
																					+ "minQualifiedCardinality" );

	final public static Property	totalDigits						= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "totalDigits" );

	final public static Property	fractionDigits					= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "fractionDigits" );

	final public static Property	minInclusive					= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "minInclusive" );

	final public static Property	minExclusive					= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "minExclusive" );

	final public static Property	maxInclusive					= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "maxInclusive" );

	final public static Property	maxExclusive					= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "maxExclusive" );

	final public static Property	pattern							= ResourceFactory
																			.createProperty( XSD
																					.getURI()
																					+ "pattern" );
	/** @deprecated Not in OWL 2 spec, only in earlier drafts. */
	final public static Property	propertyChain					= ResourceFactory
																			.createProperty( NS
																					+ "propertyChain" );

	final public static Property	propertyChainAxiom				= ResourceFactory
																			.createProperty( NS
																					+ "propertyChainAxiom" );

	/** @deprecated */
	final public static Property	enumeration						= ResourceFactory
																			.createProperty( NS
																					+ "enumeration" );

	/** @deprecated */
	final public static Property	whiteSpace						= ResourceFactory
																			.createProperty( NS
																					+ "whiteSpace" );
	
	final public static Property	members							= ResourceFactory
																			.createProperty( NS
																					+ "members" );

	final public static Property	hasKey							= ResourceFactory
																			.createProperty( NS
																					+ "hasKey" );

	final public static Property	hasSelf							= ResourceFactory
																			.createProperty( NS
																					+ "hasSelf" );

	final public static Property	sourceIndividual				= ResourceFactory
																			.createProperty( NS
																					+ "sourceIndividual" );

	final public static Property	assertionProperty				= ResourceFactory
																			.createProperty( NS
																					+ "assertionProperty" );

	final public static Property	targetIndividual				= ResourceFactory
																			.createProperty( NS
																					+ "targetIndividual" );

	final public static Property	targetValue						= ResourceFactory
																			.createProperty( NS
																					+ "targetValue" );
	
	final public static Property	annotatedSource					= ResourceFactory
																			.createProperty( NS
																					+ "annotatedSource" );
	
	final public static Property	annotatedProperty				= ResourceFactory
																			.createProperty( NS
																					+ "annotatedProperty" );
	
	final public static Property	annotatedTarget					= ResourceFactory
																			.createProperty( NS
																					+ "annotatedTarget" );
	final public static Property	object							= ResourceFactory
																			.createProperty( NS
																					+ "object" );

	final public static Property	predicate						= ResourceFactory
																			.createProperty( NS
																					+ "predicate" );

	final public static Property	subject							= ResourceFactory
																			.createProperty( NS
																					+ "subject" );
	
}