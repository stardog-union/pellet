package org.mindswap.pellet.output;

import java.util.HashMap;
import java.util.Map;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.types.floating.XSDFloat;
import com.clarkparsia.pellet.datatypes.types.real.XSDDecimal;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;

/**
 * <p>
 * Title: ATermManchesterSyntaxRenderer
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class ATermManchesterSyntaxRenderer extends ATermBaseRenderer {
	public static final Map<ATermAppl,String> FACETS;
	static {
		FACETS = new HashMap<ATermAppl,String>();
		FACETS.put( Facet.XSD.LENGTH.getName(), "length" );
		FACETS.put( Facet.XSD.MIN_LENGTH.getName(), "minLength" );
		FACETS.put( Facet.XSD.MAX_LENGTH.getName(), "maxLength" );
		FACETS.put( Facet.XSD.PATTERN.getName(), "pattern" );
		FACETS.put( Facet.XSD.MIN_INCLUSIVE.getName(), ">=" );
		FACETS.put( Facet.XSD.MIN_EXCLUSIVE.getName(), ">" );
		FACETS.put( Facet.XSD.MAX_INCLUSIVE.getName(), "<=" );
		FACETS.put( Facet.XSD.MAX_EXCLUSIVE.getName(), "<" );
	}

	public void visitAll(ATermAppl term) {
		out.print( "(" );
		visit( (ATermAppl) term.getArgument( 0 ) );
		out.print( " only " );
		visit( (ATermAppl) term.getArgument( 1 ) );
		out.print( ")" );
	}

	public void visitAnd(ATermAppl term) {
		out.print( "(" );
		visitList( (ATermList) term.getArgument( 0 ), "and" );
		out.print( ")" );
	}

	public void visitCard(ATermAppl term) {
		out.print( "(" );
		visit( (ATermAppl) term.getArgument( 0 ) );
		out.print( " exactly " + ((ATermInt) term.getArgument( 1 )).getInt() );
		out.print( ")" );
	}

	public void visitHasValue(ATermAppl term) {
		out.print( "(" );
		visit( (ATermAppl) term.getArgument( 0 ) );
		out.print( " value " );
		ATermAppl value = (ATermAppl) ((ATermAppl) term.getArgument( 1 )).getArgument( 0 );
		if( value.getArity() == 0 )
			visitTerm( value );
		else
			visitLiteral( value );
		out.print( ")" );
	}

	public void visitInverse(ATermAppl p) {
		out.print( "inverse " );
		visit( (ATermAppl) p.getArgument( 0 ) );
	}

	public void visitLiteral(ATermAppl term) {
		final ATermAppl lexicalValue = (ATermAppl) term.getArgument( ATermUtils.LIT_VAL_INDEX );
		final ATermAppl lang = (ATermAppl) term.getArgument( ATermUtils.LIT_LANG_INDEX );
		final ATermAppl datatypeURI = (ATermAppl) term.getArgument( ATermUtils.LIT_URI_INDEX );

		if( datatypeURI.equals( XSDInteger.getInstance().getName() )
				|| datatypeURI.equals( XSDDecimal.getInstance().getName() ) ) {
			out.print( lexicalValue.getName() );
		}
		else if( datatypeURI.equals( XSDFloat.getInstance().getName() ) ) {
			out.print( lexicalValue.getName() );
			out.print( "f" );
		}
		else if( !datatypeURI.equals( ATermUtils.PLAIN_LITERAL_DATATYPE ) ) {
			out.print( lexicalValue.getName() );
			out.print( "^^" );
			out.print( datatypeURI.getName() );
		}
		else {		
			out.print( "\"" + lexicalValue.getName() + "\"" );
	
			if( !lang.equals( ATermUtils.EMPTY ) )
				out.print( "@" + lang );
		}
	}

	public void visitMax(ATermAppl term) {
		out.print( "(" );
		visit( (ATermAppl) term.getArgument( 0 ) );
		out.print( " max " + ((ATermInt) term.getArgument( 1 )).getInt() + " " );
		visit( (ATermAppl) term.getArgument( 2 ) );
		out.print( ")" );
	}

	public void visitMin(ATermAppl term) {
		out.print( "(" );
		visit( (ATermAppl) term.getArgument( 0 ) );
		out.print( " min " + ((ATermInt) term.getArgument( 1 )).getInt() + " " );
		visit( (ATermAppl) term.getArgument( 2 ) );
		out.print( ")" );
	}

	public void visitNot(ATermAppl term) {
        out.print("not ");
        visit((ATermAppl) term.getArgument(0));
	}

	public void visitOneOf(ATermAppl term) {
        out.print("{");
        ATermList list = (ATermList) term.getArgument(0);
		while (!list.isEmpty()) {
			ATermAppl value = (ATermAppl) list.getFirst();
			visit((ATermAppl) value.getArgument(0));
			list = list.getNext();
			if(!list.isEmpty())
				out.print(" ");
		}
        out.print("}");
	}

	public void visitOr(ATermAppl term) {
        out.print("(");
        visitList((ATermList) term.getArgument(0), "or");
        out.print(")");
	}

	public void visitSelf(ATermAppl term) {
        out.print("(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" Self)");
	}

	public void visitSome(ATermAppl term) {
        out.print("(");
        visit((ATermAppl) term.getArgument(0));
        out.print(" some ");
        visit((ATermAppl) term.getArgument(1));
        out.print(")");
	}

	public void visitValue(ATermAppl term) {
		out.print( "(" );
		visit( (ATermAppl) term.getArgument( 0 ) );
		out.print( ")" );
	}

	public void visitList(ATermList list, String op) {
		while( !list.isEmpty() ) {
			ATermAppl term = (ATermAppl) list.getFirst();
			visit( term );
			list = list.getNext();
			if( !list.isEmpty() )
				out.print( " " + op + " " );
		}
	}
	
	public void visitRestrictedDatatype(ATermAppl dt) {
        out.print("");
        visit((ATermAppl) dt.getArgument(0));
        out.print("[");
        ATermList list = (ATermList) dt.getArgument( 1 );
		while (!list.isEmpty()) {
			ATermAppl facet = (ATermAppl) list.getFirst();
			out.print( FACETS.get( facet.getArgument( 0 ) ));
			out.print(" ");
			visit((ATermAppl) facet.getArgument( 1 ));
			list = list.getNext();
			if(!list.isEmpty())
				out.print(", ");
		}
        out.print("]");
	}
}
