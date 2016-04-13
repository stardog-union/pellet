// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.reasoner;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapiv3.ImmutableNodeSet;
import com.clarkparsia.owlapiv3.OWLListeningReasoner;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import com.clarkparsia.pellet.service.reasoner.SchemaQueryType;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.collect.ImmutableMap;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * @author Evren Sirin
 */
public class LocalSchemaReasoner implements SchemaReasoner {
	private static final Map<EntityType<?>, EntityQueryEvaluator> QUERY_EVALUATORS = ImmutableMap
		                                                                                 .<EntityType<?>, EntityQueryEvaluator>builder()
		                                                                                 .put(EntityType.CLASS, new ClassQueryEvaluator())
		                                                                                 .put(EntityType.OBJECT_PROPERTY, new ObjectPropertyQueryEvaluator())
		                                                                                 .put(EntityType.DATA_PROPERTY, new DataPropertyQueryEvaluator())
		                                                                                 .build();

	private final OWLListeningReasoner reasoner;

	private final PelletExplanation explanation;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public LocalSchemaReasoner(final PelletReasoner pellet) {
		this(pellet, pellet);
	}

	public LocalSchemaReasoner(final IncrementalReasoner incremental) {
		this(incremental, incremental.getReasoner());
	}

	private LocalSchemaReasoner(final OWLListeningReasoner reasoner,
	                            final PelletReasoner pellet) {
		this.reasoner = reasoner;
		this.explanation = new PelletExplanation(pellet);

		reasoner.setListenChanges(true);
	}

	@Override
	public <T extends OWLObject> NodeSet<T> query(final SchemaQuery query) {
		lock.readLock().lock();
		try {
			OWLLogicalEntity entity = query.getEntity();
			EntityQueryEvaluator evaluator = QUERY_EVALUATORS.get(entity.getEntityType());
			return (NodeSet) evaluator.query(reasoner, query.getType(), entity);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Set<OWLAxiom>> explain(final OWLAxiom axiom, final int limit) {
		// explanation generator makes changes to the ontology so we need to acquire the write lock to prevent overlapping updates
		lock.writeLock().lock();
		// we disable change tracking in the reasoner not to lose the current state. after explanations are computed ontology
		// would be left back in its original state so we can resume listening changes
		reasoner.setListenChanges(false);
		try {
			return explanation.getEntailmentExplanations(axiom, limit);
		}
		finally {
			reasoner.setListenChanges(true);
			lock.writeLock().unlock();
		}
	}

	@Override
	public void classify() {
		lock.readLock().lock();
		try {
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void insert(Set<OWLAxiom> additions) {
		lock.writeLock().lock();
		try {
			OntologyUtils.addAxioms(reasoner.getRootOntology(), additions);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void delete(Set<OWLAxiom> removals) {
		lock.writeLock().lock();
		try {
			OntologyUtils.removeAxioms(reasoner.getRootOntology(), removals);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void close() throws Exception {
		explanation.dispose();
		reasoner.dispose();
	}

	private interface EntityQueryEvaluator {
		NodeSet<?> query(OWLReasoner reasoner, SchemaQueryType theQueryType, OWLLogicalEntity input);
	}

	private static class ClassQueryEvaluator implements EntityQueryEvaluator {
		@Override
		public NodeSet<OWLClass> query(final OWLReasoner reasoner, final SchemaQueryType theQueryType, final OWLLogicalEntity input) {
			OWLClass cls = (OWLClass) input;
			switch (theQueryType) {
				case EQUIVALENT: return ImmutableNodeSet.of(reasoner.getEquivalentClasses(cls));
				case CHILD: return reasoner.getSubClasses(cls, true);
				case DESCENDANT: return reasoner.getSubClasses(cls, false);
				case PARENT: return reasoner.getSuperClasses(cls, true);
				case ANCESTOR: return reasoner.getSuperClasses(cls, false);
				case DISJOINT: return reasoner.getDisjointClasses(cls);
				default: throw new UnsupportedOperationException();
			}
		}
	}

	private static class ObjectPropertyQueryEvaluator implements EntityQueryEvaluator {
		@Override
		public NodeSet<?> query(final OWLReasoner reasoner, final SchemaQueryType theQueryType, final OWLLogicalEntity input) {
			OWLObjectProperty pe = (OWLObjectProperty) input;
			switch (theQueryType) {
				case EQUIVALENT: return ImmutableNodeSet.of(reasoner.getEquivalentObjectProperties(pe));
				case CHILD: return reasoner.getSubObjectProperties(pe, true);
				case DESCENDANT: return reasoner.getSubObjectProperties(pe, false);
				case PARENT: return reasoner.getSuperObjectProperties(pe, true);
				case ANCESTOR: return reasoner.getSuperObjectProperties(pe, false);
				case DISJOINT: return reasoner.getDisjointObjectProperties(pe);
				case DOMAIN: return reasoner.getObjectPropertyDomains(pe, true);
				case RANGE: return reasoner.getObjectPropertyRanges(pe, true);
				case INVERSE: return ImmutableNodeSet.of(reasoner.getInverseObjectProperties(pe));
				default: throw new UnsupportedOperationException();
			}
		}
	}

	private static class DataPropertyQueryEvaluator implements EntityQueryEvaluator {
		@Override
		public NodeSet<?> query(final OWLReasoner reasoner, final SchemaQueryType theQueryType, final OWLLogicalEntity input) {
			OWLDataProperty pe = (OWLDataProperty) input;
			switch (theQueryType) {
				case EQUIVALENT: return ImmutableNodeSet.of(reasoner.getEquivalentDataProperties(pe));
				case CHILD: return reasoner.getSubDataProperties(pe, true);
				case DESCENDANT: return reasoner.getSubDataProperties(pe, false);
				case PARENT: return reasoner.getSuperDataProperties(pe, true);
				case ANCESTOR: return reasoner.getSuperDataProperties(pe, false);
				case DISJOINT: return reasoner.getDisjointDataProperties(pe);
				case DOMAIN: return reasoner.getDataPropertyDomains(pe, true);
				default: throw new UnsupportedOperationException();
			}
		}
	}
}