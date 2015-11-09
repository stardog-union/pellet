package com.complexible.pellet.client.api;

import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import org.semanticweb.owlapi.model.IRI;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
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

	@GET("/admin/shutdown")
	Call<GenericJsonMessage> shutdown();

	@POST("/reasoner/{ontology}/query")
	Call<ResponseBody> query(@Path("ontology") IRI theOntology,
	                         @Query("type") SchemaReasoner.QueryType theType,
	                         @Body RequestBody theQueryRequest);

	@POST("/reasoner/{ontology}/explain")
	Call<ResponseBody> explain(@Path("ontology") IRI theOntology,
	                           @Query("limit") int limit,
	                           @Body RequestBody theQueryRequest);

	@PUT("/reasoner/{ontology}")
	Call<GenericJsonMessage> update(@Path("ontology") IRI theOntology,
	                                @Body RequestBody theUpdateRequest);
}
