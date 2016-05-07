package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
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

public class PelletReasoner implements OWLReasoner, OWLOntologyChangeListener
{

	public static final Logger log = Logger.getLogger(PelletReasoner.class.getName());

	private static final Set<InferenceType> PRECOMPUTABLE_INFERENCES = EnumSet.of(InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);

	private static final Version VERSION = createVersion();

	private static Version createVersion()
	{
		final String versionString = VersionInfo.getInstance().getVersionString();
		final String[] versionNumbers = versionString.split("\\.");

		final int major = parseNumberIfExists(versionNumbers, 0);
		final int minor = parseNumberIfExists(versionNumbers, 1);
		final int patch = parseNumberIfExists(versionNumbers, 2);
		final int build = parseNumberIfExists(versionNumbers, 3);

		return new Version(major, minor, patch, build);
	}

	private static int parseNumberIfExists(final String[] numbers, final int index)
	{
		try
		{
			if (0 <= index && index < numbers.length)
				return Integer.parseInt(numbers[index]);
		}
		catch (final NumberFormatException e)
		{
			log.log(Level.FINE, "Invalid number in version identifier: " + numbers[index], e);
		}

		return 0;
	}

	private class ChangeVisitor implements OWLOntologyChangeVisitor
	{

		private boolean reloadRequired;

		public boolean isReloadRequired()
		{
			return reloadRequired;
		}

		/**
		 * Process a change, providing a single call for common reset,accept,isReloadRequired pattern.
		 *
		 * @param change the {@link OWLOntologyChange} to process
		 * @return <code>true</code> if change is handled, <code>false</code> if a reload is required
		 */
		public boolean process(final OWLOntologyChange change)
		{
			this.reset();
			change.accept(this);
			return !isReloadRequired();
		}

		public void reset()
		{
			visitor.reset();
			reloadRequired = false;
		}

		@Override
		public void visit(final AddAxiom change)
		{
			visitor.setAddAxiom(true);
			change.getAxiom().accept(visitor);
			reloadRequired = visitor.isReloadRequired();
		}

		@Override
		public void visit(final RemoveAxiom change)
		{
			visitor.setAddAxiom(false);
			change.getAxiom().accept(visitor);
			reloadRequired = visitor.isReloadRequired();
		}

		@Override
		public void visit(final AddImport change)
		{
			reloadRequired = true;
		}

		@Override
		public void visit(final AddOntologyAnnotation change)
		{

		}

		@Override
		public void visit(final RemoveImport change)
		{
			reloadRequired = true;
		}

		@Override
		public void visit(final RemoveOntologyAnnotation change)
		{

		}

		@Override
		public void visit(final SetOntologyID change)
		{

		}

	}

	private class ClassMapper extends EntityMapper<OWLClass>
	{
		@Override
		public OWLClass map(final ATermAppl term)
		{
			if (term.equals(ATermUtils.TOP))
				return factory.getOWLThing();
			else
				if (term.equals(ATermUtils.BOTTOM))
					return factory.getOWLNothing();
				else
					return factory.getOWLClass(iri(term));
		}
	}

	private class DataPropertyMapper extends EntityMapper<OWLDataProperty>
	{
		@Override
		public OWLDataProperty map(final ATermAppl term)
		{
			if (ATermUtils.TOP_DATA_PROPERTY.equals(term))
				return factory.getOWLTopDataProperty();
			if (ATermUtils.BOTTOM_DATA_PROPERTY.equals(term))
				return factory.getOWLBottomDataProperty();
			return factory.getOWLDataProperty(iri(term));
		}
	}

	private class DatatypeMapper extends EntityMapper<OWLDatatype>
	{
		@Override
		public OWLDatatype map(final ATermAppl term)
		{
			return factory.getOWLDatatype(iri(term));
		}
	}

	private abstract class EntityMapper<T extends OWLObject>
	{

		public abstract T map(ATermAppl term);

