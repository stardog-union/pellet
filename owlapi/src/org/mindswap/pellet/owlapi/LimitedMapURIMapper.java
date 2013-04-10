package org.mindswap.pellet.owlapi;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owl.model.OWLOntologyURIMapper;

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
public class LimitedMapURIMapper implements OWLOntologyURIMapper {

	private Map<URI, URI> allowed;
	
	public LimitedMapURIMapper() {
		allowed = new HashMap<URI, URI>();
	}
	
	public void addAllowedURI(URI ontologyURI) {
		addAllowedURI( ontologyURI, ontologyURI );
	}
	
	public void addAllowedURI(URI fromURI, URI toURI) {
		allowed.put( fromURI, toURI );
	}
	
	public void clear() {
		allowed.clear();
	}
	
	public URI getPhysicalURI(URI ontologyURI) {
		return allowed.get( ontologyURI );
	}

}
