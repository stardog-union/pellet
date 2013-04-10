// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.output.ATermManchesterSyntaxRenderer;
import org.mindswap.pellet.utils.iterator.MultiListIterator;
import org.mindswap.pellet.utils.iterator.PairIterator;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermFactory;
import aterm.ATermInt;
import aterm.ATermList;
import aterm.pure.PureFactory;

/**
 * This class provides the functions ATerm related functions. Creating terms for
 * URI's and complex class descriptions is done here. There are also functions
 * for normalization, simplification and conversion to NNF (Normal Negation
 * Form).
 * 
 * @author Evren Sirin
 */
public class ATermUtils {
	private static final ATermFactory	factory				= new PureFactory();

	public static final AFun			LITFUN				= factory
																	.makeAFun( "literal", 3, false );
	public static final int				LIT_VAL_INDEX		= 0;
	public static final int				LIT_LANG_INDEX		= 1;
	public static final int				LIT_URI_INDEX		= 2;

	public static final AFun			ANDFUN				= factory.makeAFun( "and", 1, false );
	public static final AFun			ORFUN				= factory.makeAFun( "or", 1, false );
	public static final AFun			SOMEFUN				= factory.makeAFun( "some", 2, false );
	public static final AFun			ALLFUN				= factory.makeAFun( "all", 2, false );
	public static final AFun			NOTFUN				= factory.makeAFun( "not", 1, false );
	public static final AFun			MAXFUN				= factory.makeAFun( "max", 3, false );
	public static final AFun			MINFUN				= factory.makeAFun( "min", 3, false );
	public static final AFun			VALUEFUN			= factory.makeAFun( "value", 1, false );
	public static final AFun			SELFFUN				= factory.makeAFun( "self", 1, false );
	/**
	 * This is not used in the reasoner but kept here to be used for display
	 */
	public static final AFun			CARDFUN				= factory.makeAFun( "card", 3, false );

	public static Set<AFun>				CLASS_FUN			= SetUtils.create( new AFun[] {
			ALLFUN, SOMEFUN, MAXFUN, MINFUN, CARDFUN, ANDFUN, ORFUN, NOTFUN, VALUEFUN, SELFFUN } );
	
	public static final AFun			INVFUN				= factory.makeAFun( "inv", 1, false );

	public static final AFun			SUBFUN				= factory.makeAFun( "subClassOf", 2,
																	false );
	public static final AFun			EQCLASSFUN			= factory.makeAFun(
																	"equivalentClasses", 2, false );

	public static final AFun			SAMEASFUN			= factory.makeAFun( "sameAs", 2, false );

	public static final AFun			DISJOINTFUN			= factory.makeAFun( "disjointWith", 2,
																	false );
	public static final AFun			DISJOINTSFUN		= factory.makeAFun( "disjointClasses",
																	1, false );

	public static final AFun			DISJOINTPROPFUN		= factory.makeAFun( "disjointPropertyWith", 2,
																	false );
	public static final AFun			DISJOINTPROPSFUN	= factory.makeAFun( "disjointProperties",
																	1, false );
	
	public static final AFun			COMPLEMENTFUN		= factory.makeAFun( "complementOf", 2,
																	false );

	/**
	 * This is used to represent variables in queries
	 */
	public static final AFun			VARFUN				= factory.makeAFun( "var", 1, false );

	public static final AFun			TYPEFUN				= factory.makeAFun( "type", 2, false );

	public static final AFun			PROPFUN				= factory.makeAFun( "prop", 3, false );

	/**
	 * Added for explanations
	 */
	public static final AFun			DIFFERENTFUN		= factory.makeAFun( "different", 2,
																	false );
	public static final AFun			ALLDIFFERENTFUN		= factory.makeAFun( "allDifferent", 1,
																	false );
	public static final AFun			ASYMMETRICFUN		= factory.makeAFun( "asymmetric", 1,
																	false );
	/**
	 * @deprecated Use {@link #ASYMMETRICFUN}
	 */
	@Deprecated
    public static final AFun			ANTISYMMETRICFUN	= ASYMMETRICFUN;
	public static final AFun			FUNCTIONALFUN		= factory.makeAFun( "functional", 1,
																	false );
	public static final AFun			INVFUNCTIONALFUN	= factory.makeAFun(
																	"inverseFunctional", 1, false );
	public static final AFun			IRREFLEXIVEFUN		= factory.makeAFun( "irreflexive", 1,
																	false );
	public static final AFun			REFLEXIVEFUN		= factory.makeAFun( "reflexive", 1,
																	false );
	public static final AFun			SYMMETRICFUN		= factory.makeAFun( "symmetric", 1,
																	false );
	public static final AFun			TRANSITIVEFUN		= factory.makeAFun( "transitive", 1,
																	false );
	public static final AFun			SUBPROPFUN			= factory.makeAFun( "subProperty", 2,
																	false );
	public static final AFun			EQPROPFUN			= factory.makeAFun(
																	"equivalentProperty", 2, false );
	public static final AFun			INVPROPFUN			= factory.makeAFun( "inverseProperty",
																	2, false );
	public static final AFun			DOMAINFUN			= factory.makeAFun( "domain", 2, false );

	public static final AFun			RANGEFUN			= factory.makeAFun( "range", 2, false );
	
	public static final AFun			RULEFUN				= factory.makeAFun( "rule", 3, false );
	
	public static final AFun			BUILTINFUN			= factory.makeAFun( "builtin", 1, false );
	
	public static final AFun			DATATYPEDEFFUN		= factory.makeAFun( "datatypeDefinition", 2, false );
	
	public static final AFun			RESTRDATATYPEFUN	= factory.makeAFun( "restrictedDatatype", 2, false );
	
	public static final AFun			FACET				= factory.makeAFun( "facet", 2, false );

	public static final ATermAppl		EMPTY				= makeTermAppl( "" );

	public static final ATermList		EMPTY_LIST			= factory.makeList();
	

	/**
	 * Set of all axiom functors used in explanations
	 */
	public static Set<AFun>				AXIOM_FUN			= SetUtils.create( new AFun[] {
			TYPEFUN, PROPFUN, SAMEASFUN, DIFFERENTFUN, ALLDIFFERENTFUN,
			SUBFUN, EQCLASSFUN, DISJOINTFUN, DISJOINTSFUN, COMPLEMENTFUN,
			SUBPROPFUN, EQPROPFUN, INVPROPFUN, DOMAINFUN, RANGEFUN,
			FUNCTIONALFUN, INVFUNCTIONALFUN, TRANSITIVEFUN, SYMMETRICFUN, REFLEXIVEFUN,
			IRREFLEXIVEFUN, ANTISYMMETRICFUN, } );



	// TOP and BOTTOM concepts. TOP is not defined as T or not(T) any
	// more but added to each node manually. Defining TOP as a primitive
	// concept reduces number of GCIs and makes other reasoning tasks
	// faster
	public static final ATermAppl		TOP					= ATermUtils.makeTermAppl( "_TOP_" );
	public static final ATermAppl		BOTTOM				= ATermUtils.makeNot( TOP );

