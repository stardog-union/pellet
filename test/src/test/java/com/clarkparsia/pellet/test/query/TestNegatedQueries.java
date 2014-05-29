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
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.TypeAtom;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.not;

import org.junit.Test;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.utils.TermFactory;

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
public class TestNegatedQueries extends AbstractQueryTest {
	@Test
	public void test1() {
		classes( A, B );
		individuals( a, b );		

		kb.addType( a, A );
		kb.addType( b, A );
		kb.addType( b, B );

		Query q = query( select( x ), 
						 where( TypeAtom( x, A ), 
								NotKnownAtom( TypeAtom( x, B ) ) ) );

		testQuery( q, new ATermAppl[][] { { a } } );
	}

	@Test
	public void test2() {
		classes( A, B );
		individuals( a, b );

		kb.addType( a, A );
		kb.addType( b, A );
		kb.addType( b, B );

		Query q = query( select( x ), 
						 where( TypeAtom( x, A ), 
								NotKnownAtom( TypeAtom( x, not( B ) ) ) ) );

		testQuery( q, new ATermAppl[][] { { a }, { b } } );
	}

	@Test
	public void test3() {
		classes( A, B );
		objectProperties( p );
		individuals( a, b, c );

		kb.addType( a, A );
		kb.addType( b, B );
		kb.addType( c, B );

		kb.addPropertyValue( p, a, c );

		Query q = query( select( x, y ), 
						 where( TypeAtom( x, A ), 
								NotKnownAtom( PropertyValueAtom( x, p, y ) ), 
								TypeAtom( y, B ) ) );

		testQuery( q, new ATermAppl[][] { { a, b } } );
	}

	@Test
	public void test4() {
		classes( A, B );
		individuals( a, b );

		Query q = ask( NotKnownAtom( TypeAtom( x, B ) ) );

		testQuery( q, true );
	}

	@Test
	public void test5() {
		classes( A, B );
		individuals( a, b );

		kb.addType( b, B );

		Query q = ask( NotKnownAtom( TypeAtom( x, B ) ) );

		testQuery( q, false );
	}

	@Test
	public void test6() {
		classes( A, B );
		individuals( a, b, c );

		kb.addType( b, B );

		Query q = query( select( x ), 
						 where( TypeAtom( x, TermFactory.TOP ), 
								NotKnownAtom( TypeAtom( x, B ) ) ) );

		testQuery( q, new ATermAppl[][] { { a }, { c } } );
	}

	@Test
	public void test7() {
		classes( A, B );
		individuals( a, b );

		kb.addType( a, A );
		kb.addType( b, A );
		kb.addType( b, B );

		Query q = query( select( x ), 
						 where( TypeAtom( x, A ), 
								NotKnownAtom( TypeAtom( x, B ) ) ) );

		testQuery( q, new ATermAppl[][] { { a } } );
	}
	
	@Test
	public void test8() {
		classes( A, B );
		individuals( a, b, c );

		kb.addType( a, A );

		Query q1 = query( select( x ), 
						  where( TypeAtom( x, TOP ),
								 NotKnownAtom( TypeAtom( x, A ) ), 
								 NotKnownAtom( TypeAtom( x, B ) ) ) );

		testQuery( q1, new ATermAppl[][] { { b }, { c } } );

		Query q2 = query( select( x ), 
						  where( TypeAtom( x, TOP ),
								 NotKnownAtom( TypeAtom( x, A ), 
								 			   TypeAtom( x, B ) ) ) );

		testQuery( q2, new ATermAppl[][] { { a }, { b }, { c } } );
	}
	
	@Test
	public void test9() {
		classes( A, B );
		objectProperties( p, q );
		individuals( a, b, c );

		kb.addPropertyValue( p, a, b );

		Query q1 = query( select( x ), 
						  where( TypeAtom( x, TOP ),
								 NotKnownAtom( PropertyValueAtom( x, p, y ) ), 
								 NotKnownAtom( PropertyValueAtom( x, q, z ) ) ) );

		testQuery( q1, new ATermAppl[][] { { b }, { c } } );

		Query q2 = query( select( x ), 
				  		  where( TypeAtom( x, TOP ),
				  				 NotKnownAtom( PropertyValueAtom( x, p, y ), 
				  				 			   PropertyValueAtom( x, q, z ) ) ) );

		testQuery( q2, new ATermAppl[][] { { a }, { b }, { c } } );
	}
}
