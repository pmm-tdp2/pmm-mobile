package com.uberpets.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ServerError;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.uberpets.Constants;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.Driver;
import com.uberpets.model.Travel;
import com.uberpets.services.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker mMarker;
    private Marker originMarker;
    private Marker destinyMarker;
    private Marker driverMarker;

    private CardView mMessageCard;

    private Location currentLocation;
    private LatLng mDestiny;
    private LatLng mOrigin;

    public LatLng getmDestiny() {
        return mDestiny;
    }

    public LatLng getmOrigin() {
        return mOrigin;
    }

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float ZOOM_VALUE = 14.0f;
    private static final int locationRequestCode = 1000;
    private CardView mCardViewSearch;

    private Socket mSocket;

    private String TAG_CONNECTION_SERVER = "CONNECTION_SERVER";
    private String TAG_REQUEST_SERVER = "REQUEST_SERVER";
    private String TAG_USER_HOME = "REQUEST_SERVER";

    private Emitter.Listener mListenerConnection;
    private Emitter.Listener mListenerPositionDriver;
    private Emitter.Listener mListenerDriverArrivedToUser;
    private Emitter.Listener mListenerDriverArrivedToDestiny;

    private OptionsTravelFragment mFragmentTest;
    private Constants mConstants = Constants.getInstance();
    private boolean isQueryCanceled = false;
    //private String mUrl = mConstants.getURL();
    //    private String mUrl = mConstants.getURL_REMOTE() + mConstants.getURL_BASE_PATH();
