package com.clarkparsia.pellet.owlapiv3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.exceptions.PelletRuntimeException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.VersionInfo;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.util.Version;

import aterm.ATermAppl;

public class PelletReasoner implements OWLReasoner, OWLOntologyChangeListener  {

	public static final Logger log = Logger.getLogger( PelletReasoner.class.getName() );

	private static final Set<InferenceType> PRECOMPUTABLE_INFERENCES = EnumSet.of(InferenceType.CLASS_HIERARCHY,
	                InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_HIERARCHY,
	                InferenceType.DATA_PROPERTY_HIERARCHY);
	
	private static final Version VERSION = createVersion();
	
	private static Version createVersion() {
		String versionString = VersionInfo.getInstance().getVersionString();
		String[] versionNumbers = versionString.split( "\\." );
		
		int major = parseNumberIfExists( versionNumbers, 0 );
		int minor = parseNumberIfExists( versionNumbers, 1 );
		int patch = parseNumberIfExists( versionNumbers, 2 );
		int build = parseNumberIfExists( versionNumbers, 3 );
		
		return new Version( major, minor, patch, build );
	}
	
	private static int parseNumberIfExists(String[] numbers, int index) {
		try {
			if( 0 <= index && index < numbers.length )
				return Integer.parseInt( numbers[index] );
		} catch( NumberFormatException e ) {
			log.log( Level.FINE, "Invalid number in version identifier: " + numbers[index], e );
		}
		
		return 0;
	}

	private class ChangeVisitor implements OWLOntologyChangeVisitor {

		private boolean	reloadRequired;

		public boolean isReloadRequired() {
			return reloadRequired;
		}

		/**
		 * Process a change, providing a single call for common
		 * reset,accept,isReloadRequired pattern.
		 * 
		 * @param change
		 *            the {@link OWLOntologyChange} to process
		 * @return <code>true</code> if change is handled, <code>false</code> if
		 *         a reload is required
		 */
		public boolean process(OWLOntologyChange change) {
			this.reset();
			change.accept( this );
			return !isReloadRequired();
		}

		public void reset() {
			visitor.reset();
			reloadRequired = false;
		}

		public void visit(AddAxiom change) {
			visitor.setAddAxiom( true );
			change.getAxiom().accept( visitor );
			reloadRequired = visitor.isReloadRequired();
		}

		public void visit(RemoveAxiom change) {
			visitor.setAddAxiom( false );
			change.getAxiom().accept( visitor );
			reloadRequired = visitor.isReloadRequired();
		}

		public void visit(AddImport change) {
			reloadRequired = true;
		}

		public void visit(AddOntologyAnnotation change) {

		}

		public void visit(RemoveImport change) {
			reloadRequired = true;
		}

		public void visit(RemoveOntologyAnnotation change) {
			
		}

		public void visit(SetOntologyID change) {
			
		}

	}
	
	private class ClassMapper extends EntityMapper<OWLClass> {
		@Override
        public OWLClass map(ATermAppl term) {
			if( term.equals( ATermUtils.TOP ) )
				return factory.getOWLThing();
			else if( term.equals( ATermUtils.BOTTOM ) )
				return factory.getOWLNothing();
			else
				return factory.getOWLClass( iri( term ) );
		}
	}
	
	private class DataPropertyMapper extends EntityMapper<OWLDataProperty> {
		@Override
        public OWLDataProperty map(ATermAppl term) {
			if( ATermUtils.TOP_DATA_PROPERTY.equals( term ) )
				return factory.getOWLTopDataProperty();
			if( ATermUtils.BOTTOM_DATA_PROPERTY.equals( term ) )
				return factory.getOWLBottomDataProperty();
			return factory.getOWLDataProperty( iri( term ) );
		}
	}
	
	private class DatatypeMapper extends EntityMapper<OWLDatatype> {
		@Override
        public OWLDatatype map(ATermAppl term) {
			return factory.getOWLDatatype( iri( term ) );
		}
	}

	private abstract class EntityMapper<T extends OWLObject> {
		
		public abstract T map(ATermAppl term);
		
		final public Set<T> map( final Collection<ATermAppl> terms ) {
			Set<T> mappedSet = new HashSet<T>();
			for ( ATermAppl term : terms ) {
				T mapped = map( term );
				if ( mapped != null )
					mappedSet.add( mapped );
			}
			return mappedSet;
		}
	}

