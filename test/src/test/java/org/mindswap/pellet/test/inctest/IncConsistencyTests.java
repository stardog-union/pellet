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

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
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
import org.mindswap.pellet.KnowledgeBase.ChangeType;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;

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
public class IncConsistencyTests extends AbstractKBTests
{

	@Parameterized.Parameters
	public static Collection<Object[]> getTestCases()
	{
		final ArrayList<Object[]> cases = new ArrayList<>();
		cases.add(new Object[] { false, false, false });
		cases.add(new Object[] { true, false, false });
		cases.add(new Object[] { true, true, false });
		cases.add(new Object[] { true, true, true });
		return cases;
	}

	private boolean preUCQ;
	private boolean preUIC;
	private boolean preUSR;
	private boolean preUT;
	private boolean preUID;

	private final boolean ucq;
	private final boolean uic;
	private final boolean uid;

	private static final boolean PRINT_ABOX = false;

	private final ATermAppl _robert = term("Robert"), //
			_mary = term("Mary"), //
			_chris = term("Chris"), //
			_john = term("John"), //
			_bill = term("Bill"), //
			_victor = term("Victor"), // 
			_mbox = term("mbox"), //
			_relative = term("relative"), //
			_sibling = term("sibling"), //
			_person = term("person"), // 
			_animalOwner = term("animalOwner"),// 
			_owns = term("owns"), //
			_ownedBy = term("ownedBy"), // 
			_knows = term("knows"), //
			_notPerson = not(_person), //
			_man = term("man"), //
			_woman = term("woman"), // 
			_animal = term("animal"), //
			_dog = term("dog"), //
			_cat = term("cat"), //
			_notCat = not(_cat), //
			_notDog = not(_dog), //
			_ssn = term("ssn"), //
			_ownsAnimal = term("ownsAnimal");

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(IncConsistencyTests.class);
	}

	public IncConsistencyTests(final boolean ucq, final boolean uic, final boolean uid)
	{
		this.ucq = ucq;
		this.uic = uic;
		this.uid = uid;
	}

	/**
	 * Verify that differentFrom assertions survive ABox reset
	 */
	@Test
	public void differentAfterReset()
	{

		_kb.addIndividual(_robert);
		_kb.addIndividual(_chris);
		_kb.addDifferent(_robert, _chris);
		_kb.addDatatypeProperty(_ssn);

		final ATermAppl literal = ATermUtils.makePlainLiteral("xxx");
		_kb.addPropertyValue(_ssn, _chris, literal);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDifferentFrom(_robert, _chris));
		assertTrue(_kb.isDifferentFrom(_chris, _robert));

		// ABox property removal should cause ABox reset.
		assertTrue(_kb.removePropertyValue(_ssn, _chris, literal));
		assertTrue(_kb.isChanged(ChangeType.ABOX_DEL));

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isDifferentFrom(_robert, _chris));
		assertTrue(_kb.isDifferentFrom(_chris, _robert));
	}

	/**
	 * Test that _node merge state is correctly handled in reset. In trunk r1495, this is known to cause a NPE because Node._mergeDepends is incorrectly reset
	 * to null
	 */
	@Test
	public void mergeDependsAfterReset()
	{

		_kb.addIndividual(_robert);
		_kb.addIndividual(_chris);
		_kb.addSame(_robert, _chris);
		_kb.addDatatypeProperty(_ssn);
		_kb.addDatatypeProperty(_mbox);

		final ATermAppl literal = ATermUtils.makePlainLiteral("xxx");
		_kb.addPropertyValue(_ssn, _chris, literal);
		_kb.addPropertyValue(_mbox, _chris, literal);

		assertTrue(_kb.isConsistent());
		assertEquals(Bool.TRUE, _kb.hasKnownPropertyValue(_chris, _ssn, literal));
		assertEquals(Bool.TRUE, _kb.hasKnownPropertyValue(_robert, _ssn, literal));

		// ABox property removal should cause ABox reset.
		assertTrue(_kb.removePropertyValue(_mbox, _chris, literal));
		assertTrue(_kb.isChanged(ChangeType.ABOX_DEL));

		assertTrue(_kb.isConsistent());
		assertEquals(Bool.TRUE, _kb.hasKnownPropertyValue(_chris, _ssn, literal));
		assertEquals(Bool.TRUE, _kb.hasKnownPropertyValue(_robert, _ssn, literal));
	}

	@Before
	public void setUp()
	{
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

		_kb.setDoExplanation(PelletOptions.USE_TRACING);
	}

	@After
	public void tearDown() throws Exception
	{
		super.disposeKB();

		PelletOptions.USE_COMPLETION_QUEUE = preUCQ;
		PelletOptions.USE_INCREMENTAL_CONSISTENCY = preUIC;
		PelletOptions.USE_SMART_RESTORE = preUSR;
		PelletOptions.USE_TRACING = preUT;
		PelletOptions.USE_INCREMENTAL_DELETION = preUID;
	}

	@Test
	public void testDisjunction()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_animalOwner);
		_kb.addClass(_animal);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addDisjointClass(_dog, _cat);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addDomain(_sibling, _person);
		_kb.addDatatypeProperty(_ssn);
		_kb.addObjectProperty(_mbox);
		_kb.addObjectProperty(_ownsAnimal);
		_kb.addDomain(_ownsAnimal, _person);
		_kb.addRange(_ownsAnimal, _animal);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_mary);
		final ATermAppl ssn1 = literal("012345678");
		_kb.addPropertyValue(_ssn, _robert, ssn1);

		// add specific test case axioms/assertions
		final ATermAppl catOrDog = or(_dog, _cat);
		_kb.addSubClass(_animal, catOrDog);
		final ATermAppl ownsSomeAnimal = some(_ownsAnimal, _animal);
		_kb.addClass(ownsSomeAnimal);
		_kb.addSubClass(_animalOwner, ownsSomeAnimal);
		final ATermAppl ownsNoCats = all(_ownsAnimal, _notCat);
		final ATermAppl ownsNoDogs = all(_ownsAnimal, _notDog);

		_kb.addType(_robert, ownsSomeAnimal);
		_kb.addType(_mary, ownsSomeAnimal);

		assertTrue(_kb.isConsistent());

		_kb.addType(_mary, ownsNoDogs);
		assertTrue(_kb.isConsistent());

		_kb.addType(_robert, ownsNoDogs);
		assertTrue(_kb.isConsistent());

		_kb.addType(_robert, ownsNoCats);
		assertFalse(_kb.isConsistent());

		_kb.removeType(_robert, ownsNoDogs);
		assertTrue(_kb.isConsistent());

		_kb.addType(_mary, ownsNoCats);
		assertFalse(_kb.isConsistent());

		_kb.removeType(_mary, ownsNoDogs);
		assertTrue(_kb.isConsistent());

		_kb.removeType(_mary, ownsNoCats);
		assertTrue(_kb.isConsistent());

		_kb.addType(_mary, ownsNoDogs);
		assertTrue(_kb.isConsistent());
	}

	@Test
	public void testDisjunction2()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		_kb.addClass(ownsDog);

		_kb.addSubClass(_man, ownsDog);

		final ATermAppl owersAreDogs = all(_ownedBy, _dog);
		_kb.addClass(owersAreDogs);

		_kb.addSubClass(_dog, owersAreDogs);

		final ATermAppl manOrDog = or(_man, _dog);
		_kb.addClass(manOrDog);

		_kb.addType(_robert, manOrDog);
		_kb.addType(_robert, not(_dog));
		assertFalse(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		_kb.removeType(_robert, not(_dog));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		assertTrue(_kb.getABox().getIndividual(_robert).hasType(_dog));

	}

	@Test
	public void testMax()
	{
		// TODO

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);

		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);
		_kb.addIndividual(_chris);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_john);
		_kb.addIndividual(_bill);

		_kb.addPropertyValue(_sibling, _bill, _mary);
		_kb.addPropertyValue(_sibling, _bill, _chris);
		_kb.addPropertyValue(_sibling, _bill, _victor);

		_kb.addPropertyValue(_sibling, _robert, _mary);
		_kb.addPropertyValue(_sibling, _robert, _chris);
		_kb.addPropertyValue(_sibling, _robert, _victor);
		_kb.addPropertyValue(_sibling, _robert, _john);

		// add specific test case axioms/assertions

		final ATermAppl twoSiblings = max(_sibling, 2, _person);
		final ATermAppl twoSiblingsOrDog = or(twoSiblings, _dog);

		_kb.addType(_robert, twoSiblingsOrDog);
		_kb.addType(_bill, twoSiblingsOrDog);
		assertTrue(_kb.isConsistent());

		// we want to create clash in the merges caused by the
		// max cardinality so we add different
		final ATermAppl[] inds = { _mary, _chris, _victor, _john };
		for (int i = 0; i < inds.length - 1; i++)
			for (int j = i + 1; j < inds.length; j++)
				if (_kb.getABox().getIndividual(inds[i]).isSame(_kb.getABox().getIndividual(inds[j])))
					_kb.addDifferent(inds[i], inds[j]);
		assertTrue(_kb.isConsistent());

		_kb.addType(_bill, not(_dog));

		assertTrue(_kb.isConsistent());

		_kb.removeType(_bill, not(_dog));

		assertTrue(_kb.isConsistent());
	}

	@Test
	public void testMerge()
	{
		// TODO

		// basic classes
		_kb.addClass(_person);

		// basic properties
		_kb.addObjectProperty(_sibling);

		// basic _abox
		_kb.addIndividual(_mary);
		_kb.addIndividual(_chris);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_john);
		_kb.addIndividual(_bill);

		_kb.addPropertyValue(_sibling, _bill, _mary);
		_kb.addPropertyValue(_sibling, _bill, _john);
		_kb.addPropertyValue(_sibling, _chris, _victor);

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.hasPropertyValue(_bill, _sibling, _victor));

		_kb.addSame(_chris, _bill);
		assertTrue(_kb.isConsistent());
		assertTrue(_kb.hasPropertyValue(_bill, _sibling, _victor));

		_kb.addDifferent(_bill, _chris);
		assertFalse(_kb.isConsistent());
	}

	@Test
	public void testMerge3()
	{
		classes(_person, _man, _dog);
		objectProperties(_sibling, _owns);
		individuals(_mary, _chris, _victor, _john, _bill);

		_kb.addPropertyValue(_sibling, _bill, _mary);
		_kb.addPropertyValue(_sibling, _bill, _john);

		_kb.addSubClass(_man, some(_owns, _dog));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.getABox().getNode(_mary).isSame(_kb.getABox().getNode(_john)));

		_kb.addType(_bill, max(_sibling, 1, TOP));
		_kb.addType(_mary, or(_man, _dog));
		_kb.addType(_chris, or(_man, _dog));

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getABox().getNode(_mary).isSame(_kb.getABox().getNode(_john)));

		_kb.removeType(_bill, max(_sibling, 1, TOP));

		assertTrue(_kb.isConsistent());

		assertFalse(_kb.getABox().getNode(_mary).isSame(_kb.getABox().getNode(_john)));
		assertFalse(_kb.getABox().getNode(_john).hasType(_man) || _kb.getABox().getNode(_john).hasType(_dog));
		assertTrue(_kb.getABox().getNode(_mary).hasType(_man) || _kb.getABox().getNode(_mary).hasType(_dog));
	}

	@Test
	public void testMerge2()
	{
		// TODO

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_man);
		_kb.addClass(_woman);
		_kb.addClass(_dog);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);

		final ATermAppl max = max(_sibling, 1, TOP);
		_kb.addClass(max);

		// basic _abox
		_kb.addIndividual(_mary);
		_kb.addIndividual(_chris);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_john);
		_kb.addIndividual(_bill);

		_kb.addDisjointClass(_man, _woman);

		_kb.addPropertyValue(_sibling, _bill, _mary);
		_kb.addPropertyValue(_sibling, _bill, _john);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		_kb.addClass(ownsDog);

		_kb.addSubClass(_man, ownsDog);

		final ATermAppl manOrDog = or(_man, _dog);
		_kb.addClass(manOrDog);
		_kb.addType(_mary, manOrDog);
		_kb.addType(_chris, manOrDog);

		_kb.addType(_mary, _woman);

		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertFalse(_kb.getABox().getNode(_mary).isSame(_kb.getABox().getNode(_john)));
		assertTrue(_kb.getABox().getNode(_mary).hasType(_dog));
		assertFalse(_kb.getABox().getNode(_john).hasType(_dog));

		_kb.addType(_bill, max);

		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertTrue(_kb.getABox().getNode(_mary).isSame(_kb.getABox().getNode(_john)));
		assertTrue(_kb.getABox().getNode(_john).getSame().hasType(_dog));

		_kb.removeType(_bill, max);
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertFalse(_kb.getABox().getNode(_mary).isSame(_kb.getABox().getNode(_john)));
		assertFalse(_kb.getABox().getNode(_john).hasType(_dog));
		assertTrue(_kb.getABox().getNode(_mary).hasType(_dog));
	}

	@Test
	public void testCompletionQueueBackjumping()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_victor);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		_kb.addClass(ownsDog);
		_kb.addSubClass(_man, ownsDog);

		final ATermAppl allCat = all(_owns, _cat);
		_kb.addClass(allCat);

		final ATermAppl catAndMan = and(allCat, _man);
		_kb.addClass(catAndMan);

		final ATermAppl manOrWoman = or(_man, _woman);
		_kb.addClass(manOrWoman);

		_kb.addType(_robert, manOrWoman);
		_kb.addType(_victor, manOrWoman);

		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		// assertTrue(_kb.getABox().getIndividual(robert).hasRNeighbor(_kb.getRBox().getRole(owns)));
		// assertTrue(_kb.getABox().getIndividual(victor).hasRNeighbor(_kb.getRBox().getRole(owns)));

		_kb.addType(_victor, not(_man));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		// assertTrue(_kb.getABox().getIndividual(robert).hasRNeighbor(_kb.getRBox().getRole(owns)));
		// assertFalse(_kb.getABox().getIndividual(victor).hasRNeighbor(_kb.getRBox().getRole(owns)));

		_kb.removeType(_victor, not(_man));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertFalse(_kb.getABox().getIndividual(_victor).hasType(not(_man)));

		_kb.addType(_robert, not(_man));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertTrue(_kb.getABox().getIndividual(_robert).hasType(not(_man)));

		_kb.removeType(_robert, not(_man));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertFalse(_kb.getABox().getIndividual(_robert).hasType(not(_man)));

		_kb.addType(_robert, not(_woman));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		assertTrue(_kb.getABox().getIndividual(_robert).hasRNeighbor(_kb.getRBox().getRole(_owns)));

	}

	@Ignore("The conditions tested here are obviously incorrect, see comment below")
	@Test
	public void testRemoveBranch()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_mary);
		_kb.addIndividual(_chris);
		_kb.addIndividual(_john);
		_kb.addIndividual(_bill);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		_kb.addClass(ownsDog);
		_kb.addSubClass(_man, ownsDog);

		final ATermAppl allCat = all(_owns, _cat);
		_kb.addClass(allCat);

		final ATermAppl catAndMan = and(allCat, _man);
		_kb.addClass(catAndMan);

		final ATermAppl manOrWoman = or(_man, _woman);
		_kb.addClass(manOrWoman);

		_kb.addType(_robert, manOrWoman);
		_kb.addType(_victor, manOrWoman);
		_kb.addType(_mary, manOrWoman);
		_kb.addType(_chris, manOrWoman);
		_kb.addType(_john, manOrWoman);
		_kb.addType(_bill, manOrWoman);

		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		// FIXME the following _condition is obviously incorrect
		// there is no reason for robert to own anything since robert
		// can be woman which has no axiom involving owns
		assertTrue(_kb.getABox().getIndividual(_robert).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_victor).hasRNeighbor(_kb.getRBox().getRole(_owns)));

		_kb.removeType(_victor, manOrWoman);
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertFalse(_kb.getABox().getIndividual(_victor).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_robert).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_mary).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_chris).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_john).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_bill).hasRNeighbor(_kb.getRBox().getRole(_owns)));

		assertTrue(_kb.getABox().getBranches().size() == 5);

		_kb.addType(_chris, not(_man));
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}
		assertFalse(_kb.getABox().getIndividual(_chris).getTypes().contains(_man));
		assertFalse(_kb.getABox().getIndividual(_chris).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertFalse(_kb.getABox().getIndividual(_victor).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_robert).hasRNeighbor(_kb.getRBox().getRole(_owns)));
		assertTrue(_kb.getABox().getIndividual(_mary).hasRNeighbor(_kb.getRBox().getRole(_owns)));
	}

	@Test
	public void testUpdatedIndividuals()
	{

		// basic classes
		_kb.addClass(_person);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_relative);

		_kb.addRange(_relative, _person);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_mary);

		_kb.addPropertyValue(_relative, _robert, _mary);
		_kb.addPropertyValue(_sibling, _robert, _victor);

		// add specific test case axioms/assertions
		final ATermAppl siblingPerson = all(_sibling, _person);
		_kb.addClass(siblingPerson);

		_kb.addType(_victor, _person);
		_kb.addType(_mary, _person);
		_kb.addType(_robert, siblingPerson);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getABox().getIndividual(_victor).hasType(_person));
		assertTrue(_kb.getABox().getIndividual(_mary).hasType(_person));

		_kb.removeType(_victor, _person);
		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getABox().getIndividual(_victor).hasType(_person));

		_kb.removeType(_mary, _person);
		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getABox().getIndividual(_mary).hasType(_person));
	}

	@Test
	public void testClashDependency()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_notPerson);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);

		_kb.addType(_robert, _person);
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		_kb.addType(_robert, _notPerson);
		assertFalse(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		_kb.removeType(_robert, _notPerson);
		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

	}

	@Test
	public void testBlocking()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(not(_person));
		_kb.addDisjointClass(_person, not(_person));

		// basic properties
		_kb.addObjectProperty(_knows);
		_kb.addObjectProperty(_ownedBy);
		_kb.addObjectProperty(_owns);
		_kb.addInverseProperty(_ownedBy, _owns);

		// add specific test case axioms/assertions
		final ATermAppl allSPerson = all(_knows, not(_person));
		_kb.addClass(allSPerson);
		final ATermAppl allRInvallSPerson = all(_ownedBy, allSPerson);
		_kb.addClass(allRInvallSPerson);
		final ATermAppl allRInvallRInvallSPerson = all(_ownedBy, allRInvallSPerson);
		_kb.addClass(allRInvallRInvallSPerson);
		final ATermAppl allRInvallRInvallRInvallSPerson = all(_ownedBy, allRInvallRInvallSPerson);
		_kb.addClass(allRInvallRInvallRInvallSPerson);

		final ATermAppl allRallRInvallRInvallSPerson = all(_owns, allRInvallRInvallRInvallSPerson);
		_kb.addClass(allRallRInvallRInvallSPerson);

		final ATermAppl allRallRallRInvallRInvallSPerson = all(_owns, allRallRInvallRInvallSPerson);
		_kb.addClass(allRallRallRInvallRInvallSPerson);
		final ATermAppl allRallRallRallRInvallRInvallSPerson = all(_owns, allRallRallRInvallRInvallSPerson);
		_kb.addClass(allRallRallRallRInvallRInvallSPerson);
		final ATermAppl allSallRallRallRallRInvallRInvallSPerson = all(_knows, allRallRallRallRInvallRInvallSPerson);
		_kb.addClass(allSallRallRallRallRInvallRInvallSPerson);
		ATermAppl neg = not(allSallRallRallRallRInvallRInvallSPerson);
		neg = normalize(neg);
		_kb.addClass(neg);
		_kb.addClass(_man);
		_kb.addEquivalentClass(_man, neg);

		_kb.addClass(_woman);
		final ATermAppl someWoman = some(_owns, _woman);
		_kb.addClass(someWoman);
		_kb.addSubClass(_woman, someWoman);

		// add _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);
		_kb.addIndividual(_john);
		_kb.addType(_mary, _woman);

		_kb.addPropertyValue(_knows, _robert, _mary);
		_kb.addPropertyValue(_knows, _mary, _john);

		_kb.prepare();

		_kb.isConsistent();
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		_kb.addType(_john, _person);
		_kb.addType(_robert, not(_man));

		// consistency check
		final boolean cons = _kb.isConsistent();

		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		// check affected
		assertFalse(cons);

	}

	@Test
	public void testDisjunction3()
	{
		final ATermAppl a = term("a");

		final ATermAppl p = term("p");

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		_kb.addObjectProperty(p);

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addClass(E);

		// basic _abox
		_kb.addIndividual(a);

		// add specific test case axioms/assertions
		_kb.addSubClass(C, some(p, E));
		_kb.addType(a, or(C, D));

		// or(C, D)
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertFalse(_kb.isType(a, D));

		// or(C, D), not(C)
		_kb.addType(a, not(C));
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertTrue(_kb.isType(a, D));

		// or(C, D)
		_kb.removeType(a, not(C));
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertFalse(_kb.isType(a, D));

		// or(C, D), not(D)
		_kb.addType(a, not(D));
		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isType(a, C));
		assertFalse(_kb.isType(a, D));

		// or(C, D), not(D), not(C)
		_kb.addType(a, not(C));
		assertFalse(_kb.isConsistent());

		// or(C, D), not(C)
		_kb.removeType(a, not(D));
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertTrue(_kb.isType(a, D));

		// or(C, D)
		_kb.removeType(a, not(C));
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertFalse(_kb.isType(a, D));

		// or(C,D), not(D)
		_kb.addType(a, not(D));
		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isType(a, C));
		assertFalse(_kb.isType(a, D));
	}

	@Test
	public void testBacktracking()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);
		_kb.addIndividual(_victor);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		_kb.addClass(ownsDog);

		_kb.addSubClass(_man, ownsDog);

		final ATermAppl owersAreDogs = all(_ownedBy, _dog);
		_kb.addClass(owersAreDogs);

		final ATermAppl owersAreCats = all(_ownedBy, _cat);
		_kb.addClass(owersAreCats);

		_kb.addSubClass(_dog, owersAreDogs);

		_kb.addClass(negate(_dog));

		final ATermAppl manOrDog = or(negate(_dog), _woman);
		_kb.addClass(manOrDog);

		_kb.addType(_victor, manOrDog);
		_kb.addType(_robert, owersAreCats);

		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		_kb.addType(_victor, _man);

		_kb.addPropertyValue(_ownedBy, _robert, _mary);

		assertTrue(_kb.isConsistent());

		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		assertTrue(_kb.getABox().getIndividual(_mary).hasType(_cat));

	}

	@Test
	public void testBacktracking3()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);
		_kb.addIndividual(_victor);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		final ATermAppl manOrDog = or(_man, _dog);
		final ATermAppl catOrDog = or(_cat, _dog);
		final ATermAppl manOrDogAndWoman = and(_woman, manOrDog);
		final ATermAppl catOrDogAndWoman = and(_woman, catOrDog);

		final ATermAppl bigDisj = or(manOrDogAndWoman, catOrDogAndWoman);

		_kb.addType(_victor, ownsDog);
		_kb.addPropertyValue(_owns, _victor, _robert);
		_kb.addType(_robert, _dog);

		_kb.addType(_mary, bigDisj);

		assertTrue(_kb.isConsistent());

		_kb.removePropertyValue(_owns, _victor, _robert);

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getABox().getIndividual(_victor).getRNeighborEdges(_kb.getRBox().getRole(_owns)).size() > 0);

		_kb.addType(_mary, not(_cat));

		assertTrue(_kb.isConsistent());

		assertTrue(_kb.getABox().getIndividual(_victor).getRNeighborEdges(_kb.getRBox().getRole(_owns)).size() > 0);
	}

	@Test
	public void testBacktracking2()
	{

		// basic classes
		_kb.addClass(_person);
		_kb.addClass(_dog);
		_kb.addClass(_cat);
		_kb.addClass(_man);
		_kb.addClass(_woman);

		// basic properties
		_kb.addObjectProperty(_sibling);
		_kb.addObjectProperty(_owns);
		_kb.addObjectProperty(_ownedBy);
		_kb.addInverseProperty(_owns, _ownedBy);
		_kb.addObjectProperty(_knows);

		// basic _abox
		_kb.addIndividual(_robert);
		_kb.addIndividual(_mary);
		_kb.addIndividual(_victor);
		_kb.addIndividual(_chris);
		_kb.addIndividual(_bill);

		// add specific test case axioms/assertions
		final ATermAppl ownsDog = some(_owns, _dog);
		_kb.addClass(ownsDog);

		_kb.addSubClass(_man, ownsDog);

		final ATermAppl owersAreDogs = all(_ownedBy, _dog);
		_kb.addClass(owersAreDogs);

		final ATermAppl owersAreCats = all(_ownedBy, _cat);
		_kb.addClass(owersAreCats);
		final ATermAppl owersownersAreCats = all(_ownedBy, owersAreCats);
		_kb.addClass(owersownersAreCats);

		_kb.addSubClass(_dog, owersAreDogs);

		_kb.addClass(negate(_dog));

		final ATermAppl manOrDog = or(negate(_dog), _woman);
		_kb.addClass(manOrDog);

		_kb.addType(_victor, manOrDog);

		assertTrue(_kb.isConsistent());
		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		_kb.addType(_robert, owersownersAreCats);
		_kb.addPropertyValue(_ownedBy, _robert, _mary);
		_kb.addPropertyValue(_ownedBy, _mary, _chris);

		assertTrue(_kb.isConsistent());

		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		assertTrue(_kb.getABox().getIndividual(_chris).hasType(_cat));

		_kb.addType(_victor, _man);

		// print the _abox
		if (PRINT_ABOX)
		{

			_kb.getABox().printTree();

			System.out.println("Branches: " + _kb.getABox().getBranches());
		}

		assertTrue(_kb.getABox().getIndividual(_chris).hasType(_cat));
	}

	@Test
	public void testSimpleABoxRemove()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl a = term("a");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");

		kb.addClass(C);
		kb.addClass(D);

		kb.addIndividual(a);
		kb.addType(a, C);
		kb.addType(a, D);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isType(a, C));
		assertTrue(kb.isType(a, D));

		kb.removeType(a, D);

		assertTrue(kb.isConsistent());
		assertTrue(kb.isType(a, C));
		assertFalse(kb.isType(a, D));
	}

	@Test
	public void testABoxRemovalWithAllValues()
	{
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		final ATermAppl C = term("C");

		final ATermAppl p = term("p");

		_kb.addClass(C);

		_kb.addObjectProperty(p);

		_kb.addIndividual(a);
		_kb.addIndividual(b);

		_kb.addType(a, all(p, C));
		_kb.addType(b, C);

		_kb.addPropertyValue(p, a, b);

		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isType(b, C));
		assertTrue(_kb.hasPropertyValue(a, p, b));

		_kb.removeType(b, C);

		// nothing changed because all values restriction
		// adds the type back to b
		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isType(b, C));
		assertTrue(_kb.hasPropertyValue(a, p, b));

		_kb.removePropertyValue(p, a, b);

		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(b, C));
		assertFalse(_kb.hasPropertyValue(a, p, b));
	}

	@Test
	public void testABoxRemovalWithFunctionality()
	{
		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");

		final ATermAppl C = term("C");

		final ATermAppl p = term("p");

		_kb.addClass(C);

		_kb.addObjectProperty(p);
		_kb.addFunctionalProperty(p);

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addIndividual(c);

		_kb.addType(c, C);

		_kb.addPropertyValue(p, a, b);
		_kb.addPropertyValue(p, a, c);

		assertTrue(_kb.isType(b, C));
		assertTrue(_kb.isType(c, C));
		assertTrue(_kb.hasPropertyValue(a, p, b));
		assertTrue(_kb.hasPropertyValue(a, p, c));
		assertTrue(_kb.isSameAs(b, c));

		// this is a no-op because type is asserted at C
		// functionality still forces b sameAs c
		_kb.removeType(b, C);

		assertTrue(_kb.isType(b, C));
		assertTrue(_kb.isType(c, C));
		assertTrue(_kb.hasPropertyValue(a, p, b));
		assertTrue(_kb.hasPropertyValue(a, p, c));
		assertTrue(_kb.isSameAs(b, c));

		_kb.removePropertyValue(p, a, b);

		assertFalse(_kb.isType(b, C));
		assertTrue(_kb.isType(c, C));
		assertFalse(_kb.hasPropertyValue(a, p, b));
		assertTrue(_kb.hasPropertyValue(a, p, c));
		assertFalse(_kb.isSameAs(b, c));
	}

	@Test
	public void testABoxConsistencyChange()
	{
		final ATermAppl a = term("a");
		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addClass(E);

		_kb.addIndividual(a);
		_kb.addType(a, C);
		_kb.addType(a, D);
		_kb.addType(a, E);

		// C, D, E
		assertTrue(_kb.isConsistent());

		_kb.addType(a, not(C));

		// C, D, E, not(C)
		assertFalse(_kb.isConsistent());

		_kb.removeType(a, E);

		// C, D, not(C)
		assertFalse(_kb.isConsistent());

		_kb.removeType(a, C);

		// D, not(C)
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertTrue(_kb.isType(a, D));
		assertFalse(_kb.isType(a, E));
	}

	@Test
	public void testABoxDoubleConsistencyChange()
	{
		// This test is know to fail with incremental deletion
		assumeThat(PelletOptions.USE_INCREMENTAL_DELETION, is(false));

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addClass(E);

		_kb.addIndividual(a);
		_kb.addIndividual(b);
		_kb.addType(a, C);
		_kb.addType(b, D);
		_kb.addType(b, E);

		// C(a), D(b), E(b)
		assertTrue(_kb.isConsistent());

		_kb.addType(a, not(C));
		_kb.addType(b, not(D));

		// C(a), D(b), E(b), -C(a), -D(b)
		assertFalse(_kb.isConsistent());

		_kb.removeType(b, E);

		// C(a), D(b), -C(a), -D(b)
		assertFalse(_kb.isConsistent());

		_kb.removeType(b, D);

		// C(a), D(b), -C(a)
		assertFalse(_kb.isConsistent());

		_kb.removeType(a, C);

		// -C(a), -D(b)
		assertTrue(_kb.isConsistent());
		assertFalse(_kb.isType(a, C));
		assertFalse(_kb.isType(b, D));
		assertFalse(_kb.isType(b, E));
	}

	@Test
	public void testABoxAdditionAfterClassification()
	{
		// check if consistency check will be performed after
		// a classified ABox is changed

		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl c = term("c");

		kb.addClass(C);
		kb.addClass(D);

		kb.addSubClass(C, D);
		// bogus axiom to make ontology non-EL
		kb.addSubClass(C, or(C, D));

		kb.addIndividual(a);
		kb.addIndividual(b);
		kb.addIndividual(c);

		kb.addType(a, C);

		// _kb will be in classified state
		kb.classify();

		assertTrue(kb.isType(a, D));
		assertFalse(kb.isType(b, D));
		assertFalse(kb.isType(c, D));

		// modify ABox
		kb.addType(b, C);

		// call consistency check directly
		assertTrue(kb.isConsistent());
		assertTrue(kb.isType(a, D));
		assertTrue(kb.isType(b, D));
		assertFalse(kb.isType(c, D));

		// modify _kb
		kb.addType(c, C);
		// call prepare first
		kb.prepare();
		// concistency check later
		assertTrue(kb.isConsistent());
		assertTrue(kb.isType(a, D));
		assertTrue(kb.isType(b, D));
		assertTrue(kb.isType(c, D));
	}

	@Test
	public void testTBoxConsistencyChange()
	{
		// this test requires tracing in _order to remove TBox axioms
		// regardless of all other tests
		final boolean ut = PelletOptions.USE_TRACING;
		PelletOptions.USE_TRACING = true;

		final ATermAppl a = term("a");

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");
		final ATermAppl F = term("F");

		_kb.addClass(C);
		_kb.addClass(D);
		_kb.addClass(E);

		_kb.addIndividual(a);
		_kb.addType(a, C);
		_kb.addType(a, D);
		_kb.addType(a, E);

		// C(a), D(a), E(a)
		assertTrue(_kb.isConsistent());

		_kb.addSubClass(C, not(D));
		_kb.addSubClass(C, F);

		// C(a), D(a), E(a); C [= -D, C [= F
		assertFalse(_kb.isConsistent());

		_kb.removeType(a, E);

		// C(a), D(a); C [= -D, C [= F
		assertFalse(_kb.isConsistent());

		_kb.addType(a, E);

		// C(a), D(a), E(a); C [= -D, C [= F
		assertFalse(_kb.isConsistent());

		_kb.removeType(a, C);

		// D(a), E(a); C [= -D, C[= F
		assertTrue(_kb.isConsistent());
		assertTrue(_kb.isType(a, D));
		assertTrue(_kb.isType(a, not(C)));
		assertFalse(_kb.isType(a, C));
		assertTrue(_kb.isType(a, E));

		_kb.addType(a, C);

		// C(a), D(a), E(a); C [= -D, C [= F
		assertFalse(_kb.isConsistent());

		_kb.removeAxiom(ATermUtils.makeSub(C, F));

		// C(a), D(a), E(a); C [= -D
		assertFalse(_kb.isConsistent());

		_kb.removeAxiom(ATermUtils.makeSub(C, not(D)));

		// C(a), D(a), E(a);
		assertTrue(_kb.isConsistent());

		PelletOptions.USE_TRACING = ut;
	}

	@Test
	public void testClassificationStatus1()
	{
		// Related to ticket #193
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		kb.addClass(C);
		kb.addClass(D);
		kb.addClass(E);

		kb.addSubClass(C, D);

		// force expressivity out of EL
		kb.addSubClass(E, or(C, D));

		assertFalse(kb.isClassified());
		assertFalse(kb.isRealized());

		kb.getToldTaxonomy();
		kb.addIndividual(term("a"));
		kb.prepare();

		assertFalse(kb.isClassified());
		assertFalse(kb.isRealized());
	}

	@Test
	public void testClassificationStatus1EL()
	{
		// Same as testClassificationStatus1 but with EL
		// classifier
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		kb.addClass(C);
		kb.addClass(D);
		kb.addClass(E);

		kb.addSubClass(C, D);

		assertFalse(kb.isClassified());
		assertFalse(kb.isRealized());

		kb.getToldTaxonomy();
		kb.addIndividual(term("a"));
		kb.prepare();

		assertFalse(kb.isClassified());
		assertFalse(kb.isRealized());
	}

	@Test
	public void testClassificationStatus2()
	{
		// this test case is to verify that KB will update its internal
		// state properly after KB changes and will recompute inferences
		// as necessary
		// IMPORTANT: this test case is written with the _current _expected
		// behavior of Pellet. it is possible that this behavior will
		// change in the future and this test case can be modified
		// accordingly		

		final KnowledgeBase kb = new KnowledgeBase();
		final Timer classifyTimer = kb.timers.createTimer("classify");
		final Timer realizeTimer = kb.timers.createTimer("realize");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		final ATermAppl p = term("p");
		final ATermAppl q = term("q");

		kb.addClass(C);
		kb.addClass(D);
		kb.addClass(E);

		kb.addSubClass(C, D);
		kb.addEquivalentClass(C, some(p, TOP));
		kb.addEquivalentClass(E, some(q, TOP));

		kb.addObjectProperty(p);
		kb.addObjectProperty(q);
		kb.addSubProperty(q, p);

		kb.addIndividual(a);
		kb.addIndividual(b);

		kb.addType(a, C);
		kb.addPropertyValue(p, a, b);

		// do the first consistency test
		// ABox: C(a), p(a, b)
		// TBox: C = some(p, TOP), E = some(q, TOP)
		// RBox: q [= p
		assertTrue(kb.isConsistent());

		// no classification or realization yet
		assertEquals(0, classifyTimer.getCount());
		assertEquals(0, realizeTimer.getCount());
		assertFalse(kb.isClassified());
		assertFalse(kb.isRealized());

		// force realization
		kb.realize();

		// make sure counts are ok
		assertEquals(1, classifyTimer.getCount());
		assertEquals(1, realizeTimer.getCount());

		// make an ABox change
		kb.addType(b, E);

		// check consistency again
		assertTrue(kb.isConsistent());

		// classification results should remain but realization
		// results are invalidated
		assertTrue(kb.isClassified());
		assertTrue(!kb.isRealized());

		// force classification with a query
		assertEquals(emptySet(), kb.getEquivalentClasses(C));

		// verify classification occurred
		assertEquals(1, classifyTimer.getCount());

		// perform instance retrieval
		assertEquals(singleton(b), kb.getInstances(E));

		// verify instance retrieval did not trigger realization
		assertEquals(1, realizeTimer.getCount());

		// query direct instances to force realization
		assertEquals(singleton(b), kb.getInstances(E, true));

		// verify realization occurred
		assertEquals(2, realizeTimer.getCount());

		// make an ABox change causing p = q and as a result C = E
		kb.addSubProperty(p, q);

		// check consistency again
		assertTrue(kb.isConsistent());

		// both classification and realization results are invalidated
		assertTrue(!kb.isClassified());
		assertTrue(!kb.isRealized());

		// verify new equivalent property inference
		assertEquals(singleton(q), kb.getEquivalentProperties(p));

		// verify new property assertion inference
		assertEquals(singletonList(b), kb.getPropertyValues(q, a));

		// nothing so far should have triggered classification or realization
		assertTrue(!kb.isClassified());
		assertTrue(!kb.isRealized());

		// verify new equivalent class inference (trigger classification)
		assertEquals(singleton(E), kb.getEquivalentClasses(C));

		// verify classification
		assertEquals(2, classifyTimer.getCount());

		// verify new instance relation (trigger realization)
		assertEquals(SetUtils.create(a, b), kb.getInstances(E, true));

		// verify realization
		assertEquals(3, realizeTimer.getCount());
	}

	@Test
	public void testCopyKB()
	{
		// this test case is to verify that when a KB is copied the ABox
		// will be duplicated but TBox and RBox is shared	

		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl E = term("E");

		kb.addClass(C);
		kb.addClass(D);
		kb.addClass(E);

		kb.addSubClass(C, D);

		kb.addIndividual(a);
		kb.addIndividual(b);

		kb.addType(a, C);

		// copy before ConsistencyDone
		assertFalse(kb.copy().isConsistencyDone());

		// do the first consistency test
		assertTrue(kb.isConsistent());

		// create a copy of the KB (note that the copy
		// will have a new ABox but share the TBox and
		// RBox)
		final KnowledgeBase copyKB = kb.copy();

		// copy should be in consistency done state
		assertTrue(copyKB.isConsistencyDone());
		// the ABox of copy should be complete
		assertTrue(copyKB.getABox().isComplete());

		assertTrue(copyKB.isKnownType(a, C).isTrue());

		// change the copy KB's ABox
		copyKB.addType(b, E);

		// copy should NOT be in ConsistencyDone state anymore
		// but original KB is still in ConsistencyDone state
		assertFalse(copyKB.isConsistencyDone());
		assertTrue(kb.isConsistencyDone());

		// check consistency of the copyKB
		assertTrue(copyKB.isConsistent());

		// verify all the inferences in both KB's
		assertTrue(kb.isType(a, C));
		assertTrue(kb.isType(a, D));
		assertFalse(kb.isType(b, E));
		assertTrue(kb.isSubClassOf(C, D));
		assertTrue(copyKB.isType(a, C));
		assertTrue(copyKB.isType(a, D));
		assertTrue(copyKB.isType(b, E));
		assertTrue(copyKB.isSubClassOf(C, D));

		// change the copy KB's ABox
		copyKB.removeType(a, C);

		// copy should NOT be in ConsistencyDone state anymore
		// but original KB is still in ConsistencyDone state
		assertFalse(copyKB.isConsistencyDone());
		assertTrue(kb.isConsistencyDone());

		// check consistency of the copyKB
		assertTrue(copyKB.isConsistent());

		// verify all the inferences in both KB's
		assertTrue(kb.isType(a, C));
		assertTrue(kb.isType(a, D));
		assertFalse(kb.isType(b, E));
		assertFalse(copyKB.isType(a, C));
		assertFalse(copyKB.isType(a, D));
		assertTrue(copyKB.isType(b, E));
	}

	@Test
	public void testLiteralHasValue()
	{
		// this test case is to verify the following bug is resolved:
		// 1) an ABox with a hasValue restriction on a literal
		// 2) checking consistency causes literal to be added with
		//    rdfs:Literal (in the buggy version the _branch of the
		//    dependency for this type was not NO_BRANCH)
		// 3) a property value is removed
		// 4) a consistency check is performed causing a reset. a
		//    reset operation leaves the literal in the ABox (since
		//    it is a nominal) but removes the rdfs:Literal type
		//    (since its _branch is not NO_BRANCH)
		// 5) there is a literal in the ABox without rdfs:Literal
		//    type invalidating the main assumption about literals

		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		final ATermAppl lit = literal("lit");

		final ATermAppl p = term("p");
		final ATermAppl q = term("q");

		kb.addIndividual(a);
		kb.addIndividual(b);

		kb.addObjectProperty(p);
		kb.addDatatypeProperty(q);

		kb.addPropertyValue(p, a, b);
		kb.addType(a, some(q, value(lit)));

		kb.ensureConsistency();

		assertTrue(kb.getABox().getLiteral(lit).hasType(Datatypes.LITERAL));

		kb.removePropertyValue(p, a, b);

		kb.ensureConsistency();

		assertTrue(kb.getABox().getLiteral(lit).hasType(Datatypes.LITERAL));
	}

	@Test
	public void testPrunedNode()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl A = term("A");
		final ATermAppl B = term("B");

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");

		kb.addClass(A);
		kb.addClass(B);

		kb.addIndividual(a);
		kb.addIndividual(b);

		kb.addSame(a, b);

		assertTrue(kb.isConsistent());

		assertTrue(kb.isSameAs(a, b));

		kb.addIndividual(a);
		kb.addType(a, A);

		kb.addIndividual(b);
		kb.addType(b, B);

		assertTrue(kb.isType(a, A));
		assertTrue(kb.isType(a, B));
		assertTrue(kb.isType(b, A));
		assertTrue(kb.isType(b, B));
	}

	@Test
	public void aboxChangeWithRules()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl A = term("A");
		final ATermAppl B = term("B");

		final ATermAppl p = term("p");

		final ATermAppl a = term("a");

		kb.addClass(A);
		kb.addClass(B);

		kb.addObjectProperty(p);

		kb.addIndividual(a);

		kb.addDisjointClass(A, B);

		kb.addType(a, A);

		final AtomIVariable x = new AtomIVariable("x");
		final AtomIVariable y = new AtomIVariable("y");
		final List<RuleAtom> body = Arrays.<RuleAtom> asList(new IndividualPropertyAtom(p, x, y));
		final List<RuleAtom> head = Arrays.<RuleAtom> asList(new ClassAtom(B, x));

		kb.addRule(new Rule(head, body));

		assertTrue(kb.isConsistent());

		kb.addPropertyValue(p, a, a);

		assertFalse(kb.isConsistent());
	}
}
