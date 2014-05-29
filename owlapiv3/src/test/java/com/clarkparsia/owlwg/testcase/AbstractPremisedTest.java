package com.clarkparsia.owlwg.testcase;

import static java.lang.String.format;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: Abstract Premised Test Case
 * </p>
 * <p>
 * Description: Common base implementation shared by consistency, positive
 * entailment, and negative entailment tests.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public abstract class AbstractPremisedTest<O> extends AbstractBaseTestCase<O> implements
		PremisedTest<O> {

	private static final Logger							log;
	static {
		log = Logger.getLogger( AbstractPremisedTest.class.getCanonicalName() );
	}

	private final EnumSet<SerializationFormat>			premiseFormats;
	private final EnumMap<SerializationFormat, String>	premiseOntologyLiteral;

	public AbstractPremisedTest(OWLOntology ontology, OWLNamedIndividual i) {
		super( ontology, i );

		premiseFormats = EnumSet.noneOf( SerializationFormat.class );
		premiseOntologyLiteral = new EnumMap<SerializationFormat, String>(
				SerializationFormat.class );

		Map<OWLDataPropertyExpression, Set<OWLLiteral>> values = i
				.getDataPropertyValues( ontology );

		for( SerializationFormat f : SerializationFormat.values() ) {
			Set<OWLLiteral> premises = values.get( f.getPremiseOWLDataProperty() );
			if( premises != null ) {
				if( premises.size() > 1 ) {
					log
							.warning( format(
									"Multiple premise ontologies found for testcase (%s) with serialization format (%s).  Choosing arbitrarily.",
									getIdentifier(), f ) );
				}
				premiseOntologyLiteral.put( f, premises.iterator().next().getLiteral() );
				premiseFormats.add( f );
			}
		}
	}

	public void dispose() {
		premiseFormats.clear();
		premiseOntologyLiteral.clear();
		super.dispose();
	}
	
	public Set<SerializationFormat> getPremiseFormats() {
		return Collections.unmodifiableSet( premiseFormats );
	}

	public String getPremiseOntology(SerializationFormat format) {
		return premiseOntologyLiteral.get( format );
	}
}
