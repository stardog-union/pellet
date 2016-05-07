// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import static java.lang.String.format;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.ContinuousRulesStrategy;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.builtins.BuiltIn;
import com.clarkparsia.pellet.rules.builtins.BuiltInRegistry;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.rules.rete.NodeProvider.ConstantNodeProvider;
import com.clarkparsia.pellet.rules.rete.NodeProvider.TokenNodeProvider;
import com.clarkparsia.pellet.rules.rete.NodeProvider.WMENodeProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.SetUtils;

/**
 * <p>
 * Title: Compiler
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
 */
public class Compiler
{
	protected ContinuousRulesStrategy strategy;
	private final ABox abox;
	private final AlphaNetwork alphaNet;

	private final SafetyChecker safetyChecker = new SafetyChecker();

	public Compiler(final ContinuousRulesStrategy strategy)
	{
		this.strategy = strategy;
		this.abox = strategy.getABox();
		alphaNet = new AlphaNetwork(abox);
	}

	public AlphaNetwork getAlphaNet()
	{
		return alphaNet;
	}

	private RuleAtom pickNextAtom(final List<RuleAtom> atoms, final Set<AtomVariable> bound)
	{
		int index = 0;
		if (bound.isEmpty())
			for (int i = 0; i < atoms.size(); i++)
			{
				final RuleAtom atom = atoms.get(i);
				if (safetyChecker.isSafe(atom))
					return atoms.remove(i);
			}
		else
			for (int i = 0; i < atoms.size(); i++)
			{
				final RuleAtom atom = atoms.get(i);
				if (SetUtils.intersects(bound, atom.getAllArguments()))
				{
					index = i;
					if (safetyChecker.isSafe(atom))
						break;
				}
			}

		return atoms.remove(index);
	}

	public void compile(final Rule rule, final Set<ATermAppl> explain)
	{
		final List<RuleAtom> atoms = new ArrayList<>();
		final List<BuiltInCall> builtins = new ArrayList<>();

		for (final RuleAtom atom : rule.getBody())
			if (atom instanceof BuiltInAtom)
				builtins.add(new BuiltInCall(abox, (BuiltInAtom) atom));
			else
				if (atom instanceof DataRangeAtom)
				{
					//				builtins.add(new Pair<RuleAtom, BindingHelper>(atom, new DataRangeBindingHelper(_abox, (DataRangeAtom) atom)));
				}
				else
					atoms.add(atom);

		final Set<AtomVariable> bound = new HashSet<>();
		final List<RuleAtom> processed = new ArrayList<>();

		int lastSafe = -1;
		ReteNode lastSafeBeta = null;

		boolean canReuseBeta = false;

		RuleAtom atom = null;
		ReteNode node = null;
		while (!atoms.isEmpty())
		{
			atom = pickNextAtom(atoms, bound);

			if (!safetyChecker.isSafe(atom))
			{
				lastSafe = processed.size();
				lastSafeBeta = node;
			}

			final AlphaNode alpha = alphaNet.addNode(atom);
			final List<? extends AtomObject> args = atom.getAllArguments();

			final List<FilterCondition> conditions = new ArrayList<>();
			if (!processed.isEmpty())
				for (int i = 0, n = args.size(); i < n; i++)
				{
					final AtomObject arg = args.get(i);
					if (arg instanceof AtomVariable)
					{
						final TokenNodeProvider provider = createNodeProvider((AtomVariable) arg, processed);
						if (provider != null)
							conditions.add(new JoinCondition(new WMENodeProvider(i), provider));
					}
				}

			processed.add(atom);

			bound.addAll(VariableUtils.getVars(atom));

			// any builtin that can be evaluated with current bindings should be handled here
			for (final Iterator<BuiltInCall> i = builtins.iterator(); i.hasNext();)
			{
				final BuiltInCall call = i.next();
				if (bound.containsAll(call.getPrerequisitesVars(bound)))
				{
					final Collection<? extends AtomVariable> bindableVars = call.getBindableVars(bound);
					if (bindableVars.isEmpty() || bound.containsAll(bindableVars))
					{
						conditions.add(call.createCondition(processed));
						i.remove();
					}
				}
			}

			final boolean firstBeta = (node == null);
			BetaNode newBeta = null;

			if (canReuseBeta)
				if (firstBeta || node == null)
				{
					for (final BetaNode existingBeta : alpha.getBetas())
						if (existingBeta.isTop())
						{
							newBeta = existingBeta;
							break;
						}
				}
				else
				{
					final Collection<BetaNode> sharedBetas = SetUtils.intersection(alpha.getBetas(), node.getBetas());
					for (final BetaNode existingBeta : sharedBetas)
						if (existingBeta instanceof BetaMemoryNode)
						{
							final BetaMemoryNode existingBetaMem = (BetaMemoryNode) existingBeta;
							if (existingBetaMem.getConditions().equals(conditions))
							{
								newBeta = existingBeta;
								break;
							}
						}
				}

			if (newBeta == null)
			{
				newBeta = firstBeta ? new BetaTopNode(alpha) : new BetaMemoryNode(alpha, conditions);
				canReuseBeta = false;
			}

			alpha.addChild(newBeta);
			if ((!firstBeta) && node != null)
				node.addChild(newBeta);
			node = newBeta;

			// process builtins at the end since binding builtins may change
			int bindingCount = -1;
			while (!builtins.isEmpty() && bindingCount != bound.size())
			{
				bindingCount = bound.size();
				for (final Iterator<BuiltInCall> i = builtins.iterator(); i.hasNext();)
				{
					final BuiltInCall call = i.next();
					if (bound.containsAll(call.getPrerequisitesVars(bound)))
					{
						// create the beta _node before updating processed atoms
						newBeta = call.createBeta(processed);
						node.addChild(newBeta);
						node = newBeta;
						processed.add(call.atom);
						bound.addAll(call.getBindableVars(bound));
						canReuseBeta = false;
						i.remove();
					}
				}
			}
		}

		if (!builtins.isEmpty())
			throw new UnsupportedOperationException("Builtin using unsafe variables: " + builtins);

		if (lastSafe == 0)
			strategy.addUnsafeRule(rule, explain);
		else
			if (lastSafe > 0)
			{
				final Map<AtomVariable, NodeProvider> args = new HashMap<>();
				for (int i = 0; i < lastSafe; i++)
					for (final AtomObject arg : processed.get(i).getAllArguments())
						if (arg instanceof AtomVariable && !args.containsKey(arg))
							args.put((AtomVariable) arg, createNodeProvider((AtomVariable) arg, processed));
				if (lastSafeBeta != null)
					lastSafeBeta.addChild(new ProductionNode.ProduceBinding(strategy, explain, rule, args));
			}

		if (rule.getHead().isEmpty())
		{
			if (node != null)
				node.addChild(new ProductionNode.Inconsistency(strategy, explain));
		}
		else
		{
			final ProductionNodeCreator creator = new ProductionNodeCreator(processed, explain);
			if (node != null)
				for (final RuleAtom headAtom : rule.getHead())
					node.addChild(creator.create(headAtom));
		}
	}

