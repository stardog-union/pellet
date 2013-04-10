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


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.fsm.TransitionGraph;

import com.clarkparsia.pellet.utils.CollectionUtils;
import com.clarkparsia.pellet.utils.TermFactory;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class Role {
	@Deprecated
	public final static String[] TYPES = {"Untyped", "Object", "Datatype", "Annotation", "Ontology"	};

	@Deprecated
	final public static int UNTYPED    = 0;
	@Deprecated
	final public static int OBJECT     = 1;
	@Deprecated
	final public static int DATATYPE   = 2;
	@Deprecated
	final public static int ANNOTATION = 3;
	@Deprecated
	final public static int ONTOLOGY   = 4;

	private ATermAppl   name;

	private PropertyType type = PropertyType.UNTYPED;
	private Role inverse = null;

	private Set<Role> subRoles = SetUtils.emptySet();
	private Set<Role> superRoles = SetUtils.emptySet();
    private Map<Role,DependencySet> disjointRoles = Collections.emptyMap();
    private Set<ATermList> subRoleChains = SetUtils.emptySet();

	private Set<Role> functionalSupers = SetUtils.emptySet();
	private Set<Role> transitiveSubRoles = SetUtils.emptySet();

    private TransitionGraph<Role> tg;

    public static int TRANSITIVE     = 0x01;
    public static int FUNCTIONAL     = 0x02;
    public static int INV_FUNCTIONAL = 0x04;
    public static int REFLEXIVE      = 0x08;
    public static int IRREFLEXIVE    = 0x10;
    public static int ASYM       	 = 0x20;
    /**
     * Use {@link #ASYM}
     */
    public static int ANTI_SYM       = ASYM;

    public static int SIMPLE         = 0x40;
    public static int COMPLEX_SUB    = 0x80;

    public static int FORCE_SIMPLE   = 0x100;

    private int flags = SIMPLE;


    /*
     * Explanation related
     */
	private DependencySet explainAsymmetric  = DependencySet.INDEPENDENT;
	private DependencySet explainFunctional = DependencySet.INDEPENDENT;
	private DependencySet explainIrreflexive  = DependencySet.INDEPENDENT;
	private DependencySet explainReflexive  = DependencySet.INDEPENDENT;
	private DependencySet explainSymmetric = DependencySet.INDEPENDENT;
	private DependencySet explainTransitive = DependencySet.INDEPENDENT;
	private DependencySet explainInverseFunctional = DependencySet.INDEPENDENT;
	private Map<ATerm,DependencySet> explainSub = new HashMap<ATerm, DependencySet>();
	private Map<ATerm,DependencySet> explainSup = new HashMap<ATerm, DependencySet>();
	
	private Map<ATermAppl,DependencySet> domains = Collections.emptyMap();
	private Map<ATermAppl,DependencySet> ranges = Collections.emptyMap();



	public Role(ATermAppl name) {
		this(name, PropertyType.UNTYPED);
	}

	public Role(ATermAppl name, PropertyType type) {
		this.name = name;
		this.type = type;

		addSubRole(this, DependencySet.INDEPENDENT);
		addSuperRole(this, DependencySet.INDEPENDENT);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Role)
			return name.equals(((Role)o).getName());

		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return ATermUtils.toString( name );
	}

	public String debugString() {
		String str = "(" + type + "Role " + name;
		if(isTransitive()) str += " Transitive";
        if(isReflexive()) str += " Reflexive";
        if(isIrreflexive()) str += " Irreflexive";
		if(isSymmetric()) str += " Symmetric";
        if(isAsymmetric()) str += " Asymmetric";
		if(isFunctional()) str += " Functional";
		if(isInverseFunctional()) str += " InverseFunctional";
		if(hasComplexSubRole()) str += " ComplexSubRole";
		if(isSimple()) str += " Simple";
		if(type == PropertyType.OBJECT || type == PropertyType.DATATYPE) {
			str += " domain=" + domains;
			str += " range=" + ranges;
			str += " superPropertyOf=" + subRoles;
			str += " subPropertyOf=" + superRoles;
            str += " hasSubPropertyChain=" + subRoleChains;
            str += " disjointWith=" + disjointRoles;
		}
		str += ")";

		return str;
	}


	/**
	 * Add a sub role chain without dependency tracking information
	 * @param chain
	 */
    public void addSubRoleChain( ATermList chain ) {
    	addSubRoleChain(chain, DependencySet.INDEPENDENT);
    }

    /**
     * Add a sub role chain with dependency tracking.
     *
     * @param chain List of role names of at least length 2.
     * @param ds
     */
    public void addSubRoleChain( ATermList chain, DependencySet ds) {
        if( chain.isEmpty() )
            throw new InternalReasonerException( "Adding a subproperty chain that is empty!" );
        else if( chain.getLength() == 1 )
            throw new InternalReasonerException( "Adding a subproperty chain that has a single element!" );

        subRoleChains = SetUtils.add( chain, subRoleChains );
        explainSub.put(chain, ds);
        setSimple( false );

        if( ATermUtils.isTransitiveChain( chain, name ) ) {
        	if( !isTransitive() )
        		setTransitive( true, ds );
        }
    }

    public void removeSubRoleChain(ATermList chain) {
        subRoleChains = SetUtils.remove( chain, subRoleChains );
        explainSub.remove(chain);
        if( isTransitive() && ATermUtils.isTransitiveChain( chain, name ) ) {
            setTransitive( false, null);
        }
    }

    public void removeSubRoleChains() {
        subRoleChains = Collections.emptySet();

        if( isTransitive() )
            setTransitive( false, null);
    }

	/**
	 * r is subrole of this role
     *
	 * @param r
	 */

	public void addSubRole(Role r) {
		DependencySet ds = PelletOptions.USE_TRACING
		? new DependencySet(ATermUtils.makeSubProp(r.getName(), this.getName()))
		: DependencySet.INDEPENDENT;
		addSubRole(r, ds);
	}

	/**
	 * Add sub role with depedency set.
     *
	 * @param r subrole of this role
	 * @param ds
	 */
	public void addSubRole(Role r, DependencySet ds) {
		if (PelletOptions.USE_TRACING && explainSub.get(r.getName()) == null)
			explainSub.put(r.getName(), ds);

		subRoles = SetUtils.add( r, subRoles );
		explainSub.put(r.getName(), ds);
	}

	public boolean removeDomain(ATermAppl a, DependencySet ds) {
		final DependencySet existing = domains.get( a );

		if( existing != null ) {
			if( ds.getExplain().equals( existing.getExplain() ) ) {
				domains.remove( a );
				return true;
			}
		}

		return false;
	}

	public boolean removeRange(ATermAppl a, DependencySet ds) {
		final DependencySet existing = ranges.get( a );

		if( existing != null ) {
			if( ds.getExplain().equals( existing.getExplain() ) ) {
				ranges.remove( a );
				return true;
			}
		}


		return false;
	}
	
	void resetDomainRange() {
		domains = Collections.emptyMap();
		ranges = Collections.emptyMap();
	}

    public void removeSubRole(Role r) {
        subRoles = SetUtils.remove( r, subRoles );
    }

	/**
	 * r is superrole of this role
     *
	 * @param r
	 */
	public void addSuperRole(Role r) {
		DependencySet ds = PelletOptions.USE_TRACING
		? new DependencySet(ATermUtils.makeSubProp(name, r.getName()))
		: DependencySet.INDEPENDENT;
		addSuperRole(r, ds);
	}

	public void addSuperRole(Role r, DependencySet ds) {
        superRoles = SetUtils.add( r, superRoles );
        explainSup.put(r.getName(), ds);
	}

    public void addDisjointRole(Role r, DependencySet ds) {
        if( disjointRoles.isEmpty() )
        	disjointRoles = new HashMap<Role, DependencySet>();

        disjointRoles.put( r, ds );
    }

	public boolean addDomain(ATermAppl a, DependencySet ds) {
		if( domains.isEmpty() )
			domains = CollectionUtils.makeMap();

		DependencySet existing = domains.put( a, ds );
		if( existing != null && existing.getExplain().equals( ds.getExplain() ) )
			return false;

		return true;
	}

	public boolean addRange(ATermAppl a, DependencySet ds) {
		if( ranges.isEmpty() )
			ranges = CollectionUtils.makeMap();

		DependencySet existing = ranges.put( a, ds );
		if( existing != null && existing.getExplain().equals( ds.getExplain() ) )
			return false;

		return true;
	}

	public boolean isObjectRole() {
		return type == PropertyType.OBJECT;
	}

	public boolean isDatatypeRole() {
		return type == PropertyType.DATATYPE;
	}

	@Deprecated
	public boolean isOntologyRole() {
		return false;
	}
	/**
	 * check if a role is declared as datatype property
	 */
	public boolean isAnnotationRole() {
		return type == PropertyType.ANNOTATION;
	}

    public boolean isUntypedRole() {
        return type == PropertyType.UNTYPED;
    }

	/**
	 * @return
	 */
	public Role getInverse() {
		return inverse;
	}

	public boolean hasNamedInverse() {
		return inverse != null && !inverse.isAnon();
	}

	public boolean hasComplexSubRole() {
		return (flags & COMPLEX_SUB) != 0;
	}

	public boolean isFunctional() {
		return (flags & FUNCTIONAL) != 0;
	}

	public boolean isInverseFunctional() {
		return (flags & INV_FUNCTIONAL) != 0;
	}

	public boolean isSymmetric() {
		return inverse != null && isEquivalent(inverse);
	}

	/**
	 * @deprecated Use {@link #isAsymmetric()}
	 */
    public boolean isAntisymmetric() {
        return (flags & ASYM) != 0;
    }

    public boolean isAsymmetric() {
        return (flags & ASYM) != 0;
    }
    
	public boolean isTransitive() {
		return (flags & TRANSITIVE) != 0;
	}

    public boolean isReflexive() {
        return (flags & REFLEXIVE) != 0;
    }

    public boolean isIrreflexive() {
        return (flags & IRREFLEXIVE) != 0;
    }

	public boolean isAnon() {
	    return name.getArity() != 0;
	}

	public ATermAppl getName() {
		return name;
	}

	public Set<ATermAppl> getDomains() {
		return domains.keySet();
	}

	public Set<ATermAppl> getRanges() {
		return ranges.keySet();
	}

	public Set<Role> getSubRoles() {
		return Collections.unmodifiableSet( subRoles );
	}

	public Set<Role> getEquivalentProperties() {
		return SetUtils.intersection( subRoles, superRoles );
	}

	public boolean isEquivalent( Role r ) {
		return subRoles.contains( r ) && superRoles.contains( r );
	}

	public Set<Role> getProperSubRoles() {
		return SetUtils.difference( subRoles, superRoles );
	}
	
    public Set<ATermList> getSubRoleChains() {
        return subRoleChains;
    }

	/**
	 * @return
	 */
	public Set<Role> getSuperRoles() {
		return Collections.unmodifiableSet( superRoles );
	}

    public Set<Role> getDisjointRoles() {
        return Collections.unmodifiableSet( disjointRoles.keySet() );
    }

    public DependencySet getExplainDisjointRole(Role role) {
        return disjointRoles.get( role );
    }

	/**
	 * @return
	 */
	public PropertyType getType() {
		return type;
	}

	public String getTypeName() {
		return type.toString();
	}

	public boolean isSubRoleOf(Role r) {
		return superRoles.contains(r);
	}

	public boolean isSuperRoleOf(Role r) {
		return subRoles.contains(r);
	}

	public void setInverse(Role term) {
		inverse = term;
	}

	public void setFunctional( boolean b ) {
		DependencySet ds = DependencySet.INDEPENDENT;
		setFunctional(b, ds);
	}

	public void setFunctional( boolean b, DependencySet ds) {
        if( b ) {
            flags |= FUNCTIONAL;
            explainFunctional = ds;
        } else {
            flags &= ~FUNCTIONAL;
            explainFunctional = DependencySet.INDEPENDENT;
        }
	}

    public void setInverseFunctional(boolean b) {
    	setInverseFunctional( b, DependencySet.INDEPENDENT );
    }

    public void setInverseFunctional(boolean b, DependencySet ds) {
        if( b ) {
            flags |= INV_FUNCTIONAL;
            explainInverseFunctional = ds;
        } else {
            flags &= ~INV_FUNCTIONAL;
            explainInverseFunctional = DependencySet.INDEPENDENT;
        }
    }

	public void setTransitive(boolean b) {
		DependencySet ds = PelletOptions.USE_TRACING
		? new DependencySet(ATermUtils.makeTransitive(name))
		: DependencySet.INDEPENDENT;

		setTransitive(b, ds);
	}

	public void setTransitive(boolean b, DependencySet ds) {

        ATermList roleChain = ATermUtils.makeList( new ATerm[] { name, name } );
        if( b ) {
            flags |= TRANSITIVE;
            explainTransitive = ds;
            addSubRoleChain( roleChain, ds );
        }
        else {
            flags &= ~TRANSITIVE;
            explainTransitive = ds;
            removeSubRoleChain( roleChain );
        }
	}

	public void setReflexive(boolean b) {
		setReflexive(b, DependencySet.INDEPENDENT);
	}

    public void setReflexive(boolean b, DependencySet ds) {
        if( b )
            flags |= REFLEXIVE;
        else
            flags &= ~REFLEXIVE;
        explainReflexive = ds;
    }

    public void setIrreflexive(boolean b) {
    	setIrreflexive(b, DependencySet.INDEPENDENT);
    }

    public void setIrreflexive(boolean b, DependencySet ds) {
        if( b )
            flags |= IRREFLEXIVE;
        else
            flags &= ~IRREFLEXIVE;
        explainIrreflexive = ds;
    }

    /**
     * @deprecated Use {@link #setAsymmetric(boolean)}
     */
    public void setAntisymmetric(boolean b) {
    	setAsymmetric(b, DependencySet.INDEPENDENT);
    }
    
    public void setAsymmetric(boolean b) {
    	setAsymmetric(b, DependencySet.INDEPENDENT);
    }

    /**
     * @deprecated Use {@link #setAsymmetric(boolean,DependencySet)}
     */
    public void setAntisymmetric(boolean b, DependencySet ds) {
    	setAsymmetric(b, ds);
    }
    
    public void setAsymmetric(boolean b, DependencySet ds) {
        if( b ) {
            flags |= ANTI_SYM;
        } else {
            flags &= ~ANTI_SYM;
        }
        explainAsymmetric = ds;
    }

    public void setHasComplexSubRole(boolean b) {
    	if( b == hasComplexSubRole() )
    		return;

        if( b )
            flags |= COMPLEX_SUB;
        else
            flags &= ~COMPLEX_SUB;

        if( inverse != null )
        	inverse.setHasComplexSubRole( b );

        if( b )
        	setSimple( false );
    }

	public void setType(PropertyType type) {
		this.type = type;
	}

	/**
	 *
	 * @param subRoleChains
	 * @param dependencies map from role names (or lists) to depedencies
	 */
    public void setSubRolesAndChains(Set<Role> subRoles, Set<ATermList> subRoleChains, Map<ATerm,DependencySet> dependencies) {
        this.subRoles = subRoles;
    	this.subRoleChains = subRoleChains;
        this.explainSub = dependencies;
    }

	/**
	 * @param superRoles The superRoles to set.
	 * @param dependencies A map from role names (or role lists) to dependency sets.
	 */
	public void setSuperRoles(Set<Role> superRoles) {
		this.superRoles = superRoles;
	}

	/**
	 * @return Returns the functionalSuper.
	 */
	public Set<Role> getFunctionalSupers() {
		return functionalSupers;
	}

	/**
	 * @param functionalSuper The functionalSuper to set.
	 */
	public void addFunctionalSuper(Role r) {
        for( Role fs : functionalSupers ) {
            if( fs.isSubRoleOf( r ) ) {
                functionalSupers = SetUtils.remove( fs, functionalSupers );
                break;
            }
            else if( r.isSubRoleOf( fs ) ) {
                return;
            }
        }
        functionalSupers = SetUtils.add( r, functionalSupers );
	}

	public void setForceSimple( boolean b ) {
    	if( b == isForceSimple() )
    		return;

        if( b )
            flags |= FORCE_SIMPLE;
        else
            flags &= ~FORCE_SIMPLE;

        if( inverse != null )
        	inverse.setForceSimple( b );
	}

    public boolean isForceSimple() {
        return (flags & FORCE_SIMPLE) != 0;
    }

    public boolean isSimple() {
        return (flags & SIMPLE) != 0;
    }

    void setSimple( boolean b ) {
    	if( b == isSimple() )
    		return;

        if( b )
            flags |= SIMPLE;
        else
            flags &= ~SIMPLE;

        if( inverse != null )
        	inverse.setSimple( b );
    }

