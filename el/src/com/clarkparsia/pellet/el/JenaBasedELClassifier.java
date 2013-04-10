// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.CollectionUtils;
import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.reasoner.InfGraph;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.ClauseEntry;
import com.hp.hpl.jena.reasoner.rulesys.Functor;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Node_RuleVariable;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.builtins.NotEqual;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

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
public class JenaBasedELClassifier extends RuleBasedELClassifier implements TaxonomyBuilder {	
	private static final String PREDICATE_PREFIX = "tag:clarkparsia.com,2008:pellet:el:predicate:";
	private static final Node PRED_SUB = Node.createURI(PREDICATE_PREFIX + "subclassOf");
	private static final Builtin NOT_EQUAL = new NotEqual();
	
	private final Node TOP;
	private final Node BOTTOM;
	
	private NameStore m_Names;
	private VariableStore m_Variables;
	private Set<Rule> m_Rules;
	private Graph m_Facts;
	
	public JenaBasedELClassifier() {
		m_Names = new NameStore();
		m_Variables = new VariableStore();
		m_Rules = CollectionUtils.makeSet();
		m_Facts =  Factory.createGraphMem( ReificationStyle.Standard );
		
		TOP = m_Names.get(ATermUtils.TOP);
		BOTTOM = m_Names.get(ATermUtils.BOTTOM);
		makeRuleAxioms();
	}
	
	protected void addClasses(Collection<ATermAppl> classes) {
		for (ATermAppl c : classes) {
			Node n = m_Names.get(c);
			m_Facts.add(Triple.create(n, PRED_SUB, n));
			m_Facts.add(Triple.create(n, PRED_SUB, TOP));
		}
	}
	
	protected MultiValueMap<ATermAppl, ATermAppl> run(Collection<ATermAppl> classes) {
		addClasses( classes );
		addClasses( m_Names.getAllAnons() );
		
		Reasoner reasoner = new GenericRuleReasoner(new ArrayList<Rule>(m_Rules));
		
		InfGraph inf = reasoner.bind( m_Facts );
		inf.prepare();
		
		MultiValueMap<ATermAppl, ATermAppl> subsumers = getSubsumptions(inf);
		for( ATermAppl c : classes ) {
			subsumers.add( ATermUtils.BOTTOM, c );			
		}
		return subsumers;
	}
	
	protected MultiValueMap<ATermAppl, ATermAppl> getSubsumptions(Graph graph) {
		MultiValueMap<ATermAppl, ATermAppl> subsumers = new MultiValueMap<ATermAppl, ATermAppl>();
		ExtendedIterator it = graph.find(Node.ANY, PRED_SUB, Node.ANY);
		while (it.hasNext()) {
			Triple tri = (Triple) it.next();
			Node sub = tri.getSubject();
			Node sup = tri.getObject();
			if (NameStore.isAnon(sub) || NameStore.isAnon(sup)) continue;
			
			subsumers.add(toATermAppl(sub), toATermAppl(sup));
		}
		it.close();
		
		return subsumers;
	}
	
