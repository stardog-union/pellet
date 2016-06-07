// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
	public static String _base = "file:" + PelletTestSuite.base + "misc/";

	private static String _ns = "urn:test:";

	private static OWLOntologyManager _manager = OWLManager.createOWLOntologyManager();

	private static OWLDataFactory _factory = _manager.getOWLDataFactory();

	private static OWLClass _c1 = _factory.getOWLClass(IRI.create(_ns + "c1"));

	private static OWLClass _c2 = _factory.getOWLClass(IRI.create(_ns + "c2"));

	private static OWLObjectProperty _op1 = _factory.getOWLObjectProperty(IRI.create(_ns + "op1"));

	private static OWLDataProperty _dp1 = _factory.getOWLDataProperty(IRI.create(_ns + "dp1"));

	private static OWLIndividual _ind1 = _factory.getOWLNamedIndividual(IRI.create(_ns + "ind1"));

	private static OWLIndividual _ind2 = _factory.getOWLNamedIndividual(IRI.create(_ns + "ind2"));

	private static OWLIndividual _ind3 = _factory.getOWLAnonymousIndividual(_ns + "ind3");

	private static OWLLiteral _uc1 = _factory.getOWLLiteral("lit1");

	private static OWLLiteral _uc2 = _factory.getOWLLiteral("lit2", "en");

	private static OWLLiteral _tc1 = _factory.getOWLLiteral("lit3", OWL2Datatype.XSD_STRING);

	private static OWLLiteral _tc2 = _factory.getOWLLiteral("1", OWL2Datatype.XSD_INTEGER);

	private static OWLDatatype _d1 = _factory.getOWLDatatype(IRI.create(_ns + "d1"));

	private final KnowledgeBase _kb = new KnowledgeBase();

	private final PelletVisitor _atermConverter = new PelletVisitor(_kb);

	private final ConceptConverter _owlapiConverter = new ConceptConverter(_kb, _factory);

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(OWLAPIObjectConversionTests.class);
	}

	private void testConversion(final OWLObject object)
	{
		try
		{
			_kb.clear();
			_atermConverter.clear();
			_atermConverter.setAddAxiom(true);

			object.accept(_atermConverter);

			final ATermAppl aterm = _atermConverter.result();

			// for the ATerm->OWLObject translation to work we need
			// all the entities referred in the ATerm to be defined
			// entities. therefore, we add a dummy axiom to the KB
			// that will add the definitions for each entity.
			OWLAxiom axiom = null;
			if (object instanceof OWLClassExpression)
			{
				final OWLClassExpression c = (OWLClassExpression) object;
				axiom = _factory.getOWLSubClassOfAxiom(c, c);
			}
			else
				if (object instanceof OWLObjectProperty)
				{
					final OWLObjectProperty p = (OWLObjectProperty) object;
					axiom = _factory.getOWLSubObjectPropertyOfAxiom(p, p);
				}
				else
					if (object instanceof OWLDataProperty)
					{
						final OWLDataProperty p = (OWLDataProperty) object;
						axiom = _factory.getOWLSubDataPropertyOfAxiom(p, p);
					}
					else
						if (object instanceof OWLIndividual)
						{
							final OWLIndividual ind = (OWLIndividual) object;
							axiom = _factory.getOWLClassAssertionAxiom(_factory.getOWLThing(), ind);
						}
			if (axiom != null)
				axiom.accept(_atermConverter);

			final OWLObject converted = _owlapiConverter.convert(aterm);

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
		testConversion(_c1);
	}

	@Test
	public void testIntersectionOf()
	{
		testConversion(_factory.getOWLObjectIntersectionOf(SetUtils.create(_c1, _c2)));
	}

	@Test
	public void testUnionOf()
	{
		testConversion(_factory.getOWLObjectUnionOf(SetUtils.create(_c1, _c2)));
	}

	@Test
	public void testComplementOf()
	{
		testConversion(_factory.getOWLObjectComplementOf(_c1));
	}

	@Test
	public void testObjectSomeValuesFrom()
	{
		testConversion(_factory.getOWLObjectSomeValuesFrom(_op1, _c1));
		testConversion(_factory.getOWLObjectSomeValuesFrom(_op1.getInverseProperty(), _c1));
	}

	@Test
	public void testObjectAllValuesFrom()
	{
		testConversion(_factory.getOWLObjectAllValuesFrom(_op1, _c1));
		testConversion(_factory.getOWLObjectAllValuesFrom(_op1.getInverseProperty(), _c1));
	}

	/**
	 * {@inheritDoc}
	 */
	public void testObjectValueRestriction()
	{
		testConversion(_factory.getOWLObjectHasValue(_op1, _ind1));
		testConversion(_factory.getOWLObjectHasValue(_op1.getInverseProperty(), _ind1));
	}

	@Test
	public void testObjectMinCardinality()
	{
		testConversion(_factory.getOWLObjectMinCardinality(1, _op1));
		testConversion(_factory.getOWLObjectMinCardinality(1, _op1, _c1));
		testConversion(_factory.getOWLObjectMinCardinality(1, _op1.getInverseProperty(), _c1));
	}

	@Test
	public void testObjectExactCardinality()
	{
		testConversion(_factory.getOWLObjectExactCardinality(1, _op1));
		testConversion(_factory.getOWLObjectExactCardinality(1, _op1, _c1));
		testConversion(_factory.getOWLObjectExactCardinality(1, _op1.getInverseProperty(), _c1));
	}

	@Test
	public void testObjectMaxCardinality()
	{
		testConversion(_factory.getOWLObjectMaxCardinality(1, _op1));
		testConversion(_factory.getOWLObjectMaxCardinality(1, _op1, _c1));
		testConversion(_factory.getOWLObjectMaxCardinality(1, _op1.getInverseProperty(), _c1));
	}

	@Test
	public void testSelfRestriction()
	{
		testConversion(_factory.getOWLObjectHasSelf(_op1));
		testConversion(_factory.getOWLObjectHasSelf(_op1.getInverseProperty()));
	}

	@Test
	public void testObjectOneOf()
	{
		testConversion(_factory.getOWLObjectOneOf(SetUtils.create(_ind1, _ind2)));
	}

	@Test
	public void testDataSomeValuesFrom()
	{
		testConversion(_factory.getOWLDataSomeValuesFrom(_dp1, _d1));
	}

	@Test
	public void testDataAllValuesFrom()
	{
		testConversion(_factory.getOWLDataAllValuesFrom(_dp1, _d1));
	}

	@Test
	public void testDataValueRestriction()
	{
		testConversion(_factory.getOWLDataHasValue(_dp1, _uc1));
		testConversion(_factory.getOWLDataHasValue(_dp1, _uc2));
		testConversion(_factory.getOWLDataHasValue(_dp1, _tc1));
		testConversion(_factory.getOWLDataHasValue(_dp1, _tc2));
	}

	@Test
	public void testDataMinCardinality()
	{
		testConversion(_factory.getOWLDataMinCardinality(1, _dp1, _d1));
	}

	@Test
	public void testDataExactCardinality()
	{
		testConversion(_factory.getOWLDataExactCardinality(1, _dp1, _d1));
	}

	@Test
	public void testDataMaxCardinality()
	{
		testConversion(_factory.getOWLDataMaxCardinality(1, _dp1, _d1));
	}

	@Test
	public void testDataType()
	{
		testConversion(_d1);
	}

	@Test
	public void testDataComplementOf()
	{
		testConversion(_factory.getOWLDataComplementOf(_d1));
	}

	@Test
	public void testDataOneOf()
	{
		testConversion(_factory.getOWLDataOneOf(SetUtils.create(_uc1, _uc2, _tc1, _tc2)));
	}

	@Ignore
	@Test
	public void testDataRangeRestriction()
	{
		// TODO : add a test.
	}

	@Test
	public void testTypedConstant()
	{
		testConversion(_tc1);
		testConversion(_tc2);
	}

	@Test
	public void testUntypedConstant()
	{
		testConversion(_uc1);
		testConversion(_uc2);
	}

	@Test
	public void testObjectProperty()
	{
		testConversion(_op1);
	}

	@Test
	public void testObjectPropertyInverse()
	{
		testConversion(_op1.getInverseProperty());
	}

	@Test
	public void testDataProperty()
	{
		testConversion(_dp1);
	}

	@Test
	public void testIndividual()
	{
		testConversion(_ind1);
	}

	@Test
	public void testAnonymousIndividual()
	{
		testConversion(_ind3);
	}
}
