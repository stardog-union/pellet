// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.tbox.impl;

import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;

public class TgBox extends TBoxBase
{
	public static final Logger _subLogger = Log.getLogger(TgBox.class);

	static
	{
		_subLogger.setParent(_logger);
	}

	private Set<ATermAppl> _explanation;

	// universal concept
	private List<Unfolding> UC = null;

	/*
	 * Constructors
	 */

	public TgBox(final TBoxExpImpl tbox)
	{
		super(tbox);
	}

	/*
	 * Utility Functions
	 */

	public void internalize()
	{

		UC = new ArrayList<>();

		for (final TermDefinition termDef : _termhash.values())
		{
			for (final ATermAppl subClassAxiom : termDef.getSubClassAxioms())
			{
				final ATermAppl c1 = (ATermAppl) subClassAxiom.getArgument(0);
				final ATermAppl c2 = (ATermAppl) subClassAxiom.getArgument(1);
				final ATermAppl notC1 = ATermUtils.makeNot(c1);
				final ATermAppl notC1orC2 = ATermUtils.makeOr(notC1, c2);
				final ATermAppl norm = ATermUtils.normalize(notC1orC2);

				Set<ATermAppl> explanation;
				if (PelletOptions.USE_TRACING)
					explanation = _tbox.getAxiomExplanation(subClassAxiom);
				else
					explanation = Collections.emptySet();

				UC.add(Unfolding.create(norm, explanation));
			}

			for (final ATermAppl eqClassAxiom : termDef.getEqClassAxioms())
			{
				final ATermAppl c1 = (ATermAppl) eqClassAxiom.getArgument(0);
				final ATermAppl c2 = (ATermAppl) eqClassAxiom.getArgument(1);
				final ATermAppl notC1 = ATermUtils.makeNot(c1);
				final ATermAppl notC2 = ATermUtils.makeNot(c2);
				final ATermAppl notC1orC2 = ATermUtils.makeOr(notC1, c2);
				final ATermAppl notC2orC1 = ATermUtils.makeOr(notC2, c1);
				Set<ATermAppl> explanation;
				if (PelletOptions.USE_TRACING)
					explanation = _tbox.getAxiomExplanation(eqClassAxiom);
				else
					explanation = Collections.emptySet();

				UC.add(Unfolding.create(ATermUtils.normalize(notC1orC2), explanation));
				UC.add(Unfolding.create(ATermUtils.normalize(notC2orC1), explanation));
			}
		}
	}

	public void absorb()
	{
		_subLogger.fine("Absorption started");

		if (_subLogger.isLoggable(Level.FINE))
			_subLogger.fine("Tg.size was " + _termhash.size() + " _Tu.size was " + _tbox._Tu.size());

		final Collection<TermDefinition> terms = _termhash.values();

		_termhash = new HashMap<>();

		for (final TermDefinition def : terms)
		{
			_kb.timers.checkTimer("preprocessing");

			for (final ATermAppl subClassAxiom : def.getSubClassAxioms())
			{
				final ATermAppl c1 = (ATermAppl) subClassAxiom.getArgument(0);
				final ATermAppl c2 = (ATermAppl) subClassAxiom.getArgument(1);

				absorbSubClass(c1, c2, _tbox.getAxiomExplanation(subClassAxiom));
			}

			for (final ATermAppl eqClassAxiom : def.getEqClassAxioms())
			{
				final ATermAppl c1 = (ATermAppl) eqClassAxiom.getArgument(0);
				final ATermAppl c2 = (ATermAppl) eqClassAxiom.getArgument(1);

				absorbSubClass(c1, c2, _tbox.getAxiomExplanation(eqClassAxiom));
				absorbSubClass(c2, c1, _tbox.getAxiomExplanation(eqClassAxiom));
			}
		}

		if (_subLogger.isLoggable(Level.FINE))
			_subLogger.fine("Tg.size is " + _termhash.size() + " _Tu.size is " + _tbox._Tu.size());

		_subLogger.fine("Absorption finished");
	}

