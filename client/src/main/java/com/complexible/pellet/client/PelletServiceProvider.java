package com.complexible.pellet.client;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.complexible.pellet.client.api.PelletService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Provides the Pellet Service API client.
 *
 * @author Edgar Rodriguez-Diaz
 */
public class PelletServiceProvider implements Provider<PelletService> {

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
		final OkHttpClient httpClient = new OkHttpClient.Builder()
												.connectTimeout(mConnTimeoutMin, TimeUnit.MINUTES)
												.readTimeout(mReadTimeoutMin, TimeUnit.MINUTES)
												.writeTimeout(mWriteTimeoutMin, TimeUnit.MINUTES)
				                                .build();

		final Retrofit aRetrofit = new Retrofit.Builder().baseUrl(mEndpoint)
		                                                 .client(httpClient)
		                                                 .addConverterFactory(PRIMITIVE_FACTORY)
		                                                 .build();

		return aRetrofit.create(PelletService.class);
	}

	private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain; charset=UTF-8");
	private static final Set<Object> PRIMITIVE_TYPES = ImmutableSet.<Object>of(Boolean.class, Double.class, Float.class, Integer.class, Long.class, Short.class);

	private static final Converter.Factory PRIMITIVE_FACTORY = new Converter.Factory() {
		public Converter<Object, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
			return PRIMITIVE_TYPES.contains(type) ? PRIMITIVE_REQUEST_CONVERTER : null;
		}

		@Override
		public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
			if (type == Boolean.class) {
				return BOOLEAN_CONVERTER;
			}
			else if (type == Double.class) {
				return DOUBLE_CONVERTER;
			}
			else if (type == Float.class) {
				return FLOAT_CONVERTER;
			}
			else if (type == Integer.class) {
				return INTEGER_CONVERTER;
			}
			else if (type == Long.class) {
				return LONG_CONVERTER;
			}
			else if (type == Short.class) {
				return SHORT_CONVERTER;
			}
			return null;
		}
	};

	private static final Converter<Object, RequestBody> PRIMITIVE_REQUEST_CONVERTER = new Converter<Object, RequestBody>() {
		public RequestBody convert(Object value) throws IOException {
			return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
		}
	};

	private static final Converter<ResponseBody, Boolean> BOOLEAN_CONVERTER = new Converter<ResponseBody, Boolean>() {
		public Boolean convert(ResponseBody body) throws IOException {
			return Boolean.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Double> DOUBLE_CONVERTER = new Converter<ResponseBody, Double>() {
		public Double convert(ResponseBody body) throws IOException {
			return Double.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Float> FLOAT_CONVERTER = new Converter<ResponseBody, Float>() {
		public Float convert(ResponseBody body) throws IOException {
			return Float.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Integer> INTEGER_CONVERTER = new Converter<ResponseBody, Integer>() {
		public Integer convert(ResponseBody body) throws IOException {
			return Integer.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Long> LONG_CONVERTER = new Converter<ResponseBody, Long>() {
		public Long convert(ResponseBody body) throws IOException {
			return Long.valueOf(body.string());
		}
	};

	private static final Converter<ResponseBody, Short> SHORT_CONVERTER = new Converter<ResponseBody, Short>() {
		public Short convert(ResponseBody body) throws IOException {
			return Short.valueOf(body.string());
		}
	};
}
