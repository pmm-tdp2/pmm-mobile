package com.uberpets.services;

import android.app.Application;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.uberpets.Constants;
import com.uberpets.library.rest.RestService;


public class App extends Application {
    //public static final String BASE_URL = Constants.getInstance().getURL();
    public static RestService nodeServer;

/*    static {
        Config.setupNetwork();
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        RequestQueue volleyQueue = Volley.newRequestQueue(context);
//        nodeServer = new RestService(BASE_URL, volleyQueue);
        nodeServer = new RestService(volleyQueue);

    }
}