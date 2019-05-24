package com.uberpets.mobile;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ServerError;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.uberpets.Constants;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.Person;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.model.TravelConfirmationDTO;
import com.uberpets.model.TravelPriceDTO;
import com.uberpets.model.TravelDTO;
import com.uberpets.services.App;

import org.json.JSONObject;

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
    private Socket socketIO;
    private Emitter.Listener mListenerAssignDriver;
    private Constants mConstants = Constants.getInstance();


    public OptionsTravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        setmButtonFab();
        setmButtonGetTravel();

        return rootView;
    }


    public void setmButtonFab(){mButtonFab.setOnClickListener(view->addItem());}

    public void setmButtonGetTravel(){
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
                .setUserId(myActivity.getIdUSer())
                .setHasACompanion(optionCompanion.isChecked())
                .setpetAmountSmall(mAdapter.getAllLittlePets())
                .setpetAmountMedium(mAdapter.getAllMediumPets())
                .setpetAmountLarge(mAdapter.getAllBigPets())
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
        Log.e(this.getClass().getName(),ex.toString());
        Toast.makeText(getContext()
                , getString(R.string.error_quotation)
                , Toast.LENGTH_LONG).show();
    }


    //user send request to confirm travel
    public void confirmTravel() {
        if (readyToGetTravel){
            showSearchingDriver();
            TravelConfirmationDTO travelConfirmationDTO =
                    new TravelConfirmationDTO(travelID,myActivity.ROL,myActivity.getIdUSer(),true);
            App.nodeServer.post("/travel/confirmation",
                    travelConfirmationDTO, TravelAssignedDTO.class, new Headers())
                    //.onDone((s,ec)->finishFragmentExecuted())
                    .run(this::handleGoodResponse, this::handleErrorResponse);
        }
    }


    public void showSearchingDriver() {
        SearchingDriverFragment fragment2 = new SearchingDriverFragment();
        FragmentManager fragmentManager = myActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, fragment2);
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void finishFragmentExecuted() {
        FragmentManager fragmentManager = myActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.options_travel, this);
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

//TODO: cambiar el tipo de respuesta
    public void handleGoodResponse(TravelAssignedDTO travelAssignedDTO) {
        if(travelAssignedDTO != null){
            /*Log.i(this.getClass().getName(), travelAssignedDTO.getDriver().toString());
            myActivity.showInfoDriverAssigned();*/
            Log.d(this.getClass().getName(), "LA SOLICITUD FUE RECIBIDA");
            //listenAssignedDriver();
        }else{
            Log.d(this.getClass().getName(), "NO SE PUDO MANDAR LA SOLICUTD");
            finishFragmentExecuted();
            myActivity.showDriverNotFound();
        }
    }

    public void handleErrorResponse(Exception ex) {
        finishFragmentExecuted();
        if (ex instanceof ServerError) {
            Log.d(this.getClass().getName(), "error to connect server");
            myActivity.showMessageCard();
        } else
            Toast.makeText(getContext()
                    , getString(R.string.error_quotation)
                    , Toast.LENGTH_LONG).show();
    }


    public void setSocketIO(Socket socketIO) {
        this.socketIO = socketIO;
    }

}
