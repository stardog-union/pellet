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
	private int m_IntParam;
	private String m_StringParam;

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
		m_IntParam = v;
	}

	public int getIntParam()
	{
		return m_IntParam;
	}

	public void setStringParam(final String v)
	{
		m_StringParam = v;
	}

	public String getStringParam()
	{
		return m_StringParam;
	}

}
