package com.clarkparsia.owlwg.owlapi3.testcase.impl;

import static java.lang.String.format;

import java.util.logging.Logger;

import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.owlwg.testcase.SerializationFormat;

/**
 * <p>
 * Title: OWLAPIv3 Imports Helper
 * </p>
 * <p>
 * Description: Static implementation used to load imports for a test case into
 * the ontology manager
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
public class ImportsHelper {

	private final static Logger	log;

	static {
		log = Logger.getLogger( ImportsHelper.class.getCanonicalName() );
	}

	public static void loadImports(final OWLOntologyManager manager, final OwlApi3Case t,
 final SerializationFormat format,
            final OWLOntologyLoaderConfiguration config)
            throws OWLOntologyCreationException {

		for( IRI iri : t.getImportedOntologies() ) {
			if( !manager.contains( iri ) ) {
				String str = t.getImportedOntology( iri, format );
				if( str == null ) {
					final String msg = format(
							"Imported ontology (%s) not provided in " + format + " syntax for testcase (%s)",
							iri, t.getIdentifier() );
					log.warning( msg );
					throw new OWLOntologyCreationException( msg );
				}
				else {
                    StringDocumentSource source = new StringDocumentSource(str,
                            iri, null, null);
					try {
                        manager.loadOntologyFromOntologyDocument(source, config);
					} catch( OWLOntologyCreationException e ) {
						log.warning( format( "Failed to parse imported ontology for testcase (%s)",
								t.getIdentifier() ) );
						throw e;
					}
				}
			}
		}
	}

}
