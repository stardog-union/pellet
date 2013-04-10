package com.clarkparsia.owlwg.testrun;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * <p>
 * Title: Result Vocabulary
 * </p>
 * <p>
 * Description: Entities declared in the <a
 * href="http://www.w3.org/2007/OWL/wiki/Test_Result_Format">OWL 2 Test Result
 * Ontology</a>.
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
public class ResultVocabulary {

	public enum AnnotationProperty {
		DETAILS("details");

		private final IRI	iri;

		private AnnotationProperty(String localName) {
			iri = IRI.create( IRI_BASE + localName );
		}

		public IRI getAnnotationPropertyIRI() {
			return iri;
		}
	}

	public enum Class {
		CONSISTENCY_RUN("ConsistencyRun"), FAILING_RUN("FailingRun"),
		INCOMPLETE_RUN("IncompleteRun"), INCONSISTENCY_RUN("InconsistencyRun"),
		NEGATIVE_ENTAILMENT_RUN("NegativeEntailmentRun"), PASSING_RUN("PassingRun"),
		POSITIVE_ENTAILMENT_RUN("PositiveEntailmentRun"),
		SYNTAX_CONSTRAINT_RUN("SyntaxConstraintRun"),
		SYNTAX_TRANSLATION_RUN("SyntaxTranslationRun"), TEST_RUN("TestRun");

		private final OWLClass	cls;

		private Class(String localName) {
			cls = manager.getOWLDataFactory().getOWLClass( IRI.create( IRI_BASE + localName ) );
		}

		public OWLClass getOWLClass() {
			return cls;
		}
	}

	public enum ObjectProperty {
		SYNTAX_CONSTRAINT("syntaxConstraint"), RUNNER("runner"), TEST("test");

		private final OWLObjectProperty	op;

		private ObjectProperty(String localName) {
			op = manager.getOWLDataFactory().getOWLObjectProperty(
					IRI.create( IRI_BASE + localName ) );
		}

		public OWLObjectProperty getOWLObjectProperty() {
			return op;
		}
	}

	private static final OWLOntologyManager	manager;
	public static final IRI					ONTOLOGY_IRI;

	private static final String				IRI_BASE;

	static {
		final String ontiri = "http://www.w3.org/2007/OWL/testResultOntology";

		ONTOLOGY_IRI = IRI.create( ontiri );
		IRI_BASE = ontiri + "#";

		manager = OWLManager.createOWLOntologyManager();
	}
}
