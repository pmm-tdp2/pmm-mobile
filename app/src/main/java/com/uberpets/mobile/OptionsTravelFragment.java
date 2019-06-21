package com.uberpets.mobile;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ServerError;
import com.uberpets.Constants;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.model.TravelConfirmationDTO;
import com.uberpets.model.TravelDTO;
import com.uberpets.model.TravelPriceDTO;
import com.uberpets.services.App;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsTravelFragment extends Fragment {

    private CheckBox optionCompanion;
    private SizePetsAdapter mAdapter;
    private boolean readyToGetTravel =false;
    private UserHome myActivity;
    private FloatingActionButton mButtonFab;
    private Button mButtonGetTravel;
    private TextView mPriceText;
    private int travelID;
    private Constants mConstants = Constants.getInstance();


    public OptionsTravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_options_travel,
                container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_layout_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mButtonFab = rootView.findViewById(R.id.fab);

        mAdapter = new SizePetsAdapter(new ArrayList<>(0), mButtonFab);
        mAdapter.updateList();
        recyclerView.setAdapter(mAdapter);

        mButtonGetTravel =rootView.findViewById(R.id.button_travel);
        optionCompanion =rootView.findViewById(R.id.checkBox_option_companion);

        mPriceText = rootView.findViewById(R.id.text_price);

        myActivity = (UserHome) getActivity();

        setButtonFab();
        setButtonGetTravel();

        return rootView;
    }


    private void setButtonFab(){mButtonFab.setOnClickListener(view->addItem());}

    private void setButtonGetTravel(){
        mButtonGetTravel.setOnClickListener(view->onClickButtonGetTravel());
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


    private void addItem(){
        mAdapter.updateList();
    }


    private void onClickButtonGetTravel() {
        if (!readyToGetTravel)
            getTravelQuote();
        else
            confirmTravel();
    }

    //user send request to get quotation of travel
    private void getTravelQuote() {

        int amountPets = mAdapter.getAllLittlePets()
                + mAdapter.getAllMediumPets() + mAdapter.getAllBigPets();

        if (amountPets > 0){
            TravelDTO quotation =  new TravelDTO.TravelDTOBuilder(
                    myActivity.getmOrigin(),myActivity.getmDestiny())
                    .setUserId(myActivity.getidUser())
                    .setHasCompanion(optionCompanion.isChecked())
                    .setSmallPetQuantity(mAdapter.getAllLittlePets())
                    .setMediumPetQuantity(mAdapter.getAllMediumPets())
                    .setBigPetQuantity(mAdapter.getAllBigPets())
                    .build();

            Log.i(this.getClass().getName(),"COTIZATION: " + quotation);
            App.nodeServer.post("/api/travels/simulateQuote",
                    quotation, TravelPriceDTO.class, new Headers())
                    .run(this::responseQuotation, this::errorQuotation);
            Log.i(this.getClass().getName(),"DONE REQUEST: ");
        }else{
            Toast toast = Toast.makeText(getActivity(),
                    "Seleccione al menos una mascota",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }


    private void responseQuotation(TravelPriceDTO priceTravel) {
        Log.i(this.getClass().getName(),"COTIZATION: "+priceTravel.toString());
        mButtonGetTravel.setText(R.string.text_button_request_travel);
        String price = "$"+ priceTravel.getPrice();
        mPriceText.setText(price);
        readyToGetTravel=true;
        travelID = priceTravel.getTravelId();
    }

    private void errorQuotation(Exception ex) {
        /*
        Show message of Error
         */
        Log.e(this.getClass().getName(),ex.toString());
        Toast.makeText(getContext()
                , getString(R.string.error_quotation)
                , Toast.LENGTH_LONG).show();
    }


    //user send request to confirm travel
    private void confirmTravel() {
        Log.i(this.getClass().getName(),"he pedido un viaje");
        if (readyToGetTravel){
            showSearchingDriver();
            TravelConfirmationDTO travelConfirmationDTO =
                    new TravelConfirmationDTO(travelID,mConstants.getID_USERS()
                            ,myActivity.getidUser(),true);
            App.nodeServer.post("/api/travels/confirmation",
                    travelConfirmationDTO, TravelAssignedDTO.class, new Headers())
                    //.onDone((s,ec)->finishFragmentExecuted())
                    .run(this::handleGoodResponse, this::handleErrorResponse);
        }
    }


    private void showSearchingDriver() {
        SearchingDriverFragment fragment2 = new SearchingDriverFragment();
        FragmentManager fragmentManager = myActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, fragment2);
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void finishFragmentExecuted() {
        FragmentManager fragmentManager = myActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, this);
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void handleGoodResponse(TravelAssignedDTO travelAssignedDTO) {
        finishFragmentExecuted();
        myActivity.driverAssignedToTravel(travelAssignedDTO);
    }

    private void handleErrorResponse(Exception e) {
        Log.e(this.getClass().getName(),"Error no driver found");
        Log.e(this.getClass().getName(),e.toString());
        Activity activity = getActivity();
        if(activity != null ){
            finishFragmentExecuted();
            myActivity.showMessageCard();

            /*Toast toast = Toast.makeText(activity
                    , "No se pudo encontrar un chofer, vuelva a intentarlo "
                    , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();*/

            //show data of travel

            /*if (e instanceof ServerError) {
                Log.d(this.getClass().getName(), "error to connect server");
                myActivity.showMessageCard();
            } else
                Toast.makeText(activity
                        , getString("Error al ")
                        , Toast.LENGTH_LONG).show();*/
        }
    }
    
}
