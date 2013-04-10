// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.mindswap.pellet.Role;
import org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>
 * Title: 
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class RuleBasedELClassifier extends CDOptimizedTaxonomyBuilder implements TaxonomyBuilder {
	public static final Logger						logger	= Logger.getLogger( RuleBasedELClassifier.class.getName() );
	
	protected Timers timers = new Timers();	
	
	public RuleBasedELClassifier() {
	}
	
	protected abstract void addSubclassRule(ATermAppl sub, ATermAppl sup);

	protected abstract void addRoleDomainRule(ATermAppl p, ATermAppl domain);

	protected abstract void addRoleRangeRule(ATermAppl p, ATermAppl range);

	protected abstract void addRoleChainRule(ATerm[] chain, ATermAppl sup);

	protected abstract void addRoleHierarchyRule(ATermAppl sub, ATermAppl sup);
	
	protected abstract MultiValueMap<ATermAppl, ATermAppl> run(Collection<ATermAppl> classes);

	/**
	 * {@inheritDoc}
	 */
	public boolean classify() {		
		reset();
		
		monitor.setProgressTitle( "Classifiying" );
		monitor.setProgressLength( classes.size() );
		monitor.taskStarted();
		monitor.setProgress( 0 );
		
		logger.info( "Creating structures" );
		
		Timer t = timers.startTimer( "createConcepts" );
		processAxioms();
		t.stop();
		
		logger.info( "Running rules" );
		
		MultiValueMap<ATermAppl, ATermAppl> subsumers = run( kb.getAllClasses() );
		
		monitor.setProgress( classes.size() );
		
		logger.info( "Building hierarchy" );
		
		t = timers.startTimer( "buildHierarchy" );
		buildTaxonomy( subsumers );
		t.stop();
		
		monitor.setProgress( classes.size() );
		monitor.taskFinished();
		
		return true;
	}
		
	protected void buildTaxonomy(MultiValueMap<ATermAppl, ATermAppl> subsumers) {
//		CachedSubsumptionComparator subsumptionComparator = new CachedSubsumptionComparator( subsumers );
//		
//		POTaxonomyBuilder builder = new POTaxonomyBuilder( kb, subsumptionComparator );
//		builder.setKB( kb );
//		
//		taxonomy = builder.getTaxonomy();
//		
//		for( ATermAppl c : subsumers.keySet() ) {
//			if( subsumptionComparator.isSubsumedBy( c, ATermUtils.BOTTOM ) ) {
//				taxonomy.addEquivalentNode( c, taxonomy.getBottom() );
//			}
//			else {
//				builder.classify( c );
//			}
//		}
		
		taxonomy = new GenericTaxonomyBuilder().build( subsumers );
	}
	
	private void toELSubClassAxioms(ATermAppl axiom) {
		AFun fun = axiom.getAFun();
		ATermAppl sub = (ATermAppl) axiom.getArgument(0);
		ATermAppl sup = (ATermAppl) axiom.getArgument(1);
		
		ATermAppl subEL = ELSyntaxUtils.simplify(sub);
		if (fun.equals(ATermUtils.SUBFUN)) {
			if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup)) {
				addSubclassRule(subEL, sup);
				return;
			}
			
			ATermAppl supEL = ELSyntaxUtils.simplify(sup);
			addSubclassRule(subEL, supEL);
		} else if (fun.equals( ATermUtils.EQCLASSFUN )) {
			ATermAppl supEL = ELSyntaxUtils.simplify(sup);
			addSubclassRule(subEL, supEL);
			addSubclassRule(supEL, subEL);
		} else {
			throw new IllegalArgumentException("Axiom " + axiom + " is not EL.");
		}
	}
	
	private void processAxioms() {
		//EquivalentClass -> SubClasses
		//Disjoint Classes -> SubClass
		//Normalize ATerm lists to sets
		Collection<ATermAppl> assertedAxioms = kb.getTBox().getAssertedAxioms();
		for (ATermAppl assertedAxiom : assertedAxioms ) {
			toELSubClassAxioms(assertedAxiom);
		}

		//Role Hierarchies
		for (Role r : kb.getRBox().getRoles()) {
			ATermAppl role = r.getName();
			for (Set<ATermAppl> supers : kb.getSuperProperties(role)) {
				for (ATermAppl sup : supers) {
					addRoleHierarchyRule(role, sup);
				}
			}
		}
		
		//Role Chains
		for (Role supRole : kb.getRBox().getRoles()) {
			for (ATermList chainList : supRole.getSubRoleChains()) {
				ATerm[] chain = ATermUtils.toArray(chainList);
				addRoleChainRule(chain, supRole.getName());
			}
		}
		
		//Role Domain Restrictions
		RoleRestrictionCache roleRestrictions = new RoleRestrictionCache( kb.getRBox() );
		for (Entry<ATermAppl, ATermAppl> entry : roleRestrictions.getDomains().entrySet()) {
			addRoleDomainRule(entry.getKey(), entry.getValue());
		}
		
		//Role Range Restrictions
		for (Entry<ATermAppl, ATermAppl> entry : roleRestrictions.getRanges().entrySet()) {
			addRoleRangeRule(entry.getKey(), entry.getValue());
		}
		
		//Reflexive Roles
		for (Role role : kb.getRBox().getRoles()) {
			if (role.isReflexive()) {
				ATermAppl range = roleRestrictions.getRange(role.getName());
				if (range == null) continue;
				
				addSubclassRule(ATermUtils.TOP, range);
			}
		}
	}
}
