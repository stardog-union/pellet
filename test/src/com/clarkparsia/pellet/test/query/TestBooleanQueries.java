// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.NotKnownAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.PropertyValueAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.SubClassOfAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.TypeAtom;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static org.junit.Assert.assertEquals;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
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
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class TestBooleanQueries extends AbstractKBTests {
	private static final ATermAppl	x	= ATermUtils.makeVar( "x" );
	private static final ATermAppl	y	= ATermUtils.makeVar( "y" );

	private Query query(QueryAtom... atoms) {
		Query q = new QueryImpl( kb, true );
		for( QueryAtom atom : atoms ) {
			q.add( atom );
		}
		return q;
	}

	private void testQuery(boolean expected, Query query) {
		assertEquals( expected, !QueryEngine.exec( query ).isEmpty() );
	}

	private void testABoxQuery(boolean expected, Query query) {
		assertEquals( expected, QueryEngine.execBooleanABoxQuery( query ) );
	}

	@Test
	public void testBooleanQueries() {
		classes(A, B);
		objectProperties(p);
		individuals(a, b);
		
		kb.addType( a, A );
		kb.addType( b, B );

		kb.addPropertyValue( p, a, b );

		Query q1 = query( TypeAtom( x, A ) );
		Query q2 = query( TypeAtom( x, B ) );
		Query q3 = query( PropertyValueAtom( x, p, y ), TypeAtom( y, B ) );
		Query q4 = query( TypeAtom( x, A ), PropertyValueAtom( x, p, y ), TypeAtom( y, B ) );
		Query q5 = query( TypeAtom( x, C ) );
		Query q6 = query( TypeAtom( x, A ), TypeAtom( x, C ) );

		testABoxQuery( true, q1 );
		testABoxQuery( true, q2 );
		testABoxQuery( true, q3 );
		testABoxQuery( true, q4 );
		testABoxQuery( false, q5 );
		testABoxQuery( false, q6 );

		kb.removePropertyValue( p, a, b );

		testABoxQuery( true, q1 );
		testABoxQuery( true, q2 );
		testABoxQuery( false, q3 );
		testABoxQuery( false, q4 );
		testABoxQuery( false, q5 );
		testABoxQuery( false, q6 );

		kb.addSubClass( TOP, C );

		testABoxQuery( true, q1 );
		testABoxQuery( true, q2 );
		testABoxQuery( false, q3 );
		testABoxQuery( false, q4 );
		testABoxQuery( true, q5 );
		testABoxQuery( true, q6 );
	}

	@Test
	public void testMixedQuery() {
		classes(A, B, C);
		individuals(a);
		
		kb.addSubClass( A, C );
		kb.addSubClass( B, C );

		kb.addType( a, A );

		Query q1 = query( SubClassOfAtom( x, C ), TypeAtom( y, x ) );
		q1.addDistVar( x, VarType.CLASS );
		q1.addResultVar( x );

		QueryResult qr = QueryEngine.exec( q1 );

		List<ATermAppl> results = new ArrayList<ATermAppl>();
		for( ResultBinding result : qr ) {
			System.out.println( result );
			results.add( result.getValue( x ) );
		}

		assertIteratorValues( results.iterator(), new ATermAppl[] { A, C } );
	}

	@Test
	public void testNegatedBooleanQueries1() {
		classes(A, B);
		individuals(a);
		
		kb.addType( a, A );

		Query q1 = query( NotKnownAtom( TypeAtom( a, A ) ) );
		Query q2 = query( NotKnownAtom( TypeAtom( a, B ) ) );
		Query q3 = query( NotKnownAtom( TypeAtom( a, not( A ) ) ) );
		Query q4 = query( NotKnownAtom( TypeAtom( a, not( B ) ) ) );
		
		testQuery( false, q1 );
		testQuery( true, q2 );
		testQuery( true, q3 );
		testQuery( true, q4 );

		kb.addDisjointClass( A, B );

		testQuery( false, q1 );
		testQuery( true, q2 );
		testQuery( true, q3 );
		testQuery( false, q4 );
	}
}
