package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

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
import net.katk.tools.Log;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;

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
public class DatatypeReasonerImpl implements DatatypeReasoner
{
	private static final Logger _log = Log.getLogger(DatatypeReasonerImpl.class);
	private static final Map<ATermAppl, Datatype<?>> _coreDatatypes = new HashMap<>();

	static
	{
		{
			_coreDatatypes.put(RDFPlainLiteral.getInstance().getName(), RDFPlainLiteral.getInstance());
			_coreDatatypes.put(XSDString.getInstance().getName(), XSDString.getInstance());
			_coreDatatypes.put(XSDNormalizedString.getInstance().getName(), XSDNormalizedString.getInstance());
			_coreDatatypes.put(XSDToken.getInstance().getName(), XSDToken.getInstance());
			_coreDatatypes.put(XSDLanguage.getInstance().getName(), XSDLanguage.getInstance());
			_coreDatatypes.put(XSDNMToken.getInstance().getName(), XSDNMToken.getInstance());
			_coreDatatypes.put(XSDName.getInstance().getName(), XSDName.getInstance());
			_coreDatatypes.put(XSDNCName.getInstance().getName(), XSDNCName.getInstance());
		}

		_coreDatatypes.put(XSDBoolean.getInstance().getName(), XSDBoolean.getInstance());

		{
			_coreDatatypes.put(OWLReal.getInstance().getName(), OWLReal.getInstance());
			_coreDatatypes.put(OWLRational.getInstance().getName(), OWLRational.getInstance());
			_coreDatatypes.put(XSDDecimal.getInstance().getName(), XSDDecimal.getInstance());
			_coreDatatypes.put(XSDInteger.getInstance().getName(), XSDInteger.getInstance());
			_coreDatatypes.put(XSDLong.getInstance().getName(), XSDLong.getInstance());
			_coreDatatypes.put(XSDInt.getInstance().getName(), XSDInt.getInstance());
			_coreDatatypes.put(XSDShort.getInstance().getName(), XSDShort.getInstance());
			_coreDatatypes.put(XSDByte.getInstance().getName(), XSDByte.getInstance());
			_coreDatatypes.put(XSDNonNegativeInteger.getInstance().getName(), XSDNonNegativeInteger.getInstance());
			_coreDatatypes.put(XSDNonPositiveInteger.getInstance().getName(), XSDNonPositiveInteger.getInstance());
			_coreDatatypes.put(XSDNegativeInteger.getInstance().getName(), XSDNegativeInteger.getInstance());
			_coreDatatypes.put(XSDPositiveInteger.getInstance().getName(), XSDPositiveInteger.getInstance());
			_coreDatatypes.put(XSDUnsignedLong.getInstance().getName(), XSDUnsignedLong.getInstance());
			_coreDatatypes.put(XSDUnsignedInt.getInstance().getName(), XSDUnsignedInt.getInstance());
			_coreDatatypes.put(XSDUnsignedShort.getInstance().getName(), XSDUnsignedShort.getInstance());
			_coreDatatypes.put(XSDUnsignedByte.getInstance().getName(), XSDUnsignedByte.getInstance());
		}

		_coreDatatypes.put(XSDDouble.getInstance().getName(), XSDDouble.getInstance());

		_coreDatatypes.put(XSDFloat.getInstance().getName(), XSDFloat.getInstance());

		{
			_coreDatatypes.put(XSDDateTime.getInstance().getName(), XSDDateTime.getInstance());
			_coreDatatypes.put(XSDDateTimeStamp.getInstance().getName(), XSDDateTimeStamp.getInstance());
		}
		{
			_coreDatatypes.put(XSDDate.getInstance().getName(), XSDDate.getInstance());
			_coreDatatypes.put(XSDGYearMonth.getInstance().getName(), XSDGYearMonth.getInstance());
			_coreDatatypes.put(XSDGMonthDay.getInstance().getName(), XSDGMonthDay.getInstance());
			_coreDatatypes.put(XSDGYear.getInstance().getName(), XSDGYear.getInstance());
			_coreDatatypes.put(XSDGMonth.getInstance().getName(), XSDGMonth.getInstance());
			_coreDatatypes.put(XSDGDay.getInstance().getName(), XSDGDay.getInstance());
			_coreDatatypes.put(XSDTime.getInstance().getName(), XSDTime.getInstance());
		}

		_coreDatatypes.put(XSDDuration.getInstance().getName(), XSDDuration.getInstance());

		_coreDatatypes.put(XSDAnyURI.getInstance().getName(), XSDAnyURI.getInstance());
	}
	private static final DataRange<?> EMPTY_RANGE = new EmptyDataRange<>();

