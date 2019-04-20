package com.uberpets.mobile;

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
import com.uberpets.model.PetSize;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.model.TravelConfirmationDTO;
import com.uberpets.model.TravelPriceDTO;
import com.uberpets.model.TravelDTO;
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
    private RecyclerView mRecyclerView;
    private FloatingActionButton mButtonFab;
    private Button mButtonGetTravel;
    private TextView mPriceText;
    private CardView mCardPrice;
    private String travelID;


    public OptionsTravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_options_travel,
                container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view_layout_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new SizePetsAdapter(new ArrayList<PetSize>(0));
        mAdapter.updateList();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

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



    //init Show Searching Person
    public void cancelSearchingDriver() {
        isQueryCanceled = true;

        /**
         *falta ver cancelar la query...
         * mandar al servidor que ya no quiere el viaje...
         * esto se puede omitir
         */
    }

    /**
     *falta hacer que si se modifica alguna de las opciones de viaje
     * se tiene que desactivar el confirmar viaje y tiene que volver a cotizar
     * - evaluar que no se cambie el tamaño o la cantidad de mascotas
     * - evaluar que no se cambie el punto origen y el punto destino
     * - evaluar que no se cambie la opción de acompañante
     */

    /*
    public void changesOptionsTravel() {
        mButtonGetTravel.setText("Cotizar Viaje");
        mPriceText.setText("$0");
        readyToGetTravel=true;
    }*/


    public void addItem(){
        mAdapter.updateList();
    }


    public void onClickButtonGetTravel() {
        if (!readyToGetTravel)
            getTravelQuote();
        else
            confirmTravel();
    }

    //user send request to get quotation of travel
    public void getTravelQuote() {
        TravelDTO quotation =  new TravelDTO.TravelDTOBuilder(
                myActivity.getmOrigin(),myActivity.getmDestiny())
                .setUserId("user1")
                .setHasACompanion(optionCompanion.isChecked())
                .setPetSmallAmount(mAdapter.getAllLittlePets())
                .setPetMediumAmount(mAdapter.getAllMediumPets())
                .setPetLargeAmount(mAdapter.getAllBigPets())
                .build();

        App.nodeServer.post("/travel/cotization",
                quotation, TravelPriceDTO.class, new Headers())
                .run(this::responseQuotation, this::errorQuotation);
    }


    public void responseQuotation(TravelPriceDTO priceTravel) {
        if (priceTravel != null) {
            mButtonGetTravel.setText("Pedir Viaje");
            String price = "$"+ priceTravel.getPrice();
            mPriceText.setText(price);
            readyToGetTravel=true;
            travelID = priceTravel.getTravelID();
        }
    }

    public void errorQuotation(Exception ex) {
        /*
        Show message of Error
         */
        Toast.makeText(getContext()
                , "Error al cotizar el viaje, inténtelo más tarde"
                , Toast.LENGTH_LONG).show();
        isQueryCanceled = false;
    }


    //user send request to confirm travel
    public void confirmTravel() {
        if (readyToGetTravel){
            showSearchingDriver();
            TravelConfirmationDTO travelConfirmationDTO =
                    new TravelConfirmationDTO(travelID,myActivity.ROL);
            App.nodeServer.post("/travel/confirmation",
                    travelConfirmationDTO, TravelAssignedDTO.class, new Headers())
                    .onDone((s,ec)->finishFragmentExecuted())
                    .run(this::handleGoodResponse, this::handleErrorResponse);
        }
    }


    public void showSearchingDriver() {
        SearchingDriverFragment fragment2 = new SearchingDriverFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, fragment2);
        fragmentTransaction.commit();
    }

    public void finishFragmentExecuted() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, this);
        fragmentTransaction.commit();
    }


    public void handleGoodResponse(TravelAssignedDTO travelAssignedDTO) {
        if(!isQueryCanceled) {
            if(travelAssignedDTO != null){
                Log.i(TAG_REQUEST_SERVER, travelAssignedDTO.getDriver().toString());
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
                    default:
                        Log.d(TAG_REQUEST_SERVER, "error to connect server");
                        myActivity.showMessageCard();
                }
            } else
                Toast.makeText(getContext()
                        , "Error al solicitar el viaje, inténtelo más tarde"
                        , Toast.LENGTH_LONG).show();
            isQueryCanceled = false;
        }
    }

}
