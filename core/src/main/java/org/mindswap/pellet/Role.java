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

import com.clarkparsia.pellet.utils.CollectionUtils;
import com.clarkparsia.pellet.utils.TermFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import openllet.aterm.ATerm;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermList;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.fsm.TransitionGraph;

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
public class Role
{
	@Deprecated
	public final static String[] TYPES = { "Untyped", "Object", "Datatype", "Annotation", "Ontology" };

	@Deprecated
	final public static int UNTYPED = 0;
	@Deprecated
	final public static int OBJECT = 1;
	@Deprecated
	final public static int DATATYPE = 2;
	@Deprecated
	final public static int ANNOTATION = 3;
	@Deprecated
	final public static int ONTOLOGY = 4;

	private final ATermAppl _name;

	private PropertyType _type = PropertyType.UNTYPED;
	private Role _inverse = null;

	private Set<Role> _subRoles = SetUtils.emptySet();
	private Set<Role> _superRoles = SetUtils.emptySet();
	private Map<Role, DependencySet> _disjointRoles = Collections.emptyMap();
	private Set<ATermList> _subRoleChains = SetUtils.emptySet();

	private Set<Role> _functionalSupers = SetUtils.emptySet();
	private Set<Role> _transitiveSubRoles = SetUtils.emptySet();

	private TransitionGraph<Role> _tg;

	public static int TRANSITIVE = 0x01;
	public static int FUNCTIONAL = 0x02;
	public static int INV_FUNCTIONAL = 0x04;
	public static int REFLEXIVE = 0x08;
	public static int IRREFLEXIVE = 0x10;
	public static int ASYM = 0x20;
	/**
	 * Use {@link #ASYM}
	 */
	public static int ANTI_SYM = ASYM;

	public static int SIMPLE = 0x40;
	public static int COMPLEX_SUB = 0x80;

	public static int FORCE_SIMPLE = 0x100;

	private int _flags = SIMPLE;

	/*
	 * Explanation related
	 */
	private DependencySet _explainAsymmetric = DependencySet.INDEPENDENT;
	private DependencySet _explainFunctional = DependencySet.INDEPENDENT;
	private DependencySet _explainIrreflexive = DependencySet.INDEPENDENT;
	private DependencySet _explainReflexive = DependencySet.INDEPENDENT;
	private final DependencySet _explainSymmetric = DependencySet.INDEPENDENT;
	private DependencySet _explainTransitive = DependencySet.INDEPENDENT;
	private DependencySet _explainInverseFunctional = DependencySet.INDEPENDENT;
	private Map<ATerm, DependencySet> _explainSub = new HashMap<>();
	private final Map<ATerm, DependencySet> _explainSup = new HashMap<>();

	private Map<ATermAppl, DependencySet> _domains = Collections.emptyMap();
	private Map<ATermAppl, DependencySet> _ranges = Collections.emptyMap();

	public Role(final ATermAppl name)
	{
		this(name, PropertyType.UNTYPED);
	}

	public Role(final ATermAppl name, final PropertyType type)
	{
		this._name = name;
		this._type = type;

		addSubRole(this, DependencySet.INDEPENDENT);
		addSuperRole(this, DependencySet.INDEPENDENT);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (o instanceof Role)
			return _name.equals(((Role) o).getName());

		return false;
	}

	@Override
	public int hashCode()
	{
		return _name.hashCode();
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(_name);
	}

	public String debugString()
	{
		String str = "(" + _type + "Role " + _name;
		if (isTransitive())
			str += " Transitive";
		if (isReflexive())
			str += " Reflexive";
		if (isIrreflexive())
			str += " Irreflexive";
		if (isSymmetric())
			str += " Symmetric";
		if (isAsymmetric())
			str += " Asymmetric";
		if (isFunctional())
			str += " Functional";
		if (isInverseFunctional())
			str += " InverseFunctional";
		if (hasComplexSubRole())
			str += " ComplexSubRole";
		if (isSimple())
			str += " Simple";
		if (_type == PropertyType.OBJECT || _type == PropertyType.DATATYPE)
		{
			str += " domain=" + _domains;
			str += " range=" + _ranges;
			str += " superPropertyOf=" + _subRoles;
			str += " subPropertyOf=" + _superRoles;
			str += " hasSubPropertyChain=" + _subRoleChains;
			str += " disjointWith=" + _disjointRoles;
		}
		str += ")";

		return str;
	}

