package com.clarkparsia.pellet.datatypes.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.types.datetime.RestrictedTimelineDatatype;

/**
 * <p>
 * Title: Restricted Timeline Datatype Tests
 * </p>
 * <p>
 * Description: Unit tests for {@link RestrictedTimelineDatatype}
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class RestrictedTimelineDatatypeTests {

	private final static Datatype<XMLGregorianCalendar>	dt;

	static {
		dt = new Datatype<XMLGregorianCalendar>() {

			public RestrictedDatatype<XMLGregorianCalendar> asDataRange() {
				throw new UnsupportedOperationException();
			}

			public ATermAppl getCanonicalRepresentation(ATermAppl literal) {
				throw new UnsupportedOperationException();
			}

			public ATermAppl getLiteral(Object value) {
				throw new UnsupportedOperationException();
			}

			public ATermAppl getName() {
				throw new UnsupportedOperationException();
			}

			public Datatype<?> getPrimitiveDatatype() {
				throw new UnsupportedOperationException();
			}

			public XMLGregorianCalendar getValue(ATermAppl literal) {
				throw new UnsupportedOperationException();
			}

			public boolean isPrimitive() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String toString() {
				return "StubDt";
			}

		};
	}

	private static XMLGregorianCalendar dateTime(String s) {
		return RestrictedTimelineDatatype.getDatatypeFactory().newXMLGregorianCalendar( s );
	}

	/**
	 * Test that intersecting a full number line with the negation of one that
	 * only permits decimals, leaves only rationals
	 */
	@Test
	public void intersectToNZOnly() {
		RestrictedDatatype<XMLGregorianCalendar> dr = new RestrictedTimelineDatatype( dt,
				DatatypeConstants.DATETIME, false );

		assertTrue( dr.contains( dateTime( "2009-01-01T12:00:00Z" ) ) );
		assertTrue( dr.contains( dateTime( "2006-06-01T06:14:23" ) ) );

		dr = dr.intersect( new RestrictedTimelineDatatype( dt, DatatypeConstants.DATETIME, true ),
				true );

		assertFalse( dr.contains( dateTime( "2009-01-01T12:00:00Z" ) ) );
		assertTrue( dr.contains( dateTime( "2006-06-01T06:14:23" ) ) );
	}
}
