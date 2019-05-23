package com.uberpets.mobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uberpets.library.rest.Headers;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.model.TravelConfirmationDTO;
import com.uberpets.model.TravelDTO;
import com.uberpets.services.App;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TravelRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TravelRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelRequestFragment extends Fragment {
    private static final String ARG_PARAM1 = "info";

    // TODO: info should be info of the trip
    private String info;
    private Button mButtonAccept;
    private Button mButtonReject;
    private TravelDTO mTravelDTO;
    private String ROL;
    private int idDriver;

    private OnFragmentInteractionListener mListener;

    public TravelRequestFragment() {
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TravelRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TravelRequestFragment newInstance(String ROL, int idDriver, TravelDTO travelDTO) {
        TravelRequestFragment fragment = new TravelRequestFragment();
        Log.d("TravelRequestFragment","##################1");
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, info);
        //fragment.setArguments(args);
        fragment.setROL(ROL);
        fragment.setIdDriver(idDriver);
        fragment.setTravelDTO(travelDTO);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TravelRequestFragment","##################2");

        if (getArguments() != null) {
            info = getArguments().getString(ARG_PARAM1);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TravelRequestFragment","##################3");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d("TravelRequestFragment","##################4");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void rejectTravel();
        void acceptTravel(TravelAssignedDTO travelAssignedDTO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_travel_request,
                container, false);

        mButtonAccept = rootView.findViewById(R.id.acceptTravelButton);
        mButtonReject = rootView.findViewById(R.id.rejectTravelButton);

        setButtonReject();
        setButtonAccept();
        Log.d("TravelRequestFragment","##################6");
        updateDataTravel(rootView);
        Log.d("TravelRequestFragment","##################7");

        return rootView;
    }

    public void setTravelDTO(TravelDTO mTravelDTO) {
        this.mTravelDTO = mTravelDTO;
    }

    private void setROL(String ROL) {
        this.ROL = ROL;
    }

    private void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public void updateDataTravel(View rootView) {
        //TODO: show info the travel
        Log.d("TravelRequestFragment","##################8");
        Log.d(this.getClass().getName(),"TravelDTO: "+mTravelDTO.toString());
        TextView bigPets =rootView.findViewById(R.id.amount_big_pets);
        bigPets.setText(String.valueOf(mTravelDTO.getpetAmountLarge()));

        TextView mediumPets =rootView.findViewById(R.id.amount_medium_pets);
        mediumPets.setText(String.valueOf(mTravelDTO.getpetAmountMedium()));

        TextView littlePets =rootView.findViewById(R.id.amount_little_pets);
        littlePets.setText(String.valueOf(mTravelDTO.getpetAmountSmall()));

        TextView hasCompanion =rootView.findViewById(R.id.has_companion);
        hasCompanion.setText(mTravelDTO.isHasACompanion() ? getString(R.string.yes_string):
                getString(R.string.no_string));
        Log.d("TravelRequestFragment","##################9");
    }

    public void setButtonReject() {
        mButtonReject.setOnClickListener(view->rejectTravelFragment());
    }

    public void setButtonAccept() {
        mButtonAccept.setOnClickListener(view->acceptTravelFragment());
    }

    public void rejectTravelFragment() {
        TravelConfirmationDTO travelConfirmationDTO =
                new TravelConfirmationDTO(mTravelDTO.getTravelID(),this.ROL,this.idDriver,false);
        App.nodeServer.post("/travel/confirmation",travelConfirmationDTO,
                Object.class, new Headers())
                .run(this::responseRejectTravelFragment,this::errorRejectTravelFragment);
        //mListener.rejectTravel();
    }

    public void errorRejectTravelFragment(Exception ex){
        //TODO: muestra que hubo un error y que vuelva a intentarlo
        Log.e(this.getClass().getName(),"No se pudo rechazar el viaje....");
        Log.e(this.getClass().getName(),ex.toString());
        Toast.makeText(getActivity(),"no se pudo realizar la acción, intente nuevamente",
                Toast.LENGTH_LONG).show();
    }

    public void responseRejectTravelFragment(Object o){
        Log.d(this.getClass().getName(),"reject travel message has arrived to server successfully");
        mListener.rejectTravel();
    }

    public void acceptTravelFragment(){
        Log.d("TravelRequestFragment","##################10");
        if(mTravelDTO != null && mTravelDTO.getTravelID() >0){
            Log.d(this.getClass().getName(), "Driver accept travel and send message");
            TravelConfirmationDTO travelConfirmationDTO =
                    new TravelConfirmationDTO(mTravelDTO.getTravelID(),this.ROL,this.idDriver,true);
            App.nodeServer.post("/travel/confirmation",travelConfirmationDTO,
                    TravelAssignedDTO.class, new Headers())
                    .run(this::responseAcceptTravelFragment,this::errorAcceptTravelFragment);
        }else{
            //in mock userId is -1
            Log.d(this.getClass().getName(), "mock accept");
            mListener.acceptTravel(null);
        }

    }

    public void errorAcceptTravelFragment(Exception ex){
        //TODO: muestra que hubo un error y que vuelva a intentarlo
        Log.e(this.getClass().getName(),"No se pudo aceptar el viaje....");
        Log.e(this.getClass().getName(),ex.toString());
        Toast.makeText(getActivity(),"no se pudo realizar la acción, intente nuevamente",
                Toast.LENGTH_LONG).show();
    }

    public void responseAcceptTravelFragment(TravelAssignedDTO travelAssignedDTO){
        Log.d(this.getClass().getName(),"accept travel message has arrived to server successfully");
        mListener.acceptTravel(travelAssignedDTO);
    }

}
