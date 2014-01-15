// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;

import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Comparators;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.ModuleExtractor;
import com.clarkparsia.modularity.PelletIncremantalReasonerFactory;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

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
public class TestUtils {
	/**
	 * Checks if there is a difference between two array of elements and prints
	 * a sorted, easy to read message showing the differences between two
	 * arrays. The elements of the array are compared with toString() values
	 * so this function is suitable only if the array elements have a unique 
	 * string representation. For example, if the array element is a set then
	 * this function cannot be used reliabily. 
	 * 
	 * @param expected
	 *            expected values
	 * @param computed
	 *            computed values
	 * @return <code>true</code> if there is a difference between the modules
	 */
	public static <T> void assertToStringEquals(String msg, T[] expected, T[] computed) {
		Comparator<Object> comparator = Comparators.stringComparator;
		
		List<T> onlyInComputed = new ArrayList<T>();
		List<T> onlyInExpected = new ArrayList<T>();
		List<T> both = new ArrayList<T>();
		
		Arrays.sort( expected, comparator );
		
		Arrays.sort( computed, comparator );
		
		int i = 0, j = 0;
		while( i < computed.length && j < expected.length ) {
			if( computed[i].equals( expected[j] ) ) {
				both.add( computed[i] );
				i++;
				j++;
			}
			else if( comparator.compare( computed[i], expected[j] ) < 0 ) {
				onlyInComputed.add( computed[i] );
				i++;
			}
			else {
				onlyInExpected.add( expected[j] );
				j++;
			}
		}
		
		while( i < computed.length ) {
			onlyInComputed.add( computed[i++] );
		}

		while( j < expected.length ) {
			onlyInExpected.add( expected[j++] );
		}
		
		if( !onlyInComputed.isEmpty() || !onlyInExpected.isEmpty() ) {
			System.err.println( msg );
			System.err.println( "Both " + both.size() + " " + both );
			System.err.println( "Computed " + onlyInComputed.size() + " " + onlyInComputed );
			System.err.println( "Expected " + onlyInExpected.size() + " " + onlyInExpected );
			System.err.println();

			fail( msg );
		}
	}
	
	public static List<OWLOntologyChange> createChanges(OWLOntology ontology,
			Collection<? extends OWLAxiom> axioms, boolean add) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for( OWLAxiom axiom : axioms ) {
			OWLOntologyChange change = add
				? new AddAxiom( ontology, axiom )
				: new RemoveAxiom( ontology, axiom );
			changes.add( change );
		}