	public static final ATermAppl		TOP_OBJECT_PROPERTY = ATermUtils.makeTermAppl( "_TOP_OBJECT_PROPERTY_" );
	public static final ATermAppl		TOP_DATA_PROPERTY	= ATermUtils.makeTermAppl( "_TOP_DATA_PROPERTY_" );
	public static final ATermAppl		BOTTOM_OBJECT_PROPERTY 	= ATermUtils.makeTermAppl( "_BOTTOM_OBJECT_PROPERTY_" );
	public static final ATermAppl		BOTTOM_DATA_PROPERTY	= ATermUtils.makeTermAppl( "_BOTTOM_DATA_PROPERTY_" );
	
	public static final ATermAppl		TOP_LIT				= ATermUtils
																	.makeTermAppl( Namespaces.RDFS
																			+ "Literal" );
	public static final ATermAppl		BOTTOM_LIT			= ATermUtils.makeNot( TOP_LIT );

	public static final ATermAppl		CONCEPT_SAT_IND		= ATermUtils.makeTermAppl( "_C_" );

	public static final ATermInt		ONE					= factory.makeInt( 1 );

	public static final ATermAppl		PLAIN_LITERAL_DATATYPE	= ATermUtils
																		.makeTermAppl( Namespaces.RDF
																				+ "PlainLiteral" );

	public static QNameProvider			qnames				= new QNameProvider();

	static public ATermFactory getFactory() {
		return factory;
	}

	final static public ATermAppl makeTypeAtom(ATermAppl ind, ATermAppl c) {
		return factory.makeAppl( TYPEFUN, ind, c );
	}

	final static public ATermAppl makePropAtom(ATermAppl p, ATermAppl s, ATermAppl o) {
		return factory.makeAppl( PROPFUN, p, s, o );
	}

	static public ATermAppl makePlainLiteral(String value) {
		return factory.makeAppl( ATermUtils.LITFUN, makeTermAppl( value ), EMPTY,
				PLAIN_LITERAL_DATATYPE );
	}

	static public ATermAppl makePlainLiteral(String value, String lang) {
		return factory.makeAppl( ATermUtils.LITFUN, makeTermAppl( value ), makeTermAppl( lang ),
				PLAIN_LITERAL_DATATYPE );
	}

	static public ATermAppl makeTypedLiteral(String value, ATermAppl dt) {
		return factory
				.makeAppl( ATermUtils.LITFUN, makeTermAppl( value ), EMPTY, dt );
	}

	static public ATermAppl makeTypedLiteral(String value, String dt) {
		return factory
				.makeAppl( ATermUtils.LITFUN, makeTermAppl( value ), EMPTY, makeTermAppl( dt ) );
	}

	static public ATermAppl	NO_DATATYPE	= makeTermAppl( "NO_DATATYPE" );

	static public ATermAppl makeLiteral(ATermAppl name) {
		return factory.makeAppl( ATermUtils.LITFUN, name, EMPTY, NO_DATATYPE );
	}

	static public String getLiteralValue(ATermAppl literal) {
		return ((ATermAppl) literal.getArgument( LIT_VAL_INDEX )).getName();
	}

	static public String getLiteralLang(ATermAppl literal) {
		return ((ATermAppl) literal.getArgument( LIT_LANG_INDEX )).getName();
	}

	static public String getLiteralDatatype(ATermAppl literal) {
		return ((ATermAppl) literal.getArgument( LIT_URI_INDEX )).getName();
	}

	static public ATermAppl makeTermAppl(String name) {
		return factory.makeAppl( factory.makeAFun( name, 0, false ) );
	}

	static public ATermAppl makeTermAppl(AFun fun, ATerm[] args) {
		return factory.makeAppl( fun, args );
	}

	static public ATermAppl makeNot(ATerm c) {
		return factory.makeAppl( NOTFUN, c );
	}

	static public ATerm term(String str) {
		return factory.parse( str );
	}

	// negate all the elements in the list and return the new list
	static public ATermList negate(ATermList list) {
		if( list.isEmpty() ) {
	        return list;
        }

		ATermAppl a = (ATermAppl) list.getFirst();
		a = isNot( a )
			? (ATermAppl) a.getArgument( 0 )
			: makeNot( a );
		ATermList result = makeList( a, negate( list.getNext() ) );

		return result;
	}

	final static public ATermAppl negate(ATermAppl a) {
		return isNot( a )
			? (ATermAppl) a.getArgument( 0 )
			: makeNot( a );
	}

	public static final AFun	BNODE_FUN			= factory.makeAFun( "bnode", 1, false );
	public static final AFun	ANON_FUN			= factory.makeAFun( "anon", 1, false );
	public static final AFun	ANON_NOMINAL_FUN	= factory.makeAFun( "anon_nominal", 1, false );

	private static final ATermAppl[] anonCache = new ATermAppl[1000];
	static {
		for( int i = 0; i < anonCache.length; i++ ) {
			anonCache[i] = factory.makeAppl( ANON_FUN, factory.makeInt( i ) );
		}
	}
	
	final static public boolean isAnonNominal(ATermAppl term) {
		return term.getAFun().equals( ANON_NOMINAL_FUN );
	}

	final static public ATermAppl makeAnonNominal(int id) {
		return factory.makeAppl( ANON_NOMINAL_FUN, factory.makeInt( id ) );
	}
	
	final static public ATermAppl makeAnon(int id) {
		if( id < anonCache.length ) {
	        return anonCache[id];
        }
		return factory.makeAppl( ANON_FUN, factory.makeInt( id ) );
	}

	final static public ATermAppl makeBnode(String id) {
		return factory.makeAppl( BNODE_FUN, makeTermAppl( id ) );
	}
	
	final static public ATermAppl makeVar(String name) {
		return factory.makeAppl( VARFUN, makeTermAppl( name ) );
	}

	final static public ATermAppl makeVar(ATermAppl name) {
		return factory.makeAppl( VARFUN, name );
	}

	final static public ATermAppl makeValue(ATerm c) {
		return factory.makeAppl( VALUEFUN, c );
	}

	final static public ATermAppl makeInv(ATermAppl r) {
		if( isInv( r ) ) {
	        return (ATermAppl) r.getArgument( 0 );
        }

		return factory.makeAppl( INVFUN, r );
	}

	final static public ATermAppl makeInvProp(ATerm r, ATerm s) {
		return factory.makeAppl( INVPROPFUN, r, s );
	}

	final static public ATermAppl makeSub(ATerm a, ATerm b) {
		return factory.makeAppl( SUBFUN, a, b );
	}

	final static public ATermAppl makeEqClasses(ATerm a, ATerm b) {
		return factory.makeAppl( EQCLASSFUN, a, b );
	}

	final static public ATermAppl makeSameAs(ATerm a, ATerm b) {
		return factory.makeAppl( SAMEASFUN, a, b );
	}

	final static public ATermAppl makeSubProp(ATerm r, ATerm s) {
		return factory.makeAppl( SUBPROPFUN, r, s );
	}

