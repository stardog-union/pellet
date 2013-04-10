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

package org.mindswap.pellet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.utils.AnnotationClasses;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.mindswap.pellet.utils.progress.SilentProgressMonitor;
import org.mindswap.pellet.utils.progress.SwingProgressMonitor;

/**
 * This class contains options used throughout different modules of the
 * reasoner. Setting one of the values should have effect in the behavior of the
 * reasoner regardless of whether it is based on Jena or OWL-API (though some
 * options are applicable only in one implementation). Some of these options are
 * to control experimental extensions to the reasoner and may be removed in
 * future releases as these features are completely tested and integrated.
 * 
 * @author Evren Sirin
 */
public class PelletOptions {
	public final static Logger	log	= Logger.getLogger( PelletOptions.class.getName() );

	private interface EnumFactory<T> {
		public T create();
	}

	public enum MonitorType implements EnumFactory<ProgressMonitor> {

		CONSOLE(ConsoleProgressMonitor.class), SWING(SwingProgressMonitor.class),
		NONE(SilentProgressMonitor.class);

		private final Class<? extends ProgressMonitor>	c;

		private MonitorType(Class<? extends ProgressMonitor> c) {
			this.c = c;
		}

		public ProgressMonitor create() {
			try {
				return c.newInstance();
			} catch( InstantiationException e ) {
				throw new InternalReasonerException( e );
			} catch( IllegalAccessException e ) {
				throw new InternalReasonerException( e );
			}
		}
	}

	public enum InstanceRetrievalMethod {
		BINARY, LINEAR, TRACING_BASED
	}

	/**
	 * @see #UNDEFINED_DATATYPE_HANDLING
	 */
	public enum UndefinedDatatypeHandling {
		EMPTY, EXCEPTION, INFINITE_STRING
	}
	
	public enum OrderedClassification {
		DISABLED, ENABLED, ENABLED_LEGACY_ORDERING
	}

