// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.owlapi.modularity.locality.LocalityClass;
import com.clarkparsia.owlapi.modularity.locality.LocalityEvaluator;
import com.clarkparsia.owlapi.modularity.locality.SyntacticLocalityEvaluator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.katk.tools.Log;
import org.mindswap.pellet.utils.DisjointSet;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class AxiomBasedModuleExtractor extends AbstractModuleExtractor
{
	@SuppressWarnings("hiding")
	public static final Logger _logger = Log.getLogger(AxiomBasedModuleExtractor.class);

	private boolean _optimizeForSharedModules = true;

	public AxiomBasedModuleExtractor()
	{
		super();
	}

	public AxiomBasedModuleExtractor(final LocalityClass localityClass)
	{
		super(new SyntacticLocalityEvaluator(localityClass));
	}

	public AxiomBasedModuleExtractor(final LocalityEvaluator localityEvaluator)
	{
		super(localityEvaluator);
	}

	private OWLEntity extractModuleSignature(final OWLEntity entity, final Set<OWLEntity> stackElements, final List<OWLEntity> currentCycle, final Set<OWLEntity> module)
	{

		assert !modules.containsKey(entity) : "po already contained entity";

		assert currentCycle.isEmpty() : "non-empty _current cycle passed into function";

		final Set<OWLEntity> myCycle = new HashSet<>();

		if (entity != null)
		{
			module.add(entity);
			myCycle.add(entity);
			stackElements.add(entity);

			modules.put(entity, module);
		}

		int oldSize = -1;
		Set<OWLEntity> previousModule = new HashSet<>();

		while (module.size() != oldSize)
		{
			oldSize = module.size();

			final List<OWLEntity> newMembers = new ArrayList<>();

			// Previous updates to the module may cause additional axioms to be
			// non-local
			{
				final Set<OWLEntity> addedEntities = SetUtils.difference(module, previousModule);
				previousModule = new HashSet<>(module);
				final Set<OWLAxiom> testLocal = new HashSet<>();

				for (final OWLEntity e : addedEntities)
					axioms(e)//
					.filter(a -> testLocal.add(a) && !isLocal(a, module))//
					.forEach(a -> signature(a).filter(module::add).forEach(newMembers::add));
			}

			// Recursive calls may modify the module, iterating over a static
			// view in an array avoids concurrent modification problems
			for (final OWLEntity member : newMembers)
			{

				// ignore self references
				if (myCycle.contains(member))
					continue;

				// if we have never seen this entity extract its module
				if (!modules.containsKey(member))
				{

					final Set<OWLEntity> memberMod = new HashSet<>();
					final List<OWLEntity> memberCycle = new ArrayList<>();
					final OWLEntity root = extractModuleSignature(member, stackElements, memberCycle, memberMod);

					module.addAll(memberMod);

					// Option 1: No cycle was identified, extraction successful
					if (root.equals(member))
						assert !stackElements.contains(member) : "Recursive call did not cleanup stack";
						else
					{
							myCycle.addAll(memberCycle);
							// Option 2a: entity was the root of the cycle
							if (myCycle.contains(root))
								stackElements.addAll(memberCycle);
							else
						{
								currentCycle.addAll(myCycle);
								return root;
							}
						}
				}
				// entity is in a cycle
				else
					if (stackElements.contains(member))
					{
						currentCycle.addAll(myCycle);
						return member;
					}
					else
						// simply retrieve and copy the precomputed module
						module.addAll(modules.get(member));
			}

			for (final OWLEntity e : myCycle)
				modules.put(e, module);
		}

		stackElements.removeAll(myCycle);

		return entity;
	}

	/**
	 * This is the recursive method to actually extract the signature for an entity
	 */
	private void extractModuleSignature(final OWLEntity entity, final DisjointSet<OWLEntity> modEqCls, final ArrayList<OWLEntity> stack, final Set<OWLEntity> stackElements)
	{

		/*
		 * Two conditions should never occur
		 */
		assert !stack.contains(entity) : "stack contained entity already";

		assert !modules.containsKey(entity) : "po already contained entity";

		final Set<OWLEntity> module = new HashSet<>();

		if (entity != null)
		{
			stack.add(entity);
			stackElements.add(entity);
			modEqCls.add(entity);

			module.add(entity);

			modules.put(entity, module);
		}

		int oldSize = -1;
		Set<OWLEntity> previousModule = new HashSet<>();

		while (module.size() != oldSize)
		{
			oldSize = module.size();

			final List<OWLEntity> newMembers = new ArrayList<>();

			// Previous updates to the module may cause additional axioms to be
			// non-local
			{
				final Set<OWLEntity> addedEntities = SetUtils.difference(module, previousModule);
				previousModule = new HashSet<>(module);
				final Set<OWLAxiom> testLocal = new HashSet<>();

				for (final OWLEntity e : addedEntities)
					axioms(e)//
					.filter(a -> testLocal.add(a) && !isLocal(a, module))//
					.forEach(a -> signature(a).filter(module::add).forEach(newMembers::add));
			}

			// Recursive calls may modify the module, iterating over a static
			// view in an array avoids concurrent modification problems
			for (final OWLEntity member : newMembers)
			{
				// ignore self references
				if (member.equals(entity))
					continue;

				// if we have never seen this entity extract its module
				if (!modules.containsKey(member))
					extractModuleSignature(member, modEqCls, stack, stackElements);
				// the _node might even be on the stack
				if (stackElements.contains(member))
				{
					// sanity check
					assert stack.contains(member) : "node was supposed to be on the stack";
					// all the entities in the stack up until that _node
					// will _end up having the same module
					boolean foundMember = false;
					for (int i = stack.size() - 1; !foundMember; i--)
					{
						final OWLEntity next = stack.get(i);
						modEqCls.union(member, next);
						foundMember = next.equals(member);
					}
				}
				else
					// simply retrieve and copy the precomputed module
					module.addAll(modules.get(member));
			}
		}

		for (final OWLEntity other : modEqCls.elements())
			if (modEqCls.isSame(entity, other))
				modules.get(other).addAll(module);

		stack.remove(stack.size() - 1);
		stackElements.remove(entity);
	}

	@Override
	protected void extractModuleSignatures(final Set<? extends OWLEntity> entities, final ProgressMonitor monitor)
	{
		final Set<OWLEntity> nonLocalModule = new HashSet<>();
		axioms()//
		.filter(axiom -> !isLocal(axiom, Collections.<OWLEntity> emptySet())) //
		.forEach(axiom -> nonLocalModule.addAll(OntologyUtils.signature(axiom).collect(Collectors.toList())));

		// iterate over classes passed in, and extract all their modules
		for (final OWLEntity ent : entities)
		{
			monitor.incrementProgress();

			if (!(ent instanceof OWLClass))
				continue;

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Class: " + ent);

			if (!modules.containsKey(ent))
				if (_optimizeForSharedModules)
					extractModuleSignature(ent, new HashSet<OWLEntity>(), new ArrayList<OWLEntity>(), new HashSet<>(nonLocalModule));
				else
					extractModuleSignature(ent, new DisjointSet<OWLEntity>(), new ArrayList<OWLEntity>(), new HashSet<>(nonLocalModule));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLAxiom> extractModule(final Set<? extends OWLEntity> signature)
	{
		if (isChanged())
			resetModules();

		final Set<OWLEntity> module = new HashSet<>(signature);
		axioms()//
		.filter(axiom -> !isLocal(axiom, Collections.<OWLEntity> emptySet())) //
		.forEach(axiom -> module.addAll(OntologyUtils.signature(axiom).collect(Collectors.toList())));

		if (!entityAxioms.isEmpty())
			if (_optimizeForSharedModules)
				extractModuleSignature(null, new HashSet<OWLEntity>(), new ArrayList<OWLEntity>(), module);
			else
				extractModuleSignature(null, new DisjointSet<OWLEntity>(), new ArrayList<OWLEntity>(), module);

		return getModuleAxioms(module);
	}

	/**
	 * Returns if shared modules optimization is on.
	 */
	public boolean isOptimizeForSharedModules()
	{
		return _optimizeForSharedModules;
	}

	/**
	 * Sets the the option to optimize for shared modules during module extraction. This option improves the performance of axiom-based module extractor when
	 * there are many shared modules in the ontology. This option seems to improve the performance of modularization for NCI thesaurus significantly but has
	 * slight overhead for some other ontologies (it is not clear if the overhead would more dramatic in other untested cases). This option has no effect on
	 * graph-based extractor which by default includes optimization for shared modules that does not have negative impact on any ontology.
	 */
	public void setOptimizeForSharedModules(final boolean optimizeForSharedModules)
	{
		_optimizeForSharedModules = optimizeForSharedModules;
	}

}
