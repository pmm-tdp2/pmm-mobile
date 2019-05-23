package com.uberpets.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.uberpets.Constants;
import com.uberpets.mobile.ui.main.CanceledTravelFragment;
import com.uberpets.model.Person;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.util.AccountSession;
import com.uberpets.util.GMapV2Direction;
import com.uberpets.util.GMapV2DirectionAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public final String ROL = "USER";
    public final String TAG_ROL = "ROL";
    private GoogleMap mMap;
    private Polyline mRoute;
    private int idUSer;
    private TextView textPrice;

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

    private final String TAG_CONNECTION_SERVER = "CONNECTION_SERVER";
    private final String TAG_USER_HOME = "USER_HOME";

    private Emitter.Listener mListenerConnection;
    private Emitter.Listener mListenerPositionDriver;
    private Emitter.Listener mListenerDriverArrivedToUser;
    private Emitter.Listener mListenerDriverArrivedToDestiny;
    private Emitter.Listener mListenerDriverCancelTravel;
    private Emitter.Listener mListenerAssignDriver;

    private OptionsTravelFragment mFragmentTest;
    private CanceledTravelFragment mFragmentCanceledTravel;
    private Constants mConstants = Constants.getInstance();

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
                listenDriverPosition();
                listenDriverArrivedUser();
                listenDriverArrivedDestiny();
                listenCancelTravel();
                listenAssignedDriver();
            } catch (URISyntaxException e) {
                Log.e(TAG_CONNECTION_SERVER,"io socket failure");
            }
        }

        requestPermission();

        //erase
        //mFragmentCanceledTravel = new CanceledTravelFragment();
        //replaceFragment(mFragmentCanceledTravel,true);
    }

    public int getIdUSer() {
        return idUSer;
    }

    @Override
    public void onBackPressed() {
        if (!finishPreviousFragments()) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }else{
            returnOriginalState();
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

        }else if (id == R.id.logout_from_home_account_user) {
            AccountSession.finalizeSession(this);
            Intent intent = new Intent(this, TabLoginActivity.class);
            startActivity(intent);
            finish();
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
            }
        }
    }

    public void cancelSearchingDriver(android.view.View view){
        //TODO: do something
    }

    public void fetchLastLocation() {
        //check if user has granted location permission,
        // its necessary to use mFusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(UserHome.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(UserHome.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
                driverMarker = mMap.addMarker(new MarkerOptions().position(mockLatLng)
                        .title("conductor"));
                driverMarker.setVisible(false);

                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation
                        .getLongitude());

                //MarkerOptions are used to create a new Marker.
                // You can specify location, title etc with MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                        .title("Estas Acá");

                //Adding the created the marker on the map
                mMarker = mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_VALUE));

                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),
                        currentLocation.getLongitude());
                mMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                        .title("Estas Acá"));


                int height = 32;
                int width = 64;
                BitmapDrawable bitMapCarDraw = (BitmapDrawable)
                        getResources().getDrawable(R.drawable.car);
                Bitmap bCar = bitMapCarDraw.getBitmap();
                Bitmap bCarSmallMarker = Bitmap.createScaledBitmap(bCar, width, height, false);

                BitmapDrawable bitMapPinDraw = (BitmapDrawable)
                        getResources().getDrawable(R.drawable.ic_map_pin_36);
                Bitmap bPin = bitMapPinDraw.getBitmap();

                originMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bPin)).title("origen"));
                originMarker.setVisible(false);

                destinyMarker= mMap.addMarker(new MarkerOptions().position(currentLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bPin)).title("destino"));
                destinyMarker.setVisible(false);

                driverMarker= mMap.addMarker(new MarkerOptions().position(currentLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bCarSmallMarker)).anchor(0.5f, 0.5f));
                driverMarker.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED USER");
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


    // Call Back method  to get the data form other Activity
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
                    //mockDrawLine();
                }else{
                    Log.d(TAG_USER_HOME,"no data is returned from activity PLACEAUTOCOMPLETEACTIVITY");
                }
            }
        }
    }


    public void getRouteTravel() {
        showOptionsToTravel();
        mCardViewSearch.setVisibility(View.INVISIBLE);
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.RED);

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    mRoute = mMap.addPolyline(rectLine);
                    md.getDurationText(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        new GMapV2DirectionAsyncTask(handler, mOrigin, mDestiny,
                GMapV2Direction.MODE_DRIVING, getApplicationContext()).execute();
    }

    //init fragment options travel
    public void showOptionsToTravel() {
        mFragmentTest = new OptionsTravelFragment();
        mFragmentTest.setSocketIO(mSocket);
        replaceFragment(mFragmentTest , true);
    }


    public void showInfoDriverAssigned(TravelAssignedDTO travelAssignedDTO){
        finishPreviousFragments();
        InfoDriverAssignFragment info = new InfoDriverAssignFragment();
        info.setTravelAssignedDTO(travelAssignedDTO);
        replaceFragment(info,true);
        showRouteFullInAssignTravel();
    }

    public void showRouteFullInAssignTravel(){
        Handler handler = new Handler();
        handler.postDelayed( () -> {
                // Do something after 50ms
                try{
                    int heightScreen = getResources().getDisplayMetrics().heightPixels;
                    int padding = heightScreen/20; //space in px between box edges
                    LatLngBounds.Builder bc = new LatLngBounds.Builder();
                    bc.include(mOrigin);
                    bc.include(mDestiny);
                    FrameLayout mapLayout = findViewById(R.id.include_user_map);
                    int widthMap = mapLayout.getWidth();
                    int heightMap = mapLayout.getHeight();
                    LinearLayout optionLayout = findViewById(R.id.options_travel);
                    int heightOption = optionLayout.getHeight();

                    int newHeight = heightMap-heightOption;
                    Log.d("DIMENSION","width: "+widthMap+ "  "+" height: "+newHeight);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),widthMap,newHeight,padding));
                    //other magic number
                    mMap.animateCamera(CameraUpdateFactory.scrollBy(0,heightOption/2));
                }catch (Exception ex){
                    Log.e(TAG_USER_HOME,ex.toString());
                }
        }, 50);

    }


    public void generateMockDriverAssign(View view){
        try{
            Log.d(this.getClass().getName(),"========================");
            Fragment mapFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.map);
            int width = mapFragment.getView().getMeasuredWidth();
            int height = mapFragment.getView().getMeasuredHeight();

            Log.i("DIMENSION","width: "+width+ "  "+" height: "+height);
            width = getResources().getDisplayMetrics().widthPixels;
            height = getResources().getDisplayMetrics().heightPixels;
            Log.i("DIMENSION","width: "+width+ "  "+" height: "+height);

            Person user = new Person(1,"Juan Fernando ","Perez Gonzales");
            Person driver = new Person(1,"Chano Santiago ","Moreno Charpentier");
            TravelAssignedDTO travelAssignedDTO = new TravelAssignedDTO(1,"20 minutos",user,driver);

            mCardViewSearch.setVisibility(View.INVISIBLE);
            fastGenerationOriginDestiny(view);
            showInfoDriverAssigned(travelAssignedDTO);
        }catch (Exception ex){
            Log.e(TAG_USER_HOME,"Error to generate mock Driver Assign");
        }

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
        handler.postDelayed(() -> {
            // Do something after 2000ms
            returnOriginalState();
        }, 2000);
    }

    public void returnOriginalState(){
        if(mRoute != null)
            mRoute.remove();
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Estas Acá"));
        mCardViewSearch.setVisibility(View.VISIBLE);
        mMessageCard.setVisibility(CardView.INVISIBLE);
        originMarker.setVisible(false);
        destinyMarker.setVisible(false);
    }


    public void ShowPositionDriver(float latitude, float longitude){

        Location prevLocation = new Location("");
        prevLocation.setLatitude(driverMarker.getPosition().latitude);
        prevLocation.setLongitude(driverMarker.getPosition().longitude);

        LatLng newLatLng = new LatLng(latitude,longitude);
        Location newLocation = new Location("");
        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);

        Float bearing = prevLocation.bearingTo(newLocation);
        driverMarker.setRotation(bearing - 270);
        driverMarker.setVisible(true);
        driverMarker.setPosition(newLatLng);

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, ZOOM_VALUE));
    }


    //TODO: esto falta implementar, tiene que notificar el server cuando llega el chofer al origen
    public void initTravel(){
        finishPreviousFragments();
        replaceFragment(new TrackingTravelFragment(),true);
    }


    public void showRatingBar(){
        finishPreviousFragments();
        returnOriginalState();
        Intent intent = new Intent(this, UserFinalScreen.class);
        startActivity(intent);
    }


    /* BEGIN OF SOCKET CONNECTION*/

    public void listenDriverPosition(){
        mSocket.on(mConstants.getEVENT_POSITION_DRIVER(),
                mListenerPositionDriver = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread( ()-> {
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
                        JSONObject response = (JSONObject) args[0];
                        Log.d(TAG_CONNECTION_SERVER, "Established Connection");
                        try{
                            idUSer= response.getInt("id");
                        }catch (Exception ex){
                            Log.e(TAG_CONNECTION_SERVER,"user has no assigned a id");
                        }
                    }
                });
            }
        });
        mSocket.emit(TAG_ROL,ROL);
    }


    //listen if arrive message that driver arrived to user
    public void listenDriverArrivedUser() {
        mSocket.on(mConstants.getEVENT_DRIVER_ARRIVED_USER(),
                mListenerDriverArrivedToUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread( () -> {
                    Log.d(this.getClass().getName(),"the driver has arrived to user");
                });
            }
        });
    }

    //listen if arrive message that driver arrived to destiny
    public void listenDriverArrivedDestiny() {
        mSocket.on(mConstants.getEVENT_DRIVER_ARRIVED_DESTINY(),
                mListenerDriverArrivedToDestiny = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread( () -> {
                    Log.d(TAG_USER_HOME,"message finalize travel arrived");
                    JSONObject data = (JSONObject) args[0];
                    showRatingBar();
                });
            }
        });
    }

    //listen if arrive message that driver arrived to destiny
    public void listenCancelTravel() {
        mSocket.on(mConstants.getEVENT_CANCEL_TRAVEL(),
                mListenerDriverCancelTravel = new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread( () -> {
                            Log.d(this.getClass().getName(),"message finalize travel arrived");
                            Log.d(this.getClass().getName(),args[0].toString());
                            finishPreviousFragments();
                            returnOriginalState();
                            if (mFragmentCanceledTravel == null) mFragmentCanceledTravel = new CanceledTravelFragment();
                            replaceFragment(mFragmentCanceledTravel,true);
                        });
                    }
                });
    }


    public void fastGenerationOriginDestiny(View view) {
        showOptionsToTravel();
        mCardViewSearch.setVisibility(View.INVISIBLE);
        mOrigin= new LatLng(currentLocation.getLatitude()-0.05,
                currentLocation.getLongitude());
        //mOrigin =  new LatLng(-34.59427772830024,-58.36834330111742);
        originMarker.setVisible(true);
        originMarker.setPosition(mOrigin);

        mDestiny= new LatLng(currentLocation.getLatitude()+0.06,
                currentLocation.getLongitude());
        //mDestiny =  new LatLng(-34.67436210470961,-58.42358440160751);
        destinyMarker.setVisible(true);
        destinyMarker.setPosition(mDestiny);

        mRoute = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(mOrigin, mDestiny));
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