	/**
	 * Sets the Pellet configuration options and returns the old values for the
	 * options.
	 * 
	 * @param newOptions
	 *            the new values for configuration options
	 */
	public static Properties setOptions(Properties newOptions) {
		Properties oldOptions = new Properties();

		ALWAYS_REBUILD_RETE = getBooleanProperty( newOptions, "ALWAYS_REBUILD_RETE",
				ALWAYS_REBUILD_RETE, oldOptions );

		CACHE_RETRIEVAL = getBooleanProperty( newOptions, "CACHE_RETRIEVAL", CACHE_RETRIEVAL,
				oldOptions );

		DL_SAFE_RULES = getBooleanProperty( newOptions, "DL_SAFE_RULES", DL_SAFE_RULES, oldOptions );

		FREEZE_BUILTIN_NAMESPACES = getBooleanProperty( newOptions, "FREEZE_BUILTIN_NAMESPACES",
				FREEZE_BUILTIN_NAMESPACES, oldOptions );

		FULL_SIZE_ESTIMATE = getBooleanProperty( newOptions, "FULL_SIZE_ESTIMATE",
				FULL_SIZE_ESTIMATE, oldOptions );

		IGNORE_DEPRECATED_TERMS = getBooleanProperty( newOptions, "IGNORE_DEPRECATED_TERMS",
				IGNORE_DEPRECATED_TERMS, oldOptions );

		IGNORE_INVERSES = getBooleanProperty( newOptions, "IGNORE_INVERSES", IGNORE_INVERSES,
				oldOptions );

		IGNORE_UNSUPPORTED_AXIOMS = getBooleanProperty( newOptions, "IGNORE_UNSUPPORTED_AXIOMS",
				IGNORE_UNSUPPORTED_AXIOMS, oldOptions );

		MAINTAIN_COMPLETION_QUEUE = getBooleanProperty( newOptions, "MAINTAIN_COMPLETION_QUEUE",
				MAINTAIN_COMPLETION_QUEUE, oldOptions );

		MAX_ANONYMOUS_CACHE = getIntProperty( newOptions, "MAX_ANONYMOUS_CACHE",
				MAX_ANONYMOUS_CACHE, oldOptions );

		OPTIMIZE_DOWN_MONOTONIC = getBooleanProperty( newOptions, "OPTIMIZE_DOWN_MONOTONIC",
				OPTIMIZE_DOWN_MONOTONIC, oldOptions );

		REALIZE_INDIVIDUAL_AT_A_TIME = getBooleanProperty( newOptions,
				"REALIZE_INDIVIDUAL_AT_A_TIME", REALIZE_INDIVIDUAL_AT_A_TIME, oldOptions );

		RETURN_DEDUCTIONS_GRAPH = getBooleanProperty( newOptions, "RETURN_DEDUCTIONS_GRAPH",
				RETURN_DEDUCTIONS_GRAPH, oldOptions );

		SAMPLING_RATIO = getDoubleProperty( newOptions, "SAMPLING_RATIO", SAMPLING_RATIO,
				oldOptions );

		SILENT_UNDEFINED_ENTITY_HANDLING = getBooleanProperty( newOptions,
				"SILENT_UNDEFINED_ENTITY_HANDLING", SILENT_UNDEFINED_ENTITY_HANDLING, oldOptions );

		SIMPLIFY_QUERY = getBooleanProperty( newOptions, "SIMPLIFY_QUERY", SIMPLIFY_QUERY,
				oldOptions );

		STATIC_REORDERING_LIMIT = getIntProperty( newOptions, "STATIC_REORDERING_LIMIT",
				STATIC_REORDERING_LIMIT, oldOptions );

		TRACK_BRANCH_EFFECTS = getBooleanProperty( newOptions, "TRACK_BRANCH_EFFECTS",
				TRACK_BRANCH_EFFECTS, oldOptions );

		TREAT_ALL_VARS_DISTINGUISHED = getBooleanProperty( newOptions,
				"TREAT_ALL_VARS_DISTINGUISHED", TREAT_ALL_VARS_DISTINGUISHED, oldOptions );

		USE_ABSORPTION = getBooleanProperty( newOptions, "USE_ABSORPTION", USE_ABSORPTION,
				oldOptions );
		
		USE_NOMINAL_ABSORPTION = getBooleanProperty( newOptions, "USE_NOMINAL_ABSORPTION", USE_NOMINAL_ABSORPTION,
				oldOptions );
		
		USE_HASVALUE_ABSORPTION = getBooleanProperty( newOptions, "USE_HASVALUE_ABSORPTION", USE_HASVALUE_ABSORPTION,
				oldOptions );

		USE_ROLE_ABSORPTION = getBooleanProperty( newOptions, "USE_ROLE_ABSORPTION",
				USE_ROLE_ABSORPTION, oldOptions );
		
		USE_RULE_ABSORPTION = getBooleanProperty( newOptions, "USE_RULE_ABSORPTION", USE_RULE_ABSORPTION,
				oldOptions );
		
		USE_ADVANCED_CACHING = getBooleanProperty( newOptions, "USE_ADVANCED_CACHING",
				USE_ADVANCED_CACHING, oldOptions );

		USE_ANNOTATION_SUPPORT = getBooleanProperty( newOptions, "USE_ANNOTATION_SUPPORT",
				USE_ANNOTATION_SUPPORT, oldOptions );

		USE_BACKJUMPING = getBooleanProperty( newOptions, "USE_BACKJUMPING", USE_BACKJUMPING,
				oldOptions );

		USE_CACHING = getBooleanProperty( newOptions, "USE_CACHING", USE_CACHING, oldOptions );

		USE_CD_CLASSIFICATION = getBooleanProperty( newOptions, "USE_CD_CLASSIFICATION",
				USE_CD_CLASSIFICATION, oldOptions );

		USE_CLASSIFICATION_MONITOR = getEnumProperty( newOptions, "USE_CLASSIFICATION_MONITOR",
				USE_CLASSIFICATION_MONITOR, oldOptions );

		USE_COMPLETION_QUEUE = getBooleanProperty( newOptions, "USE_COMPLETION_QUEUE",
				USE_COMPLETION_QUEUE, oldOptions );

		USE_CONTINUOUS_RULES = getBooleanProperty( newOptions, "USE_CONTINUOUS_RULES",
				USE_CONTINUOUS_RULES, oldOptions );

		USE_FULL_DATATYPE_REASONING = getBooleanProperty( newOptions,
				"USE_FULL_DATATYPE_REASONING", USE_FULL_DATATYPE_REASONING, oldOptions );

		USE_INCREMENTAL_CONSISTENCY = getBooleanProperty( newOptions,
				"USE_INCREMENTAL_CONSISTENCY", USE_INCREMENTAL_CONSISTENCY, oldOptions );

		USE_INCREMENTAL_DELETION = getBooleanProperty( newOptions, "USE_INCREMENTAL_DELETION",
				USE_INCREMENTAL_DELETION, oldOptions );

		USE_NAIVE_QUERY_ENGINE = getBooleanProperty( newOptions, "USE_NAIVE_QUERY_ENGINE",
				USE_NAIVE_QUERY_ENGINE, oldOptions );

		USE_PSEUDO_NOMINALS = getBooleanProperty( newOptions, "USE_PSEUDO_NOMINALS",
				USE_PSEUDO_NOMINALS, oldOptions );

		USE_SEMANTIC_BRANCHING = getBooleanProperty( newOptions, "USE_SEMANTIC_BRANCHING",
				USE_SEMANTIC_BRANCHING, oldOptions );
		
		USE_SMART_RESTORE = getBooleanProperty( newOptions, "USE_SMART_RESTORE", USE_SMART_RESTORE, oldOptions);

		USE_TRACING = getBooleanProperty( newOptions, "USE_TRACING", USE_TRACING, oldOptions );
		
		USE_UNIQUE_NAME_ASSUMPTION = getBooleanProperty( newOptions, "USE_UNIQUE_NAME_ASSUMPTION",
				USE_UNIQUE_NAME_ASSUMPTION, oldOptions );
		
		HIDE_TOP_PROPERTY_VALUES = getBooleanProperty( newOptions, "HIDE_TOP_PROPERTY_VALUES",
				HIDE_TOP_PROPERTY_VALUES, oldOptions );

		ORDERED_CLASSIFICATION = getEnumProperty( newOptions, "ORDERED_CLASSIFICATION",
				ORDERED_CLASSIFICATION, oldOptions );
		
		DISABLE_EL_CLASSIFIER = getBooleanProperty( newOptions, "DISABLE_EL_CLASSIFIER", DISABLE_EL_CLASSIFIER, 
				oldOptions );
		
		PROCESS_JENA_UPDATES_INCREMENTALLY = getBooleanProperty(newOptions, "PROCESS_JENA_UPDATES_INCREMENTALLY",
		                PROCESS_JENA_UPDATES_INCREMENTALLY, oldOptions);
		
		IGNORE_ANNOTATION_CLASSES = getBooleanProperty(newOptions, "IGNORE_ANNOTATION_CLASSES",
		                IGNORE_ANNOTATION_CLASSES, oldOptions);
		
		return oldOptions;
	}

