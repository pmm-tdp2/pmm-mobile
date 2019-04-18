package com.uberpets.mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ServerError;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.Driver;
import com.uberpets.model.PetSize;
import com.uberpets.model.Travel;
import com.uberpets.services.App;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsTravelFragment extends Fragment {

    private CheckBox optionCompanion;
    private SizePetsAdapter mAdapter;
    private boolean isQueryCanceled =false;
    private boolean readyToGetTravel =false;
    private String TAG_REQUEST_SERVER="RESQUEST_SERVER_TRAVEL";
    private UserHome myActivity;
    RecyclerView mRecyclerView;
    FloatingActionButton mButtonFab;
    Button mButtonGetTravel;
    TextView mPriceText;
    CardView mCardPrice;

    public OptionsTravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_options_travel, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view_layout_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new SizePetsAdapter(new ArrayList<PetSize>(0));
        mAdapter.updateList();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mButtonFab = rootView.findViewById(R.id.fab);
        mButtonGetTravel =rootView.findViewById(R.id.button_travel);
        optionCompanion =rootView.findViewById(R.id.checkBox_option_companion);

        mPriceText = rootView.findViewById(R.id.text_price);
        mCardPrice =  rootView.findViewById(R.id.card_price);

        myActivity = (UserHome) getActivity();

        setmButtonFab();
        setmButtonGetTravel();

        return rootView;
    }


    public void setmButtonFab(){
        mButtonFab.setOnClickListener(view->addItem());
    }

    public void setmButtonGetTravel(){
        mButtonGetTravel.setOnClickListener(view->onClickButtonGetTravel());
    }



    //init Show Searching Driver
    public void cancelSearchingDriver() {
        isQueryCanceled = true;

        /**
         *falta ver cancelar la query...
         * mandar al servidor que ya no quiere el viaje...
         */
    }

    public void addItem(){
        mAdapter.updateList();
    }


    public void onClickButtonGetTravel() {
        if (!readyToGetTravel)
            getPriceTravel();
        else
            confirmTravel();
    }

/*
    public void changesOptionsTravel() {
        mButtonGetTravel.setText("Cotizar Viaje");
        mPriceText.setText("$0");
        readyToGetTravel=true;
    }*/


    //user send request to get price of travel
    public void getPriceTravel() {
        /*showSearchingDriver();
        Travel travel =  new Travel("user1",myActivity.getmOrigin(),myActivity.getmDestiny());
        App.nodeServer.post("/travels",
                travel, Driver.class, new Headers())
                .onDone((s,ec)->finishFragmentExecuted())
                .run(this::handleGoodResponse, this::handleErrorResponse);*/
        mButtonGetTravel.setText("Pedir Viaje");
        //mButtonGetTravel.setBackgroundColor(Color.rgb(147,158,250));
        mPriceText.setText("$200");
        readyToGetTravel=true;
    }


    //user send request to confirm travel
    public void confirmTravel() {
        //if (readyToGetTravel){
            showSearchingDriver();
            Travel travel =  new Travel("user1",myActivity.getmOrigin(),myActivity.getmDestiny());
            App.nodeServer.post("/travels",
                    travel, Driver.class, new Headers())
                    .onDone((s,ec)->finishFragmentExecuted())
                    .run(this::handleGoodResponse, this::handleErrorResponse);
        //}
    }


    public int getAllLittlePets() {
        return mAdapter.getAllLittlePets();
    }

    public int getAllMediumPets() {
        return mAdapter.getAllMediumPets();
    }

    public int getAllBigPets() {
        return mAdapter.getAllBigPets();
    }

    public boolean includesCompanion() { return optionCompanion.isChecked(); }


    public void showSearchingDriver() {
        SearchingDriverFragment fragment2 = new SearchingDriverFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, fragment2);
        fragmentTransaction.commit();
    }

    public void finishFragmentExecuted(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, this);
        fragmentTransaction.commit();
    }


    public void handleGoodResponse(Driver driver) {
        if(!isQueryCanceled) {
            if(driver != null){
                Log.i(TAG_REQUEST_SERVER, driver.toString());
                myActivity.showInfoDriverAssigned();
            }else{
                Log.d(TAG_REQUEST_SERVER, "no data found");
                myActivity.showDriverNotFound();
            }
        }
        isQueryCanceled = false;
    }

    public void handleErrorResponse(Exception ex) {
        if (!isQueryCanceled) {
            if (ex instanceof ServerError) {
                switch (((ServerError) ex).networkResponse.statusCode) {
                    case 500:
                        Log.d(TAG_REQUEST_SERVER, "error to connect server");
                        myActivity.showMessageCard();
                        break;
                }
            } else
                Toast.makeText(getContext()
                        , "Error al solicitar el viaje, intentelo más tarde"
                        , Toast.LENGTH_LONG).show();
            isQueryCanceled = false;
        }
    }

}
