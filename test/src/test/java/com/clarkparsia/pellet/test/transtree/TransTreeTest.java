package com.clarkparsia.pellet.test.transtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.taxonomy.POTaxonomyBuilder;
import org.mindswap.pellet.taxonomy.SubsumptionComparator;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.printer.ClassTreePrinter;
import org.mindswap.pellet.test.utils.TestUtils;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.Searcher;

import pellet.PelletTransTree;
import aterm.ATermAppl;

import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;

public class TransTreeTest {
	
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();
    
    private File testDir;
    
    @Before
    public void setUp() throws Exception {
        testDir = tempDir.newFolder("transtreetest");
    }
    
	@Test
	public void testDiscoveryOntology() throws Exception {
		testProperty( "/data/trans-tree-tests/discovery.owl", "http://purl.org/vocab/relationship/ancestorOf" );
	}
	
	private void testProperty( String ontologyURI, String propertyURI ) throws Exception {
	    String resourceToFile = TestUtils.copyResourceToFile(testDir, ontologyURI);
		OWLAPILoader loader = new OWLAPILoader();
		KnowledgeBase kb = loader.createKB( new String[] { resourceToFile } );
		
		OWLEntity entity = OntologyUtils.findEntity( propertyURI, loader.getAllOntologies() );

		if( entity == null )
			throw new IllegalArgumentException( "Property not found: " + propertyURI );

		if( !(entity instanceof OWLObjectProperty) )
			throw new IllegalArgumentException( "Not an object property: " + propertyURI );
        boolean isTransitive = false;
        OWLObjectProperty property = (OWLObjectProperty) entity;
        for (OWLOntology o : loader.getAllOntologies()) {
            if (Searcher.isTransitive(o, property)) {
                isTransitive = true;
            }
        }

        if (!isTransitive)
			throw new IllegalArgumentException( "Not a transitive property: " + propertyURI );
		
		ATermAppl p = ATermUtils.makeTermAppl( entity.getIRI().toString() );

		POTaxonomyBuilder builder = null;

		// Parts for individuals
		//builder = new POTaxonomyBuilder( kb, new PartIndividualsComparator( kb, p ) );

		// Note: this is not an optimal solution
		//for( ATermAppl individual : kb.getIndividuals() )
		//	if (!ATermUtils.isBnode( individual ))
		//		builder.classify( individual );
			
		//Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();
		//ClassTreePrinter printer = new ClassTreePrinter();
		//printer.print( taxonomy );
		
		builder = new POTaxonomyBuilder( kb, new PartClassesComparator( kb, p ) );
		builder.classify();
		
		Taxonomy<ATermAppl> taxonomy = builder.getTaxonomy();
		ClassTreePrinter printer = new ClassTreePrinter();
		printer.print( taxonomy );
	}
	
	private static class PartClassesComparator extends SubsumptionComparator {

		private ATermAppl	p;

		public PartClassesComparator(KnowledgeBase kb, ATermAppl p) {
			super( kb );
			this.p = p;
		}

		@Override
		protected boolean isSubsumedBy(ATermAppl a, ATermAppl b) {
			ATermAppl someB = ATermUtils.makeSomeValues( p, b );

			return kb.isSubClassOf( a, someB );
		}
	}

	private static class PartIndividualsComparator extends SubsumptionComparator {

		private ATermAppl	p;

		public PartIndividualsComparator(KnowledgeBase kb, ATermAppl p) {
			super( kb );
			this.p = p;
		}

		@Override
		protected boolean isSubsumedBy(ATermAppl a, ATermAppl b) {
			return kb.hasPropertyValue( a, p, b );
		}
	}
	
	@Test
	public void filter1() throws Exception {
		PelletTransTree cli = new PelletTransTree();
		
		cli.parseArgs(new String[]{"trans-tree","-p","http://clarkparsia.com/pellet/tutorial/pops#subProjectOf","-f","http://clarkparsia.com/pellet/tutorial/pops#Employee", TestUtils.copyResourceToFile(testDir, "/data/trans-tree-tests/ontology-010.ttl")});
		cli.run();
		
		Taxonomy<ATermAppl> taxonomy = cli.publicTaxonomy;
		
		assertEquals(5, taxonomy.getClasses().size());	//TOP, not(TOP), Employee, CivilServant, Contractor
		
		Set<Set<ATermAppl>> subclasses = taxonomy.getSubs(ATermUtils.TOP);

		assertEquals(4, subclasses.size());	//not(TOP), Employee, CivilServant, Contractor
		
		Iterator<Set<ATermAppl>> iterator = subclasses.iterator();
		
		Set<ATermAppl> elements = new HashSet<ATermAppl>(4);
		
		while(iterator.hasNext())
		{
			Set<ATermAppl> subclass = iterator.next();
			assertEquals(1, subclass.size());
			elements.add(subclass.iterator().next());
		}
		
		assertTrue(elements.contains(ATermUtils.makeNot(ATermUtils.TOP)));
		assertTrue(elements.contains(ATermUtils.makeTermAppl("http://clarkparsia.com/pellet/tutorial/pops#Employee")));
		assertTrue(elements.contains(ATermUtils.makeTermAppl("http://clarkparsia.com/pellet/tutorial/pops#CivilServant")));
		assertTrue(elements.contains(ATermUtils.makeTermAppl("http://clarkparsia.com/pellet/tutorial/pops#Contractor")));
		
	}
	
	@Test
	public void filter2() throws Exception {
		PelletTransTree cli = new PelletTransTree();
		
		cli.parseArgs(new String[]{"trans-tree","-p","http://clarkparsia.com/pellet/tutorial/pops#subProjectOf","-f","http://clarkparsia.com/pellet/tutorial/pops#Employee","--individuals", TestUtils.copyResourceToFile(testDir, "/data/trans-tree-tests/ontology-010.ttl")});		
		cli.run();
		
		Taxonomy<ATermAppl> taxonomy = cli.publicTaxonomy;
		
		Set<ATermAppl> classes = taxonomy.getClasses();
		assertEquals(3, classes.size());		//TOP, not(TOP), 1 Employee
		assertTrue(classes.contains(ATermUtils.makeTermAppl("http://clarkparsia.com/pellet/tutorial/pops#Employee1")));
	}
	
	@Test
	public void filter3() throws Exception {
		PelletTransTree cli = new PelletTransTree();
		
		cli.parseArgs(new String[]{"trans-tree","-p","http://clarkparsia.com/pellet/tutorial/pops#subProjectOf","-f","http://clarkparsia.com/pellet/tutorial/pops#Contractor","--individuals", TestUtils.copyResourceToFile(testDir, "/data/trans-tree-tests/ontology-010.ttl")});		
		cli.run();
		
		Taxonomy<ATermAppl> taxonomy = cli.publicTaxonomy;		
		assertEquals(2, taxonomy.getClasses().size());		//TOP, not(TOP) (no Contractors)
		}
}
