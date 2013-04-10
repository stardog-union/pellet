// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.utils.Namespaces;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.OWLUntypedConstant;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Utility class to generate OWL concepts in a concise and readable
 * way.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class OWL {
	public static final OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();

	public static final OWLDataFactory factory = manager.getOWLDataFactory();

	public static final OWLClass Nothing = factory.getOWLNothing();

	public static final OWLClass Thing = factory.getOWLThing();

	public static final OWLObjectProperty topObjectProperty = ObjectProperty(Namespaces.OWL
			+ "topObjectProperty");

	public static final OWLObjectProperty bottomObjectProperty = ObjectProperty(Namespaces.OWL
			+ "bottomObjectProperty");

	public static final OWLDataProperty topDataProperty = DataProperty(Namespaces.OWL
			+ "topDataProperty");

	public static final OWLDataProperty bottomDataProperty = DataProperty(Namespaces.OWL
			+ "bottomDataProperty");

	public static OWLDataAllRestriction all(OWLDataPropertyExpression property, OWLDataRange datatype) {
		return factory.getOWLDataAllRestriction( property, datatype );
	}

	public static OWLObjectAllRestriction all(OWLObjectPropertyExpression property,
			OWLDescription description) {
		return factory.getOWLObjectAllRestriction( property, description );
	}

	/**
	 * @deprecated Use {@link #all(OWLObjectPropertyExpression, OWLDescription)}
	 *             instead
	 */
	public static OWLObjectAllRestriction allValuesFrom(OWLObjectPropertyExpression property,
			OWLDescription description) {
		return factory.getOWLObjectAllRestriction( property, description );
	}

	public static OWLObjectIntersectionOf and(OWLDescription... descriptions) {
		return factory.getOWLObjectIntersectionOf( set( descriptions ) );
	}

	public static OWLObjectIntersectionOf and(Set<? extends OWLDescription> descriptions) {
		return factory.getOWLObjectIntersectionOf( descriptions );
	}

	public static OWLEntityAnnotationAxiom annotation(OWLEntity entity,
			OWLAnnotation<? extends OWLObject> annotation) {
		return factory.getOWLEntityAnnotationAxiom( entity, annotation );
	}

	public static OWLEntityAnnotationAxiom annotation(OWLEntity entity, URI annotationURI,
			OWLConstant constant) {
		return factory.getOWLEntityAnnotationAxiom( entity, factory.getOWLConstantAnnotation(
				annotationURI, constant ) );
	}

	public static OWLEntityAnnotationAxiom annotation(OWLEntity entity, URI annotationURI,
			OWLIndividual individual) {
		return factory.getOWLEntityAnnotationAxiom( entity, factory.getOWLObjectAnnotation(
				annotationURI, individual ) );
	}

	public static OWLIndividual AnonymousIndividual(String anonId) {
		return factory.getOWLAnonymousIndividual( URI.create( anonId ) );
	}

	/**
	 * @deprecated Use {@link #asymmetric(OWLObjectPropertyExpression)} instead
	 */
	public static OWLAntiSymmetricObjectPropertyAxiom antiSymmetric(OWLObjectPropertyExpression p) {
		return asymmetric( p );
	}

	public static OWLAntiSymmetricObjectPropertyAxiom asymmetric(OWLObjectPropertyExpression p) {
		return factory.getOWLAntiSymmetricObjectPropertyAxiom( p );
	}

	/**
	 * @deprecated
	 */
	public static OWLDescription cardinality(OWLDataProperty p, int card) {
		return factory.getOWLDataExactCardinalityRestriction( p, card );
	}

	/**
	 * @deprecated
	 */
	public static OWLDescription cardinality(OWLObjectProperty p, int card) {
		return factory.getOWLObjectExactCardinalityRestriction( p, card );
	}

	/**
	 * @deprecated
	 */
	public static OWLDescription cardinality(OWLObjectProperty p, int card, OWLDescription desc) {
		return factory.getOWLObjectExactCardinalityRestriction( p, card, desc );
	}

	public static OWLClass Class(String uri) {
		return factory.getOWLClass( URI.create( uri ) );
	}

	public static OWLClass Class(URI uri) {
		return factory.getOWLClass( uri );
	}

	public static OWLClassAssertionAxiom classAssertion(OWLIndividual ind, OWLDescription desc) {
		return factory.getOWLClassAssertionAxiom( ind, desc );
	}

	public static OWLEntityAnnotationAxiom comment(OWLEntity entity, String comment) {
		return factory
				.getOWLEntityAnnotationAxiom( entity, factory.getCommentAnnotation( comment ) );
	}

	/**
	 * @deprecated Use {@link #not(OWLDescription)} instead
	 */
	public static OWLObjectComplementOf complementOf(OWLDescription description) {
		return factory.getOWLObjectComplementOf( description );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLTypedConstant</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:boolean.
	 */
	public static OWLTypedConstant constant(boolean value) {
		return factory.getOWLTypedConstant( value );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLTypedConstant</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:double.
	 */
	public static OWLTypedConstant constant(double value) {
		return factory.getOWLTypedConstant( value );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLTypedConstant</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:float.
	 */
	public static OWLTypedConstant constant(float value) {
		return factory.getOWLTypedConstant( value );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLTypedConstant</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:integer.
	 */
	public static OWLTypedConstant constant(int value) {
		return factory.getOWLTypedConstant( value );
	}

	public static OWLUntypedConstant constant(String value) {
		return factory.getOWLUntypedConstant( value );
	}

	public static OWLTypedConstant constant(String value, OWLDataType datatype) {
		return factory.getOWLTypedConstant( value, datatype );
	}

	public static OWLUntypedConstant constant(String value, String lang) {
		return factory.getOWLUntypedConstant( value, lang );
	}

	public static OWLDataProperty DataProperty(String uri) {
		return factory.getOWLDataProperty( URI.create( uri ) );
	}

	public static OWLDataProperty DataProperty(URI uri) {
		return factory.getOWLDataProperty( uri );
	}

	public static OWLDataType dataType(String datatypeURI) {
		return factory.getOWLDataType( URI.create( datatypeURI ) );
	}

	public static OWLDataType dataType(URI datatypeURI) {
		return factory.getOWLDataType( datatypeURI );
	}

	public static OWLDeclarationAxiom declaration(OWLEntity entity) {
		return factory.getOWLDeclarationAxiom( entity );
	}

	public static OWLDifferentIndividualsAxiom differentFrom(OWLIndividual i1, OWLIndividual i2) {
		return factory.getOWLDifferentIndividualsAxiom( set( i1, i2 ) );
	}

	public static OWLDifferentIndividualsAxiom differentFrom(Set<OWLIndividual> inds) {
		return factory.getOWLDifferentIndividualsAxiom( inds );
	}

	public static OWLDisjointClassesAxiom disjointClasses(OWLDescription d1, OWLDescription d2) {
		return factory.getOWLDisjointClassesAxiom( set( d1, d2 ) );
	}

	public static OWLDisjointClassesAxiom disjointClasses(Set<? extends OWLDescription> descriptions) {
		return factory.getOWLDisjointClassesAxiom( descriptions );
	}

	public static OWLDisjointDataPropertiesAxiom disjointProperties(OWLDataPropertyExpression d1,
			OWLDataPropertyExpression d2) {
		return factory.getOWLDisjointDataPropertiesAxiom( set( d1, d2 ) );
	}

	public static OWLDisjointObjectPropertiesAxiom disjointProperties(
			OWLObjectPropertyExpression d1, OWLObjectPropertyExpression d2) {
		return factory.getOWLDisjointObjectPropertiesAxiom( set( d1, d2 ) );
	}

	public static OWLDataPropertyDomainAxiom domain(OWLDataPropertyExpression p, OWLDescription d) {
		return factory.getOWLDataPropertyDomainAxiom( p, d );
	}

	public static OWLObjectPropertyDomainAxiom domain(OWLObjectPropertyExpression p,
			OWLDescription d) {
		return factory.getOWLObjectPropertyDomainAxiom( p, d );
	}

	public static OWLEquivalentClassesAxiom equivalentClasses(OWLDescription d1, OWLDescription d2) {
		return factory.getOWLEquivalentClassesAxiom( set( d1, d2 ) );
	}

	public static OWLEquivalentClassesAxiom equivalentClasses(
			Set<? extends OWLDescription> descriptions) {
		return factory.getOWLEquivalentClassesAxiom( descriptions );
	}

	public static OWLDescription exactly(OWLDataProperty p, int card) {
		return factory.getOWLDataExactCardinalityRestriction( p, card );
	}

	public static OWLDescription exactly(OWLDataProperty p, int card, OWLDataRange d) {
		return factory.getOWLDataExactCardinalityRestriction( p, card, d );
	}

	public static OWLDescription exactly(OWLObjectProperty p, int card) {
		return factory.getOWLObjectExactCardinalityRestriction( p, card );
	}

	public static OWLDescription exactly(OWLObjectProperty p, int card, OWLDescription desc) {
		return factory.getOWLObjectExactCardinalityRestriction( p, card, desc );
	}

	public static OWLFunctionalDataPropertyAxiom functional(OWLDataPropertyExpression p) {
		return factory.getOWLFunctionalDataPropertyAxiom( p );
	}

	public static OWLFunctionalObjectPropertyAxiom functional(OWLObjectPropertyExpression p) {
		return factory.getOWLFunctionalObjectPropertyAxiom( p );
	}

	/**
	 * @deprecated Use {@link #value(OWLDataPropertyExpression, OWLConstant)} instead
	 */
	public static OWLDataValueRestriction hasValue(OWLDataPropertyExpression property,
			OWLConstant constant) {
		return factory.getOWLDataValueRestriction( property, constant );
	}

	/**
	 * @deprecated Use {@link #value(OWLObjectPropertyExpression, OWLIndividual)} instead
	 */
	public static OWLObjectValueRestriction hasValue(OWLObjectPropertyExpression property,
			OWLIndividual value) {
		return factory.getOWLObjectValueRestriction( property, value );
	}

	public static OWLIndividual Individual(String uri) {
		return factory.getOWLIndividual( URI.create( uri ) );
	}

	public static OWLIndividual Individual(URI uri) {
		return factory.getOWLIndividual( uri );
	}

	/**
	 * @deprecated Use {@link #classAssertion(OWLIndividual, OWLDescription)}
	 *             instead
	 */
	public static OWLClassAssertionAxiom instanceOf(OWLIndividual ind, OWLDescription desc) {
		return factory.getOWLClassAssertionAxiom( ind, desc );
	}

	/**
	 * @deprecated Use {@link #and(OWLDescription...)}
	 */
	public static OWLObjectIntersectionOf intersectionOf(OWLDescription... descriptions) {
		return factory.getOWLObjectIntersectionOf( set( descriptions ) );
	}

	/**
	 * @deprecated Use {@link #and(Set)} instead
	 */
	public static OWLObjectIntersectionOf intersectionOf(Set<? extends OWLDescription> descriptions) {
		return factory.getOWLObjectIntersectionOf( descriptions );
	}

	public static OWLObjectPropertyInverse inverse(OWLObjectPropertyExpression p) {
		return factory.getOWLObjectPropertyInverse( p );
	}

	public static OWLInverseFunctionalObjectPropertyAxiom inverseFunctional(
			OWLObjectPropertyExpression p) {
		return factory.getOWLInverseFunctionalObjectPropertyAxiom( p );
	}

	public static OWLInverseObjectPropertiesAxiom inverseProperties(OWLObjectPropertyExpression p1,
			OWLObjectPropertyExpression p2) {
		return factory.getOWLInverseObjectPropertiesAxiom( p1, p2 );
	}

	public static OWLIrreflexiveObjectPropertyAxiom irreflexive(OWLObjectPropertyExpression p) {
		return factory.getOWLIrreflexiveObjectPropertyAxiom( p );
	}

	public static OWLEntityAnnotationAxiom label(OWLEntity entity, String label) {
		return factory.getOWLEntityAnnotationAxiom( entity, factory.getOWLLabelAnnotation( label ) );
	}

	public static OWLDataRangeFacetRestriction length(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.LENGTH, constant );
	}

	public static OWLDataRangeFacetRestriction length(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.LENGTH, constant );
	}

	public static OWLDataMaxCardinalityRestriction max(OWLDataPropertyExpression p, int max) {
		return factory.getOWLDataMaxCardinalityRestriction( p, max );
	}

	public static OWLDataMaxCardinalityRestriction max(OWLDataPropertyExpression p, int max,
			OWLDataRange filler) {
		return factory.getOWLDataMaxCardinalityRestriction( p, max, filler );
	}

	public static OWLObjectMaxCardinalityRestriction max(OWLObjectPropertyExpression p, int max) {
		return factory.getOWLObjectMaxCardinalityRestriction( p, max );
	}

	public static OWLObjectMaxCardinalityRestriction max(OWLObjectPropertyExpression p, int max,
			OWLDescription filler) {
		return factory.getOWLObjectMaxCardinalityRestriction( p, max, filler );
	}

	/**
	 * @deprecated Use {@link #max(OWLObjectPropertyExpression, int)} instead
	 */
	public static OWLObjectMaxCardinalityRestriction maxCardinality(OWLObjectPropertyExpression p,
			int max) {
		return factory.getOWLObjectMaxCardinalityRestriction( p, max );
	}

	/**
	 * @deprecated Use
	 *             {@link #max(OWLObjectPropertyExpression, int, OWLDescription)}
	 *             instead
	 */
	public static OWLObjectMaxCardinalityRestriction maxCardinality(OWLObjectPropertyExpression p,
			int max, OWLDescription filler) {
		return factory.getOWLObjectMaxCardinalityRestriction( p, max, filler );
	}

	public static OWLDataRangeFacetRestriction maxExclusive(double constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxExclusive(float constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxExclusive(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxExclusive(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxInclusive(double constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxInclusive(float constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxInclusive(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxInclusive(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction maxLength(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_LENGTH, constant );
	}

	public static OWLDataRangeFacetRestriction maxLength(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MAX_LENGTH, constant );
	}

	public static OWLDataMinCardinalityRestriction min(OWLDataPropertyExpression p, int min) {
		return factory.getOWLDataMinCardinalityRestriction( p, min );
	}

	public static OWLDataMinCardinalityRestriction min(OWLDataPropertyExpression p, int min,
			OWLDataRange filler) {
		return factory.getOWLDataMinCardinalityRestriction( p, min, filler );
	}

	public static OWLObjectMinCardinalityRestriction min(OWLObjectPropertyExpression p, int min) {
		return factory.getOWLObjectMinCardinalityRestriction( p, min );
	}

	public static OWLObjectMinCardinalityRestriction min(OWLObjectPropertyExpression p, int min,
			OWLDescription filler) {
		return factory.getOWLObjectMinCardinalityRestriction( p, min, filler );
	}

	/**
	 * @deprecated Use {@link #min(OWLObjectPropertyExpression, int)} instead
	 */
	public static OWLObjectMinCardinalityRestriction minCardinality(OWLObjectPropertyExpression p,
			int min) {
		return factory.getOWLObjectMinCardinalityRestriction( p, min );
	}

	/**
	 * @deprecated Use
	 *             {@link #min(OWLObjectPropertyExpression, int, OWLDescription)}
	 *             instead
	 */
	public static OWLObjectMinCardinalityRestriction minCardinality(OWLObjectPropertyExpression p,
			int min, OWLDescription filler) {
		return factory.getOWLObjectMinCardinalityRestriction( p, min, filler );
	}

	public static OWLDataRangeFacetRestriction minExclusive(double constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minExclusive(float constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minExclusive(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minExclusive(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minInclusive(double constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minInclusive(float constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minInclusive(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minInclusive(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE, constant );
	}

	public static OWLDataRangeFacetRestriction minLength(int constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_LENGTH, constant );
	}

	public static OWLDataRangeFacetRestriction minLength(OWLTypedConstant constant) {
		return factory.getOWLDataRangeFacetRestriction(
				OWLRestrictedDataRangeFacetVocabulary.MIN_LENGTH, constant );
	}

	public static OWLObjectComplementOf not(OWLDescription description) {
		return factory.getOWLObjectComplementOf( description );
	}
	
	public static OWLObjectProperty ObjectProperty(String uri) {
		return factory.getOWLObjectProperty( URI.create( uri ) );
	}

	public static OWLObjectProperty ObjectProperty(URI uri) {
		return factory.getOWLObjectProperty( uri );
	}

	public static OWLDataOneOf oneOf(OWLConstant... constants) {
		return factory.getOWLDataOneOf( set( constants ) );
	}

	public static OWLObjectOneOf oneOf(OWLIndividual... individuals) {
		return factory.getOWLObjectOneOf( set( individuals ) );
	}

	public static OWLDataOneOf dataOneOf(Set<? extends OWLConstant> constants) {
		return factory.getOWLDataOneOf( constants );
	}

	public static OWLObjectOneOf objectOneOf(Set<OWLIndividual> individuals) {
		return factory.getOWLObjectOneOf( individuals );
	}
	
	public static OWLDataAllRestriction only(OWLDataPropertyExpression property,
			OWLDataRange datatype) {
		return factory.getOWLDataAllRestriction( property, datatype );
	}

	public static OWLObjectAllRestriction only(OWLObjectPropertyExpression property,
			OWLDescription description) {
		return factory.getOWLObjectAllRestriction( property, description );
	}

	public static OWLObjectUnionOf or(OWLDescription... descriptions) {
		return factory.getOWLObjectUnionOf( set( descriptions ) );
	}

	public static OWLObjectUnionOf or(Set<? extends OWLDescription> descriptions) {
		return factory.getOWLObjectUnionOf( descriptions );
	}

	public static OWLDataPropertyAssertionAxiom propertyAssertion(OWLIndividual subj,
			OWLDataPropertyExpression pred, OWLConstant obj) {
		return factory.getOWLDataPropertyAssertionAxiom( subj, pred, obj );
	}

	public static OWLObjectPropertyAssertionAxiom propertyAssertion(OWLIndividual subj,
			OWLObjectPropertyExpression pred, OWLIndividual obj) {
		return factory.getOWLObjectPropertyAssertionAxiom( subj, pred, obj );
	}

	public static OWLDataPropertyRangeAxiom range(OWLDataPropertyExpression p, OWLDataRange d) {
		return factory.getOWLDataPropertyRangeAxiom( p, d );
	}

	public static OWLObjectPropertyRangeAxiom range(OWLObjectPropertyExpression p, OWLDescription d) {
		return factory.getOWLObjectPropertyRangeAxiom( p, d );
	}

	public static OWLReflexiveObjectPropertyAxiom reflexive(OWLObjectPropertyExpression p) {
		return factory.getOWLReflexiveObjectPropertyAxiom( p );
	}

	public static OWLDataRangeRestriction restrict(OWLDataType datatype,
			OWLDataRangeFacetRestriction... restrictions) {
		return factory.getOWLDataRangeRestriction( datatype, restrictions );
	}

	public static OWLDataRangeRestriction restrict(OWLDataType datatype,
			Set<OWLDataRangeFacetRestriction> restrictions) {
		return factory.getOWLDataRangeRestriction( datatype, restrictions );
	}

	public static OWLSameIndividualsAxiom sameAs(OWLIndividual i1, OWLIndividual i2) {
		return factory.getOWLSameIndividualsAxiom( set( i1, i2 ) );
	}

	public static OWLSameIndividualsAxiom sameAs(Set<OWLIndividual> inds) {
		return factory.getOWLSameIndividualsAxiom( inds );
	}

	public static OWLObjectSelfRestriction self(OWLObjectPropertyExpression p) {
		return factory.getOWLObjectSelfRestriction( p );
	}

	private static <T> Set<T> set(T... elements) {
		Set<T> set = new HashSet<T>();
		for( T e : elements ) {
			set.add( e );
		}

		return set;
	}

	private static <T> Set<T> set(T e1, T e2) {
		Set<T> set = new HashSet<T>();
		set.add( e1 );
		set.add( e2 );

		return set;
	}

	public static OWLDataSomeRestriction some(OWLDataPropertyExpression property,
			OWLDataRange datatype) {
		return factory.getOWLDataSomeRestriction( property, datatype );
	}

	public static OWLObjectSomeRestriction some(OWLObjectPropertyExpression property,
			OWLDescription description) {
		return factory.getOWLObjectSomeRestriction( property, description );
	}

	/**
	 * @deprecated Use
	 *             {@link #some(OWLObjectPropertyExpression, OWLDescription)}
	 *             instead
	 */
	public static OWLObjectSomeRestriction someValuesFrom(OWLObjectPropertyExpression property,
			OWLDescription description) {
		return factory.getOWLObjectSomeRestriction( property, description );
	}

	public static OWLSubClassAxiom subClassOf(OWLDescription sub, OWLDescription sup) {
		return factory.getOWLSubClassAxiom( sub, sup );
	}

	public static OWLDataSubPropertyAxiom subPropertyOf(OWLDataPropertyExpression sub,
			OWLDataPropertyExpression sup) {
		return factory.getOWLSubDataPropertyAxiom( sub, sup );
	}

	public static OWLObjectSubPropertyAxiom subPropertyOf(OWLObjectPropertyExpression sub,
			OWLObjectPropertyExpression sup) {
		return factory.getOWLSubObjectPropertyAxiom( sub, sup );
	}

	public static OWLObjectPropertyChainSubPropertyAxiom subPropertyOf(
			OWLObjectPropertyExpression[] subChain, OWLObjectPropertyExpression sup) {
		return factory.getOWLObjectPropertyChainSubPropertyAxiom( Arrays.asList( subChain ), sup );
	}

	public static OWLSymmetricObjectPropertyAxiom symmetric(OWLObjectPropertyExpression p) {
		return factory.getOWLSymmetricObjectPropertyAxiom( p );
	}

	public static OWLTransitiveObjectPropertyAxiom transitive(OWLObjectPropertyExpression p) {
		return factory.getOWLTransitiveObjectPropertyAxiom( p );
	}

	/**
	 * @deprecated Use {@link #or(OWLDescription...)}
	 */
	public static OWLObjectUnionOf unionOf(OWLDescription... descriptions) {
		return factory.getOWLObjectUnionOf( set( descriptions ) );
	}

	/**
	 * @deprecated Use {@link #or(Set<? extends OWLDescription)} instead
	 */
	public static OWLObjectUnionOf unionOf(Set<? extends OWLDescription> descriptions) {
		return factory.getOWLObjectUnionOf( descriptions );
	}

	public static OWLDataValueRestriction value(OWLDataPropertyExpression property,
			OWLConstant constant) {
		return factory.getOWLDataValueRestriction( property, constant );
	}

	public static OWLObjectValueRestriction value(OWLObjectPropertyExpression property,
			OWLIndividual value) {
		return factory.getOWLObjectValueRestriction( property, value );
	}
}
