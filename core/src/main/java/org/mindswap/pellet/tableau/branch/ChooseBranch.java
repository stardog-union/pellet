// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.branch;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

public class ChooseBranch extends DisjunctionBranch {
    public ChooseBranch( ABox abox, CompletionStrategy completion, Node node, ATermAppl c, DependencySet ds ) {
        super( abox, completion, node, c, ds, new ATermAppl[] { ATermUtils.negate(c), c } );    
    }
    
    protected String getDebugMsg() {
        return "CHOS: Branch (" + getBranch() + ") try (" + (getTryNext() + 1) + "/" + getTryCount()
            + ") " + node.getName() + " " +  getDisjunct(getTryNext());
    }
}
