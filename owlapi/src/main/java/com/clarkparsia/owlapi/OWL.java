// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi;

import com.intrinsec.owlapi.facet.FacetFactoryOWL;
import com.intrinsec.owlapi.facet.FacetManagerOWL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;
import openllet.shared.tools.Log;
import openllet.shared.tools.Logging;
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
 * Description: Utility class to generate OWL concepts in a concise and readable way.
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
public class OWL implements FacetManagerOWL, FacetFactoryOWL, Logging
{
	private static final Logger _logger = Log.getLogger(OWL.class);

	public static final OWLOntologyManager _manager = OWLManager.createOWLOntologyManager();

	public static final OWLDataFactory _factory = _manager.getOWLDataFactory();

	public static final OWLClass Nothing = _factory.getOWLNothing();

	public static final OWLClass Thing = _factory.getOWLThing();

	public static final OWLObjectProperty topObjectProperty = ObjectProperty(Namespaces.OWL + "topObjectProperty");

	public static final OWLObjectProperty bottomObjectProperty = ObjectProperty(Namespaces.OWL + "bottomObjectProperty");

	public static final OWLDataProperty topDataProperty = DataProperty(Namespaces.OWL + "topDataProperty");

	public static final OWLDataProperty bottomDataProperty = DataProperty(Namespaces.OWL + "bottomDataProperty");

	public static final OWLLiteral TRUE = _factory.getOWLLiteral(true);

	public static final OWLLiteral FALSE = _factory.getOWLLiteral(false);

	@Override
	public Logger getLogger()
	{
		return _logger;
	}

	@Override
	public OWLDataFactory getFactory()
	{
		return _factory;
	}

	@Override
	public OWLOntologyManager getManager()
	{
		return _manager;
	}

	public static OWLOntology Ontology(final Collection<? extends OWLAxiom> axioms)
	{
		return Ontology(axioms.stream());
	}

	public static OWLOntology Ontology(final Stream<? extends OWLAxiom> axioms)
	{
		return Ontology(axioms, IRI.create("http://www.example.org/ontology" + UUID.randomUUID()));
	}

	public static OWLOntology Ontology(final Collection<? extends OWLAxiom> axioms, final IRI iri)
	{
		return Ontology(axioms.stream(), iri);
	}

	public static OWLOntology Ontology(final Stream<? extends OWLAxiom> axioms, final IRI iri)
	{
		OWLOntology ontology;
		try
		{
			ontology = _manager.createOntology(iri);
			OntologyUtils.addAxioms(ontology, axioms);
		}
		catch (final OWLOntologyCreationException e)
		{
			throw new RuntimeException(e);
		}
		catch (final OWLOntologyChangeException e)
		{
			throw new RuntimeException(e);
		}

		return ontology;
	}

	public static OWLOntology Ontology(final OWLAxiom... axioms)
	{
		return Ontology(Arrays.asList(axioms));
	}

	public static OWLDataAllValuesFrom all(final OWLDataPropertyExpression property, final OWLDataRange datatype)
	{
		return _factory.getOWLDataAllValuesFrom(property, datatype);
	}

	public static OWLObjectAllValuesFrom all(final OWLObjectPropertyExpression property, final OWLClassExpression description)
	{
		return _factory.getOWLObjectAllValuesFrom(property, description);
	}

	/**
	 * @deprecated 2.5.1 Use {@link #all(OWLObjectPropertyExpression, OWLClassExpression)} instead
	 */
	@Deprecated
	public static OWLObjectAllValuesFrom allValuesFrom(final OWLObjectPropertyExpression property, final OWLClassExpression description)
	{
		return _factory.getOWLObjectAllValuesFrom(property, description);
	}

	public static OWLObjectIntersectionOf and(final OWLClassExpression... descriptions)
	{
		return _factory.getOWLObjectIntersectionOf(set(descriptions));
	}

	public static OWLObjectIntersectionOf and(final Set<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLObjectIntersectionOf(descriptions);
	}

	public static OWLObjectIntersectionOf and(final Stream<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLObjectIntersectionOf(descriptions);
	}

