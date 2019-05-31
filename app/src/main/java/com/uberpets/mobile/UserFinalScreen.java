package com.uberpets.mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.CopyTravelDTO;
import com.uberpets.model.RatingDTO;
import com.uberpets.model.SimpleResponse;
import com.uberpets.model.TravelDTO;
import com.uberpets.services.App;

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
    }

    @Override
    public void onBackPressed(){
        //TODO: ver si hacemos algo aca
    }

    public void sendComment(android.view.View view){
        float rating = mRatingBar.getRating();
        Log.d(this.getClass().getName(), "Sending rating: " + rating);
        Log.d(this.getClass().getName(), "Travel DTO: " + mTravelDto);
        if(rating != 0 && mTravelDto != null) {
            Log.i(this.getClass().getName(), "user: " + mTravelDto.getUserId() + " has scored with " + rating);
            Log.i(this.getClass().getName(), "to driver: " + mTravelDto.getDriverId());
            Log.i(this.getClass().getName(),"comentario: " + mTextInput.getText().toString());

            RatingDTO ratingDto = new RatingDTO.RatingDTOBuilder()
                    .setComments(mTextInput.getText().toString())
                    .setValue(mRatingBar.getRating())
                    .setFromId(mTravelDto.getUserId())
                    .setToId(mTravelDto.getDriverId())
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
