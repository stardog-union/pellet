package com.clarkparsia.pellet.datatypes.test;

import static com.clarkparsia.pellet.datatypes.Datatypes.ANY_URI;
import static com.clarkparsia.pellet.datatypes.Datatypes.BYTE;
import static com.clarkparsia.pellet.datatypes.Datatypes.DATE_TIME;
import static com.clarkparsia.pellet.datatypes.Datatypes.DECIMAL;
import static com.clarkparsia.pellet.datatypes.Datatypes.DOUBLE;
import static com.clarkparsia.pellet.datatypes.Datatypes.FLOAT;
import static com.clarkparsia.pellet.datatypes.Datatypes.INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.LITERAL;
import static com.clarkparsia.pellet.datatypes.Datatypes.NAME;
import static com.clarkparsia.pellet.datatypes.Datatypes.NEGATIVE_INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.NON_NEGATIVE_INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.NON_POSITIVE_INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.PLAIN_LITERAL;
import static com.clarkparsia.pellet.datatypes.Datatypes.POSITIVE_INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.STRING;
import static com.clarkparsia.pellet.datatypes.Datatypes.TOKEN;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.maxExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.maxInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.minExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertSubClass;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Literal;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.DatatypeReasonerImpl;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.utils.TermFactory;

public class DatatypeReasonerTests {

	private static BigDecimal decimal(String value) {
		return new BigDecimal(value);
	}

	private static Collection<ATermAppl> getSatisfiableDecimalEnumerations() {
		final Collection<ATermAppl> dataranges = Arrays.asList(oneOf(literal("1.0", DECIMAL), literal("2.0", DECIMAL),
		                literal("3.0", DECIMAL)), oneOf(literal("2.0", DECIMAL), literal("4.0", DECIMAL), literal(
		                "6.0", DECIMAL)));
		return dataranges;
	}

	private static Collection<ATermAppl> getSatisfiableDecimalRanges() {
		final ATermAppl dt1 = restrict(DECIMAL, minInclusive(literal("1.0", DECIMAL)), maxInclusive(literal("3.0",
		                DECIMAL)));
		final ATermAppl dt2 = restrict(DECIMAL, minInclusive(literal("2.0", DECIMAL)), maxInclusive(literal("4.0",
		                DECIMAL)));
		final Collection<ATermAppl> dataranges = Arrays.asList(dt1, dt2);
		return dataranges;
	}

	private static Collection<ATermAppl> getUnsatisfiableDecimalEnumerations() {
		final Collection<ATermAppl> dataranges = Arrays.asList(oneOf(literal("1.0", DECIMAL), literal("2.0", DECIMAL),
		                literal("3.0", DECIMAL)), oneOf(literal("4.0", DECIMAL), literal("5.0", DECIMAL), literal(
		                "6.0", DECIMAL)));
		return dataranges;
	}

	private static Collection<ATermAppl> getUnsatisfiableDecimalRanges() {
		final ATermAppl dt1 = restrict(DECIMAL, minInclusive(literal("1.0", DECIMAL)), maxInclusive(literal("3.0",
		                DECIMAL)));
		final ATermAppl dt2 = restrict(DECIMAL, minInclusive(literal("4.0", DECIMAL)), maxInclusive(literal("6.0",
		                DECIMAL)));
		final Collection<ATermAppl> dataranges = Arrays.asList(dt1, dt2);
		return dataranges;
	}

	private ABox abox;

	private DatatypeReasoner reasoner;

