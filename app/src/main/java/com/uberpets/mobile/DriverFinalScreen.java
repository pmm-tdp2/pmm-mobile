package com.uberpets.mobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DriverFinalScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_final_screen);
    }

    public void sendComment(android.view.View view){
        //Intent intent = new Intent(this, DriverHome.class);
        //startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        //TODO: ver si hacemos algo aca
    }
}
