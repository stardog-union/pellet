// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.List;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Utils
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
public class Utils {

	public static <T> List<T> concat(List<T> l, List<T> m) {
		List<T> tmp = new ArrayList<T>();
		tmp.addAll( l );
		tmp.addAll( m );
		return tmp;
	}

	public static List<ATermAppl> getSharedVars(Node node1, Node node2) {
		List<ATermAppl> result = new ArrayList<ATermAppl>();

		for( ATermAppl node1var : node1.vars )
			if( node2.vars.contains( node1var ) )
				result.add( node1var );

		return Utils.removeDups( result );
	}

	public static <T> List<T> removeDups(List<T> l) {
		List<T> noDups = new ArrayList<T>();
		for( int i = 0; i < l.size(); i++ )
			if( !noDups.contains( l.get( i ) ) )
				noDups.add( l.get( i ) );
		return noDups;
	}

}