	private void absorbSubClass(final ATermAppl sub, final ATermAppl sup, final Set<ATermAppl> axiomExplanation)
	{
		if (_subLogger.isLoggable(Level.FINE))
			_subLogger.fine("Absorb: subClassOf(" + ATermUtils.toString(sub) + ", " + ATermUtils.toString(sup) + ")");

		final HashSet<ATermAppl> set = new HashSet<>();

		set.add(ATermUtils.nnf(sub));
		set.add(ATermUtils.nnf(ATermUtils.makeNot(sup)));

		// ***********************************
		// Explanation-related axiom tracking:
		// This is used in absorbII() where actual absorption takes place
		// with primitive definition
		_explanation = new HashSet<>();
		_explanation.addAll(axiomExplanation);
		// ***********************************

		absorbTerm(set);
	}

	private boolean absorbTerm(final Set<ATermAppl> set)
	{
		final RuleAbsorber ruleAbsorber = new RuleAbsorber(_tbox);
		if (_subLogger.isLoggable(Level.FINER))
			_subLogger.finer("Absorbing term " + set);
		while (true)
		{
			_subLogger.finer("Absorb rule");
			if (PelletOptions.USE_RULE_ABSORPTION && ruleAbsorber.absorbRule(set, _explanation))
				return true;
			_subLogger.finer("Absorb nominal");
			if (!PelletOptions.USE_PSEUDO_NOMINALS && (PelletOptions.USE_NOMINAL_ABSORPTION || PelletOptions.USE_HASVALUE_ABSORPTION) && absorbNominal(set))
				return true;
			_subLogger.finer("Absorb II");
			if (absorbII(set))
			{
				_subLogger.finer("Absorbed");
				return true;
			}
			_subLogger.finer("Absorb III");
			if (absorbIII(set))
			{
				_subLogger.finer("Absorb III");
				continue;
			}
			// _subLogger.finer("Absorb IV");
			// if (absorbIV(set)) {
			// _subLogger.finer("Absorb IV");
			// continue;
			// }
			_subLogger.finer("Absorb V");
			if (absorbV(set))
			{
				_subLogger.finer("Absorb V");
				continue;
			}
			_subLogger.finer("Absorb VI");
			if (absorbVI(set))
			{
				_subLogger.finer("Recursed on OR");
				return true;
			}
			_subLogger.finer("Absorb role");
			if (PelletOptions.USE_ROLE_ABSORPTION && absorbRole(set))
			{
				_subLogger.finer("Absorbed w/ Role");
				return true;
			}
			_subLogger.finer("Absorb VII");
			absorbVII(set);
			_subLogger.finer("Finished absorbTerm");
			return false;
		}
	}

	private boolean absorbNominal(final Set<ATermAppl> set)
	{
		for (final Iterator<ATermAppl> i = set.iterator(); i.hasNext();)
		{
			final ATermAppl name = i.next();
			if (PelletOptions.USE_NOMINAL_ABSORPTION && (ATermUtils.isOneOf(name) || ATermUtils.isNominal(name)))
			{
				i.remove();

				ATermList list = null;
				if (ATermUtils.isNominal(name))
					list = ATermUtils.makeList(name);
				else
					list = (ATermList) name.getArgument(0);

				final ATermAppl c = ATermUtils.makeNot(ATermUtils.makeAnd(ATermUtils.makeList(set)));

				absorbOneOf(list, c, _explanation);

				return true;
			}
			else
				if (PelletOptions.USE_HASVALUE_ABSORPTION && ATermUtils.isHasValue(name))
				{
					final ATermAppl p = (ATermAppl) name.getArgument(0);
					if (!_kb.isObjectProperty(p))
						continue;

					i.remove();
					final ATermAppl c = ATermUtils.makeNot(ATermUtils.makeAnd(ATermUtils.makeList(set)));

					final ATermAppl nominal = (ATermAppl) name.getArgument(1);
					final ATermAppl ind = (ATermAppl) nominal.getArgument(0);

					final ATermAppl invP = _kb.getProperty(p).getInverse().getName();
					final ATermAppl allInvPC = ATermUtils.makeAllValues(invP, c);

					if (_subLogger.isLoggable(Level.FINER))
						_subLogger.finer("Absorb into " + ind + " with inverse of " + p + " for " + c);

					_tbox.getAbsorbedAxioms().addAll(_explanation);

					_kb.addIndividual(ind);
					_kb.addType(ind, allInvPC, new DependencySet(_explanation));

					return true;
				}
		}

		return false;
	}

