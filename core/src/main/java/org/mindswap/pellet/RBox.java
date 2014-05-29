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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.FilterIterator;
import org.mindswap.pellet.utils.iterator.IteratorUtils;
import org.mindswap.pellet.utils.iterator.MapIterator;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class RBox {
	public static Logger log = Logger.getLogger(RBox.class.getName());
	
	private static class ValueIterator extends MapIterator<Map.Entry<ATermAppl, Set<Set<ATermAppl>>>, ATermAppl> {
        public ValueIterator(Iterator<Entry<ATermAppl, Set<Set<ATermAppl>>>> iterator) {
	        super(iterator);
        }

		@Override
        public ATermAppl map(Entry<ATermAppl, Set<Set<ATermAppl>>> e) {
	        return e.getKey();
        }
		
	}
	
	private static class DomainRangeIterator extends FilterIterator<Map.Entry<ATermAppl, Set<Set<ATermAppl>>>> {
		final ATermAppl p;
		final boolean isDomain;
		
        public DomainRangeIterator(Map<ATermAppl, Set<Set<ATermAppl>>> map, Role role, boolean isDomain) {
	        super(map.entrySet().iterator());
	        this.p = role.getName();
	        this.isDomain = isDomain;
        }

		@Override
        public boolean filter(Map.Entry<ATermAppl, Set<Set<ATermAppl>>> entry) {
			final Set<Set<ATermAppl>> allExplanations = entry.getValue();

			final Set<ATermAppl> explanation = Collections.singleton(isDomain ? ATermUtils
			                .makeDomain(p, entry.getKey()) : ATermUtils.makeRange(p, entry.getKey()));
			return !allExplanations.contains(explanation);
        }
	}

	private final Map<ATermAppl, Role> roles = new HashMap<ATermAppl, Role>();
	private final Set<Role> reflexiveRoles = new HashSet<Role>();
	
	private final Map<Role,Map<ATermAppl,Set<Set<ATermAppl>>>> domainAssertions;
	private final Map<Role,Map<ATermAppl,Set<Set<ATermAppl>>>> rangeAssertions;
	
	private Taxonomy<ATermAppl> objectTaxonomy;
	private Taxonomy<ATermAppl> dataTaxonomy;
	private Taxonomy<ATermAppl> annotationTaxonomy;

	private final FSMBuilder fsmBuilder;

	public RBox() {
		domainAssertions = new HashMap<Role, Map<ATermAppl,Set<Set<ATermAppl>>>>();
		rangeAssertions = new HashMap<Role, Map<ATermAppl,Set<Set<ATermAppl>>>>();

		fsmBuilder = new FSMBuilder(this);

		addDatatypeRole(ATermUtils.TOP_DATA_PROPERTY);
		addDatatypeRole(ATermUtils.BOTTOM_DATA_PROPERTY);
		Role topObjProp = addObjectRole(ATermUtils.TOP_OBJECT_PROPERTY);
		Role bottomObjProp = addObjectRole(ATermUtils.BOTTOM_OBJECT_PROPERTY);

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
	 * @param r
	 *            Name (URI) of the role
	 * @return
	 */
	public Role getRole(ATerm r) {
		return roles.get(r);
	}

	/**
	 * Return the role with the given name and throw and exception if it is not found.
	 * 
	 * @param r
	 *            Name (URI) of the role
	 * @return
	 */
	public Role getDefinedRole(ATerm r) {
		Role role = roles.get(r);

		if (role == null) {
			throw new RuntimeException(r + " is not defined as a property");
		}

		return role;
	}

	public Role addRole(ATermAppl r) {
		Role role = getRole(r);

		if (role == null) {
			role = new Role(r, PropertyType.UNTYPED);
			roles.put(r, role);
		}

		return role;
	}

	/**
	 * Add a non-asserted property range axiom
	 * 
	 * @param p
	 *            The property
	 * @param a
	 *            A class expression for the domain
	 * @param explanation
	 *            A set of {@link ATermAppl}s that explain the range axiom.
	 * @return <code>true</code> if range add was successful, <code>false</code> else
	 * @throws IllegalArgumentException
	 *             if <code>p</code> is not a defined property.
	 */
	public boolean addRange(ATerm p, ATermAppl range, Set<ATermAppl> explanation) {
		final Role r = getRole(p);
		if (r == null) {
			throw new IllegalArgumentException(p + " is not defined as a property");
		}
		
		Map<ATermAppl,Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		if( ranges == null) {
			ranges = new HashMap<ATermAppl, Set<Set<ATermAppl>>>();
			rangeAssertions.put(r, ranges);
		}
		
		Set<Set<ATermAppl>> allExplanations = ranges.get(range);		
		if (allExplanations == null ) {
			allExplanations = new HashSet<Set<ATermAppl>>();
			ranges.put(range, allExplanations);
		}
		
		return allExplanations.add(explanation);
	}

	/**
	 * Add an asserted property range axiom
	 * 
	 * @param p
	 *            The property
	 * @param a
	 *            A class expression for the range
	 * @return <code>true</code> if range add was successful, <code>false</code> else
	 * @throws IllegalArgumentException
	 *             if <code>p</code> is not a defined property.
	 */
	public boolean addRange(ATerm p, ATermAppl range) {
		final Set<ATermAppl> ds = Collections.singleton(ATermUtils.makeRange(p, range));

		return addRange(p, range, ds);
	}

	public Role addObjectRole(ATermAppl r) {
		Role role = getRole(r);
		PropertyType roleType = (role == null) ? PropertyType.UNTYPED : role.getType();

		switch (roleType) {
			case DATATYPE:
				role = null;
				break;
			case OBJECT:
				break;
			default:
				if (role == null) {
					role = new Role(r, PropertyType.OBJECT);
					roles.put(r, role);
				}
				else {
					role.setType(PropertyType.OBJECT);
				}

				ATermAppl invR = ATermUtils.makeInv(r);
				Role invRole = new Role(invR, PropertyType.OBJECT);
				roles.put(invR, invRole);

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

	public Role addDatatypeRole(ATermAppl r) {
		Role role = getRole(r);

		if (role == null) {
			role = new Role(r, PropertyType.DATATYPE);
			roles.put(r, role);

			addSubRole(ATermUtils.BOTTOM_DATA_PROPERTY, role.getName(), DependencySet.INDEPENDENT);
			addSubRole(role.getName(), ATermUtils.TOP_DATA_PROPERTY, DependencySet.INDEPENDENT);
		}
		else {
			switch (role.getType()) {
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
		}

		return role;
	}

	public Role addAnnotationRole(ATermAppl r) {
		Role role = getRole(r);

		if (role == null) {
			role = new Role(r, PropertyType.ANNOTATION);
			roles.put(r, role);
		}
		else {
			switch (role.getType()) {
				case ANNOTATION:
					break;
				case OBJECT:
					role = null;
					break;
				default:
					role.setType(PropertyType.ANNOTATION);
					break;
			}
		}

		return role;
	}

	@Deprecated
	public Role addOntologyRole(ATermAppl r) {
		return addAnnotationRole(r);
	}

	public boolean addSubRole(ATerm sub, ATerm sup) {
		DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeSubProp(sub, sup))
		                : DependencySet.INDEPENDENT;
		return addSubRole(sub, sup, ds);
	}

	public boolean addSubRole(ATerm sub, ATerm sup, DependencySet ds) {
		Role roleSup = getRole(sup);
		Role roleSub = getRole(sub);

		if (roleSup == null) {
			return false;
		}
		else if (sub.getType() == ATerm.LIST) {
			roleSup.addSubRoleChain((ATermList) sub, ds);
		}
		else if (roleSub == null) {
			return false;
		}
		else {
			roleSup.addSubRole(roleSub, ds);
			roleSub.addSuperRole(roleSup, ds);
		}

		// TODO Need to figure out what to do about about role lists
		// explanationTable.add(ATermUtils.makeSub(sub, sup), ds);
		return true;
	}

	public boolean addEquivalentRole(ATerm s, ATerm r) {
		DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeEqProp(s, r))
		                : DependencySet.INDEPENDENT;
		return addEquivalentRole(r, s, ds);
	}

	public boolean addEquivalentRole(ATerm s, ATerm r, DependencySet ds) {
		Role roleS = getRole(s);
		Role roleR = getRole(r);

		if (roleS == null || roleR == null) {
			return false;
		}

		roleR.addSubRole(roleS, ds);
		roleR.addSuperRole(roleS, ds);
		roleS.addSubRole(roleR, ds);
		roleS.addSuperRole(roleR, ds);

		if (roleR.getInverse() != null) {
			roleR.getInverse().addSubRole(roleS.getInverse(), ds);
			roleR.getInverse().addSuperRole(roleS.getInverse(), ds);
			roleS.getInverse().addSubRole(roleR.getInverse(), ds);
			roleS.getInverse().addSuperRole(roleR.getInverse(), ds);
		}

		return true;
	}

	public boolean addDisjointRole(ATerm s, ATerm r, DependencySet ds) {
		Role roleS = getRole(s);
		Role roleR = getRole(r);

		if (roleS == null || roleR == null) {
			return false;
		}

		roleR.addDisjointRole(roleS, ds);
		roleS.addDisjointRole(roleR, ds);

		return true;
	}

	/**
	 * Add a non-asserted property domain axiom
	 * 
	 * @param p
	 *            The property
	 * @param a
	 *            A class expression for the domain
	 * @param explain
	 *            A set of {@link ATermAppl}s that explain the domain axiom.
	 * @return <code>true</code> if domain add was successful, <code>false</code> else
	 * @throws IllegalArgumentException
	 *             if <code>p</code> is not a defined property.
	 */
	public boolean addDomain(ATerm p, ATermAppl domain, Set<ATermAppl> explanation) {
		final Role r = getRole(p);
		if (r == null) {
			throw new IllegalArgumentException(p + " is not defined as a property");
		}
		
		Map<ATermAppl,Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		if( domains == null) {
			domains = new HashMap<ATermAppl, Set<Set<ATermAppl>>>();
			domainAssertions.put(r, domains);
		}
		
		Set<Set<ATermAppl>> allExplanations = domains.get(domain);		
		if (allExplanations == null ) {
			allExplanations = new HashSet<Set<ATermAppl>>();
			domains.put(domain, allExplanations);
		}
		
		return allExplanations.add(explanation);	
	}

	/**
	 * Add an asserted property domain axiom
	 * 
	 * @param p
	 *            The property
	 * @param a
	 *            A class expression for the domain
	 * @return <code>true</code> if domain add was successful, <code>false</code> else
	 * @throws IllegalArgumentException
	 *             if <code>p</code> is not a defined property.
	 */
	public boolean addDomain(ATerm p, ATermAppl a) {
		final Set<ATermAppl> explain = Collections.singleton(ATermUtils.makeDomain(p, a));

		return addDomain(p, a, explain);
	}

	public boolean addInverseRole(ATerm s, ATerm r, DependencySet ds) {
		Role roleS = getRole(s);
		Role roleR = getRole(r);

		if (roleS == null || roleR == null || !roleS.isObjectRole() || !roleR.isObjectRole()) {
			return false;
		}
		else {
			addEquivalentRole(roleS.getInverse().getName(), r, ds);
		}

		return true;
	}
	
	public Iterator<ATermAppl> getAssertedDomains(Role r) {
		final Map<ATermAppl, Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		return domains == null ? IteratorUtils.<ATermAppl> emptyIterator() : new ValueIterator(new DomainRangeIterator(
		                domains, r, true));
	}
	
	public Iterator<ATermAppl> getAssertedRanges(Role r) {
		final Map<ATermAppl, Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		return ranges == null ? IteratorUtils.<ATermAppl> emptyIterator() : new ValueIterator(new DomainRangeIterator(
		                ranges, r, false));
	}

	@Deprecated
	public boolean isDomainAsserted(ATerm p, ATermAppl domain) {
		final Role r = getRole(p);
		if (r == null) {
			throw new IllegalArgumentException(p + " is not defined as a property");
		}
			
		final Map<ATermAppl,Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		if( domains == null) {
			return false;
		}
		
		final Set<Set<ATermAppl>> allExplanations = domains.get(domain);		
		if (allExplanations == null ) {
			return false;
		}
		
		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeDomain(p, domain));
		
		return allExplanations.contains(explanation);
	}

	@Deprecated
	public boolean isRangeAsserted(ATerm p, ATermAppl range) {
		final Role r = getRole(p);
		if (r == null) {
			throw new IllegalArgumentException(p + " is not defined as a property");
		}
			
		final Map<ATermAppl,Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		if( ranges == null) {
			return false;
		}
		
		final Set<Set<ATermAppl>> allExplanations = ranges.get(range);		
		if (allExplanations == null ) {
			return false;
		}
		
		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeRange(p, range));
		
		return allExplanations.contains(explanation);
	}

	/**
	 * check if the term is declared as a role
	 */
	public boolean isRole(ATerm r) {
		return roles.containsKey(r);
	}

	public void prepare() {

		// first pass - compute sub roles
		Set<Role> complexRoles = new HashSet<Role>();
		for (Role role : roles.values()) {
			Map<ATerm, DependencySet> subExplain = new HashMap<ATerm, DependencySet>();
			Set<Role> subRoles = new HashSet<Role>();
			Set<ATermList> subRoleChains = new HashSet<ATermList>();

			computeSubRoles(role, subRoles, subRoleChains, subExplain, DependencySet.INDEPENDENT);

			role.setSubRolesAndChains(subRoles, subRoleChains, subExplain);

			for (Role s : subRoles) {
				DependencySet explainSub = role.getExplainSub(s.getName());
				s.addSuperRole(role, explainSub);
			}

			for (ATermList chain : subRoleChains) {
				if (chain.getLength() != 2
				    || !chain.getFirst().equals(chain.getLast())
				    || !subRoles.contains(getRole(chain.getFirst()))) {
					role.setHasComplexSubRole(true);
					complexRoles.add(role);
					break;
				}
			}
		}

		// iterate over complex roles to build DFAs - needs to be done after
		// all subRoles are propagated above
		for (Role s : complexRoles) {
			fsmBuilder.build(s);
		}

		// second pass - set super roles and propagate disjoint roles through inverses
		for (Role role : roles.values()) {
			Role invR = role.getInverse();
			if (invR != null) {
				if (invR.isTransitive() && !role.isTransitive()) {
					role.setTransitive(true, invR.getExplainTransitive());
				}
				else if (role.isTransitive() && !invR.isTransitive()) {
					invR.setTransitive(true, role.getExplainTransitive());
				}
				if (invR.isFunctional() && !role.isInverseFunctional()) {
					role.setInverseFunctional(true, invR.getExplainFunctional());
				}
				if (role.isFunctional() && !invR.isInverseFunctional()) {
					invR.setInverseFunctional(true, role.getExplainFunctional());
				}
				if (invR.isInverseFunctional() && !role.isFunctional()) {
					role.setFunctional(true, invR.getExplainInverseFunctional());
				}
				if (invR.isAsymmetric() && !role.isAsymmetric()) {
					role.setAsymmetric(true, invR.getExplainAsymmetric());
				}
				if (role.isAsymmetric() && !invR.isAsymmetric()) {
					invR.setAsymmetric(true, role.getExplainAsymmetric());
				}
				if (invR.isReflexive() && !role.isReflexive()) {
					role.setReflexive(true, invR.getExplainReflexive());
				}
				if (role.isReflexive() && !invR.isReflexive()) {
					invR.setReflexive(true, role.getExplainReflexive());
				}

				for (Role disjointR : role.getDisjointRoles()) {
					invR.addDisjointRole(disjointR.getInverse(), role.getExplainDisjointRole(disjointR));
				}
			}

			for (Role s : role.getSubRoles()) {
				if (role.isForceSimple()) {
					s.setForceSimple(true);
				}
				if (!s.isSimple()) {
					role.setSimple(false);
				}
			}
		}

		// third pass - set transitivity and functionality and propagate disjoint roles through subs
		for (Role r : roles.values()) {
			if (r.isForceSimple()) {
				if (!r.isSimple()) {
					ignoreTransitivity(r);
				}
			}
			else {
				boolean isTransitive = r.isTransitive();
				DependencySet transitiveDS = r.getExplainTransitive();
				for (Role s : r.getSubRoles()) {
					if (s.isTransitive()) {
						if (r.isSubRoleOf(s) && (r != s)) {
							isTransitive = true;
							transitiveDS = r.getExplainSub(s.getName()).union(s.getExplainTransitive(), true);
						}
						r.addTransitiveSubRole(s);
					}
				}
				if (isTransitive != r.isTransitive()) {
					r.setTransitive(isTransitive, transitiveDS);
				}
			}

			if (r.isFunctional()) {
				r.addFunctionalSuper(r);
			}

			for (Role s : r.getSuperRoles()) {
				if (s.equals(r)) {
					continue;
				}

				DependencySet supDS = PelletOptions.USE_TRACING ? r.getExplainSuper(s.getName())
				                : DependencySet.INDEPENDENT;

				if (s.isFunctional()) {
					DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainFunctional(), true)
					                : DependencySet.INDEPENDENT;
					r.setFunctional(true, ds);
					r.addFunctionalSuper(s);
				}
				if (s.isIrreflexive() && !r.isIrreflexive()) {
					DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainIrreflexive(), true)
					                : DependencySet.INDEPENDENT;
					r.setIrreflexive(true, ds);
				}
				if (s.isAsymmetric() && !r.isAsymmetric()) {
					DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainAsymmetric(), true)
					                : DependencySet.INDEPENDENT;
					r.setAsymmetric(true, ds);
				}

				// create a duplicate array to avoid ConcurrentModificationException
				for (Role disjointR : s.getDisjointRoles().toArray(new Role[0])) {
					DependencySet ds = PelletOptions.USE_TRACING ? supDS.union(s.getExplainDisjointRole(disjointR),
					                true) : DependencySet.INDEPENDENT;
					r.addDisjointRole(disjointR, ds);
					disjointR.addDisjointRole(r, ds);
				}
			}

			if (r.isReflexive() && !r.isAnon()) {
				reflexiveRoles.add(r);
			}

			if (log.isLoggable(Level.FINE)) {
				log.fine(r.debugString());
			}
		}

		// we will compute the taxonomy when we need it
		objectTaxonomy = null;
		dataTaxonomy = null;
		annotationTaxonomy = null;
	}

	public void propagateDomainRange() {
		for (Role role : roles.values()) {
			role.resetDomainRange();
		}
		
		for (Role role : roles.values()) {			
			Role invRole = role.getInverse();
			if (invRole != null) {
				Map<ATermAppl,Set<Set<ATermAppl>>> invDomains = domainAssertions.get(invRole);
				Map<ATermAppl,Set<Set<ATermAppl>>> invRanges = rangeAssertions.get(invRole);
				
				propogateDomain(role, invRanges);	
				propogateRange(role, invDomains);
			}
			
			Map<ATermAppl,Set<Set<ATermAppl>>> domains = domainAssertions.get(role);
			Map<ATermAppl,Set<Set<ATermAppl>>> ranges = rangeAssertions.get(role);
			propogateDomain(role, domains);
			propogateRange(role, ranges);		
		}
	}
	
	private void propogateDomain(Role role, Map<ATermAppl,Set<Set<ATermAppl>>> domains) {
		if (domains == null || domains.isEmpty())
			return;
		for (Map.Entry<ATermAppl,Set<Set<ATermAppl>>> e : domains.entrySet()) {
			Set<ATermAppl> explanation = e.getValue().iterator().next();
			ATermAppl domain = e.getKey();
			ATermAppl normalized = ATermUtils.normalize(domain);
			
			for (Role s : role.getSubRoles()) {
				DependencySet explainSub = role.getExplainSub(s.getName());	
				DependencySet ds = explainSub.union(explanation, true);				
				
				s.addDomain(normalized, ds);
			}
        }
	}
	
	private void propogateRange(Role role, Map<ATermAppl,Set<Set<ATermAppl>>> ranges) {
		if (ranges == null || ranges.isEmpty())
			return;
		for (Map.Entry<ATermAppl,Set<Set<ATermAppl>>> e : ranges.entrySet()) {
			Set<ATermAppl> explanation = e.getValue().iterator().next();
			ATermAppl range = e.getKey();
			ATermAppl normalized = ATermUtils.normalize(range);
			
			for (Role s : role.getSubRoles()) {
				DependencySet explainSub = role.getExplainSub(s.getName());	
				DependencySet ds = explainSub.union(explanation, true);
				
				s.addRange(normalized, ds);
			}
        }
	}

	public boolean removeDomain(ATerm p, ATermAppl domain) {
		if (!PelletOptions.USE_TRACING) {
			return false;
		}

		final Role r = getRole(p);
		if (r == null) {
			return false;
		}

		final Map<ATermAppl,Set<Set<ATermAppl>>> domains = domainAssertions.get(r);
		if( domains == null) {
			return false;
		}
		
		final Set<Set<ATermAppl>> allExplanations = domains.get(domain);		
		if (allExplanations == null ) {
			return false;
		}
		
		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeDomain(p, domain));
		
		if (!allExplanations.remove(explanation)) {
			return false;
		}
		
		if (allExplanations.isEmpty()) {
			domains.remove(domain);
		}
		
		return true;
	}

	public boolean removeRange(ATerm p, ATermAppl range) {
		if (!PelletOptions.USE_TRACING) {
			return false;
		}

		final Role r = getRole(p);
		if (r == null) {
			return false;
		}

		final Map<ATermAppl,Set<Set<ATermAppl>>> ranges = rangeAssertions.get(r);
		if( ranges == null) {
			return false;
		}
		
		final Set<Set<ATermAppl>> allExplanations = ranges.get(range);		
		if (allExplanations == null ) {
			return false;
		}
		
		final Set<ATermAppl> explanation = Collections.singleton(ATermUtils.makeRange(p, range));
		
		if (!allExplanations.remove(explanation)) {
			return false;
		}
		
		if (allExplanations.isEmpty()) {
			ranges.remove(range);
		}
		
		return true;
	}

	void ignoreTransitivity(Role role) {
		Role namedRole = role.isAnon() ? role.getInverse() : role;

		String msg = "Unsupported axiom: Ignoring transitivity and/or complex subproperty axioms for " + namedRole;

		if (!PelletOptions.IGNORE_UNSUPPORTED_AXIOMS) {
			throw new UnsupportedFeatureException(msg);
		}

		log.warning(msg);

		role.removeSubRoleChains();
		role.setHasComplexSubRole(false);
		role.setSimple(true);
		role.setFSM(null);

		role.getInverse().removeSubRoleChains();
		role.getInverse().setHasComplexSubRole(false);
		role.getInverse().setSimple(true);
		role.getInverse().setFSM(null);
	}

	private void computeImmediateSubRoles(Role r, Map<ATerm, DependencySet> subs) {

		Role invR = r.getInverse();
		if (invR != null && invR != r) {

			for (Role invSubR : invR.getSubRoles()) {
				Role subR = invSubR.getInverse();
				if (subR == null) {
					if (log.isLoggable(Level.FINE)) {
	                    log.fine("Property " + invSubR + " was supposed to be an ObjectProperty but it is not!");
                    }
				}
				else if (subR != r) {
					// System.out.println("expsub:
					// "+invR.getExplainSub(invSubR.getName()));
					// System.out.println("expinv:
					// "+invSubR.getExplainInverse());
					DependencySet subDS = invR.getExplainSub(invSubR.getName());
					subs.put(subR.getName(), subDS);
				}
			}
			for (ATermList roleChain : invR.getSubRoleChains()) {
				DependencySet subDS = invR.getExplainSub(roleChain);

				ATermList subChain = inverse(roleChain);
				subs.put(subChain, subDS);
			}
		}

		for (Role sub : r.getSubRoles()) {
			DependencySet subDS = r.getExplainSub(sub.getName());

			subs.put(sub.getName(), subDS);
		}

		for (ATermList subChain : r.getSubRoleChains()) {
			DependencySet subDS = r.getExplainSub(subChain);

			subs.put(subChain, subDS);
		}

	}

	private void computeSubRoles(Role r, Set<Role> subRoles, Set<ATermList> subRoleChains,
	                Map<ATerm, DependencySet> dependencies, DependencySet ds) {
		// check for loops
		if (subRoles.contains(r)) {
			return;
		}

		// reflexive
		subRoles.add(r);
		dependencies.put(r.getName(), ds);

		// transitive closure
		Map<ATerm, DependencySet> immSubs = new HashMap<ATerm, DependencySet>();
		computeImmediateSubRoles(r, immSubs);
		for (Entry<ATerm, DependencySet> entry : immSubs.entrySet()) {
			ATerm sub = entry.getKey();
			DependencySet subDS = PelletOptions.USE_TRACING ? ds.union(entry.getValue(), true)
			                : DependencySet.INDEPENDENT;
			if (sub instanceof ATermAppl) {
				Role subRole = getRole(sub);

				computeSubRoles(subRole, subRoles, subRoleChains, dependencies, subDS);
			}
			else {
				subRoleChains.add((ATermList) sub);
				dependencies.put(sub, subDS);
			}
		}
	}

	/**
	 * Returns a string representation of the RBox where for each role subroles, superroles, and isTransitive
	 * information is given
	 */
	@Override
	public String toString() {
		return "[RBox " + roles.values() + "]";
	}

	/**
	 * for each role in the list finds an inverse role and returns the new list.
	 */
	public ATermList inverse(ATermList roles) {
		ATermList invList = ATermUtils.EMPTY_LIST;

		for (ATermList list = roles; !list.isEmpty(); list = list.getNext()) {
			ATermAppl r = (ATermAppl) list.getFirst();
			Role role = getRole(r);
			Role invR = role.getInverse();
			if (invR == null) {
				System.err.println("Property " + r + " was supposed to be an ObjectProperty but it is not!");
			}
			else {
				invList = invList.insert(invR.getName());
			}
		}

		return invList;
	}

	/**
	 * @return Returns the roles.
	 */
	public Set<ATermAppl> getRoleNames() {
		return roles.keySet();
	}

	public Set<Role> getReflexiveRoles() {
		return reflexiveRoles;
	}

	/**
	 * getRoles
	 * 
	 * @return
	 */
	public Collection<Role> getRoles() {
		return roles.values();
	}

	public Taxonomy<ATermAppl> getObjectTaxonomy() {
		if (objectTaxonomy == null) {
			RoleTaxonomyBuilder builder = new RoleTaxonomyBuilder(this, PropertyType.OBJECT);
			objectTaxonomy = builder.classify();
		}
		return objectTaxonomy;
	}

	public Taxonomy<ATermAppl> getDataTaxonomy() {
		if (dataTaxonomy == null) {
			RoleTaxonomyBuilder builder = new RoleTaxonomyBuilder(this, PropertyType.DATATYPE);
			dataTaxonomy = builder.classify();
		}
		return dataTaxonomy;
	}

	public Taxonomy<ATermAppl> getAnnotationTaxonomy() {
		if (annotationTaxonomy == null) {
			RoleTaxonomyBuilder builder = new RoleTaxonomyBuilder(this, PropertyType.ANNOTATION);
			if (PelletOptions.USE_ANNOTATION_SUPPORT) {
				annotationTaxonomy = builder.classify();
			}
		}
		return annotationTaxonomy;
	}
	
	public boolean isObjectTaxonomyPrepared() {
		return objectTaxonomy != null;
	}

	public boolean isDataTaxonomyPrepared() {
		return dataTaxonomy != null;
	}

	public boolean isAnnotationTaxonomyPrepared() {
		return annotationTaxonomy != null;
	}
}
