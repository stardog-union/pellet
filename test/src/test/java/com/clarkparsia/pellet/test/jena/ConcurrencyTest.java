/**
 * 
 */
package com.clarkparsia.pellet.test.jena;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

/**
 * Verifies that Pellet doesn't throw any exceptions when doing concurrent ABox
 * queries provided that classification and realization are down synchronously
 * before.
 * 
 * @author Pavel Klinov
 *
 */
public class ConcurrencyTest {

	private static final String ONTOLOGY_PATH_ = "/test/data/concurrency/vicodi.ttl";
	
	private static final int THREAD_NUMBER = 10;
	
	private static final Iterable<Individual> POISON = Collections.emptyList();
	
	private static final int BATCH_SIZE = 10;
	
	@Test
	public void concurrentDLQueries() throws Exception {
		// create Pellet reasoner
		System.err.println("Loading the ontology");
		OntModel ontModel = loadOntologyModel(ONTOLOGY_PATH_);
		
		// force classification and realization
		System.err.println("Realizing the ontology");
		
		((PelletInfGraph) ontModel.getGraph()).realize();
		BlockingQueue<Iterable<Individual>> toDo = new ArrayBlockingQueue<Iterable<Individual>>(2 * THREAD_NUMBER);
		
		// launching threads which will concurrently run type queries on instances
		ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUMBER);
		
		for (int i = 0; i < THREAD_NUMBER; i++) {
			pool.execute(new QueryRunner(ontModel, toDo));
		}
		
		// adding the individuals to the processing queue for the threads to process concurrently
		Iterator<Individual> individuals = ontModel.listIndividuals();
		
		while (individuals.hasNext()) {
			List<Individual> batch = new ArrayList<Individual>(BATCH_SIZE);
			
			for (int i = 0; i < BATCH_SIZE && individuals.hasNext(); i++) {
				Individual ind = individuals.next();
				
				batch.add(ind);
			}
			
			if (!batch.isEmpty()) {
				toDo.put(batch);
			}
		}
		
		// letting the threads die...
		for (int i = 0; i < THREAD_NUMBER; i++) {
			toDo.put(POISON);
		}
		
		pool.shutdown();
		pool.awaitTermination(100, TimeUnit.SECONDS);
	}

	private OntModel loadOntologyModel(String ontologyPath) throws IOException {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		InputStream ontStream = null;
        // read the file
        try {
			ontStream = ConcurrencyTest.class.getResourceAsStream(ontologyPath);
			
			model.read( ontStream, null, "TTL" );
		} finally {
			if (ontStream != null) {
				ontStream.close();
			}
		}
        
		return model;
	}
	

	private static class QueryRunner implements Runnable {

		private final OntModel model_;
		
		private final BlockingQueue<Iterable<Individual>> toDo_;
		
		QueryRunner(OntModel model, BlockingQueue<Iterable<Individual>> toDo) {
			model_ = model;
			toDo_ = toDo;
		}
		
		@Override
		public void run() {
			for (;;) {
				Iterable<Individual> batch = null;
				
				try {
					batch = toDo_.take();
				} catch (InterruptedException e) {
					break;
				}
				
				if (batch == POISON) {
					// we're done
					break;
				}
				
				for (Individual ind : batch) {
					// querying for all object property values for each individual	
					Iterator<? extends Property> propertyIter = model_.listObjectProperties();

					while (propertyIter.hasNext()) {
						Property property = propertyIter.next();

						printIterator(ind.listPropertyValues(property), Thread.currentThread().getName() + ": " + ind.getLocalName() + " -- " + property.getLocalName() + " --> ");
					}
					
					// querying for all data property values for each individual	
					propertyIter = model_.listDatatypeProperties();

					while (propertyIter.hasNext()) {
						Property property = propertyIter.next();

						printIterator(ind.listPropertyValues(property), Thread.currentThread().getName() + ": " + ind.getLocalName() + " -- " + property.getLocalName() + " --> ");
					}
				}
			}
		}
		
		public void printIterator(Iterator<?> iterator, String threadId) {
			if (iterator.hasNext()) {
				while (iterator.hasNext()) {
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
