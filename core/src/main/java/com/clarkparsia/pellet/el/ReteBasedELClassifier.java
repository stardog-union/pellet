// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.rules.rete.Fact;
import com.clarkparsia.pellet.rules.rete.Interpreter;
import com.clarkparsia.pellet.rules.rete.Rule;
import com.clarkparsia.pellet.rules.rete.TermTuple;
import com.clarkparsia.pellet.utils.CollectionUtils;

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
public class ReteBasedELClassifier extends RuleBasedELClassifier implements
		TaxonomyBuilder {
	// private static final String PREDICATE_PREFIX =
	// "tag:clarkparsia.com,2008:pellet:el:predicate:";
	private static final String PREDICATE_PREFIX = "";
	private static final ATermAppl PRED_SUB = ATermUtils
			.makeTermAppl(PREDICATE_PREFIX + "subclassOf");
	private static final ATermAppl PRED_SUB_SOME = ATermUtils
			.makeTermAppl(PREDICATE_PREFIX + "subclassOfSomeOf");

	private static final ATermAppl TOP = ATermUtils.TOP;
	private static final ATermAppl BOTTOM = ATermUtils.BOTTOM;

	private ConstantStore m_Names;
	private VariableStore m_Variables;
	private List<Rule> m_Rules;
	private List<Fact> m_Facts;

	public ReteBasedELClassifier() {
		m_Names = new ConstantStore();
		m_Variables = new VariableStore();
		m_Rules = CollectionUtils.makeList();
		m_Facts = CollectionUtils.makeList();

		makeRuleAxioms();
	}

	protected MultiValueMap<ATermAppl, ATermAppl> run(
			Collection<ATermAppl> classes) {
		KnowledgeBase reteKB = new KnowledgeBase();
		Interpreter interpreter = new Interpreter(reteKB.getABox());

		for( Rule rule : m_Rules ) {
			interpreter.rete.compile(rule, null);	
		}		

		addClasses(interpreter, classes);
		addClasses(interpreter, m_Names.getAllAnons());
		for (Fact fact : m_Facts) {
			interpreter.addFact(fact);
		}
		m_Facts = null;
		m_Names.reset();

		Set<Fact> facts = interpreter.run();

		MultiValueMap<ATermAppl, ATermAppl> subsumers = getSubsumers(facts);
		for (ATermAppl c : classes) {
			subsumers.add(c, c);
			subsumers.add(c, ATermUtils.TOP);
		}

		return subsumers;
	}

	/**
	 * {@inheritDoc}
	 */
	protected MultiValueMap<ATermAppl, ATermAppl> getSubsumers(Set<Fact> facts) {
		MultiValueMap<ATermAppl, ATermAppl> subsumers = new MultiValueMap<ATermAppl, ATermAppl>();

		for (Fact f : facts) {
			if (f.getElements().get(0).equals(PRED_SUB)) {
				List<ATermAppl> tuple = f.getElements();
				ATermAppl c1 = tuple.get(1);
				ATermAppl c2 = tuple.get(2);
				if (ConstantStore.isAnon(c1) || ConstantStore.isAnon(c2))
					continue;

				subsumers.add(c1, c2);
			}
		}

		return subsumers;
	}

	protected void addClasses(Interpreter interpreter,
			Collection<ATermAppl> classes) {
		for (ATermAppl c : classes) {
			interpreter.addFact(makeSubclassFact(c, c));
			interpreter.addFact(makeSubclassFact(c, TOP));
		}
	}

	protected void addSubclassRule(ATermAppl sub, ATermAppl sup) {
		addSubclassRule(sub, sup, new FreeVariableStore());
	}

	protected void addRoleDomainRule(ATermAppl p, ATermAppl domain) {
		List<TermTuple> body = CollectionUtils.makeList();
		List<TermTuple> head = CollectionUtils.makeList();

		FreeVariableStore freeVar = new FreeVariableStore();
		ATermAppl var0 = freeVar.next();
		ATermAppl var1 = freeVar.next();
		body.add(makeSubOfSomeTuple(var0, p, var1));
		translateSuper(head, domain, freeVar, var0);

		m_Rules.add(new Rule(body, head));
	}

	protected void addRoleRangeRule(ATermAppl p, ATermAppl range) {
		List<TermTuple> body = CollectionUtils.makeList();
		List<TermTuple> head = CollectionUtils.makeList();

		FreeVariableStore freeVar = new FreeVariableStore();
		ATermAppl var0 = freeVar.next();
		ATermAppl var1 = freeVar.next();
		body.add(makeSubOfSomeTuple(var0, p, var1));
		ATermAppl someOfRange = ATermUtils.makeSomeValues(p, range);
		translateSuper(head, someOfRange, freeVar, var0);

		m_Rules.add(new Rule(body, head));
	}

	protected void addRoleChainRule(ATerm[] chain, ATermAppl sup) {
		if (chain.length < 1)
			return;

		List<TermTuple> body = CollectionUtils.makeList();

		FreeVariableStore freeVar = new FreeVariableStore();
		ATermAppl var[] = new ATermAppl[chain.length + 1];
		var[0] = freeVar.next();
		for (int i = 0; i < chain.length; i++) {
			var[i + 1] = freeVar.next();
			body.add(makeSubOfSomeTuple(var[i], (ATermAppl) chain[i],
					var[i + 1]));
		}

		TermTuple head = makeSubOfSomeTuple(var[0], sup, var[var.length - 1]);

		m_Rules.add(new Rule(body, Collections.singletonList(head)));
	}

	protected void addRoleHierarchyRule(ATermAppl sub, ATermAppl sup) {
		addRoleChainRule(new ATerm[] { sub }, sup);
	}

	private void makeRuleAxioms() {
		makeBottomAxiom();
	}

	private void makeBottomAxiom() {
		FreeVariableStore freeVar = new FreeVariableStore();
		ATermAppl var0 = freeVar.next();
		ATermAppl var1 = freeVar.next();
		ATermAppl var2 = freeVar.next();
		List<TermTuple> body = CollectionUtils.makeList();
		body.add(makeSubOfSomeTuple(var0, var1, var2));
		body.add(makeSubclassTuple(var2, BOTTOM));
		TermTuple head = makeSubclassTuple(var0, BOTTOM);

		m_Rules.add(new Rule(body, Collections.singletonList(head)));
	}

	private void addSubclassRule(ATermAppl sub, ATermAppl sup,
			FreeVariableStore freeVar) {
		List<TermTuple> body = CollectionUtils.makeList();
		List<TermTuple> head = CollectionUtils.makeList();

		ATermAppl var = freeVar.next();
		translateSub(body, sub, freeVar, var);
		translateSuper(head, sup, freeVar, var);
		m_Rules.add(new Rule(body, head));
	}

	private void translateSub(List<TermTuple> outBody, ATermAppl sub,
			FreeVariableStore freeVar, ATermAppl currentVar) {
		AFun fun = sub.getAFun();
		if (ATermUtils.isPrimitive(sub) || ATermUtils.isBottom(sub)) {
			outBody.add(makeSubclassTuple(currentVar, sub));
		} else if (fun.equals(ATermUtils.ANDFUN)) {
			ATermList list = (ATermList) sub.getArgument(0);

			while (!list.isEmpty()) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				translateSub(outBody, conj, freeVar, currentVar);
				list = list.getNext();
			}
		} else if (fun.equals(ATermUtils.SOMEFUN)) {
			ATermAppl prop = (ATermAppl) sub.getArgument(0);
			ATermAppl q = (ATermAppl) sub.getArgument(1);
			ATermAppl nextVar = freeVar.next();
			outBody.add(makeSubOfSomeTuple(currentVar, prop, nextVar));
			translateSub(outBody, q, freeVar, nextVar);
		} else {
			assert false;
		}
	}

	private void translateSuper(List<TermTuple> outHead, ATermAppl sup,
			FreeVariableStore freeVar, ATermAppl currentVar) {
		AFun fun = sup.getAFun();
		if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup)) {
			outHead.add(makeSubclassTuple(currentVar, sup));
		} else if (fun.equals(ATermUtils.ANDFUN)) {
			ATermList list = (ATermList) sup.getArgument(0);

			while (!list.isEmpty()) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				translateSuper(outHead, conj, freeVar, currentVar);
				list = list.getNext();
			}
		} else if (fun.equals(ATermUtils.SOMEFUN)) {
			ATermAppl prop = (ATermAppl) sup.getArgument(0);
			ATermAppl q = (ATermAppl) sup.getArgument(1);

			if (!ATermUtils.isPrimitive(q) && !ATermUtils.isBottom(q)) {
				// Normalization - breaking complex concepts within someValues
				ATermAppl anon = m_Names.getNextAnon();
				// addSubclassRule( anon, q );
				translateSuperSome(anon, q);
				q = anon;
			}

			outHead.add(makeSubOfSomeTuple(currentVar, prop, q));
		} else {
			assert false;
		}
	}

	private void translateSuperSome(ATermAppl anon, ATermAppl sup) {
		AFun fun = sup.getAFun();
		if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup)) {
			m_Facts.add(makeSubclassFact(anon, sup));
		} else if (fun.equals(ATermUtils.ANDFUN)) {
			ATermList list = (ATermList) sup.getArgument(0);

			while (!list.isEmpty()) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				translateSuperSome(anon, conj);
				list = list.getNext();
			}
		} else if (fun.equals(ATermUtils.SOMEFUN)) {
			ATermAppl prop = (ATermAppl) sup.getArgument(0);
			ATermAppl q = (ATermAppl) sup.getArgument(1);

			if (!ATermUtils.isPrimitive(q) && !ATermUtils.isBottom(q)) {
				// Normalization - breaking complex concepts within someValues
				ATermAppl nextAnon = m_Names.getNextAnon();
				translateSuperSome(nextAnon, q);
				q = nextAnon;
			}

			m_Facts.add(makeSubOfSomeFact(anon, prop, q));
		} else {
			assert false;
		}
	}

	private Fact makeSubclassFact(ATermAppl t1, ATermAppl t2) {
		return new Fact(DependencySet.INDEPENDENT, PRED_SUB, t1, t2);
	}

