package com.uberpets.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.uberpets.mobile.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GMapV2DirectionAsyncTask extends AsyncTask<String, Void, Document> {

    private final static String TAG = GMapV2DirectionAsyncTask.class.getSimpleName();
    private Handler handler;
    private LatLng start, end;
    private String mode;
    private Context context;

    public GMapV2DirectionAsyncTask(Handler handler, LatLng start, LatLng end, String mode, Context context) {
        this.start = start;
        this.end = end;
        this.mode = mode;
        this.handler = handler;
        this.context = context;
    }

    @Override
    protected Document doInBackground(String... params) {

        String url = "https://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=" + mode
                +"&key="+ context.getString(R.string.google_maps_key);
        Log.d("url", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document result) {
        if (result != null) {
            Log.d(TAG, "---- GMapV2DirectionAsyncTask OK ----");
            Message message = new Message();
            message.obj = result;
            handler.dispatchMessage(message);
        } else {
            Log.d(TAG, "---- GMapV2DirectionAsyncTask ERROR ----");
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}