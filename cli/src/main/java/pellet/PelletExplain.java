// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.NONE;
import static pellet.PelletCmdOptionArg.REQUIRED;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.GlassBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.owlapi.explanation.MultipleExplanationGenerator;
import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;
import com.clarkparsia.owlapi.explanation.TransactionAwareSingleExpGen;
import com.clarkparsia.owlapi.explanation.io.ExplanationRenderer;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import com.clarkparsia.owlapi.explanation.util.ExplanationProgressMonitor;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 * @author Markus Stocker
 */
public class PelletExplain extends PelletCmdApp {
	private SatisfiabilityConverter			converter;
	/**
	 * inferences for which there was an error while generating the explanation
	 */
	private int								errorExpCount		= 0;
	private OWLAPILoader					loader;
	private int								maxExplanations		= 1;
	private boolean							useBlackBox			= false;
	private ProgressMonitor monitor;
	/**
	 * inferences whose explanation contains more than on axiom
	 */
	private int								multiAxiomExpCount	= 0;
	/**
	 * inferences with multiple explanations
	 */
	private int								multipleExpCount	= 0;

	private PelletReasoner					reasoner;
	private OWLEntity						name1;
	private OWLEntity						name2;
	private OWLObject						name3;

	public PelletExplain() {
		GlassBoxExplanation.setup();
	}

	@Override
    public String getAppId() {
		return "PelletExplain: Explains one or more inferences in a given ontology including ontology inconsistency";
	}

	@Override
    public String getAppCmd() {
		return "pellet explain " + getMandatoryOptions() + "[options] <file URI>...\n\n"
				+ "The options --unsat, --all-unsat, --inconsistent, --subclass, \n"
				+ "--hierarchy, and --instance are mutually exclusive. By default \n "
				+ "--inconsistent option is assumed. In the following descriptions \n"
				+ "C, D, and i can be URIs or local names.";
	}

	@Override
    public PelletCmdOptions getOptions() {
		PelletCmdOptions options = getGlobalOptions();

		options.add( getIgnoreImportsOption() );
		
		PelletCmdOption option = new PelletCmdOption( "unsat" );
		option.setType( "C" );
		option.setDescription( "Explain why the given class is unsatisfiable" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "all-unsat" );
		option.setDescription( "Explain all unsatisfiable classes" );
		option.setDefaultValue( false );
		option.setIsMandatory( false );
		option.setArg( NONE );
		options.add( option );

		option = new PelletCmdOption( "inconsistent" );
		option.setDescription( "Explain why the ontology is inconsistent" );
		option.setDefaultValue( false );
		option.setIsMandatory( false );
		option.setArg( NONE );
		options.add( option );

		option = new PelletCmdOption( "hierarchy" );
		option.setDescription( "Print all explanations for the class hierarchy" );
		option.setDefaultValue( false );
		option.setIsMandatory( false );
		option.setArg( NONE );
		options.add( option );

		option = new PelletCmdOption( "subclass" );
		option.setDescription( "Explain why C is a subclass of D" );
		option.setType( "C,D" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "instance" );
		option.setDescription( "Explain why i is an instance of C" );
		option.setType( "i,C" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "property-value" );
		option.setDescription( "Explain why s has value o for property p" );
		option.setType( "s,p,o" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "method" );
		option.setShortOption( "m" );
		option.setType( "glass | black" );
		option.setDescription( "Method that will be used to generate explanations" );
		option.setDefaultValue( "glass" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "max" );
		option.setShortOption( "x" );
		option.setType( "positive integer" );
		option.setDescription( "Maximum number of generated explanations for each inference" );
		option.setDefaultValue( 1 );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		option = options.getOption( "verbose" );
		option.setDescription( "Print detailed exceptions and messages about the progress" );
		
		return options;
	}