		final public Set<T> map(final Collection<ATermAppl> terms)
		{
			final Set<T> mappedSet = new HashSet<>();
			for (final ATermAppl term : terms)
			{
				final T mapped = map(term);
				if (mapped != null)
					mappedSet.add(mapped);
			}
			return mappedSet;
		}
	}

	private class LiteralMapper extends EntityMapper<OWLLiteral>
	{
		@Override
		public OWLLiteral map(final ATermAppl term)
		{
			final String lexValue = ((ATermAppl) term.getArgument(0)).getName();
			final ATermAppl lang = (ATermAppl) term.getArgument(1);
			final ATermAppl dtype = (ATermAppl) term.getArgument(2);
			if (dtype.equals(ATermUtils.PLAIN_LITERAL_DATATYPE))
			{
				if (lang.equals(ATermUtils.EMPTY))
					return factory.getOWLLiteral(lexValue);
				else
					return factory.getOWLLiteral(lexValue, lang.toString());
			}
			else
			{
				final OWLDatatype datatype = DT_MAPPER.map(dtype);
				return factory.getOWLLiteral(lexValue, datatype);
			}
		}
	}

	private class NamedIndividualMapper extends EntityMapper<OWLNamedIndividual>
	{
		@Override
		public OWLNamedIndividual map(final ATermAppl term)
		{
			if (ATermUtils.isBnode(term))
				return null;
			else
				return factory.getOWLNamedIndividual(iri(term));
		}
	}

	private class ObjectPropertyMapper extends EntityMapper<OWLObjectPropertyExpression>
	{
		@Override
		public OWLObjectPropertyExpression map(final ATermAppl term)
		{
			if (ATermUtils.TOP_OBJECT_PROPERTY.equals(term))
				return factory.getOWLTopObjectProperty();
			if (ATermUtils.BOTTOM_OBJECT_PROPERTY.equals(term))
				return factory.getOWLBottomObjectProperty();
			if (ATermUtils.isInv(term))
				return OP_MAPPER.map(term).getInverseProperty();
			return factory.getOWLObjectProperty(iri(term));
		}
	}

	private static IRI iri(final ATermAppl term)
	{
		if (term.getArity() != 0)
			throw new OWLRuntimeException("Trying to convert an anonymous term " + term);

		return IRI.create(term.getName());
	}

	private final OWLDataFactory factory;
	private KnowledgeBase kb;
	private final OWLOntologyManager manager;
	private final ReasonerProgressMonitor monitor;
	/**
	 * Main ontology for reasoner
	 */
	private final OWLOntology ontology;
	/**
	 * Imports closure for ontology
	 */
	private Set<OWLOntology> importsClosure;
	private boolean shouldRefresh;
	private final PelletVisitor visitor;

	private final BufferingMode bufferingMode;
	private final List<OWLOntologyChange> pendingChanges;

	private final IndividualNodeSetPolicy individualNodeSetPolicy;

	private final ChangeVisitor changeVisitor = new ChangeVisitor();

	private final EntityMapper<OWLNamedIndividual> IND_MAPPER = new NamedIndividualMapper();

	private final EntityMapper<OWLLiteral> LIT_MAPPER = new LiteralMapper();

	private final EntityMapper<OWLObjectPropertyExpression> OP_MAPPER = new ObjectPropertyMapper();

	private final EntityMapper<OWLDataProperty> DP_MAPPER = new DataPropertyMapper();

	private final EntityMapper<OWLDatatype> DT_MAPPER = new DatatypeMapper();

	private final EntityMapper<OWLClass> CLASS_MAPPER = new ClassMapper();

	public PelletReasoner(final OWLOntology ontology, final BufferingMode bufferingMode)
	{
		this(ontology, new SimpleConfiguration(new NullReasonerProgressMonitor(), org.mindswap.pellet.PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING ? FreshEntityPolicy.ALLOW : FreshEntityPolicy.DISALLOW, 0, IndividualNodeSetPolicy.BY_SAME_AS), bufferingMode);
	}

