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
import com.clarkparsia.pellet.utils.TermFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.Taxonomy;
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
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
@SuppressWarnings("unused")
public class SimplifiedELClassifier extends CDOptimizedTaxonomyBuilder implements TaxonomyBuilder
{
	public static final Logger logger = Logger.getLogger(SimplifiedELClassifier.class.getName());

	private static class QueueElement
	{
		private final ConceptInfo sub;
		private final ConceptInfo sup;

		public QueueElement(final ConceptInfo sub, final ConceptInfo sup)
		{
			this.sub = sub;
			this.sup = sup;
		}
	}

	public final Timers timers = new Timers();

	private static final boolean PREPROCESS_DOMAINS = false;

	private static final boolean MATERIALIZE_SUPER_PROPERTIES = false;

	private ConceptInfo TOP;
	private ConceptInfo BOTTOM;

	private boolean hasComplexRoles;

	private Queue<QueueElement> primaryQueue;

	private Map<ATermAppl, ConceptInfo> concepts;

	private MultiValueMap<ATermAppl, ConceptInfo> existentials;
	private MultiValueMap<ConceptInfo, ConceptInfo> conjunctions;

	private RoleChainCache roleChains;
	private RoleRestrictionCache roleRestrictions;

	public SimplifiedELClassifier()
	{
	}

