package openllet.owlapi;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public interface OWLManagedObject extends OWLManagementObject
{
	abstract public OWLNamedIndividual getMe();

	default Stream<OWLNamedIndividual> getObjects(final IRI property)
	{
		return getObjects(getMe(), property);
	}

	default Optional<OWLNamedIndividual> getObject(final IRI property)
	{
		return getObjects(property).findAny();
	}

	default void updateObject(final IRI property, final OWLNamedIndividual object)
	{
		final OWLObjectProperty owlProperty = getFactory().getOWLObjectProperty(property);
		removeObjectPropertyAxiom(owlProperty, getMe());
		addObjectPropertyAxiom(owlProperty, getMe(), object);
	}

	default Set<OWLLiteral> getValues(final IRI property)
	{
		return getValues(getMe(), property);
	}

	default Optional<OWLLiteral> getValue(final IRI property)
	{
		final Set<OWLLiteral> values = getValues(getMe(), property);
		return values.isEmpty() ? Optional.empty() : Optional.of(values.iterator().next());
	}

	default void addObject(final IRI property, final OWLNamedIndividual object)
	{
		final OWLObjectProperty owlProperty = getFactory().getOWLObjectProperty(property);
		addObjectPropertyAxiom(owlProperty, getMe(), object);
	}

	default void addValue(final IRI property, final OWLLiteral literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void addValue(final IRI property, final String literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void addValue(final IRI property, final int literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void addValue(final IRI property, final double literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void updateValue(final IRI property, final OWLLiteral literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		removeDataPropertyAxiom(owlProperty, getMe());
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void updateValue(final IRI property, final String literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		removeDataPropertyAxiom(owlProperty, getMe());
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void updateValue(final IRI property, final int literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		removeDataPropertyAxiom(owlProperty, getMe());
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}

	default void updateValue(final IRI property, final double literal)
	{
		final OWLDataProperty owlProperty = getFactory().getOWLDataProperty(property);
		removeDataPropertyAxiom(owlProperty, getMe());
		addDataPropertyAxiom(owlProperty, getMe(), literal);
	}
}
