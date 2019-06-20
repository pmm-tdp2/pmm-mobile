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
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.uberpets.Constants;
import com.uberpets.mobile.ui.main.CanceledTravelFragment;
import com.uberpets.model.CopyTravelDTO;
import com.uberpets.model.Person;
import com.uberpets.model.Travel;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.util.AccountImages;
import com.uberpets.util.AccountSession;
import com.uberpets.util.GMapV2Direction;
import com.uberpets.util.GMapV2DirectionAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static java.lang.Math.round;
import static org.apache.http.conn.params.ConnManagerParams.setTimeout;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public final String ROL = "USER";
    public final String TAG_ROL = "ROL";
    private GoogleMap mMap;
    private Polyline mRoute;
    private String idUser;
    private boolean onCourseTravel;
    private Marker driverPositionMarker;
    private Location driverLocation;

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

    private InfoDriverAssignFragment mFragmentTravelData;
    private OptionsTravelFragment mFragmentTest;
    private CanceledTravelFragment mFragmentCanceledTravel;
    private Constants mConstants = Constants.getInstance();
    private Travel mTravel;
    private boolean inTravel = false;

    private static final String[] TRANSPORTS = {
            "websocket"
    };

    private void moveDriverMarker(Double lat, Double lon){
        Location newLocation = new Location("");
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lon);

        driverPositionMarker.setPosition(new LatLng(lat, lon));

        Float bearing = driverLocation.bearingTo(newLocation);

        driverPositionMarker.setRotation(bearing - 270);
    }

    private void setDriverMarker(Double lat, Double lon){
        Log.d("Marker", "Setting marker");
        LatLng latLng = new LatLng(lat, lon);

        driverLocation = new Location("");
        driverLocation.setLatitude(lat);
        driverLocation.setLongitude(lon);

        int height = 32;
        int width = 64;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Su chofer")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .anchor(0.5f, 0.5f);

        //Adding the created the marker on the map
        driverPositionMarker = mMap.addMarker(markerOptions);
    }

    private Handler handler = new Handler();

    private Runnable getTravelInfo = new Runnable() {

        private void handleErrorGetTravel(Exception e) {
            Log.d("GET TRAVEL", e.getMessage());
        }

        private void handleSuccessGetTravel(Travel travel) {
            Log.d("GET TRAVEL", travel.toString());

            double arrivalTime = travel.getArrivalTime();
            double driverDistance = travel.getDriverDistance();

            mFragmentTravelData.setTimeToArrive(round(arrivalTime));
            mFragmentTravelData.setDriversDistance(round(driverDistance));

            if (onCourseTravel){
                if (driverPositionMarker == null) setDriverMarker(travel.getDriverLatitude(), travel.getDriverLongitude());
                else moveDriverMarker(travel.getDriverLatitude(), travel.getDriverLongitude());
            }
        }

        private void getTravelInfo(){
            /*String path = "/api/travelStatus/" + mTravel.getTravelId();
            App.nodeServer.get(path, Travel.class, new Headers())
                    .run(this::handleSuccessGetTravel, this::handleErrorGetTravel);

            Log.d("Handlers", "Called on main thread");*/
        }

        @Override
        public void run() {
            if (onCourseTravel){
                getTravelInfo();
            }
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCourseTravel = false;

        handler.post(getTravelInfo);

        setContentView(R.layout.activity_user_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setting id:
        this.idUser = AccountSession.getIdLogin(this).equals("") ? "123456782" : AccountSession.getIdLogin(this);
        Log.i(this.getClass().getName(),"idFacebook: "+idUser);

        NavigationView navigationView = findViewById(R.id.nav_view_user);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = hView.findViewById(R.id.image_profile_user_navigation);
        Bitmap bitmapProfile = AccountImages.getInstance().getPhotoProfile();
        imageView.setImageBitmap(bitmapProfile);
        /*if( bitmapProfile != null){
            //creamos el drawable redondeado
            RoundedBitmapDrawable roundedDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), bitmapProfile);
            //asignamos el CornerRadius
            roundedDrawable.setCornerRadius(100);
            imageView.setImageDrawable(roundedDrawable);
        }*/

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
                //listenAssignedDriver();
            } catch (URISyntaxException e) {
                Log.e(TAG_CONNECTION_SERVER,"io socket failure");
            }
        }

        requestPermission();
    }

    public String getidUser() {
        return idUser;
    }

    private void beginTravel(){this.inTravel = true;}
    private void endTravel(){this.inTravel = false;}
    private boolean isInTravel(){return inTravel;}

    @Override
    public void onBackPressed() {
        if (!finishPreviousFragments()) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
               //super.onBackPressed();
                moveTaskToBack(true);

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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        /*final Handler handler = new Handler() {
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

        };*/

        final Handler handler = new MyVeryOwnHandler(this);

        new GMapV2DirectionAsyncTask(handler, mOrigin, mDestiny,
                GMapV2Direction.MODE_DRIVING, getApplicationContext()).execute();
    }

    public static class MyVeryOwnHandler extends Handler {
        private final WeakReference<UserHome> mHome;

        MyVeryOwnHandler(UserHome service) {
            mHome = new WeakReference<>(service);
        }

        public void handleMessage(Message msg) {
            UserHome home = mHome.get();
            try {
                Document doc = (Document) msg.obj;
                GMapV2Direction md = new GMapV2Direction();
                ArrayList<LatLng> directionPoint = md.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.RED);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                home.mRoute = home.mMap.addPolyline(rectLine);
                md.getDurationText(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    //init fragment options travel
    public void showOptionsToTravel() {
        mFragmentTest = new OptionsTravelFragment();
        mFragmentTest.setSocketIO(mSocket);
        replaceFragment(mFragmentTest , true);
    }


    public void showInfoDriverAssigned(TravelAssignedDTO travelAssignedDTO){
        finishPreviousFragments();
        beginTravel();
        InfoDriverAssignFragment infoTravelFragment= new InfoDriverAssignFragment();
        mFragmentTravelData = infoTravelFragment;
        infoTravelFragment.setTravelAssignedDTO(travelAssignedDTO);
        replaceFragment(infoTravelFragment,true);
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
            Fragment mapFragment = getSupportFragmentManager().findFragmentById(R.id.map);

            int width = mapFragment.getView().getMeasuredWidth();
            int height = mapFragment.getView().getMeasuredHeight();

            Log.i("DIMENSION","width: "+width+ "  "+" height: "+height);
            width = getResources().getDisplayMetrics().widthPixels;
            height = getResources().getDisplayMetrics().heightPixels;
            Log.i("DIMENSION","width: "+width+ "  "+" height: "+height);

            Person user = new Person("1","Juan Fernando ");
            Person driver = new Person("1","Chano Santiago ");
            TravelAssignedDTO travelAssignedDTO = new TravelAssignedDTO(1,"20 minutos",user,driver);

            mCardViewSearch.setVisibility(View.INVISIBLE);
            fastGenerationOriginDestiny(view);
            showInfoDriverAssigned(travelAssignedDTO);

        }catch (Exception e){
            Log.e(this.getClass().getName(),"Error to generate mock Driver Assign");
            Log.e(this.getClass().getName(),e.getMessage());
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

        float bearing = prevLocation.bearingTo(newLocation);
        driverMarker.setRotation(bearing - 270);
        driverMarker.setVisible(true);
        driverMarker.setPosition(newLatLng);

    }


    public void showRatingBar(){
        endTravel();
        Log.d(this.getClass().getName(),"Init showRatingBard");
        finishPreviousFragments();
        returnOriginalState();
        Intent intent = new Intent(this, UserFinalScreen.class);
        CopyTravelDTO copyTravelDTO;

        copyTravelDTO = new CopyTravelDTO.CopyTravelDTOBuilder()
                .setBigPetQuantity(mTravel.getBigPetQuantity())
                .setMediumPetQuantity(mTravel.getMediumPetQuantity())
                .setSmallPetQuantity(mTravel.getSmallPetQuantity())
                .setTravelId(mTravel.getTravelId())
                .setUser(mTravel.getUser())
                .setDriver(mTravel.getDriver())
                .setHasCompanion(mTravel.isHasCompanion())
                .build();

        intent.putExtra("TRAVEL",copyTravelDTO);
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
                    public int hashCode() {
                        return super.hashCode();
                    }

                    @Override
                    public void run() {
                        Log.d(TAG_CONNECTION_SERVER, "Established Connection");
                    }
                });
            }
        });
        mSocket.emit(TAG_ROL, ROL, idUser);
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
                    if(isInTravel()){
                        Log.d(TAG_USER_HOME,"message finalize travel arrived");
                        onCourseTravel = false;
                        showRatingBar();
                    }else {
                        Log.w(this.getClass().getName(),"Server notify end travel when" +
                                "user is not in travel");
                    }
                });
            }
        });
    }



    //listen if driver cancel travel
    public void listenCancelTravel() {
        mSocket.on(mConstants.getEVENT_CANCEL_TRAVEL(),
                mListenerDriverCancelTravel = new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread( () -> {
                            if(isInTravel()) {
                                Log.d(this.getClass().getName(),"message finalize travel arrived");
                                Log.d(this.getClass().getName(),args[0].toString());
                                finishPreviousFragments();
                                returnOriginalState();
                                onCourseTravel = false;
                                if (mFragmentCanceledTravel == null) mFragmentCanceledTravel = new CanceledTravelFragment();
                                replaceFragment(mFragmentCanceledTravel,true);
                            }else{
                                Log.w(this.getClass().getName(),"Server notify travel canceled when user is not in travel");
                            }
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
        mSocket.emit("FIN ROL","USER", this.idUser);
        mSocket.disconnect();
        mSocket.off("FINISH", mListenerConnection);
        mSocket.off("FINISH", mListenerPositionDriver);
        mSocket.off("FINISH", mListenerDriverArrivedToUser);
        mSocket.off("FINISH", mListenerDriverArrivedToDestiny);
        mSocket.off("FINISH", mListenerDriverCancelTravel);
        super.onDestroy();
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

    public void driverAssignedToTravel(TravelAssignedDTO travelAssignedDTO) {

        if(travelAssignedDTO != null) {
            Log.d(this.getClass().getName(),"I was assign a driver");
            //finishFragmentExecuted();
            mTravel = new Travel((new Travel.TravelBuilder(
                    originMarker.getPosition(),
                    destinyMarker.getPosition()))
                    .setTravelId(travelAssignedDTO.getTravelId())
                    .setDriver(travelAssignedDTO.getDriver())
                    .setUser(travelAssignedDTO.getUser()));

            Log.d(this.getClass().getName(),travelAssignedDTO.toString());
            Log.d(this.getClass().getName(),"--------------------");

            Log.d(this.getClass().getName(),mTravel.toString());
            Log.d(this.getClass().getName(),"big:: "+mTravel.getBigPetQuantity());
            onCourseTravel = true;
            showInfoDriverAssigned(travelAssignedDTO);

        }else {
            Log.d(this.getClass().getName(), "No se pudo econtrar un chofer");
            showDriverNotFound();
        }
    }

}

