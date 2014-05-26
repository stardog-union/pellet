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

import java.util.Collection;
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
import org.semanticweb.owlapi.search.Searcher;

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

        Collection<OWLLiteral> identifiers = Searcher.values(
                ontology.getDataPropertyAssertionAxioms(i),
                IDENTIFIER.getOWLDataProperty());
		if( identifiers.size() != 1 )
			throw new IllegalArgumentException();

		identifier = identifiers.iterator().next().getLiteral();

        Collection<OWLIndividual> importedOntologies = Searcher.values(
                ontology.getObjectPropertyAssertionAxioms(i),
                IMPORTED_ONTOLOGY.getOWLObjectProperty());

		imports = new HashMap<IRI, ImportedOntology>();
		if( importedOntologies != null ) {
			for( OWLIndividual ind : importedOntologies ) {
				ImportedOntology io = new ImportedOntologyImpl( ontology, ind.asOWLNamedIndividual() );
				imports.put( io.getIRI(), io );
			}
		}
        Collection<OWLIndividual> statuses = Searcher.values(
                ontology.getObjectPropertyAssertionAxioms(i),
                STATUS.getOWLObjectProperty());
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
        Collection<OWLIndividual> profiles = Searcher.values(
                ontology.getObjectPropertyAssertionAxioms(i),
                PROFILE.getOWLObjectProperty());
			for( OWLIndividual p : profiles ) {
				SyntaxConstraint c = SyntaxConstraint.get( p );
				if( c == null )
					throw new NullPointerException( format(
							"Unexpected profile ( %s ) for test case %s", p.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				satisfied.add( c );
			}
        Collection<OWLIndividual> species = Searcher.values(
                ontology.getObjectPropertyAssertionAxioms(i),
                SPECIES.getOWLObjectProperty());
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

		semantics = EnumSet.noneOf( Semantics.class );
        Collection<OWLIndividual> sems = Searcher.values(
                ontology.getObjectPropertyAssertionAxioms(i),
                SEMANTICS.getOWLObjectProperty());
			for( OWLIndividual sem : sems ) {
				Semantics s = Semantics.get( sem );
				if( s == null )
					throw new NullPointerException( format(
							"Unexpected semantics ( %s ) for test case %s ", sem.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				semantics.add( s );
			}

		notsatisfied = EnumSet.noneOf( SyntaxConstraint.class );

        Collection<OWLIndividual> notprofiles = Searcher.negValues(
                ontology.getNegativeObjectPropertyAssertionAxioms(i),
                PROFILE.getOWLObjectProperty());
			for( OWLIndividual p : notprofiles ) {
				SyntaxConstraint c = SyntaxConstraint.get( p );
				if( c == null )
					throw new NullPointerException( format(
							"Unexpected profile ( %s ) for test case %s", p.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				notsatisfied.add( c );
			}

        Collection<OWLIndividual> notspecies = Searcher.negValues(
                ontology.getNegativeObjectPropertyAssertionAxioms(i),
                SPECIES.getOWLObjectProperty());
			for( OWLIndividual s : notspecies ) {
				if( Individual.DL.getOWLIndividual().equals( s ) )
					notsatisfied.add( SyntaxConstraint.DL );
				else
					throw new IllegalArgumentException( format(
							"Unexpected species ( %s ) for test case %s", s.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
			}

		notsemantics = EnumSet.noneOf( Semantics.class );
        Collection<OWLIndividual> notsems = Searcher.negValues(
                ontology.getNegativeObjectPropertyAssertionAxioms(i),
                SEMANTICS.getOWLObjectProperty());
			for( OWLIndividual sem : notsems ) {
				Semantics s = Semantics.get( sem );
				if( s == null )
					throw new NullPointerException( format(
							"Unexpected semantics ( %s ) for test case %s", sem.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
				notsemantics.add( s );
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