	private class LiteralMapper extends EntityMapper<OWLLiteral> {
		@Override
        public OWLLiteral map(ATermAppl term) {
			String lexValue = ((ATermAppl) term.getArgument( 0 )).getName();
			ATermAppl lang = (ATermAppl) term.getArgument( 1 );
			ATermAppl dtype = (ATermAppl) term.getArgument( 2 );
			if( dtype.equals( ATermUtils.PLAIN_LITERAL_DATATYPE ) ) {
				if( lang.equals( ATermUtils.EMPTY ) )
					return factory.getOWLLiteral( lexValue );
				else
					return factory.getOWLLiteral( lexValue, lang.toString() );
			}
			else {
				OWLDatatype datatype = DT_MAPPER.map( dtype );
				return factory.getOWLLiteral( lexValue, datatype );
			}		
		}
	}
	
	private class NamedIndividualMapper extends EntityMapper<OWLNamedIndividual> {
		@Override
        public OWLNamedIndividual map(ATermAppl term) {
			if( ATermUtils.isBnode( term ) ) {
				return null;
			}
			else {
				return factory.getOWLNamedIndividual( iri( term ) );
			}
		}
	}
	
	private class ObjectPropertyMapper extends EntityMapper<OWLObjectPropertyExpression> {
		@Override
        public OWLObjectPropertyExpression map(ATermAppl term) {
			if( ATermUtils.TOP_OBJECT_PROPERTY.equals( term ) )
				return factory.getOWLTopObjectProperty();
			if( ATermUtils.BOTTOM_OBJECT_PROPERTY.equals( term ) )
				return factory.getOWLBottomObjectProperty();
			if( ATermUtils.isInv( term ) )
				return factory.getOWLObjectInverseOf( OP_MAPPER.map(term) );
			return factory.getOWLObjectProperty( iri( term ) );
		}
	}
	
	private static IRI iri(ATermAppl term) {
		if( term.getArity() != 0 )
			throw new OWLRuntimeException( "Trying to convert an anonymous term " + term );

		return IRI.create( term.getName() );
	}
		


	private final OWLDataFactory			factory;
	private KnowledgeBase			kb;
	private final OWLOntologyManager		manager;
	private final ReasonerProgressMonitor	monitor;
	/**
	 * Main ontology for reasoner
	 */
	private final OWLOntology				ontology;
	/**
	 * Imports closure for ontology
	 */
	private Set<OWLOntology>		importsClosure;
	private boolean 				shouldRefresh;
	private final PelletVisitor			visitor;
	
	private final BufferingMode			bufferingMode;
	private final List<OWLOntologyChange>	pendingChanges;
	
	private final IndividualNodeSetPolicy individualNodeSetPolicy;

	private final ChangeVisitor						changeVisitor 	= new ChangeVisitor();
	
	private final EntityMapper<OWLNamedIndividual>	IND_MAPPER		= new NamedIndividualMapper();

	private final EntityMapper<OWLLiteral>			LIT_MAPPER		= new LiteralMapper();

	private final EntityMapper<OWLObjectPropertyExpression>		OP_MAPPER		= new ObjectPropertyMapper();

	private final EntityMapper<OWLDataProperty>		DP_MAPPER		= new DataPropertyMapper();

	private final EntityMapper<OWLDatatype>			DT_MAPPER		= new DatatypeMapper();

	private final EntityMapper<OWLClass>				CLASS_MAPPER	= new ClassMapper();

	
	public PelletReasoner(OWLOntology ontology, BufferingMode bufferingMode) {
		this( ontology, new SimpleConfiguration( new NullReasonerProgressMonitor(),
				org.mindswap.pellet.PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING
					? FreshEntityPolicy.ALLOW
					: FreshEntityPolicy.DISALLOW, 0, IndividualNodeSetPolicy.BY_SAME_AS ), bufferingMode );
	}
	
