package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.Literal;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.datatypes.types.bool.XSDBoolean;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDDate;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDDateTime;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDDateTimeStamp;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDGDay;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDGMonth;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDGMonthDay;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDGYear;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDGYearMonth;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDTime;
import com.clarkparsia.pellet.datatypes.types.duration.XSDDuration;
import com.clarkparsia.pellet.datatypes.types.floating.XSDDouble;
import com.clarkparsia.pellet.datatypes.types.floating.XSDFloat;
import com.clarkparsia.pellet.datatypes.types.real.OWLRational;
import com.clarkparsia.pellet.datatypes.types.real.OWLReal;
import com.clarkparsia.pellet.datatypes.types.real.XSDByte;
import com.clarkparsia.pellet.datatypes.types.real.XSDDecimal;
import com.clarkparsia.pellet.datatypes.types.real.XSDInt;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;
import com.clarkparsia.pellet.datatypes.types.real.XSDLong;
import com.clarkparsia.pellet.datatypes.types.real.XSDNegativeInteger;
import com.clarkparsia.pellet.datatypes.types.real.XSDNonNegativeInteger;
import com.clarkparsia.pellet.datatypes.types.real.XSDNonPositiveInteger;
import com.clarkparsia.pellet.datatypes.types.real.XSDPositiveInteger;
import com.clarkparsia.pellet.datatypes.types.real.XSDShort;
import com.clarkparsia.pellet.datatypes.types.real.XSDUnsignedByte;
import com.clarkparsia.pellet.datatypes.types.real.XSDUnsignedInt;
import com.clarkparsia.pellet.datatypes.types.real.XSDUnsignedLong;
import com.clarkparsia.pellet.datatypes.types.real.XSDUnsignedShort;
import com.clarkparsia.pellet.datatypes.types.text.RDFPlainLiteral;
import com.clarkparsia.pellet.datatypes.types.text.XSDLanguage;
import com.clarkparsia.pellet.datatypes.types.text.XSDNCName;
import com.clarkparsia.pellet.datatypes.types.text.XSDNMToken;
import com.clarkparsia.pellet.datatypes.types.text.XSDName;
import com.clarkparsia.pellet.datatypes.types.text.XSDNormalizedString;
import com.clarkparsia.pellet.datatypes.types.text.XSDString;
import com.clarkparsia.pellet.datatypes.types.text.XSDToken;
import com.clarkparsia.pellet.datatypes.types.uri.XSDAnyURI;