	final static public ATermAppl makeEqProp(ATerm r, ATerm s) {
		return factory.makeAppl( EQPROPFUN, r, s );
	}

	final static public ATermAppl makeDomain(ATerm r, ATerm c) {
		return factory.makeAppl( DOMAINFUN, r, c );
	}

	final static public ATermAppl makeRange(ATerm r, ATerm c) {
		return factory.makeAppl( RANGEFUN, r, c );
	}

	final static public ATermAppl makeComplement(ATerm a, ATerm b) {
		return factory.makeAppl( COMPLEMENTFUN, a, b );
	}

	final static public ATermAppl makeDisjoint(ATerm a, ATerm b) {
		return factory.makeAppl( DISJOINTFUN, a, b );
	}

	final static public ATermAppl makeDisjoints(ATermList list) {
		return factory.makeAppl( DISJOINTSFUN, list );
	}

	final static public ATermAppl makeDisjointProperty(ATerm a, ATerm b) {
		return factory.makeAppl( DISJOINTPROPFUN, a, b );
	}

	final static public ATermAppl makeDisjointProperties(ATermList list) {
		return factory.makeAppl( DISJOINTPROPSFUN, list );
	}
	
	final static public ATermAppl makeDifferent(ATerm a, ATerm b) {
		return factory.makeAppl( DIFFERENTFUN, a, b );
	}

	final static public ATermAppl makeAllDifferent(ATermList list) {
		return factory.makeAppl( ALLDIFFERENTFUN, list );
	}

	final static public ATermAppl makeAsymmetric(ATerm r) {
		return factory.makeAppl( ASYMMETRICFUN, r );
	}

    /**
     * @deprecated Use {@link #makeAsymmetric(ATerm)}
     */
	@Deprecated
    final static public ATermAppl makeAntisymmetric(ATerm r) {
		return makeAsymmetric( r );
	}

	final static public ATermAppl makeFunctional(ATerm a) {
		return factory.makeAppl( FUNCTIONALFUN, a );
	}

	final static public ATermAppl makeInverseFunctional(ATerm a) {
		return factory.makeAppl( INVFUNCTIONALFUN, a );
	}

	final static public ATermAppl makeIrreflexive(ATerm r) {
		return factory.makeAppl( IRREFLEXIVEFUN, r );
	}

	final static public ATermAppl makeReflexive(ATerm r) {
		return factory.makeAppl( REFLEXIVEFUN, r );
	}

	final static public ATermAppl makeSymmetric(ATerm r) {
		return factory.makeAppl( SYMMETRICFUN, r );
	}

	final static public ATermAppl makeTransitive(ATerm r) {
		return factory.makeAppl( TRANSITIVEFUN, r );
	}

	final static public ATermAppl makeAnd(ATerm c1, ATerm c2) {
		return makeAnd( makeList( c2 ).insert( c1 ) );
	}

	static public ATermAppl makeAnd(ATermList list) {
		if( list == null ) {
	        throw new NullPointerException();
        }
        else if( list.isEmpty() ) {
	        return TOP;
        }
        else if( list.getNext().isEmpty() ) {
	        return (ATermAppl) list.getFirst();
        }

		return factory.makeAppl( ANDFUN, list );
	}

	final static public ATermAppl makeOr(ATermAppl c1, ATermAppl c2) {
		return makeOr( makeList( c2 ).insert( c1 ) );
	}

	static public ATermAppl makeOr(ATermList list) {
		if( list == null ) {
	        throw new NullPointerException();
        }
        else if( list.isEmpty() ) {
	        return BOTTOM;
        }
        else if( list.getNext().isEmpty() ) {
	        return (ATermAppl) list.getFirst();
        }

		return factory.makeAppl( ORFUN, list );
	}

	final static public ATermAppl makeAllValues(ATerm r, ATerm c) {
		if( r.getType() == ATerm.LIST ) {
			ATermList list = (ATermList) r;
			if( list.getLength() == 1 ) {
	            r = list.getFirst();
            }
		}
		return factory.makeAppl( ALLFUN, r, c );
	}

	final static public ATermAppl makeSomeValues(ATerm r, ATerm c) {
		assertTrue( c instanceof ATermAppl );

		return factory.makeAppl( SOMEFUN, r, c );
	}

	final static public ATermAppl makeSelf(ATermAppl r) {
		return factory.makeAppl( SELFFUN, r );
	}

	final static public ATermAppl makeHasValue(ATerm r, ATerm ind) {
		ATermAppl c = makeValue( ind );
		return factory.makeAppl( SOMEFUN, r, c );
	}

	final static public ATermAppl makeNormalizedMax(ATermAppl r, int n, ATermAppl c) {
		assertTrue( n >= 0 );

		return makeNot( makeMin( r, n + 1, c ) );
	}

	final static public ATermAppl makeMax(ATerm r, int n, ATerm c) {
		// assertTrue( n >= 0 );

		// This was causing nnf to come out wrong
		// return makeNot(makeMin(r, n + 1));

		return makeMax( r, factory.makeInt( n ), c );
	}

	final static public ATermAppl makeMax(ATerm r, ATermInt n, ATerm c) {
		assertTrue( n.getInt() >= 0 );

		return factory.makeAppl( MAXFUN, r, n, c );
	}

	final static public ATermAppl makeMin(ATerm r, int n, ATerm c) {
		// comment out built-in simplification so that explanation
		// axioms will come out right
		// if( n == 0 )
		// return ATermUtils.TOP;

		return makeMin( r, factory.makeInt( n ), c );
	}

	final static public ATermAppl makeMin(ATerm r, ATermInt n, ATerm c) {
		assertTrue( n.getInt() >= 0 );

		return factory.makeAppl( MINFUN, r, n, c );
	}

	final static public ATermAppl makeDisplayCard(ATerm r, int n, ATerm c) {
		assertTrue( n >= 0 );

		return factory.makeAppl( CARDFUN, r, factory.makeInt( n ), c );
	}

	final static public ATermAppl makeDisplayMax(ATerm r, int n, ATerm c) {
		assertTrue( n >= 0 );

		return factory.makeAppl( MAXFUN, r, factory.makeInt( n ), c );
	}

	final static public ATermAppl makeDisplayMin(ATerm r, int n, ATerm c) {
		assertTrue( n >= 0 );

		return factory.makeAppl( MINFUN, r, factory.makeInt( n ), c );
	}

	final static public ATermAppl makeCard(ATerm r, int n, ATerm c) {
		return makeDisplayCard( r, n, c );
		// ATermAppl max = makeMax( r, n, c );
		// if( n == 0 )
		// return max;
		//
		// ATermAppl min = makeMin( r, n, c );
		// return makeAnd( min, max );
	}

	final static public ATermAppl makeExactCard(ATerm r, int n, ATerm c) {
		return makeExactCard( r, factory.makeInt( n ), c );
	}

