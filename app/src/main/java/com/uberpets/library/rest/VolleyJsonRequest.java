package com.uberpets.library.rest;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.uberpets.library.fun.Async;
import com.uberpets.library.fun.Consumer;
import com.google.gson.GsonBuilder;


public class VolleyJsonRequest<T> implements Async<T> {

    private static final Gson gson = new GsonBuilder()
            .create();

    private RequestQueue queue;
    private final int method;
    private final String url;
    private final Object request;
    private final Class<T> clazz;
    private final Map<String, String> headers;


    public VolleyJsonRequest(RequestQueue queue
            , int method
            , String url
            , Object request
            , Class<T> clazz
            , Map<String, String> headers) {
        this.queue = queue;
        this.method = method;
        this.url = url;
        this.request = request;
        this.clazz = clazz;
        this.headers = headers;
    }

    public void run(Consumer<T> onSuccess
            , Consumer<Exception> onError) {
        final String requestBody = gson.toJson(this.request);
        final JsonRequest<T> request = new JsonRequest<T>(method
                , url
                , requestBody
                , onSuccess::accept
                , onError::accept) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Response<T> parseNetworkResponse(NetworkResponse response) {
                try {
                    String responseBody = new String(response.data);
                    T result = gson.fromJson(responseBody
                            , clazz);
                    Log.d("Rest", url
                            + "\n Request:" + requestBody);
                    Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
                    Log.d("Rest", url
                            + "\n Response:" + responseBody);
                    return Response.success(result, entry);
                } catch (Exception e) {
                    Log.e("Rest", url, e);
                    return Response.error(new ParseError(e));
                }
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                60*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


}