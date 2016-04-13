package com.complexible.pellet.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.service.messages.JsonMessage;
import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Provides the Pellet Service API client.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceProvider implements Provider<PelletService> {

	private final String mEndpoint;

	private final long mConnTimeoutMin;

	private final long mReadTimeoutMin;

	private final long mWriteTimeoutMin;

	@Inject
	public PelletServiceProvider(@Named("endpoint") final String theEndpoint,
	                             @Named("conn_timeout") final long theConnTimeout,
	                             @Named("read_timeout") final long theReadTimeout,
	                             @Named("write_timeout") final long theWriteTimeout) {
		mEndpoint = !Strings.isNullOrEmpty(theEndpoint) ? theEndpoint
		                                                : PelletService.DEFAULT_LOCAL_ENDPOINT;
		mConnTimeoutMin = theConnTimeout;
		mReadTimeoutMin = theReadTimeout;
		mWriteTimeoutMin = theWriteTimeout;
	}

	@Override
	public PelletService get() {
		final OkHttpClient httpClient = new OkHttpClient.Builder()
												.connectTimeout(mConnTimeoutMin, TimeUnit.MINUTES)
												.readTimeout(mReadTimeoutMin, TimeUnit.MINUTES)
												.writeTimeout(mWriteTimeoutMin, TimeUnit.MINUTES)
				                                .build();

		final Retrofit aRetrofit = new Retrofit.Builder().baseUrl(mEndpoint)
		                                                 .client(httpClient)
		                                                 .addConverterFactory(PRIMITIVE_FACTORY)
		                                                 .addConverterFactory(ONTOLOGY_FACTORY)
		                                                 .addConverterFactory(AXIOM_FACTORY)
		                                                 .addConverterFactory(NODE_SET_FACTORY)
		                                                 .addConverterFactory(QUERY_FACTORY)
		                                                 .build();

		return aRetrofit.create(PelletService.class);
	}

	private static final MediaType TEXT_MEDIA_TYPE = MediaType.parse("text/plain; charset=UTF-8");
	private static final MediaType TURTLE_MEDIA_TYPE = MediaType.parse("text/turtle");
	private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

	private static final Set<Object> PRIMITIVE_TYPES = ImmutableSet.<Object>of(Boolean.class, Double.class, Float.class, Integer.class, Long.class, Short.class);

	private static final Converter.Factory PRIMITIVE_FACTORY = new Converter.Factory() {
		public Converter<Object, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
			return PRIMITIVE_TYPES.contains(type) ? PRIMITIVE_REQUEST_CONVERTER : null;
		}

		@Override
		public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
			if (type == Boolean.class) {
				return BOOLEAN_CONVERTER;
			}
			else if (type == Double.class) {
				return DOUBLE_CONVERTER;
			}
			else if (type == Float.class) {
				return FLOAT_CONVERTER;
			}
			else if (type == Integer.class) {
				return INTEGER_CONVERTER;
			}
			else if (type == Long.class) {
				return LONG_CONVERTER;
			}
			else if (type == Short.class) {
				return SHORT_CONVERTER;
			}
			return null;
		}
	};

	private static final Converter<Object, RequestBody> PRIMITIVE_REQUEST_CONVERTER = new Converter<Object, RequestBody>() {
		public RequestBody convert(Object value) throws IOException {
			return RequestBody.create(TEXT_MEDIA_TYPE, String.valueOf(value));
		}
	};

	private static final Converter<ResponseBody, Boolean> BOOLEAN_CONVERTER = new Converter<ResponseBody, Boolean>() {
		public Boolean convert(ResponseBody body) throws IOException {
			return Boolean.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Double> DOUBLE_CONVERTER = new Converter<ResponseBody, Double>() {
		public Double convert(ResponseBody body) throws IOException {
			return Double.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Float> FLOAT_CONVERTER = new Converter<ResponseBody, Float>() {
		public Float convert(ResponseBody body) throws IOException {
			return Float.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Integer> INTEGER_CONVERTER = new Converter<ResponseBody, Integer>() {
		public Integer convert(ResponseBody body) throws IOException {
			return Integer.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Long> LONG_CONVERTER = new Converter<ResponseBody, Long>() {
		public Long convert(ResponseBody body) throws IOException {
			return Long.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Short> SHORT_CONVERTER = new Converter<ResponseBody, Short>() {
		public Short convert(ResponseBody body) throws IOException {
			return Short.valueOf(body.string());
		}
	};

	private static final Converter.Factory ONTOLOGY_FACTORY = new Converter.Factory() {
		public Converter<OWLOntology, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
			return type.equals(OWLOntology.class) ? ONTOLOGY_REQUEST_CONVERTER : null;
		}

		@Override
		public Converter<ResponseBody, OWLOntology> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
			return type.equals(OWLOntology.class) ? ONTOLOGY_CONVERTER : null;
		}
	};

	private static final Converter<OWLOntology, RequestBody> ONTOLOGY_REQUEST_CONVERTER = new Converter<OWLOntology, RequestBody>() {
		public RequestBody convert(OWLOntology ont) throws IOException {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OWL.manager.saveOntology(ont, new TurtleDocumentFormat(), out);
				return RequestBody.create(TURTLE_MEDIA_TYPE, out.toByteArray());
			}
			catch (OWLOntologyStorageException e) {
				throw new IOException(e);
			}
		}
	};

	private static final Converter<ResponseBody, OWLOntology> ONTOLOGY_CONVERTER = new Converter<ResponseBody, OWLOntology>() {
		public OWLOntology convert(ResponseBody body) throws IOException {
			try {
				return OWL.manager.loadOntologyFromOntologyDocument(body.byteStream());
			}
			catch (OWLOntologyCreationException e) {
				throw new IOException(e);
			}
		}
	};

	private static final Converter.Factory AXIOM_FACTORY = new Converter.Factory() {
		public Converter<OWLAxiom, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
			return type.equals(OWLAxiom.class) ? AXIOM_REQUEST_CONVERTER : null;
		}

		@Override
		public Converter<ResponseBody, OWLAxiom> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
			return null;
		}
	};

	private static final Converter<OWLAxiom, RequestBody> AXIOM_REQUEST_CONVERTER = new Converter<OWLAxiom, RequestBody>() {
		public RequestBody convert(OWLAxiom axiom) throws IOException {
			return ONTOLOGY_REQUEST_CONVERTER.convert(OWL.Ontology(axiom));
		}
	};

	private static final Converter.Factory NODE_SET_FACTORY = new Converter.Factory() {
		public Converter<NodeSet, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
			return null;
		}

		@Override
		public Converter<ResponseBody, NodeSet> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
			return type.equals(NodeSet.class) ? NODE_SET_CONVERTER : null;
		}
	};

	private static final Converter<ResponseBody, NodeSet> NODE_SET_CONVERTER = new Converter<ResponseBody, NodeSet>() {
		public NodeSet convert(ResponseBody body) throws IOException {
			return JsonMessage.readNodeSet(body.byteStream());
		}
	};

	private static final Converter.Factory QUERY_FACTORY = new Converter.Factory() {
		public Converter<SchemaQuery, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
			return type.equals(SchemaQuery.class) ? QUERY_REQUEST_CONVERTER : null;
		}

		@Override
		public Converter<ResponseBody, SchemaQuery> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
			return null;
		}
	};

	private static final Converter<SchemaQuery, RequestBody> QUERY_REQUEST_CONVERTER = new Converter<SchemaQuery, RequestBody>() {
		public RequestBody convert(SchemaQuery query) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JsonMessage.writeSchemaQuery(query, out);
			return RequestBody.create(JSON_MEDIA_TYPE, out.toByteArray());
		}
	};

}
