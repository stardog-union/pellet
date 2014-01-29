// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.Role;
import org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.POTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.PartialOrderRelation;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.CollectionUtils;

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
public class ELClassifier extends CDOptimizedTaxonomyBuilder implements TaxonomyBuilder {
	public static final Logger						logger	= Logger.getLogger( ELClassifier.class.getName() );

	public final Timers								timers	= new Timers();

	public ConceptInfo								TOP;
	public ConceptInfo								BOTTOM;
	
	private boolean									hasComplexRoles;

	private MultiValueMap<ConceptInfo, Trigger>		queue;

	private Map<ATermAppl, ConceptInfo>				concepts;

	private RoleChainCache							roleChains;
	private RoleRestrictionCache					roleRestrictions;

	private PartialOrderComparator<ATermAppl>	subsumptionComparator	= new PartialOrderComparator<ATermAppl>() {
		public PartialOrderRelation compare(ATermAppl a, ATermAppl b) {
//			if( a == ATermUtils.BOTTOM )
//				return PartialOrderRelation.LESS;
//			else if( b == ATermUtils.TOP )
//				return PartialOrderRelation.GREATER;

			ConceptInfo aInfo = getInfo( a );
			ConceptInfo bInfo = getInfo( b );

			if( aInfo.hasSuperClass( bInfo ) ) {
				if( bInfo.hasSuperClass( aInfo ) ) {
					return PartialOrderRelation.EQUAL;
				}
				else {
					return PartialOrderRelation.LESS;
				}
			}
			else if( bInfo.hasSuperClass( aInfo ) ) {
				return PartialOrderRelation.GREATER;
			}
			else {
				return PartialOrderRelation.INCOMPARABLE;
			}
		}
	};

	public ELClassifier() {
	}
	