	final static public ATermAppl makeExactCard(ATerm r, ATermInt n, ATerm c) {
		ATermAppl max = makeMax( r, n, c );

		if( n.getInt() == 0 ) {
	        return max;
        }

		ATermAppl min = makeMin( r, n, c );
		return makeAnd( min, max );
	}

	final static public ATermAppl makeFacetRestriction(ATermAppl facetName, ATermAppl facetValue) {
		return factory.makeAppl( FACET, facetName, facetValue );
	}
	
	final static public ATermAppl makeRestrictedDatatype(ATermAppl baseDatatype, ATermAppl[] restrictions) {
		return factory.makeAppl( RESTRDATATYPEFUN, baseDatatype, makeList( restrictions ) );
	}
	
	final static public ATermAppl makeDatatypeDefinition(ATermAppl datatype, ATermAppl definition) {
		return factory.makeAppl( DATATYPEDEFFUN, datatype, definition );
	}
		
	final static public boolean isRestrictedDatatype(ATermAppl term) {
		return term.getAFun().equals( RESTRDATATYPEFUN );
	}
	
	final static public ATermList makeList(ATerm singleton) {
		return factory.makeList( singleton, EMPTY_LIST );
	}

	final static public ATermList makeList(ATerm first, ATermList rest) {
		return factory.makeList( first, rest );
	}

	public static ATermList makeList(Collection<ATermAppl> set) {
		ATermList list = EMPTY_LIST;

		for( ATerm term : set ) {
			list = list.insert( term );
		}
		return list;
	}

	final static public ATermList makeList(ATerm[] aTerms) {
		return makeList( aTerms, 0 );
	}

	static private ATermList makeList(ATerm[] aTerms, int index) {
		if( index >= aTerms.length ) {
	        return EMPTY_LIST;
        }
        else if( index == aTerms.length - 1 ) {
	        return makeList( aTerms[index] );
        }

		return makeList( aTerms[index], makeList( aTerms, index + 1 ) );
	}

	final static public boolean member(ATerm a, ATermList list) {
		return (list.indexOf( a, 0 ) != -1);
	}

	static public boolean isSet(ATermList list) {
		if( list.isEmpty() ) {
	        return true;
        }

		ATerm curr = list.getFirst();
		list = list.getNext();
		while( !list.isEmpty() ) {
			ATerm next = list.getFirst();
			if( Comparators.termComparator.compare( curr, next ) >= 0 ) {
	            return false;
            }
			curr = next;
			list = list.getNext();
		}

		return true;
	}

	static public ATermList toSet(ATermList list) {
		if( isSet( list ) ) {
	        return list;
        }

		int size = list.getLength();

		ATerm[] a = toArray( list );
		if( a == null || a.length < size ) {
	        a = new ATerm[Math.max( 100, size )];
        }

		Arrays.sort( a, 0, size, Comparators.termComparator );

		ATermList set = makeList( a[size - 1] );
		for( int i = size - 2; i >= 0; i-- ) {
			ATerm s = set.getFirst();
			if( !s.equals( a[i] ) ) {
	            set = set.insert( a[i] );
            }
		}

		return set;
	}

	static public ATermList toSet(ATerm[] a, int size) {
		Arrays.sort( a, 0, size, Comparators.termComparator );

		ATermList set = makeList( a[size - 1] );
		for( int i = size - 2; i >= 0; i-- ) {
			ATerm s = set.getFirst();
			if( !s.equals( a[i] ) ) {
	            set = set.insert( a[i] );
            }
		}

		return set;
	}

	static public ATermList toSet(Collection<ATermAppl> terms) {
		int size = terms.size();

		ATermAppl[] a = new ATermAppl[size];
		terms.toArray( a );

		return toSet( a, size );
	}
	
	/**
	 * Return the string representations of the terms in a collection. For each element of the collection
	 * {@link #toString(ATermAppl)} function will be called to create the string representation.
	 * 
	 * @param terms
	 *            a collection of terms
	 * @return string representation of the terms
	 */
	public static String toString(Collection<ATermAppl> terms) {
		if (terms.isEmpty()) {
	        return "[]";
        }
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		Iterator<ATermAppl> i = terms.iterator();
		sb.append(ATermUtils.toString(i.next()));
		while (i.hasNext()) {
			sb.append(", ");
			sb.append(ATermUtils.toString(i.next()));
		}

		sb.append("]");

		return sb.toString();
	}

	/**
	 * Return a string representation of the term which might be representing a named
	 * term, literal, variable or a complex concept expression. The URIs used in the
	 * term will be shortened into local names. The concept expressions are printed
	 * in NNF format.
	 * 
	 * @param term term whose string representation we are creating
	 * @return string representation of the term
	 */
	public static String toString(ATermAppl term) {
		return toString( term, true , true);
	}
	
	/**
	 * Return a string representation of the term which might be representing a named
	 * term, literal, variable or a complex concept expression. The URIs used in the
	 * term can be shortened into local names. The concept expressions can be printed
	 * in NNF format.
	 * 
	 * @param term term whose string representation we are creating
	 * @return string representation of the term
	 */
	public static String toString(ATermAppl term, boolean printLocalName, boolean printNNF) {
		if (term == null) {
			return "<null>";
		}
		else {
			StringBuilder sb = new StringBuilder();
			toString( term, sb, printNNF ? Bool.FALSE : Bool.UNKNOWN, printLocalName );
			return sb.toString();
		}
	}
	