//    private String mUrl = mConstants.getURL_LOCAL() + mConstants.getURL_BASE_PATH();
    private static final String[] TRANSPORTS = {
            "websocket"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //is used to obtain user's location, with this our app no needs to manually manage connections
        //to Google Play Services through GoogleApiClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mCardViewSearch = findViewById(R.id.card_view);

        mMessageCard = findViewById(R.id.card_view_message);
        mMessageCard.setVisibility(CardView.INVISIBLE);

        {
            try {
                final IO.Options options = new IO.Options();
                options.transports = TRANSPORTS;
                mSocket = IO.socket(Constants.getInstance().getURL_SOCKET(), options);
                Log.i(TAG_CONNECTION_SERVER,"io socket success");
                connectToServer();
                getDriverPosition();
                listenDriverArrivedUser();
                listenDriverArrivedDestiny();
            } catch (URISyntaxException e) {
                Log.e(TAG_CONNECTION_SERVER,"io socket failure");
            }
        }

        requestPermission();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        finishPreviousFragments(); //added for me
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestPermission(){
        //check if user has granted location permission,
        // its necessary to use mFusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(UserHome.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserHome.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},locationRequestCode);
        }else{
            fetchLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // Si se cancela la solicitud, las matrices de resultados están vacías.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
            }
        }
    }

    public void fetchLastLocation() {

        //check if user has granted location permission,
        // its necessary to use mFusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(UserHome.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserHome.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            // obtain the last location and save in task, that's Collection's Activities
            Task<Location> task = mFusedLocationProviderClient.getLastLocation();
            // add object OnSuccessListener, when the connection is established and the location is fetched
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(UserHome.this);
                    }
                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Estas Acá");

                //Adding the created the marker on the map
                mMarker = mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_VALUE));

                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                mMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Estas Acá"));

                originMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_36)));
                originMarker.setVisible(false);
                destinyMarker= mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_36)));
                destinyMarker.setVisible(false);
                driverMarker= mMap.addMarker(new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                driverMarker.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED");
        }
    }



    public void clickSearchLocations(View view) {
        goToSelectOriginDestiny();
    }


    public void goToSelectOriginDestiny(){
        Intent intent = new Intent (this, PlaceAutoCompleteActivity.class);
        intent.putExtra("CURRENT_LATITUDE",currentLocation.getLatitude());
        intent.putExtra("CURRENT_LONGITUDE",currentLocation.getLongitude());
        startActivityForResult(intent,mConstants.getREQUEST_AUTOCOMPLETE_ACTIVITY());
    }


    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == mConstants.getREQUEST_AUTOCOMPLETE_ACTIVITY()) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == mConstants.getRESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY()){
                Log.d(TAG_USER_HOME,"came back of activity PLACEAUTOCOMPLETEACTIVITY");
                if(data.getExtras() != null){
                    Log.d(TAG_USER_HOME,"data is returned from activity PLACEAUTOCOMPLETEACTIVITY");
                    double originLat = data.getDoubleExtra("LATITUDE_ORIGIN_AUTOCOMPLETE",0);
                    double originLon = data.getDoubleExtra("LONGITUDE_ORIGIN_AUTOCOMPLETE",0);
                    mOrigin= new LatLng(originLat,originLon);
                    originMarker.setVisible(true);
                    originMarker.setPosition(mOrigin);

                    double destinyLat = data.getDoubleExtra("LATITUDE_DESTINY_AUTOCOMPLETE",0);
                    double destinyLon = data.getDoubleExtra("LONGITUDE_DESTINY_AUTOCOMPLETE",0);
                    mDestiny= new LatLng(destinyLat,destinyLon);
                    destinyMarker.setVisible(true);
                    destinyMarker.setPosition(mDestiny);
                    getRouteTravel();
                }else{
                    Log.d(TAG_USER_HOME,"no data is returned from activity PLACEAUTOCOMPLETEACTIVITY");
                }
            }
        }
    }


    public void getRouteTravel() {
        // se tiene que mejorar...
        showOptionsToTravel();
        mCardViewSearch.setVisibility(View.INVISIBLE);
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(mOrigin, mDestiny));
    }

    //init fragment options travel
    public void showOptionsToTravel() {
        mFragmentTest =new OptionsTravelFragment();
        replaceFragment(mFragmentTest , true);
    }


    public void showInfoDriverAssigned(){
        finishPreviousFragments();
        replaceFragment(new InfoDriverAssingFragment(),true);
    }

    //set text of message card
    public void showDriverNotFound(){
        TextView textView =  findViewById(R.id.text_view_message);
        textView.setText("Por el momento no se encuentran Choferes, intente más tarde");
        mMessageCard.setBackgroundColor(Color.rgb(255, 160,0));
        showMessageCard();
    }

    //value default is error
    public void showMessageCard(){
        finishPreviousFragments();
        mMessageCard.setVisibility(CardView.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5000ms
                returnOriginalState();
            }
        }, 5000);
    }

    public void returnOriginalState(){
        mMap.clear();
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Estas Acá"));
        mCardViewSearch.setVisibility(View.VISIBLE);
        mMessageCard.setVisibility(CardView.INVISIBLE);
        originMarker.setVisible(false);
        destinyMarker.setVisible(false);
    }



    public void ShowPositionDriver(float latitude, float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        driverMarker.setVisible(true);
        driverMarker.setPosition(latLng);
        //falta la logica de girar...
    }


    public void initTravel(){
        finishPreviousFragments();
        replaceFragment(new TrackingTravelFragment(),true);
    }


    public void showRatingBar(){
        mMap.clear();
        originMarker.setVisible(false);
        destinyMarker.setVisible(false);
        driverMarker.setVisible(false);
        mCardViewSearch.setVisibility(CardView.VISIBLE);
        finishPreviousFragments();
        Intent intent = new Intent(this, UserFinalScreen.class);
        startActivity(intent);
    }


    /* BEGIN OF SOCKET CONNECTION*/

    public void getDriverPosition(){
        mSocket.on(mConstants.getEVENT_POSITON_DRIVER(), mListenerPositionDriver = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            String latitude;
                            String longitude;
                            latitude = data.getString("latitude");
                            longitude = data.getString("longitude");
                            Log.d(TAG_CONNECTION_SERVER,"position driver received");
                            ShowPositionDriver(Float.parseFloat(latitude),Float.parseFloat(longitude));
                        } catch (JSONException e) {
                            return;
                        }

                    }
                });
            }
        });
    }

    public void connectToServer(){
        mSocket.connect();
        mSocket.on(mConstants.getEVENT_CONNECTION(), mListenerConnection = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        Log.d(TAG_CONNECTION_SERVER, "Established Connection");
                        Log.d(TAG_CONNECTION_SERVER, data.toString());
                    }
                });
            }
        });
        mSocket.emit("ROL","USER");
    }


    //listen if arrive message that driver arrived to user
    public void listenDriverArrivedUser() {
        mSocket.on(mConstants.getEVENT_DRIVER_ARRIVED_USER(), mListenerDriverArrivedToUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        initTravel();
                    }
                });
            }
        });
    }

    //listen if arrive message that driver arrived to destiny
    public void listenDriverArrivedDestiny() {
        mSocket.on(mConstants.getEVENT_DRIVER_ARRIVED_DESTINY(), mListenerDriverArrivedToDestiny = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        showRatingBar();
                    }
                });
            }
        });
    }

    /* END OF SOCKET CONNECTION*/


    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("FINISH", mListenerConnection);
        mSocket.off("FINISH", mListenerPositionDriver);
        mSocket.off("FINISH", mListenerDriverArrivedToUser);
        mSocket.off("FINISH", mListenerDriverArrivedToDestiny);
    }

    /*BEGIN REPLACE FRAGMENT*/

    public boolean popFragment() {
        boolean isPop = false;

        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.options_travel);

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getSupportFragmentManager().popBackStackImmediate();
        }

        return isPop;
    }

    public void finishPreviousFragments() {
        if (!popFragment()) {
            finish();
        }
    }


    public void replaceFragment(Fragment fragment, boolean addToBackStack) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);

        } else {
            getSupportFragmentManager().popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }
        transaction.replace(R.id.options_travel, fragment);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    /*END REPLACE FRAGMENT*/

}