	@Override
    public void parseArgs(String[] args) {
		super.parseArgs( args );
		
		maxExplanations = options.getOption( "max" ).getValueAsNonNegativeInteger();

		loader = (OWLAPILoader) getLoader( "OWLAPIv3" );

		getKB();
		
		converter = new SatisfiabilityConverter( loader.getManager().getOWLDataFactory() );

		reasoner = loader.getReasoner();
		
		loadMethod();
		
	    loadNames();
	}
	
	@Override
    public void run() {
		try {

			if( name1 == null ) {
				// Option --hierarchy
				verbose( "Explain all the subclass relations in the ontology" );
				explainClassHierarchy();
			}
			else if( name2 == null ) {
				if( ((OWLClassExpression) name1).isOWLNothing() ) {
					// Option --all-unsat
					verbose( "Explain all the unsatisfiable classes" );
					explainUnsatisfiableClasses();
				}
				else {
					// Option --inconsistent && --unsat C
					verbose( "Explain unsatisfiability of " + name1 );
					explainUnsatisfiableClass( (OWLClass) name1 );
				}
			}
			else if( name3 != null ) {
				// Option --property-value s,p,o
				verbose( "Explain property assertion " + name1 + " and " + name2  + " and " + name3 );

				explainPropertyValue( (OWLIndividual) name1, (OWLProperty<?,?>) name2, name3 );
			}
			else if( name1.isOWLClass() && name2.isOWLClass() ) {
				// Option --subclass C,D
				verbose( "Explain subclass relation between " + name1 + " and " + name2 );

				explainSubClass( (OWLClass) name1, (OWLClass) name2 );
			}
			else if( name1.isOWLNamedIndividual() && name2.isOWLClass() ) {
				// Option --instance i,C
				verbose( "Explain instance relation between " + name1 + " and " + name2 );

				explainInstance( (OWLIndividual) name1, (OWLClass) name2 );
			}

			printStatistics();
		} catch( OWLException e ) {
			throw new RuntimeException( e );
		}
	}

	private void explainAxiom(OWLAxiom axiom) throws OWLException {

		MultipleExplanationGenerator expGen = new HSTExplanationGenerator(getSingleExplanationGenerator());
		RendererExplanationProgressMonitor rendererMonitor = new RendererExplanationProgressMonitor(axiom);
		expGen.setProgressMonitor(rendererMonitor);

		OWLClassExpression unsatClass = converter.convert( axiom );
		Timer timer = timers.startTimer("explain");
		Set<Set<OWLAxiom>> explanations = expGen.getExplanations( unsatClass, maxExplanations );
		timer.stop();

		if (explanations.isEmpty()) {
			rendererMonitor.foundNoExplanations();
		}
		
		if( timer.getCount() % 10 == 0 ) {
			// printStatistics();
		}

		int expSize = explanations.size();
		if( expSize == 0 ) {
	        errorExpCount++;
        }
        else if( expSize == 1 ) {
			if( explanations.iterator().next().size() > 1 ) {
	            multiAxiomExpCount++;
			// else
			// return;
            }
		}
        else {
	        multipleExpCount++;
        }
	}

	public void explainClassHierarchy() throws OWLException {
		Set<OWLClass> visited = new HashSet<OWLClass>();

		reasoner.flush();
		
		startTask( "Classification" );		
		reasoner.getKB().classify();
		finishTask( "Classification" );

		startTask( "Realization" );
		reasoner.getKB().realize();
		finishTask( "Realization" );		

		monitor = new ConsoleProgressMonitor();
		monitor.setProgressTitle( "Explaining" );
		monitor.setProgressLength( reasoner.getRootOntology().getClassesInSignature().size() );
		monitor.taskStarted();

		Node<OWLClass> bottoms = reasoner.getEquivalentClasses( OWL.Nothing );
		explainClassHierarchy( OWL.Nothing, bottoms, visited );

		Node<OWLClass> tops = reasoner.getEquivalentClasses( OWL.Thing );
		explainClassHierarchy( OWL.Thing, tops, visited );

		monitor.taskFinished();
	}

