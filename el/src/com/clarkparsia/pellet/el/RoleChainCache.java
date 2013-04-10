// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.CollectionUtils;

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
public class RoleChainCache {
	private static final String ANON_ROLE = "anonRole";
	
	private int m_AnonRoleCount;
	private Map<ATermAppl, MultiValueMap<ATermAppl, ATermAppl>> m_BinaryRoleInclusions;
	
	public RoleChainCache(KnowledgeBase kb) {
		m_AnonRoleCount = 0;
		m_BinaryRoleInclusions = CollectionUtils.makeMap();
		
		for (Role supRole : kb.getRBox().getRoles()) {
			if( supRole.isAnon() )
				continue;
			
			for (ATermList chain : supRole.getSubRoleChains()) {
				int chainLength = chain.getLength();
				if (chainLength <= 1) continue;
				
				ATermAppl r1 = (ATermAppl) chain.getFirst();
				chain = chain.getNext();
				ATermAppl r2 = (ATermAppl) chain.getFirst();
				ATermAppl superRole = createSuperRoleFor(r1, r2);
				for (int i = 1; i < chainLength - 1; i++) {
					add(kb, r1, r2, superRole);
					
					r1 = superRole;
					chain = chain.getNext();
					r2 = (ATermAppl) chain.getFirst();
					superRole = createSuperRoleFor(r1, r2);
				}
				
				add(kb, r1, r2, supRole.getName());
			}
		}
	}
	
	public boolean isAnon(ATermAppl r) {
		return r.getName().startsWith( ANON_ROLE );
	}
	
	public Set<ATermAppl> getAllSuperRoles(ATermAppl r1, ATermAppl r2) {
		MultiValueMap<ATermAppl, ATermAppl> innerMap = m_BinaryRoleInclusions.get(r1);
		if (innerMap == null) return Collections.emptySet();
		
		Set<ATermAppl> superRoles = innerMap.get(r2);
		if (superRoles == null) return Collections.emptySet();
	
		return superRoles;
	}	
	
	private ATermAppl createSuperRoleFor(ATermAppl r1, ATermAppl r2) {
		Set<ATermAppl> superRoles = getAllSuperRoles(r1, r2);
		if (superRoles.isEmpty()) {
			return ATermUtils.makeTermAppl(ANON_ROLE + m_AnonRoleCount++); 
		} else {
			return superRoles.iterator().next(); 
		}
	}
	

	private void add(KnowledgeBase kb, ATermAppl r1, ATermAppl r2, ATermAppl superRole) {
		Role role1 = kb.getRole( r1 );
		Role role2 = kb.getRole( r2 );
		
		if( role1 == null ) {
			if( role2 == null ) {
				add( r1, r2, superRole );
			}
			else {
				for( Role sub2 : role2.getSubRoles() ) {
					add( r1, sub2.getName(), superRole );
				}
			}
		}
		else if( role2 == null ) {
			for( Role sub1 : role1.getSubRoles() ) {
				add( sub1.getName(), r2, superRole );
			}
		}
		else {
			for( Role sub1 : role1.getSubRoles() ) {
				for( Role sub2 : role2.getSubRoles() ) {
					add( sub1.getName(), sub2.getName(), superRole );
				}
			}		
		}
	}

	private boolean add(ATermAppl r1, ATermAppl r2, ATermAppl superRole) {
		MultiValueMap<ATermAppl, ATermAppl> innerMap = m_BinaryRoleInclusions.get(r1);
		if (innerMap == null) {
			innerMap = new MultiValueMap<ATermAppl, ATermAppl>();
			m_BinaryRoleInclusions.put(r1, innerMap);
		}
		
		return innerMap.add(r2, superRole);
	}
	
	public void print() {
		System.out.println("Role Chains:");
		System.out.println(m_BinaryRoleInclusions);
	}
}
