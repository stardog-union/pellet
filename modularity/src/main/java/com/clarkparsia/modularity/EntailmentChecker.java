// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import com.clarkparsia.owlapi.OWL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.katk.tools.Log;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class EntailmentChecker implements OWLAxiomVisitor
{
	public static Logger _logger = Log.getLogger(EntailmentChecker.class);

	private final IncrementalClassifier _reasoner;
	private Boolean _isEntailed;

	public EntailmentChecker(final IncrementalClassifier reasoner)
	{
		this._reasoner = reasoner;
	}

	public boolean isEntailed(final Set<? extends OWLAxiom> axioms)
	{
		for (final OWLAxiom axiom : axioms)
			if (!isEntailed(axiom))
				return false;

		return true;
	}

	public boolean isEntailed(final OWLAxiom axiom)
	{
		_isEntailed = null;

		axiom.accept(this);

		if (_isEntailed == null)
			throw new UnsupportedEntailmentTypeException(axiom);

		return _isEntailed;
	}

	@Override
	public void visit(final OWLSubClassOfAxiom axiom)
	{
		final OWLClassExpression subClass = axiom.getSubClass();
		final OWLClassExpression superClass = axiom.getSuperClass();

		if (!_reasoner.isClassified() || subClass.isAnonymous() || superClass.isAnonymous())
			_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
		else
			_isEntailed = _reasoner.getTaxonomy().isSubNodeOf((OWLClass) subClass, (OWLClass) superClass).isTrue();
	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom)
	{
		_isEntailed = true;

		final Iterator<OWLClassExpression> i = axiom.classExpressions().iterator();
		if (i.hasNext())
		{
			final OWLClassExpression first = i.next();

			while (i.hasNext() && _isEntailed)
			{
				final OWLClassExpression next = i.next();

				if (!_reasoner.isClassified() || first.isAnonymous() || next.isAnonymous())
					_isEntailed = _reasoner.getReasoner().isEntailed(OWL.equivalentClasses(first, next));
				else
					_isEntailed = _reasoner.getTaxonomy().isEquivalent((OWLClass) first, (OWLClass) next).isTrue();
			}
		}
	}

	@Override
	public void visit(final OWLSameIndividualAxiom axiom)
	{
		if (_reasoner.isRealized())
		{
			// the code uses the assumption that if any of the individuals listed have differing direct types
			// then they cannot be the same; however, if they have the same types, they still have to
			// be checked by the underlying _reasoner
			boolean sameTypes = true;
			final Taxonomy<OWLClass> taxonomy = _reasoner.getTaxonomy();

			final Iterator<OWLIndividual> i = axiom.individuals().iterator();

			if (i.hasNext())
			{
				final OWLIndividual first = i.next();
				final Set<OWLClass> firstTypes = flatten(TaxonomyUtils.getTypes(taxonomy, first, true));

				while (i.hasNext() && sameTypes)
				{
					final OWLIndividual next = i.next();
					final Set<OWLClass> nextTypes = flatten(TaxonomyUtils.getTypes(taxonomy, next, true));

					sameTypes = firstTypes.equals(nextTypes);
				}

				if (sameTypes)
					_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
				else
					_isEntailed = false;
			}
		}
		else
			_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom axiom)
	{
		if (_reasoner.isClassified() && !containsAnonymousClasses(axiom.classExpressions()))
		{
			final OWLClass[] classes = axiom.classExpressions().map(OWLClassExpression::asOWLClass).toArray(OWLClass[]::new);

			if (possiblyDisjoint(classes))
				// no _data detected that would disqualify the axiom -- it has to be checked by the
				// underlying _reasoner
				_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
			else
				_isEntailed = false;
		}
		else
			_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	/**
	 * Performs checks for the given array whether the classes can be pair-wise disjoint. (In other words, it tries to find whether there is information that
	 * proves that there is a pair that cannot be disjoint.)
	 *
	 * @param classes an array of classes to be checked
	 * @return true if the classes may be disjoint, false if information was found that prevents the disjointness
	 */

	private boolean possiblyDisjoint(final OWLClass[] classes)
	{
		for (int i = 0; i < classes.length - 1; i++)
			for (int j = i + 1; j < classes.length; j++)
				if (!possiblyDisjoint(classes[i], classes[j]))
					return false;

		return true;
	}

	/**
	 * Tests whether two classes can be possibly disjoint; i.e., there are no disqualifying conditions for them to be disjoint. The disqualifying conditions
	 * are: the classes are listed as equivalent to each other, or one class is listed as a superclass of the other.
	 *
	 * @param first the first class in the pair
	 * @param next the next class in the pair
	 * @return if the classes may be disjoint, false if the classes cannot be disjoint
	 */
	private boolean possiblyDisjoint(final OWLClass first, final OWLClass next)
	{
		final Taxonomy<OWLClass> taxonomy = _reasoner.getTaxonomy();

		if (taxonomy.getAllEquivalents(first).contains(next))
			return false;

		// getting supers should be typically faster than getting subs
		if (taxonomy.getFlattenedSupers(first, false).contains(next))
			return false;

		if (taxonomy.getFlattenedSupers(next, false).contains(first))
			return false;

		return true;
	}

	/**
	 * Checks whether the collection contains any anonymous classes (i.e., elements that cannot be converted to OWLClass).
	 *
	 * @param classExpressions the list of class expressions to be checked
	 * @return true if the collection contains at least one anonymous class
	 */
	private boolean containsAnonymousClasses(final Stream<OWLClassExpression> classExpressions)
	{
		return classExpressions.filter(OWLClassExpression::isAnonymous).findAny().isPresent();
	}

	@Override
	public void visit(final OWLClassAssertionAxiom axiom)
	{
		if (_reasoner.isRealized() && !axiom.getClassExpression().isAnonymous())
			_isEntailed = contains(TaxonomyUtils.getTypes(_reasoner.getTaxonomy(), axiom.getIndividual(), false), axiom.getClassExpression().asOWLClass());
		else
			_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDeclarationAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLSubPropertyChainOfAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLHasKeyAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final OWLDatatypeDefinitionAxiom axiom)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(axiom);
	}

	@Override
	public void visit(final SWRLRule rule)
	{
		_isEntailed = _reasoner.getReasoner().isEntailed(rule);
	}

	/**
	 * Checks whether an element is contained in the sets of set
	 *
	 * @param <T>
	 * @param setOfSets the set of sets
	 * @param element the element
	 * @return true if the element was found in the set of sets
	 */
	private static <T> boolean contains(final Set<Set<T>> setOfSets, final T element)
	{
		for (final Set<T> set : setOfSets)
			if (set.contains(element))
				return true;

		return false;
	}

	/**
	 * Flattens a set of sets to a single set.
	 *
	 * @param <T>
	 * @param setOfSets the set to be flattened
	 * @return the flattened set
	 */
	private static <T> Set<T> flatten(final Set<Set<T>> setOfSets)
	{
		final Set<T> result = new HashSet<>();

		for (final Set<T> set : setOfSets)
			result.addAll(set);

		return result;
	}
}
