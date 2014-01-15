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

import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.and;
import static com.clarkparsia.pellet.utils.TermFactory.some;
import static java.lang.String.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.PelletOptions.InstanceRetrievalMethod;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.mindswap.pellet.exceptions.UndefinedEntityException;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.EmptySRIQStrategy;
import org.mindswap.pellet.tableau.completion.SROIQStrategy;
import org.mindswap.pellet.tableau.completion.incremental.DependencyIndex;
import org.mindswap.pellet.tableau.completion.incremental.IncrementalRestore;
import org.mindswap.pellet.taxonomy.CDOptimizedTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyBuilder;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.taxonomy.printer.ClassTreePrinter;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.tbox.TBoxFactory;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.AnnotationClasses;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.SizeEstimate;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;
import org.mindswap.pellet.utils.progress.ProgressMonitor;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermFactory;
import aterm.ATermList;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.el.SimplifiedELClassifier;
import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.expressivity.ExpressivityChecker;
import com.clarkparsia.pellet.rules.ContinuousRulesStrategy;
import com.clarkparsia.pellet.rules.RuleStrategy;
import com.clarkparsia.pellet.rules.UsableRuleFilter;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.utils.CollectionUtils;
import com.clarkparsia.pellet.utils.MultiMapUtils;

/**
 * @author Evren Sirin
 */
public class KnowledgeBase {
	public final static Logger								log		= Logger
																			.getLogger( KnowledgeBase.class
																					.getName() );

	// This field is to ensure memory profiler will first process ATermFactory
	// which makes it easier to analyze the results
	@SuppressWarnings("unused")
	private ATermFactory									factory	= ATermUtils.getFactory();

	protected ABox											abox;
	protected TBox											tbox;
	protected RBox											rbox;

	private Set<ATermAppl>									individuals;

	protected TaxonomyBuilder								builder;
	private ProgressMonitor									builderProgressMonitor;

	private boolean											consistent;

	private SizeEstimate									estimate;
	
	private boolean											explainOnlyInconsistency = false;

	private Map<ATermAppl, Map<ATermAppl, Set<ATermAppl>>>	annotations;

	/**
	 * The state of KB w.r.t. reasoning. The state is not valid if KB is
	 * changed, i.e. !changes.isEmpty(). These states are added in the order
	 * CONSISTENCY < CLASSIFY < REALIZE when the corresponding functions are
	 * called. If KB is modified after classification, calling prepare might
	 * remove CONSISTENCY but leave CLASSIFY.
	 */
	protected enum ReasoningState {
		CONSISTENCY, CLASSIFY, REALIZE
	}

	protected EnumSet<ReasoningState>		state	= EnumSet.noneOf( ReasoningState.class );

	private Map<ATermAppl, Set<ATermAppl>>	instances;

	private ExpressivityChecker				expChecker;

	/**
	 * Timers used in various different parts of KB. There may be many different
	 * timers created here depending on the level of debugging or application
	 * requirements. However, there are three major timers that are guaranteed
	 * to exist.
	 * <ul>
	 * <li> <b>main</b> - This is the main timer that exists in any Timers
	 * objects. All the other timers defined in here will have this timer as its
	 * dependant so setting a timeout on this timer will put a limit on every
	 * operation done inside KB.</li>
	 * <li> <b>preprocessing</b> - This is the operation where TBox creation,
	 * absorbtion and normalization is done. It also includes computing
	 * hierarchy of properties in RBox and merging the individuals in ABox if
	 * there are explicit sameAs assertions.</li>
	 * <li> <b>consistency</b> - This is the timer for ABox consistency check.
	 * Putting a timeout will mean that any single consistency check should be
	 * completed in a certain amount of time.</li>
	 * </ul>
	 */
	public Timers							timers	= new Timers();

	/**
	 * Rules added to this KB. The key is the asserted rule, 
	 */
	private Map<Rule,Rule>					rules;

	// !!!!THE FOLLOWING ARE USED FOR INCREMENTAL REASONING!!!!
	// Structure for tracking which assertions are deleted
	private Set<ATermAppl>					deletedAssertions;

	// Index used for abox deletions
	private DependencyIndex					dependencyIndex;

	// set of syntactic assertions
	private Set<ATermAppl>					syntacticAssertions;

	public enum AssertionType {
		TYPE, OBJ_ROLE, DATA_ROLE
	}

	protected MultiValueMap<AssertionType, ATermAppl>	aboxAssertions;

	public enum ChangeType {
		ABOX_ADD, ABOX_DEL, TBOX_ADD, TBOX_DEL, RBOX_ADD, RBOX_DEL
	}

	protected EnumSet<ChangeType>	changes;

	protected boolean				canUseIncConsistency;

	FullyDefinedClassVisitor		fullyDefinedVisitor	= new FullyDefinedClassVisitor();
	DatatypeVisitor					datatypeVisitor		= new DatatypeVisitor();

	class DatatypeVisitor extends ATermBaseVisitor {

		private boolean	isDatatype	= false;

		public boolean isDatatype(ATermAppl term) {
			isDatatype = false;
			visit( term );

			return isDatatype;
		}

		@Override
		public void visit(ATermAppl term) {
			super.visit( term );
		}

		public void visitOr(ATermAppl term) {
			visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitValue(ATermAppl term) {
			ATermAppl nominal = (ATermAppl) term.getArgument( 0 );

			if( ATermUtils.isLiteral( nominal ) )
				isDatatype = true;
		}

		public void visitTerm(ATermAppl term) {
			if( getDatatypeReasoner().isDeclared( term ) )
				isDatatype = true;
		}

		public void visitNot(ATermAppl term) {
			this.visit( (ATermAppl) term.getArgument( 0 ) );
		}

		public void visitAll(ATermAppl term) {
		}

		public void visitAnd(ATermAppl term) {
			visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitCard(ATermAppl term) {
		}

		public void visitHasValue(ATermAppl term) {
		}

		public void visitLiteral(ATermAppl term) {
		}

		public void visitMax(ATermAppl term) {
		}

		public void visitMin(ATermAppl term) {
		}

		public void visitOneOf(ATermAppl term) {
			visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitSelf(ATermAppl term) {
		}

		public void visitSome(ATermAppl term) {
		}

		public void visitInverse(ATermAppl term) {
		}

		public void visitRestrictedDatatype(ATermAppl dt) {
			isDatatype( (ATermAppl) dt.getArgument( 0 ) );
		}
	}

	class FullyDefinedClassVisitor extends ATermBaseVisitor {

		private boolean	fullyDefined	= true;

		public boolean isFullyDefined(ATermAppl term) {
			fullyDefined = true;
			visit( term );
			return fullyDefined;
		}

		private void visitQCR(ATermAppl term) {
			visitRestr( term );
			if( fullyDefined ) {
				ATermAppl q = (ATermAppl) term.getArgument( 2 );
				if( !isDatatype( q ) )
					this.visit( q );
			}
		}

		private void visitQR(ATermAppl term) {
			visitRestr( term );
			if( fullyDefined ) {
				ATermAppl q = (ATermAppl) term.getArgument( 1 );
				if( !isDatatype( q ) )
					this.visit( q );
			}
		}

		private void visitRestr(ATermAppl term) {
			fullyDefined = fullyDefined && isProperty( term.getArgument( 0 ) );
		}

		@Override
		public void visit(ATermAppl term) {
			if( term.equals( ATermUtils.TOP ) || term.equals( ATermUtils.BOTTOM )
					|| term.equals( ATermUtils.TOP_LIT ) || term.equals( ATermUtils.BOTTOM_LIT ) )
				return;

			super.visit( term );
		}

		public void visitAll(ATermAppl term) {
			visitQR( term );
		}

		public void visitAnd(ATermAppl term) {
			if( fullyDefined )
				visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitCard(ATermAppl term) {
			visitQCR( term );
		}

		public void visitHasValue(ATermAppl term) {
			visitQR( term );
		}

		public void visitLiteral(ATermAppl term) {
			return;
		}

		public void visitMax(ATermAppl term) {
			visitQCR( term );
		}

		public void visitMin(ATermAppl term) {
			visitQCR( term );
		}

		public void visitNot(ATermAppl term) {
			this.visit( (ATermAppl) term.getArgument( 0 ) );
		}

		public void visitOneOf(ATermAppl term) {
			if( fullyDefined )
				visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitOr(ATermAppl term) {
			if( fullyDefined )
				visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitSelf(ATermAppl term) {
			visitRestr( term );
		}

		public void visitSome(ATermAppl term) {
			visitQR( term );
		}

		public void visitTerm(ATermAppl term) {
			fullyDefined = fullyDefined && tbox.getClasses().contains( term );
			if( !fullyDefined )
				return;
		}

		public void visitValue(ATermAppl term) {
			ATermAppl nominal = (ATermAppl) term.getArgument( 0 );
			if( ATermUtils.isLiteral( nominal ) )
				fullyDefined = false;
			else if( !ATermUtils.isLiteral( nominal ) )
				fullyDefined = fullyDefined && individuals.contains( nominal );
		}

		public void visitInverse(ATermAppl term) {
			ATermAppl p = (ATermAppl) term.getArgument( 0 );
			if( ATermUtils.isPrimitive( p ) )
				fullyDefined = fullyDefined && isProperty( p );
			else
				visitInverse( p );
		}

		/**
		 * {@inheritDoc}
		 */
		public void visitRestrictedDatatype(ATermAppl dt) {
			fullyDefined = fullyDefined && isDatatype( (ATermAppl) dt.getArgument( 0 ) );
		}

	}

	/**
	 * 
	 */
	public KnowledgeBase() {
		clear();

		timers.createTimer( "preprocessing" );
		timers.createTimer( "consistency" );
		timers.createTimer( "complete" );
		state = EnumSet.noneOf( ReasoningState.class );

		if( PelletOptions.USE_INCREMENTAL_DELETION ) {
			deletedAssertions = new HashSet<ATermAppl>();
			dependencyIndex = new DependencyIndex( this );
			syntacticAssertions = new HashSet<ATermAppl>();
		}

		aboxAssertions = new MultiValueMap<AssertionType, ATermAppl>();

		annotations = new HashMap<ATermAppl, Map<ATermAppl, Set<ATermAppl>>>();
	}

	/**
	 * Create a KB based on an existing one. New KB has a copy of the ABox but
	 * TBox and RBox is shared between two.
	 * 
	 * @param kb
	 */
	protected KnowledgeBase(KnowledgeBase kb, boolean emptyABox) {
		tbox = kb.tbox;
		rbox = kb.rbox;
		rules = kb.rules;

		aboxAssertions = new MultiValueMap<AssertionType, ATermAppl>();
		
		annotations = kb.annotations;

		expChecker = new ExpressivityChecker( this, kb.getExpressivity() );

		changes = kb.changes.clone();

		if( PelletOptions.USE_INCREMENTAL_DELETION ) {
			deletedAssertions = new HashSet<ATermAppl>();
			dependencyIndex = new DependencyIndex( this );
			syntacticAssertions = new HashSet<ATermAppl>();
		}

		if( emptyABox ) {
			abox = new ABox( this );

			individuals = new HashSet<ATermAppl>();
			instances = new HashMap<ATermAppl, Set<ATermAppl>>();

			// even though we don't copy the individuals over to the new KB
			// we should still create individuals for the
			for( ATermAppl nominal : kb.getExpressivity().getNominals() ) {
				addIndividual( nominal );
			}
		}
		else {
			abox = kb.abox.copy(this);

			if( PelletOptions.KEEP_ABOX_ASSERTIONS ) {
				for( AssertionType assertionType : AssertionType.values() ) {
					Set<ATermAppl> assertions = kb.aboxAssertions.get( assertionType );
					if( !assertions.isEmpty() )
						aboxAssertions.put( assertionType, new HashSet<ATermAppl>( assertions ) );
				}
			}

			individuals = new HashSet<ATermAppl>( kb.individuals );
			instances = new HashMap<ATermAppl, Set<ATermAppl>>( kb.instances );

			// copy deleted assertions
			if( kb.getDeletedAssertions() != null ) {
				deletedAssertions = new HashSet<ATermAppl>( kb.getDeletedAssertions() );
			}

			if( PelletOptions.USE_INCREMENTAL_CONSISTENCY && PelletOptions.USE_INCREMENTAL_DELETION ) {
				// copy the dependency index
				dependencyIndex = new DependencyIndex( this, kb.dependencyIndex );
			}

			// copy syntactic assertions
			if( kb.syntacticAssertions != null ) {
				syntacticAssertions = new HashSet<ATermAppl>( kb.syntacticAssertions );
			}
		}

		if( kb.isConsistencyDone() ) {
			prepare();

			state = EnumSet.of( ReasoningState.CONSISTENCY );
			consistent = kb.consistent;

			abox.setComplete( true );

			estimate = new SizeEstimate( this );
		}
		else {
			state = EnumSet.noneOf( ReasoningState.class );
		}

		timers = kb.timers;
		// timers.createTimer("preprocessing");
		// timers.createTimer("consistency");
	}

	public Expressivity getExpressivity() {
		return getExpressivityChecker().getExpressivity();
	}

	public ExpressivityChecker getExpressivityChecker() {
		// if we can use incremental reasoning then expressivity has been
		// updated as only the ABox was incrementally changed
		if( canUseIncConsistency() )
			return expChecker;

		prepare();

		return expChecker;
	}

	public void clear() {

		if( abox == null ) {
			abox = new ABox( this );
		}
		else {
			boolean doExplanation = abox.doExplanation();
			boolean keepLastCompletion = abox.isKeepLastCompletion();
			abox = new ABox( this );
			abox.setDoExplanation( doExplanation );
			abox.setKeepLastCompletion( keepLastCompletion );
		}

		tbox = TBoxFactory.createTBox( this );
		
		rbox = new RBox();
		
		rules = new HashMap<Rule,Rule>();

		expChecker = new ExpressivityChecker( this );
		individuals = new HashSet<ATermAppl>();

		aboxAssertions = new MultiValueMap<AssertionType, ATermAppl>();

		instances = new HashMap<ATermAppl, Set<ATermAppl>>();
		// typeChecks = new HashMap();

		builder = null;

		state.clear();
		changes = EnumSet.of( ChangeType.ABOX_ADD, ChangeType.TBOX_ADD, ChangeType.RBOX_ADD );
	}

	/**
	 * Create a copy of this KB with a completely new ABox copy but pointing to
	 * the same RBox and TBox.
	 * 
	 * @return A copy of this KB
	 */
	public KnowledgeBase copy() {
		return copy( false );
	}

	/**
	 * Create a copy of this KB. Depending on the value of
	 * <code>emptyABox</code> either a completely new copy of ABox will be
	 * created or the new KB will have an empty ABox. If <code>emptyABox</code>
	 * parameter is true but the original KB contains nominals in its RBox or
	 * TBox the new KB will have the definition of those individuals (but not )
	 * In either case, the new KB will point to the same RBox and TBox so
	 * changing one KB's RBox or TBox will affect other.
	 * 
	 * @param emptyABox
	 *            If <code>true</code> ABox is not copied to the new KB
	 * @return A copy of this KB
	 */
	public KnowledgeBase copy(boolean emptyABox) {
		return new KnowledgeBase( this, emptyABox );
	}

	public void loadKRSS(Reader reader) throws IOException {
		KRSSLoader loader = new KRSSLoader( this );
		loader.parse( reader );
	}

	public void addClass(ATermAppl c) {
		if( c.equals( ATermUtils.TOP ) || ATermUtils.isComplexClass( c ) )
			return;

		boolean added = tbox.addClass( c );

		if( added ) {
			changes.add( ChangeType.TBOX_ADD );

			if( log.isLoggable( Level.FINER ) )
				log.finer( "class " + c );
		}
	}

	public void addSubClass(ATermAppl sub, ATermAppl sup) {
		if( sub.equals( sup ) )
			return;

		changes.add( ChangeType.TBOX_ADD );

		tbox.addAxiom( ATermUtils.makeSub( sub, sup ) );
		
		if( log.isLoggable( Level.FINER ) )
			log.finer( "sub-class " + sub + " " + sup );
	}

	public void addEquivalentClass(ATermAppl c1, ATermAppl c2) {
		if( c1.equals( c2 ) )
			return;

		changes.add( ChangeType.TBOX_ADD );

		tbox.addAxiom( ATermUtils.makeEqClasses( c1, c2 ) );
		
		if( log.isLoggable( Level.FINER ) )
			log.finer( "eq-class " + c1 + " " + c2 );
	}

	public void addKey(ATermAppl c, Set<ATermAppl> properties) {
		int varId = 0;
		Collection<RuleAtom> head = CollectionUtils.makeSet();
		Collection<RuleAtom> body = CollectionUtils.makeSet();

		AtomIVariable x = new AtomIVariable( "x" );
		AtomIVariable y = new AtomIVariable( "y" );

		head.add( new SameIndividualAtom( x, y ) );

		// Process the body
		// First add the property atom pairs for each property
		for( ATermAppl property : properties ) {
			if( isObjectProperty( property ) ) {
				AtomIVariable z = new AtomIVariable( "z" + varId );
				body.add( new IndividualPropertyAtom( property, x, z ) );
				body.add( new IndividualPropertyAtom( property, y, z ) );
			}
			else if( isDatatypeProperty( property ) ) {
				AtomDVariable z = new AtomDVariable( "z" + varId );
				body.add( new DatavaluedPropertyAtom( property, x, z ) );
				body.add( new DatavaluedPropertyAtom( property, y, z ) );
			}

			varId++;
		}

		// Then add the class atoms for the two subject variables
		body.add( new ClassAtom( c, x ) );
		body.add( new ClassAtom( c, y ) );
		
		addRule( new Rule( head, body ) );
	}

	public void addDisjointClasses(ATermList classes) {		
		changes.add( ChangeType.TBOX_ADD );

		tbox.addAxiom( ATermUtils.makeDisjoints( classes ) );
		
		if( log.isLoggable( Level.FINER ) )
			log.finer( "disjoints " + classes );
	}

	public void addDisjointClasses(List<ATermAppl> classes) {
		addDisjointClasses( ATermUtils.toSet( classes ) );
	}

	public void addDisjointClass(ATermAppl c1, ATermAppl c2) {
		changes.add( ChangeType.TBOX_ADD );

		tbox.addAxiom( ATermUtils.makeDisjoint( c1, c2 ) );
		
		if( log.isLoggable( Level.FINER ) )
			log.finer( "disjoint " + c1 + " " + c2 );
	}

	public void addComplementClass(ATermAppl c1, ATermAppl c2) {
		changes.add( ChangeType.TBOX_ADD );
		ATermAppl notC2 = ATermUtils.makeNot( c2 );

		if( c1.equals( notC2 ) )
			return;

		tbox.addAxiom( ATermUtils.makeEqClasses( c1, notC2 ) );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "complement " + c1 + " " + c2 );
	}

	/**
	 * Add the value of a DatatypeProperty.
	 * 
	 * @param p
	 *            Datatype Property
	 * @param ind
	 *            Individual value being added to
	 * @param literalValue
	 *            A literal ATerm which should be constructed with one of
	 *            ATermUtils.makeXXXLiteral functions
	 * @deprecated Use addPropertyValue instead
	 */
	public void addDataPropertyValue(ATermAppl p, ATermAppl s, ATermAppl o) {
		addPropertyValue( p, s, o );
	}

	public Individual addIndividual(ATermAppl i) {
		Node node = abox.getNode( i );
		if( node != null ) {
			if( node instanceof Literal )
				throw new UnsupportedFeatureException(
						"Trying to use a literal as an individual: " + ATermUtils.toString( i ) );

			return (Individual) node;
		}
		else if( ATermUtils.isLiteral( i ) ) {
			throw new UnsupportedFeatureException(
					"Trying to use a literal as an individual: " + ATermUtils.toString( i ) );
		}

		int remember = abox.getBranch();
		abox.setBranch( DependencySet.NO_BRANCH );

		abox.setSyntacticUpdate( true );
		Individual ind = abox.addIndividual( i, DependencySet.INDEPENDENT );
		individuals.add( i );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "individual " + i );

		abox.setSyntacticUpdate( false );

		if( !PelletOptions.USE_PSEUDO_NOMINALS ) {
			// add value(x) for nominal node but do not apply UC yet
			// because it might not be complete. it will be added
			// by CompletionStrategy.initialize()
			ATermAppl nominal = ATermUtils.makeValue( i );
			abox.addType( i, nominal, DependencySet.INDEPENDENT );
		}

		// set addition flag
		changes.add( ChangeType.ABOX_ADD );

		// if we can use inc reasoning then update incremental completion
		// structures
		if( canUseIncConsistency() ) {
			abox.setSyntacticUpdate( true );

			// need to update the branch node count as this is node has been
			// added otherwise during back jumping this node can be removed
			for( int j = 0; j < abox.getBranches().size(); j++ ) {
				// get next branch
				Branch branch = abox.getBranches().get( j );
				branch.setNodeCount( branch.getNodeCount() + 1 );
			}

			// track updated and new individuals; this is needed for the
			// incremental completion strategy
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( i ) );
			abox.getIncrementalChangeTracker().addNewIndividual( abox.getIndividual( i ) );
			abox.setSyntacticUpdate( false );
		}

		abox.setBranch( remember );

		return ind;
	}

	public void addType(ATermAppl i, ATermAppl c) {
		if (AnnotationClasses.contains(c)) {
			return;
		}

		ATermAppl typeAxiom = ATermUtils.makeTypeAtom( i, c );
		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( typeAxiom )
			: DependencySet.INDEPENDENT;

		// add type assertion to syntactic assertions and update dependency
		// index
		if( PelletOptions.USE_INCREMENTAL_DELETION ) {
			syntacticAssertions.add( typeAxiom );
			dependencyIndex.addTypeDependency( i, c, ds );
		}

		if( PelletOptions.KEEP_ABOX_ASSERTIONS )
			aboxAssertions.add( AssertionType.TYPE, typeAxiom );

		addType( i, c, ds );
	}

	public void addType(ATermAppl i, ATermAppl c, DependencySet ds) {
		// set addition flag
		changes.add( ChangeType.ABOX_ADD );

		// if use incremental reasoning then update the cached pseudo model as
		// well
		if( canUseIncConsistency() ) {
			// TODO: refactor the access to the updatedIndividuals and
			// newIndividuals - add get method
			// add this individuals to the affected list - used for inc.
			// consistency checking
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( i ) );
		}

		abox.setSyntacticUpdate( true );
		abox.addType( i, c, ds );
		abox.setSyntacticUpdate( false );

		if( canUseIncConsistency() ) {
			// incrementally update the expressivity of the KB, so that we do
			// not have to reperform if from scratch!
			updateExpressivity( i, c );
		}

		if( log.isLoggable( Level.FINER ) )
			log.finer( "type " + i + " " + c );
	}

	public void addSame(ATermAppl i1, ATermAppl i2) {
		// set addition flag
		changes.add( ChangeType.ABOX_ADD );

		if( canUseIncConsistency() ) {
			// TODO: refactor the access to the updatedIndividuals and
			// newIndividuals - add get method
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( i1 ) );
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( i2 ) );

			// add to pseudomodel - note branch is not set to zero - this is
			// done in SHOIQIncStrategy, prior
			// to merging nodes
			abox.addSame( i1, i2 );
		}

