// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy.printer;

import aterm.ATermAppl;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.TaxonomyUtils;

/**
 * <p>
 * Title: RDF/XML Taxonomy Printer
 * </p>
 * <p>
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
public class RDFXMLTaxonomyPrinter implements TaxonomyPrinter<ATermAppl>
{
	final static String OWL_EQUIVALENT_CLASS = "owl:equivalentClass";
	final static String RDFS_SUB_CLASS_OF = "rdfs:subClassOf";
	final static String RDF_TYPE = "rdf:type";

	protected boolean onlyDirectSubclass;

	private Taxonomy<ATermAppl> taxonomy;

	private PrintWriter out;

	private Set<ATermAppl> visited;

	public RDFXMLTaxonomyPrinter()
	{
		onlyDirectSubclass = true;
	}

	@Override
	public void print(final Taxonomy<ATermAppl> taxonomy)
	{
		print(taxonomy, new PrintWriter(System.out));
	}

	@Override
	public void print(final Taxonomy<ATermAppl> taxonomy, final PrintWriter out)
	{
		this.taxonomy = taxonomy;
		this.out = out;

		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println();
		out.println("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" ");
		out.println("         xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" ");
		out.println("         xmlns:owl=\"http://www.w3.org/2002/07/owl#\"> ");
		out.println();

		printTree();

		out.println();
		out.println("</rdf:RDF>");
		out.flush();
	}

	protected void printTree()
	{
		visited = new HashSet<>();
		visited.add(ATermUtils.BOTTOM);

		printTree(ATermUtils.TOP);

		printTree(ATermUtils.BOTTOM);

		for (final ATermAppl c : taxonomy.getClasses())
			printTree(c);
	}

	protected void printTree(final ATermAppl c)
	{
		if (visited.contains(c))
			return;

		final Set<ATermAppl> eqClasses = ATermUtils.primitiveOrBottom(taxonomy.getEquivalents(c));

		visited.add(c);
		visited.addAll(eqClasses);

		printConceptDefinition(c, false);
		for (final ATermAppl eq : eqClasses)
			printTriple(OWL_EQUIVALENT_CLASS, eq);

		if (!c.equals(ATermUtils.BOTTOM))
		{
			final Set<Set<ATermAppl>> supers = taxonomy.getSupers(c, onlyDirectSubclass);
			for (final Set<ATermAppl> equivalenceSet : supers)
			{

				final ATermAppl subClass = ATermUtils.primitiveOrBottom(equivalenceSet).iterator().next();

				printTriple(RDFS_SUB_CLASS_OF, subClass);
			}
		}

		out.println("</owl:Class>");

		for (final ATermAppl eqClass : eqClasses)
		{
			out.println();
			printConceptDefinition(eqClass, true);
		}

		out.println();

		final Set<ATermAppl> instances = TaxonomyUtils.getDirectInstances(taxonomy, c);
		for (final ATermAppl instance : instances)
		{
			if (ATermUtils.isBnode(instance))
				return;

			out.print("<rdf:Description rdf:about=\"");
			out.print(instance.getName());
			out.println("\">");
			printTriple(RDF_TYPE, c);
			out.println("</rdf:Description>");

			out.println();
		}
	}

	protected void printTriple(final String predicate, final ATermAppl c2)
	{
		out.print("   <" + predicate);
		out.print(" rdf:resource=\"");
		printConcept(c2);
		out.println("\"/> ");
	}

	protected void printConceptDefinition(final ATermAppl c, final boolean close)
	{
		out.print("<owl:Class rdf:about=\"");
		printConcept(c);
		if (close)
			out.println("\"/> ");
		else
			out.println("\"> ");
	}

	protected void printConcept(final ATermAppl c)
	{
		String uri = null;
		if (c.equals(ATermUtils.TOP))
			uri = "http://www.w3.org/2002/07/owl#Thing";
		else
			if (c.equals(ATermUtils.BOTTOM))
				uri = "http://www.w3.org/2002/07/owl#Nothing";
			else
				uri = c.getName();

		out.print(uri);
	}
}
