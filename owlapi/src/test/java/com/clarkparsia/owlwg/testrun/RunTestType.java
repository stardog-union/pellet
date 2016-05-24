package com.clarkparsia.owlwg.testrun;

import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.CONSISTENCY_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.INCONSISTENCY_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.NEGATIVE_ENTAILMENT_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.POSITIVE_ENTAILMENT_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.SYNTAX_CONSTRAINT_RUN;
import static com.clarkparsia.owlwg.testrun.ResultVocabulary.Class.SYNTAX_TRANSLATION_RUN;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * <p>
 * Title: Run Test Type
 * </p>
 * <p>
 * Description:
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
public enum RunTestType {

	CONSISTENCY(CONSISTENCY_RUN), INCONSISTENCY(INCONSISTENCY_RUN),
	NEGATIVE_ENTAILMENT(NEGATIVE_ENTAILMENT_RUN), POSITIVE_ENTAILMENT(POSITIVE_ENTAILMENT_RUN),
	SYNTAX_CONSTRAINT(SYNTAX_CONSTRAINT_RUN), SYNTAX_TRANSLATION(SYNTAX_TRANSLATION_RUN);

	private final OWLClass	_c;

	private RunTestType(ResultVocabulary.Class c) {
		this._c = c.getOWLClass();
	}

	public OWLClass getOWLClass() {
		return _c;
	}
}
