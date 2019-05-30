package com.uberpets.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.uberpets.Constants;
import com.uberpets.library.rest.Headers;
import com.uberpets.model.SimpleResponse;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.model.TravelConfirmationDTO;
import com.uberpets.services.App;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DriverFollowUpTravel.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DriverFollowUpTravel#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverFollowUpTravel extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button mButtonFinalize;
    private Button mButtonCancel;
    private TravelAssignedDTO mTravelAssignedDTO;
    private String idDriver;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DriverFollowUpTravel() {
        // Required empty public constructor
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverFollowUpTravel.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverFollowUpTravel newInstance(String param1, String param2) {
        DriverFollowUpTravel fragment = new DriverFollowUpTravel();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_driver_follow_up_travel,
                container, false);

        mButtonCancel = rootView.findViewById(R.id.button_cancel_travel);
        mButtonFinalize = rootView.findViewById(R.id.button_finalize_travel);
        setButtonCancel();
        setButtonFinalize();

        return rootView;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
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
        void cancelOngoingTravel();
        void finishTravel();
    }

    public void setmTravelAssignedDTO(TravelAssignedDTO mTravelAssignedDTO) {
        this.mTravelAssignedDTO = mTravelAssignedDTO;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public void setButtonCancel() {
        mButtonCancel.setOnClickListener(view->cancelTravelFragment());
    }

    public void setButtonFinalize() {
        mButtonFinalize.setOnClickListener(view->finalizeTravelFragment());
    }

    public void cancelTravelFragment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.cancel_travel_information)
                .setTitle(R.string.are_you_sure)
                .setPositiveButton(R.string.yes_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancelo el viaje
                        cancelTravelRequest();
                    }
                })
                .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Continuo con el viaje
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void cancelTravelRequest(){
        String rol = Constants.getInstance().getID_DRIVERS();
        int travelId = mTravelAssignedDTO == null ? 9999 : mTravelAssignedDTO.getTravelId();
        TravelConfirmationDTO travelCancelDTO = new TravelConfirmationDTO(
                travelId, rol, this.idDriver,true);
        App.nodeServer.post("/api/travels/cancel", travelCancelDTO, SimpleResponse.class, new Headers())
                .run(this::responseCancelTravel, this::errorCancelTravel);
    }

    public void finalizeTravelFragment() {
        if(mTravelAssignedDTO !=  null){
            String rol = Constants.getInstance().getID_DRIVERS();
            Log.i(this.getClass().getName(),"ANTES DE CREAR DTO");
            TravelConfirmationDTO travelConfirmationDTO =
                    new TravelConfirmationDTO(mTravelAssignedDTO.getTravelId()
                            , rol, this.idDriver,true);
            Log.i(this.getClass().getName(),"LUEGO DE CREAR DTO");
            App.nodeServer.post("/api/travels/finalize", travelConfirmationDTO,
                    SimpleResponse.class, new Headers())
                    .run(this::responseFinalizeTravelFragment,this::errorRejectTravelFragment);
        }else{
            mListener.finishTravel();
        }

    }

    public void responseFinalizeTravelFragment(SimpleResponse simpleResponse) {
        Log.d(this.getClass().getName(),simpleResponse.getMessage());
        mListener.finishTravel();
    }

    public void errorRejectTravelFragment(Exception e){
        //TODO: mandar un mensaje que no se pudo finalizar el viaje
        Log.e(this.getClass().getName(),e.toString());
        Toast toast = Toast.makeText(getActivity(), "no se pudo cancelar intentelo nuevamente",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void responseCancelTravel(SimpleResponse response) {
        Log.d(this.getClass().getName(), response.getMessage());
        if (response.getStatus() == 200){
            //cancelo el viaje
            Toast toast = Toast.makeText(getActivity(), "Se ha cancelado su viaje",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            mListener.cancelOngoingTravel();
        }else{
            //TODO: ocurrió un error con el servidor y no se pudo cancelar el viaje
            Toast toast = Toast.makeText(getActivity(), "no se pudo cancelar intentelo nuevamente",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void errorCancelTravel(Exception e){
        Log.e(this.getClass().getName(),"Error in cancel");
        Log.d(this.getClass().getName(), e.toString());
        //TODO: no se ha podido finalizar el viaje
    }

}
