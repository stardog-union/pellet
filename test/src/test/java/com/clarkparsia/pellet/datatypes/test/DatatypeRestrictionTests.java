package com.clarkparsia.pellet.datatypes.test;

import static com.clarkparsia.pellet.datatypes.Datatypes.INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.POSITIVE_INTEGER;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.maxInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.minExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import junit.framework.JUnit4TestAdapter;
import openllet.aterm.ATermAppl;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.test.AbstractKBTests;

public class DatatypeRestrictionTests extends AbstractKBTests
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(DatatypeRestrictionTests.class);
	}

	@Test
	public void simpleRestriction()
	{
		classes(_C);
		dataProperties(_p);
		individuals(_a, _b);

		_kb.addDatatypeDefinition(_D, restrict(INTEGER, minInclusive(literal(1))));
		_kb.addEquivalentClass(_C, some(_p, _D));
		_kb.addPropertyValue(_p, _a, literal(2));
		_kb.addPropertyValue(_p, _b, literal(3));

		assertTrue(_kb.isType(_a, _C));
		assertTrue(_kb.isType(_b, _C));
	}

	@Test
	public void nestedRestriction()
	{
		classes(_C);
		dataProperties(_p);
		individuals(_a, _b);

		_kb.addDatatypeDefinition(_E, restrict(INTEGER, maxInclusive(literal(2))));
		_kb.addDatatypeDefinition(_D, restrict(_E, minInclusive(literal(1))));
		_kb.addEquivalentClass(_C, some(_p, _D));
		_kb.addPropertyValue(_p, _a, literal(2));
		_kb.addPropertyValue(_p, _b, literal(3));

		assertTrue(_kb.isType(_a, _C));
		assertFalse(_kb.isType(_b, _C));
	}

	@Test(expected = RuntimeException.class)
	public void invalidRestriction()
	{
		classes(_C);
		dataProperties(_p);
		individuals(_a, _b);

		_kb.addDatatypeDefinition(_E, oneOf(literal(1), literal(2), literal(3)));
		_kb.addDatatypeDefinition(_D, restrict(_E, minInclusive(literal(1))));
		_kb.addEquivalentClass(_C, some(_p, _D));
		_kb.addPropertyValue(_p, _a, literal(2));
		_kb.addPropertyValue(_p, _b, literal(3));

		assertTrue(_kb.isType(_a, _C));
		assertFalse(_kb.isType(_b, _C));
	}

	@Test
	public void invalidLiteralBuiltInDatatype()
	{
		dataProperties(_p);
		individuals(_a);

		_kb.addRange(_p, INTEGER);
		_kb.addPropertyValue(_p, _a, literal("-1", POSITIVE_INTEGER));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void invalidLiteralRestrictedDatatype()
	{
		dataProperties(_p);
		individuals(_a);

		final ATermAppl uri = term("http//example.com/datatype");

		_kb.addRange(_p, INTEGER);
		_kb.addDatatypeDefinition(uri, restrict(INTEGER, minExclusive(literal(0))));
		_kb.addPropertyValue(_p, _a, literal("-1", uri));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void validLiteralRestrictedDatatype()
	{
		dataProperties(_p);
		individuals(_a);

		final ATermAppl uri = term("http//example.com/datatype");

		_kb.addRange(_p, INTEGER);
		_kb.addDatatypeDefinition(uri, restrict(INTEGER, minExclusive(literal(0))));
		_kb.addPropertyValue(_p, _a, literal("1", uri));

		assertTrue(_kb.isConsistent());
	}

	@Test
	public void validLiteralStringRestriction1()
	{
		dataProperties(_p);
		individuals(_a);

		final ATermAppl uri = term("http//example.com/datatype");

		_kb.addDatatypeDefinition(uri, oneOf(literal("a"), literal("b")));
		_kb.addRange(_p, uri);
		_kb.addPropertyValue(_p, _a, literal("a"));

		assertTrue(_kb.isConsistent());
	}

	@Test
	public void invalidLiteralStringRestriction()
	{
		assumeTrue(PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY);

		dataProperties(_p);
		individuals(_a);

		final ATermAppl uri = term("http//example.com/datatype");

		_kb.addDatatypeDefinition(uri, oneOf(literal("a"), literal("b")));
		_kb.addRange(_p, uri);
		_kb.addPropertyValue(_p, _a, literal("a", uri));

		assertFalse(_kb.isConsistent());
	}

	@Test
	public void validLiteralStringRestriction2()
	{
		dataProperties(_p);
		individuals(_a, _b, _c);

		final ATermAppl uri = term("http//example.com/datatype");

		_kb.addDatatypeDefinition(uri, oneOf(literal("a"), literal("b")));
		_kb.addRange(_p, uri);
		_kb.addPropertyValue(_p, _a, literal("c"));
		_kb.addAllDifferent(list(_a, _b, _c));

		assertFalse(_kb.isConsistent());
	}
}
