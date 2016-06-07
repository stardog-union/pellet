// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.maxExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.DNF;
import com.clarkparsia.pellet.datatypes.Datatypes;
import java.util.ArrayList;
import java.util.List;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Comparators;

public class ATermTests
{
	// Constants to be used as concepts
	public static ATermAppl _a = term("a");
	public static ATermAppl _b = term("b");
	public static ATermAppl _c = term("c");
	public static ATermAppl _d = term("d");

	// Constants to be used as roles
	public static ATermAppl _p = term("p");
	public static ATermAppl _q = term("q");
	public static ATermAppl _r = term("r");

	public static ATermAppl _d1 = restrict(Datatypes.INTEGER, minInclusive(literal(1)));
	public static ATermAppl _d2 = restrict(Datatypes.INTEGER, maxExclusive(literal(2)));
	public static ATermAppl _d3 = Datatypes.INTEGER;

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(ATermTests.class);
	}

	@Test
	public void testComparator()
	{
		// test case for #423

		// the following two terms are known to have equivalent hascodes with aterm 1.6
		final ATermAppl a = term("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Carboplatin_Paclitaxel_ZD-6474");
		final ATermAppl b = term("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Henna");
		// an arbitrary term that is known to have a different hahshcode
		final ATermAppl c = term("c");

		assertTrue(a.hashCode() == b.hashCode());
		assertFalse(a.hashCode() == c.hashCode());

		assertTrue(0 == Comparators.termComparator.compare(a, a));
		assertFalse(0 == Comparators.termComparator.compare(a, b));
		assertFalse(0 == Comparators.termComparator.compare(a, c));
	}

	@Test
	public void testNNF()
	{
		testNNF(not(some(_p, _c)), all(_p, not(_c)));
		testNNF(not(all(_p, _c)), some(_p, not(_c)));

		testNNF(not(min(_p, 1, _c)), max(_p, 0, _c));

		testNNF(not(max(_p, 0, _c)), min(_p, 1, _c));
		testNNF(not(max(_p, 1, not(some(_p, _c)))), min(_p, 2, all(_p, not(_c))));

		testNNF(and(_d1, _d2, _d3), and(_d1, _d2, _d3));
		testNNF(not(and(_d1, _d2, _d3)), or(not(_d1), not(_d2), not(_d3)));
		testNNF(some(_p, and(_d1, _d3)), some(_p, and(_d1, _d3)));
		testNNF(not(some(_p, and(_d1, _d3))), all(_p, or(not(_d1), not(_d3))));
	}

	private void testNNF(final ATermAppl c, final ATermAppl expected)
	{
		assertEquals(expected, ATermUtils.nnf(c));
	}

	@Test
	public void testNormalize()
	{
		testNormalize(some(_p, not(_c)), not(all(_p, _c)));

		testNormalize(all(_p, not(_c)), all(_p, not(_c)));
		testNormalize(all(_p, some(_q, _c)), all(_p, not(all(_q, not(_c)))));

		testNormalize(min(_p, 1, not(not(_c))), min(_p, 1, _c));
		testNormalize(min(_p, 1, some(_p, _c)), min(_p, 1, not(all(_p, not(_c)))));
		testNormalize(min(_p, 0, _c), ATermUtils.TOP);
		testNormalize(min(_p, 1, ATermUtils.BOTTOM), ATermUtils.BOTTOM);

		testNormalize(max(_p, 0, _c), not(min(_p, 1, _c)));
		testNormalize(max(_p, 1, _c), not(min(_p, 2, _c)));
		testNormalize(max(_p, 1, not(some(_p, not(not(_c))))), not(min(_p, 2, all(_p, not(_c)))));
		testNormalize(max(_p, 1, ATermUtils.BOTTOM), ATermUtils.TOP);

		testNormalize(some(_p, not(value(_a))), not(all(_p, value(_a))));

		testNormalize(some(_p, not(_d1)), not(all(_p, _d1)));

		testNormalize(all(_p, not(_d1)), all(_p, not(_d1)));
		testNormalize(all(_p, some(_q, _d1)), all(_p, not(all(_q, not(_d1)))));
	}

	private void testNormalize(final ATermAppl c, final ATermAppl expected)
	{
		assertEquals(expected, ATermUtils.normalize(c));
	}

	@Test
	public void testDoubleNormalize()
	{
		testDoubleNormalize(and(_a, _b, _c, _d), and(_d, _c, _a, _b));
		testDoubleNormalize(and(_a, _b, _c, _d), and(_d, _c, _a, _b, _b, _d, _a, _c));
		testDoubleNormalize(and(_a, and(_b, _c)), and(_a, _b, _c));

		testDoubleNormalize(or(_a, _b, _c, _d), or(_d, _c, _a, _b));
		testDoubleNormalize(or(_a, _b, _c, _d), or(_d, _c, _a, _b, _b, _d, _a, _c));
		testDoubleNormalize(or(_a, or(_b, _c)), or(_a, _b, _c));

	}

	private void testDoubleNormalize(final ATermAppl c1, final ATermAppl c2)
	{
		assertEquals(ATermUtils.normalize(c1), ATermUtils.normalize(c2));
	}

	@Test
	public void testDNF()
	{
		testDNF(_a, _a);
		testDNF(not(_a), not(_a));
		testDNF(and(_a, _b), and(_a, _b));
		testDNF(or(_a, _b), or(_a, _b));
		testDNF(or(_a, and(_b, _c)), or(_a, and(_b, _c)));
		testDNF(and(_a, or(_b, _c)), or(and(_a, _b), and(_a, _c)));
		testDNF(and(or(_a, _b), or(_b, _c)), or(and(_a, _b), and(_a, _c), _b, and(_b, _c)));
		testDNF(and(or(_a, _b), or(_c, _d)), or(and(_a, _c), and(_a, _d), and(_b, _c), and(_b, _d)));
		testDNF(and(_a, or(and(_b, _c), _d)), or(and(_a, _b, _c), and(_a, _d)));
	}

	private void testDNF(final ATermAppl c, final ATermAppl expected)
	{
		assertEquals(canonicalize(expected), DNF.dnf(c));
	}

	private ATermAppl canonicalize(final ATermAppl term)
	{
		if (ATermUtils.isAnd(term) || ATermUtils.isOr(term))
		{
			final List<ATermAppl> list = new ArrayList<>();
			for (ATermList l = (ATermList) term.getArgument(0); !l.isEmpty(); l = l.getNext())
				list.add(canonicalize((ATermAppl) l.getFirst()));
			final ATermList args = ATermUtils.toSet(list);
			if (ATermUtils.isAnd(term))
				return ATermUtils.makeAnd(args);
			else
				return ATermUtils.makeOr(args);
		}
		else
			return term;
	}

	@Test
	public void testFindPrimitives()
	{
		testFindPrimitives(some(_p, not(_c)), new ATermAppl[] { _c });

		testFindPrimitives(and(_c, _b, all(_p, _a)), new ATermAppl[] { _a, _b, _c });
		testFindPrimitives(max(_p, 1, not(some(_p, or(_a, _b)))), new ATermAppl[] { _a, _b });
		testFindPrimitives(min(_p, 2, or(_a, and(_b, not(_c)))), new ATermAppl[] { _a, _b, _c });
		testFindPrimitives(and(some(_p, ATermUtils.TOP), all(_p, _a), and(some(_p, value(_r)), or(self(_p), max(_p, 1, _b)))), new ATermAppl[] { ATermUtils.TOP, _a, _b });
		testFindPrimitives(and(_d1, _d2, _d3), new ATermAppl[] { _d3 });
		testFindPrimitives(not(and(not(_d1), _d2, _d3)), new ATermAppl[] { _d3 });
		testFindPrimitives(some(_p, and(_d1, _d3)), new ATermAppl[] { _d3 });
	}

	private void testFindPrimitives(final ATermAppl c, final ATermAppl[] expected)
	{
		assertIteratorValues(ATermUtils.findPrimitives(c).iterator(), expected);
	}
}
