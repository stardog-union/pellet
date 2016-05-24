// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class RuleAbsorber
{
	public static final Logger log = TBoxBase._logger;

	public static final Map<ATermAppl, String> FACETS;
	static
	{
		FACETS = new HashMap<>();
		FACETS.put(Facet.XSD.MIN_INCLUSIVE.getName(), Namespaces.SWRLB + "greaterThanOrEqual");
		FACETS.put(Facet.XSD.MIN_EXCLUSIVE.getName(), Namespaces.SWRLB + "greaterThan");
		FACETS.put(Facet.XSD.MAX_INCLUSIVE.getName(), Namespaces.SWRLB + "lessThanOrEqual");
		FACETS.put(Facet.XSD.MAX_EXCLUSIVE.getName(), Namespaces.SWRLB + "lessThan");
	}

	private final KnowledgeBase _kb;
	private final TuBox _Tu;

	public RuleAbsorber(final TBoxExpImpl tbox)
	{
		this._kb = tbox.getKB();
		this._Tu = tbox._Tu;
	}

	public boolean absorbRule(final Set<ATermAppl> set, final Set<ATermAppl> explanation)
	{
		int propertyAtoms = 0;
		int primitiveClassAtoms = 0;
		ATermAppl head = null;
		for (final ATermAppl term : set)
			if (ATermUtils.isPrimitive(term))
			{
				final TermDefinition td = _Tu.getTD(term);
				if (td == null || td.getEqClassAxioms().isEmpty())
					primitiveClassAtoms++;
			}
			else
				if (ATermUtils.isSomeValues(term))
					propertyAtoms++;
				else
					if (ATermUtils.isNot(term))
						head = term;

		if (head == null || (propertyAtoms == 0 && primitiveClassAtoms < 2))
			return false;

		set.remove(head);

		final AtomIObject var = new AtomIVariable("var");
		int varCount = 0;
		final List<RuleAtom> bodyAtoms = new ArrayList<>();
		for (final ATermAppl term : set)
			varCount = processClass(var, term, bodyAtoms, varCount);

		final List<RuleAtom> headAtoms = new ArrayList<>();
		processClass(var, ATermUtils.negate(head), headAtoms, 1);

		final Rule rule = new Rule(headAtoms, bodyAtoms, explanation);
		_kb.addRule(rule);

		if (log.isLoggable(Level.FINE))
			log.fine("Absorbed rule: " + rule);

		return true;
	}

	protected int processClass(final AtomIObject var, final ATermAppl c, final List<RuleAtom> atoms, int varCount)
	{
		final AFun afun = c.getAFun();
		if (afun.equals(ATermUtils.ANDFUN))
			for (ATermList list = (ATermList) c.getArgument(0); !list.isEmpty(); list = list.getNext())
			{
				final ATermAppl conjunct = (ATermAppl) list.getFirst();
				varCount = processClass(var, conjunct, atoms, varCount);
			}
		else
			if (afun.equals(ATermUtils.SOMEFUN))
			{
				final ATermAppl p = (ATermAppl) c.getArgument(0);
				final ATermAppl filler = (ATermAppl) c.getArgument(1);

				if (filler.getAFun().equals(ATermUtils.VALUEFUN))
				{
					final ATermAppl nominal = (ATermAppl) filler.getArgument(0);
					if (_kb.isDatatypeProperty(p))
					{
						final AtomDConstant arg = new AtomDConstant(nominal);
						final RuleAtom atom = new DatavaluedPropertyAtom(p, var, arg);
						atoms.add(atom);
					}
					else
					{
						final AtomIConstant arg = new AtomIConstant(nominal);
						final RuleAtom atom = new IndividualPropertyAtom(p, var, arg);
						atoms.add(atom);
					}
				}
				else
				{
					varCount++;
					if (_kb.isDatatypeProperty(p))
					{
						final AtomDObject newVar = new AtomDVariable("var" + varCount);
						final RuleAtom atom = new DatavaluedPropertyAtom(p, var, newVar);
						atoms.add(atom);
						processDatatype(newVar, filler, atoms);
					}
					else
					{
						final AtomIObject newVar = new AtomIVariable("var" + varCount);
						final RuleAtom atom = new IndividualPropertyAtom(p, var, newVar);
						atoms.add(atom);
						varCount = processClass(newVar, filler, atoms, varCount);
					}
				}
			}
			else
				if (!c.equals(ATermUtils.TOP))
					atoms.add(new ClassAtom(c, var));

		return varCount;
	}

	protected void processDatatype(final AtomDObject var, final ATermAppl c, final List<RuleAtom> atoms)
	{
		final AFun afun = c.getAFun();
		if (afun.equals(ATermUtils.ANDFUN))
			for (ATermList list = (ATermList) c.getArgument(0); !list.isEmpty(); list = list.getNext())
			{
				final ATermAppl conjunct = (ATermAppl) list.getFirst();
				processDatatype(var, conjunct, atoms);
			}
		else
			if (afun.equals(ATermUtils.RESTRDATATYPEFUN))
			{
				final ATermAppl baseDatatype = (ATermAppl) c.getArgument(0);

				atoms.add(new DataRangeAtom(baseDatatype, var));

				for (ATermList list = (ATermList) c.getArgument(1); !list.isEmpty(); list = list.getNext())
				{
					final ATermAppl facetRestriction = (ATermAppl) list.getFirst();
					final ATermAppl facet = (ATermAppl) facetRestriction.getArgument(0);
					final String builtin = FACETS.get(facet);
					if (builtin != null)
					{
						final ATermAppl value = (ATermAppl) facetRestriction.getArgument(1);
						atoms.add(new BuiltInAtom(builtin, var, new AtomDConstant(value)));
					}
					else
					{
						atoms.add(new DataRangeAtom(c, var));
						return;
					}
				}
			}
			else
				atoms.add(new DataRangeAtom(c, var));
	}

}
