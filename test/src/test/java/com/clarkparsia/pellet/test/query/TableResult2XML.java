// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.utils.ATermUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Small utility to transform text result sets from TXT to XML
 * format.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class TableResult2XML {

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out
					.println("Usage: java TableResult2XML <filename> [-vars var1 [var2] ...]");
			System.out
					.println("		where <filename> is the path to the result file or a directory of result files");
			System.out
					.println("		      -vars says that first line does not contain names of the vars and thus we supply them.");
			return;
		}

		final List<String> files = new ArrayList<String>();

		if (new File(args[0]).isDirectory()) {
			for (final File f : new File(args[0]).listFiles()) {
				if (!f.isDirectory() && !f.getAbsolutePath().endsWith(".srx")) {
					files.add(f.getAbsolutePath());
				}
			}
		} else {
			files.add(args[0]);
		}

		List<String> varNames = null;
		if (args.length > 1) {
			if (!args[1].equals("-vars")) {
				System.out.println("Unknown parameter " + args[1]
						+ " - ignoring.");
			} else {
				varNames = Arrays.asList(args).subList(2, args.length);
			}
		}

		for (final String arg : files) {

			final List<String> vars = new ArrayList<String>();
			final List<QuerySolution> solutions = new ArrayList<QuerySolution>();

			try {
				final FileInputStream f = new FileInputStream(arg);
				final BufferedReader r = new BufferedReader(
						new InputStreamReader(f));

				// first line are the result variables
				String line;
				StringTokenizer t;

				if (varNames == null) {
					line = r.readLine();
					if (line != null) {
						t = new StringTokenizer(line, " \t");
						while (t.hasMoreTokens()) {
							vars.add(t.nextToken());
						}
					}
				} else {
					vars.addAll(varNames);
				}

				final Model m = ModelFactory.createDefaultModel();

				// next lines contain data
				while ((line = r.readLine()) != null) {
					int i = 0;
					t = new StringTokenizer(line, " \t");

					final QuerySolutionMap s = new QuerySolutionMap();

					while (t.hasMoreTokens()) {
						final String token = t.nextToken();

						if (token.startsWith("http://")
								|| token.startsWith("file:///")) {
							s.add(vars.get(i++), JenaUtils.makeRDFNode(
									ATermUtils.makeTermAppl(token), m));
						} else {
							s.add(vars.get(i++), JenaUtils.makeRDFNode(
									ATermUtils.makePlainLiteral(token), m));
						}
					}

					solutions.add(s);
				}

				ResultSetFormatter.outputAsXML(new FileOutputStream(arg
						+ ".srx"), new ResultSet() {

					private int index = 0;

					public List<String> getResultVars() {
						return vars;
					}

					public int getRowNumber() {
						return index;
					}

					public boolean hasNext() {
						return index < solutions.size();
					}
					
					public QuerySolution next() {
						return nextSolution();
					}

					public Binding nextBinding() {
						return BindingUtils.asBinding(nextSolution());
					}

					public QuerySolution nextSolution() {
						return solutions.get(index++);
					}

					public void remove() {
						throw new IllegalArgumentException(
								"Removing is not supported.");
					}
					
					public Model getResourceModel() {
						return null;
					}

				});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}
}
