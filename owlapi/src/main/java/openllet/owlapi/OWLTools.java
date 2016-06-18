package openllet.owlapi;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The main difference between OWLTools and OWLGenericTools is the usage of static resources by OWLTools from OWLUtils. Statics resources have many usage for
 * resources management.
 * 
 * @since 2.5.1
 */
public class OWLTools extends OWLGenericTools
{
	private static final Logger _logger = Log.getLogger(OWLTools.class);

	@Override
	public Logger getLogger()
	{
		return _logger;
	}

	/**
	 * Load the ontology ontologyId into the good manager or create it if it doesn't previously exist.
	 * 
	 * @param ontologyID is the reference to the ontology
	 * @throws OWLOntologyCreationException is raise when things goes baddly wrong.
	 * @since 2.5.1
	 */
	public OWLTools(final OWLOntologyID ontologyID) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), ontologyID);
	}

	public OWLTools(final OWLOntologyID ontologyID, final boolean isVolatile) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), ontologyID, isVolatile);
	}

	public OWLTools(final IRI ontologyIRI, final double version) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), ontologyIRI, version);
	}

	public OWLTools(final IRI ontologyIRI, final double version, final boolean isVolatile) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), ontologyIRI, version, isVolatile);
	}

	public OWLTools(final IRI ontologyIRI, final boolean isVolatile) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), ontologyIRI, isVolatile);
	}

	protected OWLTools(final InputStream is) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), is);
	}

	public OWLTools(final OWLOntologyManager manager, final OWLOntology ontology)
	{
		super(OWLUtils.getOwlManagerGroup(), manager, ontology);
	}

	public OWLTools(final OWLOntologyManager manager, final File file) throws OWLOntologyCreationException
	{
		super(OWLUtils.getOwlManagerGroup(), manager, file);
	}

	// Raw create
	public OWLTools(final OWLOntology ontology, final OWLOntologyManager manager, final Map<String, String> namespaces)
	{
		super(OWLUtils.getOwlManagerGroup(), ontology, manager, namespaces);
	}

	public OWLTools(final File file) throws Exception
	{
		super(OWLUtils.getOwlManagerGroup(), file);
	}

	// Clone create
	public OWLTools(final OWLGenericTools tools)
	{
		this(tools.getOntology(), tools.getManager(), new HashMap<>());
	}
}
