package com.uberpets.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.uberpets.model.DataFacebook;

public class DriverRegisterActivity extends AppCompatActivity {
    private DataFacebook mDataFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        mDataFacebook = (DataFacebook)getIntent().getSerializableExtra("DATA");
        addName();
    }

    private void addName() {
        EditText editName = findViewById(R.id.name_driver_facebook);
        editName.setText(mDataFacebook.getName());
    }
}
