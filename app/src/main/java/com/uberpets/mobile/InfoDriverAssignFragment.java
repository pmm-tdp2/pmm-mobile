package com.uberpets.mobile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uberpets.mobile.R;
import com.uberpets.model.TravelAssignedDTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoDriverAssignFragment extends Fragment {

    private TravelAssignedDTO mTravelAssignedDTO;
    private TextView textTime;
    private TextView textName;
    private TextView textLastname;


    public void setTravelAssignedDTO(TravelAssignedDTO mTravelAssignedDTO) {
        this.mTravelAssignedDTO = mTravelAssignedDTO;
    }

    public InfoDriverAssignFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info_driver_assing, container, false);

        this.textTime = rootView.findViewById(R.id.waiting_time_for_driver);
        this.textName = rootView.findViewById(R.id.driver_name);
        this.textLastname =  rootView.findViewById(R.id.driver_last_name);
        updateDisplayedDataDriver();

        return rootView;
    }

    public void updateDisplayedDataDriver() {
        this.textTime.setText(this.mTravelAssignedDTO.getTime());
        this.textName.setText(this.mTravelAssignedDTO.getDriver().getFirstName());
        this.textLastname.setText(this.mTravelAssignedDTO.getDriver().getLastName());
    }

}
