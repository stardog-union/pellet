// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import static com.clarkparsia.pellet.el.ELSyntaxUtils.isEL;
import static com.clarkparsia.pellet.el.ELSyntaxUtils.simplify;

import java.util.Collection;
import java.util.Iterator;

import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.expressivity.ProfileBasedExpressivityChecker;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class ELExpressivityChecker extends ProfileBasedExpressivityChecker {

	private Expressivity	m_Expressivity;

	public ELExpressivityChecker(KnowledgeBase kb) {
		super( kb );
	}

	@Override
    public boolean compute(Expressivity expressivity) {
		m_Expressivity = expressivity;

		if( !processIndividuals() )
			return false;
		if( !processClasses() )
			return false;
		if( !processRoles() )
			return false;
		return true;
	}

	private boolean processIndividuals() {
		IndividualIterator i = m_KB.getABox().getIndIterator();
		while( i.hasNext() ) {
			Individual ind = i.next();
			ATermAppl nominal = ATermUtils.makeValue( ind.getName() );
			for( ATermAppl term : ind.getTypes() ) {
				if( term.equals( nominal ) )
					continue;

				if( !isEL( term ) )
					return false;
			}
		}

		return true;
	}

	private boolean processClasses() {
		for( ATermAppl axiom : m_KB.getTBox().getAssertedAxioms() ) {
			AFun fun = axiom.getAFun();

			if( fun.equals( ATermUtils.DISJOINTSFUN ) ) {
				m_Expressivity.setHasDisjointClasses( true );

				ATermList args = (ATermList) axiom.getArgument( 0 );
				for( ; !args.isEmpty(); args = args.getNext() ) {
					if( !isEL( (ATermAppl) args.getFirst() ) ) {
						return false;
					}
				}
			}
			else {
				ATermAppl sub = (ATermAppl) axiom.getArgument( 0 );
				ATermAppl sup = (ATermAppl) axiom.getArgument( 1 );

				if( !isEL( sub ) || !isEL( sup ) ) {
					return false;
				}

				if( fun.equals( ATermUtils.SUBFUN ) ) {
					if( ATermUtils.isBottom( simplify( sup ) ) ) {
						m_Expressivity.setHasDisjointClasses( true );
					}
				}
				else if( fun.equals( ATermUtils.EQCLASSFUN ) ) {
					if( ATermUtils.isBottom( simplify( sub ) )
							|| ATermUtils.isBottom( simplify( sup ) ) ) {
						m_Expressivity.setHasDisjointClasses( true );
					}
				}
				else if( fun.equals( ATermUtils.DISJOINTFUN ) ) {
					m_Expressivity.setHasDisjointClasses( true );
				}
				else {
					return false;
				}
			}
		}

		return true;
	}

	private boolean processRoles() {
		Collection<Role> roles = m_KB.getRBox().getRoles();

		for( Role r : roles ) {
			if( r.isBuiltin() )
				continue;
			
			if( r.isDatatypeRole() )
				return false;

			if( r.isAnon() ) {
				for( Role subRole : r.getSubRoles() ) {
					if( !subRole.isAnon() && !subRole.isBottom() )
						return false;
				}
			}

			// InverseFunctionalProperty declaration may mean that a named
			// property has an anonymous inverse property which is functional
			// The following condition checks this case
			if( r.isAnon() && r.isFunctional() )
				return false;
			if( r.isFunctional() )
				return false;
			if( r.isTransitive() )
				m_Expressivity.setHasTransitivity( true );
			if( r.isReflexive() )
				m_Expressivity.setHasReflexivity( true );
			if( r.isIrreflexive() )
				return false;
			if( r.isAsymmetric() )
				return false;
			if( !r.getDisjointRoles().isEmpty() )
				return false;
			if( r.hasComplexSubRole() ) {
				m_Expressivity.setHasComplexSubRoles( true );
				
				// if a property is named, all the properties in its subproperty chains should be named. since we have
				// anonymous inverses automatically created, we can have chains with inverses. in this case all the
				// properties in the chain should b einverse as well as the super property.  
				boolean isInv = r.isAnon();
				for (ATermList chain: r.getSubRoleChains()) {
					for( ; !chain.isEmpty(); chain = chain.getNext()) {
						if( ATermUtils.isInv((ATermAppl) chain.getFirst()) != isInv )
							return false;
					}
				}
			}

			// Each property has itself included in the subroles set. We need
			// at least two properties in the set to conclude there is a role
			// hierarchy defined in the ontology
			if( r.getSubRoles().size() > 1 )
				m_Expressivity.setHasRoleHierarchy( true );
		}

		for( Role r : roles ) {
			Iterator<ATermAppl> assertedDomains = m_KB.getRBox().getAssertedDomains(r);
			while (assertedDomains.hasNext()) {
				ATermAppl domain = assertedDomains.next();
				if( !isEL( domain ) )
					return false;

				m_Expressivity.setHasDomain( true );
			}
			
			Iterator<ATermAppl> assertedRanges = m_KB.getRBox().getAssertedRanges(r);
			while (assertedRanges.hasNext()) {
				ATermAppl range = assertedRanges.next();
				if( !isEL( range ) )
					return false;

				m_Expressivity.setHasDomain( true );
			}
		}

		return true;
	}

	@Override
    public boolean updateWith(Expressivity expressivity, ATermAppl term) {
		return false;
	}
}
