package com.uberpets.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.uberpets.Constants;
import com.uberpets.model.CopyTravelDTO;
import com.uberpets.model.GeograficCoordenate;
import com.uberpets.model.TraceDTO;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.model.TravelDTO;
import com.uberpets.services.TraceService;
import com.uberpets.util.AccountImages;
import com.uberpets.util.AccountSession;


import org.json.JSONObject;

import java.net.URISyntaxException;


public class  DriverHome
        extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            OnMapReadyCallback,
            TravelRequestFragment.OnFragmentInteractionListener,
            DriverFollowUpTravel.OnFragmentInteractionListener
{
    private GoogleMap mMap;
    private final String ROL = "DRIVER";
    private final String TAG_ROL = "ROL";
    private String idDriver;
    private final Constants mConstant = Constants.getInstance();
    private boolean inTravel = false;
    private Location currentLocation;
    private LatLng mockLocation;
    private Marker currentPositionMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static float ZOOM_VALUE = 14.0f;
    private static double MOVEMENT_SPEED = 0.001;
    private int locationRequestCode = 1000;
    private Fragment notification;

    private Socket mSocket;
    private final String EVENT_CONNECTION = "message";
    private Emitter.Listener mListenerConnection;
    private Emitter.Listener mListenerNotificationTravel;
    private TraceService traceService = new TraceService();
    Constants mConstants = Constants.getInstance();
    private TravelDTO mTravelDTO;

    //private final String URL = "https://young-wave-26125.herokuapp.com/pmm";
    //private final String URL = mConstants.getURL_REMOTE() + mConstants.getURL_BASE_PATH();
    //private final String URL = mConstants.getURL();
    private static final String[] TRANSPORTS = {
            "websocket"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setting id:
        this.idDriver = AccountSession.getIdLogin(this) == "" ? "987654399" : AccountSession.getIdLogin(this);
        Log.i(this.getClass().getName(),"idFacebook: "+idDriver);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_driver);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = hView.findViewById(R.id.image_profile_driver_navigation);
        Bitmap bitmapProfile = AccountImages.getInstance().getPhotoProfile();
        imageView.setImageBitmap(bitmapProfile);

        /*if( bitmapProfile != null){
            int lowerValue = bitmapProfile.getHeight() < bitmapProfile.getWidth()?
                    bitmapProfile.getHeight() :  bitmapProfile.getWidth();

            Bitmap bitmapResized = (Bitmap.createScaledBitmap(bitmapProfile, lowerValue, lowerValue, false));

            //creamos el drawable redondeado
            RoundedBitmapDrawable roundedDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), bitmapResized);
            //asignamos el CornerRadius
            roundedDrawable.setCornerRadius(bitmapResized.getHeight());
            imageView.setImageDrawable(roundedDrawable);
        }*/



        //is used to obtain user's location, with this our app no needs
        // to manually manage connections
        //to Google Play Services through GoogleApiClient
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);


        {
            try {
                final IO.Options options = new IO.Options();
                options.transports = TRANSPORTS;
                String uuu = Constants.getInstance().getURL_SOCKET();
                mSocket = IO.socket(Constants.getInstance().getURL_SOCKET(), options);
                Log.i(this.getClass().getName(),"io socket succes");
                connectToServer();
                listenNotificaciónTravel();
            } catch (URISyntaxException e) {
                Log.e(this.getClass().getName(),"io socket failure");
            }
        }
        requestPermission();

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_home, menu);
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

        }else if (id == R.id.logout_from_home_account_driver) {
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
        if (ActivityCompat.checkSelfPermission(DriverHome.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(DriverHome.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},locationRequestCode);
        } else{
            fetchLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String [] permissions,
                                            @NonNull int [] grantResults) {
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
        if (ActivityCompat.checkSelfPermission(DriverHome.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(DriverHome.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // obtain the last location and save in task, that's Collection's Activities
            Task<Location> task = mFusedLocationProviderClient.getLastLocation();
            // add object OnSuccessListener, when the connection is established
            // and the location is fetched
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        // Obtain the SupportMapFragment and get notified
                        // when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment)
                                getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(DriverHome.this);
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
            Log.d(this.getClass().getName(), "GOOGLE GOOD LOADED");
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mockLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            int height = 32;
            int width = 64;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Estas Acá")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .anchor(0.5f, 0.5f);

            //Adding the created the marker on the map
            currentPositionMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_VALUE));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getName(), "GOOGLE MAPS NOT LOADED DRIVER");
        }
    }

    public boolean popFragment() {
        boolean isPop = false;

        /*Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.layout_driver_to_replace);*/

        Log.i(this.getClass().getName(),"####cantidad: "+getSupportFragmentManager().getBackStackEntryCount());

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            Log.i(this.getClass().getName(),"####FRAGMEENT POPBACK");
            isPop = true;
            getSupportFragmentManager().popBackStackImmediate();
        }

        return isPop;
    }

    public boolean finishPreviousFragments() {
        return popFragment();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        Log.d(this.getClass().getName(),"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        if(!isDestroyed()){
            Log.d(this.getClass().getName(),"/////////////////////////////");
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            if (addToBackStack) {
                transaction.addToBackStack(null);

            } else {
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
            transaction.replace(R.id.layout_driver_to_replace, fragment);
            //transaction.commit();
            //por ahora....
            transaction.commitAllowingStateLoss();
            //if(!isFinishing())
            getSupportFragmentManager().executePendingTransactions(); //error recurrente
            Log.d(this.getClass().getName(),"Reemplazando fragment");
        }
    }

    private void removeUpperSectionFragment(){
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.layout_driver_to_replace);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    public void moveLocationUp(android.view.View view){
        moveLocation(MOVEMENT_SPEED,0);
    }

    public void moveLocationDown(android.view.View view){
        moveLocation(-MOVEMENT_SPEED,0);
    }

    public void moveLocationLeft(android.view.View view){
        moveLocation(0,-MOVEMENT_SPEED);
    }

    public void moveLocationRight(android.view.View view){
        moveLocation(0,MOVEMENT_SPEED);
    }

    public void moveLocation(double latitude, double longitude){
        Location prevLocation = new Location("");
        prevLocation.setLatitude(mockLocation.latitude);
        prevLocation.setLongitude(mockLocation.longitude);

        mockLocation = new LatLng(mockLocation.latitude + latitude, mockLocation.longitude + longitude);

        Location newLocation = new Location("");
        newLocation.setLongitude(mockLocation.longitude);
        newLocation.setLatitude(mockLocation.latitude);
        //TODO:id user esta harcodeado, ver porque se usaría asi
        TraceDTO traceDTO = new TraceDTO("", idDriver,
                new GeograficCoordenate(String.valueOf(newLocation.getLatitude()), String.valueOf(newLocation.getLongitude())));
        traceService.saveTrace(traceDTO, this);
        currentPositionMarker.setPosition(mockLocation);

        Float bearing = prevLocation.bearingTo(newLocation);

        currentPositionMarker.setRotation(bearing - 270);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mockLocation, ZOOM_VALUE));
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAttachFragment(Fragment fragment){

    }

    public void showNewTravelNotification(android.view.View view) {
        TravelDTO mockTravelDTO = new TravelDTO.TravelDTOBuilder(
                new LatLng(1.0,1.0), new LatLng(1.5,1.5))
                .setTravelId(-1).setDriverId("1").setHasCompanion(true)
                .setBigPetQuantity(0).setSmallPetQuantity(1).setMediumPetQuantity(0)
                .setUserId("").build();
        TravelRequestFragment travelRequestFragment= new TravelRequestFragment();
        travelRequestFragment.setTravelDTO(mockTravelDTO);
        replaceFragment(travelRequestFragment,true);
    }


    /*
        methods that implements from interface the fragment TravelRequestFragment
     */
    public void rejectTravel(){
        removeUpperSectionFragment();
        inTravel = false;
    }

    public void acceptTravel(TravelAssignedDTO travelAssignedDTO){
        //TODO: mostrar información o mandarla a otro fragment del viaje asignado
        inTravel = true;
        this.mTravelDTO.setUserId(travelAssignedDTO.getUser().getId());
        Log.i(this.getClass().getName(),"init FollowUpTravel");
        finishPreviousFragments();
        DriverFollowUpTravel driverFollowUpTravel = DriverFollowUpTravel.newInstance("","");
        driverFollowUpTravel.setIdDriver(idDriver);
        driverFollowUpTravel.setmTravelAssignedDTO(travelAssignedDTO);
        replaceFragment(driverFollowUpTravel, true);
        Log.i(this.getClass().getName(),"last line  FollowUpTravel");
    }

    /*
    * methods that are implemented by fragment interface DriverFollowUpTravel
    * */
    public void cancelOngoingTravel(){
        removeUpperSectionFragment();
    }

    public void finishTravel(){
        finishPreviousFragments();
        Intent intent = new Intent(this, DriverFinalScreen.class);
        CopyTravelDTO copyTravelDTO = new CopyTravelDTO.CopyTravelDTOBuilder()
                .setBigPetQuantity(mTravelDTO.getBigPetQuantity())
                .setMediumPetQuantity(mTravelDTO.getMediumPetQuantity())
                .setSmallPetQuantity(mTravelDTO.getSmallPetQuantity())
                .setTravelId(mTravelDTO.getTravelId())
                .setUserId(mTravelDTO.getUserId())
                .setDriverId(mTravelDTO.getDriverId())
                .setHasCompanion(mTravelDTO.isHasCompanion())
                .build();
        intent.putExtra("TRAVEL",copyTravelDTO);
        Log.i(this.getClass().getName(),"---------go to rating driver--------------");
        Log.i(this.getClass().getName(),copyTravelDTO.toString());
        Log.i(this.getClass().getName(),"-----------------------------------");
        startActivity(intent);
        //TODO: mandar notificación al server de la puntuacion
        inTravel = false;
    }

    /* BEGIN OF SOCKET CONNECTION*/

    public void connectToServer(){
        mSocket.connect();
        mSocket.on(EVENT_CONNECTION, mListenerConnection = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject response = (JSONObject) args[0];
                        Log.d(this.getClass().getName(), "Established Connection");
                        /*try{
                            idDriver= response.getString("id");
                        }catch (Exception ex){
                            Log.e(this.getClass().getName(),"driver has no assigned a id");
                        }*/
                    }
                });
            }
        });
        mSocket.emit(TAG_ROL, ROL, idDriver);
    }


    //listen request of travel
    public void listenNotificaciónTravel() {
        Log.d(this.getClass().getName(),mConstants.getEVENT_NOTIFICATION_TRAVEL());
        mSocket.on(mConstants.getEVENT_NOTIFICATION_TRAVEL(),
                mListenerNotificationTravel = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!inTravel){
                            JSONObject response = (JSONObject) args[0];
                            Gson gson = new Gson();
                            Log.d(this.getClass().getName(),"--NOTIFICACIÓN DE VIAJE-");
                            Log.d(this.getClass().getName(),response.toString());
                            Log.d(this.getClass().getName(),"------------------------");
                            mTravelDTO = gson.fromJson(response.toString(),TravelDTO.class);
                            mTravelDTO.setDriverId(idDriver);

                            //TODO: mostrar la cantidad de mascotas que tendrá el viaje
                            //TODO: dibujar el tramo del viaje
                            Log.d(this.getClass().getName(),mTravelDTO.toString());

                            TravelRequestFragment travelRequest =
                                    TravelRequestFragment.newInstance(idDriver,mTravelDTO);
                            /*TravelRequestFragment travelRequest = new TravelRequestFragment();
                            travelRequest.setTravelDTO(travelDTO);
                            travelRequest.setROL(ROL);
                            travelRequest.setIdDriver(idDriver);*/
                            Log.d(this.getClass().getName(),"----antes de replace notification---------");
                            replaceFragment(travelRequest,true);
                            Log.d(this.getClass().getName(),"----luego de replace notification---------");
                        }
                    }
                });
            }
        });
    }

    /* END OF SOCKET CONNECTION*/


    @Override
    public void onDestroy() {
        mSocket.emit("FIN ROL","DRIVER", this.idDriver);
        mSocket.off("FINISH", mListenerConnection);
        mSocket.off("FINISH", mListenerNotificationTravel);
        mSocket.disconnect();
        super.onDestroy();
    }

}