//	private Fact makeSubclassFact(Constant t1, Constant t2) {
//		return new Fact(DependencySet.INDEPENDENT, PRED_SUB, t1, t2);
//	}

	private Fact makeSubOfSomeFact(ATermAppl t1, ATermAppl t2, ATermAppl t3) {
		return new Fact(DependencySet.INDEPENDENT, PRED_SUB_SOME, t1, t2, t3);
	}

	private TermTuple makeSubclassTuple(ATermAppl t1, ATermAppl t2) {
		return new TermTuple(DependencySet.INDEPENDENT, PRED_SUB, t1, t2);
	}

	// private TermTuple makeSubOfSomeTuple(Term t1, ATermAppl p, Term t2) {
	// return makeSubOfSomeTuple( t1, m_Names.get( p ), t2 );
	// }

	private TermTuple makeSubOfSomeTuple(ATermAppl t1, ATermAppl p, ATermAppl t2) {
		return new TermTuple(DependencySet.INDEPENDENT, PRED_SUB_SOME, t1, p,
				t2);
	}

	static class ConstantStore {
		private static final String ANON = "tag:clarkparsia.com,2008:pellet:el:anon:";
		private static final int FIRST_ANON = 0;

		private Map<ATermAppl, ATermAppl> m_Constants = CollectionUtils
				.makeMap();
		private int m_NextAnon = FIRST_ANON;

		public void reset() {
			m_Constants = CollectionUtils.makeMap();
			m_NextAnon = FIRST_ANON;
		}

		protected ATermAppl get(ATermAppl term) {
			ATermAppl c = m_Constants.get(term);
			if (c == null) {
				c = term;
				m_Constants.put(term, c);
			}
		 
			return c;
		 }

		protected ATermAppl getNextAnon() {
			return makeAnon(m_NextAnon++);
		}

		protected Set<ATermAppl> getAllAnons() {
			Set<ATermAppl> anons = CollectionUtils.makeSet();
			for (int i = FIRST_ANON; i < m_NextAnon; i++) {
				anons.add(makeAnon(i));
			}
			return anons;
		}

		protected static boolean isAnon(ATermAppl c) {
			return c.getName().startsWith(ANON);
		}

		private static ATermAppl makeAnon(int id) {
			return ATermUtils.makeTermAppl(ANON + id);
		}
	}

	static class VariableStore {
		private static final String PREFIX = "x";

		private List<ATermAppl> m_Variables = CollectionUtils.makeList();

		protected ATermAppl get(int target) {
			for (int size = m_Variables.size(); size <= target; size++)
				m_Variables.add(ATermUtils.makeVar(PREFIX + size));

			return m_Variables.get(target);
		}
	}

	class FreeVariableStore {
		private int m_Next = 0;

		protected ATermAppl next() {
			return m_Variables.get(m_Next++);
		}
	}
}
