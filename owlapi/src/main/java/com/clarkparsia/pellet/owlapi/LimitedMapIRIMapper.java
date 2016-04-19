package com.clarkparsia.pellet.owlapi;

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
public class LimitedMapIRIMapper implements OWLOntologyIRIMapper
{

	/**
	 * TODO
	 *
	 * @since
	 */
	private static final long serialVersionUID = 6168686462330770641L;
	private final Map<IRI, IRI> allowed;

	public LimitedMapIRIMapper()
	{
		allowed = new HashMap<>();
	}

	public void addAllowedIRI(final IRI ontologyIRI)
	{
		addAllowedIRI(ontologyIRI, ontologyIRI);
	}

	public void addAllowedIRI(final IRI fromIRI, final IRI toIRI)
	{
		allowed.put(fromIRI, toIRI);
	}

	public void clear()
	{
		allowed.clear();
	}

	@Override
	public IRI getDocumentIRI(final IRI ontologyIRI)
	{
		return allowed.get(ontologyIRI);
	}

}
