// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under
// the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package com.clarkparsia.pellet.owlapiv3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
@Deprecated
public class Reasoner implements OWLOntologyChangeListener {
	public static Logger		log					= Logger.getLogger( Reasoner.class.getName() );

	private static final long	serialVersionUID	= 8438190652175258123L;

	private AxiomConverter		converter;

	protected KnowledgeBase		kb;

	private PelletLoader		loader;

	private OWLOntologyManager	manager;

	private OWLDataFactory		factory;

	private interface EntityMapper<T extends OWLObject> {
		public T map(ATermAppl term);
	}

	private class IndividualMapper implements EntityMapper<OWLIndividual> {
		public OWLIndividual map(ATermAppl term) {
			if( ATermUtils.isBnode( term ) ) {
				return factory.getOWLAnonymousIndividual( ( (ATermAppl) term.getArgument( 0 ) ).getName() );
			}
			else {
				return factory.getOWLNamedIndividual( iri( term ) );
			}
		}
	}

	private class LiteralMapper implements EntityMapper<OWLLiteral> {
		public OWLLiteral map(ATermAppl term) {
			String lexValue = ((ATermAppl) term.getArgument( 0 )).toString();
			ATermAppl lang = (ATermAppl) term.getArgument( 1 );
			ATermAppl dtype = (ATermAppl) term.getArgument( 2 );
			if( dtype.equals( ATermUtils.PLAIN_LITERAL_DATATYPE ) ) {
				if( lang.equals( ATermUtils.EMPTY ) )
					return factory.getOWLStringLiteral( lexValue );
				else
					return factory.getOWLStringLiteral( lexValue, lang.toString() );
			}
			else {
				OWLDatatype datatype = DT_MAPPER.map( dtype );
				return factory.getOWLTypedLiteral( lexValue, datatype );
			}
		}
	}

	private class ObjectPropertyMapper implements EntityMapper<OWLObjectProperty> {
		public OWLObjectProperty map(ATermAppl term) {
			if( ATermUtils.TOP_OBJECT_PROPERTY.equals( term ) )
				return factory.getOWLTopObjectProperty();
			if( ATermUtils.BOTTOM_OBJECT_PROPERTY.equals( term ) )
				return factory.getOWLBottomObjectProperty();
			return factory.getOWLObjectProperty( iri( term ) );
		}
	}
	
	private class DataPropertyMapper implements EntityMapper<OWLDataProperty> {
		public OWLDataProperty map(ATermAppl term) {
			if( ATermUtils.TOP_DATA_PROPERTY.equals( term ) )
				return factory.getOWLTopDataProperty();
			if( ATermUtils.BOTTOM_DATA_PROPERTY.equals( term ) )
				return factory.getOWLBottomDataProperty();
			return factory.getOWLDataProperty( iri( term ) );
		}
	}
	
	private class DatatypeMapper implements EntityMapper<OWLDatatype> {
		public OWLDatatype map(ATermAppl term) {
			return factory.getOWLDatatype( iri( term ) );
		}
	}
	
	private class ClassMapper implements EntityMapper<OWLClass> {
		public OWLClass map(ATermAppl term) {
			if( term.equals( ATermUtils.TOP ) )
				return factory.getOWLThing();
			else if( term.equals( ATermUtils.BOTTOM ) )
				return factory.getOWLNothing();
			else
				return factory.getOWLClass( iri( term ) );
		}
	}
		
	private EntityMapper<OWLIndividual>		IND_MAPPER		= new IndividualMapper();

	private EntityMapper<OWLLiteral>		LIT_MAPPER		= new LiteralMapper();

	private EntityMapper<OWLObjectProperty>	OP_MAPPER		= new ObjectPropertyMapper();

	private EntityMapper<OWLDataProperty>	DP_MAPPER		= new DataPropertyMapper();

	private EntityMapper<OWLDatatype>		DT_MAPPER		= new DatatypeMapper();

	private EntityMapper<OWLClass>			CLASS_MAPPER	= new ClassMapper();

	@SuppressWarnings("unchecked")
	private EntityMapper<OWLClassExpression>	DESC_MAPPER		= (EntityMapper<OWLClassExpression>) (EntityMapper<? extends OWLClassExpression>) CLASS_MAPPER;

