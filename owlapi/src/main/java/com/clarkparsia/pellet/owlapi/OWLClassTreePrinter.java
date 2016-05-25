package com.clarkparsia.pellet.owlapi;

import com.intrinsec.owlapi.OWLException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.printer.TreeTaxonomyPrinter;
import org.mindswap.pellet.utils.QNameProvider;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * TaxonomyPrinter for Taxonomies of OWLClasses (Taxonomy<OWLClass>)
 *
 * @author Blazej Bulka <blazej@clarkparsia.com>
 */
public class OWLClassTreePrinter extends TreeTaxonomyPrinter<OWLClass>
{
	private final QNameProvider qnames = new QNameProvider();

	public OWLClassTreePrinter()
	{
	}

	@Override
	protected void printNode(final Set<OWLClass> set)
	{
		super.printNode(set);

		final Set<OWLNamedIndividual> instances = getDirectInstances(_taxonomy, set.iterator().next());
		if (instances.size() > 0)
		{
			_out.print(" - (");
			boolean printed = false;
			for (final OWLNamedIndividual x : instances)
			{
				if (printed)
					_out.print(", ");
				else
					printed = true;
				printURI(_out, x);
			}
			_out.print(")");
		}
	}

	@Override
	protected void printURI(final PrintWriter out, final OWLClass c)
	{
		printIRI(out, c.getIRI());
	}

	private void printURI(final PrintWriter out, final OWLNamedIndividual i)
	{
		printIRI(out, i.getIRI());
	}

	private void printIRI(final PrintWriter out, final IRI iri)
	{
		out.print(qnames.shortForm(iri.toString()));
	}

	/**
	 * Retrieves direct instances of a class from Taxonomy
	 *
	 * @param t the taxonomy
	 * @param c the class
	 * @return a set of direct instances
	 */
	@SuppressWarnings("unchecked")
	public static Set<OWLNamedIndividual> getDirectInstances(final Taxonomy<OWLClass> t, final OWLClass c)
	{

		final Set<OWLNamedIndividual> instances = (Set<OWLNamedIndividual>) t.getDatum(c, TaxonomyUtils.INSTANCES_KEY);
		if (instances == null)
		{
			if (t.contains(c))
				return Collections.emptySet();

			throw new OWLException(c + " is an unknown class!");
		}

		return Collections.unmodifiableSet(instances);
	}
}