	public static void load(URL configFile) throws FileNotFoundException, IOException {
		log.fine( "Reading Pellet configuration file " + configFile );

		Properties properties = new Properties();
		properties.load( configFile.openStream() );
		setOptions( properties );
	}
	

	private static boolean getBooleanProperty(Properties properties, String property,
			boolean defaultValue, Properties defaultValues) {
		defaultValues.setProperty( property, String.valueOf( defaultValue ) );
		String value = properties.getProperty( property );
		boolean returnValue = defaultValue;

		if( value != null ) {
			value = value.trim();
			if( value.equalsIgnoreCase( "true" ) )
				returnValue = true;
			else if( value.equalsIgnoreCase( "false" ) )
				returnValue = false;
			else
				log.severe( "Ignoring invalid value (" + value + ") for the configuration option "
						+ property );
		}

		properties.setProperty( property, String.valueOf( returnValue ) );

		return returnValue;
	}

	private static double getDoubleProperty(Properties properties, String property,
			double defaultValue, Properties defaultValues) {
		defaultValues.setProperty( property, String.valueOf( defaultValue ) );
		String value = properties.getProperty( property );
		double doubleValue = defaultValue;

		if( value != null ) {
			try {
				doubleValue = Double.parseDouble( value );
			} catch( NumberFormatException e ) {
				log.severe( "Ignoring invalid double value (" + value
						+ ") for the configuration option " + property );
			}
		}

		properties.setProperty( property, String.valueOf( doubleValue ) );

		return doubleValue;
	}