	@SuppressWarnings("unchecked")
	private EntityMapper<OWLDataRange>		DR_MAPPER		= (EntityMapper<OWLDataRange>) (EntityMapper<? extends OWLDataRange>) DT_MAPPER;

	private static IRI iri(ATermAppl term) {
		if( term.getArity() != 0 )
			throw new OWLRuntimeException( "Trying to convert an anonymous term " + term );

		return IRI.create( term.getName() );
	}

	/**
	 * Create an empty reasoner.
	 * 
	 * @param manager
	 *            ontology manager for this reasoner
	 */
	public Reasoner(OWLOntologyManager manager) {
		this( manager, new KnowledgeBase() );
	}

	/**
	 * Create a reasoner instance with the given KB. Any changes to the KB will
	 * effect this reasoner.
	 * 
	 * @param manager
	 *            ontology manager for this reasoner
	 * @param kb
	 *            underlying KB instance
	 */
	public Reasoner(OWLOntologyManager manager, KnowledgeBase kb) {
		this.kb = kb;
		this.loader = new PelletLoader( kb );
		this.manager = manager;
		this.factory = manager.getOWLDataFactory();
		this.converter = new AxiomConverter( kb, factory );

		loader.setManager( manager );
	}

	public void classify() {
		kb.classify();
	}

	public void clearOntologies() {
		loader.clear();
	}

	/**
	 * Convert an axiom represented in ATermAppl format to OWLAxiom
	 * 
	 * @param term -
	 *            axiom represented as an ATermAppl
	 * @return axiom as an OWLAxiom or <code>null</code> if conversion fails
	 */
	public OWLAxiom convertAxiom(ATermAppl term) {
		return converter.convert( term );
	}

	/**
	 * Convert a set of axioms represented in <code>ATermAppl</code> format to
	 * a set of <code>OWLAxiom</code>s
	 * 
	 * @param terms -
	 *            axioms represented as an ATermAppl
	 * @return axioms as <code>OWLAxiom</code> objects
	 * @throws OWLRuntimeException -
	 *             if conversion fails for one of the axioms
	 */
	public Set<OWLAxiom> convertAxioms(Set<ATermAppl> terms) throws OWLRuntimeException {
		Set<OWLAxiom> result = new HashSet<OWLAxiom>();

		for( ATermAppl term : terms ) {
			OWLAxiom axiom = converter.convert( term );
			if( axiom == null )
				throw new OWLRuntimeException( "Cannot convert: " + term );
			result.add( axiom );
		}

		return result;
	}

	public void dispose() {
		kb = null;
	}

	public Set<Set<OWLClass>> getAncestorClasses(OWLClassExpression c) {
		return toOWLEntitySetOfSet( kb.getSuperClasses( loader.term( c ) ), CLASS_MAPPER );
	}

	public Set<Set<OWLDataProperty>> getAncestorProperties(OWLDataProperty p) {
		return toOWLEntitySetOfSet( kb.getSuperProperties( loader.term( p ) ), DP_MAPPER );
	}

	public Set<Set<OWLObjectProperty>> getAncestorProperties(OWLObjectProperty p) {
		return toOWLEntitySetOfSet( kb.getSuperProperties( loader.term( p ) ), OP_MAPPER );
	}

	/**
	 * Return the set of all named classes defined in any of the ontologies
	 * loaded in the reasoner.
	 * 
	 * @return set of OWLClass objects
	 */
	public Set<OWLClass> getClasses() {
		return toOWLEntitySet( kb.getClasses(), CLASS_MAPPER );
	}

	public Set<OWLClass> getComplementClasses(OWLClassExpression c) {
		return toOWLEntitySet( kb.getComplements( loader.term( c ) ), CLASS_MAPPER );
	}

	public Set<OWLDataProperty> getDataProperties() {
		return toOWLEntitySet( kb.getDataProperties(), DP_MAPPER );
	}

	public Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationships(
			OWLNamedIndividual individual) {
		Map<OWLDataProperty, Set<OWLLiteral>> values = new HashMap<OWLDataProperty, Set<OWLLiteral>>();
		Set<OWLDataProperty> dataProps = getDataProperties();
		for( OWLDataProperty prop : dataProps ) {
			Set<OWLLiteral> set = getRelatedValues( individual, prop );
			if( !set.isEmpty() )
				values.put( prop, set );
		}

		return values;
	}