	/**
	 * Helper for toString function.
	 * 
	 * @param term term 
	 * @param sb the builder we are 
	 * @param negated 
	 * @param useLocalName
	 */
	private static void toString(ATermAppl term, StringBuilder sb, Bool negated, boolean printLocalName) {
		if( term.equals( ATermUtils.TOP ) ) {
	        sb.append(negated.isTrue() ? "owl:Nothing" : "owl:Thing");
        }
        else if( term.equals( ATermUtils.BOTTOM ) ) {
        	sb.append(negated.isTrue() ? "owl:Thing" : "owl:Nothing");
        }
        else if( ATermUtils.isVar( term ) ) {
			String name = ((ATermAppl) term.getArgument( 0 )).getName();
			if( printLocalName ) {
	            name = URIUtils.getLocalName( name );
            }
			sb.append( "?" ).append( name );
		}
		else if( ATermUtils.isLiteral( term ) ) {
			String value = ((ATermAppl) term.getArgument( 0 )).toString();
			String lang = ((ATermAppl) term.getArgument( 1 )).getName();
			ATermAppl datatypeURI = (ATermAppl) term.getArgument( 2 );

			sb.append( '"' ).append( value ).append( '"' );
			if( !lang.equals( "" ) ) {
				sb.append( '@' ).append( lang );
			}
			else if( !datatypeURI.equals( NO_DATATYPE ) &&
					 !datatypeURI.equals( PLAIN_LITERAL_DATATYPE ) ) {
				sb.append( "^^" );
				toString( datatypeURI, sb, Bool.FALSE, printLocalName );				
			}
		}
		else if( ATermUtils.isPrimitive( term ) ) {
			if( negated.isTrue() ) {
				sb.append( "not(" );
			}
			String name = term.getName();
			sb.append( URIUtils.getLocalName( name ) );
			if( negated.isTrue() ) {
				sb.append( ")" );
			}
		}
		else if( ATermUtils.isRestrictedDatatype( term ) ) {
			if( negated.isTrue() ) {
				sb.append( "not(" );
			}
	        toString((ATermAppl) term.getArgument(0), sb, Bool.FALSE, printLocalName );
	        sb.append("[");
	        ATermList list = (ATermList) term.getArgument( 1 );
			while (!list.isEmpty()) {
				ATermAppl facet = (ATermAppl) list.getFirst();
				sb.append( ATermManchesterSyntaxRenderer.FACETS.get( facet.getArgument( 0 ) ));
				sb.append(" ");
				toString((ATermAppl) facet.getArgument( 1 ), sb, Bool.FALSE, printLocalName );
				list = list.getNext();
				if(!list.isEmpty()) {
	                sb.append(", ");
                }
			}
			sb.append("]");
			if( negated.isTrue() ) {
				sb.append( ")" );
			}
		}
		else if( negated.isKnown() && ATermUtils.isNot( term ) ) {
			toString( (ATermAppl) term.getArgument( 0 ), sb, negated.not(), printLocalName );
		}
		else {		
			AFun fun = term.getAFun();
			if( negated.isTrue() ) {
				if( fun.equals( ATermUtils.ANDFUN ) ) {
	                sb.append( ATermUtils.ORFUN.getName() );
                }
                else if( fun.equals( ATermUtils.ORFUN ) ) {
	                sb.append( ATermUtils.ANDFUN.getName() );
                }
                else if( fun.equals( ATermUtils.SOMEFUN ) ) {
	                sb.append( ATermUtils.ALLFUN.getName() );
                }
                else if( fun.equals( ATermUtils.ALLFUN ) ) {
	                sb.append( ATermUtils.SOMEFUN.getName() );
                }
                else if( fun.equals( ATermUtils.MINFUN ) ) {
	                sb.append( ATermUtils.MAXFUN.getName() );
                }
                else if( fun.equals( ATermUtils.MAXFUN ) ) {
	                sb.append( ATermUtils.MINFUN.getName() );
                }
                else if( !fun.equals( ATermUtils.NOTFUN ) ) {
					if( fun.equals( ATermUtils.VALUEFUN ) 
						|| fun.equals( ATermUtils.RESTRDATATYPEFUN )) {
	                    sb.append( "not(" );
                    }
					sb.append( fun.getName() );
				}
			}
			else {
				sb.append( fun.getName() );
			}
			
			Bool negatedRecurse = negated;
			if( negated.isKnown() && fun.equals( ATermUtils.MINFUN ) || fun.equals( ATermUtils.MAXFUN ) ) {
				negatedRecurse = Bool.FALSE;
			}
			else if( fun.equals( ATermUtils.NOTFUN ) ) {
				negatedRecurse = negated.not();
			}

			sb.append( "(" );
			for( int i = 0, n = term.getArity(); i < n; i++ ) {
				if( i > 0 ) {
					sb.append ( ", " );				
				}
				ATerm arg = term.getArgument( i );
				if( arg instanceof ATermAppl ) {
					toString( (ATermAppl) arg, sb, i > 0 ? negatedRecurse : Bool.FALSE, printLocalName );					
				}
				else if( arg instanceof ATermList ) {
					sb.append( "[" );
					ATermList list = (ATermList) arg;
					while( !list.isEmpty() ) {
						toString( (ATermAppl) list.getFirst(), sb, negatedRecurse, printLocalName );
						list = list.getNext();
						if( !list.isEmpty() ) {
	                        sb.append ( ", " );
                        }
					}
					sb.append( "]" );
				}
				else {
					int value = ((ATermInt) arg).getInt();
					if( negated.isTrue() ) {
						if( fun.equals( ATermUtils.MINFUN ) ) {
	                        value--;
                        }
                        else if( fun.equals( ATermUtils.MAXFUN ) ) {
	                        value++;
                        }
					}
					sb.append( value );
				}
			}
			sb.append( ")" );
			if( (fun.equals( ATermUtils.VALUEFUN ) 
				|| fun.equals( ATermUtils.RESTRDATATYPEFUN )) && negated.isTrue() ) {
	            sb.append( ")" );
            }
		}
	}

	static public ATermAppl[] toArray(ATermList list) {
		ATermAppl[] a = new ATermAppl[list.getLength()];

		for( int i = 0; !list.isEmpty(); list = list.getNext() ) {
	        a[i++] = (ATermAppl) list.getFirst();
        }

		return a;
	}

	public final static void assertTrue(boolean condition) {
		if( !condition ) {
			throw new RuntimeException( "assertion failed." );
		}
	}

	public static final boolean isPrimitive(ATermAppl c) {
		return c.getArity() == 0;
	}

	public static final boolean isNegatedPrimitive(ATermAppl c) {
		return isNot( c ) && isPrimitive( (ATermAppl) c.getArgument( 0 ) );
	}

	public static final boolean isPrimitiveOrNegated(ATermAppl c) {
		return isPrimitive( c ) || isNegatedPrimitive( c );
	}

	public static final boolean isBnode(ATermAppl name) {
		return name.getAFun().equals( BNODE_FUN );
	}

	public static final boolean isAnon(ATermAppl term) {
		return term.getAFun().equals( ANON_FUN );
	}
	
	public static final boolean isBuiltinProperty(ATermAppl name) {
		if ( TOP_OBJECT_PROPERTY.equals( name ) ||
				BOTTOM_OBJECT_PROPERTY.equals( name ) ||
				makeInv( TOP_OBJECT_PROPERTY).equals( name ) ||
				makeInv( BOTTOM_OBJECT_PROPERTY ).equals( name ) ||
				TOP_DATA_PROPERTY.equals( name ) ||
				BOTTOM_DATA_PROPERTY.equals( name ) ) {
	        return true;
        }
		return false;
	}

	public static Set<ATermAppl> listToSet(ATermList list) {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		while( !list.isEmpty() ) {
			set.add( (ATermAppl) list.getFirst() );
			list = list.getNext();
		}
		return set;
	}

	public static Set<ATermAppl> getPrimitives(ATermList list) {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		while( !list.isEmpty() ) {
			ATermAppl term = (ATermAppl) list.getFirst();
			if( isPrimitive( term ) ) {
	            set.add( term );
            }
			list = list.getNext();
		}
		return set;
	}

	public final static ATermAppl getTop(Role r) {
		return r.isDatatypeRole()
			? TOP_LIT
			: TOP;
	}
	
	public final static boolean isTop(ATermAppl a) {
		return a.equals( TOP ) || a.equals( TOP_LIT );
	}

	public final static boolean isBottom(ATermAppl a) {
		return a.equals( BOTTOM ) || a.equals( BOTTOM_LIT );
	}

	final static public boolean isInv(ATermAppl r) {
		return r.getAFun().equals( INVFUN );
	}

