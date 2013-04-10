package com.clarkparsia.pellet.datatypes.types.datetime;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import com.clarkparsia.pellet.datatypes.RestrictedDatatype;

/**
 * <p>
 * Title: <code>xsd:gYear</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>xsd:gYear</code> datatype.
 * This implementation diverges from the XML Schema specification because
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
public class XSDGYear extends AbstractTimelineDatatype {

	private static final XSDGYear	instance;

	static {
		instance = new XSDGYear();
	}

	public static XSDGYear getInstance() {
		return instance;
	}

	private final RestrictedTimelineDatatype	dataRange;

	private XSDGYear() {
		super( ATermUtils.makeTermAppl( Namespaces.XSD + "gYear" ), DatatypeConstants.GYEAR );

		dataRange = new RestrictedTimelineDatatype( this, DatatypeConstants.GYEAR, false );
	}

	public RestrictedDatatype<XMLGregorianCalendar> asDataRange() {
		return dataRange;
	}
}
