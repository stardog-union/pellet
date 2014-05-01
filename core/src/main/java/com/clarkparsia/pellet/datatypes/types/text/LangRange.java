package com.clarkparsia.pellet.datatypes.types.text;

/**
 * Extended language range patterns as defined in RFC 4647
 */
public class LangRange {

    private static final String[] EMPTY_SUBTAG_LIST = new String[0];
    private final String rangeString;
    private String[] rangeSubtags;

    /**
     * @param range An extended language pattern as defined in RFC 4647
     */
    public LangRange(String range) {
        if (range == null) {
            range = "";
        }
        this.rangeString = range;
        if (range.length() == 0) {
            rangeSubtags = EMPTY_SUBTAG_LIST;
        } else {
            rangeSubtags = range.split("-");
        }
    }

    /**
     * Match a language according to the extended filtering rules of RFC 4647 sec 3.3.2
     *
     * @param tag language tag to match
     * @return true if tag is in the specified range
     */
    public boolean match(String tag) {
        // empty tag only matches empty range
        if (tag == null || tag.length() == 0) {
            return rangeSubtags.length == 0;
        }
        // empty range only matches empty tag
        if (rangeSubtags.length == 0) {
            return false;
        }

        /*
            1.  Split both the extended language range and the language tag being
            compared into a list of subtags by dividing on the hyphen (%x2D)
            character.  Two subtags match if either they are the same when
            compared case-insensitively or the language range's subtag is the
            wildcard '*'.
         */

        String tagSubtags[] = tag.split("-");

        /*
            2.  Begin with the first subtag in each list.  If the first subtag in
            the range does not match the first subtag in the tag, the overall
            match fails.  Otherwise, move to the next subtag in both the
            range and the tag.
         */
        int tagIndex = 0;
        int rangeIndex = 0;

        String rangeSubTag = rangeSubtags[rangeIndex++];
        String tagSubTag = tagSubtags[tagIndex++];

        if (!rangeSubTag.equals("*") && !rangeSubTag.equalsIgnoreCase(tagSubTag)) {
            return false;
        }
        /*
           3.  While there are more subtags left in the language range's list:
         */
        while (rangeIndex < rangeSubtags.length) {
            rangeSubTag = rangeSubtags[rangeIndex];

            /*  A.  If the subtag currently being examined in the range is the
                    wildcard ('*'), move to the next subtag in the range and
                    continue with the loop.
             */
            if (rangeSubTag.equals("*")) {
                rangeIndex++;
                continue;
            }
            /* B.  Else, if there are no more subtags in the language tag's
                    list, the match fails.
             */
            if (tagIndex >= tagSubtags.length) {
                return false;
            }
            /* C.   Else, if the current subtag in the range's list matches the
                    current subtag in the language tag's list, move to the next
                    subtag in both lists and continue with the loop.
             */
            tagSubTag = tagSubtags[tagIndex];
            if (rangeSubTag.equalsIgnoreCase(tagSubTag)) {
                tagIndex++;
                rangeIndex++;
                continue;
            }
            /* D.  Else, if the language tag's subtag is a "singleton" (a single
                   letter or digit, which includes the private-use subtag 'x')
                   the match fails.
             */
            if (tagSubTag.length() == 1) {
                return false;
            }
           /* E.  Else, move to the next subtag in the language tag's list and
                  continue with the loop.
           */
            tagIndex++;
        }
        return true;
    }


    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof LangRange && rangeString.equals(((LangRange) o).rangeString);
    }

    @Override
    public int hashCode() {
        return rangeString.hashCode();
    }

    @Override
    public String toString() {
        return rangeString;
    }
}
