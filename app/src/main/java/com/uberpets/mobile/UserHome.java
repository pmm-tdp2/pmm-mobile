package com.uberpets.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.uberpets.model.Connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static float ZOOM_VALUE = 14.0f;
    private int locationRequestCode = 1000;
    //private AutocompleteSupportFragment mAutocompleteSupportFragment;
    private CardView mCardViewSearch;
    private LinearLayout mInfoDriver;
    private String TAG_PLACE_AUTO = "PLACE_AUTO_COMPLETED";
    private String TAG_FRAG_TRANS = "Fragment Trasation";
    private Connection mConnection;

    private Socket mSocket;
    private final String EVENT_POSITON_DRIVER = "POSITION DRIVER";
    private final String EVENT_DRIVER_ARRIVED_DESTINY = "DRIVER ARRIVED TO DESTINY";
    private final String EVENT_CONNECTION = "message";
    private final String EVENT_DRIVER_ARRIVED_USER = "DRIVER ARRIVED TO USER";
    private final String TAG_CONNECTION_SERVER = "CONNECTION_SERVER";
    private Emitter.Listener mListenerConnection;
    private Emitter.Listener mListenerPositionDriver;
    private Emitter.Listener mListenerDriverArrivedToUser;
    private Emitter.Listener mListenerDriverArrivedToDestiny;

    private OptionsTravelFragment mFragmentTest;

    private static final int REQUEST_AUTOCOMPLETE_ACTIVITY = 0;
    private static final int RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY = 0;
    private static final int RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY = 1;
    private static final int RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY = 2;


    //private final String URL = "http://young-wave-26125.herokuapp.com/pmm";
    private final String URL = "http://192.168.0.6:8081/pmm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //is used to obtain user's location, with this our app no needs to manually manage connections
        //to Google Play Services through GoogleApiClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mCardViewSearch = findViewById(R.id.card_view);

        mMessageCard = findViewById(R.id.card_view_message);
        mMessageCard.setVisibility(CardView.INVISIBLE);

        {
            try {
                mSocket = IO.socket(URL);
                Log.d(TAG_CONNECTION_SERVER,"io socket succes");
                connectToServer();
                getDriverPosition();
                listenDriverArrivedUser();
                listenDriverArrivedDestiny();
            } catch (URISyntaxException e) {
                Log.d(TAG_CONNECTION_SERVER,"io socket failure");
            }
        }

        requestPermission();
        //autocompleteLocation();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        finishPreviusFragments(); //added for me
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

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED");
        }
    }


    public void showCardToPickLocation() {
        mCardViewSearch.setBackgroundColor(R.color.quantum_orange500);
        ImageView imageView = findViewById(R.id.icon_search_cardView);
        imageView.setImageResource(R.drawable.ic_map_pin_36);
        TextView textView = findViewById(R.id.text_search_cardView);
        textView.setText("Haga click en la ubicación");
    }

    public void pickMapOrigin() {
        showCardToPickLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng newLatLon) {
                originMarker.setPosition(newLatLon);
                originMarker.setVisible(true);
                mOrigin = newLatLon;
                mMap.setOnMapClickListener(null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 500ms
                        goToSelectOriginDestiny();
                    }
                }, 500);
            }
        });
    }

    public void pickMapDestiny() {
        showCardToPickLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng newLatLon) {
                destinyMarker.setVisible(true);
                destinyMarker.setPosition(newLatLon);
                mDestiny = newLatLon;
                mMap.setOnMapClickListener(null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 500ms
                        goToSelectOriginDestiny();
                    }
                }, 500);
            }
        });
    }

    public void clickSearchLocations(View view) {
        goToSelectOriginDestiny();
    }


    public void goToSelectOriginDestiny(){
        Intent intent = new Intent (this, PlaceAutoCompleteActivity.class);
        if(mOrigin != null){
            intent.putExtra("ORIGIN_READY",mOrigin.toString());
        }
        if(mDestiny != null){
            intent.putExtra("DESTINY_READY",mDestiny.toString());
        }
        startActivityForResult(intent,REQUEST_AUTOCOMPLETE_ACTIVITY);
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_AUTOCOMPLETE_ACTIVITY) {
            super.onActivityResult(requestCode, resultCode, data);
            // check if the request code is same as what is passed
            if(resultCode == RESPONSE_ORIGIN_AUTOCOMPLETE_ACTIVITY) {
                pickMapOrigin();
            }else if(resultCode == RESPONSE_DESTINY_AUTOCOMPLETE_ACTIVITY){
                pickMapDestiny();
            }else if(resultCode == RESPONSE_ROUTE_AUTOCOMPLETE_ACTIVITY){
                getRouteTravel();
                showInfoTravel();
            }
        }
    }


    public void getRouteTravel() {
        // se tiene que mejorar...
        mCardViewSearch.setVisibility(View.INVISIBLE);
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(mOrigin, mDestiny));
    }

    //init fragment options travel
    public void showInfoTravel() {
        mFragmentTest =new OptionsTravelFragment();
        replaceFragment(mFragmentTest , true);
    }

    //init Show Searching Driver
    public void showSearchingDriver() {
        finishPreviusFragments();
        replaceFragment( new SearchingDriverFragment(), true);
    }

    public void getDriver(View view) {

        //logic to send to server
        //hard
        Log.d("CANTIDAD_MASCOTAS","___");
        Log.d("CANTIDAD_MASCOTAS","pequeños: "+mFragmentTest.getAllLittlePets()
        + "  medianos: "+mFragmentTest.getAllMediumPets()+ "  big: "+mFragmentTest.getAllBigPets());
        showSearchingDriver();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL+"/travels";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        driverComing(response);
                        Log.i("GETDRIVER", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("GETDRIVER", error.toString());
                finishPreviusFragments();
                mMessageCard.setVisibility(CardView.VISIBLE);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("longitude",String.valueOf(mDestiny.longitude));
                params.put("latitude",String.valueOf(mDestiny.latitude));
                return params;
            }
        };
        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }


    public void driverComing(String infoDriver){
        finishPreviusFragments();
        replaceFragment(new InfoDriverAssingFragment(),true);
    }


    public void ShowPositionDriver(float latitude, float longitude){
        LatLng latLng = new LatLng(latitude,longitude);
        driverMarker.setPosition(latLng);
    }


    public void initTravel(){
        finishPreviusFragments();
        replaceFragment(new TrackingTravelFragment(),true);
    }


    public void showRatingBar(){

    }


    /* BEGIN OF SOCKET CONNECTION*/

    public void getDriverPosition(){
        mSocket.on(EVENT_POSITON_DRIVER, mListenerPositionDriver = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String latitude;
                        String longitude;
                        //try {
                            //latitude = data.getString("latitude");
                            //longitude = data.getString("longitude");
                            Log.d(TAG_CONNECTION_SERVER,"position driver recived");
                            //ShowPositionDriver(Float.parseFloat(latitude),Float.parseFloat(longitude));
                        //} catch (JSONException e) {
                          //  return;
                        //}

                    }
                });
            }
        });
    }

    public void connectToServer(){
        mSocket.connect();
        mSocket.on(EVENT_CONNECTION, mListenerConnection = new Emitter.Listener() {
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
        mSocket.on(EVENT_DRIVER_ARRIVED_USER, mListenerDriverArrivedToUser = new Emitter.Listener() {
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
        mSocket.on(EVENT_DRIVER_ARRIVED_DESTINY, mListenerDriverArrivedToDestiny = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
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

    public void finishPreviusFragments() {
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

