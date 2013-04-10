package com.clarkparsia.pellet.sparqldl.engine;

import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.utils.TermFactory;

/**
 * Implements various methods regarding conjunctive query subsumption based on the ABox freezing method.
 * 
 * @author Hector Perez-Urbina
 */

public class QuerySubsumption {

	/**
	 * Checks whether sub is equivalent to sup
	 * @param sub
	 * @param sup
	 * @return 
	 */
	public static boolean isEquivalentTo( Query q1, Query q2 ) {
        return isSubsumedBy( q1, q2 ) && isSubsumedBy( q2, q1 );
    }

    /**
	 * Checks whether sub is subsumed by sup
	 * @param sub
	 * @param sup
	 * @return 
	 */
    public static boolean isSubsumedBy( Query sub, Query sup) {
        return !getSubsumptionMappings( sub, sup).isEmpty();
    }
    
    /**
     * Computes the subsumption mappings between sub and sup
     * @param sub
     * @param sup
     * @param backgroundKB
     * @return
     */
    public static QueryResult getSubsumptionMappings( Query sub, Query sup) {
        KnowledgeBase kb = sup.getKB().copy( true );
        
        List<QueryAtom> queryAtoms = sub.getAtoms();
        for( Iterator<QueryAtom> i = queryAtoms.iterator(); i.hasNext(); ) {
            final QueryAtom queryAtom = (QueryAtom) i.next();
            final List<ATermAppl> arguments = queryAtom.getArguments();
            
            ATermAppl ind1 = null;
            ATermAppl ind2 = null;
            ATermAppl pr = null;
            ATermAppl cl = null;
            
            switch(queryAtom.getPredicate()){
            	case Type:
            		ind1 = TermFactory.term(arguments.get(0).toString());
            		cl = arguments.get(1);
            		kb.addIndividual(ind1);
            		kb.addType(ind1, cl);
            		break;
            	case PropertyValue:
            		ind1 = TermFactory.term(arguments.get(0).toString());
            		pr = arguments.get(1);
            		ind2 = TermFactory.term(arguments.get(2).toString());
            		kb.addIndividual(ind1);
            		kb.addIndividual(ind2);
            		kb.addPropertyValue(pr, ind1, ind2);
            		break;
            	case SameAs:
            		ind1 = TermFactory.term(arguments.get(0).toString());
            		ind2 = TermFactory.term(arguments.get(1).toString());
            		kb.addIndividual(ind1);
            		kb.addIndividual(ind2);
            		kb.addSame(ind1, ind2);
            		break;
            	case DifferentFrom:
            		ind1 = TermFactory.term(arguments.get(0).toString());
            		ind2 = TermFactory.term(arguments.get(1).toString());
            		kb.addIndividual(ind1);
            		kb.addIndividual(ind2);
            		kb.addDifferent(ind1, ind2);
            		break;
            	default:
        			throw new UnsupportedQueryException( "Unsupported atom type : " + queryAtom.getPredicate().toString() );
            }	            
        }
        
        kb.isConsistent();
        
        sup.setKB( kb );
        QueryResult results = QueryEngine.exec( sup );
        sup.setKB( sup.getKB() );
        
        return results;
    }
	
}
