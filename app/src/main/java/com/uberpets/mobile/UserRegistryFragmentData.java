package com.uberpets.mobile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uberpets.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserRegistryFragmentData extends Fragment {


    public UserRegistryFragmentData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_registry_fragment_data, container, false);
    }

}