	void absorbOneOf(final ATermAppl oneOf, final ATermAppl c, final Set<ATermAppl> explain)
	{
		absorbOneOf((ATermList) oneOf.getArgument(0), c, explain);
	}

	private void absorbOneOf(ATermList list, final ATermAppl c, final Set<ATermAppl> explain)
	{
		if (PelletOptions.USE_PSEUDO_NOMINALS)
		{
			if (_subLogger.isLoggable(Level.WARNING))
				_subLogger.warning("Ignoring axiom involving nominals: " + explain);
			return;
		}

		if (_subLogger.isLoggable(Level.FINE))
			_subLogger.fine("Absorb nominals: " + ATermUtils.toString(c) + " " + list);

		_tbox.getAbsorbedAxioms().addAll(explain);

		final DependencySet ds = new DependencySet(explain);
		while (!list.isEmpty())
		{
			final ATermAppl nominal = (ATermAppl) list.getFirst();
			final ATermAppl ind = (ATermAppl) nominal.getArgument(0);
			_kb.addIndividual(ind);
			_kb.addType(ind, c, ds);
			list = list.getNext();
		}
	}

	private boolean absorbRole(final Set<ATermAppl> set)
	{
		for (final Iterator<ATermAppl> i = set.iterator(); i.hasNext();)
		{
			final ATermAppl name = i.next();

			if (ATermUtils.isSomeValues(name))
			{
				final ATermAppl r = (ATermAppl) name.getArgument(0);
				if (_kb.getRole(r).hasComplexSubRole())
					continue;

				final ATermAppl domain = ATermUtils.makeNot(ATermUtils.makeAnd(ATermUtils.makeList(set)));
				_kb.addDomain(r, domain, _explanation);

				if (_subLogger.isLoggable(Level.FINE))
					_subLogger.fine("Absorb domain: " + ATermUtils.toString(r) + " " + ATermUtils.toString(domain));

				_tbox.getAbsorbedAxioms().addAll(_explanation);
				return true;
			}
			else
				if (ATermUtils.isMin(name))
				{
					final ATermAppl r = (ATermAppl) name.getArgument(0);
					final ATermAppl q = (ATermAppl) name.getArgument(2);
					if (_kb.getRole(r).hasComplexSubRole() || !ATermUtils.isTop(q))
						continue;

					final int n = ((ATermInt) name.getArgument(1)).getInt();

					// if we have min(r,1) sub ... this is also equal to a domain
					// restriction
					if (n == 1)
					{
						i.remove();
						final ATermAppl domain = ATermUtils.makeNot(ATermUtils.makeAnd(ATermUtils.makeList(set)));
						_kb.addDomain(r, domain, _explanation);
						if (_subLogger.isLoggable(Level.FINE))
							_subLogger.fine("Absorb domain: " + ATermUtils.toString(r) + " " + ATermUtils.toString(domain));
						_tbox.getAbsorbedAxioms().addAll(_explanation);
						return true;
					}
				}
		}

		return false;
	}

	private boolean absorbII(final Set<ATermAppl> set)
	{
		for (final ATermAppl term : set)
		{
			final TermDefinition td = _tbox._Tu.getTD(term);
			boolean canAbsorb;
			if (td != null)
				canAbsorb = td.getEqClassAxioms().isEmpty();
			else
				canAbsorb = term.getArity() == 0 && set.size() > 1;

			if (canAbsorb)
				{
				set.remove(term);

				final ATermList setlist = ATermUtils.makeList(set);
				ATermAppl conjunct = ATermUtils.makeAnd(setlist);
				conjunct = ATermUtils.makeNot(conjunct);
				final ATermAppl sub = ATermUtils.makeSub(term, ATermUtils.nnf(conjunct));
				_tbox._Tu.addDef(sub);

				if (_subLogger.isLoggable(Level.FINE))
					_subLogger.fine("Absorb named: " + ATermUtils.toString(sub));

					_tbox.addAxiomExplanation(sub, _explanation);

				return true;
			}
		}

		return false;
	}

