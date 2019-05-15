package com.uberpets.mobile;

import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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

public class UserRegisterActivity extends AppCompatActivity {
    private DataFacebook mDataFacebook;
    private ImageView imageviewProfile;
    private Button continueButton;
    private EditText editName;
    private String absolutePathPhotoProfile;
    private boolean isUploadedPhotoProfile;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        imageviewProfile = findViewById(R.id.imageview_profile);
        imageviewProfile.setOnClickListener(view -> uploadImageProfile(view));

        continueButton = findViewById(R.id.end_register_user_btn);
        continueButton.setOnClickListener(view -> finishRegister(view));

        editName = findViewById(R.id.name_user_facebook);

        getExtrasPreviousActivity();
        addName();


    }


    private void getExtrasPreviousActivity() {
        if (getIntent().hasExtra("DATA"))
            mDataFacebook = (DataFacebook)getIntent().getSerializableExtra("DATA");

        // if photo profile of user is captured
        // no is required that user uploaded photo
        if (getIntent().hasExtra("PROFILE")) {
            this.absolutePathPhotoProfile = getIntent().getStringExtra("PROFILE");
            isUploadedPhotoProfile = true;
        }
    }

    private void addName() {
        if(mDataFacebook != null) {
            editName.setText(mDataFacebook.getName());
        }
    }

    public void uploadImageProfile(View view) {
        showPictureDialog(GALLERY);
        isUploadedPhotoProfile = true;
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
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    this.absolutePathPhotoProfile = saveImage(bitmap);
                    Toast.makeText(UserRegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageviewProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserRegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");imageviewProfile.setImageBitmap(thumbnail);
            this.absolutePathPhotoProfile = saveImage(thumbnail);
            Toast.makeText(UserRegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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

    public void finishRegister(View view){
        if ( editName.length() >0 &&
                isUploadedPhotoProfile){
            sendDataToServer();
        }else{
            Toast.makeText(this, "Tu nombre no puede estar incompleto",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void sendDataToServer() {
        App.nodeServer.post("/userCredentials/register",
                getRegisterDTO(), SimpleResponse.class, new Headers())
                .run(this::handleResponseRegister,this::handleErrorRegister);
    }

    private RegisterDTO getRegisterDTO(){
        return new RegisterDTO.RegisterDTOBuilder(editName.getText().toString(),
                this.absolutePathPhotoProfile).build();
    }

    private void handleResponseRegister(SimpleResponse simpleResponse) {
        if(simpleResponse.getStatus() == 200) {
            //TODO: mostrar mensaje de que el registro fue exitoso y luego de un delay redirigir
            //save session of user
            AccountSession.setRolLoggedValue(this,
                    Constants.getInstance().getID_USERS());
            AccountSession.setLoginStatusValue(this,true);
            Intent intent = new Intent(this, UserHome.class);
            startActivity(intent);
        }
    }

    private void handleErrorRegister(Exception e) {
        Log.e(this.getClass().getName(),"Error in driver register");
        Log.e(this.getClass().getName(),e.toString());
        Toast.makeText(this,"Hubo un problema al registrar, inentelo más tarde",
                Toast.LENGTH_LONG).show();
    }


}
