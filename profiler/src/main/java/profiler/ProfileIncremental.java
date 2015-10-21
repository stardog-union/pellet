// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.OntologyDiff;
import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.mindswap.pellet.utils.DurationFormat;
import org.mindswap.pellet.utils.Timer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author Evren Sirin
 */
public class ProfileIncremental {
	public static final Logger log = Logger.getLogger( ProfileIncremental.class.getName() );

	private static Timer timer = new Timer();


	public final static void main(String[] args) throws Exception  {
	    File initFile = new File(args[0]);
	    File finalFile = new File(args[1]);
		int updates = Integer.parseInt(args[2]);

		OWLOntologyManager manager = OWL.manager;

		timer.start();

	    OWLOntology finalOnt = OntologyUtils.loadOntology(finalFile.toURI().toString(), false);
	    Set<OWLAxiom> finalAxioms = Sets.newHashSet(finalOnt.getAxioms());
	    manager.removeOntology(finalOnt);

	    println("Loaded final ont");

		OWLOntology initOnt = OntologyUtils.loadOntology(initFile.toURI().toString(), false);

		println("Loaded init ont");

	    OntologyDiff diff = OntologyDiff.diffAxioms(initOnt.getAxioms(), finalAxioms);

		println("Computed diff " + diff.getDiffCount());

	    IncrementalClassifier classifier;
		File classificationFile = new File(initFile.getName() + ".zip");
		if (classificationFile.exists()) {
		    classifier = IncrementalClassifierPersistence.load(new FileInputStream(classificationFile), initOnt);
	    }
	    else {
		    classifier = new IncrementalClassifier(initOnt);
		    classifier.classify();
		    IncrementalClassifierPersistence.save(classifier, new FileOutputStream(classificationFile));
	    }

		println("Created incremental classifier");

	    int addSize = diff.getAdditions().size() / updates;
	    int removeSize = diff.getAdditions().size() / updates;
	    Iterator<OWLAxiom> additions = diff.getAdditions().iterator();
	    Iterator<OWLAxiom> deletions = diff.getDeletions().iterator();

		timer.reset();

	    for (int i = 0; i < updates; i++) {
		    List<OWLOntologyChange> changes = Lists.newArrayList();
		    Iterators.addAll(changes, Iterators.transform(Iterators.limit(additions, addSize), toUpdate(initOnt, true)));
		    Iterators.addAll(changes, Iterators.transform(Iterators.limit(deletions, removeSize), toUpdate(initOnt, false)));

		    timer.start();
		    OWL.manager.applyChanges(changes);
		    classifier.classify();
		    timer.stop();

		    println("Iteration " + (i + 1) + " " + DurationFormat.LONG.format(timer.getLast()));
	    }

		println("Finished " + DurationFormat.LONG.format((long) timer.getAverage()));
	}

	private static void println(String msg) {
		System.out.println(timer.format() + " " + msg);
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