	/**
	 * Verify that overlapping decimal ranges for a single variable are satisfiable.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void oneVSatisfiableDecimalRanges() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Literal x = new Literal(term("x"), null, abox, DependencySet.INDEPENDENT);
		for (ATermAppl a : getSatisfiableDecimalRanges()) {
			x.addType(a, DependencySet.INDEPENDENT);
		}

		assertTrue(reasoner.isSatisfiable(singleton(x), Collections.<Literal, Set<Literal>> emptyMap()));
	}

	/**
	 * Verify that overlapping decimal enumerations for a single variable are satisfiable.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void oneVSatisfiableEnumerations() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Literal x = new Literal(term("x"), null, abox, DependencySet.INDEPENDENT);
		for (ATermAppl a : getSatisfiableDecimalEnumerations()) {
			x.addType(a, DependencySet.INDEPENDENT);
		}

		assertTrue(reasoner.isSatisfiable(singleton(x), Collections.<Literal, Set<Literal>> emptyMap()));
	}

	/**
	 * Verify that non-overlapping decimal ranges for a single variable are unsatisfiable.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void oneVUnsatisfiableDecimalRanges() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Literal x = new Literal(term("x"), null, abox, DependencySet.INDEPENDENT);
		for (ATermAppl a : getUnsatisfiableDecimalRanges()) {
			x.addType(a, DependencySet.INDEPENDENT);
		}

		assertFalse(reasoner.isSatisfiable(singleton(x), Collections.<Literal, Set<Literal>> emptyMap()));
	}

	/**
	 * Verify that non-overlapping decimal enumerations for a single variable are unsatisfiable.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void oneVUnsatisfiableEnumerations() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Literal x = new Literal(term("x"), null, abox, DependencySet.INDEPENDENT);
		for (ATermAppl a : getUnsatisfiableDecimalEnumerations()) {
			x.addType(a, DependencySet.INDEPENDENT);
		}

		assertFalse(reasoner.isSatisfiable(singleton(x), Collections.<Literal, Set<Literal>> emptyMap()));
	}

	@Before
	public void reset() {
		reasoner = new DatatypeReasonerImpl();
		abox = new ABox(null);
	}

	/**
	 * Verify that overlapping decimal enumerations are satisfiable when evaluated independent of variables and
	 * constants.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void unarySatisfiableDecimalEnumerations() throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		final Collection<ATermAppl> dataranges = getSatisfiableDecimalEnumerations();
		assertTrue(reasoner.isSatisfiable(dataranges));
	}

	/**
	 * Verify that overlapping decimal ranges are satisfiable when evaluated independent of variables and constants
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void unarySatisfiableDecimalRanges() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> dataranges = getSatisfiableDecimalRanges();
		assertTrue(reasoner.isSatisfiable(dataranges));
	}

	/**
	 * Verify that non-overlapping decimal enumerations are unsatisfiable when evaluated independent of variables and
	 * constants.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void unaryUnsatisfiableDecimalEnumerations() throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		final Collection<ATermAppl> dataranges = getUnsatisfiableDecimalEnumerations();
		assertFalse(reasoner.isSatisfiable(dataranges));
	}

	/**
	 * Verify that non-overlapping decimal ranges are unsatisfiable when evaluated independent of variables and
	 * constants.
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void unaryUnsatisfiableDecimalRanges() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> dataranges = getUnsatisfiableDecimalRanges();
		assertFalse(reasoner.isSatisfiable(dataranges));
	}

	/**
	 * Verify that a decimal range contains correct constants
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void unaryValuesInDecimalRange() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final ATermAppl type = restrict(DECIMAL, minInclusive(literal("1.0", DECIMAL)), maxInclusive(literal("2.5",
		                DECIMAL)));
		final Collection<ATermAppl> types = singleton(type);
		assertFalse(reasoner.isSatisfiable(types, decimal("0.99")));
		assertTrue(reasoner.isSatisfiable(types, 1));
		assertTrue(reasoner.isSatisfiable(types, 2));
		assertTrue(reasoner.isSatisfiable(types, decimal("2.5")));
		assertFalse(reasoner.isSatisfiable(types, decimal("2.51")));
	}

	/**
	 * Verify that a named decimal range contains correct constants
	 * 
	 * @throws UnrecognizedDatatypeException
	 * @throws InvalidLiteralException
	 * @throws InvalidConstrainingFacetException
	 */
	@Test
	public void unaryValuesInNamedDecimalRange() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final ATermAppl rdt = restrict(DECIMAL, minInclusive(literal("1.0", DECIMAL)), maxInclusive(literal("2.5",
		                DECIMAL)));
		final ATermAppl name = term("newDt");
		final Collection<ATermAppl> types = singleton(name);

		assertTrue(reasoner.define(name, rdt));

