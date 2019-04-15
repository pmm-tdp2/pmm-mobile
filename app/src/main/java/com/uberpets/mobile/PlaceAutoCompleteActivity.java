/*package com.uberpets.mobile;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class PlaceAutoCompleteActivity extends AppCompatActivity {

    private String TAG_PLACE_AUTO = "PLACE_AUTO_COMPLETED";
    private boolean isReadyOrigin = false;
    private boolean isReadyDestiny = false;
    private LatLng origin;
    private LatLng destiny;
    private static final int RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY = 0;
    private static final int RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY = 1;
    private static final int RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_auto_complete);
        autocompleteLocation();
    }


    public void autocompleteLocation(){

        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),getString(R.string.google_maps_key));
        }

        AutocompleteSupportFragment mAutocompleteSupportFragmentOrigin;
        AutocompleteSupportFragment mAutocompleteSupportFragmentDestiny;

        // Initialize the AutocompleteSupportFragment
        mAutocompleteSupportFragmentOrigin = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_origin);

        mAutocompleteSupportFragmentDestiny = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_destiny);


        mAutocompleteSupportFragmentOrigin.setHint("Origen");
        mAutocompleteSupportFragmentDestiny.setHint("Destino");


        if(getIntent().getExtras() != null) {
            String latLogOrigin = getIntent().getExtras().getString("ORIGIN_READY");
            if(latLogOrigin != null) {
                mAutocompleteSupportFragmentOrigin.setHint("Origen Listo");
                //findViewById(R.id.button_origin).setBackgroundColor(Color.rgb(205,134,230));
                isReadyOrigin = true;
            }

            String latLogDestiny = getIntent().getExtras().getString("DESTINY_READY");
            if(latLogDestiny != null){
                //findViewById(R.id.button_destiny).setBackgroundColor(Color.rgb(205,134,230));
                mAutocompleteSupportFragmentDestiny.setHint("Destino Listo");
                isReadyDestiny = true;
            }

        }


        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Specify the types of place data to return.
        mAutocompleteSupportFragmentOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        mAutocompleteSupportFragmentDestiny.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        mAutocompleteSupportFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //System.out.printf(place.getName());
                Log.i(TAG_PLACE_AUTO, "Origin place: " + place.getName() + ", " + place.getId() );
                    origin = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //System.out.printf(status.toString());
                Log.i(TAG_PLACE_AUTO, "An error occurred in origin place: " + status);

            }
        });

        // Set up a PlaceSelectionListener to handle the response.
        mAutocompleteSupportFragmentDestiny.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //System.out.printf(place.getName());
                Log.i(TAG_PLACE_AUTO, "Destiny place: " + place.getName() + ", " + place.getId());
                destiny = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //System.out.printf(status.toString());
                Log.i(TAG_PLACE_AUTO, "An error occurred in destiny place: " + status);

            }
        });
    }


    public void pickOrigin(View view) {
        Intent intent = new Intent();
        intent.putExtra("ORIGIN_AUTOCOMPLETE",origin);
        intent.putExtra("DESTINY_AUTOCOMPLETE",destiny);
        setResult(RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY,intent);
        finish();//finishing activity
    }

    public void pickDestiny(View view) {
        Intent intent = new Intent();
        intent.putExtra("ORIGIN_AUTOCOMPLETE",origin);
        intent.putExtra("DESTINY_AUTOCOMPLETE",destiny);
        setResult(RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY,intent);
        finish();//finishing activity
    }

    public void showRoute(View view){
        Intent intent = new Intent();
        intent.putExtra("ORIGIN_AUTOCOMPLETE",origin);
        intent.putExtra("DESTINY_AUTOCOMPLETE",destiny);
        if (isReadyOrigin && isReadyDestiny || ) {
            setResult(RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY,new Intent());
            finish();//finishing activity
        }
    }


}
*/