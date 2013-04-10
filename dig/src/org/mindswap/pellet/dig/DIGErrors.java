
package org.mindswap.pellet.dig;

/**
 * @author Evren Sirin
 */
public class DIGErrors {
	public final static int	GENERAL_UNSPECIFIED_ERROR	= 0;
	public final static int	UNKNOWN_REQUEST				= 1;
	public final static int	MALFORMED_REQUEST			= 2;
	public final static int	UNSUPPORTED_OPERATION		= 3;
	public final static int	CANNOT_CREATE_NEW_KNOWLEDGE	= 4;
	public final static int	MALFORMED_KB_URI			= 5;
	public final static int	UNKNOWN_OR_STALE_KB_URI		= 6;
	public final static int	KB_RELEASE_ERROR			= 7;
	public final static int	MISSING_URI					= 8;
	public final static int	GENERAL_TELL_ERROR			= 9;
	public final static int	UNSUPPORTED_TELL_OPERATION	= 10;
	public final static int	UNKNOWN_TELL_OPERATION		= 11;
	public final static int	GENERAL_ASK_ERROR			= 12;
	public final static int	UNSUPPORTED_ASK_OPERATION	= 13;
	public final static int	UNKNOWN_ASK_OPERATION		= 14;

	final static String[]	codes						= {
			"100", "General Unspecified Error", "101", "Unknown Request", "102",
			"Malformed Request (XML error)", "103", "Unsupported Operation", "201",
			"Cannot create new knowledge", "202", "Malformed KB URI", "203",
			"Unknown or stale KB URI", "204", "KB Release Error", "205", "Missing URI", "300",
			"General Tell Error", "301", "Unsupported Tell Operation", "302",
			"Unknown Tell Operation", "400", "General Ask Error", "401",
			"Unsupported Ask Operation", "402", "Unknown Ask Operation" };

}
