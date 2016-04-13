// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.modularity.OntologyDiff;
import com.clarkparsia.modularity.IncremantalReasonerFactory;
import com.clarkparsia.owlapi.explanation.GlassBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.owlapi.explanation.MultipleExplanationGenerator;
import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;
import com.clarkparsia.owlapi.explanation.io.ConciseExplanationRenderer;
import com.clarkparsia.owlapi.explanation.io.ExplanationRenderer;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.mindswap.pellet.utils.DurationFormat;
import org.mindswap.pellet.utils.MemUtils;
import org.mindswap.pellet.utils.Timer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.SatisfiabilityReducer;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author Evren Sirin
 */
public class ProfileIncremental {
	public static final Logger log = Logger.getLogger( ProfileIncremental.class.getName() );

	private static Timer timer = new Timer();

	private static Random RANDOM = new Random();

	public final static void main(String[] args) throws Exception  {
	    File initFile = new File(args[0]);
	    File finalFile = new File(args[1]);
		int updates = Integer.parseInt(args[2]);

		OWLOntologyManager manager = OWL.manager;

		PelletExplanation.setup();

		timer.start();

//	    OWLOntology finalOnt = OntologyUtils.loadOntology(finalFile.toURI().toString(), false);
//	    Set<OWLAxiom> finalAxioms = Sets.newHashSet(finalOnt.getAxioms());
//	    manager.removeOntology(finalOnt);

	    println("Loaded final ont");

		OWLOntology initOnt = OntologyUtils.loadOntology(initFile.toURI().toString(), false);

		println("Loaded init ont");

//	    OntologyDiff diff = OntologyDiff.diffAxioms(initOnt.getAxioms(), finalAxioms);

//		println("Computed diff " + diff.getDiffCount());

	    IncrementalReasoner reasoner;
		File classificationFile = new File(initFile.getName() + ".zip");
		if (classificationFile.exists()) {
			reasoner = IncrementalReasoner.config().file(classificationFile).createIncrementalReasoner(initOnt);
	    }
	    else {
		    reasoner = IncremantalReasonerFactory.getInstance().createReasoner(initOnt);
		    reasoner.classify();
			reasoner.save(classificationFile);
		}

		println("Created incremental reasoner");

//	    int addSize = diff.getAdditions().size() / updates;
//	    int removeSize = diff.getAdditions().size() / updates;
//	    Iterator<OWLAxiom> additions = diff.getAdditions().iterator();
//	    Iterator<OWLAxiom> deletions = diff.getDeletions().iterator();
//
		timer.reset();
//
		boolean copyReasoner = false;
//		boolean disposeCopy = false && copyReasoner;
		int explanationCount = 2;
//
//		List<IncrementalReasoner> reasoners = Lists.newArrayList();
		SatisfiabilityConverter converter = new SatisfiabilityConverter(manager.getOWLDataFactory());
		ExplanationRenderer renderer = new ConciseExplanationRenderer();
//
	    for (int i = 1; i <= updates; i++) {
		    IncrementalReasoner targetReasoner = copyReasoner ? reasoner.copy() : reasoner;
//		    OWLOntology targetOnt = copyReasoner ? targetReasoner.getRootOntology() : initOnt;
//
//		    List<OWLOntologyChange> changes = Lists.newArrayList();
//		    Iterators.addAll(changes, Iterators.transform(Iterators.limit(additions, addSize), toUpdate(targetOnt, true)));
//		    Iterators.addAll(changes, Iterators.transform(Iterators.limit(deletions, removeSize), toUpdate(targetOnt, false)));
//
//		    timer.start();
//		    OWL.manager.applyChanges(changes);
//		    targetReasoner.classify();
//		    timer.stop();

		    PelletExplanation explanation = null;
		    if (explanationCount > 0) {
			    println("Before explanation " + i + " " + DurationFormat.LONG.format(timer.getLast()));

			    explanation = new PelletExplanation(targetReasoner.getReasoner());

			    manager.removeOntologyChangeListener(targetReasoner);

			    for (int j = 1; j <= explanationCount; j++) {
				    OWLAxiom axiom = selectRandomInference(targetReasoner);
				    try {
					    timer.start();
					    Set<Set<OWLAxiom>> explanations = explanation.getEntailmentExplanations(axiom, explanationCount);

					    renderer.startRendering(new PrintWriter(System.out));
					    renderer.render(axiom, explanations);
					    renderer.endRendering();
					    timer.stop();

					    println("Explanation " + j + " " + DurationFormat.LONG.format(timer.getLast()));
				    }
				    catch (Exception e) {
					    System.err.println("Error explaining " + axiom);
					    e.printStackTrace();
				    }
			    }
			    manager.addOntologyChangeListener(targetReasoner);
		    }

//		    if (disposeCopy) {
//                targetReasoner.dispose();
//		        OWL.manager.removeOntology(targetOnt);
//			    if (explanation != null) {
//				    explanation.dispose();
//			    }
//		    }
//		    else if (copyReasoner) {
//			    reasoners.add(targetReasoner);
//		    }
//
		    println("Iteration " + i + " " + DurationFormat.LONG.format(timer.getLast()));
	    }

		println("Finished " + DurationFormat.LONG.format((long) timer.getAverage()));
	}

	private static OWLAxiom selectRandomInference(OWLReasoner reasoner) {
		OWLOntology ont = reasoner.getRootOntology();
		OWLClass cls = selectRandom(ont.getClassesInSignature());
		OWLClass superCls = selectRandom(reasoner.getSuperClasses(cls, false).getNodes()).getRepresentativeElement();
		return ont.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(cls, superCls);
	}

	private static <T> T selectRandom(Collection<T> coll) {
		return Iterables.get(coll, RANDOM.nextInt(coll.size()));
	}

	private static void println(String msg) {
		System.out.print(timer.format() + " " + msg + " ");System.out.println(MemUtils.mb(MemUtils.usedMemoryAfterGC()));
	}

	private static Function<OWLAxiom, OWLOntologyChange> toUpdate(final OWLOntology ont, final boolean add) {
		return new Function<OWLAxiom, OWLOntologyChange>() {
			@Nullable
			@Override
			public OWLOntologyChange apply(final OWLAxiom axiom) {
				return add ? new AddAxiom(ont, axiom) : new RemoveAxiom(ont, axiom);
			}
		};
	}
}
