package com.clarkparsia.pellint.test.lintpattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;

import com.clarkparsia.pellint.lintpattern.LintPatternLoader;
import com.clarkparsia.pellint.test.lintpattern.axiom.MockAxiomLintPattern;
import com.clarkparsia.pellint.test.lintpattern.ontology.MockOntologyLintPattern;

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
public class LintPatternLoaderTest {
	private static final String MOCK_AXIOM_LINT_PATTERN_NAME = MockAxiomLintPattern.class.getName(); 
	private static final String MOCK_ONTOLOGY_LINT_PATTERN_NAME = MockOntologyLintPattern.class.getName(); 
	private static final String INT_PARAM = "IntParam";
	private static final String STRING_PARAM = "StringParam";
	
	@Test
	public void testAxiomLintPatternSuccess() {
		final int EXPECTED_INT_PARAM = 5;
		final String EXPECTED_STRING_PARAM = "HERE";
		
		Properties prop = new Properties();
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME, "ON");
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME + "." + INT_PARAM, String.valueOf(EXPECTED_INT_PARAM));
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME + "." + STRING_PARAM, EXPECTED_STRING_PARAM);
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		assertEquals(1, loader.getAxiomLintPatterns().size());
		assertEquals(0, loader.getOntologyLintPatterns().size());
		
		MockAxiomLintPattern pattern = (MockAxiomLintPattern) loader.getAxiomLintPatterns().get(0);
		assertEquals(EXPECTED_INT_PARAM, pattern.getIntParam());
		assertEquals(EXPECTED_STRING_PARAM, pattern.getStringParam());
	}

	@Test
	public void testOntologyLintPatternSuccess() {
		final int EXPECTED_INT_PARAM = 5;
		final String EXPECTED_STRING_PARAM = "HERE";
		
		Properties prop = new Properties();
		prop.setProperty(MOCK_ONTOLOGY_LINT_PATTERN_NAME, "ON");
		prop.setProperty(MOCK_ONTOLOGY_LINT_PATTERN_NAME + "." + INT_PARAM, String.valueOf(EXPECTED_INT_PARAM));
		prop.setProperty(MOCK_ONTOLOGY_LINT_PATTERN_NAME + "." + STRING_PARAM, EXPECTED_STRING_PARAM);
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		assertEquals(1, loader.getOntologyLintPatterns().size());
		assertEquals(0, loader.getAxiomLintPatterns().size());
		
		MockOntologyLintPattern pattern = (MockOntologyLintPattern) loader.getOntologyLintPatterns().get(0);
		assertEquals(EXPECTED_INT_PARAM, pattern.getIntParam());
		assertEquals(EXPECTED_STRING_PARAM, pattern.getStringParam());
	}

	@Test
	public void testOff() {
		Properties prop = new Properties();
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME, "OFF");
		prop.setProperty(MOCK_ONTOLOGY_LINT_PATTERN_NAME, "XXX");
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		assertEquals(LintPatternLoader.DEFAULT_AXIOM_LINT_PATTERNS, loader.getAxiomLintPatterns());
		assertEquals(LintPatternLoader.DEFAULT_ONTOLOGY_LINT_PATTERNS, loader.getOntologyLintPatterns());
	}
	
	@Test
	public void testPatternNotFound() {
		Properties prop = new Properties();
		prop.setProperty("com.foo", "on");
		prop.setProperty("com.foo", "off");
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		assertEquals(LintPatternLoader.DEFAULT_AXIOM_LINT_PATTERNS, loader.getAxiomLintPatterns());
		assertEquals(LintPatternLoader.DEFAULT_ONTOLOGY_LINT_PATTERNS, loader.getOntologyLintPatterns());
	}
	
	@Test
	public void testParamNotFound() {
		Properties prop = new Properties();
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME, "ON");
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME + "." + "foo", "10");
		prop.setProperty(MOCK_ONTOLOGY_LINT_PATTERN_NAME, "ON");
		prop.setProperty(MOCK_ONTOLOGY_LINT_PATTERN_NAME + "." + "foo", "X");
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		assertEquals(1, loader.getAxiomLintPatterns().size());
		assertEquals(1, loader.getOntologyLintPatterns().size());
		
		MockAxiomLintPattern axiomPattern = (MockAxiomLintPattern) loader.getAxiomLintPatterns().get(0);
		assertEquals(0, axiomPattern.getIntParam());
		assertNull(axiomPattern.getStringParam());
		
		MockOntologyLintPattern ontologyPattern = (MockOntologyLintPattern) loader.getOntologyLintPatterns().get(0);
		assertEquals(0, ontologyPattern.getIntParam());
		assertNull(ontologyPattern.getStringParam());
	}

	@Test
	public void testParamWrongType1() {
		Properties prop = new Properties();
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME, "ON");
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME + "." + INT_PARAM, "XXX");
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		MockAxiomLintPattern axiomPattern = (MockAxiomLintPattern) loader.getAxiomLintPatterns().get(0);
		assertEquals(0, axiomPattern.getIntParam());
	}

	@Test
	public void testParamWrongType2() {
		Properties prop = new Properties();
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME, "ON");
		prop.setProperty(MOCK_AXIOM_LINT_PATTERN_NAME + "." + INT_PARAM, "5.5");
		LintPatternLoader loader = new LintPatternLoader(prop);
		
		MockAxiomLintPattern axiomPattern = (MockAxiomLintPattern) loader.getAxiomLintPatterns().get(0);
		assertEquals(0, axiomPattern.getIntParam());
	}
}
