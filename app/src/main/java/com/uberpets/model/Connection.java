package com.uberpets.model;

import android.app.Activity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


public class Connection extends Activity {
    private Socket mSocket;
    private final String EVENT_POSITON_DRIVER = "POSITION DRIVER";
    private final String URL = "http://young-wave-26125.herokuapp.com/travels";
    private List<Activity> observers = new ArrayList<>();
    private List<LatLng> positions = new ArrayList<>();

    public Connection(Activity activity){
        this.observers.add(activity);
    }

    public void addActivity(Activity activity){
        this.observers.add(activity);
    }

    public void removeActivity(Activity activity){
        this.observers.remove(activity);
    }


    public void notifyAll(LatLng position) {
        Iterator<Activity> it = this.observers.iterator();
        while(it.hasNext()){
            it.next();
        }
    }

    {
        try {
            mSocket = IO.socket(URL);
        } catch (URISyntaxException e) {}
    }


    public void connectServer(){
        mSocket.connect();
    }

    public void disconnectServer(){
        mSocket.disconnect();
    }

    public void listenPositionsDriver(){
        mSocket.on(EVENT_POSITON_DRIVER, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String latitude;
                        String longitude;
                        try {
                            latitude = data.getString("latitude");
                            longitude = data.getString("longitude");
                            positions.add(new LatLng(Float.parseFloat(latitude),Float.parseFloat(longitude)));
                        } catch (JSONException e) {
                            return;
                        }


                    }
                });
            }
        });
    }


}
