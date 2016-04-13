// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.service.reasoner;

import java.util.Objects;

import org.semanticweb.owlapi.model.OWLLogicalEntity;

/**
 * Query for schema reasoner.
 *
 * @author Evren Sirin
 */
public final class SchemaQuery {
	private final SchemaQueryType type;
	private final OWLLogicalEntity entity;

	public SchemaQuery(final SchemaQueryType type, final OWLLogicalEntity entity) {
		this.type = Objects.requireNonNull(type);
		this.entity = Objects.requireNonNull(entity);
	}

	public OWLLogicalEntity getEntity() {
		return entity;
	}

	public SchemaQueryType getType() {
		return type;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SchemaQuery)) {
			return false;
		}
		final SchemaQuery that = (SchemaQuery) obj;
		return this.type.equals(that.type) &&
		       this.entity.equals(that.entity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, entity);
	}
}
