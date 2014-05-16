// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Collection;
import java.util.Collections;

import com.clarkparsia.pellet.rules.PartialBinding;

/**
 * <p>
 * Title: Interpreter
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
public class Interpreter {
	public AlphaNetwork alphaNet;

	public Interpreter(AlphaNetwork alphaNet) {
		super();

		this.alphaNet = alphaNet;
	}
	
	
	/**
	 * Remove all facts from the interpreter, leaving the rules intact.
	 */
	public void reset() {
		for (AlphaNode alpha : alphaNet) {
	        alpha.reset();
        }
	}

	/**
	 * Restore abox to the given branch
	 * 
	 * @return true if a matching TermTuple was removed. False otherwise.
	 */
	public void restore(int branch) {
		for (AlphaNode alpha : alphaNet) {
	        alpha.unmark();
        }

		for (AlphaNode alpha : alphaNet) {
	        alpha.restore(branch);
        }
	}

	public void run() {
		alphaNet.activateAll();
	}


	/**
	 * @return
	 */
    public Collection<PartialBinding> getBindings() {
	    // TODO Auto-generated method stub
	    return Collections.emptyList();
    }
}
