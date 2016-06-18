// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.tbox.impl;

import aterm.ATerm;
import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.Map;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import openllet.shared.tools.Logging;
import org.mindswap.pellet.KnowledgeBase;

public class TBoxBase implements Logging
{
	public static Logger _logger = Log.getLogger(TBoxBase.class);

	protected KnowledgeBase _kb;
	protected TBoxExpImpl _tbox;

	protected Map<ATermAppl, TermDefinition> _termhash = CollectionUtils.makeIdentityMap();

	public TBoxBase(final TBoxExpImpl tbox)
	{
		this._tbox = tbox;
		this._kb = tbox.getKB();
	}

	@Override
	public Logger getLogger()
	{
		return _logger;
	}

	public boolean addDef(final ATermAppl def)
	{
		final ATermAppl name = (ATermAppl) def.getArgument(0);
		if (_termhash.containsKey(name))
			getTD(name).addDef(def);
		else
		{
			final TermDefinition td = new TermDefinition();
			td.addDef(def);
			_termhash.put(name, td);
		}

		return true;
	}

	public boolean removeDef(final ATermAppl axiom)
	{
		boolean removed = false;

		final ATermAppl name = (ATermAppl) axiom.getArgument(0);
		final TermDefinition td = getTD(name);
		if (td != null)
			removed = td.removeDef(axiom);

		return removed;
	}

	public boolean contains(final ATerm name)
	{
		return _termhash.containsKey(name);
	}

	public TermDefinition getTD(final ATerm name)
	{
		return _termhash.get(name);

	}

	public boolean isEmpty()
	{
		return (_termhash.size() == 0);
	}

	/**
	 * Returns the number of term definitions stored in this TBox.
	 *
	 * @return
	 */
	public int size()
	{
		return _termhash.size();
	}

}
