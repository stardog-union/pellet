package openllet.owlapi;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Group of Owl Manager, volatile and persistent.
 * 
 * @since 2.5.1
 */
public class OWLManagerGroup implements AutoCloseable
{
	private static final Logger _logger = Log.getLogger(OWLManagerGroup.class);

	public OWLManagerGroup()
	{
		/**/
	}

	public OWLManagerGroup(final File ontologiesDirectory)
	{
		setOntologiesDirectory(ontologiesDirectory);
	}

	public OWLManagerGroup(final Optional<OWLOntologyManager> volatileManager, final Optional<OWLOntologyManager> storageManager)
	{
		volatileManager.ifPresent(m -> _volatileManager = m);
		storageManager.ifPresent(m -> _storageManager = m);
	}

	public volatile Optional<File> _ontologiesDirectory = Optional.empty();

	public boolean setOntologiesDirectory(final File directory)
	{
		_ontologiesDirectory = Optional.of(directory);
		return _ontologiesDirectory.isPresent();
	}

	public Optional<File> getOntologiesDirectory()
	{
		return _ontologiesDirectory;
	}

	public volatile OWLOntologyManager _volatileManager = null;

	public OWLOntologyManager getVolatileManager()
	{
		if (_volatileManager == null)
		{
			_volatileManager = OWLManager.createConcurrentOWLOntologyManager();
		}

		return _volatileManager;
	}

	public volatile OWLOntologyManager _storageManager = null;

	/**
	 * @return The storage manager if you have call setOntologiesDirectory() before; else it throw a RuntimeException.
	 * @since 1.1
	 */
	public synchronized OWLOntologyManager getStorageManager()
	{
		if (_storageManager == null)
		{
			_storageManager = OWLManager.createConcurrentOWLOntologyManager();

			if (!getOntologiesDirectory().isPresent())
			{
				final String msg = "You should define a directory for stored ontologies before using stored ontologies.";
				_logger.log(Level.SEVERE, msg, new OWLException(msg));
				throw new OWLException(msg);
			}

			try
			{
				_storageListener = new OWLStorageManagerListener(getOntologiesDirectory().get(), new File(getOntologiesDirectory().get().getPath() + OWLHelper._delta), this);
				getStorageManager().addOntologyChangeListener(_storageListener);
			}
			catch (final Exception e)
			{
				throw new OWLException(e);
			}
		}

		return _storageManager;
	}

	private OWLStorageManagerListener _storageListener;

