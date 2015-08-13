// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.reachability.EntityNode;
import com.clarkparsia.reachability.Node;
import com.clarkparsia.reachability.PairSet;
import com.clarkparsia.reachability.Reachability;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import static java.lang.String.format;

/**
 * @author Evren Sirin
 */
public class GraphBasedModuleExtractor extends AbstractModuleExtractor {

	public static final Logger log = Logger.getLogger(GraphBasedModuleExtractor.class.getName());

	public GraphBasedModuleExtractor() {
	}

	/**
	 * {@inheritDoc}
	 */
	protected void extractModuleSignatures(Set<? extends OWLEntity> entities) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Extracting module for each of " + entities);
		}

		if (entities.isEmpty()) {
			return;
		}

		ProgressMonitor monitor = new ConsoleProgressMonitor();
		monitor.setProgressTitle("Extracting");
		monitor.setProgressLength(entities.size());
		monitor.taskStarted();

		Timer t = getTimers().startTimer("buildGraph");
		GraphBuilder builder = new GraphBuilder();

		for (OWLAxiom axiom : getAxioms()) {
			builder.addAxiom(axiom);
		}

		Reachability<OWLEntity> engine = new Reachability<OWLEntity>(builder.build());
		t.stop();

		if (log.isLoggable(Level.FINER)) {
			log.finer(format("Built graph in %d ms", t.getLast()));
		}

//		DisplayGraph.display( entities, engine.getGraph(), null );

		for (OWLEntity ent : entities) {
			if (log.isLoggable(Level.FINE)) {
				log.fine("Compute module for " + ent);
			}

			Set<OWLEntity> module = modules.get(ent);

			if (module != null) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("Existing module size " + module.size());
				}

				continue;
			}

			EntityNode<OWLEntity> node = engine.getGraph().getNode(ent);

			if (log.isLoggable(Level.FINE)) {
				log.fine("Node " + node);
			}

			if (node == null) {
				// if the entity is not in the activation engine it means it was
				// not used in any logical axiom which implies its module contains
				// just itself
				module = Collections.singleton(ent);

				// update the module
				modules.put(ent, module);
			}
			else {
				extractModule(engine, node, entities, monitor);
			}
		}

		monitor.taskFinished();

		if (log.isLoggable(Level.FINER)) {
			log.finer("Modules: " + modules);
		}
	}

	private Set<OWLEntity> extractModule(Reachability<OWLEntity> engine, EntityNode<OWLEntity> node, Set<? extends OWLEntity> entities, ProgressMonitor monitor) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Extract module for " + node);
		}

		// we don't know what the module is
		Set<OWLEntity> module = null;

		// check if any of the nodes had a module that is not invalidated
		// this is possible because computing updated modules is an overestimate
		// and even though we think we need to update the module of an entity
		// we can find another entity which does not need update and which has
		// the same module as the other entity
		for (Object n : node.getEntities()) {
			module = modules.get(n);
			if (module != null) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("Existing module size " + module.size());
				}
				break;
			}
		}

		// if we don't have a module and the initial node has a single output
		// we may skip running the activation engine
		if (module == null && node.getOutputs().size() == 1) {
			Node output = node.getOutputs().iterator().next();

			// we will have cached module for the output node only if it is
			// an entity node
			if (output instanceof EntityNode) {
				// recursively extract the module for output node
				Set<OWLEntity> outputModule = extractModule(engine, (EntityNode) output, entities, monitor);

				if (log.isLoggable(Level.FINE)) {
					log.fine("Cached module size " + outputModule.size());
				}

				// the module is the union of the outputModule and the entities
				// in this node
				module = new PairSet<OWLEntity>(outputModule, node.getEntities());
			}
		}

		// compute reachability if we don't have a cached result
		if (module == null) {
			// compute nodes reachable from the current node entities
			module = engine.computeReachable(node.getEntities());
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("Setting the module for " + node.getEntities());
		}

		for (OWLEntity n : node.getEntities()) {
			// update the module for every entity even though some of them
			// might have already their module
			Set<OWLEntity> prevModule = modules.put(n, module);

			if (prevModule != null) {
				if (!prevModule.equals(module)) {
					log
						.warning(format(
							               "Possible discrepancy for the module of %s ( Previous %s , Current %s )",
							               n, prevModule, module));
				}
			}
			else {
				// update the monitor only for entities in the initial set
				if (entities.contains(n)) {
					monitor.incrementProgress();
				}
			}
		}

		return module;
	}

	public Set<OWLAxiom> extractModule(Set<? extends OWLEntity> signature) {
		throw new UnsupportedOperationException();
	}

}