	public void explainEquivalentClass(OWLClass c1, OWLClass c2) throws OWLException {
		if( c1.equals( c2 ) ) {
	        return;
        }

		OWLAxiom axiom = OWL.equivalentClasses( c1, c2 );

		explainAxiom( axiom );
	}

	public void explainInstance(OWLIndividual ind, OWLClass c) throws OWLException {
		if( c.isOWLThing() ) {
	        return;
        }

		OWLAxiom axiom = OWL.classAssertion( ind, c );

		explainAxiom( axiom );
	}

	// In the following method(s) we intentionally do not use OWLPropertyExpression<?,?>
	// because of a bug in some Sun's implementation of javac
	// http://bugs.sun.com/view_bug.do?bug_id=6548436
	// Since lack of generic type generates a warning, we suppress it
	@SuppressWarnings("unchecked")
    public void explainPropertyValue(OWLIndividual s, OWLProperty p, OWLObject o) throws OWLException {
		if( p.isOWLObjectProperty() ) {
	        explainAxiom( OWL.propertyAssertion( s, (OWLObjectProperty) p, (OWLIndividual) o ) );
        }
        else {
	        explainAxiom( OWL.propertyAssertion( s, (OWLDataProperty) p, (OWLLiteral) o ) );
        }
	}
	
	public void explainSubClass(OWLClass sub, OWLClass sup) throws OWLException {
		if( sub.equals( sup ) ) {
	        return;
        }

		if( sub.isOWLNothing() ) {
	        return;
        }

		if( sup.isOWLThing() ) {
	        return;
        }

		OWLSubClassOfAxiom axiom = OWL.subClassOf( sub, sup );
		explainAxiom( axiom );
	}

	public void explainUnsatisfiableClasses() throws OWLException {
		for( OWLClass cls : reasoner.getEquivalentClasses( OWL.Nothing ) ) {
			if( cls.isOWLNothing() ) {
				continue;
			}

			explainUnsatisfiableClass( cls );
		}
	}

	public void explainUnsatisfiableClass(OWLClass cls) throws OWLException {
		explainSubClass( cls, OWL.Nothing );
	}

	private void explainClassHierarchy(OWLClass cls, Node<OWLClass> eqClasses, Set<OWLClass> visited)
			throws OWLException {
		if( visited.contains( cls ) ) {
	        return;
        }

		visited.add( cls );
		visited.addAll( eqClasses.getEntities() );

		for( OWLClass eqClass : eqClasses ) {
			monitor.incrementProgress();

			explainEquivalentClass( cls, eqClass );
		}

		for( OWLNamedIndividual ind : reasoner.getInstances( cls, true ).getFlattened() ) {
	        explainInstance( ind, cls );
        }

		NodeSet<OWLClass> subClasses = reasoner.getSubClasses( cls, true );
		Map<OWLClass, Node<OWLClass>> subClassEqs = new HashMap<OWLClass, Node<OWLClass>>();
		for( Node<OWLClass> equivalenceSet : subClasses ) {
			if( equivalenceSet.isBottomNode() ) {
	            continue;
            }

			OWLClass subClass = equivalenceSet.getRepresentativeElement();
			subClassEqs.put( subClass, equivalenceSet );
			explainSubClass( subClass, cls );
		}

		for( Map.Entry<OWLClass, Node<OWLClass>> entry : subClassEqs.entrySet() ) {
	        explainClassHierarchy( entry.getKey(), entry.getValue(), visited );
        }
	}

	private TransactionAwareSingleExpGen getSingleExplanationGenerator() {
		if( useBlackBox ) {
			if ( options.getOption( "inconsistent" ) != null ) {
				if( !options.getOption( "inconsistent" ).getValueAsBoolean() ) {
					return new BlackBoxExplanation( reasoner.getRootOntology(), PelletReasonerFactory.getInstance(), reasoner );
				} else {
					output( "WARNING: black method cannot be used to explain inconsistency. Switching to glass." );
					return new GlassBoxExplanation( reasoner );	
				}
			} else {
				return new BlackBoxExplanation( reasoner.getRootOntology(), PelletReasonerFactory.getInstance(), reasoner );
			}
		} else {
			return new GlassBoxExplanation( reasoner );
		}
	}
	
