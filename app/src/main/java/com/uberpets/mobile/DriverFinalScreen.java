package com.uberpets.mobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.CopyTravelDTO;
import com.uberpets.model.RatingDTO;
import com.uberpets.model.SimpleResponse;
import com.uberpets.model.TravelDTO;
import com.uberpets.services.App;

public class DriverFinalScreen extends AppCompatActivity {

    RatingBar mRatingBar;
    TextInputEditText mTextInput;
    CopyTravelDTO mTravelDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_final_screen);
        mRatingBar = findViewById(R.id.ratingBar_user);
        mTextInput =  findViewById(R.id.driver_text_comment);
        mTravelDto = (CopyTravelDTO) getIntent().getSerializableExtra("TRAVEL");
    }

    public void sendComment(android.view.View view){
        float rating = mRatingBar.getRating();
        if(rating != 0 && mTravelDto != null) {
            Log.i(this.getClass().getName(),"driver: idDriver "+ " has scored with " + rating );
            Log.i(this.getClass().getName(),"comentario: "+mTextInput.getText().toString());
            //go back to activity that called it
            RatingDTO ratingDto = new RatingDTO.RatingDTOBuilder()
                    .setComments(mTextInput.getText().toString())
                    .setValue(mRatingBar.getRating())
                    .setId(mTravelDto.getDriverId())
                    .setFromId(mTravelDto.getDriverId())
                    .setToId(mTravelDto.getUserId())
                    .setTravelId(mTravelDto.getTravelId())
                    .build();
            App.nodeServer.post("/api/userScores",ratingDto,
                    SimpleResponse.class,new Headers())
                    .run(this::handleResponseRating,this::handleErrorRating);
        }
    }

    public void handleResponseRating(SimpleResponse simpleResponse){
        Log.i(this.getClass().getName(),simpleResponse.getMessage());
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

    @Override
    public void onBackPressed(){
        //TODO: ver si hacemos algo aca
    }
}
