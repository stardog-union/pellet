// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.rules;

import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.test.PelletTestCase;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.NumberUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.builtins.BooleanOperators;
import com.clarkparsia.pellet.rules.builtins.ComparisonTesters;
import com.clarkparsia.pellet.rules.builtins.DateTimeOperators;
import com.clarkparsia.pellet.rules.builtins.Function;
import com.clarkparsia.pellet.rules.builtins.FunctionApplicationVisitor;
import com.clarkparsia.pellet.rules.builtins.GeneralFunction;
import com.clarkparsia.pellet.rules.builtins.NumericFunction;
import com.clarkparsia.pellet.rules.builtins.NumericOperators;
import com.clarkparsia.pellet.rules.builtins.NumericPromotion;
import com.clarkparsia.pellet.rules.builtins.StringOperators;
import com.clarkparsia.pellet.rules.builtins.Tester;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.utils.TermFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * <p>
 * Title: Built-In Tests
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
public class BuiltInTests {

	private static BigInteger bigint( long l ) {
		return new BigInteger( String.valueOf( l ) );
	}
	
	private static BigDecimal bigdec( double d ) {
		return new BigDecimal( String.valueOf( d ) );
	}
	
	private ABox			abox;
	private KnowledgeBase	kb;

	private ATermAppl		li_1	= literal( "-1", Datatypes.INTEGER ),
			li0 = literal( "0", Datatypes.NON_NEGATIVE_INTEGER ),
			lf0 = literal( "0.0", Datatypes.FLOAT ), 
			lf00 = literal( "0.00", Datatypes.FLOAT ), 
			lp0 = literal( "0" ),
			ls0 = literal( "0", Datatypes.STRING ),
			len0 = literal( "0", "en" );

	@Before
	public void setUp() {
		kb = new KnowledgeBase();
		abox = kb.getABox();
	}

	
	
	private boolean equal(Literal l1, Literal l2) {
		if ( l1 == null && l2 == null) 
			return true;
		if ( l1 == null)
			return false;
		if ( l2 == null)
			return false;
		return ComparisonTesters.equal.test( new Literal[] { l1, l2 } );
	}
	
	private void generalFunc( GeneralFunction func, ATermAppl... args ) {
		Literal[] litArgs = new Literal[ args.length ];
		for ( int i = 0; i < args.length; i++ ) {
			litArgs[i] = abox.addLiteral( args[i] );
			assertNotNull( "Invalid iteral value: " + args[i],  litArgs[i].getValue() );
		}
		generalFunc( func, litArgs );
	}
	
	private void generalFunc( GeneralFunction func, Literal... args ) {
		assertTrue( "Full binding not accepted", func.apply( abox, args ) );
		for ( int i = 0; i < args.length; i++ ) {
			Literal[] copy = args.clone();
			copy[i] = null;
			boolean[] bound = new boolean[ args.length ];
			Arrays.fill( bound, true );
			bound[i] = false;
			
			if ( func.isApplicable( bound ) ) {
				assertTrue( "Function not accepted without argument " + i, func.apply( abox, copy ) );
				assertTrue( "Results not equal: Expected " + args[i] + ", got " + copy[i],
						equal( args[i], copy[i] ) );
			}
		}
	}

	private boolean greaterThan(Literal l1, Literal l2) {
		return ComparisonTesters.greaterThan.test( new Literal[] { l1, l2 } );
	}

	private boolean greaterThanOrEqual(Literal l1, Literal l2) {
		return ComparisonTesters.greaterThanOrEqual.test( new Literal[] { l1, l2 } );
	}

	private boolean lessThan(Literal l1, Literal l2) {
		return ComparisonTesters.lessThan.test( new Literal[] { l1, l2 } );
	}

	private boolean lessThanOrEqual(Literal l1, Literal l2) {
		return ComparisonTesters.lessThanOrEqual.test( new Literal[] { l1, l2 } );
	}

	private boolean notEqual(Literal l1, Literal l2) {
		return ComparisonTesters.notEqual.test( new Literal[] { l1, l2 } );
	}
	
	private void numeric( NumericFunction f, Number expected, Number... args ) {
		NumericPromotion promoter = new NumericPromotion();
		promoter.promote( args );
		
		FunctionApplicationVisitor visitor = new FunctionApplicationVisitor( f );
		promoter.accept( visitor );
		Number result = visitor.getResult();
		
		if( expected == null ) {
			assertNull( result );
		}
		else {
			assertNotNull( result );
			assertTrue( expected + " not equal to " + result,
					NumberUtils.compare( expected, result ) == 0 );
		}
		if ( result != null ) {
			assertEquals( "Wrong numeric type from function.", expected.getClass(), result.getClass() );
		}
			
	}
	
