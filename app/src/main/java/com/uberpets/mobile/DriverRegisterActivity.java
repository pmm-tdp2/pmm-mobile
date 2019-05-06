package com.uberpets.mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.uberpets.model.DataFacebook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class DriverRegisterActivity extends AppCompatActivity {
    private DataFacebook mDataFacebook;
    private ImageView imageviewCar;
    private ImageView imageviewCarInusrance;
    private ImageView imageviewLicense;
    private ImageView imageviewProfile;
    private Button continueButton;
    private boolean isUploadedCarImage = false;
    private boolean isUploadedLicenseImage = false;
    private boolean isUploadedInsuranceImage = false;
    private boolean isUploadedProfileImage = false;
    private EditText editNameDriver;
    private EditText editDniDriver;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY_CAR = 1, CAMERA_CAR = 2;
    private int GALLERY_LICENSE = 3, CAMERA_LICENSE = 4;
    private int GALLERY_INSURANCE = 5, CAMERA_INSURANCE = 6;
    private int GALLERY_PROFILE = 7, CAMERA_PROFILE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        editNameDriver = findViewById(R.id.name_driver_facebook);
        editDniDriver = findViewById(R.id.dni_driver);
        imageviewCar = findViewById(R.id.imageview_car);
        imageviewCar.setOnClickListener(view -> uploadImageCar(view));
        imageviewCarInusrance = findViewById(R.id.imageview_car_insurance);
        imageviewCarInusrance.setOnClickListener(view -> uploadImageCarInsurance(view));
        imageviewLicense = findViewById(R.id.imageview_license);
        imageviewLicense.setOnClickListener(view -> uploadImageLicense(view));
        imageviewProfile = findViewById(R.id.imageview_profile);
        imageviewProfile.setOnClickListener(view -> uploadImageProfile(view));

        continueButton = findViewById(R.id.end_register_driver_btn);
        continueButton.setOnClickListener(view -> finishRegister(view));

        if (getIntent().hasExtra("DATA"))
            mDataFacebook = (DataFacebook)getIntent().getSerializableExtra("DATA");
        addName();
    }

    private void addName() {
        if( mDataFacebook != null)
            editNameDriver.setText(mDataFacebook.getName());
    }

    private void showPictureDialog(int code){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallery(code);
                            break;
                        case 1:
                            takePhotoFromCamera(code+1);
                            break;
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery(int code) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, code);
    }

    private void takePhotoFromCamera(int code) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode % 2 != 0) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(DriverRegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

                    if (requestCode == GALLERY_CAR) imageviewCar.setImageBitmap(bitmap);
                    else if (requestCode == GALLERY_LICENSE) imageviewLicense.setImageBitmap(bitmap);
                    else if (requestCode == GALLERY_INSURANCE) imageviewCarInusrance.setImageBitmap(bitmap);
                    else if (requestCode == GALLERY_PROFILE) imageviewProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DriverRegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode % 2 == 0) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            if (requestCode == CAMERA_CAR) imageviewCar.setImageBitmap(thumbnail);
            else if (requestCode == CAMERA_LICENSE) imageviewCar.setImageBitmap(thumbnail);
            else if (requestCode == CAMERA_INSURANCE) imageviewCar.setImageBitmap(thumbnail);
            else if (requestCode == CAMERA_PROFILE) imageviewProfile.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(DriverRegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void uploadImageCar(View view) {
        showPictureDialog(GALLERY_CAR);
        isUploadedCarImage = true;
    }

    public void uploadImageCarInsurance(View view) {
        showPictureDialog(GALLERY_INSURANCE);
        isUploadedInsuranceImage = true;
    }

    public void uploadImageLicense(View view) {
        showPictureDialog(GALLERY_LICENSE);
        isUploadedLicenseImage = true;
    }

    public void uploadImageProfile(View view) {
        showPictureDialog(GALLERY_PROFILE);
        isUploadedProfileImage = true;
    }

    public void finishRegister(View view) {
        if (isUploadedCarImage && isUploadedLicenseImage
                && isUploadedInsuranceImage && isUploadedProfileImage
                && editNameDriver.getText().length() > 0
                && editDniDriver.getText().length() > 0) {

            Intent intent = new Intent(this, DriverHome.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Tenes que completar todos los campos para continuar",
                    Toast.LENGTH_LONG).show();
        }
    }

}
