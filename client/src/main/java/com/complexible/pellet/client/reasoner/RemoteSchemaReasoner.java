package com.complexible.pellet.client.reasoner;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.clarkparsia.pellet.service.ServiceDecoder;
import com.clarkparsia.pellet.service.ServiceEncoder;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.messages.ExplainRequest;
import com.clarkparsia.pellet.service.messages.ExplainResponse;
import com.clarkparsia.pellet.service.messages.QueryRequest;
import com.clarkparsia.pellet.service.messages.QueryResponse;
import com.clarkparsia.pellet.service.messages.UpdateRequest;
import com.clarkparsia.pellet.service.proto.ProtoServiceDecoder;
import com.clarkparsia.pellet.service.proto.ProtoServiceEncoder;
import com.complexible.pellet.client.ClientTools;
import com.complexible.pellet.client.api.PelletService;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import org.mindswap.pellet.utils.Pair;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import retrofit.Call;

/**
 * Implementation of a {@link SchemaReasoner} using the Pellet Service API remote reasoner.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class RemoteSchemaReasoner implements SchemaReasoner {

	final PelletService mService;
	final IRI mOntologyIri;

	// TODO: provide this via guice and some configurable parameters for more formats
	final ServiceEncoder mEncoder = new ProtoServiceEncoder();
	final ServiceDecoder mDecoder = new ProtoServiceDecoder();

	private LoadingCache<Pair<QueryType, OWLLogicalEntity>, NodeSet<?>> cache = CacheBuilder.newBuilder()
		                   .maximumSize(1024)
		                   .build(new CacheLoader<Pair<QueryType, OWLLogicalEntity>, NodeSet<?>>() {
			                   @Override
			                   public NodeSet<?> load(final Pair<QueryType, OWLLogicalEntity> pair) throws Exception {
				                   return executeRemoteQuery(pair.first, pair.second);
			                   }
		                   });


	@Inject
	public RemoteSchemaReasoner(final PelletService thePelletService,
	                            @Assisted final OWLOntology theOntology) {
		Preconditions.checkNotNull(theOntology, "the Ontology must not be Null.");

		mService = thePelletService;

		mOntologyIri = theOntology.getOntologyID()
		                          .getOntologyIRI();
	}

	@Override
	public <T extends OWLObject> NodeSet<T> query(final QueryType theQueryType, final OWLLogicalEntity input) {
		try {
			return (NodeSet<T>) cache.get(Pair.create(theQueryType, input));
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
		return null;
	}

	private <T extends OWLObject> NodeSet<T> executeRemoteQuery(final QueryType theQueryType, final OWLLogicalEntity input) {
		try {
			RequestBody aReqBody = RequestBody.create(MediaType.parse(mEncoder.getMediaType()),
			                                          mEncoder.encode(new QueryRequest(input)));

			Call<ResponseBody> queryCall = mService.query(mOntologyIri,
			                                              theQueryType,
			                                              mDecoder.getMediaType(),
			                                              aReqBody);
			final ResponseBody aRespBody = ClientTools.executeCall(queryCall);
			QueryResponse queryResponse = mDecoder.queryResponse(aRespBody.bytes());

			return (NodeSet<T>) queryResponse.getResults();
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
		return null;
	}

	@Override
	public Set<Set<OWLAxiom>> explain(final OWLAxiom axiom, final int limit) {
		try {
			RequestBody aReqBody = RequestBody.create(MediaType.parse(mEncoder.getMediaType()),
			                                          mEncoder.encode(new ExplainRequest(axiom)));

			Call<ResponseBody> explainCall = mService.explain(mOntologyIri,
			                                                  limit,
			                                                  mDecoder.getMediaType(),
			                                                  aReqBody);
			final ResponseBody aRespBody = ClientTools.executeCall(explainCall);
			ExplainResponse explainResponse = mDecoder.explainResponse(aRespBody.bytes());

			return explainResponse.getAxiomSets();
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
		return null;
	}

	@Override
	public void update(final Set<OWLAxiom> additions, final Set<OWLAxiom> removals) {
		try {
			cache.invalidateAll();

			RequestBody aReqBody = RequestBody.create(MediaType.parse(mEncoder.getMediaType()),
			                                          mEncoder.encode(new UpdateRequest(additions, removals)));

			Call<GenericJsonMessage> updateCall = mService.update(mOntologyIri,
			                                                      GenericJsonMessage.MIME_TYPE,
			                                                      aReqBody);
			ClientTools.executeCall(updateCall);
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public int version() {
		final Call<JsonObject> versionCall = mService.version(mOntologyIri, GenericJsonMessage.MIME_TYPE);
		final JsonObject aRespObj = ClientTools.executeCall(versionCall);

		return aRespObj.get("version").getAsInt();
	}

	@Override
	public void close() throws Exception {
	}
}
