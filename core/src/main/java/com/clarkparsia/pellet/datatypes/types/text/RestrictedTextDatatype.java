package com.clarkparsia.pellet.datatypes.types.text;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.clarkparsia.pellet.datatypes.types.text.RestrictedTextDatatype.LanguageTagPresence.LANGUAGE_TAG_FORBIDDEN;
import static com.clarkparsia.pellet.datatypes.types.text.RestrictedTextDatatype.LanguageTagPresence.LANGUAGE_TAG_MUST_BE_EMPTY;
import static com.clarkparsia.pellet.datatypes.types.text.RestrictedTextDatatype.LanguageTagPresence.LANGUAGE_TAG_REQUIRED;

/**
 * <p>
 * Title: Restricted Text Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of rdf:plainLiteral
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class RestrictedTextDatatype implements RestrictedDatatype<ATermAppl> {

    private static final String NCNAMESTARTCHAR = "[A-Z]|_|[a-z]|[\u00C0-\u00D6]|[\u00D8-\u00F6]|[\u00F8-\u02FF]|[\u0370-\u037D]|[\u037F-\u1FFF]|[\u200C-\u200D]|[\u2070-\u218F]|[\u2C00-\u2FEF]|[\u3001-\uD7FF]|[\uF900-\uFDCF]|[\uFDF0-\uFFFD]";
    private static final String NCNAMECHAR = NCNAMESTARTCHAR + "|-|\\.|[0-9]|\u00B7|[\u0300-\u036F]|[\u203F-\u2040]";
    protected static final String NCNAME = "(" + NCNAMESTARTCHAR + ")(" + NCNAMECHAR + ")*";

    private static final String NAMESTARTCHAR = ":|" + NCNAMESTARTCHAR;
    private static final String NAMECHAR = NAMESTARTCHAR + "|-|\\.|[0-9]|\u00B7|[\u0300-\u036F]|[\u203F-\u2040]";
    protected static final String NAME = "(" + NAMESTARTCHAR + ")(" + NAMECHAR + ")*";

    protected static final String NMTOKEN = "(" + NAMECHAR + ")+";

    protected static final String TOKEN = "([^\\s])(\\s([^\\s])|([^\\s]))*";

    protected static final String LANGUAGE = "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*";

    protected static final String NORMALIZED_STRING = "([^\\r\\n\\t])*";

    private static final Set<ATermAppl> permittedDts;

    private final Set<Object> excludedValues;
    private final Set<Pattern> patterns;
    private final Set<LangRange> langRanges;

    public enum LanguageTagPresence {
        LANGUAGE_TAG_REQUIRED {
            public LanguageTagPresence intersect(LanguageTagPresence that) {
                return LANGUAGE_TAG_REQUIRED;
            }

            public LanguageTagPresence union(LanguageTagPresence that) {
                if (that == LANGUAGE_TAG_REQUIRED) {
                    return LANGUAGE_TAG_REQUIRED;
                } else {
                    return LANGUAGE_TAG_ALLOWED;
                }
            }

            boolean isLanguagePresenceOk(ATermAppl literal) {
                return doesLiteralHaveLanguageTag(literal);
            }

        },
        LANGUAGE_TAG_ALLOWED {
            public LanguageTagPresence intersect(LanguageTagPresence that) {
                return that;
            }

            public LanguageTagPresence union(LanguageTagPresence that) {
                return LANGUAGE_TAG_ALLOWED;
            }

            boolean isLanguagePresenceOk(ATermAppl literal) {
                return true;
            }
        },
        LANGUAGE_TAG_MUST_BE_EMPTY {
            @Override
            public LanguageTagPresence intersect(LanguageTagPresence that) {
                if (that == LANGUAGE_TAG_FORBIDDEN) {
                    return LANGUAGE_TAG_FORBIDDEN;
                } else {
                    return LANGUAGE_TAG_MUST_BE_EMPTY;
                }
            }

            @Override
            public LanguageTagPresence union(LanguageTagPresence that) {
                switch (that) {
                    case LANGUAGE_TAG_FORBIDDEN:
                    case LANGUAGE_TAG_MUST_BE_EMPTY:
                        return LANGUAGE_TAG_MUST_BE_EMPTY;
                    default:
                        return LANGUAGE_TAG_ALLOWED;
                }

            }

            @Override
            boolean isLanguagePresenceOk(ATermAppl literal) {
                return !doesLiteralHaveLanguageTag(literal);
            }
        },
        LANGUAGE_TAG_FORBIDDEN {
            public LanguageTagPresence intersect(LanguageTagPresence that) {
                return LANGUAGE_TAG_FORBIDDEN;
            }

            public LanguageTagPresence union(LanguageTagPresence that) {
                if (that == LANGUAGE_TAG_FORBIDDEN) {
                    return LANGUAGE_TAG_FORBIDDEN;
                } else {
                    return LANGUAGE_TAG_ALLOWED;
                }
            }

            boolean isLanguagePresenceOk(ATermAppl literal) {
                return !doesLiteralHaveLanguageTag(literal);
            }


        };

        abstract public LanguageTagPresence intersect(LanguageTagPresence that);

        abstract public LanguageTagPresence union(LanguageTagPresence that);

        abstract boolean isLanguagePresenceOk(ATermAppl literal);
    }

    static {
        permittedDts = new HashSet<ATermAppl>(Arrays.asList(ATermUtils.EMPTY));
    }

    /*
     * TODO: This is awkward.
     */
    public static boolean addPermittedDatatype(ATermAppl dt) {
        return permittedDts.add(dt);
    }

    private final LanguageTagPresence languageTagPresence;
    private final Datatype<ATermAppl> dt;

    public RestrictedTextDatatype(Datatype<ATermAppl> dt, LanguageTagPresence languageTagPresence) {
        this(Collections.<Pattern>emptySet(), languageTagPresence, Collections.emptySet(), dt, Collections.<LangRange>emptySet());
    }

    public RestrictedTextDatatype(Datatype<ATermAppl> dt, String pattern) {
        this(Collections.singleton(Pattern.compile(pattern)), LANGUAGE_TAG_FORBIDDEN,
                Collections.emptySet(), dt, Collections.<LangRange>emptySet());
    }

    private RestrictedTextDatatype(Set<Pattern> patterns, LanguageTagPresence languageTagPresence, Set<Object> excludedValues, Datatype<ATermAppl> dt,
                                   Set<LangRange> langRanges) {
        this.dt = dt;
        this.languageTagPresence = languageTagPresence;
        this.excludedValues = excludedValues;
        this.patterns = patterns;
        this.langRanges = langRanges;
    }

    public RestrictedDatatype<ATermAppl> applyConstrainingFacet(ATermAppl facet, Object value)
            throws InvalidConstrainingFacetException {
        // TODO: support moar facets

        if (facet.equals(Facet.RDF.LANG_RANGE.getName())) {
            return applyLangRangFacet(facet, value);
        } else {
            throw new InvalidConstrainingFacetException(facet, value);
        }
    }

    private RestrictedDatatype<ATermAppl> applyLangRangFacet(ATermAppl facet, Object value) throws InvalidConstrainingFacetException {
        if (!(value instanceof ATermAppl)) {
            throw new InvalidConstrainingFacetException("Invalid java type for value", facet, value);
        }

        String rangePattern = ATermUtils.getLiteralValue((ATermAppl) value);
        if (languageTagPresence == LANGUAGE_TAG_FORBIDDEN) {
            throw new InvalidConstrainingFacetException("can't add langRange facet to type that doesn't allow languages:"
                    + this.dt.getName() + ":" + languageTagPresence, facet, value);
        }
        boolean rangePatternIsEmpty = rangePattern.equals("");
        if (rangePatternIsEmpty) {
            return new RestrictedTextDatatype(patterns, LANGUAGE_TAG_MUST_BE_EMPTY,
                    excludedValues, dt, Collections.<LangRange>emptySet());
        }

        Set<LangRange> newRanges = new HashSet<LangRange>();
        newRanges.addAll(langRanges);
        newRanges.add(new LangRange(rangePattern));
        return new RestrictedTextDatatype(patterns, LANGUAGE_TAG_REQUIRED, excludedValues, dt, newRanges);
    }

    public boolean contains(Object value) {
        if (value instanceof ATermAppl) {
            final ATermAppl a = (ATermAppl) value;

            if (excludedValues.contains(a)) {
                return false;
            }

            if (ATermUtils.isLiteral(a)
                    && permittedDts.contains(a.getArgument(ATermUtils.LIT_URI_INDEX))) {

                if (!languageTagPresence.isLanguagePresenceOk(a)) {
                    return false;
                }

                if (!langRanges.isEmpty()) {
                    String literalLang = ATermUtils.getLiteralLang(a);
                    for (LangRange range : langRanges) {
                        if (!range.match(literalLang)) {
                            return false;
                        }
                    }
                }

                if (!patterns.isEmpty()) {
                    String litValue = ((ATermAppl) a.getArgument(ATermUtils.LIT_VAL_INDEX)).getName();
                    for (Pattern pattern : patterns) {
                        if (!pattern.matcher(litValue).matches())
                            return false;
                    }
                }

                return true;
            }
        }
        return false;
    }

    private static boolean doesLiteralHaveLanguageTag(ATermAppl a) {
        return !ATermUtils.EMPTY.equals(a.getArgument(ATermUtils.LIT_LANG_INDEX));
    }

    public boolean containsAtLeast(int n) {
        return true;
    }

    public RestrictedDatatype<ATermAppl> exclude(Collection<?> values) {
        Set<Object> newExcludedValues = new HashSet<Object>(values);
        newExcludedValues.addAll(excludedValues);
        return new RestrictedTextDatatype(patterns, languageTagPresence, newExcludedValues, dt, langRanges);
    }

    public Datatype<? extends ATermAppl> getDatatype() {
        return dt;
    }

    public ATermAppl getValue(int i) {
        throw new UnsupportedOperationException();
    }

    protected <T> List<T> concatLists(List<T> l1, List<T> l2) {
        if (l1.isEmpty())
            return l2;
        if (l2.isEmpty())
            return l1;

        List<T> newList = new ArrayList<T>(l1.size() + l2.size());
        newList.addAll(l1);
        newList.addAll(l2);

        return newList;
    }

    public RestrictedDatatype<ATermAppl> intersect(RestrictedDatatype<?> other, boolean negated) {
        if (other instanceof RestrictedTextDatatype) {
            RestrictedTextDatatype that = (RestrictedTextDatatype) other;

            return new RestrictedTextDatatype(SetUtils.union(this.patterns, that.patterns),
                    this.languageTagPresence.intersect(that.languageTagPresence),
                    SetUtils.union(this.excludedValues, that.excludedValues),
                    dt,
                    SetUtils.union(this.langRanges, that.langRanges)
            );
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isEnumerable() {
        return false;
    }

    public boolean isFinite() {
        return false;
    }

    public int size() {
        throw new IllegalStateException();
    }

    public RestrictedDatatype<ATermAppl> union(RestrictedDatatype<?> other) {
        if (other instanceof RestrictedTextDatatype) {
            RestrictedTextDatatype that = (RestrictedTextDatatype) other;
            if (!patterns.isEmpty() || !that.patterns.isEmpty()
                    || !langRanges.isEmpty() || !that.langRanges.isEmpty()) {
                //TODO support unions with patterns or langRanges
                throw new UnsupportedOperationException("union of restricted text types with patterns or langRanges not yet done");
            }

            Set<Object> commonExcludedValues = SetUtils.intersection(this.excludedValues, that.excludedValues);
            return new RestrictedTextDatatype(patterns, this.languageTagPresence.union(that.languageTagPresence), commonExcludedValues, dt, langRanges);

        } else {
            throw new IllegalArgumentException();
        }
    }

    public Iterator<ATermAppl> valueIterator() {
        throw new IllegalStateException();
    }

}
