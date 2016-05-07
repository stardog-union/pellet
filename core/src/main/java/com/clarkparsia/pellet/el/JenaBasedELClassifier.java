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
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.TriplePattern;
import org.apache.jena.reasoner.rulesys.Builtin;
import org.apache.jena.reasoner.rulesys.ClauseEntry;
import org.apache.jena.reasoner.rulesys.Functor;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Node_RuleVariable;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.reasoner.rulesys.builtins.NotEqual;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

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
 * @author Harris Lin
 */
public class JenaBasedELClassifier extends RuleBasedELClassifier
{
	private static final String PREDICATE_PREFIX = "tag:clarkparsia.com,2008:pellet:el:predicate:";
	private static final Node PRED_SUB = NodeFactory.createURI(PREDICATE_PREFIX + "subclassOf");
	private static final Builtin NOT_EQUAL = new NotEqual();

	private final Node TOP;
	private final Node BOTTOM;

	private final NameStore m_Names;
	private final VariableStore m_Variables;
	private final Set<Rule> m_Rules;
	private final Graph m_Facts;

	public JenaBasedELClassifier()
	{
		m_Names = new NameStore();
		m_Variables = new VariableStore();
		m_Rules = CollectionUtils.makeSet();
		m_Facts = GraphFactory.createDefaultGraph();

		TOP = m_Names.get(ATermUtils.TOP);
		BOTTOM = m_Names.get(ATermUtils.BOTTOM);
		makeRuleAxioms();
	}

	protected void addClasses(final Collection<ATermAppl> classes)
	{
		for (final ATermAppl c : classes)
		{
			final Node n = m_Names.get(c);
			m_Facts.add(Triple.create(n, PRED_SUB, n));
			m_Facts.add(Triple.create(n, PRED_SUB, TOP));
		}
	}

	@Override
	protected MultiValueMap<ATermAppl, ATermAppl> run(final Collection<ATermAppl> classes)
	{
		addClasses(classes);
		addClasses(m_Names.getAllAnons());

		final Reasoner reasoner = new GenericRuleReasoner(new ArrayList<>(m_Rules));

		final InfGraph inf = reasoner.bind(m_Facts);
		inf.prepare();

		final MultiValueMap<ATermAppl, ATermAppl> subsumers = getSubsumptions(inf);
		for (final ATermAppl c : classes)
			subsumers.add(ATermUtils.BOTTOM, c);
		return subsumers;
	}

	protected MultiValueMap<ATermAppl, ATermAppl> getSubsumptions(final Graph graph)
	{
		final MultiValueMap<ATermAppl, ATermAppl> subsumers = new MultiValueMap<>();
		final ExtendedIterator it = graph.find(Node.ANY, PRED_SUB, Node.ANY);
		while (it.hasNext())
		{
			final Triple tri = (Triple) it.next();
			final Node sub = tri.getSubject();
			final Node sup = tri.getObject();
			if (NameStore.isAnon(sub) || NameStore.isAnon(sup))
				continue;

			subsumers.add(toATermAppl(sub), toATermAppl(sup));
		}
		it.close();

		return subsumers;
	}

