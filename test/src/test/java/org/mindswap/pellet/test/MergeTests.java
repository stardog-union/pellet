// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.JUnit4TestAdapter;
import openllet.aterm.ATermAppl;
import org.junit.Test;

public class MergeTests extends AbstractKBTests
{
	public static String _base = "file:" + PelletTestSuite.base + "misc/";

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(MergeTests.class);
	}

	/*
	 * The purpose of this test case is create a merging chain x -> y -> z where
	 * each merge depends on a different non-deterministic _branch. Then we do
	 * instance checking on x which should return true. This test shows that
	 * ABox.addType should add the type to each _node in the merging chain to
	 * ensure that restores will not cause the added type to be lost.
	 */
	@Test
	public void instanceCheckForMergedNode()
	{
		classes(_D, _E);
		individuals(_a, _b, _c, _d, _e);

		_kb.addType(_a, oneOf(_b, _c));

		_kb.addSubClass(_E, not(_D));

		_kb.addType(_a, _D);

		assertTrue(_kb.isConsistent());

		final ATermAppl mergedTo = _kb.getABox().getIndividual(_a).getMergedTo().getName();
		_kb.addType(mergedTo, oneOf(_d, _e));

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, not(_E)));
	}

	@Test
	public void addTypeToMergedNode()
	{
		classes(_A, _D);
		individuals(_a, _b, _c);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));
		_kb.addType(_a, _A);
		_kb.addType(_b, _B);
		_kb.addType(_c, _C);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		// we don't know which equality holds
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));
		assertFalse(_kb.isType(_a, _D));

		_kb.addType(_a, _D);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		// we still don't know which equality holds
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));
		// additional type causes a new inference
		assertTrue(_kb.isType(_a, _D));
	}

	@Test
	public void removeTypeFromMergedNode()
	{
		classes(_A, _D);
		individuals(_a, _b, _c);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));
		_kb.addType(_a, _A);
		_kb.addType(_b, _B);
		_kb.addType(_c, _C);
		_kb.addType(_a, _D);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));
		assertTrue(_kb.isType(_a, _D));

		final boolean removed = _kb.removeType(_a, _D);

		assertTrue(removed);
		assertFalse(_kb.isConsistencyDone());

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));
		assertFalse(_kb.isType(_a, _D));
	}

	@Test
	public void cannotRemoveInferredType()
	{
		classes(_A, _D);
		individuals(_a, _b, _c, _d);

		_kb.addType(_a, oneOf(_b, _c));
		_kb.addType(_a, _A);
		_kb.addType(_b, _B);
		_kb.addType(_c, _C);
		_kb.addType(_d, _D);
		_kb.addSame(_a, _d);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));
		assertTrue(_kb.isType(_d, _D));
		assertTrue(_kb.isType(_a, _D));

		final boolean removed = _kb.removeType(_a, _D);

		assertTrue(!removed);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));
		assertTrue(_kb.isType(_d, _D));
		assertTrue(_kb.isType(_a, _D));
	}

	@Test
	public void addClashingTypeToMergedNode()
	{
		classes(_A, _B, _C, _D);
		individuals(_a, _b, _c);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));
		_kb.addType(_a, _A);
		_kb.addType(_b, _B);
		_kb.addType(_c, _C);

		assertTrue(_kb.isConsistent());

		// we don't know which equality holds
		assertTrue(_kb.isType(_a, _A));
		assertFalse(_kb.isType(_a, _B));
		assertFalse(_kb.isType(_a, _C));

		// get which merge was chosen
		final ATermAppl mergedTo = _kb.getABox().getIndividual(_a).getMergedTo().getName();

		// add something to undo the merge
		if (mergedTo.equals(_b))
			_kb.addType(_a, not(_B));
		else
			_kb.addType(_a, not(_C));

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.isType(_a, _A));
		// there is now a single possibility for the merge
		if (mergedTo.equals(_b))
			assertTrue(_kb.isType(_a, _C));
		else
			assertTrue(_kb.isType(_a, _B));
	}

	@Test
	public void addEdgeToMergedSubject()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));

		assertTrue(_kb.isConsistent());

		// no edges to d
		assertFalse(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));

		_kb.addPropertyValue(_p, _a, _d);

		assertTrue(_kb.isConsistent());

		// there is an edge from a to d
		assertTrue(_kb.hasPropertyValue(_a, _p, _d));
		// still no edges from b or c to d
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
	}

	@Test
	public void addEdgeToMergedObject()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));

		assertTrue(_kb.isConsistent());

		// no edges from d
		assertFalse(_kb.hasPropertyValue(_d, _p, _a));
		assertFalse(_kb.hasPropertyValue(_d, _p, _b));
		assertFalse(_kb.hasPropertyValue(_d, _p, _c));

		_kb.addPropertyValue(_p, _d, _a);

		assertTrue(_kb.isConsistent());

		// there is an edge from d to a
		assertTrue(_kb.hasPropertyValue(_d, _p, _a));
		// still no edges to b or c from d
		assertFalse(_kb.hasPropertyValue(_d, _p, _b));
		assertFalse(_kb.hasPropertyValue(_d, _p, _c));
	}

	@Test
	public void addEdgeToMergedSubjectObject()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d, _e, _f);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));
		// d is either e or f
		_kb.addType(_d, oneOf(_e, _f));

		assertTrue(_kb.isConsistent());

		// no edges to d
		assertFalse(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
		assertFalse(_kb.hasPropertyValue(_a, _p, _e));
		assertFalse(_kb.hasPropertyValue(_b, _p, _e));
		assertFalse(_kb.hasPropertyValue(_c, _p, _e));
		assertFalse(_kb.hasPropertyValue(_a, _p, _f));
		assertFalse(_kb.hasPropertyValue(_b, _p, _f));
		assertFalse(_kb.hasPropertyValue(_c, _p, _f));

		_kb.addPropertyValue(_p, _a, _d);

		assertTrue(_kb.isConsistent());

		// there is only an edge from a to d
		assertTrue(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
		assertFalse(_kb.hasPropertyValue(_a, _p, _e));
		assertFalse(_kb.hasPropertyValue(_b, _p, _e));
		assertFalse(_kb.hasPropertyValue(_c, _p, _e));
		assertFalse(_kb.hasPropertyValue(_a, _p, _f));
		assertFalse(_kb.hasPropertyValue(_b, _p, _f));
		assertFalse(_kb.hasPropertyValue(_c, _p, _f));
	}

	@Test
	public void addEdgeToMergedSubjectWithExistingEdge()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d, _e);

		// a is either b or c
		_kb.addType(_a, oneOf(_b, _c));

		_kb.addType(_b, some(_p, oneOf(_d, _e)));
		_kb.addType(_c, some(_p, oneOf(_d, _e)));

		assertTrue(_kb.isConsistent());

		// no edges to d
		assertFalse(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
		assertFalse(_kb.hasPropertyValue(_a, _p, _e));
		assertFalse(_kb.hasPropertyValue(_b, _p, _e));
		assertFalse(_kb.hasPropertyValue(_c, _p, _e));

		final ATermAppl aMergedTo = _kb.getABox().getIndividual(_a).getMergedTo().getName();
		final ATermAppl aMergedToSucc = _kb.getABox().getIndividual(aMergedTo).getOutEdges().edgeAt(0).getToName();
		final ATermAppl aMergedToNotSucc = aMergedToSucc.equals(_d) ? _e : _d;

		_kb.addPropertyValue(_p, _a, aMergedToSucc);

		assertTrue(_kb.isConsistent());

		// there is only an edge from a to aMergedToSucc
		assertTrue(_kb.hasPropertyValue(_a, _p, aMergedToSucc));
		assertFalse(_kb.hasPropertyValue(_a, _p, aMergedToNotSucc));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _e));
		assertFalse(_kb.hasPropertyValue(_c, _p, _e));
	}

	@Test
	public void removeEdgeFromMergedObject()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d);

		_kb.addType(_a, oneOf(_b, _c));
		_kb.addPropertyValue(_p, _d, _a);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.hasPropertyValue(_d, _p, _a));
		assertFalse(_kb.hasPropertyValue(_d, _p, _b));
		assertFalse(_kb.hasPropertyValue(_d, _p, _c));

		_kb.removePropertyValue(_p, _d, _a);

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.hasPropertyValue(_d, _p, _a));
		assertFalse(_kb.hasPropertyValue(_d, _p, _b));
		assertFalse(_kb.hasPropertyValue(_d, _p, _c));
	}

	@Test
	public void removeEdgeFromMergedSubject()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d);

		_kb.addType(_a, oneOf(_b, _c));

		assertTrue(_kb.isConsistent());
		_kb.addPropertyValue(_p, _a, _d);

		assertTrue(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));

		_kb.removePropertyValue(_p, _a, _d);

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
	}

	@Test
	public void removeEdgeFromMergedSubjectObject()
	{
		objectProperties(_p);
		individuals(_a, _b, _c, _d, _e, _f);

		_kb.addType(_a, oneOf(_b, _c));
		_kb.addType(_d, oneOf(_e, _f));
		_kb.addPropertyValue(_p, _a, _d);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
		assertFalse(_kb.hasPropertyValue(_a, _p, _e));
		assertFalse(_kb.hasPropertyValue(_b, _p, _e));
		assertFalse(_kb.hasPropertyValue(_c, _p, _e));
		assertFalse(_kb.hasPropertyValue(_a, _p, _f));
		assertFalse(_kb.hasPropertyValue(_b, _p, _f));
		assertFalse(_kb.hasPropertyValue(_c, _p, _f));

		_kb.removePropertyValue(_p, _a, _d);

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.hasPropertyValue(_a, _p, _d));
		assertFalse(_kb.hasPropertyValue(_b, _p, _d));
		assertFalse(_kb.hasPropertyValue(_c, _p, _d));
		assertFalse(_kb.hasPropertyValue(_a, _p, _e));
		assertFalse(_kb.hasPropertyValue(_b, _p, _e));
		assertFalse(_kb.hasPropertyValue(_c, _p, _e));
		assertFalse(_kb.hasPropertyValue(_a, _p, _f));
		assertFalse(_kb.hasPropertyValue(_b, _p, _f));
		assertFalse(_kb.hasPropertyValue(_c, _p, _f));
	}

	@Test
	public void mergeManyIndividuals()
	{
		individuals(_a);
		objectProperties(_p);

		final int N = 5000;

		final ATermAppl[] b = new ATermAppl[N];
		final ATermAppl[] c = new ATermAppl[N];

		_kb.addObjectProperty(_p);
		_kb.addFunctionalProperty(_p);

		_kb.addIndividual(_a);

		for (int i = 0; i < N; i++)
		{
			b[i] = term("b" + i);
			_kb.addIndividual(b[i]);
			c[i] = term("c" + i);
			_kb.addIndividual(c[i]);

			if (i == 0)
			{
				_kb.addPropertyValue(_p, _a, b[i]);
				_kb.addPropertyValue(_p, _a, c[i]);
			}
			else
			{
				_kb.addPropertyValue(_p, b[i - 1], b[i]);
				_kb.addPropertyValue(_p, c[i - 1], c[i]);
			}
		}

		assertTrue(_kb.isConsistent());
	}
}
