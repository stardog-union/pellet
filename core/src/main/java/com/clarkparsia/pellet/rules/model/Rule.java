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

	private final ATermAppl name;

	private final Collection<? extends RuleAtom> body;
	private final Collection<? extends RuleAtom> head;

	private Set<ATermAppl> explanation;

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
		this.name = name;
		this.body = body;
		this.head = head;
		this.explanation = explanation;
	}

	public Set<ATermAppl> getExplanation(final RulesToATermTranslator translator)
	{
		if (explanation == null)
			explanation = singleton(translator.translate(this));
		return explanation;
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
		return body;
	}

	public Collection<? extends RuleAtom> getHead()
	{
		return head;
	}

	public ATermAppl getName()
	{
		return name;
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
		if (name != null)
		{
			sb.append(ATermUtils.toString(name));
			sb.append(" ");
		}
		sb.append(getBody());
		sb.append(" => ");
		sb.append(getHead());
		sb.append(")");

		return sb.toString();
	}
}
