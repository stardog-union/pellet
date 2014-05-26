package com.clarkparsia.owlwg.owlapi3.testcase.impl;

import java.util.EnumMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.MissingImportHandlingStrategy;

import com.clarkparsia.owlwg.testcase.AbstractEntailmentTest;
import com.clarkparsia.owlwg.testcase.EntailmentTest;
import com.clarkparsia.owlwg.testcase.OntologyParseException;
import com.clarkparsia.owlwg.testcase.SerializationFormat;

/**
 * <p>
 * Title: OWLAPIv3 Entailment Test Case Base Class
 * </p>
 * <p>
 * Description: Extended for positive and negative entailment cases
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
public abstract class OwlApi3ETImpl extends AbstractEntailmentTest<OWLOntology> implements
		EntailmentTest<OWLOntology>, OwlApi3Case {

	private final EnumMap<SerializationFormat, OWLOntology>	parsedConclusion;
	private final EnumMap<SerializationFormat, OWLOntology>	parsedPremise;
    protected OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration()
            .setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);

	public OwlApi3ETImpl(OWLOntology ontology, OWLNamedIndividual i, boolean positive) {
		super( ontology, i, positive );

		parsedPremise = new EnumMap<SerializationFormat, OWLOntology>( SerializationFormat.class );
		parsedConclusion = new EnumMap<SerializationFormat, OWLOntology>( SerializationFormat.class );
	}

	public OWLOntology parseConclusionOntology(SerializationFormat format)
			throws OntologyParseException {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            manager.getIRIMappers().clear();

            ImportsHelper.loadImports(manager, this, format, config);
			OWLOntology o = parsedConclusion.get( format );
			if( o == null ) {
				String l = getConclusionOntology( format );
				if( l == null )
					return null;

				StringDocumentSource source = new StringDocumentSource( l );
                o = OWLManager.createOWLOntologyManager()
                        .loadOntologyFromOntologyDocument(source, config);
				parsedConclusion.put( format, o );
			}
			return o;
		} catch( OWLOntologyCreationException e ) {
			throw new OntologyParseException( e );
		}
	}

	public OWLOntology parsePremiseOntology(SerializationFormat format)
			throws OntologyParseException {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            manager.getIRIMappers().clear();
            ;

            ImportsHelper.loadImports(manager, this, format, config);
			OWLOntology o = parsedPremise.get( format );
			if( o == null ) {
				String l = getPremiseOntology( format );
				if( l == null )
					return null;

				StringDocumentSource source = new StringDocumentSource( l );
                o = manager.loadOntologyFromOntologyDocument(source, config);
				parsedPremise.put( format, o );
			}
			return o;
		} catch( OWLOntologyCreationException e ) {
			throw new OntologyParseException( e );
		}
	}
}