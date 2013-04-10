package com.clarkparsia.pellint.test.lintpattern.axiom;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.axiom.AxiomLintPattern;

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
public class MockAxiomLintPattern extends AxiomLintPattern {
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