	private static final DataRange<?> TRIVIALLY_SATISFIABLE = new DataRange<Object>()
	{

		@Override
		public boolean contains(final Object value)
		{
			return true;
		}

		@Override
		public boolean containsAtLeast(final int n)
		{
			return true;
		}

		@Override
		public boolean equals(final Object obj)
		{
			return this == obj;
		}

		@Override
		public int hashCode()
		{
			return super.hashCode();
		}

		@Override
		public boolean isEmpty()
		{
			return false;
		}

		@Override
		public boolean isEnumerable()
		{
			return false;
		}

		@Override
		public boolean isFinite()
		{
			return false;
		}

		@Override
		public Iterator<Object> valueIterator()
		{
			throw new UnsupportedOperationException();
		}

	};

	private static <T> DataValueEnumeration<? extends T> findSmallestEnumeration(final Collection<DataValueEnumeration<? extends T>> ranges)
			{
				DataValueEnumeration<? extends T> ret = null;
				int best = Integer.MAX_VALUE;
				for (final DataValueEnumeration<? extends T> r : ranges)
				{
					final DataValueEnumeration<? extends T> e = r;
					@SuppressWarnings("deprecation")
					final int s = e.size(); // FIXME may crash any time.
					if (s < best)
					{
						ret = e;
						best = s;
					}
				}
				return ret;
			}

			private static final ATermAppl getDatatypeName(final ATermAppl literal)
			{
				if (!ATermUtils.isLiteral(literal))
				{
					final String msg = "Method _expected an ATermAppl literal as an argument";
					_log.severe(msg);
					throw new IllegalArgumentException(msg);
				}

				final ATermAppl dtName = (ATermAppl) literal.getArgument(ATermUtils.LIT_URI_INDEX);
				if (ATermUtils.EMPTY.equals(dtName))
				{
					final String msg = "Untyped literals not supported by this datatype reasoner";
					_log.severe(msg);
					throw new IllegalArgumentException(msg);
				}

				return dtName;
			}

			private static int inequalityCount(final Set<Integer>[] nes, final int xIndex)
			{

				final Set<Integer> others = nes[xIndex];
				return others == null ? 0 : others.size();
			}

			private static <T> void partitionDConjunction(final Collection<DataRange<? extends T>> dconjunction, final Set<DataValueEnumeration<? extends T>> positiveEnumerations, final Set<DataValueEnumeration<? extends T>> negativeEnumerations, final Set<RestrictedDatatype<? extends T>> positiveRestrictions, final Set<RestrictedDatatype<? extends T>> negativeRestrictions)
			{
				for (final DataRange<? extends T> dr : dconjunction)
					if (dr instanceof DataValueEnumeration)
						positiveEnumerations.add((DataValueEnumeration<? extends T>) dr);
					else
						if (dr instanceof RestrictedDatatype)
							positiveRestrictions.add((RestrictedDatatype<? extends T>) dr);
						else
							if (dr instanceof NegatedDataRange)
							{
								final DataRange<? extends T> ndr = ((NegatedDataRange<? extends T>) dr).getDataRange();
								if (ndr instanceof DataValueEnumeration)
									negativeEnumerations.add((DataValueEnumeration<? extends T>) ndr);
								else
									if (ndr instanceof RestrictedDatatype)
										negativeRestrictions.add((RestrictedDatatype<? extends T>) ndr);
									else
										if (dr != TRIVIALLY_SATISFIABLE)
											_log.warning("Unknown datatype: " + dr);
							}
							else
								if (dr != TRIVIALLY_SATISFIABLE)
									_log.warning("Unknown datatype: " + dr);
			}

			private static boolean removeInequalities(final Set<Integer>[] nes, final int xIndex)
			{

				final Set<Integer> others = nes[xIndex];

				if (others == null)
					return false;
				else
				{
					for (final Integer yIndex : others)
					{
						final Set<Integer> s = nes[yIndex];
						if (s == null)
							throw new IllegalStateException();
						if (!s.remove(xIndex))
							throw new IllegalStateException();
					}
					return true;
				}
			}

			private final Set<ATermAppl> declaredUndefined;
			private final NamedDataRangeExpander expander;
			private final Map<ATermAppl, ATermAppl> namedDataRanges;

