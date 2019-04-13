package com.uberpets.mobile;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uberpets.mobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsTravelFragment extends Fragment {

    private Boolean optionCompanion = false;
    private Integer numbLittlePets = 0;
    private Integer numbMediumPets = 0;
    private Integer numbBigPets = 0;
    private SizePetsAdapter mAdapter;
    RecyclerView mRecyclerView;
    FloatingActionButton mButtonFab;

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

        mAdapter = new SizePetsAdapter(new ArrayList<>(0));
        mAdapter.updateList();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mButtonFab = rootView.findViewById(R.id.fab);
        setmButtonFab();

        return rootView;
    }


    public void setmButtonFab(){
        mButtonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    public void addItem(){
        mAdapter.updateList();
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

    /*
    public void getDriver(View view) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL+"/travels";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        driverComing(response);
                        Log.i("GETDRIVER", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("GETDRIVER", error.toString());
                finishPreviusFragments();
                mMessageCard.setVisibility(CardView.VISIBLE);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("longitude",String.valueOf(mDestiny.longitude));
                params.put("latitude",String.valueOf(mDestiny.latitude));
                return params;
            }
        };
        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
*/
}
