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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * Verifies that Pellet doesn't throw any exceptions when doing concurrent ABox queries provided that classification and realization are down synchronously
 * before.
 *
 * @author Pavel Klinov
 */
public class ConcurrencyTest
{
	private static final Logger _logger = Log.getLogger(ConcurrencyTest.class);

	private static final String ONTOLOGY_PATH_ = "/test/data/concurrency/vicodi.ttl";

	private static final int THREAD_NUMBER = 10;

	private static final Iterable<Individual> POISON = Collections.emptyList();

	private static final int BATCH_SIZE = 10;

	@Test
	public void concurrentDLQueries() throws Exception
	{
		// create Pellet reasoner
		System.err.println("Loading the ontology");
		final OntModel ontModel = loadOntologyModel(ONTOLOGY_PATH_);

		// force classification and realization
		System.err.println("Realizing the ontology");

		((PelletInfGraph) ontModel.getGraph()).realize();
		final BlockingQueue<Iterable<Individual>> toDo = new ArrayBlockingQueue<>(2 * THREAD_NUMBER);

		// launching threads which will concurrently run type queries on instances
		final ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUMBER);

		for (int i = 0; i < THREAD_NUMBER; i++)
			pool.execute(new QueryRunner(ontModel, toDo));

		// adding the individuals to the processing _queue for the threads to process concurrently
		final Iterator<Individual> individuals = ontModel.listIndividuals();

		while (individuals.hasNext())
		{
			final List<Individual> batch = new ArrayList<>(BATCH_SIZE);

			for (int i = 0; i < BATCH_SIZE && individuals.hasNext(); i++)
			{
				final Individual ind = individuals.next();

				batch.add(ind);
			}

			if (!batch.isEmpty())
				toDo.put(batch);
		}

		// letting the threads die...
		for (int i = 0; i < THREAD_NUMBER; i++)
			toDo.put(POISON);

		pool.shutdown();
		pool.awaitTermination(100, TimeUnit.SECONDS);
	}

	private OntModel loadOntologyModel(final String ontologyPath) throws IOException
	{
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		// read the file
		try (final InputStream ontStream = ConcurrencyTest.class.getResourceAsStream(ontologyPath))
		{
			model.read(ontStream, null, "TTL");
		}
		return model;
	}

	private static class QueryRunner implements Runnable
	{

		private final OntModel _model;

		private final BlockingQueue<Iterable<Individual>> _toDo;

		QueryRunner(final OntModel model, final BlockingQueue<Iterable<Individual>> toDo)
		{
			_model = model;
			_toDo = toDo;
		}

		@Override
		public void run()
		{
			for (;;)
			{
				Iterable<Individual> batch = null;

				try
				{
					batch = _toDo.take();
				}
				catch (final InterruptedException e)
				{
					_logger.log(Level.FINER, "", e);
					break;
				}

				if (batch == POISON)
					// we're done
					break;

				for (final Individual ind : batch)
				{
					// querying for all object property values for each _individual
					Iterator<? extends Property> propertyIter = _model.listObjectProperties();

					while (propertyIter.hasNext())
					{
						final Property property = propertyIter.next();

						printIterator(ind.listPropertyValues(property), Thread.currentThread().getName() + ": " + ind.getLocalName() + " -- " + property.getLocalName() + " --> ");
					}

					// querying for all _data property values for each _individual
					propertyIter = _model.listDatatypeProperties();

					while (propertyIter.hasNext())
					{
						final Property property = propertyIter.next();

						printIterator(ind.listPropertyValues(property), Thread.currentThread().getName() + ": " + ind.getLocalName() + " -- " + property.getLocalName() + " --> ");
					}
				}
			}
		}

		public void printIterator(final Iterator<?> iterator, final String threadId)
		{
			if (iterator.hasNext())
				while (iterator.hasNext())
					try
					{
						System.err.println(threadId + ": " + iterator.next());
					}
					catch (final ConversionException e)
					{
						_logger.log(Level.FINE, "swallow, this is due to the lack of OWL 2 support", e);
					}
		}

	}
}