	private static int getIntProperty(Properties properties, String property, int defaultValue,
			Properties defaultValues) {
		defaultValues.setProperty( property, String.valueOf( defaultValue ) );
		String value = properties.getProperty( property );
		int intValue = defaultValue;

		if( value != null ) {
			try {
				intValue = Integer.parseInt( value );
			} catch( NumberFormatException e ) {
				log.severe( "Ignoring invalid int value (" + value
						+ ") for the configuration option " + property );
			}
		}

		properties.setProperty( property, String.valueOf( intValue ) );

		return intValue;
	}

	private static <T extends Enum<T>> T getEnumProperty(Properties properties, String property,
			T defaultValue, Properties defaultValues) {
		defaultValues.setProperty( property, String.valueOf( defaultValue ) );
		String value = properties.getProperty( property );
		T returnValue = defaultValue;

		if( value != null ) {
			value = value.trim().toUpperCase();
			try {
				returnValue = Enum.valueOf( defaultValue.getDeclaringClass(), value );
			} catch( IllegalArgumentException e ) {
				log.severe( "Ignoring invalid value (" + value + ") for the configuration option "
						+ property );
			}
		}

		properties.setProperty( property, String.valueOf( returnValue ) );

		return returnValue;
	}

	/**
	 * When this option is set completion will go on even if a clash is detected
	 * until the completion graph is saturated. Turning this option has very
	 * severe performance effect and right now is only used for experimental
	 * purposes to generate explanations.
	 * <p>
	 * <b>*********** DO NOT CHANGE THE VALUE OF THIS OPTION **************</b>
	 */
	public static boolean								SATURATE_TABLEAU						= false;

	/**
	 * This option tells Pellet to treat every individual with a distinct URI to
	 * be different from each other. This is against the semantics of OWL but is
	 * much more efficient than adding an <code><owl:AllDifferent></code>
	 * definition with all the individuals. This option does not affect b-nodes,
	 * they can still be inferred to be same.
	 */
	public static boolean								USE_UNIQUE_NAME_ASSUMPTION				= false;

	/**
	 * Track the association betweens nodes changed and branch. Reduces the work
	 * done during restoration (and during some incremental reasoning changes),
	 * at the cost of memory overhead necessary for tracking.
	 */
	public static boolean								TRACK_BRANCH_EFFECTS					= false;

	/**
	 * According to SPARQL semantics all variables are distinguished by
	 * definition and bnodes in the query are non-distinguished variables. This
	 * option overrides the default behavior and treats bnodes as distinguished
	 * variables, too. This means bnodes in the SPARQL query will only be
	 * matched to named individuals or existing bnodes in the dataset but not to
	 * inferred individuals (e.g. an individual whose existence is inferred due
	 * to an <code>owl:someValuesFrom</code> restriction)
	 */
	public static boolean								TREAT_ALL_VARS_DISTINGUISHED			= true;

	/**
	 * Sort the disjuncts based on the statistics
	 */
	public static boolean								USE_DISJUNCT_SORTING					= true && !SATURATE_TABLEAU;

	public static MonitorType							USE_CLASSIFICATION_MONITOR				= MonitorType.CONSOLE;

	public static final String							NO_SORTING								= "NO";
	public static final String							OLDEST_FIRST							= "OLDEST_FIRST";
	public static String								USE_DISJUNCTION_SORTING					= OLDEST_FIRST;

	/**
	 * TBox absorption will be used to move some of the General Inclusion Axioms
	 * (GCI) from Tg to Tu.
	 */
	public static boolean								USE_ABSORPTION							= true;

	/**
	 * If <code>EXCEPTION</code> an exception is thrown when the reasoner
	 * encounters an undefined (and unsupported) datatype. If <code>EMPTY</code>
	 * it is treated as an empty datatype and has no valid lexical forms. If
	 * <code>INFINITE</code> it is treated as an infinite datatype and all
	 * lexical forms are valid and in which equality and identity is based on
	 * lexical form.
	 */
	public static UndefinedDatatypeHandling				UNDEFINED_DATATYPE_HANDLING				= UndefinedDatatypeHandling.INFINITE_STRING;

	/**
	 * Absorb TBox axioms into domain/range restrictions in RBox
	 */
	public static boolean								USE_ROLE_ABSORPTION						= true;

	/**
	 * Absorb TBox axioms about nominals into ABox assertions
	 */
	public static boolean								USE_NOMINAL_ABSORPTION					= true;