	private ATermAppl toATermAppl(Node n) {
		if (TOP.hasURI(n.getURI())) {
			return ATermUtils.TOP;
		} else if (BOTTOM.hasURI(n.getURI())) {
			return ATermUtils.BOTTOM;
		} else {
			return ATermUtils.makeTermAppl(n.getURI());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void addSubclassRule(ATermAppl sub, ATermAppl sup) {
		addSubclassRule(sub, sup, new FreeVariableStore());
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void addRoleDomainRule(ATermAppl p, ATermAppl domain) {
		List<ClauseEntry> body = CollectionUtils.makeList();
		List<ClauseEntry> head = CollectionUtils.makeList();
		
		FreeVariableStore freeVar = new FreeVariableStore();
		Node var0 = freeVar.next();
		Node var1 = freeVar.next();
		body.add(makeSubOfSomeTriple(var0, p, var1));
		translateSuper(head, domain, freeVar, var0);
		
		m_Rules.add(new Rule(head, body));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void addRoleRangeRule(ATermAppl p, ATermAppl range) {
		List<ClauseEntry> body = CollectionUtils.makeList();
		List<ClauseEntry> head = CollectionUtils.makeList();
		
		FreeVariableStore freeVar = new FreeVariableStore();
		Node var0 = freeVar.next();
		Node var1 = freeVar.next();
		body.add(makeSubOfSomeTriple(var0, p, var1));
		ATermAppl someOfRange = ATermUtils.makeSomeValues(p, range);
		translateSuper(head, someOfRange, freeVar, var0);
		
		m_Rules.add(new Rule(head, body));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void addRoleChainRule(ATerm[] chain, ATermAppl sup) {
		if (chain.length < 1) return;
		
		List<ClauseEntry> body = CollectionUtils.makeList();
		
		FreeVariableStore freeVar = new FreeVariableStore();
		Node var[] = new Node[chain.length + 1];
		var[0] = freeVar.next();
		for (int i = 0; i < chain.length; i++) {
			var[i + 1] = freeVar.next();
			body.add(makeSubOfSomeTriple(var[i], (ATermAppl) chain[i], var[i + 1]));
		}
		
		ClauseEntry head = makeSubOfSomeTriple(var[0], sup, var[var.length - 1]);
		
		m_Rules.add(new Rule(Collections.singletonList(head), body));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void addRoleHierarchyRule(ATermAppl sub, ATermAppl sup) {
		addRoleChainRule(new ATerm[] {sub}, sup);
	}

	
	private void makeRuleAxioms() {
		makeBottomAxiom();
	}

	private void makeBottomAxiom() {
		FreeVariableStore freeVar = new FreeVariableStore();
		Node var0 = freeVar.next();
		Node var1 = freeVar.next();
		Node var2 = freeVar.next();
		List<ClauseEntry> body = CollectionUtils.makeList();
		body.add(makeSubOfSomeTriple(var0, var1, var2));
		body.add(makePropertyAssertionFunctor(var1));
		body.add(makeSubclassTriple(var2, BOTTOM));
		ClauseEntry head = makeSubclassTriple(var0, BOTTOM);
		
		m_Rules.add(new Rule(Collections.singletonList(head), body));
	}

	private void addSubclassRule(ATermAppl sub, ATermAppl sup, FreeVariableStore freeVar) {
		List<ClauseEntry> body = CollectionUtils.makeList();
		List<ClauseEntry> head = CollectionUtils.makeList();
		
		Node var = freeVar.next();
		translateSub(body, sub, freeVar, var);
		translateSuper(head, sup, freeVar, var);
		m_Rules.add(new Rule(head, body));
	}

	private void translateSub(List<ClauseEntry> outBody, ATermAppl sub, FreeVariableStore freeVar, Node currentVar) {
		AFun fun = sub.getAFun();
		if (ATermUtils.isPrimitive(sub) || ATermUtils.isBottom(sub)) {
			outBody.add(makeSubclassTriple(currentVar, m_Names.get(sub)));
		} else if (fun.equals( ATermUtils.ANDFUN )) {
			ATermList list = (ATermList) sub.getArgument(0);
			
			while( !list.isEmpty() ) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				translateSub(outBody, conj, freeVar, currentVar);
				list = list.getNext();
			}
		} else if (fun.equals( ATermUtils.SOMEFUN )) {
			ATermAppl prop = (ATermAppl) sub.getArgument(0);
			ATermAppl q = (ATermAppl) sub.getArgument(1);
			Node nextVar = freeVar.next();
			outBody.add(makeSubOfSomeTriple(currentVar, prop, nextVar));
			translateSub(outBody, q, freeVar, nextVar);
		} else {
			assert false;
		}
	}
	
	private void translateSuper(List<ClauseEntry> outHead, ATermAppl sup, FreeVariableStore freeVar, Node currentVar) {
		AFun fun = sup.getAFun();
		if (ATermUtils.isPrimitive(sup) || ATermUtils.isBottom(sup)) {
			outHead.add(makeSubclassTriple(currentVar, m_Names.get(sup)));
		} else if (fun.equals( ATermUtils.ANDFUN )) {
			ATermList list = (ATermList) sup.getArgument(0);
			
			while( !list.isEmpty() ) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				translateSuper(outHead, conj, freeVar, currentVar);
				list = list.getNext();
			}
		} else if (fun.equals( ATermUtils.SOMEFUN )) {
			ATermAppl prop = (ATermAppl) sup.getArgument(0);
			ATermAppl q = (ATermAppl) sup.getArgument(1);
			
			if (!ATermUtils.isPrimitive(q) && !ATermUtils.isBottom(q)) {
				//Normalization - breaking complex concepts within someValues
				ATermAppl anon = m_Names.getNextAnon();
//				addSubclassRule(anon, q);
				translateSuperSome( anon, q );
				q = anon;
			}
			
			outHead.add(makeSubOfSomeTriple(currentVar, prop, m_Names.get(q)));
		} else {
			assert false;
		}
	}

	private void translateSuperSome(ATermAppl anon, ATermAppl sup) {
		AFun fun = sup.getAFun();
		if( ATermUtils.isPrimitive( sup ) || ATermUtils.isBottom( sup ) ) {
			m_Facts.add( makeSubclassFact( anon, sup ) );
		}
		else if( fun.equals( ATermUtils.ANDFUN ) ) {
			ATermList list = (ATermList) sup.getArgument( 0 );

			while( !list.isEmpty() ) {
				ATermAppl conj = (ATermAppl) list.getFirst();
				translateSuperSome( anon, conj );
				list = list.getNext();
			}
		}
		else if( fun.equals( ATermUtils.SOMEFUN ) ) {
			ATermAppl prop = (ATermAppl) sup.getArgument( 0 );
			ATermAppl q = (ATermAppl) sup.getArgument( 1 );

			if( !ATermUtils.isPrimitive( q ) && !ATermUtils.isBottom( q ) ) {
				// Normalization - breaking complex concepts within someValues
				ATermAppl nextAnon = m_Names.getNextAnon();
				translateSuperSome( nextAnon, q );
				q = nextAnon;
			}

			m_Facts.add( makeSubOfSomeFact( anon, prop, q ) );
		}
		else {
			assert false;
		}		
	}
	
	private Triple makeSubclassFact(ATermAppl t1, ATermAppl t2) {
		return makeSubclassFact( m_Names.get( t1 ), m_Names.get( t2 ) );
	}
	
	private Triple makeSubclassFact(Node t1, Node t2) {
		return Triple.create(t1, PRED_SUB, t2);
	}
	
	private Triple makeSubOfSomeFact(ATermAppl t1, ATermAppl t2, ATermAppl t3) {
		return Triple.create(m_Names.get( t1 ), m_Names.get( t2 ), m_Names.get( t3 ));
	}
	
	private TriplePattern makeSubclassTriple(Node t1, Node t2) {
		return new TriplePattern(t1, PRED_SUB, t2);
	}
	
	private TriplePattern makeSubOfSomeTriple(Node t1, ATermAppl p, Node t2) {
		return makeSubOfSomeTriple(t1, m_Names.get(p), t2);
	}
	
	private TriplePattern makeSubOfSomeTriple(Node t1, Node p, Node t2) {
		return new TriplePattern(t1, p, t2);
	}
	
	private Functor makePropertyAssertionFunctor(Node p) {
		Functor f = new Functor("isNotSubClass", new Node[] {p, PRED_SUB});
		f.setImplementor(NOT_EQUAL);
		return f;
	}

	
	static class NameStore {
		private static final String ANON = "tag:clarkparsia.com,2008:pellet:el:anon:";
		private static final int FIRST_ANON = 0;
		
		private Map<ATermAppl, Node> m_Constants = CollectionUtils.makeMap();
		private int m_NextAnon = FIRST_ANON;
		
		public Node get(ATermAppl term) {
			Node c = m_Constants.get(term);
			if (c == null) {
				if (term == ATermUtils.BOTTOM) {
					c = Node.createURI("_BOTTOM_");
				} else {
					c = Node.createURI(term.getName());
				}
				m_Constants.put(term, c);
			}
			return c;
		}
		
		public ATermAppl getNextAnon() {
			return makeAnon(m_NextAnon++);
		}
		
		public Set<ATermAppl> getAllAnons() {
			Set<ATermAppl> anons = CollectionUtils.makeSet();
			for (int i = FIRST_ANON; i < m_NextAnon; i++) {
				anons.add(makeAnon(i));
			}
			return anons;
		}
		
		public static boolean isAnon(Node c) {
			return c.getURI().startsWith(ANON);
		}
		
		private static ATermAppl makeAnon(int id) {
			return ATermUtils.makeTermAppl(ANON + id); 
		}
	}
	
	static class VariableStore {
		private static final String PREFIX = "x";
		
		private List<Node> m_Variables = CollectionUtils.makeList();
		
		public Node get(int target) {
			for (int size = m_Variables.size(); size <= target; size++) {
				m_Variables.add(new Node_RuleVariable(PREFIX + size, size));
			}
			return m_Variables.get(target);
		}
	}
	
	class FreeVariableStore {
		private int m_Next = 0;
		
		public Node next() {
			return m_Variables.get(m_Next++);
		}
	}

}
