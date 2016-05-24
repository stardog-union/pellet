package com.clarkparsia.owlwg.owlapi.testcase.impl;

import com.clarkparsia.owlwg.testcase.AbstractPremisedTest;
import com.clarkparsia.owlwg.testcase.OntologyParseException;
import com.clarkparsia.owlwg.testcase.SerializationFormat;
import java.util.EnumMap;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * <p>
 * Title: OWLAPI xConsistency Test Case Base Class
 * </p>
 * <p>
 * Description: Extended for consistency and inconsistency cases
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public abstract class OwlApixCTImpl extends AbstractPremisedTest<OWLOntology> implements OwlApiCase
{

	private final EnumMap<SerializationFormat, OWLOntology> parsedPremise;

	public OwlApixCTImpl(OWLOntology ontology, OWLNamedIndividual i)
	{
		super(ontology, i);

		parsedPremise = new EnumMap<>(SerializationFormat.class);
	}

	@Override
	public void dispose()
	{
		parsedPremise.clear();
		super.dispose();
	}

	@Override
	public OWLOntology parsePremiseOntology(SerializationFormat format) throws OntologyParseException
	{
		try
		{
			final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
			manager.getIRIMappers().clear();

			ImportsHelper.loadImports(manager, this, format);
			OWLOntology o = parsedPremise.get(format);
			if (o == null)
			{
				final String l = getPremiseOntology(format);
				if (l == null) { return null; }

				final StringDocumentSource source = new StringDocumentSource(l);
				o = manager.loadOntologyFromOntologyDocument(source);
				parsedPremise.put(format, o);
			}
			return o;
		}
		catch (final OWLOntologyCreationException e)
		{
			throw new OntologyParseException(e);
		}
	}
}
