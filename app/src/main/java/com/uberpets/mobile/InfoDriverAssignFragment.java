package com.uberpets.mobile;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uberpets.library.rest.Headers;
import com.uberpets.model.FileDocumentDTO;
import com.uberpets.model.TravelAssignedDTO;
import com.uberpets.services.App;
import com.uberpets.util.ConvertImages;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoDriverAssignFragment extends Fragment {

    private TravelAssignedDTO mTravelAssignedDTO;
    private TextView textTime;
    private TextView textName;
    private TextView textLastName;
    private ImageView driverImage;


    public void setTravelAssignedDTO(TravelAssignedDTO mTravelAssignedDTO) {
        this.mTravelAssignedDTO = mTravelAssignedDTO;
    }

    public void setTimeToArrive(Integer time){
        this.textTime.setText(time.toString());
    }

    public void setDriversDistance(Integer distance){

    }

    public InfoDriverAssignFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info_driver_assing, container, false);
        this.textTime = rootView.findViewById(R.id.time_arrive);
        this.textName = rootView.findViewById(R.id.driver_name);
        this.driverImage = rootView.findViewById(R.id.image_driver);
        updateDisplayedDataDriver();

        return rootView;
    }

    public void updateDisplayedDataDriver() {
        this.textTime.setText(this.mTravelAssignedDTO.getTime());
        this.textName.setText(this.mTravelAssignedDTO.getDriver().getName());
        loadImageUser();
    }


    private void loadImageUser() {
        String path = "/api/fileDocuments/?driverId="
                +mTravelAssignedDTO.getDriver().getId()+"&name=profile";
        App.nodeServer.get(path, FileDocumentDTO[].class,new Headers())
                .run(this::handleSuccessLoadImages,this::handleErrorLoadImages);
    }

    private void handleErrorLoadImages(Exception e) {
        Log.e(this.getClass().getName(),"Error to load images of user");
        Log.e(this.getClass().getName(),e.toString());
        Toast toast = Toast.makeText(getActivity(),"Error al obtener la imagen del usuario",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        this.driverImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
    }

    private void handleSuccessLoadImages(FileDocumentDTO[] files) {
        Log.i(this.getClass().getName(),"Photo profile of user obtained successfully");
        if (files.length > 0)
            this.driverImage.setImageBitmap(ConvertImages.getBitmapImage(files[0].getData()));
    }
}