	private void stringFunc( Function func, String expected, String...args ) {
		ATermAppl expectedTerm =literal( expected );
		stringFunc( func, expectedTerm, args );
	}
	
	private void stringFunc( Function func, ATermAppl term, String... args ) {
		Literal expected = abox.addLiteral( term );
		stringFunc( func, expected, args );
	}
	
	private void stringFunc( Function func, Literal expected, String...args ) {
		Literal[] litArgs = new Literal[ args.length ];
		for ( int i = 0; i < args.length; i++ ) {
			litArgs[i] = abox.addLiteral( literal( args[i] ) );
		}
		stringFunc( func, expected, litArgs );
	}
	
	private void stringFunc( Function func, Literal expected, Literal... args ) {
		Literal result = func.apply( abox, null, args );
		if ( expected == null  || result == null)
			assertEquals( "Unexpected function result.", expected, result );
		else
			assertTrue( "Unexcepted resturn value. Expected " + expected + " but saw " + result, 
					ComparisonTesters.equal.test( new Literal[]{ expected, result } ) );
		
		assertEquals( "Wrong return value", expected, func.apply( abox, expected, args ) );
		assertEquals( "Unexpected equality", null, func.apply( abox, abox.addLiteral( DependencySet.INDEPENDENT ), args ) );
	}
	
	private boolean stringTest( Tester tester, String... args ) {
		Literal[] litArgs = new Literal[ args.length ];
		for ( int i = 0; i < args.length; i++ ) {
			litArgs[i] = abox.addLiteral( literal( args[i] ) );
		}
		return tester.test( litArgs );
	}

	@Test
	public void testBooleans() {
		Literal trueLit = abox.addLiteral( TermFactory.literal( true ) );
		Literal falseLit = abox.addLiteral( TermFactory.literal( false ) );

		generalFunc( BooleanOperators.booleanNot, trueLit, falseLit );
		generalFunc( BooleanOperators.booleanNot, falseLit, trueLit );

		assertFalse( BooleanOperators.booleanNot.apply( abox, new Literal[] { trueLit, trueLit } ) );
		assertFalse( BooleanOperators.booleanNot.apply( abox, new Literal[] { falseLit, falseLit } ) );
		assertFalse( BooleanOperators.booleanNot.apply( abox, new Literal[] { null, null } ) );
	}
	
