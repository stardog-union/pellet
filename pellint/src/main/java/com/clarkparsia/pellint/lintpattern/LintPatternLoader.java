// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.pellint.lintpattern.axiom.AxiomLintPattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToAllValuePattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToComplementPattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToMaxCardinalityPattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToTopPattern;
import com.clarkparsia.pellint.lintpattern.axiom.GCIPattern;
import com.clarkparsia.pellint.lintpattern.axiom.LargeCardinalityPattern;
import com.clarkparsia.pellint.lintpattern.axiom.LargeDisjunctionPattern;
import com.clarkparsia.pellint.lintpattern.ontology.EquivalentAndSubclassAxiomPattern;
import com.clarkparsia.pellint.lintpattern.ontology.ExistentialExplosionPattern;
import com.clarkparsia.pellint.lintpattern.ontology.OntologyLintPattern;
import com.clarkparsia.pellint.lintpattern.ontology.TooManyDifferentIndividualsPattern;
import com.clarkparsia.pellint.util.CollectionUtil;

/**
 * <p>
 * Title: Lint Pattern Loader
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
public class LintPatternLoader {
	private static final Logger LOGGER = Logger.getLogger(LintPatternLoader.class.getName());
	
	public static final List<AxiomLintPattern> DEFAULT_AXIOM_LINT_PATTERNS = Arrays.asList(
			new EquivalentToAllValuePattern(),
			new EquivalentToMaxCardinalityPattern(),
			new EquivalentToComplementPattern(),
			new EquivalentToTopPattern(),
			new GCIPattern(),
			new LargeCardinalityPattern(),
			new LargeDisjunctionPattern()			
		);
	
	public static final List<OntologyLintPattern> DEFAULT_ONTOLOGY_LINT_PATTERNS = Arrays.asList(
			new EquivalentAndSubclassAxiomPattern(),
			new ExistentialExplosionPattern(),
			new TooManyDifferentIndividualsPattern()
		);

	private List<AxiomLintPattern> m_AxiomLintPatterns;
	private List<OntologyLintPattern> m_OntologyLintPatterns;

	public LintPatternLoader() {
		m_AxiomLintPatterns = DEFAULT_AXIOM_LINT_PATTERNS;
		m_OntologyLintPatterns = DEFAULT_ONTOLOGY_LINT_PATTERNS;
	}
	
	public LintPatternLoader(Properties properties) {
		Collection<LintPattern> patterns = loadPatterns(formatProperties(properties));
		if (patterns.isEmpty()) {
			m_AxiomLintPatterns = DEFAULT_AXIOM_LINT_PATTERNS;
			m_OntologyLintPatterns = DEFAULT_ONTOLOGY_LINT_PATTERNS;
		} else {
			m_AxiomLintPatterns = CollectionUtil.makeList();
			m_OntologyLintPatterns = CollectionUtil.makeList();
			for (LintPattern pattern : patterns) {
				if (pattern instanceof AxiomLintPattern) {
					m_AxiomLintPatterns.add((AxiomLintPattern) pattern);
				} else if (pattern instanceof OntologyLintPattern) {
					m_OntologyLintPatterns.add((OntologyLintPattern) pattern);
				}
			}
		}
	}

	public List<AxiomLintPattern> getAxiomLintPatterns() {
		return m_AxiomLintPatterns;
	}

	public List<OntologyLintPattern> getOntologyLintPatterns() {
		return m_OntologyLintPatterns;
	}
	
	private static Map<String, String> formatProperties(Properties properties) {
		Map<String, String> formattedProperties = new HashMap<String, String>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			String keyStr = (key == null) ? "" : key.toString().trim();
			String valueStr = (value == null) ? "" : value.toString().trim();
			formattedProperties.put(keyStr, valueStr);
		}
		return formattedProperties;
	}

	private static Collection<LintPattern> loadPatterns(Map<String, String> properties) {
		Map<String, LintPattern> patterns = new HashMap<String, LintPattern>();
		
		//Search for enabled patterns
		Set<String> patternNames = new HashSet<String>();
		for (Entry<String, String> entry : properties.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			LintPattern pattern = parseLintPattern(key);
			if (pattern != null) {
				patternNames.add(key);
				if ("on".equalsIgnoreCase(value)) {
					patterns.put(key, pattern);
				}
			} else {
				if ("on".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value)) {
					patternNames.add(key);
					LOGGER.severe("Cannot find and construct pattern " + key);
				}
			}
		}
		
		for (String patternName : patternNames) {
			properties.remove(patternName);
		}
		
		//Search and set parameters for patterns
		for (Entry<String, String> entry : properties.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			int lastDot = key.lastIndexOf('.');
			if (lastDot < 0 || lastDot > key.length()) {
				LOGGER.severe("Cannot find field name " + key);
				continue;
			}
			
			String className = key.substring(0, lastDot); 
			String fieldName = key.substring(lastDot + 1);
			if (!patternNames.contains(className)) {
				LOGGER.severe("Cannot find pattern " + className + " to set its parameter " + fieldName);
				continue;
			}
			
			LintPattern pattern = patterns.get(className);
			if (pattern != null) {
				setParameter(pattern, className, fieldName, value);
			}
		}
		
		return patterns.values();
	}

	private static LintPattern parseLintPattern(String str) {
		try {
			Class<?> clazz = Class.forName(str);
			Class<? extends LintPattern> lpClazz = clazz.asSubclass( LintPattern.class );
			Constructor<? extends LintPattern> ctor = lpClazz.getConstructor();
			return ctor.newInstance();
		} catch (Exception e ) {
			// No error logging here because properties file have entries for
			// the configuration patterns
			// LOGGER.severe( e );
		}
		
		return null;
	}
	
	private static void setParameter(LintPattern pattern, String className, String fieldName, String value) {
		String setter = "set" + fieldName;
		Class<? extends LintPattern> clazz = pattern.getClass();
		
		try {
			Method method = clazz.getMethod(setter, int.class);
			try {
				int intValue = Integer.parseInt(value);
				method.invoke(pattern, intValue);
				return;
			} catch (NumberFormatException e) {
				LOGGER.log(Level.FINE, value + " is not an integer", e);
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			} catch (InvocationTargetException e) {
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
		} catch (SecurityException e) {
			LOGGER.log(Level.FINE, "Error accessing method " + setter + "(int) on lint pattern " + className, e);
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.FINE, "Method " + setter + "(int) not found on lint pattern " + className, e);
		}
		
		try {
			Method method = clazz.getMethod(setter, String.class);
			try {
				method.invoke(pattern, value);
				return;
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			} catch (InvocationTargetException e) {
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
		} catch (SecurityException e) {
			LOGGER.log(Level.FINE, "Error accessing method " + setter + "(String) on lint pattern " + className, e);
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.FINE, "Method " + setter + "(String) not found on lint pattern " + className, e);
		}
		
		LOGGER.severe("Cannot set paramater " + fieldName + "=" + value + " for lint pattern " + className);
	}
}