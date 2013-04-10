//The MIT License
//
//Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to
//deal in the Software without restriction, including without limitation the
//rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
//sell copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.

package org.mindswap.pellet.datatypes;

import aterm.ATermAppl;


/**
 * @author Evren Sirin
 * @deprecated Use {@link com.clarkparsia.pellet.datatypes.Datatype} instead
 */
public interface Datatype {
    public String getURI();
    
	public ATermAppl getName();
	
	public int size(); 
	
	public boolean isEmpty();

	public boolean contains(Object value);
	
	public boolean contains(Object value, AtomicDatatype datatype);
	
	public Object getValue(String value, String datatypeURI);
    
    public ATermAppl getValue( int i );

	public Datatype singleton(Object value);
	
			
}