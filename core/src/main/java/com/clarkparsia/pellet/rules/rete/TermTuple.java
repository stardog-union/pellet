// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Term Tuple
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
public class TermTuple extends Tuple<ATermAppl> {

	private static String format(ATermAppl term) {
		return ATermUtils.toString( term );
	}

	public TermTuple(DependencySet ds, List<ATermAppl> terms) {
		super(ds, terms);
	}

	public TermTuple(DependencySet ds, ATermAppl... terms) {
		super(ds, terms);
	}

	public TermTuple( ATermAppl... terms) {
		super(DependencySet.INDEPENDENT, terms);
	}
	
	public boolean equals(Object other) {
		if (other instanceof TermTuple) {
			TermTuple otherFact = (TermTuple) other;
			return getElements().equals(otherFact.getElements());
		}
		return false;
	}

	public List<ATermAppl> getVars() {
		List<ATermAppl> v = new ArrayList<ATermAppl>();

		for (ATermAppl term : getElements()) {
			if (ATermUtils.isVar(term))
				v.add(term);
		}

		return v;
	}

	public String toString() {
		if (getElements().size() == 3) {
			ATermAppl pred = getElements().get(Compiler.PRED);
			ATermAppl subj = getElements().get(Compiler.SUBJ);
			ATermAppl obj = getElements().get(Compiler.OBJ);

			if (pred.equals(Compiler.TYPE)) {
				return format(obj) + "(" + format(subj) + ")";
			} else if (pred.equals(Compiler.SAME_AS)) {
				return format(subj) + " = " + format(obj);
			} else if (pred.equals(Compiler.DIFF_FROM)) {
				return format(subj) + " != " + format(obj);
			} else {
				return format(pred) + "(" + format(subj) + "," + format(obj)
						+ ")";
			}
		} else {
			Iterator<ATermAppl> i = getElements().iterator();
			StringBuilder sb = new StringBuilder();
			if(i.hasNext())
				sb.append(format(i.next()));
			sb.append("(");
			while (i.hasNext()) {
				sb.append(format(i.next()));
				if (i.hasNext())
					sb.append(", ");
			}
			sb.append(")");
			return sb.toString();
		}
	}

}
