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

package aterm.pure;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermFactory;
import aterm.ATermList;
import aterm.ATermPlaceholder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import jjtraveler.VisitFailure;
import shared.SharedObjectWithID;

public abstract class ATermImpl extends ATermVisitableImpl implements ATerm, SharedObjectWithID
{
	private ATermList annotations;

	protected PureFactory factory;

	private int hashCode;

	private int uniqueId;

	/**
	 * depricated Use the new constructor instead.
	 * 
	 * @param factory x
	 */
	protected ATermImpl(PureFactory factory)
	{
		super();
		this.factory = factory;
	}

	protected ATermImpl(PureFactory factory, ATermList annos)
	{
		super();

		this.factory = factory;
		this.annotations = annos;
	}

	@Override
	public final int hashCode()
	{
		return this.hashCode;
	}

	protected void setHashCode(int hashcode)
	{
		this.hashCode = hashcode;
	}

	protected void internSetAnnotations(ATermList annos)
	{
		this.annotations = annos;
	}

	/**
	 * depricated Just here for backwards compatibility.
	 * 
	 * @param hashCode x
	 * @param annos x
	 */
	protected void init(int hashCode, ATermList annos)
	{
		this.hashCode = hashCode;
		this.annotations = annos;
	}

	@Override
	public ATermFactory getFactory()
	{
		return factory;
	}

	protected PureFactory getPureFactory()
	{
		return factory;
	}

	@Override
	public boolean hasAnnotations()
	{
		return (annotations != null && !annotations.isEmpty());
	}

	@Override
	public ATerm setAnnotation(ATerm label, ATerm anno)
	{
		final ATermList new_annos = annotations.dictPut(label, anno);
		final ATerm result = setAnnotations(new_annos);

		return result;
	}

	@Override
	public ATerm removeAnnotation(ATerm label)
	{
		return setAnnotations(annotations.dictRemove(label));
	}

	@Override
	public ATerm getAnnotation(ATerm label)
	{
		return annotations.dictGet(label);
	}

	@Override
	public ATerm removeAnnotations()
	{
		return setAnnotations(getPureFactory().getEmpty());
	}

	@Override
	public ATermList getAnnotations()
	{
		return annotations;
	}

	@Override
	public List<Object> match(String pattern)
	{
		return match(factory.parsePattern(pattern));
	}

	@Override
	public List<Object> match(ATerm pattern)
	{
		final List<Object> list = new LinkedList<Object>();
		if (match(pattern, list)) { return list; }
		return null;
	}

	@Override
	public boolean isEqual(ATerm term)
	{
		if (term instanceof ATermImpl) { return this == term; }

		return factory.isDeepEqual(this, term);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (this == obj);
	}

	boolean match(ATerm pattern, List<Object> list)
	{
		if (pattern.getType() == PLACEHOLDER)
		{
			final ATerm type = ((ATermPlaceholder) pattern).getPlaceholder();
			if (type.getType() == ATerm.APPL)
			{
				final ATermAppl appl = (ATermAppl) type;
				final AFun afun = appl.getAFun();
				if (afun.getName().equals("term") && afun.getArity() == 0 && !afun.isQuoted())
				{
					list.add(this);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ATerm make(List<Object> list)
	{
		return this;
	}

	public void writeToTextFile(ATermWriter writer) throws IOException
	{
		try
		{
			writer.voidVisitChild(this);
			writer.getStream().flush();
		}
		catch (final VisitFailure e)
		{
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void writeToSharedTextFile(OutputStream stream) throws IOException
	{
		final ATermWriter writer = new ATermWriter(stream);
		writer.initializeSharing();
		stream.write('!');
		writeToTextFile(writer);
	}

	@Override
	public void writeToTextFile(OutputStream stream) throws IOException
	{
		final ATermWriter writer = new ATermWriter(stream);
		writeToTextFile(writer);
	}

	@Override
	public String toString()
	{
		try
		{
			final OutputStream stream = new ByteArrayOutputStream();
			final ATermWriter writer = new ATermWriter(stream);
			writeToTextFile(writer);

			return stream.toString();
		}
		catch (final IOException e)
		{
			throw new RuntimeException("IOException: " + e.getMessage());
		}
	}

	@Override
	public int getNrSubTerms()
	{
		return 0;
	}

	@Override
	public ATerm getSubTerm(int index)
	{
		throw new RuntimeException("no children!");
	}

	@Override
	public ATerm setSubTerm(int index, ATerm t)
	{
		throw new RuntimeException("no children!");
	}

	@Override
	public int getUniqueIdentifier()
	{
		return uniqueId;
	}

	@Override
	public void setUniqueIdentifier(int uniqueId)
	{
		this.uniqueId = uniqueId;
	}

}
