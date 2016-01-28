package com.complexible.pellet.client.api;

import java.util.UUID;

import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.google.gson.JsonObject;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.semanticweb.owlapi.model.IRI;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface definition for Pellet Service.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PelletService {

	String DEFAULT_LOCAL_ENDPOINT = "http://localhost:18080";

	@GET("/admin/shutdown")
	Call<Void> shutdown();

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

	@POST("/reasoner/{ontology}")
	Call<Void> update(@Path("ontology") IRI theOntology,
	                  @Query("client") UUID theClientID,
	                  @Body RequestBody theUpdateRequest);

	@GET("/reasoner/{ontology}/version")
	Call<Integer> version(@Path("ontology") IRI theOntology,
	                      @Query("client") UUID theClientID);

	@PUT("/reasoner/{ontology}")
	Call<Void> add(@Path("ontology") String theOntologyPath);

	@DELETE("/reasoner/{ontology}")
	Call<Void> remove(@Path("ontology") IRI theOntology);
}
