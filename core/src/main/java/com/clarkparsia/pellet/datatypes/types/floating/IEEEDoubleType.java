package com.clarkparsia.pellet.datatypes.types.floating;

/**
 * <p>
 * Title: IEEE Double Type
 * </p>
 * <p>
 * Description: Implementation of {@link FloatingPointType} to support
 * <code>xsd:double</code>
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
public class IEEEDoubleType implements FloatingPointType<Double> {

	private static final IEEEDoubleType	INSTANCE;

	static {
		INSTANCE = new IEEEDoubleType();
	}

	public static IEEEDoubleType getInstance() {
		return INSTANCE;
	}

	private IEEEDoubleType() {
		;
	}

	public Double cast(Object o) {
		return Double.class.cast( o );
	}

	public Double decrement(Double n) {
		return FloatingPointUtils.decrement( n );
	}

	public Double getNaN() {
		return Double.NaN;
	}

	public Double getNegativeInfinity() {
		return Double.NEGATIVE_INFINITY;
	}

	public Double getPositiveInfinity() {
		return Double.POSITIVE_INFINITY;
	}

	public Double increment(Double n) {
		return FloatingPointUtils.increment( n );
	}

	public Number intervalSize(Double lower, Double upper) {
		return FloatingPointUtils.intervalSize( lower, upper );
	}

	public boolean isInstance(Object o) {
		return Double.class.isInstance( o );
	}

	public boolean isNaN(Double f) {
		return f.isNaN();
	}

}
