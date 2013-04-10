package com.clarkparsia.pellet.datatypes;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.Literal;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;

/**
 * <p>
 * Title: Datatype Reasoner
 * </p>
 * <p>
 * Description: Reasoner encapsulating all concrete domain and datatype handling
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public interface DatatypeReasoner {

	/**
	 * Check that the intersection of a collection of data ranges contains a
	 * minimum number of elements.
	 * 
	 * @param n
	 *            the minimum number of elements
	 * @param ranges
	 *            the data ranges to intersect
	 * @return <code>true</code> if the intersection of <code>ranges</code>
	 *         contains at least <code>n</code> elements, <code>false</code>
	 *         else
	 */
	public boolean containsAtLeast(int n, Collection<ATermAppl> ranges)
			throws InvalidConstrainingFacetException, InvalidLiteralException,
			UnrecognizedDatatypeException;

	/**
	 * Declare a named datatype. See {@link #isDeclared(ATermAppl)}.
	 * 
	 * @param name
	 *            the name (normally URI) of the datatype
	 * @return <code>true</code> if <code>name</code> has not been previously
	 *         declared, <code>false</code> else.
	 */
	public boolean declare(ATermAppl name);

	/**
	 * Get the canonical representation of a literal. There is a 1:1 mapping
	 * from elements in a datatype value space and the canonical
	 * representations.
	 * 
	 * @param literal
	 *            the literal to canonicalize
	 * @return the canonical form of the literal
	 * @throws InvalidLiteralException
	 *             if the literal is not in the lexical space of the datatype
	 * @throws UnrecognizedDatatypeException
	 *             if the datatype is not recognized
	 */
	public ATermAppl getCanonicalRepresentation(ATermAppl literal) throws InvalidLiteralException,
			UnrecognizedDatatypeException;

	/**
	 * Get the datatype identified by a uri
	 * 
	 * @param uri
	 *            the datatype identifier (e.g., <code>xsd:integer</code>)
	 * @return the <code>Datatype</code> if <code>uri</code> is recognized,
	 *         <code>null</code> otherwise
	 */
	public Datatype<?> getDatatype(ATermAppl uri);

	/**
	 * Get the canonical {@link ATermAppl} literal representation for a value
	 * space object
	 * 
	 * @param value
	 *            Element of the value space for some datatype
	 * @return The {@link ATermAppl} representation of <code>value</code>
	 * @throws IllegalArgumentException
	 *             if <code>value</code> is not contained in the value space of
	 *             any recognized datatype
	 */
	public ATermAppl getLiteral(Object value);

	/**
	 * Get the Java object representation of the data value. E.g., if the
	 * literal passed in represents "1"^^xsd:byte, then the object returned is a
	 * <code>java.lang.Byte</code>.
	 * 
	 * @param literal
	 * @return Java object representation of <code>literal</code>
	 * @throws InvalidLiteralException
	 *             if the literal is not in the lexical space of the datatype
	 * @throws UnrecognizedDatatypeException
	 *             if the datatype is not recognized
	 */
	public Object getValue(ATermAppl literal) throws InvalidLiteralException,
			UnrecognizedDatatypeException;

	/**
	 * Check if a specific datatype or named data range has been declared.
	 * Declaratations can happen by
	 * <ul>
	 * <li>The datatype being built-in (e.g., <code>xsd:integer</code>)</li>
	 * <li>A call to {@link #declare(ATermAppl)}, which typically precedes a
	 * call to {@link #define(ATermAppl, ATermAppl)}</li>
	 * <li>A call to {@link #define(ATermAppl, ATermAppl)}</li>
	 * 
	 * @param name
	 *            the name (normally URI) of the datatype
	 * @return <code>true</code> if <code>name</code> has been declared, <code>
	 *         false</code>
	 *         else
	 */
	public boolean isDeclared(ATermAppl name);

	/**
	 * Check if a specific datatype or named data range can be supported by the
	 * datatype reasoner. I.e., it is either built-in or was the
	 * <code>name</code> parameter of a call
	 * {@link #define(ATermAppl, ATermAppl)}
	 * 
	 * @param name
	 *            the name (normally URI) of the datatype or named data range
	 * @return <code>true</code> if <code>name</code> is supported,
	 *         <code>false</code> else
	 */
	public boolean isDefined(ATermAppl name);

	/**
	 * Returns the definition for the given datatype name if it is defined, or <code>null</code> otherwise.
	 * 
	 * @param name the name of the datatype 
	 * @return the definition for the given datatype name if it is defined, or <code>null</code> otherwise.
	 */
	public ATermAppl getDefinition(ATermAppl name);

	/**
	 * Shorthand for {@link #isSatisfiable(Collection, Object)} where
	 * <code>value == null</code>.
	 * 
	 * @param dataranges
	 *            The data ranges on the literal node
	 * @return <code>true</code> if the conjunction of the data ranges is
	 *         satisfiable, <code>false</code> else.
	 */
	public boolean isSatisfiable(Collection<ATermAppl> dataranges)
			throws InvalidConstrainingFacetException, InvalidLiteralException,
			UnrecognizedDatatypeException;

	/**
	 * Check the data ranges on a single literal node are satisfiable
	 * (independent of other nodes).
	 * 
	 * @param dconjunction
	 *            The data ranges on the literal node
	 * @param value
	 *            A constant value asserted for the node or <code>null</code> if
	 *            no such value is present.
	 * @return <code>true</code> if the conjunction of the data ranges is
	 *         satisfiable, <code>false</code> else.
	 */
	public boolean isSatisfiable(Collection<ATermAppl> dataranges, Object value)
			throws InvalidConstrainingFacetException, InvalidLiteralException,
			UnrecognizedDatatypeException;

	/**
	 * Check that the data ranges and inequalities asserted on a collection of
	 * literal nodes are satisfiable.
	 * 
	 * @param nodes
	 *            A set representing all literals with in-edges from a single
	 *            individual
	 * @param ineqs
	 *            A map representing all literal inequality constraints
	 * @return <code>true</code> if the data ranges and inequalities are
	 *         satisfiable, <code>false</code> else.
	 */
	public boolean isSatisfiable(Set<Literal> nodes, Map<Literal, Set<Literal>> ineqs)
			throws InvalidConstrainingFacetException, InvalidLiteralException,
			UnrecognizedDatatypeException;

	
	/**
	 * Return a collection of all known data ranges.
	 */
	public Collection<ATermAppl> listDataRanges();
	
	/*
	 * TODO: For all isSatisfiable calls, consider if this could do a more fine
	 * grained job tracking dependency sets in the event it returns false.
	 */

	/**
	 * Name a data range (see OWL 2 <code>DatatypeDefinition</code>)
	 * 
	 * @param name
	 *            a URI used to refer to the datatype
	 * @param datarange
	 *            the datarange
	 * @return true if successful, false if <code>name</code> is already used
	 */
	public boolean define(ATermAppl name, ATermAppl datarange);

	/**
	 * Check if a typed literal is valid (i.e., it is in the lexical space for
	 * its datatype)
	 * 
	 * @param literal
	 *            The literal
	 * @return <code>true</code> if the literal is in the lexical space of the
	 *         datatype, <code>false</code>else
	 */
	public boolean validLiteral(ATermAppl typedLiteral) throws UnrecognizedDatatypeException;

	/**
	 * Get an iterator for the values of a finite datarange
	 * 
	 * @param dataranges
	 *            the data ranges
	 * @return an {@link Iterator}
	 * @throws InvalidConstrainingFacetException
	 *             if <code>dataranges</code> contains a datatype restriction in
	 *             which the constraining facet is not valid
	 * @throws InvalidLiteralException
	 *             if <code>dataranges</code> references a literal value that is
	 *             invalid
	 * @throws UnrecognizedDatatypeException
	 *             if <code>dataranges</code> references an unrecognized
	 *             datatype
	 * @throws IllegalArgumentException
	 *             if the conjunction of <code>dataranges</code> is not
	 *             enumerable
	 */
	public Iterator<?> valueIterator(Collection<ATermAppl> dataranges)
			throws InvalidConstrainingFacetException, InvalidLiteralException,
			UnrecognizedDatatypeException;
}
