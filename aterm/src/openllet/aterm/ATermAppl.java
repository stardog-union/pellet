/*
 * Copyright (c) 2002-2007, CWI and INRIA
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package openllet.aterm;

/**
 * An ATermAppl represents a function application.
 *
 * @author Hayco de Jong (jong@cwi.nl)
 * @author Pieter Olivier (olivierp@cwi.nl)
 */
public interface ATermAppl extends ATerm
{

	/**
	 * Gets the AFun object that represents the function symbol of this application
	 *
	 * @return the function symbol of this application.
	 *
	 */
	public AFun getAFun();

	/**
	 * Gets the function name of this application.
	 *
	 * @return the function name of this application.
	 *
	 */
	public String getName();

	/**
	 * Gets the arguments of this application.
	 *
	 * @return a list containing all arguments of this application.
	 */
	public ATermList getArguments();

	/**
	 * Gets the arguments of this application as an array of ATerm objects.
	 *
	 * @return an array containing all arguments of this application.
	 *
	 */

	public ATerm[] getArgumentArray();

	/**
	 * Gets a specific argument of this application.
	 *
	 * @param i the index of the argument to be retrieved.
	 *
	 * @return the ith argument of the application.
	 */
	public ATerm getArgument(int i);

	/**
	 * Sets a specific argument of this application.
	 *
	 * @param arg the new ith argument.
	 * @param i the index of the argument to be set.
	 *
	 * @return a copy of this application with argument i replaced by arg.
	 */
	public ATermAppl setArgument(ATerm arg, int i);

	/**
	 * Checks if this application is quoted. A quoted application looks
	 * like this: "foo", whereas an unquoted looks like this: foo.
	 *
	 * @return true if this application is quoted, false otherwise.
	 */
	public boolean isQuoted();

	/**
	 * Gets the arity of this application. Arity is the number
	 * of arguments of a function application.
	 *
	 * @return the number of arguments of this application.
	 */
	public int getArity();
}