	private static TokenNodeProvider createNodeProvider(final AtomVariable arg, final List<RuleAtom> processed)
	{
		return (TokenNodeProvider) createNodeProvider(arg, processed, false);
	}

	private static NodeProvider createNodeProvider(final AtomVariable arg, final List<RuleAtom> processed, final boolean lastWME)
	{
		for (int index = 0, n = processed.size(); index < n; index++)
		{
			final RuleAtom sharedAtom = processed.get(index);
			final int indexArg = sharedAtom.getAllArguments().indexOf(arg);
			if (indexArg != -1)
				if (lastWME && index == n - 1)
					return new WMENodeProvider(indexArg);
				else
					return new TokenNodeProvider(index, indexArg);
		}

		return null;
	}

	private class SafetyChecker implements RuleAtomVisitor
	{

		private boolean result = false;

		/**
		 * May return true if atom is something that will be added to the ABox during completion.
		 */
		public boolean isSafe(final RuleAtom atom)
		{
			atom.accept(this);
			return result;
		}

		@Override
		public void visit(final BuiltInAtom atom)
		{
			result = true;
		}

		@Override
		public void visit(final ClassAtom atom)
		{
			final ATermAppl c = atom.getPredicate();
			result = abox.getKB().getTBox().isPrimitive(c);
		}

		@Override
		public void visit(final DataRangeAtom atom)
		{
			result = true;
		}

		@Override
		public void visit(final DatavaluedPropertyAtom atom)
		{
			result = true;
		}

		@Override
		public void visit(final DifferentIndividualsAtom atom)
		{
			result = false;
		}

		@Override
		public void visit(final IndividualPropertyAtom atom)
		{
			result = abox.getRole(atom.getPredicate()).isSimple();
		}

		@Override
		public void visit(final SameIndividualAtom atom)
		{
			result = true;
		}
	}

	private class ProductionNodeCreator implements RuleAtomVisitor
	{
		private final AtomObjectTranslator translator;

		private final Set<ATermAppl> explain;
		private ProductionNode node;

		public ProductionNodeCreator(final List<RuleAtom> processed, final Set<ATermAppl> explain)
		{
			this.translator = new AtomObjectTranslator(abox, processed, false);
			this.explain = explain;
		}

		private ProductionNode create(final RuleAtom atom)
		{
			node = null;
			atom.accept(this);
			if (node == null)
				throw new UnsupportedOperationException("Not supported " + atom);
			return node;
		}

		@Override
		public void visit(final SameIndividualAtom atom)
		{
			final NodeProvider s = translator.translateObject(atom.getArgument1());
			final NodeProvider o = translator.translateObject(atom.getArgument2());
			node = new ProductionNode.SameAs(strategy, explain, s, o);
		}

		@Override
		public void visit(final IndividualPropertyAtom atom)
		{
			final NodeProvider s = translator.translateObject(atom.getArgument1());
			final NodeProvider o = translator.translateObject(atom.getArgument2());
			final Role r = abox.getRole(atom.getPredicate());
			node = new ProductionNode.Edge(strategy, explain, s, r, o);
		}

