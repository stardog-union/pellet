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

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.utils.TermFactory;
import openllet.aterm.ATermAppl;
import org.junit.Test;

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
public class TestNegatedQueries extends AbstractQueryTest
{
	@Test
	public void test1()
	{
		classes(_A, _B);
		individuals(_a, _b);

		_kb.addType(_a, _A);
		_kb.addType(_b, _A);
		_kb.addType(_b, _B);

		final Query q = query(select(x), where(TypeAtom(x, _A), NotKnownAtom(TypeAtom(x, _B))));

		testQuery(q, new ATermAppl[][] { { _a } });
	}

	@Test
	public void test2()
	{
		classes(_A, _B);
		individuals(_a, _b);

		_kb.addType(_a, _A);
		_kb.addType(_b, _A);
		_kb.addType(_b, _B);

		final Query q = query(select(x), where(TypeAtom(x, _A), NotKnownAtom(TypeAtom(x, not(_B)))));

		testQuery(q, new ATermAppl[][] { { _a }, { _b } });
	}

	@Test
	public void test3()
	{
		classes(_A, _B);
		objectProperties(_p);
		individuals(_a, _b, _c);

		_kb.addType(_a, _A);
		_kb.addType(_b, _B);
		_kb.addType(_c, _B);

		_kb.addPropertyValue(_p, _a, _c);

		final Query q = query(select(x, y), where(TypeAtom(x, _A), NotKnownAtom(PropertyValueAtom(x, _p, y)), TypeAtom(y, _B)));

		testQuery(q, new ATermAppl[][] { { _a, _b } });
	}

	@Test
	public void test4()
	{
		classes(_A, _B);
		individuals(_a, _b);

		final Query q = ask(NotKnownAtom(TypeAtom(x, _B)));

		testQuery(q, true);
	}

	@Test
	public void test5()
	{
		classes(_A, _B);
		individuals(_a, _b);

		_kb.addType(_b, _B);

		final Query q = ask(NotKnownAtom(TypeAtom(x, _B)));

		testQuery(q, false);
	}

	@Test
	public void test6()
	{
		classes(_A, _B);
		individuals(_a, _b, _c);

		_kb.addType(_b, _B);

		final Query q = query(select(x), where(TypeAtom(x, TermFactory.TOP), NotKnownAtom(TypeAtom(x, _B))));

		testQuery(q, new ATermAppl[][] { { _a }, { _c } });
	}

	@Test
	public void test7()
	{
		classes(_A, _B);
		individuals(_a, _b);

		_kb.addType(_a, _A);
		_kb.addType(_b, _A);
		_kb.addType(_b, _B);

		final Query q = query(select(x), where(TypeAtom(x, _A), NotKnownAtom(TypeAtom(x, _B))));

		testQuery(q, new ATermAppl[][] { { _a } });
	}

	@Test
	public void test8()
	{
		classes(_A, _B);
		individuals(_a, _b, _c);

		_kb.addType(_a, _A);

		final Query q1 = query(select(x), where(TypeAtom(x, TOP), NotKnownAtom(TypeAtom(x, _A)), NotKnownAtom(TypeAtom(x, _B))));

		testQuery(q1, new ATermAppl[][] { { _b }, { _c } });

		final Query q2 = query(select(x), where(TypeAtom(x, TOP), NotKnownAtom(TypeAtom(x, _A), TypeAtom(x, _B))));

		testQuery(q2, new ATermAppl[][] { { _a }, { _b }, { _c } });
	}

	@Test
	public void test9()
	{
		classes(_A, _B);
		objectProperties(_p, _q);
		individuals(_a, _b, _c);

		_kb.addPropertyValue(_p, _a, _b);

		final Query q1 = query(select(x), where(TypeAtom(x, TOP), NotKnownAtom(PropertyValueAtom(x, _p, y)), NotKnownAtom(PropertyValueAtom(x, _q, z))));

		testQuery(q1, new ATermAppl[][] { { _b }, { _c } });

		final Query q2 = query(select(x), where(TypeAtom(x, TOP), NotKnownAtom(PropertyValueAtom(x, _p, y), PropertyValueAtom(x, _q, z))));

		testQuery(q2, new ATermAppl[][] { { _a }, { _b }, { _c } });
	}
}
