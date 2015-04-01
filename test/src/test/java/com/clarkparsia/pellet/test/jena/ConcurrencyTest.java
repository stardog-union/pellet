/**
 * 
 */
package com.clarkparsia.pellet.test.jena;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Verifies that Pellet doesn't throw any exceptions when doing concurrent ABox
 * queries provided that classification and realization are down synchronously
 * before.
 * 
 * @author Pavel Klinov
 *
 */
public class ConcurrencyTest {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(ConcurrencyTest.class);
	
	private static final String ONTOLOGY_PATH_ = "/test/data/concurrency/vicodi.ttl";
	
	private static final int THREAD_NUMBER = 10;
	
	@Test
	public void concurrentDLQueries() throws Exception {
		// create Pellet reasoner
        //Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		OntModel ontModel = loadOntologyModel(ONTOLOGY_PATH_);
		
		// force classification and realization
		((PelletInfGraph) ontModel.getGraph()).realize();
		
		// launching threads which will concurrently run type queries on instances
		ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUMBER);
		
		for (int i = 0; i < THREAD_NUMBER; i++) {
			pool.execute(new QueryRunner(ontModel));
		}
		
		pool.shutdown();
		pool.awaitTermination(100, TimeUnit.SECONDS);
	}

	private OntModel loadOntologyModel(String ontologyPath) {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
        
        // read the file
        InputStream ontStream = ConcurrencyTest.class.getResourceAsStream(ontologyPath);
        
        model.read( ontStream, null, "TTL" );
        
		return model;
	}
	

	private static class QueryRunner implements Runnable {

		private final OntModel model_;
		
		QueryRunner(OntModel model) {
			model_ = model;
		}
		
		@Override
		public void run() {
			// listing all individuals
			Iterator<Individual> indIter = model_.listIndividuals();
			List<Individual> indList = new ArrayList<Individual>();
			
			while (indIter.hasNext()) {
				indList.add(indIter.next());
			}
			
			Collections.shuffle(indList);
			
			// now running the queries
			for (Individual ind : indList.subList(0, Math.min(1000, indList.size() - 1))) {
				printIterator(ind.listOntClasses(false), Thread.currentThread().getName());
			}
		}
		
		public void printIterator(Iterator<?> iterator, String threadId) {
			if (iterator.hasNext()) {
				while (iterator.hasNext()) {
					//LOGGER_.trace("{}: {}", threadId, iterator.next());
					try {
						System.err.println(threadId + ": "+ iterator.next());
					} catch (ConversionException e) {
						// swallow, this is due to the lack of OWL 2 support
					}
				}
			}
		}

	}
}