	public final static boolean isAnd(ATermAppl a) {
		return a.getAFun().equals( ANDFUN );
	}

	public final static boolean isOr(ATermAppl a) {
		return a.getAFun().equals( ORFUN );
	}

	public final static boolean isAllValues(ATermAppl a) {
		return a.getAFun().equals( ALLFUN );
	}

	public final static boolean isSomeValues(ATermAppl a) {
		return a.getAFun().equals( SOMEFUN );
	}

	public final static boolean isSelf(ATermAppl a) {
		return a.getAFun().equals( SELFFUN );
	}

	public final static boolean isHasValue(ATermAppl a) {
		return a.getAFun().equals( SOMEFUN )
				&& ((ATermAppl) a.getArgument( 1 )).getAFun().equals( VALUEFUN );
	}

	public final static boolean isNominal(ATermAppl a) {
		return a.getAFun().equals( VALUEFUN );
	}

	public final static boolean isOneOf(ATermAppl a) {
		if( !a.getAFun().equals( ORFUN ) ) {
	        return false;
        }

		ATermList list = (ATermList) a.getArgument( 0 );
		while( !list.isEmpty() ) {
			if( !isNominal( (ATermAppl) list.getFirst() ) ) {
	            return false;
            }
			list = list.getNext();
		}
		return true;
	}

	public final static boolean isDataRange(ATermAppl a) {
		if( !a.getAFun().equals( ORFUN ) ) {
	        return false;
        }

		ATermList list = (ATermList) a.getArgument( 0 );
		while( !list.isEmpty() ) {
			ATermAppl term = (ATermAppl) list.getFirst();
			if( !isNominal( term ) || !isLiteral( (ATermAppl) term.getArgument( 0 ) ) ) {
	            return false;
            }
			list = list.getNext();
		}
		return true;
	}

	public final static boolean isNot(ATermAppl a) {
		return a.getAFun().equals( NOTFUN );
	}

	public final static boolean isMax(ATermAppl a) {
		return a.getAFun().equals( MAXFUN );
	}

	public final static boolean isMin(ATermAppl a) {
		return a.getAFun().equals( MINFUN );
	}

	public final static boolean isCard(ATermAppl a) {
		if( isMin( a ) || isMax( a ) ) {
	        return true;
        }
        else if( isAnd( a ) ) {
			a = (ATermAppl) a.getArgument( 0 );
			return isMin( a ) || isMax( a );
		}

		return false;
	}

	public final static boolean isLiteral(ATermAppl a) {
		return a.getAFun().equals( LITFUN );
	}

	final static public boolean isVar(ATermAppl a) {
		return a.getAFun().equals( VARFUN );
	}

	final static public boolean isTransitiveChain(ATermList chain, ATerm r) {
		return chain.getLength() == 2 && chain.getFirst().equals( r ) && chain.getLast().equals( r );
	}

	public static boolean isComplexClass(ATerm c) {
		if( c instanceof ATermAppl ) {
			ATermAppl a = (ATermAppl) c;
			AFun f = a.getAFun();
			return CLASS_FUN.contains( f );
		}
		return false;
	}

	public final static boolean isPropertyAssertion(ATermAppl a) {
		return a.getAFun().equals( PROPFUN );
	}

	public final static boolean isTypeAssertion(ATermAppl a) {
		return a.getAFun().equals( TYPEFUN );
	}

	public static ATerm nnf(ATerm term) {
		if( term instanceof ATermList ) {
	        return nnf( (ATermList) term );
        }
		if( term instanceof ATermAppl ) {
	        return nnf( (ATermAppl) term );
        }

		return null;
	}

	public static ATermList nnf(ATermList list) {
		ATermList newList = factory.makeList();
		while( !list.isEmpty() ) {
			newList = newList.append( nnf( (ATermAppl) list.getFirst() ) );
			list = list.getNext();
		}

		return newList;
	}

	/*
	 * return the term in NNF form, i.e. negation only occurs in front of atomic
	 * concepts
	 */
	public static ATermAppl nnf(ATermAppl term) {
		ATermAppl newterm = null;

		AFun af = term.getAFun();

		if( af.equals( ATermUtils.NOTFUN ) ) { // Function is a NOT
			// Take the first argument to the NOT, then check
			// the type of that argument to determine what needs to be done.
			ATermUtils.assertTrue( af.getArity() == 1 );
			ATermAppl arg = (ATermAppl) term.getArgument( 0 );
			af = arg.getAFun();

			if( arg.getArity() == 0 ) {
				newterm = term; // Negation is in as far as it can go
			}
			else if( af.equals( ATermUtils.NOTFUN ) ) { // Double negation.
				newterm = nnf( (ATermAppl) arg.getArgument( 0 ) );
			}
			else if( af.equals( ATermUtils.VALUEFUN ) || af.equals( ATermUtils.SELFFUN )  || af.equals( ATermUtils.RESTRDATATYPEFUN ) ) {
				newterm = term;
			}
			else if( af.equals( ATermUtils.MAXFUN ) ) {
				ATermInt n = (ATermInt) arg.getArgument( 1 );
				newterm = ATermUtils.makeMin( arg.getArgument( 0 ), n.getInt() + 1, nnf( arg
						.getArgument( 2 ) ) );
			}
			else if( af.equals( ATermUtils.MINFUN ) ) {
				ATermInt n = (ATermInt) arg.getArgument( 1 );
				if( n.getInt() == 0 ) {
	                newterm = ATermUtils.BOTTOM;
                }
                else {
	                newterm = ATermUtils.makeMax( arg.getArgument( 0 ), n.getInt() - 1, nnf( arg
							.getArgument( 2 ) ) );
                }
			}
			else if( af.equals( ATermUtils.CARDFUN ) ) {
				newterm = nnf( makeNot( makeExactCard( arg.getArgument( 0 ), ((ATermInt) arg
						.getArgument( 1 )), arg.getArgument( 2 ) ) ) );
			}
			else if( af.equals( ATermUtils.ANDFUN ) ) {
				newterm = ATermUtils.makeOr( nnf( negate( (ATermList) arg.getArgument( 0 ) ) ) );
			}
			else if( af.equals( ATermUtils.ORFUN ) ) {
				newterm = ATermUtils.makeAnd( nnf( negate( (ATermList) arg.getArgument( 0 ) ) ) );
			}
			else if( af.equals( ATermUtils.SOMEFUN ) ) {
				ATerm p = arg.getArgument( 0 );
				ATerm c = arg.getArgument( 1 );
				newterm = ATermUtils.makeAllValues( p, nnf( makeNot( c ) ) );
			}
			else if( af.equals( ATermUtils.ALLFUN ) ) {
				ATerm p = arg.getArgument( 0 );
				ATerm c = arg.getArgument( 1 );
				newterm = ATermUtils.makeSomeValues( p, nnf( makeNot( c ) ) );
			}
			else {
				throw new InternalReasonerException( "Unknown term type: " + term );
			}
		}
		else if( af.equals( ATermUtils.MINFUN ) || af.equals( ATermUtils.MAXFUN )
				|| af.equals( ATermUtils.SELFFUN ) ) {
			newterm = term;
		}
		else if( af.equals( ATermUtils.CARDFUN ) ) {
			newterm = nnf( makeExactCard( term.getArgument( 0 ),
					((ATermInt) term.getArgument( 1 )), term.getArgument( 2 ) ) );
		}
		else {
			// Return the term with all of its arguments in nnf
			ATerm args[] = new ATerm[term.getArity()];
			for( int i = 0; i < term.getArity(); i++ ) {
				args[i] = nnf( term.getArgument( i ) );
			}
			newterm = factory.makeAppl( af, args );
		}

		ATermUtils.assertTrue( newterm != null );

		return newterm;
	}

