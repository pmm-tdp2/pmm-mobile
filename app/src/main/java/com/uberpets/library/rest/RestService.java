package com.uberpets.library.rest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.uberpets.Constants;
import com.uberpets.library.fun.Async;

public class RestService {
    //private final String url;
    private final RequestQueue queue;

    /*public RestService(String url, RequestQueue queue) {
        this.url = url;
        this.queue = queue;
    }*/
    public RestService( RequestQueue queue) {
        this.queue = queue;
    }

    public <T> Async<T> post(String path
            , Object request
            , Class<T> responseClass, Headers headers) {
        return new VolleyJsonRequest<>(queue
                , Request.Method.POST
                , Constants.getInstance().getURL() + path
                , request
                , responseClass
                , headers.headers
        );
    }

    public <T> Async<T> put(String path
            , Object request
            , Class<T> responseClass, Headers headers) {
        return new VolleyJsonRequest<>(queue
                , Request.Method.PUT
                , Constants.getInstance().getURL() + path
                , request
                , responseClass
                , headers.headers
        );
    }

    public <T> Async<T> get(String path
            , Class<T> responseClass, Headers headers) {
        return new VolleyJsonRequest<>(queue
                , Request.Method.GET
                , Constants.getInstance().getURL() + path
                , null
                , responseClass
                , headers.headers
        );
    }

    public <T> Async<T> get(Query queryParams
            , Class<T> responseClass, Headers headers) {
        return new VolleyJsonRequest<>(queue
                , Request.Method.GET
                , Constants.getInstance().getURL() + queryParams.string
                , null
                , responseClass
                , headers.headers
        );
    }
}

