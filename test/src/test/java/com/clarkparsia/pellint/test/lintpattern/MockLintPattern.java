package com.clarkparsia.pellint.test.lintpattern;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.LintPattern;

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
public class MockLintPattern implements LintPattern {
	private boolean m_IsFixable;
	
	public MockLintPattern() {
		this(false);
	}

	public MockLintPattern(boolean isFixable) {
		m_IsFixable = isFixable;
	}
	
	public String getName() {
		return this.toString();
	}

	public String getDescription() {
		return this.toString();
	}

	public boolean isFixable() {
		return m_IsFixable;
	}

	public LintFormat getDefaultLintFormat() {
		return null;
	}

}
