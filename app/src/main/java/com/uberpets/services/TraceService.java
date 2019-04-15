package com.uberpets.services;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.LoginFilter;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.uberpets.Constants;
import com.uberpets.mobile.MainActivity;
import com.uberpets.model.TraceDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class TraceService {

    //private final String URL = "https://young-wave-26125.herokuapp.com";
    private final String URL = Constants.getInstance().getURL_REMOTE() + Constants.getInstance().getURL_BASE_PATH();

    public void saveTrace(TraceDTO traceDTO, AppCompatActivity activity) {

        final String userId = traceDTO.getUserId();
        final String driverId = traceDTO.getDriverId();
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = URL + "/trace";

        JSONObject traceJsonObject = new JSONObject();
        try{
            traceJsonObject.put("userId",userId);
            traceJsonObject.put("driverId",driverId);
            JSONObject gcJsonObject = new JSONObject();
            gcJsonObject.put("latitude", traceDTO.getGeograficCoordenate().getLatitude());
            gcJsonObject.put("longitude", traceDTO.getGeograficCoordenate().getLongitude());
            traceJsonObject.put("geograficCoordenate", gcJsonObject);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, traceJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("INFORMATIVE", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR",error.toString(),error);
            }
        });
        // Adding the request to the queue along with a unique string tag
        MainActivity.getInstance().addToRequestQueue(jsonObjectRequest, "postRequest");
    }

}
