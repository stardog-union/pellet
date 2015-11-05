package com.complexible.pellet.client.api;

import com.clarkparsia.pellet.json.GenericJsonMessage;
import com.complexible.pellet.service.reasoner.SchemaReasoner;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import org.semanticweb.owlapi.model.IRI;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
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

	@Multipart
	@PUT("/reasoner/{ontology}")
	Call<ResponseBody> update(@Path("ontology") IRI theOntology,
	                          @Part("additions") RequestBody theAdditions,
	                          @Part("removals") RequestBody theRemovals);
}