	/**
	 * Create a reasoner for the given ontology and configuration.
	 * @param ontology
	 */
	public PelletReasoner(OWLOntology ontology, OWLReasonerConfiguration config, BufferingMode bufferingMode) throws IllegalConfigurationException {
		
		individualNodeSetPolicy = config.getIndividualNodeSetPolicy();
		
		if( !getFreshEntityPolicy().equals( config.getFreshEntityPolicy() ) ) {
			throw new IllegalConfigurationException(
					"PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING conflicts with reasoner configuration",
					config );
		}

		this.ontology = ontology;
		monitor = config.getProgressMonitor();
		

		kb = new KnowledgeBase();
		kb.setTaxonomyBuilderProgressMonitor( new ProgressAdapter( monitor ) );
		if( config.getTimeOut() > 0 ) {
			kb.timers.mainTimer.setTimeout( config.getTimeOut() );
		}
		
		this.manager = ontology.getOWLOntologyManager();
		this.factory = manager.getOWLDataFactory();
		this.visitor = new PelletVisitor( kb );
		
		this.bufferingMode = bufferingMode;
		
		manager.addOntologyChangeListener( this );
		
		this.shouldRefresh = true;
		this.pendingChanges = new ArrayList<OWLOntologyChange>();
		
		refresh();
	}

	private PelletRuntimeException convert(PelletRuntimeException e) throws InconsistentOntologyException,
			ReasonerInterruptedException, TimeOutException, FreshEntitiesException {

		if( e instanceof org.mindswap.pellet.exceptions.TimeoutException ) {
			throw new TimeOutException();
		}
		
		if( e instanceof org.mindswap.pellet.exceptions.TimerInterruptedException ) {
			throw new ReasonerInterruptedException( e );
		}
		
		if( e instanceof org.mindswap.pellet.exceptions.InconsistentOntologyException ) {
			throw new InconsistentOntologyException();
		}
		
		if( e instanceof org.mindswap.pellet.exceptions.UndefinedEntityException ) {
			Set<OWLEntity> unknown = Collections.emptySet();
			throw new FreshEntitiesException( unknown );
		}
		
		return e;
	}

	public void dispose() {
		kb = null;
		manager.removeOntologyChangeListener( this );
	}

	/**
	 * {@inheritDoc}
	 */
	public void flush() {
		processChanges( pendingChanges );
		pendingChanges.clear();
		refreshCheck();
	}

