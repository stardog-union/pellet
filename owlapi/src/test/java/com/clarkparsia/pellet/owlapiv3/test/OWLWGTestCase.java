package com.clarkparsia.pellet.owlapiv3.test;

import static com.clarkparsia.owlwg.Constants.OWLWG_TEST_CASES_IRI;
import static com.clarkparsia.owlwg.Constants.RESULTS_ONTOLOGY_PHYSICAL_IRI;
import static com.clarkparsia.owlwg.Constants.TEST_ONTOLOGY_PHYSICAL_IRI;
import static org.junit.Assert.fail;

import com.clarkparsia.owlwg.owlapi.testcase.impl.OwlApiTestCaseFactory;

import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import com.clarkparsia.owlwg.TestCollection;
import com.clarkparsia.owlwg.cli.FilterConditionParser;
import com.clarkparsia.owlwg.runner.pellet.PelletOA3TestRunner;
import com.clarkparsia.owlwg.testcase.TestCase;
import com.clarkparsia.owlwg.testcase.filter.FilterCondition;
import com.clarkparsia.owlwg.testrun.RunResultType;
import com.clarkparsia.owlwg.testrun.TestRunResult;


@Ignore("Failing tests")
@RunWith(Parameterized.class)
public class OWLWGTestCase {
    
    /**
     * Ensure that test cases timeout after 30 seconds.
     * 
     * This is in slightly broader than the 20 second timeout for each PelletOA3TestRunner.
     */
    @Rule
    public Timeout timeout = new Timeout(30000);
    
	@Parameters
    public static List<Object[]> data() throws OWLOntologyCreationException, OWLOntologyChangeException {
    	final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	OwlApiTestCaseFactory factory = new OwlApiTestCaseFactory();
    	
    	FilterCondition filter = FilterConditionParser.parse( "approved proposed extracredit or direct dl and" );
    	
    	
    	/*
		 * Load the test and results ontology from local files before
		 * reading the test cases, otherwise import of them is likely to
		 * fail.
		 */
		manager.loadOntologyFromOntologyDocument( OWLWGTestCase.class.getResourceAsStream(TEST_ONTOLOGY_PHYSICAL_IRI) );
		manager.loadOntologyFromOntologyDocument( OWLWGTestCase.class.getResourceAsStream(RESULTS_ONTOLOGY_PHYSICAL_IRI) );

		OWLOntology casesOntology = manager.loadOntologyFromOntologyDocument( OWLWGTestCase.class.getResourceAsStream(OWLWG_TEST_CASES_IRI) );
		
		try {
    		TestCollection<OWLOntology> cases = new TestCollection<>( factory, casesOntology, filter );
    		
    		List<Object[]> testParams = new ArrayList<>( cases.size() );
    		for( Object test : cases.asList() ) {
    			testParams.add( new Object[] { test } );
    		}
    		
        	return testParams;
		} finally {
    	       manager.removeOntology( casesOntology );
    	}
    }
    
    private TestCase<OWLOntology> test;
    
    public OWLWGTestCase( TestCase<OWLOntology> test ) {
    	this.test = test;
    }
    
	@Test
	public void runTestCase() {
		try {
			PelletOA3TestRunner runner = new PelletOA3TestRunner();
			for( TestRunResult result : runner.run( test, 10 * 1000 ) ) {
				RunResultType resultType = result.getResultType();
				if( !RunResultType.PASSING.equals( resultType ) ) {
					if( result.getCause() != null ) {
						// FIXME Can get rid of conditional once #295 is fixed.
						if ( ! ( result.getCause() instanceof FreshEntitiesException ) )
							throw new RuntimeException( test.getIdentifier(), result.getCause() );
					}
					else {
						fail( result.toString() );
					}
				}
			}
		} finally {
			test.dispose();
			test = null;
			System.gc();
		}
	}
    
    
    
}