//	public boolean isSimple() {
//	    return !isTransitive() && transitiveSubRoles.isEmpty();
//	}

	/**
	 * @return Returns transitive sub roles.
	 */
	public Set<Role> getTransitiveSubRoles() {
		return transitiveSubRoles;
	}

	/**
	 * @param r The transtive sub role to add.
	 */
	public void addTransitiveSubRole( Role r ) {
		setSimple( false );

	    if( transitiveSubRoles.isEmpty() ) {
	        transitiveSubRoles = SetUtils.singleton( r );
	    }
	    else if( transitiveSubRoles.size() == 1 ) {
            Role tsr = transitiveSubRoles.iterator().next();
	        if( tsr.isSubRoleOf( r ) ) {
	            transitiveSubRoles = SetUtils.singleton( r );
	        }
	        else if( !r.isSubRoleOf( tsr ) ) {
	            transitiveSubRoles = new HashSet<Role>( 2 );
	            transitiveSubRoles.add( tsr );
	            transitiveSubRoles.add( r );
	        }
        }
        else {
	        for( Role tsr : transitiveSubRoles ) {
		        if( tsr.isSubRoleOf( r ) ) {
		            transitiveSubRoles.remove( tsr );
		            transitiveSubRoles.add( r );
		            return;
		        }
		        else if( r.isSubRoleOf( tsr ) ) {
		            return;
		        }
            }
	        transitiveSubRoles.add( r );
        }
	}

    public void setFSM( TransitionGraph<Role> tg ) {
        this.tg = tg;
    }

    public TransitionGraph<Role> getFSM() {
        return tg;
    }

    /* Dependency Retreival */

    public DependencySet getExplainAsymmetric() {
    	return explainAsymmetric;
    }

    public DependencySet getExplainDomain(ATermAppl a) {
    	return domains.get( a );
    }

    public DependencySet getExplainFunctional() {
    	return explainFunctional;
    }

    public DependencySet getExplainInverseFunctional() {
    	return explainInverseFunctional;
    }

    public DependencySet getExplainIrreflexive() {
    	return explainIrreflexive;
    }

    public DependencySet getExplainRange(ATermAppl a) {
    	return ranges.get( a );
    }

    public DependencySet getExplainReflexive() {
    	return explainReflexive;
    }

    public DependencySet getExplainSub(ATerm r) {
    	DependencySet ds = explainSub.get(r);
    	if (ds == null)
    		return DependencySet.INDEPENDENT;
    	return ds;
    }
    

    public DependencySet getExplainSubOrInv(Role r) {
    	DependencySet ds = explainSub.get( r.getName() );
    	if (ds == null)
    		return inverse.getExplainSub( r.getName() );
    	return ds;
    }

    public DependencySet getExplainSuper(ATerm r) {
    	DependencySet ds = explainSup.get(r);
    	if (ds == null)
    		return DependencySet.INDEPENDENT;
    	return ds;
    }

    public DependencySet getExplainSymmetric() {
    	return explainSymmetric;
    }

    public DependencySet getExplainTransitive() {
    	return explainTransitive;
    }

    public boolean isTop() {
    	return name.equals( TermFactory.TOP_OBJECT_PROPERTY ) || name.equals( TermFactory.TOP_DATA_PROPERTY );
    }
    
    public boolean isBottom() {
    	return name.equals( TermFactory.BOTTOM_OBJECT_PROPERTY ) || name.equals( TermFactory.BOTTOM_DATA_PROPERTY );
    }
    
    public boolean isBuiltin() {
    	return isTop() || isBottom() || (inverse != null && (inverse.isTop() || inverse.isBottom()));
    }
}
