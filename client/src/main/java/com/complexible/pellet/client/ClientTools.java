package com.complexible.pellet.client;

import java.io.IOException;

import com.google.common.base.Throwables;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Set of static tools to work with client API
 *
 * @author edgar
 */
public final class ClientTools {

	public static <O> O executeCall(final Call<O> theCall) {
		O results = null;

		try {
			Response<O> aResp = theCall.execute();

			if (!aResp.isSuccess()) {
				throw new ClientException(String.format("Request call failed: [%d] %s",
				                                        aResp.code(), aResp.message()));
			}

			results = aResp.body();
		}
		catch (IOException theE) {
			Throwables.propagate(new ClientException(theE.getMessage(), theE));
		}
		catch (ClientException theE) {
			Throwables.propagate(theE);
		}

		return results;
	}
}
