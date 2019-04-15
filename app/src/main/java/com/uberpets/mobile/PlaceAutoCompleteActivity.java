package com.uberpets.mobile;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class PlaceAutoCompleteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG_PLACE_AUTO = "PLACE_AUTO_COMPLETED";
    private boolean isReadyOrigin = false;
    private boolean isReadyDestiny = false;
    private LatLng placeOrigin;
    private LatLng placeDestiny;
    private SupportMapFragment mFragmentMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY = 0;
    private static final int RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY = 1;
    private static final int RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY = 2;
    private GoogleMap mMap;
    AutocompleteSupportFragment mAutocompleteSupportFragmentOrigin;
    AutocompleteSupportFragment mAutocompleteSupportFragmentDestiny;

    private Marker mMarker;
    private Marker originMarker;
    private Marker destinyMarker;
    private Marker driverMarker;

    private CardView mCardPick;

    private LatLng currentPlace;
    private static float ZOOM_VALUE = 14.0f;
    private int locationRequestCode = 1000;
    private LinearLayout mMenu;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_auto_complete);
        autocompleteLocation();

        mFragmentMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_place_autocomplete));
        ((SupportMapFragment) mFragmentMap).getView().setVisibility(View.INVISIBLE);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mCardPick = findViewById(R.id.card_view_autocomplete);
        mCardPick.setVisibility(View.INVISIBLE);

        mMenu = findViewById(R.id.menu_autocomplete);
        mButton = findViewById(R.id.button_show_route);
        fetchLastLocation();
    }


    public void autocompleteLocation(){

        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),getString(R.string.google_maps_key));
        }

        // Initialize the AutocompleteSupportFragment
        mAutocompleteSupportFragmentOrigin = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_origin);

        mAutocompleteSupportFragmentDestiny = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_destiny);


        mAutocompleteSupportFragmentOrigin.setHint("Ingrese Origen");
        mAutocompleteSupportFragmentDestiny.setHint("Ingrese Destino");


        /*if(getIntent().getExtras() != null) {
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

        }*/


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
                    placeOrigin = place.getLatLng();
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
                placeDestiny = place.getLatLng();
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
        showMap();
        if(mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng newLatLon) {
                    originMarker.setPosition(newLatLon);
                    originMarker.setVisible(true);
                    placeOrigin = newLatLon;
                    mAutocompleteSupportFragmentOrigin.setHint("Origen Listo");
                    mMap.setOnMapClickListener(null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 500ms
                            showMenu();
                        }
                    }, 500);
                }
            });
        }
    }

    public void pickDestiny(View view) {
        showMap();
        if(mMap != null){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng newLatLon) {
                    destinyMarker.setVisible(true);
                    destinyMarker.setPosition(newLatLon);
                    placeDestiny = newLatLon;
                    mAutocompleteSupportFragmentDestiny.setHint("Destino Listo");
                    mMap.setOnMapClickListener(null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 500ms
                            showMenu();
                        }
                    }, 500);
                }
            });
        }
    }

    public void showRoute(View view){
        if (placeOrigin != null && placeDestiny != null) {
            Intent intent = new Intent();
            intent.putExtra("LATITUDE_ORIGIN_AUTOCOMPLETE",placeOrigin.latitude);
            intent.putExtra("LONGITUDE_ORIGIN_AUTOCOMPLETE",placeOrigin.longitude);
            intent.putExtra("LATITUDE_DESTINY_AUTOCOMPLETE",placeDestiny.latitude);
            intent.putExtra("LONGITUDE_DESTINY_AUTOCOMPLETE",placeDestiny.longitude);
            setResult(RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY,intent);
            finish();//finishing activity
        }
    }


    public void showMenu(){
        mMenu.setVisibility(View.VISIBLE);
        mCardPick.setVisibility(View.INVISIBLE);
        mButton.setVisibility(View.VISIBLE);
        ((SupportMapFragment) mFragmentMap).getView().setVisibility(View.INVISIBLE);
    }


    public void showMap(){
        mMenu.setVisibility(View.INVISIBLE);
        mCardPick.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.INVISIBLE);
        ((SupportMapFragment) mFragmentMap).getView().setVisibility(View.VISIBLE);
    }


    public void fetchLastLocation() {
/*
        //check if user has granted location permission,
        // its necessary to use mFusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(PlaceAutoCompleteActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PlaceAutoCompleteActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            // obtain the last location and save in task, that's Collection's Activities
            Task<Location> task = mFusedLocationProviderClient.getLastLocation();
            // add object OnSuccessListener, when the connection is established and the location is fetched
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentPlace = location;*/
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map_place_autocomplete);
                        mapFragment.getMapAsync(PlaceAutoCompleteActivity.this);
                    /*}
                }
            });
        }*/

        if(getIntent().getExtras() != null){
            double cuLat = getIntent().getExtras().getDouble("CURRENT_LATITUDE");
            double cuLon = getIntent().getExtras().getDouble("CURRENT_LONGITUDE");
            Log.d("$$$","values: "+ cuLat);
            Log.d("$$$","values: "+ cuLon);
            currentPlace = new LatLng(cuLat,cuLon);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (googleMap != null) {
                Log.d("INFO", "GOOGLE GOOD LOADED");
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                LatLng mockLatLng = new LatLng(0,0);
                driverMarker = mMap.addMarker(new MarkerOptions().position(mockLatLng).title("conductor"));
                driverMarker.setVisible(false);

                LatLng latLng = new LatLng(currentPlace.latitude,currentPlace.longitude);

                //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Estas Acá");

                //Adding the created the marker on the map
                mMarker = mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_VALUE));

                mMarker = mMap.addMarker(new MarkerOptions().position(currentPlace).title("Estas Acá"));

                originMarker = mMap.addMarker(new MarkerOptions().position(currentPlace).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_36)));
                originMarker.setVisible(false);
                destinyMarker= mMap.addMarker(new MarkerOptions().position(currentPlace).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_36)));
                destinyMarker.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED");
        }
    }
}
