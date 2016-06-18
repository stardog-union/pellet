package openllet.owlapi;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Management of ontologies that have a reference on self.
 * 
 * @since 2.5.1
 */
public class OWLSelfTools extends OWLTools implements OWLManagedObject
{

	private final OWLNamedIndividual _me;

	@Override
	public OWLNamedIndividual getMe()
	{
		return _me;
	}

	public OWLSelfTools(final IRI ontologyIRI, final double version) throws OWLOntologyCreationException
	{
		super(ontologyIRI, version);
		_me = getFactory().getOWLNamedIndividual(ontologyIRI);
	}
}