		assertFalse(reasoner.isSatisfiable(types, decimal("0.99")));
		assertTrue(reasoner.isSatisfiable(types, 1));
		assertTrue(reasoner.isSatisfiable(types, 2));
		assertTrue(reasoner.isSatisfiable(types, decimal("2.5")));
		assertFalse(reasoner.isSatisfiable(types, decimal("2.51")));
	}

	public void assertSatisfiable(ATermAppl... dataranges) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		testSatisfiability(true, dataranges);
	}

	public void assertUnsatisfiable(ATermAppl... dataranges) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		testSatisfiability(false, dataranges);
	}

	public void testSatisfiability(boolean isSatisfiable, ATermAppl... dataranges)
	                throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException {
		assertTrue(isSatisfiable == reasoner.isSatisfiable(Arrays.asList(dataranges)));
	}

	@Test
	public void intersectIntegerDecimal() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable(INTEGER, DECIMAL);
	}

	@Test
	public void intersectIntegerNotDecimal() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertUnsatisfiable(INTEGER, not(DECIMAL));
	}

	@Test
	public void intersectIntegerBytePositiveInteger() throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		assertSatisfiable(INTEGER, BYTE, POSITIVE_INTEGER);
	}

	@Test
	public void intersectPositiveNegativeInteger() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertUnsatisfiable(NEGATIVE_INTEGER, POSITIVE_INTEGER);
	}

	@Test
	public void intersectNonPositiveNonNegativeInteger() throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		assertSatisfiable(NON_NEGATIVE_INTEGER, NON_POSITIVE_INTEGER);
	}

	@Test
	public void intersectIntegerFloat() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertUnsatisfiable(INTEGER, FLOAT);
	}

	@Test
	public void intersectFloatDouble() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertUnsatisfiable(FLOAT, DOUBLE);
	}

	@Test
	public void intersectIntegerNotFloat() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable(INTEGER, not(FLOAT));
	}
	
	@Test
	public void intersectIntegerIntervalNotInteger() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl integerInterval = restrict( INTEGER, 
						minInclusive( literal( 0 ) ),
						maxInclusive( literal( 1 ) ) );
		assertUnsatisfiable( integerInterval, not( INTEGER ) );
	}
	
	@Test
	public void intersectFloatIntervalNotFloat() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl floatInterval = restrict( FLOAT, 
						minInclusive( literal( 0.0f ) ),
						maxInclusive( literal( 1.0f ) ) );
		assertUnsatisfiable( floatInterval, not( FLOAT ) );
	}
	
	@Test
	public void intersectNegatedIntegerInterval() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl intInterval1 = restrict( INTEGER, 
						minInclusive( literal( 0 ) ),
						maxInclusive( literal( 1 ) ) );
		ATermAppl intInterval2 = restrict( INTEGER, 
 						minInclusive( literal( 2 ) ),
 						maxInclusive( literal( 3 ) ) );		
		assertSatisfiable( intInterval1, not( intInterval2 ) );
	}
	
	@Test
	public void intersectNegatedDoubleInterval() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl doubleInterval1 = restrict( DOUBLE, 
						minInclusive( literal( 0.0 ) ),
						maxInclusive( literal( 1.0 ) ) );
		ATermAppl doubleInterval2 = restrict( DOUBLE, 
 						minInclusive( literal( 2.0 ) ),
 						maxInclusive( literal( 3.0 ) ) );		
		assertSatisfiable( doubleInterval1, not( doubleInterval2 ) );
	}
	
	@Test
	public void intersectNegatedFloatInterval() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl floatInterval1 = restrict( FLOAT, 
						minInclusive( literal( 0.0f ) ),
						maxInclusive( literal( 1.0f ) ) );
		ATermAppl floatInterval2 = restrict( FLOAT, 
 						minInclusive( literal( 2.0f ) ),
 						maxInclusive( literal( 3.0f ) ) );		
		assertSatisfiable( floatInterval1, not( floatInterval2 ) );
	}
	
	@Test
	public void intersectFloatAndFloatInterval() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl floatInterval = restrict( FLOAT, 
						minInclusive( literal( 0.0f ) ),
						maxInclusive( literal( 1.0f ) ) );	
		assertSatisfiable( floatInterval, FLOAT, LITERAL );
	}
	
	@Test
	public void intersectDoubleIntervalNotDouble() throws InvalidConstrainingFacetException,
			InvalidLiteralException, UnrecognizedDatatypeException {
		ATermAppl doubleInterval = restrict( DOUBLE, 
						minInclusive( literal( 0.0 ) ),
						maxInclusive( literal( 1.0 ) ) );
		assertUnsatisfiable( doubleInterval, not( DOUBLE ) );
	}

	@Test
	public void intersectIntegerNotByte() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable(INTEGER, not(BYTE));
	}

	@Test
	public void intersectDoubleNotIntegerNotFloat() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable(DOUBLE, not(INTEGER), not(FLOAT));
	}

	@Test
	public void intersectNotIntegerByte() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertUnsatisfiable(not(INTEGER), BYTE);
	}

	@Test
	public void intersectNotIntegerNotByte() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable(BYTE, not(POSITIVE_INTEGER), not(NEGATIVE_INTEGER));
	}

	@Test
	public void intersectIntegerNotLiteral() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertUnsatisfiable(BYTE, not(LITERAL));
	}

	@Test
	public void intersectIntegerNegatedEmptyIntegerRange() throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		final ATermAppl type = not(restrict(INTEGER, minInclusive(literal("2", INTEGER)), maxInclusive(literal("1",
		                INTEGER))));

		final Collection<ATermAppl> types = Arrays.asList(INTEGER, type);

		assertTrue(reasoner.isSatisfiable(types));

		assertTrue(reasoner.containsAtLeast(2, types));
	}

	@Test
	public void intersectIntegerNegatedIntegerValue() throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(INTEGER, not(value(literal(3))));

		assertTrue(reasoner.isSatisfiable(types));

		assertTrue(reasoner.containsAtLeast(2, types));

		assertTrue(reasoner.isSatisfiable(types, reasoner.getValue(literal(1))));

		assertFalse(reasoner.isSatisfiable(types, reasoner.getValue(literal(3))));
	}

	@Test
	public void intersectTextNegatedTextValue() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(PLAIN_LITERAL, not(value(literal("http://example.org"))));

		assertTrue(reasoner.isSatisfiable(types));

		assertTrue(reasoner.containsAtLeast(2, types));

		assertTrue(reasoner.isSatisfiable(types, literal("http://example.com")));

		assertFalse(reasoner.isSatisfiable(types, literal("http://example.org")));
	}

	@Test
	public void intersectStringToken() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable( STRING, TOKEN );
	}

	@Test
	public void intersectNameToken() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		assertSatisfiable( NAME, TOKEN );
	}

	@Test
	public void intersectAnyURINegatedTextValue() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(ANY_URI, not(value(literal("http://example.org"))));

		assertTrue(reasoner.isSatisfiable(types));

		assertTrue(reasoner.containsAtLeast(2, types));

		assertTrue(reasoner.isSatisfiable(types, literal("http://example.com", Datatypes.ANY_URI)));

		assertTrue(reasoner.isSatisfiable(types, literal("http://example.org", Datatypes.ANY_URI)));
	}

	@Test
	public void intersectAnyURINegatedURIValue() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(ANY_URI, not(value(literal("http://example.org", ANY_URI))));

		assertTrue(reasoner.isSatisfiable(types));

		assertTrue(reasoner.containsAtLeast(2, types));

		assertTrue(reasoner.isSatisfiable(types, literal("http://example.com", Datatypes.ANY_URI)));

		assertFalse(reasoner.isSatisfiable(types, literal("http://example.org", Datatypes.ANY_URI)));
	}

	@Test
	public void unionWithEmptyDatatype() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final ATermAppl dt1 = restrict(INTEGER, minInclusive(literal(1)), maxInclusive(literal(3)));
		final ATermAppl dt2 = restrict(INTEGER, minInclusive(literal(4)), maxInclusive(literal(6)));
		final ATermAppl dt3 = and(NEGATIVE_INTEGER, POSITIVE_INTEGER);
		ATermAppl datarange = or(dt1, dt2, dt3);

		assertTrue(reasoner.isSatisfiable(Collections.singleton(datarange)));

		assertTrue(reasoner.containsAtLeast(6, Collections.singleton(datarange)));

		assertFalse(reasoner.containsAtLeast(7, Collections.singleton(datarange)));
	}

	@Test
	public void emptyFloatInterval() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl emptyFloatRestriction = restrict(FLOAT, minExclusive(literal(Float.intBitsToFloat(0x00000000))),
		                maxExclusive(literal(Float.intBitsToFloat(0x00000001))));

		assertFalse(reasoner.isSatisfiable(Collections.singleton(emptyFloatRestriction)));
	}

	@Test
	public void floatRestrictionWithSevenValues() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl floatRestrictionWithSevenValues = restrict(FLOAT, minExclusive(literal(Float
		                .intBitsToFloat(0x00000000))), maxExclusive(literal(Float.intBitsToFloat(0x00000008))));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(floatRestrictionWithSevenValues)));

		assertTrue(reasoner.containsAtLeast(7, Collections.singleton(floatRestrictionWithSevenValues)));

		assertFalse(reasoner.containsAtLeast(8, Collections.singleton(floatRestrictionWithSevenValues)));
	}

	@Test
	public void floatExclusiveRandom() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final long seed = System.currentTimeMillis();
		final Random rand = new Random(seed);
		try {

			float high, low;
			high = rand.nextFloat();
			low = rand.nextFloat();
			if (low > high) {
				float tmp = low;
				low = high;
				high = tmp;
			}

			ATermAppl floatExclusiveRandom = restrict(FLOAT, minExclusive(literal(Float.valueOf(low))),
			                maxExclusive(literal(Float.valueOf(high))));

			// Both floats are known to be in (0,1) so this will work
			int size = Float.floatToIntBits(high) - Float.floatToIntBits(low) - 1;

			assertTrue(reasoner.isSatisfiable(Collections.singleton(floatExclusiveRandom)));

			assertTrue(reasoner.containsAtLeast(size, Collections.singleton(floatExclusiveRandom)));

			assertFalse(reasoner.containsAtLeast(size + 1, Collections.singleton(floatExclusiveRandom)));
		}
		catch (AssertionError e) {
			System.err.println("Random seed: " + seed);
			throw e;
		}
	}

	@Test
	public void floatTwoZeros() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl floatTwoZeros = restrict(FLOAT, minExclusive(literal(-Float.MIN_VALUE)),
		                maxExclusive(literal(Float.MIN_VALUE)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(floatTwoZeros)));

		assertTrue(reasoner.containsAtLeast(2, Collections.singleton(floatTwoZeros)));

		assertFalse(reasoner.containsAtLeast(3, Collections.singleton(floatTwoZeros)));
	}

	@Test
	public void floatInterval() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl floatInterval = restrict(FLOAT, minInclusive(literal(1.0f)), maxExclusive(literal(2.0f)));

		assertFalse(reasoner.isSatisfiable(Collections.singleton(not(floatInterval)), 1.0f));
		assertFalse(reasoner.isSatisfiable(Collections.singleton(not(floatInterval)), 1.5f));
		assertTrue(reasoner.isSatisfiable(Collections.singleton(not(floatInterval)), 2.0f));
	}

	@Test
	public void emptyIntegerInterval() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl integerExclusiveInterval = restrict(INTEGER, minExclusive(literal(0)), maxExclusive(literal(1)));

		assertFalse(reasoner.isSatisfiable(Collections.singleton(integerExclusiveInterval)));
	}

	@Test
	public void integerTwoValues() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl integerExclusiveInterval = restrict(INTEGER, minExclusive(literal(-1)), maxExclusive(literal(1)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(integerExclusiveInterval)));

		assertTrue(reasoner.containsAtLeast(1, Collections.singleton(integerExclusiveInterval)));

		assertFalse(reasoner.containsAtLeast(2, Collections.singleton(integerExclusiveInterval)));
	}

	@Test
	public void integerExclusiveIntervalExtreme() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl integerExclusiveInterval = restrict(INTEGER, minExclusive(literal(0)),
		                maxExclusive(literal(Integer.MAX_VALUE)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(integerExclusiveInterval)));

		assertTrue(reasoner.containsAtLeast(Integer.MAX_VALUE - 1, Collections.singleton(integerExclusiveInterval)));

		assertFalse(reasoner.containsAtLeast(Integer.MAX_VALUE, Collections.singleton(integerExclusiveInterval)));
	}

	public void integerExclusiveIntervalOverflow() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl integerExclusiveInterval = restrict(INTEGER, minExclusive(literal(Integer.MIN_VALUE)),
		                maxExclusive(literal(Integer.MAX_VALUE)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(integerExclusiveInterval)));

		assertTrue(reasoner.containsAtLeast(Integer.MAX_VALUE, Collections.singleton(integerExclusiveInterval)));
	}

	@Test
	public void integerExclusiveRandom() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {

		final long seed = System.currentTimeMillis();
		final Random rand = new Random(seed);
		try {

			int high, low;
			high = rand.nextInt();
			do {
				low = rand.nextInt();
				if (low > high) {
					int tmp = low;
					low = high;
					high = tmp;
				}
			}
			while (Long.valueOf(high) - Long.valueOf(low) > Integer.MAX_VALUE);

			ATermAppl integerExclusiveInterval = restrict(INTEGER, minExclusive(literal(low)),
			                maxExclusive(literal(high)));

			assertTrue(reasoner.isSatisfiable(Collections.singleton(integerExclusiveInterval)));

			assertTrue(reasoner.containsAtLeast(high - low - 1, Collections.singleton(integerExclusiveInterval)));

			assertFalse(reasoner.containsAtLeast(high - low, Collections.singleton(integerExclusiveInterval)));
		}
		catch (AssertionError e) {
			System.err.println("Random seed: " + seed);
			throw e;
		}
	}

	/* For bug #303 */
	@Test
	public void userDefinedDatatypes303a() {
		ATermAppl c = TermFactory.term("C");
		ATermAppl v = TermFactory.term("v");
		ATermAppl i = TermFactory.term("i");
		ATermAppl one = TermFactory.literal(1);

		KnowledgeBase kb = new KnowledgeBase();
		kb.addClass(c);
		kb.addDatatypeProperty(v);
		kb.addIndividual(i);

		kb.addSubClass(c, TermFactory.min(v, 1, INTEGER));
		kb.addRange(v, TermFactory.oneOf(one));
		kb.addType(i, c);

		assertTrue(kb.hasPropertyValue(i, v, one));

	}

	/* For bug #303 */
	@Test
	public void userDefinedDatatypes303b() {
		ATermAppl c = TermFactory.term("C");
		ATermAppl v = TermFactory.term("v");
		ATermAppl i = TermFactory.term("i");
		ATermAppl one = TermFactory.literal(1);

		KnowledgeBase kb = new KnowledgeBase();
		kb.addClass(c);
		kb.addDatatypeProperty(v);
		kb.addIndividual(i);

		kb.addSubClass(c, TermFactory.some(v, INTEGER));
		kb.addRange(v, TermFactory.oneOf(one));
		kb.addType(i, c);

		assertTrue(kb.hasPropertyValue(i, v, one));

	}

	@Test
	public void anyURI383() {
		ATermAppl C = TermFactory.term("C");
		ATermAppl D = TermFactory.term("D");
		ATermAppl p = TermFactory.term("p");
		ATermAppl uri = TermFactory.literal(URI.create("http://www.example.org"));

		KnowledgeBase kb = new KnowledgeBase();
		kb.addClass(C);
		kb.addClass(D);
		kb.addDatatypeProperty(p);

		kb.addRange(p, ANY_URI);
		kb.addEquivalentClass(C, hasValue(p, uri));
		kb.addEquivalentClass(D, min(p, 1, TOP_LIT));

		assertSubClass(kb, C, D, true);
		assertSubClass(kb, D, C, false);
	}

	@Ignore("See ticket #524")
	@Test
	public void incomparableDateTime() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl d = restrict(DATE_TIME, minInclusive(literal("1956-01-01T04:00:00-05:00", DATE_TIME)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(d)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(d),reasoner.getValue( literal("1956-01-01T10:00:00", DATE_TIME))));
		assertFalse(reasoner.isSatisfiable(Collections.singleton(d), reasoner.getValue(literal("1956-01-01T10:00:00Z", DATE_TIME))));
	}

	@Ignore("Equal but not identical semantics is very counter-intuitive and currently Pellet treats equals values as identical")
	@Test
	public void equalbutNotIdenticalDateTime() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl d = restrict(DATE_TIME, minInclusive(literal("1956-06-25T04:00:00-05:00", DATE_TIME)),
		                       maxInclusive(literal("1956-06-25T04:00:00-05:00", DATE_TIME)));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(d)));
				
		assertTrue(reasoner.isSatisfiable(Collections.singleton(d), reasoner.getValue(literal("1956-06-25T04:00:00-05:00", DATE_TIME))));
		assertFalse(reasoner.isSatisfiable(Collections.singleton(d), reasoner.getValue(literal("1956-06-25T10:00:00+01:00", DATE_TIME))));
	}

	@Ignore("Equal but not identical semantics is very counter-intuitive and currently Pellet treats equals values as identical")
	@Test
	public void equalbutNotIdenticalDateTimeOneOf() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		ATermAppl d = oneOf(literal("1956-06-25T04:00:00-05:00", DATE_TIME));

		assertTrue(reasoner.isSatisfiable(Collections.singleton(d)));
				
		assertTrue(reasoner.isSatisfiable(Collections.singleton(d), reasoner.getValue(literal("1956-06-25T04:00:00-05:00", DATE_TIME))));
		assertFalse(reasoner.isSatisfiable(Collections.singleton(d), reasoner.getValue(literal("1956-06-25T10:00:00+01:00", DATE_TIME))));
	}
}
