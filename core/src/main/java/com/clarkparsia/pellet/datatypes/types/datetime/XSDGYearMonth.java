package com.clarkparsia.pellet.datatypes.types.datetime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.RestrictedDatatype;

/**
 * <p>
 * Title: <code>xsd:gYearMonth</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:gYearMonth</code>
 * datatype. This implementation diverges from the XML Schema specification
 * because
 * <ol>
 * <li>the value space is disjoint from the value space of other timeline based
 * datatypes (e.g., xsd:dateTime)</li>
 * <li>values are treated as points, not as intervals</li>
 * </ol>
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
public class XSDGYearMonth extends AbstractTimelineDatatype {

	private static final XSDGYearMonth	instance;

	static {
		instance = new XSDGYearMonth();
	}

	public static XSDGYearMonth getInstance() {
		return instance;
	}

	private final RestrictedTimelineDatatype	dataRange;

	private XSDGYearMonth() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "gYearMonth" ),
				DatatypeConstants.GYEARMONTH );

		dataRange = new RestrictedTimelineDatatype( this, DatatypeConstants.GYEARMONTH, false );
	}

	public RestrictedDatatype<XMLGregorianCalendar> asDataRange() {
		return dataRange;
	}
}