/*        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.options_travel);*/

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getSupportFragmentManager().popBackStackImmediate();
        }

        return isPop;
    }

    public boolean finishPreviousFragments() {
        return popFragment();
    }


    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        if(!isDestroyed()){
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            if (addToBackStack) {
                transaction.addToBackStack(null);

            } else {
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
            transaction.replace(R.id.options_travel, fragment);
            //transaction.commit();
            //por ahora....
            transaction.commitAllowingStateLoss();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    public void acceptAction(View view){
        getSupportFragmentManager().beginTransaction().
                remove(mFragmentCanceledTravel).commit();
    }

    /*END REPLACE FRAGMENT*/


    public void listenAssignedDriver() {
        mSocket.on(mConstants.getEVENT_NOTIFICATION_TRAVEL(),
                mListenerAssignDriver = new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread( () -> {
                            Log.d(this.getClass().getName(),"I was assign a driver");
                            //finishFragmentExecuted();
                            try{
                                JSONObject response = (JSONObject) args[0];
                                Log.i(this.getClass().getName(), response.toString());
                                Gson gson =  new Gson();

                                TravelAssignedDTO travelAssignedDTO =
                                      gson.fromJson(response.toString(),TravelAssignedDTO.class);


                                /*Person user = new Person(1,"Juan Fernando ","Perez Gonzales");
                                Person driver = new Person(1,"Chano Santiago ","Moreno Charpentier");
                                TravelAssignedDTO travelAssignedDTO = new TravelAssignedDTO(1,"20 minutos",user,driver);*/



                                Log.i(this.getClass().getName(), travelAssignedDTO
                                        .getDriver().toString());

                                showInfoDriverAssigned(travelAssignedDTO);

                            }catch (Exception e){
                                Log.d(this.getClass().getName(),e.toString());
                                Log.d(this.getClass().getName(), "no data found");
                                showDriverNotFound();
                            }

                            mSocket.off(mConstants.getEVENT_NOTIFICATION_TRAVEL(),
                                    mListenerAssignDriver);
                        });
                    }
                });
    }

}

