package com.complexible.pellet.client;

import java.io.IOException;

import com.complexible.pellet.client.api.PelletService;
import com.complexible.pellet.service.Message;
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

		PelletService aPelletService = aRetrofit.create(PelletService.class);

		Message aMessage = aPelletService.shutdown()
		                                 .execute()
		                                 .body();

		System.out.println(aMessage.message);
	}

}
