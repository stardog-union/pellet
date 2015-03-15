package com.clarkparsia.pellet.owlapiv3;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

/**
 * <p>
 * Title: Limited Map IRI Mapper
 * </p>
 * <p>
 * Description: Only allow IRIs that have been explicitly mapped.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
public class LimitedMapIRIMapper implements OWLOntologyIRIMapper {

	private Map<IRI, IRI> allowed;
	
	public LimitedMapIRIMapper() {
		allowed = new HashMap<IRI, IRI>();
	}
	
	public void addAllowedIRI(IRI ontologyIRI) {
		addAllowedIRI( ontologyIRI, ontologyIRI );
	}
	
	public void addAllowedIRI(IRI fromIRI, IRI toIRI) {
		allowed.put( fromIRI, toIRI );
	}
	
	public void clear() {
		allowed.clear();
	}
	
	public IRI getDocumentIRI(IRI ontologyIRI) {
		return allowed.get( ontologyIRI );
	}

}
