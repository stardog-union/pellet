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

package org.mindswap.pellet;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.FilterIterator;
import org.mindswap.pellet.utils.iterator.IteratorUtils;
import org.mindswap.pellet.utils.iterator.MapIterator;

/**
 * <p>
 * Title:
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
public class RBox
{
	public static Logger _logger = Log.getLogger(RBox.class);

	private static class ValueIterator extends MapIterator<Map.Entry<ATermAppl, Set<Set<ATermAppl>>>, ATermAppl>
	{
		public ValueIterator(final Iterator<Entry<ATermAppl, Set<Set<ATermAppl>>>> iterator)
		{
			super(iterator);
		}

		@Override
		public ATermAppl map(final Entry<ATermAppl, Set<Set<ATermAppl>>> e)
		{
			return e.getKey();
		}

	}

	private static class DomainRangeIterator extends FilterIterator<Map.Entry<ATermAppl, Set<Set<ATermAppl>>>>
	{
		final ATermAppl _p;
		final boolean _isDomain;

		public DomainRangeIterator(final Map<ATermAppl, Set<Set<ATermAppl>>> map, final Role role, final boolean isDomain)
		{
			super(map.entrySet().iterator());
			this._p = role.getName();
			this._isDomain = isDomain;
		}

		@Override
		public boolean filter(final Map.Entry<ATermAppl, Set<Set<ATermAppl>>> entry)
		{
			final Set<Set<ATermAppl>> allExplanations = entry.getValue();

			final Set<ATermAppl> explanation = Collections.singleton(_isDomain ? ATermUtils.makeDomain(_p, entry.getKey()) : ATermUtils.makeRange(_p, entry.getKey()));
			return !allExplanations.contains(explanation);
		}
	}

	private final Map<ATermAppl, Role> _roles = new HashMap<>();
	private final Set<Role> reflexiveRoles = new HashSet<>();

	private final Map<Role, Map<ATermAppl, Set<Set<ATermAppl>>>> domainAssertions;
	private final Map<Role, Map<ATermAppl, Set<Set<ATermAppl>>>> rangeAssertions;

	private Taxonomy<ATermAppl> objectTaxonomy;
	private Taxonomy<ATermAppl> dataTaxonomy;
	private Taxonomy<ATermAppl> annotationTaxonomy;

	private final FSMBuilder fsmBuilder;

	public RBox()
	{
		domainAssertions = new HashMap<>();
		rangeAssertions = new HashMap<>();

		fsmBuilder = new FSMBuilder(this);

		addDatatypeRole(ATermUtils.TOP_DATA_PROPERTY);
		addDatatypeRole(ATermUtils.BOTTOM_DATA_PROPERTY);
		final Role topObjProp = addObjectRole(ATermUtils.TOP_OBJECT_PROPERTY);
		final Role bottomObjProp = addObjectRole(ATermUtils.BOTTOM_OBJECT_PROPERTY);

		topObjProp.setTransitive(true, DependencySet.INDEPENDENT);
		topObjProp.setReflexive(true, DependencySet.INDEPENDENT);

		bottomObjProp.setIrreflexive(true, DependencySet.INDEPENDENT);
		bottomObjProp.setAsymmetric(true, DependencySet.INDEPENDENT);

		addEquivalentRole(topObjProp.getName(), topObjProp.getInverse().getName(), DependencySet.INDEPENDENT);
		addEquivalentRole(bottomObjProp.getName(), bottomObjProp.getInverse().getName(), DependencySet.INDEPENDENT);

	}

	/**
	 * Return the role with the given name
	 *
	 * @param r Name (URI) of the role
	 * @return
	 */
	public Role getRole(final ATerm r)
	{
		return _roles.get(r);
	}

	/**
	 * Return the role with the given name and throw and exception if it is not found.
	 *
	 * @param r Name (URI) of the role
	 * @return
	 */
	public Role getDefinedRole(final ATerm r)
	{
		final Role role = _roles.get(r);

		if (role == null)
			throw new RuntimeException(r + " is not defined as a property");

		return role;
	}

	public Role addRole(final ATermAppl r)
	{
		Role role = getRole(r);

		if (role == null)
		{
			role = new Role(r, PropertyType.UNTYPED);
			_roles.put(r, role);
		}

		return role;
	}

	/**
	 * Add a non-asserted property range axiom
	 *
	 * @param p The property
	 * @param a A class expression for the domain
	 * @param clashExplanation A set of {@link ATermAppl}s that explain the range axiom.
	 * @return <code>true</code> if range add was successful, <code>false</code> else
	 * @throws IllegalArgumentException if <code>p</code> is not a defined property.
	 */
	public boolean addRange(final ATerm p, final ATermAppl range, final Set<ATermAppl> explanation)
	{
		final Role r = getRole(p);
		if (r == null)
			throw new IllegalArgumentException(p + " is not defined as a property");

		Map<ATermAppl, Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		if (ranges == null)
		{
			ranges = new HashMap<>();
			rangeAssertions.put(r, ranges);
		}

		Set<Set<ATermAppl>> allExplanations = ranges.get(range);
		if (allExplanations == null)
		{
			allExplanations = new HashSet<>();
			ranges.put(range, allExplanations);
		}

		return allExplanations.add(explanation);
	}

	/**
	 * Add an asserted property range axiom
	 *
	 * @param p The property
	 * @param a A class expression for the range
	 * @return <code>true</code> if range add was successful, <code>false</code> else
	 * @throws IllegalArgumentException if <code>p</code> is not a defined property.
	 */
	public boolean addRange(final ATerm p, final ATermAppl range)
	{
		final Set<ATermAppl> ds = Collections.singleton(ATermUtils.makeRange(p, range));

		return addRange(p, range, ds);
	}

	public Role addObjectRole(final ATermAppl r)
	{
		Role role = getRole(r);
		final PropertyType roleType = (role == null) ? PropertyType.UNTYPED : role.getType();

		switch (roleType)
		{
			case DATATYPE:
				role = null;
				break;
			case OBJECT:
				break;
			default:
				if (role == null)
				{
					role = new Role(r, PropertyType.OBJECT);
					_roles.put(r, role);
				}
				else
					role.setType(PropertyType.OBJECT);

				final ATermAppl invR = ATermUtils.makeInv(r);
				final Role invRole = new Role(invR, PropertyType.OBJECT);
				_roles.put(invR, invRole);

				role.setInverse(invRole);
				invRole.setInverse(role);

				addSubRole(ATermUtils.BOTTOM_OBJECT_PROPERTY, role.getName(), DependencySet.INDEPENDENT);
				addSubRole(role.getName(), ATermUtils.TOP_OBJECT_PROPERTY, DependencySet.INDEPENDENT);
				addSubRole(ATermUtils.BOTTOM_OBJECT_PROPERTY, role.getName(), DependencySet.INDEPENDENT);
				addSubRole(role.getName(), ATermUtils.TOP_OBJECT_PROPERTY, DependencySet.INDEPENDENT);

				break;
		}

		return role;
	}

	public Role addDatatypeRole(final ATermAppl r)
	{
		Role role = getRole(r);

		if (role == null)
		{
			role = new Role(r, PropertyType.DATATYPE);
			_roles.put(r, role);

			addSubRole(ATermUtils.BOTTOM_DATA_PROPERTY, role.getName(), DependencySet.INDEPENDENT);
			addSubRole(role.getName(), ATermUtils.TOP_DATA_PROPERTY, DependencySet.INDEPENDENT);
		}
		else
			switch (role.getType())
			{
				case DATATYPE:
					break;
				case OBJECT:
					role = null;
					break;
				default:
					role.setType(PropertyType.DATATYPE);
					addSubRole(ATermUtils.BOTTOM_DATA_PROPERTY, role.getName(), DependencySet.INDEPENDENT);
					addSubRole(role.getName(), ATermUtils.TOP_DATA_PROPERTY, DependencySet.INDEPENDENT);
					break;
			}

		return role;
	}

	public Role addAnnotationRole(final ATermAppl r)
	{
		Role role = getRole(r);

		if (role == null)
		{
			role = new Role(r, PropertyType.ANNOTATION);
			_roles.put(r, role);
		}
		else
			switch (role.getType())
			{
				case ANNOTATION:
					break;
				case OBJECT:
					role = null;
					break;
				default:
					role.setType(PropertyType.ANNOTATION);
					break;
			}

		return role;
	}

	@Deprecated
	public Role addOntologyRole(final ATermAppl r)
	{
		return addAnnotationRole(r);
	}

	public boolean addSubRole(final ATerm sub, final ATerm sup)
	{
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeSubProp(sub, sup)) : DependencySet.INDEPENDENT;
		return addSubRole(sub, sup, ds);
	}

	public boolean addSubRole(final ATerm sub, final ATerm sup, final DependencySet ds)
	{
		final Role roleSup = getRole(sup);
		final Role roleSub = getRole(sub);

		if (roleSup == null)
			return false;
		else
			if (sub.getType() == ATerm.LIST)
				roleSup.addSubRoleChain((ATermList) sub, ds);
			else
				if (roleSub == null)
					return false;
				else
				{
					roleSup.addSubRole(roleSub, ds);
					roleSub.addSuperRole(roleSup, ds);
				}

		// TODO Need to figure out what to do about about role lists
		// explanationTable.add(ATermUtils.makeSub(sub, sup), ds);
		return true;
	}

	public boolean addEquivalentRole(final ATerm s, final ATerm r)
	{
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeEqProp(s, r)) : DependencySet.INDEPENDENT;
		return addEquivalentRole(r, s, ds);
	}

	public boolean addEquivalentRole(final ATerm s, final ATerm r, final DependencySet ds)
	{
		final Role roleS = getRole(s);
		final Role roleR = getRole(r);

		if (roleS == null || roleR == null)
			return false;

		roleR.addSubRole(roleS, ds);
		roleR.addSuperRole(roleS, ds);
		roleS.addSubRole(roleR, ds);
		roleS.addSuperRole(roleR, ds);

		if (roleR.getInverse() != null)
		{
			roleR.getInverse().addSubRole(roleS.getInverse(), ds);
			roleR.getInverse().addSuperRole(roleS.getInverse(), ds);
			roleS.getInverse().addSubRole(roleR.getInverse(), ds);
			roleS.getInverse().addSuperRole(roleR.getInverse(), ds);
		}

		return true;
	}

	public boolean addDisjointRole(final ATerm s, final ATerm r, final DependencySet ds)
	{
		final Role roleS = getRole(s);
		final Role roleR = getRole(r);

		if (roleS == null || roleR == null)
			return false;

		roleR.addDisjointRole(roleS, ds);
		roleS.addDisjointRole(roleR, ds);

		return true;
	}

	/**
	 * Add a non-asserted property domain axiom
	 *
	 * @param p The property
	 * @param a A class expression for the domain
	 * @param explain A set of {@link ATermAppl}s that explain the domain axiom.
	 * @return <code>true</code> if domain add was successful, <code>false</code> else
	 * @throws IllegalArgumentException if <code>p</code> is not a defined property.
	 */
	public boolean addDomain(final ATerm p, final ATermAppl domain, final Set<ATermAppl> explanation)
	{
		final Role r = getRole(p);
		if (r == null)
			throw new IllegalArgumentException(p + " is not defined as a property");

		Map<ATermAppl, Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		if (domains == null)
		{
			domains = new HashMap<>();
			domainAssertions.put(r, domains);
		}

		Set<Set<ATermAppl>> allExplanations = domains.get(domain);
		if (allExplanations == null)
		{
			allExplanations = new HashSet<>();
			domains.put(domain, allExplanations);
		}

		return allExplanations.add(explanation);
	}

	/**
	 * Add an asserted property domain axiom
	 *
	 * @param p The property
	 * @param a A class expression for the domain
	 * @return <code>true</code> if domain add was successful, <code>false</code> else
	 * @throws IllegalArgumentException if <code>p</code> is not a defined property.
	 */
	public boolean addDomain(final ATerm p, final ATermAppl a)
	{
		final Set<ATermAppl> explain = Collections.singleton(ATermUtils.makeDomain(p, a));

		return addDomain(p, a, explain);
	}

	public boolean addInverseRole(final ATerm s, final ATerm r, final DependencySet ds)
	{
		final Role roleS = getRole(s);
		final Role roleR = getRole(r);

		if (roleS == null || roleR == null || !roleS.isObjectRole() || !roleR.isObjectRole())
			return false;
		else
			addEquivalentRole(roleS.getInverse().getName(), r, ds);

		return true;
	}

	public Iterator<ATermAppl> getAssertedDomains(final Role r)
	{
		final Map<ATermAppl, Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		return domains == null ? IteratorUtils.<ATermAppl> emptyIterator() : new ValueIterator(new DomainRangeIterator(domains, r, true));
	}

	public Iterator<ATermAppl> getAssertedRanges(final Role r)
	{
		final Map<ATermAppl, Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		return ranges == null ? IteratorUtils.<ATermAppl> emptyIterator() : new ValueIterator(new DomainRangeIterator(ranges, r, false));
	}

	@Deprecated
	public boolean isDomainAsserted(final ATerm p, final ATermAppl domain)
	{
		final Role r = getRole(p);
		if (r == null)
			throw new IllegalArgumentException(p + " is not defined as a property");

		final Map<ATermAppl, Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		if (domains == null)
			return false;

		final Set<Set<ATermAppl>> allExplanations = domains.get(domain);
		if (allExplanations == null)
			return false;

		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeDomain(p, domain));

		return allExplanations.contains(explanation);
	}

	@Deprecated
	public boolean isRangeAsserted(final ATerm p, final ATermAppl range)
	{
		final Role r = getRole(p);
		if (r == null)
			throw new IllegalArgumentException(p + " is not defined as a property");

		final Map<ATermAppl, Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		if (ranges == null)
			return false;

		final Set<Set<ATermAppl>> allExplanations = ranges.get(range);
		if (allExplanations == null)
			return false;

		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeRange(p, range));

		return allExplanations.contains(explanation);
	}

	/**
	 * check if the term is declared as a role
	 */
	public boolean isRole(final ATerm r)
	{
		return _roles.containsKey(r);
	}

	public void prepare()
	{

		// first pass - compute sub _roles
		final Set<Role> complexRoles = new HashSet<>();
		for (final Role role : _roles.values())
		{
			final Map<ATerm, DependencySet> subExplain = new HashMap<>();
			final Set<Role> subRoles = new HashSet<>();
			final Set<ATermList> subRoleChains = new HashSet<>();

			computeSubRoles(role, subRoles, subRoleChains, subExplain, DependencySet.INDEPENDENT);

			role.setSubRolesAndChains(subRoles, subRoleChains, subExplain);

			for (final Role s : subRoles)
			{
				final DependencySet explainSub = role.getExplainSub(s.getName());
				s.addSuperRole(role, explainSub);
			}

			for (final ATermList chain : subRoleChains)
				if (chain.getLength() != 2 || !chain.getFirst().equals(chain.getLast()) || !subRoles.contains(getRole(chain.getFirst())))
				{
					role.setHasComplexSubRole(true);
					complexRoles.add(role);
					break;
				}
		}

		// iterate over complex _roles to build DFAs - needs to be done after
		// all subRoles are propagated above
		for (final Role s : complexRoles)
			fsmBuilder.build(s);

		// second pass - set super _roles and propagate disjoint _roles through inverses
		for (final Role role : _roles.values())
		{
			final Role invR = role.getInverse();
			if (invR != null)
			{
				if (invR.isTransitive() && !role.isTransitive())
					role.setTransitive(true, invR.getExplainTransitive());
				else
					if (role.isTransitive() && !invR.isTransitive())
						invR.setTransitive(true, role.getExplainTransitive());
				if (invR.isFunctional() && !role.isInverseFunctional())
					role.setInverseFunctional(true, invR.getExplainFunctional());
				if (role.isFunctional() && !invR.isInverseFunctional())
					invR.setInverseFunctional(true, role.getExplainFunctional());
				if (invR.isInverseFunctional() && !role.isFunctional())
					role.setFunctional(true, invR.getExplainInverseFunctional());
				if (invR.isAsymmetric() && !role.isAsymmetric())
					role.setAsymmetric(true, invR.getExplainAsymmetric());
				if (role.isAsymmetric() && !invR.isAsymmetric())
					invR.setAsymmetric(true, role.getExplainAsymmetric());
				if (invR.isReflexive() && !role.isReflexive())
					role.setReflexive(true, invR.getExplainReflexive());
				if (role.isReflexive() && !invR.isReflexive())
					invR.setReflexive(true, role.getExplainReflexive());

				for (final Role disjointR : role.getDisjointRoles())
					invR.addDisjointRole(disjointR.getInverse(), role.getExplainDisjointRole(disjointR));
			}

			for (final Role s : role.getSubRoles())
			{
				if (role.isForceSimple())
					s.setForceSimple(true);
				if (!s.isSimple())
					role.setSimple(false);
			}
		}

		// third pass - set transitivity and functionality and propagate disjoint _roles through subs
		for (final Role r : _roles.values())
		{
			if (r.isForceSimple())
			{
				if (!r.isSimple())
					ignoreTransitivity(r);
			}
			else
			{
				boolean isTransitive = r.isTransitive();
				DependencySet transitiveDS = r.getExplainTransitive();
				for (final Role s : r.getSubRoles())
					if (s.isTransitive())
					{
						if (r.isSubRoleOf(s) && (r != s))
						{
							isTransitive = true;
							transitiveDS = r.getExplainSub(s.getName()).union(s.getExplainTransitive(), true);
						}
						r.addTransitiveSubRole(s);
					}
				if (isTransitive != r.isTransitive())
					r.setTransitive(isTransitive, transitiveDS);
			}

			if (r.isFunctional())
				r.addFunctionalSuper(r);

			for (final Role s : r.getSuperRoles())
			{
				if (s.equals(r))
					continue;

				final DependencySet supDS = PelletOptions.USE_TRACING ? r.getExplainSuper(s.getName()) : DependencySet.INDEPENDENT;

				if (s.isFunctional())
				{
					final DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainFunctional(), true) : DependencySet.INDEPENDENT;
					r.setFunctional(true, ds);
					r.addFunctionalSuper(s);
				}
				if (s.isIrreflexive() && !r.isIrreflexive())
				{
					final DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainIrreflexive(), true) : DependencySet.INDEPENDENT;
					r.setIrreflexive(true, ds);
				}
				if (s.isAsymmetric() && !r.isAsymmetric())
				{
					final DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainAsymmetric(), true) : DependencySet.INDEPENDENT;
					r.setAsymmetric(true, ds);
				}

				// create a duplicate array to avoid ConcurrentModificationException
				for (final Role disjointR : s.getDisjointRoles().toArray(new Role[0]))
				{
					final DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainDisjointRole(disjointR), true) : DependencySet.INDEPENDENT;
					r.addDisjointRole(disjointR, ds);
					disjointR.addDisjointRole(r, ds);
				}
			}

			if (r.isReflexive() && !r.isAnon())
				reflexiveRoles.add(r);

			if (_logger.isLoggable(Level.FINE))
				_logger.fine(r.debugString());
		}

		// we will compute the taxonomy when we need it
		objectTaxonomy = null;
		dataTaxonomy = null;
		annotationTaxonomy = null;
	}

	public void propagateDomainRange()
	{
		for (final Role role : _roles.values())
			role.resetDomainRange();

		for (final Role role : _roles.values())
		{
			final Role invRole = role.getInverse();
			if (invRole != null)
			{
				final Map<ATermAppl, Set<Set<ATermAppl>>> invDomains = domainAssertions.get(invRole);
				final Map<ATermAppl, Set<Set<ATermAppl>>> invRanges = rangeAssertions.get(invRole);

				propogateDomain(role, invRanges);
				propogateRange(role, invDomains);
			}

			final Map<ATermAppl, Set<Set<ATermAppl>>> domains = domainAssertions.get(role);
			final Map<ATermAppl, Set<Set<ATermAppl>>> ranges = rangeAssertions.get(role);
			propogateDomain(role, domains);
			propogateRange(role, ranges);
		}
	}

	private void propogateDomain(final Role role, final Map<ATermAppl, Set<Set<ATermAppl>>> domains)
	{
		if (domains == null || domains.isEmpty())
			return;
		for (final Map.Entry<ATermAppl, Set<Set<ATermAppl>>> e : domains.entrySet())
		{
			final Set<ATermAppl> explanation = e.getValue().iterator().next();
			final ATermAppl domain = e.getKey();
			final ATermAppl normalized = ATermUtils.normalize(domain);

			for (final Role s : role.getSubRoles())
			{
				final DependencySet explainSub = role.getExplainSub(s.getName());
				final DependencySet ds = explainSub.union(explanation, true);

				s.addDomain(normalized, ds);
			}
		}
	}

	private void propogateRange(final Role role, final Map<ATermAppl, Set<Set<ATermAppl>>> ranges)
	{
		if (ranges == null || ranges.isEmpty())
			return;
		for (final Map.Entry<ATermAppl, Set<Set<ATermAppl>>> e : ranges.entrySet())
		{
			final Set<ATermAppl> explanation = e.getValue().iterator().next();
			final ATermAppl range = e.getKey();
			final ATermAppl normalized = ATermUtils.normalize(range);

			for (final Role s : role.getSubRoles())
			{
				final DependencySet explainSub = role.getExplainSub(s.getName());
				final DependencySet ds = explainSub.union(explanation, true);

				s.addRange(normalized, ds);
			}
		}
	}

	public boolean removeDomain(final ATerm p, final ATermAppl domain)
	{
		if (!PelletOptions.USE_TRACING)
			return false;

		final Role r = getRole(p);
		if (r == null)
			return false;

		final Map<ATermAppl, Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		if (domains == null)
			return false;

		final Set<Set<ATermAppl>> allExplanations = domains.get(domain);
		if (allExplanations == null)
			return false;

		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeDomain(p, domain));

		if (!allExplanations.remove(explanation))
			return false;

		if (allExplanations.isEmpty())
			domains.remove(domain);

		return true;
	}

	public boolean removeRange(final ATerm p, final ATermAppl range)
	{
		if (!PelletOptions.USE_TRACING)
			return false;

		final Role r = getRole(p);
		if (r == null)
			return false;

		final Map<ATermAppl, Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		if (ranges == null)
			return false;

		final Set<Set<ATermAppl>> allExplanations = ranges.get(range);
		if (allExplanations == null)
			return false;

		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeRange(p, range));

		if (!allExplanations.remove(explanation))
			return false;

		if (allExplanations.isEmpty())
			ranges.remove(range);

		return true;
	}

	void ignoreTransitivity(final Role role)
	{
		final Role namedRole = role.isAnon() ? role.getInverse() : role;

		final String msg = "Unsupported axiom: Ignoring transitivity and/or complex subproperty axioms for " + namedRole;

		if (!PelletOptions.IGNORE_UNSUPPORTED_AXIOMS)
			throw new UnsupportedFeatureException(msg);

		_logger.warning(msg);

		role.removeSubRoleChains();
		role.setHasComplexSubRole(false);
		role.setSimple(true);
		role.setFSM(null);

		role.getInverse().removeSubRoleChains();
		role.getInverse().setHasComplexSubRole(false);
		role.getInverse().setSimple(true);
		role.getInverse().setFSM(null);
	}

	private void computeImmediateSubRoles(final Role r, final Map<ATerm, DependencySet> subs)
	{

		final Role invR = r.getInverse();
		if (invR != null && invR != r)
		{

			for (final Role invSubR : invR.getSubRoles())
			{
				final Role subR = invSubR.getInverse();
				if (subR == null)
				{
					if (_logger.isLoggable(Level.FINE))
						_logger.fine("Property " + invSubR + " was supposed to be an ObjectProperty but it is not!");
				}
				else
					if (subR != r)
					{
						// System.out.println("expsub:
						// "+invR.getExplainSub(invSubR.getName()));
						// System.out.println("expinv:
						// "+invSubR.getExplainInverse());
						final DependencySet subDS = invR.getExplainSub(invSubR.getName());
						subs.put(subR.getName(), subDS);
					}
			}
			for (final ATermList roleChain : invR.getSubRoleChains())
			{
				final DependencySet subDS = invR.getExplainSub(roleChain);

				final ATermList subChain = inverse(roleChain);
				subs.put(subChain, subDS);
			}
		}

		for (final Role sub : r.getSubRoles())
		{
			final DependencySet subDS = r.getExplainSub(sub.getName());

			subs.put(sub.getName(), subDS);
		}

		for (final ATermList subChain : r.getSubRoleChains())
		{
			final DependencySet subDS = r.getExplainSub(subChain);

			subs.put(subChain, subDS);
		}

	}

	private void computeSubRoles(final Role r, final Set<Role> subRoles, final Set<ATermList> subRoleChains, final Map<ATerm, DependencySet> dependencies, final DependencySet ds)
	{
		// check for loops
		if (subRoles.contains(r))
			return;

		// reflexive
		subRoles.add(r);
		dependencies.put(r.getName(), ds);

		// transitive closure
		final Map<ATerm, DependencySet> immSubs = new HashMap<>();
		computeImmediateSubRoles(r, immSubs);
		for (final Entry<ATerm, DependencySet> entry : immSubs.entrySet())
		{
			final ATerm sub = entry.getKey();
			final DependencySet subDS = PelletOptions.USE_TRACING ? ds.union(entry.getValue(), true) : DependencySet.INDEPENDENT;
			if (sub instanceof ATermAppl)
			{
				final Role subRole = getRole(sub);

				computeSubRoles(subRole, subRoles, subRoleChains, dependencies, subDS);
			}
			else
			{
				subRoleChains.add((ATermList) sub);
				dependencies.put(sub, subDS);
			}
		}
	}

	/**
	 * Returns a string representation of the RBox where for each role subroles, superroles, and isTransitive information is given
	 */
	@Override
	public String toString()
	{
		return "[RBox " + _roles.values() + "]";
	}

	/**
	 * for each role in the list finds an inverse role and returns the new list.
	 */
	public ATermList inverse(final ATermList roles)
	{
		ATermList invList = ATermUtils.EMPTY_LIST;

		for (ATermList list = roles; !list.isEmpty(); list = list.getNext())
		{
			final ATermAppl r = (ATermAppl) list.getFirst();
			final Role role = getRole(r);
			final Role invR = role.getInverse();
			if (invR == null)
				System.err.println("Property " + r + " was supposed to be an ObjectProperty but it is not!");
			else
				invList = invList.insert(invR.getName());
		}

		return invList;
	}

	/**
	 * @return Returns the _roles.
	 */
	public Set<ATermAppl> getRoleNames()
	{
		return _roles.keySet();
	}

	public Set<Role> getReflexiveRoles()
	{
		return reflexiveRoles;
	}

	/**
	 * getRoles
	 *
	 * @return
	 */
	public Collection<Role> getRoles()
	{
		return _roles.values();
	}

	public Taxonomy<ATermAppl> getObjectTaxonomy()
	{
		if (objectTaxonomy == null)
		{
			final RoleTaxonomyBuilder builder = new RoleTaxonomyBuilder(this, PropertyType.OBJECT);
			objectTaxonomy = builder.classify();
		}
		return objectTaxonomy;
	}

	public Taxonomy<ATermAppl> getDataTaxonomy()
	{
		if (dataTaxonomy == null)
		{
			final RoleTaxonomyBuilder builder = new RoleTaxonomyBuilder(this, PropertyType.DATATYPE);
			dataTaxonomy = builder.classify();
		}
		return dataTaxonomy;
	}

	public Taxonomy<ATermAppl> getAnnotationTaxonomy()
	{
		if (annotationTaxonomy == null)
		{
			final RoleTaxonomyBuilder builder = new RoleTaxonomyBuilder(this, PropertyType.ANNOTATION);
			if (PelletOptions.USE_ANNOTATION_SUPPORT)
				annotationTaxonomy = builder.classify();
		}
		return annotationTaxonomy;
	}

	public boolean isObjectTaxonomyPrepared()
	{
		return objectTaxonomy != null;
	}

	public boolean isDataTaxonomyPrepared()
	{
		return dataTaxonomy != null;
	}

	public boolean isAnnotationTaxonomyPrepared()
	{
		return annotationTaxonomy != null;
	}
}
