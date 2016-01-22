/**
 * 
 */
package com.clarkparsia.jena.test;

import com.clarkparsia.pellet.test.query.MiscSPARQLDLTest;
import com.hp.hpl.jena.ontology.OntDocumentManager.ReadFailureHandler;
import com.hp.hpl.jena.rdf.model.Model;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Pavel Klinov
 *
 */
public class ResourceImportLoader implements ReadFailureHandler
{

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.ontology.OntDocumentManager.ReadFailureHandler#handleFailedRead(java.lang.String, com.hp.hpl.jena.rdf.model.Model, java.lang.Exception)
	 */
	@Override
	public void handleFailedRead(String url, Model m, Exception e)
	{
		//FIXME
		System.out.println("+++" + url + "+++");

		if (url.startsWith("resource:"))
		{
			final String resourceName = "/" + url.substring(9);

			try (InputStream resourceStream = MiscSPARQLDLTest.class.getResourceAsStream(resourceName))
			{
				m.read(resourceStream, null);
			}
			catch (final IOException exception)
			{
				exception.printStackTrace();
			}
		}

	}

}
