package com.uberpets.mobile.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uberpets.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CanceledTravelFragment extends Fragment {

    //private Button acceptButton;

    public CanceledTravelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //acceptButton = container.findViewById(R.id.accept_canceled_travel_button);
        //acceptButton.setOnClickListener(view->acceptAction(view));
        return inflater.inflate(R.layout.fragment_canceled_travel, container, false);
    }

    /*public void acceptAction(View view){
        Intent intent = new Intent(getActivity(), UserHome.class);
        startActivity(intent);
    }*/

}
