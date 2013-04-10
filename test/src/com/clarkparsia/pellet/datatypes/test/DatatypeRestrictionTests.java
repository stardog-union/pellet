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

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.test.AbstractKBTests;

import aterm.ATermAppl;

public class DatatypeRestrictionTests extends AbstractKBTests {
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DatatypeRestrictionTests.class);
	}

	@Test
	public void simpleRestriction() {
		classes(C);
		dataProperties(p);
		individuals(a, b);

		kb.addDatatypeDefinition(D, restrict(INTEGER, minInclusive(literal(1))));
		kb.addEquivalentClass(C, some(p, D));
		kb.addPropertyValue(p, a, literal(2));
		kb.addPropertyValue(p, b, literal(3));

		assertTrue(kb.isType(a, C));
		assertTrue(kb.isType(b, C));
	}

	@Test
	public void nestedRestriction() {
		classes(C);
		dataProperties(p);
		individuals(a, b);

		kb.addDatatypeDefinition(E, restrict(INTEGER, maxInclusive(literal(2))));
		kb.addDatatypeDefinition(D, restrict(E, minInclusive(literal(1))));
		kb.addEquivalentClass(C, some(p, D));
		kb.addPropertyValue(p, a, literal(2));
		kb.addPropertyValue(p, b, literal(3));

		assertTrue(kb.isType(a, C));
		assertFalse(kb.isType(b, C));
	}

	@Test(expected = RuntimeException.class)
	public void invalidRestriction() {
		classes(C);
		dataProperties(p);
		individuals(a, b);

		kb.addDatatypeDefinition(E, oneOf(literal(1), literal(2), literal(3)));
		kb.addDatatypeDefinition(D, restrict(E, minInclusive(literal(1))));
		kb.addEquivalentClass(C, some(p, D));
		kb.addPropertyValue(p, a, literal(2));
		kb.addPropertyValue(p, b, literal(3));

		assertTrue(kb.isType(a, C));
		assertFalse(kb.isType(b, C));
	}

	@Test
	public void invalidLiteralBuiltInDatatype() {
		dataProperties(p);
		individuals(a);

		kb.addRange(p, INTEGER);
		kb.addPropertyValue(p, a, literal("-1", POSITIVE_INTEGER));

		assertFalse(kb.isConsistent());
	}

	@Test
	public void invalidLiteralRestrictedDatatype() {
		dataProperties(p);
		individuals(a);

		ATermAppl uri = term("http//example.com/datatype");

		kb.addRange(p, INTEGER);
		kb.addDatatypeDefinition(uri, restrict(INTEGER, minExclusive(literal(0))));
		kb.addPropertyValue(p, a, literal("-1", uri));

		assertFalse(kb.isConsistent());
	}

	@Test
	public void validLiteralRestrictedDatatype() {
		dataProperties(p);
		individuals(a);

		ATermAppl uri = term("http//example.com/datatype");

		kb.addRange(p, INTEGER);
		kb.addDatatypeDefinition(uri, restrict(INTEGER, minExclusive(literal(0))));
		kb.addPropertyValue(p, a, literal("1", uri));

		assertTrue(kb.isConsistent());
	}

	@Test
	public void validLiteralStringRestriction1() {
		dataProperties(p);
		individuals(a);

		ATermAppl uri = term("http//example.com/datatype");

		kb.addDatatypeDefinition(uri, oneOf(literal("a"), literal("b")));
		kb.addRange(p, uri);
		kb.addPropertyValue(p, a, literal("a"));

		assertTrue(kb.isConsistent());
	}

	@Test
	public void invalidLiteralStringRestriction() {
		assumeTrue(PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY);
		
		dataProperties(p);
		individuals(a);

		ATermAppl uri = term("http//example.com/datatype");

		kb.addDatatypeDefinition(uri, oneOf(literal("a"), literal("b")));
		kb.addRange(p, uri);
		kb.addPropertyValue(p, a, literal("a", uri));

		assertFalse(kb.isConsistent());
	}

	@Test
	public void validLiteralStringRestriction2() {
		dataProperties(p);
		individuals(a, b, c);

		ATermAppl uri = term("http//example.com/datatype");

		kb.addDatatypeDefinition(uri, oneOf(literal("a"), literal("b")));
		kb.addRange(p, uri);
		kb.addPropertyValue(p, a, literal("c"));
		kb.addAllDifferent(list(a, b, c));

		assertFalse(kb.isConsistent());
	}
}
