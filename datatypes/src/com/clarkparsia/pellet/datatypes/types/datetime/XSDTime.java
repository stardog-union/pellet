package com.clarkparsia.pellet.datatypes.types.datetime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.RestrictedDatatype;

/**
 * <p>
 * Title: <code>xsd:time</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:time</code> datatype. This
 * implementation diverges from the XML Schema specification because
 * <ol>
 * <li>the value space is disjoint from the value space of other timeline based
 * datatypes (e.g., xsd:dateTime)</li>
 * <li>values are treated as points on a line that represents a (time zone
 * extended) single day, not as recurring intervals on the infinte datetime
 * timeline</li>
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
public class XSDTime extends AbstractTimelineDatatype {

	private static final XSDTime	instance;

	static {
		instance = new XSDTime();
	}

	public static XSDTime getInstance() {
		return instance;
	}

	private final RestrictedTimelineDatatype	dataRange;

	private XSDTime() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "time" ), DatatypeConstants.TIME );

		dataRange = new RestrictedTimelineDatatype( this, DatatypeConstants.TIME, false );
	}

	public RestrictedDatatype<XMLGregorianCalendar> asDataRange() {
		return dataRange;
	}
}
