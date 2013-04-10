package com.clarkparsia.owlwg.testcase;

import java.util.Set;

/**
 * <p>
 * Title: Entailment Test Case
 * </p>
 * <p>
 * Description: Shared interface of all entailment tests.
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
public interface EntailmentTest<O> extends PremisedTest<O> {

	public Set<SerializationFormat> getConclusionFormats();

	public String getConclusionOntology(SerializationFormat format);

	public O parseConclusionOntology(SerializationFormat format) throws OntologyParseException;
}