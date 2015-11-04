// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.reasoner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapiv3.OWLListeningReasoner;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.complexible.pellet.service.reasoner.SchemaReasonerUtil;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * @author Evren Sirin
 */
public class LocalSchemaReasoner implements SchemaReasoner {
	private final OWLListeningReasoner reasoner;

	private final PelletExplanation explanation;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public LocalSchemaReasoner(final PelletReasoner pellet) {
		this(pellet, pellet);
	}

	public LocalSchemaReasoner(final IncrementalReasoner incremental) {
		this(incremental, incremental.getReasoner());
	}

	private LocalSchemaReasoner(final OWLListeningReasoner reasoner, final PelletReasoner pellet) {
		this.reasoner = reasoner;
		this.explanation = new PelletExplanation(pellet);

		reasoner.setListenChanges(true);
	}

	@Override
	public <T extends OWLObject> NodeSet<T> query(final QueryType theQueryType, final OWLLogicalEntity input) {
		lock.readLock().lock();
		try {
			return SchemaReasonerUtil.query(reasoner, theQueryType, input);
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
	public void update(Set<OWLAxiom> additions, Set<OWLAxiom> removals) {
		lock.writeLock().lock();
		try {
			OWLOntology ontology = reasoner.getRootOntology();

			OntologyUtils.updateOntology(ontology, additions, removals);

			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
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
}