	/**
	 * Create a reasoner for the given ontology and configuration.
	 *
	 * @param ontology
	 */
	public PelletReasoner(final OWLOntology ontology, final OWLReasonerConfiguration config, final BufferingMode bufferingMode) throws IllegalConfigurationException
	{

		individualNodeSetPolicy = config.getIndividualNodeSetPolicy();

		if (!getFreshEntityPolicy().equals(config.getFreshEntityPolicy()))
			throw new IllegalConfigurationException("PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING conflicts with reasoner configuration", config);

		this.ontology = ontology;
		monitor = config.getProgressMonitor();

		kb = new KnowledgeBase();
		kb.setTaxonomyBuilderProgressMonitor(new ProgressAdapter(monitor));
		if (config.getTimeOut() > 0)
			kb.timers.mainTimer.setTimeout(config.getTimeOut());

		this.manager = ontology.getOWLOntologyManager();
		this.factory = manager.getOWLDataFactory();
		this.visitor = new PelletVisitor(kb);

		this.bufferingMode = bufferingMode;

		manager.addOntologyChangeListener(this);

		this.shouldRefresh = true;
		this.pendingChanges = new ArrayList<>();

		refresh();
	}

	private PelletRuntimeException convert(final PelletRuntimeException e) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException, FreshEntitiesException
	{

		if (e instanceof org.mindswap.pellet.exceptions.TimeoutException)
			throw new TimeOutException();

		if (e instanceof org.mindswap.pellet.exceptions.TimerInterruptedException)
			throw new ReasonerInterruptedException(e);

		if (e instanceof org.mindswap.pellet.exceptions.InconsistentOntologyException)
			throw new InconsistentOntologyException();

		if (e instanceof org.mindswap.pellet.exceptions.UndefinedEntityException)
		{
			final Set<OWLEntity> unknown = Collections.emptySet();
			throw new FreshEntitiesException(unknown);
		}

		return e;
	}

