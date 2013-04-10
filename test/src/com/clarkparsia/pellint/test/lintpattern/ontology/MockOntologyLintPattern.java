package com.clarkparsia.pellint.test.lintpattern.ontology;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.ontology.OntologyLintPattern;
import com.clarkparsia.pellint.model.Lint;

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
public class MockOntologyLintPattern implements OntologyLintPattern {
	private int m_IntParam;
	private String m_StringParam;

	public String getName() {
		return this.toString();
	}
	
	public String getDescription() {
		return this.toString();
	}

	public boolean isFixable() {
		return false;
	}

	public LintFormat getDefaultLintFormat() {
		return null;
	}
	
	public List<Lint> match(OWLOntology ontology) {
		return null;
	}

	public void setIntParam(int v) {
		m_IntParam = v;
	}
	
	public int getIntParam() {
		return m_IntParam;
	}

	public void setStringParam(String v) {
		m_StringParam = v;
	}
	
	public String getStringParam() {
		return m_StringParam;
	}

}
