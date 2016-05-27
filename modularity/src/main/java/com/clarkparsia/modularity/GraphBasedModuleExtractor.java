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
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.model.AsOWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Evren Sirin
 */
public class GraphBasedModuleExtractor extends AbstractModuleExtractor
{

	@SuppressWarnings("hiding")
	public static final Logger _logger = Log.getLogger(GraphBasedModuleExtractor.class);

	public GraphBasedModuleExtractor()
	{
	}

	@Override
	protected void extractModuleSignatures(final Set<? extends OWLEntity> entities, final ProgressMonitor monitor)
	{
		final Timer timer = getTimers().startTimer("buildGraph");
		final GraphBuilder builder = new GraphBuilder();

		axioms().forEach(builder::addAxiom);
		final Reachability<AsOWLNamedIndividual> graph = new Reachability<>(builder.build());
		final Reachability<OWLEntity> engine = (Reachability) graph; // FIXME : this is wrong but type erasure mask it.
		timer.stop();

		_logger.finer(() -> format("Built graph in %d ms", timer.getLast()));

		//		DisplayGraph.display( entities, engine.getGraph(), null );

		for (final OWLEntity ent : entities)
		{
			if (!(ent instanceof OWLClass))
			{
				monitor.incrementProgress();
				continue;
			}

			_logger.fine(() -> "Compute module for " + ent);

			final Set<OWLEntity> module = modules.get(ent);

			if (module != null)
			{
				_logger.fine(() -> "Existing module size " + module.size());
				continue;
			}

			final EntityNode<OWLEntity> node = engine.getGraph().getNode(ent);

			_logger.fine(() -> "Node " + node);

			if (node == null)
			{
				// if the entity is not in the activation engine it means it was
				// not used in any logical axiom which implies its module contains
				// just itself.
				// so, update the module
				modules.put(ent, /*module =*/Collections.singleton(ent));
			}
			else
				extractModule(engine, node, entities, monitor);
		}
	}

	private Set<OWLEntity> extractModule(final Reachability<OWLEntity> engine, final EntityNode<OWLEntity> node, final Set<? extends OWLEntity> entities, final ProgressMonitor monitor)
	{
		_logger.fine(() -> "Extract module for " + node);

		// we don't know what the module is
		Set<OWLEntity> module = null;

		// check if any of the nodes had a module that is not invalidated
		// this is possible because computing updated modules is an overestimate
		// and even though we think we need to update the module of an entity
		// we can find another entity which does not need update and which has
		// the same module as the other entity
		for (final Object n : node.getEntities())
		{
			module = modules.get(n);
			if (module != null)
				break;
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

				_logger.fine(() -> "Cached module size " + outputModule.size());

				// the module is the union of the outputModule and the entities
				// in this _node
				module = new PairSet<>(outputModule, node.getEntities());
			}
		}

		// compute reachability if we don't have a cached result
		if (module == null)
			// compute _nodes reachable from the _current _node entities
			module = engine.computeReachable(node.getEntities());

		_logger.fine(() -> "Setting the module for " + node.getEntities());

		for (final OWLEntity n : node.getEntities())
		{
			// update the module for every entity even though some of them
			// might have already their module
			final Set<OWLEntity> prevModule = modules.put(n, module);

			if (prevModule != null)
			{
				if (!prevModule.equals(module))
					_logger.warning(format("Possible discrepancy for the module of %s ( Previous %s , Current %s )", n, prevModule, module));
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
