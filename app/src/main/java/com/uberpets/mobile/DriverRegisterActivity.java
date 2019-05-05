package com.uberpets.mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.uberpets.model.DataFacebook;

public class DriverRegisterActivity extends AppCompatActivity {
    private DataFacebook mDataFacebook;
    private boolean isUploadedCarImage = false;
    private boolean isUploadedLicenseImage = false;
    private boolean isUploadedInsuranceImage = false;
    private boolean isUploadedProfileImage = false;
    private EditText editNameDriver;
    private EditText editDniDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        if (getIntent().hasExtra("DATA"))
            mDataFacebook = (DataFacebook)getIntent().getSerializableExtra("DATA");

        editNameDriver = findViewById(R.id.name_driver_facebook);
        editDniDriver = findViewById(R.id.dni_driver);
        addName();
    }

    private void addName() {
        if( mDataFacebook != null)
            editNameDriver.setText(mDataFacebook.getName());
    }


    public void uploadImageCar(View view) {
        isUploadedCarImage = true;
    }

    public void uploadImageProfile(View view) {
        isUploadedProfileImage = true;
    }

    public void uploadImageInsurance(View view) {
        isUploadedInsuranceImage = true;
    }

    public void uploadImageLicense(View view) {
        isUploadedLicenseImage = true;
    }

    public void buttonFinishRegister(View view) {
        if (isUploadedCarImage && isUploadedLicenseImage
                && isUploadedInsuranceImage && isUploadedProfileImage
                && editNameDriver.getText().length() > 0
                && editDniDriver.getText().length() > 0) {
            Intent intent = new Intent(this, DriverHome.class);
            startActivity(intent);
        }
    }

}
