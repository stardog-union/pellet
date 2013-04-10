package com.clarkparsia.pellet.datatypes.types.floating;

/**
 * <p>
 * Title: IEEE Float Type
 * </p>
 * <p>
 * Description: Implementation of {@link FloatingPointType} to support
 * <code>xsd:float</code>
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
public class IEEEFloatType implements FloatingPointType<Float> {

	private static final IEEEFloatType	INSTANCE;

	static {
		INSTANCE = new IEEEFloatType();
	}

	public static IEEEFloatType getInstance() {
		return INSTANCE;
	}

	private IEEEFloatType() {
		;
	}

	public Float cast(Object o) {
		return Float.class.cast( o );
	}

	public Float decrement(Float n) {
		return FloatingPointUtils.decrement( n );
	}

	public Float getNaN() {
		return Float.NaN;
	}

	public Float getNegativeInfinity() {
		return Float.NEGATIVE_INFINITY;
	}

	public Float getPositiveInfinity() {
		return Float.POSITIVE_INFINITY;
	}

	public Float increment(Float n) {
		return FloatingPointUtils.increment( n );
	}

	public Number intervalSize(Float lower, Float upper) {
		return FloatingPointUtils.intervalSize( lower, upper );
	}

	public boolean isInstance(Object o) {
		return Float.class.isInstance( o );
	}

	public boolean isNaN(Float f) {
		return f.isNaN();
	}

}
