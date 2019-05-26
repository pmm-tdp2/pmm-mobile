package com.uberpets.mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;

public class UserFinalScreen extends AppCompatActivity {

    private RatingBar mRatingBar;
    private CheckBox mCheckBox;

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
        float rating = mRatingBar.getRating();
        if(rating != 0) {
            Log.i(this.getClass().getName(),"user: idUser "+ " has scored with "+rating );

        //go back to activity that called it
        finish();
        }
    }

    public void onRatingBarChange(android.view.View view){
        float rating = mRatingBar.getRating();
        if (rating <= 3) mCheckBox.setVisibility(View.VISIBLE);
        else mCheckBox.setVisibility(View.INVISIBLE);
    }
}
