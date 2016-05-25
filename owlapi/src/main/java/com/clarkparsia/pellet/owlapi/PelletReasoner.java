package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
import com.intrinsec.owlapi.facet.FacetFactoryOWL;
import com.intrinsec.owlapi.facet.FacetManagerOWL;
import com.intrinsec.owlapi.facet.FacetOntologyOWL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.katk.tools.Log;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.exceptions.PelletRuntimeException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.VersionInfo;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
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

public class PelletReasoner implements OWLReasoner, OWLOntologyChangeListener, FacetManagerOWL, FacetOntologyOWL, FacetFactoryOWL
{

	public static final Logger _logger = Log.getLogger(PelletReasoner.class);

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
			_logger.log(Level.FINE, "Invalid number in version identifier: " + numbers[index], e);
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
			_visitor.reset();
			reloadRequired = false;
		}

		@Override
		public void visit(final AddAxiom change)
		{
			_visitor.setAddAxiom(true);
			change.getAxiom().accept(_visitor);
			reloadRequired = _visitor.isReloadRequired();
		}

		@Override
		public void visit(final RemoveAxiom change)
		{
			_visitor.setAddAxiom(false);
			change.getAxiom().accept(_visitor);
			reloadRequired = _visitor.isReloadRequired();
		}

		@Override
		public void visit(final AddImport change)
		{
			reloadRequired = true;
		}

		@Override
		public void visit(final RemoveImport change)
		{
			reloadRequired = true;
		}
	}

	private class ClassMapper extends EntityMapper<OWLClass>
	{
		@Override
		public OWLClass map(final ATermAppl term)
		{
			if (term.equals(ATermUtils.TOP))
				return _factory.getOWLThing();
			else
				if (term.equals(ATermUtils.BOTTOM))
					return _factory.getOWLNothing();
				else
					return _factory.getOWLClass(iri(term));
		}
	}

	private class DataPropertyMapper extends EntityMapper<OWLDataProperty>
	{
		@Override
		public OWLDataProperty map(final ATermAppl term)
		{
			if (ATermUtils.TOP_DATA_PROPERTY.equals(term))
				return _factory.getOWLTopDataProperty();
			if (ATermUtils.BOTTOM_DATA_PROPERTY.equals(term))
				return _factory.getOWLBottomDataProperty();
			return _factory.getOWLDataProperty(iri(term));
		}
	}

	private class DatatypeMapper extends EntityMapper<OWLDatatype>
	{
		@Override
		public OWLDatatype map(final ATermAppl term)
		{
			return _factory.getOWLDatatype(iri(term));
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
					return _factory.getOWLLiteral(lexValue);
				else
					return _factory.getOWLLiteral(lexValue, lang.toString());
			}
			else
			{
				final OWLDatatype datatype = DT_MAPPER.map(dtype);
				return _factory.getOWLLiteral(lexValue, datatype);
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
				return _factory.getOWLNamedIndividual(iri(term));
		}
	}

	private class ObjectPropertyMapper extends EntityMapper<OWLObjectPropertyExpression>
	{
		@Override
		public OWLObjectPropertyExpression map(final ATermAppl term)
		{
			if (ATermUtils.TOP_OBJECT_PROPERTY.equals(term))
				return _factory.getOWLTopObjectProperty();
			if (ATermUtils.BOTTOM_OBJECT_PROPERTY.equals(term))
				return _factory.getOWLBottomObjectProperty();
			if (ATermUtils.isInv(term))
				return OP_MAPPER.map(term).getInverseProperty();
			return _factory.getOWLObjectProperty(iri(term));
		}
	}

	private static IRI iri(final ATermAppl term)
	{
		if (term.getArity() != 0)
			throw new OWLRuntimeException("Trying to convert an anonymous term " + term);

		return IRI.create(term.getName());
	}

	private final OWLOntologyManager _manager;

	@Override
	public OWLOntologyManager getManager()
	{
		return _manager;
	}

	private final OWLDataFactory _factory;

	@Override
	public OWLDataFactory getFactory()
	{
		return _factory;
	}

	/**
	 * Main _ontology for reasoner
	 */
	private final OWLOntology _ontology;

	@Override
	public OWLOntology getOntology()
	{
		return _ontology;
	}

	private KnowledgeBase _kb;

	/**
	 * Return the underlying Pellet knowledge base.
	 *
	 * @return the underlying Pellet knowledge base
	 */
	public KnowledgeBase getKB()
	{
		return _kb;
	}

	private final ReasonerProgressMonitor _monitor;

	/**
	 * Imports closure for _ontology
	 */
	private Set<OWLOntology> _importsClosure;
	private boolean _shouldRefresh;
	private final PelletVisitor _visitor;

	private final BufferingMode _bufferingMode;
	private final List<OWLOntologyChange> _pendingChanges;

	private final IndividualNodeSetPolicy _individualNodeSetPolicy;

	private final ChangeVisitor _changeVisitor = new ChangeVisitor();

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
	 * Create a reasoner for the given _ontology and configuration.
	 *
	 * @param _ontology
	 */
	public PelletReasoner(final OWLOntology ontology, final OWLReasonerConfiguration config, final BufferingMode bufferingMode) throws IllegalConfigurationException
	{

		_individualNodeSetPolicy = config.getIndividualNodeSetPolicy();

		if (!getFreshEntityPolicy().equals(config.getFreshEntityPolicy()))
			throw new IllegalConfigurationException("PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING conflicts with reasoner configuration", config);

		_ontology = ontology;
		_monitor = config.getProgressMonitor();

		_kb = new KnowledgeBase();
		_kb.setTaxonomyBuilderProgressMonitor(new ProgressAdapter(_monitor));
		if (config.getTimeOut() > 0)
			_kb.timers.mainTimer.setTimeout(config.getTimeOut());

		_manager = ontology.getOWLOntologyManager();
		_factory = _manager.getOWLDataFactory();
		_visitor = new PelletVisitor(_kb);

		_bufferingMode = bufferingMode;

		_manager.addOntologyChangeListener(this);

		_shouldRefresh = true;
		_pendingChanges = new ArrayList<>();

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
		_kb = null;
		_manager.removeOntologyChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush()
	{
		processChanges(_pendingChanges);
		_pendingChanges.clear();
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
		return toDataPropertyNode(_kb.getAllEquivalentProperties(ATermUtils.BOTTOM_DATA_PROPERTY));
	}

	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode()
	{
		refreshCheck();
		return toObjectPropertyNode(_kb.getAllEquivalentProperties(ATermUtils.BOTTOM_OBJECT_PROPERTY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferingMode getBufferingMode()
	{
		return _bufferingMode;
	}

	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();

		try
		{
			final ATermAppl some = ATermUtils.makeSomeValues(term(pe), ATermUtils.TOP_LIT);

			final Set<ATermAppl> equivalents = _kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty())
				return toClassNodeSet(Collections.singleton(equivalents));

			final Set<Set<ATermAppl>> result = _kb.getSuperClasses(some, direct);
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
			return toLiteralSet(_kb.getDataPropertyValues(term(pe), term(ind)));
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
			return toLiteralSet(_kb.getPropertyValues(term(pe), term(ind)));
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
			return getIndividualNodeSet(_kb.getDifferents(term(ind)));
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
			final Set<Set<ATermAppl>> disjoints = _kb.getDisjointClasses(term(ce));
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
			for (final Set<ATermAppl> val : _kb.getDisjointProperties(term(pe)))
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
			for (final Set<ATermAppl> val : _kb.getDisjointProperties(term(pe)))
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
			return toClassNode(_kb.getAllEquivalentClasses(term(ce)));
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
			return toDataPropertyNode(_kb.getAllEquivalentProperties(term(pe)));
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
			return toObjectPropertyNode(_kb.getAllEquivalentProperties(term(pe)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy()
	{
		return _individualNodeSetPolicy;
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetBySameAs(final Collection<ATermAppl> individuals)
	{
		final Set<Node<OWLNamedIndividual>> instances = new HashSet<>();
		final Set<ATermAppl> seen = new HashSet<>();
		for (final ATermAppl ind : individuals)
			if (!seen.contains(ind))
			{
				final Set<ATermAppl> equiv = _kb.getAllSames(ind);
				instances.add(toIndividualNode(equiv));
				seen.addAll(equiv);
			}

		return new OWLNamedIndividualNodeSet(instances);
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetByName(final Collection<ATermAppl> individuals)
	{
		final Set<Node<OWLNamedIndividual>> instances = new HashSet<>();

		for (final ATermAppl ind : individuals)
			for (final ATermAppl equiv : _kb.getAllSames(ind))
				instances.add(toIndividualNode(equiv));

		return new OWLNamedIndividualNodeSet(instances);
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSet(final Collection<ATermAppl> individuals)
	{
		if (IndividualNodeSetPolicy.BY_NAME.equals(_individualNodeSetPolicy))
			return getIndividualNodeSetByName(individuals);
		else
			if (IndividualNodeSetPolicy.BY_SAME_AS.equals(_individualNodeSetPolicy))
				return getIndividualNodeSetBySameAs(individuals);
			else
				throw new AssertionError("Unsupported IndividualNodeSetPolicy : " + _individualNodeSetPolicy);
	}

	@Override
	public NodeSet<OWLNamedIndividual> getInstances(final OWLClassExpression ce, final boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return getIndividualNodeSet(_kb.getInstances(term(ce), direct));
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
			return toObjectPropertyNode(_kb.getInverses(term(pe)));
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final ATermAppl some = ATermUtils.makeSomeValues(term(pe), ATermUtils.TOP);

			final Set<ATermAppl> equivalents = _kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty())
				return toClassNodeSet(Collections.singleton(equivalents));

			final Set<Set<ATermAppl>> result = _kb.getSuperClasses(some, direct);
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

			final Set<ATermAppl> equivalents = _kb.getEquivalentClasses(some);
			if (direct && !equivalents.isEmpty())
				return toClassNodeSet(Collections.singleton(equivalents));

			final Set<Set<ATermAppl>> result = _kb.getSuperClasses(some, direct);
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
			return getIndividualNodeSet(_kb.getObjectPropertyValues(term(pe), term(ind)));
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
		return _pendingChanges;
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
		return _ontology;
	}

	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(final OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return toIndividualNode(_kb.getAllSames(term(ind)));
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
			final Set<Set<ATermAppl>> result = _kb.getSubClasses(term(ce), direct);
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
			for (final Set<ATermAppl> val : _kb.getSubProperties(term(pe), direct))
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
			for (final Set<ATermAppl> val : _kb.getSubProperties(term(pe), direct))
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
			final Set<Set<ATermAppl>> result = _kb.getSuperClasses(term(ce), direct);
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
			for (final Set<ATermAppl> val : _kb.getSuperProperties(term(pe), direct))
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
			for (final Set<ATermAppl> val : _kb.getSuperProperties(term(pe), direct))
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
		return _kb.timers.mainTimer.getTimeout();
	}

	@Override
	public Node<OWLClass> getTopClassNode()
	{
		refreshCheck();
		return toClassNode(_kb.getAllEquivalentClasses(ATermUtils.TOP));
	}

	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode()
	{
		refreshCheck();
		return toDataPropertyNode(_kb.getAllEquivalentProperties(ATermUtils.TOP_DATA_PROPERTY));
	}

	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode()
	{
		refreshCheck();
		return toObjectPropertyNode(_kb.getAllEquivalentProperties(ATermUtils.TOP_OBJECT_PROPERTY));
	}

	@Override
	public NodeSet<OWLClass> getTypes(final OWLNamedIndividual ind, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			final Set<Set<ATermAppl>> result = _kb.getTypes(term(ind), direct);
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
			return toClassNode(_kb.getAllUnsatisfiableClasses());
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	@Override
	public void interrupt()
	{
		_kb.timers.interrupt();
	}

	@Override
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		try
		{
			return _kb.isConsistent();
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
			return _kb.isSatisfiable(term(classExpression));
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
		switch (_bufferingMode)
		{
			case BUFFERING:
				_pendingChanges.addAll(changes);
				break;
			case NON_BUFFERING:
				processChanges(changes);
				break;
			default:
				throw new AssertionError("Unexpected buffering mode: " + _bufferingMode);
		}
	}

	/**
	 * Process all the given changes in an incremental fashion. Processing will _stop if a change cannot be handled incrementally and requires a reload. The
	 * reload will not be done as part of processing.
	 *
	 * @param changes the changes to be applied to the reasoner
	 * @return <code>true</code> if all changes have been processed successfully, <code>false</code> otherwise (indicates reasoner will reload the whole
	 *         _ontology next time it needs to do reasoning)
	 */
	public boolean processChanges(final List<? extends OWLOntologyChange> changes)
	{
		if (_shouldRefresh)
			return false;

		for (final OWLOntologyChange change : new ArrayList<>(changes)) // avoid ConcurrentModificationException that is too mush common.
		{

			if (_logger.isLoggable(Level.FINER))
				_logger.fine("Changed: " + change + " in " + change.getOntology());

			if (!_importsClosure.contains(change.getOntology()))
				continue;

			if (!_changeVisitor.process(change))
			{
				if (_logger.isLoggable(Level.FINE))
					_logger.fine("Reload required by _ontology change " + change);

				_shouldRefresh = true;
				break;
			}
		}

		return !_shouldRefresh;
	}

	/**
	 * {@inheritDoc}
	 */
	public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException
	{
		refreshCheck();
		if (_kb.isConsistent())
			_kb.realize();
	}

	/**
	 * Clears the reasoner and reloads all the axioms in the imports closure.
	 */
	public void refresh()
	{
		_visitor.clear();
		_kb.clear();

		_importsClosure = _ontology.importsClosure().collect(Collectors.toSet());

		_visitor.setAddAxiom(true);
		for (final OWLOntology ont : _importsClosure)
			ont.accept(_visitor);
		_visitor.verify();

		_shouldRefresh = false;
	}

	/**
	 * Make sure the reasoner is ready to answer queries. This function does not process changes but if changes processed earlier required a refresh this
	 * funciton will call {@link #refresh()}.
	 */
	private void refreshCheck()
	{
		if (_kb == null)
			throw new OWLRuntimeException("Trying to use a disposed reasoner");

		if (_shouldRefresh)
			refresh();
	}

	public ATermAppl term(final OWLObject d)
	{
		refreshCheck();

		_visitor.reset();
		_visitor.setAddAxiom(false);
		d.accept(_visitor);

		final ATermAppl a = _visitor.result();

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
				return _kb.isClassified();
			case CLASS_ASSERTIONS:
				return _kb.isRealized();
			case OBJECT_PROPERTY_HIERARCHY:
				return _kb.getRBox().isObjectTaxonomyPrepared();
			case DATA_PROPERTY_HIERARCHY:
				return _kb.getRBox().isDataTaxonomyPrepared();
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
					_kb.classify();
					//$FALL-THROUGH$
				case CLASS_ASSERTIONS:
					_kb.realize();
					//$FALL-THROUGH$
				case OBJECT_PROPERTY_HIERARCHY:
					_kb.getRBox().getObjectTaxonomy();
					//$FALL-THROUGH$
				case DATA_PROPERTY_HIERARCHY:
					_kb.getRBox().getDataTaxonomy();
					//$FALL-THROUGH$
				default:
					break;
			}
	}

}
