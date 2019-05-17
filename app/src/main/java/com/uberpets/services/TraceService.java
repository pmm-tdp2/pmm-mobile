package com.uberpets.services;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uberpets.Constants;
import com.uberpets.mobile.MainActivity;
import com.uberpets.model.TraceDTO;

import org.json.JSONException;
import org.json.JSONObject;

public class TraceService {

    //private final String URL = "https://young-wave-26125.herokuapp.com";
//    private final String URL = Constants.getInstance().getURL_REMOTE() + Constants.getInstance().getURL_BASE_PATH();
    private final String URL = Constants.getInstance().getURL();
    private final String TAG_TRAVEL_SERVICE = "TRAVEL_SERVICE";

    public void saveTrace(TraceDTO traceDTO, AppCompatActivity activity) {

        final int userId = traceDTO.getUserId();
        final int driverId = traceDTO.getDriverId();
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
                Log.i("TAG_TRAVEL_SERVICE", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_TRAVEL_SERVICE",error.toString(),error);
            }
        });
        // Adding the request to the queue along with a unique string tag
        MainActivity.getInstance().addToRequestQueue(jsonObjectRequest, "postRequest");
    }

}
