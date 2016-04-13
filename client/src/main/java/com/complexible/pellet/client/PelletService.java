package com.complexible.pellet.client;

import java.util.UUID;

import com.clarkparsia.pellet.service.reasoner.SchemaQuery;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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
	Call<NodeSet> query(@Path("ontology") IRI theOntology,
	                    @Query("client") UUID theClientID,
	                    @Body SchemaQuery query);

	@POST("/reasoner/{ontology}/explain")
	Call<OWLOntology> explain(@Path("ontology") IRI theOntology,
	                           @Query("client") UUID theClientID,
	                           @Query("limit") int limit,
	                           @Body OWLAxiom inference);

	@POST("/reasoner/{ontology}/insert")
	Call<Void> insert(@Path("ontology") IRI theOntology,
	                  @Query("client") UUID theClientID,
	                  @Body OWLOntology axioms);

	@POST("/reasoner/{ontology}/delete")
	Call<Void> delete(@Path("ontology") IRI theOntology,
	                  @Query("client") UUID theClientID,
	                  @Body OWLOntology axioms);

	@GET("/reasoner/{ontology}/classify")
	Call<Void> classify(@Path("ontology") IRI theOntology,
	                    @Query("client") UUID theClientID);

	@GET("/reasoner/{ontology}/version")
	Call<Integer> version(@Path("ontology") IRI theOntology,
	                      @Query("client") UUID theClientID);

	@PUT("/reasoner/{ontology}")
	Call<Void> load(@Path("ontology") String theOntologyPath);

	@DELETE("/reasoner/{ontology}")
	Call<Void> unload(@Path("ontology") IRI theOntology);
}
