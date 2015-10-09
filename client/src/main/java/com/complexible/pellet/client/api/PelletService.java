package com.complexible.pellet.client.api;

import com.complexible.pellet.service.GenericJsonMessage;
import retrofit.Call;
import retrofit.http.GET;

/**
 * Interface definition for Pellet Service.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PelletService {

	@GET("/shutdown")
	Call<GenericJsonMessage> shutdown();

}
