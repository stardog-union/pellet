// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import aterm.ATermList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.mindswap.pellet.utils.Pair;
import org.mindswap.pellet.utils.fsm.State;
import org.mindswap.pellet.utils.fsm.Transition;
import org.mindswap.pellet.utils.fsm.TransitionGraph;

/**
 * @author Evren Sirin
 */
public class FSMBuilder
{
	public static Logger _logger = Log.getLogger(FSMBuilder.class);

	private final RBox _rbox;

	public FSMBuilder(final RBox rbox)
	{
		this._rbox = rbox;
	}

	public boolean build(final Role s)
	{
		return build(s, new HashSet<Role>()) != null;
	}

	private TransitionGraph<Role> build(final Role s, final Set<Role> visited)
	{
		if (!visited.add(s))
			return null;

		TransitionGraph<Role> tg = s.getFSM();
		if (tg == null)
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Building NFA for " + s);

			tg = buildNondeterministicFSM(s, visited);

			if (tg == null)
			{
				_logger.warning("Cycle detected in the complex subproperty chain involving " + s);
				s.setForceSimple(true);
				_rbox.ignoreTransitivity(s);
				return null;
			}

			assert tg.isConnected();

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Determinize " + s + ": " + tg.size() + "\n" + tg);

			tg.determinize();

			assert tg.isConnected();
			assert tg.isDeterministic();

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Minimize NFA for " + s + ": " + tg.size() + "\n" + tg);

			tg.minimize();

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Minimized NFA for " + s + ": " + tg.size() + "\n" + tg);

			assert tg.isConnected();

			tg.renumber();

			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Renumbered " + s + ": " + tg.size() + "\n" + tg);

			assert tg.isConnected();

			setFSM(s, tg);

			setFSM(s.getInverse(), mirror(tg).determinize().renumber());
		}

		visited.remove(s);

		return tg;
	}

	private void setFSM(final Role s, final TransitionGraph<Role> tg)
	{
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("NFA for " + s + ":\n" + tg);

		s.setFSM(tg);

		final Set<Role> eqRoles = s.getEquivalentProperties();
		eqRoles.remove(s);
		for (final Role eqRole : eqRoles)
			eqRole.setFSM(tg);
	}

	private TransitionGraph<Role> buildNondeterministicFSM(final Role s, final Set<Role> visited)
	{
		final TransitionGraph<Role> tg = new TransitionGraph<>();

		final State<Role> i = tg.newState();
		final State<Role> f = tg.newState();

		tg.setInitialState(i);
		tg.addFinalState(f);

		tg.addTransition(i, s, f);

		if (s.isBuiltin())
			return tg;

		for (final Role sub : s.getProperSubRoles())
			if (!sub.isBottom() && !sub.getInverse().isBottom())
				tg.addTransition(i, sub, f);

		for (final ATermList subChain : s.getSubRoleChains())
			if (!addRoleChainTransition(tg, s, subChain))
				return null;

		final Set<Role> alphabet = new HashSet<>(tg.getAlpahabet());
		for (final Role r : alphabet)
			for (final Pair<State<Role>, State<Role>> pair : tg.findTransitions(r))
			{
				if (s.isEquivalent(r))
					if (tg.isInitial(pair.first) || tg.isFinal(pair.second) || (tg.isFinal(pair.first) && tg.isInitial(pair.second)))
						continue;
					else
						return null;

				final TransitionGraph<Role> newGraph = build(r, visited);

				if (newGraph == null)
					return null;

				tg.insert(newGraph, pair.first, pair.second);
			}

		return tg;
	}

	private boolean addRoleChainTransition(final TransitionGraph<Role> tg, final Role s, final ATermList chain)
	{
		final Role firstRole = _rbox.getRole(chain.getFirst());
		final Role lastRole = _rbox.getRole(chain.getLast());
		final boolean firstRoleSame = s.isEquivalent(firstRole);
		final boolean lastRoleSame = s.isEquivalent(lastRole);
		final int length = chain.getLength();

		if (firstRoleSame)
		{
			if (lastRoleSame && length != 2)
				return false;

			addRoleChainTransition(tg, tg.getFinalState(), tg.getFinalState(), chain.getNext(), length - 1);
		}
		else
			if (lastRoleSame)
				addRoleChainTransition(tg, tg.getInitialState(), tg.getInitialState(), chain, length - 1);
			else
				addRoleChainTransition(tg, tg.getInitialState(), tg.getFinalState(), chain, length);

		return true;
	}

	private void addRoleChainTransition(final TransitionGraph<Role> tg, final State<Role> initialState, final State<Role> finalState, ATermList chain, final int length)
	{
		State<Role> prev = initialState;
		for (int i = 0; i < length; i++, chain = chain.getNext())
		{
			final Role role = _rbox.getRole(chain.getFirst());
			final State<Role> next = i == length - 1 ? finalState : tg.newState();
			tg.addTransition(prev, role, next);
			prev = next;
		}
	}

	private TransitionGraph<Role> mirror(final TransitionGraph<Role> tg)
	{
		final Map<State<Role>, State<Role>> newStates = new HashMap<>();

		final TransitionGraph<Role> mirror = new TransitionGraph<>();

		final State<Role> oldInitialState = tg.getInitialState();
		final State<Role> newFinalState = copyState(oldInitialState, mirror, newStates);

		mirror.addFinalState(newFinalState);

		final Set<State<Role>> oldFinalStates = tg.getFinalStates();
		State<Role> newInitialState = null;
		if (oldFinalStates.size() == 1)
		{
			final State<Role> oldFinalState = oldFinalStates.iterator().next();
			newInitialState = newStates.get(oldFinalState);
		}
		else
		{
			newInitialState = mirror.newState();
			for (final State<Role> oldFinalState : oldFinalStates)
				mirror.addTransition(newInitialState, newStates.get(oldFinalState));
		}

		mirror.setInitialState(newInitialState);

		return mirror;
	}

	private State<Role> copyState(final State<Role> oldState, final TransitionGraph<Role> newTG, final Map<State<Role>, State<Role>> newStates)
	{
		State<Role> newState = newStates.get(oldState);
		if (newState == null)
		{
			newState = newTG.newState();
			newStates.put(oldState, newState);
			for (final Transition<Role> t : oldState.getTransitions())
			{
				final State<Role> oldTo = t.getTo();
				final State<Role> newFrom = copyState(oldTo, newTG, newStates);
				if (t.isEpsilon())
					newTG.addTransition(newFrom, newState);
				else
					newTG.addTransition(newFrom, t.getName().getInverse(), newState);
			}
		}

		return newState;
	}
}
