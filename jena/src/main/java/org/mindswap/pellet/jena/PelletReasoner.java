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

package org.mindswap.pellet.jena;

import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.graph.loader.DefaultGraphLoader;

import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerException;
import com.hp.hpl.jena.reasoner.BaseInfGraph.InfFindSafeCapabilities;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

/**
 * @author Evren Sirin
 */
public class PelletReasoner implements Reasoner {
    protected static Logger log = Logger.getLogger( PelletReasoner.class.getName() );

    private Model reasonerCapabilities;

    private Capabilities graphCapabilities;
    
    private Graph schema;

    public PelletReasoner( ) {
        this( null, PelletReasonerFactory.theInstance().getCapabilities() );
    }

    public PelletReasoner( Graph schema ) {
        this( schema, PelletReasonerFactory.theInstance().getCapabilities() );
    }
    
    protected PelletReasoner( Model reasonerCapabilities ) {
        this( null, reasonerCapabilities );
    }
    
    protected PelletReasoner( Graph schema, Model reasonerCapabilities ) {
        this.schema = schema;
        this.reasonerCapabilities = reasonerCapabilities;
        
        graphCapabilities = new InfFindSafeCapabilities();
     }
    
    public Graph getSchema() {
        return schema;
    }

    public Reasoner bindSchema( Graph graph ) throws ReasonerException {
        return new PelletReasoner( graph, reasonerCapabilities );
    }

    public Reasoner bindSchema( Model model ) throws ReasonerException {
        return bindSchema( model.getGraph() );
    }

    public PelletInfGraph bind( Graph graph ) throws ReasonerException {
        log.fine("In bind!");
        return new PelletInfGraph( graph, this, new DefaultGraphLoader() );
    }
    
    public InfModel bind( Model model ) throws ReasonerException {
        log.fine("In bind!");
    	return ModelFactory.createInfModel( bind( model.getGraph() ) );
    }
    
    public PelletInfGraph bind( KnowledgeBase kb ) throws ReasonerException {
        return new PelletInfGraph( kb, this, new DefaultGraphLoader() );
    }

    public void setDerivationLogging( boolean enable ) {
    }

    public void setParameter(Property arg0, Object arg1) {
    }

    public Model getReasonerCapabilities() {
        return reasonerCapabilities;
    }
    
    public Capabilities getGraphCapabilities() {
        return graphCapabilities;
    }

    public void addDescription(Model arg0, Resource arg1) {
    }

    public boolean supportsProperty(Property property) {
        Model caps = getReasonerCapabilities();
        if( caps == null ) return false;
        return caps.contains(null, ReasonerVocabulary.supportsP, property);
    }

}
