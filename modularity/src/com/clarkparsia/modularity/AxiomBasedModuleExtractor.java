// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.utils.DisjointSet;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.clarkparsia.owlapi.modularity.locality.LocalityClass;
import com.clarkparsia.owlapi.modularity.locality.LocalityEvaluator;
import com.clarkparsia.owlapi.modularity.locality.SyntacticLocalityEvaluator;
import com.clarkparsia.owlapiv3.OntologyUtils;

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
public class AxiomBasedModuleExtractor extends AbstractModuleExtractor {
	public static final Logger					log							= Logger
																					.getLogger( AxiomBasedModuleExtractor.class
																							.getName() );
	
	private boolean optimizeForSharedModules = true;
	
	public AxiomBasedModuleExtractor() {
		super();
	}
		
	public AxiomBasedModuleExtractor(LocalityClass localityClass) {
		super( new SyntacticLocalityEvaluator( localityClass ) );
	}
		
	public AxiomBasedModuleExtractor(LocalityEvaluator localityEvaluator) {
		super( localityEvaluator );
	}

	private OWLEntity extractModuleSignature(OWLEntity entity, Set<OWLEntity> stackElements,
			List<OWLEntity> currentCycle, Set<OWLEntity> module) {

		assert !modules.containsKey( entity ) : "po already contained entity";

		assert currentCycle.isEmpty() : "non-empty current cycle passed into function";

		Set<OWLEntity> myCycle = new HashSet<OWLEntity>();

		if( entity != null ) {
			module.add( entity );
			myCycle.add( entity );
			stackElements.add( entity );
	
			modules.put( entity, module );
		}
		
		int oldSize = -1;
		Set<OWLEntity> previousModule = new HashSet<OWLEntity>();

		while( module.size() != oldSize ) {
			oldSize = module.size();

			List<OWLEntity> newMembers = new ArrayList<OWLEntity>();

			// Previous updates to the module may cause additional axioms to be
			// non-local
			{
				Set<OWLEntity> addedEntities = SetUtils.difference( module, previousModule );
				previousModule = new HashSet<OWLEntity>( module );
				Set<OWLAxiom> testLocal = new HashSet<OWLAxiom>();

				for( OWLEntity e : addedEntities ) {
					for( OWLAxiom a : getAxioms( e ) ) {
						if( testLocal.add( a ) && !isLocal( a, module ) ) {
							for( OWLEntity ent : getSignature( a ) ) {
								if( module.add( ent ) )
									newMembers.add( ent );
							}
						}
					}
				}

			}

			// Recursive calls may modify the module, iterating over a static
			// view in an array avoids concurrent modification problems
			for( OWLEntity member : newMembers ) {

				// ignore self references
				if( myCycle.contains( member ) )
					continue;

				// if we have never seen this entity extract its module
				if( !modules.containsKey( member ) ) {

					Set<OWLEntity> memberMod = new HashSet<OWLEntity>();
					List<OWLEntity> memberCycle = new ArrayList<OWLEntity>();
					OWLEntity root = extractModuleSignature( member, stackElements, memberCycle,
							memberMod );

					module.addAll( memberMod );

					// Option 1: No cycle was identified, extraction successful
					if( root.equals( member ) ) {
						assert !stackElements.contains( member ) : "Recursive call did not cleanup stack";
					}
					// Option 2: entity is in a cycle
					else {
						myCycle.addAll( memberCycle );
						// Option 2a: entity was the root of the cycle
						if( myCycle.contains( root ) ) {
							stackElements.addAll( memberCycle );
						}
						// Option 2b: entity is mid-cycle
						else {
							currentCycle.addAll( myCycle );
							return root;
						}
					}
				}
				// entity is in a cycle
				else if( stackElements.contains( member ) ) {
					currentCycle.addAll( myCycle );
					return member;
				}
				else {
					// simply retrieve and copy the precomputed module
					module.addAll( modules.get( member ) );
				}
			}

			for( OWLEntity e : myCycle )
				modules.put( e, module );
		}

		stackElements.removeAll( myCycle );

		return entity;
	}

