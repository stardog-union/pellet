// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.el;

import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.list;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aterm.ATermAppl;
import com.clarkparsia.pellet.el.SimplifiedELClassifier;
import com.clarkparsia.pellet.utils.PropertiesBuilder;
import com.clarkparsia.pellet.utils.TermFactory;
import java.util.Properties;
import junit.framework.JUnit4TestAdapter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.test.AbstractKBTests;
import org.mindswap.pellet.test.PelletTestSuite;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.progress.SilentProgressMonitor;

/**
 * <p>
 * Title:
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
 * @author Evren Sirin
 */
public class ELTests extends AbstractKBTests
{
	private static final ATermAppl[] X = new ATermAppl[5];

	@BeforeClass
	public static void initTerms()
	{
		for (int i = 0; i < X.length; i++)
			X[i] = term("X" + i);
	}

	private final Class<? extends TaxonomyBuilder> builderClass;

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(ELTests.class);
	}

	public ELTests()
	{
		this.builderClass = SimplifiedELClassifier.class;
	}

	public Taxonomy<ATermAppl> getHierarchy()
	{
		assertTrue("Expressivity is not EL", _kb.getExpressivity().isEL());

		TaxonomyBuilder builder = null;
		try
		{
			builder = builderClass.newInstance();
			builder.setKB(_kb);
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
		builder.setProgressMonitor(new SilentProgressMonitor());
		builder.classify();
		final Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

		//		 taxonomy.getTop().print();

		return taxonomy;
	}

	@Test
	public void testEL1()
	{
		classes(_A, _B, _C, _D, _E);
		objectProperties(_p);

		_kb.addSubClass(_A, and(_B, some(_p, _C)));
		_kb.addSubClass(some(_p, ATermUtils.TOP), _D);
		_kb.addSubClass(and(_B, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSubs(_B, true));
	}

	@Test
	public void testEL2()
	{
		classes(_A, _C, _D, _E);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, _C));
		_kb.addSubClass(_C, _D);
		_kb.addSubClass(some(_p, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_D), hierarchy.getSupers(_C, true));
		assertEquals(singletonSets(_E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testEL3a()
	{
		classes(_A, _C, _D, _E, _F);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, _C));
		_kb.addSubClass(_C, _D);
		_kb.addSubClass(_C, _E);
		_kb.addSubClass(some(_p, and(_D, _E)), _F);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_D, _E), hierarchy.getSupers(_C, true));
		assertEquals(singletonSets(_F), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testEL3b()
	{
		classes(_A, _C, _D, _E, _F, _G);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, _C));
		_kb.addSubClass(_C, _D);
		_kb.addSubClass(_C, _E);
		_kb.addSubClass(some(_p, _G), _F);
		_kb.addEquivalentClass(_G, and(_D, _E));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_D, _E), hierarchy.getSupers(_G, true));
		assertEquals(singletonSets(_F), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testEL3c()
	{
		classes(_A, _C, _D, _E);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, and(_C, _D)));
		_kb.addSubClass(some(_p, _C), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testEL4()
	{
		classes(_A, _B, _C, _D, _E);

		_kb.addSubClass(_A, and(_B, _C, _D));
		_kb.addSubClass(and(_C, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSubs(_B, true));
	}

	@Test
	public void testEL5a()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p);

		_kb.addSubClass(and(_A, some(_p, and(some(_p, _B), _C))), _D);
		_kb.addSubClass(_E, _A);
		_kb.addSubClass(_E, _F);
		_kb.addSubClass(_F, some(_p, _G));
		_kb.addSubClass(_G, _C);
		_kb.addSubClass(_G, some(_p, _B));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A, _D, _F), hierarchy.getSupers(_E, true));
	}

	@Test
	public void testEL5b()
	{
		classes(_A, _B, _C, _D, _E, _F, _G, X[1], X[2], X[3]);
		objectProperties(_p);

		_kb.addSubClass(and(_A, X[1]), _D);
		_kb.addEquivalentClass(X[1], some(_p, X[2]));
		_kb.addEquivalentClass(X[2], and(X[3], _C));
		_kb.addEquivalentClass(X[3], some(_p, _B));
		_kb.addSubClass(_E, _A);
		_kb.addSubClass(_E, _F);
		_kb.addSubClass(_F, some(_p, _G));
		_kb.addSubClass(_G, _C);
		_kb.addSubClass(_G, some(_p, _B));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A, _D, _F), hierarchy.getSupers(_E, true));
	}

	@Test
	public void testEL6()
	{
		classes(_A, _B, _C, _D, _E, _G);
		objectProperties(_p);

		_kb.addSubClass(and(_A, some(_p, and(_B, _C))), _D);
		_kb.addSubClass(_E, _A);
		_kb.addSubClass(_E, some(_p, _G));
		_kb.addSubClass(_G, _B);
		_kb.addSubClass(_G, _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A, _D), hierarchy.getSupers(_E, true));
	}

	@Test
	public void testEL7()
	{
		classes(_A, _B, _C, _D, _E);

		_kb.addSubClass(_A, _B);
		_kb.addSubClass(and(_A, _B), and(_C, _D, ATermUtils.TOP));
		_kb.addSubClass(and(_A, _C), _E);
		_kb.addSubClass(and(_A, _D, ATermUtils.TOP), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_B, _C, _D, _E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testEL8()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_B, _C);
		_kb.addSubClass(_C, _D);
		_kb.addSubClass(some(_p, and(_D, ATermUtils.TOP)), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testELNormalization1()
	{
		classes(_A, _B, _C, _D);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, and(_B, _C)));
		_kb.addSubClass(some(_p, and(_C, _B)), _D);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_D), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testELNormalization2()
	{
		classes(_A, _B, _C, _D, _E);

		_kb.addSubClass(_A, and(_B, and(_C, _D)));
		_kb.addSubClass(and(_C, and(_B, _D)), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_B, _C, _D, _E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testELNormalization3()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);

		_kb.addSubClass(_A, and(_B, and(_C, _D, and(_E, _F))));
		_kb.addSubClass(and(and(_C, _F), and(_B, _D, _E)), _G);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_B, _C, _D, _E, _F, _G), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testBottom1()
	{
		classes(_A, _B, _C, _D);

		_kb.addSubClass(_A, ATermUtils.BOTTOM);
		_kb.addSubClass(_C, and(_A, _B));
		_kb.addSubClass(ATermUtils.BOTTOM, _D);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A, _C), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testBottom2()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(some(_p, _B), _C);
		_kb.addSubClass(_C, ATermUtils.BOTTOM);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A, _C), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testTop1()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(ATermUtils.TOP, _A);
		_kb.addSubClass(_C, some(_p, _B));
		_kb.addSubClass(some(_p, _A), _D);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_D), hierarchy.getSupers(_C, true));
	}

	@Test
	public void testBottomWithSome1()
	{
		classes(_A, _B);
		objectProperties(_p);

		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_B, ATermUtils.BOTTOM);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A, _B), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testBottomWithSome2()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(_B, some(_p, _A));
		_kb.addSubClass(_A, ATermUtils.BOTTOM);
		_kb.addSubClass(_C, some(_q, _B));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A, _B, _C), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testDisjoint()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(and(_A, _B), ATermUtils.BOTTOM);
		_kb.addSubClass(_A, _B);
		_kb.addDisjointClass(_C, _D);
		_kb.addEquivalentClass(_C, _D);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A, _C, _D), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testDisjointWithSome1()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(_p, _q);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_A, _D);
		_kb.addSubClass(some(_p, _B), some(_p, _C));
		_kb.addDisjointClass(some(_q, _C), _D);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testDisjointWithSome2()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(_A, some(_p, and(_B, _C)));
		_kb.addDisjointClass(_B, _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testRoles1a()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(_p, _q);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(some(_q, _B), _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_C), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles1b()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(_p, _q);
		_kb.addSubClass(_A, and(_D, some(_p, _B)));
		_kb.addSubClass(and(_D, some(_q, _B)), _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_C, _D), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles2a()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(list(_p, _p), _p);
		_kb.addSubProperty(_q, _p);
		_kb.addSubClass(_A, some(_q, some(_p, _B)));
		_kb.addSubClass(some(_p, _B), _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_C), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles2b()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(list(_p, _q), _p);
		_kb.addSubProperty(_r, _q);
		_kb.addSubClass(_A, and(_F, some(_p, _B)));
		_kb.addSubClass(_B, and(_G, some(_r, _C)));
		_kb.addSubClass(_C, some(_q, _D));
		_kb.addSubClass(some(_p, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E, _F), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles2c()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(list(_p, _q), _p);
		_kb.addSubProperty(list(_p, _q), _r);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_B, some(_q, _C));
		_kb.addSubClass(_C, some(_q, _D));
		_kb.addSubClass(some(_r, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles3a()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(list(_p, _q, _r), _p);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_B, some(_q, _C));
		_kb.addSubClass(_C, some(_r, _D));
		_kb.addSubClass(some(_p, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles3b()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(list(_p, _q, _r), _p);
		_kb.addSubProperty(list(_p, _q, _s), _s);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_B, some(_q, _C));
		_kb.addSubClass(_C, some(_r, _D));
		_kb.addSubClass(_D, some(_q, _E));
		_kb.addSubClass(_E, some(_s, _F));
		_kb.addSubClass(some(_s, _F), _G);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_G), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testRoles4()
	{
		classes(_A, _B, _C, _D, _E, _F, _G, X[0], X[1]);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubProperty(list(_q, _r), _s);
		_kb.addSubProperty(list(_p, _q, _r, _s), _p);
		_kb.addSubProperty(list(_p, _s), _p);
		_kb.addSubClass(X[0], X[1]);
		_kb.addSubClass(_A, and(X[0], some(_p, _B)));
		_kb.addSubClass(_B, and(X[1], some(_q, _C)));
		_kb.addSubClass(_C, and(X[2], some(_r, _D)));
		_kb.addSubClass(_D, and(X[1], some(_s, _E)));
		_kb.addSubClass(and(X[0], some(_p, _E)), _F);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(X[0], _F), hierarchy.getSupers(_A, true));
	}

	@Test
	public void testHeart()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb = new KnowledgeBase();
		final ATermAppl endocardium = term("Endocardium");
		final ATermAppl tissue = term("Tissue");
		final ATermAppl heartWall = term("HeartWall");
		final ATermAppl heartValve = term("HeartValve");
		final ATermAppl bodyWall = term("BodyWall");
		final ATermAppl bodyValve = term("BodyValve");
		final ATermAppl heart = term("Heart");
		final ATermAppl endocarditis = term("Endocarditis");
		final ATermAppl inflammation = term("Inflammation");
		final ATermAppl disease = term("Disease");
		final ATermAppl heartDisease = term("HeartDisease");
		final ATermAppl criticalDisease = term("CriticalDisease");
		final ATermAppl contIn = term("cont-in");
		final ATermAppl partOf = term("part-of");
		final ATermAppl hasLoc = term("has-loc");
		final ATermAppl actsOn = term("acts-on");

		_kb.addClass(endocardium);
		_kb.addClass(tissue);
		_kb.addClass(heartWall);
		_kb.addClass(heartValve);
		_kb.addClass(bodyWall);
		_kb.addClass(bodyValve);
		_kb.addClass(heart);
		_kb.addClass(endocarditis);
		_kb.addClass(inflammation);
		_kb.addClass(disease);
		_kb.addClass(heartDisease);
		_kb.addClass(criticalDisease);
		_kb.addObjectProperty(contIn);
		_kb.addObjectProperty(partOf);
		_kb.addObjectProperty(hasLoc);
		_kb.addObjectProperty(actsOn);

		_kb.addSubClass(endocardium, and(tissue, some(contIn, heartWall), some(contIn, heartValve)));
		_kb.addSubClass(heartWall, and(bodyWall, some(partOf, heart)));
		_kb.addSubClass(heartValve, and(bodyValve, some(partOf, heart)));
		_kb.addSubClass(endocarditis, and(inflammation, some(hasLoc, endocardium)));
		_kb.addSubClass(inflammation, and(disease, some(actsOn, tissue)));
		_kb.addSubClass(and(heartDisease, some(hasLoc, heartValve)), criticalDisease);
		_kb.addEquivalentClass(heartDisease, and(disease, some(hasLoc, heart)));
		_kb.addSubProperty(list(partOf, partOf), partOf);
		_kb.addSubProperty(partOf, contIn);
		_kb.addSubProperty(list(hasLoc, contIn), hasLoc);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(ATermUtils.TOP, inflammation, disease, heartDisease, criticalDisease), hierarchy.getSupers(endocarditis));
	}

	@Test
	public void testDomain1()
	{
		classes(_A, _B);
		objectProperties(_p);

		_kb.addDomain(_p, _A);
		_kb.addSubClass(_B, some(_p, ATermUtils.TOP));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSupers(_B, true));
	}

	@Test
	public void testDomain2()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addDomain(_p, and(_A, _B));
		_kb.addDomain(_p, _C);
		_kb.addSubClass(_B, some(_p, ATermUtils.TOP));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A, _C), hierarchy.getSupers(_B, true));
	}

	@Test
	public void testDomainAbsorption()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addDomain(_p, _A);
		_kb.addSubClass(and(some(_p, _B), some(_p, ATermUtils.TOP)), _C);
		_kb.addSubClass(_E, some(_p, _D));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSupers(_E, true));
	}

	@Test
	public void testDomainBottom()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addDomain(_p, ATermUtils.BOTTOM);
		_kb.addSubClass(_A, some(_p, _B));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testReflexiveRole()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addReflexiveProperty(_p);
		_kb.addRange(_p, _A);
		_kb.addRange(_p, and(_B, _C));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A, _B, _C), hierarchy.getEquivalents(ATermUtils.TOP));
	}

	@Test
	public void testRange1()
	{
		classes(_A, _B, _C, _D);
		objectProperties(_p);

		_kb.addRange(_p, _A);
		_kb.addSubClass(_B, some(_p, _C));
		_kb.addSubClass(some(_p, _A), _D);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_D), hierarchy.getSupers(_B, true));
	}

	@Test
	public void testRange2()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, and(_A, _B));
		_kb.addSubClass(_C, some(_p, _D));
		_kb.addSubClass(some(_p, and(_A, _B)), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_C, true));
	}

	@Test
	public void testRange3()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, and(_A, _B));
		_kb.addSubClass(_C, some(_p, _D));
		_kb.addSubClass(some(_p, _A), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_C, true));
	}

	@Test
	public void testRange5()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, _A);
		_kb.addSubClass(_B, and(_A, some(_p, _C)));
		_kb.addSubClass(_C, _A);
		_kb.addEquivalentClass(_D, some(_p, _C));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_B, _C), hierarchy.getSubs(_A, true));
	}

	@Test
	public void testDomainNormalization1()
	{
		classes(_A, _B, _C, _D, _E, _F, _G, X[1]);
		objectProperties(_p, _q, _r, _s);

		_kb.addDomain(_p, some(_q, _C));
		_kb.addDomain(_p, and(_B, _C));
		_kb.addSubClass(_D, some(_p, X[1]));
		_kb.addSubClass(some(_q, _C), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_B, _C, _E), hierarchy.getSupers(_D, true));
	}

	@Test
	public void testRangeNormalization1()
	{
		classes(_A, _B, _C, _D, _E, _F, _G, X[1]);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, _A);
		_kb.addRange(_p, and(_B, _C));
		_kb.addSubClass(_D, some(_p, X[1]));
		_kb.addSubClass(some(_p, and(and(_A, _B), _C)), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_E), hierarchy.getSupers(_D, true));
	}

	@Test
	public void testRangeNormalization2()
	{
		classes(_A, _B, _C, _D, _E, _F, _G, X[0]);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, some(_q, _A));
		_kb.addSubClass(_B, some(_p, X[0]));
		_kb.addSubClass(some(_p, some(_q, _A)), _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_C), hierarchy.getSupers(_B, true));
	}

	@Test
	public void testDomainAndRange()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, _A);
		_kb.addDomain(_q, _B);
		_kb.addSubClass(_C, some(_p, ATermUtils.TOP));
		_kb.addSubClass(some(_p, _A), some(_q, ATermUtils.TOP));

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_B), hierarchy.getSupers(_C, true));
	}

	@Test
	public void testRange4()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, _C);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(and(_B, _C), _D);
		_kb.addSubClass(some(_p, _D), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSubs(_E, true));
	}

	@Test
	public void testSomeConjunction()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(_A, some(_p, and(_B, _C, _D)));
		_kb.addSubClass(some(_p, and(_B, _C)), _E);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSubs(_E, true));
	}

	@Test
	public void testDisjointRange()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, _C);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addDisjointClass(_B, _C);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testDisjointRangeSuper()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addRange(_p, _C);
		_kb.addSubClass(_A, some(_p, _B));
		_kb.addSubClass(_B, _D);
		_kb.addDisjointClass(_D, _C);
		_kb.addSubClass(_A, _E);
		_kb.addSubClass(_B, _F);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(SetUtils.create(_A), hierarchy.getEquivalents(ATermUtils.BOTTOM));
	}

	@Test
	public void testTicket424()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		final KBLoader loader = new JenaLoader();

		final KnowledgeBase kb = loader.createKB(new String[] { "file:" + PelletTestSuite.base + "misc/ticket-424-test-case.owl" });

		final Taxonomy<ATermAppl> toldTaxonomy = kb.getToldTaxonomy();

		try
		{

			for (final ATermAppl aTerm : kb.getClasses())
			{

				assertNotNull(toldTaxonomy.getNode(aTerm));

				toldTaxonomy.getFlattenedSubs(TermFactory.TOP, false);

			}
		}
		catch (final NullPointerException e)
		{
			fail("Caught NullPointerException when querying the told taxonomy: ticket #424");
		}
	}

	@Test
	public void testTicket465()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		_kb.addSubClass(_B, _A);
		_kb.addSubClass(_C, _B);
		_kb.addSubClass(_F, _C);
		_kb.addSubClass(_F, some(_p, and(some(_r, _G), _E)));
		_kb.addEquivalentClass(_D, and(some(_q, _E), _A));
		_kb.addSubProperty(_p, _q);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_F), hierarchy.getSubs(_D, true));
	}

	@Test
	public void testNestedSubProperty()
	{
		classes(_A, _B, _C);
		objectProperties(_p, _q, _r);

		_kb.addEquivalentClass(_A, some(_p, some(_q, _C)));
		_kb.addEquivalentClass(_B, some(_p, some(_r, _C)));
		_kb.addSubProperty(_q, _r);

		final Taxonomy<ATermAppl> hierarchy = getHierarchy();

		assertEquals(singletonSets(_A), hierarchy.getSubs(_B, true));
	}

	/**
	 * Tests to verify whether PelletOptions.DISABLE_EL_CLASSIFIER = false is respected. (Ticket #461)
	 */
	@Test
	public void testELClassifierEnabled()
	{
		classes(_A, _B, _C, _D, _E, _F, _G);
		objectProperties(_p, _q, _r, _s);

		final boolean savedValue = PelletOptions.DISABLE_EL_CLASSIFIER;

		try
		{
			PelletOptions.DISABLE_EL_CLASSIFIER = false;

			final KBLoader loader = new JenaLoader();

			final KnowledgeBase kb = loader.createKB(new String[] { "file:" + PelletTestSuite.base + "misc/ticket-424-test-case.owl" });

			assertEquals(SimplifiedELClassifier.class, kb.getTaxonomyBuilder().getClass());
		}
		finally
		{
			PelletOptions.DISABLE_EL_CLASSIFIER = savedValue;
		}
	}

	/**
	 * Tests to verify whether PelletOptions.DISABLE_EL_CLASSIFIER = true is respected. (Ticket #461)
	 */
	@Test
	public void testELClassifierDisabled()
	{
		final boolean savedValue = PelletOptions.DISABLE_EL_CLASSIFIER;

		try
		{
			PelletOptions.DISABLE_EL_CLASSIFIER = true;

			final KBLoader loader = new JenaLoader();

			final KnowledgeBase kb = loader.createKB(new String[] { "file:" + PelletTestSuite.base + "misc/ticket-424-test-case.owl" });

			assertFalse(SimplifiedELClassifier.class.equals(kb.getTaxonomyBuilder().getClass()));
		}
		finally
		{
			PelletOptions.DISABLE_EL_CLASSIFIER = savedValue;
		}
	}

	/**
	 * Tests whether PelletOptions.DISABLE_EL_CLASSIFIER can be properly read from a properties file
	 */
	@Test
	public void testDisableELClassifierOptionRead()
	{
		Properties newOptions = new PropertiesBuilder().set("DISABLE_EL_CLASSIFIER", "true").build();
		final Properties savedOptions = PelletOptions.setOptions(newOptions);

		try
		{
			assertTrue(PelletOptions.DISABLE_EL_CLASSIFIER);

			newOptions = new PropertiesBuilder().set("DISABLE_EL_CLASSIFIER", "false").build();
			PelletOptions.setOptions(newOptions);

			assertFalse(PelletOptions.DISABLE_EL_CLASSIFIER);
		}
		finally
		{
			PelletOptions.setOptions(savedOptions);
		}
	}

	@Test
	public void testELExpressivityAnonymousInverseRestriction()
	{
		classes(_C, _D);
		objectProperties(_p);

		_kb.addSubClass(_C, some(inv(_p), _D));

		assertFalse(_kb.getExpressivity().isEL());

		assertFalse(SimplifiedELClassifier.class.equals(_kb.getTaxonomyBuilder().getClass()));
	}

	@Test
	public void testELExpressivityAnonymousInverseChain()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addSubProperty(list(_p, inv(_q)), _r);
		_kb.addSubClass(_C, some(_p, _D));

		assertFalse(_kb.getExpressivity().isEL());

		assertFalse(SimplifiedELClassifier.class.equals(_kb.getTaxonomyBuilder().getClass()));
	}
}
