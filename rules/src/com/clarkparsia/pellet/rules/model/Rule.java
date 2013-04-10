// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.Set;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.RulesToATermTranslator;

/**
 * <p>
 * Title: Rule
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
 * @author Ron Alford
 */ 
public class Rule {

	private ATermAppl name;

	private Collection<? extends RuleAtom>	body;
	private Collection<? extends RuleAtom>	head;
	
	private Set<ATermAppl> explanation;
	
	public Rule( Collection<? extends RuleAtom> head, Collection<? extends RuleAtom> body ) {
		this( head, body, null );
	}
	
	public Rule( Collection<? extends RuleAtom> head, Collection<? extends RuleAtom> body, Set<ATermAppl> explanation ) {
		this( null, head, body, explanation );
	}
	
	public Rule( ATermAppl name, Collection<? extends RuleAtom> head, Collection<? extends RuleAtom> body) {
		this( name, head, body, null );
	}
		
	public Rule( ATermAppl name, Collection<? extends RuleAtom> head, Collection<? extends RuleAtom> body, Set<ATermAppl> explanation ) {
		this.name= name;
		this.body = body;
		this.head = head;
		this.explanation = explanation;
	}
	
	public Set<ATermAppl> getExplanation(RulesToATermTranslator translator) {
		if( explanation == null ) {
			explanation = singleton( translator.translate( this ) );
		}
		return explanation;
	}
	
	public boolean equals( Object other ) {
		if ( other != null && getClass().equals( other.getClass() ) ) {
			Rule rule = ( Rule ) other;
			return getHead().equals( rule.getHead() ) && getBody().equals( rule.getBody() ) ; 
				
		}
		return false;
	}

	public Collection<? extends RuleAtom> getBody() {
		return body;
	}

	public Collection<? extends RuleAtom> getHead() {
		return head;
	}
	
	public ATermAppl getName() {
		return name;
	}
	
	public int hashCode() {
		return getBody().hashCode() + getHead().hashCode();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(  "Rule(" );
		if( name != null ) {
			sb.append( ATermUtils.toString(name) );
			sb.append( " " );
		}
		sb.append( getBody() );
		sb.append( " => " );
		sb.append( getHead() );
		sb.append( ")" );
		
		return sb.toString();
	}
}