	private void loadMethod() {
		String method = options.getOption( "method" ).getValueAsString();

		if( method.equalsIgnoreCase( "black" ) ) {
			useBlackBox = true;
		} else if( method.equalsIgnoreCase( "glass" ) ) {
			useBlackBox = false;
		} else {
			throw new PelletCmdException( "Unrecognized method: " + method );
		}
	}

	private void loadNames() {
		PelletCmdOption option;
		
		name1 = name2 = null;
		name3 = null;

		if( (option = options.getOption( "hierarchy" )) != null ) {
			if( option.getValueAsBoolean() ) {
				return;
			}
		}

		if( (option = options.getOption( "all-unsat" )) != null ) {
			if( option.getValueAsBoolean() ) {
				name1 = OWL.Nothing;
				return;
			}
		}

		if( (option = options.getOption( "inconsistent" )) != null ) {
			if( option.getValueAsBoolean() ) {
				if( useBlackBox ) {
	                throw new PelletCmdException("Black box method cannot be used to explain ontology inconsistency");
                }
				name1 = OWL.Thing;
				return;
			}
		}

		if( (option = options.getOption( "unsat" )) != null ) {
			String unsatisfiable = option.getValueAsString();
			if( unsatisfiable != null ) {
				name1 = OntologyUtils.findEntity( unsatisfiable, loader.getAllOntologies() );

				if( name1 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + unsatisfiable );
                }
                else if( !name1.isOWLClass() ) {
	                throw new PelletCmdException( "Not a defined class: " + unsatisfiable );
                }
                else if( name1.isTopEntity() && useBlackBox) {
    				throw new PelletCmdException("Black box method cannot be used to explain unsatisfiability of owl:Thing");
                }

				return;
			}
		}