	public static boolean								USE_HASVALUE_ABSORPTION					= true;
	
	public static boolean								USE_RULE_ABSORPTION						= false;
	
	public static boolean								USE_BINARY_ABSORPTION					= true;

	/**
	 * Use dependency directed backjumping
	 */
	public static boolean								USE_BACKJUMPING							= !SATURATE_TABLEAU & true;

	/**
	 * Check the cardinality restrictions on datatype properties and handle
	 * inverse functional datatype properties
	 */
	public static boolean								USE_FULL_DATATYPE_REASONING				= true;

	/**
	 * Cache the pseudo models for named classes and individuals.
	 */
	public static boolean								USE_CACHING								= true;

	/**
	 * Cache the pseudo models for anonymous classes. Used inside
	 * EmptySHNStrategy to prevent the expansion of completion graph nodes whose
	 * satisfiability status is already cached.
	 */
	public static boolean								USE_ADVANCED_CACHING					= true;

	/**
	 * Cache the pseudo models for anonymous classes when inverses are present. 
	 * This can improve reasoning performance but will introduce memory overhead.
	 */
	public static boolean								USE_INVERSE_CACHING						= true;
	
	public static boolean								USE_ANYWHERE_BLOCKING					= true;
	
	/**
	 * The maximum number of cached pseudo models for anonymous classes. The
	 * named concepts (and their negations) are always cached regardless of this
	 * limit. This setting is mostly relevant for SHN ontologies as
	 * {@link #USE_ADVANCED_CACHING} option. If the cache reaches the maximum
	 * number of entries for anonymous classes the subsequent additions will be
	 * handled specially (default behavior is to remove the Least Recently Used
	 * (LRU) element from cache). Setting this value too high will increase the
	 * memory requirements and setting it too low will slow down the reasoning
	 * process.
	 */
	public static int									MAX_ANONYMOUS_CACHE						= 20000;

	/**
	 * To decide if individual <code>i</code> has type class <code>c</code>
	 * check if the edges from cached model of <code>c</code> to nominal nodes
	 * also exists for the cached model of <code>i</code>.
	 */
	public static boolean								CHECK_NOMINAL_EDGES						= true;

	/**
	 * Treat nominals (classes defined by enumeration) as named atomic concepts
	 * rather than individual names. Turning this option improves the
	 * performance but soundness and completeness cannot be established.
	 */
	public static boolean								USE_PSEUDO_NOMINALS						= false;

	/**
	 * This option is mainly used for debugging and causes the reasoner to
	 * ignore all inverse properties including inverseOf,
	 * InverseFunctionalProperty and SymmetricProperty definitions.
	 */
	public static boolean								IGNORE_INVERSES							= false;

	/**
	 * Dynamically find the best completion strategy for the KB. If disabled
	 * SROIQ strategy will be used for all the ontologies.
	 */
	public static boolean								USE_COMPLETION_STRATEGY					= !SATURATE_TABLEAU & true;

	/**
	 * Use continuous rete execution for applying rules.
	 */
	public static boolean								USE_CONTINUOUS_RULES					= true;

	/**
	 * Always rebuild rete (no incremental update)
	 */
	public static boolean								ALWAYS_REBUILD_RETE						= false;

	/**
	 * Use semantic branching, i.e. add the negation of a disjunct when the next
	 * branch is being tried
	 */
	public static boolean								USE_SEMANTIC_BRANCHING					= !SATURATE_TABLEAU & true;

	/**
	 * The default strategy used for ABox completion. If this values is set,
	 * this strategy will be used for all the KB's regardless of the
	 * expressivity.
	 * <p>
	 * <b>*********** DO NOT CHANGE THE VALUE OF THIS OPTION **************</b>
	 */
	@Deprecated
	public static Class<? extends CompletionStrategy>	DEFAULT_COMPLETION_STRATEGY				= null;

	/**
	 * When doing a satisfiability check for a concept, do not copy the
	 * individuals even if there are nominals in the KB until you hit a nominal
	 * rule application.
	 */
	public static boolean								COPY_ON_WRITE							= true;

