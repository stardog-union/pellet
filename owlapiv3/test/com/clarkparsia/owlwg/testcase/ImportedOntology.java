package com.clarkparsia.owlwg.testcase;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

/**
 * <p>
 * Title: Imported Ontology
 * </p>
 * <p>
 * Description: See <a
 * href="http://www.w3.org/TR/owl2-test/#Imported_Ontologies">OWL 2
 * Conformance: Imported Ontologies</a>.
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
public interface ImportedOntology {

	public Set<SerializationFormat> getFormats();

	public String getOntology(SerializationFormat format);

	public IRI getIRI();
}