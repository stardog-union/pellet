// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.complexible.pellet.client.reasoner;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.clarkparsia.owlapiv3.BufferingOntologyChangeListener;
import com.clarkparsia.owlapiv3.OWL;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.complexible.pellet.service.reasoner.SchemaReasonerFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.util.Version;

import static com.complexible.pellet.service.reasoner.SchemaReasoner.QueryType;

/**
 * @author Evren Sirin
 */
public class SchemaOWLReasonerFactory implements OWLReasonerFactory {
	private final SchemaReasonerFactory factory;

	public SchemaOWLReasonerFactory(final SchemaReasonerFactory theFactory) {
		factory = theFactory;
	}

	@Nonnull
	@Override
	public String getReasonerName() {
		return "Pellet Schema Reasoner";
	}

	@Nonnull
	@Override
	public OWLReasoner createNonBufferingReasoner(@Nonnull final OWLOntology ontology) {
		return createReasoner(ontology);
	}

	@Nonnull
	@Override
	public OWLReasoner createReasoner(@Nonnull final OWLOntology ontology) {
		return new SchemaOWLReasoner(ontology, factory);
	}

	@Nonnull
	@Override
	public OWLReasoner createNonBufferingReasoner(@Nonnull final OWLOntology ontology, @Nonnull final OWLReasonerConfiguration theOWLReasonerConfiguration) {
		return createReasoner(ontology);
	}

	@Nonnull
	@Override
	public OWLReasoner createReasoner(@Nonnull final OWLOntology ontology, @Nonnull final OWLReasonerConfiguration theOWLReasonerConfiguration) {
		return createReasoner(ontology);
	}
}