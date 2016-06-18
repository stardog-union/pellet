// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.jena;

import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.apache.jena.graph.Capabilities;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.BaseInfGraph.InfFindSafeCapabilities;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerException;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.graph.loader.DefaultGraphLoader;

/**
 * @author Evren Sirin
 */
public class PelletReasoner implements Reasoner
{
	protected static Logger _logger = Log.getLogger(PelletReasoner.class);

	private final Model _reasonerCapabilities;

	private final Capabilities _graphCapabilities;

	private final Graph _schema;

	private boolean _fixedSchema;

	public PelletReasoner()
	{
		this(null, PelletReasonerFactory.theInstance().getCapabilities());
	}

	public PelletReasoner(final Graph schema)
	{
		this(schema, PelletReasonerFactory.theInstance().getCapabilities());
	}

	protected PelletReasoner(final Model reasonerCapabilities)
	{
		this(null, reasonerCapabilities);
	}

	protected PelletReasoner(final Graph schema, final Model reasonerCapabilities)
	{
		this._schema = schema;
		this._reasonerCapabilities = reasonerCapabilities;

		_graphCapabilities = new InfFindSafeCapabilities();
	}

	public Graph getSchema()
	{
		return _schema;
	}

	public boolean isFixedSchema()
	{
		return _fixedSchema;
	}

	public void setFixedSchema(final boolean fixedSchema)
	{
		this._fixedSchema = fixedSchema;
	}

	@Override
	public Reasoner bindSchema(final Graph graph) throws ReasonerException
	{
		return new PelletReasoner(graph, _reasonerCapabilities);
	}

	@Override
	public Reasoner bindSchema(final Model model) throws ReasonerException
	{
		return bindSchema(model.getGraph());
	}

	public Reasoner bindFixedSchema(final Graph graph) throws ReasonerException
	{
		final PelletReasoner reasoner = new PelletReasoner(graph, _reasonerCapabilities);
		reasoner.setFixedSchema(true);
		return reasoner;
	}

	public Reasoner bindFixedSchema(final Model model) throws ReasonerException
	{
		final PelletReasoner reasoner = new PelletReasoner(model.getGraph(), _reasonerCapabilities);
		reasoner.setFixedSchema(true);
		return reasoner;
	}

	@Override
	public PelletInfGraph bind(final Graph graph) throws ReasonerException
	{
		_logger.fine("In bind!");
		return new PelletInfGraph(graph, this, new DefaultGraphLoader());
	}

	public InfModel bind(final Model model) throws ReasonerException
	{
		_logger.fine("In bind!");
		return ModelFactory.createInfModel(bind(model.getGraph()));
	}

	public PelletInfGraph bind(final KnowledgeBase kb) throws ReasonerException
	{
		return new PelletInfGraph(kb, this, new DefaultGraphLoader());
	}

	@Override
	public void setDerivationLogging(final boolean enable)
	{
		// non sens
	}

	@Override
	public void setParameter(final Property arg0, final Object arg1)
	{
		// non sens
	}

	@Override
	public Model getReasonerCapabilities()
	{
		return _reasonerCapabilities;
	}

	@Override
	public Capabilities getGraphCapabilities()
	{
		return _graphCapabilities;
	}

	@Override
	public void addDescription(final Model arg0, final Resource arg1)
	{
		// non sens
	}

	@Override
	public boolean supportsProperty(final Property property)
	{
		final Model caps = getReasonerCapabilities();
		if (caps == null)
			return false;
		return caps.contains(null, ReasonerVocabulary.supportsP, property);
	}

}