	public void loadDirectory(final File directory)
	{
		if (!directory.exists())
		{
			if (!directory.mkdir()) { throw new OWLException("Can't create the directory " + directory + " ."); }
		}

		if (!directory.isDirectory()) { throw new OWLException("The directory parameter must be a true existing directory. " + directory + " isn't."); }

		for (final File file : directory.listFiles())
		{
			if (file.isFile() && file.canRead() && file.getName().endsWith(OWLHelper._fileExtention))
			{
				try
				{
					_logger.info("loading from " + file);
					// We just want the ontology to be put into the manager and configuration set to our standard. We don't care of the tools for now.
					final OWLOntologyManager manager = getStorageManager();
					final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file); // The side effect is wanted.
					OWLHelper.setFormat(manager, ontology);
					_logger.info(ontology.getOntologyID() + "loaded from " + file);
				}
				catch (final Exception e)
				{
					_logger.log(Level.SEVERE, "Can't load ontology of file " + file, e);
				}
			}
			else
			{
				_logger.info(file + " will not be load.");
			}
		}
	}

	/**
	 * Seek the asked ontology. First in the volatile ontologies, then in the stored ontologies that are already stored.
	 * 
	 * @param ontologyID the id of the ontology you are looking for.
	 * @return an ontology if found.
	 * @since 2.5.1
	 */
	public Optional<OWLOntology> getOntology(final OWLOntologyID ontologyID)
	{
		final Optional<OWLOntology> ontology = getOntology(getVolatileManager(), ontologyID);
		return ontology.isPresent() ? ontology : getOntology(getStorageManager(), ontologyID);
	}

	/**
	 * The standard 'getOntology' from the OWLManager don't really take care of versionning. This function is here to enforce the notion of version
	 * 
	 * @param manager to look into (mainly storage or volatile)
	 * @param ontologyID with version information
	 * @return the ontology if already load into the given manager.
	 * @since 2.5.1
	 */
	public static Optional<OWLOntology> getOntology(final OWLOntologyManager manager, final OWLOntologyID ontologyID)
	{
		final Optional<IRI> ontIri = ontologyID.getOntologyIRI();
		if (!ontIri.isPresent())
			return Optional.empty();

		final Optional<IRI> verIri = ontologyID.getVersionIRI();

		if (verIri.isPresent())
			return manager.versions(ontIri.get()).findAny();
		else
			return manager.versions(ontIri.get())//
					.filter(candidat ->
					{
						final Optional<IRI> version = candidat.getOntologyID().getVersionIRI();
						return version.isPresent() && version.get().equals(verIri.get());
					})//
					.findAny();
	}

	public String ontology2filename(final OWLOntologyID ontId)
	{
		if (_ontologiesDirectory.isPresent()) { return OWLHelper.ontology2filename(_ontologiesDirectory.get(), ontId); }
		throw new OWLException("Storage directory should be define to enable loading of ontology by iri.");
	}

	public String ontology2filename(final OWLOntology ontology)
	{
		return ontology2filename(ontology.getOntologyID());
	}

	void check(final OWLOntologyManager manager)
	{
		if (manager == _volatileManager || manager == _storageManager) { return; }
		throw new OWLException("The given manager isn't know from in the OWLManagerGroup. Check your manager usage.");
	}

	/**
	 * Free all in memory resource. The 'in memory' space taken by the persistent data is also free, but the persistent is maintain for future usage. The
	 * storage system is disable.
	 * 
	 * @since 2.5.1
	 */
	@Override
	public void close()
	{
		if (this == OWLUtils.getOwlManagerGroup())
		{
			_logger.log(Level.WARNING, "You try to close a static resource that should never be closed.");
			return;
		}

		if (_volatileManager != null)
		{
			_volatileManager.ontologies().forEach(_volatileManager::removeOntology);
			_volatileManager.getIRIMappers().clear();
			_volatileManager = null; // Mark for GC.
		}
		if (_storageManager != null)
		{
			_storageManager.ontologies().forEach(_storageManager::removeOntology);
			_storageListener.close();
			_storageManager.removeOntologyChangeListener(_storageListener);
			_storageManager.getIRIMappers().clear();
			_storageManager = null; // Mark for GC.
		}
	}

	/**
	 * Connect to the speficied ontology.
	 * 
	 * @param ontology is the pointer to the resource
	 * @param version of the designated ontology
	 * @param persistent or not.
	 * @return An ontology tool set that allow you the management of the requested ontology.
	 * @since 2.5.1
	 */
	public OWLHelper connect(final IRI ontology, final double version, final boolean persistent)
	{
		return connect(computeOwlApiOntologyId(ontology, version), persistent);
	}

	public OWLHelper connect(final IRI ontology, final boolean persistent)
	{
		return connect(ontology, 0., persistent);
	}

	public OWLHelper connect(final OWLOntologyID id, final boolean persistent)
	{
		try
		{
			return new OWLGenericTools(this, id, !persistent);
		}
		catch (final OWLOntologyCreationException exception)
		{
			throw new OWLException(exception);
		}
	}

	public OWLOntologyID computeOwlApiOntologyId(final IRI ontologyIRI, final double version)
	{
		return new OWLOntologyID(ontologyIRI, OWLHelper.buildVersion(ontologyIRI, version));
	}
}
