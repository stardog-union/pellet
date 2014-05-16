// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;

import org.mindswap.pellet.Node;

import com.clarkparsia.pellet.rules.model.RuleAtom;

/**
 */
public abstract class AlphaNode extends ReteNode {
	protected static final Iterator<WME> NO_MATCH = Collections.<WME>emptyList().iterator();
	
	protected boolean doExplanation; 

	public abstract Iterator<WME> getMatches(int argIndex, Node arg);

	public abstract Iterator<WME> getMatches();
	
	public abstract boolean matches(RuleAtom atom);
		
	protected void activate(WME wme) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Activate alpha " + wme);
		}
        for (BetaNode beta : getBetas()) {
            beta.activate(wme);
        }
	}
	
	public void setDoExplanation(boolean doExplanation) {
	    this.doExplanation = doExplanation;
    }	
	
	@Override
	public void print(String indent) {
		for (BetaNode node : getBetas()) {
			if (node.isTop()) {
				node.print(indent);
			}
        }
	}
}
