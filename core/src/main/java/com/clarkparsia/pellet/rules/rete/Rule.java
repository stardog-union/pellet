// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Collections;
import java.util.List;

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
 */
public class Rule {

	private List<TermTuple>	head;
	private List<TermTuple>	body;

	public Rule(List<TermTuple> body, TermTuple head) {
		this( body, Collections.singletonList( head ) );
	}
	
	public Rule(List<TermTuple> body, List<TermTuple> head) {
		this.body = body;
		this.head = head;
	}

	public boolean equals(Object other) {
		if( other != null && getClass().equals( other.getClass() ) ) {
			Rule rule = (Rule) other;
			return getHead().equals( rule.getHead() ) && getBody().equals( rule.getBody() );
		}
		return false;
	}

	public List<TermTuple> getBody() {
		return body;
	}

	public List<TermTuple> getHead() {
		return head;
	}

	public int hashCode() {
		return getBody().hashCode() + getHead().hashCode();
	}

	public String toString() {
		return getBody() + " => " + getHead();
	}
}
