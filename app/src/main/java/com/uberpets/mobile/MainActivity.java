package com.uberpets.mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.uberpets.Constants;

public class MainActivity extends AppCompatActivity {

    //Declare a private RequestQueue variable
    private RequestQueue requestQueue;

    private static MainActivity mInstance;
    private RadioButton radioCloudServer;
    private RadioButton radioWriteIp;
    private EditText ip_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button driverButton = findViewById(R.id.driverButton);
        radioCloudServer = findViewById(R.id.cloud_server);
        radioWriteIp = findViewById(R.id.local_ip_configurable);
        ip_input = findViewById(R.id.ip_input);
    }

    public boolean isIpServerSelected(){
        boolean success = false;
        if (radioCloudServer.isChecked()){
            Constants.getInstance().setIpToConnect("SERVER_CLOUD");
            success = true;
        }else if (radioWriteIp.isChecked() && ip_input.getText().length() != 0){
            Constants.getInstance().setIpToConnect(ip_input.getText().toString());
            success = true;
        }
        return success;
    }

    public void goToDriverHome(View view){
        if (isIpServerSelected()){
            Intent intent = new Intent(this, DriverHome.class);
            startActivity(intent);
        }
    }

    public void goToUserHome(View view){
        if (isIpServerSelected()) {
            Intent intent = new Intent(this, UserHome.class);
            startActivity(intent);
        }
    }

/*    public void goToLoginView(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }*/

    public void goToTab(View view){
        if (isIpServerSelected()){
            Intent intent = new Intent(this, TabLoginActivity.class);
            startActivity(intent);
        }
    }

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }
    /*
    Create a getRequestQueue() method to return the instance of
    RequestQueue.This kind of implementation ensures that
    the variable is instatiated only once and the same
    instance is used throughout the application
    */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplication());
        return requestQueue;
    }
    /*
    public method to add the Request to the the single
    instance of RequestQueue created above.Setting a tag to every
    request helps in grouping them. Tags act as identifier
    for requests and can be used while cancelling them
    */
    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }
    /*
    Cancel all the requests matching with the given tag
    */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }


    public void goRegisterDriver(View view){
        if(isIpServerSelected()){
            Intent intent = new Intent(this, DriverRegisterActivity.class);
            startActivity(intent);
        }
    }

    public void goRegisterUser(View view){
        if(isIpServerSelected()){
            Intent intent = new Intent(this, UserRegisterActivity.class);
            startActivity(intent);
        }
    }

}
