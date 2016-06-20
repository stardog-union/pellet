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

package openllet.aterm.pure;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermBlob;
import openllet.aterm.ATermFwdVoid;
import openllet.aterm.ATermInt;
import openllet.aterm.ATermList;
import openllet.aterm.ATermLong;
import openllet.aterm.ATermPlaceholder;
import openllet.aterm.ATermReal;
import openllet.aterm.VisitFailure;
import openllet.aterm.stream.BufferedOutputStreamWriter;

class ATermWriter extends ATermFwdVoid
{

	private static char[] TOBASE64 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	private final BufferedOutputStreamWriter stream;

	private int position;

	private Map<ATerm, Integer> table;

	private int next_abbrev;

	ATermWriter(final OutputStream stream)
	{
		this.stream = new BufferedOutputStreamWriter(stream);
	}

	public Writer getStream()
	{
		return stream;
	}

	private void emitAbbrev(int abbrev)
	{
		stream.write('#');
		position++;

		final StringBuilder buf = new StringBuilder();

		if (abbrev == 0)
		{
			buf.append(TOBASE64[0]);
		}

		while (abbrev > 0)
		{
			buf.append(TOBASE64[abbrev % 64]);
			abbrev /= 64;
		}
		final String txt = buf.reverse().toString();
		stream.write(txt);
		position += txt.length();
	}

	public void voidVisitChild(final ATerm child)
	{
		if (table != null)
		{
			final Integer abbrev = table.get(child);
			if (abbrev != null)
			{
				emitAbbrev(abbrev.intValue());
				return;
			}
		}

		final int start = position;
		if (child.getType() == ATerm.LIST)
		{
			stream.write('[');
			position++;
		}
		visit(child);
		if (child.getType() == ATerm.LIST)
		{
			stream.write(']');
			position++;
		}

		final ATermList annos = child.getAnnotations();
		if (!annos.isEmpty())
		{
			stream.write('{');
			position++;
			visit(annos);
			stream.write('}');
			position++;
		}

		if (table != null)
		{
			final int length = position - start;
			if (length > PureFactory.abbrevSize(next_abbrev))
			{
				final Integer key = new Integer(next_abbrev++);
				table.put(child, key);
			}
		}
	}

	@Override
	public void voidVisitAppl(final ATermAppl appl)
	{
		final AFun fun = appl.getAFun();
		int nameLength;
		try
		{
			nameLength = fun.serialize(stream);
		}
		catch (final IOException ioex)
		{
			throw new VisitFailure(ioex.getMessage());
		}
		position += nameLength;
		if (fun.getArity() > 0 || nameLength == 0)
		{
			stream.write('(');
			position++;
			for (int i = 0; i < fun.getArity(); i++)
			{
				if (i != 0)
				{
					stream.write(',');
					position++;
				}
				voidVisitChild(appl.getArgument(i));
			}
			stream.write(')');
			position++;
		}
	}

	@Override
	public void voidVisitList(ATermList list)
	{
		while (!list.isEmpty())
		{
			voidVisitChild(list.getFirst());
			list = list.getNext();
			if (!list.isEmpty())
			{
				stream.write(',');
				position++;
			}
		}
	}

	@Override
	public void voidVisitPlaceholder(final ATermPlaceholder ph)
	{
		stream.write('<');
		position++;
		voidVisitChild(ph.getPlaceholder());
		stream.write('>');
		position++;
	}

	@Override
	public void voidVisitInt(final ATermInt i)
	{
		final String txt = String.valueOf(i.getInt());
		stream.write(txt);
		position += txt.length();
	}

	@Override
	public void voidVisitLong(final ATermLong i)
	{
		final String txt = String.valueOf(i.getLong());
		stream.write(txt);
		position += txt.length();
	}

	@Override
	public void voidVisitReal(final ATermReal r)
	{
		final String txt = String.valueOf(r.getReal());
		stream.write(txt);
		position += txt.length();
	}

	@Override
	public void voidVisitBlob(final ATermBlob blob)
	{
		final String txt = String.valueOf(blob.getBlobSize()) + '#' + String.valueOf(blob.hashCode());
		stream.write(txt);
		position += txt.length();
	}

	public void initializeSharing()
	{
		table = new HashMap<>();
	}

}
