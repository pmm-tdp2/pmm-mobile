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
import com.uberpets.model.DataFacebook;

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
    private boolean isUploadedProfileImage;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        if (getIntent().hasExtra("DATA"))
            mDataFacebook = (DataFacebook)getIntent().getSerializableExtra("DATA");
        addName();

        imageviewProfile = findViewById(R.id.imageview_profile);
        imageviewProfile.setOnClickListener(view -> uploadImageProfile(view));

        continueButton = findViewById(R.id.end_register_user_btn);
        continueButton.setOnClickListener(view -> finishRegister(view));

        editName = findViewById(R.id.name_user_facebook);
    }

    private void addName() {
        if(mDataFacebook != null) {
            editName.setText(mDataFacebook.getName());
        }
    }

    public void uploadImageProfile(View view) {
        showPictureDialog(GALLERY);
        isUploadedProfileImage = true;
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
                    String path = saveImage(bitmap);
                    Toast.makeText(UserRegisterActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageviewProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserRegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");imageviewProfile.setImageBitmap(thumbnail);
            saveImage(thumbnail);
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
        if ( isUploadedProfileImage
        && editName.length() >0 ){
            Intent intent = new Intent(this, UserHome.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Tenes que completar todos los campos para continuar",
                    Toast.LENGTH_LONG).show();
        }

    }

}