	protected void reset() {
		super.reset();
		
		hasComplexRoles = kb.getExpressivity().hasTransitivity()
				|| kb.getExpressivity().hasComplexSubRoles();
		
		queue = new MultiValueMap<ConceptInfo, Trigger>();
		concepts = CollectionUtils.makeMap();
		
		roleChains = new RoleChainCache(kb);
		roleRestrictions = new RoleRestrictionCache( kb.getRBox() );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean classify() {
		reset();
		
		kb.prepare();
		
		Timer t = timers.startTimer( "createConcepts" );
		logger.info( "Creating structures" );
		createConcepts();
		logger.info( "Created structures" );
		t.stop();
		
		monitor.setProgressTitle( "Classifiying" );
		monitor.setProgressLength( queue.size() );
		monitor.taskStarted();

		printStructures();

		logger.info( "Processing queue" );
		t = timers.startTimer( "processQueue" );
		processQueue();
		t.stop();
		logger.info( "Processed queue" );

		if( logger.isLoggable( Level.FINE ) )
			print();

		logger.info( "Building hierarchy" );
		t = timers.startTimer( "buildHierarchy" );
		
		taxonomy = new ELTaxonomyBuilder().build( concepts );
//		buildTaxonomyWithPO();
		
		t.stop();
		logger.info( "Builded hierarchy" );

		monitor.taskFinished();
		
		return true;
	}

	private void buildTaxonomyWithPO() {
		POTaxonomyBuilder builder = new POTaxonomyBuilder(kb, subsumptionComparator);
		taxonomy = builder.getTaxonomy();
		
		for( ConceptInfo ci : concepts.values() ) {
			classify( ci );
		}
	}
	
	private void classify(ConceptInfo ci) {
		ATermAppl c = ci.getConcept();
		if( !ATermUtils.isPrimitive( c ) ) 
			return;
		
		if( ci.getSuperClasses().contains(BOTTOM) ) {
			taxonomy.addEquivalentNode( c, taxonomy.getBottom() );
			return;
		}
		
		Set<ATermAppl> equivalents = new HashSet<ATermAppl>();
		for( ConceptInfo subsumer : ci.getSuperClasses() ) {
			if( !ATermUtils.isPrimitive( subsumer.getConcept() ) ) {
				continue;
			}

			if( ci.equals( subsumer ) ) {
				continue;
			}
			else if( subsumer.hasSuperClass( ci ) ) {
				equivalents.add( subsumer.getConcept() );
			}
			else {
				classify( subsumer );
			}			
		}

		taxonomy.addEquivalents( c, equivalents );
	}

	private void addExistential(ConceptInfo ci, ATermAppl prop, ConceptInfo qi) {
		if (ci.hasSuccessor(prop, qi)) return;
		
		addExistentialP( ci, prop, qi );

		Set<Set<ATermAppl>> supEqs = kb.getSuperProperties(prop);
		for( Set<ATermAppl> supEq : supEqs ) {
			for( ATermAppl sup : supEq ) {
				addExistentialP( ci, sup, qi );
			}
		}
	}

	private void addExistentialP(ConceptInfo ci, ATermAppl prop, ConceptInfo qi) {
		if (ci.hasSuccessor(prop, qi)) return;
		
		for( ConceptInfo supInfo : qi.getSuperClasses() ) {
			if( ci.addSuccessor( prop, supInfo ) ) {
				if (supInfo.equals(BOTTOM)) {
					addSubsumer(ci, BOTTOM);
					
					Iterator<ConceptInfo> preds = ci.getPredecessors().flattenedValues();
					while( preds.hasNext() ) {
						addSubsumer(preds.next(), BOTTOM);
					}
				}
				
				ATermAppl some = ATermUtils.makeSomeValues( prop, supInfo.getConcept() );
				ConceptInfo si = getInfo( some );
				if( si != null ) {
					addToQueue( ci, si.getTriggers() );
				}
			}
		}
		
		ATermAppl q = qi.getConcept();
		if (ATermUtils.isAnd(q)) {
			ATermList list = (ATermList) q.getArgument( 0 );
			while( !list.isEmpty() ) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				ConceptInfo conjInfo = createConcept(conj);
				if( ci.addSuccessor( prop, conjInfo ) ) {
					ATermAppl some = ATermUtils.makeSomeValues( prop, conj );
					ConceptInfo si = createConcept( some );
					addToQueue( ci, si.getTriggers() );
				}

				list = list.getNext();
			}
		}

		ATermAppl propRange = roleRestrictions.getRange(prop);
		if (propRange != null) {
			ATermAppl some = ATermUtils.makeSomeValues(prop, propRange);
			ConceptInfo si = createConcept(some);
			addSubsumer(ci, si);
		}
		
		if( hasComplexRoles ) {
			for (Entry<ATermAppl, Set<ConceptInfo>> entry : ci.getPredecessors().entrySet()) {
				ATermAppl predProp = entry.getKey();
				for (ConceptInfo pred : entry.getValue()) {
					for (ATermAppl supProp : roleChains.getAllSuperRoles(predProp, prop)) {
						addExistential(pred, supProp, qi);
					}
				}
			}
	
			for (Entry<ATermAppl, Set<ConceptInfo>> entry : qi.getSuccessors().entrySet()) {
				ATermAppl succProp = entry.getKey();
				for (ConceptInfo succ : entry.getValue()) {
					for (ATermAppl supProp : roleChains.getAllSuperRoles(prop, succProp)) {
						addExistential(ci, supProp, succ);
					}
				}
			}
		}
	}

	private void addToQueue(ConceptInfo ci, Set<Trigger> triggers) {
		if( queue.addAll( ci, triggers ) ) {
			if( logger.isLoggable( Level.FINE ) )
				logger.fine( "Add to queue: " + ci + " " + triggers );
		}
	}

