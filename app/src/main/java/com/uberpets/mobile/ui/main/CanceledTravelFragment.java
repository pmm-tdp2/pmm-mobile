package com.uberpets.mobile.ui.main;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uberpets.mobile.R;
import com.uberpets.mobile.UserHome;

/**
 * A simple {@link Fragment} subclass.
 */
public class CanceledTravelFragment extends Fragment {


    public CanceledTravelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_canceled_travel, container, false);
    }

    public void acceptAction(View view){
        Intent intent = new Intent(getActivity(), UserHome.class);
        startActivity(intent);
    }

}
