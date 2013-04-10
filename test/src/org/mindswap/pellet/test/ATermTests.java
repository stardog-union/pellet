// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.maxExclusive;
import static com.clarkparsia.pellet.utils.TermFactory.min;
import static com.clarkparsia.pellet.utils.TermFactory.minInclusive;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.restrict;
import static com.clarkparsia.pellet.utils.TermFactory.self;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mindswap.pellet.test.PelletTestCase.assertIteratorValues;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Comparators;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.DNF;
import com.clarkparsia.pellet.datatypes.Datatypes;

public class ATermTests {
    // Constants to be used as concepts
    public static ATermAppl a = term( "a" );
    public static ATermAppl b = term( "b" );
    public static ATermAppl c = term( "c" );
    public static ATermAppl d = term( "d" );

    // Constants to be used as roles
    public static ATermAppl p = term( "p" );
    public static ATermAppl q = term( "q" );
    public static ATermAppl r = term( "r" );

    public static ATermAppl d1 = restrict( Datatypes.INTEGER, minInclusive( literal( 1 ) ) ); 
    public static ATermAppl d2 = restrict( Datatypes.INTEGER, maxExclusive( literal( 2 ) ) ); 
    public static ATermAppl d3 = Datatypes.INTEGER;

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( ATermTests.class );
	}
	
	@Test
	public void testComparator() {
		// test case for #423
		
		// the following two terms are known to have equivalent hascodes with aterm 1.6
		ATermAppl a = term("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Carboplatin_Paclitaxel_ZD-6474"); 
		ATermAppl b = term("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Henna");
		// an arbitrary term that is known to have a different hahshcode
		ATermAppl c = term("c");
		
		assertTrue( a.hashCode() == b.hashCode() );
		assertFalse( a.hashCode() == c.hashCode() );
		
		assertTrue( 0 == Comparators.termComparator.compare( a, a ) );
		assertFalse( 0 == Comparators.termComparator.compare( a, b ) );
		assertFalse( 0 == Comparators.termComparator.compare( a, c ) );
	}
	
    @Test
    public void testNNF() {
        testNNF( not(some(p,c)), all(p, not(c)) );
        testNNF( not(all(p,c)), some(p, not(c)) );
        
        testNNF( not(min(p,1,c)), max(p,0,c) );
        
        testNNF( not(max(p,0,c)), min(p,1,c) );
        testNNF( not(max(p,1,not(some(p,c)))), min(p,2,all(p,not(c))) );
        
        testNNF( and(d1,d2,d3), and(d1,d2,d3) );
        testNNF( not(and(d1,d2,d3)), or(not(d1),not(d2),not(d3)) );
        testNNF( some(p,and(d1,d3)), some(p,and(d1,d3)) );
        testNNF( not(some(p,and(d1,d3))), all(p,or(not(d1),not(d3))) );
    }
    
    private void testNNF( ATermAppl c, ATermAppl expected ) {
        assertEquals( expected, ATermUtils.nnf( c ) ); 
    }

    @Test
    public void testNormalize() {        
        testNormalize( some(p,not(c)), not(all(p, c)) );
        
        testNormalize( all(p,not(c)), all(p, not(c)) );
        testNormalize( all(p,some(q,c)), all(p, not(all(q,not(c)))) );
        
        testNormalize( min(p,1,not(not(c))), min(p,1,c) );
        testNormalize( min(p,1,some(p,c)), min(p,1,not(all(p,not(c)))) );
        testNormalize( min(p,0,c), ATermUtils.TOP );
        testNormalize( min(p,1,ATermUtils.BOTTOM), ATermUtils.BOTTOM );
        
        testNormalize( max(p,0,c), not(min(p,1,c)) );
        testNormalize( max(p,1,c), not(min(p,2,c)) );
        testNormalize( max(p,1,not(some(p,not(not(c))))), not(min(p,2,all(p,not(c)))) );
        testNormalize( max(p,1,ATermUtils.BOTTOM), ATermUtils.TOP );    
        
        testNormalize( some(p,not(value(a))), not(all(p, value(a))) );
        

        testNormalize( some(p,not(d1)), not(all(p, d1)) );
        
        testNormalize( all(p,not(d1)), all(p, not(d1)) );
        testNormalize( all(p,some(q,d1)), all(p, not(all(q,not(d1)))) );
    }
        
    private void testNormalize( ATermAppl c, ATermAppl expected ) {
        assertEquals( expected, ATermUtils.normalize( c ) );        
    }    
    
    @Test
    public void testDoubleNormalize() {        
		testDoubleNormalize( and( a, b, c, d ), and( d, c, a, b ) );		
		testDoubleNormalize( and( a, b, c, d ), and( d, c, a, b, b, d, a, c ) );
		testDoubleNormalize( and( a, and( b, c ) ), and( a, b, c ) );
		
		testDoubleNormalize( or( a, b, c, d ), or( d, c, a, b ) );		
		testDoubleNormalize( or( a, b, c, d ), or( d, c, a, b, b, d, a, c ) );
		testDoubleNormalize( or( a, or( b, c ) ), or( a, b, c ) );

    }
    
    private void testDoubleNormalize( ATermAppl c1, ATermAppl c2 ) {
        assertEquals( ATermUtils.normalize( c1 ), ATermUtils.normalize( c2 ) );        
    }

    @Test
    public void testDNF() {
		testDNF( a, a );
		testDNF( not( a ), not( a ) );
		testDNF( and( a, b ), and( a, b ) );
		testDNF( or( a, b ), or( a, b ) );
		testDNF( or( a, and( b, c ) ), or( a, and( b, c ) ) );
		testDNF( and( a, or( b, c ) ), or( and( a, b ), and( a, c ) ) );
		testDNF( and( or( a, b ), or( b, c ) ), or( and( a, b ), and( a, c ), b, and( b, c ) ) );
		testDNF( and( or( a, b ), or( c, d ) ), or( and( a, c ), and( a, d ), and( b, c ), and( b, d ) ) );
		testDNF( and( a, or( and( b, c ), d ) ), or( and( a, b, c ), and( a, d ) ) );
    }
    
    private void testDNF( ATermAppl c, ATermAppl expected ) {
        assertEquals( canonicalize( expected ), DNF.dnf( c ) ); 
    }
    
    private ATermAppl canonicalize(ATermAppl term) {
		if( ATermUtils.isAnd( term ) || ATermUtils.isOr( term ) ) {
			List<ATermAppl> list = new ArrayList<ATermAppl>();
			for( ATermList l = (ATermList) term.getArgument( 0 ); !l.isEmpty(); l = l.getNext() )
				list.add( canonicalize( (ATermAppl) l.getFirst() ) );
			ATermList args = ATermUtils.toSet( list );
			if( ATermUtils.isAnd( term ) )
				return ATermUtils.makeAnd( args );
			else
				return ATermUtils.makeOr( args );
		}
		else {
			return term;
		}
    }    
    
    @Test
    public void testFindPrimitives() {        
    	testFindPrimitives( some(p,not(c)), new ATermAppl[] { c } );
        
    	testFindPrimitives( and( c, b, all( p, a ) ), new ATermAppl[] { a, b, c } );
		testFindPrimitives( max( p, 1, not( some( p, or( a, b ) ) ) ), new ATermAppl[] { a, b } );
		testFindPrimitives( min( p, 2, or( a, and( b, not( c ) ) ) ), new ATermAppl[] { a, b, c } );
		testFindPrimitives( and( some( p, ATermUtils.TOP ), all( p, a ), and(
				some( p, value( r ) ), or( self( p ), max( p, 1, b ) ) ) ),
				new ATermAppl[] { ATermUtils.TOP, a, b } );
    	testFindPrimitives( and( d1, d2, d3 ), new ATermAppl[] { d3 } );
		testFindPrimitives( not( and( not( d1 ), d2, d3 ) ), new ATermAppl[] { d3 } );
		testFindPrimitives( some( p, and( d1, d3 ) ), new ATermAppl[] { d3 } );
    }
    
    private void testFindPrimitives( ATermAppl c, ATermAppl[] expected ) {
        assertIteratorValues( ATermUtils.findPrimitives( c ).iterator(), expected );        
    } 
}