	/**
	 * Control the behavior if a function such as kb.getInstances(),
	 * kb.getTypes(), kb.getPropertyValues() is called with a parameter that is
	 * an undefined class, property or individual. If this option is set to
	 * false then an exception is thrown each time this occurs, if true set the
	 * corresponding function returns a false value (or an empty set where
	 * appropriate).
	 */
	public static boolean								SILENT_UNDEFINED_ENTITY_HANDLING		= true;

	/**
	 * Control the realization strategy where we loop over individuals or
	 * concepts. When this flag is set we loop over each individual and find the
	 * most specific type for that individual by traversing the class hierarchy.
	 * If this flag is not set we traverse the class hierarchy and for each
	 * concept find the instances. Then any individual that is also an instance
	 * of a subclass is removed. Both techniques have advantages and
	 * disadvantages. Best performance depends on the ontology characteristics.
	 */
	public static boolean								REALIZE_INDIVIDUAL_AT_A_TIME			= false;

	/**
	 * Validate ABox structure during completion (Should be used only for
	 * debugging purposes).
	 */
	public static boolean								VALIDATE_ABOX							= false;

	/**
	 * Print completion graph after each iteration (Should be used only for
	 * debugging purposes).
	 */
	public static boolean								PRINT_ABOX								= false;

	public static final boolean							DEPTH_FIRST								= true;
	public static final boolean							BREADTH_FIRST							= false;

	/**
	 * Keep ABox assertions in the KB so they can be accessed later. Currently
	 * not used by the reasoner but could be useful for outside applications.
	 */
	public static boolean								KEEP_ABOX_ASSERTIONS					= false;

	public static boolean								SEARCH_TYPE								= DEPTH_FIRST;

	public static InstanceRetrievalMethod				INSTANCE_RETRIEVAL						= InstanceRetrievalMethod.BINARY;

	/**
	 * If <code>true</code> invalid literals cause inconsistencies. If
	 * <code>false</code> they cause exceptions to be generated.
	 */
	public static boolean								INVALID_LITERAL_AS_INCONSISTENCY		= true;

	/**
	 * When this option is set the query engine for distinguished variables uses
	 * taxonomies to prune downmonotonic variables in subClassOf and
	 * subPropertyOf atoms.
	 */
	public static boolean								OPTIMIZE_DOWN_MONOTONIC					= false;

	/**
	 * Remove query atoms that are trivially entailed by other atoms. For
	 * example, the query <blockquote>
	 * <code>query(x, y) :- Person(x), worksAt(x, y), Organization(y)</code>
	 * </blockquote> can be simplified to <blockquote>
	 * <code>query(x, y) :- worksAt(x, y)</code> </blockquote> if the domain
	 * of <code>worksAt</code> is <code>Person</code> and the range is
	 * <code>Organization</code>.
	 */
	public static boolean								SIMPLIFY_QUERY							= true;

	/**
	 * The ratio of individuals that will be inspected while generating the size
	 * estimate. The query reordering optimization uses size estimates for
	 * classes and properties to estimate the cost of a certain query ordering.
	 * The size estimates are computed by random sampling. Increasing the
	 * sampling ratio yields more accurate results but is very costly for large
	 * ABoxes.
	 */
	public static double								SAMPLING_RATIO							= 0.2;

	/**
	 * The number of query atoms in a query that will trigger the switch from
	 * static query reordering to dynamic (incremental) query reordering.
	 */
	public static int									STATIC_REORDERING_LIMIT					= 8;

	/**
	 * This option controls if the size estimates for all the classes and
	 * properties in a KB will be computed fully when the PelletQueryExecution
	 * object is created.
	 */
	public static boolean								FULL_SIZE_ESTIMATE						= false;

	public static boolean								CACHE_RETRIEVAL							= false;

	public static boolean								USE_TRACING								= false;

	public static String								DEFAULT_CONFIGURATION_FILE				= "pellet.properties";

	/**
	 * With this option all triples that contains an unrecognized term from RDF,
	 * RDF-S, OWL, OWL 1.1, or XSD namespaces will be ignored.
	 */
	public static boolean								FREEZE_BUILTIN_NAMESPACES				= true;

	/**
	 * This option causes all classes and properties defined as deprecated
	 * (using <code>owl:DeprecetedClass</code> or
	 * <code>owl:DeprecetedProperty</code>) to be ignored. If turned off,
	 * these will be treated as ordinary classes and properties. Note that, even
	 * if this option is turned on deprecated entities used in ordinary axioms
	 * will be added to the KB.
	 */
	public static boolean								IGNORE_DEPRECATED_TERMS					= true;

