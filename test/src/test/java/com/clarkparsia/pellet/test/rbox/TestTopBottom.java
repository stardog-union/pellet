package com.clarkparsia.pellet.test.rbox;

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_LIT;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.oneOf;
import static com.clarkparsia.pellet.utils.TermFactory.or;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import openllet.aterm.ATermAppl;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;

public class TestTopBottom
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(TestTopBottom.class);
	}

	@Test
	public void bottomDataAssertion()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl x = term("x");
		final ATermAppl y = literal("y");

		kb.addIndividual(x);

		assertFalse(kb.hasPropertyValue(x, BOTTOM_DATA_PROPERTY, y));

		assertTrue(kb.isType(x, not(some(BOTTOM_DATA_PROPERTY, TOP_LIT))));
		assertTrue(kb.isType(x, not(hasValue(BOTTOM_DATA_PROPERTY, y))));

		kb.addPropertyValue(BOTTOM_DATA_PROPERTY, x, y);
		assertFalse(kb.isConsistent());
	}

	@Test
	public void bottomDataDomain()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.hasDomain(BOTTOM_DATA_PROPERTY, BOTTOM));
	}

	@Test
	public void bottomDataFunctional()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isFunctionalProperty(BOTTOM_DATA_PROPERTY));
	}

	@Test
	public void bottomDataRange()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.hasRange(BOTTOM_DATA_PROPERTY, BOTTOM_LIT));
	}

	@Test
	public void bottomDataSuper()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		kb.addDatatypeProperty(p);

		assertTrue(kb.isSubPropertyOf(BOTTOM_DATA_PROPERTY, p));
	}

	@Test
	public void bottomObjectAssertion()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl x = term("x");
		final ATermAppl y = term("y");

		kb.addIndividual(x);
		kb.addIndividual(y);

		assertFalse(kb.hasPropertyValue(x, BOTTOM_OBJECT_PROPERTY, x));
		assertFalse(kb.hasPropertyValue(x, BOTTOM_OBJECT_PROPERTY, y));
		assertFalse(kb.hasPropertyValue(y, BOTTOM_OBJECT_PROPERTY, x));
		assertFalse(kb.hasPropertyValue(y, BOTTOM_OBJECT_PROPERTY, y));

		assertTrue(kb.isType(x, not(some(BOTTOM_OBJECT_PROPERTY, TOP))));
		assertTrue(kb.isType(x, not(hasValue(BOTTOM_OBJECT_PROPERTY, x))));
		assertTrue(kb.isType(x, not(hasValue(BOTTOM_OBJECT_PROPERTY, y))));

		assertTrue(kb.isType(y, not(some(BOTTOM_OBJECT_PROPERTY, TOP))));
		assertTrue(kb.isType(y, not(hasValue(BOTTOM_OBJECT_PROPERTY, x))));
		assertTrue(kb.isType(y, not(hasValue(BOTTOM_OBJECT_PROPERTY, y))));

		kb.addPropertyValue(BOTTOM_OBJECT_PROPERTY, x, y);
		assertFalse(kb.isConsistent());
	}

	@Test
	public void bottomObjectAsymm()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isAsymmetricProperty(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectFunc()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isFunctionalProperty(BOTTOM_OBJECT_PROPERTY));
		assertTrue(kb.getFunctionalProperties().contains(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectInverse()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isInverse(BOTTOM_OBJECT_PROPERTY, BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectInverseManual()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl c = term("_C_");
		final ATermAppl notC = not(c);

		final ATermAppl r = BOTTOM_OBJECT_PROPERTY;
		final ATermAppl test = and(c, or(some(r, all(r, notC)), some(r, all(r, notC))));
		assertFalse(kb.isSatisfiable(test));
	}

	@Test
	public void bottomObjectInvFunc()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isInverseFunctionalProperty(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectIrreflexive()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isIrreflexiveProperty(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectReflexive()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertFalse(kb.isReflexiveProperty(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectSuper()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		kb.addObjectProperty(p);

		assertTrue(kb.isSubPropertyOf(BOTTOM_OBJECT_PROPERTY, p));
	}

	@Test
	public void topEquivalent()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("concrete");
		final ATermAppl r = term("abstract");
		final ATermAppl s = term("universal");

		kb.addObjectProperty(p);
		kb.addObjectProperty(r);
		kb.addObjectProperty(s);
		kb.addSubProperty(p, r);
		kb.addSubProperty(r, s);

		assertTrue(kb.isSubPropertyOf(p, s));
		assertTrue(kb.getSubProperties(s).contains(Collections.singleton(p)));

		kb.addEquivalentProperty(s, TOP_OBJECT_PROPERTY);

		assertTrue(kb.isSubPropertyOf(p, s));
		assertTrue(kb.getSubProperties(s).contains(Collections.singleton(p)));
	}

	@Test
	public void bottomObjectSymm()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isSymmetricProperty(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void bottomObjectTransitive()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isTransitiveProperty(BOTTOM_OBJECT_PROPERTY));
	}

	@Test
	public void topDataAssertion()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl r = term("r");
		final ATermAppl x = term("x");
		final ATermAppl y = literal("y");

		kb.addDatatypeProperty(r);
		kb.addIndividual(x);
		kb.addPropertyValue(r, x, y);

		assertTrue(kb.hasPropertyValue(x, TOP_DATA_PROPERTY, y));
	}

	@Test
	public void topDataFunctional()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertFalse(kb.isFunctionalProperty(TOP_DATA_PROPERTY));
	}

	@Test
	public void topDataSuper()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		kb.addDatatypeProperty(p);

		assertTrue(kb.isSubPropertyOf(p, TOP_DATA_PROPERTY));
	}

	@Test
	public void topObjectAllValues()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl c = term("C");
		final ATermAppl x = term("x");
		final ATermAppl y = term("y");
		final ATermAppl z = term("z");

		kb.addClass(c);
		kb.addIndividual(x);
		kb.addIndividual(y);
		kb.addIndividual(z);
		kb.addDifferent(x, y);
		kb.addDifferent(x, z);

		kb.addSubClass(c, oneOf(x, y));
		kb.addSubClass(TOP, all(TOP_OBJECT_PROPERTY, c));

		assertTrue(kb.isSameAs(y, z));
	}

	@Test
	public void topObjectallValuesFreshInd()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl c = term("_C_");
		final ATermAppl notC = not(c);

		final ATermAppl a = term("a");
		final ATermAppl b = term("b");
		kb.addIndividual(a);
		kb.addType(a, all(TOP_OBJECT_PROPERTY, notC));
		kb.addIndividual(b);
		kb.addType(b, some(TOP_OBJECT_PROPERTY, c));

		assertFalse(kb.isConsistent());

	}

	@Test
	public void topObjectAssertion()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl x = term("x");
		final ATermAppl y = term("y");

		kb.addIndividual(x);
		kb.addIndividual(y);

		assertTrue(kb.hasPropertyValue(x, TOP_OBJECT_PROPERTY, x));
		assertTrue(kb.hasPropertyValue(x, TOP_OBJECT_PROPERTY, y));
		assertTrue(kb.hasPropertyValue(y, TOP_OBJECT_PROPERTY, x));
		assertTrue(kb.hasPropertyValue(y, TOP_OBJECT_PROPERTY, y));
	}

	@Test
	public void topObjectAsymm()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertFalse(kb.isAsymmetricProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectDomain()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl c = term("C");
		final ATermAppl x = term("x");
		final ATermAppl y = term("y");
		final ATermAppl z = term("z");

		kb.addClass(c);
		kb.addIndividual(x);
		kb.addIndividual(y);
		kb.addIndividual(z);
		kb.addDifferent(x, y);
		kb.addDifferent(x, z);

		kb.addSubClass(c, oneOf(x, y));
		kb.addDomain(TOP_OBJECT_PROPERTY, c);

		assertTrue(kb.isSameAs(y, z));
	}

	@Test
	public void topObjectFunc()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertFalse(kb.isFunctionalProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectInverse()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isInverse(TOP_OBJECT_PROPERTY, TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectInverseManual()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl c = term("_C_");
		final ATermAppl notC = not(c);

		final ATermAppl r = TOP_OBJECT_PROPERTY;
		final ATermAppl test = and(c, or(some(r, all(r, notC)), some(r, all(r, notC))));
		assertFalse(kb.isSatisfiable(test));
	}

	@Test
	public void topObjectInvFunc()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertFalse(kb.isInverseFunctionalProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectIrreflexive()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertFalse(kb.isIrreflexiveProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectRange()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl c = term("C");
		final ATermAppl x = term("x");
		final ATermAppl y = term("y");
		final ATermAppl z = term("z");

		kb.addClass(c);
		kb.addIndividual(x);
		kb.addIndividual(y);
		kb.addIndividual(z);
		kb.addDifferent(x, y);
		kb.addDifferent(x, z);

		kb.addSubClass(c, oneOf(x, y));
		kb.addRange(TOP_OBJECT_PROPERTY, c);

		assertTrue(kb.isSameAs(y, z));
	}

	@Test
	public void topObjectReflexive()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isReflexiveProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectSomeValuesBottom()
	{
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addSubClass(TOP, some(TOP_OBJECT_PROPERTY, BOTTOM));

		assertFalse(kb.isConsistent());
	}

	@Test
	public void topObjectSuper()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		kb.addObjectProperty(p);

		assertTrue(kb.isSubPropertyOf(p, TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectSymm()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isSymmetricProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectTransitive()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		assertTrue(kb.isTransitiveProperty(TOP_OBJECT_PROPERTY));
	}

	@Test
	public void topObjectDisjoints()
	{
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl p = term("p");
		final ATermAppl subP = term("subP");
		final ATermAppl q = term("q");

		kb.addObjectProperty(p);
		kb.addObjectProperty(subP);
		kb.addObjectProperty(q);

		kb.addSubProperty(subP, p);
		kb.addDisjointProperty(p, q);

		kb.getRoleTaxonomy(true).getTop().print();
		kb.getRoleTaxonomy(false).getTop().print();

		assertTrue(kb.isDisjointProperty(BOTTOM_OBJECT_PROPERTY, TOP_OBJECT_PROPERTY));
		assertTrue(kb.isDisjointProperty(TOP_OBJECT_PROPERTY, BOTTOM_OBJECT_PROPERTY));

		assertEquals(singletonSets(BOTTOM_OBJECT_PROPERTY), kb.getDisjointProperties(TOP_OBJECT_PROPERTY));
		assertEquals(singletonSets(BOTTOM_OBJECT_PROPERTY, TOP_OBJECT_PROPERTY, p, subP, q), kb.getDisjointProperties(BOTTOM_OBJECT_PROPERTY));
		assertEquals(singletonSets(BOTTOM_OBJECT_PROPERTY, q), kb.getDisjointProperties(p));
		assertEquals(singletonSets(BOTTOM_OBJECT_PROPERTY, p, subP), kb.getDisjointProperties(q));

		assertEquals(singletonSets(BOTTOM_OBJECT_PROPERTY), kb.getDisjointProperties(TOP_OBJECT_PROPERTY, true));
		assertEquals(singletonSets(TOP_OBJECT_PROPERTY), kb.getDisjointProperties(BOTTOM_OBJECT_PROPERTY, true));
		assertEquals(singletonSets(q), kb.getDisjointProperties(p, true));
		assertEquals(singletonSets(p), kb.getDisjointProperties(q, true));

	}

	@SafeVarargs
	public static <T> Set<Set<T>> singletonSets(final T... es)
	{
		final Set<Set<T>> set = new HashSet<>();
		for (final T e : es)
			set.add(Collections.singleton(e));
		return set;
	}
}
