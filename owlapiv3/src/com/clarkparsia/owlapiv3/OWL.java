// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapiv3;

import java.rmi.server.UID;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.utils.Namespaces;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

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
	public static final OWLOntologyManager	manager	= OWLManager.createOWLOntologyManager();

	public static final OWLDataFactory		factory	= manager.getOWLDataFactory();

	public static final OWLClass			Nothing	= factory.getOWLNothing();

	public static final OWLClass			Thing	= factory.getOWLThing();

	public static final OWLObjectProperty topObjectProperty = ObjectProperty(Namespaces.OWL
			+ "topObjectProperty");

	public static final OWLObjectProperty bottomObjectProperty = ObjectProperty(Namespaces.OWL
			+ "bottomObjectProperty");

	public static final OWLDataProperty topDataProperty = DataProperty(Namespaces.OWL
			+ "topDataProperty");

	public static final OWLDataProperty bottomDataProperty = DataProperty(Namespaces.OWL
			+ "bottomDataProperty");

	
	public static OWLOntology Ontology(Collection<? extends OWLAxiom> axioms) {
		IRI uri = IRI.create( "http://www.example.org/ontology" + new UID() );

		return Ontology( axioms, uri );
	}

	public static OWLOntology Ontology(Collection<? extends OWLAxiom> axioms, IRI iri) {
		OWLOntology ontology;
		try {
			ontology = manager.createOntology( iri );
			OntologyUtils.addAxioms( ontology, axioms );
		} catch( OWLOntologyCreationException e ) {
			throw new RuntimeException( e );
		} catch( OWLOntologyChangeException e ) {
			throw new RuntimeException( e );
		}

		return ontology;
	}

	public static OWLOntology Ontology(OWLAxiom... axioms) {
		return Ontology( Arrays.asList( axioms ) );
	}
	
	public static OWLDataAllValuesFrom all(OWLDataPropertyExpression property, OWLDataRange datatype) {
		return factory.getOWLDataAllValuesFrom( property, datatype );
	}

	public static OWLObjectAllValuesFrom all(OWLObjectPropertyExpression property,
			OWLClassExpression description) {
		return factory.getOWLObjectAllValuesFrom( property, description );
	}

	/**
	 * @deprecated Use
	 *             {@link #all(OWLObjectPropertyExpression, OWLClassExpression)}
	 *             instead
	 */
	public static OWLObjectAllValuesFrom allValuesFrom(OWLObjectPropertyExpression property,
			OWLClassExpression description) {
		return factory.getOWLObjectAllValuesFrom( property, description );
	}

	public static OWLObjectIntersectionOf and(OWLClassExpression... descriptions) {
		return factory.getOWLObjectIntersectionOf( set( descriptions ) );
	}

	public static OWLObjectIntersectionOf and(Set<? extends OWLClassExpression> descriptions) {
		return factory.getOWLObjectIntersectionOf( descriptions );
	}

	public static OWLDataIntersectionOf dataAnd(OWLDataRange... descriptions) {
		return factory.getOWLDataIntersectionOf( set( descriptions ) );
	}

	public static OWLDataIntersectionOf dataAnd(Set<? extends OWLDataRange> descriptions) {
		return factory.getOWLDataIntersectionOf( descriptions );
	}

	public static OWLAnnotationAssertionAxiom annotation(OWLEntity entity, OWLAnnotation annotation) {
		return factory.getOWLAnnotationAssertionAxiom( entity.getIRI(), annotation );
	}

	public static OWLAnnotationAssertionAxiom annotation(OWLEntity entity, IRI annotationIRI,
			OWLLiteral constant) {
		return factory.getOWLAnnotationAssertionAxiom( factory
				.getOWLAnnotationProperty( annotationIRI ), entity.getIRI(), constant );
	}

	public static OWLAnnotationAssertionAxiom annotation(OWLEntity entity, IRI annotationIRI,
			OWLIndividual individual) {
		if( individual.isAnonymous() )
			return factory.getOWLAnnotationAssertionAxiom( entity.getIRI(), factory
					.getOWLAnnotation( factory.getOWLAnnotationProperty( annotationIRI ),
							individual.asOWLAnonymousIndividual() ) );
		else
			return factory.getOWLAnnotationAssertionAxiom( entity.getIRI(), 
					factory.getOWLAnnotation( factory.getOWLAnnotationProperty( annotationIRI ),
							individual.asOWLNamedIndividual().getIRI() ) );
	}

	public static OWLAnonymousIndividual AnonymousIndividual() {
		return factory.getOWLAnonymousIndividual();
	}
	
	public static OWLAnonymousIndividual AnonymousIndividual(String anonId) {
		return factory.getOWLAnonymousIndividual( anonId );
	}

	public static OWLAsymmetricObjectPropertyAxiom asymmetric(OWLObjectPropertyExpression p) {
		return factory.getOWLAsymmetricObjectPropertyAxiom( p );
	}

	public static OWLClass Class(String iri) {
		return factory.getOWLClass( IRI.create( iri ) );
	}

	public static OWLClass Class(IRI iri) {
		return factory.getOWLClass( iri );
	}

	public static OWLClassAssertionAxiom classAssertion(OWLIndividual ind, OWLClassExpression desc) {
		return factory.getOWLClassAssertionAxiom( desc, ind );
	}

	public static OWLAnnotationAssertionAxiom comment(OWLEntity entity, String comment) {
		return factory.getOWLAnnotationAssertionAxiom( entity.getIRI(), factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty( OWLRDFVocabulary.RDFS_COMMENT.getIRI() ), 
				factory.getOWLLiteral( comment ) ) );
		
	}

	/**
	 * @deprecated Use {@link #not(OWLClassExpression)} instead
	 */
	public static OWLObjectComplementOf complementOf(OWLClassExpression description) {
		return factory.getOWLObjectComplementOf( description );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:boolean.
	 */
	public static OWLLiteral constant(boolean value) {
		return factory.getOWLLiteral( value );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:double.
	 */
	public static OWLLiteral constant(double value) {
		return factory.getOWLLiteral( value );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:float.
	 */
	public static OWLLiteral constant(float value) {
		return factory.getOWLLiteral( value );
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 * 
	 * @param value
	 *            The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical
	 *         value of the integer, and whose data type is xsd:integer.
	 */
	public static OWLLiteral constant(int value) {
		return factory.getOWLLiteral( value );
	}

	public static OWLLiteral constant(String value) {
		return factory.getOWLLiteral( value );
	}

	public static OWLLiteral constant(String value, OWLDatatype datatype) {
		return factory.getOWLLiteral( value, datatype );
	}

	public static OWLLiteral constant(String value, String lang) {
		return factory.getOWLLiteral( value, lang );
	}

	public static OWLDataProperty DataProperty(String iri) {
		return factory.getOWLDataProperty( IRI.create( iri ) );
	}

	public static OWLDataProperty DataProperty(IRI iri) {
		return factory.getOWLDataProperty( iri );
	}

	public static OWLDatatype Datatype(String datatypeURI) {
		return factory.getOWLDatatype( IRI.create( datatypeURI ) );
	}

	public static OWLDatatype Datatype(IRI datatypeIRI) {
		return factory.getOWLDatatype( datatypeIRI );
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

	public static OWLDisjointClassesAxiom disjointClasses(OWLClassExpression d1,
			OWLClassExpression d2) {
		return factory.getOWLDisjointClassesAxiom( set( d1, d2 ) );
	}

	public static OWLDisjointClassesAxiom disjointClasses(
			Set<? extends OWLClassExpression> descriptions) {
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

	public static OWLDataPropertyDomainAxiom domain(OWLDataPropertyExpression p,
			OWLClassExpression d) {
		return factory.getOWLDataPropertyDomainAxiom( p, d );
	}

	public static OWLObjectPropertyDomainAxiom domain(OWLObjectPropertyExpression p,
			OWLClassExpression d) {
		return factory.getOWLObjectPropertyDomainAxiom( p, d );
	}
	
	public static OWLDatatypeDefinitionAxiom datatypeDefinition(OWLDatatype d1,
			OWLDataRange d2) {
		return factory.getOWLDatatypeDefinitionAxiom( d1, d2 );
	}
	
	public static OWLEquivalentClassesAxiom equivalentClasses(OWLClassExpression d1,
			OWLClassExpression d2) {
		return factory.getOWLEquivalentClassesAxiom( set( d1, d2 ) );
	}

	public static OWLEquivalentClassesAxiom equivalentClasses(
			Set<? extends OWLClassExpression> descriptions) {
		return factory.getOWLEquivalentClassesAxiom( descriptions );
	}

	public static OWLEquivalentDataPropertiesAxiom equivalentDataProperties(OWLDataPropertyExpression p1,
			OWLDataPropertyExpression p2) {
		return factory.getOWLEquivalentDataPropertiesAxiom( set( p1, p2 ) );
	}

	public static OWLEquivalentDataPropertiesAxiom equivalentDataProperties(
			Set<? extends OWLDataPropertyExpression> properties) {
		return factory.getOWLEquivalentDataPropertiesAxiom( properties );
	}
	
	public static OWLEquivalentObjectPropertiesAxiom equivalentProperties(OWLObjectPropertyExpression p1,
			OWLObjectPropertyExpression p2) {
		return factory.getOWLEquivalentObjectPropertiesAxiom( set( p1, p2 ) );
	}

	public static OWLEquivalentObjectPropertiesAxiom equivalentProperties(
			Set<? extends OWLObjectPropertyExpression> properties) {
		return factory.getOWLEquivalentObjectPropertiesAxiom( properties );
	} 
	
	public static OWLClassExpression exactly(OWLDataProperty p, int card) {
		return factory.getOWLDataExactCardinality( card, p );
	}

	public static OWLClassExpression exactly(OWLDataProperty p, int card, OWLDataRange d) {
		return factory.getOWLDataExactCardinality( card, p, d);
	}

	public static OWLClassExpression exactly(OWLObjectProperty p, int card) {
		return factory.getOWLObjectExactCardinality( card, p);
	}

	public static OWLClassExpression exactly(OWLObjectProperty p, int card, OWLClassExpression desc) {
		return factory.getOWLObjectExactCardinality( card, p, desc );
	}

	public static OWLFunctionalDataPropertyAxiom functional(OWLDataPropertyExpression p) {
		return factory.getOWLFunctionalDataPropertyAxiom( p );
	}

	public static OWLFunctionalObjectPropertyAxiom functional(OWLObjectPropertyExpression p) {
		return factory.getOWLFunctionalObjectPropertyAxiom( p );
	}

	public static OWLNamedIndividual Individual(String iri) {
		return factory.getOWLNamedIndividual( IRI.create( iri ) );
	}

	public static OWLNamedIndividual Individual(IRI iri) {
		return factory.getOWLNamedIndividual( iri );
	}

	
	public static OWLObjectInverseOf inverse(OWLObjectPropertyExpression p) {
		return factory.getOWLObjectInverseOf( p );
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

	public static OWLAnnotationAssertionAxiom label(OWLEntity entity, String label) {
		return factory.getOWLAnnotationAssertionAxiom( 
				factory.getOWLAnnotationProperty( OWLRDFVocabulary.RDFS_LABEL.getIRI() ),
				entity.getIRI(), factory.getOWLLiteral( label ) );
	}

	public static OWLFacetRestriction length(int constant) {
		return factory.getOWLFacetRestriction( OWLFacet.LENGTH, constant);
	}

	public static OWLFacetRestriction length(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.LENGTH, constant );
	}

	public static OWLDataMaxCardinality max(OWLDataPropertyExpression p, int max) {
		return factory.getOWLDataMaxCardinality( max, p );
	}

	public static OWLDataMaxCardinality max(OWLDataPropertyExpression p, int max,
			OWLDataRange filler) {
		return factory.getOWLDataMaxCardinality( max, p, filler );
	}

	public static OWLObjectMaxCardinality max(OWLObjectPropertyExpression p, int max) {
		return factory.getOWLObjectMaxCardinality( max, p );
	}

	public static OWLObjectMaxCardinality max(OWLObjectPropertyExpression p, int max,
			OWLClassExpression filler) {
		return factory.getOWLObjectMaxCardinality( max, p, filler );
	}

	public static OWLFacetRestriction maxExclusive(double constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxExclusive(float constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxExclusive(int constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxExclusive(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxInclusive(double constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxInclusive(float constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxInclusive(int constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxInclusive(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction maxLength(int constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_LENGTH, constant );
	}

	public static OWLFacetRestriction maxLength(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MAX_LENGTH, constant );
	}

	public static OWLDataMinCardinality min(OWLDataPropertyExpression p, int min) {
		return factory.getOWLDataMinCardinality( min, p );
	}

	public static OWLDataMinCardinality min(OWLDataPropertyExpression p, int min,
			OWLDataRange filler) {
		return factory.getOWLDataMinCardinality( min, p, filler );
	}

	public static OWLObjectMinCardinality min(OWLObjectPropertyExpression p, int min) {
		return factory.getOWLObjectMinCardinality( min, p );
	}

	public static OWLObjectMinCardinality min(OWLObjectPropertyExpression p, int min,
			OWLClassExpression filler) {
		return factory.getOWLObjectMinCardinality( min, p, filler );
	}

	public static OWLFacetRestriction minExclusive(double constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction minExclusive(float constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction minExclusive(int constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction minExclusive(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_EXCLUSIVE, constant );
	}

	public static OWLFacetRestriction minInclusive(double constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction minInclusive(float constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction minInclusive(int constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction minInclusive(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_INCLUSIVE, constant );
	}

	public static OWLFacetRestriction minLength(int constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_LENGTH, constant );
	}

	public static OWLFacetRestriction minLength(OWLLiteral constant) {
		return factory.getOWLFacetRestriction(
				OWLFacet.MIN_LENGTH, constant );
	}

	public static OWLObjectComplementOf not(OWLClassExpression description) {
		return factory.getOWLObjectComplementOf( description );
	}

	public static OWLDataComplementOf dataNot(OWLDataRange description) {
		return factory.getOWLDataComplementOf( description );
	}

	public static OWLObjectProperty ObjectProperty(String iri) {
		return factory.getOWLObjectProperty( IRI.create( iri ) );
	}

	public static OWLObjectProperty ObjectProperty(IRI iri) {
		return factory.getOWLObjectProperty( iri );
	}

	public static OWLDataOneOf oneOf(OWLLiteral... constants) {
		return factory.getOWLDataOneOf( set( constants ) );
	}

	public static OWLObjectOneOf oneOf(OWLIndividual... individuals) {
		return factory.getOWLObjectOneOf( set( individuals ) );
	}

	public static OWLDataOneOf dataOneOf(Set<? extends OWLLiteral> constants) {
		return factory.getOWLDataOneOf( constants );
	}

	public static OWLObjectOneOf objectOneOf(Set<OWLIndividual> individuals) {
		return factory.getOWLObjectOneOf( individuals );
	}

	public static OWLDataAllValuesFrom only(OWLDataPropertyExpression property,
			OWLDataRange datatype) {
		return factory.getOWLDataAllValuesFrom( property, datatype );
	}

	public static OWLObjectAllValuesFrom only(OWLObjectPropertyExpression property,
			OWLClassExpression description) {
		return factory.getOWLObjectAllValuesFrom( property, description );
	}

	public static OWLObjectUnionOf or(OWLClassExpression... descriptions) {
		return factory.getOWLObjectUnionOf( set( descriptions ) );
	}

	public static OWLObjectUnionOf or(Set<? extends OWLClassExpression> descriptions) {
		return factory.getOWLObjectUnionOf( descriptions );
	}	

	public static OWLDataUnionOf dataOr(OWLDataRange... descriptions) {
		return factory.getOWLDataUnionOf( set( descriptions ) );
	}

	public static OWLDataUnionOf dataOr(Set<? extends OWLDataRange> descriptions) {
		return factory.getOWLDataUnionOf( descriptions );
	}

	public static OWLDataPropertyAssertionAxiom propertyAssertion(OWLIndividual subj,
			OWLDataPropertyExpression pred, OWLLiteral obj) {
		return factory.getOWLDataPropertyAssertionAxiom( pred, subj, obj );
	}

	public static OWLObjectPropertyAssertionAxiom propertyAssertion(OWLIndividual subj,
			OWLObjectPropertyExpression pred, OWLIndividual obj) {
		return factory.getOWLObjectPropertyAssertionAxiom( pred, subj, obj );
	}

	public static OWLDataPropertyRangeAxiom range(OWLDataPropertyExpression p, OWLDataRange d) {
		return factory.getOWLDataPropertyRangeAxiom( p, d );
	}

	public static OWLObjectPropertyRangeAxiom range(OWLObjectPropertyExpression p,
			OWLClassExpression d) {
		return factory.getOWLObjectPropertyRangeAxiom( p, d );
	}

	public static OWLReflexiveObjectPropertyAxiom reflexive(OWLObjectPropertyExpression p) {
		return factory.getOWLReflexiveObjectPropertyAxiom( p );
	}

	public static OWLDataRange restrict(OWLDatatype datatype,
			OWLFacetRestriction... restrictions) {
		return factory.getOWLDatatypeRestriction( datatype, restrictions );
	}

	public static OWLDataRange restrict(OWLDatatype datatype,
			Set<OWLFacetRestriction> restrictions) {
		return factory.getOWLDatatypeRestriction( datatype, restrictions );
	}

	public static OWLSameIndividualAxiom sameAs(OWLIndividual i1, OWLIndividual i2) {
		return factory.getOWLSameIndividualAxiom( set( i1, i2 ) );
	}

	public static OWLSameIndividualAxiom sameAs(Set<OWLIndividual> inds) {
		return factory.getOWLSameIndividualAxiom( inds );
	}

	public static OWLObjectHasSelf self(OWLObjectPropertyExpression p) {
		return factory.getOWLObjectHasSelf( p );
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

	public static OWLDataSomeValuesFrom some(OWLDataPropertyExpression property,
			OWLDataRange datatype) {
		return factory.getOWLDataSomeValuesFrom( property, datatype );
	}

	public static OWLObjectSomeValuesFrom some(OWLObjectPropertyExpression property,
			OWLClassExpression description) {
		return factory.getOWLObjectSomeValuesFrom( property, description );
	}

	public static OWLSubClassOfAxiom subClassOf(OWLClassExpression sub, OWLClassExpression sup) {
		return factory.getOWLSubClassOfAxiom( sub, sup );
	}

	public static OWLSubDataPropertyOfAxiom subPropertyOf(OWLDataPropertyExpression sub,
			OWLDataPropertyExpression sup) {
		return factory.getOWLSubDataPropertyOfAxiom( sub, sup );
	}

	public static OWLSubObjectPropertyOfAxiom subPropertyOf(OWLObjectPropertyExpression sub,
			OWLObjectPropertyExpression sup) {
		return factory.getOWLSubObjectPropertyOfAxiom( sub, sup );
	}

	public static OWLSubPropertyChainOfAxiom subPropertyOf(
			OWLObjectPropertyExpression[] subChain, OWLObjectPropertyExpression sup) {
		return factory.getOWLSubPropertyChainOfAxiom( Arrays.asList( subChain ), sup );
	}

	public static OWLSymmetricObjectPropertyAxiom symmetric(OWLObjectPropertyExpression p) {
		return factory.getOWLSymmetricObjectPropertyAxiom( p );
	}

	public static OWLTransitiveObjectPropertyAxiom transitive(OWLObjectPropertyExpression p) {
		return factory.getOWLTransitiveObjectPropertyAxiom( p );
	}

	public static OWLDataHasValue value(OWLDataPropertyExpression property, OWLLiteral constant) {
		return factory.getOWLDataHasValue( property, constant );
	}

	public static OWLObjectHasValue value(OWLObjectPropertyExpression property, OWLIndividual value) {
		return factory.getOWLObjectHasValue( property, value );
	}
}
