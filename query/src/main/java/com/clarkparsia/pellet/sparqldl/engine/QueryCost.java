// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import com.clarkparsia.pellet.sparqldl.model.Core;
import com.clarkparsia.pellet.sparqldl.model.NotKnownQueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.UnionQueryAtom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import openllet.aterm.ATermAppl;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.KBOperation;
import org.mindswap.pellet.utils.SizeEstimate;

/**
 * <p>
 * Title: AtomCostImpl
 * </p>
 * <p>
 * Description: Computes the cost _estimate for given atom.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Petr Kremen
 */
public class QueryCost
{
	private double _staticCost;

	private double _branchCount;

	private final KnowledgeBase _kb;

	private final SizeEstimate _estimate;

	public QueryCost(final KnowledgeBase kb)
	{
		this._kb = kb;
		this._estimate = kb.getSizeEstimate();
	}

	public double estimate(final List<QueryAtom> atoms)
	{
		return estimate(atoms, new HashSet<ATermAppl>());
	}

	public double estimate(final List<QueryAtom> atoms, final Collection<ATermAppl> bound)
	{
		double totalStaticCount = 1.0;
		double totalBranchCount = 1.0;

		_branchCount = 1;
		_staticCost = 1.0;

		final int n = atoms.size();

		Set<ATermAppl> lastBound = new HashSet<>(bound);
		final List<Set<ATermAppl>> boundList = new ArrayList<>(n);
		for (int i = 0; i < n; i++)
		{
			final QueryAtom atom = atoms.get(i);

			boundList.add(lastBound);

			lastBound = new HashSet<>(lastBound);
			lastBound.addAll(atom.getArguments());
		}

		for (int i = n - 1; i >= 0; i--)
		{
			final QueryAtom atom = atoms.get(i);

			estimate(atom, boundList.get(i));

			totalBranchCount *= _branchCount;
			totalStaticCount = _staticCost + _branchCount * totalStaticCount;
		}

		_staticCost = totalStaticCount;
		_branchCount = totalBranchCount;

		return _staticCost;
	}

	public double estimate(final QueryAtom atom)
	{
		return estimate(atom, new HashSet<ATermAppl>());
	}

