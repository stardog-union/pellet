// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;

import com.clarkparsia.pellet.utils.CollectionUtils;
import com.clarkparsia.pellet.utils.MultiMapUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.aterm.AFun;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermList;
import openllet.shared.tools.Log;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.MultiIterator;

/**
 * <p>
 * Title: Implementation of TBox interface to generate explanations efficiently and correctly.
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
public class TBoxExpImpl implements TBox
{
	public static Logger _logger = Log.getLogger(TBox.class);

	private static final Set<Set<ATermAppl>> SINGLE_EMPTY_SET = Collections.singleton(Collections.<ATermAppl> emptySet());

	protected KnowledgeBase _kb;

	protected Set<ATermAppl> _classes = CollectionUtils.makeIdentitySet();
	private Set<ATermAppl> _allClasses;

	/**
	 * MultiValueMap where key is an axiom and the values are the explanations of the key
	 */
	private final Map<ATermAppl, Set<Set<ATermAppl>>> _tboxAxioms = CollectionUtils.makeIdentityMap();
	/**
	 * MultiValueMap where key is an axiom and the values are axioms for which the key is a part of an clashExplanation
	 */
	private final Map<ATermAppl, Set<ATermAppl>> _reverseExplain = CollectionUtils.makeIdentityMap();

	private final Set<ATermAppl> _tboxAssertedAxioms = CollectionUtils.makeIdentitySet();

	/**
	 * Set of axioms that have been absorbed into ABox or RBox
	 */
	private final Set<ATermAppl> _absorbedAxioms = CollectionUtils.makeIdentitySet();

	public TuBox _Tu = null;
	public TgBox _Tg = null;

	/*
	 * Constructors
	 */

	public TBoxExpImpl(final KnowledgeBase kb)
	{
		this._kb = kb;

		_Tu = new TuBox(this);
		_Tg = new TgBox(this);

		this._kb = kb;
	}

	public KnowledgeBase getKB()
	{
		return _kb;
	}

	@Override
	public Set<ATermAppl> getAllClasses()
	{
		if (_allClasses == null)
		{
			_allClasses = new HashSet<>(_classes);
			_allClasses.add(ATermUtils.TOP);
			_allClasses.add(ATermUtils.BOTTOM);
		}
		return _allClasses;
	}

	@Override
	public Set<Set<ATermAppl>> getAxiomExplanations(final ATermAppl axiom)
	{
		return _tboxAxioms.get(axiom);
	}

	@Override
	public Set<ATermAppl> getAxiomExplanation(final ATermAppl axiom)
	{
		final Set<Set<ATermAppl>> explains = _tboxAxioms.get(axiom);

		if (explains == null || explains.isEmpty())
		{
			_logger.warning("No clashExplanation for " + axiom);
			return Collections.emptySet();
		}

		// we won't be generating multiple explanations using axiom
		// tracing so we just pick one clashExplanation. the other option
		// would be to return the union of all explanations which
		// would cause Pellet to return non-minimal explanations sets
		for (final Set<ATermAppl> explain : explains)
			return explain;
		return Collections.emptySet();
	}

	/**
	 * Add a new clashExplanation for the given axiom. If a previous clashExplanation exists this will be stored as another clashExplanation.
	 *
	 * @param axiom
	 * @param explain
	 * @return
	 */
	protected boolean addAxiomExplanation(final ATermAppl axiom, final Set<ATermAppl> explain)
	{
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Axiom: " + ATermUtils.toString(axiom) + " Explanation: " + explain);

		boolean added = false;
		if (!PelletOptions.USE_TRACING)
			added = _tboxAxioms.put(axiom, SINGLE_EMPTY_SET) == null;
		else
			added = MultiMapUtils.add(_tboxAxioms, axiom, explain);

		if (added)
			for (final ATermAppl explainAxiom : explain)
				if (!axiom.equals(explainAxiom))
					MultiMapUtils.add(_reverseExplain, explainAxiom, axiom);

		return added;
	}

	private static void addDisjointAxiom(final ATermAppl c1, final ATermAppl c2, final List<ATermAppl> axioms)
	{
		final ATermAppl notC2 = ATermUtils.makeNot(c2);
		axioms.add(ATermUtils.makeSub(c1, notC2));

		if (ATermUtils.isPrimitive(c2))
		{
			final ATermAppl notC1 = ATermUtils.makeNot(c1);
			axioms.add(ATermUtils.makeSub(c2, notC1));
		}
	}

	@Override
	public boolean addAxiom(final ATermAppl axiom)
	{
		_tboxAssertedAxioms.add(axiom);

		List<ATermAppl> axioms = null;

		final Set<ATermAppl> explain = PelletOptions.USE_TRACING ? Collections.singleton(axiom) : Collections.<ATermAppl> emptySet();

		if (axiom.getAFun().equals(ATermUtils.EQCLASSFUN))
			axioms = Collections.singletonList(axiom);
		else
			if (axiom.getAFun().equals(ATermUtils.SUBFUN))
				axioms = Collections.singletonList(axiom);
			else
				if (axiom.getAFun().equals(ATermUtils.DISJOINTFUN))
				{
					axioms = CollectionUtils.makeList();

					final ATermAppl c1 = (ATermAppl) axiom.getArgument(0);
					final ATermAppl c2 = (ATermAppl) axiom.getArgument(1);
					addDisjointAxiom(c1, c2, axioms);
				}
				else
					if (axiom.getAFun().equals(ATermUtils.DISJOINTSFUN))
					{
						axioms = CollectionUtils.makeList();

						final ATermList concepts = (ATermList) axiom.getArgument(0);
						for (ATermList l1 = concepts; !l1.isEmpty(); l1 = l1.getNext())
						{
							final ATermAppl c1 = (ATermAppl) l1.getFirst();
							for (ATermList l2 = l1.getNext(); !l2.isEmpty(); l2 = l2.getNext())
							{
								final ATermAppl c2 = (ATermAppl) l2.getFirst();
								addDisjointAxiom(c1, c2, axioms);
							}
						}
					}
					else
					{
						_logger.warning("Not a valid TBox axiom: " + axiom);
						return false;
					}

		boolean added = false;
		for (final ATermAppl a : axioms)
			if (absorbNominals(a, explain))
				added = true;
			else
				added |= addAxiom(a, explain, false);

		return added;
	}

	protected boolean absorbNominals(ATermAppl axiom, final Set<ATermAppl> explain)
	{
		// absorb nominals on the fly because sometimes they might _end up in the
		// _Tu directly without going into _Tg which is still less effective than
		// absorbing
		if (PelletOptions.USE_NOMINAL_ABSORPTION || PelletOptions.USE_PSEUDO_NOMINALS)
			if (axiom.getAFun().equals(ATermUtils.EQCLASSFUN))
			{
				final ATermAppl c1 = (ATermAppl) axiom.getArgument(0);
				final ATermAppl c2 = (ATermAppl) axiom.getArgument(1);

				// the first concept is oneOF
				if (ATermUtils.isOneOf(c1))
				{
					// absorb SubClassOf(c1,c2)
					_Tg.absorbOneOf(c1, c2, explain);
					// the second concept is oneOf
					if (ATermUtils.isOneOf(c2))
					{
						// absorb SubClassOf(c2,c1)
						_Tg.absorbOneOf(c2, c1, explain);
						// axioms completely absorbed so return
						return true;
					}
					else
						// SubClassOf(c2,c1) is not absorbed so continue with
						// addAxiom function
						axiom = ATermUtils.makeSub(c2, c1);
				}
				else
					if (ATermUtils.isOneOf(c2))
						// absorb SubClassOf(c2,c1)
						_Tg.absorbOneOf(c2, c1, explain);
			}
			else
				if (axiom.getAFun().equals(ATermUtils.SUBFUN))
				{
					final ATermAppl sub = (ATermAppl) axiom.getArgument(0);

					if (ATermUtils.isOneOf(sub))
					{
						final ATermAppl sup = (ATermAppl) axiom.getArgument(1);
						_Tg.absorbOneOf(sub, sup, explain);
						return true;
					}
				}

		return false;
	}

	protected boolean addAxiom(final ATermAppl axiom, final Set<ATermAppl> explain, final boolean forceAddition)
	{
		final boolean added = addAxiomExplanation(axiom, explain);

		if (added || forceAddition)
			if (!_Tu.addIfUnfoldable(axiom))
				if (axiom.getAFun().equals(ATermUtils.EQCLASSFUN))
				{
					// Try reversing the term if it is a 'same' construct
					final ATermAppl name = (ATermAppl) axiom.getArgument(0);
					final ATermAppl desc = (ATermAppl) axiom.getArgument(1);
					final ATermAppl reversedAxiom = ATermUtils.makeEqClasses(desc, name);

					if (!_Tu.addIfUnfoldable(reversedAxiom))
						_Tg.addDef(axiom);
					else
						addAxiomExplanation(reversedAxiom, explain);
				}
				else
					_Tg.addDef(axiom);

		return added;
	}

	@Override
	public boolean removeAxiom(final ATermAppl axiom)
	{
		return removeAxiom(axiom, axiom);
	}

	@Override
	public boolean removeAxiom(final ATermAppl dependantAxiom, final ATermAppl explanationAxiom)
	{

		if (!PelletOptions.USE_TRACING)
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Cannot remove axioms when PelletOptions.USE_TRACING is false");
			return false;
		}

		if (_absorbedAxioms.contains(dependantAxiom))
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Cannot remove axioms that have been absorbed outside TBox");
			return false;
		}

		_tboxAssertedAxioms.remove(dependantAxiom);

		final Set<ATermAppl> sideEffects = new HashSet<>();
		final boolean removed = removeExplanation(dependantAxiom, explanationAxiom, sideEffects);

		// an axiom might be effectively removed as a side-effect of another
		// removal. For example see TBoxTests.removedByAbsorbReaddedOnChange
		for (final ATermAppl readdAxiom : sideEffects)
		{
			final Set<Set<ATermAppl>> explanations = _tboxAxioms.get(readdAxiom);
			// if the axiom is really removed (and not just side-effected)
			// then there wouldn't be any clashExplanation and we shouldn't readd
			if (explanations != null)
			{
				final Iterator<Set<ATermAppl>> i = explanations.iterator();
				addAxiom(readdAxiom, i.next(), true);
				while (i.hasNext())
					addAxiomExplanation(readdAxiom, i.next());
			}
		}

		return removed;
	}

	private boolean removeExplanation(final ATermAppl dependantAxiom, final ATermAppl explanationAxiom, final Set<ATermAppl> sideEffects)
	{
		boolean success = false;

		if (!PelletOptions.USE_TRACING)
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Cannot remove axioms when PelletOptions.USE_TRACING is false");
			return false;
		}

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Removing " + ATermUtils.toString(explanationAxiom));

		// this axiom is being removed so it cannot support any other axiom
		MultiMapUtils.remove(_reverseExplain, explanationAxiom, dependantAxiom);

		final Set<Set<ATermAppl>> explains = _tboxAxioms.get(dependantAxiom);
		final Set<Set<ATermAppl>> newExplains = new HashSet<>();

		if (explains != null)
			for (final Set<ATermAppl> explain : explains)
				if (!explain.contains(explanationAxiom))
					newExplains.add(explain);
				else
				{
					sideEffects.addAll(explain);
					sideEffects.remove(explanationAxiom);
				}

		if (!newExplains.isEmpty())
		{
			// there are still other axioms supporting this axiom so it won't be
			// removed but we still need to update the explanations
			_tboxAxioms.put(dependantAxiom, newExplains);

			// also make sure the concept on the left hand side is normalized
			_Tu.updateDef(dependantAxiom);

			// this axiom is not removed but the operation is successful (so far)
			success = true;
		}
		else
		{
			// there is no other clashExplanation for this dependant axiom so
			// we can safely remove it
			success |= (_tboxAxioms.remove(dependantAxiom) != null);

			final AFun fun = dependantAxiom.getAFun();
			if (fun.equals(ATermUtils.SUBFUN) || fun.equals(ATermUtils.EQCLASSFUN))
			{
				// remove the axiom fom _Tu and _Tg
				success |= _Tu.removeDef(dependantAxiom);
				success |= _Tg.removeDef(dependantAxiom);
			}
		}

		// find if this axiom supports any other axiom
		// note that it is possible dependantAxiom itself is not removed but an axiom that dependantAxiom supports
		// will be removed. this situation occurs typically when there is redundancy in the TBox.
		final Set<ATermAppl> otherDependants = _reverseExplain.remove(dependantAxiom);
		if (otherDependants != null)
			for (final ATermAppl otherDependant : otherDependants)
			{
				// remove this axiom from any clashExplanation it contributes to

				if (otherDependant.equals(dependantAxiom))
					continue;

				success |= removeExplanation(otherDependant, dependantAxiom, sideEffects);
			}

		return success;
	}

	@Override
	public Collection<ATermAppl> getAxioms()
	{
		return _tboxAxioms.keySet();
	}

	@Override
	public Collection<ATermAppl> getAssertedAxioms()
	{
		return _tboxAssertedAxioms;
	}

	public Collection<ATermAppl> getAbsorbedAxioms()
	{
		return _absorbedAxioms;
	}

	public boolean containsAxiom(final ATermAppl axiom)
	{
		return _tboxAxioms.containsKey(axiom);
	}

	public void absorb()
	{
		_Tg.absorb();
	}

	public void print()
	{
		print(System.out);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		print(sb);
		return sb.toString();
	}

	public void print(final Appendable str)
	{
		try
		{
			_Tu.print(str);
			_Tg.print(str);
			str.append("Explain: [\n");
			for (final ATermAppl axiom : _tboxAxioms.keySet())
			{
				str.append(ATermUtils.toString(axiom));
				str.append(" -> ");
				str.append("[");
				boolean first = true;
				for (final Set<ATermAppl> axioms : _tboxAxioms.get(axiom))
				{
					if (first)
						first = false;
					else
						str.append(", ");
					str.append(ATermUtils.toString(axioms));
				}
				str.append("]");
				str.append("\n");
			}
			str.append("]\nReverseExplain: [\n");
			for (final ATermAppl axiom : _reverseExplain.keySet())
			{
				str.append(ATermUtils.toString(axiom));
				str.append(" -> ");
				str.append(ATermUtils.toString(_reverseExplain.get(axiom)));
				str.append("\n");
			}
			str.append("]\n");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean addClass(final ATermAppl term)
	{
		final boolean added = _classes.add(term);

		if (added)
			_allClasses = null;

		return added;
	}

	@Override
	public Set<ATermAppl> getClasses()
	{
		return _classes;
	}

	@Override
	public Collection<ATermAppl> getAxioms(final ATermAppl term)
	{
		final List<ATermAppl> axioms = new ArrayList<>();
		TermDefinition def = _Tg.getTD(term);
		if (def != null)
		{
			axioms.addAll(def.getSubClassAxioms());
			axioms.addAll(def.getEqClassAxioms());
		}
		def = _Tu.getTD(term);
		if (def != null)
		{
			axioms.addAll(def.getSubClassAxioms());
			axioms.addAll(def.getEqClassAxioms());
		}

		return axioms;
	}

	@Override
	public void prepare()
	{
		_Tg.absorb();
		_Tg.internalize();
		_Tu.normalize();
	}

	@Override
	public Iterator<Unfolding> unfold(final ATermAppl c)
	{
		final MultiIterator<Unfolding> result = new MultiIterator<>(_Tu.unfold(c).iterator());
		if (c.equals(TOP) && !_Tg.getUC().isEmpty())
			result.append(_Tg.getUC().iterator());
		return result;
	}

	@Override
	public boolean isPrimitive(final ATermAppl c)
	{
		final TermDefinition td = _Tu.getTD(c);
		return ATermUtils.isPrimitive(c) && (td == null || td.isPrimitive());
	}
}