	private ATermAppl toATermAppl(final Node n)
	{
		if (TOP.hasURI(n.getURI()))
			return ATermUtils.TOP;
		else
			if (BOTTOM.hasURI(n.getURI()))
				return ATermUtils.BOTTOM;
			else
				return ATermUtils.makeTermAppl(n.getURI());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSubclassRule(final ATermAppl sub, final ATermAppl sup)
	{
		addSubclassRule(sub, sup, new FreeVariableStore());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addRoleDomainRule(final ATermAppl p, final ATermAppl domain)
	{
		final List<ClauseEntry> body = CollectionUtils.makeList();
		final List<ClauseEntry> head = CollectionUtils.makeList();

		final FreeVariableStore freeVar = new FreeVariableStore();
		final Node var0 = freeVar.next();
		final Node var1 = freeVar.next();
		body.add(makeSubOfSomeTriple(var0, p, var1));
		translateSuper(head, domain, freeVar, var0);

		m_Rules.add(new Rule(head, body));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addRoleRangeRule(final ATermAppl p, final ATermAppl range)
	{
		final List<ClauseEntry> body = CollectionUtils.makeList();
		final List<ClauseEntry> head = CollectionUtils.makeList();

		final FreeVariableStore freeVar = new FreeVariableStore();
		final Node var0 = freeVar.next();
		final Node var1 = freeVar.next();
		body.add(makeSubOfSomeTriple(var0, p, var1));
		final ATermAppl someOfRange = ATermUtils.makeSomeValues(p, range);
		translateSuper(head, someOfRange, freeVar, var0);

		m_Rules.add(new Rule(head, body));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addRoleChainRule(final ATerm[] chain, final ATermAppl sup)
	{
		if (chain.length < 1)
			return;

		final List<ClauseEntry> body = CollectionUtils.makeList();

		final FreeVariableStore freeVar = new FreeVariableStore();
		final Node var[] = new Node[chain.length + 1];
		var[0] = freeVar.next();
		for (int i = 0; i < chain.length; i++)
		{
			var[i + 1] = freeVar.next();
			body.add(makeSubOfSomeTriple(var[i], (ATermAppl) chain[i], var[i + 1]));
		}

		final ClauseEntry head = makeSubOfSomeTriple(var[0], sup, var[var.length - 1]);

		m_Rules.add(new Rule(Collections.singletonList(head), body));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addRoleHierarchyRule(final ATermAppl sub, final ATermAppl sup)
	{
		addRoleChainRule(new ATerm[] { sub }, sup);
	}

	private void makeRuleAxioms()
	{
		makeBottomAxiom();
	}

	private void makeBottomAxiom()
	{
		final FreeVariableStore freeVar = new FreeVariableStore();
		final Node var0 = freeVar.next();
		final Node var1 = freeVar.next();
		final Node var2 = freeVar.next();
		final List<ClauseEntry> body = CollectionUtils.makeList();
		body.add(makeSubOfSomeTriple(var0, var1, var2));
		body.add(makePropertyAssertionFunctor(var1));
		body.add(makeSubclassTriple(var2, BOTTOM));
		final ClauseEntry head = makeSubclassTriple(var0, BOTTOM);

		m_Rules.add(new Rule(Collections.singletonList(head), body));
	}

	private void addSubclassRule(final ATermAppl sub, final ATermAppl sup, final FreeVariableStore freeVar)
	{
		final List<ClauseEntry> body = CollectionUtils.makeList();
		final List<ClauseEntry> head = CollectionUtils.makeList();

		final Node var = freeVar.next();
		translateSub(body, sub, freeVar, var);
		translateSuper(head, sup, freeVar, var);
		m_Rules.add(new Rule(head, body));
	}

	private void translateSub(final List<ClauseEntry> outBody, final ATermAppl sub, final FreeVariableStore freeVar, final Node currentVar)
	{
		final AFun fun = sub.getAFun();
		if (ATermUtils.isPrimitive(sub) || ATermUtils.isBottom(sub))
			outBody.add(makeSubclassTriple(currentVar, m_Names.get(sub)));
		else
			if (fun.equals(ATermUtils.ANDFUN))
			{
				ATermList list = (ATermList) sub.getArgument(0);

				while (!list.isEmpty())
				{
					final ATermAppl conj = (ATermAppl) list.getFirst();
					translateSub(outBody, conj, freeVar, currentVar);
					list = list.getNext();
				}
			}
			else
				if (fun.equals(ATermUtils.SOMEFUN))
				{
					final ATermAppl prop = (ATermAppl) sub.getArgument(0);
					final ATermAppl q = (ATermAppl) sub.getArgument(1);
					final Node nextVar = freeVar.next();
					outBody.add(makeSubOfSomeTriple(currentVar, prop, nextVar));
					translateSub(outBody, q, freeVar, nextVar);
				}
				else
					assert false;
	}

	private void translateSuper(final List<ClauseEntry> outHead, final ATermAppl sup, final FreeVariableStore freeVar, final Node currentVar)
	{
		final AFun fun = sup.getAFun();
		if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup))
			outHead.add(makeSubclassTriple(currentVar, m_Names.get(sup)));
		else
			if (fun.equals(ATermUtils.ANDFUN))
			{
				ATermList list = (ATermList) sup.getArgument(0);

				while (!list.isEmpty())
				{
					final ATermAppl conj = (ATermAppl) list.getFirst();
					translateSuper(outHead, conj, freeVar, currentVar);
					list = list.getNext();
				}
			}
			else
				if (fun.equals(ATermUtils.SOMEFUN))
				{
					final ATermAppl prop = (ATermAppl) sup.getArgument(0);
					ATermAppl q = (ATermAppl) sup.getArgument(1);

					if (!ATermUtils.isPrimitive(q) && !ATermUtils.isBottom(q))
					{
						//Normalization - breaking complex concepts within someValues
						final ATermAppl anon = m_Names.getNextAnon();
						//				addSubclassRule(anon, q);
						translateSuperSome(anon, q);
						q = anon;
					}

					outHead.add(makeSubOfSomeTriple(currentVar, prop, m_Names.get(q)));
				}
				else
					assert false;
	}

	private void translateSuperSome(final ATermAppl anon, final ATermAppl sup)
	{
		final AFun fun = sup.getAFun();
		if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup))
			m_Facts.add(makeSubclassFact(anon, sup));
		else
			if (fun.equals(ATermUtils.ANDFUN))
			{
				ATermList list = (ATermList) sup.getArgument(0);

				while (!list.isEmpty())
				{
					final ATermAppl conj = (ATermAppl) list.getFirst();
					translateSuperSome(anon, conj);
					list = list.getNext();
				}
			}
			else
				if (fun.equals(ATermUtils.SOMEFUN))
				{
					final ATermAppl prop = (ATermAppl) sup.getArgument(0);
					ATermAppl q = (ATermAppl) sup.getArgument(1);

					if (!ATermUtils.isPrimitive(q) && !ATermUtils.isBottom(q))
					{
						// Normalization - breaking complex concepts within someValues
						final ATermAppl nextAnon = m_Names.getNextAnon();
						translateSuperSome(nextAnon, q);
						q = nextAnon;
					}

					m_Facts.add(makeSubOfSomeFact(anon, prop, q));
				}
				else
					assert false;
	}

	private Triple makeSubclassFact(final ATermAppl t1, final ATermAppl t2)
	{
		return makeSubclassFact(m_Names.get(t1), m_Names.get(t2));
	}

	private Triple makeSubclassFact(final Node t1, final Node t2)
	{
		return Triple.create(t1, PRED_SUB, t2);
	}

	private Triple makeSubOfSomeFact(final ATermAppl t1, final ATermAppl t2, final ATermAppl t3)
	{
		return Triple.create(m_Names.get(t1), m_Names.get(t2), m_Names.get(t3));
	}

	private TriplePattern makeSubclassTriple(final Node t1, final Node t2)
	{
		return new TriplePattern(t1, PRED_SUB, t2);
	}

	private TriplePattern makeSubOfSomeTriple(final Node t1, final ATermAppl p, final Node t2)
	{
		return makeSubOfSomeTriple(t1, m_Names.get(p), t2);
	}

	private TriplePattern makeSubOfSomeTriple(final Node t1, final Node p, final Node t2)
	{
		return new TriplePattern(t1, p, t2);
	}

	private Functor makePropertyAssertionFunctor(final Node p)
	{
		final Functor f = new Functor("isNotSubClass", new Node[] { p, PRED_SUB });
		f.setImplementor(NOT_EQUAL);
		return f;
	}

	static class NameStore
	{
		private static final String ANON = "tag:clarkparsia.com,2008:pellet:el:anon:";
		private static final int FIRST_ANON = 0;

		private final Map<ATermAppl, Node> m_Constants = CollectionUtils.makeMap();
		private int m_NextAnon = FIRST_ANON;

		public Node get(final ATermAppl term)
		{
			Node c = m_Constants.get(term);
			if (c == null)
			{
				if (term == ATermUtils.BOTTOM)
					c = NodeFactory.createURI("_BOTTOM_");
				else
					c = NodeFactory.createURI(term.getName());
				m_Constants.put(term, c);
			}
			return c;
		}

		public ATermAppl getNextAnon()
		{
			return makeAnon(m_NextAnon++);
		}

		public Set<ATermAppl> getAllAnons()
		{
			final Set<ATermAppl> anons = CollectionUtils.makeSet();
			for (int i = FIRST_ANON; i < m_NextAnon; i++)
				anons.add(makeAnon(i));
			return anons;
		}

		public static boolean isAnon(final Node c)
		{
			return c.getURI().startsWith(ANON);
		}

		private static ATermAppl makeAnon(final int id)
		{
			return ATermUtils.makeTermAppl(ANON + id);
		}
	}

	static class VariableStore
	{
		private static final String PREFIX = "x";

		private final List<Node> m_Variables = CollectionUtils.makeList();

		public Node get(final int target)
		{
			for (int size = m_Variables.size(); size <= target; size++)
				m_Variables.add(new Node_RuleVariable(PREFIX + size, size));
			return m_Variables.get(target);
		}
	}

	class FreeVariableStore
	{
		private int m_Next = 0;

		public Node next()
		{
			return m_Variables.get(m_Next++);
		}
	}

}
