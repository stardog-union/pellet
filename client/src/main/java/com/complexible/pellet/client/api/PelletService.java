package com.complexible.pellet.client.api;

import com.complexible.pellet.service.Message;
import retrofit.Call;
import retrofit.http.GET;

/**
 * Interface definition for Pellet Service.
 *
 * @author Edgar Rodriguez-Diaz
 */
public interface PelletService {

	@GET("/shutdown")
	Call<Message> shutdown();

}
