package com.clarkparsia.owlwg.testcase;

import java.util.Set;
import org.semanticweb.owlapi.model.IRI;

/**
 * <p>
 * Title: Test Case
 * </p>
 * <p>
 * Description: Interface based on test cases described at <a href="http://www.w3.org/TR/owl2-test/">http://www.w3.org/TR/owl2-test/</a>. Parameterized based on
 * the object returned when parsing an ontology.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public interface TestCase<O>
{

	public void accept(final TestCaseVisitor<O> visitor);

	public void dispose();

	public Set<Semantics> getApplicableSemantics();

	public String getIdentifier();

	public Set<IRI> getImportedOntologies();

	public String getImportedOntology(final IRI iri, final SerializationFormat format);

	public Set<SerializationFormat> getImportedOntologyFormats(final IRI iri);

	public Set<Semantics> getNotApplicableSemantics();

	public Set<SyntaxConstraint> getSatisfiedConstraints();

	public Status getStatus();

	public Set<SyntaxConstraint> getUnsatisfiedConstraints();

	public IRI getIRI();
}