			public DatatypeReasonerImpl()
			{
				declaredUndefined = new HashSet<>();
				expander = new NamedDataRangeExpander();
				namedDataRanges = new HashMap<>();
			}

			private boolean containedIn(final Object value, final ATermAppl dconjunction) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{
				if (ATermUtils.isAnd(dconjunction))
				{
					for (ATermList l = (ATermList) dconjunction.getArgument(0); !l.isEmpty(); l = l.getNext())
						if (!getDataRange((ATermAppl) l.getFirst()).contains(value))
							return false;
					return true;
				}
				else
					return getDataRange(dconjunction).contains(value);
			}

			@Override
			public boolean containsAtLeast(final int n, final Collection<ATermAppl> ranges) throws UnrecognizedDatatypeException, InvalidConstrainingFacetException, InvalidLiteralException
			{

				final ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(ranges));
				final ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
				if (ATermUtils.isOr(dnf))
				{
					final List<DataRange<?>> disjuncts = new ArrayList<>();
					for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext())
					{
						final DataRange<?> dr = normalizeVarRanges((ATermAppl) l.getFirst());
						if (!dr.isEmpty())
							disjuncts.add(dr);
					}

					final DataRange<?> disjunction = getDisjunction(disjuncts);
					return disjunction.containsAtLeast(n);
				}
				else
				{
					final DataRange<?> dr = normalizeVarRanges(dnf);
					return dr.containsAtLeast(n);
				}

			}

			@Override
			public boolean declare(final ATermAppl name)
			{
				if (isDeclared(name))
					return false;
				else
				{
					declaredUndefined.add(name);
					return true;
				}
			}

			@Override
			public ATermAppl getCanonicalRepresentation(final ATermAppl literal) throws InvalidLiteralException, UnrecognizedDatatypeException
			{
				final ATermAppl dtName = getDatatypeName(literal);
				final Datatype<?> dt = getDatatype(dtName);
				if (dt == null)
					switch (PelletOptions.UNDEFINED_DATATYPE_HANDLING)
					{
						case INFINITE_STRING:
							return literal;
						case EMPTY:
							throw new InvalidLiteralException(dtName, ATermUtils.getLiteralValue(literal));
						case EXCEPTION:
							throw new UnrecognizedDatatypeException(dtName);
						default:
							throw new IllegalStateException();
					}
				else
					return dt.getCanonicalRepresentation(literal);
			}

			private DataRange<?> getDataRange(final ATermAppl a) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{
				// TODO: Investigate the impact of keeping a results _cache here

				/*
				 * rdfs:Literal
				 */
				if (a.equals(ATermUtils.TOP_LIT))
					return TRIVIALLY_SATISFIABLE;

				/*
				 * Negation of rdfs:Literal
				 */
				if (a.equals(ATermUtils.BOTTOM_LIT))
					return EMPTY_RANGE;

				/*
				 * Named datatype
				 */
				if (ATermUtils.isPrimitive(a))
				{
					Datatype<?> dt = getDatatype(a);
					if (dt == null)
						switch (PelletOptions.UNDEFINED_DATATYPE_HANDLING)
						{
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
					return dt.asDataRange();
				}

				/*
				 * Datatype restriction
				 */
				if (ATermUtils.isRestrictedDatatype(a))
				{

					/*
					 * Start with the full _data range for the datatype
					 */
					final ATermAppl dtTerm = (ATermAppl) a.getArgument(0);
					final DataRange<?> dt = getDataRange(dtTerm);
					if (!(dt instanceof RestrictedDatatype<?>))
						throw new InvalidConstrainingFacetException(dtTerm, dt);

					RestrictedDatatype<?> dr = (RestrictedDatatype<?>) dt;

					/*
					 * Apply each constraining facet value pair in turn
					 */
					final ATermList facetValues = (ATermList) a.getArgument(1);
					for (ATermList l = facetValues; !l.isEmpty(); l = l.getNext())
					{
						final ATermAppl fv = (ATermAppl) l.getFirst();
						final ATermAppl facet = (ATermAppl) fv.getArgument(0);
						final ATermAppl valueTerm = (ATermAppl) fv.getArgument(1);

						Object value;
						try
						{
							value = getValue(valueTerm);
						}
						catch (final InvalidLiteralException e)
						{
							throw new InvalidConstrainingFacetException(facet, valueTerm, e);
						}
						dr = dr.applyConstrainingFacet(facet, value);
					}

					return dr;
				}

				/*
				 * Negated datarange
				 */
				if (ATermUtils.isNot(a))
				{
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
				if (ATermUtils.isNominal(a))
				{
					final ATermAppl literal = (ATermAppl) a.getArgument(0);
					final DataRange<?> dr = new DataValueEnumeration<>(Collections.singleton(getValue(literal)));
					return dr;
				}

				final String msg = format("Unrecognized input term (%s) for datarange conversion", a);
				_log.severe(msg);
				throw new IllegalArgumentException(msg);
			}

			@Override
			public Datatype<?> getDatatype(final ATermAppl uri)
			{
				try
				{
					Datatype<?> dt = _coreDatatypes.get(uri);
					if (dt == null)
					{
						final ATermAppl definition = namedDataRanges.get(uri);
						if (definition != null)
							if (ATermUtils.isRestrictedDatatype(definition))
							{
								final RestrictedDatatype<?> dataRange = (RestrictedDatatype<?>) getDataRange(definition);
								final NamedDatatype<?> namedDatatype = new NamedDatatype<>(uri, dataRange);
								dt = namedDatatype;
							}
					}

					return dt;
				}
				catch (final Exception e)
				{
					throw new RuntimeException(e);
				}
			}

			private DataRange<?> getDisjunction(final Collection<DataRange<?>> ranges)
			{

				if (ranges.size() == 1)
					return ranges.iterator().next();

				for (final DataRange<?> r : ranges)
					if (r == TRIVIALLY_SATISFIABLE)
						return r;

				Set<Object> oneOf = Collections.emptySet();
				final Map<Datatype<?>, Set<RestrictedDatatype<?>>> byPrimitive = new HashMap<>();

				/*
				 * Organize the input _data ranges into restrictions partitioned by _data and a merged value enumeration.
				 */
				for (final DataRange<?> dr : ranges)
					if (dr instanceof RestrictedDatatype)
					{
						final RestrictedDatatype<?> rd = (RestrictedDatatype<?>) dr;
						final Datatype<?> pd = rd.getDatatype().getPrimitiveDatatype();
						Set<RestrictedDatatype<?>> others = byPrimitive.get(pd);
						if (others == null)
						{
							others = new HashSet<>();
							byPrimitive.put(pd, others);
						}
						others.add(rd);
					}
					else
						if (dr instanceof DataValueEnumeration)
						{
							final DataValueEnumeration<?> enm = (DataValueEnumeration<?>) dr;
							if (oneOf.isEmpty())
								oneOf = new HashSet<>();
							for (final Iterator<?> it = enm.valueIterator(); it.hasNext();)
								oneOf.add(it.next());
						}

				/*
				 * Merge _data ranges that have the same primitive datatype
				 */
				final Set<RestrictedDatatype<?>> disjointRanges = new HashSet<>();
				for (final Set<RestrictedDatatype<?>> s : byPrimitive.values())
				{
					final Iterator<RestrictedDatatype<?>> it = s.iterator();
					RestrictedDatatype<?> merge = it.next();
					while (it.hasNext())
						merge = merge.union(it.next());

					disjointRanges.add(merge);
				}

				/*
				 * Discard any enum elements that are included in other disjuncts
				 */
				for (final Iterator<Object> it = oneOf.iterator(); it.hasNext();)
				{
					final Object o = it.next();
					for (final RestrictedDatatype<?> rd : disjointRanges)
						if (rd.contains(o))
							it.remove();
				}

				return new UnionDataRange<>(disjointRanges, oneOf);
			}

			@Override
			public ATermAppl getLiteral(final Object value)
			{
				for (final Datatype<?> dt : _coreDatatypes.values())
					if (dt.isPrimitive())
						if (dt.asDataRange().contains(value))
							return dt.getLiteral(value);

				final String msg = "Value is not in the value space of any recognized datatypes: " + value.toString();
				_log.severe(msg);
				throw new IllegalArgumentException(msg);
			}

			@Override
			public Object getValue(final ATermAppl literal) throws InvalidLiteralException, UnrecognizedDatatypeException
			{
				final ATermAppl dtName = getDatatypeName(literal);
				final Datatype<?> dt = getDatatype(dtName);
				if (dt == null)
					switch (PelletOptions.UNDEFINED_DATATYPE_HANDLING)
					{
						case INFINITE_STRING:
							return literal;
						case EMPTY:
							throw new InvalidLiteralException(dtName, ATermUtils.getLiteralValue(literal));
						case EXCEPTION:
							throw new UnrecognizedDatatypeException(dtName);
						default:
							throw new IllegalStateException();
					}
				else
					return dt.getValue(literal);
			}

			@Override
			public boolean isDeclared(final ATermAppl name)
			{
				return ATermUtils.TOP_LIT.equals(name) || _coreDatatypes.containsKey(name) || namedDataRanges.containsKey(name) || declaredUndefined.contains(name);
			}

			@Override
			public boolean isDefined(final ATermAppl name)
			{
				if (ATermUtils.TOP_LIT.equals(name))
					return true;

				if (_coreDatatypes.containsKey(name))
					return true;
				if (namedDataRanges.containsKey(name))
					return true;

				return false;
			}

			@Override
			public ATermAppl getDefinition(final ATermAppl name)
			{
				return namedDataRanges.get(name);
			}

			@Override
			public boolean isSatisfiable(final Collection<ATermAppl> dataranges) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{
				return isSatisfiable(dataranges, null);
			}

			@Override
			public boolean isSatisfiable(final Collection<ATermAppl> dataranges, final Object value) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{
				Set<Integer> consts, vars;

				if (value == null)
				{
					/*
					 * TODO: See if code in next method can be restructured to avoid this allocation.
					 */
					consts = new HashSet<>();
					vars = new HashSet<>(Collections.singleton(0));
				}
				else
				{
					consts = Collections.singleton(0);
					vars = Collections.emptySet();
				}

				final ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(dataranges));
				final ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
				Collection<ATermAppl> dnfDisjuncts;
				if (ATermUtils.isOr(dnf))
				{
					final List<ATermAppl> disjuncts = new ArrayList<>();
					for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext())
						disjuncts.add((ATermAppl) l.getFirst());
					dnfDisjuncts = disjuncts;
				}
				else
					dnfDisjuncts = Collections.singleton(dnf);

				@SuppressWarnings("unchecked")
				final Collection<ATermAppl>[] dnfTypes = new Collection[] { dnfDisjuncts };

				@SuppressWarnings("unchecked")
				final Set<Integer>[] ne = new Set[] { Collections.<Integer> emptySet() };

				return isSatisfiable(consts, vars, dnfTypes, new Object[] { value }, ne);
			}

			private boolean isSatisfiable(final Set<Integer> consts, final Set<Integer> vars, final Collection<ATermAppl>[] dnfTypes, final Object[] constValues, final Set<Integer>[] ne) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{

				/*
				 * TODO: Remove need for consts and vars sets by using null in constValues array
				 */

				final int n = dnfTypes.length;

				/*
				 * 1. Loop and eliminate any easy, obvious unsats
				 */
				for (int i = 0; i < n; i++)
				{
					final Collection<ATermAppl> drs = dnfTypes[i];
					for (final Iterator<ATermAppl> it = drs.iterator(); it.hasNext();)
					{
						final ATermAppl dr = it.next();
						if (ATermUtils.BOTTOM_LIT.equals(dr))
							it.remove();
					}
					if (drs.isEmpty())
						return false;
				}

				/*
				 * 2. Get normalized form of _data ranges
				 */
				final DataRange<?>[] normalized = new DataRange[n];
				for (int i = 0; i < n; i++)
					if (consts.contains(i))
					{

						boolean satisfied = false;
						for (final ATermAppl a : dnfTypes[i])
							if (containedIn(constValues[i], a))
							{
								satisfied = true;
								break;
							}
						if (satisfied)
							normalized[i] = TRIVIALLY_SATISFIABLE;
						else
							return false;

					}
					else
					{

						List<DataRange<?>> drs = new ArrayList<>();
						for (final ATermAppl a : dnfTypes[i])
						{
							final DataRange<?> dr = normalizeVarRanges(a);
							if (dr == TRIVIALLY_SATISFIABLE)
							{
								drs = Collections.<DataRange<?>> singletonList(TRIVIALLY_SATISFIABLE);
								break;
							}
							else
								if (!dr.isEmpty())
									drs.add(dr);
						}
						if (drs.isEmpty())
							return false;
						else
							normalized[i] = getDisjunction(drs);

					}

				/*
				 * Alg lines 7 - 22 (without the 12-13 or 19-20 blocks)
				 */
				for (final Iterator<Integer> it = vars.iterator(); it.hasNext();)
				{
					final Integer i = it.next();
					final DataRange<?> dr = normalized[i];

					/*
					 * First half of _condition 9 - 11 block
					 */
					if (TRIVIALLY_SATISFIABLE == dr)
					{
						it.remove();
						removeInequalities(ne, i);
						continue;
					}

					/*
					 * Line 15
					 */
					if (dr.isEmpty())
						return false;

					/*
					 * Second half of _condition 9 - 11 block
					 */
					if (dr.containsAtLeast(inequalityCount(ne, i) + 1))
					{
						it.remove();
						removeInequalities(ne, i);
						continue;
					}

					/*
					 * Data range is a singleton, replace variable with constant (lines 17 - 18)
					 */
					if (dr.isFinite() && dr.isEnumerable() && !dr.containsAtLeast(2))
					{
						final Object c = dr.valueIterator().next();
						it.remove();
						consts.add(i);
						constValues[i] = c;
						normalized[i] = TRIVIALLY_SATISFIABLE;
						continue;
					}
				}

				if (_log.isLoggable(Level.FINEST))
					_log.finest(format("After variable _data range normalization %d variables and %d constants", vars.size(), consts.size()));

				/*
				 * Constant checks (alg lines 23 - 30)
				 */
				for (final Integer i : consts)
				{

					/*
					 * Check that any constant,constant inequalities are satisfied
					 */
					final Set<Integer> diffs = ne[i];
					if (diffs != null)
						for (final Iterator<Integer> it = diffs.iterator(); it.hasNext();)
						{
							final int j = it.next();
							if (consts.contains(j))
							{

								if (constValues[i].equals(constValues[j]))
									return false;

								it.remove();
								ne[j].remove(i);
							}
						}
				}

				/*
				 * Try to eliminate any more variables that can be removed
				 */
				for (final Iterator<Integer> it = vars.iterator(); it.hasNext();)
				{
					final int i = it.next();

					final DataRange<?> dr = normalized[i];

					final Set<Integer> diffs = ne[i];
					final int min = (diffs == null) ? 1 : diffs.size() + 1;
					if (dr.containsAtLeast(min))
					{
						it.remove();
						if (diffs != null)
							for (final int j : diffs)
								if (ne[j] != null)
									ne[j].remove(i);

						ne[i] = null;
						vars.remove(i);
					}
				}

				if (_log.isLoggable(Level.FINEST))
					_log.finest(format("After size check on variable _data ranges %d variables", vars.size()));

				if (vars.isEmpty())
					return true;

				/*
				 * Assertion: at this point, all remaining variables are from finite and enumerable _data ranges.
				 */

				/*
				 * Partition remaining variables into disjoint collections
				 */
				final Set<Integer> remaining = new HashSet<>(vars);
				final List<Set<Integer>> partitions = new ArrayList<>();
				while (!remaining.isEmpty())
				{
					final Set<Integer> p = new HashSet<>();
					final Iterator<Integer> it = remaining.iterator();
					final int i = it.next();
					it.remove();
					p.add(i);
					if (ne[i] != null)
					{
						final Set<Integer> others = new HashSet<>();
						others.addAll(ne[i]);
						while (!others.isEmpty())
						{
							final Iterator<Integer> jt = others.iterator();
							final int j = jt.next();
							jt.remove();
							if (remaining.contains(j))
							{
								p.add(j);
								remaining.remove(j);
								if (ne[j] != null)
									others.addAll(ne[j]);
							}
						}

					}
					partitions.add(p);
				}

				if (_log.isLoggable(Level.FINEST))
					_log.finest(format("Enumerating to find solutions for %d partitions", partitions.size()));

				/*
				 * Enumerate until a solution is found
				 */
				for (final Set<Integer> p : partitions)
				{
					final int nPart = p.size();

					final int[] indices = new int[nPart];
					final Map<Integer, Integer> revInd = new HashMap<>();
					final DataRange<?>[] drs = new DataRange[nPart];

					int i = 0;
					for (final int j : p)
					{
						drs[i] = normalized[j];
						indices[i] = j;
						revInd.put(j, i);
						i++;
					}

					final Iterator<?>[] its = new Iterator[nPart];
					for (i = 0; i < nPart; i++)
						its[i] = drs[i].valueIterator();

					final Object[] values = new Object[nPart];
					/*
					 * Assign a value to each
					 */
					for (i = 0; i < nPart; i++)
						values[i] = its[i].next();

					boolean solutionFound = false;
					while (!solutionFound)
					{
						/*
						 * Check solution
						 */
						solutionFound = true;
						for (i = 0; i < nPart && solutionFound; i++)
						{
							final Set<Integer> diffs = ne[indices[i]];
							if (diffs != null)
							{
								final Object a = values[i];
								for (final int j : diffs)
								{

									Object b;
									if (p.contains(j))
										b = values[revInd.get(j)];
									else
										b = constValues[j];

									if (a.equals(b))
									{
										solutionFound = false;
										break;
									}
								}
							}
						}

						/*
						 * If _current values are not a solution try a new solution. If no more combinations are available, fail.
						 */
						if (!solutionFound)
						{
							i = nPart - 1;
							while (!its[i].hasNext())
							{
								if (i == 0)
									return false;
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

			@Override
			public boolean isSatisfiable(final Set<Literal> nodes, final Map<Literal, Set<Literal>> neqs) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{

				final Literal[] literals = nodes.toArray(new Literal[0]);

				// TODO: Evaluate replacing with intset or just int arrays.
				final Set<Integer> vars = new HashSet<>();
				final Set<Integer> consts = new HashSet<>();
				final Object[] constValues = new Object[literals.length];

				final Map<Literal, Integer> rev = new HashMap<>();

				for (int i = 0; i < literals.length; i++)
				{
					rev.put(literals[i], i);
					if (literals[i].isNominal())
					{
						consts.add(i);
						constValues[i] = literals[i].getValue();
					}
					else
						vars.add(i);
				}

				@SuppressWarnings("unchecked")
				final Set<Integer>[] ne = new Set[literals.length];
				for (final Map.Entry<Literal, Set<Literal>> e : neqs.entrySet())
				{
					final int index = rev.get(e.getKey());
					ne[index] = new HashSet<>();
					for (final Literal l : e.getValue())
						ne[index].add(rev.get(l));

				}

				if (_log.isLoggable(Level.FINEST))
					_log.finest(format("Checking satisfiability for %d variables and %d constants", vars.size(), consts.size()));

				/*
				 * 1. Get to DNF. After this step <code>dnfMap</code> associates literals with a collection of D-conjunctions,
				 * of which it must satisfy at least one to be generally satisfied.
				 */
				@SuppressWarnings("unchecked")
				final Collection<ATermAppl>[] dnfs = new Collection[literals.length];
				for (int i = 0; i < literals.length; i++)
				{
					final ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(literals[i].getTypes()));
					final ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
					if (ATermUtils.isOr(dnf))
					{
						final List<ATermAppl> disjuncts = new ArrayList<>();
						for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext())
							disjuncts.add((ATermAppl) l.getFirst());
						dnfs[i] = disjuncts;
					}
					else
						dnfs[i] = Collections.singleton(dnf);
				}

				return isSatisfiable(consts, vars, dnfs, constValues, ne);
			}

			@Override
			public boolean define(final ATermAppl name, final ATermAppl datarange)
			{
				if (name.equals(datarange))
					throw new IllegalArgumentException();

				if (namedDataRanges.containsKey(name))
					return false;

				namedDataRanges.put(name, datarange);
				declaredUndefined.remove(name);

				return true;
			}

			private DataRange<?> normalizeVarRanges(final ATermAppl dconjunction) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{

				DataRange<?> ret;

				if (ATermUtils.isAnd(dconjunction))
				{
					final Collection<DataRange<?>> ranges = new LinkedHashSet<>();
					for (ATermList l = (ATermList) dconjunction.getArgument(0); !l.isEmpty(); l = l.getNext())
					{
						final DataRange<?> dr = getDataRange((ATermAppl) l.getFirst());
						if (dr.isEmpty())
							return EMPTY_RANGE;
						ranges.add(dr);
					}

					final Set<DataValueEnumeration<?>> positiveEnumerations = new HashSet<>();
					final Set<DataValueEnumeration<?>> negativeEnumerations = new HashSet<>();
					final Set<RestrictedDatatype<?>> positiveRestrictions = new HashSet<>();
					final Set<RestrictedDatatype<?>> negativeRestrictions = new HashSet<>();

					partitionDConjunction(ranges, positiveEnumerations, negativeEnumerations, positiveRestrictions, negativeRestrictions);

					/*
					 * 1. If an enumeration is present, test each element in it against other conjuncts
					 */
					if (!positiveEnumerations.isEmpty())
					{
						final DataRange<?> enumeration = findSmallestEnumeration(positiveEnumerations);
						final Set<Object> remainingValues = new HashSet<>();
						final Iterator<?> it = enumeration.valueIterator();
						boolean same = true;
						while (it.hasNext())
						{
							final Object value = it.next();
							boolean permit = true;
							for (final DataRange<?> dr : ranges)
								if ((dr != enumeration) && !dr.contains(value))
								{
									permit = false;
									same = false;
									break;
								}
							if (permit)
								remainingValues.add(value);
						}
						if (same)
							return enumeration;
						else
							if (remainingValues.isEmpty())
								return EMPTY_RANGE;
							else
								return new DataValueEnumeration<>(remainingValues);
					}

					/*
					 * If there are only negative restrictions, the conjunction is trivially satisfiable (because the
					 * interpretation domain is infinite).
					 */
					if (positiveRestrictions.isEmpty())
						return TRIVIALLY_SATISFIABLE;

					/*
					 * Verify that all positive restrictions are on the same primitive type. If not, the _data range is empty
					 * because the primitives are disjoint.
					 */
					Datatype<?> rootDt = null;
					for (final RestrictedDatatype<?> pr : positiveRestrictions)
					{
						final Datatype<?> dt = pr.getDatatype().getPrimitiveDatatype();

						if (rootDt == null)
							rootDt = dt;
						else
							if (!rootDt.equals(dt))
								return EMPTY_RANGE;
					}

					final Iterator<RestrictedDatatype<?>> it = positiveRestrictions.iterator();
					RestrictedDatatype<?> rd = it.next();
					while (it.hasNext())
					{
						final RestrictedDatatype<?> other = it.next();
						rd = rd.intersect(other, false);
					}

					for (final RestrictedDatatype<?> other : negativeRestrictions)
					{
						if (other.isEmpty())
							continue;

						final Datatype<?> dt = other.getDatatype().getPrimitiveDatatype();

						if (rootDt != null && !rootDt.equals(dt))
							continue;

						rd = rd.intersect(other, true);
					}

					if (!negativeEnumerations.isEmpty())
					{
						final Set<Object> notOneOf = new HashSet<>();
						for (final DataValueEnumeration<?> enm : negativeEnumerations)
							for (final Iterator<?> oi = enm.valueIterator(); oi.hasNext();)
								notOneOf.add(oi.next());
						rd = rd.exclude(notOneOf);
					}

					ret = rd;
				}
				else
					ret = getDataRange(dconjunction);

				if (!ret.isFinite())
					return TRIVIALLY_SATISFIABLE;

				return ret;
			}

			@Override
			public Collection<ATermAppl> listDataRanges()
			{
				final Collection<ATermAppl> dataRanges = new HashSet<>(_coreDatatypes.keySet());
				dataRanges.addAll(declaredUndefined);
				dataRanges.addAll(namedDataRanges.keySet());

				return dataRanges;
			}

			@Override
			public boolean validLiteral(final ATermAppl typedLiteral) throws UnrecognizedDatatypeException
			{
				if (!ATermUtils.isLiteral(typedLiteral))
					throw new IllegalArgumentException();
				final ATermAppl dtTerm = (ATermAppl) typedLiteral.getArgument(ATermUtils.LIT_URI_INDEX);
				if (dtTerm == null)
					throw new IllegalArgumentException();
				final Datatype<?> dt = getDatatype(dtTerm);
				if (dt == null)
					throw new UnrecognizedDatatypeException(dtTerm);
				try
				{
					dt.getValue(typedLiteral);
				}
				catch (final InvalidLiteralException e)
				{
					return false;
				}
				return true;
			}

			@Override
			public Iterator<?> valueIterator(final Collection<ATermAppl> dataranges) throws InvalidConstrainingFacetException, InvalidLiteralException, UnrecognizedDatatypeException
			{

				final ATermAppl and = ATermUtils.makeAnd(ATermUtils.makeList(dataranges));
				final ATermAppl dnf = DNF.dnf(expander.expand(and, namedDataRanges));
				if (ATermUtils.isOr(dnf))
				{
					final List<DataRange<?>> disjuncts = new ArrayList<>();
					for (ATermList l = (ATermList) dnf.getArgument(0); !l.isEmpty(); l = l.getNext())
					{
						final DataRange<?> dr = normalizeVarRanges((ATermAppl) l.getFirst());
						disjuncts.add(dr);
					}

					final DataRange<?> disjunction = getDisjunction(disjuncts);
					if (!disjunction.isEnumerable())
						throw new IllegalArgumentException();
					else
						return disjunction.valueIterator();
				}
				else
				{
					final DataRange<?> dr = normalizeVarRanges(dnf);
					if (!dr.isEnumerable())
						throw new IllegalArgumentException();
					else
						return dr.valueIterator();
				}
			}

}