	@Test
	public void testComparisons() {
		Literal l1, l2;

		l1 = abox.addLiteral( li_1 );
		l2 = abox.addLiteral( li0 );

		assertFalse( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertTrue( greaterThan( l2, l1 ) );
		assertFalse( greaterThanOrEqual( l1, l2 ) );
		assertTrue( lessThan( l1, l2 ) );
		assertFalse( lessThan( l2, l1 ) );
		assertTrue( notEqual( l1, l2 ) );

		l1 = abox.addLiteral( lf0 );
		l2 = abox.addLiteral( lf00 );

		assertTrue( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertTrue( greaterThanOrEqual( l1, l2 ) );
		assertFalse( lessThan( l1, l2 ) );
		assertTrue( lessThanOrEqual( l1, l2 ) );
		assertFalse( notEqual( l1, l2 ) );

		l1 = abox.addLiteral( lf0 );
		l2 = abox.addLiteral( li0 );

		assertTrue( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertTrue( greaterThanOrEqual( l1, l2 ) );
		assertFalse( lessThan( l1, l2 ) );
		assertTrue( lessThanOrEqual( l1, l2 ) );
		assertFalse( notEqual( l1, l2 ) );
		
		l1 = abox.addLiteral( lp0 );
		l2 = abox.addLiteral( li0 );
		
		assertFalse( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertFalse( greaterThanOrEqual( l1, l2 ) );
		assertFalse( lessThan( l1, l2 ) );
		assertFalse( lessThanOrEqual( l1, l2 ) );
		assertTrue( notEqual( l1, l2 ) );
		
		l1 = abox.addLiteral( lp0 );
		l2 = abox.addLiteral( ls0 );
		assertTrue( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertTrue( greaterThanOrEqual( l1, l2 ) );
		assertFalse( lessThan( l1, l2 ) );
		assertTrue( lessThanOrEqual( l1, l2 ) );
		assertFalse( notEqual( l1, l2 ) );
		
		l1 = abox.addLiteral( lp0 );
		l2 = abox.addLiteral( len0 );
		assertFalse( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertFalse( greaterThanOrEqual( l1, l2 ) );
		assertFalse( lessThan( l1, l2 ) );
		assertFalse( lessThanOrEqual( l1, l2 ) );
		assertTrue( notEqual( l1, l2 ) );

		l1 = abox.addLiteral( literal("2000-01-01", Datatypes.DATE) );
		l2 = abox.addLiteral( literal("2010-01-01", Datatypes.DATE) );

		assertFalse( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertTrue( greaterThan( l2, l1 ) );
		assertFalse( greaterThanOrEqual( l1, l2 ) );
		assertTrue( lessThan( l1, l2 ) );
		assertFalse( lessThan( l2, l1 ) );
		assertTrue( notEqual( l1, l2 ) );

		l1 = abox.addLiteral( literal("2010-01-01T01:00:00Z", Datatypes.DATE_TIME) );
		l2 = abox.addLiteral( literal("2010-01-01T02:00:00Z", Datatypes.DATE_TIME) );

		assertFalse( equal( l1, l2 ) );
		assertFalse( greaterThan( l1, l2 ) );
		assertTrue( greaterThan( l2, l1 ) );
		assertFalse( greaterThanOrEqual( l1, l2 ) );
		assertTrue( lessThan( l1, l2 ) );
		assertFalse( lessThan( l2, l1 ) );
		assertTrue( notEqual( l1, l2 ) );

	}
	
	@Test
	public void testDateTimes() {
		System.out.println( "Starting date time tests" );
		// Date Creation tests.
//		stringFunc( DateTimeOperators.date, literal( "2008-01-28", Datatypes.DATE ), 
//				"2008", "01", "28" );
//		stringFunc( DateTimeOperators.date, literal( "2008-01-28Z", Datatypes.DATE ), 
//				"2008", "01", "28", "Z" );
//		stringFunc( DateTimeOperators.date, literal( "2008-01-28", Datatypes.DATE ), 
//				"2008", "1", "28" );
		generalFunc( DateTimeOperators.date, literal( "2008-01-28", Datatypes.DATE),
				literal( "2008", Datatypes.INTEGER ),
				literal( "1", Datatypes.INTEGER ),
				literal( "28", Datatypes.INTEGER ) );
		generalFunc( DateTimeOperators.date, literal( "2008-01-28Z", Datatypes.DATE),
				literal( "2008", Datatypes.INTEGER ),
				literal( "1", Datatypes.INTEGER ),
				literal( "28", Datatypes.INTEGER ),
				literal("Z") 
				);
		generalFunc( DateTimeOperators.dateTime, literal( "2008-01-28T00:01:03.1", Datatypes.DATE_TIME),
				literal( "2008", Datatypes.INTEGER ),
				literal( "1", Datatypes.INTEGER ),
				literal( "28", Datatypes.INTEGER ),
				literal( "00", Datatypes.INTEGER ),
				literal( "01", Datatypes.INTEGER ),
				literal( "03.1", Datatypes.DECIMAL )
				);
		generalFunc( DateTimeOperators.dateTime, literal( "2008-01-28T00:01:03.1Z", Datatypes.DATE_TIME),
				literal( "2008", Datatypes.INTEGER ),
				literal( "1", Datatypes.INTEGER ),
				literal( "28", Datatypes.INTEGER ),
				literal( "00", Datatypes.INTEGER ),
				literal( "01", Datatypes.INTEGER ),
				literal( "03.1", Datatypes.DECIMAL ),
				literal("Z")
				);
		generalFunc( DateTimeOperators.time, literal( "00:01:03.1", Datatypes.TIME),
				literal( "00", Datatypes.INTEGER ),
				literal( "01", Datatypes.INTEGER ),
				literal( "03.1", Datatypes.DECIMAL )
				);
		generalFunc( DateTimeOperators.time, literal( "00:01:03.1Z", Datatypes.TIME),
				literal( "00", Datatypes.INTEGER ),
				literal( "01", Datatypes.INTEGER ),
				literal( "03.1", Datatypes.DECIMAL ),
				literal("Z")
				);
		
//		stringFunc( DateTimeOperators.dateTime, literal( "2008-01-28T10:01:01.4", Datatypes.DATE_TIME ),
//				"2008", "01", "28", "10", "01", "01.4");
//		stringFunc( DateTimeOperators.dateTime, literal( "2008-01-28T10:01:01.4", Datatypes.DATE_TIME ),
//				"2008", "1", "28", "10", "01", "01.4");
	}

	@Ignore("Duration datatypes are not supported")
	@Test
	public void testDurations() {
		stringFunc( DateTimeOperators.dayTimeDuration, literal( "P1DT1H", Datatypes.DURATION ),
				"1", "01");
		stringFunc( DateTimeOperators.dayTimeDuration, literal( "P1DT1H", Datatypes.DURATION ),
				"1", "1");
		stringFunc( DateTimeOperators.yearMonthDuration, literal( "P3Y", Datatypes.DURATION ),
				"1", "24");
	}

	@Test
	public void testNumerics() {
		
		numeric( NumericOperators.abs, bigint( 5000 ), bigint( -5000 ) );
		numeric( NumericOperators.abs, bigdec( 500.0 ), bigdec( -500.0 ) );
		numeric( NumericOperators.abs, 500.0f, -500.0f );
		numeric( NumericOperators.abs, 500.0, -500.0 );
		
		numeric( NumericOperators.add, bigint( 10000 ), 7500, 2500 );
		numeric( NumericOperators.add, bigdec( 10000.25 ), bigdec( 7500.25 ), 2500 );
		numeric( NumericOperators.add, 500.5f, 250.5f, 250 );
		numeric( NumericOperators.add, 500.25, 250.125, 250.125 );
		
		numeric( NumericOperators.ceiling, bigint( 1000 ), 1000 );
//		numeric( NumericOperators.ceiling, bigint( 1000 ), 1000, 2 );
		numeric( NumericOperators.ceiling, bigdec( 500.0 ), bigdec( 499.1 ) );
//		numeric( NumericOperators.ceiling, bigdec( 500.5 ), bigdec( 500.45 ), 1 );
		numeric( NumericOperators.ceiling, 500.0f, 499.01f );
//		numeric( NumericOperators.ceiling, 500.02f, 499.012f, 2 );
		numeric( NumericOperators.ceiling, 500.0, 499.01 );
//		numeric( NumericOperators.ceiling, 500.02, 499.012, 2 );
		
		numeric( NumericOperators.cos, Math.cos( 25 ), 25 );
		numeric( NumericOperators.cos, Math.cos( 25 ), bigdec( 25.0 ) );
		numeric( NumericOperators.cos, Math.cos( 25 ), 25.0f );
		numeric( NumericOperators.cos, Math.cos( 25 ), 25.0 );
		
		numeric( NumericOperators.divide, bigdec( 2.5 ), 5, 2 );
		numeric( NumericOperators.divide, bigdec( 2.25 ), bigdec( 4.5 ), 2 );
		numeric( NumericOperators.divide, 4.125f, 16.5f, 4 );
		numeric( NumericOperators.divide, 4.125, 16.5, 4 );
		
		numeric( NumericOperators.floor, bigint( 1000 ), 1000 );
//		numeric( NumericOperators.floor, bigint( 1000 ), 1000, 2 );
		numeric( NumericOperators.floor, bigdec( 499.0 ), bigdec( 499.1 ) );
//		numeric( NumericOperators.floor, bigdec( 500.4 ), bigdec( 500.45 ), 1 );
		numeric( NumericOperators.floor, null, bigdec( 500.45 ), bigdec( 1.5 ) );
		numeric( NumericOperators.floor, 499.0f, 499.01f );
//		numeric( NumericOperators.floor, 499.01f, 499.012f, 2 );
		numeric( NumericOperators.floor, 499.0, 499.01 );
//		numeric( NumericOperators.floor, 499.01, 499.012, 2 );
		
		numeric( NumericOperators.integerDivide, bigint( 500 ), 1001, 2 );
		numeric( NumericOperators.integerDivide, bigint( 500 ), bigdec( 1001.1 ), 2 );
		numeric( NumericOperators.integerDivide, bigint( 500 ), 1001.125f, 2 );
		numeric( NumericOperators.integerDivide, bigint( 500 ), 1001.125, 2.0 );
		
		numeric( NumericOperators.mod, bigint( 1 ), 10, 3 );
		numeric( NumericOperators.mod, bigint( -1 ), -10, 3 );
		numeric( NumericOperators.mod, bigint( 0 ), 6, -2 );
		numeric( NumericOperators.mod, bigdec( 0.9 ), bigdec( 4.5 ), bigdec( 1.2 ) );
		numeric( NumericOperators.mod, bigdec( 0.9 ), bigdec( 4.5 ), bigdec( -1.2 ) );
		numeric( NumericOperators.mod, bigdec( -0.9 ), bigdec( -4.5 ), bigdec( 1.2 ) );
		numeric( NumericOperators.mod, bigdec( -0.9 ), bigdec( -4.5 ), bigdec( -1.2 ) );
		numeric( NumericOperators.mod, 3.0E0f, 1.23E2f, 0.6E1f );
		numeric( NumericOperators.mod, 3.0E0f, 1.23E2f, -0.6E1f );
		numeric( NumericOperators.mod, -3.0E0f, -1.23E2f, 0.6E1f );
		numeric( NumericOperators.mod, -3.0E0f, -1.23E2f, -0.6E1f );
		numeric( NumericOperators.mod, 3.0E0, 1.23E2, 0.6E1 );
		numeric( NumericOperators.mod, 3.0E0, 1.23E2, -0.6E1 );
		numeric( NumericOperators.mod, -3.0E0, -1.23E2, 0.6E1 );
		numeric( NumericOperators.mod, -3.0E0, -1.23E2, -0.6E1 );
		
		numeric( NumericOperators.multiply, bigint( 7500*2500 ), 7500, 2500 );
		numeric( NumericOperators.multiply, bigdec( 7500.25*2500 ), bigdec( 7500.25 ), 2500 );
		numeric( NumericOperators.multiply, 250.5f * 250.0f, 250.5f, 250 );
		numeric( NumericOperators.multiply, 250.125 * 250.125, 250.125, 250.125 );
		
		numeric( NumericOperators.pow, bigint( Long.MAX_VALUE ).pow( 99 ), Long.MAX_VALUE, 99 );
//		numeric( NumericOperators.pow, bigdec( Math.PI ).pow( 30 ), bigdec( Math.PI ), 30 );
		// TODO If we found (or made) an implementation of pow for decimals, we could return a result.
		numeric( NumericOperators.pow, null, bigdec( Math.PI ), bigdec( 3.5 ) );
		numeric( NumericOperators.pow, (float) Math.pow( 40.0, 5.125 ), 40.0f, 5.125f );
		numeric( NumericOperators.pow, Math.pow( 40.0, 5.125), 40.0, 5.125 );
		
		// examples from XQuery spec
		numeric( NumericOperators.round, bigdec( 3 ), bigdec( 2.5 ) );
		numeric( NumericOperators.round, bigdec( 2 ), bigdec( 2.49999 ) );
		numeric( NumericOperators.round, bigdec( -2 ), bigdec( -2.5 ) );
		numeric( NumericOperators.round, 3d, 2.5d );
		numeric( NumericOperators.round, 2d, 2.49999d );
		numeric( NumericOperators.round, -2d, -2.5d );
		numeric( NumericOperators.round, 3f, 2.5f );
		numeric( NumericOperators.round, 2f, 2.49999f );
		numeric( NumericOperators.round, -2f, -2.5f );
		
		// more custom examples
		numeric( NumericOperators.round, bigint( 1000 ), 1000 );
//		numeric( NumericOperators.round, bigint( 1000 ), 1000, 2 );
		numeric( NumericOperators.round, bigdec( 500.0 ), bigdec( 499.5 ) );
//		numeric( NumericOperators.round, bigdec( 500.4 ), bigdec( 500.44 ), 1 );
//		numeric( NumericOperators.round, null, bigdec( 500.45 ), bigdec( 1.5 ) );
		numeric( NumericOperators.round, 499.0f, 499.04f );
//		numeric( NumericOperators.round, 499.02f, 499.015f, 2 );
		numeric( NumericOperators.round, 499.0, 499.01 );
//		numeric( NumericOperators.round, 499.02, 499.015, 2 );

	    numeric( NumericOperators.roundHalfToEven, bigdec( 0 ), bigdec( 0.5 ) );
	    numeric( NumericOperators.roundHalfToEven, bigdec( 2 ), bigdec( 1.5 ) );
	    numeric( NumericOperators.roundHalfToEven, bigdec( 2 ), bigdec( 2.5 ) );
	    numeric( NumericOperators.roundHalfToEven, bigdec( 3567.81E0 ), bigdec( 3.567812E+3 ), 2 );
	    numeric( NumericOperators.roundHalfToEven, bigdec( 35600 ), bigdec( 35612.25 ), -2 );
		
		numeric( NumericOperators.roundHalfToEven, bigint( 1000 ), 1000 );
		numeric( NumericOperators.roundHalfToEven, bigint( 1000 ), 1000, 2 );
		numeric( NumericOperators.roundHalfToEven, bigdec( 500.0 ), bigdec( 499.5 ) );
		numeric( NumericOperators.roundHalfToEven, bigdec( 500.0 ), bigdec( 500.5 ) );
		numeric( NumericOperators.roundHalfToEven, bigdec( 500.4 ), bigdec( 500.44 ), 1 );
		numeric( NumericOperators.roundHalfToEven, null, bigdec( 500.45 ), bigdec( 1.5 ) );
		numeric( NumericOperators.roundHalfToEven, 499.0f, 499.04f );
		numeric( NumericOperators.roundHalfToEven, 499.02f, 499.015f, 2 );
		numeric( NumericOperators.roundHalfToEven, 499.02f, 499.025f, 2 );
		numeric( NumericOperators.roundHalfToEven, 499.0, 499.01 );
		numeric( NumericOperators.roundHalfToEven, 499.02, 499.015, 2 );
		numeric( NumericOperators.roundHalfToEven, 499.02, 499.025, 2 );
		
		numeric( NumericOperators.sin, Math.sin( 25 ), 25 );
		numeric( NumericOperators.sin, Math.sin( 25 ), bigdec( 25.0 ) );
		numeric( NumericOperators.sin, Math.sin( 25 ), 25.0f );
		numeric( NumericOperators.sin, Math.sin( 25 ), 25.0 );
		
		numeric( NumericOperators.subtract, bigint( 5000 ), 7500, 2500 );
		numeric( NumericOperators.subtract, bigdec( 5000.25 ), bigdec( 7500.25 ), 2500 );
		numeric( NumericOperators.subtract, 0.5f, 250.5f, 250 );
		numeric( NumericOperators.subtract, 0.125, 250.25, 250.125 );
		
		numeric( NumericOperators.tan, Math.tan( 25 ), 25 );
		numeric( NumericOperators.tan, Math.tan( 25 ), bigdec( 25.0 ) );
		numeric( NumericOperators.tan, Math.tan( 25 ), 25.0f );
		numeric( NumericOperators.tan, Math.tan( 25 ), 25.0 );
		
		numeric( NumericOperators.unaryMinus, bigint( 25 ), -25 );
		numeric( NumericOperators.unaryMinus, bigdec( 25.0 ), bigdec( -25.0 ) );
		numeric( NumericOperators.unaryMinus, 25.0f, -25.0f );
		numeric( NumericOperators.unaryMinus, 25.0, -25.0 );
		
		numeric( NumericOperators.unaryPlus, bigint( 25 ), 25 );
		numeric( NumericOperators.unaryPlus, bigdec( 25.0 ), bigdec( 25.0 ) );
		numeric( NumericOperators.unaryPlus, 25.0f, 25.0f );
		numeric( NumericOperators.unaryPlus, 25.0, 25.0 );
	}
	

	@Test
	public void testStrings() {
		
		assertFalse( stringTest( StringOperators.contains, "defg", "abcdefghij" ) );
		assertTrue( stringTest( StringOperators.contains, "abcdefghij", "defg" ) );
		
		assertFalse( stringTest( StringOperators.containsIgnoreCase, "defG", "abcDefghij" ) );
		assertTrue( stringTest( StringOperators.containsIgnoreCase, "abcDefghij", "defG" ) );
		
		assertFalse( stringTest( StringOperators.endsWith, "defg", "abcdefg" ) );
		assertTrue( stringTest( StringOperators.endsWith, "abcdefg", "defg" ) );
		
		stringFunc( StringOperators.lowerCase, "abcdefg", "AbCDefg" );
		stringFunc( StringOperators.lowerCase, (Literal)null, "abc", "deF" );
		stringFunc( StringOperators.lowerCase, "", "" );
		
		assertTrue( stringTest( StringOperators.matches, "abcdefg", ".*cs*.+" ) );
		assertFalse( stringTest( StringOperators.matches, "abcdefg", "s+" ) );
		assertTrue( stringTest( StringOperators.matches, "abcdefg", "^abc+.*g$" ) );
		
		stringFunc( StringOperators.normalizeSpace, "ab cd efg", "\tab cd   efg\t    " );
		
		stringFunc( StringOperators.replace, "ba", "aaa", "aa", "b" );
		stringFunc( StringOperators.replace, "aab", "aab", "cc", "b" );
		
		assertTrue( stringTest( StringOperators.startsWith, "abcdefg", "abc" ) );
		assertFalse( stringTest( StringOperators.startsWith, "abc", "abcdefg" ) );
		
		stringFunc( StringOperators.stringConcat, "" );
		stringFunc( StringOperators.stringConcat, "abcdefg", "ab", "cde", "f", "g", "" );
		
		assertTrue( stringTest( StringOperators.stringEqualIgnoreCase, "abCdEfG", "ABcDeFg" ) );
		assertFalse( stringTest( StringOperators.stringEqualIgnoreCase, "abCd", "abCde" ) );
		
		stringFunc( StringOperators.stringLength, literal( "5", Datatypes.INTEGER ), "abcde" );
		stringFunc( StringOperators.stringLength, (Literal) null, "abcde", "fgh" );
		
		/*

	      fn:substring("motor car", 6) returns " car".

	      Characters starting at position 6 to the end of $sourceString are selected.
	    */
		stringFunc( StringOperators.substring, " car", "motor car", "6");
		
		/*
	      fn:substring("metadata", 4, 3) returns "ada".

	      Characters at positions greater than or equal to 4 and less than 7 are selected.
	    */
		
		stringFunc( StringOperators.substring, "ada", "metadata", "4", "3" );

		/*
	      fn:substring("12345", 1.5, 2.6) returns "234".

	      Characters at positions greater than or equal to 2 and less than 5 are selected.
	    */
		
		stringFunc( StringOperators.substring, "234", "12345", "1.5", "2.6" );

		/*
	      fn:substring("12345", 0, 3) returns "12".

	      Characters at positions greater than or equal to 0 and less than 3 are selected. Since the first position is 1, these are the characters at positions 1 and 2.
	    */
		
		stringFunc( StringOperators.substring, "12", "12345", "0", "3" );

		/*
	      fn:substring("12345", 5, -3) returns "".

	      Characters at positions greater than or equal to 5 and less than 2 are selected.
	    */
		
		stringFunc( StringOperators.substring, "", "12345", "5", "-3" );
		
		/*
	      fn:substring("12345", -3, 5) returns "1".

	      Characters at positions greater than or equal to -3 and less than 2 are selected. Since the first position is 1, this is the character at position 1.
	    */
		
		stringFunc( StringOperators.substring, "1", "12345", "-3", "5" );

		/*
	      fn:substring("12345", 0 div 0E0, 3) returns "".

	      Since 0 div 0E0 returns NaN, and NaN compared to any other number returns false, no characters are selected.
	    */

		stringFunc( StringOperators.substring, "", "12345", "NaN", "3" );
		
		/*
	      fn:substring("12345", 1, 0 div 0E0) returns "".

	      As above.
	    */
		
		stringFunc( StringOperators.substring, "", "12345", "1", "NaN" );

		/*
	      fn:substring((), 1, 3) returns "".
	    */
		
		stringFunc( StringOperators.substring, "", "", "1", "3" );

		/*
	      fn:substring("12345", -42, 1 div 0E0) returns "12345".

	      Characters at positions greater than or equal to -42 and less than INF are selected.
	    */
		
		stringFunc( StringOperators.substring, "12345", "12345", "-42", new Double(1 / 0E0).toString() );

		/*
	      fn:substring("12345", -1 div 0E0, 1 div 0E0) returns "".

	      Since -INF + INF returns NaN, no characters are selected.
 		*/
		
		stringFunc( StringOperators.substring, "", "12345", new Double(-1 / 0E0).toString(), new Double( 1 / 0E0).toString() );
		
		stringFunc( StringOperators.substring, "g", "abcdefg", "7", "9" );

		stringFunc( StringOperators.substringAfter, "fg", "abcdefg", "de" );
		stringFunc( StringOperators.substringAfter, "", "abcdefg", "" );
		stringFunc( StringOperators.substringAfter, "", "abcdefg", "zzz" );
		
		stringFunc( StringOperators.substringBefore, "abc", "abcdefg", "de" );
		stringFunc( StringOperators.substringBefore, "", "abcdefg", "" );
		stringFunc( StringOperators.substringBefore, "", "abcdefg", "zzz" );
		
		stringFunc( StringOperators.translate, "acdefgh", "abcdefg", "bcdefg", "cdefgh" );
		stringFunc( StringOperators.translate, "acdegh", "abcdefg", "abcdfge", "acdegh" );
		
		stringFunc( StringOperators.upperCase, "ABCDEFG", "abcDefG" );
	}
	

	@Test
	public void testQuery() {
		final String ns = "http://owldl.com/ontologies/swrl/tests/builtIns/007#";

		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( "file:" + SWRLTestSuite.base + "builtIns/007-premise.n3", null, "N3" );
		model.prepare();

		Resource a = model.createResource( ns + "a" );
		Resource b = model.createResource( ns + "b" );

		Property feet = model.createProperty( ns + "lengthInFeet" );
		Property inches = model.createProperty( ns + "lengthInInches" );

		RDFNode lit12 = model.createTypedLiteral( 12.0f );
		RDFNode lit24 = model.createTypedLiteral( 24.0f );

		Model inferences = ModelFactory.createDefaultModel();
		PelletTestCase.addStatements( inferences, a, feet, model.createTypedLiteral( 1f ) );
		PelletTestCase.addStatements( inferences, b, feet, model.createTypedLiteral( 2f ) );				
		PelletTestCase.assertPropertyValues( model, feet, inferences );
		
		inferences = ModelFactory.createDefaultModel();
		PelletTestCase.addStatements( inferences, a, inches, lit12 );
		PelletTestCase.addStatements( inferences, b, inches, lit24 );				
		PelletTestCase.assertPropertyValues( model, inches, inferences );
	}
	
	@Test
	public void testTokenizeBinding() {
		KnowledgeBase kb = new KnowledgeBase();
		AtomDConstant data = new AtomDConstant( literal( "hi;bye;foo;bar" ) );
		
		AtomDVariable x = new AtomDVariable("x");
		AtomDConstant semicolan = new AtomDConstant( literal( ";" ) );
		Collection<AtomVariable> emptyCollection = Collections.emptySet();
		Collection<AtomVariable> xSingleton = Collections.singleton( (AtomVariable) x );
		
		BuiltInAtom oneVarAtom = new BuiltInAtom( Namespaces.SWRLB + "tokenize", x, data, semicolan );
		BindingHelper sharedVarHelper = StringOperators.tokenize.createHelper( oneVarAtom );
		assertTrue( sharedVarHelper.getBindableVars( emptyCollection ).equals( xSingleton ) );
		VariableBinding emptyBinding = new VariableBinding( kb.getABox() );
		sharedVarHelper.rebind( emptyBinding );
		
		VariableBinding fillBinding = new VariableBinding( kb.getABox() );
		List<String> expected = Arrays.asList( new String[] { "hi", "bye", "foo", "bar" } );
		List<String> tokens = new ArrayList<String>();
		while ( sharedVarHelper.selectNextBinding() ) {
			sharedVarHelper.setCurrentBinding( fillBinding );
			String token = ATermUtils.getLiteralValue( fillBinding.get( x ).getTerm() );
			tokens.add( token );
		}
		assertEquals("String tokenizer returned unexpected sequence of tokens", expected, tokens );
	}
	
	@Test
	public void testTokenizeBindingEmpty() {
		KnowledgeBase kb = new KnowledgeBase();
		AtomDConstant data = new AtomDConstant( literal( "hi;bye;foo;bar" ) );
		
		AtomDVariable x = new AtomDVariable("x");
		AtomDConstant comma = new AtomDConstant( literal( "," ) );
		Collection<AtomVariable> emptyCollection = Collections.emptySet();
		Collection<AtomVariable> xSingleton = Collections.singleton( (AtomVariable) x );
		
		BuiltInAtom oneVarAtom = new BuiltInAtom( Namespaces.SWRLB + "tokenize", x, data, comma );
		BindingHelper sharedVarHelper = StringOperators.tokenize.createHelper( oneVarAtom );
		assertTrue( sharedVarHelper.getBindableVars( emptyCollection ).equals( xSingleton ) );
		VariableBinding emptyBinding = new VariableBinding( kb.getABox() );
		sharedVarHelper.rebind( emptyBinding );
		
		VariableBinding fillBinding = new VariableBinding( kb.getABox() );
		List<String> expected = Collections.singletonList( ATermUtils.getLiteralValue( data.getValue() ) );
		List<String> tokens = new ArrayList<String>();
		while ( sharedVarHelper.selectNextBinding() ) {
			sharedVarHelper.setCurrentBinding( fillBinding );
			String token = ATermUtils.getLiteralValue( fillBinding.get( x ).getTerm() );
			tokens.add( token );
		}
		assertEquals("String tokenizer returned unexpected sequence of tokens", expected, tokens );
	}
	
	@Test
	public void testTokenizeSharedSuccess() {
		KnowledgeBase kb = new KnowledgeBase();
		AtomDConstant data = new AtomDConstant( literal( "hi;bye;foo;bar" ) );
		
		AtomDVariable x = new AtomDVariable("x");
		AtomDConstant comma = new AtomDConstant( literal( "," ) );
		Collection<AtomVariable> emptyCollection = Collections.emptySet();
		Collection<AtomVariable> xSingleton = Collections.singleton( (AtomVariable) x );
		
		BuiltInAtom sharedVarAtom = new BuiltInAtom( Namespaces.SWRLB + "tokenize", x, x, comma );
		BindingHelper sharedVarHelper = StringOperators.tokenize.createHelper( sharedVarAtom );
		assertTrue( sharedVarHelper.getBindableVars( emptyCollection ).isEmpty() );
		assertTrue( sharedVarHelper.getBindableVars( xSingleton ).isEmpty() );
		VariableBinding xdataBinding = new VariableBinding( kb.getABox() );
		xdataBinding.set( x, data.getValue() );
		sharedVarHelper.rebind( xdataBinding );
		assertTrue( sharedVarHelper.selectNextBinding() );
		assertFalse( sharedVarHelper.selectNextBinding() );
	
	}

	@Test
	public void testTokenizeSharedFailure() {
		KnowledgeBase kb = new KnowledgeBase();
		AtomDConstant data = new AtomDConstant( literal( "hi;bye;foo;bar" ) );
		
		AtomDVariable x = new AtomDVariable("x");
		AtomDConstant semicolan = new AtomDConstant( literal( ";" ) );
		Collection<AtomVariable> emptyCollection = Collections.emptySet();
		Collection<AtomVariable> xSingleton = Collections.singleton( (AtomVariable) x );
		
		BuiltInAtom sharedVarAtom = new BuiltInAtom( Namespaces.SWRLB + "tokenize", x, x, semicolan );
		BindingHelper sharedVarHelper = StringOperators.tokenize.createHelper( sharedVarAtom );
		assertTrue( sharedVarHelper.getBindableVars( emptyCollection ).isEmpty() );
		assertTrue( sharedVarHelper.getBindableVars( xSingleton ).isEmpty() );
		VariableBinding xdataBinding = new VariableBinding( kb.getABox() );
		xdataBinding.set( x, data.getValue() );
		sharedVarHelper.rebind( xdataBinding );
		assertFalse( sharedVarHelper.selectNextBinding() );
	
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( BuiltInTests.class );
	}
}
