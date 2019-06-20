package com.uberpets.mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.CopyTravelDTO;
import com.uberpets.model.FileDocumentDTO;
import com.uberpets.model.RatingDTO;
import com.uberpets.model.SimpleResponse;
import com.uberpets.services.App;
import com.uberpets.util.ConvertImages;

public class UserFinalScreen extends AppCompatActivity {

    private RatingBar mRatingBar;
    private CheckBox mCheckBox;
    TextInputEditText mTextInput;
    private CopyTravelDTO mTravelDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_final_screen);
        mRatingBar = findViewById(R.id.ratingBar_user);
        mCheckBox = findViewById(R.id.betterServiceCheckbox);
        mCheckBox.setVisibility(View.INVISIBLE);
        mTextInput =  findViewById(R.id.user_text_comment);
        mTravelDto = (CopyTravelDTO) getIntent().getSerializableExtra("TRAVEL");
        loadImageDriver();
        TextView nameDriver = findViewById(R.id.name_driver_rating);
        nameDriver.setText(mTravelDto.getDriver().getName());
    }

    @Override
    public void onBackPressed(){
        //TODO: ver si hacemos algo aca
    }

    private void loadImageDriver() {
        String path = "/api/fileDocuments/?driverId="+mTravelDto.getDriver().getId()+"&name=profile";
        App.nodeServer.get(path, FileDocumentDTO[].class,new Headers())
                .run(this::handleSuccessLoadImages,this::handleErrorLoadImages);
    }

    private void handleErrorLoadImages(Exception e) {
        Log.e(this.getClass().getName(),"Error to load images of driver");
        Log.e(this.getClass().getName(),e.toString());
        Toast toast = Toast.makeText(this,"Error al obtener la imagen del chofer"
                ,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void handleSuccessLoadImages(FileDocumentDTO[] files) {
        Log.i(this.getClass().getName(),"Photo profile of user obtained successfully");
        if(files.length > 0){
            ImageView imageView = findViewById(R.id.image_driver_to_rate);
            imageView.setImageBitmap(ConvertImages.getBitmapImage(files[0].getData()));
        }
    }

    public void sendComment(android.view.View view){
        float rating = mRatingBar.getRating();
        Log.d(this.getClass().getName(), "Sending rating: " + rating);
        Log.d(this.getClass().getName(), "Travel DTO: " + mTravelDto);
        if(rating != 0 && mTravelDto != null) {
            Log.i(this.getClass().getName(), "user: " + mTravelDto.getUser().getId() + " has scored with " + rating);
            Log.i(this.getClass().getName(), "to driver: " + mTravelDto.getDriver().getId());
            Editable editable = mTextInput.getText();
            if(editable != null)
                Log.i(this.getClass().getName(),"comentario: " + editable.toString());

            RatingDTO ratingDto = new RatingDTO.RatingDTOBuilder()
                    .setComments(mTextInput.getText().toString())
                    .setValue(mRatingBar.getRating())
                    .setFromId(mTravelDto.getUser().getId())
                    .setToId(mTravelDto.getDriver().getId())
                    //.setFromId("123456782")
                    //.setToId("987654321")
                    .setTravelId(mTravelDto.getTravelId())
                    .build();

            App.nodeServer.post("/api/driverScores",ratingDto,
                    SimpleResponse.class,new Headers())
                    .run(this::handleResponseRating,this::handleErrorRating);
        }
    }

    public void handleResponseRating(SimpleResponse simpleResponse){
        //Log.i(this.getClass().getName(),simpleResponse.getMessage());
        Toast toast = Toast.makeText(this,
                "LA puntuación se realizó con éxito",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        finish();
    }

    public void handleErrorRating(Exception e){
        Log.e(this.getClass().getName(),"Error in rating driver");
        Log.e(this.getClass().getName(),e.toString());
    }

    public void onRatingBarChange(android.view.View view){
        float rating = mRatingBar.getRating();
        if (rating <= 3) mCheckBox.setVisibility(View.VISIBLE);
        else mCheckBox.setVisibility(View.INVISIBLE);
    }
}