	private boolean absorbIII(final Set<ATermAppl> set)
	{
		for (final ATermAppl term : set)
		{
			ATermAppl negatedTerm = null;

			TermDefinition td = _tbox._Tu.getTD(term);

			if (td == null && ATermUtils.isNegatedPrimitive(term))
			{
				negatedTerm = (ATermAppl) term.getArgument(0);
				td = _tbox._Tu.getTD(negatedTerm);
			}

			if (td == null || ATermUtils.isTop(td.getName()))
				continue;

			final List<ATermAppl> eqClassAxioms = td.getEqClassAxioms();
			if (!eqClassAxioms.isEmpty())
			{
				final ATermAppl eqClassAxiom = eqClassAxioms.get(0);
				final ATermAppl eqClass = (ATermAppl) eqClassAxiom.getArgument(1);

				set.remove(term);

				if (negatedTerm == null)
					set.add(eqClass);
				else
					set.add(ATermUtils.negate(eqClass));
				// *******************************
				// Explanation-related tracking of axioms
				_explanation.addAll(_tbox.getAxiomExplanation(eqClassAxiom));
				// *******************************

				return true;
			}
		}

		return false;
	}

	private boolean absorbV(final Set<ATermAppl> set)
	{
		for (final ATermAppl term : set)
		{
			final ATermAppl nnfterm = ATermUtils.nnf(term);
			// System.out.println(term);
			if (nnfterm.getAFun().equals(ATermUtils.ANDFUN))
			{
				set.remove(term);
				ATermList andlist = (ATermList) nnfterm.getArgument(0);
				while (!andlist.isEmpty())
				{
					set.add((ATermAppl) andlist.getFirst());
					andlist = andlist.getNext();
				}
				return true;
			}
		}
		return false;
	}

	private boolean absorbVI(final Set<ATermAppl> set)
	{
		for (final ATermAppl term : set)
		{
			final ATermAppl nnfterm = ATermUtils.nnf(term);
			if (nnfterm.getAFun().equals(ATermUtils.ORFUN))
			{
				set.remove(term);
				for (ATermList orlist = (ATermList) nnfterm.getArgument(0); !orlist.isEmpty(); orlist = orlist.getNext())
				{
					final Set<ATermAppl> cloned = new HashSet<>(set);
					cloned.add((ATermAppl) orlist.getFirst());
					// System.out.println("Term: "+term);
					// System.out.println("Recursing on "+cloned);
					// System.out.println("--");
					absorbTerm(cloned);
				}
				return true;
			}
		}

		return false;
	}

	private boolean absorbVII(final Set<ATermAppl> set)
	{
		ATermList list = ATermUtils.makeList(set);
		final ATermAppl sub = ATermUtils.nnf((ATermAppl) list.getFirst());
		list = list.getNext();

		ATermAppl sup = list.isEmpty() ? ATermUtils.makeNot(sub) : ATermUtils.makeNot(ATermUtils.makeAnd(list));

		sup = ATermUtils.nnf(sup);

		final ATermAppl subClassAxiom = ATermUtils.makeSub(sub, sup);

		if (_subLogger.isLoggable(Level.FINE))
			_subLogger.fine("GCI: " + subClassAxiom + "\nexplanation: " + _explanation);

		addDef(subClassAxiom);

		_tbox.addAxiomExplanation(subClassAxiom, _explanation);

		return true;
	}

	/**
	 * @return Returns the UC.
	 */
	public List<Unfolding> getUC()
	{
		return UC;
	}

	@Override
	public int size()
	{
		return UC == null ? 0 : UC.size();
	}

	public void print(final Appendable out)
	{
		try
		{
			out.append("Tg: [\n");
			if (UC != null)
			{
				for (final Unfolding unf : UC)
				{
					out.append(ATermUtils.toString(unf.getResult()));
					out.append(", ");
				}
				out.append("\n");
			}

			out.append("]");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
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
}
