// Copyright (c) 2006 - 2009, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.sparqlowl.parser;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * <p>
 * Title: Parser Utilities
 * </p>
 * <p>
 * Description: Static utility methods and fields used by the ANTLR generated
 * parser sources. This code is in a separate Java file rather than in the ANTLR
 * sources to make it easier to maintain with comfortable Java tools (e.g.,
 * Eclipse).
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith <a
 *         href="mailto:msmith@clarkparsia.com">msmith@clarkparsia.com</a>
 */
public class ParserUtilities {

	/**
	 * Trim first and last character from <code>s</code>. Used when the parser
	 * matches STRING_LITERAL1 or STRING_LITERAL2 (SPARQL A.8[87]-[88])
	 * 
	 * @param s
	 *            The string to be trimmed
	 * @return The string without its first and last character
	 * @throw IllegalArgumentException if string has fewer than 2 characters
	 */
	public static String dropFirstAndLast(String s) {
		if( s == null )
			throw new NullPointerException();

		final int n = s.length();
		if( n < 3 ) {
			if( n == 2 )
				return "";
			else
				throw new IllegalArgumentException();
		}
		else
			return s.substring( 1, n - 1 );
	}

	/**
	 * Trim first and last character from <code>s</code>. Used when the parser
	 * matches STRING_LITERAL_LONG1 or STRING_LITERAL_LONG2 (SPARQL
	 * A.8[89]-[90])
	 * 
	 * @param s
	 *            The string to be trimmed
	 * @return The string without its first and last character
	 * @throw IllegalArgumentException if string has fewer than 6 characters
	 */
	public static String dropFirstAndLast3(String s) {
		if( s == null )
			throw new NullPointerException();

		final int n = s.length();
		if( n < 7 ) {
			if( n == 6 )
				return "";
			else
				throw new IllegalArgumentException();
		}
		else
			return s.substring( 3, n - 3 );
	}

	/**
	 * Reverse character escaping in a string literal consistent with SPARQL
	 * A.7, A.8[91]
	 */
	public static String sparqlUnescape(String s) {
		final StringBuilder buf = new StringBuilder();
		final StringCharacterIterator it = new StringCharacterIterator( s );
		char c = it.current();
		while( c != CharacterIterator.DONE ) {
			if( (c == '\\') ) {
				c = it.next();
				switch ( c ) {
				case 't':
					buf.append( '\t' );
					break;
				case 'b':
					buf.append( '\b' );
					break;
				case 'n':
					buf.append( '\n' );
					break;
				case 'r':
					buf.append( '\r' );
					break;
				case 'f':
					buf.append( '\f' );
					break;
				case '\\':
					buf.append( '\\' );
					break;
				case '"':
					buf.append( '"' );
					break;
				case '\'':
					buf.append( '\'' );
					break;
				case CharacterIterator.DONE:
					buf.append( '\\' );
					break;
				default:
					buf.append( '\\' );
					buf.append( c );
				}
			}
			else
				buf.append( c );

			c = it.next();
		}
		return buf.toString();
	}
}
