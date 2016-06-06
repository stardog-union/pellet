// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aterm.ATermAppl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.Filter;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

public final class PelletTestCase
{
	@SafeVarargs
	public static <T> Set<T> set(final T... elements)
	{
		final Set<T> set = new HashSet<>();
		for (final T element : elements)
			set.add(element);
		return set;
	}

	protected static boolean isAnonValue(final Object n)
	{
		return ((n instanceof Resource) && ((Resource) n).isAnon()) || ((n instanceof Statement) && ((Statement) n).getSubject().isAnon()) || ((n instanceof Statement) && isAnonValue(((Statement) n).getObject()));
	}

	public static <T> void assertIteratorContains(final Iterator<T> it, final T val)
	{
		boolean found = false;
		while (it.hasNext() && !found)
		{
			final Object obj = it.next();
			found = obj.equals(val);
		}

		assertTrue("Failed to find _expected iterator value: " + val, found);
	}

	@SuppressWarnings("unchecked")
	public static <T> void assertIteratorValues(final Iterator<? extends T> it, final Iterator<? extends T> expected)
	{
		assertIteratorValues(it, (T[]) IteratorUtils.toList(expected).toArray());
	}

	public static <T> void assertIteratorValues(final Iterator<? extends T> it, final T... expected)
	{
		final boolean[] found = new boolean[expected.length];

		for (int i = 0; i < expected.length; i++)
			found[i] = false;

		while (it.hasNext())
		{
			final Object n = it.next();
			boolean gotit = false;

			for (int i = 0; i < expected.length; i++)
				if (n.equals(expected[i]))
				{
					gotit = true;
					found[i] = true;
				}
			assertTrue("Found unexpected iterator value: " + n, gotit);
		}

		// check that all _expected values were found
		final List<T> unfound = new ArrayList<>();
		for (int i = 0; i < expected.length; i++)
			if (!found[i])
				unfound.add(expected[i]);

		assertTrue("Failed to find _expected iterator values: " + unfound, unfound.isEmpty());
	}

	public static Set<Statement> createStatements(final Resource subject, final Property predicate, final RDFNode... objects)
	{
		final Set<Statement> set = new HashSet<>();
		for (final RDFNode object : objects)
			set.add(ResourceFactory.createStatement(subject, predicate, object));

		return set;
	}

	public static void addStatements(final Model model, final Resource subject, final Property predicate, final RDFNode... objects)
	{
		for (final RDFNode object : objects)
			model.add(subject, predicate, object);
	}

	public static void assertPropertyValues(final Model model, final Resource subject, final Property predicate, final RDFNode... objects)
	{
		final Model values = ModelFactory.createDefaultModel();
		addStatements(values, subject, predicate, objects);
		assertPropertyValues(model, predicate, values);
	}

	public static void assertPropertyValues(final Model model, final Property pred, final Model inferences)
	{
		final Filter<Statement> predFilter = new Filter<Statement>()
		{
			@Deprecated
			@Override
			public boolean accept(final Statement stmt)
			{
				return stmt.getPredicate().equals(pred);
			}
		};

		for (final StmtIterator i = inferences.listStatements(); i.hasNext();)
		{
			final Statement statement = i.nextStatement();

			assertEquals(pred, statement.getPredicate());

			assertTrue(statement + " not inferred", model.contains(statement));
		}

		assertIteratorValues(model.listStatements(null, pred, (RDFNode) null), inferences.listStatements());

		final Set<Resource> testedSubj = new HashSet<>();
		final Set<RDFNode> testedObj = new HashSet<>();
		for (final StmtIterator i = inferences.listStatements(); i.hasNext();)
		{
			final Statement statement = i.nextStatement();
			final Resource subj = statement.getSubject();
			final RDFNode obj = statement.getObject();

			if (testedSubj.add(subj))
			{
				assertIteratorValues(model.listStatements(subj, pred, (RDFNode) null), inferences.listStatements(subj, pred, (RDFNode) null));

				assertIteratorValues(model.listStatements(subj, null, (RDFNode) null).filterKeep(predFilter), inferences.listStatements(subj, null, (RDFNode) null).filterKeep(predFilter));
			}

			if (testedObj.add(obj))
			{
				assertIteratorValues(model.listStatements(null, pred, obj), inferences.listStatements(null, pred, obj));

				assertIteratorValues(model.listStatements(null, null, obj).filterKeep(predFilter), inferences.listStatements(null, null, obj).filterKeep(predFilter));
			}
		}
	}

