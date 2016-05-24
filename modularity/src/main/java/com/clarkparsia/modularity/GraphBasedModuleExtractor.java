// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import static java.lang.String.format;

import com.clarkparsia.reachability.EntityNode;
import com.clarkparsia.reachability.Node;
import com.clarkparsia.reachability.PairSet;
import com.clarkparsia.reachability.Reachability;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Evren Sirin
 */
public class GraphBasedModuleExtractor extends AbstractModuleExtractor
{

	public static final Logger log = Log.getLogger(GraphBasedModuleExtractor.class);

	public GraphBasedModuleExtractor()
	{
	}

	@Override
	protected void extractModuleSignatures(final Set<? extends OWLEntity> entities, final ProgressMonitor monitor)
	{
		final Timer t = getTimers().startTimer("buildGraph");
		final GraphBuilder builder = new GraphBuilder();

		for (final OWLAxiom axiom : getAxioms())
			builder.addAxiom(axiom);

		final Reachability<OWLEntity> engine = new Reachability<>(builder.build());
		t.stop();

		if (log.isLoggable(Level.FINER))
			log.finer(format("Built graph in %d ms", t.getLast()));

		//		DisplayGraph.display( entities, engine.getGraph(), null );

		for (final OWLEntity ent : entities)
		{
			if (!(ent instanceof OWLClass))
			{
				monitor.incrementProgress();
				continue;
			}

			if (log.isLoggable(Level.FINE))
				log.fine("Compute module for " + ent);

			Set<OWLEntity> module = modules.get(ent);

			if (module != null)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Existing module size " + module.size());

				continue;
			}

			final EntityNode<OWLEntity> node = engine.getGraph().getNode(ent);

			if (log.isLoggable(Level.FINE))
				log.fine("Node " + node);

			if (node == null)
			{
				// if the entity is not in the activation engine it means it was
				// not used in any logical axiom which implies its module contains
				// just itself
				module = Collections.singleton(ent);

				// update the module
				modules.put(ent, module);
			}
			else
				extractModule(engine, node, entities, monitor);
		}
	}

	private Set<OWLEntity> extractModule(final Reachability<OWLEntity> engine, final EntityNode<OWLEntity> node, final Set<? extends OWLEntity> entities, final ProgressMonitor monitor)
	{
		if (log.isLoggable(Level.FINE))
			log.fine("Extract module for " + node);

		// we don't know what the module is
		Set<OWLEntity> module = null;

		// check if any of the _nodes had a module that is not invalidated
		// this is possible because computing updated modules is an overestimate
		// and even though we think we need to update the module of an entity
		// we can find another entity which does not need update and which has
		// the same module as the other entity
		for (final Object n : node.getEntities())
		{
			module = modules.get(n);
			if (module != null)
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Existing module size " + module.size());
				break;
			}
		}

		// if we don't have a module and the initial _node has a single output
		// we may skip running the activation engine
		if (module == null && node.getOutputs().size() == 1)
		{
			final Node output = node.getOutputs().iterator().next();

			// we will have cached module for the output _node only if it is
			// an entity _node
			if (output instanceof EntityNode)
			{
				// recursively extract the module for output _node
				final Set<OWLEntity> outputModule = extractModule(engine, (EntityNode) output, entities, monitor);

				if (log.isLoggable(Level.FINE))
					log.fine("Cached module size " + outputModule.size());

				// the module is the union of the outputModule and the entities
				// in this _node
				module = new PairSet<>(outputModule, node.getEntities());
			}
		}

		// compute reachability if we don't have a cached result
		if (module == null)
			// compute _nodes reachable from the _current _node entities
			module = engine.computeReachable(node.getEntities());

		if (log.isLoggable(Level.FINE))
			log.fine("Setting the module for " + node.getEntities());

		for (final OWLEntity n : node.getEntities())
		{
			// update the module for every entity even though some of them
			// might have already their module
			final Set<OWLEntity> prevModule = modules.put(n, module);

			if (prevModule != null)
			{
				if (!prevModule.equals(module))
					log.warning(format("Possible discrepancy for the module of %s ( Previous %s , Current %s )", n, prevModule, module));
			}
			else
				// update the monitor only for entities in the initial set
				if (entities.contains(n))
					monitor.incrementProgress();
		}

		return module;
	}

	@Override
	public Set<OWLAxiom> extractModule(final Set<? extends OWLEntity> signature)
	{
		throw new UnsupportedOperationException();
	}

}
