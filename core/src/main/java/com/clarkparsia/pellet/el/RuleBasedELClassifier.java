// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;

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
@SuppressWarnings("unused")
public abstract class RuleBasedELClassifier extends CDOptimizedTaxonomyBuilder implements TaxonomyBuilder
{
	@SuppressWarnings("hiding")
	public static final Logger _logger = Log.getLogger(RuleBasedELClassifier.class);

	protected Timers _timers = new Timers();

	public RuleBasedELClassifier()
	{
		_logger.fine("new RuleBasedELClassifier");
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
	@Override
	public boolean classify()
	{
		reset();

		_monitor.setProgressTitle("Classifiying");
		_monitor.setProgressLength(_classes.size());
		_monitor.taskStarted();
		_monitor.setProgress(0);

		_logger.info("Creating structures");

		Timer t = _timers.startTimer("createConcepts");
		processAxioms();
		t.stop();

		_logger.info("Running rules");

		final MultiValueMap<ATermAppl, ATermAppl> subsumers = run(_kb.getAllClasses());

		_monitor.setProgress(_classes.size());

		_logger.info("Building hierarchy");

		t = _timers.startTimer("buildHierarchy");
		buildTaxonomy(subsumers);
		t.stop();

		_monitor.setProgress(_classes.size());
		_monitor.taskFinished();

		return true;
	}

	protected void buildTaxonomy(final MultiValueMap<ATermAppl, ATermAppl> subsumers)
	{
		//		CachedSubsumptionComparator subsumptionComparator = new CachedSubsumptionComparator( subsumers );
		//		
		//		POTaxonomyBuilder _builder = new POTaxonomyBuilder( _kb, subsumptionComparator );
		//		_builder.setKB( _kb );
		//		
		//		taxonomy = _builder.getTaxonomy();
		//		
		//		for( ATermAppl c : subsumers.keySet() ) {
		//			if( subsumptionComparator.isSubsumedBy( c, ATermUtils.BOTTOM ) ) {
		//				taxonomy.addEquivalentNode( c, taxonomy.getBottom() );
		//			}
		//			else {
		//				_builder.classify( c );
		//			}
		//		}

		_taxonomy = new GenericTaxonomyBuilder().build(subsumers);
	}

	private void toELSubClassAxioms(final ATermAppl axiom)
	{
		final AFun fun = axiom.getAFun();
		final ATermAppl sub = (ATermAppl) axiom.getArgument(0);
		final ATermAppl sup = (ATermAppl) axiom.getArgument(1);

		final ATermAppl subEL = ELSyntaxUtils.simplify(sub);
		if (fun.equals(ATermUtils.SUBFUN))
		{
			if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup))
			{
				addSubclassRule(subEL, sup);
				return;
			}

			final ATermAppl supEL = ELSyntaxUtils.simplify(sup);
			addSubclassRule(subEL, supEL);
		}
		else
			if (fun.equals(ATermUtils.EQCLASSFUN))
			{
				final ATermAppl supEL = ELSyntaxUtils.simplify(sup);
				addSubclassRule(subEL, supEL);
				addSubclassRule(supEL, subEL);
			}
			else
				throw new IllegalArgumentException("Axiom " + axiom + " is not EL.");
	}

	private void processAxioms()
	{
		//EquivalentClass -> SubClasses
		//Disjoint Classes -> SubClass
		//Normalize ATerm lists to sets
		final Collection<ATermAppl> assertedAxioms = _kb.getTBox().getAssertedAxioms();
		for (final ATermAppl assertedAxiom : assertedAxioms)
			toELSubClassAxioms(assertedAxiom);

		//Role Hierarchies
		for (final Role r : _kb.getRBox().getRoles())
		{
			final ATermAppl role = r.getName();
			for (final Set<ATermAppl> supers : _kb.getSuperProperties(role))
				for (final ATermAppl sup : supers)
					addRoleHierarchyRule(role, sup);
		}

		//Role Chains
		for (final Role supRole : _kb.getRBox().getRoles())
			for (final ATermList chainList : supRole.getSubRoleChains())
			{
				final ATerm[] chain = ATermUtils.toArray(chainList);
				addRoleChainRule(chain, supRole.getName());
			}

		//Role Domain Restrictions
		final RoleRestrictionCache roleRestrictions = new RoleRestrictionCache(_kb.getRBox());
		for (final Entry<ATermAppl, ATermAppl> entry : roleRestrictions.getDomains().entrySet())
			addRoleDomainRule(entry.getKey(), entry.getValue());

		//Role Range Restrictions
		for (final Entry<ATermAppl, ATermAppl> entry : roleRestrictions.getRanges().entrySet())
			addRoleRangeRule(entry.getKey(), entry.getValue());

		//Reflexive Roles
		for (final Role role : _kb.getRBox().getRoles())
			if (role.isReflexive())
			{
				final ATermAppl range = roleRestrictions.getRange(role.getName());
				if (range == null)
					continue;

				addSubclassRule(ATermUtils.TOP, range);
			}
	}
}
