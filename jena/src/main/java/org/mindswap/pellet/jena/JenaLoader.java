// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;

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
public class JenaLoader extends KBLoader
{
	private static final FileManager manager = FileManager.get();

	private OntModel _model;

	private PelletInfGraph _pellet;

	public JenaLoader()
	{
		clear();
	}

	public PelletInfGraph getGraph()
	{
		return _pellet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KnowledgeBase getKB()
	{
		return _pellet.getKB();
	}

	public OntModel getModel()
	{
		return _model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load()
	{
		_pellet.prepare(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseFile(final String file)
	{
		manager.readModel(_model, file, _inputFormat);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear()
	{
		if (_model != null)
			_model.close();
		_model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		_pellet = (PelletInfGraph) _model.getGraph();
	}

	/**
	 * Used for the Jena loader
	 *
	 * @param _inputFormat
	 */
	public void setInputFormat(final String inputFormat)
	{
		this._inputFormat = inputFormat.toUpperCase();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIgnoreImports(final boolean ignoreImports)
	{
		_model.getDocumentManager().setProcessImports(!ignoreImports);
	}

}
