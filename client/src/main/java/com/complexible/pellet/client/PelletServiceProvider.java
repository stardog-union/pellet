package com.complexible.pellet.client;

import com.complexible.pellet.client.api.PelletService;
import com.google.inject.Provider;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * TODO: add parameter for custom endpoint
 *
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceProvider implements Provider<PelletService> {

	private static final String DEFAULT_ENDPOINT = "http://localhost:8080";

	PelletService mPelletService = null;

	@Override
	public PelletService get() {
		if (mPelletService == null) {
			Retrofit aRetrofit = new Retrofit.Builder().baseUrl(DEFAULT_ENDPOINT)
			                                           .addConverterFactory(GsonConverterFactory.create())
			                                           .build();

			mPelletService = aRetrofit.create(PelletService.class);
		}

		return mPelletService;
	}
}