/**
 * <p>
 * Title: Datatype Reasoner Implementation
 * </p>
 * <p>
 * Description: Default implementation of interface {@link DatatypeReasoner}
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
public class DatatypeReasonerImpl implements DatatypeReasoner {

	private static final Map<ATermAppl, Datatype<?>> coreDatatypes;
	private static final DataRange<?> EMPTY_RANGE;
	private static final Logger log;
	private static final DataRange<?> TRIVIALLY_SATISFIABLE;

	static {
		log = Logger.getLogger(DatatypeReasonerImpl.class.getCanonicalName());

		coreDatatypes = new HashMap<ATermAppl, Datatype<?>>();

		{
			coreDatatypes.put(RDFPlainLiteral.getInstance().getName(), RDFPlainLiteral.getInstance());
			coreDatatypes.put(XSDString.getInstance().getName(), XSDString.getInstance());
			coreDatatypes.put(XSDNormalizedString.getInstance().getName(), XSDNormalizedString.getInstance());
			coreDatatypes.put(XSDToken.getInstance().getName(), XSDToken.getInstance());
			coreDatatypes.put(XSDLanguage.getInstance().getName(), XSDLanguage.getInstance());
			coreDatatypes.put(XSDNMToken.getInstance().getName(), XSDNMToken.getInstance());
			coreDatatypes.put(XSDName.getInstance().getName(), XSDName.getInstance());
			coreDatatypes.put(XSDNCName.getInstance().getName(), XSDNCName.getInstance());
		}

		coreDatatypes.put(XSDBoolean.getInstance().getName(), XSDBoolean.getInstance());

		{
			coreDatatypes.put(OWLReal.getInstance().getName(), OWLReal.getInstance());
			coreDatatypes.put(OWLRational.getInstance().getName(), OWLRational.getInstance());
			coreDatatypes.put(XSDDecimal.getInstance().getName(), XSDDecimal.getInstance());
			coreDatatypes.put(XSDInteger.getInstance().getName(), XSDInteger.getInstance());
			coreDatatypes.put(XSDLong.getInstance().getName(), XSDLong.getInstance());
			coreDatatypes.put(XSDInt.getInstance().getName(), XSDInt.getInstance());
			coreDatatypes.put(XSDShort.getInstance().getName(), XSDShort.getInstance());
			coreDatatypes.put(XSDByte.getInstance().getName(), XSDByte.getInstance());
			coreDatatypes.put(XSDNonNegativeInteger.getInstance().getName(), XSDNonNegativeInteger.getInstance());
			coreDatatypes.put(XSDNonPositiveInteger.getInstance().getName(), XSDNonPositiveInteger.getInstance());
			coreDatatypes.put(XSDNegativeInteger.getInstance().getName(), XSDNegativeInteger.getInstance());
			coreDatatypes.put(XSDPositiveInteger.getInstance().getName(), XSDPositiveInteger.getInstance());
			coreDatatypes.put(XSDUnsignedLong.getInstance().getName(), XSDUnsignedLong.getInstance());
			coreDatatypes.put(XSDUnsignedInt.getInstance().getName(), XSDUnsignedInt.getInstance());
			coreDatatypes.put(XSDUnsignedShort.getInstance().getName(), XSDUnsignedShort.getInstance());
			coreDatatypes.put(XSDUnsignedByte.getInstance().getName(), XSDUnsignedByte.getInstance());
		}

		coreDatatypes.put(XSDDouble.getInstance().getName(), XSDDouble.getInstance());

		coreDatatypes.put(XSDFloat.getInstance().getName(), XSDFloat.getInstance());

		{
			coreDatatypes.put(XSDDateTime.getInstance().getName(), XSDDateTime.getInstance());
			coreDatatypes.put(XSDDateTimeStamp.getInstance().getName(), XSDDateTimeStamp.getInstance());
		}
		{
			coreDatatypes.put(XSDDate.getInstance().getName(), XSDDate.getInstance());
			coreDatatypes.put(XSDGYearMonth.getInstance().getName(), XSDGYearMonth.getInstance());
			coreDatatypes.put(XSDGMonthDay.getInstance().getName(), XSDGMonthDay.getInstance());
			coreDatatypes.put(XSDGYear.getInstance().getName(), XSDGYear.getInstance());
			coreDatatypes.put(XSDGMonth.getInstance().getName(), XSDGMonth.getInstance());
			coreDatatypes.put(XSDGDay.getInstance().getName(), XSDGDay.getInstance());
			coreDatatypes.put(XSDTime.getInstance().getName(), XSDTime.getInstance());
		}

		coreDatatypes.put(XSDDuration.getInstance().getName(), XSDDuration.getInstance());

		coreDatatypes.put(XSDAnyURI.getInstance().getName(), XSDAnyURI.getInstance());
	}

	static {
		EMPTY_RANGE = new EmptyDataRange<Object>();

		TRIVIALLY_SATISFIABLE = new DataRange<Object>() {

			public boolean contains(Object value) {
				return true;
			}

			public boolean containsAtLeast(int n) {
				return true;
			}

			@Override
			public boolean equals(Object obj) {
				return this == obj;
			}

			public Object getValue(int i) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int hashCode() {
				return super.hashCode();
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
				throw new UnsupportedOperationException();
			}

			public Iterator<Object> valueIterator() {
				throw new UnsupportedOperationException();
			}

		};
	}

	private static <T> DataValueEnumeration<? extends T> findSmallestEnumeration(
	                Collection<DataValueEnumeration<? extends T>> ranges) {
		DataValueEnumeration<? extends T> ret = null;
		int best = Integer.MAX_VALUE;
		for (DataValueEnumeration<? extends T> r : ranges) {
			final DataValueEnumeration<? extends T> e = r;
			final int s = e.size();
			if (s < best) {
				ret = e;
				best = s;
			}
		}
		return ret;
	}

	private static final ATermAppl getDatatypeName(ATermAppl literal) {
		if (!ATermUtils.isLiteral(literal)) {
			final String msg = "Method expected an ATermAppl literal as an argument";
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		final ATermAppl dtName = (ATermAppl) literal.getArgument(ATermUtils.LIT_URI_INDEX);
		if (ATermUtils.EMPTY.equals(dtName)) {
			final String msg = "Untyped literals not supported by this datatype reasoner";
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		return dtName;
	}

	private static int inequalityCount(Set<Integer>[] nes, int xIndex) {

		final Set<Integer> others = nes[xIndex];
		return others == null ? 0 : others.size();
	}

	private static <T> void partitionDConjunction(Collection<DataRange<? extends T>> dconjunction,
	                Set<DataValueEnumeration<? extends T>> positiveEnumerations,
	                Set<DataValueEnumeration<? extends T>> negativeEnumerations,
	                Set<RestrictedDatatype<? extends T>> positiveRestrictions,
	                Set<RestrictedDatatype<? extends T>> negativeRestrictions) {
		for (DataRange<? extends T> dr : dconjunction) {
			if (dr instanceof DataValueEnumeration) {
				positiveEnumerations.add((DataValueEnumeration<? extends T>) dr);
			}
			else if (dr instanceof RestrictedDatatype) {
				positiveRestrictions.add((RestrictedDatatype<? extends T>) dr);
			}
			else if (dr instanceof NegatedDataRange) {
				DataRange<? extends T> ndr = ((NegatedDataRange<? extends T>) dr).getDataRange();
				if (ndr instanceof DataValueEnumeration) {
					negativeEnumerations.add((DataValueEnumeration<? extends T>) ndr);
				}
				else if (ndr instanceof RestrictedDatatype) {
					negativeRestrictions.add((RestrictedDatatype<? extends T>) ndr);
				}
				else if (dr != TRIVIALLY_SATISFIABLE) {
					log.warning("Unknown datatype: " + dr);
				}
			}
			else if (dr != TRIVIALLY_SATISFIABLE) {
				log.warning("Unknown datatype: " + dr);
			}
		}
	}

	private static boolean removeInequalities(Set<Integer>[] nes, int xIndex) {

		final Set<Integer> others = nes[xIndex];

		if (others == null) {
	        return false;
        }
        else {
			for (Integer yIndex : others) {
				Set<Integer> s = nes[yIndex];
				if (s == null) {
	                throw new IllegalStateException();
                }
				if (!s.remove(xIndex)) {
	                throw new IllegalStateException();
                }
			}
			return true;
		}
	}

	private final Set<ATermAppl> declaredUndefined;
	private final NamedDataRangeExpander expander;
	private final Map<ATermAppl, ATermAppl> namedDataRanges;

	public DatatypeReasonerImpl() {
		declaredUndefined = new HashSet<ATermAppl>();
		expander = new NamedDataRangeExpander();
		namedDataRanges = new HashMap<ATermAppl, ATermAppl>();
	}

	private boolean containedIn(Object value, ATermAppl dconjunction) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		if (ATermUtils.isAnd(dconjunction)) {
			for (ATermList l = (ATermList) dconjunction.getArgument(0); !l.isEmpty(); l = l.getNext()) {
				if (!getDataRange((ATermAppl) l.getFirst()).contains(value)) {
	                return false;
                }
			}
			return true;
		}
        else {
	        return getDataRange(dconjunction).contains(value);
        }
	}

	public boolean containsAtLeast(int n, Collection<ATermAppl> ranges) throws UnrecognizedDatatypeException,
	                InvalidConstrainingFacetException, InvalidLiteralException {

		ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(ranges));
		ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
		if (ATermUtils.isOr(dnf)) {
			List<DataRange<?>> disjuncts = new ArrayList<DataRange<?>>();
			for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext()) {
				final DataRange<?> dr = normalizeVarRanges((ATermAppl) l.getFirst());
				if (!dr.isEmpty()) {
	                disjuncts.add(dr);
                }
			}

			final DataRange<?> disjunction = getDisjunction(disjuncts);
			return disjunction.containsAtLeast(n);
		}
		else {
			final DataRange<?> dr = normalizeVarRanges(dnf);
			return dr.containsAtLeast(n);
		}

	}

	public boolean declare(ATermAppl name) {
		if (isDeclared(name)) {
	        return false;
        }
        else {
			declaredUndefined.add(name);
			return true;
		}
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl literal) throws InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final ATermAppl dtName = getDatatypeName(literal);
		final Datatype<?> dt = getDatatype(dtName);
		if (dt == null) {
			switch (PelletOptions.UNDEFINED_DATATYPE_HANDLING) {
				case INFINITE_STRING:
					return literal;
				case EMPTY:
					throw new InvalidLiteralException(dtName, ATermUtils.getLiteralValue(literal));
				case EXCEPTION:
					throw new UnrecognizedDatatypeException(dtName);
				default:
					throw new IllegalStateException();
			}
		}
        else {
	        return dt.getCanonicalRepresentation(literal);
        }
	}

	private DataRange<?> getDataRange(ATermAppl a) throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		// TODO: Investigate the impact of keeping a results cache here

		/*
		 * rdfs:Literal
		 */
		if (a.equals(ATermUtils.TOP_LIT)) {
	        return TRIVIALLY_SATISFIABLE;
        }

		/*
		 * Negation of rdfs:Literal
		 */
		if (a.equals(ATermUtils.BOTTOM_LIT)) {
	        return EMPTY_RANGE;
        }

		/*
		 * Named datatype
		 */
		if (ATermUtils.isPrimitive(a)) {
			Datatype<?> dt = getDatatype(a);
			if (dt == null) {
				switch (PelletOptions.UNDEFINED_DATATYPE_HANDLING) {
					case INFINITE_STRING:
						dt = InfiniteNamedDatatype.get(a);
						break;
					case EMPTY:
						return EMPTY_RANGE;
					case EXCEPTION:
						throw new UnrecognizedDatatypeException(a);
					default:
						throw new IllegalStateException();
				}
			}
			return dt.asDataRange();
		}

		/*
		 * Datatype restriction
		 */
		if (ATermUtils.isRestrictedDatatype(a)) {

			/*
			 * Start with the full data range for the datatype
			 */
			final ATermAppl dtTerm = (ATermAppl) a.getArgument(0);
			final DataRange<?> dt = getDataRange(dtTerm);
			if (!(dt instanceof RestrictedDatatype<?>)) {
				throw new InvalidConstrainingFacetException(dtTerm, dt);
			}

			RestrictedDatatype<?> dr = (RestrictedDatatype<?>) dt;

			/*
			 * Apply each constraining facet value pair in turn
			 */
			final ATermList facetValues = (ATermList) a.getArgument(1);
			for (ATermList l = facetValues; !l.isEmpty(); l = l.getNext()) {
				final ATermAppl fv = (ATermAppl) l.getFirst();
				final ATermAppl facet = (ATermAppl) fv.getArgument(0);
				final ATermAppl valueTerm = (ATermAppl) fv.getArgument(1);

				Object value;
				try {
					value = getValue(valueTerm);
				}
				catch (InvalidLiteralException e) {
					throw new InvalidConstrainingFacetException(facet, valueTerm, e);
				}
				dr = dr.applyConstrainingFacet(facet, value);
			}

			return dr;
		}

		/*
		 * Negated datarange
		 */
		if (ATermUtils.isNot(a)) {
			final ATermAppl n = (ATermAppl) a.getArgument(0);
			final DataRange<?> ndr = getDataRange(n);
			final DataRange<?> dr = new NegatedDataRange<Object>(ndr);

			return dr;
		}

		/*
		 * TODO: Consider if work before this point to group enumerations (i.e., treat them differently than
		 * disjunctions of singleton enumerations) is worthwhile.
		 */
		/*
		 * Data value enumeration
		 */
		if (ATermUtils.isNominal(a)) {
			final ATermAppl literal = (ATermAppl) a.getArgument(0);
			final DataRange<?> dr = new DataValueEnumeration<Object>(Collections.singleton(getValue(literal)));
			return dr;
		}

		final String msg = format("Unrecognized input term (%s) for datarange conversion", a);
		log.severe(msg);
		throw new IllegalArgumentException(msg);
	}

	public Datatype<?> getDatatype(ATermAppl uri) {
		try {
			Datatype<?> dt = coreDatatypes.get(uri);
			if (dt == null) {
				ATermAppl definition = namedDataRanges.get(uri);
				if (definition != null) {
					if (ATermUtils.isRestrictedDatatype(definition)) {
						RestrictedDatatype<?> dataRange = (RestrictedDatatype<?>) getDataRange(definition);
						@SuppressWarnings("unchecked")
						NamedDatatype namedDatatype = new NamedDatatype(uri, dataRange);
						dt = namedDatatype;
					}
				}
			}

			return dt;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private DataRange<?> getDisjunction(Collection<DataRange<?>> ranges) {

		if (ranges.size() == 1) {
	        return ranges.iterator().next();
        }

		for (DataRange<?> r : ranges) {
	        if (r == TRIVIALLY_SATISFIABLE) {
	            return r;
            }
        }

		Set<Object> oneOf = Collections.emptySet();
		Map<Datatype<?>, Set<RestrictedDatatype<?>>> byPrimitive = new HashMap<Datatype<?>, Set<RestrictedDatatype<?>>>();

		/*
		 * Organize the input data ranges into restrictions partitioned by data and a merged value enumeration.
		 */
		for (DataRange<?> dr : ranges) {
			if (dr instanceof RestrictedDatatype) {
				final RestrictedDatatype<?> rd = (RestrictedDatatype<?>) dr;
				final Datatype<?> pd = rd.getDatatype().getPrimitiveDatatype();
				Set<RestrictedDatatype<?>> others = byPrimitive.get(pd);
				if (others == null) {
					others = new HashSet<RestrictedDatatype<?>>();
					byPrimitive.put(pd, others);
				}
				others.add(rd);
			}
			else if (dr instanceof DataValueEnumeration) {
				final DataValueEnumeration<?> enm = (DataValueEnumeration<?>) dr;
				if (oneOf.isEmpty()) {
	                oneOf = new HashSet<Object>();
                }
				for (Iterator<?> it = enm.valueIterator(); it.hasNext();) {
	                oneOf.add(it.next());
                }
			}
		}

		/*
		 * Merge data ranges that have the same primitive datatype
		 */
		Set<RestrictedDatatype<?>> disjointRanges = new HashSet<RestrictedDatatype<?>>();
		for (Set<RestrictedDatatype<?>> s : byPrimitive.values()) {
			Iterator<RestrictedDatatype<?>> it = s.iterator();
			RestrictedDatatype<?> merge = it.next();
			while (it.hasNext()) {
	            merge = merge.union(it.next());
            }

			disjointRanges.add(merge);
		}

		/*
		 * Discard any enum elements that are included in other disjuncts
		 */
		for (Iterator<Object> it = oneOf.iterator(); it.hasNext();) {
			final Object o = it.next();
			for (RestrictedDatatype<?> rd : disjointRanges) {
				if (rd.contains(o)) {
	                it.remove();
                }
			}
		}

		return new UnionDataRange<Object>(disjointRanges, oneOf);
	}

	public ATermAppl getLiteral(Object value) {
		for (Datatype<?> dt : coreDatatypes.values()) {
			if (dt.isPrimitive()) {
				if (dt.asDataRange().contains(value)) {
					return dt.getLiteral(value);
				}
			}
		}

		final String msg = "Value is not in the value space of any recognized datatypes: " + value.toString();
		log.severe(msg);
		throw new IllegalArgumentException(msg);
	}

	public Object getValue(ATermAppl literal) throws InvalidLiteralException, UnrecognizedDatatypeException {
		final ATermAppl dtName = getDatatypeName(literal);
		final Datatype<?> dt = getDatatype(dtName);
		if (dt == null) {
			switch (PelletOptions.UNDEFINED_DATATYPE_HANDLING) {
				case INFINITE_STRING:
					return literal;
				case EMPTY:
					throw new InvalidLiteralException(dtName, ATermUtils.getLiteralValue(literal));
				case EXCEPTION:
					throw new UnrecognizedDatatypeException(dtName);
				default:
					throw new IllegalStateException();
			}
		}
        else {
	        return dt.getValue(literal);
        }
	}

	public boolean isDeclared(ATermAppl name) {
		return ATermUtils.TOP_LIT.equals(name) || coreDatatypes.containsKey(name) || namedDataRanges.containsKey(name)
		       || declaredUndefined.contains(name);
	}

	public boolean isDefined(ATermAppl name) {
		if (ATermUtils.TOP_LIT.equals(name)) {
	        return true;
        }

		if (coreDatatypes.containsKey(name)) {
	        return true;
        }
		if (namedDataRanges.containsKey(name)) {
	        return true;
        }

		return false;
	}
	
	public ATermAppl getDefinition(ATermAppl name) {
		return namedDataRanges.get( name );	
	}

	public boolean isSatisfiable(Collection<ATermAppl> dataranges) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {
		return isSatisfiable(dataranges, null);
	}

	public boolean isSatisfiable(Collection<ATermAppl> dataranges, Object value)
	                throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException {
		Set<Integer> consts, vars;

		if (value == null) {
			/*
			 * TODO: See if code in next method can be restructured to avoid this allocation.
			 */
			consts = new HashSet<Integer>();
			vars = new HashSet<Integer>(Collections.singleton(0));
		}
		else {
			consts = Collections.singleton(0);
			vars = Collections.emptySet();
		}

		ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(dataranges));
		ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
		Collection<ATermAppl> dnfDisjuncts;
		if (ATermUtils.isOr(dnf)) {
			List<ATermAppl> disjuncts = new ArrayList<ATermAppl>();
			for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext()) {
	            disjuncts.add((ATermAppl) l.getFirst());
            }
			dnfDisjuncts = disjuncts;
		}
        else {
	        dnfDisjuncts = Collections.singleton(dnf);
        }

		@SuppressWarnings("unchecked")
		final Collection<ATermAppl>[] dnfTypes = new Collection[] { dnfDisjuncts };

		@SuppressWarnings("unchecked")
		final Set<Integer>[] ne = new Set[] { Collections.<Integer> emptySet() };

		return isSatisfiable(consts, vars, dnfTypes, new Object[] { value }, ne);
	}

	private boolean isSatisfiable(Set<Integer> consts, Set<Integer> vars, Collection<ATermAppl>[] dnfTypes,
	                Object[] constValues, Set<Integer>[] ne) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {

		/*
		 * TODO: Remove need for consts and vars sets by using null in constValues array
		 */

		final int n = dnfTypes.length;

		/*
		 * 1. Loop and eliminate any easy, obvious unsats
		 */
		for (int i = 0; i < n; i++) {
			final Collection<ATermAppl> drs = dnfTypes[i];
			for (Iterator<ATermAppl> it = drs.iterator(); it.hasNext();) {
				ATermAppl dr = it.next();
				if (ATermUtils.BOTTOM_LIT.equals(dr)) {
	                it.remove();
                }
			}
			if (drs.isEmpty()) {
	            return false;
            }
		}

		/*
		 * 2. Get normalized form of data ranges
		 */
		DataRange<?>[] normalized = new DataRange[n];
		for (int i = 0; i < n; i++) {

			if (consts.contains(i)) {

				boolean satisfied = false;
				for (ATermAppl a : dnfTypes[i]) {
					if (containedIn(constValues[i], a)) {
						satisfied = true;
						break;
					}
				}
				if (satisfied) {
	                normalized[i] = TRIVIALLY_SATISFIABLE;
                }
                else {
	                return false;
                }

			}
			else {

				List<DataRange<?>> drs = new ArrayList<DataRange<?>>();
				for (ATermAppl a : dnfTypes[i]) {
					DataRange<?> dr = normalizeVarRanges(a);
					if (dr == TRIVIALLY_SATISFIABLE) {
						drs = Collections.<DataRange<?>> singletonList(TRIVIALLY_SATISFIABLE);
						break;
					}
					else if (!dr.isEmpty()) {
						drs.add(dr);
					}
				}
				if (drs.isEmpty()) {
	                return false;
                }
                else {
	                normalized[i] = getDisjunction(drs);
                }

			}
		}

		/*
		 * Alg lines 7 - 22 (without the 12-13 or 19-20 blocks)
		 */
		for (Iterator<Integer> it = vars.iterator(); it.hasNext();) {
			Integer i = it.next();
			final DataRange<?> dr = normalized[i];

			/*
			 * First half of condition 9 - 11 block
			 */
			if (TRIVIALLY_SATISFIABLE == dr) {
				it.remove();
				removeInequalities(ne, i);
				continue;
			}

			/*
			 * Line 15
			 */
			if (dr.isEmpty()) {
	            return false;
            }

			/*
			 * Second half of condition 9 - 11 block
			 */
			if (dr.containsAtLeast(inequalityCount(ne, i) + 1)) {
				it.remove();
				removeInequalities(ne, i);
				continue;
			}

			/*
			 * Data range is a singleton, replace variable with constant (lines 17 - 18)
			 */
			if (dr.isFinite() && dr.isEnumerable() && !dr.containsAtLeast(2)) {
				final Object c = dr.valueIterator().next();
				it.remove();
				consts.add(i);
				constValues[i] = c;
				normalized[i] = TRIVIALLY_SATISFIABLE;
				continue;
			}
		}

		if (log.isLoggable(Level.FINEST)) {
			log.finest(format("After variable data range normalization %d variables and %d constants", vars.size(),
			                consts.size()));
		}

		/*
		 * Constant checks (alg lines 23 - 30)
		 */
		for (Integer i : consts) {

			/*
			 * Check that any constant,constant inequalities are satisfied
			 */
			Set<Integer> diffs = ne[i];
			if (diffs != null) {
				for (Iterator<Integer> it = diffs.iterator(); it.hasNext();) {
					final int j = it.next();
					if (consts.contains(j)) {

						if (constValues[i].equals(constValues[j])) {
	                        return false;
                        }

						it.remove();
						ne[j].remove(i);
					}
				}
			}
		}

		/*
		 * Try to eliminate any more variables that can be removed
		 */
		for (Iterator<Integer> it = vars.iterator(); it.hasNext();) {
			final int i = it.next();

			final DataRange<?> dr = normalized[i];

			final Set<Integer> diffs = ne[i];
			final int min = (diffs == null) ? 1 : diffs.size() + 1;
			if (dr.containsAtLeast(min)) {
				it.remove();
				for (int j : diffs) {
					if (ne[j] != null) {
	                    ne[j].remove(i);
                    }
				}
				ne[i] = null;
				vars.remove(i);
			}
		}

		if (log.isLoggable(Level.FINEST)) {
			log.finest(format("After size check on variable data ranges %d variables", vars.size()));
		}

		if (vars.isEmpty()) {
	        return true;
        }

		/*
		 * Assertion: at this point, all remaining variables are from finite and enumerable data ranges.
		 */

		/*
		 * Partition remaining variables into disjoint collections
		 */
		Set<Integer> remaining = new HashSet<Integer>(vars);
		List<Set<Integer>> partitions = new ArrayList<Set<Integer>>();
		while (!remaining.isEmpty()) {
			Set<Integer> p = new HashSet<Integer>();
			Iterator<Integer> it = remaining.iterator();
			int i = it.next();
			it.remove();
			p.add(i);
			if (ne[i] != null) {
				Set<Integer> others = new HashSet<Integer>();
				others.addAll(ne[i]);
				while (!others.isEmpty()) {
					Iterator<Integer> jt = others.iterator();
					int j = jt.next();
					jt.remove();
					if (remaining.contains(j)) {
						p.add(j);
						remaining.remove(j);
						if (ne[j] != null) {
	                        others.addAll(ne[j]);
                        }
					}
				}

			}
			partitions.add(p);
		}

		if (log.isLoggable(Level.FINEST)) {
			log.finest(format("Enumerating to find solutions for %d partitions", partitions.size()));
		}

		/*
		 * Enumerate until a solution is found
		 */
		for (Set<Integer> p : partitions) {
			final int nPart = p.size();

			int[] indices = new int[nPart];
			Map<Integer, Integer> revInd = new HashMap<Integer, Integer>();
			DataRange<?>[] drs = new DataRange[nPart];

			int i = 0;
			for (int j : p) {
				drs[i] = normalized[j];
				indices[i] = j;
				revInd.put(j, i);
				i++;
			}

			Iterator<?>[] its = new Iterator[nPart];
			for (i = 0; i < nPart; i++) {
	            its[i] = drs[i].valueIterator();
            }

			Object[] values = new Object[nPart];
			/*
			 * Assign a value to each
			 */
			for (i = 0; i < nPart; i++) {
	            values[i] = its[i].next();
            }

			boolean solutionFound = false;
			while (!solutionFound) {
				/*
				 * Check solution
				 */
				solutionFound = true;
				for (i = 0; i < nPart && solutionFound; i++) {
					Set<Integer> diffs = ne[indices[i]];
					if (diffs != null) {
						final Object a = values[i];
						for (int j : diffs) {

							Object b;
							if (p.contains(j)) {
	                            b = values[revInd.get(j)];
                            }
                            else {
	                            b = constValues[j];
                            }

							if (a.equals(b)) {
								solutionFound = false;
								break;
							}
						}
					}
				}

				/*
				 * If current values are not a solution try a new solution. If no more combinations are available, fail.
				 */
				if (!solutionFound) {
					i = nPart - 1;
					while (!its[i].hasNext()) {
						if (i == 0) {
	                        return false;
                        }
						its[i] = drs[i].valueIterator();
						values[i] = its[i].next();
						i--;
					}
					values[i] = its[i].next();
				}
			}
		}

		return true;
	}

	public boolean isSatisfiable(Set<Literal> nodes, Map<Literal, Set<Literal>> neqs)
	                throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException {

		Literal[] literals = nodes.toArray(new Literal[0]);

		// TODO: Evaluate replacing with intset or just int arrays.
		Set<Integer> vars = new HashSet<Integer>();
		Set<Integer> consts = new HashSet<Integer>();
		Object[] constValues = new Object[literals.length];

		Map<Literal, Integer> rev = new HashMap<Literal, Integer>();

		for (int i = 0; i < literals.length; i++) {
			rev.put(literals[i], i);
			if (literals[i].isNominal()) {
				consts.add(i);
				constValues[i] = literals[i].getValue();
			}
            else {
	            vars.add(i);
            }
		}

		@SuppressWarnings("unchecked")
		Set<Integer>[] ne = new Set[literals.length];
		for (Map.Entry<Literal, Set<Literal>> e : neqs.entrySet()) {
			int index = rev.get(e.getKey());
			ne[index] = new HashSet<Integer>();
			for (Literal l : e.getValue()) {
	            ne[index].add(rev.get(l));
            }

		}

		if (log.isLoggable(Level.FINEST)) {
			log.finest(format("Checking satisfiability for %d variables and %d constants", vars.size(), consts.size()));
		}

		/*
		 * 1. Get to DNF. After this step <code>dnfMap</code> associates literals with a collection of D-conjunctions,
		 * of which it must satisfy at least one to be generally satisfied.
		 */
		@SuppressWarnings("unchecked")
		Collection<ATermAppl>[] dnfs = new Collection[literals.length];
		for (int i = 0; i < literals.length; i++) {
			ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(literals[i].getTypes()));
			ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
			if (ATermUtils.isOr(dnf)) {
				List<ATermAppl> disjuncts = new ArrayList<ATermAppl>();
				for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext()) {
	                disjuncts.add((ATermAppl) l.getFirst());
                }
				dnfs[i] = disjuncts;
			}
            else {
	            dnfs[i] = Collections.singleton(dnf);
            }
		}

		return isSatisfiable(consts, vars, dnfs, constValues, ne);
	}

	public boolean define(ATermAppl name, ATermAppl datarange) {
		if (name.equals(datarange)) {
	        throw new IllegalArgumentException();
        }

		if (namedDataRanges.containsKey(name)) {
	        return false;
        }

		namedDataRanges.put(name, datarange);
		declaredUndefined.remove(name);

		return true;
	}

	private DataRange<?> normalizeVarRanges(ATermAppl dconjunction) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {

		DataRange<?> ret;

		if (ATermUtils.isAnd(dconjunction)) {
			Collection<DataRange<?>> ranges = new LinkedHashSet<DataRange<?>>();
			for (ATermList l = (ATermList) dconjunction.getArgument(0); !l.isEmpty(); l = l.getNext()) {
				DataRange<?> dr = getDataRange((ATermAppl) l.getFirst());
				if (dr.isEmpty()) {
	                return EMPTY_RANGE;
                }
				ranges.add(dr);
			}

			Set<DataValueEnumeration<?>> positiveEnumerations = new HashSet<DataValueEnumeration<?>>();
			Set<DataValueEnumeration<?>> negativeEnumerations = new HashSet<DataValueEnumeration<?>>();
			Set<RestrictedDatatype<?>> positiveRestrictions = new HashSet<RestrictedDatatype<?>>();
			Set<RestrictedDatatype<?>> negativeRestrictions = new HashSet<RestrictedDatatype<?>>();

			partitionDConjunction(ranges, positiveEnumerations, negativeEnumerations, positiveRestrictions,
			                negativeRestrictions);

			/*
			 * 1. If an enumeration is present, test each element in it against other conjuncts
			 */
			if (!positiveEnumerations.isEmpty()) {
				DataRange<?> enumeration = findSmallestEnumeration(positiveEnumerations);
				Set<Object> remainingValues = new HashSet<Object>();
				Iterator<?> it = enumeration.valueIterator();
				boolean same = true;
				while (it.hasNext()) {
					Object value = it.next();
					boolean permit = true;
					for (DataRange<?> dr : ranges) {
						if ((dr != enumeration) && !dr.contains(value)) {
							permit = false;
							same = false;
							break;
						}
					}
					if (permit) {
	                    remainingValues.add(value);
                    }
				}
				if (same) {
	                return enumeration;
                }
                else if (remainingValues.isEmpty()) {
	                return EMPTY_RANGE;
                }
                else {
	                return new DataValueEnumeration<Object>(remainingValues);
                }
			}

			/*
			 * If there are only negative restrictions, the conjunction is trivially satisfiable (because the
			 * interpretation domain is infinite).
			 */
			if (positiveRestrictions.isEmpty()) {
	            return TRIVIALLY_SATISFIABLE;
            }

			/*
			 * Verify that all positive restrictions are on the same primitive type. If not, the data range is empty
			 * because the primitives are disjoint.
			 */
			Datatype<?> rootDt = null;
			for (RestrictedDatatype<?> pr : positiveRestrictions) {
				final Datatype<?> dt = pr.getDatatype().getPrimitiveDatatype();

				if (rootDt == null) {
	                rootDt = dt;
                }
                else if (!rootDt.equals(dt)) {
	                return EMPTY_RANGE;
                }
			}

			Iterator<RestrictedDatatype<?>> it = positiveRestrictions.iterator();
			RestrictedDatatype<?> rd = it.next();
			while (it.hasNext()) {
				RestrictedDatatype<?> other = it.next();
				rd = rd.intersect(other, false);
			}

			for (RestrictedDatatype<?> other : negativeRestrictions) {
				if (other.isEmpty()) {
	                continue;
                }

				final Datatype<?> dt = other.getDatatype().getPrimitiveDatatype();

				if (!rootDt.equals(dt)) {
	                continue;
                }

				rd = rd.intersect(other, true);
			}

			if (!negativeEnumerations.isEmpty()) {
				Set<Object> notOneOf = new HashSet<Object>();
				for (DataValueEnumeration<?> enm : negativeEnumerations) {
					for (Iterator<?> oi = enm.valueIterator(); oi.hasNext();) {
						notOneOf.add(oi.next());
					}
				}
				rd = rd.exclude(notOneOf);
			}

			ret = rd;
		}
        else {
	        ret = getDataRange(dconjunction);
        }

		if (!ret.isFinite()) {
	        return TRIVIALLY_SATISFIABLE;
        }

		return ret;
	}

	public Collection<ATermAppl> listDataRanges() {
		Collection<ATermAppl> dataRanges = new HashSet<ATermAppl>(coreDatatypes.keySet());
		dataRanges.addAll(declaredUndefined);
		dataRanges.addAll(namedDataRanges.keySet());

		return dataRanges;
	}

	public boolean validLiteral(ATermAppl typedLiteral) throws UnrecognizedDatatypeException {
		if (!ATermUtils.isLiteral(typedLiteral)) {
	        throw new IllegalArgumentException();
        }
		final ATermAppl dtTerm = (ATermAppl) typedLiteral.getArgument(ATermUtils.LIT_URI_INDEX);
		if (dtTerm == null) {
	        throw new IllegalArgumentException();
        }
		final Datatype<?> dt = getDatatype(dtTerm);
		if (dt == null) {
	        throw new UnrecognizedDatatypeException(dtTerm);
        }
		try {
			dt.getValue(typedLiteral);
		}
		catch (InvalidLiteralException e) {
			return false;
		}
		return true;
	}

	public Iterator<?> valueIterator(Collection<ATermAppl> dataranges) throws InvalidConstrainingFacetException,
	                InvalidLiteralException, UnrecognizedDatatypeException {

		ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(dataranges));
		ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
		if (ATermUtils.isOr(dnf)) {
			List<DataRange<?>> disjuncts = new ArrayList<DataRange<?>>();
			for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext()) {
				final DataRange<?> dr = normalizeVarRanges((ATermAppl) l.getFirst());
				disjuncts.add(dr);
			}

			final DataRange<?> disjunction = getDisjunction(disjuncts);
			if (!disjunction.isEnumerable()) {
	            throw new IllegalArgumentException();
            }
            else {
	            return disjunction.valueIterator();
            }
		}
		else {
			final DataRange<?> dr = normalizeVarRanges(dnf);
			if (!dr.isEnumerable()) {
	            throw new IllegalArgumentException();
            }
            else {
	            return dr.valueIterator();
            }
		}
	}

}
