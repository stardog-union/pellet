// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.complexible.pellet.client.reasoner;

import java.util.AbstractList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.clarkparsia.owlapiv3.BufferingOntologyChangeListener;
import com.clarkparsia.owlapiv3.ImmutableNodeSet;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.clarkparsia.pellet.service.reasoner.SchemaReasonerFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.semanticweb.owlapi.model.AddAxiom;
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
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.RemoveAxiom;
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
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;
import org.semanticweb.owlapi.util.Version;

import com.clarkparsia.pellet.service.reasoner.SchemaQueryType;

/**
 * Implementation of {@link OWLReasoner} interface backed by a {@link SchemaReasoner}.
 *
 * @author Evren Sirin
 */
public class SchemaOWLReasoner implements OWLReasoner {
	public static final Logger LOGGER = Logger.getLogger(SchemaOWLReasoner.class.getName());

	private final SchemaReasoner client;

	private final OWLOntology ontology;

	private final BufferingOntologyChangeListener changeListener;

	private final BufferingMode bufferingMode;

	public SchemaOWLReasoner(OWLOntology ontology, SchemaReasonerFactory reasonerFactory, BufferingMode bufferingMode) {
		this.ontology = ontology;
		this.client = reasonerFactory.create(ontology);
		this.bufferingMode = bufferingMode;

		Iterable<OWLOntologyID> ontologies = Iterables.transform(ontology.getImportsClosure(), new Function<OWLOntology, OWLOntologyID>() {
			@Override
			public OWLOntologyID apply(final OWLOntology ont) {
				return ont.getOntologyID();
			}
		});
        changeListener = new BufferingOntologyChangeListener(ontologies);

		ontology.getOWLOntologyManager().addOntologyChangeListener(changeListener);

		LOGGER.info("Create schema reasoner with " + bufferingMode);
	}

	public OWLOntologyChangeListener getListener() {
		return changeListener;
	}


	public Set<Set<OWLAxiom>> explain(OWLAxiom axiom, int limit) {
		return client.explain(axiom, limit);
	}

	private boolean isFlushed() {
		return !changeListener.isChanged();
	}

	private <O extends OWLObject, T extends OWLEntity> Node<O> executeSingletonQuery(SchemaQueryType theQueryType, OWLLogicalEntity input) {
		NodeSet<O> result = executeQuery(theQueryType, input);
		if (!result.isSingleton()) {
			throw new IllegalArgumentException("A singleton result expected");
		}

		return Iterables.getOnlyElement(result);
	}

	private <O extends OWLObject> NodeSet<O> executeQuery(SchemaQueryType queryType, OWLLogicalEntity input) {
		return client.query(new SchemaQuery(queryType, input));
	}

