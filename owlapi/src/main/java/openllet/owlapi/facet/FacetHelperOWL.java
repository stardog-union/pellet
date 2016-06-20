package openllet.owlapi.facet;

import openllet.owlapi.OWLHelper;

/**
 * An object that is not itself an ontology but have one.
 * 
 * @since 2.5.1
 */
public interface FacetHelperOWL
{
	abstract public OWLHelper getKB();
}
