package com.complexible.pellet.client;

import java.util.concurrent.TimeUnit;

import com.complexible.pellet.client.api.PelletService;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.squareup.okhttp.OkHttpClient;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * TODO: add parameter for custom endpoint
 *
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceProvider implements Provider<PelletService> {

	PelletService mPelletService = null;

	private final String mEndpoint;

	private final long mConnTimeoutMin;

	private final long mReadTimeoutMin;

	private final long mWriteTimeoutMin;

	@Inject
	public PelletServiceProvider(@Named("endpoint") final String theEndpoint,
	                             @Named("conn_timeout") final long theConnTimeout,
	                             @Named("read_timeout") final long theReadTimeout,
	                             @Named("write_timeout") final long theWriteTimeout) {
		mEndpoint = !Strings.isNullOrEmpty(theEndpoint) ? theEndpoint
		                                                : PelletService.DEFAULT_LOCAL_ENDPOINT;
		mConnTimeoutMin = theConnTimeout;
		mReadTimeoutMin = theReadTimeout;
		mWriteTimeoutMin = theWriteTimeout;
	}

	@Override
	public PelletService get() {
		if (mPelletService == null) {
			final OkHttpClient httpClient = new OkHttpClient();
			httpClient.setConnectTimeout(mConnTimeoutMin, TimeUnit.MINUTES);
			httpClient.setReadTimeout(mReadTimeoutMin, TimeUnit.MINUTES);
			httpClient.setWriteTimeout(mWriteTimeoutMin, TimeUnit.MINUTES);

			final Retrofit aRetrofit = new Retrofit.Builder().baseUrl(mEndpoint)
			                                                 .addConverterFactory(GsonConverterFactory.create())
			                                                 .build();

			mPelletService = aRetrofit.create(PelletService.class);
		}

		return mPelletService;
	}
}
