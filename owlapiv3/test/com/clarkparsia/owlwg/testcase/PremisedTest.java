package com.clarkparsia.owlwg.testcase;

import java.util.Set;

/**
 * <p>
 * Title: Premised Test Case
 * </p>
 * <p>
 * Description: Shared interface of all premised tests.
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
public interface PremisedTest<O> extends TestCase<O> {

	public Set<SerializationFormat> getPremiseFormats();

	public String getPremiseOntology(SerializationFormat format);

	public O parsePremiseOntology(SerializationFormat format) throws OntologyParseException;
}