package com.uberpets.mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.uberpets.Constants;
import com.uberpets.model.DataFacebook;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class obtain name, last name and photo of user or driver
 * tho show it to user or driver
 * and the case of driver obtain additionally
 * -
 */
public class WelcomeToAppActivity extends AppCompatActivity {

    private Constants mConstant = Constants.getInstance();
    private DataFacebook mDataFacebook;
    private String absolutePathProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_app);
        if (getIntent().hasExtra("DATA"))
            mDataFacebook = (DataFacebook)getIntent().getSerializableExtra("DATA");

        TextView textView = findViewById(R.id.ROL_TEXT);
        textView.setText(getIntent().getStringExtra(mConstant.getID_ROL()));

        setProfileToView();
    }

    public DataFacebook getDataFacebook() {
        return mDataFacebook;
    }

    @Override
    public void onBackPressed() {
        //nothing
    }


    public void setProfileToView() {
        if( mDataFacebook != null ) {
            TextView name = findViewById(R.id.name_facebook);
            name.setText(mDataFacebook.getName());
            new TaskPictureProfile(this).execute();
        }
    }

    public void buttonContinueRegister(View view) {
        String idRol = getIntent().getStringExtra(mConstant.getID_ROL());
        Intent intent = new Intent(this,
                idRol.equals(mConstant.getID_USERS()) ?
                        UserRegisterActivity.class : DriverRegisterActivity.class);
        intent.putExtra("DATA",this.mDataFacebook);
        intent.putExtra("PROFILE",this.absolutePathProfile);
        startActivity(intent);
    }

    public void logoutInRegister(View view) {
        LoginManager.getInstance().logOut();
        finish();
    }

    private static class TaskPictureProfile extends AsyncTask<Void, Void, Bitmap> {

        private WeakReference<WelcomeToAppActivity> activityReference;

        // only retain a weak reference to the activity
        TaskPictureProfile(WelcomeToAppActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            Bitmap bmp = null;
            try {
                String url = activityReference.get().getDataFacebook().getPictureUrl();
                Log.d(this.getClass().getName(),url);
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.setUseCaches(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bmp = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (Exception e) {
                Log.d(this.getClass().getName(), e.toString());
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            // get a reference to the activity if it is still there
            WelcomeToAppActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // modify the activity's UI
            ImageView photo = activity.findViewById(R.id.image_user_register);
            photo.setImageBitmap(bitmap);
            activity.generateAbsolutePathProfile(bitmap);
        }

    }

    private void generateAbsolutePathProfile(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        this.absolutePathProfile = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
