package com.clarkparsia.pellet.datatypes.test;

import com.clarkparsia.pellet.datatypes.types.text.LangRange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ses on 4/30/14.
 */
public class LangRangeTests {

    @Test
    public void testLangEmptyRange() {
        LangRange emptyRange = new LangRange("");
        assertTrue("empty matches empty", emptyRange.match(""));
        assertTrue("empty matches empty", emptyRange.match(null));
        assertFalse("empty does not match en", emptyRange.match("en"));
    }

    @Test
    public void testUniversalRange() {
        LangRange any = new LangRange("*");
        assertTrue("any mathches en", any.match("en"));
        assertFalse("any doesn't match empty", any.match(""));
        assertFalse("any doesn't match empty", any.match(null));
    }

    @Test
    public void testRFC4647TestCases() {
        matchAllAgainstRange("de-*-DE", true,
                "de-DE", "de-de", "de-Latn-DE", "de-Latf-DE", "de-DE-x-goethe", "de-Latn-DE-1996", "de-Deva-DE");

        matchAllAgainstRange("de-DE", true,
                "de-DE", "de-de", "de-Latn-DE", "de-Latf-DE", "de-DE-x-goethe", "de-Latn-DE-1996", "de-Deva-DE");

        matchAllAgainstRange("de-*-DE", false,
                "de", "de-x-DE", "de-Deva");
        matchAllAgainstRange("de-DE", false,
                "de", "de-x-DE", "de-Deva");
    }

    private void matchAllAgainstRange(String rangeString, boolean expected, String... tags) {
        LangRange range = new LangRange(rangeString);
        for (String tag : tags) {
            assertEquals("match " + rangeString + " against " + tag, expected, range.match(tag));
        }
    }
}
