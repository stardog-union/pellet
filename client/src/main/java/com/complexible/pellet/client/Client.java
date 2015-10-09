package com.complexible.pellet.client;

import java.io.IOException;

import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.service.GenericJsonMessage;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class Client {

	public static void main(String[] args) throws IOException {
		Retrofit aRetrofit = new Retrofit.Builder().baseUrl("http://localhost:8080")
		                                           .addConverterFactory(GsonConverterFactory.create())
		                                           .build();

		// TODO: move this to a provider class (using guice)
		PelletService aPelletService = aRetrofit.create(PelletService.class);

		GenericJsonMessage aMessage = aPelletService.shutdown()
		                                 .execute()
		                                 .body();

		System.out.println(aMessage.message);
	}

}
