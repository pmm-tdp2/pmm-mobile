package com.uberpets;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    private static final Constants ourInstance = new Constants();

    private String URL_REMOTE;
    private String URL_LOCAL;
    private String URL_BASE_PATH;
    private String URL;

    private String EVENT_POSITON_DRIVER;
    private String EVENT_DRIVER_ARRIVED_DESTINY;
    private String EVENT_CONNECTION;
    private String EVENT_DRIVER_ARRIVED_USER;

    private int REQUEST_AUTOCOMPLETE_ACTIVITY;
    private int RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY;
    private int RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY;
    private int RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY;

    private String TypeUrl;

     private String[] SOCKET_IO_TRANSPORT;

    public static Constants getInstance() {
        return ourInstance;
    }

    public void setIpToConnect(String ip) {
        switch (ip){
            case "LOCALHOST":
                URL = URL_LOCAL + URL_BASE_PATH;
                break;

            case "SERVER_CLOUD":
                URL = URL_REMOTE + URL_BASE_PATH;
                break;

            default:
                URL = "http://"+ip+":8081" +URL_BASE_PATH;
        }
    }


    private Constants() {
        Properties prop =  new Properties();
        String propFileName = "/application.properties";

        try (InputStream input = Constants.class.getResourceAsStream(propFileName)) {
            if (input != null) {
                prop.load(input);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            URL_REMOTE = prop.getProperty("url.remote_server");
            URL_LOCAL = prop.getProperty("url.local_server");
            URL_BASE_PATH = prop.getProperty("url.base_path");

            EVENT_POSITON_DRIVER = prop.getProperty("event.position_driver");
            EVENT_DRIVER_ARRIVED_DESTINY = prop.getProperty("event.driver_arrived_destiny");
            EVENT_CONNECTION = prop.getProperty("event.connection");
            EVENT_DRIVER_ARRIVED_USER = prop.getProperty("event.driver_arrived_user");

            REQUEST_AUTOCOMPLETE_ACTIVITY = Integer.parseInt(prop.getProperty("intent.request_autocomplete_activity"));
            RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY = Integer.parseInt(prop.getProperty("intent.autocomplete_response_origin"));
            RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY = Integer.parseInt(prop.getProperty("intent.autocomplete_response_destiny"));
            RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY = Integer.parseInt(prop.getProperty("intent.autocomplete_response_route"));

            SOCKET_IO_TRANSPORT = new String[] {prop.getProperty("socket.io.transport")};
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e("CONSTANTS",ex.getMessage());
        }

    }



    public String getEVENT_POSITON_DRIVER() {
        return EVENT_POSITON_DRIVER;
    }

    /*public String getURL_REMOTE() {
        return URL_REMOTE;
    }

    //public String getURL_BASE_PATH() {
        return URL_BASE_PATH;
    }

    public String getURL_LOCAL() {
        return URL_LOCAL;
    }*/

    public String getURL(){
        return URL;
    }

    public String getEVENT_DRIVER_ARRIVED_DESTINY() {
        return EVENT_DRIVER_ARRIVED_DESTINY;
    }

    public String getEVENT_CONNECTION() {
        return EVENT_CONNECTION;
    }

    public String getEVENT_DRIVER_ARRIVED_USER() {
        return EVENT_DRIVER_ARRIVED_USER;
    }

    public int getREQUEST_AUTOCOMPLETE_ACTIVITY() {
        return REQUEST_AUTOCOMPLETE_ACTIVITY;
    }

    public int getRESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY() {
        return RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY;
    }

    public int getRESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY() {
        return RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY;
    }

    public int getRESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY() {
        return RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY;
    }

    public String[] getSOCKET_IO_TRANSPORT() {
        return SOCKET_IO_TRANSPORT;
    }

}
