// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.PropertyValueAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.TypeAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.UnionAtom;

import java.util.Arrays;

import org.junit.Test;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.Query;

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
public class TestUnionQueries extends AbstractQueryTest {
	@Test
	public void test1() {
		classes( A, B );
		individuals( a, b, c );
		
		kb.addType( a, A );
		kb.addType( b, B );

		Query q = query( select( x ), 
						 where( UnionAtom(Arrays.asList(
								Arrays.asList(TypeAtom( x, A )), 
								Arrays.asList(TypeAtom( x, B ))) ) ) );

		testQuery( q, new ATermAppl[][] { { a }, { b } } );
	}

	@Test
	public void test2() {
		classes( A, B, C );
		objectProperties( p );
		individuals( a, b, c );

		kb.addType( a, A );
		kb.addType( a, C );
		kb.addType( b, A );
		kb.addType( b, B );

		Query q = query( select( x ), 
						 where( TypeAtom( x, A ), 
								UnionAtom(Arrays.asList(
										Arrays.asList(TypeAtom( x, B )), 
										Arrays.asList(TypeAtom( x, C ))) ) ) );

		testQuery( q, new ATermAppl[][] { { a }, { b } } );
	}
	
	@Test
	public void test3() {
		classes( A, B );
		objectProperties( p, q );
		individuals( a, b, c );

		kb.addType( a, A );
		kb.addType( b, A );
		kb.addPropertyValue( p, a, c );
		kb.addPropertyValue( p, b, c );

		Query q1 = query( select( x, y ), 
						 where( TypeAtom( x, A ), 
								UnionAtom(Arrays.asList(
										Arrays.asList(PropertyValueAtom( x, p, y )), 
										Arrays.asList(PropertyValueAtom( x, q, y ))))) );

		testQuery( q1, new ATermAppl[][] { { a, c }, { b, c } } );
	}
}