	public void autoFlush() {
		if (bufferingMode == BufferingMode.NON_BUFFERING) {
			flush();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() {
		System.out.println("Flushing schema reasoner " + isFlushed() + " with updates (+" + changeListener.getAdditions().size() + ", -" + changeListener.getRemovals().size() + ")");

		if (!isFlushed()) {
			LOGGER.info("Flushing schema reasoner  with updates (+" + changeListener.getAdditions().size() + ", -" + changeListener.getAdditions().size() + ")");
			client.insert(changeListener.getAdditions());
			client.delete(changeListener.getRemovals());
			client.classify();
			changeListener.reset();
		}
	}

	@Override
	public void dispose() {
		ontology.getOWLOntologyManager().removeOntologyChangeListener(changeListener);
	}
	
	private OWLLogicalEntity requireNamedObject(OWLObject o) {
		if (o instanceof OWLLogicalEntity) {
			return (OWLLogicalEntity) o;
		}
		
		throw new IllegalArgumentException("This reasoner only supports named entities");
	}

	private SchemaQueryType querySub(boolean direct) {
		return direct ? SchemaQueryType.CHILD : SchemaQueryType.DESCENDANT;
	}

	private SchemaQueryType querySuper(boolean direct) {
		return direct ? SchemaQueryType.PARENT : SchemaQueryType.ANCESTOR;
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(OWLClassExpression clsC) {
		autoFlush();

		return executeSingletonQuery(SchemaQueryType.EQUIVALENT, requireNamedObject(clsC));
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) {
		autoFlush();

		return executeQuery(querySub(direct), requireNamedObject(ce));
	}

	@Override
	public boolean isSatisfiable(OWLClassExpression clsC) {
		return !getUnsatisfiableClasses().getEntities().contains(requireNamedObject(clsC));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLClass> getBottomClassNode() {
		return getEquivalentClasses(OWL.Nothing);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		return getEquivalentDataProperties(OWL.bottomDataProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		return getEquivalentObjectProperties(OWL.bottomObjectProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferingMode getBufferingMode() {
		return bufferingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(SchemaQueryType.DOMAIN, pe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind, OWLDataProperty pe)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return ImmutableSet.of();
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return ImmutableNodeSet.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
		autoFlush();

		return executeQuery(SchemaQueryType.DISJOINT, requireNamedObject(ce));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression pe)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(SchemaQueryType.DISJOINT, requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression pe)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {		
		autoFlush();

		return executeQuery(SchemaQueryType.DISJOINT, requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeSingletonQuery(SchemaQueryType.EQUIVALENT, pe);
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression pe)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return (Node) executeSingletonQuery(SchemaQueryType.EQUIVALENT, requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return FreshEntityPolicy.DISALLOW;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return IndividualNodeSetPolicy.BY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce, boolean direct)
		throws InconsistentOntologyException, ClassExpressionNotInProfileException,
		       FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return ImmutableNodeSet.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression pe)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeSingletonQuery(SchemaQueryType.INVERSE, requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe, boolean direct)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(SchemaQueryType.DOMAIN, requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe, boolean direct)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(SchemaQueryType.RANGE, requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind,
	                                                           OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
	                                                                                                  FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return ImmutableNodeSet.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		return changeListener.getAdditions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		return changeListener.getRemovals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		return new AbstractList<OWLOntologyChange>() {
			@Override
			public OWLOntologyChange get(final int index) {
				int additionsSize = getPendingAxiomAdditions().size();
				OWLOntology ont = getRootOntology();
				return index < additionsSize
				       ? new AddAxiom(ont, Iterables.get(getPendingAxiomAdditions(), index))
				       : new RemoveAxiom(ont, Iterables.get(getPendingAxiomRemovals(), index - additionsSize));
			}

			@Override
			public int size() {
				return getPendingAxiomAdditions().size() + getPendingAxiomRemovals().size();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getReasonerName() {
		return "Pellet (Remote)";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Version getReasonerVersion() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OWLOntology getRootOntology() {
		return ontology;
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return NodeFactory.getOWLNamedIndividualNode();
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe, boolean direct)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(querySub(direct), pe);
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression pe,
	                                                                   boolean direct) throws InconsistentOntologyException, FreshEntitiesException,
	                                                                                          ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(querySub(direct), requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct)
		throws InconsistentOntologyException, ClassExpressionNotInProfileException,
		       FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(querySuper(direct), requireNamedObject(ce));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe, boolean direct)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(querySuper(direct), requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression pe,
	                                                                     boolean direct) throws InconsistentOntologyException, FreshEntitiesException,
	                                                                                            ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return executeQuery(querySuper(direct), requireNamedObject(pe));
	}

	/**
	 * {@inheritDoc}
	 */
	public long getTimeOut() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLClass> getTopClassNode() {
		return getEquivalentClasses(OWL.Thing);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		return getEquivalentDataProperties(OWL.topDataProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		return getEquivalentObjectProperties(OWL.topObjectProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
		throws InconsistentOntologyException, FreshEntitiesException,
		       ReasonerInterruptedException, TimeOutException {
		autoFlush();

		return ImmutableNodeSet.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException,
	                                                       TimeOutException {
		return getBottomClassNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void interrupt() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
		return isSatisfiable(OWL.Thing);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEntailed(OWLAxiom axiom) throws ReasonerInterruptedException,
	                                                 UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException,
	                                                 FreshEntitiesException {
		throw new UnsupportedEntailmentTypeException(axiom);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEntailed(Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException,
	                                                                 UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException,
	                                                                 FreshEntitiesException {
		throw new UnsupportedEntailmentTypeException(axioms.iterator().next());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return ImmutableSet.of(InferenceType.CLASS_HIERARCHY);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPrecomputed(InferenceType inferenceType) {
		switch (inferenceType) {
			case CLASS_HIERARCHY:
				return isFlushed();
			default:
				return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void precomputeInferences(InferenceType... inferenceTypes) throws ReasonerInterruptedException,
	                                                                         TimeOutException, InconsistentOntologyException {
		for (InferenceType inferenceType : inferenceTypes) {
			switch (inferenceType) {
				case CLASS_HIERARCHY:
					autoFlush();
				default:
					break;
			}
		}
	}
}