		return changes;
	}

	public static <E> Set<E> flatten(Set<Set<E>> setOfSets) {
		Set<E> result = new HashSet<E>();
		for( Set<E> set : setOfSets ) {
			result.addAll( set );
		}
		return result;
	}

	public static double[] getSizes(Collection<? extends Collection<?>> collections) {
		double[] sizes = new double[collections.size()];

		int i = 0;
		for( Collection<?> collection : collections ) {
			sizes[i++] = collection.size();
		}

		return sizes;
	}

	/**
	 * Selects a random axiom from an ontology
	 * 
	 * @param args
	 */
	public static OWLAxiom selectRandomAxiom(OWLOntology ontology) throws OWLException {
		Set<OWLAxiom> selectedAxioms = selectRandomAxioms( ontology, 1 );

		return selectedAxioms.iterator().next();
	}

	/**
	 * Selects a set of random axioms from an ontology
	 */
	public static Set<OWLAxiom> selectRandomAxioms(OWLOntology ontology, int count) {
		Set<OWLAxiom> axioms = ontology.getAxioms();

		return selectRandomElements( axioms, count );
	}

	public static <T> Set<T> selectRandomElements(Collection<T> coll, int K) {
		// get the size
		int N = coll.size();

		if( K > N )
			throw new IllegalArgumentException( K + " >= " + N );

		List<T> list = (coll instanceof RandomAccess)
			? (List<T>) coll
			: new ArrayList<T>( coll );

		Random rand = new Random();

		for( int k = 0; k < K; k++ ) {
			int j = rand.nextInt( N - k ) + k;
			Collections.swap( list, k, j );
		}

		return new HashSet<T>( list.subList( 0, K ) );
	}

	public static void assertClassificationEquals(OWLReasoner expected, OWLReasoner actual) {
//		assertClassificationEquals( expected, actual, OWL.Nothing );
		
		for( OWLClass cls : actual.getRootOntology().getClassesInSignature() ) {
			assertClassificationEquals( expected, actual, cls );
		}
	}
	
	public static void assertClassificationEquals(OWLReasoner expected, OWLReasoner actual, OWLClass cls) {
		Set<OWLClass> expectedEquivalents = expected.getEquivalentClasses( cls ).getEntities();
		Set<OWLClass> actualEquivalents = actual.getEquivalentClasses( cls ).getEntities();

		assertEquals( "Equivalents different for Class: " + cls, expectedEquivalents, actualEquivalents );

		Set<OWLClass> expectedSupers = expected.getSuperClasses( cls, true ).getFlattened();
		Set<OWLClass> actualSupers = actual.getSuperClasses( cls, true ).getFlattened();

		assertEquals( "Supers different for Class: " + cls, expectedSupers, actualSupers );		
	}
	
	public static void assertDisjointnessEquals(OWLReasoner expected, OWLReasoner actual) {
		for( OWLClass cls : actual.getRootOntology().getClassesInSignature() ) {
			assertDisjointnessEquals( expected, actual, cls );
		}
	}

	public static void assertDisjointnessEquals(OWLReasoner expected, OWLReasoner actual, OWLClass cls) {
		Set<OWLClass> expectedDisjoints = expected.getDisjointClasses( cls ).getFlattened();
		Set<OWLClass> actualDisjoints = actual.getDisjointClasses( cls ).getFlattened();
			
		assertEquals( "Disjoint classes different for Class: " + cls, expectedDisjoints, actualDisjoints );
	}

	public static void assertInstancesEquals(OWLReasoner expected, OWLReasoner actual) {
		for( OWLClass cls : actual.getRootOntology().getClassesInSignature() ) {
			assertInstancesEquals( expected, actual, cls );
		}
	}
	
	public static void assertInstancesEquals(OWLReasoner expected, OWLReasoner actual, OWLClass cls) {
		Set<OWLNamedIndividual> expectedIndividuals = expected.getInstances( cls, true ).getFlattened();
		Set<OWLNamedIndividual> actualIndividuals = actual.getInstances( cls, true ).getFlattened();
		
		assertEquals( "Instances different for Class: " + cls, expectedIndividuals, actualIndividuals );
	}
	
	public static void assertTypesEquals(OWLReasoner expected, OWLReasoner actual) {
		for( OWLNamedIndividual ind : actual.getInstances( OWL.Thing, false ).getFlattened() ) {
			assertTypesEquals( expected, actual, ind );
		}
	}
	
	public static void assertTypesEquals(OWLReasoner expected, OWLReasoner actual, OWLNamedIndividual individual) {
		Set<OWLClass> expectedTypes = expected.getTypes( individual, true ).getFlattened();
		Set<OWLClass> actualTypes = actual.getTypes( individual, true ).getFlattened();
		
		assertEquals( "Types different for individual: " + individual, expectedTypes, actualTypes );
	}

	
	public static void runDisjointnessTest(OWLOntology ontology, ModuleExtractor modExtractor) {
		runComparisonTest(ontology, modExtractor, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertDisjointnessEquals(expected, actual);		
			}
		});
	}
	
	public static void runDisjointnessUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions) {
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertDisjointnessEquals( expected, actual );
			}
		});
	}
	
	public static void runInstancesTest(OWLOntology ontology, ModuleExtractor modExtractor) {
		runComparisonTest(ontology, modExtractor, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertInstancesEquals(expected, actual);		
			}
		});
	}
	
	public static void runInstancesUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions) {
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertInstancesEquals( expected, actual );
			}
		});
	}

	public static void runTypesTest(OWLOntology ontology, ModuleExtractor modExtractor) {
		runComparisonTest(ontology, modExtractor, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertTypesEquals(expected, actual);		
			}
		});
	}
	
	public static void runTypesUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions) {
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertTypesEquals( expected, actual );
			}
		});
	}
	
	public static void runUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, 
			Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions) throws OWLException {
		runComparisonUpdateTest(ontology, modExtractor, additions, deletions, new ReasonerComparisonMethod() {
			public void compare(OWLReasoner expected, OWLReasoner actual) {
				assertClassificationEquals( expected, actual );
			}
		});
	}
	
	private static void runComparisonTest(OWLOntology ontology, ModuleExtractor modExtractor, ReasonerComparisonMethod comparisonMethod) {
		PelletReasoner unified = PelletReasonerFactory.getInstance().createNonBufferingReasoner( ontology );
		IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, modExtractor );
		
		PelletOptions.USE_CLASSIFICATION_MONITOR = PelletOptions.MonitorType.CONSOLE;
		modular.classify();
		unified.getKB().classify();
		
		comparisonMethod.compare( unified, modular );
		
		modular.dispose();
	}
	
	private static void runComparisonUpdateTest(OWLOntology ontology, ModuleExtractor modExtractor, 
			Collection<OWLAxiom> additions, Collection<OWLAxiom> deletions, ReasonerComparisonMethod comparisonMethod) {
		PelletReasoner unified = PelletReasonerFactory.getInstance().createNonBufferingReasoner( ontology );
		IncrementalClassifier modular = PelletIncremantalReasonerFactory.getInstance().createReasoner( ontology, modExtractor );

		PelletOptions.USE_CLASSIFICATION_MONITOR = PelletOptions.MonitorType.CONSOLE;
		modular.classify();

		comparisonMethod.compare( unified, modular );
		
		OntologyUtils.addAxioms( ontology, additions );		
		OntologyUtils.removeAxioms( ontology, deletions );
		
		modular.classify();
		unified.flush();
		unified.getKB().classify();
		
		modular.timers.print();

		comparisonMethod.compare( unified, modular );
		
		modular.dispose();
	}
	
	public static <T> Set<T> set(T... elements) {
		switch ( elements.length ) {
		case 0:
			return emptySet();
		case 1:
			return singleton( elements[0] );
		default:
			return new HashSet<T>( Arrays.asList( elements ) );
		}
	}
	
	private interface ReasonerComparisonMethod {
		public void compare(OWLReasoner expected, OWLReasoner actual);
	}
}
