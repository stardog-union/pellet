package com.clarkparsia.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Abstract base datatype type
 * </p>
 * <p>
 * Description: Base implementation to handle some boiler plate code shared by
 * all datatype implementations
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
public abstract class AbstractBaseDatatype<T> implements Datatype<T> {

	private final int		hashCode;
	private final ATermAppl	name;

	protected AbstractBaseDatatype(ATermAppl name) {
		if( name == null )
			throw new NullPointerException();
		if( name.getArity() != 0 )
			throw new IllegalArgumentException();

		this.name = name;
		this.hashCode = name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		/*
		 * Note that this implementation assumes singleton classes for each
		 * datatype
		 */
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;

		return true;
	}

	/**
	 * Gets the lexical form for a properly typed literal. Useful because it
	 * also validates that the input value is valid (i.e., has the correct ATerm
	 * structure and datatype).
	 * 
	 * @param input
	 *            Input <code>ATermAppl</code>, should be a literal
	 * @return <code>ATermUtils.getLiteralValue( input )</code>
	 * @throws IllegalArgumentException
	 *             if <code>!ATermUtils.isLiteral( input )</code> or if the
	 *             datatype URI does not match this datatype
	 */
	protected String getLexicalForm(ATermAppl input) {
		if( !ATermUtils.isLiteral( input ) )
			throw new IllegalArgumentException();

		return ATermUtils.getLiteralValue( input );
	}

	public ATermAppl getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

}