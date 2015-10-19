// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.complexible.pellet.client.reasoner;

import java.util.Map;

import com.clarkparsia.owlapiv3.ImmutableNodeSet;
import com.google.common.collect.ImmutableMap;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * @author Evren Sirin
 */
public class SchemaReasonerUtil {
    private static final Map<EntityType<?>, EntityQueryEvaluator> QUERY_EVALUATORS = ImmutableMap
                                                                                         .<EntityType<?>, EntityQueryEvaluator>builder()
                                                                                         .put(EntityType.CLASS, new ClassQueryEvaluator())
                                                                                         .put(EntityType.OBJECT_PROPERTY, new ObjectPropertyQueryEvaluator())
                                                                                         .put(EntityType.DATA_PROPERTY, new DataPropertyQueryEvaluator())
                                                                                         .build();

    /**
     * Execute a schema query using an OWLReasoner instance.
     *
     * @param reasoner
     * @param query
     * @param input
     * @param <T>
     * @return
     */
    public static <T extends OWLObject> NodeSet<T> query(OWLReasoner reasoner, SchemaQuery query, OWLLogicalEntity input) {
        EntityQueryEvaluator evaluator = QUERY_EVALUATORS.get(input.getEntityType());
        return (NodeSet) evaluator.query(reasoner, query, input);
    }

    private interface EntityQueryEvaluator {
        NodeSet<?> query(OWLReasoner reasoner, SchemaQuery query, OWLLogicalEntity input);
    }

    private static class ClassQueryEvaluator implements EntityQueryEvaluator {
        @Override
        public NodeSet<OWLClass> query(final OWLReasoner reasoner, final SchemaQuery query, final OWLLogicalEntity input) {
            OWLClass cls = (OWLClass) input;
            switch (query) {
                case EQUIVALENT: return ImmutableNodeSet.of(reasoner.getEquivalentClasses(cls));
                case CHILD: return reasoner.getSubClasses(cls, true);
                case DESCENDANT: return reasoner.getSubClasses(cls, false);
                case PARENT: return reasoner.getSuperClasses(cls, true);
                case ANCESTOR: return reasoner.getSuperClasses(cls, false);
                case DISJOINT: return reasoner.getDisjointClasses(cls);
                default: throw new UnsupportedOperationException();
            }
        }
    }

    private static class ObjectPropertyQueryEvaluator implements EntityQueryEvaluator {
        @Override
        public NodeSet<?> query(final OWLReasoner reasoner, final SchemaQuery query, final OWLLogicalEntity input) {
            OWLObjectProperty pe = (OWLObjectProperty) input;
            switch (query) {
                case EQUIVALENT: return ImmutableNodeSet.of(reasoner.getEquivalentObjectProperties(pe));
                case CHILD: return reasoner.getSubObjectProperties(pe, true);
                case DESCENDANT: return reasoner.getSubObjectProperties(pe, false);
                case PARENT: return reasoner.getSuperObjectProperties(pe, true);
                case ANCESTOR: return reasoner.getSuperObjectProperties(pe, false);
                case DISJOINT: return reasoner.getDisjointObjectProperties(pe);
                case DOMAIN: return reasoner.getObjectPropertyDomains(pe, true);
                case RANGE: return reasoner.getObjectPropertyRanges(pe, true);
                case INVERSE: return ImmutableNodeSet.of(reasoner.getInverseObjectProperties(pe));
                default: throw new UnsupportedOperationException();
            }
        }
    }

    private static class DataPropertyQueryEvaluator implements EntityQueryEvaluator {
        @Override
        public NodeSet<?> query(final OWLReasoner reasoner, final SchemaQuery query, final OWLLogicalEntity input) {
            OWLDataProperty pe = (OWLDataProperty) input;
            switch (query) {
                case EQUIVALENT: return ImmutableNodeSet.of(reasoner.getEquivalentDataProperties(pe));
                case CHILD: return reasoner.getSubDataProperties(pe, true);
                case DESCENDANT: return reasoner.getSubDataProperties(pe, false);
                case PARENT: return reasoner.getSuperDataProperties(pe, true);
                case ANCESTOR: return reasoner.getSuperDataProperties(pe, false);
                case DISJOINT: return reasoner.getDisjointDataProperties(pe);
                case DOMAIN: return reasoner.getDataPropertyDomains(pe, true);
                default: throw new UnsupportedOperationException();
            }
        }
    }
}
