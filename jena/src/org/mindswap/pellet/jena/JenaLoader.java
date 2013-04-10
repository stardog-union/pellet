// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class JenaLoader extends KBLoader {
	private static final FileManager manager = FileManager.get();
	
	private OntModel		model;

	private PelletInfGraph	pellet;
	
	public JenaLoader() {
		clear();
	}

	public PelletInfGraph getGraph() {
		return pellet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KnowledgeBase getKB() {
		return pellet.getKB();
	}

	public OntModel getModel() {
		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		pellet.prepare( false );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseFile(String file) {
		manager.readModel( model, file, inputFormat );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		if( model != null )
			model.close();
		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		pellet = (PelletInfGraph) model.getGraph();
	}

	/**
	 * Used for the Jena loader
	 * 
	 * @param inputFormat
	 */
	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat.toUpperCase();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIgnoreImports(boolean ignoreImports) {
		model.getDocumentManager().setProcessImports( !ignoreImports );
	}
	
}
