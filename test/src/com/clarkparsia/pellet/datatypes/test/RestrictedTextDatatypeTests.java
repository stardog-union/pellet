package com.clarkparsia.pellet.datatypes.test;

import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.DatatypeReasonerImpl;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.datatypes.types.text.RestrictedTextDatatype;

/**
 * <p>
 * Title: Restricted Text Datatype Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link RestrictedTextDatatype}
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author HŽctor PŽrez-Urbina
 */
public class RestrictedTextDatatypeTests {
	private DatatypeReasoner reasoner = new DatatypeReasonerImpl();

	@Test
	public void testXSDString() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.STRING);

		assertTrue(reasoner.isSatisfiable(types));

		// String
		assertTrue(reasoner.isSatisfiable(types, literal("\t")));
		assertTrue(reasoner.isSatisfiable(types, literal("\n")));
		assertTrue(reasoner.isSatisfiable(types, literal("  This is a string  ")));
		assertTrue(reasoner.isSatisfiable(types, literal("This is   a string")));

		// token
		assertTrue(reasoner.isSatisfiable(types, literal("This is a string")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-123456789")));
		assertTrue(reasoner.isSatisfiable(types, literal("12345678-12345678")));

		// language
		assertTrue(reasoner.isSatisfiable(types, literal("a")));
		assertTrue(reasoner.isSatisfiable(types, literal("token")));
		assertTrue(reasoner.isSatisfiable(types, literal("en")));
		assertTrue(reasoner.isSatisfiable(types, literal("FR")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-00")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-12345678")));

		// nmtoken
		assertTrue(reasoner.isSatisfiable(types, literal("-")));
		assertTrue(reasoner.isSatisfiable(types, literal(".")));
		assertTrue(reasoner.isSatisfiable(types, literal("8")));
		assertTrue(reasoner.isSatisfiable(types, literal("\u00B7")));

		// name
		assertTrue(reasoner.isSatisfiable(types, literal(":")));
		assertTrue(reasoner.isSatisfiable(types, literal(":_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("B00A")));
		assertTrue(reasoner.isSatisfiable(types, literal(":aA-.")));

		// ncname
		assertTrue(reasoner.isSatisfiable(types, literal("_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("A8A")));
		assertTrue(reasoner.isSatisfiable(types, literal("aA-.")));
	}

	@Test
	public void testXSDNormalizedString() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.NORMALIZED_STRING);

		assertTrue(reasoner.isSatisfiable(types));

		// String
		assertFalse(reasoner.isSatisfiable(types, literal("\t")));
		assertFalse(reasoner.isSatisfiable(types, literal("\n")));

		// normalizedString
		assertTrue(reasoner.isSatisfiable(types, literal("  This is a string  ")));
		assertTrue(reasoner.isSatisfiable(types, literal("This is   a string")));

		// token
		assertTrue(reasoner.isSatisfiable(types, literal("This is a string")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-123456789")));
		assertTrue(reasoner.isSatisfiable(types, literal("12345678-12345678")));

		// language
		assertTrue(reasoner.isSatisfiable(types, literal("a")));
		assertTrue(reasoner.isSatisfiable(types, literal("token")));
		assertTrue(reasoner.isSatisfiable(types, literal("en")));
		assertTrue(reasoner.isSatisfiable(types, literal("FR")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-00")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-12345678")));

		// nmtoken
		assertTrue(reasoner.isSatisfiable(types, literal("-")));
		assertTrue(reasoner.isSatisfiable(types, literal(".")));
		assertTrue(reasoner.isSatisfiable(types, literal("8")));
		assertTrue(reasoner.isSatisfiable(types, literal("\u00B7")));

		// name
		assertTrue(reasoner.isSatisfiable(types, literal(":")));
		assertTrue(reasoner.isSatisfiable(types, literal(":_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("B00A")));
		assertTrue(reasoner.isSatisfiable(types, literal(":aA-.")));

		// ncname
		assertTrue(reasoner.isSatisfiable(types, literal("_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("A8A")));
		assertTrue(reasoner.isSatisfiable(types, literal("aA-.")));
	}

	@Test
	public void testXSDToken() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.TOKEN);

		assertTrue(reasoner.isSatisfiable(types));

		// String
		assertFalse(reasoner.isSatisfiable(types, literal("\t")));
		assertFalse(reasoner.isSatisfiable(types, literal("\n")));

		// normalizedString
		assertFalse(reasoner.isSatisfiable(types, literal("  This is a string  ")));
		assertFalse(reasoner.isSatisfiable(types, literal("This is   a string")));

		// token
		assertTrue(reasoner.isSatisfiable(types, literal("This is a string")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-123456789")));
		assertTrue(reasoner.isSatisfiable(types, literal("12345678-12345678")));

		// language
		assertTrue(reasoner.isSatisfiable(types, literal("a")));
		assertTrue(reasoner.isSatisfiable(types, literal("token")));
		assertTrue(reasoner.isSatisfiable(types, literal("en")));
		assertTrue(reasoner.isSatisfiable(types, literal("FR")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-00")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-12345678")));

		// nmtoken
		assertTrue(reasoner.isSatisfiable(types, literal("-")));
		assertTrue(reasoner.isSatisfiable(types, literal(".")));
		assertTrue(reasoner.isSatisfiable(types, literal("8")));
		assertTrue(reasoner.isSatisfiable(types, literal("\u00B7")));

		// name
		assertTrue(reasoner.isSatisfiable(types, literal(":")));
		assertTrue(reasoner.isSatisfiable(types, literal(":_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("B00A")));
		assertTrue(reasoner.isSatisfiable(types, literal(":aA-.")));

		// ncname
		assertTrue(reasoner.isSatisfiable(types, literal("_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("A8A")));
		assertTrue(reasoner.isSatisfiable(types, literal("aA-.")));
	}

	@Test
	public void testXSDLanguage() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.LANGUAGE);

		assertTrue(reasoner.isSatisfiable(types));

		// String
		assertFalse(reasoner.isSatisfiable(types, literal("\t")));
		assertFalse(reasoner.isSatisfiable(types, literal("\n")));

		// normalizedString
		assertFalse(reasoner.isSatisfiable(types, literal("  This is a string  ")));
		assertFalse(reasoner.isSatisfiable(types, literal("This is   a string")));

		// token
		assertFalse(reasoner.isSatisfiable(types, literal("This is a string")));
		assertFalse(reasoner.isSatisfiable(types, literal("sp-")));
		assertFalse(reasoner.isSatisfiable(types, literal("EN-123456789")));
		assertFalse(reasoner.isSatisfiable(types, literal("12345678-12345678")));

		// language
		assertTrue(reasoner.isSatisfiable(types, literal("a")));
		assertTrue(reasoner.isSatisfiable(types, literal("token")));
		assertTrue(reasoner.isSatisfiable(types, literal("en")));
		assertTrue(reasoner.isSatisfiable(types, literal("FR")));
		assertTrue(reasoner.isSatisfiable(types, literal("sp-00")));
		assertTrue(reasoner.isSatisfiable(types, literal("EN-12345678")));

		// nmtoken
		assertFalse(reasoner.isSatisfiable(types, literal("-")));
		assertFalse(reasoner.isSatisfiable(types, literal(".")));
		assertFalse(reasoner.isSatisfiable(types, literal("8")));
		assertFalse(reasoner.isSatisfiable(types, literal("\u00B7")));

		// name
		assertFalse(reasoner.isSatisfiable(types, literal(":")));
		assertFalse(reasoner.isSatisfiable(types, literal(":_.")));
		assertFalse(reasoner.isSatisfiable(types, literal("B00A")));
		assertFalse(reasoner.isSatisfiable(types, literal(":aA-.")));

		// ncname
		assertFalse(reasoner.isSatisfiable(types, literal("_.")));
		assertFalse(reasoner.isSatisfiable(types, literal("A8A")));
		assertFalse(reasoner.isSatisfiable(types, literal("aA-.")));
	}

	@Test
	public void testXSDNMToken() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.NMTOKEN);

		assertTrue(reasoner.isSatisfiable(types));

		// nmtoken
		assertTrue(reasoner.isSatisfiable(types, literal("-")));
		assertTrue(reasoner.isSatisfiable(types, literal(".")));
		assertTrue(reasoner.isSatisfiable(types, literal("8")));
		assertTrue(reasoner.isSatisfiable(types, literal("\u00B7")));
		assertTrue(reasoner.isSatisfiable(types, literal(":")));

		assertFalse(reasoner.isSatisfiable(types, literal("")));
		assertFalse(reasoner.isSatisfiable(types, literal(" ")));
		assertFalse(reasoner.isSatisfiable(types, literal(": ")));

	}

	@Test
	public void testXSDName() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.NAME);

		assertTrue(reasoner.isSatisfiable(types));

		// name
		assertTrue(reasoner.isSatisfiable(types, literal(":")));
		assertTrue(reasoner.isSatisfiable(types, literal(":_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("B00A")));
		assertTrue(reasoner.isSatisfiable(types, literal(":aA-.")));

		assertFalse(reasoner.isSatisfiable(types, literal(" ")));
		assertFalse(reasoner.isSatisfiable(types, literal("0:")));
		assertFalse(reasoner.isSatisfiable(types, literal("98w")));

	}

	@Test
	public void testXSDNCName() throws InvalidConstrainingFacetException, InvalidLiteralException,
	                UnrecognizedDatatypeException {
		final Collection<ATermAppl> types = Arrays.asList(Datatypes.NCNAME);

		assertTrue(reasoner.isSatisfiable(types));

		// ncname
		assertTrue(reasoner.isSatisfiable(types, literal("_.")));
		assertTrue(reasoner.isSatisfiable(types, literal("A8A")));
		assertTrue(reasoner.isSatisfiable(types, literal("aA-.")));

		// name
		assertFalse(reasoner.isSatisfiable(types, literal(":")));
		assertFalse(reasoner.isSatisfiable(types, literal(":_.")));
		assertFalse(reasoner.isSatisfiable(types, literal(":B00A")));
		assertFalse(reasoner.isSatisfiable(types, literal(":aA-.")));
	}
}
