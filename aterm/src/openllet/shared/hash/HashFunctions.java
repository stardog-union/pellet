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

public class HashFunctions
{

	static public int oneAtATime(final Object[] o)
	{
		// [arg1,...,argn,symbol]
		int hash = 0;
		for (final Object element : o)
		{
			hash += element.hashCode();
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		//return (hash & 0x0000FFFF);
		return hash;
	}

	static public int simple(final Object[] o)
	{
		// [arg1,...,argn,symbol]
		int hash = o[o.length - 1].hashCode();
		//      res = 65599*res + o[i].hashCode();
		//      res = 16*res + (1+i)*o[i].hashCode();
		for (int i = 0; i < o.length - 1; i++)
			hash = 16 * hash + o[i].hashCode();
		return hash;
	}

	static public int cwi(final Object[] o)
	{
		// [arg1,...,argn,symbol]
		int hash = 0;
		for (final Object element : o)
			hash = (hash << 1) ^ (hash >> 1) ^ element.hashCode();
		return hash;
	}

	@SuppressWarnings("incomplete-switch")
	static public int doobs(final Object[] o)
	{
		//System.out.println("static doobs_hashFuntion");

		final int initval = 0; /* the previous hash value */
		int a, b, c, len;

		/* Set up the internal state */
		len = o.length;
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		c = initval; /* the previous hash value */

		/*---------------------------------------- handle most of the key */
		int k = 0;
		while (len >= 12)
		{
			a += (o[k + 0].hashCode() + (o[k + 1].hashCode() << 8) + (o[k + 2].hashCode() << 16) + (o[k + 3].hashCode() << 24));
			b += (o[k + 4].hashCode() + (o[k + 5].hashCode() << 8) + (o[k + 6].hashCode() << 16) + (o[k + 7].hashCode() << 24));
			c += (o[k + 8].hashCode() + (o[k + 9].hashCode() << 8) + (o[k + 10].hashCode() << 16) + (o[k + 11].hashCode() << 24));
			//mix(a,b,c);
			a -= b;
			a -= c;
			a ^= (c >> 13);
			b -= c;
			b -= a;
			b ^= (a << 8);
			c -= a;
			c -= b;
			c ^= (b >> 13);
			a -= b;
			a -= c;
			a ^= (c >> 12);
			b -= c;
			b -= a;
			b ^= (a << 16);
			c -= a;
			c -= b;
			c ^= (b >> 5);
			a -= b;
			a -= c;
			a ^= (c >> 3);
			b -= c;
			b -= a;
			b ^= (a << 10);
			c -= a;
			c -= b;
			c ^= (b >> 15);

			k += 12;
			len -= 12;
		}

		/*------------------------------------- handle the last 11 bytes */
		c += o.length;
		switch (len)
		/* all the case statements fall through */{
			case 11:
				c += (o[k + 10].hashCode() << 24);
				//$FALL-THROUGH$
			case 10:
				c += (o[k + 9].hashCode() << 16);
				//$FALL-THROUGH$
			case 9:
				c += (o[k + 8].hashCode() << 8);
				/* the first byte of c is reserved for the length */
				//$FALL-THROUGH$
			case 8:
				b += (o[k + 7].hashCode() << 24);
				//$FALL-THROUGH$
			case 7:
				b += (o[k + 6].hashCode() << 16);
				//$FALL-THROUGH$
			case 6:
				b += (o[k + 5].hashCode() << 8);
				//$FALL-THROUGH$
			case 5:
				b += o[k + 4].hashCode();
				//$FALL-THROUGH$
			case 4:
				a += (o[k + 3].hashCode() << 24);
				//$FALL-THROUGH$
			case 3:
				a += (o[k + 2].hashCode() << 16);
				//$FALL-THROUGH$
			case 2:
				a += (o[k + 1].hashCode() << 8);
				//$FALL-THROUGH$
			case 1:
				a += o[k + 0].hashCode();
				/* case 0: nothing left to add */
		}
		//mix(a,b,c);
		c = mix(a, b, c);

		/*-------------------------------------------- report the result */
		return c;
	}

	@SuppressWarnings("incomplete-switch")
	static public int doobs(final String s, int c)
	{
		// o[] = [name,Integer(arity), Boolean(isQuoted)]
		// o[] = [value,offset,count,Integer(arity), Boolean(isQuoted)]

		int offset = 0;
		int count = 0;
		char[] source = null;

		count = s.length();
		source = new char[count];
		offset = 0;
		s.getChars(0, count, source, 0);

		int a, b, len;
		/* Set up the internal state */
		len = count;
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		/*------------------------------------- handle the last 11 bytes */
		int k = offset;

		while (len >= 12)
		{
			a += (source[k + 0] + (source[k + 1] << 8) + (source[k + 2] << 16) + (source[k + 3] << 24));
			b += (source[k + 4] + (source[k + 5] << 8) + (source[k + 6] << 16) + (source[k + 7] << 24));
			c += (source[k + 8] + (source[k + 9] << 8) + (source[k + 10] << 16) + (source[k + 11] << 24));
			// mix(a,b,c);
			a -= b;
			a -= c;
			a ^= (c >> 13);
			b -= c;
			b -= a;
			b ^= (a << 8);
			c -= a;
			c -= b;
			c ^= (b >> 13);
			a -= b;
			a -= c;
			a ^= (c >> 12);
			b -= c;
			b -= a;
			b ^= (a << 16);
			c -= a;
			c -= b;
			c ^= (b >> 5);
			a -= b;
			a -= c;
			a ^= (c >> 3);
			b -= c;
			b -= a;
			b ^= (a << 10);
			c -= a;
			c -= b;
			c ^= (b >> 15);

			k += 12;
			len -= 12;
		}
		/*---------------------------------------- handle most of the key */
		c += count;
		switch (len)
		{
			case 11:
				c += (source[k + 10] << 24);
				//$FALL-THROUGH$
			case 10:
				c += (source[k + 9] << 16);
				//$FALL-THROUGH$
			case 9:
				c += (source[k + 8] << 8);
				/* the first byte of c is reserved for the length */
				//$FALL-THROUGH$
			case 8:
				b += (source[k + 7] << 24);
				//$FALL-THROUGH$
			case 7:
				b += (source[k + 6] << 16);
				//$FALL-THROUGH$
			case 6:
				b += (source[k + 5] << 8);
				//$FALL-THROUGH$
			case 5:
				b += source[k + 4];
				//$FALL-THROUGH$
			case 4:
				a += (source[k + 3] << 24);
				//$FALL-THROUGH$
			case 3:
				a += (source[k + 2] << 16);
				//$FALL-THROUGH$
			case 2:
				a += (source[k + 1] << 8);
				//$FALL-THROUGH$
			case 1:
				a += source[k + 0];
				/* case 0: nothing left to add */
		}

		c = mix(a, b, c);

		return c;
	}

	public static int mix(int a, int b, int c)
	{
		a -= b;
		a -= c;
		a ^= (c >> 13);
		b -= c;
		b -= a;
		b ^= (a << 8);
		c -= a;
		c -= b;
		c ^= (b >> 13);
		a -= b;
		a -= c;
		a ^= (c >> 12);
		b -= c;
		b -= a;
		b ^= (a << 16);
		c -= a;
		c -= b;
		c ^= (b >> 5);
		a -= b;
		a -= c;
		a ^= (c >> 3);
		b -= c;
		b -= a;
		b ^= (a << 10);
		c -= a;
		c -= b;
		c ^= (b >> 15);

		return c;
	}

	@SuppressWarnings("incomplete-switch")
	public static int stringHashFunction(final String name, final int arity)
	{
		int a, b, c;
		/* Set up the internal state */
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		/*------------------------------------- handle the last 11 bytes */
		final int len = name.length();
		if (len >= 12)
			return stringHashFunctionLong(name, arity);
		c = arity + 1;
		c += len;
		switch (len)
		{
			case 11:
				c += (name.charAt(10) << 24);
				//$FALL-THROUGH$
			case 10:
				c += (name.charAt(9) << 16);
				//$FALL-THROUGH$
			case 9:
				c += (name.charAt(8) << 8);
				/* the first byte of c is reserved for the length */
				//$FALL-THROUGH$
			case 8:
				b += (name.charAt(7) << 24);
				//$FALL-THROUGH$
			case 7:
				b += (name.charAt(6) << 16);
				//$FALL-THROUGH$
			case 6:
				b += (name.charAt(5) << 8);
				//$FALL-THROUGH$
			case 5:
				b += name.charAt(4);
				//$FALL-THROUGH$
			case 4:
				a += (name.charAt(3) << 24);
				//$FALL-THROUGH$
			case 3:
				a += (name.charAt(2) << 16);
				//$FALL-THROUGH$
			case 2:
				a += (name.charAt(1) << 8);
				//$FALL-THROUGH$
			case 1:
				a += name.charAt(0);
				/* case 0: nothing left to add */
		}
		// mix(a,b,c);

		a -= b;
		a -= c;
		a ^= (c >> 13);
		b -= c;
		b -= a;
		b ^= (a << 8);
		c -= a;
		c -= b;
		c ^= (b >> 13);
		a -= b;
		a -= c;
		a ^= (c >> 12);
		b -= c;
		b -= a;
		b ^= (a << 16);
		c -= a;
		c -= b;
		c ^= (b >> 5);
		a -= b;
		a -= c;
		a ^= (c >> 3);
		b -= c;
		b -= a;
		b ^= (a << 10);
		c -= a;
		c -= b;
		c ^= (b >> 15);

		return c;
	}

	@SuppressWarnings("incomplete-switch")
	private static int stringHashFunctionLong(final String name, final int arity)
	{
		int offset = 0;
		final int count = name.length();
		final char[] source = new char[count];

		offset = 0;
		name.getChars(0, count, source, 0);
		int a, b, c;
		/* Set up the internal state */
		int len = count;
		a = b = 0x9e3779b9; /* the golden ratio; an arbitrary value */
		c = arity + 1; // to avoid collison
		/*------------------------------------- handle the last 11 bytes */
		int k = offset;

		while (len >= 12)
		{
			a += (source[k + 0] + (source[k + 1] << 8) + (source[k + 2] << 16) + (source[k + 3] << 24));
			b += (source[k + 4] + (source[k + 5] << 8) + (source[k + 6] << 16) + (source[k + 7] << 24));
			c += (source[k + 8] + (source[k + 9] << 8) + (source[k + 10] << 16) + (source[k + 11] << 24));
			// mix(a,b,c);
			a -= b;
			a -= c;
			a ^= (c >> 13);
			b -= c;
			b -= a;
			b ^= (a << 8);
			c -= a;
			c -= b;
			c ^= (b >> 13);
			a -= b;
			a -= c;
			a ^= (c >> 12);
			b -= c;
			b -= a;
			b ^= (a << 16);
			c -= a;
			c -= b;
			c ^= (b >> 5);
			a -= b;
			a -= c;
			a ^= (c >> 3);
			b -= c;
			b -= a;
			b ^= (a << 10);
			c -= a;
			c -= b;
			c ^= (b >> 15);

			k += 12;
			len -= 12;
		}
		/*---------------------------------------- handle most of the key */
		c += count;
		switch (len)
		{
			case 11:
				c += (source[k + 10] << 24);
				//$FALL-THROUGH$
			case 10:
				c += (source[k + 9] << 16);
				//$FALL-THROUGH$
			case 9:
				c += (source[k + 8] << 8);
				/* the first byte of c is reserved for the length */
				//$FALL-THROUGH$
			case 8:
				b += (source[k + 7] << 24);
				//$FALL-THROUGH$
			case 7:
				b += (source[k + 6] << 16);
				//$FALL-THROUGH$
			case 6:
				b += (source[k + 5] << 8);
				//$FALL-THROUGH$
			case 5:
				b += source[k + 4];
				//$FALL-THROUGH$
			case 4:
				a += (source[k + 3] << 24);
				//$FALL-THROUGH$
			case 3:
				a += (source[k + 2] << 16);
				//$FALL-THROUGH$
			case 2:
				a += (source[k + 1] << 8);
				//$FALL-THROUGH$
			case 1:
				a += source[k + 0];
				/* case 0: nothing left to add */
		}
		// mix(a,b,c);
		a -= b;
		a -= c;
		a ^= (c >> 13);
		b -= c;
		b -= a;
		b ^= (a << 8);
		c -= a;
		c -= b;
		c ^= (b >> 13);
		a -= b;
		a -= c;
		a ^= (c >> 12);
		b -= c;
		b -= a;
		b ^= (a << 16);
		c -= a;
		c -= b;
		c ^= (b >> 5);
		a -= b;
		a -= c;
		a ^= (c >> 3);
		b -= c;
		b -= a;
		b ^= (a << 10);
		c -= a;
		c -= b;
		c ^= (b >> 15);

		return c;
	}

}
