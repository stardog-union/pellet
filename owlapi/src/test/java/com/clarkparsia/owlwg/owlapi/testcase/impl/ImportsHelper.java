package com.clarkparsia.owlwg.owlapi.testcase.impl;

import static java.lang.String.format;

import com.clarkparsia.owlwg.testcase.SerializationFormat;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * <p>
 * Title: OWLAPI Imports Helper
 * </p>
 * <p>
 * Description: Static implementation used to load imports for a test case into the ontology manager
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
public class ImportsHelper
{
	private final static Logger _logger = Log.getLogger(ImportsHelper.class);

	public static void loadImports(final OWLOntologyManager manager, final OwlApiCase t, final SerializationFormat format) throws OWLOntologyCreationException
	{

		for (final IRI iri : t.getImportedOntologies())
		{
			if (!manager.contains(iri))
			{
				final String str = t.getImportedOntology(iri, format);
				if (str == null)
				{
					final String msg = format("Imported ontology (%s) not provided in " + format + " syntax for testcase (%s)", iri, t.getIdentifier());
					_logger.warning(msg);
					throw new OWLOntologyCreationException(msg);
				}
				else
				{
					final StringDocumentSource source = new StringDocumentSource(str, iri);
					try
					{
						manager.loadOntologyFromOntologyDocument(source);
					}
					catch (final OWLOntologyCreationException e)
					{
						_logger.warning(format("Failed to parse imported ontology for testcase (%s)", t.getIdentifier()));
						throw e;
					}
				}
			}
		}
	}

}
