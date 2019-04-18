/*package com.uberpets.library.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.comprame.R;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

public class GlideSliderView extends DefaultSliderView {

    public GlideSliderView(Context context) {
        super(context);
    }

    protected void bindEventAndShow(final View v, ImageView targetImageView) {
        View progressBar = v.findViewById(com.daimajia.slider.library.R.id.loading_bar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        Glide.with(getContext())
                .load(getUrl())
                .centerCrop()
                .fitCenter()
                .crossFade()
                .into(targetImageView);
    }
}*/