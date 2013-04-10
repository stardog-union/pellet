// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
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

package org.mindswap.pellet.owlapi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.mindswap.pellet.utils.progress.SilentProgressMonitor;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeVisitor;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.model.SetOntologyURI;

import aterm.ATermAppl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class PelletLoader {
	public static Logger							log				= Logger
																		.getLogger( PelletLoader.class.getName() );

	private KnowledgeBase						kb;

	// private Set<URI> loadedFiles;

	private OWLOntologyManager					manager;

	private Set<OWLOntology>					ontologies;

	/**
	 * Flag to check if imports will be automatically loaded/unloaded
	 */
	private boolean								processImports;

	/**
	 * Ontologies that are loaded due to imports but they have not been included
	 * in an explicit load statement by the user
	 */
	private Set<OWLOntology>					notImported;

	/**
	 * This is the reverse mapping of imports. The key is an ontology and the
	 * value is a set of ontology that imports the ontology used as the key
	 */
	private Map<OWLOntology, Set<OWLOntology>>	importDependencies;

	private PelletVisitor						visitor;

	private ChangeVisitor						changeVisitor	= new ChangeVisitor();

	private class ChangeVisitor implements OWLOntologyChangeVisitor {
		public void visit(AddAxiom change) {
			visitor.setAddAxiom( true );
			change.getAxiom().accept( visitor );
		}

		public void visit(RemoveAxiom change) {
			visitor.setAddAxiom( false );
			change.getAxiom().accept( visitor );
		}

		public void visit(SetOntologyURI change) {
			// nothing to do here
		}
	}

	public PelletLoader(KnowledgeBase kb) {
		this.kb = kb;

		visitor = new PelletVisitor( kb );

		processImports = true;

		ontologies = new HashSet<OWLOntology>();
		notImported = new HashSet<OWLOntology>();
		importDependencies = new HashMap<OWLOntology, Set<OWLOntology>>();
	}

	/**
	 * @deprecated Use {@link #getProcessImports()} instead
	 */
	public boolean loadImports() {
		return getProcessImports();
	}

	/**
	 * @deprecated Use {@link #setProcessImports(boolean)} instead
	 */
	public void setLoadImports(boolean loadImports) {
		setProcessImports( loadImports );
	}

	public boolean getProcessImports() {
		return processImports;
	}

	public void setProcessImports(boolean processImports) {
		this.processImports = processImports;
	}

	public void clear() {
		visitor.clear();
		kb.clear();
		ontologies.clear();
		notImported.clear();
		importDependencies.clear();

		// loadedFiles = new HashSet();
		// loadedFiles.add( Namespaces.OWL );
		// loadedFiles.add( Namespaces.RDF );
		// loadedFiles.add( Namespaces.RDFS );
	}

	public KnowledgeBase getKB() {
		return kb;
	}

	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}

	public ATermAppl term(OWLObject d) {
		visitor.reset();
		visitor.setAddAxiom( false );
		d.accept( visitor );

		ATermAppl a = visitor.result();

		if( a == null )
			throw new InternalReasonerException( "Cannot create ATerm from description " + d );

		return a;
	}

	public void reload() {
		log.fine( "Reloading the ontologies" );

		// copy the loaded ontologies
		Set<OWLOntology> notImportedOnts = new HashSet<OWLOntology>( notImported );

		// clear everything
		clear();

		// load ontologies again
		load( notImportedOnts );
	}

	public void load(Set<OWLOntology> ontologies) {
		Timer timer = kb.timers.startTimer( "load" );

		int axiomCount = 0;
		Collection<OWLOntology> toBeLoaded = new LinkedHashSet<OWLOntology>();
		for( OWLOntology ontology : ontologies )
			axiomCount += load( ontology, false, toBeLoaded );

		ProgressMonitor monitor = new SilentProgressMonitor();
		monitor.setProgressTitle( "Loading" );
		monitor.setProgressLength( axiomCount );
		monitor.taskStarted();

		visitor.reset();
		visitor.setAddAxiom( true );
		visitor.setMonitor( monitor );

		for( OWLOntology ontology : toBeLoaded )
			ontology.accept( visitor );
		
		visitor.verify();

		monitor.taskFinished();

		timer.stop();
	}

	private int load(OWLOntology ontology, boolean imported, Collection<OWLOntology> toBeLoaded) {
		// if not imported add it to notImported set
		if( !imported )
			notImported.add( ontology );

		// add to the loaded ontologies
		boolean added = ontologies.add( ontology );

		// if it was already there, nothing more to do
		if( !added )
			return 0;

		int axiomCount = ontology.getAxioms().size();
		toBeLoaded.add( ontology );

		// if processing imports load the imported ontologies
		if( processImports ) {
			for( OWLOntology importedOnt : ontology.getImports( manager ) ) {
				// load the importedOnt
				axiomCount += load( importedOnt, true, toBeLoaded );

				// update the import dependencies
				Set<OWLOntology> importees = importDependencies.get( importedOnt );
				if( importees == null ) {
					importees = new HashSet<OWLOntology>();
					importDependencies.put( importedOnt, importees );
				}
				importees.add( ontology );
			}
		}

		return axiomCount;
	}

	public void unload(Set<OWLOntology> ontologies) {
		for( OWLOntology ontology : ontologies )
			unload( ontology );
	}

	private void unload(OWLOntology ontology) {
		// remove the ontology from the set
		boolean removed = ontologies.remove( ontology );

		// if it is not there silently return
		if( !removed )
			return;

		// remove it from notImported set, too
		notImported.remove( ontology );

		// if we are processing imports we might need to unload the
		// imported ontologies
		if( processImports ) {
			// go over the imports
			for( OWLOntology importOnt : ontology.getImports( manager ) ) {
				// see if the importedOnt is imported by any other ontology
				Set<OWLOntology> importees = importDependencies.get( importOnt );
				if( importees != null ) {
					// remove the unloaded ontology from the dependencies
					importees.remove( ontology );
					// if nothing is left
					if( importees.isEmpty() ) {
						// remove the empty set from dependencies
						importDependencies.remove( importOnt );
						// only unload if this ontology was not loaded by the
						// user explicitly
						if( !notImported.contains( importOnt ) )
							unload( importOnt );
					}
				}
			}
		}
	}

	/**
	 * @return Returns the loaded ontologies.
	 */
	public Set<OWLOntology> getOntologies() {
		return Collections.unmodifiableSet( ontologies );
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}
	
	public Set<OWLAxiom> getUnsupportedAxioms() {
		return visitor.getUnsupportedAxioms();
	}

	/**
	 * Apply the given changes to the Pellet KB.
	 * 
	 * @param changes
	 *            List of ontology changes to be applied
	 * @return <code>true</code> if changes applied successfully,
	 *         <code>false</code> otherwise indicating a reload is required
	 * @throws OWLException
	 */
	public boolean applyChanges(List<? extends OWLOntologyChange> changes) {
		visitor.reset();

		for( OWLOntologyChange change : changes ) {
			if( !ontologies.contains( change.getOntology() ) )
				continue;

			change.accept( changeVisitor );
			if( visitor.isReloadRequired() ) {
				OWLAxiom axiom = change.getAxiom();
				if( axiom instanceof OWLClassAxiom )
					log.fine( "Removal failed for " + axiom );
				return false;
			}
		}

		return true;
	}

}
