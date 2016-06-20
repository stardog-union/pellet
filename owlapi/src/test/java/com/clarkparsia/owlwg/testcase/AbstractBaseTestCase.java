package com.clarkparsia.owlwg.testcase;

import static com.clarkparsia.owlwg.testcase.TestVocabulary.DatatypeProperty.IDENTIFIER;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.Individual.FULL;
import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.*;
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
import org.semanticweb.owlapi.search.EntitySearcher;

import com.clarkparsia.owlwg.testcase.TestVocabulary.Individual;

/**
 * <p>
 * Title: Abstract Base Test Case
 * </p>
 * <p>
 * Description: Common base implementation openllet.shared.hash by all test case
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

	private final String						_identifier;

	private final Map<IRI, ImportedOntology>	_imports;

	private final EnumSet<SyntaxConstraint>		_notsatisfied;

	private final EnumSet<Semantics>			_notsemantics;

	private final EnumSet<SyntaxConstraint>		_satisfied;

	private final EnumSet<Semantics>			_semantics;

	private final Status						_status;

	private final IRI							_iri;

	public AbstractBaseTestCase(OWLOntology ontology, OWLNamedIndividual i) {

		_iri = i.getIRI();

        Map<OWLDataPropertyExpression, Collection<OWLLiteral>> dpValues = EntitySearcher
                .getDataPropertyValues(i, ontology).asMap();
        Collection<OWLLiteral> identifiers = dpValues.get(IDENTIFIER
                .getOWLDataProperty());
		if( identifiers == null ) {
            throw new NullPointerException();
        }
		if( identifiers.size() != 1 ) {
            throw new IllegalArgumentException();
        }

		_identifier = identifiers.iterator().next().getLiteral();

        Map<OWLObjectPropertyExpression, Collection<OWLIndividual>> opValues = EntitySearcher
                .getObjectPropertyValues(i, ontology).asMap();

		_imports = new HashMap<>();
        Collection<OWLIndividual> importedOntologies = opValues
                .get(IMPORTED_ONTOLOGY
				.getOWLObjectProperty() );
		if( importedOntologies != null ) {
			for( OWLIndividual ind : importedOntologies ) {
				ImportedOntology io = new ImportedOntologyImpl( ontology, ind.asOWLNamedIndividual() );
				_imports.put( io.getIRI(), io );
			}
		}

        Collection<OWLIndividual> statuses = opValues.get(STATUS
                .getOWLObjectProperty());
		if( statuses == null || statuses.isEmpty() ) {
            _status = null;
        } else if( statuses.size() > 1 ) {
            throw new IllegalArgumentException();
        } else {
			OWLNamedIndividual s = statuses.iterator().next().asOWLNamedIndividual();
			_status = Status.get( s );
			if( _status == null ) {
                throw new NullPointerException( format(
						"Unexpected _status ( %s ) for test case %s", s.getIRI().toURI().toASCIIString(), i
								.getIRI() ) );
            }
		}

		_satisfied = EnumSet.noneOf( SyntaxConstraint.class );
        Collection<OWLIndividual> profiles = opValues.get(PROFILE
                .getOWLObjectProperty());
		if( profiles != null ) {
			for( OWLIndividual p : profiles ) {
				SyntaxConstraint c = SyntaxConstraint.get( p );
				if( c == null ) {
                    throw new NullPointerException( format(
							"Unexpected profile ( %s ) for test case %s", p.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
                }
				_satisfied.add( c );
			}
		}

        Collection<OWLIndividual> species = opValues.get(SPECIES
                .getOWLObjectProperty());
		if( species != null ) {
			for( OWLIndividual s : species ) {
				if( FULL.getOWLIndividual().equals( s ) ) {
                    continue;
                }
				if( Individual.DL.getOWLIndividual().equals( s ) ) {
                    _satisfied.add( SyntaxConstraint.DL );
                } else {
                    throw new IllegalArgumentException( format(
							"Unexpected species ( %s ) for test case %s", s.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
                }
			}
		}

		_semantics = EnumSet.noneOf( Semantics.class );
        Collection<OWLIndividual> sems = opValues.get(SEMANTICS
                .getOWLObjectProperty());
		if( sems != null ) {
			for( OWLIndividual sem : sems ) {
				Semantics s = Semantics.get( sem );
				if( s == null ) {
                    throw new NullPointerException( format(
							"Unexpected _semantics ( %s ) for test case %s ", sem.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
                }
				_semantics.add( s );
			}
		}

        Map<OWLObjectPropertyExpression, Collection<OWLIndividual>> nopValues = EntitySearcher
                .getNegativeObjectPropertyValues(i, ontology).asMap();

		_notsatisfied = EnumSet.noneOf( SyntaxConstraint.class );

        Collection<OWLIndividual> notprofiles = nopValues.get(PROFILE
                .getOWLObjectProperty());
		if( notprofiles != null ) {
			for( OWLIndividual p : notprofiles ) {
				SyntaxConstraint c = SyntaxConstraint.get( p );
				if( c == null ) {
                    throw new NullPointerException( format(
							"Unexpected profile ( %s ) for test case %s", p.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
                }
				_notsatisfied.add( c );
			}
		}

        Collection<OWLIndividual> notspecies = nopValues.get(SPECIES
                .getOWLObjectProperty());
		if( notspecies != null ) {
			for( OWLIndividual s : notspecies ) {
				if( Individual.DL.getOWLIndividual().equals( s ) ) {
                    _notsatisfied.add( SyntaxConstraint.DL );
                } else {
                    throw new IllegalArgumentException( format(
							"Unexpected species ( %s ) for test case %s", s.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
                }
			}
		}

		_notsemantics = EnumSet.noneOf( Semantics.class );
        Collection<OWLIndividual> notsems = nopValues.get(SEMANTICS
                .getOWLObjectProperty());
		if( notsems != null ) {
			for( OWLIndividual sem : notsems ) {
				Semantics s = Semantics.get( sem );
				if( s == null ) {
                    throw new NullPointerException( format(
							"Unexpected _semantics ( %s ) for test case %s", sem.asOWLNamedIndividual().getIRI()
									.toURI().toASCIIString(), i.getIRI() ) );
                }
				_notsemantics.add( s );
			}
		}
	}

	@Override
    public void dispose() {
		_imports.clear();
		_notsatisfied.clear();
		_semantics.clear();
	}
	
	@Override
    public Set<Semantics> getApplicableSemantics() {
		return unmodifiableSet( _semantics );
	}

	@Override
    public String getIdentifier() {
		return _identifier;
	}

	@Override
    public String getImportedOntology(IRI iri, SerializationFormat format) {
		ImportedOntology io = _imports.get( iri );
		if( io == null ) {
            return null;
        } else {
            return io.getOntology( format );
        }
	}

	@Override
    public Set<IRI> getImportedOntologies() {
		return unmodifiableSet( _imports.keySet() );
	}

	@Override
    public Set<SerializationFormat> getImportedOntologyFormats(IRI iri) {
		ImportedOntology io = _imports.get( iri );
		if( io == null ) {
            return EnumSet.noneOf( SerializationFormat.class );
        } else {
            return io.getFormats();
        }
	}

	@Override
    public Set<Semantics> getNotApplicableSemantics() {
		return unmodifiableSet( _notsemantics );
	}

	@Override
    public Set<SyntaxConstraint> getSatisfiedConstraints() {
		return unmodifiableSet( _satisfied );
	}

	@Override
    public Status getStatus() {
		return _status;
	}

	@Override
    public Set<SyntaxConstraint> getUnsatisfiedConstraints() {
		return unmodifiableSet( _notsatisfied );
	}

	@Override
    public IRI getIRI() {
		return _iri;
	}
}