		abox.addSame( i1, i2 );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "same " + i1 + " " + i2 );
	}

	public void addAllDifferent(ATermList list) {
		// set addition flag
		changes.add( ChangeType.ABOX_ADD );

		// if we can use incremental consistency checking then add to
		// pseudomodel
		if( canUseIncConsistency() ) {
			ATermList outer = list;
			// add to updated inds
			while( !outer.isEmpty() ) {
				ATermList inner = outer.getNext();
				while( !inner.isEmpty() ) {
					// TODO: refactor the access to the updatedIndividuals and
					// newIndividuals - add get method
					abox.getIncrementalChangeTracker().addUpdatedIndividual(
							abox.getIndividual( outer.getFirst() ) );
					abox.getIncrementalChangeTracker().addUpdatedIndividual(
							abox.getIndividual( inner.getFirst() ) );
					inner = inner.getNext();
				}
				outer = outer.getNext();
			}

			// add to pseudomodel - note branch must be temporarily set to 0 to
			// ensure that asssertion
			// will not be restored during backtracking
			int branch = abox.getBranch();
			abox.setBranch( 0 );
			// update pseudomodel
			abox.addAllDifferent( list );
			abox.setBranch( branch );
		}

		abox.addAllDifferent( list );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "all diff " + list );
	}

	public void addDifferent(ATermAppl i1, ATermAppl i2) {
		// set addition flag
		changes.add( ChangeType.ABOX_ADD );

		// if we can use incremental consistency checking then add to
		// pseudomodel
		if( canUseIncConsistency() ) {
			// TODO: refactor the access to the updatedIndividuals and
			// newIndividuals - add get method
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( i1 ) );
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( i2 ) );

			// add to pseudomodel - note branch must be temporarily set to 0 to
			// ensure that asssertion
			// will not be restored during backtracking
			int branch = abox.getBranch();
			abox.setBranch( 0 );
			abox.addDifferent( i1, i2 );
			abox.setBranch( branch );
		}

		abox.addDifferent( i1, i2 );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "diff " + i1 + " " + i2 );
	}

	/**
	 * @deprecated Use addPropertyValue instead
	 */
	public void addObjectPropertyValue(ATermAppl p, ATermAppl s, ATermAppl o) {
		addPropertyValue( p, s, o );
	}

	public boolean addPropertyValue(ATermAppl p, ATermAppl s, ATermAppl o) {
		Individual subj = abox.getIndividual( s );
		Role role = getRole( p );
		Node obj = null;

		if( subj == null ) {
			log.warning( s + " is not a known individual!" );
			return false;
		}

		if( role == null ) {
			log.warning( p + " is not a known property!" );
			return false;
		}

		if( !role.isObjectRole() && !role.isDatatypeRole() )
			return false;

		ATermAppl propAxiom = ATermUtils.makePropAtom( p, s, o );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( propAxiom )
			: DependencySet.INDEPENDENT;

		if( role.isObjectRole() ) {
			obj = abox.getIndividual( o );
			if( obj == null ) {
				if( ATermUtils.isLiteral( o ) ) {
					log.warning( "Ignoring literal value " + o + " for object property " + p );
					return false;
				}
				else {
					log.warning( o + " is not a known individual!" );
					return false;
				}
			}
			if( PelletOptions.KEEP_ABOX_ASSERTIONS )
				aboxAssertions.add( AssertionType.OBJ_ROLE, propAxiom );
		}
		else if( role.isDatatypeRole() ) {
			if( !ATermUtils.isLiteral( o ) ) {
				log.warning( "Ignoring non-literal value " + o + " for data property " + p );
				return false;
			}
			obj = abox.addLiteral( o, ds );
			if( PelletOptions.KEEP_ABOX_ASSERTIONS )
				aboxAssertions.add( AssertionType.DATA_ROLE, propAxiom );
		}

		// set addition flag
		changes.add( ChangeType.ABOX_ADD );

		if( !canUseIncConsistency() ) {
			Edge edge = abox.addEdge( p, s, obj.getName(), ds );
			
			if( edge == null ) {
				abox.reset();
				edge = abox.addEdge( p, s, obj.getName(), ds );
				
				assert edge != null;
			}

			if( PelletOptions.USE_INCREMENTAL_DELETION ) {
				// add to syntactic assertions
				syntacticAssertions.add( propAxiom );

				// add to dependency index
				dependencyIndex.addEdgeDependency( edge, edge.getDepends() );
			}
		}
		else if( canUseIncConsistency() ) {
			// TODO: refactor the access to the updatedIndividuals and
			// newIndividuals - add get method
			// add this individual to the affected list
			abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( s ) );

			if( role.isObjectRole() ) {
				// if this is an object property then add the object to the
				// affected list
				abox.getIncrementalChangeTracker().addUpdatedIndividual( abox.getIndividual( o ) );
				
				obj = abox.getIndividual( o );
				if( obj.isPruned() || obj.isMerged() )
					obj = obj.getSame();
			}

			// get the subject
			Individual subj2 = abox.getIndividual( s );
			if( subj2.isPruned() || subj2.isMerged() )
				subj2 = subj2.getSame();

			// generate dependency for new edge
			ds = PelletOptions.USE_TRACING
				? new DependencySet( ATermUtils.makePropAtom( p, s, o ) )
				: DependencySet.INDEPENDENT;

			// add to pseudomodel - note branch must be temporarily set to 0 to
			// ensure that assertion
			// will not be restored during backtracking
			int branch = abox.getBranch();
			abox.setBranch( DependencySet.NO_BRANCH );
			// add the edge
			Edge newEdge = subj2.addEdge( role, obj, ds );
			abox.setBranch( branch );

			// add new edge to affected set
			if( newEdge != null )
				abox.getIncrementalChangeTracker().addNewEdge( newEdge );
		}

		if( log.isLoggable( Level.FINER ) )
			log.finer( "prop-value " + s + " " + p + " " + o );

		return true;
	}

	public boolean addNegatedPropertyValue(ATermAppl p, ATermAppl s, ATermAppl o) {
		changes.add( ChangeType.ABOX_ADD );

		Individual subj = abox.getIndividual( s );
		Role role = getRole( p );
		
		if( subj == null ) {
			log.warning( s + " is not a known individual!" );
			return false;
		}

		if( role == null ) {
			log.warning( p + " is not a known property!" );
			return false;
		}

		ATermAppl propAxiom = ATermUtils.makeNot( ATermUtils.makePropAtom( p, s, o ) );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( propAxiom )
			: DependencySet.INDEPENDENT;
			
		if( role.isObjectRole() ) {
			if( abox.getIndividual( o )  == null ) {
				if( ATermUtils.isLiteral( o ) ) {
					log.warning( "Ignoring literal value " + o + " for object property " + p );
					return false;
				}
				else {
					log.warning( o + " is not a known individual!" );
					return false;
				}
			}
		}
		else if( role.isDatatypeRole() ) {
			abox.addLiteral( o, ds );
		}

		ATermAppl C = ATermUtils.makeNot( ATermUtils.makeHasValue( p, o ) );

		addType( s, C, ds );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "not-prop-value " + s + " " + p + " " + o );

		return true;
	}

	public void addProperty(ATermAppl p) {
		changes.add( ChangeType.RBOX_ADD );
		rbox.addRole( p );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "prop " + p );
	}

	/**
	 * Add a new object property. If property was earlier defined to be a
	 * datatype property then this function will simply return without changing
	 * the KB.
	 * 
	 * @param p
	 *            Name of the property
	 * @return True if property is added, false if not
	 */
	public boolean addObjectProperty(ATerm p) {
		boolean exists = getPropertyType( p ) == PropertyType.OBJECT;

		Role role = rbox.addObjectRole( (ATermAppl) p );

		if( !exists ) {
			changes.add( ChangeType.RBOX_ADD );
			if( log.isLoggable( Level.FINER ) )
				log.finer( "object-prop " + p );
		}

		return role != null;
	}

	/**
	 * Add a new object property. If property was earlier defined to be a
	 * datatype property then this function will simply return without changing
	 * the KB.
	 * 
	 * @param p
	 * @return True if property is added, false if not
	 */
	public boolean addDatatypeProperty(ATerm p) {
		boolean exists = getPropertyType( p ) == PropertyType.DATATYPE;

		Role role = rbox.addDatatypeRole( (ATermAppl) p );

		if( !exists ) {
			changes.add( ChangeType.RBOX_ADD );
			if( log.isLoggable( Level.FINER ) )
				log.finer( "data-prop " + p );
		}

		return role != null;
	}

	@Deprecated
	public void addOntologyProperty(ATermAppl p) {
		addAnnotationProperty(p);		
	}

	public boolean addAnnotationProperty(ATerm p) {
		boolean exists = getPropertyType( p ) == PropertyType.ANNOTATION;

		Role role = rbox.addAnnotationRole( (ATermAppl) p );

		if( !exists ) {
			changes.add( ChangeType.RBOX_ADD );
			if( log.isLoggable( Level.FINER ) )
				log.finer( "annotation-prop " + p );
		}

		return role != null;
	}

	public boolean addAnnotation(ATermAppl s, ATermAppl p, ATermAppl o) {
		if( !PelletOptions.USE_ANNOTATION_SUPPORT )
			return false;

		if( !isAnnotationProperty( p ) )
			return false;

		Map<ATermAppl, Set<ATermAppl>> pidx = annotations.get( s );

		if( pidx == null )
			pidx = new HashMap<ATermAppl, Set<ATermAppl>>();

		Set<ATermAppl> oidx = pidx.get( p );

		if( oidx == null )
			oidx = new HashSet<ATermAppl>();

		oidx.add( o );
		pidx.put( p, oidx );
		annotations.put( s, pidx );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "annotation " + s + " " + p + " " + o );

		return true;
	}

	public Set<ATermAppl> getAnnotations(ATermAppl s, ATermAppl p) {
		Map<ATermAppl, Set<ATermAppl>> pidx = annotations.get( s );

		if( pidx == null )
			return Collections.emptySet();

		Set<ATermAppl> values = new HashSet<ATermAppl>();

		for( ATermAppl subproperty : getSubAnnotationProperties( p ) ) {
			if( pidx.get( subproperty ) != null ) {
				for( ATermAppl value : pidx.get( subproperty ) ) {
					values.add( value );
				}
			}
		}
		
		return values;
	}

	/**
	 * Temporary method until we incorporate annotation properties to the taxonomy ([t:412])
	 * @param p
	 * @return
	 */
	private Set<ATermAppl> getSubAnnotationProperties(ATermAppl p) {
		
		Set<ATermAppl> values = new HashSet<ATermAppl>();
		
		List<ATermAppl> temp = new ArrayList<ATermAppl>(); 
		temp.add(p);
		while(!temp.isEmpty()){
			ATermAppl value = temp.remove(0);
			values.add(value);
			
			for(ATermAppl property : this.getAnnotationProperties()){
				if(value != property && this.isSubPropertyOf(property, value)){
					temp.add(property);
				}
			}
		}
		
		return values;
	}

	public Set<ATermAppl> getIndividualsWithAnnotation(ATermAppl p, ATermAppl o) {
		Set<ATermAppl> ret = new HashSet<ATermAppl>();

		for( Map.Entry<ATermAppl, Map<ATermAppl, Set<ATermAppl>>> e1 : annotations.entrySet() ) {
			ATermAppl st = e1.getKey();
			Map<ATermAppl, Set<ATermAppl>> pidx = e1.getValue();

			for( Map.Entry<ATermAppl, Set<ATermAppl>> e2 : pidx.entrySet() ) {
				ATermAppl pt = e2.getKey();
				Set<ATermAppl> oidx = e2.getValue();

				if( pt.equals( p ) && oidx.contains( o ) )
					ret.add( st );
			}
		}

		return ret;
	}

	public boolean isAnnotation(ATermAppl s, ATermAppl p, ATermAppl o) {
		Set<ATermAppl> oidx = getAnnotations( s, p );

		if( oidx == null )
			return false;

		return oidx.contains( o );
	}

	public void addSubProperty(ATerm sub, ATermAppl sup) {
		changes.add( ChangeType.RBOX_ADD );
		rbox.addSubRole( sub, sup );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "sub-prop " + sub + " " + sup );
	}

	public void addEquivalentProperty(ATermAppl p1, ATermAppl p2) {
		changes.add( ChangeType.RBOX_ADD );
		rbox.addEquivalentRole( p1, p2 );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "same-prop " + p1 + " " + p2 );
	}

	public void addDisjointProperties(ATermList properties) {
		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeDisjointProperties( properties ) )
			: DependencySet.INDEPENDENT;

		for( ATermList l1 = properties; !l1.isEmpty(); l1 = l1.getNext() ) {
			ATermAppl p1 = (ATermAppl) l1.getFirst();
			for( ATermList l2 = l1.getNext(); !l2.isEmpty(); l2 = l2.getNext() ) {
				ATermAppl p2 = (ATermAppl) l2.getFirst();
				addDisjointProperty( p1, p2, ds );
			}
		}
		if( log.isLoggable( Level.FINER ) )
			log.finer( "disjoints " + properties );
	}

	public void addDisjointProperties(List<ATermAppl> properties) {
		addDisjointProperties( ATermUtils.toSet( properties ) );
	}

	public void addDisjointProperty(ATermAppl p1, ATermAppl p2) {
		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeDisjointProperty( p1, p2 ) )
			: DependencySet.INDEPENDENT;

		addDisjointProperty( p1, p2, ds );
	}

	public void addDisjointProperty(ATermAppl p1, ATermAppl p2, DependencySet ds) {
		changes.add( ChangeType.RBOX_ADD );
		rbox.addDisjointRole( p1, p2, ds );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "dis-prop " + p1 + " " + p2 );
	}

	public void addInverseProperty(ATermAppl p1, ATermAppl p2) {
		if( PelletOptions.IGNORE_INVERSES ) {
			log.warning( "Ignoring inverseOf(" + p1 + " " + p2
					+ ") axiom due to the IGNORE_INVERSES option" );
			return;
		}

		changes.add( ChangeType.RBOX_ADD );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeInvProp( p1, p2 ) )
			: DependencySet.INDEPENDENT;

		rbox.addInverseRole( p1, p2, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "inv-prop " + p1 + " " + p2 );
	}

	public void addTransitiveProperty(ATermAppl p) {
		changes.add( ChangeType.RBOX_ADD );

		Role r = rbox.getDefinedRole( p );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeTransitive( p ) )
			: DependencySet.INDEPENDENT;

		// r.setTransitive(true);
		r.addSubRoleChain( ATermUtils.makeList( new ATerm[] { p, p } ), ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "trans-prop " + p );
	}

	public void addSymmetricProperty(ATermAppl p) {
		if( PelletOptions.IGNORE_INVERSES ) {
			log.warning( "Ignoring SymmetricProperty(" + p
					+ ") axiom due to the IGNORE_INVERSES option" );
			return;
		}

		changes.add( ChangeType.RBOX_ADD );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeSymmetric( p ) )
			: DependencySet.INDEPENDENT;

		rbox.addInverseRole( p, p, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "sym-prop " + p );
	}

	/**
	 * @deprecated Use {@link #addAsymmetricProperty(ATermAppl)}
	 */
	public void addAntisymmetricProperty(ATermAppl p) {
		addAsymmetricProperty( p );
	}

	public void addAsymmetricProperty(ATermAppl p) {
		changes.add( ChangeType.RBOX_ADD );
		Role r = rbox.getDefinedRole( p );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeAsymmetric( p ) )
			: DependencySet.INDEPENDENT;

		r.setAsymmetric( true, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "anti-sym-prop " + p );
	}

	public void addReflexiveProperty(ATermAppl p) {
		changes.add( ChangeType.RBOX_ADD );
		Role r = rbox.getDefinedRole( p );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeReflexive( p ) )
			: DependencySet.INDEPENDENT;

		r.setReflexive( true, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "reflexive-prop " + p );
	}

	public void addIrreflexiveProperty(ATermAppl p) {
		changes.add( ChangeType.RBOX_ADD );
		Role r = rbox.getDefinedRole( p );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeIrreflexive( p ) )
			: DependencySet.INDEPENDENT;

		r.setIrreflexive( true, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "irreflexive-prop " + p );
	}

	public void addFunctionalProperty(ATermAppl p) {
		changes.add( ChangeType.RBOX_ADD );
		Role r = rbox.getDefinedRole( p );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeFunctional( p ) )
			: DependencySet.INDEPENDENT;

		r.setFunctional( true, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "func-prop " + p );
	}

	public void addInverseFunctionalProperty(ATerm p) {
		if( PelletOptions.IGNORE_INVERSES ) {
			log.warning( "Ignoring InverseFunctionalProperty(" + p
					+ ") axiom due to the IGNORE_INVERSES option" );
			return;
		}

		changes.add( ChangeType.RBOX_ADD );
		Role role = rbox.getDefinedRole( p );

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeInverseFunctional( p ) )
			: DependencySet.INDEPENDENT;

		role.setInverseFunctional( true, ds );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "inv-func-prop " + p );
	}

	public void addDomain(ATerm p, ATermAppl c) {
		changes.add( ChangeType.RBOX_ADD );

		rbox.addDomain( p, c );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "domain " + p + " " + c );
	}

	/**
	 * For internal use when domain axioms come from TBox absorption
	 */
	public void addDomain(ATerm p, ATermAppl c, Set<ATermAppl> explain) {
		changes.add( ChangeType.RBOX_ADD );

		rbox.addDomain( p, c, explain );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "domain " + p + " " + c + " " + explain );
	}

	public void addRange(ATerm p, ATermAppl c) {
		changes.add( ChangeType.RBOX_ADD );

		rbox.addRange( p, c );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "range " + p + " " + c );
	}

	/**
	 * For internal use when range axioms come from TBox absorption
	 */
	public void addRange(ATerm p, ATermAppl c, Set<ATermAppl> explain) {
		changes.add( ChangeType.RBOX_ADD );

		rbox.addRange( p, c, explain );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "range " + p + " " + c + " " + explain );
	}

	public void addDatatype(ATermAppl p) {
		getDatatypeReasoner().declare( p );
	}

	/**
	 * Adds a new datatype defined to be equivalent to the given data range expression.
	 * 
	 * @param name name of the datatype
	 * @param datarange a data range expression
	 * @return
	 */
	public boolean addDatatypeDefinition(ATermAppl name, ATermAppl datarange) {
		return getDatatypeReasoner().define( name, datarange );
	}

	/**
	 * Removes (if possible) the given property domain axiom from the KB and
	 * return <code>true</code> if removal was successful. See also
	 * {@link #addDomain(ATerm, ATermAppl)}.
	 * 
	 * @param p
	 *            Property in domain axiom
	 * @param c
	 *            Class in domain axiom
	 * @return <code>true</code> if axiom is removed, <code>false</code> if
	 *         removal failed
	 */
	public boolean removeDomain(ATerm p, ATermAppl c) {

		final Role role = getRole( p );
		if( role == null ) {
			handleUndefinedEntity( p + " is not a property!" );
			return false;
		}
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a valid class expression" );
			return false;
		}

		boolean removed = getRBox().removeDomain( p, c );

		if( removed )
			changes.add( ChangeType.RBOX_DEL );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Remove domain " + p + " " + c );

		return removed;
	}

	public boolean removePropertyValue(ATermAppl p, ATermAppl i1, ATermAppl i2) {
		if( ATermUtils.isLiteral( i2 ) ) {
			try {
				i2 = abox.getDatatypeReasoner().getCanonicalRepresentation( i2 );
			} catch( InvalidLiteralException e ) {
				log.warning( format(
						"Unable to remove property value (%s,%s,%s) due to invalid literal: %s", p,
						i1, i2, e.getMessage() ) );
				return false;
			} catch( UnrecognizedDatatypeException e ) {
				log
						.warning( format(
								"Unable to remove property value (%s,%s,%s) due to unrecognized datatype for literal: %s",
								p, i1, i2, e.getMessage() ) );
				return false;
			}
		}

		Individual subj = abox.getIndividual( i1 );
		Node obj = abox.getNode( i2 );
		Role role = getRole( p );

		if( subj == null ) {
			if( PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING )
				throw new UnsupportedFeatureException( i1 + " is not an individual!" );
			else
				return false;
		}

		if( obj == null ) {
			handleUndefinedEntity( i2 + " is not an individual!" );
			return false;
		}

		if( role == null ) {
			handleUndefinedEntity( p + " is not a property!" );
			return false;
		}

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Remove ObjectPropertyValue " + i1 + " " + p + " " + i2 );

		// make sure edge exists in assertions
		Edge edge = subj.getOutEdges().getExactEdge( subj, role, obj );
		
		if( edge == null && obj.isMerged() ) {
			edge = obj.getInEdges().getExactEdge( subj, role, obj );
		}		

		if( edge == null )
			return false;

		// set deletion flag
		changes.add( ChangeType.ABOX_DEL );

		if( !canUseIncConsistency() ) {
			abox.reset();
			
			subj.removeEdge( edge );
			obj.removeInEdge( edge );
		}
		else {
			// if use inc. reasoning then we need to track the deleted
			// assertion.
			// Note that the actual edge will be deleted when
			// undo all dependent
			// structures in ABox.isIncConsistent()

			// add to deleted assertions
			getDeletedAssertions().add( ATermUtils.makePropAtom( p, i1, i2 ) );

			// add this individual to the affected list
			abox.getIncrementalChangeTracker().addUpdatedIndividual( subj );

			// if this is an object property then add the object to the affected
			// list
			if( !role.isDatatypeRole() )
				abox.getIncrementalChangeTracker().addUpdatedIndividual( (Individual) obj );
		}

		if( PelletOptions.KEEP_ABOX_ASSERTIONS ) {
			ATermAppl propAxiom = ATermUtils.makePropAtom( p, i1, i2 );
			if( ATermUtils.isLiteral( i2 ) )
				aboxAssertions.remove( AssertionType.DATA_ROLE, propAxiom );
			else
				aboxAssertions.remove( AssertionType.OBJ_ROLE, propAxiom );
		}

		return true;
	}

	/**
	 * Removes (if possible) the given property range axiom from the KB and
	 * return <code>true</code> if removal was successful. See also
	 * {@link #addRange(ATerm, ATermAppl)}.
	 * 
	 * @param p
	 *            Property in range axiom
	 * @param c
	 *            Class or datatype in range axiom
	 * @return <code>true</code> if axiom is removed, <code>false</code> if
	 *         removal failed
	 */
	public boolean removeRange(ATerm p, ATermAppl c) {

		final Role role = getRole( p );
		if( role == null ) {
			handleUndefinedEntity( p + " is not a property!" );
			return false;
		}
		if( !isClass( c ) && !isDatatype( c ) ) {
			handleUndefinedEntity( c + " is not a valid class expression or data range" );
			return false;
		}

		boolean removed = getRBox().removeRange( p, c );

		if( removed )
			changes.add( ChangeType.RBOX_DEL );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Remove range" + p + " " + c );

		return removed;
	}


	public boolean removeType(ATermAppl ind, ATermAppl c) {
		Individual subj = abox.getIndividual( ind );

		if( subj == null ) {
			if( PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING )
				return false;
			else
				throw new UnsupportedFeatureException( ind + " is not an individual!" );
		}
		
		ATermAppl normC = ATermUtils.normalize( c );
		DependencySet ds = subj.getDepends( normC );
		
		if( ds == null || !ds.isIndependent() )
			return false;

		boolean removed = true;
		
		if( !canUseIncConsistency() || !PelletOptions.USE_INCREMENTAL_DELETION ) {
			abox.reset();
			
			removed = subj.removeType( normC );
		}
		else {
			// if use inc. reasoning then we need to track the deleted
			// assertion.
			// Note that the actual edge type be deleted when undo all dependent
			// structures in ABox.isIncConsistent()

			// add axiom to deletion set
			getDeletedAssertions().add( ATermUtils.makeTypeAtom( ind, c ) );

			// add this individuals to the affected list - used for inc.
			// consistency checking
			abox.getIncrementalChangeTracker().addUpdatedIndividual( subj );

			// we may need to update the expressivity here, however so far it
			// does not seem necessary!
			// updateExpressivity(i, c);
		}

		if( PelletOptions.KEEP_ABOX_ASSERTIONS ) {
			ATermAppl typeAxiom = ATermUtils.makeTypeAtom( ind, c );
			aboxAssertions.remove( AssertionType.TYPE, typeAxiom );
		}
		
		// set deletion flag
		changes.add( ChangeType.ABOX_DEL );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Remove Type " + ind + " " + c );
		
		return removed;
	}

	/**
	 * Removes (if possible) the given TBox axiom from the KB and return
	 * <code>true</code> if removal was successful.
	 * 
	 * @param axiom
	 *            TBox axiom to remove
	 * @return <code>true</code> if axiom is removed, <code>false</code> if
	 *         removal failed
	 */
	public boolean removeAxiom(ATermAppl axiom) {
		boolean removed = false;

		try {
			removed = tbox.removeAxiom( axiom );
		} catch( Exception e ) {
			log.log( Level.SEVERE, "Removal failed for axiom " + axiom, e );
		}

		if( removed )
			changes.add( ChangeType.TBOX_DEL );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Remove " + axiom + ": " + removed );

		return removed;
	}

	public void prepare() {
		if( !isChanged() )
			return;

		boolean explain = abox.doExplanation();
		abox.setDoExplanation( true );

		Timer timer = timers.startTimer( "preprocessing" );
		Timer t;

		// consistency need to be repeated after modifications
		state.remove( ReasoningState.CONSISTENCY );
		// realization need to be repeated after modifications
		state.remove( ReasoningState.REALIZE );
		
		// classification may notbve repeated if ...
		boolean reuseTaxonomy =
				// classification has been previously done
				state.contains( ReasoningState.CLASSIFY )
				// TBox did not change since classification
				&& !isTBoxChanged()
				// RBox did not change since classification
				&& !isRBoxChanged()
				// there are no nominals
				&& (!expChecker.getExpressivity().hasNominal() || PelletOptions.USE_PSEUDO_NOMINALS);

		if( isRBoxChanged() ) {
			if( log.isLoggable( Level.FINER ) )
				log.finer( "Role hierarchy..." );
			t = timers.startTimer( "rbox" );
			rbox.prepare();
			t.stop();
		}

		if( isTBoxChanged() ) {
			if( log.isLoggable( Level.FINER ) )
				log.finer( "Prepare TBox..." );
			t = timers.startTimer( "normalize" );
			tbox.prepare();
			t.stop();
		}

		if( isRBoxChanged() ) {
			rbox.propagateDomainRange();
		}

		canUseIncConsistency = canUseIncConsistency();

		if( abox.isComplete() ) {
			if( changes.contains( ChangeType.TBOX_DEL ) || changes.contains( ChangeType.RBOX_DEL )
					|| (!canUseIncConsistency && changes.contains( ChangeType.ABOX_DEL )) ) {
				abox.reset();
			}
			else if( changes.contains( ChangeType.TBOX_ADD )
					|| changes.contains( ChangeType.RBOX_ADD ) ) {
				abox.resetQueue();
			}
			else if( canUseIncConsistency && changes.contains( ChangeType.ABOX_DEL ) ) {
				IncrementalRestore.restoreDependencies( this );
			}
		}

		// reset flags
		changes.clear();

		instances.clear();

		estimate = new SizeEstimate( this );
		abox.setDoExplanation( explain );

		if( !canUseIncConsistency ) {
			if( log.isLoggable( Level.FINER ) )
				log.finer( "Expressivity..." );

			expChecker.prepare();
		}
		
		abox.clearCaches( !reuseTaxonomy );
		abox.cache.setMaxSize( PelletOptions.MAX_ANONYMOUS_CACHE );

		if( !reuseTaxonomy ) {
			state.remove( ReasoningState.CLASSIFY );
			builder = null;
			// taxonomy = null;
		}

		timer.stop();

		if( log.isLoggable( Level.FINE ) ) {
			StringBuffer info = new StringBuffer();
			info.append( "Expressivity: " + expChecker.getExpressivity() + ", " );
			info.append( "Classes: " + getClasses().size() + " " );
			info.append( "Properties: " + getProperties().size() + " " );
			info.append( "Individuals: " + individuals.size() );
			// info.append( " Strategy: " + chooseStrategy( abox ) );
			log.fine( info.toString() );
		}
	}

	/**
	 * This method is used for incremental reasoning. We do not want to
	 * recompute the expressivity from scratch.
	 */
	public void updateExpressivity(ATermAppl i, ATermAppl c) {

		// if the tbox or rbox changed then we cannot use incremental reasoning!
		if( !isChanged() || isTBoxChanged() || isRBoxChanged() )
			return;

		// update expressivity given this individual
		expChecker.updateWithIndividual( i, c );

		// update the size estimate as this could be a new individual
		estimate = new SizeEstimate( this );
	}

	public String getInfo() {
		prepare();

		StringBuffer buffer = new StringBuffer();
		buffer.append( "Expressivity: " + expChecker.getExpressivity() + " " );
		buffer.append( "Classes: " + getClasses().size() + " " );
		buffer.append( "Properties: " + getProperties().size() + " " );
		buffer.append( "Individuals: " + individuals.size() + " " );

		Expressivity expressivity = expChecker.getExpressivity();
		if( expressivity.hasNominal() )
			buffer.append( "Nominals: " + expressivity.getNominals().size() + " " );

		return buffer.toString();
	}

	/**
	 * Returns true if the consistency check has been done and nothing in th KB
	 * has changed after that.
	 */
	public boolean isConsistencyDone() {
		return !isChanged() && state.contains( ReasoningState.CONSISTENCY );
	}

	/**
	 * Returns true if the classification check has been done and nothing in the
	 * KB has changed after that.
	 */
	public boolean isClassified() {
		return !isChanged() && state.contains( ReasoningState.CLASSIFY );
	}

	public boolean isRealized() {
		return !isChanged() && state.contains( ReasoningState.REALIZE );
	}

	public boolean isChanged() {
		return !changes.isEmpty();
	}

	public boolean isChanged(ChangeType change) {
		return changes.contains( change );
	}

	public boolean isTBoxChanged() {
		return changes.contains( ChangeType.TBOX_ADD ) || changes.contains( ChangeType.TBOX_DEL );
	}

	public boolean isRBoxChanged() {
		return changes.contains( ChangeType.RBOX_ADD ) || changes.contains( ChangeType.RBOX_DEL );
	}

	public boolean isABoxChanged() {
		return changes.contains( ChangeType.ABOX_ADD ) || changes.contains( ChangeType.ABOX_DEL );
	}

	/**
	 * Returns all unsatisfiable classes in the KB excluding the BOTTOM concept. The result may be empty if there is no
	 * user-defined concept in the KB that is unsatisfiable.
	 * 
	 * @return all unsatisfiable classes in the KB excluding the BOTTOM concept
	 */
	public Set<ATermAppl> getUnsatisfiableClasses() {
		return getUnsatisfiableClasses(false);
	}
		
	/**
	 * Returns all unsatisfiable classes in the KB including the BOTTOM concept. Since BOTTOM concept is built-in the
	 * result will always have at least one element.
	 * 
	 * @return all unsatisfiable classes in the KB including the BOTTOM concept
	 */
	public Set<ATermAppl> getAllUnsatisfiableClasses() {
		return getUnsatisfiableClasses(true);
	}
		
	private Set<ATermAppl> getUnsatisfiableClasses(boolean includeBottom) {	
		Set<ATermAppl> aUnsatClasses = new HashSet<ATermAppl>();

		if (isClassified()) {
			// if the kb is already classified we can get them this way
			aUnsatClasses = includeBottom ? getAllEquivalentClasses(ATermUtils.BOTTOM)
			                : getEquivalentClasses(ATermUtils.BOTTOM);				
		}
		else {
			if (includeBottom)
				aUnsatClasses.add(BOTTOM);

			// if not, check for them like this, without triggering classification
			Set<ATermAppl> aClasses = getClasses();
			for (ATermAppl aClass : aClasses) {
				if (!isSatisfiable(aClass)) {
					aUnsatClasses.add(aClass);
				}
			}
		}

		return aUnsatClasses;
	}

	private void consistency() {
		if( isConsistencyDone() )
			return;

		abox.setInitialized( false );

		// prepare the KB
		prepare();		
		
		for( Entry<Rule, Rule> normalizedRule : rules.entrySet() ) {
			if( normalizedRule.getValue() == null ) {
				Rule rule = normalizedRule.getKey();
				String msg = UsableRuleFilter.explainNotUsable( rule );				
				log.warning( "Ignoring rule " + rule + ": " + msg );
			}
		}

		Timer timer = timers.startTimer( "consistency" );

		boolean doExplanation = abox.doExplanation();
		
		if( PelletOptions.USE_TRACING && !explainOnlyInconsistency ) 
			abox.setDoExplanation( true );
			
		// perform the consistency check
		consistent = canUseIncConsistency
			? abox.isIncConsistent()
			: abox.isConsistent();

		// final clean up
		if( PelletOptions.USE_INCREMENTAL_CONSISTENCY )
			abox.getIncrementalChangeTracker().clear();

		if( PelletOptions.USE_INCREMENTAL_DELETION )
			getDeletedAssertions().clear();

		if( !consistent ) {
			// the behavior of Pellet 1.5.1 (and prior versions) was to generate
			// explanations for inconsistent ontologies even if the
			// doExplanation
			// was not set. this was causing an overhead for repeated
			// consistency
			// tests that mostly turn out to be consistent. the new strategy is
			// to repeat the consistency test for inconsistent ontologies by
			// manually setting the doExplanation flag. this will generate more
			// overhead for inconsistent ontologies but inconsistent ontologies
			// are much less frequent so this trade-off is preferred

			// create explanation by default for the ABox consistency check
			// but only if we can generate it (i.e. tracing is turned on) and
			// we haven't already done so (i.e. doExplanation flag was false at
			// the beginning)
			if( PelletOptions.USE_TRACING && explainOnlyInconsistency && !abox.doExplanation() ) {
				abox.setDoExplanation( true );

				abox.reset();
				abox.isConsistent();

				abox.setDoExplanation( false );
			}

			if ( log.isLoggable( Level.FINE )) {
				log.fine( "Inconsistent ontology. Reason: " + getExplanation() );
			}

			if( PelletOptions.USE_TRACING && log.isLoggable( Level.FINE )) {
				log.fine( renderExplanationSet() );
			}
		}
		
		abox.setDoExplanation( doExplanation );
		
		state.add( ReasoningState.CONSISTENCY );
		
		timer.stop();
		
		if ( log.isLoggable( Level.FINE ) ) {
			log.fine( "Consistent: " + consistent + " (" + timer.getLast() + "ms)" );
		}

		assert isConsistencyDone() : "Consistency flag not set";
	}

	private String renderExplanationSet() {
		StringBuilder msg = new StringBuilder("ExplanationSet: [");
		Set<ATermAppl> explanation = getExplanationSet();
		for( ATermAppl axiom : explanation ) {
			msg.append( ATermUtils.toString( axiom ) );
			msg.append( "," );
		}
		if( explanation.isEmpty() )
			msg.append( ']' );
		else
			msg.setCharAt( msg.length() - 1, ']' );

		return msg.toString();
	}

	public boolean isConsistent() {
		consistency();

		return consistent;
	}

	public Taxonomy<ATermAppl> getToldTaxonomy() {
		return getTaxonomyBuilder().getToldTaxonomy();
	}

	public Map<ATermAppl, Set<ATermAppl>> getToldDisjoints() {
		return getTaxonomyBuilder().getToldDisjoints();
	}

	public void ensureConsistency() {
		if( !isConsistent() )
			throw new InconsistentOntologyException(
					"Cannot do reasoning with inconsistent ontologies!\n" +
					"Reason for inconsistency: " + getExplanation() +
					(PelletOptions.USE_TRACING ? "\n" + renderExplanationSet() : "" ));
	}

	public void classify() {
		ensureConsistency();

		if( isClassified() )
			return;

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Classifying..." );

		Timer timer = timers.startTimer( "classify" );

		builder = getTaxonomyBuilder();

		boolean isClassified = builder.classify();

		timer.stop();

		if( !isClassified )
			return;

		state.add( ReasoningState.CLASSIFY );

		estimate.computKBCosts();
	}

	public void realize() {
		if( isRealized() )
			return;

		classify();

		if( !isClassified() )
			return;

		Timer timer = timers.startTimer( "realize" );

		// This is false if the progress monitor is canceled
		boolean isRealized = builder.realize();

		timer.stop();

		if( !isRealized )
			return;

		state.add( ReasoningState.REALIZE );

		estimate.computKBCosts();
	}

	/**
	 * Return the set of all named classes. Returned set is unmodifiable!
	 * 
	 * @return
	 */
	public Set<ATermAppl> getClasses() {
		return Collections.unmodifiableSet( tbox.getClasses() );
	}

	/**
	 * Return the set of all named classes including TOP and BOTTOM. Returned
	 * set is modifiable.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getAllClasses() {
		return Collections.unmodifiableSet( tbox.getAllClasses() );
	}

	/**
	 * Return the set of all properties.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p )
					&& (role.isObjectRole() || role.isDatatypeRole() || role.isAnnotationRole()) )
				set.add( p );
		}
		return set;
	}

	/**
	 * Return the set of all object properties.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getObjectProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isObjectRole() )
				set.add( p );
		}
		return set;
	}

	public Set<ATermAppl> getAnnotationProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isAnnotationRole() )
				set.add( p );
		}
		return set;
	}

	public Set<ATermAppl> getTransitiveProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isTransitive() )
				set.add( p );
		}
		set.add( ATermUtils.BOTTOM_OBJECT_PROPERTY );
		return set;
	}

	public Set<ATermAppl> getSymmetricProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isSymmetric() )
				set.add( p );
		}
		return set;
	}

	/**
	 * @deprecated Use {@link #getAntisymmetricProperties()}
	 */
	public Set<ATermAppl> getAntisymmetricProperties() {
		return getAsymmetricProperties();
	}

	public Set<ATermAppl> getAsymmetricProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isAsymmetric() )
				set.add( p );
		}
		return set;
	}

	public Set<ATermAppl> getReflexiveProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isReflexive() )
				set.add( p );
		}
		return set;
	}

	public Set<ATermAppl> getIrreflexiveProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isIrreflexive() )
				set.add( p );
		}
		return set;
	}

	public Set<ATermAppl> getFunctionalProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isFunctional() )
				set.add( p );
		}
		set.add( ATermUtils.BOTTOM_DATA_PROPERTY );
		set.add( ATermUtils.BOTTOM_OBJECT_PROPERTY );
		return set;
	}

	public Set<ATermAppl> getInverseFunctionalProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isInverseFunctional() )
				set.add( p );
		}
		set.add( ATermUtils.BOTTOM_OBJECT_PROPERTY );
		return set;
	}

	/**
	 * Return the set of all object properties.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getDataProperties() {
		Set<ATermAppl> set = new HashSet<ATermAppl>();
		for( Role role : rbox.getRoles() ) {
			ATermAppl p = role.getName();
			if( ATermUtils.isPrimitive( p ) && role.isDatatypeRole() )
				set.add( p );
		}
		return set;
	}

	/**
	 * Return the set of all individuals. Returned set is unmodifiable!
	 * 
	 * @return
	 */
	public Set<ATermAppl> getIndividuals() {
		return Collections.unmodifiableSet( individuals );
	}
	
	/**
	 * Returns the set of key values of the annotations map
	 * @return
	 */
	public Set<ATermAppl> getAnnotationSubjects(){
		return annotations.keySet();
	}

	public Role getProperty(ATerm r) {
		return rbox.getRole( r );
	}

	public PropertyType getPropertyType(ATerm r) {
		Role role = getProperty( r );
		return (role == null)
			? PropertyType.UNTYPED
			: role.getType();
	}

	public boolean isClass(ATerm c) {

		if( tbox.getClasses().contains( c ) || c.equals( ATermUtils.TOP ) )
			return true;
		else if( ATermUtils.isComplexClass( c ) ) {
			return fullyDefinedVisitor.isFullyDefined( (ATermAppl) c );
		}
		else
			return false;
	}

	public boolean isProperty(ATerm p) {
		return rbox.isRole( p );
	}

	public boolean isDatatypeProperty(ATerm p) {
		return getPropertyType( p ) == PropertyType.DATATYPE;
	}

	public boolean isObjectProperty(ATerm p) {
		return getPropertyType( p ) == PropertyType.OBJECT;
	}

	public boolean isABoxProperty(ATerm p) {
		PropertyType type = getPropertyType( p );
		return (type == PropertyType.OBJECT) || (type == PropertyType.DATATYPE);
	}

	public boolean isAnnotationProperty(ATerm p) {
		return getPropertyType( p ) == PropertyType.ANNOTATION;
	}

	@Deprecated
	public boolean isOntologyProperty(ATerm p) {
		return false;
	}

	public boolean isIndividual(ATerm ind) {
		return getIndividuals().contains( ind );
	}

	public boolean isTransitiveProperty(ATermAppl r) {
		Role role = getRole( r );

		if( role == null ) {
			handleUndefinedEntity( r + " is not a known property" );
			return false;
		}

		if( role.isTransitive() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainTransitive() );
			return true;
		}
		else if( !role.isObjectRole() || role.isFunctional() || role.isInverseFunctional() )
			return false;

		ensureConsistency();

		ATermAppl c = ATermUtils.makeTermAppl( "_C_" );
		ATermAppl notC = ATermUtils.makeNot( c );
		ATermAppl test = ATermUtils.makeAnd( ATermUtils.makeSomeValues( r, ATermUtils
				.makeSomeValues( r, c ) ), ATermUtils.makeAllValues( r, notC ) );

		return !abox.isSatisfiable( test );
	}

	public boolean isSymmetricProperty(ATermAppl p) {
		return isInverse( p, p );
	}

	public boolean isFunctionalProperty(ATermAppl p) {
		Role role = getRole( p );

		if( role == null ) {
			handleUndefinedEntity( p + " is not a known property" );
			return false;
		}
		
		if(role.isAnnotationRole())
			return false;
		
		if( role.isBottom() ) {
			if ( doExplanation() )
				abox.setExplanation( DependencySet.INDEPENDENT );
			return true;
		}
		else if( role.isFunctional() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainFunctional() );
			return true;
		}
		else if( !role.isSimple() )
			return false;

		ATermAppl min2P = role.isDatatypeRole()
			? ATermUtils.makeMin( p, 2, ATermUtils.TOP_LIT )
			: ATermUtils.makeMin( p, 2, ATermUtils.TOP );
		return !isSatisfiable( min2P );
	}

	public boolean isInverseFunctionalProperty(ATermAppl p) {
		Role role = getRole( p );

		if( role == null ) {
			handleUndefinedEntity( p + " is not a known property" );
			return false;
		}

		if( !role.isObjectRole() ) {
			return false;
		}		
		else if( role.isInverseFunctional() || role.isBottom() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainInverseFunctional() );
			return true;
		}

		ATermAppl invP = role.getInverse().getName();
		ATermAppl max1invP = ATermUtils.makeMax( invP, 1, ATermUtils.TOP );
		return isSubClassOf( ATermUtils.TOP, max1invP );
	}

	public boolean isReflexiveProperty(ATermAppl p) {
		Role role = getRole( p );

		if( role == null ) {
			handleUndefinedEntity( p + " is not a known property" );
			return false;
		}

		if( !role.isObjectRole() || role.isIrreflexive() )
			return false;
		else if( role.isReflexive() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainReflexive() );
			return true;
		}

		ensureConsistency();

		ATermAppl c = ATermUtils.makeTermAppl( "_C_" );
		ATermAppl notC = ATermUtils.makeNot( c );
		ATermAppl test = ATermUtils.makeAnd( c, ATermUtils.makeAllValues( p, notC ) );

		return !abox.isSatisfiable( test );
	}

	public boolean isIrreflexiveProperty(ATermAppl p) {
		Role role = getRole( p );

		if( role == null ) {
			handleUndefinedEntity( p + " is not a known property" );
			return false;
		}

		if( !role.isObjectRole() || role.isReflexive() )
			return false;
		else if( role.isIrreflexive() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainIrreflexive() );
			return true;
		}
		else if( role.isAsymmetric() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainAsymmetric() );
			return true;
		}

		ensureConsistency();

		ATermAppl test = ATermUtils.makeSelf( p );

		return !abox.isSatisfiable( test );
	}

	/**
	 * @deprecated Use {@link #isAsymmetricProperty(ATermAppl)}
	 */
	public boolean isAntisymmetricProperty(ATermAppl p) {
		return isAsymmetricProperty( p );
	}

	public boolean isAsymmetricProperty(ATermAppl p) {
		Role role = getRole( p );

		if( role == null ) {
			handleUndefinedEntity( p + " is not a known property" );
			return false;
		}

		if( !role.isObjectRole() )
			return false;
		else if( role.isAsymmetric() ) {
			if( doExplanation() )
				abox.setExplanation( role.getExplainAsymmetric() );
			return true;
		}

		ensureConsistency();

		ATermAppl o = ATermUtils.makeAnonNominal( Integer.MAX_VALUE );
		ATermAppl nom = ATermUtils.makeValue( o );
		ATermAppl test = ATermUtils.makeAnd( nom, ATermUtils.makeSomeValues( p, ATermUtils.makeAnd(
				ATermUtils.makeNot( nom ), ATermUtils.makeSomeValues( p, nom ) ) ) );

		return !abox.isSatisfiable( test );
	} 

	public boolean isSubPropertyOf(ATermAppl sub, ATermAppl sup) {
		Role roleSub = rbox.getRole( sub );
		Role roleSup = rbox.getRole( sup );

		if( roleSub == null ) {
			handleUndefinedEntity( sub + " is not a known property" );
			return false;
		}

		if( roleSup == null ) {
			handleUndefinedEntity( sup + " is not a known property" );
			return false;
		}

		if( roleSub.isSubRoleOf( roleSup ) ) {
			if( doExplanation() )
				abox.setExplanation( roleSub.getExplainSuper( sup ) );
			return true;
		}

		if( roleSub.getType() != roleSup.getType() )
			return false;
		
		ensureConsistency();
		
		ATermAppl test;
		if( roleSub.isObjectRole() ) {
			ATermAppl c = ATermUtils.makeTermAppl( "_C_" );
			ATermAppl notC = ATermUtils.makeNot( c );
			test = ATermUtils.makeAnd( ATermUtils.makeSomeValues( sub, c ), ATermUtils
					.makeAllValues( sup, notC ) );
		}
		else if( roleSub.isDatatypeRole() ) {
			ATermAppl anon = ATermUtils
					.makeLiteral( ATermUtils.makeAnonNominal( Integer.MAX_VALUE ) );
			test = ATermUtils.makeAnd( ATermUtils.makeHasValue( sub, anon ), ATermUtils
					.makeAllValues( sup, ATermUtils.makeNot( ATermUtils.makeValue( anon ) ) ) );
		}
		else if( roleSub.isAnnotationRole() ) {
			return false; //temporary statement until we incorporate annotation properties to the taxonomy ([t:412])
		} else
			throw new IllegalArgumentException();

		return !abox.isSatisfiable( test );
	}

	public boolean isEquivalentProperty(ATermAppl p1, ATermAppl p2) {
		Role role1 = rbox.getRole( p1 );
		Role role2 = rbox.getRole( p2 );

		if( role1 == null ) {
			handleUndefinedEntity( p1 + " is not a known property" );
			return false;
		}

		if( role2 == null ) {
			handleUndefinedEntity( p2 + " is not a known property" );
			return false;
		}

		if( role1.isSubRoleOf( role2 ) && role2.isSubRoleOf( role1 ) ) {
			if( doExplanation() )
				abox.setExplanation( role1.getExplainSuper( p2 ).union( role1.getExplainSub( p2 ),
						doExplanation() ) );
			return true;
		}

		if( role1.isAnnotationRole() || role2.isAnnotationRole() )
			return false;

		if( role1.getType() != role2.getType() )
			return false;

		ensureConsistency();

		ATermAppl test;
		if( role1.isObjectRole() ) {
			ATermAppl c = !role1.getRanges().isEmpty()
				? role1.getRanges().iterator().next()
				: !role2.getRanges().isEmpty()
					? role2.getRanges().iterator().next()
					: ATermUtils.makeTermAppl( "_C_" );
			ATermAppl notC = ATermUtils.makeNot( c );
			test = ATermUtils.makeOr( ATermUtils.makeAnd( ATermUtils.makeSomeValues( p1, c ),
					ATermUtils.makeAllValues( p2, notC ) ), ATermUtils.makeAnd( ATermUtils
					.makeSomeValues( p2, c ), ATermUtils.makeAllValues( p1, notC ) ) );
		}
		else if( role1.isDatatypeRole() ) {
			ATermAppl anon = ATermUtils
			.makeLiteral( ATermUtils.makeAnonNominal( Integer.MAX_VALUE ) );
			test = ATermUtils.makeOr(
				ATermUtils.makeAnd(
					ATermUtils.makeHasValue( p1, anon ),
					ATermUtils.makeAllValues( p2, ATermUtils.makeNot( ATermUtils.makeValue( anon ) ) )
				),
				ATermUtils.makeAnd(
					ATermUtils.makeHasValue( p2, anon ),
					ATermUtils.makeAllValues( p1, ATermUtils.makeNot( ATermUtils.makeValue( anon ) ) )
				)
			);
		}
		else
			throw new IllegalArgumentException();

		return !abox.isSatisfiable( test );
	}

	public boolean isInverse(ATermAppl r1, ATermAppl r2) {
		Role role1 = getRole( r1 );
		Role role2 = getRole( r2 );

		if( role1 == null ) {
			handleUndefinedEntity( r1 + " is not a known property" );
			return false;
		}

		if( role2 == null ) {
			handleUndefinedEntity( r2 + " is not a known property" );
			return false;
		}

		// the following condition is wrong due to nominals, see OWL test
		// cases SymmetricProperty-002
		// if( !role1.hasNamedInverse() )
		// return false;

		if( !role1.isObjectRole() || !role2.isObjectRole() )
			return false;

		if( role1.getInverse().equals( role2 ) ) {
			return true;
		}

		ensureConsistency();

		ATermAppl c = ATermUtils.makeTermAppl( "_C_" );
		ATermAppl notC = ATermUtils.makeNot( c );

		ATermAppl test = ATermUtils.makeAnd( c, ATermUtils.makeOr( ATermUtils.makeSomeValues( r1,
				ATermUtils.makeAllValues( r2, notC ) ), ATermUtils.makeSomeValues( r2, ATermUtils
				.makeAllValues( r1, notC ) ) ) );

		return !abox.isSatisfiable( test );
	}

	public boolean hasDomain(ATermAppl p, ATermAppl c) {
		Role r = rbox.getRole( p );
		if( r == null ) {
			handleUndefinedEntity( p + " is not a property!" );
			return false;
		}

		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a valid class expression" );
			return false;
		}

		ATermAppl someP = ATermUtils.makeSomeValues( p, ATermUtils.getTop( r ) );
		return isSubClassOf( someP, c );
	}

	public boolean hasRange(ATermAppl p, ATermAppl c) {
		if( !isClass( c ) && !isDatatype( c ) ) {
			handleUndefinedEntity( c + " is not a valid class expression" );
			return false;
		}
		ATermAppl allValues = ATermUtils.makeAllValues( p, c );
		return isSubClassOf( ATermUtils.TOP, allValues );
	}

	public boolean isDatatype(ATermAppl c) {
		return datatypeVisitor.isDatatype( c );
	}

	public boolean isSatisfiable(ATermAppl c) {
		ensureConsistency();

		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a known class!" );
			return false;
		}

		c = ATermUtils.normalize( c );

		if( isClassified() && !doExplanation() ) {
			Bool equivToBottom = builder.getTaxonomy().isEquivalent( ATermUtils.BOTTOM, c );
			if( equivToBottom.isKnown() )
				return equivToBottom.isFalse();
		}

		return abox.isSatisfiable( c );
	}

	/**
	 * Returns true if there is at least one named individual that belongs to
	 * the given class
	 * 
	 * @param c
	 * @return
	 */
	public boolean hasInstance(ATerm d) {
		if( !isClass( d ) ) {
			handleUndefinedEntity( d + " is not a class!" );
			return false;
		}

		ensureConsistency();

		ATermAppl c = ATermUtils.normalize( (ATermAppl) d );

		List<ATermAppl> unknowns = new ArrayList<ATermAppl>();
		Iterator<Individual> i = new IndividualIterator( abox );
		while( i.hasNext() ) {
			ATermAppl x = i.next().getName();

			Bool knownType = abox.isKnownType( x, c );
			if( knownType.isTrue() )
				return true;
			else if( knownType.isUnknown() )
				unknowns.add( x );
		}

		boolean hasInstance = !unknowns.isEmpty() && abox.isType( unknowns, c );

		return hasInstance;
	}

	/*
	public boolean isSubTypeOf(ATermAppl d1, ATermAppl d2) {
		if( !isDatatype( d1 ) ) {
			handleUndefinedEntity( d1 + " is not a known datatype" );
			return false;
		}

		if( !isDatatype( d2 ) ) {
			handleUndefinedEntity( d2 + " is not a known datatype" );
			return false;
		}

		return getDatatypeReasoner().isSubTypeOf( d1, d2 );
	}
	*/

	/**
	 * Check if class c1 is subclass of class c2.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public boolean isSubClassOf(ATermAppl c1, ATermAppl c2) {
		ensureConsistency();

		if( !isClass( c1 ) ) {
			handleUndefinedEntity( c1 + " is not a known class" );
			return false;
		}

		if( !isClass( c2 ) ) {
			handleUndefinedEntity( c2 + " is not a known class" );
			return false;
		}

		if( c1.equals( c2 ) )
			return true;

		// normalize concepts
		c1 = ATermUtils.normalize( c1 );
		c2 = ATermUtils.normalize( c2 );

		if( isClassified() && !doExplanation() ) {
			Bool isSubNode = builder.getTaxonomy().isSubNodeOf( c1, c2 );
			if( isSubNode.isKnown() )
				return isSubNode.isTrue();
		}

		return abox.isSubClassOf( c1, c2 );
	}

	/**
	 * Check if class c1 is equivalent to class c2.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public boolean isEquivalentClass(ATermAppl c1, ATermAppl c2) {
		ensureConsistency();

		if( !isClass( c1 ) ) {
			handleUndefinedEntity( c1 + " is not a known class" );
			return false;
		}

		if( !isClass( c2 ) ) {
			handleUndefinedEntity( c2 + " is not a known class" );
			return false;
		}

		if( c1.equals( c2 ) )
			return true;

		// normalize concepts
		c1 = ATermUtils.normalize( c1 );
		c2 = ATermUtils.normalize( c2 );

		if( !doExplanation() ) {
			Bool isEquivalent = Bool.UNKNOWN;
			if( isClassified() )
				isEquivalent = builder.getTaxonomy().isEquivalent( c1, c2 );

			if( isEquivalent.isUnknown() )
				isEquivalent = abox.isKnownSubClassOf( c1, c2 ).and(
						abox.isKnownSubClassOf( c2, c1 ) );

			if( isEquivalent.isKnown() )
				return isEquivalent.isTrue();
		}

		ATermAppl notC2 = ATermUtils.negate( c2 );
		ATermAppl notC1 = ATermUtils.negate( c1 );
		ATermAppl c1NotC2 = ATermUtils.makeAnd( c1, notC2 );
		ATermAppl c2NotC1 = ATermUtils.makeAnd( c2, notC1 );
		ATermAppl test = ATermUtils.makeOr( c1NotC2, c2NotC1 );

		return !isSatisfiable( test );
	}

	public boolean isDisjoint(ATermAppl c1, ATermAppl c2) {
		if( isClass( c1 ) && isClass( c2 ) )
			return isDisjointClass( c1, c2 );
		else if( isProperty( c1 ) && isProperty( c2 ) )
			return isDisjointProperty( c1, c2 );
		else
			return false;
	}

	public boolean isDisjointClass(ATermAppl c1, ATermAppl c2) {
		ATermAppl notC2 = ATermUtils.makeNot( c2 );

		return isSubClassOf( c1, notC2 );
	}

	public boolean isDisjointProperty(ATermAppl r1, ATermAppl r2) {
		Role role1 = getRole( r1 );
		Role role2 = getRole( r2 );

		if( role1 == null ) {
			handleUndefinedEntity( r1 + " is not a known property" );
			return false;
		}

		if( role2 == null ) {
			handleUndefinedEntity( r2 + " is not a known property" );
			return false;
		}
				
		if( role1.getType() != role2.getType() ) {
			return false;
		}
		else if( role1.isBottom() || role2.isBottom() ) {
			if( doExplanation() )
				abox.setExplanation( DependencySet.INDEPENDENT );
			return true;
		}
		else if( role1.isTop() || role2.isTop() ) {
			return false;
		}
		else if( role1.getSubRoles().contains(role2) || role2.getSubRoles().contains(role1) ) {
			return false;
		}

		if( role1.getDisjointRoles().contains( role2 ) && !doExplanation() )
			return true;

		ensureConsistency();
				
		ATermAppl anon = ATermUtils.makeAnonNominal(Integer.MAX_VALUE);
		if( role1.isDatatypeRole() ) {
			anon = ATermUtils.makeLiteral(anon);
		}
		ATermAppl nominal = ATermUtils.makeValue(anon);
		ATermAppl test = and( some( r1, nominal ), some( r2, nominal ));
		
		return !abox.isSatisfiable( test );
	}

	public boolean isComplement(ATermAppl c1, ATermAppl c2) {
		ATermAppl notC2 = ATermUtils.makeNot( c2 );

		return isEquivalentClass( c1, notC2 );
	}

	/**
	 * Answers the isType question without doing any satisfiability check. It
	 * might return <code>Bool.TRUE</code>, <code>Bool.FALSE</code>, or
	 * <code>Bool.UNKNOWN</code>. If <code>Bool.UNKNOWN</code> is returned
	 * <code>isType</code> function needs to be called to get the answer.
	 * 
	 * @param x
	 * @param c
	 * @return
	 */
	public Bool isKnownType(ATermAppl x, ATermAppl c) {
		ensureConsistency();

		if( !isIndividual( x ) ) {
			handleUndefinedEntity( x + " is not an individual!" );
			return Bool.FALSE;
		}
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a valid class expression" );
			return Bool.FALSE;
		}

		c = ATermUtils.normalize( c );

		return abox.isKnownType( x, c );
	}

	public boolean isType(ATermAppl x, ATermAppl c) {
		ensureConsistency();

		if( !isIndividual( x ) ) {
			handleUndefinedEntity( x + " is not an individual!" );
			return false;
		}
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a valid class expression" );
			return false;
		}

		if( isRealized() && !doExplanation() ) {
			if( builder == null )
				throw new NullPointerException( "Builder is null" );

			Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

			if( taxonomy == null )
				throw new NullPointerException( "Taxonomy is null" );

			if( taxonomy.contains( c ) )
				return TaxonomyUtils.isType( taxonomy, x, c );
		}

		return abox.isType( x, c );
	}

	public boolean isSameAs(ATermAppl t1, ATermAppl t2) {
		ensureConsistency();

		if( !isIndividual( t1 ) ) {
			handleUndefinedEntity( t1 + " is not an individual!" );
			return false;
		}
		if( !isIndividual( t2 ) ) {
			handleUndefinedEntity( t2 + " is not an individual!" );
			return false;
		}

		if( t1.equals( t2 ) )
			return true;

		Set<ATermAppl> knowns = new HashSet<ATermAppl>();
		Set<ATermAppl> unknowns = new HashSet<ATermAppl>();

		Individual ind = abox.getIndividual( t1 );
		if( ind.isMerged() && !ind.getMergeDependency( true ).isIndependent() )
			abox.getSames( ind.getSame(), unknowns, unknowns );
		else
			abox.getSames( ind.getSame(), knowns, unknowns );

		if( knowns.contains( t2 ) ) {
			if( !doExplanation() )
				return true;
		}
		else if( !unknowns.contains( t2 ) ) {
			return false;
		}

		return abox.isSameAs( t1, t2 );
	}

	public boolean isDifferentFrom(ATermAppl t1, ATermAppl t2) {
		Individual ind1 = abox.getIndividual( t1 );
		Individual ind2 = abox.getIndividual( t2 );

		if( ind1 == null ) {
			handleUndefinedEntity( t1 + " is not an individual!" );
			return false;
		}

		if( ind2 == null ) {
			handleUndefinedEntity( t2 + " is not an individual!" );
			return false;
		}

		if( ind1.isDifferent( ind2 ) && !doExplanation() )
			return true;

		ATermAppl c = ATermUtils.makeNot( ATermUtils.makeValue( t2 ) );

		return isType( t1, c );
	}

	public Set<ATermAppl> getDifferents(ATermAppl name) {
		ensureConsistency();
		
		Individual ind = abox.getIndividual( name );

		if( ind == null ) {
			handleUndefinedEntity( name + " is not an individual!" );
			return Collections.emptySet();
		}

		boolean isIndependent = true;
		if( ind.isMerged() ) {
			isIndependent = ind.getMergeDependency( true ).isIndependent();
			ind = ind.getSame();
		}
		
		ATermAppl c = ATermUtils.makeNot( ATermUtils.makeValue( name ) );

		Set<ATermAppl> differents = new HashSet<ATermAppl>();
		for( ATermAppl x : individuals ) {
			Bool isType = abox.isKnownType( x, c );
			if( isIndependent && isType.isKnown() ) {
				if( isType.isTrue() )
					differents.add( x );
			}
			else if( isType( x, c ) ) {
				differents.add( x );				
			}
		}

		return differents;
	}

	public boolean hasPropertyValue(ATermAppl s, ATermAppl p, ATermAppl o) {
		ensureConsistency();

		if( !isIndividual( s ) ) {
			handleUndefinedEntity( s + " is not an individual!" );
			return false;
		}

		if( !isProperty( p ) ) {
			handleUndefinedEntity( p + " is not a known property!" );
			return false;
		}

		if( o != null ) {
			if( isDatatypeProperty( p ) ) {
				if( !ATermUtils.isLiteral( o ) )
					return false;
			}
			else if( !isIndividual( o ) ) {
				return false;
			}
		}

		return abox.hasPropertyValue( s, p, o );
	}

	/**
	 * Answers the hasPropertyValue question without doing any satisfiability
	 * check. It might return <code>Boolean.TRUE</code>,
	 * <code>Boolean.FALSE</code>, or <code>null</code> (unknown). If the
	 * null value is returned <code>hasPropertyValue</code> function needs to
	 * be called to get the answer.
	 * 
	 * @param s
	 *            Subject
	 * @param p
	 *            Predicate
	 * @param o
	 *            Object (<code>null</code> can be used as wildcard)
	 * @return
	 */
	public Bool hasKnownPropertyValue(ATermAppl s, ATermAppl p, ATermAppl o) {
		ensureConsistency();

		return abox.hasObviousPropertyValue( s, p, o );
	}

	/**
	 * @return Returns the abox.
	 */
	public ABox getABox() {
		return abox;
	}

	/**
	 * @return Returns the rbox.
	 */
	public RBox getRBox() {
		return rbox;
	}

	/**
	 * @return Returns the tbox.
	 */
	public TBox getTBox() {
		return tbox;
	}

	/**
	 * @return Returns the DatatypeReasoner
	 */
	public DatatypeReasoner getDatatypeReasoner() {
		return abox.getDatatypeReasoner();
	}

	/**
	 * Returns the (named) superclasses of class c. Depending on the second
	 * parameter the resulting list will include either all or only the direct
	 * superclasses. A class d is a direct superclass of c iff
	 * <ol>
	 * <li> d is superclass of c </li>
	 * <li> there is no other class x such that x is superclass of c and d is
	 * superclass of x </li>
	 * </ol>
	 * The class c itself is not included in the list but all the other classes
	 * that are sameAs c are put into the list. Also note that the returned list
	 * will always have at least one element. The list will either include one
	 * other concept from the hierarchy or the TOP concept if no other class
	 * subsumes c. By definition TOP concept is superclass of every concept.
	 * <p>
	 * *** This function will first classify the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose superclasses are returned
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<ATermAppl>> getSuperClasses(ATermAppl c, boolean direct) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}

		c = ATermUtils.normalize( c );

		classify();

		Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

		if( !taxonomy.contains( c ) )
			builder.classify( c );

		Set<Set<ATermAppl>> supers = new HashSet<Set<ATermAppl>>();
		for( Set<ATermAppl> s : taxonomy.getSupers( c, direct ) ) {
			Set<ATermAppl> supEqSet = ATermUtils.primitiveOrBottom( s );
			if( !supEqSet.isEmpty() )
				supers.add( supEqSet );
		}

		return supers;
	}

	/**
	 * Returns all the (named) subclasses of class c. The class c itself is not
	 * included in the list but all the other classes that are equivalent to c
	 * are put into the list. Also note that the returned list will always have
	 * at least one element, that is the BOTTOM concept. By definition BOTTOM
	 * concept is subclass of every concept. This function is equivalent to
	 * calling getSubClasses(c, true).
	 * <p>
	 * *** This function will first classify the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose subclasses are returned
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<ATermAppl>> getSubClasses(ATermAppl c) {
		return getSubClasses( c, false );
	}

	public Set<Set<ATermAppl>> getDisjoints(ATermAppl c) {
		if( isClass( c ) )
			return getDisjointClasses( c );
		else if( isProperty( c ) )
			return getDisjointProperties( c );
		else
			handleUndefinedEntity( c + " is not a property nor a class!" );
		return Collections.emptySet();
	}

	public Set<Set<ATermAppl>> getDisjointClasses(ATermAppl c) {
		return getDisjointClasses( c, false );
	}
	
	public Set<Set<ATermAppl>> getDisjointClasses(ATermAppl c, boolean direct) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}
		
		ATermAppl notC = ATermUtils.normalize( ATermUtils.makeNot( c ) );
		

		Set<ATermAppl> complements = getAllEquivalentClasses( notC );
		if( notC.equals( ATermUtils.BOTTOM ) ) 
			complements.add( ATermUtils.BOTTOM );
		if( direct && !complements.isEmpty() ) {
			return Collections.singleton( complements );
		}
		
		Set<Set<ATermAppl>> disjoints = getSubClasses( notC, direct );		
		
		if( !complements.isEmpty() )
			disjoints.add( complements );

		return disjoints;
	}

	public Set<Set<ATermAppl>> getDisjointProperties(ATermAppl p) {
		return getDisjointProperties( p, false );
	}
	
	public Set<Set<ATermAppl>> getDisjointProperties(ATermAppl p, boolean direct) {
		if( !isProperty( p ) ) {
			handleUndefinedEntity( p + " is not a property!" );
			return Collections.emptySet();
		}

		Role role = rbox.getRole( p );

		if( !role.isObjectRole() && !role.isDatatypeRole() )
			return Collections.emptySet();
		
		Set<Set<ATermAppl>> disjoints = new HashSet<Set<ATermAppl>>();
		
		TaxonomyNode<ATermAppl> node = getRoleTaxonomy( role.isObjectRole() ).getTop();
		
		Set<TaxonomyNode<ATermAppl>> marked = new HashSet<TaxonomyNode<ATermAppl>>();
		List<TaxonomyNode<ATermAppl>> visit = new ArrayList<TaxonomyNode<ATermAppl>>();
		visit.add( node );

		for( int i = 0; i < visit.size(); i++ ) {
			node = visit.get( i );

			if( node.isHidden() || node.getEquivalents().isEmpty() || marked.contains( node ) )
				continue;
			
			ATermAppl r = node.getName();
			if( isDisjointProperty( p, r ) ) {
				Set<ATermAppl> eqs = getAllEquivalentProperties( r );
				if( !eqs.isEmpty() )
					disjoints.add( eqs );
				if( direct )
					mark( node, marked );
				else
					disjoints.addAll( getSubProperties( r ) );
			}
			else {
				visit.addAll( node.getSubs() );
			}

		}

		return disjoints;
	}

	private void mark(TaxonomyNode<ATermAppl> node, Set<TaxonomyNode<ATermAppl>> marked) {	    
	    marked.add( node );
	    
		for( TaxonomyNode<ATermAppl> next : node.getSubs() ) {
			mark( next, marked );
		}
	}
	
	public Set<ATermAppl> getComplements(ATermAppl c) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}

		ATermAppl notC = ATermUtils.normalize( ATermUtils.makeNot( c ) );
		Set<ATermAppl> complements = getAllEquivalentClasses( notC );

		if( notC.equals( ATermUtils.BOTTOM ) ) 
			complements.add( ATermUtils.BOTTOM );

		return complements;
	}

	/**
	 * Returns the (named) classes individual belongs to. Depending on the
	 * second parameter the result will include either all types or only the
	 * direct types.
	 * 
	 * @param ind
	 *            An individual name
	 * @param direct
	 *            If true return only the direct types, otherwise return all
	 *            types
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<ATermAppl>> getTypes(ATermAppl ind, boolean direct) {
		if( !isIndividual( ind ) ) {
			handleUndefinedEntity( ind + " is not an individual!" );
			return Collections.emptySet();
		}

		realize();

		Set<Set<ATermAppl>> types = new HashSet<Set<ATermAppl>>();
		for( Set<ATermAppl> t : TaxonomyUtils.getTypes( builder.getTaxonomy(), ind, direct ) ) {
			Set<ATermAppl> eqSet = ATermUtils.primitiveOrBottom( t );
			if( !eqSet.isEmpty() )
				types.add( eqSet );
		}

		return types;
	}

	/**
	 * Get all the (named) classes individual belongs to.
	 * <p>
	 * *** This function will first realize the whole ontology ***
	 * </p>
	 * 
	 * @param ind
	 *            An individual name
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<ATermAppl>> getTypes(ATermAppl ind) {
		return getTypes( ind, /* direct = */false );
	}

	public ATermAppl getType(ATermAppl ind) {
		if( !isIndividual( ind ) ) {
			handleUndefinedEntity( ind + " is not an individual!" );
			return null;
		}

		// there is always at least one atomic class guranteed to exist (i.e.
		// owl:Thing)
		return abox.getIndividual( ind ).getTypes( Node.ATOM ).iterator().next();
	}

	public ATermAppl getType(ATermAppl ind, boolean direct) {
		if( !isIndividual( ind ) ) {
			handleUndefinedEntity( ind + " is not an individual!" );
			return null;
		}

		realize();

		for( Set<ATermAppl> t : TaxonomyUtils.getTypes( builder.getTaxonomy(), ind, direct ) ) {
			Set<ATermAppl> eqSet = ATermUtils.primitiveOrBottom( t );
			if( !eqSet.isEmpty() )
				return eqSet.iterator().next();
		}

		return null;
	}

	/**
	 * Returns all the instances of concept c. If TOP concept is used every
	 * individual in the knowledge base will be returned
	 * 
	 * @param c
	 *            class whose instances are returned
	 * @return A set of ATerm objects
	 */
	public Set<ATermAppl> getInstances(ATermAppl c) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}

		if( instances.containsKey( c ) )
			return instances.get( c );
		else if( isRealized() ) {
			if( builder == null )
				throw new NullPointerException( "Builder is null" );

			Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

			if( taxonomy == null )
				throw new NullPointerException( "Taxonomy is null" );

			if( taxonomy.contains( c ) && ATermUtils.isPrimitive( c ) )
				return TaxonomyUtils.getAllInstances( taxonomy, c );
		}

		return new HashSet<ATermAppl>( retrieve( c, individuals ) );
	}

	/**
	 * Returns the instances of class c. Depending on the second parameter the
	 * resulting list will include all or only the direct instances. An
	 * individual x is a direct instance of c iff x is of type c and there is no
	 * subclass d of c such that x is of type d.
	 * <p>
	 * *** This function will first realize the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose instances are returned
	 * @param direct
	 *            if true return only the direct instances, otherwise return all
	 *            the instances
	 * @return A set of ATerm objects
	 */
	public Set<ATermAppl> getInstances(ATermAppl c, boolean direct) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}

		// All instances for anonymous concepts
		if( !direct )
			return getInstances( c );

		realize();

		if( builder == null )
			throw new NullPointerException( "Builder is null" );

		Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

		if( taxonomy == null )
			throw new NullPointerException( "Taxonomy is null" );

		// Named concepts
		if( ATermUtils.isPrimitive( c ) )
			return TaxonomyUtils.getDirectInstances( taxonomy, c );

		if( !taxonomy.contains( c ) )
			builder.classify( c );

		// Direct instances for anonymous concepts
		Set<ATermAppl> ret = new HashSet<ATermAppl>();
		Set<Set<ATermAppl>> sups = getSuperClasses( c, true );

		for( Set<ATermAppl> s : sups ) {
			Iterator<ATermAppl> i = s.iterator();
			ATermAppl term = i.next();
			Set<ATermAppl> cand = TaxonomyUtils.getDirectInstances( taxonomy, term );

			if( ret.isEmpty() )
				ret.addAll( cand );
			else
				ret.retainAll( cand );

			if( ret.isEmpty() )
				return ret;
		}

		return retrieve( c, ret );
	}

	/**
	 * Returns all the classes that are equivalent to class c, excluding c
	 * itself.
	 * <p>
	 * *** This function will first classify the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose equivalent classes are found
	 * @return A set of ATerm objects
	 */
	public Set<ATermAppl> getEquivalentClasses(ATermAppl c) {
		Set<ATermAppl> result = getAllEquivalentClasses( c );
		result.remove( c );

		return result;
	}

	/**
	 * Returns all the classes that are equivalent to class c, including c
	 * itself.
	 * <p>
	 * *** This function will first classify the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose equivalent classes are found
	 * @return A set of ATerm objects
	 */
	public Set<ATermAppl> getAllEquivalentClasses(ATermAppl c) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}

		c = ATermUtils.normalize( c );

		classify();

		Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

		if( !taxonomy.contains( c ) )
			builder.classify( c );

		return ATermUtils.primitiveOrBottom( taxonomy.getAllEquivalents( c ) );
	}

	/**
	 * Returns all the superclasses (implicitly or explicitly defined) of class
	 * c. The class c itself is not included in the list. but all the other
	 * classes that are sameAs c are put into the list. Also note that the
	 * returned list will always have at least one element, that is TOP concept.
	 * By definition TOP concept is superclass of every concept. This function
	 * is equivalent to calling getSuperClasses(c, true).
	 * <p>
	 * *** This function will first classify the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose superclasses are returned
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<ATermAppl>> getSuperClasses(ATermAppl c) {
		return getSuperClasses( c, false );
	}

	/**
	 * Returns the (named) subclasses of class c. Depending on the second
	 * parameter the result will include either all subclasses or only the
	 * direct subclasses. A class d is a direct subclass of c iff
	 * <ol>
	 * <li>d is subclass of c</li>
	 * <li>there is no other class x different from c and d such that x is
	 * subclass of c and d is subclass of x</li>
	 * </ol>
	 * The class c itself is not included in the list but all the other classes
	 * that are sameAs c are put into the list. Also note that the returned list
	 * will always have at least one element. The list will either include one
	 * other concept from the hierarchy or the BOTTOM concept if no other class
	 * is subsumed by c. By definition BOTTOM concept is subclass of every
	 * concept.
	 * <p>
	 * *** This function will first classify the whole ontology ***
	 * </p>
	 * 
	 * @param c
	 *            class whose subclasses are returned
	 * @param direct
	 *            If true return only the direct subclasses, otherwise return
	 *            all the subclasses
	 * @return A set of sets, where each set in the collection represents an
	 *         equivalence class. The elements of the inner class are ATermAppl
	 *         objects.
	 */
	public Set<Set<ATermAppl>> getSubClasses(ATermAppl c, boolean direct) {
		if( !isClass( c ) ) {
			handleUndefinedEntity( c + " is not a class!" );
			return Collections.emptySet();
		}

		c = ATermUtils.normalize( c );

		classify();

		Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

		if( !taxonomy.contains( c ) )
			builder.classify( c );

		Set<Set<ATermAppl>> subs = new HashSet<Set<ATermAppl>>();
		for( Set<ATermAppl> s : taxonomy.getSubs( c, direct ) ) {
			Set<ATermAppl> subEqSet = ATermUtils.primitiveOrBottom( s );
			if( !subEqSet.isEmpty() )
				subs.add( subEqSet );
		}

		return subs;
	}

	public Set<Set<ATermAppl>> getAllSuperProperties(ATermAppl prop) {
		if( !isProperty( prop ) ) {
			handleUndefinedEntity( prop + " is not a property!" );
			return Collections.emptySet();
		}
		
		Set<Set<ATermAppl>> supers = getSuperProperties( prop );
		supers.add( getAllEquivalentProperties( prop ) );

		return supers;
	}

	/**
	 * Return all the super properties of p.
	 * 
	 * @param prop
	 * @return A set of sets, where each set in the collection represents a set
	 *         of equivalent properties. The elements of the inner class are
	 *         Role objects.
	 */
	public Set<Set<ATermAppl>> getSuperProperties(ATermAppl prop) {
		return getSuperProperties( prop, false );
	}

	/**
	 * Return the super properties of p. Depending on the second parameter the
	 * result will include either all super properties or only the direct super
	 * properties.
	 * 
	 * @param prop
	 * @param direct
	 *            If true return only the direct super properties, otherwise
	 *            return all the super properties
	 * @return A set of sets, where each set in the collection represents a set
	 *         of equivalent properties. The elements of the inner class are
	 *         Role objects.
	 */
	public Set<Set<ATermAppl>> getSuperProperties(ATermAppl prop, boolean direct) {
		if( !isProperty( prop ) ) {
			handleUndefinedEntity( prop + " is not a property!" );
			return Collections.emptySet();
		}
		
		Set<Set<ATermAppl>> supers = new HashSet<Set<ATermAppl>>();
		Taxonomy<ATermAppl> taxonomy = getRoleTaxonomy( prop );
		if( taxonomy != null ) {
			for( Set<ATermAppl> s : taxonomy.getSupers( prop, direct ) ) {
				Set<ATermAppl> supEqSet = ATermUtils.primitiveOrBottom( s );
				if( !supEqSet.isEmpty() )
					supers.add( supEqSet );
			}
		}

		return supers;
	}

	public Set<Set<ATermAppl>> getAllSubProperties(ATermAppl prop) {
		if( !isProperty( prop ) ) {
			handleUndefinedEntity( prop + " is not a property!" );
			return Collections.emptySet();
		}
		
		Set<Set<ATermAppl>> subs = getSubProperties( prop );
		subs.add( getAllEquivalentProperties( prop ) );

		return subs;
	}

	/**
	 * Return all the sub properties of p.
	 * 
	 * @param prop
	 * @return A set of sets, where each set in the collection represents a set
	 *         of equivalent properties. The elements of the inner class are
	 *         ATermAppl objects.
	 */
	public Set<Set<ATermAppl>> getSubProperties(ATermAppl prop) {
		return getSubProperties( prop, false );
	}

	/**
	 * Return the sub properties of p. Depending on the second parameter the
	 * result will include either all subproperties or only the direct
	 * subproperties.
	 * 
	 * @param prop
	 * @param direct
	 *            If true return only the direct subproperties, otherwise return
	 *            all the subproperties
	 * @return A set of sets, where each set in the collection represents a set
	 *         of equivalent properties. The elements of the inner class are
	 *         ATermAppl objects.
	 */
	public Set<Set<ATermAppl>> getSubProperties(ATermAppl prop, boolean direct) {
		if( !isProperty( prop ) ) {
			handleUndefinedEntity( prop + " is not a property!" );
			return Collections.emptySet();
		}

		Set<Set<ATermAppl>> subs = new HashSet<Set<ATermAppl>>();
		Taxonomy<ATermAppl> taxonomy = getRoleTaxonomy( prop );
		if( taxonomy != null ) {
			for( Set<ATermAppl> s : taxonomy.getSubs( prop, direct ) ) {
				Set<ATermAppl> subEqSet = ATermUtils.primitiveOrBottom( s );
				if( !subEqSet.isEmpty() )
					subs.add( subEqSet );
			}
		}
		else {
			System.out.print("");
		}

		return subs;
	}

	/**
	 * Return all the properties that are equivalent to p.
	 * 
	 * @param prop
	 * @return A set of ATermAppl objects.
	 */
	public Set<ATermAppl> getEquivalentProperties(ATermAppl prop) {
		if( !isProperty( prop ) ) {
			handleUndefinedEntity( prop + " is not a property!" );
			return Collections.emptySet();
		}
		
		Taxonomy<ATermAppl> taxonomy = getRoleTaxonomy( prop );		
		return taxonomy != null ? ATermUtils.primitiveOrBottom(taxonomy.getEquivalents(prop)) : Collections
		                .<ATermAppl> emptySet();
	}

	public Set<ATermAppl> getAllEquivalentProperties(ATermAppl prop) {
		if( !isProperty( prop ) ) {
			handleUndefinedEntity( prop + " is not a property!" );
			return Collections.emptySet();
		}
		
		Taxonomy<ATermAppl> taxonomy = getRoleTaxonomy( prop );		
		return taxonomy != null ? ATermUtils.primitiveOrBottom(taxonomy.getAllEquivalents(prop)) : Collections
		                .<ATermAppl> emptySet();
	}

	/**
	 * Return the named inverse property and all its equivalent properties.
	 * 
	 * @param prop
	 * @return
	 */
	public Set<ATermAppl> getInverses(ATerm name) {
		ATermAppl invR = getInverse( name );
		if( invR != null ) {
			Set<ATermAppl> inverses = getAllEquivalentProperties( invR );
			return inverses;
		}

		return Collections.emptySet();
	}

	/**
	 * Returns the inverse of given property. This could possibly be an internal
	 * property created by the reasoner rather than a named property. In case
	 * the given property has more than one inverse any one of them can be
	 * returned.
	 * 
	 * @param name
	 *            Property whose inverse being sought
	 * @return Inverse property or null if given property is not defined or it
	 *         is not an object property
	 */
	public ATermAppl getInverse(ATerm name) {
		Role prop = rbox.getRole( name );
		if( prop == null ) {
			handleUndefinedEntity( name + " is not a property!" );
			return null;
		}

		Role invProp = prop.getInverse();

		return invProp != null
			? invProp.getName()
			: null;
	}

	/**
	 * Return the domain restrictions on the property. The results of this
	 * function is not guaranteed to be complete. Use
	 * {@link #hasDomain(ATermAppl, ATermAppl)} to get complete answers.
	 * 
	 * @param prop
	 * @return
	 */
	public Set<ATermAppl> getDomains(ATermAppl name) {
		ensureConsistency();

		Role prop = rbox.getRole( name );
		if( prop == null ) {
			handleUndefinedEntity( name + " is not a property!" );
			return Collections.emptySet();
		}

		return ATermUtils.primitiveOrBottom( prop.getDomains() );
	}

	/**
	 * Return the domain restrictions on the property. The results of this
	 * function is not guaranteed to be complete. Use
	 * {@link #hasRange(ATermAppl, ATermAppl)} to get complete answers.
	 * 
	 * @param prop
	 * @return
	 */
	public Set<ATermAppl> getRanges(ATerm name) {
		ensureConsistency();

		Set<ATermAppl> set = Collections.emptySet();
		Role prop = rbox.getRole( name );
		if( prop == null ) {
			handleUndefinedEntity( name + " is not a property!" );
			return set;
		}

		return ATermUtils.primitiveOrBottom( prop.getRanges() );
	}

	/**
	 * Return all the indviduals asserted to be equal to the given individual
	 * inluding the individual itself.
	 * 
	 * @param name
	 * @return
	 */
	public Set<ATermAppl> getAllSames(ATermAppl name) {
		ensureConsistency();

		Set<ATermAppl> knowns = new HashSet<ATermAppl>();
		Set<ATermAppl> unknowns = new HashSet<ATermAppl>();

		Individual ind = abox.getIndividual( name );
		if( ind == null ) {
			handleUndefinedEntity( name + " is not an individual!" );
			return Collections.emptySet();
		}

		if( ind.isMerged() && !ind.getMergeDependency( true ).isIndependent() ) {
			knowns.add( name );
			abox.getSames( ind.getSame(), unknowns, unknowns );
			unknowns.remove( name );
		}
		else
			abox.getSames( ind.getSame(), knowns, unknowns );

		for( ATermAppl other : unknowns ) {
			if( abox.isSameAs( name, other ) )
				knowns.add( other );
		}

		return knowns;
	}

	/**
	 * Return all the individuals asserted to be equal to the given individual
	 * but not the the individual itself.
	 * 
	 * @param name
	 * @return
	 */
	public Set<ATermAppl> getSames(ATermAppl name) {
		Set<ATermAppl> sames = getAllSames( name );
		sames.remove( name );

		return sames;
	}

	/**
	 * Return all literal values for a given dataproperty that belongs to the
	 * specified datatype.
	 * 
	 * @param r
	 * @param x
	 * @param lang
	 * @return List of ATermAppl objects representing literals. These objects
	 *         are in the form literal(value, lang, datatypeURI).
	 */
	public List<ATermAppl> getDataPropertyValues(ATermAppl r, ATermAppl x, ATermAppl datatype) {
		ensureConsistency();

		Individual ind = abox.getIndividual( x );
		Role role = rbox.getRole( r );

		if( ind == null ) {
			handleUndefinedEntity( x + " is not an individual!" );
			return Collections.emptyList();
		}

		if( role == null || !role.isDatatypeRole() ) {
			handleUndefinedEntity( r + " is not a known data property!" );
			return Collections.emptyList();
		}
		
		if( role.isTop() ) {
			List<ATermAppl> literals = new ArrayList<ATermAppl>();
			if( !PelletOptions.HIDE_TOP_PROPERTY_VALUES ) {
				for( Node node : abox.getNodes() ) {
					if( node.isLiteral() && node.getTerm() != null )
						literals.add( node.getTerm() );
				}
			}
			return literals;
		}
		else if( role.isBottom() ) {
			return Collections.emptyList();			
		}
		else {
			return abox.getDataPropertyValues( x, role, datatype );
		}
	}

	/**
	 * Return all literal values for a given dataproperty that has the specified
	 * language identifier.
	 * 
	 * @param r
	 * @param x
	 * @param lang
	 * @return List of ATermAppl objects.
	 */
	public List<ATermAppl> getDataPropertyValues(ATermAppl r, ATermAppl x, String lang) {
		List<ATermAppl> values = getDataPropertyValues( r, x );
		if( lang == null )
			return values;

		List<ATermAppl> result = new ArrayList<ATermAppl>();
		for( ATermAppl lit : values ) {
			String litLang = ((ATermAppl) lit.getArgument( 1 )).getName();

			if( litLang.equals( lang ) )
				result.add( lit );
		}

		return result;
	}

	/**
	 * Return all literal values for a given dataproperty and subject value.
	 * 
	 * @param r
	 * @param x
	 * @return List of ATermAppl objects.
	 */
	public List<ATermAppl> getDataPropertyValues(ATermAppl r, ATermAppl x) {
		return getDataPropertyValues( r, x, (ATermAppl) null );
	}

	/**
	 * Return all property values for a given object property and subject value.
	 * 
	 * @param r
	 * @param x
	 * @return A list of ATermAppl objects
	 */
	public List<ATermAppl> getObjectPropertyValues(ATermAppl r, ATermAppl x) {
		ensureConsistency();

		Role role = rbox.getRole( r );

		if( role == null || !role.isObjectRole() ) {
			handleUndefinedEntity( r + " is not a known object property!" );
			return Collections.emptyList();
		}

		if( !isIndividual( x ) ) {
			handleUndefinedEntity( x + " is not a known individual!" );
			return Collections.emptyList();
		}
		
		// TODO get rid of unnecessary Set + List creation
		Set<ATermAppl> knowns = new HashSet<ATermAppl>();
		Set<ATermAppl> unknowns = new HashSet<ATermAppl>();

		if( role.isTop() ) {
			if( !PelletOptions.HIDE_TOP_PROPERTY_VALUES )
				knowns = getIndividuals();
		}
		else if( !role.isBottom() ) {
			abox.getObjectPropertyValues( x, role, knowns, unknowns, true );			
		}

		if( !unknowns.isEmpty() ) {
			ATermAppl valueX = ATermUtils.makeHasValue( role.getInverse().getName(), x );
			ATermAppl c = ATermUtils.normalize( valueX );

			binaryInstanceRetrieval( c, new ArrayList<ATermAppl>( unknowns ), knowns );
		}

		return new ArrayList<ATermAppl>( knowns );
	}

	/**
	 * Return all property values for a given property and subject value.
	 * 
	 * @param r
	 * @param x
	 * @return List of ATermAppl objects.
	 */
	public List<ATermAppl> getPropertyValues(ATermAppl r, ATermAppl x) {
		Role role = rbox.getRole( r );

		if( role == null || role.isUntypedRole() ) {
			handleUndefinedEntity( r + " is not a known property!" );
			return Collections.emptyList();
		}

		if( role.isObjectRole() )
			return getObjectPropertyValues( r, x );
		else if ( role.isDatatypeRole() )
			return getDataPropertyValues( r, x );
		else if( role.isAnnotationRole() ) {
			final Set<ATermAppl> values = getAnnotations( x, r );
			return values.isEmpty()
				? Collections.<ATermAppl> emptyList()
				: Arrays.asList( values.toArray( new ATermAppl[0] ) );
		}
		else
			throw new IllegalArgumentException();
	}

	/**
	 * List all subjects with a given property and property value.
	 * 
	 * @param r
	 * @param x
	 *            If property is an object property an ATermAppl object that is
	 *            the URI of the individual, if the property is a data property
	 *            an ATerm object that contains the literal value (See {#link
	 *            #getIndividualsWithDataProperty(ATermAppl, ATermAppl)} for
	 *            details)
	 * @return List of ATermAppl objects.
	 */
	public List<ATermAppl> getIndividualsWithProperty(ATermAppl r, ATermAppl x) {
		Role role = rbox.getRole( r );

		if( role == null ) {
			handleUndefinedEntity( r + " is not a known property!" );
			return Collections.emptyList();
		}

		if( role.isObjectRole() ) {
			return getIndividualsWithObjectProperty( r, x );
		}
		else if( role.isDatatypeRole() ) {
			return getIndividualsWithDataProperty( r, x );
		}
		else if( role.isAnnotationRole() )
			return Arrays.asList( getIndividualsWithAnnotation( r, x ).toArray( new ATermAppl[0] ) );
		else
			throw new IllegalArgumentException();
	}

	/**
	 * List all subjects with the given literal value for the specified data
	 * property.
	 * 
	 * @param r
	 *            An ATerm object that contains the literal value in the form
	 *            literal(lexicalValue, langIdentifier, datatypeURI). Should be
	 *            created with ATermUtils.makeXXXLiteral() functions.
	 * @param x
	 * @return List of ATermAppl objects.
	 */
	public List<ATermAppl> getIndividualsWithDataProperty(ATermAppl r, ATermAppl litValue) {
		if (!ATermUtils.isLiteral(litValue)) {
			return Collections.emptyList();
		}

		ensureConsistency();
		
		List<ATermAppl> knowns = new ArrayList<ATermAppl>();
		List<ATermAppl> unknowns = new ArrayList<ATermAppl>();

		ATermAppl canonicalLit;
		try {
			canonicalLit = getDatatypeReasoner().getCanonicalRepresentation( litValue );
		} catch( InvalidLiteralException e ) {
			log.warning( format("Invalid literal '%s' passed as input, returning empty set of individuals: %s", litValue, e.getMessage()) );
			return Collections.emptyList();
		} catch( UnrecognizedDatatypeException e ) {
			log.warning( format("Unrecognized datatype for literal '%s' passed as input, returning empty set of individuals: %s", litValue, e.getMessage()) );
			return Collections.emptyList();
		}
		Literal literal = abox.getLiteral( canonicalLit );

		if( literal != null ) {
			Role role = getRole( r );
			EdgeList edges = literal.getInEdges();
			for( Edge edge : edges ) {
				if( edge.getRole().isSubRoleOf( role ) ) {
					ATermAppl subj = edge.getFrom().getName();
					if( edge.getDepends().isIndependent() )
						knowns.add( subj );
					else
						unknowns.add( subj );
				}
			}

			if( !unknowns.isEmpty() ) {
				ATermAppl c = ATermUtils.normalize( ATermUtils.makeHasValue( r, litValue ) );

				binaryInstanceRetrieval( c, unknowns, knowns );
			}
		}

		return knowns;
	}

	/**
	 * List all subjects with the given value for the specified object property.
	 * 
	 * @param r
	 * @param o
	 *            An ATerm object that is the URI of an individual
	 * @return List of ATermAppl objects.
	 */
	public List<ATermAppl> getIndividualsWithObjectProperty(ATermAppl r, ATermAppl o) {
		ensureConsistency();

		if( !isIndividual( o ) ) {
			handleUndefinedEntity( o + " is not an individual!" );
			return Collections.emptyList();
		}

		Role role = rbox.getRole( r );

		ATermAppl invR = role.getInverse().getName();

		return getObjectPropertyValues( invR, o );
	}

	/**
	 * List all properties asserted between a subject and object.
	 */
	public List<ATermAppl> getProperties(ATermAppl s, ATermAppl o) {
		if( !isIndividual( s ) ) {
			handleUndefinedEntity( s + " is not an individual!" );
			return Collections.emptyList();
		}

		if( !isIndividual( o ) && !ATermUtils.isLiteral( o ) ) {
			handleUndefinedEntity( o + " is not an individual!" );
			return Collections.emptyList();
		}

		List<ATermAppl> props = new ArrayList<ATermAppl>();

		Set<ATermAppl> allProps = ATermUtils.isLiteral( o )
			? getDataProperties()
			: getObjectProperties();
		for( ATermAppl p : allProps ) {
			if( abox.hasPropertyValue( s, p, o ) )
				props.add( p );
		}

		return props;
	}

	public Map<ATermAppl, List<ATermAppl>> getPropertyValues(ATermAppl pred) {
		Map<ATermAppl, List<ATermAppl>> result = new HashMap<ATermAppl, List<ATermAppl>>();

		for( ATermAppl subj : individuals ) {
			List<ATermAppl> objects = getPropertyValues( pred, subj );
			if( !objects.isEmpty() )
				result.put( subj, objects );
		}

		return result;
	}

	/**
	 * Return all the individuals that belong to the given class which is not
	 * necessarily a named class.
	 * 
	 * @param d
	 * @return
	 */
	public Set<ATermAppl> retrieve(ATermAppl d, Collection<ATermAppl> individuals) {
		ensureConsistency();

		ATermAppl c = ATermUtils.normalize( d );

		Timer timer = timers.startTimer( "retrieve" );

		ATermAppl notC = ATermUtils.negate( c );
		List<ATermAppl> knowns = new ArrayList<ATermAppl>();

		// this is mostly to ensure that a model for notC is cached
		if( !abox.isSatisfiable( notC ) ) {
			// if negation is unsat c itself is TOP
			knowns.addAll( getIndividuals() );
		}
		else if( abox.isSatisfiable( c ) ) {
			Set<ATermAppl> subs = Collections.emptySet();
			if( isClassified() ) {
				if( builder == null )
					throw new NullPointerException( "Builder is null" );

				Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();

				if( taxonomy == null )
					throw new NullPointerException( "Taxonomy" );

				if( taxonomy.contains( c ) )
					subs = taxonomy.getFlattenedSubs( c, false );
			}

			List<ATermAppl> unknowns = new ArrayList<ATermAppl>();
			for( ATermAppl x : individuals ) {
				Bool isType = abox.isKnownType( x, c, subs );
				if( isType.isTrue() )
					knowns.add( x );
				else if( isType.isUnknown() )
					unknowns.add( x );
			}

			if( !unknowns.isEmpty() ) {
				if( PelletOptions.INSTANCE_RETRIEVAL == InstanceRetrievalMethod.TRACING_BASED
						&& PelletOptions.USE_TRACING ) {
					tracingBasedInstanceRetrieval( c, unknowns, knowns );
				}
				else if( abox.isType( unknowns, c ) ) {
					if( PelletOptions.INSTANCE_RETRIEVAL == InstanceRetrievalMethod.BINARY )
						binaryInstanceRetrieval( c, unknowns, knowns );
					else
						linearInstanceRetrieval( c, unknowns, knowns );
				}
			}

		}

		timer.stop();

		Set<ATermAppl> result = Collections.unmodifiableSet( new HashSet<ATermAppl>( knowns ) );

		if( PelletOptions.CACHE_RETRIEVAL )
			instances.put( c, result );

		return result;
	}

	/**
	 * Retrieve individuals which possibly have a property value for the given
	 * property.
	 */
	public List<ATermAppl> retrieveIndividualsWithProperty(ATermAppl r) {
		ensureConsistency();

		Role role = rbox.getRole( r );
		if( role == null ) {
			handleUndefinedEntity( r + " is not a known property!" );
			return Collections.emptyList();
		}
		
		List<ATermAppl> result = new ArrayList<ATermAppl>();
		for( ATermAppl ind : individuals ) {
			if( !abox.hasObviousPropertyValue( ind, r, null ).isFalse() )
				result.add( ind );
		}

		return result;
	}

	public void tracingBasedInstanceRetrieval(ATermAppl c, List<ATermAppl> candidates,
			Collection<ATermAppl> results) {
		boolean doExplanation = doExplanation();
		setDoExplanation( true );

		ATermAppl notC = ATermUtils.negate( c );
		while( abox.isType( candidates, c ) ) {
			final Set<ATermAppl> explanationSet = getExplanationSet();

			for( ATermAppl axiom : explanationSet ) {
				if( axiom.getAFun().equals( ATermUtils.TYPEFUN )
						&& axiom.getArgument( 1 ).equals( notC ) ) {
					ATermAppl ind = (ATermAppl) axiom.getArgument( 0 );
					int index = candidates.indexOf( ind );
					if( index >= 0 ) {
						if( log.isLoggable( Level.FINER ) )
							log.finer( "Filter instance " + axiom + " while retrieving " + c );
						Collections.swap( candidates, index, 0 );
						results.add( ind );
						candidates = candidates.subList( 1, candidates.size() );
						break;
					}
				}
			}
		}

		setDoExplanation( doExplanation );
	}

	public void linearInstanceRetrieval(ATermAppl c, List<ATermAppl> candidates,
			Collection<ATermAppl> results) {
		for( ATermAppl ind : candidates ) {
			if( abox.isType( ind, c ) )
				results.add( ind );
		}
	}

	public void binaryInstanceRetrieval(ATermAppl c, List<ATermAppl> candidates,
			Collection<ATermAppl> results) {
		if( candidates.isEmpty() )
			return;
		else {
			List<ATermAppl>[] partitions = partition( candidates );
			partitionInstanceRetrieval( c, partitions, results );
		}
	}

	private void partitionInstanceRetrieval(ATermAppl c, List<ATermAppl>[] partitions,
			Collection<ATermAppl> results) {
		if( partitions[0].size() == 1 ) {
			ATermAppl i = partitions[0].get( 0 );
			binaryInstanceRetrieval( c, partitions[1], results );

			if( abox.isType( i, c ) )
				results.add( i );
		}
		else if( !abox.isType( partitions[0], c ) ) {
			binaryInstanceRetrieval( c, partitions[1], results );
		}
		else {
			if( !abox.isType( partitions[1], c ) ) {
				binaryInstanceRetrieval( c, partitions[0], results );
			}
			else {
				binaryInstanceRetrieval( c, partitions[0], results );
				binaryInstanceRetrieval( c, partitions[1], results );
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<ATermAppl>[] partition(List<ATermAppl> candidates) {
		List<ATermAppl>[] partitions = new List[2];
		int n = candidates.size();
		if( n <= 1 ) {
			partitions[0] = candidates;
			partitions[1] = new ArrayList<ATermAppl>();
		}
		else {
			partitions[0] = candidates.subList( 0, n / 2 );
			partitions[1] = candidates.subList( n / 2, n );
		}

		return partitions;
	}

	// private List binarySubClassRetrieval(ATermAppl c, List candidates) {
	// if(candidates.isEmpty())
	// return new ArrayList();
	// else{
	// List[] partitions = partition(candidates);
	// return partitionSubClassRetrieval(c, partitions);
	// }
	// }
	//	
	// private List partitionSubClassRetrieval(ATermAppl c, List[] partitions) {
	// if(partitions[0].size() == 1) {
	// ATermAppl d = (ATermAppl) partitions[0].get(0);
	// List l = binarySubClassRetrieval(c, partitions[1]);
	//
	// if(isSubclassOf(d, c))
	// l.add(d);
	//			
	// return l;
	// }
	// else if(!abox.isSubClassOf(partitions[0], c))
	// return binarySubClassRetrieval(c, partitions[1]);
	// else if(!abox.isSubClassOf(partitions[1], c))
	// return binarySubClassRetrieval(c, partitions[0]);
	// else {
	// List l1 = binarySubClassRetrieval(c, partitions[0]);
	// List l2 = binarySubClassRetrieval(c, partitions[1]);
	//			
	// l1.addAll(l2);
	//			
	// return l1;
	// }
	// }

	/**
	 * Print the class hierarchy on the standard output.
	 */
	public void printClassTree() {
		classify();

		new ClassTreePrinter().print( builder.getTaxonomy() );
	}

	public void printClassTree(PrintWriter out) {
		classify();

		new ClassTreePrinter().print( builder.getTaxonomy(), out );
	}

	public boolean doExplanation() {
		return abox.doExplanation();
	}

	/**
	 * @param doExplanation
	 *            The doExplanation to set.
	 */
	public void setDoExplanation(boolean doExplanation) {
		abox.setDoExplanation( doExplanation );
	}

	public String getExplanation() {
		return abox.getExplanation();
	}

	/**
	 * @deprecated Use setDoExplanation instead
	 */
	public void setDoDependencyAxioms(boolean doDepAxioms) {
		if( log.isLoggable( Level.FINER ) )
			log.finer( "Setting DoDependencyAxioms = " + doDepAxioms );
	}

	/**
	 * @deprecated Use getExplanation instead
	 */
	public boolean getDoDependencyAxioms() {
		return false;
	}

	public Set<ATermAppl> getExplanationSet() {
		return abox.getExplanationSet();
	}

	/**
	 * @param rbox
	 *            The rbox to set.
	 */
	public void setRBox(RBox rbox) {
		this.rbox = rbox;
	}

	/**
	 * @param tbox
	 *            The tbox to set.
	 */
	public void setTBox(TBox tbox) {
		this.tbox = tbox;
	}

	CompletionStrategy chooseStrategy(ABox abox) {
		return chooseStrategy( abox, getExpressivity() );
	}

	/**
	 * Choose a completion strategy based on the expressivity of the KB. The
	 * abox given is not necessarily the ABox that belongs to this KB but can be
	 * a derivative.
	 * 
	 * @return
	 */
	CompletionStrategy chooseStrategy(ABox abox, Expressivity expressivity) {
		boolean conceptSatisfiability = (abox.size() == 1)
				&& new IndividualIterator( abox ).next().isConceptRoot();
		
		// We don't need to use rules strategy if we are checking concept satisfiability unless
		// there are nominals because then rules may affect concept satisfiability and we need
		// to use rules strategy
		if( getRules().size() > 0 && (expressivity.hasNominal() || !conceptSatisfiability) ) {
			if( PelletOptions.USE_CONTINUOUS_RULES ) {
				return new ContinuousRulesStrategy( abox );
			}
			else {
				return new RuleStrategy( abox );
			}
		}
		
		boolean fullDatatypeReasoning = PelletOptions.USE_FULL_DATATYPE_REASONING
				&& (expressivity.hasCardinalityD() || expressivity.hasKeys());

		if( !fullDatatypeReasoning ) {
			if( conceptSatisfiability && !expressivity.hasNominal() ) {
				return new EmptySRIQStrategy( abox );
			}							
		}		

		return new SROIQStrategy( abox );
	}

	/**
	 * Set a timeout for the main timer. Used to stop an automated test after a
	 * reasonable amount of time has passed.
	 * 
	 * @param timeout
	 */
	public void setTimeout(long timeout) {
		timers.mainTimer.setTimeout( timeout );
	}

	/**
	 * @param term
	 * @return
	 */
	public Role getRole(ATerm term) {
		return rbox.getRole( term );
	}

	/**
	 * Get the classification results.
	 */
	public Taxonomy<ATermAppl> getTaxonomy() {
		classify();

		return builder.getTaxonomy();
	}

	public TaxonomyBuilder getTaxonomyBuilder() {
		if( builder == null ) {
			prepare();

			if( expChecker.getExpressivity().isEL() && !PelletOptions.DISABLE_EL_CLASSIFIER ) {
				builder = new SimplifiedELClassifier();
			}
			else {
				builder = new CDOptimizedTaxonomyBuilder();
			}
			builder.setKB( this );
			
			if (builderProgressMonitor != null) {
				builder.setProgressMonitor(builderProgressMonitor);
			}
		}

		return builder;
	}
	
	public void setTaxonomyBuilderProgressMonitor(ProgressMonitor progressMonitor) {
		builderProgressMonitor = progressMonitor;
		
		if (builder != null) {
			builder.setProgressMonitor( progressMonitor );
		}
	}

	public Taxonomy<ATermAppl> getRoleTaxonomy(boolean objectTaxonomy) {
		prepare();
		
		return objectTaxonomy
			? rbox.getObjectTaxonomy()
			: rbox.getDataTaxonomy();
		
	}
	
	public Taxonomy<ATermAppl> getRoleTaxonomy(ATermAppl r) {
		prepare();
		
		if (isObjectProperty(r)) {
			return rbox.getObjectTaxonomy();
		}
		else if (isDatatypeProperty(r)) {
			return rbox.getDataTaxonomy();
		}
		else if (isAnnotationProperty(r)) {
			return rbox.getAnnotationTaxonomy();
		}
		
		return null;
	}

	public SizeEstimate getSizeEstimate() {
		return estimate;
	}

	/**
	 * Add a rule to the KB.
	 */
	public boolean addRule(Rule rule) {
		// DL-safe rules affects the ABox so we might redo the reasoning
		changes.add( ChangeType.ABOX_ADD );

		rules.put( rule, normalize( rule ) );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "rule " + rule );

		return true;
	}

	private Rule normalize(Rule rule) {
		if( !UsableRuleFilter.isUsable( rule ) ) {
			return null;
		}

		Set<RuleAtom> head = new LinkedHashSet<RuleAtom>();
		Set<RuleAtom> body = new LinkedHashSet<RuleAtom>();

		for( RuleAtom atom : rule.getHead() ) {
			if( atom instanceof ClassAtom ) {
				ClassAtom ca = (ClassAtom) atom;
				AtomIObject arg = ca.getArgument();
				ATermAppl c = ca.getPredicate();
				ATermAppl normC = ATermUtils.normalize( c );
				if( c != normC )
					atom = new ClassAtom( normC, arg );
			}
			head.add( atom );
		}

		Map<AtomIObject, Set<ATermAppl>> types = new HashMap<AtomIObject, Set<ATermAppl>>();

		for( RuleAtom atom : rule.getBody() ) {
			if( atom instanceof IndividualPropertyAtom ) {
				IndividualPropertyAtom propAtom = (IndividualPropertyAtom) atom;
				ATermAppl prop = propAtom.getPredicate();

				AtomIObject subj = propAtom.getArgument1();
				if( subj instanceof AtomIVariable ) {
					Set<ATermAppl> domains = getRole( prop ).getDomains();
					if( domains != null )
						MultiMapUtils.addAll( types, subj, domains );
				}

				AtomIObject obj = propAtom.getArgument2();
				if( obj instanceof AtomIVariable ) {
					Set<ATermAppl> ranges = getRole( prop ).getRanges();
					if( ranges != null )
						MultiMapUtils.addAll( types, obj, ranges );
				}
			}
		}

		for( RuleAtom atom : rule.getBody() ) {
			if( atom instanceof ClassAtom ) {
				ClassAtom ca = (ClassAtom) atom;
				AtomIObject arg = ca.getArgument();
				ATermAppl c = ca.getPredicate();
				ATermAppl normC = ATermUtils.normalize( c );
				if( MultiMapUtils.contains( types, arg, normC ) )
					continue;
				else if( c != normC )
					atom = new ClassAtom( normC, ca.getArgument() );
			}
			body.add( atom );
		}

		return new Rule( rule.getName(), head, body );
	}

	/**
	 * Return all the asserted rules.
	 */
	public Set<Rule> getRules() {
		return rules.keySet();
	}

	/**
	 * Return the asserted rules with their normalized form. A normalized rule
	 * is a rule where any class expression occurring in the rules is in
	 * normalized form.
	 * 
	 * @return set of rules where
	 */
	public Map<Rule,Rule> getNormalizedRules() {
		return rules;
	}

	/**
	 * Check if we can use incremental consistency checking
	 * 
	 * @return
	 */
	protected boolean canUseIncConsistency() {
		// can we do incremental consistency checking
		Expressivity expressivity = expChecker.getExpressivity();
		if( expressivity == null )
			return false;

		boolean canUseIncConsistency = 
				!(expressivity.hasNominal() && expressivity.hasInverse())
				&& getRules().isEmpty()
				&& !isTBoxChanged() && !isRBoxChanged() && abox.isComplete()
				&& PelletOptions.USE_INCREMENTAL_CONSISTENCY &&
				// support additions only; also support deletions with or with
				// additions, however tracing must be on to support incremental
				// deletions
				(!changes.contains( ChangeType.ABOX_DEL ) || PelletOptions.USE_INCREMENTAL_DELETION);

		return canUseIncConsistency;
	}

	public void ensureIncConsistency(boolean aboxDeletion) {
		if( canUseIncConsistency() )
			return;

		Expressivity expressivity = expChecker.getExpressivity();

		String msg = "ABox " + (aboxDeletion
			? "deletion"
			: "addition") + " failed because ";
		if( expressivity == null )
			msg += "an initial consistency check has not been performed on this KB";
		else if( expressivity.hasNominal() )
			msg += "KB has nominals";
		else if( expressivity.hasInverse() )
			msg += "KB has inverse properties";
		else if( isTBoxChanged() )
			msg += "TBox changed";
		else if( isRBoxChanged() )
			msg += "RBox changed";
		else if( PelletOptions.USE_INCREMENTAL_CONSISTENCY )
			msg += "configuration option USE_INCREMENTAL_CONSISTENCY is not enabled";
		else if( aboxDeletion )
			msg += "configuration option USE_INCREMENTAL_DELETION is not enabled";
		else
			msg += "of an unknown reason";

		throw new UnsupportedOperationException( msg );
	}

	/**
	 * Get the dependency index for syntactic assertions in this kb
	 * 
	 * @return
	 */
	public DependencyIndex getDependencyIndex() {
		return dependencyIndex;
	}

	/**
	 * Get syntactic assertions in the kb
	 * 
	 * @return
	 */
	public Set<ATermAppl> getSyntacticAssertions() {
		return syntacticAssertions;
	}

	protected static void handleUndefinedEntity(String s) {
		if( !PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING )
			throw new UndefinedEntityException( s );
	}

	public Set<ATermAppl> getABoxAssertions(AssertionType assertionType) {
		Set<ATermAppl> assertions = aboxAssertions.get( assertionType );

		if( assertions == null )
			return Collections.emptySet();
		else
			return Collections.unmodifiableSet( assertions );
	}

	/**
	 * @deprecated Use
	 *             {@link #getABoxAssertions(org.mindswap.pellet.KnowledgeBase.AssertionType)}
	 *             instead
	 */
	public Set<ATermAppl> getAboxMembershipAssertions() {
		return getABoxAssertions( AssertionType.TYPE );
	}

	/**
	 * @deprecated Use
	 *             {@link #getABoxAssertions(org.mindswap.pellet.KnowledgeBase.AssertionType)}
	 *             instead
	 */
	public Set<ATermAppl> getAboxObjectRoleAssertions() {
		return getABoxAssertions( AssertionType.OBJ_ROLE );
	}

	/**
	 * @deprecated Use
	 *             {@link #getABoxAssertions(org.mindswap.pellet.KnowledgeBase.AssertionType)}
	 *             instead
	 */
	public Set<ATermAppl> getAboxDataRoleAssertions() {
		return getABoxAssertions( AssertionType.DATA_ROLE );
	}

	/**
	 * @return the deletedAssertions
	 */
	public Set<ATermAppl> getDeletedAssertions() {
		return deletedAssertions;
	}

	/**
	 * Returns current value of explainOnlyInconsistency option.
	 * 
	 * @see #setExplainOnlyInconsistency(boolean)
	 * @return current value of explainOnlyInconsistency option
	 */
	public boolean isExplainOnlyInconsistency() {
		return explainOnlyInconsistency;
	}

	/**
	 * Controls what kind of explanations can be generated using this KB. With
	 * this option enabled explanations for inconsistent ontologies will be
	 * returned. But if the ontology is consistent, it will not be possible to
	 * retrieve explanations for inferences about instances. This option is
	 * disabled by default. It should be turned on if explanations are only
	 * needed for inconsistencies but not other inferences. Turning this option
	 * on improves the performance of consistency checking for consistent
	 * ontologies.
	 * 
	 * @param explainOnlyInconsistency
	 *            new value for explainOnlyInconsistency option
	 */
	public void setExplainOnlyInconsistency(boolean explainOnlyInconsistency) {
		this.explainOnlyInconsistency = explainOnlyInconsistency;
	}

}
