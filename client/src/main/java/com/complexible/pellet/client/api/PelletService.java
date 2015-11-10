package com.complexible.pellet.client.api;

import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
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

	String DEFAULT_LOCAL_ENDPOINT = "http://localhost:8080";

	@GET("/admin/shutdown")
	Call<GenericJsonMessage> shutdown();

	@POST("/reasoner/{ontology}/query")
	Call<ResponseBody> query(@Path("ontology") IRI theOntology,
	                         @Query("type") SchemaReasoner.QueryType theType,
	                         @Header("Accept") String theAcceptMediaType,
	                         @Body RequestBody theQueryRequest);

	@POST("/reasoner/{ontology}/explain")
	Call<ResponseBody> explain(@Path("ontology") IRI theOntology,
	                           @Query("limit") int limit,
	                           @Header("Accept") String theAcceptMediaType,
	                           @Body RequestBody theQueryRequest);

	@PUT("/reasoner/{ontology}")
	Call<GenericJsonMessage> update(@Path("ontology") IRI theOntology,
	                                @Header("Accept") String theAcceptMediaType,
	                                @Body RequestBody theUpdateRequest);
}