	/**
	 * Add a sub role chain without dependency tracking information
	 * 
	 * @param chain
	 */
	public void addSubRoleChain(final ATermList chain)
	{
		addSubRoleChain(chain, DependencySet.INDEPENDENT);
	}

	/**
	 * Add a sub role chain with dependency tracking.
	 *
	 * @param chain List of role names of at least length 2.
	 * @param ds
	 */
	public void addSubRoleChain(final ATermList chain, final DependencySet ds)
	{
		if (chain.isEmpty())
			throw new InternalReasonerException("Adding a subproperty chain that is empty!");
		else
			if (chain.getLength() == 1)
				throw new InternalReasonerException("Adding a subproperty chain that has a single element!");

		_subRoleChains = SetUtils.add(chain, _subRoleChains);
		_explainSub.put(chain, ds);
		setSimple(false);

		if (ATermUtils.isTransitiveChain(chain, _name))
			if (!isTransitive())
				setTransitive(true, ds);
	}

	public void removeSubRoleChain(final ATermList chain)
	{
		_subRoleChains = SetUtils.remove(chain, _subRoleChains);
		_explainSub.remove(chain);
		if (isTransitive() && ATermUtils.isTransitiveChain(chain, _name))
			setTransitive(false, null);
	}

	public void removeSubRoleChains()
	{
		_subRoleChains = Collections.emptySet();

		if (isTransitive())
			setTransitive(false, null);
	}

	/**
	 * r is subrole of this role
	 *
	 * @param r
	 */