	@Override
	public void dispose()
	{
		kb = null;
		manager.removeOntologyChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush()
	{
		processChanges(pendingChanges);
		pendingChanges.clear();
		refreshCheck();
	}

	@Override
	public Node<OWLClass> getBottomClassNode()
	{
		return getUnsatisfiableClasses();
	}

	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode()
	{
		refreshCheck();
		return toDataPropertyNode(kb.getAllEquivalentProperties(ATermUtils.BOTTOM_DATA_PROPERTY));
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode()
	{
		refreshCheck();
		return toObjectPropertyNode(kb.getAllEquivalentProperties(ATermUtils.BOTTOM_OBJECT_PROPERTY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferingMode getBufferingMode()
	{
		return bufferingMode;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();

		try
		{
			final ATermAppl some = ATermUtils.makeSomeValues(term(pe), ATermUtils.TOP_LIT);

			final Set<ATermAppl> equivalents = kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty())
				return toClassNodeSet(Collections.singleton(equivalents));

			final Set<Set<ATermAppl>> result = kb.getSuperClasses(some, direct);
			if (!equivalents.isEmpty())
				result.add(equivalents);

			return toClassNodeSet(result);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public Set<OWLLiteral> getDataPropertyValues(final OWLNamedIndividual ind, final OWLDataProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toLiteralSet(kb.getDataPropertyValues(term(pe), term(ind)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	public Set<OWLLiteral> getAnnotationPropertyValues(final OWLNamedIndividual ind, final OWLAnnotationProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toLiteralSet(kb.getPropertyValues(term(pe), term(ind)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(final OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return getIndividualNodeSet(kb.getDifferents(term(ind)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLClass> getDisjointClasses(final OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{

		refreshCheck();
		try
		{
			final Set<Set<ATermAppl>> disjoints = kb.getDisjointClasses(term(ce));
			return toClassNodeSet(disjoints);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(final OWLDataPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Node<OWLDataProperty>> values = new HashSet<>();
			for (final Set<ATermAppl> val : kb.getDisjointProperties(term(pe)))
				values.add(toDataPropertyNode(val));

			return new OWLDataPropertyNodeSet(values);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Node<OWLObjectPropertyExpression>> values = new HashSet<>();
			for (final Set<ATermAppl> val : kb.getDisjointProperties(term(pe)))
				values.add(toObjectPropertyNode(val));

			return new OWLObjectPropertyNodeSet(values);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(final OWLClassExpression ce) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toClassNode(kb.getAllEquivalentClasses(term(ce)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(final OWLDataProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toDataPropertyNode(kb.getAllEquivalentProperties(term(pe)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toObjectPropertyNode(kb.getAllEquivalentProperties(term(pe)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy()
	{
		return individualNodeSetPolicy;
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetBySameAs(final Collection<ATermAppl> individuals)
	{
		final Set<Node<OWLNamedIndividual>> instances = new HashSet<>();
		final Set<ATermAppl> seen = new HashSet<>();
		for (final ATermAppl ind : individuals)
			if (!seen.contains(ind))
			{
				final Set<ATermAppl> equiv = kb.getAllSames(ind);
				instances.add(toIndividualNode(equiv));
				seen.addAll(equiv);
			}

		return new OWLNamedIndividualNodeSet(instances);
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetByName(final Collection<ATermAppl> individuals)
	{
		final Set<Node<OWLNamedIndividual>> instances = new HashSet<>();

		for (final ATermAppl ind : individuals)
			for (final ATermAppl equiv : kb.getAllSames(ind))
				instances.add(toIndividualNode(equiv));

		return new OWLNamedIndividualNodeSet(instances);
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSet(final Collection<ATermAppl> individuals)
	{
		if (IndividualNodeSetPolicy.BY_NAME.equals(individualNodeSetPolicy))
			return getIndividualNodeSetByName(individuals);
		else
			if (IndividualNodeSetPolicy.BY_SAME_AS.equals(individualNodeSetPolicy))
				return getIndividualNodeSetBySameAs(individuals);
			else
				throw new AssertionError("Unsupported IndividualNodeSetPolicy : " + individualNodeSetPolicy);
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(final OWLClassExpression ce, final boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return getIndividualNodeSet(kb.getInstances(term(ce), direct));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toObjectPropertyNode(kb.getInverses(term(pe)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	/**
	 * Return the underlying Pellet knowledge base.
	 *
	 * @return the underlying Pellet knowledge base
	 */
	public KnowledgeBase getKB()
	{
		return kb;
	}

	public OWLOntologyManager getManager()
	{
		return manager;
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final ATermAppl some = ATermUtils.makeSomeValues(term(pe), ATermUtils.TOP);

			final Set<ATermAppl> equivalents = kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty())
				return toClassNodeSet(Collections.singleton(equivalents));

			final Set<Set<ATermAppl>> result = kb.getSuperClasses(some, direct);
			if (!equivalents.isEmpty())
				result.add(equivalents);

			return toClassNodeSet(result);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final ATermAppl some = ATermUtils.makeSomeValues(ATermUtils.makeInv(term(pe)), ATermUtils.TOP);

			final Set<ATermAppl> equivalents = kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty())
				return toClassNodeSet(Collections.singleton(equivalents));

			final Set<Set<ATermAppl>> result = kb.getSuperClasses(some, direct);
			if (!equivalents.isEmpty())
				result.add(equivalents);

			return toClassNodeSet(result);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(final OWLNamedIndividual ind, final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return getIndividualNodeSet(kb.getObjectPropertyValues(term(pe), term(ind)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions()
	{
		return Collections.emptySet();
	}

	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals()
	{
		return Collections.emptySet();
	}

	@Override
	public List<OWLOntologyChange> getPendingChanges()
	{
		return pendingChanges;
	}

	@Override
	public String getReasonerName()
	{
		return PelletReasonerFactory.getInstance().getReasonerName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Version getReasonerVersion()
	{
		return VERSION;
	}

	@Override
	public OWLOntology getRootOntology()
	{
		return ontology;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(final OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toIndividualNode(kb.getAllSames(term(ind)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(final OWLClassExpression ce, final boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Set<ATermAppl>> result = kb.getSubClasses(term(ce), direct);
			return toClassNodeSet(result);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Node<OWLDataProperty>> values = new HashSet<>();
			for (final Set<ATermAppl> val : kb.getSubProperties(term(pe), direct))
				values.add(toDataPropertyNode(val));
			return new OWLDataPropertyNodeSet(values);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Node<OWLObjectPropertyExpression>> values = new HashSet<>();
			for (final Set<ATermAppl> val : kb.getSubProperties(term(pe), direct))
				values.add(toObjectPropertyNode(val));
			return new OWLObjectPropertyNodeSet(values);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLClass> getSuperClasses(final OWLClassExpression ce, final boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Set<ATermAppl>> result = kb.getSuperClasses(term(ce), direct);
			return toClassNodeSet(result);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Node<OWLDataProperty>> values = new HashSet<>();
			for (final Set<ATermAppl> val : kb.getSuperProperties(term(pe), direct))
				values.add(toDataPropertyNode(val));
			return new OWLDataPropertyNodeSet(values);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Node<OWLObjectPropertyExpression>> values = new HashSet<>();
			for (final Set<ATermAppl> val : kb.getSuperProperties(term(pe), direct))
				values.add(toObjectPropertyNode(val));
			return new OWLObjectPropertyNodeSet(values);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public long getTimeOut()
	{
		return kb.timers.mainTimer.getTimeout();
	}

	@Override
	public Node<OWLClass> getTopClassNode()
	{
		refreshCheck();
		return toClassNode(kb.getAllEquivalentClasses(ATermUtils.TOP));
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode()
	{
		refreshCheck();
		return toDataPropertyNode(kb.getAllEquivalentProperties(ATermUtils.TOP_DATA_PROPERTY));
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode()
	{
		refreshCheck();
		return toObjectPropertyNode(kb.getAllEquivalentProperties(ATermUtils.TOP_OBJECT_PROPERTY));
	}

	@Override
	public NodeSet<OWLClass> getTypes(final OWLNamedIndividual ind, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Set<ATermAppl>> result = kb.getTypes(term(ind), direct);
			return toClassNodeSet(result);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy()
	{
		return org.mindswap.pellet.PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING ? FreshEntityPolicy.ALLOW : FreshEntityPolicy.DISALLOW;
	}

	@Override
	public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toClassNode(kb.getAllUnsatisfiableClasses());
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public void interrupt()
	{
		kb.timers.interrupt();
	}

	@Override
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return kb.isConsistent();
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public boolean isEntailed(final OWLAxiom axiom) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, FreshEntitiesException, InconsistentOntologyException
	{
		refreshCheck();
		try
		{
			return isEntailed(Collections.singleton(axiom));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public boolean isEntailed(final Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, FreshEntitiesException, InconsistentOntologyException
	{
		refreshCheck();
		try
		{
			final EntailmentChecker entailmentChecker = new EntailmentChecker(this);
			return entailmentChecker.isEntailed(axioms);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public boolean isEntailmentCheckingSupported(final AxiomType<?> axiomType)
	{
		return !EntailmentChecker.UNSUPPORTED_ENTAILMENT.contains(axiomType);
	}

	@Override
	public boolean isSatisfiable(final OWLClassExpression classExpression) throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException, FreshEntitiesException, InconsistentOntologyException
	{
		refreshCheck();
		try
		{
			return kb.isSatisfiable(term(classExpression));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)// throws OWLException
	{
		switch (bufferingMode)
		{
			case BUFFERING:
				pendingChanges.addAll(changes);
				break;
			case NON_BUFFERING:
				processChanges(changes);
				break;
			default:
				throw new AssertionError("Unexpected buffering mode: " + bufferingMode);
		}
	}

	/**
	 * Process all the given changes in an incremental fashion. Processing will _stop if a change cannot be handled incrementally and requires a reload. The
	 * reload will not be done as part of processing.
	 *
	 * @param changes the changes to be applied to the reasoner
	 * @return <code>true</code> if all changes have been processed successfully, <code>false</code> otherwise (indicates reasoner will reload the whole
	 *         ontology next time it needs to do reasoning)
	 */
	public boolean processChanges(final List<? extends OWLOntologyChange> changes)
	{
		if (shouldRefresh)
			return false;

		for (final OWLOntologyChange change : new ArrayList<>(changes)) // avoid ConcurrentModificationException that is too mush common.
		{

			if (log.isLoggable(Level.FINER))
				log.fine("Changed: " + change + " in " + change.getOntology());

			if (!importsClosure.contains(change.getOntology()))
				continue;

			if (!changeVisitor.process(change))
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Reload required by ontology change " + change);

				shouldRefresh = true;
				break;
			}
		}

		return !shouldRefresh;
	}

	/**
	 * {@inheritDoc}
	 */
	public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		if (kb.isConsistent())
			kb.realize();
	}

	/**
	 * Clears the reasoner and reloads all the axioms in the imports closure.
	 */
	public void refresh()
	{
		visitor.clear();
		kb.clear();

		importsClosure = ontology.getImportsClosure();

		visitor.setAddAxiom(true);
		for (final OWLOntology ont : importsClosure)
			ont.accept(visitor);
		visitor.verify();

		shouldRefresh = false;
	}

	/**
	 * Make sure the reasoner is ready to answer queries. This function does not process changes but if changes processed earlier required a refresh this
	 * funciton will call {@link #refresh()}.
	 */
	private void refreshCheck()
	{
		if (kb == null)
			throw new OWLRuntimeException("Trying to use a disposed reasoner");

		if (shouldRefresh)
			refresh();
	}

	public ATermAppl term(final OWLObject d)
	{
		refreshCheck();

		visitor.reset();
		visitor.setAddAxiom(false);
		d.accept(visitor);

		final ATermAppl a = visitor.result();

		if (a == null)
			throw new InternalReasonerException("Cannot create ATerm from description " + d);

		return a;
	}

	private NodeSet<OWLClass> toClassNodeSet(final Set<Set<ATermAppl>> termSets)
	{
		final Set<Node<OWLClass>> nodes = new HashSet<>();
		for (final Set<ATermAppl> termSet : termSets)
			nodes.add(toClassNode(termSet));
		return new OWLClassNodeSet(nodes);
	}

	private Node<OWLClass> toClassNode(final Set<ATermAppl> terms)
	{
		return NodeFactory.getOWLClassNode(CLASS_MAPPER.map(terms));
	}

	private Node<OWLDataProperty> toDataPropertyNode(final Set<ATermAppl> terms)
	{
		return NodeFactory.getOWLDataPropertyNode(DP_MAPPER.map(terms));
	}

	private Node<OWLNamedIndividual> toIndividualNode(final Set<ATermAppl> terms)
	{
		return NodeFactory.getOWLNamedIndividualNode(IND_MAPPER.map(terms));
	}

	private Set<OWLLiteral> toLiteralSet(final Collection<ATermAppl> terms)
	{
		return LIT_MAPPER.map(terms);
	}

	private Node<OWLObjectPropertyExpression> toObjectPropertyNode(final Set<ATermAppl> terms)
	{
		return NodeFactory.getOWLObjectPropertyNode(OP_MAPPER.map(terms));
	}

	private Node<OWLNamedIndividual> toIndividualNode(final ATermAppl term)
	{
		return NodeFactory.getOWLNamedIndividualNode(IND_MAPPER.map(term));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes()
	{
		return PRECOMPUTABLE_INFERENCES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPrecomputed(final InferenceType inferenceType)
	{
		switch (inferenceType)
		{
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
	@Override
	public void precomputeInferences(final InferenceType... inferenceTypes) throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException
	{
		for (final InferenceType inferenceType : inferenceTypes)
			switch (inferenceType)
			{
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