	public static Collection<ATermAppl> normalize(Collection<ATermAppl> terms) {
		List<ATermAppl> list = new ArrayList<ATermAppl>();
		for( ATermAppl term : terms ) {
			list.add( normalize( term ) );
		}

		return list;
	}

	public static ATermList normalize(ATermList list) {
		int size = list.getLength();
		ATerm[] terms = new ATerm[size];
		for( int i = 0; i < size; i++ ) {
			terms[i] = normalize( (ATermAppl) list.getFirst() );
			list = list.getNext();
		}

		ATermList set = toSet( terms, size );

		return set;
	}

	/**
	 * Normalize the term by making following changes:
	 * <ul>
	 * <li>or([a1, a2,..., an]) -> not(and[not(a1), not(a2), ..., not(an)]])</li>
	 * <li>some(p, c) -> all(p, not(c))</li>
	 * <li>max(p, n) -> not(min(p, n+1))</li>
	 * </ul>
	 * 
	 * @param term
	 * @return
	 */
	public static ATermAppl normalize(ATermAppl term) {
		ATermAppl norm = term;
		AFun fun = term.getAFun();
		ATerm arg1 = (term.getArity() > 0)
			? term.getArgument( 0 )
			: null;
		ATerm arg2 = (term.getArity() > 1)
			? term.getArgument( 1 )
			: null;
		ATerm arg3 = (term.getArity() > 2)
			? term.getArgument( 2 )
			: null;

		if( arg1 == null || fun.equals( SELFFUN ) || fun.equals( VALUEFUN ) || fun.equals( INVFUN ) 
			|| fun.equals( RESTRDATATYPEFUN ) ) {
			// do nothing because these terms cannot be decomposed any further
		}
		else if( fun.equals( NOTFUN ) ) {
			if( !isPrimitive( (ATermAppl) arg1 ) ) {
	            norm = simplify( makeNot( normalize( (ATermAppl) arg1 ) ) );
            }
		}
		else if( fun.equals( ANDFUN ) ) {
			norm = simplify( makeAnd( normalize( (ATermList) arg1 ) ) );
		}
		else if( fun.equals( ORFUN ) ) {
			ATermList neg = negate( (ATermList) arg1 );
			ATermAppl and = makeAnd( neg );
			ATermAppl notAnd = makeNot( and );
			norm = normalize( notAnd );
		}
		else if( fun.equals( ALLFUN ) ) {
			norm = simplify( makeAllValues( arg1, normalize( (ATermAppl) arg2 ) ) );
		}
		else if( fun.equals( SOMEFUN ) ) {
			norm = normalize( makeNot( makeAllValues( arg1, makeNot( arg2 ) ) ) );
		}
		else if( fun.equals( MAXFUN ) ) {
			norm = normalize( makeNot( makeMin( arg1, ((ATermInt) arg2).getInt() + 1, arg3 ) ) );
		}
		else if( fun.equals( MINFUN ) ) {
			norm = simplify( makeMin( arg1, (ATermInt) arg2, normalize( (ATermAppl) arg3 ) ) );
		}
		else if( fun.equals( CARDFUN ) ) {
			ATermAppl normMin = simplify( makeMin( arg1, ((ATermInt) arg2).getInt(),
					normalize( (ATermAppl) arg3 ) ) );
			ATermAppl normMax = normalize( makeMax( arg1, ((ATermInt) arg2).getInt(), arg3 ) );
			norm = simplify( makeAnd( normMin, normMax ) );
		}
        else {
	        throw new InternalReasonerException( "Unknown concept type: " + term );
        }

		return norm;
	}

	/**
	 * Simplify the term by making following changes:
	 * <ul>
	 * <li>and([]) -> TOP</li>
	 * <li>all(p, TOP) -> TOP</li>
	 * <li>min(p, 0) -> TOP</li>
	 * <li>and([a1, and([a2,...,an])]) -> and([a1, a2, ..., an]))</li>
	 * <li>and([a, not(a), ...]) -> BOTTOM</li>
	 * <li>not(C) -> not(simplify(C))</li>
	 * </ul>
	 * 
	 * @param term
	 * @return
	 */
	public static ATermAppl simplify(ATermAppl term) {
		ATermAppl simp = term;
		AFun fun = term.getAFun();
		ATerm arg1 = (term.getArity() > 0)
			? term.getArgument( 0 )
			: null;
		ATerm arg2 = (term.getArity() > 1)
			? term.getArgument( 1 )
			: null;
		ATerm arg3 = (term.getArity() > 2)
			? term.getArgument( 2 )
			: null;

		if( arg1 == null || fun.equals( SELFFUN ) || fun.equals( VALUEFUN ) || fun.equals( ATermUtils.RESTRDATATYPEFUN ) ) {
			// do nothing because term is primitive or self restriction
		}
		else if( fun.equals( NOTFUN ) ) {
			ATermAppl arg = (ATermAppl) arg1;
			if( isNot( arg ) ) {
	            simp = simplify( (ATermAppl) arg.getArgument( 0 ) );
            }
            else if( isMin( arg ) ) {
				ATermInt n = (ATermInt) arg.getArgument( 1 );
				if( n.getInt() == 0 ) {
	                simp = BOTTOM;
                }
			}
		}
		else if( fun.equals( ANDFUN ) ) {
			ATermList conjuncts = (ATermList) arg1;
			if( conjuncts.isEmpty() ) {
	            simp = TOP;
            }
            else {
				Set<ATermAppl> set = new HashSet<ATermAppl>();
				List<ATermAppl> negations = new ArrayList<ATermAppl>();
				MultiListIterator i = new MultiListIterator( conjuncts );
				while( i.hasNext() ) {
					ATermAppl c = i.next();
					if( c.equals( TOP ) ) {
	                    continue;
                    }
                    else if( c.equals( BOTTOM ) ) {
	                    return BOTTOM;
                    }
                    else if( isAnd( c ) ) {
	                    i.append( (ATermList) c.getArgument( 0 ) );
                    }
                    else if( isNot( c ) ) {
	                    negations.add( c );
                    }
                    else {
	                    set.add( c );
                    }
				}

				for( ATermAppl notC : negations ) {
					ATermAppl c = (ATermAppl) notC.getArgument( 0 );
					if( set.contains( c ) ) {
	                    return BOTTOM;
                    }
				}

				if( set.isEmpty() ) {
					if( negations.isEmpty() ) {
	                    return TOP;
                    }
                    else if( negations.size() == 1 ) {
	                    return negations.get( 0 );
                    }
				}
				else if( set.size() == 1 && negations.isEmpty() ) {
	                return set.iterator().next();
                }

				negations.addAll( set );
				int size = negations.size();
				ATermAppl[] terms = new ATermAppl[size];
				negations.toArray( terms );
				simp = makeAnd( toSet( terms, size ) );
			}
		}
		else if( fun.equals( ALLFUN ) ) {
			if( arg2.equals( TOP ) ) {
	            simp = TOP;
            }
		}
		else if( fun.equals( MINFUN ) ) {
			ATermInt n = (ATermInt) arg2;
			if( n.getInt() == 0 ) {
	            simp = TOP;
            }
			if( arg3.equals( ATermUtils.BOTTOM ) ) {
	            simp = BOTTOM;
            }
		}
		else if( fun.equals( MAXFUN ) ) {
			ATermInt n = (ATermInt) arg2;
			if( n.getInt() > 0 && arg3.equals( ATermUtils.BOTTOM ) ) {
	            simp = TOP;
            }
		}
        else {
	        throw new InternalReasonerException( "Unknown term type: " + term );
        }

		return simp;
	}