	public void addSubRole(final Role r)
	{
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeSubProp(r.getName(), this.getName())) : DependencySet.INDEPENDENT;
		addSubRole(r, ds);
	}

	/**
	 * Add sub role with depedency set.
	 *
	 * @param r subrole of this role
	 * @param ds
	 */
	public void addSubRole(final Role r, final DependencySet ds)
	{
		if (PelletOptions.USE_TRACING && _explainSub.get(r.getName()) == null)
			_explainSub.put(r.getName(), ds);

		_subRoles = SetUtils.add(r, _subRoles);
		_explainSub.put(r.getName(), ds);
	}

	public boolean removeDomain(final ATermAppl a, final DependencySet ds)
	{
		final DependencySet existing = _domains.get(a);

		if (existing != null)
			if (ds.getExplain().equals(existing.getExplain()))
			{
				_domains.remove(a);
				return true;
			}

		return false;
	}

	public boolean removeRange(final ATermAppl a, final DependencySet ds)
	{
		final DependencySet existing = _ranges.get(a);

		if (existing != null)
			if (ds.getExplain().equals(existing.getExplain()))
			{
				_ranges.remove(a);
				return true;
			}

		return false;
	}

	void resetDomainRange()
	{
		_domains = Collections.emptyMap();
		_ranges = Collections.emptyMap();
	}

	public void removeSubRole(final Role r)
	{
		_subRoles = SetUtils.remove(r, _subRoles);
	}

	/**
	 * r is superrole of this role
	 *
	 * @param r
	 */
	public void addSuperRole(final Role r)
	{
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeSubProp(_name, r.getName())) : DependencySet.INDEPENDENT;
		addSuperRole(r, ds);
	}

	public void addSuperRole(final Role r, final DependencySet ds)
	{
		_superRoles = SetUtils.add(r, _superRoles);
		_explainSup.put(r.getName(), ds);
	}

	public void addDisjointRole(final Role r, final DependencySet ds)
	{
		if (_disjointRoles.isEmpty())
			_disjointRoles = new HashMap<>();

		_disjointRoles.put(r, ds);
	}

	public boolean addDomain(final ATermAppl a, final DependencySet ds)
	{
		if (_domains.isEmpty())
			_domains = CollectionUtils.makeMap();

		final DependencySet existing = _domains.put(a, ds);
		if (existing != null && existing.getExplain().equals(ds.getExplain()))
			return false;

		return true;
	}

	public boolean addRange(final ATermAppl a, final DependencySet ds)
	{
		if (_ranges.isEmpty())
			_ranges = CollectionUtils.makeMap();

		final DependencySet existing = _ranges.put(a, ds);
		if (existing != null && existing.getExplain().equals(ds.getExplain()))
			return false;

		return true;
	}

	public boolean isObjectRole()
	{
		return _type == PropertyType.OBJECT;
	}

	public boolean isDatatypeRole()
	{
		return _type == PropertyType.DATATYPE;
	}

	@Deprecated
	public boolean isOntologyRole()
	{
		return false;
	}

	/**
	 * check if a role is declared as datatype property
	 */
	public boolean isAnnotationRole()
	{
		return _type == PropertyType.ANNOTATION;
	}

	public boolean isUntypedRole()
	{
		return _type == PropertyType.UNTYPED;
	}

	/**
	 * @return
	 */
	public Role getInverse()
	{
		return _inverse;
	}

	public boolean hasNamedInverse()
	{
		return _inverse != null && !_inverse.isAnon();
	}

	public boolean hasComplexSubRole()
	{
		return (_flags & COMPLEX_SUB) != 0;
	}

	public boolean isFunctional()
	{
		return (_flags & FUNCTIONAL) != 0;
	}

	public boolean isInverseFunctional()
	{
		return (_flags & INV_FUNCTIONAL) != 0;
	}

	public boolean isSymmetric()
	{
		return _inverse != null && isEquivalent(_inverse);
	}

	/**
	 * @deprecated Use {@link #isAsymmetric()}
	 */
	@Deprecated
	public boolean isAntisymmetric()
	{
		return (_flags & ASYM) != 0;
	}

	public boolean isAsymmetric()
	{
		return (_flags & ASYM) != 0;
	}

	public boolean isTransitive()
	{
		return (_flags & TRANSITIVE) != 0;
	}

	public boolean isReflexive()
	{
		return (_flags & REFLEXIVE) != 0;
	}

	public boolean isIrreflexive()
	{
		return (_flags & IRREFLEXIVE) != 0;
	}

	public boolean isAnon()
	{
		return _name.getArity() != 0;
	}

	public ATermAppl getName()
	{
		return _name;
	}

	public Set<ATermAppl> getDomains()
	{
		return _domains.keySet();
	}

	public Set<ATermAppl> getRanges()
	{
		return _ranges.keySet();
	}

	public Set<Role> getSubRoles()
	{
		return Collections.unmodifiableSet(_subRoles);
	}

	public Set<Role> getEquivalentProperties()
	{
		return SetUtils.intersection(_subRoles, _superRoles);
	}

	public boolean isEquivalent(final Role r)
	{
		return _subRoles.contains(r) && _superRoles.contains(r);
	}

	public Set<Role> getProperSubRoles()
	{
		return SetUtils.difference(_subRoles, _superRoles);
	}

	public Set<ATermList> getSubRoleChains()
	{
		return _subRoleChains;
	}

	/**
	 * @return
	 */
	public Set<Role> getSuperRoles()
	{
		return Collections.unmodifiableSet(_superRoles);
	}

	public Set<Role> getDisjointRoles()
	{
		return Collections.unmodifiableSet(_disjointRoles.keySet());
	}

	public DependencySet getExplainDisjointRole(final Role role)
	{
		return _disjointRoles.get(role);
	}

	/**
	 * @return
	 */
	public PropertyType getType()
	{
		return _type;
	}

	public String getTypeName()
	{
		return _type.toString();
	}

	public boolean isSubRoleOf(final Role r)
	{
		return _superRoles.contains(r);
	}

	public boolean isSuperRoleOf(final Role r)
	{
		return _subRoles.contains(r);
	}

	public void setInverse(final Role term)
	{
		_inverse = term;
	}

	public void setFunctional(final boolean b)
	{
		final DependencySet ds = DependencySet.INDEPENDENT;
		setFunctional(b, ds);
	}

	public void setFunctional(final boolean b, final DependencySet ds)
	{
		if (b)
		{
			_flags |= FUNCTIONAL;
			_explainFunctional = ds;
		}
		else
		{
			_flags &= ~FUNCTIONAL;
			_explainFunctional = DependencySet.INDEPENDENT;
		}
	}

	public void setInverseFunctional(final boolean b)
	{
		setInverseFunctional(b, DependencySet.INDEPENDENT);
	}

	public void setInverseFunctional(final boolean b, final DependencySet ds)
	{
		if (b)
		{
			_flags |= INV_FUNCTIONAL;
			_explainInverseFunctional = ds;
		}
		else
		{
			_flags &= ~INV_FUNCTIONAL;
			_explainInverseFunctional = DependencySet.INDEPENDENT;
		}
	}

	public void setTransitive(final boolean b)
	{
		final DependencySet ds = PelletOptions.USE_TRACING ? new DependencySet(ATermUtils.makeTransitive(_name)) : DependencySet.INDEPENDENT;

		setTransitive(b, ds);
	}

	public void setTransitive(final boolean b, final DependencySet ds)
	{

		final ATermList roleChain = ATermUtils.makeList(new ATerm[] { _name, _name });
		if (b)
		{
			_flags |= TRANSITIVE;
			_explainTransitive = ds;
			addSubRoleChain(roleChain, ds);
		}
		else
		{
			_flags &= ~TRANSITIVE;
			_explainTransitive = ds;
			removeSubRoleChain(roleChain);
		}
	}

	public void setReflexive(final boolean b)
	{
		setReflexive(b, DependencySet.INDEPENDENT);
	}

	public void setReflexive(final boolean b, final DependencySet ds)
	{
		if (b)
			_flags |= REFLEXIVE;
		else
			_flags &= ~REFLEXIVE;
		_explainReflexive = ds;
	}

	public void setIrreflexive(final boolean b)
	{
		setIrreflexive(b, DependencySet.INDEPENDENT);
	}

	public void setIrreflexive(final boolean b, final DependencySet ds)
	{
		if (b)
			_flags |= IRREFLEXIVE;
		else
			_flags &= ~IRREFLEXIVE;
		_explainIrreflexive = ds;
	}

	/**
	 * @deprecated Use {@link #setAsymmetric(boolean)}
	 */
	@Deprecated
	public void setAntisymmetric(final boolean b)
	{
		setAsymmetric(b, DependencySet.INDEPENDENT);
	}

	public void setAsymmetric(final boolean b)
	{
		setAsymmetric(b, DependencySet.INDEPENDENT);
	}

	/**
	 * @deprecated Use {@link #setAsymmetric(boolean,DependencySet)}
	 */
	@Deprecated
	public void setAntisymmetric(final boolean b, final DependencySet ds)
	{
		setAsymmetric(b, ds);
	}

	public void setAsymmetric(final boolean b, final DependencySet ds)
	{
		if (b)
			_flags |= ANTI_SYM;
		else
			_flags &= ~ANTI_SYM;
		_explainAsymmetric = ds;
	}

	public void setHasComplexSubRole(final boolean b)
	{
		if (b == hasComplexSubRole())
			return;

		if (b)
			_flags |= COMPLEX_SUB;
		else
			_flags &= ~COMPLEX_SUB;

		if (_inverse != null)
			_inverse.setHasComplexSubRole(b);

		if (b)
			setSimple(false);
	}

	public void setType(final PropertyType type)
	{
		this._type = type;
	}

	/**
	 * @param _subRoleChains
	 * @param dependencies map from role names (or lists) to depedencies
	 */
	public void setSubRolesAndChains(final Set<Role> subRoles, final Set<ATermList> subRoleChains, final Map<ATerm, DependencySet> dependencies)
	{
		this._subRoles = subRoles;
		this._subRoleChains = subRoleChains;
		this._explainSub = dependencies;
	}

	/**
	 * @param _superRoles The _superRoles to set.
	 * @param dependencies A map from role names (or role lists) to dependency sets.
	 */
	public void setSuperRoles(final Set<Role> superRoles)
	{
		this._superRoles = superRoles;
	}

	/**
	 * @return Returns the functionalSuper.
	 */
	public Set<Role> getFunctionalSupers()
	{
		return _functionalSupers;
	}

	/**
	 * @param functionalSuper The functionalSuper to set.
	 */
	public void addFunctionalSuper(final Role r)
	{
		for (final Role fs : _functionalSupers)
			if (fs.isSubRoleOf(r))
			{
				_functionalSupers = SetUtils.remove(fs, _functionalSupers);
				break;
			}
			else
				if (r.isSubRoleOf(fs))
					return;
		_functionalSupers = SetUtils.add(r, _functionalSupers);
	}

	public void setForceSimple(final boolean b)
	{
		if (b == isForceSimple())
			return;

		if (b)
			_flags |= FORCE_SIMPLE;
		else
			_flags &= ~FORCE_SIMPLE;

		if (_inverse != null)
			_inverse.setForceSimple(b);
	}

	public boolean isForceSimple()
	{
		return (_flags & FORCE_SIMPLE) != 0;
	}

	public boolean isSimple()
	{
		return (_flags & SIMPLE) != 0;
	}

	void setSimple(final boolean b)
	{
		if (b == isSimple())
			return;

		if (b)
			_flags |= SIMPLE;
		else
			_flags &= ~SIMPLE;

		if (_inverse != null)
			_inverse.setSimple(b);
	}

	//	public boolean isSimple() {
	//	    return !isTransitive() && _transitiveSubRoles.isEmpty();
	//	}

	/**
	 * @return Returns transitive sub roles.
	 */
	public Set<Role> getTransitiveSubRoles()
	{
		return _transitiveSubRoles;
	}

	/**
	 * @param r The transtive sub role to add.
	 */
	public void addTransitiveSubRole(final Role r)
	{
		setSimple(false);

		if (_transitiveSubRoles.isEmpty())
			_transitiveSubRoles = SetUtils.singleton(r);
		else
			if (_transitiveSubRoles.size() == 1)
			{
				final Role tsr = _transitiveSubRoles.iterator().next();
				if (tsr.isSubRoleOf(r))
					_transitiveSubRoles = SetUtils.singleton(r);
				else
					if (!r.isSubRoleOf(tsr))
					{
						_transitiveSubRoles = new HashSet<>(2);
						_transitiveSubRoles.add(tsr);
						_transitiveSubRoles.add(r);
					}
			}
			else
			{
				for (final Role tsr : _transitiveSubRoles)
					if (tsr.isSubRoleOf(r))
					{
						_transitiveSubRoles.remove(tsr);
						_transitiveSubRoles.add(r);
						return;
					}
					else
						if (r.isSubRoleOf(tsr))
							return;
				_transitiveSubRoles.add(r);
			}
	}

	public void setFSM(final TransitionGraph<Role> tg)
	{
		this._tg = tg;
	}

	public TransitionGraph<Role> getFSM()
	{
		return _tg;
	}

	/* Dependency Retreival */

	public DependencySet getExplainAsymmetric()
	{
		return _explainAsymmetric;
	}

	public DependencySet getExplainDomain(final ATermAppl a)
	{
		return _domains.get(a);
	}

	public DependencySet getExplainFunctional()
	{
		return _explainFunctional;
	}

	public DependencySet getExplainInverseFunctional()
	{
		return _explainInverseFunctional;
	}

	public DependencySet getExplainIrreflexive()
	{
		return _explainIrreflexive;
	}

	public DependencySet getExplainRange(final ATermAppl a)
	{
		return _ranges.get(a);
	}

	public DependencySet getExplainReflexive()
	{
		return _explainReflexive;
	}

	public DependencySet getExplainSub(final ATerm r)
	{
		final DependencySet ds = _explainSub.get(r);
		if (ds == null)
			return DependencySet.INDEPENDENT;
		return ds;
	}

	public DependencySet getExplainSubOrInv(final Role r)
	{
		final DependencySet ds = _explainSub.get(r.getName());
		if (ds == null)
			return _inverse.getExplainSub(r.getName());
		return ds;
	}

	public DependencySet getExplainSuper(final ATerm r)
	{
		final DependencySet ds = _explainSup.get(r);
		if (ds == null)
			return DependencySet.INDEPENDENT;
		return ds;
	}

	public DependencySet getExplainSymmetric()
	{
		return _explainSymmetric;
	}

	public DependencySet getExplainTransitive()
	{
		return _explainTransitive;
	}

	public boolean isTop()
	{
		return _name.equals(TermFactory.TOP_OBJECT_PROPERTY) || _name.equals(TermFactory.TOP_DATA_PROPERTY);
	}

	public boolean isBottom()
	{
		return _name.equals(TermFactory.BOTTOM_OBJECT_PROPERTY) || _name.equals(TermFactory.BOTTOM_DATA_PROPERTY);
	}

	public boolean isBuiltin()
	{
		return isTop() || isBottom() || (_inverse != null && (_inverse.isTop() || _inverse.isBottom()));
	}
}
