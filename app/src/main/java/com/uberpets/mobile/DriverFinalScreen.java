package com.uberpets.mobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
        Log.d(this.getClass().getName(), "Travel : " + mTravelDto);
        loadImageUser();
        TextView nameUser = findViewById(R.id.name_user_rating);
        nameUser.setText(mTravelDto.getUser().getName());
    }

    private void loadImageUser() {
        String path = "/api/fileDocuments/?userId="+mTravelDto.getUser().getId()+"&name=profile";
        App.nodeServer.get(path, FileDocumentDTO[].class,new Headers())
                .run(this::handleSuccessLoadImages,this::handleErrorLoadImages);
    }

    private void handleErrorLoadImages(Exception e) {
        Log.e(this.getClass().getName(),"Error to load images of user");
        Log.e(this.getClass().getName(),e.toString());
        Toast toast = Toast.makeText(this,"Error al obtener la imagen del usuario"
                ,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
    }

    private void handleSuccessLoadImages(FileDocumentDTO[] files) {
        Log.i(this.getClass().getName(),"Photo profile of user obtained successfully");
        if(files.length>0){
            ImageView imageView = findViewById(R.id.image_user_to_rate);
            imageView.setImageBitmap(ConvertImages.getBitmapImage(files[0].getData()));
        }
    }

    public void sendComment(android.view.View view){
        float rating = mRatingBar.getRating();
        Log.d(this.getClass().getName(), "Rating: " + rating);

        if(rating != 0 && mTravelDto != null) {
            Log.i(this.getClass().getName(),"driver: " + mTravelDto.getDriver().getId() +
                    " has scored with " + rating + "to user: " + mTravelDto.getUser().getId());

            Log.i(this.getClass().getName(),"comentario: "+mTextInput.getText().toString());

            //go back to activity that called it
            //TODO: deshardcodear los ids
            RatingDTO ratingDto = new RatingDTO.RatingDTOBuilder()
                    .setComments(mTextInput.getText().toString())
                    .setValue(mRatingBar.getRating())
                    .setFromId(mTravelDto.getDriver().getId())
                    .setToId(mTravelDto.getUser().getId())
                    .setTravelId(mTravelDto.getTravelId())
                    .build();
            Log.d(this.getClass().getName(), "RatingDTO: " + ratingDto);
            App.nodeServer.post("/api/userScores",ratingDto,
                    SimpleResponse.class,new Headers())
                    .run(this::handleResponseRating,this::handleErrorRating);
        }
    }

    public void handleResponseRating(SimpleResponse simpleResponse){
        Log.i(this.getClass().getName(), "Rating Response:" + simpleResponse.getMessage());
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
