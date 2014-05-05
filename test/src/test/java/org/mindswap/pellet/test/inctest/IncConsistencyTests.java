// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.inctest;

import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.max;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static com.clarkparsia.pellet.utils.TermFactory.value;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.mindswap.pellet.utils.ATermUtils.negate;
import static org.mindswap.pellet.utils.ATermUtils.normalize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.KnowledgeBase.ChangeType;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Unit tests for incremental consistency checking.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Christian Halaschek-Wiener
 */
@RunWith(Parameterized.class)
public class IncConsistencyTests extends AbstractKBTests {

	@Parameterized.Parameters
	public static Collection<Object[]> getTestCases() {
		ArrayList<Object[]> cases = new ArrayList<Object[]>();
		cases.add( new Object[] { false, false, false } );
		cases.add( new Object[] { true, false, false } );
		cases.add( new Object[] { true, true, false } );
		cases.add( new Object[] { true, true, true } );
		return cases;
	}

	private boolean					preUCQ;
	private boolean					preUIC;
	private boolean					preUSR;
	private boolean					preUT;
	private boolean					preUID;

	private boolean					ucq;
	private boolean					uic;
	private boolean					uid;

	private static final boolean	PRINT_ABOX	= false;

	private ATermAppl				robert		= term( "Robert" ), mary = term( "Mary" ),
			chris = term( "Chris" ), john = term( "John" ), bill = term( "Bill" ),
			victor = term( "Victor" ), mbox = term( "mbox" ), relative = term( "relative" ),
			sibling = term( "sibling" ), person = term( "person" ),
			animalOwner = term( "animalOwner" ), owns = term( "owns" ),
			ownedBy = term( "ownedBy" ), knows = term( "knows" ), notPerson = not( person ),
			man = term( "man" ), woman = term( "woman" ), animal = term( "animal" ),
			dog = term( "dog" ), cat = term( "cat" ), notCat = not( cat ), notDog = not( dog ),
			ssn = term( "ssn" ), ownsAnimal = term( "ownsAnimal" );

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( IncConsistencyTests.class );
	}

	public IncConsistencyTests(boolean ucq, boolean uic, boolean uid) {
		this.ucq = ucq;
		this.uic = uic;
		this.uid = uid;
	}

	/**
	 * Verify that differentFrom assertions survive ABox reset
	 */
	@Test
	public void differentAfterReset() {
		
		kb.addIndividual( robert );
		kb.addIndividual( chris );
		kb.addDifferent( robert, chris );
		kb.addDatatypeProperty( ssn );

		ATermAppl literal = ATermUtils.makePlainLiteral( "xxx" );
		kb.addPropertyValue( ssn, chris, literal );
		
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isDifferentFrom( robert, chris ));
		assertTrue( kb.isDifferentFrom( chris, robert ));
		
		// ABox property removal should cause ABox reset.
		assertTrue( kb.removePropertyValue( ssn, chris, literal ) );
		assertTrue( kb.isChanged( ChangeType.ABOX_DEL ) );
		
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isDifferentFrom( robert, chris ));
		assertTrue( kb.isDifferentFrom( chris, robert ));	
	}
	
	/**
	 * Test that node merge state is correctly handled in reset. In trunk r1495,
	 * this is known to cause a NPE because Node.mergeDepends is incorrectly
	 * reset to null
	 */
	@Test
	public void mergeDependsAfterReset() {
		
		kb.addIndividual( robert );
		kb.addIndividual( chris );
		kb.addSame( robert, chris );
		kb.addDatatypeProperty( ssn );
		kb.addDatatypeProperty( mbox );

		ATermAppl literal = ATermUtils.makePlainLiteral( "xxx" );
		kb.addPropertyValue( ssn, chris, literal );
		kb.addPropertyValue( mbox, chris, literal );
		
		assertTrue( kb.isConsistent() );
		assertEquals( Bool.TRUE, kb.hasKnownPropertyValue( chris, ssn, literal ));
		assertEquals( Bool.TRUE, kb.hasKnownPropertyValue( robert, ssn, literal ));
		
		// ABox property removal should cause ABox reset.
		assertTrue( kb.removePropertyValue( mbox, chris, literal ) );
		assertTrue( kb.isChanged( ChangeType.ABOX_DEL ) );
		
		assertTrue( kb.isConsistent() );
		assertEquals( Bool.TRUE, kb.hasKnownPropertyValue( chris, ssn, literal ));
		assertEquals( Bool.TRUE, kb.hasKnownPropertyValue( robert, ssn, literal ));
	}
	
	@Before
	public void setUp() {
		preUCQ = PelletOptions.USE_COMPLETION_QUEUE;
		preUIC = PelletOptions.USE_INCREMENTAL_CONSISTENCY;
		preUSR = PelletOptions.USE_SMART_RESTORE;
		preUID = PelletOptions.USE_INCREMENTAL_DELETION;
		preUT = PelletOptions.USE_TRACING;

		PelletOptions.USE_COMPLETION_QUEUE = ucq;
		PelletOptions.USE_INCREMENTAL_CONSISTENCY = uic;
		PelletOptions.USE_INCREMENTAL_DELETION = uid;
		PelletOptions.USE_TRACING = uid;
		PelletOptions.USE_SMART_RESTORE = true;
		PelletOptions.PRINT_ABOX = PRINT_ABOX;

		super.initializeKB();
		
		kb.setDoExplanation( PelletOptions.USE_TRACING );
	}

	@After
	public void tearDown() throws Exception {
		super.disposeKB();
		
		PelletOptions.USE_COMPLETION_QUEUE = preUCQ;
		PelletOptions.USE_INCREMENTAL_CONSISTENCY = preUIC;
		PelletOptions.USE_SMART_RESTORE = preUSR;
		PelletOptions.USE_TRACING = preUT;
		PelletOptions.USE_INCREMENTAL_DELETION = preUID;
	}

	@Test
	public void testDisjunction() {

		// basic classes
		kb.addClass( person );
		kb.addClass( animalOwner );
		kb.addClass( animal );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addDisjointClass( dog, cat );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addDomain( sibling, person );
		kb.addDatatypeProperty( ssn );
		kb.addObjectProperty( mbox );
		kb.addObjectProperty( ownsAnimal );
		kb.addDomain( ownsAnimal, person );
		kb.addRange( ownsAnimal, animal );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( victor );
		kb.addIndividual( mary );
		ATermAppl ssn1 = literal( "012345678" );
		kb.addPropertyValue( ssn, robert, ssn1 );

		// add specific test case axioms/assertions
		ATermAppl catOrDog = or( dog, cat );
		kb.addSubClass( animal, catOrDog );
		ATermAppl ownsSomeAnimal = some( ownsAnimal, animal );
		kb.addClass( ownsSomeAnimal );
		kb.addSubClass( animalOwner, ownsSomeAnimal );
		ATermAppl ownsNoCats = all( ownsAnimal, notCat );
		ATermAppl ownsNoDogs = all( ownsAnimal, notDog );

		kb.addType( robert, ownsSomeAnimal );
		kb.addType( mary, ownsSomeAnimal );

		assertTrue( kb.isConsistent() );

		kb.addType( mary, ownsNoDogs );
		assertTrue( kb.isConsistent() );

		kb.addType( robert, ownsNoDogs );
		assertTrue( kb.isConsistent() );

		kb.addType( robert, ownsNoCats );
		assertFalse( kb.isConsistent() );

		kb.removeType( robert, ownsNoDogs );
		assertTrue( kb.isConsistent() );

		kb.addType( mary, ownsNoCats );
		assertFalse( kb.isConsistent() );

		kb.removeType( mary, ownsNoDogs );
		assertTrue( kb.isConsistent() );

		kb.removeType( mary, ownsNoCats );
		assertTrue( kb.isConsistent() );

		kb.addType( mary, ownsNoDogs );
		assertTrue( kb.isConsistent() );
	}

	@Test
	public void testDisjunction2() {

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		kb.addClass( ownsDog );

		kb.addSubClass( man, ownsDog );

		ATermAppl owersAreDogs = all( ownedBy, dog );
		kb.addClass( owersAreDogs );

		kb.addSubClass( dog, owersAreDogs );

		ATermAppl manOrDog = or( man, dog );
		kb.addClass( manOrDog );

		kb.addType( robert, manOrDog );
		kb.addType( robert, not( dog ) );
		assertFalse( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		kb.removeType( robert, not( dog ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		assertTrue( kb.getABox().getIndividual( robert ).hasType( dog ) );

	}

	@Test
	public void testMax() {
		// TODO

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );

		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( chris );
		kb.addIndividual( victor );
		kb.addIndividual( john );
		kb.addIndividual( bill );

		kb.addPropertyValue( sibling, bill, mary );
		kb.addPropertyValue( sibling, bill, chris );
		kb.addPropertyValue( sibling, bill, victor );

		kb.addPropertyValue( sibling, robert, mary );
		kb.addPropertyValue( sibling, robert, chris );
		kb.addPropertyValue( sibling, robert, victor );
		kb.addPropertyValue( sibling, robert, john );

		// add specific test case axioms/assertions

		ATermAppl twoSiblings = max( sibling, 2, person );
		ATermAppl twoSiblingsOrDog = or( twoSiblings, dog );

		kb.addType( robert, twoSiblingsOrDog );
		kb.addType( bill, twoSiblingsOrDog );
		assertTrue( kb.isConsistent() );

		// we want to create clash in the merges caused by the
		// max cardinality so we add different
		ATermAppl[] inds = { mary, chris, victor, john };
		for( int i = 0; i < inds.length - 1; i++ ) {
			for( int j = i + 1; j < inds.length; j++ ) {
				if( kb.getABox().getIndividual( inds[i] ).isSame(
						kb.getABox().getIndividual( inds[j] ) ) ) {
					kb.addDifferent( inds[i], inds[j] );
				}
			}
		}
		assertTrue( kb.isConsistent() );

		kb.addType( bill, not( dog ) );

		assertTrue( kb.isConsistent() );

		kb.removeType( bill, not( dog ) );

		assertTrue( kb.isConsistent() );
	}

	@Test
	public void testMerge() {
		// TODO

		// basic classes
		kb.addClass( person );

		// basic properties
		kb.addObjectProperty( sibling );

		// basic abox
		kb.addIndividual( mary );
		kb.addIndividual( chris );
		kb.addIndividual( victor );
		kb.addIndividual( john );
		kb.addIndividual( bill );

		kb.addPropertyValue( sibling, bill, mary );
		kb.addPropertyValue( sibling, bill, john );
		kb.addPropertyValue( sibling, chris, victor );

		assertTrue( kb.isConsistent() );
		assertFalse( kb.hasPropertyValue( bill, sibling, victor ) );

		kb.addSame( chris, bill );
		assertTrue( kb.isConsistent() );
		assertTrue( kb.hasPropertyValue( bill, sibling, victor ) );

		kb.addDifferent( bill, chris );
		assertFalse( kb.isConsistent() );
	}

	@Test
	public void testMerge3() {
		classes( person, man, dog );
		objectProperties( sibling, owns );
		individuals( mary, chris, victor, john, bill );

		kb.addPropertyValue( sibling, bill, mary );
		kb.addPropertyValue( sibling, bill, john );

		kb.addSubClass( man, some( owns, dog ) );

		assertTrue( kb.isConsistent() );

		assertFalse( kb.getABox().getNode( mary ).isSame( kb.getABox().getNode( john ) ) );

		kb.addType( bill, max( sibling, 1, TOP ) );
		kb.addType( mary, or( man, dog ) );
		kb.addType( chris, or( man, dog ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.getABox().getNode( mary ).isSame( kb.getABox().getNode( john ) ) );

		kb.removeType( bill, max( sibling, 1, TOP ) );
		
		assertTrue( kb.isConsistent() );

		assertFalse( kb.getABox().getNode( mary ).isSame( kb.getABox().getNode( john ) ) );
		assertFalse( kb.getABox().getNode( john ).hasType( man ) || kb.getABox().getNode( john ).hasType( dog ) );
		assertTrue( kb.getABox().getNode( mary ).hasType( man ) || kb.getABox().getNode( mary ).hasType( dog ) );
	}

	@Test
	public void testMerge2() {
		// TODO

		// basic classes
		kb.addClass( person );
		kb.addClass( man );
		kb.addClass( woman );
		kb.addClass( dog );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );

		ATermAppl max = max( sibling, 1, TOP );
		kb.addClass( max );

		// basic abox
		kb.addIndividual( mary );
		kb.addIndividual( chris );
		kb.addIndividual( victor );
		kb.addIndividual( john );
		kb.addIndividual( bill );

		kb.addDisjointClass( man, woman );

		kb.addPropertyValue( sibling, bill, mary );
		kb.addPropertyValue( sibling, bill, john );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		kb.addClass( ownsDog );

		kb.addSubClass( man, ownsDog );

		ATermAppl manOrDog = or( man, dog );
		kb.addClass( manOrDog );
		kb.addType( mary, manOrDog );
		kb.addType( chris, manOrDog );

		kb.addType( mary, woman );

		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertFalse( kb.getABox().getNode( mary ).isSame( kb.getABox().getNode( john ) ) );
		assertTrue( kb.getABox().getNode( mary ).hasType( dog ) );
		assertFalse( kb.getABox().getNode( john ).hasType( dog ) );

		kb.addType( bill, max );

		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertTrue( kb.getABox().getNode( mary ).isSame( kb.getABox().getNode( john ) ) );
		assertTrue( kb.getABox().getNode( john ).getSame().hasType( dog ) );

		kb.removeType( bill, max );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertFalse( kb.getABox().getNode( mary ).isSame( kb.getABox().getNode( john ) ) );
		assertFalse( kb.getABox().getNode( john ).hasType( dog ) );
		assertTrue( kb.getABox().getNode( mary ).hasType( dog ) );
	}

	@Test
	public void testCompletionQueueBackjumping() {

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( victor );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		kb.addClass( ownsDog );
		kb.addSubClass( man, ownsDog );

		ATermAppl allCat = all( owns, cat );
		kb.addClass( allCat );

		ATermAppl catAndMan = and( allCat, man );
		kb.addClass( catAndMan );

		ATermAppl manOrWoman = or( man, woman );
		kb.addClass( manOrWoman );

		kb.addType( robert, manOrWoman );
		kb.addType( victor, manOrWoman );

		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		// assertTrue(kb.getABox().getIndividual(robert).hasRNeighbor(kb.getRBox().getRole(owns)));
		// assertTrue(kb.getABox().getIndividual(victor).hasRNeighbor(kb.getRBox().getRole(owns)));

		kb.addType( victor, not( man ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		// assertTrue(kb.getABox().getIndividual(robert).hasRNeighbor(kb.getRBox().getRole(owns)));
		// assertFalse(kb.getABox().getIndividual(victor).hasRNeighbor(kb.getRBox().getRole(owns)));

		kb.removeType( victor, not( man ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertFalse( kb.getABox().getIndividual( victor ).hasType( not( man ) ) );

		kb.addType( robert, not( man ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertTrue( kb.getABox().getIndividual( robert ).hasType( not( man ) ) );

		kb.removeType( robert, not( man ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertFalse( kb.getABox().getIndividual( robert ).hasType( not( man ) ) );

		kb.addType( robert, not( woman ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		assertTrue( kb.getABox().getIndividual( robert )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );

	}

	@Ignore("The conditions tested here are obviously incorrect, see comment below")
	@Test
	public void testRemoveBranch() {

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( victor );
		kb.addIndividual( mary );
		kb.addIndividual( chris );
		kb.addIndividual( john );
		kb.addIndividual( bill );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		kb.addClass( ownsDog );
		kb.addSubClass( man, ownsDog );

		ATermAppl allCat = all( owns, cat );
		kb.addClass( allCat );

		ATermAppl catAndMan = and( allCat, man );
		kb.addClass( catAndMan );

		ATermAppl manOrWoman = or( man, woman );
		kb.addClass( manOrWoman );

		kb.addType( robert, manOrWoman );
		kb.addType( victor, manOrWoman );
		kb.addType( mary, manOrWoman );
		kb.addType( chris, manOrWoman );
		kb.addType( john, manOrWoman );
		kb.addType( bill, manOrWoman );

		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		
		// FIXME the following condition is obviously incorrect
		// there is no reason for robert to own anything since robert
		// can be woman which has no axiom involving owns 
		assertTrue( kb.getABox().getIndividual( robert )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( victor )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );

		kb.removeType( victor, manOrWoman );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertFalse( kb.getABox().getIndividual( victor )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( robert )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( mary ).hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( chris ).hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( john ).hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( bill ).hasRNeighbor( kb.getRBox().getRole( owns ) ) );

		assertTrue( kb.getABox().getBranches().size() == 5 );

		kb.addType( chris, not( man ) );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}
		assertFalse( kb.getABox().getIndividual( chris ).getTypes().contains( man ) );
		assertFalse( kb.getABox().getIndividual( chris )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertFalse( kb.getABox().getIndividual( victor )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( robert )
				.hasRNeighbor( kb.getRBox().getRole( owns ) ) );
		assertTrue( kb.getABox().getIndividual( mary ).hasRNeighbor( kb.getRBox().getRole( owns ) ) );
	}

	@Test
	public void testUpdatedIndividuals() {

		// basic classes
		kb.addClass( person );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( relative );

		kb.addRange( relative, person );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( victor );
		kb.addIndividual( mary );

		kb.addPropertyValue( relative, robert, mary );
		kb.addPropertyValue( sibling, robert, victor );

		// add specific test case axioms/assertions
		ATermAppl siblingPerson = all( sibling, person );
		kb.addClass( siblingPerson );

		kb.addType( victor, person );
		kb.addType( mary, person );
		kb.addType( robert, siblingPerson );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.getABox().getIndividual( victor ).hasType( person ) );
		assertTrue( kb.getABox().getIndividual( mary ).hasType( person ) );

		kb.removeType( victor, person );
		assertTrue( kb.isConsistent() );

		assertTrue( kb.getABox().getIndividual( victor ).hasType( person ) );

		kb.removeType( mary, person );
		assertTrue( kb.isConsistent() );

		assertTrue( kb.getABox().getIndividual( mary ).hasType( person ) );
	}

	@Test
	public void testClashDependency() {

		// basic classes
		kb.addClass( person );
		kb.addClass( notPerson );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );

		kb.addType( robert, person );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		kb.addType( robert, notPerson );
		assertFalse( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		kb.removeType( robert, notPerson );
		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

	}

	@Test
	public void testBlocking() {

		// basic classes
		kb.addClass( person );
		kb.addClass( not( person ) );
		kb.addDisjointClass( person, not( person ) );

		// basic properties
		kb.addObjectProperty( knows );
		kb.addObjectProperty( ownedBy );
		kb.addObjectProperty( owns );
		kb.addInverseProperty( ownedBy, owns );

		// add specific test case axioms/assertions
		ATermAppl allSPerson = all( knows, not( person ) );
		kb.addClass( allSPerson );
		ATermAppl allRInvallSPerson = all( ownedBy, allSPerson );
		kb.addClass( allRInvallSPerson );
		ATermAppl allRInvallRInvallSPerson = all( ownedBy, allRInvallSPerson );
		kb.addClass( allRInvallRInvallSPerson );
		ATermAppl allRInvallRInvallRInvallSPerson = all( ownedBy, allRInvallRInvallSPerson );
		kb.addClass( allRInvallRInvallRInvallSPerson );

		ATermAppl allRallRInvallRInvallSPerson = all( owns, allRInvallRInvallRInvallSPerson );
		kb.addClass( allRallRInvallRInvallSPerson );

		ATermAppl allRallRallRInvallRInvallSPerson = all( owns, allRallRInvallRInvallSPerson );
		kb.addClass( allRallRallRInvallRInvallSPerson );
		ATermAppl allRallRallRallRInvallRInvallSPerson = all( owns,
				allRallRallRInvallRInvallSPerson );
		kb.addClass( allRallRallRallRInvallRInvallSPerson );
		ATermAppl allSallRallRallRallRInvallRInvallSPerson = all( knows,
				allRallRallRallRInvallRInvallSPerson );
		kb.addClass( allSallRallRallRallRInvallRInvallSPerson );
		ATermAppl neg = not( allSallRallRallRallRInvallRInvallSPerson );
		neg = normalize( neg );
		kb.addClass( neg );
		kb.addClass( man );
		kb.addEquivalentClass( man, neg );

		kb.addClass( woman );
		ATermAppl someWoman = some( owns, woman );
		kb.addClass( someWoman );
		kb.addSubClass( woman, someWoman );

		// add abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( john );
		kb.addType( mary, woman );

		kb.addPropertyValue( knows, robert, mary );
		kb.addPropertyValue( knows, mary, john );

		kb.prepare();

		kb.isConsistent();
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		kb.addType( john, person );
		kb.addType( robert, not( man ) );

		// consistency check
		boolean cons = kb.isConsistent();

		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		// check affected
		assertFalse( cons );

	}

	@Test
	public void testDisjunction3() {
		ATermAppl a = term( "a" );

		ATermAppl p = term( "p" );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		kb.addObjectProperty( p );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		// basic abox
		kb.addIndividual( a );

		// add specific test case axioms/assertions
		kb.addSubClass( C, some( p, E ) );
		kb.addType( a, or( C, D ) );

		// or(C, D)
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );

		// or(C, D), not(C)
		kb.addType( a, not( C ) );
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );

		// or(C, D)
		kb.removeType( a, not( C ) );
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );

		// or(C, D), not(D)
		kb.addType( a, not( D ) );
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );

		// or(C, D), not(D), not(C)
		kb.addType( a, not( C ) );
		assertFalse( kb.isConsistent() );

		// or(C, D), not(C)
		kb.removeType( a, not( D ) );
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );

		// or(C, D)
		kb.removeType( a, not( C ) );
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );

		// or(C,D), not(D)
		kb.addType( a, not( D ) );
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );
	}

	@Test
	public void testBacktracking() {

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( victor );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		kb.addClass( ownsDog );

		kb.addSubClass( man, ownsDog );

		ATermAppl owersAreDogs = all( ownedBy, dog );
		kb.addClass( owersAreDogs );

		ATermAppl owersAreCats = all( ownedBy, cat );
		kb.addClass( owersAreCats );

		kb.addSubClass( dog, owersAreDogs );

		kb.addClass( negate( dog ) );

		ATermAppl manOrDog = or( negate( dog ), woman );
		kb.addClass( manOrDog );

		kb.addType( victor, manOrDog );
		kb.addType( robert, owersAreCats );

		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		kb.addType( victor, man );

		kb.addPropertyValue( ownedBy, robert, mary );

		assertTrue( kb.isConsistent() );

		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		assertTrue( kb.getABox().getIndividual( mary ).hasType( cat ) );

	}

	@Test
	public void testBacktracking3() {

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( victor );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		ATermAppl manOrDog = or( man, dog );
		ATermAppl catOrDog = or( cat, dog );
		ATermAppl manOrDogAndWoman = and( woman, manOrDog );
		ATermAppl catOrDogAndWoman = and( woman, catOrDog );

		ATermAppl bigDisj = or( manOrDogAndWoman, catOrDogAndWoman );

		kb.addType( victor, ownsDog );
		kb.addPropertyValue( owns, victor, robert );
		kb.addType( robert, dog );

		kb.addType( mary, bigDisj );

		assertTrue( kb.isConsistent() );

		kb.removePropertyValue( owns, victor, robert );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.getABox().getIndividual( victor ).getRNeighborEdges(
				kb.getRBox().getRole( owns ) ).size() > 0 );

		kb.addType( mary, not( cat ) );

		assertTrue( kb.isConsistent() );

		assertTrue( kb.getABox().getIndividual( victor ).getRNeighborEdges(
				kb.getRBox().getRole( owns ) ).size() > 0 );
	}

	@Test
	public void testBacktracking2() {

		// basic classes
		kb.addClass( person );
		kb.addClass( dog );
		kb.addClass( cat );
		kb.addClass( man );
		kb.addClass( woman );

		// basic properties
		kb.addObjectProperty( sibling );
		kb.addObjectProperty( owns );
		kb.addObjectProperty( ownedBy );
		kb.addInverseProperty( owns, ownedBy );
		kb.addObjectProperty( knows );

		// basic abox
		kb.addIndividual( robert );
		kb.addIndividual( mary );
		kb.addIndividual( victor );
		kb.addIndividual( chris );
		kb.addIndividual( bill );

		// add specific test case axioms/assertions
		ATermAppl ownsDog = some( owns, dog );
		kb.addClass( ownsDog );

		kb.addSubClass( man, ownsDog );

		ATermAppl owersAreDogs = all( ownedBy, dog );
		kb.addClass( owersAreDogs );

		ATermAppl owersAreCats = all( ownedBy, cat );
		kb.addClass( owersAreCats );
		ATermAppl owersownersAreCats = all( ownedBy, owersAreCats );
		kb.addClass( owersownersAreCats );

		kb.addSubClass( dog, owersAreDogs );

		kb.addClass( negate( dog ) );

		ATermAppl manOrDog = or( negate( dog ), woman );
		kb.addClass( manOrDog );

		kb.addType( victor, manOrDog );

		assertTrue( kb.isConsistent() );
		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		kb.addType( robert, owersownersAreCats );
		kb.addPropertyValue( ownedBy, robert, mary );
		kb.addPropertyValue( ownedBy, mary, chris );

		assertTrue( kb.isConsistent() );

		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		assertTrue( kb.getABox().getIndividual( chris ).hasType( cat ) );

		kb.addType( victor, man );

		// print the abox
		if( PRINT_ABOX ) {

			kb.getABox().printTree();

			System.out.println( "Branches: " + kb.getABox().getBranches() );
		}

		assertTrue( kb.getABox().getIndividual( chris ).hasType( cat ) );
	}

	@Test
	public void testSimpleABoxRemove() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );

		kb.addClass( C );
		kb.addClass( D );

		kb.addIndividual( a );
		kb.addType( a, C );
		kb.addType( a, D );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );

		kb.removeType( a, D );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, C ) );
		assertFalse( kb.isType( a, D ) );
	}

	@Test
	public void testABoxRemovalWithAllValues() {
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		ATermAppl C = term( "C" );

		ATermAppl p = term( "p" );

		kb.addClass( C );

		kb.addObjectProperty( p );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addType( a, all( p, C ) );
		kb.addType( b, C );

		kb.addPropertyValue( p, a, b );

		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( b, C ) );
		assertTrue( kb.hasPropertyValue( a, p, b ) );

		kb.removeType( b, C );

		// nothing changed because all values restriction
		// adds the type back to b
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( b, C ) );
		assertTrue( kb.hasPropertyValue( a, p, b ) );

		kb.removePropertyValue( p, a, b );

		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( b, C ) );
		assertFalse( kb.hasPropertyValue( a, p, b ) );
	}

	@Test
	public void testABoxRemovalWithFunctionality() {
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );

		ATermAppl C = term( "C" );

		ATermAppl p = term( "p" );

		kb.addClass( C );

		kb.addObjectProperty( p );
		kb.addFunctionalProperty( p );

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );

		kb.addType( c, C );

		kb.addPropertyValue( p, a, b );
		kb.addPropertyValue( p, a, c );

		assertTrue( kb.isType( b, C ) );
		assertTrue( kb.isType( c, C ) );
		assertTrue( kb.hasPropertyValue( a, p, b ) );
		assertTrue( kb.hasPropertyValue( a, p, c ) );
		assertTrue( kb.isSameAs( b, c ) );

		// this is a no-op because type is asserted at C
		// functionality still forces b sameAs c
		kb.removeType( b, C );

		assertTrue( kb.isType( b, C ) );
		assertTrue( kb.isType( c, C ) );
		assertTrue( kb.hasPropertyValue( a, p, b ) );
		assertTrue( kb.hasPropertyValue( a, p, c ) );
		assertTrue( kb.isSameAs( b, c ) );

		kb.removePropertyValue( p, a, b );

		assertFalse( kb.isType( b, C ) );
		assertTrue( kb.isType( c, C ) );
		assertFalse( kb.hasPropertyValue( a, p, b ) );
		assertTrue( kb.hasPropertyValue( a, p, c ) );
		assertFalse( kb.isSameAs( b, c ) );
	}

	@Test
	public void testABoxConsistencyChange() {
		ATermAppl a = term( "a" );
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addIndividual( a );
		kb.addType( a, C );
		kb.addType( a, D );
		kb.addType( a, E );

		// C, D, E
		assertTrue( kb.isConsistent() );

		kb.addType( a, not( C ) );

		// C, D, E, not(C)
		assertFalse( kb.isConsistent() );

		kb.removeType( a, E );

		// C, D, not(C)
		assertFalse( kb.isConsistent() );

		kb.removeType( a, C );

		// D, not(C)
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );
		assertFalse( kb.isType( a, E ) );
	}

	@Test
	public void testABoxDoubleConsistencyChange() {
		// This test is know to fail with incremental deletion
		assumeThat( PelletOptions.USE_INCREMENTAL_DELETION, is( false ) );

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addType( a, C );
		kb.addType( b, D );
		kb.addType( b, E );

		// C(a), D(b), E(b)
		assertTrue( kb.isConsistent() );

		kb.addType( a, not( C ) );
		kb.addType( b, not( D ) );

		// C(a), D(b), E(b), -C(a), -D(b)
		assertFalse( kb.isConsistent() );

		kb.removeType( b, E );

		// C(a), D(b), -C(a), -D(b)
		assertFalse( kb.isConsistent() );

		kb.removeType( b, D );

		// C(a), D(b), -C(a)
		assertFalse( kb.isConsistent() );

		kb.removeType( a, C );

		// -C(a), -D(b)
		assertTrue( kb.isConsistent() );
		assertFalse( kb.isType( a, C ) );
		assertFalse( kb.isType( b, D ) );
		assertFalse( kb.isType( b, E ) );
	}
	
	@Test
	public void testABoxAdditionAfterClassification() {
		// check if consistency check will be performed after
		// a classified ABox is changed
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl c = term( "c" );

		kb.addClass( C );
		kb.addClass( D );

		kb.addSubClass( C, D );
		// bogus axiom to make ontology non-EL
		kb.addSubClass( C, or( C, D ) );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
		kb.addIndividual( c );
		
		kb.addType( a, C );
						
		// kb will be in classified state
		kb.classify();
		
		assertTrue( kb.isType( a, D ) );
		assertFalse( kb.isType( b, D ) );
		assertFalse( kb.isType( c, D ) );
		
		// modify ABox
		kb.addType( b, C );
		
		// call consistency check directly
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, D ) );
		assertTrue( kb.isType( b, D ) );
		assertFalse( kb.isType( c, D ) );
		
		// modify kb
		kb.addType( c, C );
		// call prepare first
		kb.prepare();
		// concistency check later
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, D ) );
		assertTrue( kb.isType( b, D ) );
		assertTrue( kb.isType( c, D ) );
	}

	@Test
	public void testTBoxConsistencyChange() {
		// this test requires tracing in order to remove TBox axioms
		// regardless of all other tests
		boolean ut = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;

		ATermAppl a = term( "a" );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );
		ATermAppl F = term( "F" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addIndividual( a );
		kb.addType( a, C );
		kb.addType( a, D );
		kb.addType( a, E );

		// C(a), D(a), E(a)
		assertTrue( kb.isConsistent() );

		kb.addSubClass( C, not( D ) );
		kb.addSubClass( C, F );

		// C(a), D(a), E(a); C [= -D, C [= F
		assertFalse( kb.isConsistent() );

		kb.removeType( a, E );

		// C(a), D(a); C [= -D, C [= F
		assertFalse( kb.isConsistent() );

		kb.addType( a, E );

		// C(a), D(a), E(a); C [= -D, C [= F
		assertFalse( kb.isConsistent() );

		kb.removeType( a, C );

		// D(a), E(a); C [= -D, C[= F
		assertTrue( kb.isConsistent() );
		assertTrue( kb.isType( a, D ) );
		assertTrue( kb.isType( a, not( C ) ) );
		assertFalse( kb.isType( a, C ) );
		assertTrue( kb.isType( a, E ) );

		kb.addType( a, C );

		// C(a), D(a), E(a); C [= -D, C [= F
		assertFalse( kb.isConsistent() );

		kb.removeAxiom( ATermUtils.makeSub( C, F ) );

		// C(a), D(a), E(a); C [= -D
		assertFalse( kb.isConsistent() );

		kb.removeAxiom( ATermUtils.makeSub( C, not( D ) ) );

		// C(a), D(a), E(a);
		assertTrue( kb.isConsistent() );

		PelletOptions.USE_TRACING = ut;
	}
	
	@Test
	public void testClassificationStatus1() {
		// Related to ticket #193
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addSubClass( C, D );
		
		// force expressivity out of EL
		kb.addSubClass( E, or( C, D ) );
						
		assertFalse( kb.isClassified() );
		assertFalse( kb.isRealized() );
		
		kb.getToldTaxonomy();
		kb.addIndividual( term( "a" ) );
		kb.prepare();
		
		assertFalse( kb.isClassified() );
		assertFalse( kb.isRealized() );
	}
	
	@Test
	public void testClassificationStatus1EL() {
		// Same as testClassificationStatus1 but with EL
		// classifier
		KnowledgeBase kb = new KnowledgeBase();
		
		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addSubClass( C, D );
						
		assertFalse( kb.isClassified() );
		assertFalse( kb.isRealized() );
		
		kb.getToldTaxonomy();
		kb.addIndividual( term( "a" ) );
		kb.prepare();
		
		assertFalse( kb.isClassified() );
		assertFalse( kb.isRealized() );
	}
	
	@Test
	public void testClassificationStatus2() {
		// this test case is to verify that KB will update its internal
		// state properly after KB changes and will recompute inferences
		// as necessary
		// IMPORTANT: this test case is written with the current expected
		// behavior of Pellet. it is possible that this behavior will
		// change in the future and this test case can be modified
		// accordingly		
		
		KnowledgeBase kb = new KnowledgeBase();
		Timer classifyTimer = kb.timers.createTimer( "classify" );
		Timer realizeTimer = kb.timers.createTimer( "realize" );

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addSubClass( C, D );
		kb.addEquivalentClass( C, some( p, TOP ) );
		kb.addEquivalentClass( E, some( q, TOP ) );

		kb.addObjectProperty( p );
		kb.addObjectProperty( q );
		kb.addSubProperty( q, p );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addType( a, C );
		kb.addPropertyValue( p, a, b );

		// do the first consistency test
		// ABox: C(a), p(a, b) 
		// TBox: C = some(p, TOP), E = some(q, TOP)
		// RBox: q [= p
		assertTrue( kb.isConsistent() );
		
		// no classification or realization yet
		assertEquals( 0, classifyTimer.getCount() );
		assertEquals( 0, realizeTimer.getCount() );		
		assertFalse( kb.isClassified() );
		assertFalse( kb.isRealized() );
		
		// force realization
		kb.realize();

		// make sure counts are ok
		assertEquals( 1, classifyTimer.getCount() );
		assertEquals( 1, realizeTimer.getCount() );

		// make an ABox change
		kb.addType( b, E );

		// check consistency again
		assertTrue( kb.isConsistent() );
		
		// classification results should remain but realization
		// results are invalidated
		assertTrue( kb.isClassified() );
		assertTrue( !kb.isRealized() );
		
		// force classification with a query
		assertEquals( emptySet(), kb.getEquivalentClasses( C ) );
		
		// verify classification occurred
		assertEquals( 1, classifyTimer.getCount() );
		
		// perform instance retrieval
		assertEquals( singleton( b ), kb.getInstances( E ) );
		
		// verify instance retrieval did not trigger realization 
		assertEquals( 1, realizeTimer.getCount() );

		// query direct instances to force realization
		assertEquals( singleton( b ), kb.getInstances( E, true ) );
		
		// verify realization occurred
		assertEquals( 2, realizeTimer.getCount() );
		
		// make an ABox change causing p = q and as a result C = E
		kb.addSubProperty( p, q );

		// check consistency again
		assertTrue( kb.isConsistent() );
		
		// both classification and realization results are invalidated
		assertTrue( !kb.isClassified() );
		assertTrue( !kb.isRealized() );

		// verify new equivalent property inference
		assertEquals( singleton( q ), kb.getEquivalentProperties( p ) );
		
		// verify new property assertion inference
		assertEquals( singletonList( b ), kb.getPropertyValues( q, a ) );
		
		// nothing so far should have triggered classification or realization
		assertTrue( !kb.isClassified() );
		assertTrue( !kb.isRealized() );

		// verify new equivalent class inference (trigger classification)
		assertEquals( singleton( E ), kb.getEquivalentClasses( C ) );
		
		// verify classification
		assertEquals( 2, classifyTimer.getCount() );

		// verify new instance relation (trigger realization)
		assertEquals( SetUtils.create( a, b ), kb.getInstances( E, true ) );
		
		// verify realization
		assertEquals( 3, realizeTimer.getCount() );
	}
	

	@Test
	public void testCopyKB() {
		// this test case is to verify that when a KB is copied the ABox
		// will be duplicated but TBox and RBox is shared	
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );

		ATermAppl C = term( "C" );
		ATermAppl D = term( "D" );
		ATermAppl E = term( "E" );

		kb.addClass( C );
		kb.addClass( D );
		kb.addClass( E );

		kb.addSubClass( C, D );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addType( a, C );

		// copy before ConsistencyDone
		assertFalse( kb.copy().isConsistencyDone() );
		
		// do the first consistency test
		assertTrue( kb.isConsistent() );

		// create a copy of the KB (note that the copy
		// will have a new ABox but share the TBox and
		// RBox)
		KnowledgeBase copyKB = kb.copy();
		
		// copy should be in consistency done state
		assertTrue( copyKB.isConsistencyDone() );
		// the ABox of copy should be complete
		assertTrue( copyKB.getABox().isComplete() );

		assertTrue( copyKB.isKnownType( a, C ).isTrue() );
		
		// change the copy KB's ABox
		copyKB.addType( b, E );

		// copy should NOT be in ConsistencyDone state anymore
		// but original KB is still in ConsistencyDone state
		assertFalse( copyKB.isConsistencyDone() );
		assertTrue( kb.isConsistencyDone() );
		
		// check consistency of the copyKB
		assertTrue( copyKB.isConsistent() );		
		
		// verify all the inferences in both KB's
		assertTrue( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );
		assertFalse( kb.isType( b, E ) );
		assertTrue( kb.isSubClassOf( C, D ) );
		assertTrue( copyKB.isType( a, C ) );
		assertTrue( copyKB.isType( a, D ) );
		assertTrue( copyKB.isType( b, E ) );
		assertTrue( copyKB.isSubClassOf( C, D ) );
		
		// change the copy KB's ABox
		copyKB.removeType( a, C );

		// copy should NOT be in ConsistencyDone state anymore
		// but original KB is still in ConsistencyDone state
		assertFalse( copyKB.isConsistencyDone() );
		assertTrue( kb.isConsistencyDone() );
		
		// check consistency of the copyKB
		assertTrue( copyKB.isConsistent() );		
		
		// verify all the inferences in both KB's
		assertTrue( kb.isType( a, C ) );
		assertTrue( kb.isType( a, D ) );
		assertFalse( kb.isType( b, E ) );
		assertFalse( copyKB.isType( a, C ) );
		assertFalse( copyKB.isType( a, D ) );
		assertTrue( copyKB.isType( b, E ) );
	}

	
	@Test
	public void testLiteralHasValue() {
		// this test case is to verify the following bug is resolved:
		// 1) an ABox with a hasValue restriction on a literal
		// 2) checking consistency causes literal to be added with
		//    rdfs:Literal (in the buggy version the branch of the
		//    dependency for this type was not NO_BRANCH)
		// 3) a property value is removed
		// 4) a consistency check is performed causing a reset. a
		//    reset operation leaves the literal in the ABox (since
		//    it is a nominal) but removes the rdfs:Literal type 
		//    (since its branch is not NO_BRANCH)
		// 5) there is a literal in the ABox without rdfs:Literal
		//    type invalidating the main assumption about literals
		
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		ATermAppl lit = literal( "lit" );
		
		ATermAppl p = term( "p" );
		ATermAppl q = term( "q" );

		kb.addIndividual( a );
		kb.addIndividual( b );

		kb.addObjectProperty( p );
		kb.addDatatypeProperty( q );
		
		kb.addPropertyValue( p, a, b );
		kb.addType( a, some( q, value( lit ) ) );
		
		kb.ensureConsistency();
		
		assertTrue( kb.getABox().getLiteral( lit ).hasType( Datatypes.LITERAL ) );
		
		kb.removePropertyValue( p, a, b );
		
		kb.ensureConsistency();
		
		assertTrue( kb.getABox().getLiteral( lit ).hasType( Datatypes.LITERAL ) );
	}
	
	@Test
	public void testPrunedNode() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		
		ATermAppl a = term( "a" );
		ATermAppl b = term( "b" );
		
		kb.addClass( A );
		kb.addClass( B );
		
		kb.addIndividual( a );
		kb.addIndividual( b );
		
		kb.addSame( a, b );
		
		assertTrue( kb.isConsistent() );
		
		assertTrue( kb.isSameAs( a, b ) );
		
		kb.addIndividual( a );
		kb.addType( a, A );
		
		kb.addIndividual( b );
		kb.addType( b, B );
		
		assertTrue( kb.isType( a, A ) );
		assertTrue( kb.isType( a, B ) );
		assertTrue( kb.isType( b, A ) );
		assertTrue( kb.isType( b, B ) );
	}
	
	@Test
	public void aboxChangeWithRules() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl A = term( "A" );
		ATermAppl B = term( "B" );
		
		ATermAppl p = term( "p" );
		
		ATermAppl a = term( "a" );
		
		kb.addClass( A );
		kb.addClass( B );
		
		kb.addObjectProperty( p );
		
		kb.addIndividual( a );
		
		kb.addDisjointClass( A, B );
		
		kb.addType( a, A );
		
		AtomIVariable x = new AtomIVariable( "x" );	
		AtomIVariable y = new AtomIVariable( "y" );	
		List<RuleAtom> body = Arrays.<RuleAtom>asList( new IndividualPropertyAtom( p, x, y ) );
		List<RuleAtom> head = Arrays.<RuleAtom>asList( new ClassAtom( B, x ) );
	
		kb.addRule( new Rule( head, body ) );
	
		assertTrue( kb.isConsistent() );
		
		kb.addPropertyValue( p, a, a );
		
		assertFalse( kb.isConsistent() );					
	}
}
