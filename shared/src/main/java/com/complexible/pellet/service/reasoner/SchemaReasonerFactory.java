package com.complexible.pellet.service.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Edgar Rodriguez-Diaz
 */
public interface SchemaReasonerFactory {

	SchemaReasoner create(final OWLOntology ontology);

}