		if( (option = options.getOption( "subclass" )) != null ) {
			String subclass = option.getValueAsString();
			if( subclass != null ) {
				String[] names = subclass.split( "," );
				if( names.length != 2 ) {
	                throw new PelletCmdException(
							"Invalid format for subclass option: " + subclass );
                }

				name1 = OntologyUtils.findEntity( names[0], loader.getAllOntologies() );
				name2 = OntologyUtils.findEntity( names[1], loader.getAllOntologies() );

				if( name1 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + names[0] );
                }
                else if( !name1.isOWLClass() ) {
	                throw new PelletCmdException( "Not a defined class: " + names[0] );
                }
				if( name2 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + names[1] );
                }
                else if( !name2.isOWLClass() ) {
	                throw new PelletCmdException( "Not a defined class: " + names[1] );
                }
				return;
			}
		}

		if( (option = options.getOption( "instance" )) != null ) {
			String instance = option.getValueAsString();
			if( instance != null ) {
				String[] names = instance.split( "," );
				if( names.length != 2 ) {
	                throw new PelletCmdException( "Invalid format for instance option: " + instance );
                }

				name1 = OntologyUtils.findEntity( names[0], loader.getAllOntologies() );
				name2 = OntologyUtils.findEntity( names[1], loader.getAllOntologies() );

				if( name1 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + names[0] );
                }
                else if( !name1.isOWLNamedIndividual() ) {
	                throw new PelletCmdException( "Not a defined individual: " + names[0] );
                }
				if( name2 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + names[1] );
                }
                else if( !name2.isOWLClass() ) {
	                throw new PelletCmdException( "Not a defined class: " + names[1] );
                }

				return;
			}
		}

		if( (option = options.getOption( "property-value" )) != null ) {
			String optionValue = option.getValueAsString();
			if( optionValue != null ) {
				String[] names = optionValue.split( "," );
				if( names.length != 3 ) {
	                throw new PelletCmdException( "Invalid format for property-value option: " + optionValue );
                }

				name1 = OntologyUtils.findEntity( names[0], loader.getAllOntologies() );
				name2 = OntologyUtils.findEntity( names[1], loader.getAllOntologies() );

				if( name1 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + names[0] );
                }
                else if( !name1.isOWLNamedIndividual() ) {
	                throw new PelletCmdException( "Not an individual: " + names[0] );
                }
				if( name2 == null ) {
	                throw new PelletCmdException( "Undefined entity: " + names[1] );
                }
                else if( !name2.isOWLObjectProperty() && !name2.isOWLDataProperty() ) {
	                throw new PelletCmdException( "Not a defined property: " + names[1] );
                }
				if( name2.isOWLObjectProperty() ) {
					name3 = OntologyUtils.findEntity( names[2], loader.getAllOntologies() );
					if( name3 == null ) {
	                    throw new PelletCmdException( "Undefined entity: " + names[2] );
                    }
                    else if( !(name3 instanceof OWLIndividual) ) {
	                    throw new PelletCmdException( "Not a defined individual: " + names[2] );
                    }
				}
				else {
					ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
							loader.getManager().getOWLDataFactory(), names[2] );
					try {
						name3 = parser.parseConstant();
					} catch( ParserException e ) {
						throw new PelletCmdException( "Not a valid literal: " + names[2] );
					}
				}

				return;
			}
		}

		// Per default we explain why the ontology is inconsistent
		name1 = OWL.Thing;
		if( useBlackBox ) {
            throw new PelletCmdException("Black box method cannot be used to explain ontology inconsistency");
        }
		
		return;
	}

	private void printStatistics() throws OWLException {
		if(!verbose) {
	        return;
        }
		
		Timer timer = timers.getTimer( "explain" );
		if( timer != null ) {
			verbose( "Subclass relations   : " + timer.getCount() );
			verbose( "Multiple explanations: " + multipleExpCount );
			verbose( "Single explanation     " );
			verbose( " with multiple axioms: " + multiAxiomExpCount );
			verbose( "Error explaining     : " + errorExpCount );
			verbose( "Average time         : " + timer.getAverage() + "ms" );
		}
	}

	private class RendererExplanationProgressMonitor implements ExplanationProgressMonitor {

		private ExplanationRenderer rend = new ManchesterSyntaxExplanationRenderer();
		private OWLAxiom axiom;
		private Set<Set<OWLAxiom>> setExplanations;
		private PrintWriter pw;

		private RendererExplanationProgressMonitor(OWLAxiom axiom) {
			this.axiom = axiom;
			this.pw = new PrintWriter(System.out);
			
			setExplanations = new HashSet<Set<OWLAxiom>>();
			try {
				rend.startRendering(pw);
			}
			catch (OWLException e) {
				System.err.println("Error rendering explanation: " + e);
			}
			catch (IOException e) {
				System.err.println("Error rendering explanation: " + e);
			}
		}

		public void foundExplanation(Set<OWLAxiom> axioms) {

			if (!setExplanations.contains(axioms)) {
				setExplanations.add(axioms);
				pw.flush();
				try {
					rend.render(axiom, Collections.singleton(axioms));
				}
				catch (IOException e) {
					System.err.println("Error rendering explanation: " + e);
				}
				catch (OWLException e) {
					System.err.println("Error rendering explanation: " + e);
				}
			}
		}

		public boolean isCancelled() {
			return false;
		}

		public void foundAllExplanations() {
			try {
				rend.endRendering();
			}
			catch (OWLException e) {
				System.err.println("Error rendering explanation: " + e);
			}
			catch (IOException e) {
				System.err.println("Error rendering explanation: " + e);
			}
		}
		
		public void foundNoExplanations() {
			try {
				rend.render(axiom, Collections.<Set<OWLAxiom>>emptySet());
				rend.endRendering();
			}
			catch (OWLException e) {
				System.err.println("Error rendering explanation: " + e);
			}
			catch (IOException e) {
				System.err.println("Error rendering explanation: " + e);
			}
		}
	}
}
