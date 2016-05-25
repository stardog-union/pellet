package com.intrinsec.owlapi.facet;

import com.intrinsec.owlapi.OWLHelper;

/**
 * An object that is not itself an ontology but have one.
 * 
 * @since 2.5.1
 */
public interface FacetHelperOWL
{
	abstract public OWLHelper getKB();
}
