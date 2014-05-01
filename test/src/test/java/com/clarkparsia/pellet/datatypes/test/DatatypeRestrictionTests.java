package com.clarkparsia.pellet.datatypes.test;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Facet;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.utils.ATermUtils;

import static com.clarkparsia.pellet.datatypes.Datatypes.INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.LANG_STRING;
import static com.clarkparsia.pellet.datatypes.Datatypes.PLAIN_LITERAL;
import static com.clarkparsia.pellet.datatypes.Datatypes.POSITIVE_INTEGER;
import static com.clarkparsia.pellet.datatypes.Datatypes.STRING;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.maxInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.minExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

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

    @Test
    public void testEmptyLangRangeRestriction() {
        classes(C);
        dataProperties(p);
        individuals(a, b);
        kb.addDatatypeDefinition(D, restrict(PLAIN_LITERAL, ATermUtils.makeFacetRestriction(Facet.RDF.LANG_RANGE.getName(), literal(""))));
        kb.addEquivalentClass(C, some(p, D));
        kb.addPropertyValue(p, a, literal("no lang tag"));
        kb.addPropertyValue(p, b, literal("english string", "en"));

        assertTrue(kb.isType(a, C));
        assertFalse(kb.isType(b, C));


    }

    @Test
    public void testAnyLangRangeRestriction() {
        classes(C);
        dataProperties(p);
        individuals(a, b);
        kb.addDatatypeDefinition(E, restrict(PLAIN_LITERAL, ATermUtils.makeFacetRestriction(Facet.RDF.LANG_RANGE.getName(), literal("*"))));
        kb.addEquivalentClass(C, some(p, E));
        kb.addPropertyValue(p, a, literal("no lang tag"));
        kb.addPropertyValue(p, b, literal("english string", "en"));

        assertTrue(kb.isType(b, C));
        assertFalse(kb.isType(a, C));


    }
    @Test
    public void testRDF11PlainLiteralLangTagPartition() {
        ATermAppl langs = term("P_SOME_LANG_STRING");
        ATermAppl strings = term("P_SOME_STRING");
        ATermAppl plains = term("P_SOME_PLAIN_LITERAL");
        ATermAppl ors = term("P_SOME_STRING_OR_LANG_STRING");

        ATermAppl noTag = term("no_tag");
        ATermAppl withTag = term("with_tag");

        classes(langs,strings,plains,ors);
        kb.addDatatypeDefinition(D,or(LANG_STRING,STRING) );

        dataProperties(p);
        individuals(noTag, withTag);
        kb.addEquivalentClass(langs, some(p, LANG_STRING));
        kb.addEquivalentClass(strings, some(p, STRING));
        kb.addEquivalentClass(plains, some(p, PLAIN_LITERAL));
        kb.addEquivalentClass(ors,some(p,D));

        kb.addPropertyValue(p, noTag, literal("no lang tag"));
        kb.addPropertyValue(p, withTag, literal("english string", "en"));

        assertClassMembershipEquals(langs, true, withTag );
        assertClassMembershipEquals(langs, false, noTag );

        assertClassMembershipEquals(strings, true, noTag );
        assertClassMembershipEquals(strings, false, withTag );

        assertClassMembershipEquals(plains, true, noTag,withTag );
        assertClassMembershipEquals(ors, true, noTag,withTag );




    }

    private  void assertClassMembershipEquals(ATermAppl c, boolean expected, ATermAppl... individuals) {
        for (ATermAppl individual : individuals) {
            String message = String.format("%s a %s",individual.getName(),c.getName());
            assertEquals(message,expected,kb.isType(individual,c));
        }
    }

    @Test
    public void testEnglishLangRangeRestriction() {
        classes(C);
        dataProperties(p);
        individuals(a, b, c);
        kb.addDatatypeDefinition(D, restrict(PLAIN_LITERAL, ATermUtils.makeFacetRestriction(Facet.RDF.LANG_RANGE.getName(), literal("en"))));
        kb.addEquivalentClass(C, some(p, D));
        kb.addPropertyValue(p, a, literal("no lang tag"));
        kb.addPropertyValue(p, b, literal("english string", "en"));
        kb.addPropertyValue(p, c, literal("american english string", "en-US"));

        assertFalse(kb.isType(a, C));
        assertTrue(kb.isType(b, C));
        assertTrue(kb.isType(c, C));


    }

    @Test
    public void testUSEnglishLangRangeRestriction() {
        classes(C);
        dataProperties(p);
        individuals(a, b, c);
        kb.addDatatypeDefinition(D, restrict(PLAIN_LITERAL, ATermUtils.makeFacetRestriction(Facet.RDF.LANG_RANGE.getName(), literal("en"))));
        ATermAppl literal = literal("en-US");
        kb.addDatatypeDefinition(E, restrict(D, ATermUtils.makeFacetRestriction(Facet.RDF.LANG_RANGE.getName(), literal)));
        kb.addEquivalentClass(C, some(p, E));
        kb.addPropertyValue(p, a, literal("no lang tag"));
        kb.addPropertyValue(p, b, literal("english string", "en"));
        kb.addPropertyValue(p, c, literal("american english string", "en-US"));

        assertFalse(kb.isType(a, C));
        assertFalse(kb.isType(b, C));
        assertTrue(kb.isType(c, C));


    }

}
