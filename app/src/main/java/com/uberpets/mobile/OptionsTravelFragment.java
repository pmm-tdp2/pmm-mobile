package com.uberpets.mobile;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uberpets.mobile.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsTravelFragment extends Fragment {


    public OptionsTravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options_travel, container, false);
    }

}
