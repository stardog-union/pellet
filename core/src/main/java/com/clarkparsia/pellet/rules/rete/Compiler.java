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
	protected ContinuousRulesStrategy _strategy;
	private final ABox _abox;
	private final AlphaNetwork _alphaNet;

	private final SafetyChecker _safetyChecker = new SafetyChecker();

	public Compiler(final ContinuousRulesStrategy strategy)
	{
		this._strategy = strategy;
		this._abox = strategy.getABox();
		_alphaNet = new AlphaNetwork(_abox);
	}

	public AlphaNetwork getAlphaNet()
	{
		return _alphaNet;
	}

	private RuleAtom pickNextAtom(final List<RuleAtom> atoms, final Set<AtomVariable> bound)
	{
		int index = 0;
		if (bound.isEmpty())
			for (int i = 0; i < atoms.size(); i++)
			{
				final RuleAtom atom = atoms.get(i);
				if (_safetyChecker.isSafe(atom))
					return atoms.remove(i);
			}
		else
			for (int i = 0; i < atoms.size(); i++)
			{
				final RuleAtom atom = atoms.get(i);
				if (SetUtils.intersects(bound, atom.getAllArguments()))
				{
					index = i;
					if (_safetyChecker.isSafe(atom))
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
				builtins.add(new BuiltInCall(_abox, (BuiltInAtom) atom));
			else
				if (atom instanceof DataRangeAtom)
				{
					//				builtins.add(new Pair<RuleAtom, BindingHelper>(_atom, new DataRangeBindingHelper(_abox, (DataRangeAtom) _atom)));
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

			if (!_safetyChecker.isSafe(atom))
			{
				lastSafe = processed.size();
				lastSafeBeta = node;
			}

			final AlphaNode alpha = _alphaNet.addNode(atom);
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

			// any _builtin that can be evaluated with _current bindings should be handled here
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

			// process builtins at the _end since binding builtins may change
			int bindingCount = -1;
			while (!builtins.isEmpty() && bindingCount != bound.size())
			{
				bindingCount = bound.size();
				for (final Iterator<BuiltInCall> i = builtins.iterator(); i.hasNext();)
				{
					final BuiltInCall call = i.next();
					if (bound.containsAll(call.getPrerequisitesVars(bound)))
					{
						// create the beta _node before updating _processed atoms
						newBeta = call.createBeta(processed);
						node.addChild(newBeta);
						node = newBeta;
						processed.add(call._atom);
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
			_strategy.addUnsafeRule(rule, explain);
		else
			if (lastSafe > 0)
			{
				final Map<AtomVariable, NodeProvider> args = new HashMap<>();
				for (int i = 0; i < lastSafe; i++)
					for (final AtomObject arg : processed.get(i).getAllArguments())
						if (arg instanceof AtomVariable && !args.containsKey(arg))
							args.put((AtomVariable) arg, createNodeProvider((AtomVariable) arg, processed));
				if (lastSafeBeta != null)
					lastSafeBeta.addChild(new ProductionNode.ProduceBinding(_strategy, explain, rule, args));
			}

		if (rule.getHead().isEmpty())
		{
			if (node != null)
				node.addChild(new ProductionNode.Inconsistency(_strategy, explain));
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
		 * May return true if _atom is something that will be added to the ABox during completion.
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
			result = _abox.getKB().getTBox().isPrimitive(c);
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
			result = _abox.getRole(atom.getPredicate()).isSimple();
		}

		@Override
		public void visit(final SameIndividualAtom atom)
		{
			result = true;
		}
	}

	private class ProductionNodeCreator implements RuleAtomVisitor
	{
		private final AtomObjectTranslator _translator;

		private final Set<ATermAppl> _explain;
		private ProductionNode _node;

		public ProductionNodeCreator(final List<RuleAtom> processed, final Set<ATermAppl> explain)
		{
			this._translator = new AtomObjectTranslator(_abox, processed, false);
			this._explain = explain;
		}

		private ProductionNode create(final RuleAtom atom)
		{
			_node = null;
			atom.accept(this);
			if (_node == null)
				throw new UnsupportedOperationException("Not supported " + atom);
			return _node;
		}

		@Override
		public void visit(final SameIndividualAtom atom)
		{
			final NodeProvider s = _translator.translateObject(atom.getArgument1());
			final NodeProvider o = _translator.translateObject(atom.getArgument2());
			_node = new ProductionNode.SameAs(_strategy, _explain, s, o);
		}

		@Override
		public void visit(final IndividualPropertyAtom atom)
		{
			final NodeProvider s = _translator.translateObject(atom.getArgument1());
			final NodeProvider o = _translator.translateObject(atom.getArgument2());
			final Role r = _abox.getRole(atom.getPredicate());
			_node = new ProductionNode.Edge(_strategy, _explain, s, r, o);
		}

		@Override
		public void visit(final DifferentIndividualsAtom atom)
		{
			final NodeProvider s = _translator.translateObject(atom.getArgument1());
			final NodeProvider o = _translator.translateObject(atom.getArgument2());
			_node = new ProductionNode.DiffFrom(_strategy, _explain, s, o);
		}

		@Override
		public void visit(final DatavaluedPropertyAtom atom)
		{
			final NodeProvider s = _translator.translateObject(atom.getArgument1());
			final NodeProvider o = _translator.translateObject(atom.getArgument2());
			final Role r = _abox.getRole(atom.getPredicate());
			_node = new ProductionNode.Edge(_strategy, _explain, s, r, o);
		}

		@Override
		public void visit(final DataRangeAtom atom)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(final ClassAtom atom)
		{
			final NodeProvider s = _translator.translateObject(atom.getArgument());
			final ATermAppl type = atom.getPredicate();
			_node = new ProductionNode.Type(_strategy, _explain, s, type);
		}

		@Override
		public void visit(final BuiltInAtom atom)
		{
			// TODO Auto-generated method stub
		}
	}

	private static class AtomObjectTranslator implements AtomObjectVisitor
	{
		private DependencySet _dependency = DependencySet.INDEPENDENT;
		private NodeProvider _result = null;

		private final ABox _translatorAbox;
		private final List<RuleAtom> _processed;
		private final boolean _lastWME;

		public AtomObjectTranslator(final ABox abox, final List<RuleAtom> processed, final boolean lastWME)
		{
			this._translatorAbox = abox;
			this._processed = processed;
			this._lastWME = lastWME;
		}

		@SuppressWarnings("unused")
		public DependencySet getDependency()
		{
			return _dependency;
		}

		public NodeProvider translateObject(final AtomObject obj)
		{
			return translateObject(obj, false);
		}

		public NodeProvider translateObject(final AtomObject obj, final boolean allowNull)
		{
			_dependency = DependencySet.INDEPENDENT;
			obj.accept(this);
			if (_result == null && !allowNull)
				throw new UnsupportedOperationException();
			return _result;
		}

		@Override
		public void visit(final AtomDConstant constant)
		{
			ATermAppl canonical;
			final ATermAppl literal = constant.getValue();
			try
			{
				canonical = _translatorAbox.getKB().getDatatypeReasoner().getCanonicalRepresentation(literal);
			}
			catch (final InvalidLiteralException e)
			{
				final String msg = format("Invalid literal (%s) in SWRL _data constant: %s", literal, e.getMessage());
				if (PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY)
					canonical = literal;
				else
					throw new InternalReasonerException(msg, e);
			}
			catch (final UnrecognizedDatatypeException e)
			{
				final String msg = format("Unrecognized datatype in literal appearing (%s) in SWRL _data constant: %s", literal, e.getMessage());
				throw new InternalReasonerException(msg, e);
			}

			_result = new ConstantNodeProvider(_translatorAbox.addLiteral(canonical));
		}

		@Override
		public void visit(final AtomDVariable variable)
		{
			_result = createNodeProvider(variable, _processed, _lastWME);
		}

		@Override
		public void visit(final AtomIConstant constant)
		{
			_translatorAbox.copyOnWrite();
			final Individual individual = _translatorAbox.getIndividual(constant.getValue());
			//			if (_individual.isMerged()) {
			//				_dependency = _individual.getMergeDependency(true);
			//				_individual = _individual.getSame();
			//			}

			_result = new ConstantNodeProvider(individual);
		}

		@Override
		public void visit(final AtomIVariable variable)
		{
			_result = createNodeProvider(variable, _processed, _lastWME);
		}
	}

	private static class BuiltInCall
	{
		private final ABox _builtInCallAbox;
		private final BuiltInAtom _atom;
		private final BuiltIn _builtin;
		private final BindingHelper _helper;

		public BuiltInCall(final ABox abox, final BuiltInAtom atom)
		{
			this._builtInCallAbox = abox;
			this._atom = atom;
			_builtin = BuiltInRegistry.instance.getBuiltIn(atom.getPredicate());
			_helper = _builtin.createHelper(atom);
		}

		public BetaBuiltinNode createBeta(final List<RuleAtom> processed)
		{
			return new BetaBuiltinNode(_builtInCallAbox, _atom.getPredicate(), _builtin, createProviders(processed, false));
		}

		public FilterCondition createCondition(final List<RuleAtom> processed)
		{
			return new BuiltInCondition(_builtInCallAbox, _atom.getPredicate(), _builtin, createProviders(processed, true));
		}

		private NodeProvider[] createProviders(final List<RuleAtom> processed, final boolean lastWME)
		{
			final List<AtomDObject> args = _atom.getAllArguments();
			final NodeProvider[] providers = new NodeProvider[args.size()];
			final AtomObjectTranslator translator = new AtomObjectTranslator(_builtInCallAbox, processed, lastWME);
			for (int i = 0; i < providers.length; i++)
				providers[i] = translator.translateObject(args.get(i), true);

			return providers;
		}

		public Collection<? extends AtomVariable> getPrerequisitesVars(final Collection<AtomVariable> bound)
		{
			return _helper.getPrerequisiteVars(bound);
		}

		public Collection<? extends AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
		{
			return _helper.getBindableVars(bound);
		}

		@Override
		public String toString()
		{
			return _atom.toString();
		}
	}
}
