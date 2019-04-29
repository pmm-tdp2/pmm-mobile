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

    public void setmTravelAssignedDTO(TravelAssignedDTO mTravelAssignedDTO) {
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
        if(mTravelAssignedDTO != null) {
            this.textTime.setText(mTravelAssignedDTO.getTime());
        }else{
            this.textTime.setText("30.00");
        }
        return rootView;
    }

}
