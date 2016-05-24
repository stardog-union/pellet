package com.clarkparsia.owlwg.owlapi.testcase.impl;

import com.clarkparsia.owlwg.testcase.AbstractEntailmentTest;
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
 * Title: OWLAPI Entailment Test Case Base Class
 * </p>
 * <p>
 * Description: Extended for positive and negative entailment cases
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
public abstract class OwlApiETImpl extends AbstractEntailmentTest<OWLOntology> implements OwlApiCase
{

	private final EnumMap<SerializationFormat, OWLOntology> parsedConclusion;
	private final EnumMap<SerializationFormat, OWLOntology> parsedPremise;

	public OwlApiETImpl(OWLOntology ontology, OWLNamedIndividual i, boolean positive)
	{
		super(ontology, i, positive);

		parsedPremise = new EnumMap<>(SerializationFormat.class);
		parsedConclusion = new EnumMap<>(SerializationFormat.class);
	}

	@Override
	public OWLOntology parseConclusionOntology(SerializationFormat format) throws OntologyParseException
	{
		try
		{
			final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
			manager.getIRIMappers().clear();

			ImportsHelper.loadImports(manager, this, format);
			OWLOntology o = parsedConclusion.get(format);
			if (o == null)
			{
				final String l = getConclusionOntology(format);
				if (l == null) { return null; }

				final StringDocumentSource source = new StringDocumentSource(l);
				o = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(source);
				parsedConclusion.put(format, o);
			}
			return o;
		}
		catch (final OWLOntologyCreationException e)
		{
			throw new OntologyParseException(e);
		}
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
