// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
/**
 */
public class BetaMemoryNode extends BetaNode {
	private final BetaMemoryIndex memory;
	
	private final AlphaNode alpha;
	
	private final List<FilterCondition> conditions;

	public BetaMemoryNode(AlphaNode alpha, List<FilterCondition> conditions) {
		if (conditions == null) {
			throw new NullPointerException();
		}
		this.alpha = alpha;
		this.conditions = conditions;
		this.memory = createIndex(conditions);
	}
	
	private static BetaMemoryIndex createIndex(List<FilterCondition> conditions) {		
		if (!conditions.isEmpty() && (conditions.get(0) instanceof JoinCondition)) {
			return BetaMemoryIndex.withJoin((JoinCondition) conditions.get(0));
		}
		
		return BetaMemoryIndex.withoutJoin();
	}
	
	public AlphaNode getAlphaNode() {
		return alpha;
	}	

    public List<FilterCondition> getConditions() {
	    return conditions;
    }

	
	@Override
	public void activate(WME wme) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Activate beta " + wme);
		}

		Iterator<Token> wmeTokens = memory.getTokens(wme);
		
		while (wmeTokens.hasNext()) {
            Token token = wmeTokens.next();
	        if (testConditions(wme, token, 0)) {
		        activateChildren(wme, token);
            }	        
        }
	}
	
	@Override
	public void activate(Token token) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Activate beta " + token);
		}

		memory.add(token);
		
		Iterator<WME> matches = memory.getWMEs(token, alpha);
		while (matches.hasNext()) {
			WME wme = matches.next();
			if (testConditions(wme, token, 1)) {
		        activateChildren(wme, token);
            }
		}
	}
	
	private boolean testConditions(WME wme, Token token, int start) {
		for (int i = start, n = conditions.size(); i < n; i++) {
			FilterCondition condition = conditions.get(i);
			if (!condition.test(wme, token)) {
            	return false;
            }
        }
		return true;
	}
	
	@Override
	public void reset() {
	    super.reset();
	    
	    memory.clear();
	}
	
	@Override
	public void restore(int branch) { 
		super.restore(branch);
		memory.restore(branch);
	}
	
	@Override
	public void print(String indent) {
		System.out.print(indent);
		System.out.println(alpha);
		indent += "  ";
		System.out.print(indent);
		System.out.print(this);
		System.out.print( " ");
		System.out.println(memory);
		for (BetaNode node : getBetas()) {
	        node.print(indent);
        }
	}
	
	public String toString() {
		return isTop() ? "Top" : "Beta" + conditions;
	}
}
