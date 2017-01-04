/**
 * 
 */
package com.clarkparsia.jena.test;

import java.io.IOException;
import java.io.InputStream;

import com.clarkparsia.pellet.test.query.MiscSPARQLDLTest;
import org.apache.jena.ontology.OntDocumentManager.ReadFailureHandler;
import org.apache.jena.rdf.model.Model;

/**
 * @author Pavel Klinov
 *
 */
public class ResourceImportLoader implements ReadFailureHandler {

	/* (non-Javadoc)
	 * @see org.apache.jena.ontology.OntDocumentManager.ReadFailureHandler#handleFailedRead(java.lang.String, org.apache.jena.rdf.model.Model, java.lang.Exception)
	 */
	@Override
	public void handleFailedRead(String url, Model m, Exception e) {
		//FIXME
		System.out.println("+++" + url + "+++");
		
		if (url.startsWith("resource:")) {
			String resourceName = "/" + url.substring(9);
			InputStream resourceStream = null;
		
			try {
				resourceStream = MiscSPARQLDLTest.class.getResourceAsStream(resourceName);
		
				m.read(resourceStream, null);
			}
			finally {
				if (resourceStream != null) {
					try {
						resourceStream.close();
					} catch (IOException e1) {
					}
				}
			}
		}

	}

}
