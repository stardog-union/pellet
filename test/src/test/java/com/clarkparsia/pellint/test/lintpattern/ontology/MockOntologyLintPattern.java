package com.clarkparsia.pellint.test.lintpattern.ontology;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.ontology.OntologyLintPattern;
import com.clarkparsia.pellint.model.Lint;
import java.util.List;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class MockOntologyLintPattern implements OntologyLintPattern
{
	private int _intParam;
	private String _stringParam;

	@Override
	public String getName()
	{
		return this.toString();
	}

	@Override
	public String getDescription()
	{
		return this.toString();
	}

	@Override
	public boolean isFixable()
	{
		return false;
	}

	@Override
	public LintFormat getDefaultLintFormat()
	{
		return null;
	}

	@Override
	public List<Lint> match(final OWLOntology ontology)
	{
		return null;
	}

	public void setIntParam(final int v)
	{
		_intParam = v;
	}

	public int getIntParam()
	{
		return _intParam;
	}

	public void setStringParam(final String v)
	{
		_stringParam = v;
	}

	public String getStringParam()
	{
		return _stringParam;
	}

}
