
package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;


/**
 * @author Evren Sirin
 */
public class XSDSimpleType extends BaseUnionDatatype {
	public static final XSDSimpleType instance = new XSDSimpleType();

	/**
	 * @param members
	 */
	XSDSimpleType() {
		super(ATermUtils.makeTermAppl("XSDSimpleType"),
		new Datatype[] { 
			XSDDecimal.instance, 
			XSDString.instance, 
			XSDBoolean.instance,
			XSDFloat.instance, 
			XSDDouble.instance,
			XSDYear.instance,
			XSDDateTime.instance,
			XSDDay.instance,
			XSDMonthDay.instance,
			XSDMonth.instance,			
			XSDDate.instance,
			XSDYearMonth.instance,
			XSDTime.instance,
			XSDAnyURI.instance 
		});
	}
}