	private void addSubsumer(ConceptInfo ci, ConceptInfo supInfo) {
		if (ci.hasSuperClass(supInfo)) return;

		ci.addSuperClass(supInfo);
		
		if (ATermUtils.isBottom(supInfo.getConcept())) {			
			Iterator<ConceptInfo> preds = ci.getPredecessors().flattenedValues();
			while( preds.hasNext() ) {
				addSubsumer( preds.next(), supInfo );
			}
			return;
		}
		
		addToQueue(ci, supInfo.getTriggers());		
		
		ATermAppl c = supInfo.getConcept();
		if( logger.isLoggable( Level.FINE ) )
			logger.fine( "Adding subsumers to " + ci + " " + c + " " + ci.getSuperClasses() );

		if( ATermUtils.isAnd( c ) ) {
			ATermList list = (ATermList) c.getArgument( 0 );
			while( !list.isEmpty() ) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				
				addSubsumer( ci, createConcept( conj ) );

				list = list.getNext();
			}
		} else if( ATermUtils.isSomeValues( c ) ) {
			ATermAppl p = (ATermAppl) c.getArgument( 0 );
			ATermAppl qualification = (ATermAppl) c.getArgument( 1 );
			ConceptInfo q = createConcept( qualification );

			addExistential( ci, p, q );
		} else {
			assert ATermUtils.isPrimitive( c );
		}

