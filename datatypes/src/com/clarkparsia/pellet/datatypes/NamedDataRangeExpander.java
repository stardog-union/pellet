package com.clarkparsia.pellet.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>
 * Title: Named Data Range Expander
 * </p>
 * <p>
 * Description: Substitutes one {@link ATermAppl} for another in a data range
 * description, based on input map. Used to implement OWL 2 datatype
 * definitions.
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
public class NamedDataRangeExpander extends ATermBaseVisitor {

	private Map<ATermAppl, ATermAppl>	map;
	private ATermAppl					ret;
	private boolean						change;

	/*
	 * TODO: Handle nesting and cycles in definitions
	 */
	public ATermAppl expand(ATermAppl input, Map<ATermAppl, ATermAppl> map) {
		if( map.isEmpty() )
			return input;

		this.map = map;
		try {
			this.visit( input );
		} catch( UnsupportedOperationException e ) {
			throw new IllegalArgumentException( e );
		}
		return ret;
	}

	public void visitAll(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitAnd(ATermAppl term) {
		boolean listChange = false;
		List<ATermAppl> args = new ArrayList<ATermAppl>();
		for( ATermList l = (ATermList) term.getArgument( 0 ); !l.isEmpty(); l = l.getNext() ) {
			ATermAppl a = (ATermAppl) l.getFirst();
			this.visit( a );
			args.add( ret );
			if( change )
				listChange = true;
		}
		if( listChange ) {
			change = true;
			ret = ATermUtils.makeAnd( ATermUtils.makeList( args ) );
		}
		else {
			change = false;
			ret = term;
		}
	}

	public void visitCard(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitHasValue(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitInverse(ATermAppl p) {
		throw new UnsupportedOperationException();
	}

	public void visitLiteral(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitMax(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitMin(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitNot(ATermAppl term) {
		ATermAppl a = (ATermAppl) term.getArgument( 0 );
		this.visit( a );
		if( change )
			ret = ATermUtils.makeNot( ret );
		else
			ret = term;
	}

	public void visitOneOf(ATermAppl term) {
		ret = term;
		change = false;
	}

	public void visitOr(ATermAppl term) {
		boolean listChange = false;
		List<ATermAppl> args = new ArrayList<ATermAppl>();
		for( ATermList l = (ATermList) term.getArgument( 0 ); !l.isEmpty(); l = l.getNext() ) {
			ATermAppl a = (ATermAppl) l.getFirst();
			this.visit( a );
			args.add( ret );
			if( change )
				listChange = true;
		}
		if( listChange ) {
			change = true;
			ret = ATermUtils.makeOr( ATermUtils.makeList( args ) );
		}
		else {
			change = false;
			ret = term;
		}
	}

	public void visitRestrictedDatatype(ATermAppl dt) {
		ret = dt;
		change = false;
	}

	public void visitSelf(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitSome(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public void visitTerm(ATermAppl term) {
		ATermAppl a = map.get( term );
		if( a == null ) {
			ret = term;
			change = false;
		}
		else {
			ret = a;
			change = true;
		}
	}

	public void visitValue(ATermAppl term) {
		ret = term;
		change = false;
	}
}
