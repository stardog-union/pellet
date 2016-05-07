// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.NONE;
import static pellet.PelletCmdOptionArg.REQUIRED;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory.QueryEngineType;
import com.clarkparsia.sparqlowl.parser.arq.ARQTerpParser;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.NotFoundException;
import org.apache.jena.util.FileManager;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.jena.NodeFormatter;
import org.mindswap.pellet.output.TableData;

/**
 * <p>
 * Title: PelletQuery
 * </p>
 * <p>
 * Description: This is the command-line version of Pellet for querying. It is provided as a stand-alone program and should not be directly used in
 * applications.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Markus Stocker
 * @author Evren Sirin
 */
public class PelletQuery extends PelletCmdApp
{

	private String queryFile;
	private String queryString;
	private Query query;
	private JenaLoader loader;
	private ResultSet queryResults;
	private Model constructQueryModel;
	private boolean askQueryResult;
	private Syntax queryFormat = Syntax.syntaxSPARQL;
	private OutputFormat outputFormat = OutputFormat.TABULAR;
	private QueryEngineType queryEngine = null;

	static
	{
		/*
		 * Register the Terp parser with ARQ
		 */
		ARQTerpParser.registerFactory();
	}

	private enum OutputFormat
	{
		TABULAR, XML, JSON
	}

	@Override
	public String getAppId()
	{
		return "PelletQuery: SPARQL-DL Query Engine";
	}