	public static void testResultSet(final ResultSet results, final List<Map<String, RDFNode>> ans)
	{
		final List<Map<String, RDFNode>> answers = new ArrayList<>(ans);
		while (results.hasNext())
		{
			final QuerySolution sol = results.nextSolution();
			assertNotNull("QuerySolution", sol);

			final Map<String, RDFNode> answer = new HashMap<>();
			for (final String var : results.getResultVars())
			{
				final RDFNode val = sol.get(var);
				assertNotNull("Variable: " + var, val);

				answer.put(var, val);
			}

			assertTrue("Unexpected binding found: " + answer, answers.remove(answer));
		}

		assertTrue("Binding not found: " + answers, answers.isEmpty());
	}

	public static Map<String, RDFNode> createBinding(final String[] keys, final RDFNode[] values)
	{
		assertTrue(keys.length == values.length);

		final Map<String, RDFNode> answer = new HashMap<>();
		for (int i = 0; i < keys.length; i++)
			answer.put(keys[i], values[i]);

		return answer;
	}

	public static List<Map<String, RDFNode>> createBindings(final String[] keys, final RDFNode[][] values)
	{
		final List<Map<String, RDFNode>> answers = new ArrayList<>();
		for (final RDFNode[] value : values)
		{
			final Map<String, RDFNode> answer = new HashMap<>();
			for (int j = 0; j < keys.length; j++)
				answer.put(keys[j], value[j]);
			answers.add(answer);
		}

		return answers;
	}

	public static void printAll(final Iterator<?> i)
	{
		while (i.hasNext())
			System.out.println(i.next());
	}

	public static void printAll(final Iterator<?> i, final String head)
	{
		System.out.print(head + ": ");
		if (i.hasNext())
		{
			System.out.println();
			while (i.hasNext())
				System.out.println(i.next());
		}
		else
			System.out.println("<EMPTY>");
	}

	public static void assertSatisfiable(final KnowledgeBase kb, final ATermAppl c)
	{
		assertSatisfiable(kb, c, true);
	}

	public static void assertUnsatisfiable(final KnowledgeBase kb, final ATermAppl c)
	{
		assertSatisfiable(kb, c, false);
	}

	public static void assertSatisfiable(final KnowledgeBase kb, final ATermAppl c, final boolean isSatisfiable)
	{
		assertEquals("Satisfiability for " + c + " failed", isSatisfiable, kb.isSatisfiable(c));
	}

	public static void assertSubClass(final KnowledgeBase kb, final String c1, final String c2)
	{
		assertSubClass(kb, term(c1), term(c2));
	}

	public static void assertSubClass(final KnowledgeBase kb, final ATermAppl c1, final ATermAppl c2)
	{
		assertSubClass(kb, c1, c2, true);
	}

	public static void assertNotSubClass(final KnowledgeBase kb, final ATermAppl c1, final ATermAppl c2)
	{
		assertSubClass(kb, c1, c2, false);
	}

	public static void assertSubClass(final KnowledgeBase kb, final ATermAppl c1, final ATermAppl c2, final boolean expectedSubClass)
	{
		boolean computedSubClass = kb.isSubClassOf(c1, c2);

		assertEquals("Subclass check failed for (" + ATermUtils.toString(c1) + " [= " + ATermUtils.toString(c2) + ")", expectedSubClass, computedSubClass);

		kb.isSatisfiable(c1);
		kb.isSatisfiable(not(c1));
		kb.isSatisfiable(c2);
		kb.isSatisfiable(not(c2));

		final long satCount = kb.getABox().stats.satisfiabilityCount;
		computedSubClass = kb.isSubClassOf(c1, c2);
		final boolean cached = (satCount == kb.getABox().stats.satisfiabilityCount);

		assertEquals("Subclass check (Cached: " + cached + ") failed for (" + ATermUtils.toString(c1) + " [= " + ATermUtils.toString(c2) + ")", expectedSubClass, computedSubClass);
	}
}
