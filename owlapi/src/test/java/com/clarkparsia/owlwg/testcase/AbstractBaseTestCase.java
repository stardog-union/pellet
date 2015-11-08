package com.clarkparsia.owlwg.testcase;

import static com.clarkparsia.owlwg.testcase.TestVocabulary.DatatypeProperty.IDENTIFIER;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.Individual.FULL;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.IMPORTED_ONTOLOGY;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.PROFILE;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.SEMANTICS;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.SPECIES;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.STATUS;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlwg.testcase.TestVocabulary.Individual;

/**
 * <p>
 * Title: Abstract Base Test Case
 * </p>
 * <p>
 * Description: Common base implementation shared by all test case
 * implementations.
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
public abstract class AbstractBaseTestCase<O> implements TestCase<O> {

	private final String						identifier;

	private final Map<IRI, ImportedOntology>	imports;

	private final EnumSet<SyntaxConstraint>		notsatisfied;

	private final EnumSet<Semantics>			notsemantics;

	private final EnumSet<SyntaxConstraint>		satisfied;

	private final EnumSet<Semantics>			semantics;

	private final Status						status;

	private final IRI							iri;

	public AbstractBaseTestCase(OWLOntology ontology, OWLNamedIndividual i) {

		iri = i.getIRI();

		Map<OWLDataPropertyExpression, Set<OWLLiteral>> dpValues = i
				.getDataPropertyValues( ontology );
		Set<OWLLiteral> identifiers = dpValues.get( IDENTIFIER.getOWLDataProperty() );
		if( identifiers == null )
			throw new NullPointerException();
		if( identifiers.size() != 1 )
			throw new IllegalArgumentException();

		identifier = identifiers.iterator().next().getLiteral();

		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> opValues = i
				.getObjectPropertyValues( ontology );

		imports = new HashMap<IRI, ImportedOntology>();
		Set<OWLIndividual> importedOntologies = opValues.get( IMPORTED_ONTOLOGY
				.getOWLObjectProperty() );
		if( importedOntologies != null ) {
			for( OWLIndividual ind : importedOntologies ) {
				ImportedOntology io = new ImportedOntologyImpl( ontology, ind.asOWLNamedIndividual() );
				imports.put( io.getIRI(), io );
			}
		}

		Set<OWLIndividual> statuses = opValues.get( STATUS.getOWLObjectProperty() );
		if( statuses == null || statuses.isEmpty() )
			status = null;
		else if( statuses.size() > 1 )
			throw new IllegalArgumentException();
		else {
			OWLNamedIndividual s = statuses.iterator().next().asOWLNamedIndividual();
			status = Status.get( s );
			if( status == null )
				throw new NullPointerException( format(
						"Unexpected status ( %s ) for test case %s", s.getIRI().toURI().toASCIIString(), i
								.getIRI() ) );
		}

		satisfied = EnumSet.noneOf( SyntaxConstraint.class );
		Set<OWLIndividual> profiles = opValues.get( PROFILE.getOWLObjectProperty() );
		if( profiles != null ) {
			for( OWLIndividual p : profiles ) {
				SyntaxConstraint c = SyntaxConstraint.get( p );
				if( c == null )
					throw new NullPointerException( format(
							"Unexpected profile ( %s ) for test case %s", p.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				satisfied.add( c );
			}
		}

		Set<OWLIndividual> species = opValues.get( SPECIES.getOWLObjectProperty() );
		if( species != null ) {
			for( OWLIndividual s : species ) {
				if( FULL.getOWLIndividual().equals( s ) )
					continue;
				if( Individual.DL.getOWLIndividual().equals( s ) )
					satisfied.add( SyntaxConstraint.DL );
				else
					throw new IllegalArgumentException( format(
							"Unexpected species ( %s ) for test case %s", s.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
			}
		}

		semantics = EnumSet.noneOf( Semantics.class );
		Set<OWLIndividual> sems = opValues.get( SEMANTICS.getOWLObjectProperty() );
		if( sems != null ) {
			for( OWLIndividual sem : sems ) {
				Semantics s = Semantics.get( sem );
				if( s == null )
					throw new NullPointerException( format(
							"Unexpected semantics ( %s ) for test case %s ", sem.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				semantics.add( s );
			}
		}

		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> nopValues = i
				.getNegativeObjectPropertyValues( ontology );

		notsatisfied = EnumSet.noneOf( SyntaxConstraint.class );

		Set<OWLIndividual> notprofiles = nopValues.get( PROFILE.getOWLObjectProperty() );
		if( notprofiles != null ) {
			for( OWLIndividual p : notprofiles ) {
				SyntaxConstraint c = SyntaxConstraint.get( p );
				if( c == null )
					throw new NullPointerException( format(
							"Unexpected profile ( %s ) for test case %s", p.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				notsatisfied.add( c );
			}
		}

		Set<OWLIndividual> notspecies = nopValues.get( SPECIES.getOWLObjectProperty() );
		if( notspecies != null ) {
			for( OWLIndividual s : notspecies ) {
				if( Individual.DL.getOWLIndividual().equals( s ) )
					notsatisfied.add( SyntaxConstraint.DL );
				else
					throw new IllegalArgumentException( format(
							"Unexpected species ( %s ) for test case %s", s.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
			}
		}

		notsemantics = EnumSet.noneOf( Semantics.class );
		Set<OWLIndividual> notsems = nopValues.get( SEMANTICS.getOWLObjectProperty() );
		if( notsems != null ) {
			for( OWLIndividual sem : notsems ) {
				Semantics s = Semantics.get( sem );
				if( s == null )
					throw new NullPointerException( format(
							"Unexpected semantics ( %s ) for test case %s", sem.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				notsemantics.add( s );
			}
		}
	}

	public void dispose() {
		imports.clear();
		notsatisfied.clear();
		semantics.clear();
	}
	
	public Set<Semantics> getApplicableSemantics() {
		return unmodifiableSet( semantics );
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getImportedOntology(IRI iri, SerializationFormat format) {
		ImportedOntology io = imports.get( iri );
		if( io == null )
			return null;
		else
			return io.getOntology( format );
	}

	public Set<IRI> getImportedOntologies() {
		return unmodifiableSet( imports.keySet() );
	}

	public Set<SerializationFormat> getImportedOntologyFormats(IRI iri) {
		ImportedOntology io = imports.get( iri );
		if( io == null )
			return EnumSet.noneOf( SerializationFormat.class );
		else
			return io.getFormats();
	}

	public Set<Semantics> getNotApplicableSemantics() {
		return unmodifiableSet( notsemantics );
	}

	public Set<SyntaxConstraint> getSatisfiedConstraints() {
		return unmodifiableSet( satisfied );
	}

	public Status getStatus() {
		return status;
	}

	public Set<SyntaxConstraint> getUnsatisfiedConstraints() {
		return unmodifiableSet( notsatisfied );
	}

	public IRI getIRI() {
		return iri;
	}
}