	public Set<Set<OWLClass>> getDescendantClasses(OWLClassExpression c) {
		return toOWLEntitySetOfSet( kb.getSubClasses( loader.term( c ) ), CLASS_MAPPER );
	}

	public Set<Set<OWLDataProperty>> getDescendantProperties(OWLDataProperty p) {
		return toOWLEntitySetOfSet( kb.getSubProperties( loader.term( p ), false ), DP_MAPPER );
	}

	public Set<Set<OWLObjectProperty>> getDescendantProperties(OWLObjectProperty p) {
		return toOWLEntitySetOfSet( kb.getSubProperties( loader.term( p ), false ), OP_MAPPER );
	}

	public Set<OWLIndividual> getDifferentFromIndividuals(OWLIndividual ind) {
		return toOWLEntitySet( kb.getDifferents( loader.term( ind ) ), IND_MAPPER );
	}

	public Set<Set<OWLClass>> getDisjointClasses(OWLClassExpression c) {
		return toOWLEntitySetOfSet( kb.getDisjoints( loader.term( c ) ), CLASS_MAPPER );
	}

	public Set<Set<OWLClassExpression>> getDomains(OWLDataProperty p) {
		ATermAppl some = ATermUtils.makeSomeValues( loader.term( p ), ATermUtils.TOP_LIT );
		return toOWLEntitySetOfSet( kb.getSuperClasses( some ), DESC_MAPPER );
	}

	public Set<Set<OWLClassExpression>> getDomains(OWLObjectProperty p) {
		ATermAppl some = ATermUtils.makeSomeValues( loader.term( p ), ATermUtils.TOP );
		return toOWLEntitySetOfSet( kb.getSuperClasses( some ), DESC_MAPPER );
	}

	public Set<OWLClass> getEquivalentClasses(OWLClassExpression c) {
		return toOWLEntitySet( kb.getEquivalentClasses( loader.term( c ) ), CLASS_MAPPER );
	}

	public Set<OWLClass> getAllEquivalentClasses(OWLClassExpression c) {
		return toOWLEntitySet( kb.getAllEquivalentClasses( loader.term( c ) ), CLASS_MAPPER );
	}

	public Set<OWLDataProperty> getEquivalentProperties(OWLDataProperty p) {
		return toOWLEntitySet( kb.getEquivalentProperties( loader.term( p ) ), DP_MAPPER );
	}

	public Set<OWLObjectProperty> getEquivalentProperties(OWLObjectProperty p) {
		return toOWLEntitySet( kb.getEquivalentProperties( loader.term( p ) ), OP_MAPPER );
	}

	/**
	 * Returns the explanation for the last performed reasoning operation. The
	 * last reasoning operation must be a boolean query that checks for a
	 * specific entailment, e.g. concept satisfiability, subclass entailment,
	 * etc. Note that, the options for turning on the explanation feature should
	 * be enabled to retrieve any explanation.
	 * 
	 * @return the explanation as a set of axioms that cause the entailment
	 * @throws OWLRuntimeException -
	 *             if no explanation was generated or axioms in the explanation
	 *             cannot be converted to <code>OWLAxiom</code>s
	 */
	public Set<OWLAxiom> getExplanation() throws OWLRuntimeException {
		Set<ATermAppl> explanation = kb.getExplanationSet();

		if( explanation == null || explanation.isEmpty() )
			throw new OWLRuntimeException( "No explanation computed" );

		return convertAxioms( explanation );
	}

	public Set<OWLClass> getInconsistentClasses() {
		return toOWLEntitySet( kb.getUnsatisfiableClasses(), CLASS_MAPPER );
	}

	/**
	 * Return the set of all individuals defined in any of the ontologies loaded
	 * in the reasoner.
	 * 
	 * @return set of OWLIndividual objects
	 */
	public Set<OWLIndividual> getIndividuals() {
		return toOWLEntitySet( kb.getIndividuals(), IND_MAPPER );
	}

	/**
	 * Returns all or only direct instances of a concept expression
	 */
	public Set<OWLNamedIndividual> getIndividuals(OWLClassExpression clsC, boolean direct) {
		Set<OWLIndividual> all = toOWLEntitySet( kb.getInstances( loader.term( clsC ), direct ),
				IND_MAPPER );
		Set<OWLNamedIndividual> ret = new HashSet<OWLNamedIndividual>( all.size() );
		for( OWLIndividual i : all ) {
			if( !i.isAnonymous() )
				ret.add( i.asOWLNamedIndividual() );
		}
		return ret;
	}

