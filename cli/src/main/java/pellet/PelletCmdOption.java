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
 * Description: Represents a pellet command line option, i.e. the option name, the long option name and the option _value given on command line
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
public class PelletCmdOption
{

	private final String _longOption;
	private String _shortOption;
	private String _type;
	private String _description;
	private boolean _isMandatory;
	private Object _value;
	private Object _defaultValue;
	private boolean _exists;
	private PelletCmdOptionArg _arg = PelletCmdOptionArg.NONE;

	public PelletCmdOption(final String longOption)
	{
		if (longOption == null)
			throw new PelletCmdException("A long option must be defined for a command line option");

		_longOption = removeHyphen(longOption);
		_defaultValue = null;
	}

	public String getShortOption()
	{
		return _shortOption;
	}

	public String getLongOption()
	{
		return _longOption;
	}

	public void setShortOption(final String shortOption)
	{
		_shortOption = removeHyphen(shortOption);
	}

	public String getType()
	{
		return _type;
	}

	public void setType(final String type)
	{
		_type = type;
	}

	public void setDescription(final String description)
	{
		_description = description;
	}

	public String getDescription()
	{
		return _description;
	}

	public void setDefaultValue(final Object defaultValue)
	{
		_defaultValue = defaultValue;
	}

	public Object getDefaultValue()
	{
		return _defaultValue;
	}

	public Object getValue()
	{
		return _value;
	}

	public String getValueAsString()
	{
		if (_value != null)
			return _value.toString();

		if (_defaultValue != null)
			return _defaultValue.toString();

		return null;
	}

	/**
	 * Returns the option _value as an integer and verifies that the _value is a positive integer (>= 1).
	 *
	 * @return an integer _value
	 * @throws PelletCmdException If the option _value does not exist or is a not a valid positive integer _value (>= 1)
	 */
	public int getValueAsPositiveInteger() throws PelletCmdException
	{
		return getValueAsInteger(1, Integer.MAX_VALUE);
	}

	/**
	 * Returns the option _value as an integer and verifies that the _value is a non-negative integer (>= 0).
	 *
	 * @return an integer _value
	 * @throws PelletCmdException If the option _value does not exist or is a not a valid non-negative integer _value (>= 0)
	 */
	public int getValueAsNonNegativeInteger() throws PelletCmdException
	{
		return getValueAsInteger(0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the option _value as an integer
	 *
	 * @return an integer _value
	 * @throws PelletCmdException If the option _value does not exist or is a not a valid integer _value
	 */
	public int getValueAsInteger() throws PelletCmdException
	{
		return getValueAsInteger(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Returns the option _value as an integer and verifies that it is in the given range.
	 *
	 * @param minAllowed Minimum allowed _value for the integer (inclusive)
	 * @param maxAllowed Maximum allowed _value for the integer (inclusive)
	 * @return an integer _value in the specified range
	 * @throws PelletCmdException If the option _value does not exist, is a not a valid integer _value, or not in the specified range
	 */
	public int getValueAsInteger(final int minAllowed, final int maxAllowed) throws PelletCmdException
	{
		final String value = getValueAsString();

		if (value == null)
			throw new PelletCmdException(String.format("The _value for option <%s> does not exist%n", _longOption));

		try
		{
			final int intValue = Integer.parseInt(value);
			if (intValue < minAllowed)
				throw new PelletCmdException(String.format("The _value for option <%s> should be greater than or equal to %d but was: %d%n", _longOption, minAllowed, intValue));

			if (intValue > maxAllowed)
				throw new PelletCmdException(String.format("The _value for option <%s> should be less than or equal to %d but was: %d%n", _longOption, maxAllowed, intValue));
			return intValue;
		}
		catch (final NumberFormatException e)
		{
			throw new PelletCmdException(String.format("The _value for option <%s> is not a valid integer: %s%n", _longOption, value), e);
		}
	}

	/**
	 * Returns the string _value as a boolean. If no _value _exists returns <code>false</code> by default.
	 *
	 * @return returns the string _value as a boolean
	 */
	public boolean getValueAsBoolean()
	{
		final String value = getValueAsString();

		return Boolean.parseBoolean(value);
	}

	public void setValue(final String value)
	{
		_value = value;
	}

	public void setValue(final Boolean value)
	{
		_value = value;
	}

	public void setIsMandatory(final boolean isMandatory)
	{
		_isMandatory = isMandatory;
	}

	public boolean isMandatory()
	{
		return _isMandatory;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof PelletCmdOption))
			return false;

		final PelletCmdOption other = (PelletCmdOption) o;

		if (((_shortOption == null && other.getShortOption() == null) || (_shortOption != null && _shortOption.equals(other.getShortOption()))) && _longOption.equals(other.getLongOption()) && ((_type == null && other.getType() == null) || (_type != null && _type.equals(other.getType()))) && ((_description == null && other.getDescription() == null) || (_description != null && _description.equals(other.getDescription()))) && _isMandatory == other.isMandatory() && ((_value == null && other.getValue() == null) || (_value != null && _value.equals(other.getValue()))) && ((_defaultValue == null && other.getDefaultValue() == null) || (_defaultValue != null && _defaultValue.equals(other.getDefaultValue()))))
			return true;

		return false;
	}

	@Override
	public int hashCode()
	{
		int code = 0;
		if (_shortOption != null)
			code += _shortOption.hashCode();
		if (_longOption != null)
			code += _longOption.hashCode();
		return code;
	}

	@Override
	public String toString()
	{
		return "[ " + _longOption + ", " + _shortOption + ", " + _type + ", " + _description + ", " + _isMandatory + ", " + _value + ", " + _defaultValue + " ]";
	}

	private String removeHyphen(final String option)
	{
		int start = 0;
		while (option.charAt(start) == '-')
			start++;

		return option.substring(start);
	}

	public void setArg(final PelletCmdOptionArg arg)
	{
		_arg = arg;
	}

	public PelletCmdOptionArg getArg()
	{
		return _arg;
	}

	/**
	 * Returns if the option _exists in the command-line arguments. If the argument for this option is mandatory then this implies {@link #getValue()} will
	 * return a non-null _value. If the argument for this option is optional then {@link #getValue()} may still return null.
	 *
	 * @return if the option _exists in the command-line argument
	 */
	public boolean exists()
	{
		return _exists || _value != null;
	}

	public void setExists(final boolean exists)
	{
		_exists = exists;
	}

}
