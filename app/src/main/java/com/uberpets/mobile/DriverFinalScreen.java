package com.uberpets.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DriverFinalScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_final_screen);
    }

    public void sendComment(android.view.View view){
        Intent intent = new Intent(this, DriverHome.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        //TODO: ver si hacemos algo aca
    }
}
