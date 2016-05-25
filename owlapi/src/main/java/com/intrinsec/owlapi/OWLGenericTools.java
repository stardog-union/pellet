package com.intrinsec.owlapi;

import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.katk.tools.Log;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The main difference between OWLTools and OWLGenericTools is the usage of static resources by OWLTools.
 * 
 * @since 2.5.1
 */
public class OWLGenericTools implements OWLHelper
{
	private static final Logger _logger = Log.getLogger(OWLGenericTools.class);

	@Override
	public Logger getLogger()
	{
		return _logger;
	}

	/**
	 * Ontology denote the current ontology. So it can change in version of environment.
	 * 
	 * @since 2.5.1
	 */
	protected volatile OWLOntology _ontology;

	@Override
	public OWLOntology getOntology()
	{
		return _ontology;
	}

	protected final OWLOntologyManager _manager;

	@Override
	public OWLOntologyManager getManager()
	{
		return _manager;
	}

	protected final OWLManagerGroup _group;

	@Override
	public OWLManagerGroup getGroup()
	{
		return _group;
	}

	protected boolean _isVolatile = true;

	@Override
	public boolean isVolatile()
	{
		return _isVolatile;
	}

	@Override
	public OWLDataFactory getFactory()
	{
		return _manager.getOWLDataFactory();
	}

	private Optional<PelletReasoner> _pelletReasoner = Optional.empty();

	@Override
	public PelletReasoner getReasoner()
	{

		if (!_pelletReasoner.isPresent())
		{
			try
			{

				final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(getOntology());
				reasoner.isConsistent();
				_pelletReasoner = Optional.of(reasoner);
			}
			catch (final Exception e)
			{
				_logger.log(Level.SEVERE, "", e);
			}
		}

		_pelletReasoner.get().flush();
		return _pelletReasoner.get();
	}

	/**
	 * Load the ontology ontologyId into the good manager or create it if it doesn't previously exist.
	 * 
	 * @param group of managers.
	 * @param ontologyID is the reference to the ontology
	 * @throws OWLOntologyCreationException is raise when things goes baddly wrong.
	 * @since 2.5.1
	 */
	public OWLGenericTools(final OWLManagerGroup group, final OWLOntologyID ontologyID) throws OWLOntologyCreationException
	{
		if (ontologyID.getOntologyIRI().toString().indexOf(' ') != -1)
		{
			throw new OWLOntologyCreationException("Illegal character ' ' in name on [" + ontologyID.getOntologyIRI() + "]");
		}
		else
			if (ontologyID.getVersionIRI() != null && ontologyID.getVersionIRI().toString().indexOf(' ') != -1) { throw new OWLOntologyCreationException("Illegal character ' ' in version on [" + ontologyID.getVersionIRI() + "]"); }
		_group = group;

		// Maybe already in a manager.
		if (group.getOntologiesDirectory().isPresent())
		{
			_ontology = OWLManagerGroup.getOntology(group.getStorageManager(), ontologyID).orElse(null);
		}

		if (_ontology != null)
		{
			_manager = group.getStorageManager();
		}
		else
		{ // Not in storage manager
			_ontology = OWLManagerGroup.getOntology(group.getVolatileManager(), ontologyID).orElse(null);
			if (_ontology != null)
			{
				_manager = group.getVolatileManager();
			}
			else
			{ // Not in volatile manager.
				// Maybe we should load it.
				_manager = group.getOntologiesDirectory().isPresent() ? group.getStorageManager() : group.getVolatileManager();
				final File file = new File(group.ontology2filename(ontologyID));
				if (file.exists())
				{
					try
					{
						_ontology = _manager.loadOntologyFromOntologyDocument(file);
					}
					catch (final Exception e)
					{
						_logger.log(Level.INFO, "Ontology " + ontologyID + " couldn't be load", e);
					}
				}
				if (_ontology == null) // Maybe we should create it.
				{
					_logger.log(Level.FINE, "Ontology " + ontologyID + " will be create now");
					_ontology = _manager.createOntology(ontologyID);
				}
			}
		}

		_isVolatile = _manager == group.getVolatileManager();
		OWLHelper.setFormat(_manager, _ontology);
	}