		for( Map.Entry<ATermAppl, Set<ConceptInfo>> e : ci.getPredecessors().entrySet() ) {
			ATermAppl prop = e.getKey();
			for( ConceptInfo pred: e.getValue() ) {
				ATermAppl some = ATermUtils.makeSomeValues( prop, c );
				ConceptInfo si = getInfo( some );
				if( si != null) {
					addToQueue( pred, si.getTriggers() );
				}
			}
		}
	}

	private ConceptInfo createConcept(ATermAppl c) {
		ConceptInfo ci = getInfo( c );
		if( ci == null ) {
			ci = new ConceptInfo( c, hasComplexRoles, false );
			concepts.put( c, ci );
			ci.addSuperClass( TOP );
			addSubsumer(ci, ci);

			if( ATermUtils.isAnd( c ) ) {
				ConceptInfo[] conjuncts = createConceptArray((ATermList) c.getArgument(0));
				for (ConceptInfo conjInfo : conjuncts) {
					conjInfo.addTrigger(new Trigger(conjuncts, ci));
				}
			} else if( ATermUtils.isSomeValues( c ) ) {
				ATermAppl q = (ATermAppl) c.getArgument(1);
				createConcept(q);
				ci.addTrigger(new Trigger(ci));
			}
		}

		return ci;
	}	

	private ConceptInfo[] createConceptArray(ATermList list) {
		ConceptInfo[] a = new ConceptInfo[list.getLength()];

		for( int i = 0; !list.isEmpty(); list = list.getNext() )
			a[i++] = createConcept( (ATermAppl) list.getFirst() );

		return a;
	}

	private void createConceptsFromAxiom(ATermAppl sub, ATermAppl sup) {
		ConceptInfo ciSub = createConcept( sub );
		ConceptInfo ciSup = createConcept( sup );

		Trigger trigger = new Trigger( ciSup );
		
		ciSub.addTrigger(trigger);
	}

	private void toELSubClassAxioms(ATermAppl axiom) {
		AFun fun = axiom.getAFun();
		ATermAppl sub = (ATermAppl) axiom.getArgument(0);
		ATermAppl sup = (ATermAppl) axiom.getArgument(1);
		
		ATermAppl subEL = ELSyntaxUtils.simplify(sub);
		if (fun.equals(ATermUtils.SUBFUN)) {
			if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup)) {
				createConceptsFromAxiom(subEL, sup);
				return;
			}
			
			ATermAppl supEL = ELSyntaxUtils.simplify(sup);
			createConceptsFromAxiom(subEL, supEL);
		} else if (fun.equals( ATermUtils.EQCLASSFUN )) {
			ATermAppl supEL = ELSyntaxUtils.simplify(sup);
			createConceptsFromAxiom(subEL, supEL);
			createConceptsFromAxiom(supEL, subEL);
		} else {
			throw new IllegalArgumentException("Axiom " + axiom + " is not EL.");
		}
	}
	
	private void normalizeAxioms() {
		//EquivalentClass -> SubClasses
		//Disjoint Classes -> SubClass
		//Normalize ATerm lists to sets
		Collection<ATermAppl> assertedAxioms = kb.getTBox().getAssertedAxioms();
		for (ATermAppl assertedAxiom : assertedAxioms ) {
			toELSubClassAxioms(assertedAxiom);
		}

		//Convert Role Domains to axioms
		for (Entry<ATermAppl, ATermAppl> entry : roleRestrictions.getDomains().entrySet()) {
			ATermAppl roleName = entry.getKey();
			ATermAppl domain = entry.getValue();
			createConceptsFromAxiom(ATermUtils.makeSomeValues(roleName, ATermUtils.TOP), domain);
		}
		
		//Convert Reflexive Roles to axioms
		for (Role role : kb.getRBox().getRoles()) {
			if (role.isReflexive()) {
				ATermAppl range = roleRestrictions.getRange(role.getName());
				if (range == null) continue;
				
				createConceptsFromAxiom(ATermUtils.TOP, range);
			}
		}
	}

	private void createConcepts() {
		TOP		= new ConceptInfo( ATermUtils.TOP, hasComplexRoles, false );
		concepts.put( ATermUtils.TOP, TOP );
		TOP.addSuperClass(TOP);
		
		BOTTOM	= new ConceptInfo( ATermUtils.BOTTOM, hasComplexRoles, false );
		concepts.put( ATermUtils.BOTTOM, BOTTOM );
		BOTTOM.addSuperClass(BOTTOM);
		
		for (ATermAppl c : kb.getClasses()) {
			createConcept(c);
		}
		
		normalizeAxioms();
		
		final Set<Trigger> TOP_TRIGGERS = TOP.getTriggers();
		for( ConceptInfo ci : concepts.values() ) {
			Set<Trigger> queueList = CollectionUtils.makeSet(TOP_TRIGGERS);
			queueList.addAll( ci.getTriggers() );

			if( !queueList.isEmpty() )
				queue.addAll( ci, queueList );
		}
	}

	private ConceptInfo getInfo(ATermAppl concept) {
		return concepts.get( concept );
	}

	public void print() {
		for( ConceptInfo ci : concepts.values() ) {
			System.out.println( ci + " " + ci.getSuperClasses() );
		}
		System.out.println();
		roleChains.print();
	}

	public void printStructures() {
		if( logger.isLoggable( Level.FINE ) ) {
			for( ConceptInfo ci : concepts.values() ) {
				logger.fine( ci + "\t" + ci.getTriggers() + "\t" + ci.getSuperClasses() );
			}
		}
	}

	private void processQueue() {
		int startingSize = queue.size();
		while( !queue.isEmpty() ) {
			int processed = startingSize - queue.size();
			if( monitor.getProgress() < processed )
				monitor.setProgress( processed );

			MultiValueMap<ConceptInfo, Trigger> localQueue = queue;
			queue = new MultiValueMap<ConceptInfo, Trigger>();
			
			for (Entry<ConceptInfo, Set<Trigger>> entry : localQueue.entrySet()) {
				ConceptInfo ci = entry.getKey();
				for (Trigger trigger : entry.getValue()) {
					processTrigger(ci, trigger);
				}
			}
		}
	}

	private void processTrigger(ConceptInfo ci, Trigger trigger) {
		if (trigger.isTriggered(ci)) {
			//if( logger.isLoggable( Level.FINE ) ) logger.fine( "Process (true) for " + ci + " " + trigger );
			
			addSubsumer(ci, trigger.getConsequence());
		}
	}
}