	/**
	 * This is the recursive method to actually extract the signature for an
	 * entity
	 * 
	 * @param po
	 *            the partial order which houses all modules; this is updated as
	 *            the function proceeds
	 * @param modMap
	 *            index to track the root class for nodes contained in cycles
	 * @param entity
	 *            the entity to extract
	 */
	private void extractModuleSignature(OWLEntity entity, DisjointSet<OWLEntity> modEqCls,
			ArrayList<OWLEntity> stack, Set<OWLEntity> stackElements) {

		/*
		 * Two conditions should never occur
		 */
		assert !stack.contains( entity ) : "stack contained entity already";

		assert !modules.containsKey( entity ) : "po already contained entity";

		Set<OWLEntity> module = new HashSet<OWLEntity>();

		if( entity != null ) {
			stack.add( entity );
			stackElements.add( entity );
			modEqCls.add( entity );
	
			module.add( entity );
	
			modules.put( entity, module );
		}

		int oldSize = -1;
		Set<OWLEntity> previousModule = new HashSet<OWLEntity>();

		while( module.size() != oldSize ) {
			oldSize = module.size();

			List<OWLEntity> newMembers = new ArrayList<OWLEntity>();

			// Previous updates to the module may cause additional axioms to be
			// non-local
			{
				Set<OWLEntity> addedEntities = SetUtils.difference( module, previousModule );
				previousModule = new HashSet<OWLEntity>( module );
				Set<OWLAxiom> testLocal = new HashSet<OWLAxiom>();

				for( OWLEntity e : addedEntities ) {
					for( OWLAxiom a : getAxioms( e ) ) {
						if( testLocal.add( a ) && !isLocal( a, module ) ) {
							for( OWLEntity ent : getSignature( a ) ) {
								if( module.add( ent ) )
									newMembers.add( ent );
							}
						}
					}
				}

			}

			// Recursive calls may modify the module, iterating over a static
			// view in an array avoids concurrent modification problems
			for( OWLEntity member : newMembers ) {
				// ignore self references
				if( member.equals( entity ) )
					continue;

				// if we have never seen this entity extract its module
				if( !modules.containsKey( member ) )
					extractModuleSignature( member, modEqCls, stack, stackElements );
				// the node might even be on the stack
				if( stackElements.contains( member ) ) {
					// sanity check
					assert stack.contains( member ) : "node was supposed to be on the stack";
					// all the entities in the stack up until that node
					// will end up having the same module
					boolean foundMember = false;
					for( int i = stack.size() - 1; !foundMember; i-- ) {
						OWLEntity next = stack.get( i );
						modEqCls.union( member, next );
						foundMember = next.equals( member );
					}
				}
				else {
					// simply retrieve and copy the precomputed module
					module.addAll( modules.get( member ) );
				}
			}
		}

		for( OWLEntity other : modEqCls.elements() ) {
			if( modEqCls.isSame( entity, other ) ) {
				modules.get( other ).addAll( module );
			}
		}

		stack.remove( stack.size() - 1 );
		stackElements.remove( entity );
	}

	/**
	 * {@inheritDoc}
	 */
	protected void extractModuleSignatures(Set<? extends OWLEntity> entities) {

		log.fine( "Extract module for " + entities );

		ProgressMonitor monitor = new ConsoleProgressMonitor();
		monitor.setProgressTitle( "Extracting" );
		monitor.setProgressLength( entities.size() );
		monitor.taskStarted();
		
		Set<OWLEntity> nonLocalModule = new HashSet<OWLEntity>();
		for( OWLAxiom axiom : getAxioms() ) {
			if( !isLocal( axiom, Collections.<OWLEntity> emptySet() ) )
				nonLocalModule.addAll( OntologyUtils.getSignature( axiom ) );
		}

		// iterate over classes passed in, and extract all their modules
		for( OWLEntity ent : entities ) {
			monitor.incrementProgress();

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Class: " + ent );

			if( !modules.containsKey( ent ) )
				if( optimizeForSharedModules )
					extractModuleSignature( ent, new HashSet<OWLEntity>(),
							new ArrayList<OWLEntity>(), new HashSet<OWLEntity>( nonLocalModule ) );
				else
					extractModuleSignature( ent, new DisjointSet<OWLEntity>(),
							new ArrayList<OWLEntity>(), new HashSet<OWLEntity>( nonLocalModule ) );
		}

		monitor.taskFinished();

		if( log.isLoggable( Level.FINE ) )
			log.fine( modules.toString() );
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<OWLAxiom> extractModule(Set<? extends OWLEntity> signature) {	
		if( isChanged() )
			resetModules();
		
		Set<OWLEntity> module = new HashSet<OWLEntity>( signature );
		for( OWLAxiom axiom : getAxioms() ) {
			if( !isLocal( axiom, Collections.<OWLEntity> emptySet() ) )
				module.addAll( OntologyUtils.getSignature( axiom ) );
		}
		
		if( !entityAxioms.isEmpty() ) {		
			if( optimizeForSharedModules )
				extractModuleSignature( null, new HashSet<OWLEntity>(),
						new ArrayList<OWLEntity>(), module );
			else
				extractModuleSignature( null, new DisjointSet<OWLEntity>(),
						new ArrayList<OWLEntity>(), module );
		}
		
		return getModuleAxioms( module );
	}

	/**
	 * Returns if shared modules optimization is on.
	 */
	public boolean isOptimizeForSharedModules() {
		return optimizeForSharedModules;
	}

	/**
	 * Sets the the option to optimize for shared modules during module
	 * extraction. This option improves the performance of axiom-based module
	 * extractor when there are many shared modules in the ontology. This option
	 * seems to improve the performance of modularization for NCI thesaurus
	 * significantly but has slight overhead for some other ontologies (it is
	 * not clear if the overhead would more dramatic in other untested cases).
	 * This option has no effect on graph-based extractor which by default
	 * includes optimization for shared modules that does not have negative
	 * impact on any ontology.
	 */
	public void setOptimizeForSharedModules(boolean optimizeForSharedModules) {
		this.optimizeForSharedModules = optimizeForSharedModules;
	}

}