	public Node<OWLClass> getBottomClassNode() {		
		return getUnsatisfiableClasses();
	}

	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		refreshCheck();
		return toDataPropertyNode( kb.getAllEquivalentProperties( ATermUtils.BOTTOM_DATA_PROPERTY ) );
	}

	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		refreshCheck();
		return toObjectPropertyNode( kb.getAllEquivalentProperties( ATermUtils.BOTTOM_OBJECT_PROPERTY ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public BufferingMode getBufferingMode() {
		return bufferingMode;
	}

	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();

		try {
			ATermAppl some = ATermUtils.makeSomeValues(term(pe), ATermUtils.TOP_LIT);

			Set<ATermAppl> equivalents = kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty()) {
				return toClassNodeSet(Collections.singleton(equivalents));
			}
			
			Set<Set<ATermAppl>> result = kb.getSuperClasses( some, direct );
			if (!equivalents.isEmpty()) {
				result.add(equivalents);
			}

			return toClassNodeSet(result);
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind, OWLDataProperty pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toLiteralSet( kb.getDataPropertyValues( term( pe ), term( ind ) ) );
		}
		catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Set<OWLLiteral> getAnnotationPropertyValues(OWLNamedIndividual ind, OWLAnnotationProperty pe)
	throws InconsistentOntologyException, FreshEntitiesException,
	ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toLiteralSet( kb.getPropertyValues(term( pe ), term( ind ) ) );
		}
		catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}
	
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {			
			return getIndividualNodeSet( kb.getDifferents( term( ind ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {

		refreshCheck();
		try {			
			Set<Set<ATermAppl>> disjoints = kb.getDisjointClasses( term( ce ) );
			return toClassNodeSet( disjoints );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Node<OWLDataProperty>> values = new HashSet<Node<OWLDataProperty>>();
			for( Set<ATermAppl> val : kb.getDisjointProperties( term( pe ) ) ) {
				values.add( toDataPropertyNode( val ) );
			}

			return new OWLDataPropertyNodeSet( values );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Node<OWLObjectPropertyExpression>> values = new HashSet<Node<OWLObjectPropertyExpression>>();
			for( Set<ATermAppl> val : kb.getDisjointProperties( term( pe ) ) ) {
				values.add( toObjectPropertyNode( val ) );
			}

			return new OWLObjectPropertyNodeSet( values );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toClassNode( kb.getAllEquivalentClasses( term( ce ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toDataPropertyNode( kb.getAllEquivalentProperties( term( pe ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toObjectPropertyNode( kb.getAllEquivalentProperties( term( pe ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return individualNodeSetPolicy;
	}
	
	private NodeSet<OWLNamedIndividual> getIndividualNodeSetBySameAs( Collection<ATermAppl> individuals ) {
		Set<Node<OWLNamedIndividual>> instances = new HashSet<Node<OWLNamedIndividual>>();
		Set<ATermAppl> seen = new HashSet<ATermAppl>();
		for( ATermAppl ind : individuals ) {
			if( !seen.contains( ind ) ) {
				Set<ATermAppl> equiv = kb.getAllSames( ind );
				instances.add( toIndividualNode( equiv ) );
				seen.addAll( equiv );
			}
		}

		return new OWLNamedIndividualNodeSet( instances );
	}
	
	private NodeSet<OWLNamedIndividual> getIndividualNodeSetByName( Collection<ATermAppl> individuals ) {
		Set<Node<OWLNamedIndividual>> instances = new HashSet<Node<OWLNamedIndividual>>();
		
		for( ATermAppl ind : individuals ) {			
			for ( ATermAppl equiv :  kb.getAllSames( ind ) ) {
				instances.add( toIndividualNode( equiv ) );			
			}
		}
		
		return new OWLNamedIndividualNodeSet( instances );
	}
	
	private NodeSet<OWLNamedIndividual> getIndividualNodeSet( Collection<ATermAppl> individuals ) {
		if ( IndividualNodeSetPolicy.BY_NAME.equals( individualNodeSetPolicy ) ) {
			return getIndividualNodeSetByName( individuals );
		} else if ( IndividualNodeSetPolicy.BY_SAME_AS.equals( individualNodeSetPolicy ) ) {
			return getIndividualNodeSetBySameAs( individuals );
		} else {
			throw new AssertionError( "Unsupported IndividualNodeSetPolicy : " + individualNodeSetPolicy );
		}
	}

	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce, boolean direct)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {			
			return getIndividualNodeSet( kb.getInstances( term( ce ), direct ) );	
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toObjectPropertyNode( kb.getInverses( term( pe ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}
	
	/**
	 * Return the underlying Pellet knowledge base.
	 * 
	 * @return the underlying Pellet knowledge base
	 */
	public KnowledgeBase getKB() {
		return kb;
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			ATermAppl some = ATermUtils.makeSomeValues(term(pe), ATermUtils.TOP);

			Set<ATermAppl> equivalents = kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty()) {
				return toClassNodeSet(Collections.singleton(equivalents));
			}
			
			Set<Set<ATermAppl>> result = kb.getSuperClasses( some, direct );
			if (!equivalents.isEmpty()) {
				result.add(equivalents);
			}

			return toClassNodeSet(result);
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			ATermAppl some = ATermUtils.makeSomeValues(ATermUtils.makeInv(term(pe)), ATermUtils.TOP);

			Set<ATermAppl> equivalents = kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty()) {
				return toClassNodeSet(Collections.singleton(equivalents));
			}
			
			Set<Set<ATermAppl>> result = kb.getSuperClasses( some, direct );
			if (!equivalents.isEmpty()) {
				result.add(equivalents);
			}

			return toClassNodeSet(result);
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind,
			OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {			
			return getIndividualNodeSet( kb.getObjectPropertyValues( term( pe ), term( ind ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public Set<OWLAxiom> getPendingAxiomAdditions() {
		return Collections.emptySet();
	}

	public Set<OWLAxiom> getPendingAxiomRemovals() {
		return Collections.emptySet();
	}

	public List<OWLOntologyChange> getPendingChanges() {
		return pendingChanges;
	}

	public String getReasonerName() {
		return PelletReasonerFactory.getInstance().getReasonerName();
	}

	/**
	 * {@inheritDoc}
	 */
	public Version getReasonerVersion() {
		return VERSION;
	}

	public OWLOntology getRootOntology() {
		return ontology;
	}

	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return toIndividualNode( kb.getAllSames( term( ind ) ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Set<ATermAppl>> result = kb.getSubClasses( term( ce ), direct );
			return toClassNodeSet( result );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Node<OWLDataProperty>> values = new HashSet<Node<OWLDataProperty>>();
			for( Set<ATermAppl> val : kb.getSubProperties( term( pe ), direct ) ) {
				values.add( toDataPropertyNode( val ) );
			}
			return new OWLDataPropertyNodeSet( values );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression pe,
			boolean direct) throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Node<OWLObjectPropertyExpression>> values = new HashSet<Node<OWLObjectPropertyExpression>>();
			for( Set<ATermAppl> val : kb.getSubProperties( term( pe ), direct ) ) {
				values.add( toObjectPropertyNode( val ) );
			}
			return new OWLObjectPropertyNodeSet( values );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Set<ATermAppl>> result = kb.getSuperClasses( term( ce ), direct );
			return toClassNodeSet( result );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Node<OWLDataProperty>> values = new HashSet<Node<OWLDataProperty>>();
			for( Set<ATermAppl> val : kb.getSuperProperties( term( pe ), direct ) ) {
				values.add( toDataPropertyNode( val ) );
			}
			return new OWLDataPropertyNodeSet( values );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression pe,
			boolean direct) throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Node<OWLObjectPropertyExpression>> values = new HashSet<Node<OWLObjectPropertyExpression>>();
			for( Set<ATermAppl> val : kb.getSuperProperties( term( pe ), direct ) ) {
				values.add( toObjectPropertyNode( val ) );
			}
			return new OWLObjectPropertyNodeSet( values );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}
	
	public long getTimeOut() {
		return kb.timers.mainTimer.getTimeout();
	}
	
	public Node<OWLClass> getTopClassNode() {
		refreshCheck();
		return toClassNode( kb.getAllEquivalentClasses( ATermUtils.TOP ) );
	}
	
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		refreshCheck();
		return toDataPropertyNode( kb.getAllEquivalentProperties( ATermUtils.TOP_DATA_PROPERTY ) );
	}
	
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		refreshCheck();
		return toObjectPropertyNode( kb.getAllEquivalentProperties( ATermUtils.TOP_OBJECT_PROPERTY ) );
	}
	
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			Set<Set<ATermAppl>> result = kb.getTypes( term( ind ), direct );
			return toClassNodeSet( result );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public FreshEntityPolicy getFreshEntityPolicy() {
		return org.mindswap.pellet.PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING ?
			FreshEntityPolicy.ALLOW : FreshEntityPolicy.DISALLOW;
	}

	public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException,
			TimeOutException {
		refreshCheck();
		try {
			return toClassNode( kb.getAllUnsatisfiableClasses() );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public void interrupt() {
		kb.timers.interrupt();
	}
	
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		try {
			return kb.isConsistent();
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}
	
	public boolean isEntailed(OWLAxiom axiom) throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException,
			FreshEntitiesException, InconsistentOntologyException {
		refreshCheck();
		try {
			return isEntailed( Collections.singleton( axiom ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public boolean isEntailed(Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException,
			FreshEntitiesException, InconsistentOntologyException {
		refreshCheck();
		try {
			EntailmentChecker entailmentChecker = new EntailmentChecker( this );
			return entailmentChecker.isEntailed( axioms );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
		return !EntailmentChecker.UNSUPPORTED_ENTAILMENT.contains( axiomType );
	}

	public boolean isSatisfiable(OWLClassExpression classExpression)
			throws ReasonerInterruptedException, TimeOutException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			InconsistentOntologyException {
		refreshCheck();
		try {
			return kb.isSatisfiable( term( classExpression ) );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
		switch( bufferingMode ) {
		case BUFFERING:
			pendingChanges.addAll( changes );
			break;
		case NON_BUFFERING:
			processChanges( changes );
			break;
		default:
			throw new AssertionError( "Unexpected buffering mode: " + bufferingMode );
		}
	}

	/**
	 * Process all the given changes in an incremental fashion. Processing will
	 * stop if a change cannot be handled incrementally and requires a reload.
	 * The reload will not be done as part of processing.
	 * 
	 * @param changes
	 *            the changes to be applied to the reasoner
	 * @return <code>true</code> if all changes have been processed
	 *         successfully, <code>false</code> otherwise (indicates reasoner
	 *         will reload the whole ontology next time it needs to do
	 *         reasoning)
	 */
	public boolean processChanges(List<? extends OWLOntologyChange> changes) {
		if( shouldRefresh )
			return false;
		
		for( OWLOntologyChange change : changes ) {
			
			if( log.isLoggable( Level.FINER ) ) 
				log.fine( "Changed: " + change + " in " + change.getOntology() );
			
			if( !importsClosure.contains( change.getOntology() ) )
				continue;

			if( !changeVisitor.process( change ) ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Reload required by ontology change " + change );

				shouldRefresh = true;
				break;
			}
		}
		
		return !shouldRefresh;
	}

	/**
	 * {@inheritDoc}
	 */
	public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException {
		refreshCheck();
		if ( kb.isConsistent() ) {
			kb.realize();
		}
	}

	/**
	 * Clears the reasoner and reloads all the axioms in the imports closure.
	 */
	public void refresh() {
		visitor.clear();
		kb.clear();
		
		importsClosure = ontology.getImportsClosure();
		
		visitor.setAddAxiom( true );
		for ( OWLOntology ont : importsClosure ) {
			ont.accept( visitor );
		}
		visitor.verify();
		
		shouldRefresh = false;
	}
	
	/**
	 * Make sure the reasoner is ready to answer queries. This function does 
	 * not process changes but if changes processed earlier required a refresh
	 * this funciton will call {@link #refresh()}.
	 */
	private void refreshCheck() {
		if( kb == null )
			throw new OWLRuntimeException( "Trying to use a disposed reasoner" );

		if( shouldRefresh )
			refresh();
	}
	
	public ATermAppl term( OWLObject d ) {
		refreshCheck();
		
		visitor.reset();
		visitor.setAddAxiom( false );
		d.accept( visitor );

		ATermAppl a = visitor.result();

		if( a == null )
			throw new InternalReasonerException( "Cannot create ATerm from description " + d );

		return a;
	}

	private NodeSet<OWLClass> toClassNodeSet( Set<Set<ATermAppl>> termSets ) {
		Set<Node<OWLClass>> nodes = new HashSet<Node<OWLClass>>();
		for( Set<ATermAppl> termSet : termSets ) {
			nodes.add( toClassNode( termSet ) );
		}
		return new OWLClassNodeSet( nodes );
	}

	private Node<OWLClass> toClassNode( Set<ATermAppl> terms ) {
		return NodeFactory.getOWLClassNode( CLASS_MAPPER.map( terms ) );
	}

	private Node<OWLDataProperty> toDataPropertyNode( Set<ATermAppl> terms ) {
		return NodeFactory.getOWLDataPropertyNode( DP_MAPPER.map( terms ) );
	}

	private Node<OWLNamedIndividual> toIndividualNode( Set<ATermAppl> terms ) {
		return NodeFactory.getOWLNamedIndividualNode( IND_MAPPER.map( terms ) );
	}

	private Set<OWLLiteral> toLiteralSet( Collection<ATermAppl> terms ) {
		return LIT_MAPPER.map( terms );
	}

	private Node<OWLObjectPropertyExpression> toObjectPropertyNode( Set<ATermAppl> terms ) {
		return NodeFactory.getOWLObjectPropertyNode( OP_MAPPER.map( terms ) );
	}
	
	private Node<OWLNamedIndividual> toIndividualNode( ATermAppl term ) {
		return NodeFactory.getOWLNamedIndividualNode( IND_MAPPER.map( term ) );
	}

	/**
     * {@inheritDoc}
     */
    public Set<InferenceType> getPrecomputableInferenceTypes() {
	    return PRECOMPUTABLE_INFERENCES;
    }

	/**
     * {@inheritDoc}
     */
    public boolean isPrecomputed(InferenceType inferenceType) {
		switch (inferenceType) {
			case CLASS_HIERARCHY:
				return kb.isClassified();
			case CLASS_ASSERTIONS:
				return kb.isRealized();
			case OBJECT_PROPERTY_HIERARCHY:
				return kb.getRBox().isObjectTaxonomyPrepared();
			case DATA_PROPERTY_HIERARCHY:
				return kb.getRBox().isDataTaxonomyPrepared();
			default:
				return false;
		}
    }

	/**
     * {@inheritDoc}
     */
    public void precomputeInferences(InferenceType... inferenceTypes) throws ReasonerInterruptedException,
                    TimeOutException, InconsistentOntologyException {
    	for (InferenceType inferenceType : inferenceTypes) {
    		switch (inferenceType) {
    			case CLASS_HIERARCHY:
    				kb.classify();
    			case CLASS_ASSERTIONS:
    				kb.realize();
    			case OBJECT_PROPERTY_HIERARCHY:
    				kb.getRBox().getObjectTaxonomy();
    			case DATA_PROPERTY_HIERARCHY:
    				kb.getRBox().getDataTaxonomy();
    			default:
    				break;
    		}
        }
    }
}