	@Override
	public String getAppCmd()
	{
		return "pellet query " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
	public PelletCmdOptions getOptions()
	{
		final PelletCmdOptions options = getGlobalOptions();

		PelletCmdOption option = new PelletCmdOption("query-file");
		option.setShortOption("q");
		option.setType("<file URI>");
		option.setDescription("Read the SPARQL query from the given file");
		option.setIsMandatory(true);
		option.setArg(REQUIRED);
		options.add(option);

		option = new PelletCmdOption("output-format");
		option.setShortOption("o");
		option.setType("Tabular | XML | JSON");
		option.setDescription("Format of result set (SELECT queries)");
		option.setDefaultValue("Tabular");
		option.setIsMandatory(false);
		option.setArg(REQUIRED);
		options.add(option);

		option = new PelletCmdOption("query-format");
		option.setType("SPARQL | ARQ | TERP");
		option.setDescription("The query format");
		option.setDefaultValue("SPARQL");
		option.setIsMandatory(false);
		option.setArg(REQUIRED);
		options.add(option);

		options.add(getIgnoreImportsOption());
		options.add(getInputFormatOption());

		option = new PelletCmdOption("query-engine");
		option.setType("Pellet | ARQ | Mixed");
		option.setShortOption("e");
		option.setDescription("The query engine that will be used. Default behavior " + "is to auto select the engine that can handle the given " + "query with best performance. Pellet query " + "engine is the typically fastest but cannot handle " + "FILTER, OPTIONAL, UNION, DESCRIBE or named graphs. " + "Mixed engine uses ARQ to handle SPARQL algebra and " + "uses Pellet to answer Basic Graph Patterns (BGP) " + "which can be expressed in SPARQL-DL. ARQ engine uses " + "Pellet to answer single triple patterns and can handle " + "queries that do not fit into SPARQL-DL. As a " + "consequence SPARQL-DL extensions and complex class " + "expressions encoded inside the SPARQL query are not " + "supported.");
		option.setIsMandatory(false);
		option.setArg(REQUIRED);
		options.add(option);

		option = new PelletCmdOption("bnode");
		option.setDescription("Treat bnodes in the query as undistinguished variables. Undistinguished " + "variables can match individuals whose existence is inferred by the " + "reasoner, e.g. due to a someValuesFrom restriction. This option has " + "no effect if ARQ engine is selected.");
		option.setDefaultValue(false);
		option.setIsMandatory(false);
		option.setArg(NONE);
		options.add(option);

		return options;
	}

	public PelletQuery()
	{
	}

	@Override
	public void parseArgs(final String[] args)
	{
		super.parseArgs(args);

		setQueryFile(options.getOption("query-file").getValueAsString());
		setOutputFormat(options.getOption("output-format").getValueAsString());
		setQueryFormat(options.getOption("query-format").getValueAsString());
		setQueryEngine(options.getOption("query-engine").getValueAsString());
		PelletOptions.TREAT_ALL_VARS_DISTINGUISHED = !options.getOption("bnode").getValueAsBoolean();
	}

	@Override
	public void run()
	{
		loadQuery();
		loadInput();
		execQuery();
		printQueryResults();
	}

	public void setQueryFile(final String s)
	{
		queryFile = s;
	}

	public void setOutputFormat(final String s)
	{
		if (s == null)
			outputFormat = OutputFormat.TABULAR;
		else
			if (s.equalsIgnoreCase("Tabular"))
				outputFormat = OutputFormat.TABULAR;
			else
				if (s.equalsIgnoreCase("XML"))
					outputFormat = OutputFormat.XML;
				else
					if (s.equalsIgnoreCase("JSON"))
						outputFormat = OutputFormat.JSON;
					else
						throw new PelletCmdException("Invalid output format: " + outputFormat);
	}

	public ResultSet getQueryResults()
	{
		return queryResults;
	}

	public Model getConstructQueryModel()
	{
		return constructQueryModel;
	}

	public boolean getAskQueryResult()
	{
		return askQueryResult;
	}

	public void setQueryFormat(final String s)
	{
		if (s == null)
			throw new PelletCmdException("Query format is null");

		if (s.equalsIgnoreCase("SPARQL"))
			queryFormat = Syntax.lookup("SPARQL");
		else
			if (s.equalsIgnoreCase("ARQ"))
				queryFormat = Syntax.lookup("ARQ");
			else
				if (s.equalsIgnoreCase("TERP"))
					queryFormat = Syntax.lookup("TERP");
				else
					throw new PelletCmdException("Unknown query format: " + s);

		if (queryFormat == null)
			throw new PelletCmdException("Query format is null: " + s);
	}

	public void setQueryEngine(final String s)
	{
		if (s == null)
		{
			queryEngine = null;
			return;
		}

		try
		{
			queryEngine = QueryEngineType.valueOf(s.toUpperCase());
		}
		catch (final IllegalArgumentException e)
		{
			throw new PelletCmdException("Unknown query engine: " + s);
		}
	}

	private void loadInput()
	{
		try
		{
			loader = (JenaLoader) getLoader("Jena");

			final KnowledgeBase kb = getKB(loader);

			startTask("consistency check");
			final boolean isConsistent = kb.isConsistent();
			finishTask("consistency check");

			if (!isConsistent)
				throw new PelletCmdException("Ontology is inconsistent, run \"pellet explain\" to get the reason");

		}
		catch (final NotFoundException e)
		{
			throw new PelletCmdException(e);
		}
		catch (final QueryParseException e)
		{
			throw new PelletCmdException(e);
		}
		catch (final InconsistentOntologyException e)
		{
			throw new PelletCmdException("Cannot query inconsistent ontology!");
		}
	}

	private void loadQuery()
	{
		try
		{
			verbose("Query file: " + queryFile);
			startTask("parsing query file");

			queryString = FileManager.get().readWholeFileAsUTF8(queryFile);
			query = QueryFactory.create(queryString, queryFormat);

			finishTask("parsing query file");

			verbose("Query: ");
			verbose("-----------------------------------------------------");
			verbose(queryString.trim());
			verbose("-----------------------------------------------------");
		}
		catch (final NotFoundException e)
		{
			throw new PelletCmdException(e);
		}
		catch (final QueryParseException e)
		{
			throw new PelletCmdException(e);
		}
	}

	private void execQuery()
	{
		final Dataset dataset = DatasetFactory.create(loader.getModel());
		final QueryExecution qe = (queryEngine == null) ? SparqlDLExecutionFactory.create(query, dataset) : SparqlDLExecutionFactory.create(query, dataset, null, queryEngine);

		verbose("Created query engine: " + qe.getClass().getName());

		startTask("query execution");
		if (query.isSelectType())
			queryResults = ResultSetFactory.makeRewindable(qe.execSelect());
		else
			if (query.isConstructType())
				constructQueryModel = qe.execConstruct();
			else
				if (query.isAskType())
					askQueryResult = qe.execAsk();
				else
					throw new UnsupportedOperationException("Unsupported query type");
		finishTask("query execution");
	}

	private void printQueryResults()
	{
		if (query.isSelectType())
			printSelectQueryResuts();
		else
			if (query.isConstructType())
				printConstructQueryResults();
			else
				if (query.isAskType())
					printAskQueryResult();

	}

	private void printSelectQueryResuts()
	{
		if (queryResults.hasNext())
		{
			if (outputFormat == OutputFormat.TABULAR)
				printTabularQueryResults();
			else
				if (outputFormat == OutputFormat.XML)
					printXMLQueryResults();
				else
					if (outputFormat == OutputFormat.JSON)
						printJSONQueryResults();
					else
						printTabularQueryResults();
		}
		else
		{
			output("Query Results (0 answers): ");
			output("NO RESULTS");
		}
	}

	private void printTabularQueryResults()
	{
		// number of distinct bindings
		int count = 0;

		final NodeFormatter formatter = new NodeFormatter(loader.getModel());

		// variables used in select
		final List<?> resultVars = query.getResultVars();

		final List<List<String>> data = new ArrayList<>();
		while (queryResults.hasNext())
		{
			final QuerySolution binding = queryResults.nextSolution();
			final List<String> formattedBinding = new ArrayList<>();
			for (int i = 0; i < resultVars.size(); i++)
			{
				final String var = (String) resultVars.get(i);
				final RDFNode result = binding.get(var);

				// format the result
				formattedBinding.add(formatter.format(result));
			}

			if (data.add(formattedBinding))
				count++;
		}

		output("Query Results (" + count + " answers): ");

		@SuppressWarnings({ "unchecked", "rawtypes" })
		final TableData table = new TableData((Collection) data, resultVars);
		final StringWriter tableSW = new StringWriter();
		table.print(tableSW);
		output(tableSW.toString());
	}

	private void printXMLQueryResults()
	{
		ResultSetFormatter.outputAsXML(queryResults);
	}

	private void printJSONQueryResults()
	{
		if (verbose)
		{
			System.out.println("/* ");
			System.out.println(queryString.replace("*/", "* /"));
			System.out.println("*/ ");
		}
		ResultSetFormatter.outputAsJSON(queryResults);
	}

	private void printConstructQueryResults()
	{
		final StringWriter modelSW = new StringWriter();
		constructQueryModel.write(modelSW);
		output(modelSW.toString());
	}

	private void printAskQueryResult()
	{
		output("ASK query result: ");
		output(askQueryResult ? "yes" : "no");
	}
}
