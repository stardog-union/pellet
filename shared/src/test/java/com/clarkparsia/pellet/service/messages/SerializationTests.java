package com.clarkparsia.pellet.service.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import com.clarkparsia.pellet.service.reasoner.SchemaQueryType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evren Sirin
 */
public class SerializationTests {

	@Test
	public void classQuery() throws Exception {
		schemaQuery(new SchemaQuery(SchemaQueryType.CHILD, OWL.Class("urn:test")));
	}

	@Test
	public void objPropertyQuery() throws Exception {
		schemaQuery(new SchemaQuery(SchemaQueryType.PARENT, OWL.ObjectProperty("urn:test")));
	}

	@Test
	public void dataPropertyQuery() throws Exception {
		schemaQuery(new SchemaQuery(SchemaQueryType.EQUIVALENT, OWL.DataProperty("urn:test")));
	}

	private void schemaQuery(SchemaQuery expected) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JsonMessage.writeSchemaQuery(expected, out);
		SchemaQuery actual = JsonMessage.readQuery(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(expected, actual);
	}
}