	/**
	 * This option controls the behavior of Pellet while an ontology is being
	 * loaded. Some axioms, e.g. cardinality restrictions on transitive
	 * properties, is not supported by Pellet. If an axiom is used in the input
	 * ontology Pellet can just ignore that axiom (and print a warning) or
	 * simply throw an exception at the time that axiom is added to the KB.
	 * Default behavior is to ignore unsupported axioms.
	 */
	public static boolean								IGNORE_UNSUPPORTED_AXIOMS				= true;

	/**
	 * This option tells the reasoner to enable support for DL-safe rules
	 * (encoded in SWRL). If the value is set to ture then the rules will be
	 * taken into account during reasoning. Otherwise, rules will simply be
	 * ignored by the reasoner. Note that, some SWRL features such as
	 * DatavaluedPropertyAtom and BuiltinAtom is not supported. The behavior for
	 * what happens when rules containing such atoms is controlled by the
	 * {@link #IGNORE_UNSUPPORTED_AXIOMS} option, e.g. such rules can be ignored
	 * or reasoner can throw an exception.
	 */
	public static boolean								DL_SAFE_RULES							= true;

	/**
	 * This option controls the behavior of
	 * <code>PelletInfGraph.getDeductionsGraph()</code> function affecting in
	 * turn how <code>InfModel.getDeductionsGraph()</code> behave. Jena
	 * documentation describes this function to apply only to forward-chaining
	 * rule engines which does not include pellet. The behavior of this function
	 * in Pellet prior to 1.5.1 release was to return an incomplete subset of
	 * all the inferences that the reasoner can compute from the base model.
	 * This is obviously not correct and Pellet should simply return
	 * <code>null</code> for this function according to the Jena
	 * documentation. But considering that the incorrect behavior of this
	 * function might already be being used by the users, Pellet 1.5.1
	 * introduces this option for backward compatibility and forces
	 * PelletInfGraph return to the previous incorrect behavior.
	 * 
	 * @deprecated This option is introduced as a temporary solution for
	 *             backward compatibility and is scheduled to be removed in
	 *             future releases. One should avoid using
	 *             <code>InfGraph.getDeductionsGraph()</code> (similarly
	 *             <code>InfModel.getDeductionsModel()</code>) with Pellet.
	 *             Model.listStatements() can be used to retrieve all the
	 *             asserted and inferred statements.
	 */
	public static boolean								RETURN_DEDUCTIONS_GRAPH					= false;

	/**
	 * Flag set if the completion queue should be utilized. This optimization
	 * will introduce memory overhead but will (in some cases) dramatically
	 * reduce reasoning time. Rather than iterating over all individuals during
	 * the completion strategy, only those which need to have the rules fired
	 * are selected for rule applications.
	 */
	public static boolean								USE_COMPLETION_QUEUE					= false;

	/**
	 * Flag set if the optimized basic completion queue should be used. The
	 * difference between the basic completion queue is that it maintains queues
	 * of individuals for each rule type. In contrast the basic completion queue
	 * simply one list of individuals which all rules iterate over
	 */
	public static boolean								USE_OPTIMIZED_BASIC_COMPLETION_QUEUE	= false && USE_COMPLETION_QUEUE;

	/**
	 * During backjumping use dependency set information to restore node labels
	 * rather than restoring the label exactly to the previous state.
	 */
	public static boolean								USE_SMART_RESTORE						= true;

	/**
	 * Flag set if incremental consistency checking should be used. Currently it
	 * can only be used on KBs with SHIQ or SHOQ expressivity
	 */
	public static boolean								USE_INCREMENTAL_CONSISTENCY				= false && USE_COMPLETION_QUEUE;

	/**
	 * Flag set if incremental support for deletions should be used. Currently
	 * it can only be used on KBs with SHIQ or SHOQ expressivity. This flag is
	 * used as incremental deletions introduces memory overhead, which may not
	 * be suitable for some KBs
	 */
	public static boolean								USE_INCREMENTAL_DELETION				= false
																										&& USE_INCREMENTAL_CONSISTENCY
																										&& USE_TRACING;

