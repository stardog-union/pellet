// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public abstract class AbstractQueryTest extends AbstractKBTests {
	protected static final ATermAppl	x	= ATermUtils.makeVar( "x" );
	protected static final ATermAppl	y	= ATermUtils.makeVar( "y" );
	protected static final ATermAppl	z	= ATermUtils.makeVar( "z" );
	
	protected ATermAppl[] select(ATermAppl... vars) {
		return vars;
	}
	
	protected QueryAtom[] where(QueryAtom... atoms) {
		return atoms;
	}

	protected Query ask(QueryAtom... atoms) {
		return query( new ATermAppl[0], atoms );
	}

	protected Query query(ATermAppl[] vars, QueryAtom[] atoms) {
		Query q = new QueryImpl( kb, true );
		for( ATermAppl var : vars ) {			
			q.addResultVar( var );
		}
		
		for( QueryAtom atom : atoms ) {
			q.add( atom );
		}
		
		for( ATermAppl var : q.getUndistVars() ) {
			q.addDistVar( var, VarType.INDIVIDUAL );
		}
		
		return q;
	}

	protected void testQuery(Query query, boolean expected) {
		QueryResult result = QueryEngine.exec( query );

		assertEquals( expected, !result.isEmpty() );
	}

	protected void testQuery(Query query, ATermAppl[]... values) {
		List<ATermAppl> resultVars = query.getResultVars();

		Map<List<ATermAppl>, Integer> answers = new HashMap<List<ATermAppl>, Integer>();
		for( int i = 0; i < values.length; i++ ) {
			List<ATermAppl> answer = Arrays.asList( values[i] );
			Integer count = answers.get( answer );
			if( count == null ) {
				answers.put( answer, 1 );
			}
			else {
				answers.put( answer, count + 1 );
			}

		}

		QueryResult result = QueryEngine.exec( query );
		for( ResultBinding binding : result ) {
			List<ATermAppl> list = new ArrayList<ATermAppl>( resultVars.size() );
			for( ATermAppl var : resultVars ) {
				list.add( binding.getValue( var ) );
			}

			Integer count = answers.get( list );
			if( count == null ) {
				Assert.fail( "Unexpected binding in the result: " + list );
			}
			else if( count == 1 ) {
				answers.remove( list );
			}
			else {
				answers.put( list, count - 1 );
			}
		}

		assertTrue( "Unfound bindings: " + answers.keySet(), answers.isEmpty() );
	}

}
