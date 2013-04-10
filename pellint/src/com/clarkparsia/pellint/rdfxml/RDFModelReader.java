// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class RDFModelReader {
	public RDFModel read(String uri, boolean loadImports) {
		RDFModel m = new RDFModel();
		
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		model.getDocumentManager().setProcessImports( loadImports );
		
		FileManager.get().readModel( model, uri );

		StmtIterator stmtIter = model.listStatements();

		while( stmtIter.hasNext() ) {
			Statement stmt = stmtIter.nextStatement();
			m.addStatement( stmt );
		}

		return m;
	}
}
