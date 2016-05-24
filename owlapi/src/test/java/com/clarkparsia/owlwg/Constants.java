package com.clarkparsia.owlwg;

/**
 * <p>
 * Title: Constants
 * </p>
 * <p>
 * Description: Constants used by multiple classes
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class Constants
{
	public static final String OWLWG_BASE_PHYSICAL_URI;
	public static final String RESULTS_ONTOLOGY_PHYSICAL_IRI;
	public static final String TEST_ONTOLOGY_PHYSICAL_IRI;
	public static final String OWLWG_TEST_CASES_IRI;

	static
	{
		//File f = new File( "owlapiv3/test_data/owlwg/" );
		OWLWG_BASE_PHYSICAL_URI = "/test_data/owlwg/";

		TEST_ONTOLOGY_PHYSICAL_IRI = OWLWG_BASE_PHYSICAL_URI + "ontologies/test-ontology.owl";

		RESULTS_ONTOLOGY_PHYSICAL_IRI = OWLWG_BASE_PHYSICAL_URI + "ontologies/results-ontology.owl";

		OWLWG_TEST_CASES_IRI = OWLWG_BASE_PHYSICAL_URI + "semantics-direct.rdf";
	}
}