	public OWLGenericTools(final OWLManagerGroup group, final OWLOntologyID ontologyID, final boolean isVolatile) throws OWLOntologyCreationException
	{
		if (ontologyID.getOntologyIRI().toString().indexOf(' ') != -1)
		{
			throw new OWLOntologyCreationException("Illegal character ' ' in name on [" + ontologyID.getOntologyIRI() + "]");
		}
		else
			if (ontologyID.getVersionIRI() != null && ontologyID.getVersionIRI().toString().indexOf(' ') != -1) { throw new OWLOntologyCreationException("Illegal character ' ' in version on [" + ontologyID.getVersionIRI() + "]"); }

		_group = group;
		_isVolatile = isVolatile;
		_manager = _isVolatile ? group.getVolatileManager() : group.getStorageManager();
		_ontology = OWLManagerGroup.getOntology(_manager, ontologyID).orElse(null); // Maybe already in the manager.

		// Maybe we should load it.
		if (_ontology == null && !_isVolatile)
		{
			{
				final File file = new File(group.ontology2filename(ontologyID));
				if (file.exists())
				{
					try
					{
						_ontology = _manager.loadOntologyFromOntologyDocument(file);
					}
					catch (final Exception e)
					{
						_logger.log(Level.INFO, "Ontology " + ontologyID + " couldn't be load", e);
					}
				}
			}
		}

		if (_ontology == null)
		{
			_logger.log(Level.INFO, "Ontology " + ontologyID + " will be create now");
			_ontology = _manager.createOntology(ontologyID);// Maybe we should create it.
		}

		OWLHelper.setFormat(_manager, _ontology);
	}

	public OWLGenericTools(final OWLManagerGroup group, final IRI ontologyIRI, final double version) throws OWLOntologyCreationException
	{
		this(group, new OWLOntologyID(ontologyIRI, OWLHelper.buildVersion(ontologyIRI, version)));
	}

	public OWLGenericTools(final OWLManagerGroup group, final IRI ontologyIRI, final double version, final boolean isVolatile) throws OWLOntologyCreationException
	{
		this(group, new OWLOntologyID(ontologyIRI, OWLHelper.buildVersion(ontologyIRI, version)), isVolatile);
	}

	public OWLGenericTools(final OWLManagerGroup group, final IRI ontologyIRI, final boolean isVolatile) throws OWLOntologyCreationException
	{
		this(group, ontologyIRI, 0, isVolatile);
	}

	protected OWLGenericTools(final OWLManagerGroup group, final InputStream is) throws OWLOntologyCreationException
	{
		_group = group;
		_manager = group.getVolatileManager();
		_ontology = _manager.loadOntologyFromOntologyDocument(is);
		_isVolatile = true;
		OWLHelper.setFormat(_manager, _ontology);
	}

	public OWLGenericTools(final OWLManagerGroup group, final OWLOntologyManager manager, final OWLOntology ontology)
	{
		_group = group;
		_manager = manager;
		_ontology = ontology;
		_isVolatile = manager == group.getVolatileManager();
		OWLHelper.setFormat(_manager, _ontology);
		group.check(getManager());
	}

	public OWLGenericTools(final OWLManagerGroup group, final OWLOntologyManager manager, final File file) throws OWLOntologyCreationException
	{
		_group = group;
		_manager = manager;
		_ontology = _manager.loadOntologyFromOntologyDocument(file);
		_isVolatile = manager == group.getVolatileManager();
		OWLHelper.setFormat(_manager, _ontology);
		group.check(getManager());
	}

	// Raw create
	public OWLGenericTools(final OWLManagerGroup group, final OWLOntology ontology, final OWLOntologyManager manager, final Map<String, String> namespaces)
	{
		_group = group;
		_manager = manager;
		_ontology = ontology;
		if (!namespaces.isEmpty())
			getNamespaces().ifPresent(space -> namespaces.forEach(space::setPrefix));
		_isVolatile = manager == group.getVolatileManager();
		OWLHelper.setFormat(_manager, _ontology);
		group.check(getManager());
	}

	public OWLGenericTools(final OWLManagerGroup group, final File file) throws Exception
	{
		this(group, group.getStorageManager(), file);
	}

	@Override
	public String toString()
	{
		return getOntology().axioms().map(OWLAxiom::toString).sorted().collect(Collectors.joining("\n"));
	}
}
