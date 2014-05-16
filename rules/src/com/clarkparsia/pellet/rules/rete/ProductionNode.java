// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.Set;

import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.ContinuousRulesStrategy;
import com.clarkparsia.pellet.rules.PartialBinding;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.Rule;

/**
 * 
 * @author Evren Sirin
 */
public abstract class ProductionNode extends BetaNode {
	protected ContinuousRulesStrategy strategy;
	protected Set<ATermAppl> explain;
	protected DependencySet ds;
	
	public ProductionNode(ContinuousRulesStrategy strategy, Set<ATermAppl> explain) {
		this.strategy = strategy;
		this.explain = explain;
	}
	
	@Override
	public void activate(WME wme) {
	    throw new UnsupportedOperationException();
	}
	
	protected void resetDependencySet(Token token) {
		boolean doExplanation = strategy.getABox().doExplanation();
		ds = token.getDepends(doExplanation);
		if (doExplanation) {
			ds = ds.union(explain, doExplanation);
		}
	}
	
	protected Node getNode(NodeProvider provider, Token token) {
		Node node = provider.getNode(null, token);
		if (node.isMerged()) {
			boolean doExplanation = strategy.getABox().doExplanation();
			ds = ds.union(node.getMergeDependency(true), doExplanation);
			node = node.getSame();
		}
		return node;
	}
	
	public void print(String indent) {
		System.out.print(indent);
		System.out.print("  ");
		System.out.println(this);
	}
	
	public static class Inconsistency extends ProductionNode {
		public Inconsistency(ContinuousRulesStrategy strategy, Set<ATermAppl> explain) {
			super(strategy, explain);
		}
		
		@Override
		public void activate(Token token) {
			resetDependencySet(token);
		    strategy.getABox().setClash(Clash.unexplained(null, ds));
		}
		
		@Override
		public String toString() {
			return "Produce[clash]";
		}		
	}
	
	public static class Type extends ProductionNode {
		private final NodeProvider subject;
		private final ATermAppl type; 
		
		public Type(ContinuousRulesStrategy strategy, Set<ATermAppl> explain, 
						NodeProvider subject, ATermAppl type) {
			super(strategy, explain);
			this.explain = explain;
			this.subject = subject;
			this.type = type;
		}
		
		@Override
		public void activate(Token token) {
			resetDependencySet(token);
			Node s = getNode(subject, token);
			if (!s.hasType(type)) {
				strategy.addType(s, type, ds);
			}
		}
		
		@Override
		public String toString() {
			return "Produce[" + ATermUtils.toString(type) + "(" + subject + ")]";
		}		
	}
	
	private static abstract class Binary extends ProductionNode {
		protected final NodeProvider subject;
		protected final NodeProvider object;
		
		public Binary(ContinuousRulesStrategy strategy, Set<ATermAppl> explain, NodeProvider subject, NodeProvider object) {
			super(strategy, explain);
			this.explain = explain;
			this.subject = subject;
			this.object = object;
		}		
	}
	
	public static class Edge extends Binary {
		private final Role role;
		
		public Edge(ContinuousRulesStrategy strategy, Set<ATermAppl> explain, NodeProvider subject, Role role, NodeProvider object) {
			super(strategy, explain, subject, object);		
			this.role = role;
		}		
		
		@Override
		public void activate(Token token) {
			resetDependencySet(token);
			Node s = getNode(subject, token);
			Node o = getNode(object, token);
		    Object edge = strategy.addEdge((Individual) s, role, o, ds);
		    if (log.isLoggable(Level.FINE)) {
				log.fine("Produce edge " + token + " -> " + edge);
		    }
		}
		
		@Override
		public String toString() {
			return "Produce[" + ATermUtils.toString(role.getName()) + "(" + subject + ", " + object + ")]";
		}
	}
	
	public static class SameAs extends Binary {
		public SameAs(ContinuousRulesStrategy strategy, Set<ATermAppl> explain, NodeProvider subject, NodeProvider object) {
			super(strategy, explain, subject, object);		
		}		
		
		@Override
		public void activate(Token token) {
			resetDependencySet(token);
			Node s = getNode(subject, token);
			Node o = getNode(object, token);
		    strategy.mergeTo(s, o, ds);
		}
		
		@Override
		public String toString() {
			return "Produce[SameAs(" + subject + ", " + object + ")]";
		}
	}
	
	public static class DiffFrom extends Binary {
		public DiffFrom(ContinuousRulesStrategy strategy, Set<ATermAppl> explain, NodeProvider subject, NodeProvider object) {
			super(strategy, explain, subject, object);		
		}		
		
		@Override
		public void activate(Token token) {
			resetDependencySet(token);
			Node s = getNode(subject, token);
			Node o = getNode(object, token);
		    strategy.setDifferent(s, o, ds);
		}
		
		@Override
		public String toString() {
			return "Produce[DiffFrom(" + subject + ", " + object + ")]";
		}
	}
	
	public static class ProduceBinding extends ProductionNode {
		private final Rule rule;
		private final Map<AtomVariable, NodeProvider> args;
		
		public ProduceBinding(ContinuousRulesStrategy strategy, Set<ATermAppl> explain, 
						Rule rule, Map<AtomVariable, NodeProvider> args) {
			super(strategy, explain);
			this.rule = rule;
			this.args = args;
		}
		
		@Override
		public void activate(Token token) {
			resetDependencySet(token);
			VariableBinding binding = new VariableBinding(strategy.getABox());
			for (Entry<AtomVariable, NodeProvider> entry : args.entrySet()) {
				AtomObject arg = entry.getKey();
				Node node = getNode(args.get(arg), token);
				if (arg instanceof AtomIVariable) {
					binding.set((AtomIVariable) arg, (Individual) node);
				}
				else {
					binding.set((AtomDVariable) arg, (Literal) node);
				}
            }
			if (log.isLoggable(Level.FINE)) {
				log.fine("Produce binding " + rule + " -> " + binding);
		    }
			strategy.addPartialBinding(new PartialBinding(rule, binding, ds));
		}
		
		@Override
		public String toString() {
			return "Produce[Binding(" + args + ")]";
		}		
	}
}