	public Set<Set<OWLObjectProperty>> getInverseProperties(OWLObjectProperty prop) {
		return Collections.singleton( toOWLEntitySet( kb.getInverses( loader.term( prop ) ),
				OP_MAPPER ) );
	}

	/**
	 * @return Returns the kb.
	 */
	public KnowledgeBase getKB() {
		return kb;
	}

	public Set<OWLOntology> getLoadedOntologies() {
		return loader.getOntologies();
	}

	public PelletLoader getLoader() {
		return loader;
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public Set<OWLObjectProperty> getObjectProperties() {
		return toOWLEntitySet( kb.getObjectProperties(), OP_MAPPER );
	}

	public Map<OWLObjectProperty, Set<OWLNamedIndividual>> getObjectPropertyRelationships(
			OWLNamedIndividual individual) {
		Map<OWLObjectProperty, Set<OWLNamedIndividual>> values = new HashMap<OWLObjectProperty, Set<OWLNamedIndividual>>();
		Set<OWLObjectProperty> objProps = getObjectProperties();
		for( OWLObjectProperty prop : objProps ) {
			Set<OWLNamedIndividual> set = getRelatedIndividuals( individual, prop );
			if( !set.isEmpty() )
				values.put( prop, set );
		}

		return values;
	}

	/**
	 * Return all the object and data properties defined in the loaded
	 * ontologies
	 */
	public Set<OWLProperty<?, ?>> getProperties() {
		Set<OWLProperty<?, ?>> properties = new HashSet<OWLProperty<?, ?>>();
		properties.addAll( getObjectProperties() );
		properties.addAll( getDataProperties() );

		return properties;
	}

	public Map<OWLIndividual, Set<OWLLiteral>> getDataPropertyAssertions(OWLDataProperty prop) {
		Map<OWLIndividual, Set<OWLLiteral>> map = new HashMap<OWLIndividual, Set<OWLLiteral>>();
		ATermAppl p = loader.term( prop );
		for( ATermAppl candidate : kb.getIndividuals() ) {
			List<ATermAppl> list = kb.getDataPropertyValues( p, candidate );
			if( list.isEmpty() )
				continue;

			OWLIndividual subj = IND_MAPPER.map( candidate );
			Set<OWLLiteral> objects = toOWLEntitySet( list, LIT_MAPPER );

			map.put( subj, objects );
		}

		return map;
	}

	public Map<OWLIndividual, Set<OWLIndividual>> getObjectPropertyAssertions(OWLObjectProperty prop) {
		Map<OWLIndividual, Set<OWLIndividual>> result = new HashMap<OWLIndividual, Set<OWLIndividual>>();
		ATermAppl p = loader.term( prop );

		Map<ATermAppl, List<ATermAppl>> values = kb.getPropertyValues( p );
		for( Map.Entry<ATermAppl, List<ATermAppl>> entry : values.entrySet() ) {
			ATermAppl subjTerm = entry.getKey();

			List<ATermAppl> objTerms = entry.getValue();

			OWLIndividual subj = IND_MAPPER.map( subjTerm );

			Set<OWLIndividual> objects = toOWLEntitySet( objTerms, IND_MAPPER );

			result.put( subj, objects );
		}

		return result;
	}

	public Set<OWLDataRange> getRanges(OWLDataProperty p) {
		return toOWLEntitySet( kb.getRanges( loader.term( p ) ), DR_MAPPER );
	}

	public Set<OWLClassExpression> getRanges(OWLObjectProperty p) {
		return toOWLEntitySet( kb.getRanges( loader.term( p ) ), DESC_MAPPER );
	}

	public Set<OWLNamedIndividual> getRelatedIndividuals(OWLNamedIndividual subject,
			OWLObjectPropertyExpression property) {

		final Set<OWLIndividual> all = toOWLEntitySet( kb.getObjectPropertyValues( loader
				.term( property ), loader.term( subject ) ), IND_MAPPER );
		if( all.isEmpty() )
			return Collections.emptySet();

		Set<OWLNamedIndividual> ret = new HashSet<OWLNamedIndividual>( all.size() );

		for( OWLIndividual ind : all ) {
			if( !ind.isAnonymous() )
				ret.add( ind.asOWLNamedIndividual() );
		}
		return ret;
	}

	public Set<OWLLiteral> getRelatedValues(OWLNamedIndividual subject,
			OWLDataPropertyExpression property) {
		return toOWLEntitySet( kb.getDataPropertyValues( loader.term( property ), loader
				.term( subject ) ), LIT_MAPPER );
	}

	/**
	 * Return a set of sameAs individuals given a specific individual based on
	 * axioms in the ontology
	 * 
	 * @param ind -
	 *            specific individual to test
	 * @return
	 * @throws OWLException
	 */
	public Set<OWLIndividual> getSameAsIndividuals(OWLIndividual ind) {
		return toOWLEntitySet( kb.getSames( loader.term( ind ) ), IND_MAPPER );
	}

	public Set<Set<OWLClass>> getSubClasses(OWLClassExpression c) {
		return toOWLEntitySetOfSet( kb.getSubClasses( loader.term( c ), true ), CLASS_MAPPER );
	}

	public Set<Set<OWLDataProperty>> getSubProperties(OWLDataProperty p) {
		return toOWLEntitySetOfSet( kb.getSubProperties( loader.term( p ), true ), DP_MAPPER );
	}

	public Set<Set<OWLObjectProperty>> getSubProperties(OWLObjectProperty p) {
		return toOWLEntitySetOfSet( kb.getSubProperties( loader.term( p ), true ), OP_MAPPER );
	}

	public Set<Set<OWLClass>> getSuperClasses(OWLClassExpression c) {
		return toOWLEntitySetOfSet( kb.getSuperClasses( loader.term( c ), true ), CLASS_MAPPER );
	}

	public Set<Set<OWLDataProperty>> getSuperProperties(OWLDataProperty p) {
		return toOWLEntitySetOfSet( kb.getSuperProperties( loader.term( p ), true ), DP_MAPPER );
	}

	public Set<Set<OWLObjectProperty>> getSuperProperties(OWLObjectProperty p) {
		return toOWLEntitySetOfSet( kb.getSuperProperties( loader.term( p ), true ), OP_MAPPER );
	}

//	public Set<Set<OWLClass>> getTypes(OWLNamedIndividual individual) {
//		return toOWLEntitySetOfSet( kb.getTypes( loader.term( individual ), true ), CLASS_MAPPER );
//	}

	/**
	 * Returns all the named classes that this individual belongs. This returns
	 * a set of sets where each set is an equivalent class
	 * 
	 * @param ind
	 * @return Set of OWLClassExpression objects
	 * @throws OWLException
	 */
	public Set<Set<OWLClass>> getTypes(OWLNamedIndividual ind, boolean direct) {
		return toOWLEntitySetOfSet( kb.getTypes( loader.term( ind ), direct ), CLASS_MAPPER );
	}

	public Set<OWLClass> getUnsatisfiableClasses() throws OWLReasonerException {
		return toOWLEntitySet( kb.getUnsatisfiableClasses(), CLASS_MAPPER );
	}

	public boolean hasDataPropertyRelationship(OWLNamedIndividual subject,
			OWLDataPropertyExpression property, OWLLiteral object) {
		return kb.hasPropertyValue( loader.term( subject ), loader.term( property ), loader
				.term( object ) );
	}

	public boolean hasDomain(OWLDataProperty p, OWLClassExpression c) {
		return kb.hasDomain( loader.term( p ), loader.term( c ) );
	}

	public boolean hasDomain(OWLObjectProperty p, OWLClassExpression c) {
		return kb.hasDomain( loader.term( p ), loader.term( c ) );
	}

	public boolean hasObjectPropertyRelationship(OWLNamedIndividual subject,
			OWLObjectPropertyExpression property, OWLNamedIndividual object) {
		return kb.hasPropertyValue( loader.term( subject ), loader.term( property ), loader
				.term( object ) );
	}

	public boolean hasRange(OWLDataProperty p, OWLDataRange d) {
		return kb.hasRange( loader.term( p ), loader.term( d ) );
	}

	public boolean hasRange(OWLObjectProperty p, OWLClassExpression c) {
		return kb.hasRange( loader.term( p ), loader.term( c ) );
	}

	/**
	 * Checks if the given individual is a direct or indirect instance of the
	 * given type
	 */
	public boolean hasType(OWLNamedIndividual individual, OWLClassExpression type, boolean direct) {
		if( direct )
			return getTypes( individual, direct ).contains( type );
		else
			return kb.isType( loader.term( individual ), loader.term( type ) );
	}

	
	public boolean isAntiSymmetric(OWLObjectProperty p) {
		return kb.isAsymmetricProperty( loader.term( p ) );
	}

	//@Override
	public boolean isAsymmetric(OWLObjectProperty p) {
		return kb.isAsymmetricProperty( loader.term( p ) );
	}
	
	public boolean isClassified() {
		return kb.isClassified();
	}

	public boolean isComplementOf(OWLClassExpression c1, OWLClassExpression c2) {
		return kb.isComplement( loader.term( c1 ), loader.term( c2 ) );
	}

	/**
	 * Returns true if the loaded ontology is consistent.
	 * 
	 * @param c
	 * @return
	 * @throws OWLException
	 */
	public boolean isConsistent() {
		return kb.isConsistent();
	}

	/**
	 * @deprecated Use {@link #isSatisfiable(OWLClassExpression)} instead
	 */
	public boolean isConsistent(OWLClassExpression d) {
		return isSatisfiable( d );
	}

	public boolean isConsistent(OWLOntology ontology) {
		setOntology( ontology );

		return isConsistent();
	}

	public boolean isDefined(OWLClass cls) {
		ATermAppl term = loader.term( cls );

		return kb.isClass( term );
	}

	public boolean isDefined(OWLDataProperty prop) {
		ATermAppl term = loader.term( prop );

		return kb.isDatatypeProperty( term );
	}

	public boolean isDefined(OWLIndividual ind) {
		ATermAppl term = loader.term( ind );

		return kb.isIndividual( term );
	}

	public boolean isDefined(OWLObjectProperty prop) {
		ATermAppl term = loader.term( prop );

		return kb.isObjectProperty( term );
	}

	/**
	 * Test if two individuals are owl:DifferentFrom each other.
	 * 
	 * @return
	 * @throws OWLException
	 */
	public boolean isDifferentFrom(OWLIndividual ind1, OWLIndividual ind2) {
		return kb.isDifferentFrom( loader.term( ind1 ), loader.term( ind2 ) );
	}

	public boolean isDisjointWith(OWLDataProperty p1, OWLDataProperty p2) {
		return kb.isDisjointProperty( loader.term( p1 ), loader.term( p2 ) );
	}

	public boolean isDisjointWith(OWLClassExpression c1, OWLClassExpression c2) {
		return kb.isDisjointClass( loader.term( c1 ), loader.term( c2 ) );
	}

	public boolean isDisjointWith(OWLObjectProperty p1, OWLObjectProperty p2) {
		return kb.isDisjointProperty( loader.term( p1 ), loader.term( p2 ) );
	}

//	public boolean isEntailed(OWLOntology ont) {
//		return isEntailed( ont.getAxioms() );
//	}
//
//	public boolean isEntailed(Set<? extends OWLAxiom> axioms) {
//		EntailmentChecker entailmentChecker = new EntailmentChecker( this );
//		return entailmentChecker.isEntailed(axioms);
//	}
//
//	public boolean isEntailed(OWLAxiom axiom) {
//		return isEntailed(Collections.singleton(axiom));
//	}

	public boolean isEquivalentClass(OWLClassExpression c1, OWLClassExpression c2) {
		return kb.isEquivalentClass( loader.term( c1 ), loader.term( c2 ) );
	}

	public boolean isEquivalentProperty(OWLDataProperty p1, OWLDataProperty p2) {
		return kb.isEquivalentProperty( loader.term( p1 ), loader.term( p2 ) );
	}

	public boolean isEquivalentProperty(OWLObjectProperty p1, OWLObjectProperty p2) {
		return kb.isEquivalentProperty( loader.term( p1 ), loader.term( p2 ) );
	}
	
	public boolean isFunctional(OWLDataProperty p) {
		return kb.isFunctionalProperty( loader.term( p ) );
	}

	public boolean isFunctional(OWLObjectProperty p) {
		return kb.isFunctionalProperty( loader.term( p ) );
	}

	public boolean isInverseFunctional(OWLObjectProperty p) {
		return kb.isInverseFunctionalProperty( loader.term( p ) );
	}

	public boolean isInverseOf(OWLObjectProperty p1, OWLObjectProperty p2) {
		return kb.isInverse( loader.term( p1 ), loader.term( p2 ) );
	}

	public boolean isIrreflexive(OWLObjectProperty p) {
		return kb.isIrreflexiveProperty( loader.term( p ) );
	}

	public boolean isRealised() throws OWLReasonerException {
		return kb.isRealized();
	}

	public boolean isReflexive(OWLObjectProperty p) {
		return kb.isReflexiveProperty( loader.term( p ) );
	}

	/**
	 * Test if two individuals are owl:DifferentFrom each other.
	 * 
	 * @return
	 * @throws OWLException
	 */
	public boolean isSameAs(OWLIndividual ind1, OWLIndividual ind2) {
		return kb.isSameAs( loader.term( ind1 ), loader.term( ind2 ) );
	}

	/**
	 * Returns true if the given class is satisfiable.
	 * 
	 * @param c
	 * @return
	 * @throws OWLException
	 */
	public boolean isSatisfiable(OWLClassExpression d) {
		if( !kb.isConsistent() )
			return false;

		return kb.isSatisfiable( loader.term( d ) );
	}

	public boolean isSubClassOf(OWLClassExpression c1, OWLClassExpression c2) {
		return kb.isSubClassOf( loader.term( c1 ), loader.term( c2 ) );
	}

	public boolean isSubPropertyOf(OWLDataProperty c1, OWLDataProperty c2) {
		return kb.isSubPropertyOf( loader.term( c1 ), loader.term( c2 ) );
	}

	public boolean isSubPropertyOf(OWLObjectProperty c1, OWLObjectProperty c2) {
		return kb.isSubPropertyOf( loader.term( c1 ), loader.term( c2 ) );
	}

	public boolean isSubTypeOf(OWLDatatype d1, OWLDatatype d2) {
		return kb.isSubClassOf( loader.term( d1 ), loader.term( d2 ) );
	}

	public boolean isSymmetric(OWLObjectProperty p) {
		return kb.isSymmetricProperty( loader.term( p ) );
	}

	public boolean isTransitive(OWLObjectProperty p) {
		return kb.isTransitiveProperty( loader.term( p ) );
	}

	public void loadOntologies(Set<OWLOntology> ontologies) {
		if( manager == null ) {
			log
					.warning( "Cannot load an ontology without an ontology manager. Use setManager(OWLOntologyManager) first." );
			return;
		}

		loader.load( ontologies );
	}

	public void loadOntology(OWLOntology ontology) {
		loadOntologies( Collections.singleton( ontology ) );
	}

	/**
	 * Listens to ontology changes and refreshes the underlying KB. Applies some
	 * of the ontology changes incrementally but applies full reload if
	 * incremental update cannot be handled for one of the changes. All
	 * additions can be handled incrementally but removal of some axioms cannot
	 * be handled. Note that, the incremental processing here is meant to refer
	 * only loading and not reasoning, i.e. it is different from the incremental
	 * reasoning support provided by Pellet.
	 */
	public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
		boolean changesApplied = loader.applyChanges( changes );

		if( !changesApplied ) {
			refresh();
		}
	}

