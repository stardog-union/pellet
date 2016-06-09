// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.output.ATermManchesterSyntaxRenderer;
import org.mindswap.pellet.output.ATermRenderer;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.QNameProvider;

/**
 * @author Evren Sirin
 */
public class NodeFormatter
{
	protected static Logger _logger = Log.getLogger(NodeFormatter.class);

	private final QNameProvider _qnames;
	private final StringWriter _sw;
	private final ATermRenderer _renderer;
	private final PrintWriter _formatter;

	private Model _rawModel;

	private final static Resource NULL = null;

	//    private Set usedStatements;

	public NodeFormatter(final Model model)
	{
		if (model == null)
			throw new NullPointerException("No model given!");

		//_renderer = new ATermAbstractSyntaxRenderer();
		_renderer = new ATermManchesterSyntaxRenderer();
		_sw = new StringWriter();

		_formatter = new PrintWriter(_sw);
		_renderer.setWriter(_formatter);

		_qnames = JenaUtils.makeQNameProvider(model);
		//        _formatter.setQNames(_qnames);

		if (model instanceof InfModel)
		{
			final InfGraph graph = (InfGraph) model.getGraph();
			_rawModel = ModelFactory.createModelForGraph(graph.getRawGraph());
		}
		else
			_rawModel = model;
	}

	public QNameProvider getQNames()
	{
		return _qnames;
	}

	public String format(final RDFNode node)
	{
		if (node == null)
			return "<<null>>";
		//        usedStatements = new HashSet();
		_sw.getBuffer().setLength(0);

		final ATerm term = node2term(node);

		if (term instanceof ATermAppl)
			_renderer.visit((ATermAppl) term);
		else
		{
			_sw.write("{");
			_renderer.visitList((ATermList) term);
			_sw.write("}");
		}

		return _sw.toString();
	}

	public ATerm node2term(final RDFNode node)
	{
		ATerm aTerm = null;

		if (node.equals(OWL.Thing))
			return ATermUtils.TOP;
		else
			if (node.equals(OWL.Nothing))
				return ATermUtils.BOTTOM;
			else
				if (node.equals(OWL2.topDataProperty))
					return ATermUtils.TOP_DATA_PROPERTY;
				else
					if (node.equals(OWL2.bottomDataProperty))
						return ATermUtils.BOTTOM_DATA_PROPERTY;
					else
						if (node.equals(OWL2.topObjectProperty))
							return ATermUtils.TOP_OBJECT_PROPERTY;
						else
							if (node.equals(OWL2.bottomObjectProperty))
								return ATermUtils.BOTTOM_OBJECT_PROPERTY;
							else
								if (node instanceof org.apache.jena.rdf.model.Literal)
								{
									final org.apache.jena.rdf.model.Literal l = (org.apache.jena.rdf.model.Literal) node;
									final String datatypeURI = l.getDatatypeURI();
									if (datatypeURI != null)
										aTerm = ATermUtils.makeTypedLiteral(l.getString(), datatypeURI);
									else
										aTerm = ATermUtils.makePlainLiteral(l.getString(), l.getLanguage());
								}
								else
									if (node instanceof Resource)
									{
										final Resource r = (Resource) node;

										if (_rawModel.contains(r, OWL.onProperty, NULL))
											aTerm = createRestriction(r);
										else
											if (r.isAnon())
											{
												if (_rawModel.contains(r, RDF.first, NULL))
													aTerm = createList(r);
												else
													if (_rawModel.contains(r, OWL.intersectionOf))
													{
														final ATermList list = createList(_rawModel.getProperty(r, OWL.intersectionOf).getResource());
														aTerm = ATermUtils.makeAnd(list);
													}
													else
														if (_rawModel.contains(r, OWL.unionOf))
														{
															final ATermList list = createList(_rawModel.getProperty(r, OWL.unionOf).getResource());
															aTerm = ATermUtils.makeOr(list);
														}
														else
															if (_rawModel.contains(r, OWL.oneOf))
															{
																final ATermList list = createList(_rawModel.getProperty(r, OWL.oneOf).getResource());
																ATermList result = ATermUtils.EMPTY_LIST;
																for (ATermList l = list; !l.isEmpty(); l = l.getNext())
																{
																	final ATermAppl c = (ATermAppl) l.getFirst();
																	final ATermAppl nominal = ATermUtils.makeValue(c);
																	result = result.insert(nominal);
																}

																aTerm = ATermUtils.makeOr(result);
															}
															else
																if (_rawModel.contains(r, OWL.complementOf))
																{
																	final ATerm complement = node2term(_rawModel.getProperty(r, OWL.complementOf).getResource());
																	aTerm = ATermUtils.makeNot(complement);
																}
																else
																	aTerm = ATermUtils.makeBnode(r.getId().toString());
											}
											else
												aTerm = ATermUtils.makeTermAppl(r.getURI());
									}

		return aTerm;
	}

