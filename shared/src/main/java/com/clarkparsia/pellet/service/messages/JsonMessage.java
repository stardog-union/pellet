package com.clarkparsia.pellet.service.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;

import com.clarkparsia.owlapiv3.ImmutableNode;
import com.clarkparsia.owlapiv3.ImmutableNodeSet;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import com.clarkparsia.pellet.service.reasoner.SchemaQueryType;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Utility class to encode schema query and response objects in JSON.
 * @author Evren Sirin
 */
public class JsonMessage {
	private JsonMessage() { throw new AssertionError(); }

	private static JsonFactory FACTORY = new JsonFactory();

	private enum Field {
		QUERY, ENTITY, NODE, IRI, TYPE;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public static void writeSchemaQuery(final SchemaQuery query, final OutputStream out) throws IOException {
		JsonGenerator g = FACTORY.createGenerator(out, JsonEncoding.UTF8);
		writeSchemaQuery(g, query);
		g.close();
	}

	private static void writeSchemaQuery(JsonGenerator g, SchemaQuery query) throws IOException {
		g.writeStartObject();
		g.writeStringField(Field.QUERY.toString(), query.getType().name());
		g.writeFieldName(Field.ENTITY.toString());
		writeEntity(g, query.getEntity());
		g.writeEndObject();
	}

	public static void writeNodeSet(final NodeSet<? extends OWLObject> nodeSet, final OutputStream out) throws IOException {
		JsonGenerator g = FACTORY.createGenerator(out, JsonEncoding.UTF8);
		writeNodeSet(g, nodeSet);
		g.close();
	}

	private static void writeNodeSet(JsonGenerator g, NodeSet<? extends OWLObject> nodeSet) throws IOException {
		g.writeStartObject();
		g.writeArrayFieldStart(Field.NODE.toString());
		for (Node<? extends OWLObject> node : nodeSet) {
			writeNode(g, node);
		}
		g.writeEndArray();
		g.writeEndObject();
	}

	private static void writeNode(JsonGenerator g, Node<? extends OWLObject> node) throws IOException {
		g.writeStartObject();
		g.writeArrayFieldStart(Field.ENTITY.toString());
		for (OWLObject obj : node) {
			writeEntity(g, (OWLEntity) obj);
		}
		g.writeEndArray();
		g.writeEndObject();
	}

	private static void writeEntity(JsonGenerator g, OWLEntity entity) throws IOException {
		g.writeStartObject();
		g.writeStringField(Field.IRI.toString(), entity.getIRI().toString());
		g.writeStringField(Field.TYPE.toString(), entity.getEntityType().getName());
		g.writeEndObject();
	}

	private static void assertNextToken(final JsonToken expected, final JsonParser jp) throws IOException {
		assertRead(expected, jp.nextToken());
	}

	private static void assertCurrentToken(final JsonToken expected, final JsonParser jp) throws IOException {
		assertRead(expected, jp.getCurrentToken());
	}

	private static <T> void assertRead(final T expected, final T actual) throws IOException {
		if (!actual.equals(expected)) {
			throwUnexpected(expected, actual);
		}
	}

	private static void throwUnexpected(Object expected, Object actual) throws IOException {
		throw new IOException("Expecting " + expected + " but got " + actual);
	}

	private static void throwUnexpectedField(String actual, Field... fields) throws IOException {
		throwUnexpected("one of " + Arrays.toString(fields), actual);
	}

	public static SchemaQuery readQuery(final InputStream in) throws IOException {
		final JsonParser jp = FACTORY.createParser(in);
		assertNextToken(JsonToken.START_OBJECT, jp);

		SchemaQueryType queryType = null;
		OWLLogicalEntity entity = null;

		while (jp.nextToken() == JsonToken.FIELD_NAME) {
			String field = jp.getCurrentName();
			if (field.equals(Field.QUERY.toString())) {
				queryType = SchemaQueryType.valueOf(jp.nextTextValue());
			}
			else if (field.equals(Field.ENTITY.toString())) {
				entity = readEntity(jp);
			}
			else {
				throwUnexpectedField(field, Field.QUERY, Field.ENTITY);
			}
		}
		assertCurrentToken(JsonToken.END_OBJECT, jp);
		return new SchemaQuery(queryType, entity);
	}

	public static <T extends OWLObject> NodeSet<T> readNodeSet(final InputStream in) throws IOException {
		final JsonParser jp = FACTORY.createParser(in);
		assertNextToken(JsonToken.START_OBJECT, jp);
		assertNextToken(JsonToken.FIELD_NAME, jp);

		assertRead(jp.getCurrentName(), Field.NODE.toString());
		assertNextToken(JsonToken.START_ARRAY, jp);

		Set<Node<T>> nodes = Sets.newHashSet();
		while (jp.nextToken() == JsonToken.START_OBJECT) {
			Node<T> node = readNode(jp);
			nodes.add(node);
		}
		assertCurrentToken(JsonToken.END_ARRAY, jp);
		assertNextToken(JsonToken.END_OBJECT, jp);

		return ImmutableNodeSet.of(nodes);
	}

	private static <T extends OWLObject> Node<T> readNode(final JsonParser jp) throws IOException {
		if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
			assertNextToken(JsonToken.START_OBJECT, jp);
		}
		assertNextToken(JsonToken.FIELD_NAME, jp);

		assertRead(jp.getCurrentName(), Field.ENTITY.toString());
		assertNextToken(JsonToken.START_ARRAY, jp);

		Set<T> entities = Sets.newHashSet();
		while (jp.nextToken() == JsonToken.START_OBJECT) {
			T entity = readEntity(jp);
			entities.add(entity);
		}
		assertCurrentToken(JsonToken.END_ARRAY, jp);
		assertNextToken(JsonToken.END_OBJECT, jp);

		return ImmutableNode.of(entities);
	}

	private static <T extends OWLObject> T readEntity(final JsonParser jp) throws IOException {
		if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
			assertNextToken(JsonToken.START_OBJECT, jp);
		}

		String entityIRI = null;
		String entityType = null;

		while (jp.nextToken() == JsonToken.FIELD_NAME) {
			String field = jp.getCurrentName();
			if (field.equals(Field.IRI.toString())) {
				entityIRI = jp.nextTextValue();
			}
			else if (field.equals(Field.TYPE.toString())) {
				entityType = jp.nextTextValue();
			}
			else {
				throwUnexpectedField(field, Field.IRI, Field.TYPE);
			}
		}
		assertCurrentToken(JsonToken.END_OBJECT, jp);

		return (T) createEntity(entityType, IRI.create(entityIRI));
	}

	private static OWLLogicalEntity createEntity(final String entityType, final IRI entityIRI) {
		if (entityType.equalsIgnoreCase(EntityType.CLASS.getName())) {
			return OWL.Class(entityIRI);
		}
		else if (entityType.equalsIgnoreCase(EntityType.OBJECT_PROPERTY.getName())) {
			return OWL.ObjectProperty(entityIRI);
		}
		else if (entityType.equalsIgnoreCase(EntityType.DATA_PROPERTY.getName())) {
			return OWL.DataProperty(entityIRI);
		}

		throw new IllegalArgumentException("Invalid entity type: " + entityType);
	}
}
