package com.clarkparsia.pellint.test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarkparsia.pellint.test.lintpattern.LintPatternLoaderTest;
import com.clarkparsia.pellint.test.lintpattern.axiom.EquivalentToAllValuePatternTest;
import com.clarkparsia.pellint.test.lintpattern.axiom.EquivalentToComplementPatternTest;
import com.clarkparsia.pellint.test.lintpattern.axiom.EquivalentToTopPatternTest;
import com.clarkparsia.pellint.test.lintpattern.axiom.GCIPatternTest;
import com.clarkparsia.pellint.test.lintpattern.axiom.LargeCardinalityPatternTest;
import com.clarkparsia.pellint.test.lintpattern.axiom.LargeDisjunctionPatternTest;
import com.clarkparsia.pellint.test.lintpattern.ontology.EquivalentAndSubclassAxiomPatternTest;
import com.clarkparsia.pellint.test.lintpattern.ontology.ExistentialExplosionPatternTest;
import com.clarkparsia.pellint.test.lintpattern.ontology.TooManyDifferentIndividualsPatternTest;
import com.clarkparsia.pellint.test.model.LintFixerTest;
import com.clarkparsia.pellint.test.model.LintTest;
import com.clarkparsia.pellint.test.model.OntologyLintsTest;
import com.clarkparsia.pellint.test.rdfxml.DoubtfulSetTest;
import com.clarkparsia.pellint.test.rdfxml.OWLDatatypeTest;
import com.clarkparsia.pellint.test.rdfxml.OWLSyntaxCheckerTest;
import com.clarkparsia.pellint.test.rdfxml.RDFModelTest;
import com.clarkparsia.pellint.test.util.OWL2DLProfileViolationsTest;
import com.clarkparsia.pellint.test.util.OptimizedDirectedMultigraphTest;

/**
 * <p>
 * Title: Pellint test suite
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
@RunWith(Suite.class)
@Suite.SuiteClasses({
	DoubtfulSetTest.class,
	RDFModelTest.class,
	OptimizedDirectedMultigraphTest.class,
	LintTest.class,
	LintFixerTest.class,
	OntologyLintsTest.class,
	LintPatternLoaderTest.class,
	EquivalentToAllValuePatternTest.class,
	EquivalentToComplementPatternTest.class,
	EquivalentToTopPatternTest.class,
	GCIPatternTest.class,
	LargeCardinalityPatternTest.class,
	LargeDisjunctionPatternTest.class,
	EquivalentAndSubclassAxiomPatternTest.class,
	ExistentialExplosionPatternTest.class,
	TooManyDifferentIndividualsPatternTest.class,
	OWLSyntaxCheckerTest.class,
	OWLDatatypeTest.class,
	OWL2DLProfileViolationsTest.class
})
public class PellintTestSuite {
	
	public static Test suite() { 
		return new JUnit4TestAdapter( PellintTestSuite.class );
	}
	
}