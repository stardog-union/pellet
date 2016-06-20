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

import com.clarkparsia.pellet.sparqldl.model.Query;
import java.util.Arrays;
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
public class TestUnionQueries extends AbstractQueryTest
{
	@Test
	public void test1()
	{
		classes(_A, _B);
		individuals(_a, _b, _c);

		_kb.addType(_a, _A);
		_kb.addType(_b, _B);

		final Query q = query(select(x), where(UnionAtom(Arrays.asList(Arrays.asList(TypeAtom(x, _A)), Arrays.asList(TypeAtom(x, _B))))));

		testQuery(q, new ATermAppl[][] { { _a }, { _b } });
	}

	@Test
	public void test2()
	{
		classes(_A, _B, _C);
		objectProperties(_p);
		individuals(_a, _b, _c);

		_kb.addType(_a, _A);
		_kb.addType(_a, _C);
		_kb.addType(_b, _A);
		_kb.addType(_b, _B);

		final Query q = query(select(x), where(TypeAtom(x, _A), UnionAtom(Arrays.asList(Arrays.asList(TypeAtom(x, _B)), Arrays.asList(TypeAtom(x, _C))))));

		testQuery(q, new ATermAppl[][] { { _a }, { _b } });
	}

	@Test
	public void test3()
	{
		classes(_A, _B);
		objectProperties(_p, _q);
		individuals(_a, _b, _c);

		_kb.addType(_a, _A);
		_kb.addType(_b, _A);
		_kb.addPropertyValue(_p, _a, _c);
		_kb.addPropertyValue(_p, _b, _c);

		final Query q1 = query(select(x, y), where(TypeAtom(x, _A), UnionAtom(Arrays.asList(Arrays.asList(PropertyValueAtom(x, _p, y)), Arrays.asList(PropertyValueAtom(x, _q, y))))));

		testQuery(q1, new ATermAppl[][] { { _a, _c }, { _b, _c } });
	}
}