	public void realise() {
		kb.realize();
	}

	public void refresh() {
		loader.reload();
	}

	/**
	 * This will first clear the ontologies and then load the give ontology with
	 * is imports
	 */
	public void setOntology(OWLOntology ontology) {
		clearOntologies();
		loadOntologies( Collections.singleton( ontology ) );
	}

	private <T extends OWLObject> Set<Set<T>> toOWLEntitySetOfSet(Set<Set<ATermAppl>> setOfTerms,
			EntityMapper<T> mapper) {
		Set<Set<T>> results = new HashSet<Set<T>>();
		for( Set<ATermAppl> terms : setOfTerms ) {
			Set<T> entitySet = toOWLEntitySet( terms, mapper );
			if( !entitySet.isEmpty() )
				results.add( entitySet );
		}

		return results;
	}

	private <T extends OWLObject> Set<T> toOWLEntitySet(Collection<ATermAppl> terms,
			EntityMapper<T> mapper) {
		Set<T> results = new HashSet<T>();
		for( ATermAppl term : terms )
			results.add( mapper.map( term ) );

		return results;
	}

	public void unloadOntologies(Set<OWLOntology> ontologies) {
		loader.unload( ontologies );

		refresh();
	}

	public void unloadOntology(OWLOntology ontology) {
		unloadOntologies( Collections.singleton( ontology ) );
	}
}
