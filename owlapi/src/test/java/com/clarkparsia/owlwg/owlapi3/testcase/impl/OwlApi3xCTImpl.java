package com.clarkparsia.owlwg.owlapi3.testcase.impl;

import java.util.EnumMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.owlwg.testcase.AbstractPremisedTest;
import com.clarkparsia.owlwg.testcase.OntologyParseException;
import com.clarkparsia.owlwg.testcase.PremisedTest;
import com.clarkparsia.owlwg.testcase.SerializationFormat;

/**
 * <p>
 * Title: OWLAPIv3 xConsistency Test Case Base Class
 * </p>
 * <p>
 * Description: Extended for consistency and inconsistency cases
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
public abstract class OwlApi3xCTImpl extends AbstractPremisedTest<OWLOntology> implements
		PremisedTest<OWLOntology>, OwlApi3Case {

	private final EnumMap<SerializationFormat, OWLOntology>	parsedPremise;

	public OwlApi3xCTImpl(OWLOntology ontology, OWLNamedIndividual i) {
		super( ontology, i );

		parsedPremise = new EnumMap<>( SerializationFormat.class );
	}

	@Override
    public void dispose() {
		parsedPremise.clear();
		super.dispose();
	}

	@Override
    public OWLOntology parsePremiseOntology(SerializationFormat format)
			throws OntologyParseException {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            manager.setOntologyLoaderConfiguration(manager
                    .getOntologyLoaderConfiguration()
                    .setMissingImportHandlingStrategy(
                            MissingImportHandlingStrategy.SILENT));
			manager.clearIRIMappers();
			
			ImportsHelper.loadImports( manager, this, format );
			OWLOntology o = parsedPremise.get( format );
			if( o == null ) {
				String l = getPremiseOntology( format );
				if( l == null ) {
                    return null;
                }

				StringDocumentSource source = new StringDocumentSource( l );
				o = manager.loadOntologyFromOntologyDocument( source );
				parsedPremise.put( format, o );
			}
			return o;
		} catch( OWLOntologyCreationException e ) {
			throw new OntologyParseException( e );
		}
	}
}
