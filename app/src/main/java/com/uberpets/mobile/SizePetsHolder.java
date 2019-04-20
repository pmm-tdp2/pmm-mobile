package com.uberpets.mobile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

public class SizePetsHolder extends RecyclerView.ViewHolder {
    private ImageButton littlePet;
    private ImageButton bigPet;
    private ImageButton deleteButton;
    private ImageButton mediumPet;

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public ImageButton getLittlePet() {
        return littlePet;
    }

    public ImageButton getMediumPet() {
        return mediumPet;
    }

    public ImageButton getBigPet() {
        return bigPet;
    }

    public SizePetsHolder(View itemView){
        super(itemView);
        littlePet = itemView.findViewById(R.id.little_pet);
        mediumPet = itemView.findViewById(R.id.medium_pet);
        bigPet = itemView.findViewById(R.id.big_pet);
        deleteButton = itemView.findViewById(R.id.button_delete);
    }

}