	/**
	 * Creates a simplified and assuming that all the elements have already been
	 * normalized.
	 * 
	 * @param conjuncts
	 * @return
	 */
	public static ATermAppl makeSimplifiedAnd(Collection<ATermAppl> conjuncts) {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		List<ATermAppl> negations = new ArrayList<ATermAppl>();
		MultiListIterator listIt = new MultiListIterator( EMPTY_LIST );
		Iterator<ATermAppl> i = new PairIterator<ATermAppl>( conjuncts.iterator(), listIt );
		while( i.hasNext() ) {
			ATermAppl c = i.next();
			if( c.equals( TOP ) ) {
	            continue;
            }
            else if( c.equals( BOTTOM ) ) {
	            return BOTTOM;
            }
            else if( isAnd( c ) ) {
	            listIt.append( (ATermList) c.getArgument( 0 ) );
            }
            else if( isNot( c ) ) {
	            negations.add( c );
            }
            else {
	            set.add( c );
            }
		}

		for( ATermAppl notC : negations ) {
			ATermAppl c = (ATermAppl) notC.getArgument( 0 );
			if( set.contains( c ) ) {
	            return BOTTOM;
            }
		}

		if( set.isEmpty() ) {
			if( negations.isEmpty() ) {
	            return TOP;
            }
            else if( negations.size() == 1 ) {
	            return negations.get( 0 );
            }
		}
		else if( set.size() == 1 && negations.isEmpty() ) {
	        return set.iterator().next();
        }

		negations.addAll( set );
		int size = negations.size();
		ATermAppl[] terms = new ATermAppl[size];
		negations.toArray( terms );
		return makeAnd( toSet( terms, size ) );
	}

	public static Set<ATermAppl> findPrimitives(ATermAppl term) {
		Set<ATermAppl> primitives = new HashSet<ATermAppl>();

		findPrimitives( term, primitives, false, false );
		return primitives;
	}

	public static Set<ATermAppl> findPrimitives(ATermAppl term, boolean skipRestrictions,
			boolean skipTopLevel) {
		Set<ATermAppl> primitives = new HashSet<ATermAppl>();

		findPrimitives( term, primitives, skipRestrictions, skipTopLevel );

		return primitives;
	}
	
	public static void findPrimitives(ATermAppl term, Set<ATermAppl> primitives) {
		findPrimitives( term, primitives, false, false );
	}

	public static void findPrimitives(ATermAppl term, Set<ATermAppl> primitives,
			boolean skipRestrictions, boolean skipTopLevel) {
		AFun fun = term.getAFun();

		if( isPrimitive( term ) ) {
			primitives.add( term );
		}
		else if( fun.equals( SELFFUN ) || fun.equals( VALUEFUN ) || fun.equals( RESTRDATATYPEFUN ) ) {
			// do nothing because there is no atomic concept here
		}
		else if( fun.equals( NOTFUN ) ) {
			ATermAppl arg = (ATermAppl) term.getArgument( 0 );
			if( !isPrimitive( arg ) || !skipTopLevel ) {
	            findPrimitives( arg, primitives, skipRestrictions, false );
            }
		}
		else if( fun.equals( ANDFUN ) || fun.equals( ORFUN ) ) {
			ATermList list = (ATermList) term.getArgument( 0 );
			while( !list.isEmpty() ) {
				ATermAppl arg = (ATermAppl) list.getFirst();
				if( !isNegatedPrimitive( arg ) || !skipTopLevel ) {
	                findPrimitives( arg, primitives, skipRestrictions, false );
                }
				list = list.getNext();
			}
		}
		else if( !skipRestrictions ) {
			if( fun.equals( ALLFUN ) || fun.equals( SOMEFUN ) ) {
				ATermAppl arg = (ATermAppl) term.getArgument( 1 );
				findPrimitives( arg, primitives, skipRestrictions, false );
			}
			else if( fun.equals( MAXFUN ) || fun.equals( MINFUN ) || fun.equals( CARDFUN ) ) {
				ATermAppl arg = (ATermAppl) term.getArgument( 2 );
				findPrimitives( arg, primitives, skipRestrictions, false );
			}
            else {
	            throw new InternalReasonerException( "Unknown concept type: " + term );
            }
		}
	}
	
	public static Collection<ATermAppl> primitiveOrBottom(Collection<ATermAppl> collection) {
		List<ATermAppl> ret = new ArrayList<ATermAppl>();
		for( Iterator<ATermAppl> i = collection.iterator(); i.hasNext(); ) {
			ATermAppl a = i.next();
			if( isPrimitive( a ) || a == ATermUtils.BOTTOM ) {
	            ret.add( a );
            }
		}
		return ret;
	}

	public static Set<ATermAppl> primitiveOrBottom(Set<ATermAppl> collection) {
		Set<ATermAppl> ret = new HashSet<ATermAppl>();
		for( Iterator<ATermAppl> i = collection.iterator(); i.hasNext(); ) {
			ATermAppl a = i.next();
			if( isPrimitive( a ) || a == ATermUtils.BOTTOM ) {
	            ret.add( a );
            }
		}
		return ret;
	}

	public static ATermAppl makeRule(ATermAppl[] head, ATermAppl[] body) {
		return makeRule( null, head, body );
	}
	
	public static ATermAppl makeRule(ATermAppl name, ATermAppl[] head, ATermAppl[] body) {
		return factory.makeAppl( RULEFUN, name == null ? EMPTY : name, makeList(head), makeList(body) );		
	}
	
	public static ATermAppl makeBuiltinAtom(ATermAppl[] args) {		
		return factory.makeAppl( BUILTINFUN, makeList( args ) );
	}
}
