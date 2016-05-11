// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

import static java.util.Collections.singleton;

import aterm.ATermAppl;
import com.clarkparsia.pellet.rules.RulesToATermTranslator;
import java.util.Collection;
import java.util.Set;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Rule
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class Rule
{

	private final ATermAppl _name;

	private final Collection<? extends RuleAtom> _body;// FIXME : use an ordered collection here.
	private final Collection<? extends RuleAtom> _head;

	private Set<ATermAppl> _explanation;

	public Rule(final Collection<? extends RuleAtom> head, final Collection<? extends RuleAtom> body)
	{
		this(head, body, null);
	}

	public Rule(final Collection<? extends RuleAtom> head, final Collection<? extends RuleAtom> body, final Set<ATermAppl> explanation)
	{
		this(null, head, body, explanation);
	}

	public Rule(final ATermAppl name, final Collection<? extends RuleAtom> head, final Collection<? extends RuleAtom> body)
	{
		this(name, head, body, null);
	}

	public Rule(final ATermAppl name, final Collection<? extends RuleAtom> head, final Collection<? extends RuleAtom> body, final Set<ATermAppl> explanation)
	{
		this._name = name;
		this._body = body;
		this._head = head;
		this._explanation = explanation;
	}

	public Set<ATermAppl> getExplanation(final RulesToATermTranslator translator)
	{
		if (_explanation == null)
			_explanation = singleton(translator.translate(this));
		return _explanation;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other != null && getClass().equals(other.getClass()))
		{
			final Rule rule = (Rule) other;
			return getHead().equals(rule.getHead()) && getBody().equals(rule.getBody());

		}
		return false;
	}

	public Collection<? extends RuleAtom> getBody()
	{
		return _body;
	}

	public Collection<? extends RuleAtom> getHead()
	{
		return _head;
	}

	public ATermAppl getName()
	{
		return _name;
	}

	@Override
	public int hashCode()
	{
		return getBody().hashCode() + getHead().hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("Rule(");
		if (_name != null)
		{
			sb.append(ATermUtils.toString(_name));
			sb.append(" ");
		}
		sb.append(getBody());
		sb.append(" => ");
		sb.append(getHead());
		sb.append(")");

		return sb.toString();
	}
}
