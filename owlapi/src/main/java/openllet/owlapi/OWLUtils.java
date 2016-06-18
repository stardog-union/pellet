package openllet.owlapi;

import com.clarkparsia.owlapi.OWL;
import java.io.File;
import java.util.Optional;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

/**
 * Utils to play with singletons.
 * 
 * @since 2.5.1
 */
public class OWLUtils
{
	//	private static final Logger _logger = Log.getLogger(OWLUtils.class);

	/**
	 * @param iri an iri that is potentially valid or with a namespace separator.
	 * @return The iri without the part that show the namespace as separate object as the individual name.
	 * @since 2.5.1
	 */
	static public String iriModel2iri(final String iri)
	{
		return (!iri.startsWith("{")) ? iri : iri.replaceAll("[\\{\\}]", "");
	}

	static private OWLManagerGroup _owlManagerGroup = new OWLManagerGroup(Optional.of(OWL._manager), Optional.empty());

	static public OWLManagerGroup getOwlManagerGroup()
	{
		return _owlManagerGroup;
	}

	static public void loadDirectory(final File directory)
	{
		_owlManagerGroup.loadDirectory(directory);
	}

	public static String ontology2filename(final OWLOntologyID ontId)
	{
		return _owlManagerGroup.ontology2filename(ontId);
	}

	public static String ontology2filename(final OWLOntology ontology)
	{
		return _owlManagerGroup.ontology2filename(ontology);
	}
}
