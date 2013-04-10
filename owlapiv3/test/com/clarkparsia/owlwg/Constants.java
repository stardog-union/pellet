package com.clarkparsia.owlwg;

import static com.clarkparsia.owlwg.Constants.OWLWG_BASE_PHYSICAL_URI;

import java.io.File;
import java.net.URI;

import org.semanticweb.owlapi.model.IRI;

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
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class Constants {
	public static final URI OWLWG_BASE_PHYSICAL_URI;
	public static final IRI	RESULTS_ONTOLOGY_PHYSICAL_IRI;
	public static final IRI	TEST_ONTOLOGY_PHYSICAL_IRI;
	public static final IRI OWLWG_TEST_CASES_IRI;

	static {
		File f = new File( "owlapiv3/test_data/owlwg/" );
		OWLWG_BASE_PHYSICAL_URI = f.toURI();

		TEST_ONTOLOGY_PHYSICAL_IRI = IRI.create( OWLWG_BASE_PHYSICAL_URI.resolve( "ontologies/test-ontology.owl" ) );

		RESULTS_ONTOLOGY_PHYSICAL_IRI = IRI.create( OWLWG_BASE_PHYSICAL_URI.resolve( "ontologies/results-ontology.owl" ) );

		OWLWG_TEST_CASES_IRI = IRI
				.create( OWLWG_BASE_PHYSICAL_URI.resolve( "semantics-direct.rdf" ) );
	}
}