	@Override
	protected void reset()
	{
		super.reset();

		hasComplexRoles = kb.getExpressivity().hasTransitivity() || kb.getExpressivity().hasComplexSubRoles();

		primaryQueue = new LinkedList<>();

		concepts = CollectionUtils.makeMap();

		existentials = new MultiValueMap<>();
		conjunctions = new MultiValueMap<>();

		roleChains = new RoleChainCache(kb);
		roleRestrictions = new RoleRestrictionCache(kb.getRBox());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean classify()
	{
		logger.fine("Reset");
		reset();

		Timer t = timers.startTimer("createConcepts");
		logger.fine("Creating structures");
		createConcepts();
		logger.fine("Created structures");
		t.stop();

		final int queueSize = primaryQueue.size();
		monitor.setProgressTitle("Classifiying");
		monitor.setProgressLength(queueSize);
		monitor.taskStarted();

		logger.fine("Processing queue");
		t = timers.startTimer("processQueue");
		processQueue();
		t.stop();
		logger.fine("Processed queue");

		if (logger.isLoggable(Level.FINER))
			print();

		monitor.setProgress(queueSize);

		logger.fine("Building hierarchy");
		t = timers.startTimer("buildHierarchy");

		taxonomy = new ELTaxonomyBuilder().build(concepts);

		t.stop();
		logger.fine("Builded hierarchy");

		monitor.taskFinished();

		return true;
	}

	private void addSuccessor(final ConceptInfo pred, final ATermAppl p, final ConceptInfo succ)
	{
		if (!pred.addSuccessor(p, succ))
			return;

		if (logger.isLoggable(Level.FINER))
			logger.finer("Adding " + pred + " -> " + ATermUtils.toString(p) + " -> " + succ);

		if (succ == BOTTOM)
		{
			addToQueue(pred, BOTTOM);
			return;
		}

		for (final ConceptInfo supOfSucc : succ.getSuperClasses())
			addSuccessor(pred, p, supOfSucc);

		if (!roleChains.isAnon(p))
			if (MATERIALIZE_SUPER_PROPERTIES)
			{
				if (existentials.contains(p, succ))
				{
					final ATermAppl some = ATermUtils.makeSomeValues(p, succ.getConcept());
					addToQueue(pred, concepts.get(some));
				}

				final Set<Role> superRoles = kb.getRole(p).getSuperRoles();
				for (final Role superRole : superRoles)
					addSuccessor(pred, superRole.getName(), succ);
			}
			else
			{
				final Set<Role> superRoles = kb.getRole(p).getSuperRoles();
				for (final Role superRole : superRoles)
					if (existentials.contains(superRole.getName(), succ))
					{
						final ATermAppl some = ATermUtils.makeSomeValues(superRole.getName(), succ.getConcept());
						addToQueue(pred, concepts.get(some));
					}
			}

		if (!PREPROCESS_DOMAINS)
		{
			final ATermAppl propDomain = roleRestrictions.getDomain(p);
			if (propDomain != null)
				addToQueue(pred, concepts.get(propDomain));
		}

		if (hasComplexRoles)
		{
			for (final Entry<ATermAppl, Set<ConceptInfo>> entry : CollectionUtils.makeList(pred.getPredecessors().entrySet()))
			{
				final ATermAppl predProp = entry.getKey();
				for (final ATermAppl supProp : roleChains.getAllSuperRoles(predProp, p))
					for (final ConceptInfo predOfPred : CollectionUtils.makeList(entry.getValue()))
						addSuccessor(predOfPred, supProp, succ);
			}

			for (final Entry<ATermAppl, Set<ConceptInfo>> entry : CollectionUtils.makeList(succ.getSuccessors().entrySet()))
			{
				final ATermAppl succProp = entry.getKey();
				for (final ATermAppl supProp : roleChains.getAllSuperRoles(p, succProp))
					for (final ConceptInfo succOfSucc : CollectionUtils.makeList(entry.getValue()))
						addSuccessor(pred, supProp, succOfSucc);
			}
		}
	}

	private void addToQueue(final ConceptInfo sub, final ConceptInfo sup)
	{
		if (sub.addSuperClass(sup))
		{
			primaryQueue.add(new QueueElement(sub, sup));
			if (logger.isLoggable(Level.FINER))
				logger.finer("Queue " + sub + " " + sup);
		}
	}

	private void addSuperClass(final ConceptInfo sub, final ConceptInfo sup)
	{
		if (logger.isLoggable(Level.FINER))
			logger.finer("Adding " + sub + " < " + sup);

		if (sup == BOTTOM)
		{
			final Iterator<ConceptInfo> preds = sub.getPredecessors().flattenedValues();
			while (preds.hasNext())
				addToQueue(preds.next(), sup);
			return;
		}

		for (final ConceptInfo supOfSup : sup.getSuperClasses())
			if (!supOfSup.equals(sup))
				addToQueue(sub, supOfSup);

		final ATermAppl c = sup.getConcept();
		if (ATermUtils.isAnd(c))
		{
			ATermList list = (ATermList) c.getArgument(0);
			while (!list.isEmpty())
			{
				final ATermAppl conj = (ATermAppl) list.getFirst();

				addToQueue(sub, concepts.get(conj));

				list = list.getNext();
			}
		}
		else
			if (ATermUtils.isSomeValues(c))
			{
				final ATermAppl p = (ATermAppl) c.getArgument(0);
				final ATermAppl qualification = (ATermAppl) c.getArgument(1);

				addSuccessor(sub, p, concepts.get(qualification));
			}
			else
				assert ATermUtils.isPrimitive(c);

		final Set<ConceptInfo> referredConjunctions = conjunctions.get(sup);
		if (referredConjunctions != null)
			for (final ConceptInfo conjunction : referredConjunctions)
			{
				ATermList list = (ATermList) conjunction.getConcept().getArgument(0);
				while (!list.isEmpty())
				{
					final ATermAppl conj = (ATermAppl) list.getFirst();

					if (!sub.hasSuperClass(concepts.get(conj)))
						break;

					list = list.getNext();
				}

				if (list.isEmpty())
					addToQueue(sub, conjunction);
			}

		for (final Entry<ATermAppl, Set<ConceptInfo>> e : sub.getPredecessors().entrySet())
		{
			final ATermAppl prop = e.getKey();
			if (MATERIALIZE_SUPER_PROPERTIES)
			{
				if (existentials.contains(prop, sup))
				{
					final ATermAppl some = ATermUtils.makeSomeValues(prop, c);
					for (final ConceptInfo pred : e.getValue())
						addToQueue(pred, concepts.get(some));
				}
			}
			else
			{
				final Role role = kb.getRole(prop);
				if (role != null)
				{
					final Set<Role> superRoles = role.getSuperRoles();
					for (final Role superRole : superRoles)
						if (existentials.contains(superRole.getName(), sup))
						{
							final ATermAppl some = ATermUtils.makeSomeValues(superRole.getName(), c);
							for (final ConceptInfo pred : e.getValue())
								addToQueue(pred, concepts.get(some));
						}
				}
			}
		}
	}

	private ConceptInfo createConcept(ATermAppl c)
	{
		ConceptInfo concept = concepts.get(c);
		if (concept == null)
		{
			concept = new ConceptInfo(c, hasComplexRoles, false);

			if (ATermUtils.isAnd(c))
			{
				ATermList list = (ATermList) c.getArgument(0);
				for (; !list.isEmpty(); list = list.getNext())
				{
					final ATermAppl conj = (ATermAppl) list.getFirst();

					final ConceptInfo conjConcept = createConcept(conj);
					addToQueue(concept, conjConcept);

					conjunctions.add(conjConcept, concept);
				}
			}
			else
				if (ATermUtils.isSomeValues(c))
				{
					final ATermAppl p = (ATermAppl) c.getArgument(0);
					final ATermAppl q = (ATermAppl) c.getArgument(1);

					if (ATermUtils.isInv(p))
						throw new UnsupportedOperationException("Anonmyous inverse found in restriction: " + ATermUtils.toString(c));

					final ATermAppl range = roleRestrictions.getRange(p);
					if (range != null)
					{
						final ATermAppl newQ = ATermUtils.makeSimplifiedAnd(Arrays.asList(range, q));
						if (!newQ.equals(q))
						{
							final ATermAppl newC = ATermUtils.makeSomeValues(p, newQ);
							concept = createConcept(newC);
							concepts.put(c, concept);
							c = newC;
						}
					}

					final ConceptInfo succ = createConcept(q);

					existentials.add(p, succ);

					// Add this to the queue so that successor relation some(p,q) -> p -> q will be established. Due to
					// sub property interactions adding successor relation here directly causes missing inferences.
					// Note that, we are taking advantage of the fact that concept.addSuperClass(concept) call has not
					// been executed yet which would have caused the add queue call to have no effect.
					addToQueue(concept, concept);
				}

			concepts.put(c, concept);

			concept.addSuperClass(concept);

			if (TOP != null)
				addToQueue(concept, TOP);
		}

		return concept;
	}

	private void createConceptsFromAxiom(final ATermAppl sub, final ATermAppl sup)
	{
		addToQueue(createConcept(sub), createConcept(sup));
	}

	private void createDisjointAxiom(final ATermAppl c1, final ATermAppl c2)
	{
		createConcept(c1);
		createConcept(c2);

		final ATermAppl and = ATermUtils.makeSimplifiedAnd(Arrays.asList(c1, c2));
		createConceptsFromAxiom(and, ATermUtils.BOTTOM);
	}

	private void processAxiom(final ATermAppl axiom)
	{
		final AFun fun = axiom.getAFun();

		if (fun.equals(ATermUtils.DISJOINTSFUN))
		{
			ATermList concepts = (ATermList) axiom.getArgument(0);
			final int n = concepts.getLength();
			final ATermAppl[] simplified = new ATermAppl[n];
			for (int i = 0; !concepts.isEmpty(); concepts = concepts.getNext(), i++)
				simplified[i] = ELSyntaxUtils.simplify((ATermAppl) concepts.getFirst());
			for (int i = 0; i < n - 1; i++)
				for (int j = i + 1; j < n; j++)
					createDisjointAxiom(simplified[i], simplified[j]);
		}
		else
		{
			ATermAppl sub = (ATermAppl) axiom.getArgument(0);
			ATermAppl sup = (ATermAppl) axiom.getArgument(1);

			sub = ELSyntaxUtils.simplify(sub);
			sup = ELSyntaxUtils.simplify(sup);

			if (fun.equals(ATermUtils.SUBFUN))
				createConceptsFromAxiom(sub, sup);
			else
				if (fun.equals(ATermUtils.EQCLASSFUN))
				{
					createConceptsFromAxiom(sub, sup);
					createConceptsFromAxiom(sup, sub);
				}
				else
					if (fun.equals(ATermUtils.DISJOINTFUN))
						createDisjointAxiom(sub, sup);
					else
						throw new IllegalArgumentException("Axiom " + axiom + " is not EL.");
		}
	}

	private void processAxioms()
	{
		//EquivalentClass -> SubClasses
		//Disjoint Classes -> SubClass
		//Normalize ATerm lists to sets
		final Collection<ATermAppl> assertedAxioms = kb.getTBox().getAssertedAxioms();
		for (final ATermAppl assertedAxiom : assertedAxioms)
			processAxiom(assertedAxiom);

		if (PREPROCESS_DOMAINS)
			//Convert ATermAppl Domains to axioms
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
		TOP = createConcept(ATermUtils.TOP);
		BOTTOM = createConcept(ATermUtils.BOTTOM);

		for (final ATermAppl c : kb.getClasses())
			createConcept(c);

		processAxioms();

		logger.fine("Process domain and ranges");
		for (final ATermAppl c : roleRestrictions.getRanges().values())
			createConcept(c);

		for (final ATermAppl c : roleRestrictions.getDomains().values())
			createConcept(c);
	}

	public void print()
	{
		for (final ATermAppl c : concepts.keySet())
			log.finer(c + " " + concepts.get(c).getSuperClasses());
		log.finer("");
		roleChains.print();
	}

	private void processQueue()
	{
		final int startingSize = primaryQueue.size();
		while (!primaryQueue.isEmpty())
		{
			final int processed = startingSize - primaryQueue.size();
			if (monitor.getProgress() < processed)
				monitor.setProgress(processed);

			final QueueElement qe = primaryQueue.remove();
			addSuperClass(qe.sub, qe.sup);
		}
	}

	@Override
	public Map<ATermAppl, Set<ATermAppl>> getToldDisjoints()
	{
		return Collections.emptyMap();
	}

	@Override
	public Taxonomy<ATermAppl> getToldTaxonomy()
	{
		return new Taxonomy<>(kb.getTBox().getClasses(), TermFactory.TOP, TermFactory.BOTTOM);
	}
}
