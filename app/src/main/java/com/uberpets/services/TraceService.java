package com.uberpets.services;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.LoginFilter;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uberpets.mobile.MainActivity;
import com.uberpets.model.TraceDTO;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class TraceService {

    //private final String URL = "https://young-wave-26125.herokuapp.com";
    private final String URL = "http://192.168.0.6:8081/pmm";

    public void saveTrace(TraceDTO traceDTO, AppCompatActivity activity) {
        son gson = new Gson();
        String json = gson.toJson(traceDTO);

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = URL + "/trace";

        StringRequest response = new StringRequest(Request.Method.POST,
                url, json,
                new Response.Listener() {
                    @Override
                    public void onResponse(StringRequest response) {
                        Log.i("INFORMATIVE", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e("ERROR", e.getMessage());
                    }
                });
        // Adding the request to the queue along with a unique string tag
        MainActivity.getInstance().addToRequestQueue(response, "postRequest");
    }
}
