package com.complexible.pellet.client.api;

import java.util.UUID;

import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.gson.JsonObject;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import org.semanticweb.owlapi.model.IRI;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Interface definition for Pellet Service.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PelletService {

	String DEFAULT_LOCAL_ENDPOINT = "http://localhost:18080";

	@GET("/admin/shutdown")
	Call<GenericJsonMessage> shutdown();

	@POST("/reasoner/{ontology}/query")
	Call<ResponseBody> query(@Path("ontology") IRI theOntology,
	                         @Query("type") SchemaReasoner.QueryType theType,
	                         @Query("client") UUID theClientID,
	                         @Header("Accept") String theAcceptMediaType,
	                         @Body RequestBody theQueryRequest);

	@POST("/reasoner/{ontology}/explain")
	Call<ResponseBody> explain(@Path("ontology") IRI theOntology,
	                           @Query("limit") int limit,
	                           @Query("client") UUID theClientID,
	                           @Header("Accept") String theAcceptMediaType,
	                           @Body RequestBody theQueryRequest);

	@PUT("/reasoner/{ontology}")
	Call<GenericJsonMessage> update(@Path("ontology") IRI theOntology,
	                                @Query("client") UUID theClientID,
	                                @Header("Accept") String theAcceptMediaType,
	                                @Body RequestBody theUpdateRequest);

	@GET("/reasoner/{ontology}/version")
	Call<JsonObject> version(@Path("ontology") IRI theOntology,
	                         @Query("client") UUID theClientID,
	                         @Header("Accept") String theAcceptMediaType);
}
