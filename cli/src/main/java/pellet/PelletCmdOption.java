// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

/**
 * <p>
 * Title: PelletCmdOption
 * </p>
 * <p>
 * Description: Represents a pellet command line option, i.e. the option name,
 * the long option name and the option value given on command line
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class PelletCmdOption {

	private String				longOption;
	private String				shortOption;
	private String				type;
	private String				description;
	private boolean				isMandatory;
	private Object				value;
	private Object				defaultValue;
	private boolean				exists;
	private PelletCmdOptionArg	arg	= PelletCmdOptionArg.NONE;

	public PelletCmdOption(String longOption) {
		if( longOption == null )
			throw new PelletCmdException(
					"A long option must be defined for a command line option" );

		this.longOption = removeHyphen( longOption );
		this.defaultValue = null;
	}

	public String getShortOption() {
		return shortOption;
	}

	public String getLongOption() {
		return longOption;
	}

	public void setShortOption(String shortOption) {
		this.shortOption = removeHyphen( shortOption );
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public Object getValue() {
		return value;
	}

	public String getValueAsString() {
		if( value != null )
			return value.toString();

		if( defaultValue != null )
			return defaultValue.toString();

		return null;
	}

	/**
	 * Returns the option value as an integer and verifies that the value is a
	 * positive integer (>= 1).
	 * 
	 * @return an integer value
	 * @throws PelletCmdException
	 *             If the option value does not exist or is a not a valid
	 *             positive integer value (>= 1)
	 */
	public int getValueAsPositiveInteger() throws PelletCmdException {
		return getValueAsInteger( 1, Integer.MAX_VALUE );
	}

	/**
	 * Returns the option value as an integer and verifies that the value is a
	 * non-negative integer (>= 0).
	 * 
	 * @return an integer value
	 * @throws PelletCmdException
	 *             If the option value does not exist or is a not a valid
	 *             non-negative integer value (>= 0)
	 */
	public int getValueAsNonNegativeInteger() throws PelletCmdException {
		return getValueAsInteger( 0, Integer.MAX_VALUE );
	}
	
	/**
	 * Returns the option value as an integer
	 * 
	 * @return an integer value
	 * @throws PelletCmdException
	 *             If the option value does not exist or is a not a valid
	 *             integer value
	 */
	public int getValueAsInteger() throws PelletCmdException {
		return getValueAsInteger( Integer.MIN_VALUE, Integer.MAX_VALUE );
	}

	/**
	 * Returns the option value as an integer and verifies that it is in the
	 * given range.
	 * 
	 * @param minAllowed
	 *            Minimum allowed value for the integer (inclusive)
	 * @param maxAllowed
	 *            Maximum allowed value for the integer (inclusive)
	 * @return an integer value in the specified range
	 * @throws PelletCmdException
	 *             If the option value does not exist, is a not a valid integer
	 *             value, or not in the specified range
	 */
	public int getValueAsInteger(int minAllowed, int maxAllowed) throws PelletCmdException {
		String value = getValueAsString();

		if( value == null ) {
			throw new PelletCmdException( String.format(
					"The value for option <%s> does not exist%n", longOption ) );
		}
		
		try {
			int intValue = Integer.parseInt( value );
			if( intValue < minAllowed )
				throw new PelletCmdException(
						String
								.format(
										"The value for option <%s> should be greater than or equal to %d but was: %d%n",
										longOption, minAllowed, intValue ) );	

			if( intValue > maxAllowed )
				throw new PelletCmdException(
						String
								.format(
										"The value for option <%s> should be less than or equal to %d but was: %d%n",
										longOption, maxAllowed, intValue ) );
			return intValue;
		} catch( NumberFormatException e ) {
			throw new PelletCmdException( String
					.format( "The value for option <%s> is not a valid integer: %s%n",
							longOption, value ) );
		}		
	}

	/**
	 * Returns the string value as a boolean. If no value exists returns <code>false</code> by
	 * default.
	 * 
	 * @return returns the string value as a boolean
	 */
	public boolean getValueAsBoolean() {
		String value = getValueAsString();

		return Boolean.parseBoolean( value );
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	@Override
	public boolean equals(Object o) {
		if( !(o instanceof PelletCmdOption) )
			return false;

		PelletCmdOption other = (PelletCmdOption) o;

		if( ( ( shortOption == null && other.getShortOption() == null ) 
					|| ( shortOption != null && shortOption.equals( other.getShortOption() ) ) )
				&& longOption.equals( other.getLongOption() )
				&& ( ( type == null && other.getType() == null) || 
					( type != null && type.equals( other.getType() ) ) )
				&& ( ( description == null && other.getDescription() == null ) 
					|| ( description != null && description.equals( other.getDescription() ) ) )
				&& isMandatory == other.isMandatory()
				&& ( ( value == null && other.getValue() == null ) 
					|| ( value != null && value.equals( other.getValue() ) ) )
				&& ( ( defaultValue == null && other.getDefaultValue() == null ) 
					|| ( defaultValue != null && defaultValue.equals( other.getDefaultValue() ) ) ) )
			return true;

		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		if( shortOption != null )
			code += shortOption.hashCode();
		if( longOption != null )
			code += longOption.hashCode();
		return code;
	}

	public String toString() {
		return "[ " + longOption + ", " + shortOption + ", " + type + ", " + description + ", "
				+ isMandatory + ", " + value + ", " + defaultValue + " ]";
	}

	private String removeHyphen(String option) {
		int start = 0;
		while( option.charAt( start ) == '-' )
			start++;

		return option.substring( start );
	}

	public void setArg(PelletCmdOptionArg arg) {
		this.arg = arg;
	}

	public PelletCmdOptionArg getArg() {
		return arg;
	}

	/**
	 * Returns if the option exists in the command-line arguments. If the argument for this option
	 * is mandatory then this implies {@link #getValue()} will return a non-null value. If the
	 * argument for this option is optional then {@link #getValue()} may still return null.
	 *  
	 * @return if the option exists in the command-line argument
	 */
	public boolean exists() {
		return exists || value != null;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
	
}
