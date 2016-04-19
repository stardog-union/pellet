// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import aterm.ATermAppl;
import com.clarkparsia.pellet.owlapi.ConceptConverter;
import com.clarkparsia.pellet.owlapi.PelletVisitor;
import junit.framework.JUnit4TestAdapter;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

/**
 * <p>
 * Title: Conversion Aterm/OwlApi
 * </p>
 * <p>
 * Description: Tests of conversion from Aterm element into individuals classes properties
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
public class OWLAPIObjectConversionTests
{
	public static String base = "file:" + PelletTestSuite.base + "misc/";

	private static String ns = "urn:test:";

	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	private static OWLDataFactory factory = manager.getOWLDataFactory();

	private static OWLClass c1 = factory.getOWLClass(IRI.create(ns + "c1"));

	private static OWLClass c2 = factory.getOWLClass(IRI.create(ns + "c2"));

	private static OWLObjectProperty op1 = factory.getOWLObjectProperty(IRI.create(ns + "op1"));

	private static OWLDataProperty dp1 = factory.getOWLDataProperty(IRI.create(ns + "dp1"));

	private static OWLIndividual ind1 = factory.getOWLNamedIndividual(IRI.create(ns + "ind1"));

	private static OWLIndividual ind2 = factory.getOWLNamedIndividual(IRI.create(ns + "ind2"));

	private static OWLIndividual ind3 = factory.getOWLAnonymousIndividual(ns + "ind3");

	private static OWLLiteral uc1 = factory.getOWLLiteral("lit1");

	private static OWLLiteral uc2 = factory.getOWLLiteral("lit2", "en");

	private static OWLLiteral tc1 = factory.getOWLLiteral("lit3", OWL2Datatype.XSD_STRING);

	private static OWLLiteral tc2 = factory.getOWLLiteral("1", OWL2Datatype.XSD_INTEGER);

	private static OWLDatatype d1 = factory.getOWLDatatype(IRI.create(ns + "d1"));

	private final KnowledgeBase kb = new KnowledgeBase();

	private final PelletVisitor atermConverter = new PelletVisitor(kb);

	private final ConceptConverter owlapiConverter = new ConceptConverter(kb, factory);

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(OWLAPIObjectConversionTests.class);
	}

	private void testConversion(final OWLObject object)
	{
		try
		{
			kb.clear();
			atermConverter.clear();
			atermConverter.setAddAxiom(true);

			object.accept(atermConverter);

			final ATermAppl aterm = atermConverter.result();

			// for the ATerm->OWLObject translation to work we need
			// all the entities referred in the ATerm to be defined
			// entities. therefore, we add a dummy axiom to the KB
			// that will add the definitions for each entity.
			OWLAxiom axiom = null;
			if (object instanceof OWLClassExpression)
			{
				final OWLClassExpression c = (OWLClassExpression) object;
				axiom = factory.getOWLSubClassOfAxiom(c, c);
			}
			else
				if (object instanceof OWLObjectProperty)
				{
					final OWLObjectProperty p = (OWLObjectProperty) object;
					axiom = factory.getOWLSubObjectPropertyOfAxiom(p, p);
				}
				else
					if (object instanceof OWLDataProperty)
					{
						final OWLDataProperty p = (OWLDataProperty) object;
						axiom = factory.getOWLSubDataPropertyOfAxiom(p, p);
					}
					else
						if (object instanceof OWLIndividual)
						{
							final OWLIndividual ind = (OWLIndividual) object;
							axiom = factory.getOWLClassAssertionAxiom(factory.getOWLThing(), ind);
						}
			if (axiom != null)
				axiom.accept(atermConverter);

			final OWLObject converted = owlapiConverter.convert(aterm);

			assertEquals(object, converted);
		}
		catch (final Exception e)
		{
			e.printStackTrace();

			fail("Explanation failed");
		}
	}

	@Test
	public void testClass()
	{
		testConversion(c1);
	}

	@Test
	public void testIntersectionOf()
	{
		testConversion(factory.getOWLObjectIntersectionOf(SetUtils.create(c1, c2)));
	}

	@Test
	public void testUnionOf()
	{
		testConversion(factory.getOWLObjectUnionOf(SetUtils.create(c1, c2)));
	}

	@Test
	public void testComplementOf()
	{
		testConversion(factory.getOWLObjectComplementOf(c1));
	}

	@Test
	public void testObjectSomeValuesFrom()
	{
		testConversion(factory.getOWLObjectSomeValuesFrom(op1, c1));
		testConversion(factory.getOWLObjectSomeValuesFrom(op1.getInverseProperty(), c1));
	}

	@Test
	public void testObjectAllValuesFrom()
	{
		testConversion(factory.getOWLObjectAllValuesFrom(op1, c1));
		testConversion(factory.getOWLObjectAllValuesFrom(op1.getInverseProperty(), c1));
	}

	/**
	 * {@inheritDoc}
	 */
	public void testObjectValueRestriction()
	{
		testConversion(factory.getOWLObjectHasValue(op1, ind1));
		testConversion(factory.getOWLObjectHasValue(op1.getInverseProperty(), ind1));
	}

	@Test
	public void testObjectMinCardinality()
	{
		testConversion(factory.getOWLObjectMinCardinality(1, op1));
		testConversion(factory.getOWLObjectMinCardinality(1, op1, c1));
		testConversion(factory.getOWLObjectMinCardinality(1, op1.getInverseProperty(), c1));
	}

	@Test
	public void testObjectExactCardinality()
	{
		testConversion(factory.getOWLObjectExactCardinality(1, op1));
		testConversion(factory.getOWLObjectExactCardinality(1, op1, c1));
		testConversion(factory.getOWLObjectExactCardinality(1, op1.getInverseProperty(), c1));
	}

	@Test
	public void testObjectMaxCardinality()
	{
		testConversion(factory.getOWLObjectMaxCardinality(1, op1));
		testConversion(factory.getOWLObjectMaxCardinality(1, op1, c1));
		testConversion(factory.getOWLObjectMaxCardinality(1, op1.getInverseProperty(), c1));
	}

	@Test
	public void testSelfRestriction()
	{
		testConversion(factory.getOWLObjectHasSelf(op1));
		testConversion(factory.getOWLObjectHasSelf(op1.getInverseProperty()));
	}

	@Test
	public void testObjectOneOf()
	{
		testConversion(factory.getOWLObjectOneOf(SetUtils.create(ind1, ind2)));
	}

	@Test
	public void testDataSomeValuesFrom()
	{
		testConversion(factory.getOWLDataSomeValuesFrom(dp1, d1));
	}

	@Test
	public void testDataAllValuesFrom()
	{
		testConversion(factory.getOWLDataAllValuesFrom(dp1, d1));
	}

	@Test
	public void testDataValueRestriction()
	{
		testConversion(factory.getOWLDataHasValue(dp1, uc1));
		testConversion(factory.getOWLDataHasValue(dp1, uc2));
		testConversion(factory.getOWLDataHasValue(dp1, tc1));
		testConversion(factory.getOWLDataHasValue(dp1, tc2));
	}

	@Test
	public void testDataMinCardinality()
	{
		testConversion(factory.getOWLDataMinCardinality(1, dp1, d1));
	}

	@Test
	public void testDataExactCardinality()
	{
		testConversion(factory.getOWLDataExactCardinality(1, dp1, d1));
	}

	@Test
	public void testDataMaxCardinality()
	{
		testConversion(factory.getOWLDataMaxCardinality(1, dp1, d1));
	}

	@Test
	public void testDataType()
	{
		testConversion(d1);
	}

	@Test
	public void testDataComplementOf()
	{
		testConversion(factory.getOWLDataComplementOf(d1));
	}

	@Test
	public void testDataOneOf()
	{
		testConversion(factory.getOWLDataOneOf(SetUtils.create(uc1, uc2, tc1, tc2)));
	}

	@Ignore
	@Test
	public void testDataRangeRestriction()
	{
	}

	@Test
	public void testTypedConstant()
	{
		testConversion(tc1);
		testConversion(tc2);
	}

	@Test
	public void testUntypedConstant()
	{
		testConversion(uc1);
		testConversion(uc2);
	}

	@Test
	public void testObjectProperty()
	{
		testConversion(op1);
	}

	@Test
	public void testObjectPropertyInverse()
	{
		testConversion(op1.getInverseProperty());
	}

	@Test
	public void testDataProperty()
	{
		testConversion(dp1);
	}

	@Test
	public void testIndividual()
	{
		testConversion(ind1);
	}

	@Test
	public void testAnonymousIndividual()
	{
		testConversion(ind3);
	}
}
