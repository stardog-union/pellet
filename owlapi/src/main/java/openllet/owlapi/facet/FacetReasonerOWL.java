package openllet.owlapi.facet;

import com.clarkparsia.pellet.owlapi.PelletReasoner;

/**
 * @return a PelletReasoner that reason over this ontology.
 * @since 2.5.1
 */
public interface FacetReasonerOWL
{
	/**
	 * @return a PelletReasoner that reason over this ontology.
	 * @since 2.5.1
	 */
	public abstract PelletReasoner getReasoner();
}