	/**
	 * Flag if the completion queue should be maintained through incremental
	 * deletions. It can be the case that a removal of a syntactic assertion
	 * will require a queue element to be removed, as it is no longer
	 * applicable. If this is set to false then a simple check before each rule
	 * is fired will be performed - if the ds for the label is null, then the
	 * rule will not be fired. If this is set to true and tracing is on, then
	 * the queue will be maintained through deletions. TODO: Note currently the
	 * queue maintenance is not implemented, so this should always be FALSE!
	 * <p>
	 * <b>*********** DO NOT CHANGE THE VALUE OF THIS OPTION **************</b>
	 */
	public static boolean								MAINTAIN_COMPLETION_QUEUE				= false
																										&& USE_TRACING
																										&& USE_COMPLETION_QUEUE;

	/**
	 * Use (if applicable) special optimization for completely defined (CD)
	 * concepts during classification.
	 */
	public static boolean								USE_CD_CLASSIFICATION					= true;

	@Deprecated
	public static boolean								USE_NAIVE_QUERY_ENGINE					= false;

	/**
	 * Activate annotation support in Pellet. If this variable is true, Ontology
	 * annotations are stored to the KB and they can be retrieved, either by
	 * querying the KB or using the query engines.
	 */
	public static boolean								USE_ANNOTATION_SUPPORT					= false;

	/**
	 * Do not include owl:topObjectProperty and owl:topDataProperty values when
	 * retrieving the property values for an individual. Even tough such values
	 * are trivially inferred according to OWL 2 semantics, including these
	 * values in results increase result size drastically. This option will not
	 * affect boolean queries (asking whether a towl:topObjectProperty b is
	 * entailed will still return true) or property queries (sub and super
	 * property queries will include top properties).
	 */
	public static boolean								HIDE_TOP_PROPERTY_VALUES				= true;
	
	public static boolean								USE_LEGACY_TBOX							= true;

	/**
	 * Classifier orders classes based on their usage to guarantee the
	 * correctness of the classification results. In certain cases, there might
	 * be no semantic reason to choose between two classes. In these cases, the
	 * reasoner can use a deterministic algorithm to choose which of the classes
	 * to process first. This option ensures that the reasoner will use same
	 * ordering at every run. Disabling this option means the classifier will
	 * break ties randomly which might result in different classification times.
	 */
	public static OrderedClassification					ORDERED_CLASSIFICATION					= OrderedClassification.ENABLED;
	
	/**
	 * Do not use EL Classifier, even if the ontology is EL
	 */
	public static boolean 								DISABLE_EL_CLASSIFIER					= false;
	
	
	public static boolean 								PROCESS_JENA_UPDATES_INCREMENTALLY		= true;

	/**
	 * In some ontologies, such as the ones from OBO, annotations may be nested and contain type assertions on
	 * annotation values. Such type assertions will be treated as regular assertions and processed by the reasoner.
	 * This causes many superfluous logical axioms to be considered by the reasoner which might have significant
	 * impact on performance. This options tells Pellet to ignore such annotations. The set of URIs that will be
	 * treated as annotation classes are defined in {@link AnnotationClasses} and has to be programmatically
	 * modified. Annotation classes from OBO are included by default.   
	 */
	public static boolean 								IGNORE_ANNOTATION_CLASSES				= true;
	
	static {
		String configFile = System.getProperty( "pellet.configuration" );

		URL url = null;

		// if the user has not specified the pellet.configuration
		// property, we search for the file "pellet.properties"
		if( configFile == null ) {
			url = PelletOptions.class.getClassLoader().getResource( DEFAULT_CONFIGURATION_FILE );
		}
		else {
			try {
				url = new URL( configFile );
			} catch( MalformedURLException ex ) {
				ex.printStackTrace();

				// so, resource is not a URL:
				// attempt to get the resource from the class path
				url = PelletOptions.class.getClassLoader().getResource( configFile );
			}

			if( url == null )
				log.severe( "Cannot file Pellet configuration file " + configFile );
		}

		if( url != null ) {
			try {
				load( url );	
			} catch( FileNotFoundException e ) {
				log.severe( "Pellet configuration file cannot be found" );
			} catch( IOException e ) {
				log.severe( "I/O error while reading Pellet configuration file" );
			}
		}
	}
}