		@Override
		public void visit(final DifferentIndividualsAtom atom)
		{
			final NodeProvider s = translator.translateObject(atom.getArgument1());
			final NodeProvider o = translator.translateObject(atom.getArgument2());
			node = new ProductionNode.DiffFrom(strategy, explain, s, o);
		}

		@Override
		public void visit(final DatavaluedPropertyAtom atom)
		{
			final NodeProvider s = translator.translateObject(atom.getArgument1());
			final NodeProvider o = translator.translateObject(atom.getArgument2());
			final Role r = abox.getRole(atom.getPredicate());
			node = new ProductionNode.Edge(strategy, explain, s, r, o);
		}

		@Override
		public void visit(final DataRangeAtom atom)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(final ClassAtom atom)
		{
			final NodeProvider s = translator.translateObject(atom.getArgument());
			final ATermAppl type = atom.getPredicate();
			node = new ProductionNode.Type(strategy, explain, s, type);
		}

		@Override
		public void visit(final BuiltInAtom atom)
		{
			// TODO Auto-generated method stub
		}
	}

	private static class AtomObjectTranslator implements AtomObjectVisitor
	{
		private DependencySet dependency = DependencySet.INDEPENDENT;
		private NodeProvider result = null;

		private final ABox abox;
		private final List<RuleAtom> processed;
		private final boolean lastWME;

		public AtomObjectTranslator(final ABox abox, final List<RuleAtom> processed, final boolean lastWME)
		{
			this.abox = abox;
			this.processed = processed;
			this.lastWME = lastWME;
		}

		public DependencySet getDependency()
		{
			return dependency;
		}

		public NodeProvider translateObject(final AtomObject obj)
		{
			return translateObject(obj, false);
		}

		public NodeProvider translateObject(final AtomObject obj, final boolean allowNull)
		{
			dependency = DependencySet.INDEPENDENT;
			obj.accept(this);
			if (result == null && !allowNull)
				throw new UnsupportedOperationException();
			return result;
		}

		@Override
		public void visit(final AtomDConstant constant)
		{
			ATermAppl canonical;
			final ATermAppl literal = constant.getValue();
			try
			{
				canonical = abox.getKB().getDatatypeReasoner().getCanonicalRepresentation(literal);
			}
			catch (final InvalidLiteralException e)
			{
				final String msg = format("Invalid literal (%s) in SWRL data constant: %s", literal, e.getMessage());
				if (PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY)
					canonical = literal;
				else
					throw new InternalReasonerException(msg, e);
			}
			catch (final UnrecognizedDatatypeException e)
			{
				final String msg = format("Unrecognized datatype in literal appearing (%s) in SWRL data constant: %s", literal, e.getMessage());
				throw new InternalReasonerException(msg, e);
			}

			result = new ConstantNodeProvider(abox.addLiteral(canonical));
		}

		@Override
		public void visit(final AtomDVariable variable)
		{
			result = createNodeProvider(variable, processed, lastWME);
		}

		@Override
		public void visit(final AtomIConstant constant)
		{
			abox.copyOnWrite();
			final Individual individual = abox.getIndividual(constant.getValue());
			//			if (individual.isMerged()) {
			//				dependency = individual.getMergeDependency(true);
			//				individual = individual.getSame();
			//			}

			result = new ConstantNodeProvider(individual);
		}

		@Override
		public void visit(final AtomIVariable variable)
		{
			result = createNodeProvider(variable, processed, lastWME);
		}
	}

	private static class BuiltInCall
	{
		private final ABox abox;
		private final BuiltInAtom atom;
		private final BuiltIn builtin;
		private final BindingHelper helper;

		public BuiltInCall(final ABox abox, final BuiltInAtom atom)
		{
			this.abox = abox;
			this.atom = atom;
			builtin = BuiltInRegistry.instance.getBuiltIn(atom.getPredicate());
			helper = builtin.createHelper(atom);
		}

		public BetaBuiltinNode createBeta(final List<RuleAtom> processed)
		{
			return new BetaBuiltinNode(abox, atom.getPredicate(), builtin, createProviders(processed, false));
		}

		public FilterCondition createCondition(final List<RuleAtom> processed)
		{
			return new BuiltInCondition(abox, atom.getPredicate(), builtin, createProviders(processed, true));
		}

		private NodeProvider[] createProviders(final List<RuleAtom> processed, final boolean lastWME)
		{
			final List<AtomDObject> args = atom.getAllArguments();
			final NodeProvider[] providers = new NodeProvider[args.size()];
			final AtomObjectTranslator translator = new AtomObjectTranslator(abox, processed, lastWME);
			for (int i = 0; i < providers.length; i++)
				providers[i] = translator.translateObject(args.get(i), true);

			return providers;
		}

		public Collection<? extends AtomVariable> getPrerequisitesVars(final Collection<AtomVariable> bound)
		{
			return helper.getPrerequisiteVars(bound);
		}

		public Collection<? extends AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
		{
			return helper.getBindableVars(bound);
		}

		@Override
		public String toString()
		{
			return atom.toString();
		}
	}
}
