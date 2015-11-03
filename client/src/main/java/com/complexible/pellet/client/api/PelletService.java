package com.complexible.pellet.client.api;

import com.complexible.pellet.service.messages.request.ExplainRequest;
import com.complexible.pellet.service.messages.GenericJsonMessage;
import com.complexible.pellet.service.messages.request.QueryRequest;
import com.complexible.pellet.service.messages.request.UpdateRequest;
import com.complexible.pellet.service.messages.response.ExplainResponse;
import com.complexible.pellet.service.messages.response.QueryResponse;
import com.complexible.pellet.service.messages.response.UpdateResponse;
import org.semanticweb.owlapi.model.IRI;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Interface definition for Pellet Service.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PelletService {

	@GET("/admin/shutdown")
	Call<GenericJsonMessage> shutdown();

	@POST("/reasoner/{ontology}/query")
	Call<QueryResponse> query(@Path("ontology") IRI theOntology, @Body QueryRequest theQueryRequest);

	@POST("/reasoner/{ontology}/explain")
	Call<ExplainResponse> explain(@Path("ontology") IRI theOntology, @Body ExplainRequest theQueryRequest);

	@PUT("/reasoner/{ontology}")
	Call<UpdateResponse> update(@Path("ontology") IRI theOntology, @Body UpdateRequest theQueryRequest);
}
