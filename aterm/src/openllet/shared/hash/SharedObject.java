/*
 * Copyright (c) 2003-2007, CWI and INRIA
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 	- Redistributions of source code must retain the above copyright
 * 	notice, this list of conditions and the following disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright
 * 	notice, this list of conditions and the following disclaimer in the
 * 	documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the CWI, INRIA nor the names of its
 * 	contributors may be used to endorse or promote products derived from
 * 	this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package openllet.shared.hash;

public interface SharedObject
{

	/**
	 * This method should ONLY be used by a SharedObjectFactory! Makes a clone of a prototype. Just
	 * like Object.clone(), but it returns a SharedObject instead of an Object. Use this method to
	 * duplicate a Prototype object (an object that is allocated only once and gets updated with new
	 * information). Using the Prototype design pattern will lead to an efficient ObjectFactory that
	 * minimizes on object allocation time.
	 *
	 * @return An exact duplicate of the current object
	 */
	SharedObject duplicate();

	/**
	 * This method should ONLY be used by a SharedObjectFactory! Checks whether an object is really
	 * equivalent. The types should be equal, the fields should be equivalent too. So this is
	 * complete recursive equivalence.
	 *
	 * @param o
	 *            The object to compare to
	 * @return true if the object is really equivalent, or false otherwise
	 */
	boolean equivalent(SharedObject o);

	@Override
	/**
	 * This method is typically used by a SharedObjectFactory! Returns the hash code of an object.
	 * It is a good idea to compute this code once, and store it locally in a field to let this
	 * hashCode() method return it. Because a SharedObject should be immutable, the hashCode has to
	 * be computed only once. Note that a hashCode() of 0 should also lead to a correct
	 * implementation, but it will be very slow. A good uniform hash leads to the fastest
	 * implementations.
	 *
	 * @return a proper hash code for this object
	 */
	int hashCode();
}