	public double estimate(final QueryAtom atom, final Collection<ATermAppl> bound)
	{
		boolean direct = false;
		boolean strict = false;

		final List<ATermAppl> arguments = atom.getArguments();
		for (final ATermAppl a : arguments)
			if (isConstant(a))
				bound.add(a);

		switch (atom.getPredicate())
		{
			case DirectType:
				direct = true;
				//$FALL-THROUGH$
			case Type:
				final ATermAppl instance = arguments.get(0);
				final ATermAppl clazz = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = direct ? _estimate.getCost(KBOperation.IS_DIRECT_TYPE) : _estimate.getCost(KBOperation.IS_TYPE);
					_branchCount = 1;
				}
				else
					if (bound.contains(clazz))
					{
						_staticCost = direct ? _estimate.getCost(KBOperation.GET_DIRECT_INSTANCES) : _estimate.getCost(KBOperation.GET_INSTANCES);
						_branchCount = isConstant(clazz) ? _estimate.size(clazz) : _estimate.avgInstancesPerClass(direct);
					}
					else
						if (bound.contains(instance))
						{
							_staticCost = _estimate.getCost(KBOperation.GET_TYPES);
							_branchCount = isConstant(instance) ? _estimate.classesPerInstance(instance, direct) : _estimate.avgClassesPerInstance(direct);
						}
						else
						{
							_staticCost = _estimate.getClassCount() * (direct ? _estimate.getCost(KBOperation.GET_DIRECT_INSTANCES) : _estimate.getCost(KBOperation.GET_INSTANCES));
							_branchCount = _estimate.getClassCount() * _estimate.avgInstancesPerClass(direct);
						}
				break;

			case Annotation: // TODO
			case PropertyValue:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.HAS_PROPERTY_VALUE);
					_branchCount = 1;
				}
				else
				{
					final ATermAppl subject = arguments.get(0);
					final ATermAppl predicate = arguments.get(1);
					final ATermAppl object = arguments.get(2);

					if (bound.contains(predicate))
					{
						if (bound.contains(subject))
						{
							_staticCost = _estimate.getCost(KBOperation.GET_PROPERTY_VALUE);
							_branchCount = isConstant(predicate) ? _estimate.avg(predicate) : _estimate.avgSubjectsPerProperty();
						}
						else
							if (bound.contains(object))
							{
								_staticCost = _estimate.getCost(KBOperation.GET_PROPERTY_VALUE);
								if (isConstant(predicate))
								{
									if (_kb.isObjectProperty(predicate))
										_branchCount = _estimate.avg(inv(predicate));
									else
										_branchCount = _estimate.avgSubjectsPerProperty();
								}
								else
									_branchCount = _estimate.avgSubjectsPerProperty();
							}
							else
							{
								_staticCost = _estimate.getCost(KBOperation.GET_PROPERTY_VALUE)
										/*
										 * TODO should be st. like
										 * GET_INSTANCES_OF_ROLLED_CONCEPT that reflects the
										 * complexity of the concept.
										 */
										+ (isConstant(predicate) ? _estimate.avg(predicate) : _estimate.avgSubjectsPerProperty()) * _estimate.getCost(KBOperation.GET_PROPERTY_VALUE);
								_branchCount = (isConstant(predicate) ? _estimate.size(predicate) : _estimate.avgPairsPerProperty());
							}
					}
					else
						if (bound.contains(subject) || bound.contains(object))
						{
							_staticCost = _estimate.getPropertyCount() * _estimate.getCost(KBOperation.GET_PROPERTY_VALUE);
							_branchCount = _estimate.getPropertyCount() * _estimate.avgSubjectsPerProperty();
						}
						else
						{
							_staticCost = _estimate.getPropertyCount() * (_estimate.getCost(KBOperation.GET_PROPERTY_VALUE)
							/*
							 * TODO should be st. like
							 * GET_INSTANCES_OF_ROLLED_CONCEPT that reflects the
							 * complexity of the concept.
							 */+ _estimate.avgSubjectsPerProperty() * _estimate.getCost(KBOperation.GET_PROPERTY_VALUE));
							_branchCount = _estimate.avgPairsPerProperty() * _estimate.getPropertyCount();
						}
				}
				break;

			case SameAs:
				final ATermAppl saLHS = arguments.get(0);
				final ATermAppl saRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_SAME_AS);
					_branchCount = 1;
				}
				else
					if (bound.contains(saLHS) || bound.contains(saRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_SAMES);

						if (bound.contains(saLHS))
							_branchCount = isConstant(saLHS) ? _estimate.sames(saLHS) : _estimate.avgSamesPerInstance();
							else
								_branchCount = isConstant(saRHS) ? _estimate.sames(saRHS) : _estimate.avgSamesPerInstance();
					}
					else
					{
						_staticCost = _estimate.getInstanceCount() * _estimate.getCost(KBOperation.GET_SAMES);
						_branchCount = _estimate.getInstanceCount() * _estimate.avgSamesPerInstance();
					}
				break;
			case DifferentFrom:
				final ATermAppl dfLHS = arguments.get(0);
				final ATermAppl dfRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_DIFFERENT_FROM);
					_branchCount = 1;
				}
				else
					if (bound.contains(dfLHS) || bound.contains(dfRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_DIFFERENTS);

						if (bound.contains(dfLHS))
							_branchCount = isConstant(dfLHS) ? _estimate.differents(dfLHS) : _estimate.avgDifferentsPerInstance();
							else
								_branchCount = isConstant(dfRHS) ? _estimate.differents(dfRHS) : _estimate.avgDifferentsPerInstance();
					}
					else
					{
						_staticCost = _estimate.getInstanceCount() * _estimate.getCost(KBOperation.GET_DIFFERENTS);
						_branchCount = _estimate.getInstanceCount() * _estimate.avgDifferentsPerInstance();
					}
				break;

			case DirectSubClassOf:
				direct = true;
				//$FALL-THROUGH$
			case StrictSubClassOf:
				strict = true;
				//$FALL-THROUGH$
			case SubClassOf:
				final ATermAppl clazzLHS = arguments.get(0);
				final ATermAppl clazzRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					if (strict)
					{
						if (direct)
							_staticCost = _estimate.getCost(KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES);
						else
							_staticCost = _estimate.getCost(KBOperation.IS_SUBCLASS_OF) + _estimate.getCost(KBOperation.GET_EQUIVALENT_CLASSES);
					}
					else
						_staticCost = _estimate.getCost(KBOperation.IS_SUBCLASS_OF);

					_branchCount = 1;
				}
				else
					if (bound.contains(clazzLHS) || bound.contains(clazzRHS))
					{
						if (strict && !direct)
							_staticCost = _estimate.getCost(KBOperation.GET_SUB_OR_SUPERCLASSES) + _estimate.getCost(KBOperation.GET_EQUIVALENT_CLASSES);
						else
							_staticCost = direct ? _estimate.getCost(KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES) : _estimate.getCost(KBOperation.GET_SUB_OR_SUPERCLASSES);
							if (bound.contains(clazzLHS))
							{
								_branchCount = isConstant(clazzLHS) ? _estimate.superClasses(clazzLHS, direct) : _estimate.avgSuperClasses(direct);

								if (strict)
								{
									_branchCount -= isConstant(clazzLHS) ? _estimate.equivClasses(clazzLHS) : _estimate.avgEquivClasses();
									_branchCount = Math.max(_branchCount, 0);
								}
							}
							else
							{
								_branchCount = isConstant(clazzRHS) ? _estimate.superClasses(clazzRHS, direct) : _estimate.avgSuperClasses(direct);

								if (strict)
								{
									_branchCount -= isConstant(clazzRHS) ? _estimate.equivClasses(clazzRHS) : _estimate.avgEquivClasses();
									_branchCount = Math.max(_branchCount, 0);
								}
							}
					}
					else
					{
						if (strict && !direct)
							_staticCost = _estimate.getCost(KBOperation.GET_SUB_OR_SUPERCLASSES) + _estimate.getCost(KBOperation.GET_EQUIVALENT_CLASSES);
						else
							_staticCost = direct ? _estimate.getCost(KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES) : _estimate.getCost(KBOperation.GET_SUB_OR_SUPERCLASSES);

							_staticCost *= _estimate.getClassCount();

							_branchCount = _estimate.getClassCount() * _estimate.avgSubClasses(direct);

							if (strict)
							{
								_branchCount -= _estimate.avgEquivClasses();
								_branchCount = Math.max(_branchCount, 0);
							}
					}
				break;
			case EquivalentClass:
				final ATermAppl eqcLHS = arguments.get(0);
				final ATermAppl eqcRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_EQUIVALENT_CLASS);
					_branchCount = 1;
				}
				else
					if (bound.contains(eqcLHS) || bound.contains(eqcRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_EQUIVALENT_CLASSES);

						if (bound.contains(eqcLHS))
							_branchCount = isConstant(eqcLHS) ? _estimate.equivClasses(eqcLHS) : _estimate.avgEquivClasses();
							else
								_branchCount = isConstant(eqcRHS) ? _estimate.equivClasses(eqcRHS) : _estimate.avgEquivClasses();
					}
					else
					{
						_staticCost = _estimate.getClassCount() * _estimate.getCost(KBOperation.GET_EQUIVALENT_CLASSES);
						_branchCount = _estimate.getClassCount() * _estimate.avgEquivClasses();
					}
				break;
			case DisjointWith:
				final ATermAppl dwLHS = arguments.get(0);
				final ATermAppl dwRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_DISJOINT_WITH);
					_branchCount = 1;
				}
				else
					if (bound.contains(dwLHS) || bound.contains(dwRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_DISJOINT_CLASSES);

						if (bound.contains(dwLHS))
							_branchCount = isConstant(dwLHS) ? _estimate.disjoints(dwLHS) : _estimate.avgDisjointClasses();
							else
								_branchCount = isConstant(dwRHS) ? _estimate.disjoints(dwRHS) : _estimate.avgDisjointClasses();
					}
					else
					{
						_staticCost = _estimate.getClassCount() * _estimate.getCost(KBOperation.GET_DISJOINT_CLASSES);
						_branchCount = _estimate.getClassCount() * _estimate.avgDisjointClasses();
					}
				break;
			case ComplementOf:
				final ATermAppl coLHS = arguments.get(0);
				final ATermAppl coRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_COMPLEMENT_OF);
					_branchCount = 1;
				}
				else
					if (bound.contains(coLHS) || bound.contains(coRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_COMPLEMENT_CLASSES);

						if (bound.contains(coLHS))
							_branchCount = isConstant(coLHS) ? _estimate.complements(coLHS) : _estimate.avgComplementClasses();
							else
								_branchCount = isConstant(coRHS) ? _estimate.complements(coRHS) : _estimate.avgComplementClasses();
					}
					else
					{
						_staticCost = _estimate.getClassCount() * _estimate.getCost(KBOperation.GET_COMPLEMENT_CLASSES);
						_branchCount = _estimate.getClassCount() * _estimate.avgComplementClasses();
					}
				break;

			case DirectSubPropertyOf:
				direct = true;
				//$FALL-THROUGH$
			case StrictSubPropertyOf:
				strict = true;
				//$FALL-THROUGH$
			case SubPropertyOf:
				final ATermAppl spLHS = arguments.get(0);
				final ATermAppl spRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					if (strict)
					{
						if (direct)
							_staticCost = _estimate.getCost(KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES);
						else
							_staticCost = _estimate.getCost(KBOperation.IS_SUBPROPERTY_OF) + _estimate.getCost(KBOperation.GET_EQUIVALENT_PROPERTIES);
					}
					else
						_staticCost = _estimate.getCost(KBOperation.IS_SUBPROPERTY_OF);

					_branchCount = 1;
				}
				else
					if (bound.contains(spLHS) || bound.contains(spRHS))
					{
						if (strict && !direct)
							_staticCost = _estimate.getCost(KBOperation.GET_SUB_OR_SUPERPROPERTIES) + _estimate.getCost(KBOperation.GET_EQUIVALENT_PROPERTIES);
						else
							_staticCost = direct ? _estimate.getCost(KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES) : _estimate.getCost(KBOperation.GET_SUB_OR_SUPERPROPERTIES);
							if (bound.contains(spLHS))
							{
								_branchCount = isConstant(spLHS) ? _estimate.superProperties(spLHS, direct) : _estimate.avgSuperProperties(direct);

								if (strict)
								{
									_branchCount -= isConstant(spLHS) ? _estimate.equivProperties(spLHS) : _estimate.avgEquivProperties();
									_branchCount = Math.max(_branchCount, 0);

								}
							}
							else
							{
								_branchCount = isConstant(spRHS) ? _estimate.superProperties(spRHS, direct) : _estimate.avgSuperProperties(direct);

								if (strict)
								{
									_branchCount -= isConstant(spRHS) ? _estimate.equivProperties(spRHS) : _estimate.avgEquivProperties();
									_branchCount = Math.max(_branchCount, 0);

								}
							}
					}
					else
					{
						if (strict && !direct)
							_staticCost = _estimate.getCost(KBOperation.GET_SUB_OR_SUPERPROPERTIES) + _estimate.getCost(KBOperation.GET_EQUIVALENT_PROPERTIES);
						else
							_staticCost = direct ? _estimate.getCost(KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES) : _estimate.getCost(KBOperation.GET_SUB_OR_SUPERPROPERTIES);

							_staticCost *= _estimate.getPropertyCount();

							_branchCount = _estimate.getPropertyCount() * _estimate.avgSubProperties(direct);

							if (strict)
							{
								_branchCount -= _estimate.avgEquivProperties();
								_branchCount = Math.max(_branchCount, 0);

							}
					}
				break;

			case EquivalentProperty:
				final ATermAppl eqpLHS = arguments.get(0);
				final ATermAppl eqpRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_EQUIVALENT_PROPERTY);
					_branchCount = 1;
				}
				else
					if (bound.contains(eqpLHS) || bound.contains(eqpRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_EQUIVALENT_PROPERTIES);

						if (bound.contains(eqpLHS))
							_branchCount = isConstant(eqpLHS) ? _estimate.equivProperties(eqpLHS) : _estimate.avgEquivProperties();
							else
								_branchCount = isConstant(eqpRHS) ? _estimate.equivProperties(eqpRHS) : _estimate.avgEquivProperties();
					}
					else
					{
						_staticCost = _estimate.getPropertyCount() * _estimate.getCost(KBOperation.GET_EQUIVALENT_PROPERTIES);
						_branchCount = _estimate.getPropertyCount() * _estimate.avgEquivProperties();
					}
				break;
			case Domain:
				final ATermAppl domLHS = arguments.get(0);
				final ATermAppl domRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_DOMAIN);
					_branchCount = 1;
				}
				else
					if (bound.contains(domLHS) || bound.contains(domRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_DOMAINS);

						if (bound.contains(domLHS))
							_branchCount = isConstant(domLHS) ? _estimate.equivProperties(domLHS) : _estimate.avgEquivProperties();
							else
								_branchCount = isConstant(domRHS) ? _estimate.equivClasses(domRHS) : _estimate.avgEquivClasses();
					}
					else
					{
						_staticCost = _estimate.getPropertyCount() * _estimate.getCost(KBOperation.GET_DOMAINS);
						_branchCount = _estimate.getPropertyCount() * _estimate.avgEquivProperties();
					}
				break;
			case Range:
				final ATermAppl rangeLHS = arguments.get(0);
				final ATermAppl rangeRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_RANGE);
					_branchCount = 1;
				}
				else
					if (bound.contains(rangeLHS) || bound.contains(rangeRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_RANGES);

						if (bound.contains(rangeLHS))
							_branchCount = isConstant(rangeLHS) ? _estimate.equivProperties(rangeLHS) : _estimate.avgEquivProperties();
							else
								_branchCount = isConstant(rangeRHS) ? _estimate.equivClasses(rangeRHS) : _estimate.avgEquivClasses();
					}
					else
					{
						_staticCost = _estimate.getPropertyCount() * _estimate.getCost(KBOperation.GET_RANGES);
						_branchCount = _estimate.getPropertyCount() * _estimate.avgEquivProperties();
					}

				break;
			case InverseOf:
				final ATermAppl ioLHS = arguments.get(0);
				final ATermAppl ioRHS = arguments.get(1);

				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_INVERSE_OF);
					_branchCount = 1;
				}
				else
					if (bound.contains(ioLHS) || bound.contains(ioRHS))
					{
						_staticCost = _estimate.getCost(KBOperation.GET_INVERSES);

						if (bound.contains(ioLHS))
							_branchCount = isConstant(ioLHS) ? _estimate.inverses(ioLHS) : _estimate.avgInverseProperties();
							else
								_branchCount = isConstant(ioRHS) ? _estimate.inverses(ioRHS) : _estimate.avgInverseProperties();
					}
					else
					{
						_staticCost = _estimate.getPropertyCount() * _estimate.getCost(KBOperation.GET_INVERSES);
						_branchCount = _estimate.getPropertyCount() * _estimate.avgInverseProperties();
					}
				break;
			case ObjectProperty:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_OBJECT_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_OBJECT_PROPERTIES);
					_branchCount = _estimate.getObjectPropertyCount();
				}
				break;
			case DatatypeProperty:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_DATATYPE_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_DATATYPE_PROPERTIES);
					_branchCount = _estimate.getDataPropertyCount();
				}
				break;
			case Functional:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_FUNCTIONAL_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_FUNCTIONAL_PROPERTIES);
					_branchCount = _estimate.getFunctionalPropertyCount();
				}
				break;
			case InverseFunctional:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_INVERSE_FUNCTIONAL_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_INVERSE_FUNCTIONAL_PROPERTIES);
					_branchCount = _estimate.getInverseFunctionalPropertyCount();
				}
				break;
			case Transitive:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_TRANSITIVE_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_TRANSITIVE_PROPERTIES);
					_branchCount = _estimate.getTransitivePropertyCount();
				}
				break;
			case Symmetric:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_SYMMETRIC_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_SYMMETRIC_PROPERTIES);
					_branchCount = _estimate.getSymmetricPropertyCount();
				}
				break;
			case Asymmetric:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_ASYMMETRIC_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_ASYMMETRIC_PROPERTIES);
					_branchCount = _estimate.getSymmetricPropertyCount();
				}
				break;
			case Reflexive:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_REFLEXIVE_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_REFLEXIVE_PROPERTIES);
					_branchCount = _estimate.getSymmetricPropertyCount();
				}
				break;
			case Irreflexive:
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_IRREFLEXIVE_PROPERTY);
					_branchCount = 1;
				}
				else
				{
					_staticCost = _estimate.getCost(KBOperation.GET_IRREFLEXIVE_PROPERTIES);
					_branchCount = _estimate.getSymmetricPropertyCount();
				}
				break;
			case NotKnown:
				estimate(((NotKnownQueryAtom) atom).getAtoms(), bound);
				break;

			case Union:
			{
				double totalStaticCount = 1.0;
				double totalBranchCount = 1.0;

				for (final List<QueryAtom> atoms : ((UnionQueryAtom) atom).getUnion())
				{
					estimate(atoms, bound);

					totalBranchCount += _branchCount;
					totalStaticCount += _staticCost;
				}

				_staticCost = totalStaticCount;
				_branchCount = totalBranchCount;

				break;
			}

			case UndistVarCore:
				// if (!bound.containsAll(query.getDistVarsForType(VarType.CLASS))
				// || !bound.containsAll(query
				// .getDistVarsForType(VarType.PROPERTY))) {
				// // meanwhile not supporting query orderings that allow evaluate
				// // schema atoms after a core.
				// return Double.MAX_VALUE;
				// }

				// neglecting rolling-ups
				if (bound.containsAll(arguments))
				{
					_staticCost = _estimate.getCost(KBOperation.IS_TYPE);
					_branchCount = 1;
				}
				else
				{
					final Core core = (Core) atom;
					final int n = core.getDistVars().size();

					final double b = Math.pow(_estimate.avgInstancesPerClass(false), n);
					_branchCount = b;

					switch (QueryEngine.getStrategy(atom))
					{
						case ALLFAST: // TODO
						case SIMPLE:
							_staticCost = n * _estimate.getCost(KBOperation.GET_INSTANCES) + b * _estimate.getCost(KBOperation.IS_TYPE);
							break;
						default:
							throw new IllegalArgumentException("Not yet implemented.");
					}
				}
				break;

			case Datatype:
				if (bound.containsAll(arguments))
					_staticCost = 1;
				else
					_staticCost = Integer.MAX_VALUE;
				_branchCount = 1;
				break;

			default:
				throw new UnsupportedFeatureException("Unknown atom type " + atom.getPredicate() + ".");
		}

		return _staticCost;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getBranchCount()
	{
		return _branchCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getStaticCost()
	{
		return _staticCost;
	}

	private ATermAppl inv(final ATermAppl pred)
	{
		return _kb.getRBox().getRole(pred).getInverse().getName();
	}

	private boolean isConstant(final ATermAppl a)
	{
		return !ATermUtils.isVar(a);
	}

}