	private ATermAppl createRestriction(final Resource s)
	{
		ATermAppl aTerm = ATermUtils.BOTTOM;

		Statement stmt = _rawModel.getProperty(s, OWL.onProperty);
		//        usedStatements.add(stmt);

		final Resource p = stmt.getResource();
		final ATerm pt = node2term(p);

		if (s.hasProperty(OWL.hasValue))
		{
			stmt = _rawModel.getProperty(s, OWL.hasValue);
			final RDFNode o = stmt.getObject();
			//	        usedStatements.add(stmt);

			final ATerm ot = node2term(o);
			aTerm = ATermUtils.makeHasValue(pt, ot);
		}
		else
			if (s.hasProperty(OWL.allValuesFrom))
			{
				stmt = _rawModel.getProperty(s, OWL.allValuesFrom);
				//	        usedStatements.add(stmt);

				final Resource o = stmt.getResource();
				final ATerm ot = node2term(o);
				aTerm = ATermUtils.makeAllValues(pt, ot);
			}
			else
				if (s.hasProperty(OWL.someValuesFrom))
				{
					stmt = _rawModel.getProperty(s, OWL.someValuesFrom);
					//	        usedStatements.add(stmt);

					final Resource o = stmt.getResource();
					final ATerm ot = node2term(o);
					aTerm = ATermUtils.makeSomeValues(pt, ot);
				}
				else
					if (s.hasProperty(OWL.minCardinality))
					{
						stmt = _rawModel.getProperty(s, OWL.minCardinality);
						//	        usedStatements.add(stmt);

						int cardinality = 0;
						try
						{
							cardinality = stmt.getInt();
						} // try
						catch (final Exception ex)
						{
							_logger.log(Level.FINER, "", ex);
							cardinality = Integer.parseInt(stmt.getLiteral().getLexicalForm());
						} // catch
						aTerm = ATermUtils.makeDisplayMin(pt, cardinality, ATermUtils.EMPTY);
					}
					else
						if (s.hasProperty(OWL.maxCardinality))
						{
							stmt = _rawModel.getProperty(s, OWL.maxCardinality);
							//	        usedStatements.add(stmt);

							int cardinality = 0;
							try
							{
								cardinality = stmt.getInt();
							} // try
							catch (final Exception ex)
							{
								_logger.log(Level.FINER, "", ex);
								cardinality = Integer.parseInt(stmt.getLiteral().getLexicalForm());
							} // catch
							aTerm = ATermUtils.makeDisplayMax(pt, cardinality, ATermUtils.EMPTY);
						}
						else
							if (s.hasProperty(OWL.cardinality))
							{
								stmt = _rawModel.getProperty(s, OWL.cardinality);
								//	        usedStatements.add(stmt);

								int cardinality = 0;
								try
								{
									cardinality = stmt.getInt();
								} // try
								catch (final Exception ex)
								{
									_logger.log(Level.FINER, "", ex);
									cardinality = Integer.parseInt(stmt.getLiteral().getLexicalForm());
								} // catch
								aTerm = ATermUtils.makeDisplayCard(pt, cardinality, ATermUtils.EMPTY);
							}
							else
							{
								// default nothing
							}

		return aTerm;
	} // createRestriction

	private ATermList createList(final Resource r)
	{
		if (r.equals(RDF.nil))
			return ATermUtils.EMPTY_LIST;
		else
			if (!_rawModel.contains(r, RDF.first))
			{
				System.err.println("Invalid list structure: List " + r + " does not have a rdf:first property. Ignoring rest of the list.");
				return ATermUtils.EMPTY_LIST;
			}

		final ATerm first = node2term(_rawModel.getProperty(r, RDF.first).getObject());
		final Resource rest = _rawModel.getProperty(r, RDF.rest).getResource();
		return ATermUtils.makeList(first, createList(rest));
	} // createList

}
