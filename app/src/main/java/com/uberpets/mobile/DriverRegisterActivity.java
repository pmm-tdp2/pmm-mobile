package com.uberpets.mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uberpets.Constants;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.DataFacebook;
import com.uberpets.model.RegisterDTO;
import com.uberpets.model.SimpleResponse;
import com.uberpets.services.App;
import com.uberpets.util.AccountSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private Long id = 0l;
    private EditText editNameDriver;
    private EditText editDniDriver;
    private EditText editPhoneDriver;
    private TextView sendingDataText;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY_CAR = 1, CAMERA_CAR = 2;
    private int GALLERY_LICENSE = 3, CAMERA_LICENSE = 4;
    private int GALLERY_INSURANCE = 5, CAMERA_INSURANCE = 6;
    private int GALLERY_PROFILE = 7, CAMERA_PROFILE = 8;
    private Map<Integer,String> imagesPath;

    @Override
    public Resources getResources() {
        return super.getResources();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        editNameDriver = findViewById(R.id.name_driver_facebook);
        editDniDriver = findViewById(R.id.dni_driver);
        editPhoneDriver = findViewById(R.id.phone_driver);
        sendingDataText = findViewById(R.id.sending_data_text);
        sendingDataText.setVisibility(View.INVISIBLE);
        addName();
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
        imagesPath =  new HashMap<>();
    }

    private void addName() {
        if( mDataFacebook != null)
            editNameDriver.setText(mDataFacebook.getName());
    }

    private void showPictureDialog(int code){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Seleccionar una acción");
        String[] pictureDialogItems = {
                "Elegir foto de la galeria",
                "Subir una foto desde la camara" };
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
        //Tengo que pedir permiso
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        requestCameraPermission();

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, code);
        }else{
            Toast.makeText(DriverRegisterActivity.this, "No tenes permisos para realizar esta acción.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCameraPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
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
                    saveImage(bitmap);
                    imagesPath.put(requestCode,generatePhotoProfileCoded(bitmap));
                    Toast.makeText(DriverRegisterActivity.this, "Imagen guardada!", Toast.LENGTH_SHORT).show();

                    if (requestCode == GALLERY_CAR) imageviewCar.setImageBitmap(bitmap);
                    else if (requestCode == GALLERY_LICENSE) imageviewLicense.setImageBitmap(bitmap);
                    else if (requestCode == GALLERY_INSURANCE) imageviewCarInusrance.setImageBitmap(bitmap);
                    else if (requestCode == GALLERY_PROFILE) imageviewProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DriverRegisterActivity.this, "Falló la carga de imagen", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode % 2 == 0) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            if (requestCode == CAMERA_CAR) imageviewCar.setImageBitmap(thumbnail);
            else if (requestCode == CAMERA_LICENSE) imageviewLicense.setImageBitmap(thumbnail);
            else if (requestCode == CAMERA_INSURANCE) imageviewCarInusrance.setImageBitmap(thumbnail);
            else if (requestCode == CAMERA_PROFILE) imageviewProfile.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            imagesPath.put(requestCode,generatePhotoProfileCoded(thumbnail));
            Toast.makeText(DriverRegisterActivity.this, "Imagen guardada!", Toast.LENGTH_SHORT).show();
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

    private RegisterDTO getRegisterDTO(){
       return new RegisterDTO.RegisterDTOBuilder( mDataFacebook.getIdFacebook(),
                this.editNameDriver.getText().toString(),
                imagesPath.containsKey(GALLERY_PROFILE) ?
                        imagesPath.get(GALLERY_PROFILE): imagesPath.get(CAMERA_PROFILE))
                .setDni(this.editDniDriver.getText().toString())
                .setPhone(editPhoneDriver.getText().toString())
                .setPhotoCar(imagesPath.containsKey(GALLERY_CAR)?
                        imagesPath.get(GALLERY_CAR) : imagesPath.get(CAMERA_CAR))
                .setPhotoInsurance(imagesPath.containsKey(GALLERY_INSURANCE)?
                        imagesPath.get(GALLERY_INSURANCE): imagesPath.get(CAMERA_INSURANCE))
                .setPhotoLicense(imagesPath.containsKey(GALLERY_LICENSE)?
                        imagesPath.get(GALLERY_LICENSE): imagesPath.get(CAMERA_LICENSE))
               .setRole("driver")
                .build();
    }

    private void sendDataToServer() {
        App.nodeServer.post("/api/register",
                getRegisterDTO(), SimpleResponse.class, new Headers())
                .run(this::handleResponseRegister,this::handleErrorRegister);
    }

    private void handleResponseRegister(SimpleResponse simpleResponse) {
        if(simpleResponse.getStatus() == 200) {
            //TODO: mostrar mensaje de que el registro fue exitoso y luego de un delay redirigir
            Intent intent = new Intent(this,DriverHome.class);
            startActivity(intent);
            //saving info session
            AccountSession.setLoginStatusValue(this,true);
            AccountSession.setLoginId(this,this.mDataFacebook.getIdFacebook());
            AccountSession.setRolLoggedValue(this, Constants.getInstance().getID_DRIVERS());
            finish();
        }
    }

    private void handleErrorRegister(Exception e) {
        Log.e(this.getClass().getName(),"Error in driver register");
        Log.e(this.getClass().getName(),e.toString());
        Toast.makeText(this,"Hubo un problema al registrar, inentelo más tarde",
                Toast.LENGTH_LONG).show();
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

    private String generatePhotoProfileCoded(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void finishRegister(View view) {
        sendingDataText.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.INVISIBLE);

        if (isUploadedCarImage && isUploadedLicenseImage
                && isUploadedInsuranceImage && isUploadedProfileImage
                && editNameDriver.getText().length() > 0
                && editDniDriver.getText().length() > 0
                && editPhoneDriver.getText().length() > 0) {

            sendDataToServer();
        }
        else{
            sendingDataText.setVisibility(View.INVISIBLE);
            continueButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Tenes que completar todos los campos para continuar",
                    Toast.LENGTH_LONG).show();
        }
    }

}
