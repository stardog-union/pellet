// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.POTaxonomyBuilder;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.PartialOrderRelation;
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
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class ELClassifier extends CDOptimizedTaxonomyBuilder
{
	public static final Logger logger = Logger.getLogger(ELClassifier.class.getName());

	public final Timers timers = new Timers();

	public ConceptInfo TOP;
	public ConceptInfo BOTTOM;

	private boolean hasComplexRoles;

	private MultiValueMap<ConceptInfo, Trigger> queue;

	private Map<ATermAppl, ConceptInfo> concepts;

	private RoleChainCache roleChains;
	private RoleRestrictionCache roleRestrictions;

	private final PartialOrderComparator<ATermAppl> subsumptionComparator = (a, b) ->
	{
		//			if( a == ATermUtils.BOTTOM )
		//				return PartialOrderRelation.LESS;
		//			else if( b == ATermUtils.TOP )
		//				return PartialOrderRelation.GREATER;

		final ConceptInfo aInfo = getInfo(a);
		final ConceptInfo bInfo = getInfo(b);

		if (aInfo.hasSuperClass(bInfo))
		{
			if (bInfo.hasSuperClass(aInfo))
				return PartialOrderRelation.EQUAL;
			else
				return PartialOrderRelation.LESS;
		}
		else
			if (bInfo.hasSuperClass(aInfo))
				return PartialOrderRelation.GREATER;
			else
				return PartialOrderRelation.INCOMPARABLE;
	};

	public ELClassifier()
	{
	}

	@Override
	protected void reset()
	{
		super.reset();

		hasComplexRoles = kb.getExpressivity().hasTransitivity() || kb.getExpressivity().hasComplexSubRoles();

		queue = new MultiValueMap<>();
		concepts = CollectionUtils.makeMap();

		roleChains = new RoleChainCache(kb);
		roleRestrictions = new RoleRestrictionCache(kb.getRBox());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean classify()
	{
		reset();

		kb.prepare();

		Timer t = timers.startTimer("createConcepts");
		logger.info("Creating structures");
		createConcepts();
		logger.info("Created structures");
		t.stop();

		monitor.setProgressTitle("Classifiying");
		monitor.setProgressLength(queue.size());
		monitor.taskStarted();

		printStructures();

		logger.info("Processing queue");
		t = timers.startTimer("processQueue");
		processQueue();
		t.stop();
		logger.info("Processed queue");

		if (logger.isLoggable(Level.FINE))
			print();

		logger.info("Building hierarchy");
		t = timers.startTimer("buildHierarchy");

		taxonomy = new ELTaxonomyBuilder().build(concepts);
		//		buildTaxonomyWithPO();

		t.stop();
		logger.info("Builded hierarchy");

		monitor.taskFinished();

		return true;
	}

	private void buildTaxonomyWithPO()
	{
		final POTaxonomyBuilder builder = new POTaxonomyBuilder(kb, subsumptionComparator);
		taxonomy = builder.getTaxonomy();

		for (final ConceptInfo ci : concepts.values())
			classify(ci);
	}

	private void classify(final ConceptInfo ci)
	{
		final ATermAppl c = ci.getConcept();
		if (!ATermUtils.isPrimitive(c))
			return;

		if (ci.getSuperClasses().contains(BOTTOM))
		{
			taxonomy.addEquivalentNode(c, taxonomy.getBottom());
			return;
		}

		final Set<ATermAppl> equivalents = new HashSet<>();
		for (final ConceptInfo subsumer : ci.getSuperClasses())
		{
			if (!ATermUtils.isPrimitive(subsumer.getConcept()))
				continue;

			if (ci.equals(subsumer))
				continue;
			else
				if (subsumer.hasSuperClass(ci))
					equivalents.add(subsumer.getConcept());
				else
					classify(subsumer);
		}

		taxonomy.addEquivalents(c, equivalents);
	}

	private void addExistential(final ConceptInfo ci, final ATermAppl prop, final ConceptInfo qi)
	{
		if (ci.hasSuccessor(prop, qi))
			return;

		addExistentialP(ci, prop, qi);

		final Set<Set<ATermAppl>> supEqs = kb.getSuperProperties(prop);
		for (final Set<ATermAppl> supEq : supEqs)
			for (final ATermAppl sup : supEq)
				addExistentialP(ci, sup, qi);
	}

	private void addExistentialP(final ConceptInfo ci, final ATermAppl prop, final ConceptInfo qi)
	{
		if (ci.hasSuccessor(prop, qi))
			return;

		for (final ConceptInfo supInfo : qi.getSuperClasses())
			if (ci.addSuccessor(prop, supInfo))
			{
				if (supInfo.equals(BOTTOM))
				{
					addSubsumer(ci, BOTTOM);

					final Iterator<ConceptInfo> preds = ci.getPredecessors().flattenedValues();
					while (preds.hasNext())
						addSubsumer(preds.next(), BOTTOM);
				}

				final ATermAppl some = ATermUtils.makeSomeValues(prop, supInfo.getConcept());
				final ConceptInfo si = getInfo(some);
				if (si != null)
					addToQueue(ci, si.getTriggers());
			}

		final ATermAppl q = qi.getConcept();
		if (ATermUtils.isAnd(q))
		{
			ATermList list = (ATermList) q.getArgument(0);
			while (!list.isEmpty())
			{
				final ATermAppl conj = (ATermAppl) list.getFirst();
				final ConceptInfo conjInfo = createConcept(conj);
				if (ci.addSuccessor(prop, conjInfo))
				{
					final ATermAppl some = ATermUtils.makeSomeValues(prop, conj);
					final ConceptInfo si = createConcept(some);
					addToQueue(ci, si.getTriggers());
				}

				list = list.getNext();
			}
		}

		final ATermAppl propRange = roleRestrictions.getRange(prop);
		if (propRange != null)
		{
			final ATermAppl some = ATermUtils.makeSomeValues(prop, propRange);
			final ConceptInfo si = createConcept(some);
			addSubsumer(ci, si);
		}

		if (hasComplexRoles)
		{
			for (final Entry<ATermAppl, Set<ConceptInfo>> entry : ci.getPredecessors().entrySet())
			{
				final ATermAppl predProp = entry.getKey();
				for (final ConceptInfo pred : entry.getValue())
					for (final ATermAppl supProp : roleChains.getAllSuperRoles(predProp, prop))
						addExistential(pred, supProp, qi);
			}

			for (final Entry<ATermAppl, Set<ConceptInfo>> entry : qi.getSuccessors().entrySet())
			{
				final ATermAppl succProp = entry.getKey();
				for (final ConceptInfo succ : entry.getValue())
					for (final ATermAppl supProp : roleChains.getAllSuperRoles(prop, succProp))
						addExistential(ci, supProp, succ);
			}
		}
	}

	private void addToQueue(final ConceptInfo ci, final Set<Trigger> triggers)
	{
		if (queue.addAll(ci, triggers))
			if (logger.isLoggable(Level.FINE))
				logger.fine("Add to queue: " + ci + " " + triggers);
	}

	private void addSubsumer(final ConceptInfo ci, final ConceptInfo supInfo)
	{
		if (ci.hasSuperClass(supInfo))
			return;

		ci.addSuperClass(supInfo);

		if (ATermUtils.isBottom(supInfo.getConcept()))
		{
			final Iterator<ConceptInfo> preds = ci.getPredecessors().flattenedValues();
			while (preds.hasNext())
				addSubsumer(preds.next(), supInfo);
			return;
		}

		addToQueue(ci, supInfo.getTriggers());

		final ATermAppl c = supInfo.getConcept();
		if (logger.isLoggable(Level.FINE))
			logger.fine("Adding subsumers to " + ci + " " + c + " " + ci.getSuperClasses());

		if (ATermUtils.isAnd(c))
		{
			ATermList list = (ATermList) c.getArgument(0);
			while (!list.isEmpty())
			{
				final ATermAppl conj = (ATermAppl) list.getFirst();

				addSubsumer(ci, createConcept(conj));

				list = list.getNext();
			}
		}
		else
			if (ATermUtils.isSomeValues(c))
			{
				final ATermAppl p = (ATermAppl) c.getArgument(0);
				final ATermAppl qualification = (ATermAppl) c.getArgument(1);
				final ConceptInfo q = createConcept(qualification);

				addExistential(ci, p, q);
			}
			else
				assert ATermUtils.isPrimitive(c);

		for (final Map.Entry<ATermAppl, Set<ConceptInfo>> e : ci.getPredecessors().entrySet())
		{
			final ATermAppl prop = e.getKey();
			for (final ConceptInfo pred : e.getValue())
			{
				final ATermAppl some = ATermUtils.makeSomeValues(prop, c);
				final ConceptInfo si = getInfo(some);
				if (si != null)
					addToQueue(pred, si.getTriggers());
			}
		}
	}

	private ConceptInfo createConcept(final ATermAppl c)
	{
		ConceptInfo ci = getInfo(c);
		if (ci == null)
		{
			ci = new ConceptInfo(c, hasComplexRoles, false);
			concepts.put(c, ci);
			ci.addSuperClass(TOP);
			addSubsumer(ci, ci);

			if (ATermUtils.isAnd(c))
			{
				final ConceptInfo[] conjuncts = createConceptArray((ATermList) c.getArgument(0));
				for (final ConceptInfo conjInfo : conjuncts)
					conjInfo.addTrigger(new Trigger(conjuncts, ci));
			}
			else
				if (ATermUtils.isSomeValues(c))
				{
					final ATermAppl q = (ATermAppl) c.getArgument(1);
					createConcept(q);
					ci.addTrigger(new Trigger(ci));
				}
		}

		return ci;
	}

	private ConceptInfo[] createConceptArray(ATermList list)
	{
		final ConceptInfo[] a = new ConceptInfo[list.getLength()];

		for (int i = 0; !list.isEmpty(); list = list.getNext())
			a[i++] = createConcept((ATermAppl) list.getFirst());

		return a;
	}

	private void createConceptsFromAxiom(final ATermAppl sub, final ATermAppl sup)
	{
		final ConceptInfo ciSub = createConcept(sub);
		final ConceptInfo ciSup = createConcept(sup);

		final Trigger trigger = new Trigger(ciSup);

		ciSub.addTrigger(trigger);
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
				createConceptsFromAxiom(subEL, sup);
				return;
			}

			final ATermAppl supEL = ELSyntaxUtils.simplify(sup);
			createConceptsFromAxiom(subEL, supEL);
		}
		else
			if (fun.equals(ATermUtils.EQCLASSFUN))
			{
				final ATermAppl supEL = ELSyntaxUtils.simplify(sup);
				createConceptsFromAxiom(subEL, supEL);
				createConceptsFromAxiom(supEL, subEL);
			}
			else
				throw new IllegalArgumentException("Axiom " + axiom + " is not EL.");
	}

	private void normalizeAxioms()
	{
		//EquivalentClass -> SubClasses
		//Disjoint Classes -> SubClass
		//Normalize ATerm lists to sets
		final Collection<ATermAppl> assertedAxioms = kb.getTBox().getAssertedAxioms();
		for (final ATermAppl assertedAxiom : assertedAxioms)
			toELSubClassAxioms(assertedAxiom);

		//Convert Role Domains to axioms
		for (final Entry<ATermAppl, ATermAppl> entry : roleRestrictions.getDomains().entrySet())
		{
			final ATermAppl roleName = entry.getKey();
			final ATermAppl domain = entry.getValue();
			createConceptsFromAxiom(ATermUtils.makeSomeValues(roleName, ATermUtils.TOP), domain);
		}

		//Convert Reflexive Roles to axioms
		for (final Role role : kb.getRBox().getRoles())
			if (role.isReflexive())
			{
				final ATermAppl range = roleRestrictions.getRange(role.getName());
				if (range == null)
					continue;

				createConceptsFromAxiom(ATermUtils.TOP, range);
			}
	}

	private void createConcepts()
	{
		TOP = new ConceptInfo(ATermUtils.TOP, hasComplexRoles, false);
		concepts.put(ATermUtils.TOP, TOP);
		TOP.addSuperClass(TOP);

		BOTTOM = new ConceptInfo(ATermUtils.BOTTOM, hasComplexRoles, false);
		concepts.put(ATermUtils.BOTTOM, BOTTOM);
		BOTTOM.addSuperClass(BOTTOM);

		for (final ATermAppl c : kb.getClasses())
			createConcept(c);

		normalizeAxioms();

		final Set<Trigger> TOP_TRIGGERS = TOP.getTriggers();
		for (final ConceptInfo ci : concepts.values())
		{
			final Set<Trigger> queueList = CollectionUtils.makeSet(TOP_TRIGGERS);
			queueList.addAll(ci.getTriggers());

			if (!queueList.isEmpty())
				queue.addAll(ci, queueList);
		}
	}

	private ConceptInfo getInfo(final ATermAppl concept)
	{
		return concepts.get(concept);
	}

	public void print()
	{
		for (final ConceptInfo ci : concepts.values())
			System.out.println(ci + " " + ci.getSuperClasses());
		System.out.println();
		roleChains.print();
	}

	public void printStructures()
	{
		if (logger.isLoggable(Level.FINE))
			for (final ConceptInfo ci : concepts.values())
				logger.fine(ci + "\t" + ci.getTriggers() + "\t" + ci.getSuperClasses());
	}

	private void processQueue()
	{
		final int startingSize = queue.size();
		while (!queue.isEmpty())
		{
			final int processed = startingSize - queue.size();
			if (monitor.getProgress() < processed)
				monitor.setProgress(processed);

			final MultiValueMap<ConceptInfo, Trigger> localQueue = queue;
			queue = new MultiValueMap<>();

			for (final Entry<ConceptInfo, Set<Trigger>> entry : localQueue.entrySet())
			{
				final ConceptInfo ci = entry.getKey();
				for (final Trigger trigger : entry.getValue())
					processTrigger(ci, trigger);
			}
		}
	}

	private void processTrigger(final ConceptInfo ci, final Trigger trigger)
	{
		if (trigger.isTriggered(ci))
			addSubsumer(ci, trigger.getConsequence());
	}
}
