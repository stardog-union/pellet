package openllet.owlapi.facet;

import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @return the manager that manage the current ontology.
 * @since 2.5.1
 */
public interface FacetManagerOWL
{
	/**
	 * @return the manager that manage the current ontology.
	 * @since 2.5.1
	 */
	public OWLOntologyManager getManager();
}