	public static OWLDataIntersectionOf dataAnd(final OWLDataRange... descriptions)
	{
		return _factory.getOWLDataIntersectionOf(set(descriptions));
	}

	public static OWLDataIntersectionOf dataAnd(final Set<? extends OWLDataRange> descriptions)
	{
		return _factory.getOWLDataIntersectionOf(descriptions);
	}

	public static OWLAnnotationAssertionAxiom annotation(final OWLEntity entity, final OWLAnnotation annotation)
	{
		return _factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), annotation);
	}

	public static OWLAnnotationAssertionAxiom annotation(final OWLEntity entity, final IRI annotationIRI, final OWLLiteral constant)
	{
		return _factory.getOWLAnnotationAssertionAxiom(_factory.getOWLAnnotationProperty(annotationIRI), entity.getIRI(), constant);
	}

	public static OWLAnnotationAssertionAxiom annotation(final OWLEntity entity, final IRI annotationIRI, final OWLIndividual individual)
	{
		if (individual.isAnonymous())
			return _factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), _factory.getOWLAnnotation(_factory.getOWLAnnotationProperty(annotationIRI), individual.asOWLAnonymousIndividual()));
		else
			return _factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), _factory.getOWLAnnotation(_factory.getOWLAnnotationProperty(annotationIRI), individual.asOWLNamedIndividual().getIRI()));
	}

	public static OWLAnonymousIndividual AnonymousIndividual()
	{
		return _factory.getOWLAnonymousIndividual();
	}

	public static OWLAnonymousIndividual AnonymousIndividual(final String anonId)
	{
		return _factory.getOWLAnonymousIndividual(anonId);
	}

	public static OWLAsymmetricObjectPropertyAxiom asymmetric(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLAsymmetricObjectPropertyAxiom(p);
	}

	public static OWLClass Class(final String iri)
	{
		return _factory.getOWLClass(IRI.create(iri));
	}

	public static OWLClass Class(final IRI iri)
	{
		return _factory.getOWLClass(iri);
	}

	public static OWLClassAssertionAxiom classAssertion(final OWLIndividual ind, final OWLClassExpression desc)
	{
		return _factory.getOWLClassAssertionAxiom(desc, ind);
	}

	public static OWLAnnotationAssertionAxiom comment(final OWLEntity entity, final String comment)
	{
		return _factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), _factory.getOWLAnnotation(_factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI()), _factory.getOWLLiteral(comment)));

	}

	/**
	 * @deprecated 2.5.1 Use {@link #not(OWLClassExpression)} instead
	 */
	@Deprecated
	public static OWLObjectComplementOf complementOf(final OWLClassExpression description)
	{
		return _factory.getOWLObjectComplementOf(description);
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 *
	 * @param value The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical value of the integer, and whose _data type is xsd:boolean.
	 */
	public static OWLLiteral constant(final boolean value)
	{
		return _factory.getOWLLiteral(value);
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 *
	 * @param value The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical value of the integer, and whose _data type is xsd:double.
	 */
	public static OWLLiteral constant(final double value)
	{
		return _factory.getOWLLiteral(value);
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 *
	 * @param value The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical value of the integer, and whose _data type is xsd:float.
	 */
	public static OWLLiteral constant(final float value)
	{
		return _factory.getOWLLiteral(value);
	}

	/**
	 * Convenience method that obtains a constant typed as an integer.
	 *
	 * @param value The value of the constant
	 * @return An <code>OWLLiteral</code> whose literal is the lexical value of the integer, and whose _data type is xsd:integer.
	 */
	public static OWLLiteral constant(final int value)
	{
		return _factory.getOWLLiteral(value);
	}

	public static OWLLiteral constant(final String value)
	{
		return _factory.getOWLLiteral(value);
	}

	public static OWLLiteral constant(final String value, final OWLDatatype datatype)
	{
		return _factory.getOWLLiteral(value, datatype);
	}

	public static OWLLiteral constant(final String value, final String lang)
	{
		return _factory.getOWLLiteral(value, lang);
	}

	public static OWLDataProperty DataProperty(final String iri)
	{
		return _factory.getOWLDataProperty(IRI.create(iri));
	}

	public static OWLDataProperty DataProperty(final IRI iri)
	{
		return _factory.getOWLDataProperty(iri);
	}

	public static OWLDatatype Datatype(final String datatypeURI)
	{
		return _factory.getOWLDatatype(IRI.create(datatypeURI));
	}

	public static OWLDatatype Datatype(final IRI datatypeIRI)
	{
		return _factory.getOWLDatatype(datatypeIRI);
	}

	public static OWLDeclarationAxiom declaration(final OWLEntity entity)
	{
		return _factory.getOWLDeclarationAxiom(entity);
	}

	public static OWLDifferentIndividualsAxiom differentFrom(final OWLIndividual i1, final OWLIndividual i2)
	{
		return _factory.getOWLDifferentIndividualsAxiom(set(i1, i2));
	}

	public static OWLDifferentIndividualsAxiom differentFrom(final Set<OWLIndividual> inds)
	{
		return _factory.getOWLDifferentIndividualsAxiom(inds);
	}

	public static OWLDisjointClassesAxiom disjointClasses(final OWLClassExpression d1, final OWLClassExpression d2)
	{
		return _factory.getOWLDisjointClassesAxiom(set(d1, d2));
	}

	public static OWLDisjointClassesAxiom disjointClasses(final Set<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLDisjointClassesAxiom(descriptions);
	}

	public static OWLDisjointDataPropertiesAxiom disjointProperties(final OWLDataPropertyExpression d1, final OWLDataPropertyExpression d2)
	{
		return _factory.getOWLDisjointDataPropertiesAxiom(set(d1, d2));
	}

	public static OWLDisjointObjectPropertiesAxiom disjointProperties(final OWLObjectPropertyExpression d1, final OWLObjectPropertyExpression d2)
	{
		return _factory.getOWLDisjointObjectPropertiesAxiom(set(d1, d2));
	}

	public static OWLDataPropertyDomainAxiom domain(final OWLDataPropertyExpression p, final OWLClassExpression d)
	{
		return _factory.getOWLDataPropertyDomainAxiom(p, d);
	}

	public static OWLObjectPropertyDomainAxiom domain(final OWLObjectPropertyExpression p, final OWLClassExpression d)
	{
		return _factory.getOWLObjectPropertyDomainAxiom(p, d);
	}

	public static OWLDatatypeDefinitionAxiom datatypeDefinition(final OWLDatatype d1, final OWLDataRange d2)
	{
		return _factory.getOWLDatatypeDefinitionAxiom(d1, d2);
	}

	public static OWLEquivalentClassesAxiom equivalentClasses(final OWLClassExpression d1, final OWLClassExpression d2)
	{
		return _factory.getOWLEquivalentClassesAxiom(set(d1, d2));
	}

	public static OWLEquivalentClassesAxiom equivalentClasses(final Set<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLEquivalentClassesAxiom(descriptions);
	}

	public static OWLEquivalentClassesAxiom equivalentClasses(final Stream<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLEquivalentClassesAxiom(descriptions);
	}

	public static OWLEquivalentDataPropertiesAxiom equivalentDataProperties(final OWLDataPropertyExpression p1, final OWLDataPropertyExpression p2)
	{
		return _factory.getOWLEquivalentDataPropertiesAxiom(set(p1, p2));
	}

	public static OWLEquivalentDataPropertiesAxiom equivalentDataProperties(final Set<? extends OWLDataPropertyExpression> properties)
	{
		return _factory.getOWLEquivalentDataPropertiesAxiom(properties);
	}

	public static OWLEquivalentObjectPropertiesAxiom equivalentProperties(final OWLObjectPropertyExpression p1, final OWLObjectPropertyExpression p2)
	{
		return _factory.getOWLEquivalentObjectPropertiesAxiom(set(p1, p2));
	}

	public static OWLEquivalentObjectPropertiesAxiom equivalentProperties(final Set<? extends OWLObjectPropertyExpression> properties)
	{
		return _factory.getOWLEquivalentObjectPropertiesAxiom(properties);
	}

	public static OWLClassExpression exactly(final OWLDataProperty p, final int card)
	{
		return _factory.getOWLDataExactCardinality(card, p);
	}

	public static OWLClassExpression exactly(final OWLDataProperty p, final int card, final OWLDataRange d)
	{
		return _factory.getOWLDataExactCardinality(card, p, d);
	}

	public static OWLClassExpression exactly(final OWLObjectProperty p, final int card)
	{
		return _factory.getOWLObjectExactCardinality(card, p);
	}

	public static OWLClassExpression exactly(final OWLObjectProperty p, final int card, final OWLClassExpression desc)
	{
		return _factory.getOWLObjectExactCardinality(card, p, desc);
	}

	public static OWLFunctionalDataPropertyAxiom functional(final OWLDataPropertyExpression p)
	{
		return _factory.getOWLFunctionalDataPropertyAxiom(p);
	}

	public static OWLFunctionalObjectPropertyAxiom functional(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLFunctionalObjectPropertyAxiom(p);
	}

	public static OWLNamedIndividual Individual(final String iri)
	{
		return _factory.getOWLNamedIndividual(IRI.create(iri));
	}

	public static OWLNamedIndividual Individual(final IRI iri)
	{
		return _factory.getOWLNamedIndividual(iri);
	}

	public static OWLObjectInverseOf inverse(final OWLObjectProperty p)
	{
		return _factory.getOWLObjectInverseOf(p);
	}

	public static OWLInverseFunctionalObjectPropertyAxiom inverseFunctional(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLInverseFunctionalObjectPropertyAxiom(p);
	}

	public static OWLInverseObjectPropertiesAxiom inverseProperties(final OWLObjectPropertyExpression p1, final OWLObjectPropertyExpression p2)
	{
		return _factory.getOWLInverseObjectPropertiesAxiom(p1, p2);
	}

	public static OWLIrreflexiveObjectPropertyAxiom irreflexive(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLIrreflexiveObjectPropertyAxiom(p);
	}

	public static OWLAnnotationAssertionAxiom label(final OWLEntity entity, final String label)
	{
		return _factory.getOWLAnnotationAssertionAxiom(_factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), entity.getIRI(), _factory.getOWLLiteral(label));
	}

	public static OWLFacetRestriction length(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.LENGTH, constant);
	}

	public static OWLFacetRestriction length(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.LENGTH, constant);
	}

	public static OWLDataMaxCardinality max(final OWLDataPropertyExpression p, final int max)
	{
		return _factory.getOWLDataMaxCardinality(max, p);
	}

	public static OWLDataMaxCardinality max(final OWLDataPropertyExpression p, final int max, final OWLDataRange filler)
	{
		return _factory.getOWLDataMaxCardinality(max, p, filler);
	}

	public static OWLObjectMaxCardinality max(final OWLObjectPropertyExpression p, final int max)
	{
		return _factory.getOWLObjectMaxCardinality(max, p);
	}

	public static OWLObjectMaxCardinality max(final OWLObjectPropertyExpression p, final int max, final OWLClassExpression filler)
	{
		return _factory.getOWLObjectMaxCardinality(max, p, filler);
	}

	public static OWLFacetRestriction maxExclusive(final double constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxExclusive(final float constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxExclusive(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxExclusive(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxInclusive(final double constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxInclusive(final float constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxInclusive(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxInclusive(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction maxLength(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_LENGTH, constant);
	}

	public static OWLFacetRestriction maxLength(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MAX_LENGTH, constant);
	}

	public static OWLDataMinCardinality min(final OWLDataPropertyExpression p, final int min)
	{
		return _factory.getOWLDataMinCardinality(min, p);
	}

	public static OWLDataMinCardinality min(final OWLDataPropertyExpression p, final int min, final OWLDataRange filler)
	{
		return _factory.getOWLDataMinCardinality(min, p, filler);
	}

	public static OWLObjectMinCardinality min(final OWLObjectPropertyExpression p, final int min)
	{
		return _factory.getOWLObjectMinCardinality(min, p);
	}

	public static OWLObjectMinCardinality min(final OWLObjectPropertyExpression p, final int min, final OWLClassExpression filler)
	{
		return _factory.getOWLObjectMinCardinality(min, p, filler);
	}

	public static OWLFacetRestriction minExclusive(final double constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction minExclusive(final float constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction minExclusive(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction minExclusive(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE, constant);
	}

	public static OWLFacetRestriction minInclusive(final double constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction minInclusive(final float constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction minInclusive(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction minInclusive(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, constant);
	}

	public static OWLFacetRestriction minLength(final int constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_LENGTH, constant);
	}

	public static OWLFacetRestriction minLength(final OWLLiteral constant)
	{
		return _factory.getOWLFacetRestriction(OWLFacet.MIN_LENGTH, constant);
	}

	public static OWLObjectComplementOf not(final OWLClassExpression description)
	{
		return _factory.getOWLObjectComplementOf(description);
	}

	public static OWLDataComplementOf dataNot(final OWLDataRange description)
	{
		return _factory.getOWLDataComplementOf(description);
	}

	public static OWLObjectProperty ObjectProperty(final String iri)
	{
		return _factory.getOWLObjectProperty(IRI.create(iri));
	}

	public static OWLObjectProperty ObjectProperty(final IRI iri)
	{
		return _factory.getOWLObjectProperty(iri);
	}

	public static OWLDataOneOf oneOf(final OWLLiteral... constants)
	{
		return _factory.getOWLDataOneOf(set(constants));
	}

	public static OWLObjectOneOf oneOf(final OWLIndividual... individuals)
	{
		return _factory.getOWLObjectOneOf(set(individuals));
	}

	public static OWLDataOneOf dataOneOf(final Set<? extends OWLLiteral> constants)
	{
		return _factory.getOWLDataOneOf(constants);
	}

	public static OWLObjectOneOf objectOneOf(final Set<OWLIndividual> individuals)
	{
		return _factory.getOWLObjectOneOf(individuals);
	}

	public static OWLObjectOneOf objectOneOf(final Stream<OWLIndividual> individuals)
	{
		return _factory.getOWLObjectOneOf(individuals);
	}

	public static OWLDataAllValuesFrom only(final OWLDataPropertyExpression property, final OWLDataRange datatype)
	{
		return _factory.getOWLDataAllValuesFrom(property, datatype);
	}

	public static OWLObjectAllValuesFrom only(final OWLObjectPropertyExpression property, final OWLClassExpression description)
	{
		return _factory.getOWLObjectAllValuesFrom(property, description);
	}

	public static OWLObjectUnionOf or(final OWLClassExpression... descriptions)
	{
		return _factory.getOWLObjectUnionOf(set(descriptions));
	}

	public static OWLObjectUnionOf or(final Set<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLObjectUnionOf(descriptions);
	}

	public static OWLObjectUnionOf or(final Stream<? extends OWLClassExpression> descriptions)
	{
		return _factory.getOWLObjectUnionOf(descriptions);
	}

	public static OWLDataUnionOf dataOr(final OWLDataRange... descriptions)
	{
		return _factory.getOWLDataUnionOf(set(descriptions));
	}

	public static OWLDataUnionOf dataOr(final Set<? extends OWLDataRange> descriptions)
	{
		return _factory.getOWLDataUnionOf(descriptions);
	}

	public static OWLDataUnionOf dataOr(final Stream<? extends OWLDataRange> descriptions)
	{
		return _factory.getOWLDataUnionOf(descriptions);
	}

	public static OWLDataPropertyAssertionAxiom propertyAssertion(final OWLIndividual subj, final OWLDataPropertyExpression pred, final OWLLiteral obj)
	{
		return _factory.getOWLDataPropertyAssertionAxiom(pred, subj, obj);
	}

	public static OWLObjectPropertyAssertionAxiom propertyAssertion(final OWLIndividual subj, final OWLObjectPropertyExpression pred, final OWLIndividual obj)
	{
		return _factory.getOWLObjectPropertyAssertionAxiom(pred, subj, obj);
	}

	public static OWLDataPropertyRangeAxiom range(final OWLDataPropertyExpression p, final OWLDataRange d)
	{
		return _factory.getOWLDataPropertyRangeAxiom(p, d);
	}

	public static OWLObjectPropertyRangeAxiom range(final OWLObjectPropertyExpression p, final OWLClassExpression d)
	{
		return _factory.getOWLObjectPropertyRangeAxiom(p, d);
	}

	public static OWLReflexiveObjectPropertyAxiom reflexive(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLReflexiveObjectPropertyAxiom(p);
	}

	public static OWLDataRange restrict(final OWLDatatype datatype, final OWLFacetRestriction... restrictions)
	{
		return _factory.getOWLDatatypeRestriction(datatype, restrictions);
	}

	public static OWLDataRange restrict(final OWLDatatype datatype, final Set<OWLFacetRestriction> restrictions)
	{
		return _factory.getOWLDatatypeRestriction(datatype, restrictions);
	}

	public static OWLSameIndividualAxiom sameAs(final OWLIndividual i1, final OWLIndividual i2)
	{
		return _factory.getOWLSameIndividualAxiom(set(i1, i2));
	}

	public static OWLSameIndividualAxiom sameAs(final Set<OWLIndividual> inds)
	{
		return _factory.getOWLSameIndividualAxiom(inds);
	}

	public static OWLObjectHasSelf self(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLObjectHasSelf(p);
	}

	@SafeVarargs
	private static <T> Set<T> set(final T... elements)
	{
		final Set<T> set = new HashSet<>();
		for (final T e : elements)
			set.add(e);

		return set;
	}

	private static <T> Set<T> set(final T e1, final T e2)
	{
		final Set<T> set = new HashSet<>();
		set.add(e1);
		set.add(e2);

		return set;
	}

	public static OWLDataSomeValuesFrom some(final OWLDataPropertyExpression property, final OWLDataRange datatype)
	{
		return _factory.getOWLDataSomeValuesFrom(property, datatype);
	}

	public static OWLObjectSomeValuesFrom some(final OWLObjectPropertyExpression property, final OWLClassExpression description)
	{
		return _factory.getOWLObjectSomeValuesFrom(property, description);
	}

	public static OWLSubClassOfAxiom subClassOf(final OWLClassExpression sub, final OWLClassExpression sup)
	{
		return _factory.getOWLSubClassOfAxiom(sub, sup);
	}

	public static OWLSubDataPropertyOfAxiom subPropertyOf(final OWLDataPropertyExpression sub, final OWLDataPropertyExpression sup)
	{
		return _factory.getOWLSubDataPropertyOfAxiom(sub, sup);
	}

	public static OWLSubObjectPropertyOfAxiom subPropertyOf(final OWLObjectPropertyExpression sub, final OWLObjectPropertyExpression sup)
	{
		return _factory.getOWLSubObjectPropertyOfAxiom(sub, sup);
	}

	public static OWLSubPropertyChainOfAxiom subPropertyOf(final OWLObjectPropertyExpression[] subChain, final OWLObjectPropertyExpression sup)
	{
		return _factory.getOWLSubPropertyChainOfAxiom(Arrays.asList(subChain), sup);
	}

	public static OWLSymmetricObjectPropertyAxiom symmetric(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLSymmetricObjectPropertyAxiom(p);
	}

	public static OWLTransitiveObjectPropertyAxiom transitive(final OWLObjectPropertyExpression p)
	{
		return _factory.getOWLTransitiveObjectPropertyAxiom(p);
	}

	public static OWLDataHasValue value(final OWLDataPropertyExpression property, final OWLLiteral constant)
	{
		return _factory.getOWLDataHasValue(property, constant);
	}

	public static OWLObjectHasValue value(final OWLObjectPropertyExpression property, final OWLIndividual value)
	{
		return _factory.getOWLObjectHasValue(property, value);
	}

}
