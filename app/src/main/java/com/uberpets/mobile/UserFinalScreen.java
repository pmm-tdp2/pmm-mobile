package com.uberpets.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;

public class UserFinalScreen extends AppCompatActivity {

    private RatingBar mRatingBar;
    private CheckBox mCheckBox;
    private String TAG_RATING_USER = "USER_RATING_STARS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_final_screen);
        mRatingBar = findViewById(R.id.ratingBar_user);
        mCheckBox = findViewById(R.id.betterServiceCheckbox);
        mCheckBox.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed(){
        //TODO: ver si hacemos algo aca
    }

    public void sendComment(android.view.View view){
        //int numStars = mRatingBar.getNumStars();
        //if(numStars != 0) {
        //    Log.i(TAG_RATING_USER,"user: idUser "+ " has scored with "+numStars );
        //go back to activity that called it
        finish();
        //}
    }

    public void onRatingBarChange(android.view.View view){
        int rating = mRatingBar.getNumStars();
        if (rating <= 3) mCheckBox.setVisibility(View.VISIBLE);
        else mCheckBox.setVisibility(View.INVISIBLE);
    }
}
